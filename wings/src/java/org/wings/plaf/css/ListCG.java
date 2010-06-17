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

import java.awt.Color;
import org.wings.SCellRendererPane;
import org.wings.SComponent;
import org.wings.SDefaultListCellRenderer;
import org.wings.SList;
import org.wings.SListCellRenderer;
import org.wings.io.Device;
import org.wings.io.StringBuilderDevice;
import org.wings.plaf.CGManager;
import org.wings.plaf.Update;
import org.wings.script.JavaScriptEvent;
import org.wings.script.JavaScriptListener;

import java.awt.Rectangle;
import java.io.IOException;
import java.util.List;

public final class ListCG extends AbstractComponentCG<SList> implements org.wings.plaf.ListCG {

    private static final long serialVersionUID = 1L;

    public void installCG(final SList list) {
        super.installCG(list);
        final CGManager manager = list.getSession().getCGManager();
        Object value;
        value = manager.getObject("SList.cellRenderer", SDefaultListCellRenderer.class);
        if (value != null) {
            list.setCellRenderer((SDefaultListCellRenderer) value);
        }
    }

    protected void writeFormList(final Device device, final SList list) throws IOException {
        Object clientProperty = list.getClientProperty("onChangeSubmitListener");
        // If the application developer attached any ListSelectionListeners to this
        // SList, the surrounding form gets submitted as soon as the state / the
        // selection of this SList changed.
        if (list.getListSelectionListeners().length > 0) {
            if (clientProperty == null) {
                String event = JavaScriptEvent.ON_CHANGE;
            	String code = "wingS.request.sendEvent(event, true, " + !list.isReloadForced() + ");";
                JavaScriptListener javaScriptListener = new JavaScriptListener(event, code);
                list.addScriptListener(javaScriptListener);
                list.putClientProperty("onChangeSubmitListener", javaScriptListener);
            }
        } else if (clientProperty != null && clientProperty instanceof JavaScriptListener) {
            list.removeScriptListener((JavaScriptListener) clientProperty);
            list.putClientProperty("onChangeSubmitListener", null);
        }

        device.print("<span");
        StringBuilder builder = new StringBuilder();
        Utils.appendCSSInlineSize(builder, list);
        if (builder.length() != 0) {
            device.print(" style=\"");
            device.print(builder.toString());
            device.print("\"");
        }
        device.print("><select wrapping=\"1\"");
        Utils.writeAllAttributes(device, list);

        Utils.optAttribute(device, "name", Utils.event(list));
        Utils.optAttribute(device, "tabindex", list.getFocusTraversalIndex());
        Utils.optAttribute(device, "size", list.getVisibleRowCount());
        Utils.optAttribute(device, "multiple", (list.getSelectionMode() == SList.MULTIPLE_SELECTION) ? "multiple" : null);
        Utils.writeEvents(device, list, null);

        if (!list.isEnabled())
            device.print(" disabled=\"true\"");
        if (list.isFocusOwner())
            Utils.optAttribute(device, "foc", list.getName());

        device.print(">");

        final javax.swing.ListModel model = list.getModel();
        final int size = model.getSize();
        final SListCellRenderer cellRenderer = list.getCellRenderer();
        final StringBuilderDevice stringBuilderDevice = new StringBuilderDevice(512);

        for (int i = 0; i < size; i++) {
            SComponent renderer = null;
            if (cellRenderer != null) {
                renderer = cellRenderer.getListCellRendererComponent(list, model.getElementAt(i), false, i);
            }

            Utils.printNewline(device, list, 1);
            device.print("<option");
            Utils.optAttribute(device, "value", list.getSelectionParameter(i));
            if (renderer != null) {
                Utils.optAttribute(device, "title", renderer.getToolTipText());
            }
            if (list.isSelectedIndex(i)) {
                device.print(" selected");
                //Utils.optAttribute(device, "class", "selected");
            }

            if (renderer != null) {
                Utils.optAttribute(device, "style", Utils.generateCSSComponentInlineStyle(renderer));
            }
            device.print(">");

            if (renderer != null) {
                String tooltipText = renderer.getToolTipText();
                renderer.setToolTipText(null);

                // Hack: remove all tags, because in form selections, looks ugly.
                stringBuilderDevice.reset();
                renderer.write(stringBuilderDevice);

                final int charsWritten = Utils.writeWithoutHTML(device, stringBuilderDevice.toString());
                if (charsWritten == 0) {
                    // If the option is empty ("") Firefox
                    // renders somehow smaller comboboxes!
                    device.print("&nbsp;");
                }

                renderer.setToolTipText( tooltipText );
            } else {
                device.print(model.getElementAt(i).toString());
            }

            device.print("</option>");
        }

        Utils.printNewline(device, list);
        device.print("</select>");
        Utils.printNewline(device, list);
        device.print("<input type=\"hidden\"");
        Utils.optAttribute(device, "name", Utils.event(list));
        Utils.optAttribute(device, "value", -1);
        device.print("/></span>");
    }

