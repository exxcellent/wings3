package org.wings.adapter.impl;

import java.util.ArrayList;
import java.util.List;

import org.wings.CmsFrame;
import org.wings.SFrame;
import org.wings.STemplateLayout;
import org.wings.adapter.AbstractCmsAdapter;
import org.wings.conf.Cms;
import org.wings.header.Script;
import org.wings.util.HtmlParserUtils;

import au.id.jericho.lib.html.Attribute;
import au.id.jericho.lib.html.Attributes;
import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.OutputDocument;
import au.id.jericho.lib.html.Source;
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

	/**
	 * @param frame
	 * @param layout
	 * @param cms
	 */
	public Joomla15Adapter(SFrame frame, STemplateLayout layout, Cms cms) {
		super(frame, layout, cms);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.wings.adapter.CmsAdapter#parseHead(au.id.jericho.lib.html.Source)
	 */
	public Source parseHead(Source headSource) {
		
		headSource = prepareTitle(headSource);
		headSource = HtmlParserUtils.removeAllTags(headSource, Tag.META);
		headSource = prepareLinks(headSource);
		headSource = prepareScripts(headSource);

		((CmsFrame) getFrame()).setHeadExtension(headSource.toString().trim());
		
		return headSource;
	}

	/* (non-Javadoc)
	 * @see org.wings.adapter.CmsAdapter#parseBody(au.id.jericho.lib.html.Source)
	 */
	public Source parseBody(Source bodySource) {

		bodySource = HtmlParserUtils.removeAllTags(bodySource, Tag.FORM);
		bodySource = prepareAnchors(bodySource);
		bodySource = prepareImages(bodySource);
		
		return bodySource;
	}

	/**
	 * @param source
	 * @param output
	 */
	private Source prepareTitle(Source headSource) {
		Element titleElement = headSource.findNextElement(0, Tag.TITLE);

		if (titleElement != null) {
			String title = titleElement.getTextExtractor().toString();
			getFrame().setTitle("Joomla15Adapter :: " + title);
			
			return HtmlParserUtils.removeAllTags(headSource, Tag.TITLE);
		}
		return headSource;
	}

	/**
	 * @param headSource
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Source prepareLinks(Source headSource) {

		List<Element> links = headSource.findAllElements(Tag.LINK);
		if (links.size() < 1) {
			return headSource;
		}
		
		OutputDocument outputDocument = new OutputDocument(headSource);
		for (Element link : links) {
			Attributes attributes = link.getAttributes();
			Attribute attribute = attributes.get("href");
			if (attribute != null) {
				String value = attribute.getValue();
				outputDocument.replace(attribute.getValueSegment(), HtmlParserUtils.convertURLHost(value, getCmsBaseUrl().getPath(), getCmsBaseUrl().toExternalForm()));
			}
		}
		return new Source(outputDocument.toString());
	}

	/**
	 * @param headSource
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Source prepareScripts(Source headSource) {
		
		List<Element> scripts = headSource.findAllElements(Tag.SCRIPT);
		if (scripts.size() < 1) {
			return headSource;
		}
		
		List<Script> newScripts = new ArrayList<Script>();
		for (Element script : scripts) {
			Attributes attributes = script.getAttributes();
			String type = attributes.getValue("type");
			String src = attributes.getValue("src");
			if (src != null) {
				newScripts.add(new Script(type, new Url(src)));
			}
		}
		if (!newScripts.equals(this.scripts)) {
			for (Script script : this.scripts) {
				getFrame().removeHeader(script);
			}
			for (Script script : newScripts) {
				getFrame().addHeader(script);
			}

			this.scripts.clear();
			this.scripts.addAll(newScripts);
		}
		return headSource;
	}

	/**
	 * @param bodySource
	 */
	@SuppressWarnings("unchecked")
	private Source prepareAnchors(Source bodySource) {
		List<Element> anchors = bodySource.findAllElements(Tag.A);
		
		if (anchors.size() < 1) {
			return bodySource;
		}
		
		OutputDocument outputDocument = new OutputDocument(bodySource);
		for (Element anchor : anchors) {
			Attributes attributes = anchor.getAttributes();
			Attribute attribute = attributes.get("href");
			if (attribute != null) {
				String value = attribute.getValue();
				outputDocument.replace(attribute.getValueSegment(), HtmlParserUtils.convertURLHost(value, getCmsBaseUrl().getPath(), getPath()));
			}
		}
		return new Source(outputDocument.toString());
	}

	/**
	 * @param bodySource
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Source prepareImages(Source bodySource) {
		List<Element> images = bodySource.findAllElements(Tag.IMG);
		
		if (images.size() < 1) {
			return bodySource;
		}
		
		OutputDocument outputDocument = new OutputDocument(bodySource);
		for (Element image : images) {
			Attributes attributes = image.getAttributes();
			Attribute attribute = attributes.get("src");
			if (attribute != null) {
				String value = attribute.getValue();
				outputDocument.replace(attribute.getValueSegment(), HtmlParserUtils.convertURLHost(value, getCmsBaseUrl().getPath(), getCmsBaseUrl().toExternalForm()));
			}
		}
		return new Source(outputDocument.toString());
	}
}