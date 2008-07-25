/**
 * 
 */
package org.wingx.plaf.css;

import java.io.IOException;

import org.wings.SComponent;
import org.wings.io.Device;
import org.wings.plaf.Update;
import org.wings.plaf.css.AbstractComponentCG;
import org.wings.plaf.css.script.OnPageRenderedScript;
import org.wings.session.ScriptManager;
import org.wingx.XEditor;

/**
 * <code>EditorCG</code>.
 * 
 * <pre>
 * Date: Apr 1, 2008
 * Time: 7:39:52 PM
 * </pre>
 *
 * @author <a href="mailto:Roman.Raedle@uni-konstanz.de">Roman R&auml;dle</a>
 * @version $Id
 */
public class EditorCG extends AbstractComponentCG<XEditor> implements
		org.wingx.plaf.EditorCG {

	/* (non-Javadoc)
	 * @see org.wings.plaf.css.AbstractComponentCG#writeInternal(org.wings.io.Device, org.wings.SComponent)
	 */
	@Override
	public void writeInternal(Device device, XEditor component)
			throws IOException {
		
		String name = component.getName();
		
		device.print("<textarea name=\"").print(name).print("_editor\" id=\"").print(name).print("_editor\" cols=\"50\" rows=\"10\">");
	    device.print("</textarea>");
	    
	    StringBuilder js = new StringBuilder();
	    
	    js.append("var ").append(name).append("_editor = new YAHOO.widget.Editor('").append(name).append("_editor', {")
	        .append("height:'300px',")
	        .append("width:'522px',")
	        .append("dompath:true,")
	        .append("animate:true")
	    	.append("});\n");
	    js.append(name).append("_editor.render();\n");
	    
	    ScriptManager.getInstance().addScriptListener(new OnPageRenderedScript(js.toString()));
	}
}
