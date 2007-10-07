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

package org.wingx.example;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.wings.SButton;
import org.wings.SComponent;
import org.wings.SForm;
import org.wings.SGridLayout;
import org.wings.SIcon;
import org.wings.SLabel;
import org.wings.SPanel;
import org.wings.STextField;
import org.wings.SURLIcon;
import org.wings.plaf.WingSetExample;
import org.wings.border.SLineBorder;
import org.wings.event.SDocumentEvent;
import org.wings.event.SDocumentListener;
import org.wingx.XInplaceEditor;

/**
 * Example demonstrating the use of component XInplaceEditor.
 * @author Christian Schyma
 */
public class XInplaceEditorExample implements WingSetExample
{
    private final String someText =
            "\"There is a theory which states that if anybody ever discovers " +
            "exactly what the Universe is for and why it is here, it will " +
            "instantly disappear and be replaced by something even more " +
            "bizarre and inexplicable. There is another theory which states " +
            "that this has already happened.\" \n-- Douglas Adams";

    private final SIcon HZ_PICTURE = new SURLIcon("../icons/Hohenzollern.jpg");
    private SForm form;

    public void activateExample() {
        SLabel picture = new SLabel(HZ_PICTURE);
        XInplaceEditor pictureDescription = new XInplaceEditor("click here to give me a name!", 49, 1);

        SPanel picturePanel = new SPanel(new SGridLayout(1));
        picturePanel.add(picture);
        picturePanel.add(pictureDescription);

        final XInplaceEditor editor = new XInplaceEditor(someText, 80, 2);
        editor.addAjaxDocumentListener(new SDocumentListener() {
            public void changedUpdate(SDocumentEvent e) {
            }
            public void insertUpdate(SDocumentEvent e) {
                e.getDocument().setText(e.getDocument().getText().replaceAll("foo", ""));
            }
            public void removeUpdate(SDocumentEvent e) {
            }
        });

        final STextField rowsTextField = new STextField("2");
        final STextField colsTextField = new STextField("80");

        SButton saveChanges = new SButton("apply changes");
        saveChanges.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                editor.setRows(Integer.parseInt(rowsTextField.getText()));
                editor.setCols(Integer.parseInt(colsTextField.getText()));
            }
        });

        SPanel configPanel = new SPanel(new SGridLayout(2));
        configPanel.setBorder(new SLineBorder(1));
        configPanel.add(new SLabel("In-Place Editor configuration of above example"));
        configPanel.add(saveChanges);
        configPanel.add(new SLabel("# rows:")); configPanel.add(rowsTextField);
        configPanel.add(new SLabel("# cols:")); configPanel.add(colsTextField);

        SGridLayout gridLayout = new SGridLayout(1);
        gridLayout.setHgap(10);
        gridLayout.setVgap(20);
        form = new SForm(gridLayout);
        form.add(picturePanel);
        form.add(editor);
        form.add(configPanel);
    }

    public void passivateExample() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public SComponent getExample() {
        return form;
    }

    public String getExampleName() {
        return "XInplaceEditor";
    }

    public String getExampleGroup() {
        return "Experiment";
    }
}
