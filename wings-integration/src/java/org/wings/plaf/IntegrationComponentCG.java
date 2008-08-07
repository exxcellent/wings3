package org.wings.plaf;

import org.wings.SComponent;
import org.wings.io.Device;
import org.wings.macro.MacroContainer;
import org.wings.macro.MacroContext;
import org.wings.macro.MacroTag;

import java.io.IOException;

public class IntegrationComponentCG implements ComponentCG, IntegrationCG {

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
        macros.execute();
    }

    public Update getComponentUpdate(SComponent component) {
        // can be ignored at the moment
        return null;
    }

    public void setMacros(MacroContainer macros) {
        this.macros = macros;
    }

    @MacroTag
    public void id(MacroContext context) throws IOException {
        context.getDevice().print("id=\"" + context.getComponent().getName() + "\"");
    }
}
