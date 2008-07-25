package org.wings.adapter.cms;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
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
import org.wings.Resource;
import org.wings.SFrame;
import org.wings.STemplateLayout;
import org.wings.SimpleURL;
import org.wings.URLResource;
import org.wings.adapter.AbstractIntegrationAdapter;
import org.wings.adapter.parser.HtmlParser;
import org.wings.conf.Integration;
import org.wings.conf.UrlExtension;
import org.wings.header.Link;
import org.wings.header.Script;
import org.wings.session.SessionManager;
import org.wings.template.StringTemplateSource;
import org.wings.template.TemplateSource;
import org.wings.util.HtmlParserUtils;

import au.id.jericho.lib.html.Attribute;
import au.id.jericho.lib.html.Attributes;
import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.Source;
import au.id.jericho.lib.html.Tag;

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
public abstract class AbstractCmsAdapter extends AbstractIntegrationAdapter implements HtmlParser {

	private STemplateLayout layout;

	protected Map<String, StringTemplateSource> contentMap = new HashMap<String, StringTemplateSource>();
	protected Map<String, Date> obtainedMap = new HashMap<String, Date>();
	SimpleDateFormat httpdate;
	private String path;

	protected Collection<Link> links = new ArrayList<Link>();
	protected Collection<Script> scripts = new ArrayList<Script>();

	public AbstractCmsAdapter(SFrame frame, Integration integration, STemplateLayout layout) {
		super(frame, integration);
		this.layout = layout;

		// httpdate parses and formats dates in HTTP Date Format (RFC1123)
		httpdate = new SimpleDateFormat(DateParser.PATTERN_RFC1123, Locale.ENGLISH);
		httpdate.setTimeZone(TimeZone.getTimeZone("GMT"));

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

			Enumeration enumeration = request.getParameterNames();
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

			Enumeration enumeration = request.getParameterNames();
			while (enumeration.hasMoreElements()) {
				String name = (String) enumeration.nextElement();
				String value = request.getParameter(name);
				((PostMethod) method).addParameter(name, value);
			}
		}

		String responseBody = process(request(method));

		TemplateSource templateSource = new StringTemplateSource(responseBody);

		setTemplate(templateSource);
	}

	protected void setTemplate(TemplateSource templateSource) throws IOException {
		layout.setTemplate(templateSource);
	}

	/**
	 * @param responseBody
	 * @return
	 */
	protected String process(String responseBody) {

		Source source = new Source(responseBody);

		// Resolves all includes contained in source (recursive).
		source = resolveIncludes(source);

		// Parses the head of the template if head tag is present.
		if (HtmlParserUtils.isTagPresent(source, Tag.HEAD)) {
			Source headSource = HtmlParserUtils.getSourcesForTag(source, Tag.HEAD)[0];
			parseHead(headSource);
		}

		// Parses the head of the template if body tag is present.
		if (HtmlParserUtils.isTagPresent(source, Tag.BODY)) {
			Source bodySource = HtmlParserUtils.getSourcesForTag(source, Tag.BODY)[0];
			bodySource = parseBody(bodySource);
			
			return bodySource.toString();
		}

		return source.toString();
	}

	public Source resolveIncludes(Source source) {
		return resolveIncludes0(source);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wings.adapter.CmsAdapter#resolveIncludes(au.id.jericho.lib.html.Source
	 * )
	 */
	private Source resolveIncludes0(Source source) {

		Element include;
		while ((include = source.findNextElement(0, "include")) != null) {

			// Get the URL (default) extension.
			Attribute type = include.getAttributes().get("type");
			UrlExtension urlExtension = integration.getResource()
					.getUrlExtension((type != null ? type.getValue() : null));

			List<String> variables = urlExtension.getVariables();
			List<String> values = new ArrayList<String>();

			Attributes attributes = include.getAttributes();
			for (String variable : variables) {
				Attribute attribute = attributes.get(variable);

				if (attribute != null) {
					values.add(attribute.getValue());
				}
				else {
					System.out.println("No attribute " + variable + " has been found within element \"" + include
							+ "\".");
				}
			}

			String extension = urlExtension.getReplacedValue(values.toArray(new String[values.size()]));

			String url = integration.getBaseUrl().toExternalForm() + "/" + extension;
			System.out.println(url);

			int begin = include.getBegin();
			int end = include.getEnd();
			int end2 = source.length();

			StringBuffer sb = new StringBuffer();
			sb.append(source.subSequence(0, begin));

			// Resolves nested of includes.
			sb.append(resolveIncludes0(new Source(requestInclude(url))).toString());

			sb.append(source.subSequence(end, end2));

			source = new Source(sb);
		}

		return source;
	}

	private String requestInclude(String url) {
		HttpClient httpClient = new HttpClient();

		GetMethod method = new GetMethod(url);

		try {
			int status = httpClient.executeMethod(method);
			if (status == HttpStatus.SC_OK) {
				return method.getResponseBodyAsString();
			}
		}
		catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	public String getPath() {
		if (path == null) {
			// path =
			// SessionManager.getSession().getServletRequest().getContextPath()
			// +
			// SessionManager.getSession().getServletRequest().getServletPath();
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