// (c) copyright 2006 by eXXcellent solutions, Ulm. Author: bschmid

package org.wings.plaf;

import org.wings.SComponent;

/**
 * Implement this interface with a class declared in your default.properties to
 * show it as example within the wingset demo.
 *
 * @author Benjamin Schmid <B.Schmid@exxcellent.de>
 */
public interface WingSetExample {

    /**
     * Called by the demo to indicate that it will soon use and show your example.
     */
    void activateExample();

    /**
     * Indicated that your example has been hidden until next {@link #activateExample()} 
     */
    void passivateExample();

    /**
     * @return The SComponent representing and showing your example.
     */
    SComponent getExample();

    /**
     * @return The name of your example and/or component
     */
    String getExampleName();

    /**
     * @return The group your example belongs to.
     */
    String getExampleGroup();
}
