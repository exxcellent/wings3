package org.wings.example;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.wings.SButton;
import org.wings.SComponent;
import org.wings.SForm;
import org.wings.SGridLayout;
import org.wings.SSlider;
import org.wings.SLabel;
import org.wings.plaf.WingSetExample;

/**
 *
 * @author Christian Schyma
 */
public class SliderExample
        implements WingSetExample
{
    private SForm form;

    public SliderExample() {
    }

    public void activateExample() {
        final SLabel label = new SLabel("");

        SGridLayout layout = new SGridLayout(2);
        layout.setHgap(10);
        layout.setVgap(40);

        form = new SForm(layout);

        SSlider horizSlider1 = new SSlider(0, 200, 150);

        SSlider horizSlider2 = new SSlider(-100, 100, 50);

        SSlider horizSlider3 = new SSlider(-400, -200, -300);
        horizSlider3.setMajorTickSpacing(25);
        horizSlider3.setSnapToTicks(true);

        SSlider vertSlider = new SSlider(SSlider.VERTICAL, 0, 300, 0);
        vertSlider.setMajorTickSpacing(30);
        vertSlider.setSnapToTicks(true);
        vertSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                label.setText("value of slider 4 is "+ String.valueOf(((SSlider)e.getSource()).getValue()));
            }
        });

        final SButton button = new SButton("store slider positions");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // just to simulate a POST
            }
        });

        form.add(new SLabel("slider 1 [0,200]"));
        form.add(horizSlider1);

        form.add(new SLabel("slider 2 [-100, 100]"));
        form.add(horizSlider2);

        form.add(new SLabel("slider 3 [-400, -200], snaps to every 25 ticks"));
        form.add(horizSlider3);

        form.add(new SLabel("slider 4 [0, 300], snaps to every 30 ticks"));
        form.add(vertSlider);

        form.add(button);
        form.add(label);
    }

    public void passivateExample() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public SComponent getExample() {
        return form;
    }

    public String getExampleName() {
        return "Slider";
    }

    public String getExampleGroup() {
        return "Experimental";
    }
}
