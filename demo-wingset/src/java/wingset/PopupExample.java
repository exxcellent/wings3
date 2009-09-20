/*
 * Copyright 2000,2005 wingS development team.
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

import org.wings.*;
import org.wings.border.SLineBorder;

import javax.swing.AbstractAction;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author Christian Schyma
 */
public class PopupExample
        extends WingSetPane {

    private static final SIcon WINGS_IMAGE = new SURLIcon("../icons/wings-logo.png");
    private static final String SOME_TEXT =
            "What is wingS? In a nutshell, wingS is a web application\n" +
            "framework based on Java Servlets, resembling the Swing API with\n" +
            "its MVC paradigm and event oriented design principles. It utilizes\n" +
            "the models, events, and event listeners of Swing. Like in Swing,\n" +
            "components are arranged in a hierarchy of containers, whose root\n" +
            "container is hooked to a frame.";

    private SPanel createContent() {
        SPanel content = new SPanel(new SBorderLayout());
        content.setBorder(new SLineBorder(1));
        content.setBackground(Color.WHITE);
        content.setPreferredSize(new SDimension(200, 200));
        content.add(new SLabel(WINGS_IMAGE), SBorderLayout.NORTH);
        content.add(new SLabel(SOME_TEXT), SBorderLayout.CENTER);
        final SButton button = new SButton(new AbstractAction("Change button text") {

            public void actionPerformed(ActionEvent e) {
                ((SButton) e.getSource()).setText(String.valueOf(System.currentTimeMillis()));
            }
        });
        content.add(button, SBorderLayout.SOUTH);
        content.setPreferredSize(SDimension.FULLAREA);
        return content;
    }

    public SComponent createExample() {
        SPanel panel = new SPanel(new SGridLayout(1, 2, 50, 50));

        SButton popupButton1 = new SButton("Popup button 1");
        SButton popupButton2 = new SButton("Popup button 2");
        panel.add(popupButton1);
        panel.add(popupButton2);
        final SPopup popup1 = new SPopup(popupButton1, SPopup.BOTTOM_LEFT, 2, 2);
        popup1.setPreferredSize(new SDimension(400, 200));
        popup1.setLayout(new SBorderLayout());
        popup1.add(createContent(), SBorderLayout.CENTER);
        final SPopup popup2 = new SPopup(popupButton2, SPopup.BOTTOM_LEFT, 2, 2);
        popup2.setPreferredSize(new SDimension(300, 100));
        popup2.setLayout(new SBorderLayout());
        popup2.add(createContent(), SBorderLayout.CENTER);

        popupButton1.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (popup1.isVisible()) {
                    popup1.setVisible(false);
                } else {
                    popup1.setVisible(true);
                }
            }
        });
        popupButton2.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (popup2.isVisible()) {
                    popup2.setVisible(false);
                } else {
                    popup2.setVisible(true);
                }
            }
        });
        return panel;
    }

    protected SComponent createControls() {
        return null;
    }

}
