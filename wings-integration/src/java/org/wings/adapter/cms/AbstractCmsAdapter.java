package org.wings.adapter.cms;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.util.DateParser;
import org.wings.IntegrationFrame;
import org.wings.Resource;
import org.wings.SimpleURL;
import org.wings.URLResource;
import org.wings.adapter.AbstractTemplateIntegrationAdapter;
import org.wings.conf.Integration;
import org.wings.header.Link;
import org.wings.header.Script;
import org.wings.session.SessionManager;
import org.wings.template.StringTemplateSource;

/**
 * <code>AbstractCMSAdapter<code>.
 * <p/>
 * User: rrd
 * Date: 08.08.2007
 * Time: 09:03:52
 * 
 * @author rrd
 * @version $Id
 */
public abstract class AbstractCmsAdapter extends AbstractTemplateIntegrationAdapter {

	protected Map<String, StringTemplateSource> contentMap = new HashMap<String, StringTemplateSource>();
	protected Map<String, Date> obtainedMap = new HashMap<String, Date>();
	SimpleDateFormat httpdate;
	private String path;

	protected Collection<Link> links = new ArrayList<Link>();
	protected Collection<Script> scripts = new ArrayList<Script>();

	public AbstractCmsAdapter(IntegrationFrame frame, Integration integration) {
		super(frame, integration);

		// httpdate parses and formats dates in HTTP Date Format (RFC1123)
		httpdate = new SimpleDateFormat(DateParser.PATTERN_RFC1123, Locale.ENGLISH);
		httpdate.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	
	public void initialize() {
	    super.initialize();
	    try {
            navigate(SessionManager.getSession().getServletRequest().getPathInfo());
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public URL getIntegrationBaseUrl() {
		return integration.getBaseUrl();
	}

	/* (non-Javadoc)
	 * @see org.wings.adapter.AbstractIntegrationAdapter#mapResource(java.lang.String)
	 */
	@SuppressWarnings("deprecation")
	public Resource mapResource(String url) {
		try {
			navigate(url);
		}
		catch (HttpException e) {
			switch (e.getReasonCode()) {
			case HttpStatus.SC_NOT_FOUND:
				return null;
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return super.mapResource(url);
	}
	
	/**
	 * @param method
	 * @return
	 */
	@SuppressWarnings("deprecation")
	protected String request(HttpMethod method) throws IOException {
		
		HttpClient httpClient = new HttpClient();
		String host = System.getProperty("http.proxyHost");
		String port = System.getProperty("http.proxyPort");
		
		if (host != null && port != null) method.getHostConfiguration().setProxy(host, Integer.valueOf(port));

		String responseBody = null;

		// Execute http request
		int httpStatus = httpClient.executeMethod(method);

		// TODO Invoke handleUnknownResourceRequested
		if (HttpStatus.SC_OK == httpStatus) {
			 responseBody = method.getResponseBodyAsString();
		}
		else {
			HttpException e = new HttpException();
			e.setReasonCode(httpStatus);
			throw e;
		}

		method.releaseConnection();
		
		return responseBody;
	}

	/**
	 * @param url
	 * @throws HttpException
	 * @throws IOException
	 */
	protected void navigate(String url) throws HttpException, IOException {
		HttpServletRequest request = SessionManager.getSession().getServletRequest();

		String methodName = request.getMethod();
		HttpMethod method = null;
		if ("GET".equals(methodName)) {
			url += "?";

			Enumeration<?> enumeration = request.getParameterNames();
			while (enumeration.hasMoreElements()) {
				String name = (String) enumeration.nextElement();
				String value = request.getParameter(name);

				url += name + "=" + value + "&";
			}
			url = url.substring(0, url.length() - 1);

			if (!url.startsWith("/")) {
				url = "/" + url;
			}

			method = new GetMethod(integration.getBaseUrl().toExternalForm() + url);
		}
		else if ("POST".equals(methodName)) {
			method = new PostMethod(integration.getBaseUrl() + url);

			Enumeration<?> enumeration = request.getParameterNames();
			while (enumeration.hasMoreElements()) {
				String name = (String) enumeration.nextElement();
				String value = request.getParameter(name);
				((PostMethod) method).addParameter(name, value);
			}
		}
		
		setTemplate(new StringTemplateSource(process(request(method))));
	}
	
	@Override
    protected String prepareUrl(String extensionUrl) {
        String baseUrl = integration.getBaseUrl().toExternalForm();
        if (!baseUrl.endsWith("/")) {
            baseUrl += "/";
        }
        return baseUrl + extensionUrl;
    }
	
	@Override
	protected String requestInclude(String url) {
		GetMethod method = new GetMethod(url);

		try {
			return request(method);
		}
		catch (HttpException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}

	public String getPath() {
		if (path == null) {
			HttpServletRequest request = SessionManager.getSession().getServletRequest();

			String path = request.getRequestURL().toString();
			String pathInfo = request.getPathInfo();
			this.path = path.substring(0, path.lastIndexOf(pathInfo));
		}

		return path;
	}

	protected class Url implements URLResource {
		private SimpleURL url;

		public Url(String href) {
			this.url = new SimpleURL(href);
		}

		public SimpleURL getURL() {
			return url;
		}
	}
}