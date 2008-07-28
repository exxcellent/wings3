package org.wings.adapter.cms.joomla;

import java.util.ArrayList;
import java.util.List;

import org.wings.IntegrationFrame;
import org.wings.TemplateIntegrationFrame;
import org.wings.conf.Integration;
import org.wings.header.Script;
import org.wings.util.HtmlParserUtils;

import au.id.jericho.lib.html.Attribute;
import au.id.jericho.lib.html.Attributes;
import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.OutputDocument;
import au.id.jericho.lib.html.Source;
import au.id.jericho.lib.html.Tag;

/**
 * <code>Joomla15Adapter<code>.
 * <p/>
 * User: rrd
 * Date: 08.08.2007
 * Time: 08:40:54
 * 
 * @author rrd
 * @version $Id
 */
public class Joomla15Adapter extends AbstactJoomlaAdapter {

	/**
	 * @param frame
	 * @param layout
	 * @param integration
	 */
	public Joomla15Adapter(IntegrationFrame frame, Integration integration) {
		super(frame, integration);
	}
	
	public Source parseHead(Source headSource) {
		
		headSource = prepareTitle(headSource);
		headSource = HtmlParserUtils.removeAllTags(headSource, Tag.META);
		headSource = prepareLinks(headSource);
		headSource = prepareScripts(headSource);

		((TemplateIntegrationFrame) frame).setHeadExtension(headSource.toString().trim());
		
		return headSource;
	}
	
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
			frame.setTitle(title);
			
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
				outputDocument.replace(attribute.getValueSegment(), HtmlParserUtils.convertURLHost(value, getIntegrationBaseUrl().getPath(), getIntegrationBaseUrl().toExternalForm()));
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
				frame.removeHeader(script);
			}
			for (Script script : newScripts) {
				frame.addHeader(script);
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
				outputDocument.replace(attribute.getValueSegment(), HtmlParserUtils.convertURLHost(value, getIntegrationBaseUrl().getPath(), getPath()));
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
				outputDocument.replace(attribute.getValueSegment(), HtmlParserUtils.convertURLHost(value, getIntegrationBaseUrl().getPath(), getIntegrationBaseUrl().toExternalForm()));
			}
		}
		return new Source(outputDocument.toString());
	}
}