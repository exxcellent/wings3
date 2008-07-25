/**
 * 
 */
package org.wings.adapter.parser;

import au.id.jericho.lib.html.Source;

/**
 * <code>HtmlParser</code>.
 * 
 * <pre>
 * Date: Jul 25, 2008
 * Time: 6:17:39 PM
 * </pre>
 *
 * @author <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman R&auml;dle</a>
 * @version $Id
 */
public interface HtmlParser {

	public Source parseHead(Source headSource);
    
	public Source parseBody(Source bodySource);
    
	public Source resolveIncludes(Source source);
}
