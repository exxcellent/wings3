/*
 * Copyright 2000,2005 wingS development team.
 *
 * This file is part of wingS (http://wingsframework.org).
 *
 * wingS is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * Please see COPYING for the complete licence.
 */
package org.wings.plaf.css;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wings.*;
import org.wings.dnd.DragAndDropManager;
import org.wings.io.Device;
import org.wings.plaf.css.script.OnPageRenderedScript;
import org.wings.resource.ResourceNotFoundException;
import org.wings.script.*;
import org.wings.session.*;

import java.io.IOException;
import java.util.*;

/**
 * PLAF renderer for SFrames in Portlets.
 * 
 * @author Marc Musch
 */
public final class PortletFrameCG extends FrameCG {

	private final static Log log = LogFactory.getLog(PortletFrameCG.class);
	
	@Override
	public void write(Device device, SComponent component) throws IOException {
        final SFrame frame = (SFrame) component;

        String strokes = strokes(frame.getGlobalInputMapComponents());
        if (strokes != null)
            component.getSession().getScriptManager().addScriptListener(new JavaScriptListener(null, null, strokes));

        if (!frame.isVisible())
            return;
        else
            frame.fireRenderEvent(SComponent.START_RENDERING);
        
        device.print("<div class=\"wingsbody\"");
        Utils.writeEvents(device, frame, null);
        Utils.writeAllAttributes(device, frame);
        device.print(">\n");
        
        // Render all headers
        for (Object next : frame.getHeaders()) {
            if (next instanceof Renderable) {
                try {
                    ((Renderable) next).write(device);
                } catch (ResourceNotFoundException e) {
                    log.error("Unable to deliver inlined renderable", e);
                }
            } else {
                Utils.write(device, next.toString());
            }
            device.print("\n");
        }

        // Focus management. Put focus in selected object.
        if (frame.getFocus() != null) {
            String script = "wingS.util.requestFocus('" + frame.getFocus().getName() + "');";
            ScriptManager.getInstance().addScriptListener(new OnPageRenderedScript(script));
        }

        // Write contents of the frame
        if (frame.isVisible()) {
            Utils.createExternalizedJSHeaderFromProperty(Utils.JS_ETC_WZ_TOOLTIP).write(device);
            device.print("\n");

            // Write components
            frame.getLayout().write(device);

            // Write menus
            device.print("\n\n<div id=\"wings_menues\">\n");
            Set menues = frame.getSession().getMenuManager().getMenues(frame);
            for (Iterator i = menues.iterator(); i.hasNext();) {
                SComponent menuItem = (SComponent) i.next();
                menuItem.putClientProperty("popup", Boolean.TRUE);
                menuItem.write(device);
                menuItem.putClientProperty("popup", null);
            }
            device.print("\n</div>\n\n");

            DragAndDropManager dndManager = frame.getSession().getDragAndDropManager();
            dndManager.getCG().write(device, dndManager);

            handleScripts(device, frame);
        }

        // override js method for portlet
        device.print("<script type=\"text/javascript\">\n"+
        	"wingS.util.getReloadResource = function() {\n" + 
        	"var splits = new Array();\n" + 
        	"splits = wingS.global.reloadResource.split(\"org.wings.portlet.resourceAsParam=\");\n" +
        	"if(splits.length==2) {\n" + 
        	"return splits[0] + \"org.wings.portlet.resourceAsParam=\" + wingS.global.eventEpoch + \"-\" + splits[1];\n" +
        	"}\n" +
        	"else {\n" +
        	"return wingS.global.reloadResource\n" +
        	"}\n" +	     
        	"};)\n" +
        	"wingS.util.getUpdateResource = function() {\n" +
        	"var splits = new Array();\n" + 
        	"splits = wingS.global.reloadResource.split(\"org.wings.portlet.resourceAsParam=\");\n" +
        	"if(splits.length==2) {\n" + 
        	"return splits[0] + \"org.wings.portlet.resourceAsParam=\" + wingS.global.eventEpoch + \"-\" + splits[1];\n" +
        	"}\n" +
        	"else {\n" +
        	"return wingS.global.reloadResource\n" +
        	"}\n" +	     
        	"};)\n" + 
        	"</script>");
        
        device.print("</div>\n");

        component.fireRenderEvent(SComponent.DONE_RENDERING);

	}

   

}
