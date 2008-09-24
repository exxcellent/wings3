package org.wings.plaf;

import org.wings.SComponent;
import org.wings.plaf.css.AbstractComponentCG;
import org.wings.plaf.css.Utils;
import org.wings.session.ScriptManager;
import org.wings.io.Device;
import org.wings.macro.MacroContainer;
import org.wings.macro.MacroContext;
import org.wings.macro.MacroTag;

import java.io.IOException;

public class IntegrationComponentCG extends AbstractComponentCG<SComponent> implements IntegrationCG {

    private static final long serialVersionUID = 1L;

    private MacroContainer macros;

    public void installCG(SComponent c) {
        // ignore
    }

    public void uninstallCG(SComponent c) {
        // ignore
    }

    public void componentChanged(SComponent c) {
        // can be ignored at the moment
    }

    public void write(Device device, SComponent component) throws IOException {
    	Utils.printDebug(device, "<!-- ");
        Utils.printDebug(device, component.getName());
        Utils.printDebug(device, " -->");
        
        component.fireRenderEvent(SComponent.START_RENDERING);

        writeInternal(device, component);

        component.fireRenderEvent(SComponent.DONE_RENDERING);
        Utils.printDebug(device, "<!-- /");
        Utils.printDebug(device, component.getName());
        Utils.printDebug(device, " -->");

        updateDragAndDrop(component);
    }

	public void writeInternal(Device device, SComponent component) throws IOException {
		
		device.print("<div id=\"").print(component.getName()).print("\">");
		
		macros.getContext().setDevice(device);
		macros.execute();
		
		device.print("</div>");
    }

    public void setMacros(MacroContainer macros) {
        this.macros = macros;
    }

    @MacroTag
    public void id(MacroContext context) throws IOException {
        context.getDevice().print("id=\"" + context.getComponent().getName() + "\"");
    }
}
