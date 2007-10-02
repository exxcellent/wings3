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

package org.wingx.plaf.css;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.wings.SComponent;
import org.wings.SIcon;
import org.wings.SResourceIcon;
import org.wings.event.SParentFrameEvent;
import org.wings.event.SParentFrameListener;
import org.wings.header.SessionHeaders;
import org.wings.io.Device;
import org.wings.plaf.css.AbstractComponentCG;
import org.wings.plaf.css.Utils;
import org.wings.plaf.css.dwr.CallableManager;
import org.wings.plaf.css.script.OnHeadersLoadedScript;
import org.wings.session.ScriptManager;
import org.wings.util.SStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wingx.XColorPicker;
import org.wingx.XColorPickerInterface;

public class ColorPickerCG extends AbstractComponentCG implements org.wingx.plaf.ColorPickerCG, SParentFrameListener {

    private final transient static Log log = LogFactory.getLog(ColorPickerCG.class);

    protected final List headers = new ArrayList(); 

    // these graphics are needed for hsvcolorpicker.css and hsvcolorpicker_lt_ie7.css
    static {
        String[] images = new String [] {
            "org/wingx/colorpicker/images/select.gif",
            "org/wingx/colorpicker/images/hline.png",
            "org/wingx/colorpicker/images/hue.png",
            "org/wingx/colorpicker/images/pickerbg.png"
        };

        for ( int x = 0, y = images.length ; x < y ; x++ ) {
            SIcon icon = new SResourceIcon(images[x]);
            icon.getURL();
        }
    }

    public ColorPickerCG() {
        // TODO export to properties file!
        headers.add(Utils.createExternalizedJSHeaderFromProperty(Utils.JS_YUI_SLIDER));
        headers.add(Utils.createExternalizedCSSHeader("org/wingx/colorpicker/hsvcolorpicker.css"));
        headers.add(Utils.createExternalizedJSHeader("org/wingx/colorpicker/hsvcolorpicker.js"));
        headers.add(Utils.createExternalizedJSHeader("org/wingx/colorpicker/color.js"));
    }

    /**
     * Preparation of DOM element ids. Every id has to be individual and
     * therefore the component id is used upfront.
     */
    public void prepareIds(SComponent component) {
        log.debug("preparing ids");

        String id = component.getName();

        component.putClientProperty("pickerPanelId", id);
        component.putClientProperty("pickerHandleId", (new SStringBuilder(id).append("_pickerHandle")).toString());
        component.putClientProperty("pickerId", (new SStringBuilder(id).append("_pickerDiv")).toString());
        component.putClientProperty("pickerSwatchId", (new SStringBuilder(id).append("_pickerSwatch")).toString());
        component.putClientProperty("selectorId", (new SStringBuilder(id).append("_selector")).toString());
        component.putClientProperty("hueBackgroundId", (new SStringBuilder(id).append("_hueBg")).toString());
        component.putClientProperty("hueThumbId", (new SStringBuilder(id).append("_hueThumb")).toString());
        component.putClientProperty("pickerFormName", (new SStringBuilder(id).append("_pickerform")).toString());

        component.putClientProperty("pickerRvalId", (new SStringBuilder(id).append("_pickerrval")).toString());
        component.putClientProperty("pickerGvalId", (new SStringBuilder(id).append("_pickergval")).toString());
        component.putClientProperty("pickerBvalId", (new SStringBuilder(id).append("_pickerbval")).toString());

        component.putClientProperty("pickerHvalId", (new SStringBuilder(id).append("_pickerhval")).toString());
        component.putClientProperty("pickerSvalId", (new SStringBuilder(id).append("_pickersval")).toString());
        component.putClientProperty("pickerVvalId", (new SStringBuilder(id).append("_pickervval")).toString());

        component.putClientProperty("pickerHexValId", (new SStringBuilder(id).append("_pickerhexval")).toString());
    }

    protected void printPicker(Device device, SComponent component) throws IOException {
        device.print("<div");
        Utils.optAttribute(device, "id", (String)component.getClientProperty("pickerId"));
        Utils.optAttribute(device, "class", "hsvcolorpicker pickerDiv");
        device.print(">");
        device.print("<div");
        Utils.optAttribute(device, "class", "hsvcolorpicker pickerbg");
        device.print("></div>");
        device.print("<div");
        Utils.optAttribute(device, "id", (String)component.getClientProperty("selectorId"));
        Utils.optAttribute(device, "class", "hsvcolorpicker selector");
        device.print("></div>");
        device.print("</div>");
    }

    private void printHueSlider(Device device, SComponent component) throws IOException {
        device.print("<div");
        Utils.optAttribute(device, "id", (String)component.getClientProperty("hueBackgroundId"));
        Utils.optAttribute(device, "class", "hsvcolorpicker hueBg");
        device.print(">");
        device.print("<div");
        Utils.optAttribute(device, "id", (String)component.getClientProperty("hueThumbId"));
        Utils.optAttribute(device, "class", "hsvcolorpicker hueThumb");
        device.print("></div>");
        device.print("</div>");
    }

