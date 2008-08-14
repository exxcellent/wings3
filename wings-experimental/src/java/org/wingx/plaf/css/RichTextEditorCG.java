package org.wingx.plaf.css;

import org.wingx.XRichTextEditor;
import org.wings.io.Device;
import org.wings.plaf.Update;
import org.wings.plaf.css.Utils;

import java.io.IOException;

public class RichTextEditorCG implements org.wingx.plaf.RichTextEditorCG {
    public void installCG(XRichTextEditor c) {
    }

    public void uninstallCG(XRichTextEditor c) {
    }

    public void componentChanged(XRichTextEditor c) {
    }

    public void write(Device device, XRichTextEditor component) throws IOException {
        device.print("<textarea");
        Utils.attribute(device, "id", component.getName());
        device.print(">");
        device.print(component.getDocument().getText());
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
        if(component.getEditorType() == XRichTextEditor.NORMAL_EDITOR)
            editor = "Editor";

        device.print("var " + name + " = new YAHOO.widget." + editor + "('" + component.getName() + "', {" +
                "height:'" + height + "'," +
                "width:'" + width + "'," +
                "handleSubmit:true" +
                "});");

        device.print(name + ".render();");
        device.print("</script>");
    }

    public Update getComponentUpdate(XRichTextEditor component) {
        return null;
    }
}
