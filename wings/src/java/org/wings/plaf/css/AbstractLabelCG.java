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
import org.wings.SComponent;
import org.wings.SIcon;
import org.wings.SLabel;
import org.wings.SResourceIcon;
import org.wings.io.Device;
import org.wings.io.StringBuilderDevice;

import java.io.IOException;

public abstract class AbstractLabelCG extends AbstractComponentCG {

    private static final Log log = LogFactory.getLog(AbstractLabelCG.class);

    public static SIcon TRANSPARENT_ICON = new SResourceIcon("org/wings/icons/transdot.gif");

    public void writeText(Device device, String text, boolean wordWrap) throws IOException {
        // white-space:nowrap seems to work in all major browser.
        // Except leading and trailing spaces!
        device.print("<span").print(wordWrap ? ">" : " style=\"white-space:nowrap\">");
        if ((text.length() > 5) && (text.startsWith("<html>"))) {
            Utils.writeRaw(device, text.substring(6));
        } else {
            // Only quote leading/trailing whitespace
            int len = text.length();
            int off = 0;
            
            while ( off < len && text.charAt(off) <= ' ' ) {
                off++;
                device.print("&nbsp;");
            }
            while ( off < len && text.charAt(len - 1) <= ' ' ) {
                len--;
            }
            
            Utils.quote(device, text.substring(off,len), true, false, false);
            
            for ( int x = len; x < text.length() ; x++ ) {
                device.print("&nbsp;");
            }
            
        }

        device.print("</span>");
    }

    public void writeIcon(Device device, SIcon icon, boolean isMSIE) throws IOException {
        device.print("<img class=\"nopad\"");
        if (isMSIE && icon.getURL().toString().endsWith(".png") && icon.getIconWidth() > 0 && icon.getIconHeight() > 0) {
            Utils.optAttribute(device, "style", "filter:progid:DXImageTransform.Microsoft.AlphaImageLoader(src='" + icon.getURL() + "', sizingMethod='scale')");
            Utils.optAttribute(device, "src", TRANSPARENT_ICON.getURL());
        }
        else
            Utils.optAttribute(device, "src", icon.getURL());

        Utils.optAttribute(device, "width", icon.getIconWidth());
        Utils.optAttribute(device, "height", icon.getIconHeight());
        Utils.attribute(device, "alt", icon.getIconTitle());
        device.print("/>");
    }

    protected class TextUpdate extends AbstractUpdate {

        private String text;

        public TextUpdate(SComponent component, String text) {
            super(component);
            this.text = text;
        }

        public Handler getHandler() {
            String textCode = "";
            String exception = null;

            try {
                StringBuilderDevice textDevice = new StringBuilderDevice();
                boolean wordWrap = false;
                if (component instanceof SLabel)
                    wordWrap = ((SLabel) component).isWordWrap();
                writeText(textDevice, text, wordWrap);
                textCode = textDevice.toString();
            } catch (Throwable t) {
                log.fatal("An error occured during rendering", t);
                exception = t.getClass().getName();
            }

            UpdateHandler handler = new UpdateHandler("text");
            handler.addParameter(component.getName());
            handler.addParameter(textCode);
            if (exception != null) {
                handler.addParameter(exception);
            }
            return handler;
        }

    }

    protected class IconUpdate extends AbstractUpdate {

        private SIcon icon;

        public IconUpdate(SComponent component, SIcon icon) {
            super(component);
            this.icon = icon;
        }

        public Handler getHandler() {
            String iconCode = "";
            String exception = null;

            try {
                StringBuilderDevice iconDevice = new StringBuilderDevice();
                writeIcon(iconDevice, icon, Utils.isMSIE(component));
                iconCode = iconDevice.toString();
            } catch (Throwable t) {
                log.fatal("An error occured during rendering", t);
                exception = t.getClass().getName();
            }

            UpdateHandler handler = new UpdateHandler("icon");
            handler.addParameter(component.getName());
            handler.addParameter(iconCode);
            if (exception != null) {
                handler.addParameter(exception);
            }
            return handler;
        }

    }

}
