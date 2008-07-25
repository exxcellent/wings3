/**
 * 
 */
package org.wings.template;

import java.io.IOException;

/**
 * <code>TemplateResolver</code>.
 * 
 * <pre>
 * Date: Jul 25, 2008
 * Time: 4:55:17 PM
 * </pre>
 * 
 * @author <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman R&auml;dle</a>
 * @version $Id
 */
public interface TemplateResolver {

	/**
	 * Returns a template that matches with the given parameter name.
	 * 
	 * @param name
	 *            The name of the template.
	 * @return The resolved template.
	 * @throws The
	 *             IOException can occur while trying to resolve the template.
	 */
	public String getTemplate(String name) throws IOException;

	/**
	 * Returns a template that matches with the given parameter name and a type
	 * parameter to differ allow groups of templates.
	 * 
	 * @param name
	 *            The name of the template.
	 * @param type
	 *            The type of a template.
	 * @return The resolved template.
	 * @throws The
	 *             IOException can occur while trying to resolve the template.
	 */
	public String getTemplate(String name, String type) throws IOException;
}
