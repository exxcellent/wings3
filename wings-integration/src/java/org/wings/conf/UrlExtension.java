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
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

/**
 * <code>UrlExtension</code>.
 * 
 * <pre>
 * Date: Jul 24, 2008
 * Time: 2:22:26 PM
 * </pre>
 *
 * @author <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman R&auml;dle</a>
 * @version $Id
 */
@XmlType(name = "url-extension")
@XmlAccessorType(XmlAccessType.NONE)
public class UrlExtension {

	@XmlAttribute(name = "type")
	private String type;

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}
	
	@XmlValue
	private String value;

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	public List<String> getVariables() {
		
		List<String> variables = new ArrayList<String>();

		Pattern pattern = Pattern.compile("(\\{.*?\\})");
		Matcher matcher = pattern.matcher(value);
		while (matcher.find()) {
			MatchResult matchResult = matcher.toMatchResult();
			String match = matchResult.group();

			variables.add(match.substring(1, match.length() - 1));
		}

		return variables;
	}

	public String getReplacedValue(String[] values) {

		if (values.length == 0) {
			return "";
		}

		Pattern pattern = Pattern.compile("(\\{.*?\\})");
		Matcher matcher = pattern.matcher(value);

		StringBuffer urlExtension = new StringBuffer();
		for (int i = 0; matcher.find(); i++) {
			try {
				matcher.appendReplacement(urlExtension, values[i]);
			}
			catch (IndexOutOfBoundsException e) {
				e.printStackTrace();
			}
		}
		matcher.appendTail(urlExtension);

		return urlExtension.toString();
	}
}
