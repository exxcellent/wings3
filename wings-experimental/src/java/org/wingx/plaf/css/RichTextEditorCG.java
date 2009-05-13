package org.wingx.plaf.css;

import org.wingx.XRichTextEditor;
import org.wings.io.Device;
import org.wings.plaf.Update;
import org.wings.plaf.css.Utils;
import org.wings.plaf.css.AbstractUpdate;
import org.wings.plaf.css.UpdateHandler;
import org.wings.plaf.css.AbstractComponentCG;
import org.wings.SDimension;

import java.io.IOException;

public class RichTextEditorCG extends AbstractComponentCG<XRichTextEditor> implements org.wingx.plaf.RichTextEditorCG<XRichTextEditor> {
    int horizontalOversize = 2;

    public int getHorizontalOversize() {
        return horizontalOversize;
    }

    public void setHorizontalOversize(int horizontalOversize) {
        this.horizontalOversize = horizontalOversize;
    }

    @Override
    public void installCG(XRichTextEditor component) {
        if (Utils.isMSIE(component))
            component.putClientProperty("horizontalOversize", new Integer(horizontalOversize));

        super.installCG(component);
    }

    public String getText(XRichTextEditor component) {
        String text = component.getText();

        text = text.replaceAll("'", "\\'");

        return text;
    }

    private String doubleEscape(String text) {
        text = text.replaceAll("\\\'", "\\\\'");
        return text;
    }

    public void writeInternal(Device device, final XRichTextEditor component) throws IOException {
        SDimension preferredSize = component.getPreferredSize();
        boolean tableWrapping = Utils.isMSIE(component) && preferredSize != null && "%".equals(preferredSize.getWidthUnit());
        String actualWidth = null;
        if (tableWrapping) {
            actualWidth = preferredSize.getWidth();
            Utils.setPreferredSize(component, "100%", preferredSize.getHeight());
            device.print("<table style=\"table-layout: fixed; width: " + actualWidth + "\"><tr>");
            device.print("<td style=\"padding-right: " + Utils.calculateHorizontalOversize(component, true) + "px\">");
        }
        /*
        Object clientProperty = component.getClientProperty("onChangeSubmitListener");
        // If the application developer attached any SDocumentListeners to this
    	// STextArea, the surrounding form gets submitted as soon as the content
        // of this STextArea changed.
        if (component.getDocumentListeners().length > 1) {
        	// We need to test if there are at least 2 document
        	// listeners because each text component registers
        	// itself as a listener of its document as well.
            if (clientProperty == null) {
            	String event = JavaScriptEvent.ON_CHANGE;
            	String code = "wingS.request.sendEvent(event, true, " + !component.isReloadForced() + ");";
                JavaScriptListener javaScriptListener = new JavaScriptListener(event, code);
                component.addScriptListener(javaScriptListener);
                component.putClientProperty("onChangeSubmitListener", javaScriptListener);
            }
        } else if (clientProperty != null && clientProperty instanceof JavaScriptListener) {
        	component.removeScriptListener((JavaScriptListener) clientProperty);
        	component.putClientProperty("onChangeSubmitListener", null);
        }
                          */
        device.print("<textarea ");
        Utils.optAttribute(device, "id", component.getName());
        Utils.optAttribute(device, "eid", component.getName());
        Utils.optAttribute(device, "name", component.getName());
        Utils.optAttribute(device, "tabIndex", "-1");
        //Utils.writeEvents(device, component, new String[] { "special" });

        device.print(">");
        device.print(getText(component));
        device.print("</textarea>");

//        Utils.optAttribute(device, "tabindex", component.getFocusTraversalIndex());
        if(tableWrapping) {
            device.print("</td></tr></table>");
        }
    }

    public Update getTextUpdate(XRichTextEditor component) {
        return new TextUpdate(component);
    }

    private class TextUpdate extends AbstractUpdate {
        XRichTextEditor editor;

        public TextUpdate(XRichTextEditor component) {
            super(component);

            this.editor = component;
        }

        public Handler getHandler() {
            UpdateHandler update = new UpdateHandler("runScript");
            String name = "editor_" + component.getName();
            StringBuilder builder = new StringBuilder("wingS.global.onHeadersLoaded(function() {");
            builder.append("window.").append(name);
            builder.append(".setEditorHTML('").append(doubleEscape(getText(editor))).append("');");
            builder.append(" });");
            update.addParameter(builder.toString());
            // TODO: update size, border, etc.
            return update;
        }

        @Override
        public int getPriority() {
            return 1;
        }
    }

    public Update getEnabledAndWritabilityUpdate(XRichTextEditor component) {
        return new EnabledAndWritabilityUpdate(component);
    }

    private class EnabledAndWritabilityUpdate extends AbstractUpdate {
        private EnabledAndWritabilityUpdate(XRichTextEditor component) {
            super(component);
        }

        public Handler getHandler() {
            XRichTextEditor editor = (XRichTextEditor)component;
            UpdateHandler handler = new UpdateHandler("runScript");
            StringBuilder builder = new StringBuilder(editor.getEditorName());
            builder.append(".set('disabled', ");
            builder.append(!editor.isEnabled());
            builder.append(");");

            handler.addParameter(builder.toString());

            // TODO: find a solution for editable

            return handler;
        }

        @Override
        public int getPriority() {
            return 2;
        }
    }
}