    private void printPickerValues(Device device, SComponent component) throws IOException {
        device.print("<div");
        Utils.optAttribute(device, "class", "hsvcolorpicker pickervaldiv");
        device.print(">");
        device.print("<br />");
        printValueInputField(device, (String)component.getClientProperty("pickerRvalId"), "R", 3);
        printValueInputField(device, (String)component.getClientProperty("pickerHvalId"), "H", 3);
        device.print("<br />");
        printValueInputField(device, (String)component.getClientProperty("pickerGvalId"), "G", 3);
        printValueInputField(device, (String)component.getClientProperty("pickerSvalId"), "S", 3);
        device.print("<br />");
        printValueInputField(device, (String)component.getClientProperty("pickerBvalId"), "B", 3);
        printValueInputField(device, (String)component.getClientProperty("pickerVvalId"), "V", 3);
        device.print("<br />");
        device.print("<br />");
        printValueInputField(device, (String)component.getClientProperty("pickerHexValId"), "#", 6);
        device.print("<br />");
        device.print("</div>");
    }

    private void printPickerSwatch(Device device, SComponent component) throws IOException {
        device.print("<div");
        Utils.optAttribute(device, "id", (String)component.getClientProperty("pickerSwatchId"));
        Utils.optAttribute(device, "class", "hsvcolorpicker pickerSwatch");
        device.print(">&nbsp;</div>");
    }

    private void printPickerDNDHandle(Device device, SComponent component) throws IOException {
        device.print("<h4");
        Utils.optAttribute(device, "id", (String)component.getClientProperty("pickerHandleId"));
        device.print(">&nbsp;</h4>");
    }

    public void writeInternal(Device device, SComponent component) throws IOException {
        // render HTML
        device.print("<div");
        Utils.optAttribute(device, "id", (String)component.getClientProperty("pickerPanelId"));
        Utils.optAttribute(device, "class", "hsvcolorpicker dragPanel");
        device.print(">");
        printPickerDNDHandle(device, component);
        printPicker(device, component);
        printHueSlider(device, component);
        printPickerValues(device, component);
        printPickerSwatch(device, component);
        device.print("</div>");

        // prepare script
        XColorPicker picker = (XColorPicker)component;
        Color c = picker.getSelectedColor();
        SStringBuilder code = new SStringBuilder("function() {");
        code.append("new wingS.HSVColorPicker(")
        .append(component.getClientProperty("DWR_JS_OBJECT")).append(", ")
        .append(picker.getTimeout()).append(", ")
        .append("'").append((String)component.getClientProperty("pickerPanelId")).append("', ")
        .append("'").append((String)component.getClientProperty("pickerHandleId")).append("', ")
        .append("'").append((String)component.getClientProperty("pickerId")).append("', ")
        .append("'").append((String)component.getClientProperty("pickerSwatchId")).append("', ")
        .append("'").append((String)component.getClientProperty("pickerHvalId")).append("', ")
        .append("'").append((String)component.getClientProperty("pickerSvalId")).append("', ")
        .append("'").append((String)component.getClientProperty("pickerVvalId")).append("', ")
        .append("'").append((String)component.getClientProperty("pickerRvalId")).append("', ")
        .append("'").append((String)component.getClientProperty("pickerGvalId")).append("', ")
        .append("'").append((String)component.getClientProperty("pickerBvalId")).append("', ")
        .append("'").append((String)component.getClientProperty("pickerHexValId")).append("', ")
        .append("'").append((String)component.getClientProperty("selectorId")).append("', ")
        .append("'").append((String)component.getClientProperty("hueBackgroundId")).append("', ")
        .append("'").append((String)component.getClientProperty("hueThumbId")).append("', ")
        .append("'").append(c.getRed()).append("', ")
        .append("'").append(c.getGreen()).append("', ")
        .append("'").append(c.getBlue()).append("');")
        .append("}");

        ScriptManager.getInstance().addScriptListener(new OnHeadersLoadedScript(code.toString(), false));
    }

    private void printValueInputField(Device device, String inputId, String prefix, int size) throws IOException {
        device.print(prefix);
        device.print(" <input");
        Utils.optAttribute(device, "class", "hsvcolorpicker");
        Utils.optAttribute(device, "name", inputId);
        Utils.optAttribute(device, "id", inputId);
        device.print(" type='text' value='0' size='"+ size +"' maxlength='"+ size +"' readonly>");
    }

    public void installCG(final SComponent comp) {
        super.installCG(comp);
        comp.addParentFrameListener(this);
    }

    public void parentFrameAdded(SParentFrameEvent e) {
        SessionHeaders.getInstance().registerHeaders(headers);

        SComponent c = e.getComponent();
        String DWR_JS_OBJECT = c.getName() + "_colorpicker";
        c.putClientProperty("DWR_JS_OBJECT", DWR_JS_OBJECT);

        // expose methods to JavaScript by using DWR        
        CallableManager.getInstance().registerCallable(DWR_JS_OBJECT, c, XColorPickerInterface.class);
    }

    public void parentFrameRemoved(SParentFrameEvent e) {
        SessionHeaders.getInstance().deregisterHeaders(headers);

        String DWR_JS_OBJECT = (String)e.getComponent().getClientProperty("DWR_JS_OBJECT");
        CallableManager.getInstance().unregisterCallable(DWR_JS_OBJECT);
    }

}
