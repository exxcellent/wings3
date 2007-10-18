/*
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

package org.wingx.example;

import javax.swing.event.ChangeEvent;
import org.wings.SComponent;
import org.wings.SDimension;
import org.wings.SFrame;
import org.wings.SPanel;
import org.wings.plaf.WingSetExample;
import org.wings.border.SLineBorder;
import org.wings.event.SAjaxChangeListener;
import org.wings.script.JavaScriptDOMListener;
import org.wings.script.JavaScriptEvent;
import org.wingx.XColorPicker;
import org.wingx.XPopupFrame;

/**
 * Example demonstrating the use of component XPopupFrame.
 * @author Christian Schyma
 */
public class XPopupFrameExperiment implements WingSetExample
{
    private SPanel panel;

    public void activateExample() {
        panel = new SPanel();

        SFrame f = new SFrame();
        XColorPicker picker = new XColorPicker(0, 0, 0);
        f.getContentPane().add(picker);

        XPopupFrame popupFrame = new XPopupFrame(f, 400, 300);
        panel.add(popupFrame);

        final SPanel colorPanel = new SPanel();
        colorPanel.setBorder(new SLineBorder(1));
        colorPanel.setPreferredSize(new SDimension(50, 50));
        colorPanel.setToolTipText("click me!");
        colorPanel.addScriptListener(new JavaScriptDOMListener(JavaScriptEvent.ON_CLICK, popupFrame.showScript(), colorPanel));
        panel.add(colorPanel);

        picker.addAjaxChangeListener(new SAjaxChangeListener(){
            public void stateChanged(ChangeEvent e) {
                colorPanel.setBackground(((XColorPicker)e.getSource()).getSelectedColor());
            }
        });
    }

    public void passivateExample() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public SComponent getExample() {
        return panel;
    }

    public String getExampleName() {
        return "XPopupFrame";
    }

    public String getExampleGroup() {
        return "Experiment";
    }
}




