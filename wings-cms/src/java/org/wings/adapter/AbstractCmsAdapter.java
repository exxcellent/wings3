package org.wings.adapter;

import org.wings.*;
import org.wings.event.*;
import org.wings.plaf.css.CmsLayoutCG;
import org.wings.resource.DynamicResource;
import org.wings.resource.ReloadResource;
import org.wings.template.StringTemplateSource;
import org.wings.template.TemplateSource;
import org.wings.session.SessionManager;
import org.wings.header.Link;
import org.wings.header.Script;
import org.wings.conf.CmsDetail;
import org.wings.adapter.CmsAdapter;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.util.DateParser;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import java.util.*;
import java.text.SimpleDateFormat;
import java.io.IOException;

import au.id.jericho.lib.html.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletRequest;

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
public abstract class AbstractCmsAdapter implements CmsAdapter {

    private SFrame frame;
    private STemplateLayout layout;

    private CmsDetail cfg;

    DynamicResource defaultResource;
    Map<String, StringTemplateSource> contentMap = new HashMap<String, StringTemplateSource>();
    Map<String, Date> obtainedMap = new HashMap<String, Date>();
    SimpleDateFormat httpdate;
    private String path;

    protected Collection<Link> links = new ArrayList<Link>();
    protected Collection<Script> scripts = new ArrayList<Script>();

    public AbstractCmsAdapter(SFrame frame, STemplateLayout layout, CmsDetail cfg) {
        setFrame(frame);
        setConfiguration(cfg);
        this.layout = layout;
        defaultResource = frame.getDynamicResource(ReloadResource.class);

        // httpdate parses and formats dates in HTTP Date Format (RFC1123)
        httpdate = new SimpleDateFormat(DateParser.PATTERN_RFC1123, Locale.ENGLISH);
        httpdate.setTimeZone(TimeZone.getTimeZone("GMT"));

        navigate(SessionManager.getSession().getServletRequest().getPathInfo());
    }

    public SFrame getFrame() {
        return frame;
    }

    public void setFrame(SFrame frame) {
        this.frame = frame;
    }

    public CmsDetail getConfiguration() {
        return cfg;
    }

    public void setConfiguration(CmsDetail cfg) {
        this.cfg = cfg;
    }


    public Resource mapResource(String url) {
        navigate(url);
        return defaultResource;
    }

    protected void navigate(String url) {
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

            method = new GetMethod(cfg.getServerPath() + url);
        }
        else if ("POST".equals(methodName)) {
            method = new PostMethod(cfg.getServerPath() + url);

            Enumeration enumeration = request.getParameterNames();
            while (enumeration.hasMoreElements()) {
                String name = (String) enumeration.nextElement();
                String value = request.getParameter(name);
                ((PostMethod) method).addParameter(name, value);
            }
        }
        HttpClient httpclient = new HttpClient();
        String host = System.getProperty("http.proxyHost");
        String port = System.getProperty("http.proxyPort");
        if (host != null && port != null)
            method.getHostConfiguration().setProxy(host, Integer.valueOf(port));

        String templateString = null;

        // Sets the "If-Modified-Since" header to the date in cache
        if (obtainedMap.containsKey(url)) {
            method.addRequestHeader("If-Modified-Since", httpdate.format(obtainedMap.get(url)));
        }

        try {
            // Execute http request
            int httpstatus = httpclient.executeMethod(method);

            // Invoke handleUnknownResourceRequested
            if (httpstatus != HttpStatus.SC_OK && httpstatus != HttpStatus.SC_NOT_MODIFIED)
                return;

            // If the 'If-Modified-Since' header is sent, the server should set the status to
            // SC_NOT_MODIFIED (304). Sometimes this does not work. In this case we try to compare the
            // 'Last-Modified' response header with the date in cache ourselves.
            boolean cached = true;
            if (httpstatus == HttpStatus.SC_OK) {
                try {
                    Date httplastmodified = httpdate.parse(method.getResponseHeader("Last-Modified").getValue());
                    if (!httplastmodified.before(obtainedMap.get(url))) cached = false;
                }
                catch (Exception ex) {
                    // Cannot parse the Last-Modified header or file is not in cache --> Don't use caching
                    cached = false;
                }
            }

            StringTemplateSource templateSource;
            // Load the template from cache or use the response body as new template
            if (cached) {
                templateSource = contentMap.get(url);
            } else {
                templateString = method.getResponseBodyAsString();
                templateString = process(templateString);

                // Add template to cache
                templateSource = new StringTemplateSource(templateString);
                contentMap.put(url, templateSource);
                obtainedMap.put(url, new Date());
            }
            setTemplate(templateSource);

            method.releaseConnection();
        }
        catch (Exception ex) {
            // Http get request failed or can't set template --> Invoke handleUnknownResourceRequested
            ex.printStackTrace();
            System.err.println(templateString);
        }
    }

    protected void setTemplate(TemplateSource templateSource) throws IOException {
        layout.setTemplate(templateSource);
    }

    protected String process(String templateString) {

//        String wingsServerPath = getPath();
//        String cmsServerPath = cfg.getServerPath();

        Source source = new Source(templateString);

        parseTitle(source);
        parseLinks(source);
        parseScripts(source);

//            Segment content = source.getElementById("body").getContent();

        Element body = source.findNextElement(0, "body");
        if (body == null) {
            return templateString;
        }

        Segment content = body.getContent();
        source = new Source(content.toString());

        OutputDocument outputDocument = new OutputDocument(source);

        parseAnchors(source, outputDocument);
        parseImages(source, outputDocument);

        templateString = outputDocument.toString();
        return templateString;
    }

    public String getPath() {
        if (path == null) {
            //path = SessionManager.getSession().getServletRequest().getContextPath() + SessionManager.getSession().getServletRequest().getServletPath();
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