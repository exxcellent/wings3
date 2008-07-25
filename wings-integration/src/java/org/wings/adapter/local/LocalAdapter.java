package org.wings.adapter.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.wings.Resource;
import org.wings.SFrame;
import org.wings.STemplateLayout;
import org.wings.SimpleURL;
import org.wings.URLResource;
import org.wings.adapter.AbstractIntegrationAdapter;
import org.wings.conf.Integration;
import org.wings.header.Link;
import org.wings.header.Script;
import org.wings.resource.DynamicResource;
import org.wings.resource.ReloadResource;
import org.wings.session.SessionManager;
import org.wings.template.StringTemplateSource;
import org.wings.template.TemplateSource;

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
public class LocalAdapter extends AbstractIntegrationAdapter {

	protected Collection<Link> links = new ArrayList<Link>();
	protected Collection<Script> scripts = new ArrayList<Script>();
	
	private String path;
	
	private STemplateLayout layout;
	
	public LocalAdapter(SFrame frame, Integration integration, STemplateLayout layout) {
		super(frame, integration);
		
		this.layout = layout;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.wings.session.ResourceMapper#mapResource(java.lang.String)
	 */
	public Resource mapResource(String url) {
		if (!url.endsWith("html")) return null;

		return super.mapResource(url);
	}

	/* (non-Javadoc)
	 * @see org.wings.adapter.AbstractIntegrationAdapter#navigate(java.lang.String)
	 */
	protected void navigate(String url) throws IOException {
		ServletContext context = SessionManager.getSession().getServletContext();

		String path = context.getRealPath(integration.getBaseUrl() + url);
		File file = new File(path);

		int length = (int) file.length();
		byte[] bytes = new byte[length];
		new FileInputStream(file).read(bytes, 0, length);
		String templateString = new String(bytes, "UTF-8");

		// Add template to cache
		TemplateSource templateSource = new StringTemplateSource(templateString);
		setTemplate(templateSource);
	}

	/* (non-Javadoc)
	 * @see org.wings.template.ResourceResolver#getResource(java.lang.Object[])
	 */
	public Object getResource(Object... params) throws IOException {
		return getResource(null, params);
	}

	/* (non-Javadoc)
	 * @see org.wings.template.ResourceResolver#getResource(java.lang.String, java.lang.Object[])
	 */
	public Object getResource(String type, Object... params) throws IOException {
		throw new UnsupportedOperationException("The getResource is currently not implemented.");
	}

	/**
	 * {@inheritDoc}
	 */
	private void parseTitle(Source source, OutputDocument output) {
		Element titleElement = source.findNextElement(0, "title");

		if (titleElement != null) {
			String title = titleElement.getTextExtractor().toString();
			frame.setTitle("LocalAdapter :: " + title);
		}
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

		/*
		 * String cmsServerPath = getConfiguration().getServerPath();
		 * 
		 * List<StartTag> imgTags = source.findAllStartTags(Tag.IMG); for
		 * (StartTag imgTag : imgTags) { Attributes attributes =
		 * imgTag.getAttributes(); Attribute attribute = attributes.get("src");
		 * if (attribute != null) { String value = attribute.getValue();
		 * 
		 * if (!value.startsWith(getConfiguration().getProtocol())) { value =
		 * cmsServerPath + "/" + value; }
		 * output.replace(attribute.getValueSegment(), value); } }
		 */
	}

	// /**
	// * {@inheritDoc}
	// */
	// public void parseLinks(Source source) {
	//
	// HttpServletRequest request =
	// SessionManager.getSession().getServletRequest();
	// Cms cms = getCms();
	//
	// Collection<Link> newLinks = new ArrayList<Link>();
	// Collection<StartTag> linkTags = source.findAllStartTags("link");
	// for (StartTag linkTag : linkTags) {
	// Attributes attributes = linkTag.getAttributes();
	// String rel = attributes.getValue("rel");
	// String rev = attributes.getValue("rev");
	// String type = attributes.getValue("type");
	// String target = attributes.getValue("target");
	// String href = attributes.getValue("href");
	// if (!href.startsWith("http")) {
	// String pathInfo = request.getPathInfo();
	// if (pathInfo.contains(".php")) pathInfo = pathInfo.substring(0,
	// pathInfo.lastIndexOf("/"));
	// href = cms.getBaseUrl().toExternalForm() + pathInfo + "/" + href;
	// }
	//
	// newLinks.add(new Link(rel, rev, type, target, new Url(href)));
	// }
	// if (!newLinks.equals(links)) {
	// // System.out.println("links    = " + links);
	// // System.out.println("newLinks = " + newLinks);
	//
	// for (Link link : links)
	// getFrame().removeHeader(link);
	// for (Link link : newLinks)
	// getFrame().addHeader(link);
	//
	// links.clear();
	// links.addAll(newLinks);
	// }
	// }

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
			if (src != null) newScripts.add(new Script(type, new Url(src)));
		}
		if (!newScripts.equals(scripts)) {
			// System.out.println("scripts    = " + scripts);
			// System.out.println("newScripts = " + newScripts);

			for (Script script : scripts)
				frame.removeHeader(script);
			for (Script script : newScripts)
				frame.addHeader(script);

			scripts.clear();
			scripts.addAll(newScripts);
		}
	}

	protected void setTemplate(TemplateSource templateSource) throws IOException {
		layout.setTemplate(templateSource);
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