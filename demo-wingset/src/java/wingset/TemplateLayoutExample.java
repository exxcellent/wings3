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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.tree.DefaultTreeModel;

import org.wings.SButton;
import org.wings.SComponent;
import org.wings.SConstants;
import org.wings.SDimension;
import org.wings.SLabel;
import org.wings.SPanel;
import org.wings.SScrollPane;
import org.wings.STemplateLayout;
import org.wings.STextArea;
import org.wings.STextField;
import org.wings.STree;
import org.wings.border.SLineBorder;
import org.wings.style.CSSProperty;
import org.wings.template.StringTemplateSource;
import org.wings.template.propertymanagers.DefaultPropertyManager;

/**
 * @author <a href="mailto:armin.haaf@mercatis.de">Armin Haaf</a>
 */
public class TemplateLayoutExample
    extends WingSetPane
        implements SConstants {

    final static String TEMPLATE = "/templates/TemplateExample.thtml";

    private String templateString;
    protected StringTemplateSource templateSource;
    protected STextArea templateInput;

    private ComponentControls controls;

    protected SComponent createControls() {
        controls = new Controls();
        return controls;
    }

    protected SComponent createExample() {

        try {
            java.net.URL templateURL = getSession().getServletContext().getResource(TEMPLATE);
            StringBuilder buffer = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(templateURL.openStream()));

            String line = reader.readLine();
            while (line != null) {
                buffer.append(line).append('\n');
                line = reader.readLine();
            }
            templateString = buffer.toString();
        }
        catch (Exception ex) {
            templateString =
                    "A simple interactive example how to use template layouts:<br/>\n" +
                    "<input type=textarea column=\"100\" rows=\"10\" name=\"TemplateInput\"/> <br/>\n" +
                    "<input type=submit text=\"Apply\" name=\"Apply\"/>";
            ex.printStackTrace();
        }

        templateSource = new StringTemplateSource(templateString);
        templateInput = new STextArea(templateString);
        templateInput.setAttribute(CSSProperty.FONT, "12px monospace");

        SButton applyButton = new SButton("Apply");
        applyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                templateSource.setTemplate(templateInput.getText());
                reload();
            }
        });

        SLabel label = new SLabel("Simple Label");

        SPanel panel = new SPanel();
        panel.add(new SLabel("BeanScript support not enabled. Define value 'true' for " +
                "property "+ DefaultPropertyManager.BEANSCRIPT_ENABLE+" in web.xml " +
                "to enable BeanScript support!"), "theLabel");
        panel.add(new STextField(), "NAME");
        panel.add(new STextField(), "FIRSTNAME");

        STree tree = new STree(new DefaultTreeModel(HugeTreeModel.ROOT_NODE));
        SScrollPane scrollPane = new SScrollPane(tree);
        scrollPane.setMode(SScrollPane.MODE_COMPLETE);
        scrollPane.setPreferredSize(new SDimension("320px", "180px"));
        panel.add(scrollPane, "TREE");
        panel.setVerticalAlignment(SConstants.TOP_ALIGN);
        try {
			panel.setLayout(new STemplateLayout(templateSource));
		} catch (IOException e) {
			e.printStackTrace();
		}
		panel.add(templateInput, "TemplateInput");
        panel.add(applyButton, "Apply");
        panel.add(label, "Label");
        panel.setVerticalAlignment(SConstants.TOP_ALIGN);

        return panel;
    }

    class Controls extends ComponentControls {
        public Controls() {
            globalControls.setVisible(false);
            
            SLineBorder border = new SLineBorder(new Color(0xCC, 0xCC, 0xCC), 0);
            border.setThickness(1, SConstants.TOP);
            setBorder(border);
            SButton resetButton = new SButton("Reset");
            resetButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    templateSource.setTemplate(templateString);
                    templateInput.setText(templateString);
                    TemplateLayoutExample.this.reload();
                }
            });

            addControl(resetButton);
        }
    }
}
