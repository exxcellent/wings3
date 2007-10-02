/*
 * Copyright (c) 2005 Your Corporation. All Rights Reserved.
 */
package org.wings.plaf.css;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.wings.*;
import org.wings.io.Device;

import java.io.IOException;

/**
 * CG for a pagescroller.
 *
 * @author holger
 */
public final class PageScrollerCG extends AbstractComponentCG implements org.wings.plaf.PageScrollerCG {
    private static final long serialVersionUID = 1L;

    private final static Log log = LogFactory.getLog(PageScrollerCG.class);

    public static final int FORWARD = 0;
    public static final int BACKWARD = 1;
    public static final int FORWARD_BLOCK = 2;
    public static final int BACKWARD_BLOCK = 3;
    public static final int FIRST = 4;
    public static final int LAST = 5;
    private final static SIcon[][][] DEFAULT_ICONS = new SIcon[2][6][2];

    static {
        String[] postfixes = new String[6];
        String[] prefixes = new String[6];
        for (int orientation = 0; orientation < 2; orientation++) {
            prefixes[BACKWARD] = "";
            prefixes[FORWARD] = "";
            prefixes[FIRST] = "Margin";
            prefixes[LAST] = "Margin";
            prefixes[FORWARD_BLOCK] = "Block";
            prefixes[BACKWARD_BLOCK] = "Block";
            if (orientation == SConstants.VERTICAL) {
                postfixes[BACKWARD] = "Up";
                postfixes[FORWARD] = "Down";
                postfixes[FIRST] = "Up";
                postfixes[LAST] = "Down";
                postfixes[BACKWARD_BLOCK] = "Up";
                postfixes[FORWARD_BLOCK] = "Down";
            } else {
                postfixes[BACKWARD] = "Left";
                postfixes[FORWARD] = "Right";
                postfixes[FIRST] = "Left";
                postfixes[LAST] = "Right";
                postfixes[BACKWARD_BLOCK] = "Left";
                postfixes[FORWARD_BLOCK] = "Right";
            }

            for (int direction = 0; direction < postfixes.length; direction++) {
                DEFAULT_ICONS[orientation][direction][0] =
                        new SResourceIcon("org/wings/icons/"
                        + prefixes[direction]
                        + "Scroll"
                        + postfixes[direction] + ".gif");
                DEFAULT_ICONS[orientation][direction][1] =
                        new SResourceIcon("org/wings/icons/Disabled"
                        + prefixes[direction]
                        + "Scroll"
                        + postfixes[direction] + ".gif");
            }
        }
    }

    public void writeInternal(Device d, SComponent c)
            throws IOException {
        log.debug("write = " + c);
        SPageScroller sb = (SPageScroller) c;

        String style = sb.getStyle();
        if (sb.getLayoutMode() == SConstants.VERTICAL) {
            sb.setStyle(style + " SPageScroller_vertical");
            writeVerticalPageScroller(d, sb);
        }
        else {
            sb.setStyle(style + " SPageScroller_horizontal");
            writeHorizontalPageScroller(d, sb);
        }
        sb.setStyle(style);
    }

    private void writeVerticalPageScroller(Device d, SPageScroller sb) throws IOException {
        int value = sb.getValue();
        int extent = sb.getExtent();
        int minimum = sb.getMinimum();
        int maximum = sb.getMaximum();
        boolean backEnabled = value > minimum;
        boolean forwardEnabled = value < maximum - extent;
        boolean firstPage = (value == minimum);
        boolean lastPage = (value == (maximum - extent));

        d.print("<table");
        writeAllAttributes(d, sb);
        Utils.writeEvents(d, sb, null);
        d.print("><tbody><tr height=\"1%\">")
            .print("<td height=\"1%\"><table class=\"buttons\"><tbody>");

        d.print("<tr><td");
        boolean enabled2 = !firstPage;
        Utils.printClickability(d, sb, "" + minimum, enabled2, sb.getShowAsFormComponent());
        d.print(">");
        writeIcon(d, DEFAULT_ICONS[SConstants.VERTICAL][FIRST][0]);
        d.print("</td></tr>");

        d.print("<tr><td");
        Utils.printClickability(d, sb, "" + (value - extent), backEnabled, sb.getShowAsFormComponent());
        d.print(">");
        writeIcon(d, DEFAULT_ICONS[SConstants.VERTICAL][BACKWARD][0]);
        d.print("</td></tr>");

        d.print("</tbody></table></td>")
                .print("</tr>")
                .print("<tr>")
                .print("<td><table class=\"pages\"><tbody>");

        int firstDirectPage = sb.getCurrentPage() - (sb.getDirectPages() - 1) / 2;
        firstDirectPage = Math.min(firstDirectPage, sb.getPageCount() - sb.getDirectPages());
        firstDirectPage = Math.max(firstDirectPage, 0);

        for (int i = 0; i < Math.min(sb.getDirectPages(), sb.getPageCount() - firstDirectPage); i++) {
            int page = firstDirectPage + i;
            d.print("<tr><td");
            boolean isCurrentPage = (sb.getCurrentPage() == page);
            Utils.optAttribute(d, "class", isCurrentPage ? "page_selected" : null);
            boolean enabled = !isCurrentPage;
            Utils.printClickability(d, sb, String.valueOf(page * sb.getExtent()), enabled, sb.getShowAsFormComponent());
            d.print(">");
            d.print(Integer.toString(page + 1));
            d.print("</td></tr>");
        }

        d.print("</tbody></table></td>")
                .print("</tr>")
                .print("<tr height=\"1%\">")
                .print("<td height=\"1%\"><table class=\"buttons\"><tbody>");

        d.print("<tr><td");
        Utils.printClickability(d, sb, "" + (value + extent), forwardEnabled, sb.getShowAsFormComponent());
        d.print(">");
        writeIcon(d, DEFAULT_ICONS[SConstants.VERTICAL][FORWARD][0]);
        d.print("</td></tr>");

        d.print("<tr><td");
        boolean enabled1 = !lastPage;
        Utils.printClickability(d, sb, "" + (maximum + 1 - extent), enabled1, sb.getShowAsFormComponent());
        d.print(">");
        writeIcon(d, DEFAULT_ICONS[SConstants.VERTICAL][LAST][0]);
        d.print("</td></tr>");

        d.print("</tbody></table></td>")
                .print("</tr>")
                .print("</tbody></table>");
    }

