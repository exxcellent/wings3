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

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * <code>DialogExample</code>.
 * <p/>
 * User: raedler
 * Date: Oct 11, 2007
 * Time: 12:42:05 PM
 *
 * @author raedler
 * @version $Id
 */
public class DialogExample extends WingSetPane {

    protected static final Boolean[] TRUE_FALSE = new Boolean[] { Boolean.TRUE, Boolean.FALSE };

    private ComponentControls controls;

    protected SComponent createControls() {
        controls = new DialogControls();
        return controls;
    }

    protected SComponent createExample() {
        return new SPanel();
    }

    class DialogControls extends ComponentControls {

        DialogControls() {
            globalControls.setVisible(false);

            final SComboBox modality = new SComboBox(TRUE_FALSE);
            addControl(new SLabel("Modal:"));
            addControl(modality);

            final SComboBox draggability = new SComboBox(TRUE_FALSE);
            addControl(new SLabel("Draggable"));
            addControl(draggability);

            SButton createDialog = new SButton("Create Dialog");
            addControl(createDialog);

            createDialog.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    Boolean modal = (Boolean) modality.getSelectedItem();
                    Boolean draggable = (Boolean) draggability.getSelectedItem();

                    final SDialog dialog = new SDialog(getParentFrame(), "SDialog", modal);
                    dialog.setLayout(new SFlowDownLayout());
                    dialog.setDraggable(draggable);

                    if (draggable) {
                        dialog.add(new SLabel("I am draggable. Drag me!"));
                    }

                    SButton close = new SButton("Close");
                    dialog.add(close);

                    close.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            dialog.setVisible(false);
                        }
                    });

                    dialog.setVisible(true);
                }
            });
        }
    }
}
