package org.wings.plaf.css;

import java.io.IOException;

import org.wings.SSeparator;
import org.wings.io.Device;

public final class SeparatorCG extends AbstractComponentCG<SSeparator>
        implements org.wings.plaf.SeparatorCG {

    private static final long serialVersionUID = 4536595730181839306L;

    public void writeInternal(final Device device, final SSeparator separator)
            throws IOException {
        writeDivPrefix(device, separator, null);
        device.print(".");
        writeDivSuffix(device, separator);
    }
    
}