    private void verticalArea(Device d, String s, int v) throws IOException {
        d.print("<tr><td style=\"background-color: ");
        d.print(s);
        d.print("\" height=\"");
        d.print(v + "%");
        d.print("\"></td></tr>");
    }

    private void writeHorizontalPageScroller(Device d, SPageScroller sb) throws IOException {
        int value = sb.getValue();
        int extent = sb.getExtent();
        int minimum = sb.getMinimum();
        int maximum = sb.getMaximum();
        boolean backEnabled = value > minimum;
        boolean forwardEnabled = value < maximum - extent;
        boolean firstPage = (value == minimum);
        boolean lastPage = (value >= (maximum - extent));

        d.print("<table");
        writeAllAttributes(d, sb);
        Utils.writeEvents(d, sb, null);
        d.print("><tbody><tr>")
            .print("<td><table class=\"buttons\"><tbody><tr>");

        d.print("<td");
        boolean enabled2 = !firstPage;
        Utils.printClickability(d, sb, "" + minimum, enabled2, sb.getShowAsFormComponent());
        d.print(">");
        writeIcon(d, DEFAULT_ICONS[SConstants.HORIZONTAL][FIRST][0]);
        d.print("</td>");

        d.print("<td");
        Utils.printClickability(d, sb, "" + (value - extent), backEnabled, sb.getShowAsFormComponent());
        d.print(">");
        writeIcon(d, DEFAULT_ICONS[SConstants.HORIZONTAL][BACKWARD][0]);
        d.print("</td>");

        d.print("</tr></tbody></table></td>")
                .print("<td><table class=\"pages\"><tbody><tr>");

        int firstDirectPage = sb.getCurrentPage() - (sb.getDirectPages() - 1) / 2;
        firstDirectPage = Math.min(firstDirectPage, sb.getPageCount() - sb.getDirectPages());
        firstDirectPage = Math.max(firstDirectPage, 0);
        for (int i = 0; i < Math.min(sb.getDirectPages(), sb.getPageCount() - firstDirectPage); i++) {
            int page = firstDirectPage + i;
            d.print("<td");
            boolean isCurrentPage = (sb.getCurrentPage() == page);
            Utils.optAttribute(d, "class", isCurrentPage ? "page_selected": null);
            boolean enabled = !isCurrentPage;
            Utils.printClickability(d, sb, String.valueOf(page * sb.getExtent()), enabled, sb.getShowAsFormComponent());
            d.print(">");
            d.print(Integer.toString(page + 1));
            d.print("</td>");
        }

        d.print("</tr></tbody></table></td>")
                .print("<td><table class=\"buttons\"><tbody><tr>");

        d.print("<td");
        Utils.printClickability(d, sb, "" + (value + extent), forwardEnabled, sb.getShowAsFormComponent());
        d.print(">");
        writeIcon(d, DEFAULT_ICONS[SConstants.HORIZONTAL][FORWARD][0]);
        d.print("</td>");

        d.print("<td");
        boolean enabled1 = !lastPage;
        Utils.printClickability(d, sb, "" + (sb.getPageCount() - 1) * extent, enabled1, sb.getShowAsFormComponent());
        d.print(">");

        writeIcon(d, DEFAULT_ICONS[SConstants.HORIZONTAL][LAST][0]);
        d.print("</td>");

        d.print("</tr></tbody></table></td>")
                .print("</tr></tbody></table>");
    }

    private void writeIcon(Device device, SIcon icon) throws IOException {
        device.print("<img class=\"nopad\"");
        Utils.optAttribute(device, "src", icon.getURL());
        Utils.optAttribute(device, "width", icon.getIconWidth());
        Utils.optAttribute(device, "height", icon.getIconHeight());
        Utils.attribute(device, "alt", icon.getIconTitle());
        device.print("/>");
    }
}
