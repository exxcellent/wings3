/*
 * $Id: CSSLookAndFeel.java 3059 2006-11-14 20:41:26Z cjschyma $
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

package org.wingx.plaf.css.msie;

import org.wings.SComponent;
import org.wings.SIcon;
import org.wings.SResourceIcon;
import org.wings.io.Device;
import org.wings.plaf.css.Utils;

import java.io.IOException;

public class ColorPickerCG extends org.wingx.plaf.css.ColorPickerCG {

    private final SIcon icon = new SResourceIcon("org/wings/icons/pickerbg.png");

    /**
     * Specialized CG to support transparent PNGs for IE < 7.
     */
    public ColorPickerCG() {
        headers.set(0, Utils.createExternalizedCSSHeader("org/wingx/colorpicker/hsvcolorpicker_lt_ie7.css"));
    }

    @Override
    protected void printPicker(Device device, SComponent component) throws IOException {
        device.print("<div");
        Utils.optAttribute(device, "id", (String)component.getClientProperty("pickerId"));
        Utils.optAttribute(device, "class", "hsvcolorpicker pickerDiv");
        device.print(">");
        device.print("<div");
        Utils.optAttribute(device, "class", "hsvcolorpicker pickerbg");
        Utils.optAttribute(device, "style", "filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='" + icon.getURL() + "', sizingMethod='scale')");
        device.print("></div>");
        device.print("<div");
        Utils.optAttribute(device, "id", (String)component.getClientProperty("selectorId"));
        Utils.optAttribute(device, "class", "hsvcolorpicker selector");
        device.print("></div>");
        device.print("</div>");
    }

    @Override
    public void installCG(final SComponent comp) {
        super.installCG(comp);
        comp.addParentFrameListener(this);
    }

}
