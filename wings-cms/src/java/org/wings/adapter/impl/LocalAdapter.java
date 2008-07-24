package org.wings.adapter.impl;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.wings.Resource;
import org.wings.SFrame;
import org.wings.STemplateLayout;
import org.wings.adapter.AbstractCmsAdapter;
import org.wings.conf.Cms;
import org.wings.header.Link;
import org.wings.header.Script;
import org.wings.session.SessionManager;
import org.wings.template.StringTemplateSource;

import au.id.jericho.lib.html.Attribute;
import au.id.jericho.lib.html.Attributes;
import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.OutputDocument;
import au.id.jericho.lib.html.Source;
import au.id.jericho.lib.html.StartTag;
import au.id.jericho.lib.html.Tag;

/**
 * @author hengels
 * @version $Id
 */
public class LocalAdapter extends AbstractCmsAdapter {

    public LocalAdapter(SFrame frame, STemplateLayout layout, Cms cms) {
        super(frame, layout, cms);
    }

    public Resource mapResource(String url) {
        if (!url.endsWith("html"))
            return null;

        return super.mapResource(url);
    }

    protected void navigate(String url) {
        ServletContext context = SessionManager.getSession().getServletContext();

        String path = context.getRealPath(getCms().getBaseUrl() + url);
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
            StringTemplateSource templateSource;
            if (cached) {
                templateSource = contentMap.get(url);
            }
            else {
                int length = (int)file.length();
                byte[] bytes = new byte[length];
                new FileInputStream(file).read(bytes, 0, length);
                templateString = new String(bytes, "UTF-8");

                // Add template to cache
                templateSource = new StringTemplateSource(templateString);
                contentMap.put(url, templateSource);
                obtainedMap.put(url, new Date());
            }
            setTemplate(templateSource);
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
        String cmsServerPath = getCms().getBaseUrl().toExternalForm();

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

        /*
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
        */
    }

    /**
     * {@inheritDoc}
     */
    public void parseLinks(Source source) {

        HttpServletRequest request = SessionManager.getSession().getServletRequest();
        Cms cms = getCms();

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
                href = cms.getBaseUrl().toExternalForm() + pathInfo + "/" + href;
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