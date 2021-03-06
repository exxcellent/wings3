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
import org.wings.io.Device;
import org.wings.io.StringBuilderDevice;
import org.wings.plaf.CGManager;
import org.wings.plaf.Update;
import org.wings.plaf.css.script.OnPageRenderedScript;
import org.wings.script.*;

import java.io.IOException;

public final class ComboBoxCG extends AbstractComponentCG implements org.wings.plaf.ComboBoxCG {

    private static final long serialVersionUID = 1L;

    public void installCG(final SComponent comp) {
        super.installCG(comp);
        final SComboBox component = (SComboBox) comp;
        final CGManager manager = component.getSession().getCGManager();
        Object value;
        value = manager.getObject("SComboBox.renderer", SDefaultListCellRenderer.class);
        if (value != null) {
            component.setRenderer((SDefaultListCellRenderer) value);
        }
    }

    protected void writeFormComboBox(Device device, SComboBox component) throws IOException {
    	Object clientProperty = component.getClientProperty("onChangeSubmitListener");
        if (clientProperty == null) {
        	String event = JavaScriptEvent.ON_CHANGE;
        	String code = "wingS.request.sendEvent(event, true, " + !component.isReloadForced() + ");";
            JavaScriptListener javaScriptListener = new JavaScriptListener(event, code);
            component.addScriptListener(javaScriptListener);
            component.putClientProperty("onChangeSubmitListener", javaScriptListener);
        }

        device.print("<span><select size=\"1\" wrapping=\"1\"");

        Utils.writeAllAttributes(device, component);

        Utils.optAttribute(device, "name", Utils.event(component));
        Utils.optAttribute(device, "tabindex", component.getFocusTraversalIndex());
        Utils.writeEvents(device, component, null);
        if (!component.isEnabled())
            device.print(" disabled=\"true\"");
        if (component.isFocusOwner())
            Utils.optAttribute(device, "foc", component.getName());

        device.print(">");

        final javax.swing.ComboBoxModel model = component.getModel();
        final int size = model.getSize();
        final int selected = component.getSelectedIndex();

        final SListCellRenderer renderer = component.getRenderer();
        final org.wings.io.StringBuilderDevice stringBuilderDevice = new StringBuilderDevice(512);
        for (int i = 0; i < size; i++) {
            SComponent cellRenderer = null;
            if (renderer != null) {
                cellRenderer = renderer.getListCellRendererComponent(component, model.getElementAt(i), false, i);
            } else {
                device.print("<!--renderer==null-->");
            }


            Utils.printNewline(device, component);
            device.print("<option");
            Utils.optAttribute(device, "value", component.getSelectionParameter(i));
            if (selected == i) {
                device.print(" selected=\"selected\"");
            }

            if (cellRenderer != null) {
                Utils.optAttribute(device, "title", cellRenderer.getToolTipText());
                StringBuilder buffer = Utils.generateCSSComponentInlineStyle(cellRenderer);
                Utils.optAttribute(device, "style", buffer.toString());
            }

            device.print(">"); //option

            if (cellRenderer != null) {
                // Hack: remove all tags, because in form selections, looks ugly.
                stringBuilderDevice.reset();
                cellRenderer.write(stringBuilderDevice);
                final int printedChars = Utils.writeWithoutHTML(device, stringBuilderDevice.toString());
                
                if (printedChars == 0) {
                    // If the option is empty ("") Firefox
                    // renders somehow smaller comboboxes!
                    device.print("&nbsp;");
                }
            } else {
                device.print("<!--cellrenderer==null, use toString-->");
                device.print(model.getElementAt(i).toString());
            }

            device.print("</option>");
        }

        Utils.printNewline(device, component);
        device.print("</select>");
        // util method

        device.print("<input type=\"hidden\"");
        Utils.optAttribute(device, "name", Utils.event(component));
        Utils.optAttribute(device, "value", -1);
        device.print("/></span>");
        if (selected == -1) {
            component.getSession().getScriptManager().addScriptListener(
                new OnPageRenderedScript("document.getElementById(\"" + component.getName() + "\").selectedIndex = -1;"));
        }
    }

    public void writeInternal(final Device device, final SComponent _c) throws IOException {
        final SComboBox comboBox = (SComboBox) _c;
        // TODO: implement anchor combobox
        //if (comboBox.getShowAsFormComponent()) {
        writeFormComboBox(device, comboBox);
        //} else {
        //    writeAnchorComboBox(device, comboBox);
        // }
    }

    public Update getSelectionUpdate(SComboBox comboBox, int selectedIndex) {
        return new SelectionUpdate(comboBox, selectedIndex);
    }

    protected class SelectionUpdate extends AbstractUpdate {

        private Integer selectedIndex;

        public SelectionUpdate(SComponent component, int selectedIndex) {
            super(component);
            this.selectedIndex = new Integer(selectedIndex);
        }

        public Handler getHandler() {
            UpdateHandler handler = new UpdateHandler("selectionComboBox");
            handler.addParameter(component.getName());
            handler.addParameter(selectedIndex);
            return handler;
        }

    }
}
