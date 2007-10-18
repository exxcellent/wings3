// (c) copyright 2006 by eXXcellent solutions, Ulm. Author: bschmid

package foo.bar;

import org.wings.SBorderLayout;
import org.wings.SComponent;
import org.wings.SPanel;
import org.wings.SDimension;
import org.wings.SConstants;
import org.wings.border.SLineBorder;
import org.wings.plaf.WingSetExample;

import java.awt.Color;

/**
 * A simple example to show this component up inside the wingset demo.
 *
 * @author Benjamin Schmid <B.Schmid@exxcellent.de>
 */
public class MyComponentExample implements WingSetExample {

    private SPanel panel;
    private MyComponent myComponent;

    public void activateExample() {
        panel = new SPanel(new SBorderLayout());
        panel.setBackground(Color.yellow);
        myComponent = new MyComponent("ExampleTest");
        myComponent.setBorder(new SLineBorder(Color.blue));
        myComponent.setPreferredSize(new SDimension(300,100));
        myComponent.setHorizontalAlignment(SConstants.CENTER);
        panel.add(myComponent, SBorderLayout.CENTER);
    }

    public void passivateExample() {
        // don't care
    }

    public SComponent getExample() {
        return panel;
    }

    public String getExampleName() {
        return "MyComponent";
    }

    public String getExampleGroup() {
        return "Custom";
    }
}
