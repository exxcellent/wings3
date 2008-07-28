package org.wings.adapter.cms.joomla;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.wings.IntegrationFrame;
import org.wings.conf.Integration;
import org.wings.header.Script;

import au.id.jericho.lib.html.Attribute;
import au.id.jericho.lib.html.Attributes;
import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.OutputDocument;
import au.id.jericho.lib.html.Source;
import au.id.jericho.lib.html.StartTag;
import au.id.jericho.lib.html.Tag;

/**
 * <code>Joomla10Adapter<code>.
 * <p/>
 * User: rrd
 * Date: 08.08.2007
 * Time: 08:40:54
 *
 * @author rrd
 * @version $Id
 */
public class Joomla10Adapter extends AbstactJoomlaAdapter {

    public Joomla10Adapter(IntegrationFrame frame, Integration integration) {
        super(frame, integration);
    }

    /**
     * {@inheritDoc}
     */
    public void parseTitle(Source source, OutputDocument output) {
        Element titleElement = source.findNextElement(0, "title");

        if (titleElement != null) {
            String title = titleElement.getTextExtractor().toString();
            frame.setTitle(title);
        }
    }
    
	public Source parseHead(Source source) {
		return source;
	}
	
	public Source parseBody(Source source) {
		return source;
	}

	public void parseAnchors(Source source, OutputDocument output) {

        String wingsServerPath = getPath();
        String cmsServerPath = integration.getBaseUrl().toExternalForm();

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

        String cmsServerPath = integration.getBaseUrl().toExternalForm();

        List<StartTag> imgTags = source.findAllStartTags(Tag.IMG);
        for (StartTag imgTag : imgTags) {
            Attributes attributes = imgTag.getAttributes();
            Attribute attribute = attributes.get("src");
            if (attribute != null) {
                String value = attribute.getValue();

                if (!value.startsWith(integration.getBaseUrl().getProtocol())) {
                    value = cmsServerPath + "/" + value;
                }
                output.replace(attribute.getValueSegment(), value);
            }
        }
    }

//    /**
//     * {@inheritDoc}
//     */
//    public void parseLinks(Source source) {
//
//        HttpServletRequest request = SessionManager.getSession().getServletRequest();
//        Cms cms = getCms();
//
//        Collection<Link> newLinks = new ArrayList<Link>();
//        Collection<StartTag> linkTags = source.findAllStartTags("link");
//        for (StartTag linkTag : linkTags) {
//            Attributes attributes = linkTag.getAttributes();
//            String rel = attributes.getValue("rel");
//            String rev = attributes.getValue("rev");
//            String type = attributes.getValue("type");
//            String target = attributes.getValue("target");
//            String href = attributes.getValue("href");
//            if (!href.startsWith("http")) {
//                String pathInfo = request.getPathInfo();
//                if (pathInfo.contains(".php")) pathInfo = pathInfo.substring(0, pathInfo.lastIndexOf("/"));
//                href = cms.getBaseUrl() + pathInfo + "/" + href;
//            }
//
//            newLinks.add(new Link(rel, rev, type, target, new Url(href)));
//        }
//        if (!newLinks.equals(links)) {
////            System.out.println("links    = " + links);
////            System.out.println("newLinks = " + newLinks);
//
//            for (Link link : links)
//                getFrame().removeHeader(link);
//            for (Link link : newLinks)
//                getFrame().addHeader(link);
//
//            links.clear();
//            links.addAll(newLinks);
//        }
//    }

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
            	frame.removeHeader(script);
            for (Script script : newScripts)
            	frame.addHeader(script);

            scripts.clear();
            scripts.addAll(newScripts);
        }
    }
}