    public void writeAnchorList(Device device, SList list) throws IOException {
        // Remove superfluous 'onChangeSubmitListener' (in case there is any).
        // This is because we don't want to render 'onclick' AND 'onchange'.
        Object clientProperty = list.getClientProperty("onChangeSubmitListener");
        if (clientProperty != null && clientProperty instanceof JavaScriptListener) {
            list.removeScriptListener((JavaScriptListener) clientProperty);
            list.putClientProperty("onChangeSubmitListener", null);
        }

        boolean renderSelection = list.getSelectionMode() != SList.NO_SELECTION;

        device.print("<");
        device.print(list.getType());
        Utils.optAttribute(device, "type", list.getOrderType());
        Utils.optAttribute(device, "start", list.getStart());
        Utils.writeAllAttributes(device, list);
        device.print(">");

        javax.swing.ListModel model = list.getModel();
        SListCellRenderer cellRenderer = list.getCellRenderer();
        SCellRendererPane rendererPane = list.getCellRendererPane();

        Rectangle currentViewport = list.getViewportSize();
        Rectangle maximalViewport = list.getScrollableViewportSize();
        int start = 0;
        int end = model.getSize();
        int empty = maximalViewport != null ? maximalViewport.height : end;

        if (currentViewport != null) {
            start = currentViewport.y;
            end = start + currentViewport.height;
        }

        for (int i = start; i < end; ++i) {
            if (i >= empty) {
                device.print("<li class=\"empty\">&nbsp;</li>");
                continue;
            }

            boolean selected = list.isSelectedIndex(i);

            if (renderSelection && selected)
                device.print("<li class=\"selected clickable\"");
            else
                device.print("<li class=\"clickable\"");

            SComponent renderer = cellRenderer.getListCellRendererComponent(list, model.getElementAt(i), selected, i);

            if (renderSelection) {
                Utils.printClickability(device, list, list.getToggleSelectionParameter(i) + ";shiftKey='+event.shiftKey+';ctrlKey='+event.ctrlKey+'", true, list.getShowAsFormComponent());
                Utils.optAttribute(device, "foc", renderer.getName());
            }
            device.print(">");
            rendererPane.writeComponent(device, renderer, list);
            device.print("</li>\n");
        }

        device.print("</");
        Utils.write(device, list.getType());
        device.print(">");
    }

    @Override
    public void writeInternal(final Device device, final SList list) throws IOException {
        if (list.getShowAsFormComponent()) {
            writeFormList(device, list);
        } else {
            writeAnchorList(device, list);
        }
    }

    public Update getSelectionUpdate(SList list, List deselectedIndices, List selectedIndices) {
        return new SelectionUpdate(list, deselectedIndices, selectedIndices);
    }

    protected static class SelectionUpdate extends AbstractUpdate<SList> {
        private List deselectedIndices;
        private List selectedIndices;

        public SelectionUpdate(SList component, List deselectedIndices, List selectedIndices) {
            super(component);
            this.deselectedIndices = deselectedIndices;
            this.selectedIndices = selectedIndices;
        }

        public Handler getHandler() {
            UpdateHandler handler = new UpdateHandler("selectionList");
            handler.addParameter(component.getName());
            handler.addParameter(Utils.listToJsArray(deselectedIndices));
            handler.addParameter(Utils.listToJsArray(selectedIndices));
            return handler;
        }

    }
}
