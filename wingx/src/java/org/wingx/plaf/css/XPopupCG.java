/*
 * Copyright 2000,2006 wingS development team.
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
package org.wingx.plaf.css;


import org.wings.SComponent;
import org.wings.header.SessionHeaders;
import org.wings.io.Device;
import java.io.IOException;
import org.wings.SDimension;
import org.wings.header.Header;
import org.wings.plaf.css.FormCG;
import org.wings.plaf.css.Utils;
import org.wings.plaf.css.script.OnHeadersLoadedScript;
import org.wings.session.ScriptManager;
import org.wingx.XPopup;

/**
 * CG for SPopup instances.
 */
public final class XPopupCG extends FormCG implements org.wingx.plaf.XPopupCG {

    private static final long serialVersionUID = 1L;

    private Header header;

    private static final SDimension DEFAULT_DIMENSION = new SDimension(400, 300);

    public XPopupCG() {
        header = Utils.createExternalizedJSHeader("org/wingx/popup/popup.js");
        SessionHeaders.getInstance().registerHeader(header);
    }

    private String getInitScript(XPopup popup) {
        StringBuilder code = new StringBuilder();
        String anchor = popup.isAnchored() ? popup.getAnchor().getName() : "";
        String corner = popup.isAnchored() ? popup.getCorner() : "";
        String name = "popup_" + popup.getName();
        SDimension dim = popup.getPreferredSize();
        if (dim == null) {
            dim = DEFAULT_DIMENSION;
        }
        String heightUnit = dim.getHeightUnit();
        if (heightUnit != null && !heightUnit.equals("px")) {
            throw new IllegalStateException("Only 'px' is a valid unit, but height was specified as " + dim.getHeight());
        }
        String widthUnit = dim.getWidthUnit();
        if (widthUnit != null && !widthUnit.equals("px")) {
            throw new IllegalStateException("Only 'px' is a valid unit, but width was specified as " + dim.getWidth());
        }
        code.append(name).
                append(" = new wingS.XPopup(").
                append("'").append(popup.getName()).append("', ").
                append(popup.getX() + ", ").
                append(popup.getY() + ", ").
                append(dim.getWidthInt()).append(", ").
                append(dim.getHeightInt()).append(", ").
                append("'").append(anchor).append("', ").
                append("'").append(corner).append("'").
                append(");");
        code.append(name).append(".show();");
        return code.toString();
    }

    @Override
    public void writeInternal(Device device, SComponent component) throws IOException {
        XPopup popup = (XPopup) component;
        // contents are written here
        device.print("<div id='outer_" + popup.getName() + "'>");
        super.writeInternal(device, popup);
        device.print("</div>");
        ScriptManager.getInstance().addScriptListener(new OnHeadersLoadedScript(getInitScript(popup)));
    }

}
