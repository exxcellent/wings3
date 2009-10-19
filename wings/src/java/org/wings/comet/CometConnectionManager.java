package org.wings.comet;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wings.session.Browser;
import org.wings.session.BrowserType;
import org.wings.session.SessionManager;

/**
 * Manages open HangingGet requests and limits their number per browser.
 * <p>
 * This is necessary to circumvent browsers connection limits. The number of
 * concurrent connections per host is limited as follows (see <a
 * href="https://bugzilla.mozilla.org/show_bug.cgi?id=423377"
 * >https://bugzilla.mozilla.org/show_bug.cgi?id=423377</a>):
 * <ul>
 * <li>Firefox 2: 2</li>
 * <li>Firefox 3: 6</li>
 * <li>Opera 9.26: 4</li>
 * <li>Opera 9.5 beta: 4</li>
 * <li>Safari 3.0.4 Mac/Windows: 4</li>
 * <li>IE 7: 2</li>
 * <li>IE 8: 6</li>
 * </ul>
 * If this maximum number of connections would be all occupied by HangingGet
 * requests, all network traffic of this browser would be blocked. To avoid this
 * the number of HangingGet requests should be limited to a number below the
 * maximum number of connections.
 * <p>
 * Subclasses implement different strategies to restrict the number of
 * HangingGet requests.
 */
public abstract class CometConnectionManager {

    static final String COOKIE_NAME = "COMET_BROWSER";
    static final String NAME = "org.wings.comet.connectionSet";

    private String browserId = null;
    
    private int maxHangingGetCount;
    
    protected static final transient Log log = LogFactory.getLog(CometConnectionManager.class);
    
    protected CometConnectionManager() {
    	maxHangingGetCount = determineMaxHangingGetCount();
	}

    /**
	 * Checks whether another HangingGet request could be opened by the current
	 * browser (i.e. the maximum number of HangingGet requests isn't yet reached
	 * for this browser)
	 */
	abstract boolean canAddHangingGet();

	/**
	 * Adds an incoming HangingGet request to the number of managed HangingGets.
	 * This is possible only if the maximum number of HangingGets isn't yet
	 * reached for this browser.
	 * 
	 * @return true, if the HangingGet request was successfully added. Returns
	 *         false, if the limit is reached. In this case the HangingGet
	 *         request should be rejected
	 */
	abstract boolean addHangingGet();

	/**
	 * Removes one HangingGet request from the number of managed HangingGets.
	 */
	abstract void removeHangingGet();

    public void setBrowserId(HttpServletRequest request, HttpServletResponse response) {
        String browser_id = getBrowserIdFromCookie(request);
        if (browser_id == null) {
            browser_id = setupCometBrowserCookie(request, response);
            log.debug("setupCometBrowserCookie: browser_id="+browser_id);
        }
        this.browserId = browser_id;
        log.debug("setBrowserId: browser_id="+browser_id);
    }

    protected String getBrowserId(){
    	return browserId;
    }
    
    protected int getMaxHangingGetCount(){
    	return maxHangingGetCount;
    }
    
    private String getBrowserIdFromCookie(HttpServletRequest request) {
        final Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (COOKIE_NAME.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private String setupCometBrowserCookie(HttpServletRequest request, HttpServletResponse response) {
        final String browser_id = Long.toHexString(System.currentTimeMillis()) +
            Long.toHexString(request.getRemoteAddr().hashCode());

        final Cookie cookie = new Cookie(COOKIE_NAME, browser_id);
        cookie.setPath("/");
        cookie.setMaxAge(-1);

        response.addCookie(cookie);
        return browser_id;
    }
    
	private int determineMaxHangingGetCount() {
		Browser browser = SessionManager.getSession().getUserAgent();
		BrowserType type = browser.getBrowserType();
		int version = browser.getMajorVersion();

		if (type.equals(BrowserType.IE) && version >= 8)
			return 4;
		if (type.equals(BrowserType.GECKO) && version >= 3)
			return 4;
		// TODO: Increase and Test for other Browsers
		return 1;
	}
}
