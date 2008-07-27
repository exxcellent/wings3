package org.wings.macro;

import org.wings.io.Device;
import org.wings.SComponent;

import java.util.HashMap;

/**
 * <code>MacroContext<code>.
 * <p/>
 * User: rrd
 * Date: 13.08.2007
 * Time: 11:37:00
 *
 * @author rrd
 * @version $Id
 */
public class MacroContext extends HashMap<Object, Object> {

    private static final long serialVersionUID = 1L;

    private static enum MacroContextKey {
        DEVICE, COMPONENT
    }

    public void setDevice(Device device) {
        //noinspection unchecked
        put(MacroContextKey.DEVICE, device);
    }

    public Device getDevice() {
        return (Device) get(MacroContextKey.DEVICE);
    }

    public void setComponent(SComponent component) {
        // noinspection unchecked
        put(MacroContextKey.COMPONENT, component);
    }

    public SComponent getComponent() {
        return (SComponent) get(MacroContextKey.COMPONENT);
    }
}
