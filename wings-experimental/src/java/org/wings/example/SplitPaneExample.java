package org.wings.example;

import org.wings.*;
import org.wings.plaf.WingSetExample;
import org.wings.border.SLineBorder;

import java.awt.event.*;

/**
 * @author <a href="mailto:haaf@mercatis.de">Armin Haaf</a>
 */
public class SplitPaneExample
    implements WingSetExample
{
    //private SplitPaneControls controls;
    private SSplitPane splitPane;

    /*
    protected SComponent createControls() {
        controls = new SplitPaneControls();
        return controls;
    }
    */

    public void activateExample() {
        SLabel left = new SLabel("left");
        left.setPreferredSize(SDimension.FULLAREA);
        left.setBorder(new SLineBorder(1));
        SLabel right = new SLabel("right");
        right.setPreferredSize(SDimension.FULLAREA);
        right.setBorder(new SLineBorder(1));
        splitPane = new SSplitPane(SSplitPane.HORIZONTAL_SPLIT, left, right) {
            public void fireIntermediateEvents() {
                super.fireIntermediateEvents();
                //controls.dividerLocationTextField.setText("" + getDividerLocation());
            }
        };
        splitPane.setPreferredSize(SDimension.FULLAREA);

        //controls.addControllable(splitPane);
    }

    public void passivateExample() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public SComponent getExample() {
        return splitPane;
    }

    public String getExampleName() {
        return "SplitPane";
    }

    public String getExampleGroup() {
        return "Experimental";
    }

    /*
    class SplitPaneControls
        extends ComponentControls {
        private final String[] ORIENTATIONS = new String[] { "horizontal", "vertical" };
        public STextField dividerLocationTextField;

        public SplitPaneControls() {
            widthTextField.setText("100%");
            heightTextField.setText("100%");

            final SComboBox orientationCombo = new SComboBox(ORIENTATIONS);
            orientationCombo.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (ORIENTATIONS[0].equals(orientationCombo.getSelectedItem())) {
                        splitPane.setOrientation(SSplitPane.HORIZONTAL_SPLIT);
                    }
                    else if (ORIENTATIONS[1].equals(orientationCombo.getSelectedItem())) {
                        splitPane.setOrientation(SSplitPane.VERTICAL_SPLIT);
                    }
                }
            });

            final STextField dividerSizeTextField = new STextField();
            dividerSizeTextField.setText("4");
            dividerSizeTextField.setColumns(1);
            dividerSizeTextField.setToolTipText("length only (example: '4')");
            dividerSizeTextField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    try {
                        int dividerSize = Integer.parseInt(dividerSizeTextField.getText());
                        splitPane.setDividerSize(dividerSize);
                    }
                    catch (NumberFormatException e) {}
                }
            });

            dividerLocationTextField = new STextField();
            dividerLocationTextField.setText("-1");
            dividerLocationTextField.setColumns(3);
            dividerLocationTextField.setToolTipText("length only (example: '4')");
            dividerLocationTextField.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    try {
                        int dividerSize = Integer.parseInt(dividerLocationTextField.getText());
                        splitPane.setDividerLocation(dividerSize);
                    }
                    catch (NumberFormatException e) {}
                }
            });

            addControl(new SLabel(" orientation"));
            addControl(orientationCombo);
            addControl(new SLabel(" divider size"));
            addControl(dividerSizeTextField);
            addControl(new SLabel(" divider location"));
            addControl(dividerLocationTextField);
        }
    }
    */
}
