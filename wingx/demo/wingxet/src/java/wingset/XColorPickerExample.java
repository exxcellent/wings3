/*
 * $Id: TextComponentExample.java 2750 2006-08-02 08:10:54Z hengels $
 * Copyright 2006 wingS development team.
 *
 * This file is part of wingS (http://wingsframework.org).
 *
 * wingS is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 *
 * Please see COPYING for the complete licence.
 */

package wingset;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.event.ChangeEvent;
import org.wings.SButton;
import org.wings.SComponent;
import org.wings.SDimension;
import org.wings.SForm;
import org.wings.SGridLayout;
import org.wings.SLabel;
import org.wings.SPanel;
import org.wings.SToggleButton;
import org.wings.border.SLineBorder;
import org.wings.event.SAjaxChangeListener;
import org.wingx.XColorPicker;

/**
 * Example demonstrating the component XColorPicker.
 * @author Christian Schyma
 */
public class XColorPickerExample extends WingSetPane {

    protected SComponent createControls() {
        return null;
    }

    protected SComponent createExample() {

        final SPanel colorPanel = new SPanel();
        final XColorPicker picker = new XColorPicker();

        colorPanel.setBorder(new SLineBorder(1));
        colorPanel.setPreferredSize(new SDimension(150, 100));
        SLabel label = new SLabel("this is an individual SPanel component " +
                "that gets colored by the color picker to the left");
        label.setWordWrap(true);
        colorPanel.add(label);

        picker.addAjaxChangeListener(new SAjaxChangeListener(){
            public void stateChanged(ChangeEvent e) {
                colorPanel.setBackground(((XColorPicker)e.getSource()).getSelectedColor());
            }
        });

        final SButton updatePanelButton = new SButton("update panel");
        updatePanelButton.setVisible(false);
        final SToggleButton toggleImmediateUpdateButton = new SToggleButton("disable immediate updates");

        SPanel buttonPanel = new SPanel(new SGridLayout(2, 1));
        buttonPanel.add(updatePanelButton);
        buttonPanel.add(toggleImmediateUpdateButton);

        updatePanelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // just to simulate a POST
            }
        });

        toggleImmediateUpdateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                picker.setImmediateUpdate(!picker.isImmediateUpdate());
                updatePanelButton.setVisible(!updatePanelButton.isVisible());

            }
        });

        SGridLayout gridLayout = new SGridLayout(3);
        gridLayout.setHgap(50);
        SForm form = new SForm(gridLayout);
        form.add(picker);
        form.add(colorPanel);
        form.add(buttonPanel);

        return form;
    }

}
