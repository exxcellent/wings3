package org.wings.comet;

import javax.servlet.http.*;

public abstract class CometConnectionManager {

    static final String COOKIE_NAME = "COMET_BROWSER";
    static final String NAME = "org.wings.comet.connectionSet";

    String browserId = null;

    abstract boolean isHangingGetActive();
    abstract boolean hangingGetActive(boolean value);
    abstract void setHangingGetActive(boolean value);
    abstract Object getSharedObject();

    public void setBrowserId(HttpServletRequest request, HttpServletResponse response) {
        String browser_id = getBrowserIdFromCookie(request);
        if (browser_id == null) {
            browser_id = setupCometBrowserCookie(request, response);
        }
        this.browserId = browser_id;
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
}
