package org.wings.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.id.jericho.lib.html.Element;
import au.id.jericho.lib.html.OutputDocument;
import au.id.jericho.lib.html.Source;

/**
 * <code>HtmlParserUtils</code>.
 * 
 * <pre>
 * Date: Jul 25, 2008
 * Time: 9:45:50 AM
 * </pre>
 * 
 * @author <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman R&auml;dle</a>
 * @version $Id
 */
public class HtmlParserUtils {

	/**
	 * Returns true whether the tag is present within the source or not.
	 * 
	 * @param source
	 *            The source that should contain the tag at least once.
	 * @param tagName
	 *            The name of the tag.
	 * @return True if the tag is present and false if not.
	 */
	public static boolean isTagPresent(final Source source, final String tagName) {
		Element tag = source.findNextElement(0, tagName);
		return tag != null;
	}

	/**
	 * Returns an array of sources with the content of each tag named as
	 * parameter.
	 * 
	 * @param source
	 *            The source that may contain the tag(s).
	 * @param tagName
	 *            The name of the tag.
	 * @return An array of source and each contains the content of the tag named
	 *         as parameter.
	 */
	@SuppressWarnings("unchecked")
	public static Source[] getSourcesForTag(final Source source, final String tagName) {
		List<Element> elements = source.findAllElements(tagName);

		List<Source> sources = new ArrayList<Source>(elements.size());
		for (Element element : elements) {
			sources.add(new Source(element.getContent()));
		}
		return sources.toArray(new Source[sources.size()]);
	}

	/**
	 * Removes all tags of a source and returns the result as new source.
	 * 
	 * @param source
	 *            The source that contains all tags to be removed.
	 * @param tagName
	 *            The tag(s) that will be removed.
	 * @return The same source as the parameter source except the removed tags.
	 */
	@SuppressWarnings("unchecked")
	public static Source removeAllTags(final Source source, final String tagName) {

		OutputDocument outputDocument = new OutputDocument(source);

		List<Element> metas = source.findAllElements(tagName);
		outputDocument.remove(metas);

		return new Source(outputDocument.toString());
	}

	public static String convertURLHost(String url, String pattern, String replacement) {
		return convertURLHost("http", url, pattern, replacement);
	}

	public static String convertURLHost(String protocol, String url, String pattern, String replacement) {

		if (!url.startsWith(protocol)) {

			Pattern p = Pattern.compile(pattern);
			Matcher m = p.matcher(url);
			if (m.find()) {
				return m.replaceFirst(replacement);
			}
			else {
				if (!url.startsWith("/")) {
					return replacement + "/" + url;
				}
				else {
					return replacement + url;
				}
			}
		}
		return url;
	}
}
