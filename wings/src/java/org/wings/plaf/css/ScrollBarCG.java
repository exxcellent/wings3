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

import org.wings.*;
import org.wings.style.CSSProperty;
import org.wings.io.Device;
import org.wings.plaf.css.script.LayoutFillScript;
import org.wings.plaf.css.script.OnPageRenderedScript;
import org.wings.plaf.Update;

import java.io.IOException;

/**
 * CG for a scrollbar.
 *
 * @author holger
 */
public final class ScrollBarCG extends org.wings.plaf.css.AbstractComponentCG<SScrollBar>
        implements org.wings.plaf.ScrollBarCG {
    private static final long serialVersionUID = 1L;

    public void writeInternal(final Device d, final SScrollBar scrollBar)
            throws IOException {
        final String style = scrollBar.getStyle();
        if (scrollBar.getOrientation() == SConstants.VERTICAL) {
            scrollBar.setStyle(style + " SScrollBar_vertical");
            writeVerticalScrollbar(d, scrollBar);
        } else {
            scrollBar.setStyle(style + " SScrollBar_horizontal");
            writeHorizontalScrollbar(d, scrollBar);
        }
        scrollBar.setStyle(style);
    }

    private void writeVerticalScrollbar(Device device, SScrollBar sb) throws IOException {
        SDimension preferredSize = sb.getPreferredSize();
        String height = preferredSize != null ? preferredSize.getHeight() : null;
        boolean clientLayout = isMSIE(sb) && height != null && !"auto".equals(height);

        Utils.printNewline(device, sb);
        device.print("<table");

        if (clientLayout) {
            Utils.optAttribute(device, "layoutHeight", height);
            Utils.setPreferredSize(sb, preferredSize.getWidth(), null);
        }

        sb.setAttribute(CSSProperty.TABLE_LAYOUT, "fixed");
        Utils.writeAllAttributes(device, sb);

        if (clientLayout) {
            Utils.setPreferredSize(sb, preferredSize.getWidth(), height);
            sb.getSession().getScriptManager().addScriptListener(new LayoutFillScript(sb.getName()));
        }

        device.print("><tbody><tr><td>\n");
        device.print("<div class=\"outer\"><div class=\"inner\"/></div>\n");
        device.print("</td></tr></tbody></table>");

        sb.getSession().getScriptManager().addScriptListener(new VerticalScrollBarLayoutScript(sb));
        sb.getSession().getScriptManager().addScriptListener(new VerticalScrollBarSetScript(sb));
    }

    private void writeHorizontalScrollbar(Device device, SScrollBar sb) throws IOException {
        device.print("<table");
        Utils.writeAllAttributes(device, sb);
        device.print("><tbody><tr><td>\n");
        device.print("<div class=\"outer\"><div class=\"inner\"/></div>\n");
        device.print("</td></tr></tbody></table>");

        sb.getSession().getScriptManager().addScriptListener(new HorizontalScrollBarLayoutScript(sb));
        sb.getSession().getScriptManager().addScriptListener(new HorizontalScrollBarSetScript(sb));
    }

    public Update getAdjustmentUpdate(SScrollBar scrollBar, int value, int extent, int size) {
        if (scrollBar.isChangeFromEvent())
            return null;
        return new ComponentUpdate<SScrollBar>(this, scrollBar);
    }

    private static class VerticalScrollBarLayoutScript
        extends OnPageRenderedScript
    {
        public VerticalScrollBarLayoutScript(SScrollBar sb) {
            super("wingS.scrollbar.layout_vertical(\"" + sb.getName() + "\");");
        }
    }
    private static class HorizontalScrollBarLayoutScript
        extends OnPageRenderedScript
    {
        public HorizontalScrollBarLayoutScript(SScrollBar sb) {
            super("wingS.scrollbar.layout_horizontal(\"" + sb.getName() + "\");");
        }
    }
    private static class VerticalScrollBarSetScript
        extends OnPageRenderedScript
    {
        public VerticalScrollBarSetScript(SScrollBar sb) {
            super("wingS.scrollbar.set_vertical(\"" + sb.getName() + "\"" + params(sb) + ");");
        }
    }
    private static class HorizontalScrollBarSetScript
        extends OnPageRenderedScript
    {
        public HorizontalScrollBarSetScript(SScrollBar sb) {
            super("wingS.scrollbar.set_horizontal(\"" + sb.getName() + "\"" + params(sb) + ");");
        }
    }

    static String params(SScrollBar sb) {
        final int value = sb.getValue();
        final int extent = sb.getExtent();
        final int minimum = sb.getMinimum();
        final int maximum = sb.getMaximum();
        final int size = maximum - minimum;
        return ", " + value + ", " + extent + ", " + size;
    }
}
