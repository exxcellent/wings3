/**
 * 
 */
package org.wings.conf;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.wings.macro.MacroProcessor;

/**
 * <code>TemplateDetail</code>.
 * 
 * <pre>
 * Date: Jul 23, 2008
 * Time: 5:32:54 PM
 * </pre>
 * 
 * @author <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman R&auml;dle</a>
 * @version $Id
 */
@XmlType(name = "resource")
@XmlAccessorType(XmlAccessType.NONE)
public class Resource {

	@XmlAttribute(name = "processor", required = true)
	@XmlJavaTypeAdapter(ClassAdapter.class)
	private Class<? extends MacroProcessor> macroProcessor;

	/**
	 * @return the macroProcessor
	 */
	public Class<? extends MacroProcessor> getMacroProcessor() {
		return macroProcessor;
	}

	/**
	 * @param macroProcessor
	 *            the macroProcessor to set
	 */
	public void setMacroProcessor(Class<? extends MacroProcessor> macroProcessor) {
		this.macroProcessor = macroProcessor;
	}

	@XmlAttribute(name = "cache")
	private Boolean cache;

	/**
	 * @return the cache
	 */
	public Boolean getCache() {
		return cache;
	}

	/**
	 * @param cache
	 *            the cache to set
	 */
	public void setCache(Boolean cache) {
		this.cache = cache;
	}

	@XmlElement(name = "url-extension", required = false)
	private Set<UrlExtension> urlExtensions;

	/**
	 * @return the urlExtensions
	 */
	public Set<UrlExtension> getUrlExtensions() {
		return urlExtensions;
	}

	/**
	 * @param urlExtensions
	 *            the urlExtensions to set
	 */
	public void setUrlExtensions(Set<UrlExtension> urlExtensions) {
		this.urlExtensions = urlExtensions;
	}
	
	/**
	 * Returns the default URL extension (default: type == null).
	 * 
	 * @return The default URL extension.
	 */
	public UrlExtension getDefaultUrlExtension() {
		return getUrlExtension(null);
	}

	/**
	 * Either returns the URL extension of a matching type or null if
	 * no extensions have been set or no matching type has been found.
	 * 
	 * @param type The type of the URL extension.
	 * @return Either the matching URL extension or null.
	 */
	public UrlExtension getUrlExtension(String type) {
		if (urlExtensions == null || urlExtensions.size() < 1) {
			return null;
		}
		
		String extensionType;
		for (UrlExtension urlExtension : urlExtensions) {
			extensionType = urlExtension.getType();
			if (type == null && extensionType == null) {
				return urlExtension;
			}
			else if (extensionType != null && urlExtension.getType().equals(type)) {
				return urlExtension;
			}
		}
		return null;
	}
}
