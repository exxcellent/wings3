package org.wings.adapter.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.wings.CmsFrame;
import org.wings.SFrame;
import org.wings.STemplateLayout;
import org.wings.adapter.AbstractCmsAdapter;
import org.wings.conf.Cms;
import org.wings.header.Script;

import au.id.jericho.lib.html.Attribute;
import au.id.jericho.lib.html.Attributes;
import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.OutputDocument;
import au.id.jericho.lib.html.Source;
import au.id.jericho.lib.html.StartTag;
import au.id.jericho.lib.html.Tag;

/**
 * <code>JoomlaAdapter<code>.
 * <p/>
 * User: rrd
 * Date: 08.08.2007
 * Time: 08:40:54
 *
 * @author rrd
 * @version $Id
 */
public class Joomla15Adapter extends AbstractCmsAdapter {

    public Joomla15Adapter(SFrame frame, STemplateLayout layout, Cms cms) {
        super(frame, layout, cms);
    }

    /**
     * {@inheritDoc}
     */
    public void parseTitle(Source source) {
        Element titleElement = source.findNextElement(0, "title");

        if (titleElement != null) {
            String title = titleElement.getTextExtractor().toString();
            getFrame().setTitle("Joomla15Adapter :: " + title);
        }
    }

    /* (non-Javadoc)
	 * @see org.wings.adapter.CmsAdapter#parseHeader(au.id.jericho.lib.html.Source)
	 */
	public void parseHeader(Source source, OutputDocument output) {
		Element head = source.findNextElement(0, Tag.HEAD);
		
		source = new Source(head.getContent());
		
		output = new OutputDocument(source);
		parseLinks(source, output);
		parseScripts(source);
		
		((CmsFrame) getFrame()).setHeadExtension(String.valueOf(output.toString()));
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

                boolean changed = false;
                if (!value.startsWith("http")) {
            		changed = true;
            		
                	Pattern pattern = Pattern.compile(getCms().getBaseUrl().getPath());
                	Matcher matcher = pattern.matcher(value);
                	if (matcher.find()) {
                		value = matcher.replaceFirst(wingsServerPath);
                	}
                	else {
                		if (!value.startsWith("/")) {
                			value = wingsServerPath + "/" + value;
                		}
                		else {
                			value = wingsServerPath + value;
                		}
                    }
				}
            	
				if (changed) {
					output.replace(attribute.getValueSegment(), value);
				}
            }
        }

        List<StartTag> formTags = source.findAllStartTags(Tag.FORM);
        for (StartTag formTag : formTags) {
            Attributes attributes = formTag.getAttributes();
            Attribute attribute = attributes.get("action");
            if (attribute != null) {
                String value = attribute.getValue();

                boolean changed = false;
                if (!value.startsWith("http")) {
                	changed = true;
                	
                	Pattern pattern = Pattern.compile(getCms().getBaseUrl().getPath());
                	Matcher matcher = pattern.matcher(value);
                	if (matcher.find()) {
                		value = matcher.replaceFirst(wingsServerPath);
                	}
                	else {
                		if (!value.startsWith("/")) {
                			value = wingsServerPath + "/" + value;
                		}
                		else {
                			value = wingsServerPath + value;
                		}
                    }
				}
            	
				if (changed) {
					output.replace(attribute.getValueSegment(), value);
				}
            }
        }
    }

    public void parseImages(Source source, OutputDocument output) {

    	String cmsServerPath = getCms().getBaseUrl().toExternalForm();

        List<StartTag> imgTags = source.findAllStartTags(Tag.IMG);
        for (StartTag imgTag : imgTags) {
            Attributes attributes = imgTag.getAttributes();
            Attribute attribute = attributes.get("src");
            if (attribute != null) {
                String value = attribute.getValue();

                boolean changed = false;
                if (!value.startsWith("http")) {
                	changed = true;
                	
                	Pattern pattern = Pattern.compile(getCms().getBaseUrl().getPath());
                	Matcher matcher = pattern.matcher(value);
                	if (matcher.find()) {
                		value = matcher.replaceFirst(cmsServerPath);
                	}
                	else {
                		if (!value.startsWith("/")) {
                			value = cmsServerPath + "/" + value;
                		}
                		else {
                			value = cmsServerPath + value;
                		}
                    }
				}
                
                if (changed) {
					output.replace(attribute.getValueSegment(), value);
				}
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    private void parseLinks(Source source, OutputDocument output) {

    	String cmsServerPath = getCms().getBaseUrl().toExternalForm();

        Collection<StartTag> linkTags = source.findAllStartTags("link");
        for (StartTag linkTag : linkTags) {
            Attributes attributes = linkTag.getAttributes();
            String rel = attributes.getValue("rel");
            String rev = attributes.getValue("rev");
            String type = attributes.getValue("type");
            String target = attributes.getValue("target");
            String href = attributes.getValue("href");
            
            boolean changed = false;
            if (!href.startsWith("http")) {
            	changed = true;
            	
            	Pattern pattern = Pattern.compile(getCms().getBaseUrl().getPath());
            	Matcher matcher = pattern.matcher(href);
            	if (matcher.find()) {
            		href = matcher.replaceFirst(cmsServerPath);
            	}
            	else {
            		if (!href.startsWith("/")) {
            			href = cmsServerPath + "/" + href;
            		}
            		else {
            			href = cmsServerPath + href;
            		}
                }
			}
            
            if (changed) {
				output.replace(attributes.get("href").getValueSegment(), href);
			}
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