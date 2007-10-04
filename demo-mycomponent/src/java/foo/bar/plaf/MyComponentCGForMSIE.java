// (c) copyright 2006 by eXXcellent solutions, Ulm. Author: bschmid

package foo.bar.plaf;

import org.wings.io.Device;
import foo.bar.MyComponent;

import java.io.IOException;

/**
 * A special component CG for specific browsers. Here only for the Microsoft IE.
 *
 * @author Benjamin Schmid <B.Schmid@exxcellent.de>
 */
public final class MyComponentCGForMSIE extends MyComponentCG {

    @Override
    public void writeInternal(final Device device, final MyComponent myComponent) throws IOException {
       writeTablePrefix(device, myComponent);
       device.print("<div onclick=\"foobar()\">").print("msieversion:").print(myComponent.getPayload()).print("</div>");
       writeTableSuffix(device, myComponent);
    }
}
