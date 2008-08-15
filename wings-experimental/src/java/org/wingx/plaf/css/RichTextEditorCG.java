package org.wingx.plaf.css;

import org.wingx.XRichTextEditor;
import org.wings.io.Device;
import org.wings.plaf.Update;
import org.wings.plaf.css.Utils;
import org.wings.plaf.css.AbstractUpdate;
import org.wings.plaf.css.UpdateHandler;
import org.wings.plaf.css.AbstractComponentCG;
import org.wings.SComponent;

import java.io.IOException;

public class RichTextEditorCG extends AbstractComponentCG<XRichTextEditor> implements org.wingx.plaf.RichTextEditorCG<XRichTextEditor> {

    public void writeInternal(Device device, XRichTextEditor component) throws IOException {
        device.print("<textarea");
        Utils.optAttribute(device, "id", component.getName());
        Utils.optAttribute(device, "eid", component.getName());
        Utils.optAttribute(device, "name", component.getName());
        device.print(">");
        device.print(((XRichTextEditor)component).getDocument().getText());
        device.print("</textarea>");

        device.print("<script type=\"text/javascript\">");
        String name = "editor_" + component.getName();

        String height = "";
        String width = "";
        if(component.getPreferredSize() != null) {
            height = component.getPreferredSize().getHeight();
            width = component.getPreferredSize().getWidth();
        } else {
            height = "100%";
            width = "100%";
        }

        String editor = "SimpleEditor";
        if(((XRichTextEditor)component).getEditorType() == XRichTextEditor.NORMAL_EDITOR)
            editor = "Editor";

        device.print("var " + name + " = new YAHOO.widget." + editor + "('" + component.getName() + "', {" +
                "height:\"" + height + "\"," +
                "width:\"" + width + "\"" +
                "});\n");
        /*device.print(name + ".on('editorKeyUp', function(o) { "+ name + ".saveHTML(); }, " + name + ", true);\n");
        device.print(name + ".on('toolbarLoaded', function(o) { " +
                                                    "this.toolbar.on('buttonClick', function(o) { "
                                                                                            + name + ".saveHTML(); alert('click');" +
                                                                                        "});" +
                                                           "}, " + name + ", true);\n"); */
        device.print(name + ".on('afterNodeChange', function(o) { var elt = document.getElementById('" + component.getName() + "'); " + name + ".saveHTML(); }, " + name + ", true);");
        device.print(name + ".on('editorKeyUp', function(o) { var elt = document.getElementById('" + component.getName() + "'); " + name + ".saveHTML(); }, " + name + ", true);");
        device.print(name + ".render();");
        device.print("</script>");
    }

    public Update getComponentUpdate(XRichTextEditor component) {
        return new ComponentUpdate(component);
    }

    private class ComponentUpdate extends AbstractUpdate {
        XRichTextEditor editor;

        public ComponentUpdate(XRichTextEditor component) {
            super(component);

            this.editor = component;
        }

        public Handler getHandler() {
            UpdateHandler update = new UpdateHandler("runScript");
            String name = "editor_" + component.getName();
            update.addParameter(name + ".setEditorHTML('" + editor.getText() +"');");
            // TODO: update size, border, etc.
            return update;
        }
    }
}
