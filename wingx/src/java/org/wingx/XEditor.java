/**
 * 
 */
package org.wingx;

import org.wings.SComponent;
import org.wings.plaf.ComponentCG;
import org.wingx.plaf.EditorCG;

/**
 * <code>XEditor</code>.
 * 
 * <pre>
 * Date: Apr 1, 2008
 * Time: 7:38:35 PM
 * </pre>
 *
 * @author <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman R&auml;dle</a>
 * @version $Id
 */
public class XEditor extends SComponent {
	
	public XEditor() {
		setCG(getSession().getCGManager().getCG(XEditor.class));
	}
}
