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
import org.wings.IntegrationFrame;
import org.wings.Resource;
import org.wings.SimpleURL;
import org.wings.URLResource;
import org.wings.adapter.AbstractTemplateIntegrationAdapter;
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
import au.id.jericho.lib.html.StartTagType;
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
public abstract class AbstractCmsAdapter extends AbstractTemplateIntegrationAdapter implements HtmlParser {

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wings.adapter.AbstractIntegrationAdapter#mapResource(java.lang.String
	 * )
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

		String responseBody = process(request(method));

		TemplateSource templateSource = new StringTemplateSource(responseBody);

		setTemplate(templateSource);
	}

	/**
	 * @param responseBody
	 * @return
	 */
	protected String process(String responseBody) {

		Source source = new Source(responseBody);

		// Resolves all includes contained in source (recursive).
		source = resolveIncludes(source);

		// Removes all wingS integration comments like <!-- IGNORE BEGIN --> and
		// <!-- IGNORE END -->
		source = removeIgnoreComments(source);

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wings.adapter.parser.HtmlParser#resolveIncludes(au.id.jericho.lib
	 * .html.Source)
	 */
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
			UrlExtension urlExtension = integration.getResource().getUrlExtension(
					(type != null ? type.getValue() : null));

			List<String> variables = urlExtension.getVariables();
			List<String> values = new ArrayList<String>();

			Attributes attributes = include.getAttributes();
			for (String variable : variables) {
				Attribute attribute = attributes.get(variable);

				if (attribute != null) {
					values.add(attribute.getValue());
				}
				else {
					System.out.println("No attribute " + variable + " has been found within element \"" + include + "\".");
				}
			}
			
			String baseUrl = integration.getBaseUrl().toExternalForm();
	        if (!baseUrl.endsWith("/")) {
	            baseUrl += "/";
	        }
	        String extensionUrl = urlExtension.getReplacedValue(values.toArray(new String[values.size()]));

			String url = baseUrl + extensionUrl;

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

	/**
	 * @param url
	 * @return
	 */
	private String requestInclude(String url) {
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

	/**
	 * @param source
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Source removeIgnoreComments(Source source) {
		List<Element> comments = source.findAllElements(StartTagType.COMMENT);
		if (comments.size() < 1) {
			return source;
		}

		List<Element> ignoreBeginComments = new ArrayList<Element>();
		List<Element> ignoreEndComments = new ArrayList<Element>();
		for (Element comment : comments) {
			String commentContent = comment.toString().trim();

			if ("<!-- IGNORE BEGIN -->".equalsIgnoreCase(commentContent)) {
				ignoreBeginComments.add(comment);
			}
			else if ("<!-- IGNORE END -->".equalsIgnoreCase(commentContent)) {
				ignoreEndComments.add(comment);
			}
		}

		// Throw an exception if size of ignore begin and size of ignore end
		// doesn't match.
		if (ignoreBeginComments.size() != ignoreEndComments.size()) {
			throw new IllegalStateException("Amount of <!-- IGNORE BEGIN --> (" + ignoreBeginComments.size()
					+ ") doesn't match amount of <!-- IGNORE END --> (" + ignoreEndComments.size() + ") comments.");
		}
		
		// Return immediately if no ignore comment has been found.
		if (ignoreBeginComments.size() < 1) {
			return source;
		}

		// Removes ignore comments.
		// TODO: Tweak approach to achieve performance upgrade.
		StringBuilder withoutIgnoreComments = new StringBuilder();
		int length = ignoreBeginComments.size();
		for (int i = 0; i < length; i++) {
			Element ignoreBegin = ignoreBeginComments.get(i);
			Element ignoreEnd = ignoreEndComments.get(i);
			
			withoutIgnoreComments.append(source.subSequence(0, ignoreBegin.getBegin()));
			
			if ((i + 1) == length) {
				withoutIgnoreComments.append(source.subSequence(ignoreEnd.getEnd(), source.length()));
			}
			else {
				Element nextIgnoreBegin = ignoreBeginComments.get(i + 1);
				withoutIgnoreComments.append(source.subSequence(ignoreEnd.getEnd(), nextIgnoreBegin.getBegin()));
			}		
		}
		
		return new Source(withoutIgnoreComments);
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