// (c) copyright 2006 by eXXcellent solutions, Ulm. Author: bschmid

package foo.bar.plaf;

import foo.bar.MyComponent;
import org.wings.header.Header;
import org.wings.io.Device;
import org.wings.plaf.css.AbstractComponentCG;
import org.wings.plaf.css.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Example implementation for a component CG. <p>The mapping component to CG can be achieved using custom <code>default.properties</code>
 * and <code>default_shortbrowsername.properties</code> in the <code>org.wings.plaf.css</code> package.
 *
 * @author Benjamin Schmid <B.Schmid@exxcellent.de>
 */
public class MyComponentCG extends AbstractComponentCG<MyComponent> {

    /** A static immutable list of all external resources we need to run our custom component. */
    protected final static List<Header> headers;

    static {
        List<Header> headerList = new ArrayList<Header>();
        // add and register our custom css stylesheet
        headerList.add(Utils.createExternalizedCSSHeader("foo/bar/css/customcomponent.css"));
        // reuse and declare that we utilize some libraries that are already bundled with wingS core.
        headerList.add(Utils.createExternalizedJSHeaderFromProperty(Utils.JS_YUI_EVENT));
        // Include our custom javascript
        headerList.add(Utils.createExternalizedJSHeader("foo/bar/js/customcomponent.js"));
        headers = Collections.unmodifiableList(headerList);
    }

    @Override
    public void writeInternal(final Device device, final MyComponent component) throws IOException {

    }
}
