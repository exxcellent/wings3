// (c) copyright 2006 by eXXcellent solutions, Ulm. Author: bschmid

package foo.bar;

import org.wings.SComponent;

/**
 * A very simple component showing how to create and implement custom component for the wingS framework.
 *
 * @author Benjamin Schmid <B.Schmid@exxcellent.de>
 */
public class MyComponent extends SComponent {

    private String payload;

    public MyComponent(final String payload) {
        this.payload = payload;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(final String payload) {
        // We must notify the reload manager if the view of our component changes i.e. due to
        // a changed attribute.
        reloadIfChange(payload, this.payload);
        this.payload = payload;
    }
}
