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


import org.wings.SComponent;
import org.wings.SDialog;
import org.wings.SRootContainer;
import org.wings.io.Device;
import org.wings.plaf.css.script.OnPageRenderedScript;
import org.wings.session.ScriptManager;

import java.io.IOException;

public class DialogCG extends WindowCG implements org.wings.plaf.DialogCG {

    public static final String CSS_ASSETS_CONTAINER = "CSS.yuiAssetsContainer";

    public void writeInternal(Device device, SComponent component) throws IOException {

        SDialog dialog = (SDialog) component;
        SRootContainer owner = dialog.getOwner();

        String name = dialog.getName();

        device.print("\n\n" +
                "<div id=\"" + name + "\">\n" +
                "  <div class=\"hd\">" + (dialog.getTitle() != null ? dialog.getTitle() : "&#160;") + "</div>\n" +
                "  <div class=\"bd\">");

        super.writeInternal(device, dialog);

        device.print("  </div>\n" +
                "</div>\n");

        StringBuilder sb = new StringBuilder();
        sb.append("\n\n" +
                "var " + name + " = new wingS.dialog.SDialog(\"" + name + "\", { " +
                "  visible:" + dialog.isVisible() + ", " +
                "  modal:" + dialog.isModal() + "," +
                "  draggable:" + dialog.isDraggable() + ", " +
                "  close: false," +
                "  constraintoviewport:true," +
                "  viewportelement:\"" + owner.getName() + "\"," +
                "  fixedcenter: true" +
                "} );\n" +
                name + ".render();\n" +
//                name + ".show();" +
                "\n\n");

//        sb.append(owner.getName() + "_overlay_manager.register(" + name + ");\n\n");

        ScriptManager.getInstance().addScriptListener(new OnPageRenderedScript(sb.toString()));
    }
}
