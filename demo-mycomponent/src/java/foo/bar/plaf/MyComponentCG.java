// (c) copyright 2006 by eXXcellent solutions, Ulm. Author: bschmid

package foo.bar.plaf;

import foo.bar.MyComponent;
import org.wings.io.Device;
import org.wings.plaf.css.AbstractComponentCG;

import java.io.IOException;

/**
 * Example implementation for a component CG.
 * <p>The mapping component to CG can be achieved using custom <code>default.properties</code> 
 * and <code>default_shortbrowsername.properties</code> in the <code>org.wings.plaf.css</code>
 * package.  
 *
 * @author Benjamin Schmid <B.Schmid@exxcellent.de>
 */
public class MyComponentCG extends AbstractComponentCG<MyComponent> {

    @Override
    public void writeInternal(final Device device, final MyComponent component) throws IOException {

    }
}
