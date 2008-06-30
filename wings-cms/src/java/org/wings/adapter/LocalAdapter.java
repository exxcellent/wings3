package org.wings.adapter;

import org.wings.*;
import org.wings.template.StringTemplateSource;
import org.wings.template.TemplateSource;
import org.wings.session.SessionManager;
import org.wings.header.Link;
import org.wings.header.Script;
import org.wings.conf.CmsDetail;
import org.apache.commons.httpclient.*;
import au.id.jericho.lib.html.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContext;
import java.util.*;
import java.io.*;
import java.nio.CharBuffer;

/**
 * @author hengels
 * @version $Id
 */
public class LocalAdapter extends AbstractCmsAdapter {

    public LocalAdapter(SFrame frame, STemplateLayout layout, CmsDetail cfg) {
        super(frame, layout, cfg);
    }

    protected void navigate(String url) {
        ServletContext context = SessionManager.getSession().getServletContext();

        String path = context.getRealPath(getConfiguration().getBasePath() + url);
        File file = new File(path);
        String templateString = null;

        boolean cached = false;
        if (obtainedMap.containsKey(url)) {
            cached = true;
            if (!new Date(file.lastModified()).before(obtainedMap.get(url)))
                cached = false;
        }
        try {
            // Load the template from cache or use the response body as new template
            if (cached) {
                templateString = contentMap.get(url);
            }
            else {
                FileReader reader = new FileReader(file);
                StringBuilder builder = new StringBuilder();
                char[] buffer = new char[4096];
                int len;
                while ((len = reader.read(buffer)) != -1)
                    builder.append(buffer, 0, len);
                templateString = builder.toString();
                templateString = process(templateString);

                // Add template to cache
                contentMap.put(url, templateString);
                obtainedMap.put(url, new Date());
            }
            setTemplate(new StringTemplateSource(templateString));
        }
        catch (Exception ex) {
            // Http get context failed or can't set template --> Invoke handleUnknownResourceRequested
            ex.printStackTrace();
            System.err.println(templateString);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void parseTitle(Source source) {
        Element titleElement = source.findNextElement(0, "title");

        if (titleElement != null) {
            String title = titleElement.getTextExtractor().toString();
            getFrame().setTitle("LocalAdapter :: " + title);
        }
    }

    public void parseAnchors(Source source, OutputDocument output) {

        String wingsServerPath = getPath();
        String cmsServerPath = getConfiguration().getServerPath();

        List<StartTag> anchorTags = source.findAllStartTags(Tag.A);
        for (StartTag anchorTag : anchorTags) {
            Attributes attributes = anchorTag.getAttributes();
            Attribute attribute = attributes.get("href");
            if (attribute != null) {
                String value = attribute.getValue();

                String replacedValue = value.replaceFirst(cmsServerPath, wingsServerPath);
                if (!replacedValue.equals(value)) {
                    output.replace(attribute.getValueSegment(), replacedValue);
                }
            }
        }

        List<StartTag> formTags = source.findAllStartTags(Tag.FORM);
        for (StartTag formTag : formTags) {
            Attributes attributes = formTag.getAttributes();
            Attribute attribute = attributes.get("action");
            if (attribute != null) {
                String value = attribute.getValue();

                String replacedValue = value.replaceFirst(cmsServerPath, wingsServerPath);
                if (!replacedValue.equals(value)) {
                    output.replace(attribute.getValueSegment(), replacedValue);
                }
            }
        }
    }

    public void parseImages(Source source, OutputDocument output) {

        String cmsServerPath = getConfiguration().getServerPath();

        List<StartTag> imgTags = source.findAllStartTags(Tag.IMG);
        for (StartTag imgTag : imgTags) {
            Attributes attributes = imgTag.getAttributes();
            Attribute attribute = attributes.get("src");
            if (attribute != null) {
                String value = attribute.getValue();

                if (!value.startsWith(getConfiguration().getProtocol())) {
                    value = cmsServerPath + "/" + value;
                }
                output.replace(attribute.getValueSegment(), value);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void parseLinks(Source source) {

        HttpServletRequest request = SessionManager.getSession().getServletRequest();
        CmsDetail cfg = getConfiguration();

        Collection<Link> newLinks = new ArrayList<Link>();
        Collection<StartTag> linkTags = source.findAllStartTags("link");
        for (StartTag linkTag : linkTags) {
            Attributes attributes = linkTag.getAttributes();
            String rel = attributes.getValue("rel");
            String rev = attributes.getValue("rev");
            String type = attributes.getValue("type");
            String target = attributes.getValue("target");
            String href = attributes.getValue("href");
            if (!href.startsWith("http")) {
                String pathInfo = request.getPathInfo();
                if (pathInfo.contains(".php")) pathInfo = pathInfo.substring(0, pathInfo.lastIndexOf("/"));
                href = cfg.getServerPath() + pathInfo + "/" + href;
            }

            newLinks.add(new Link(rel, rev, type, target, new Url(href)));
        }
        if (!newLinks.equals(links)) {
//            System.out.println("links    = " + links);
//            System.out.println("newLinks = " + newLinks);

            for (Link link : links)
                getFrame().removeHeader(link);
            for (Link link : newLinks)
                getFrame().addHeader(link);

            links.clear();
            links.addAll(newLinks);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void parseScripts(Source source) {
        Collection<Script> newScripts = new ArrayList<Script>();
        Collection<StartTag> scriptTags = source.findAllStartTags("script");
        for (StartTag scriptTag : scriptTags) {
            Attributes attributes = scriptTag.getAttributes();
            String type = attributes.getValue("type");
            String src = attributes.getValue("src");
            if (src != null)
                newScripts.add(new Script(type, new Url(src)));
        }
        if (!newScripts.equals(scripts)) {
//            System.out.println("scripts    = " + scripts);
//            System.out.println("newScripts = " + newScripts);

            for (Script script : scripts)
                getFrame().removeHeader(script);
            for (Script script : newScripts)
                getFrame().addHeader(script);

            scripts.clear();
            scripts.addAll(newScripts);
        }
    }
}