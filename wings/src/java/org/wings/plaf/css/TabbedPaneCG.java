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

import net.sf.uadetector.UserAgentFamily;

import org.wings.*;
import org.wings.io.Device;
import org.wings.plaf.css.script.LayoutFillScript;
import org.wings.session.Browser;
import org.wings.session.ScriptManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TabbedPaneCG extends AbstractComponentCG {
    private static final long serialVersionUID = 1L;
    private static final Map<Integer, String> placements = new HashMap<Integer, String>();

    static {
        placements.put(SConstants.TOP, "top");
        placements.put(SConstants.BOTTOM, "bottom");
        placements.put(SConstants.LEFT, "left");
        placements.put(SConstants.RIGHT, "right");
    }

    public void installCG(SComponent component) {
        super.installCG(component);

        final STabbedPane tab = (STabbedPane) component;
        InputMap inputMap = new InputMap();
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.ALT_DOWN_MASK, false), "previous");
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.ALT_DOWN_MASK, false), "next");
        tab.setInputMap(SComponent.WHEN_FOCUSED_OR_ANCESTOR_OF_FOCUSED_COMPONENT, inputMap);


        Action action = new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                if (tab.getSelectedIndex() > 0 && "previous".equals(e.getActionCommand())) {
                    int index = tab.getSelectedIndex() - 1;
                    if (tab.isEnabledAt(index))
                        tab.setSelectedIndex(index);
                } else if (tab.getSelectedIndex() < tab.getTabCount() - 1 && "next".equals(e.getActionCommand())) {
                    int index = tab.getSelectedIndex() + 1;
                    if (tab.isEnabledAt(index))
                        tab.setSelectedIndex(index);
                }
                tab.requestFocus();
            }
        };
        ActionMap actionMap = new ActionMap();
        actionMap.put("previous", action);
        actionMap.put("next", action);
        tab.setActionMap(actionMap);
    }

    public void writeInternal(final Device device, final SComponent component)
            throws java.io.IOException {

        final STabbedPane tabbedPane = (STabbedPane) component;
        if (tabbedPane.getTabCount() > 0) {
            final int placement = tabbedPane.getTabPlacement();

            String tabAreaStyle = component.getStyle(STabbedPane.SELECTOR_TABS);
            String contentAreaStyle = component.getStyle(STabbedPane.SELECTOR_CONTENT);
            StringBuilder tabAreaInline = Utils.inlineStyles(component.getDynamicStyle(STabbedPane.SELECTOR_TABS));
            StringBuilder contentAreaInline = Utils.inlineStyles(component.getDynamicStyle(STabbedPane.SELECTOR_CONTENT));

            SDimension preferredSize = component.getPreferredSize();
            String height = preferredSize != null ? preferredSize.getHeight() : null;
            boolean clientLayout = Utils.isMSIE(component) && height != null && !"auto".equals(height)
                && (placement == SConstants.TOP || placement == SConstants.BOTTOM);

            device.print("<table");

            if (clientLayout) {
                Utils.optAttribute(device, "layoutHeight", height);
                Utils.setPreferredSize(component, preferredSize.getWidth(), null);
            }

            Utils.writeAllAttributes(device, component);
            Utils.writeEvents(device, tabbedPane, null);

            if (clientLayout) {
                Utils.setPreferredSize(component, preferredSize.getWidth(), height);
                ScriptManager.getInstance().addScriptListener(new LayoutFillScript(component.getName()));
            }

            device.print(">");

            if (placement == SConstants.TOP) {
                device.print("<tr><th");
            } else if (placement == SConstants.LEFT) {
                device.print("<tr><th");
            } else if (placement == SConstants.RIGHT) {
                device.print("<tr><td");
                Utils.printTableCellAlignment(device, tabbedPane.getSelectedComponent(), SConstants.LEFT, SConstants.TOP);
            } else if (placement == SConstants.BOTTOM) {
                device.print("<tr yweight=\"100\" oversize=\"2\"");
                device.print("><td");
                Utils.printTableCellAlignment(device, tabbedPane.getSelectedComponent(), SConstants.LEFT, SConstants.TOP);
            }

            if (placement == SConstants.TOP) {
                Utils.optAttribute(device, "class", tabAreaStyle != null ? tabAreaStyle + "_top" : "STabbedPane_top");
                Utils.optAttribute(device, "style", tabAreaInline);
            } else if (placement == SConstants.LEFT) {
                Utils.optAttribute(device, "class", tabAreaStyle != null ? tabAreaStyle + "_left" : "STabbedPane_left");
                Utils.optAttribute(device, "style", tabAreaInline);
            } else {
                Utils.optAttribute(device, "class", contentAreaStyle != null ? contentAreaStyle + "_pane" : "STabbedPane_pane");
                Utils.optAttribute(device, "style", contentAreaInline);
            }
            device.print(">");

            if (placement == SConstants.TOP || placement == SConstants.LEFT) {
                writeTabs(device, tabbedPane);
            } else {
                writeSelectedPaneContent(device, tabbedPane);
            }

            if (placement == SConstants.TOP) {
                device.print("</th></tr>\n<tr yweight=\"100\" oversize=\"2\"><td");
                Utils.printTableCellAlignment(device, tabbedPane.getSelectedComponent(), SConstants.LEFT, SConstants.TOP);
            } else if (placement == SConstants.LEFT) {
                device.print("</th><td");
                Utils.printTableCellAlignment(device, tabbedPane.getSelectedComponent(), SConstants.LEFT, SConstants.TOP);
            } else if (placement == SConstants.RIGHT) {
                device.print("</td><th");
            } else if (placement == SConstants.BOTTOM) {
                device.print("</td></tr>\n<tr><th");
            }

            if (placement == SConstants.RIGHT) {
                Utils.optAttribute(device, "class", tabAreaStyle != null ? tabAreaStyle + "_right" : "STabbedPane_right");
                Utils.optAttribute(device, "style", tabAreaInline);
            } else if (placement == SConstants.BOTTOM) {
                Utils.optAttribute(device, "class", tabAreaStyle != null ? tabAreaStyle + "_bottom" : "STabbedPane_bottom");
                Utils.optAttribute(device, "style", tabAreaInline);
            } else {
                Utils.optAttribute(device, "class", tabAreaStyle != null ? tabAreaStyle + "_pane" : "STabbedPane_pane");
                Utils.optAttribute(device, "style", contentAreaInline);
            }
            device.print(">");

            if (placement == SConstants.TOP || placement == SConstants.LEFT) {
                writeSelectedPaneContent(device, tabbedPane);
                device.print("</td></tr></table>");
            } else {
                writeTabs(device, tabbedPane);
                device.print("</th></tr></table>");
            }
        } else {
            Utils.printDebug(device, "<!-- tabbed pane has no tabs -->");
        }
    }

    /**
     * Renders the currently selected pane of the tabbed Pane.
     */
    protected void writeSelectedPaneContent(Device device, STabbedPane tabbedPane) throws IOException {
        SComponent selected = tabbedPane.getSelectedComponent();
        if (selected != null) {
            selected.write(device);
        }
    }

    protected void writeTabs(Device device, STabbedPane tabbedPane) throws IOException {
        final Browser browser = tabbedPane.getSession().getUserAgent();
        // substitute whitespaces for konqueror and ie5.0x
        final boolean nbspWorkaround = browser.getBrowserType().equals(UserAgentFamily.KONQUEROR);

        String selectedTabStyle = tabbedPane.getStyle(STabbedPane.SELECTOR_SELECTED_TAB);
        String unselectedTabStyle = tabbedPane.getStyle(STabbedPane.SELECTOR_UNSELECTED_TAB);
        String disabledTabStyle = tabbedPane.getStyle(STabbedPane.SELECTOR_DISABLED_TAB);
        StringBuilder selectedTabInline = Utils.inlineStyles(tabbedPane.getDynamicStyle(STabbedPane.SELECTOR_SELECTED_TAB));
        StringBuilder unselectedTabInline = Utils.inlineStyles(tabbedPane.getDynamicStyle(STabbedPane.SELECTOR_UNSELECTED_TAB));
        StringBuilder disabledTabInline = Utils.inlineStyles(tabbedPane.getDynamicStyle(STabbedPane.SELECTOR_DISABLED_TAB));

        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            final SIcon icon = tabbedPane.getIconAt(i);
            final String tooltip = tabbedPane.getToolTipText();
            final String title = nbspWorkaround ? Utils.nonBreakingSpaces(tabbedPane.getTitleAt(i)) : tabbedPane.getTitleAt(i);
            final boolean enabledTab = tabbedPane.isEnabledAt(i);
            final String eventValue = String.valueOf(i);

            /*
             * needed here so that the tabs can be wrapped. else they are in
             * one long line. noticed in firefox and konqueror.
             */
            Utils.printNewline(device, tabbedPane);

            Utils.printButtonStart(device, tabbedPane, eventValue, enabledTab, tabbedPane.getShowAsFormComponent());

            Utils.optAttribute(device, "tab", i);

            if (tooltip != null) {
                Utils.optAttribute(device, "title", tooltip);
            }

            if (i == tabbedPane.getSelectedIndex() && tabbedPane.isFocusOwner()) {
                Utils.optAttribute(device, "foc", tabbedPane.getName());
            }

            // TODO: selector styles
            final StringBuilder cssClassName = new StringBuilder("STabbedPane_Tab_");
            cssClassName.append(placements.get(tabbedPane.getTabPlacement()));
            if (i == tabbedPane.getSelectedIndex()) {
                cssClassName.append(" STabbedPane_Tab_selected");
            } else if (!enabledTab) {
                cssClassName.append(" STabbedPane_Tab_disabled");
            } else {
                cssClassName.append(" STabbedPane_Tab_unselected");
            }
            if (i == tabbedPane.getSelectedIndex()) {
                Utils.optAttribute(device, "style", selectedTabInline);
            } else if (!enabledTab) {
                Utils.optAttribute(device, "style", disabledTabInline);
            } else {
                Utils.optAttribute(device, "style", unselectedTabInline);
            }
            Utils.optAttribute(device, "class", cssClassName);

            device.print(">");

            if (icon != null && tabbedPane.getTabPlacement() != SConstants.RIGHT) {
                device.print("<img");
                Utils.optAttribute(device, "src", icon.getURL());
                Utils.optAttribute(device, "width", icon.getIconWidth());
                Utils.optAttribute(device, "height", icon.getIconHeight());
                Utils.attribute(device, "alt", icon.getIconTitle());
                device.print("/>");
            }

            if (title != null) {
                device.print("&nbsp;");
                Utils.write(device, title);
                device.print("&nbsp;");
            }

            if (icon != null && tabbedPane.getTabPlacement() == SConstants.RIGHT) {
                device.print("<img");
                Utils.optAttribute(device, "src", icon.getURL());
                Utils.optAttribute(device, "width", icon.getIconWidth());
                Utils.optAttribute(device, "height", icon.getIconHeight());
                Utils.attribute(device, "alt", icon.getIconTitle());
                device.print("/>");
            }

            Utils.printButtonEnd(device, enabledTab);
        }
    }
}
