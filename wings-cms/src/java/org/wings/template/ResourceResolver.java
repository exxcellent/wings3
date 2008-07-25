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
public interface ResourceResolver {

	public Object getResource(Object... params) throws IOException;

	public Object getResource(String type, Object... params) throws IOException;
}
