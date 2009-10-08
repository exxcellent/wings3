package org.wings.plaf.css;

import org.wings.SComponent;
import org.wings.SLabel;
import org.wings.SIcon;
import org.wings.STransferHandler;
import org.wings.script.ScriptListener;
import org.wings.session.SCursor;
import org.wings.session.ScriptManager;
import org.wings.plaf.Update;
import org.wings.io.Device;
import org.wings.io.StringBuilderDevice;

import java.io.IOException;
import java.util.List;
import java.util.Collection;

public class CursorCG extends AbstractComponentCG implements org.wings.plaf.CursorCG {
    public CursorCG() {
    }

    public void writeInnerComponent(Device device, SCursor component) throws IOException {
        List<SIcon> icons = component.getIconsByPriority();

        device.print("<span>");

        for(SIcon icon : icons) {
            device.print("<img src=\"");
            device.print(icon.getURL());
            device.print("\" alt=\"");
            device.print(icon.getIconTitle());
            if(icon.getIconHeight() != -1) {
                device.print("\" height=\"");
                device.print(icon.getIconHeight());
            }
            if(icon.getIconWidth() != -1) {
                device.print("\" width=\"");
                device.print(icon.getIconWidth());
            }
            device.print("\" />");
        }

        SLabel label = component.getLabel();
        if(label != null) {
            LabelCG labelCG = (LabelCG)label.getCG();

            labelCG.write(device, label);
        }

        device.print("</span>");
    }

    public void writeComponent(Device device, SCursor component) throws IOException {
        //Write outer element (Updates update everything inside)
		device.print("<div");
        Utils.writeAllAttributes(device, component);
        device.print(">");
		
		//Write inner element
        writeInnerComponent(device, component);

        device.print("</div>");

    }

    public void writeInternal(Device device, SComponent component) throws IOException {
        writeComponent(device, (SCursor)component);

        final SComponent cursor = component;

        ScriptManager.getInstance().addScriptListener(new ScriptListener() {
            public String getScript() {
                StringBuilder builder = AbstractComponentCG.STRING_BUILDER.get();
                builder.setLength(0);

                builder.append("wingS.cursor.setCursorElement('" + cursor.getName() + "');\n");

                return builder.toString();
            }


            public String getEvent() {
                return null;
            }

            public String getCode() {
                return null;
            }

            public int getPriority() {
                return LOW_PRIORITY;
            }
        });
    }

    public Update getContentUpdate(SComponent component) {
        return new ContentUpdate(component);
    }

    protected final static class ContentUpdate extends AbstractUpdate {
        public ContentUpdate(SComponent component) {
            super(component);
        }
        String htmlCode = "";
        String exception = null;

        public Handler getHandler() {
            UpdateHandler handler = new UpdateHandler("text");
            handler.addParameter(component.getName());
            
            SCursor cursor = (SCursor)component;
            try {
                StringBuilderDevice htmlDevice = new StringBuilderDevice(1024);
                CursorCG cg = (CursorCG)cursor.getCG();
                cg.writeInnerComponent(htmlDevice, cursor);
                htmlCode = htmlDevice.toString();
            } catch(Throwable t) {
                exception = t.getClass().getName();
            }

            handler.addParameter(htmlCode);

            if(exception != null)
                handler.addParameter(exception);

            return handler;
        }
    }

    public Update getVisibilityUpdate(SComponent component, boolean newVisibility) {
        return new VisibilityUpdate(component, newVisibility);
    }

    protected final static class VisibilityUpdate extends AbstractUpdate {
        private boolean visibility;
        public VisibilityUpdate(SComponent component, boolean visibility) {
            super(component);

            this.visibility = visibility;
        }
        
        public Handler getHandler() {
            UpdateHandler handler = new UpdateHandler("visibility");
            handler.addParameter(component.getName());
            if(visibility) {
                handler.addParameter("block");
                handler.addParameter("visible");
            } else {
                handler.addParameter("none");
                handler.addParameter("hidden");
            }


            return handler;
        }
    }
}
