/**
 * 
 */
package org.wings.conf;

import java.util.ArrayList;
import java.util.List;
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
@XmlType(name = "template")
@XmlAccessorType(XmlAccessType.NONE)
public class Templates {

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
	 * @param macroProcessor the macroProcessor to set
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
	 * @param cache the cache to set
	 */
	public void setCache(Boolean cache) {
		this.cache = cache;
	}
	
	@XmlElement(name = "url-extension", required = false)
	private String urlExtension;

	/**
	 * @return the urlExtension
	 */
	public String getUrlExtension() {
		return urlExtension;
	}

	/**
	 * @param urlExtension the urlExtension to set
	 */
	public void setUrlExtension(String urlExtension) {
		this.urlExtension = urlExtension;
	}
	
	public List<String> getUrlExtensionVariables() {
		List<String> variables = new ArrayList<String>();
		
		Pattern pattern = Pattern.compile("(\\{.*?\\})");
		Matcher matcher = pattern.matcher(urlExtension);
		while (matcher.find()) {
			MatchResult matchResult = matcher.toMatchResult();
			String match = matchResult.group();
			
			variables.add(match.substring(1, match.length() - 1));
		}
		
		return variables;
	}
	
	public String getUrlExtension(String[] values) {
		
		if (values.length == 0) {
			return "";
		}
		
		Pattern pattern = Pattern.compile("(\\{.*?\\})");
		Matcher matcher = pattern.matcher(urlExtension);
		
		StringBuffer urlExtension = new StringBuffer();
		for (int i = 0 ; matcher.find(); i++) {
			matcher.appendReplacement(urlExtension, values[i]);
		}
		matcher.appendTail(urlExtension);
		
		return urlExtension.toString();
	}
}
