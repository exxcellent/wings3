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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.MutableComboBoxModel;

import org.wings.SButton;
import org.wings.SComboBox;
import org.wings.SComponent;
import org.wings.SContainer;
import org.wings.SDefaultComboBoxModel;
import org.wings.SGridLayout;
import org.wings.SLabel;
import org.wings.SPanel;

/**
 * @author <a href="mailto:haaf@mercatis.de">Armin Haaf</a>
 */
public class ComboBoxModelTest
        extends WingSetPane
{
	private final MutableComboBoxModel comboBoxModel = new SDefaultComboBoxModel();
    private final SComboBox comboBox = new SComboBox();

    private final SLabel reportLabel = new SLabel("No button pressed");
    private final SLabel reportSelectionLabel = new SLabel("Nothing selected");
    protected ActionListener action = new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
        	reportSelectionLabel.setText("<html>Comobox <b>'" + comboBoxModel.getSelectedItem() + "'</b> selected");
        }
    };
    
    private int min = 1;
    private int max = 5;
        
    protected SComponent createControls() {
        return null;
    }

    public SComponent createExample() {
        return createComboBoxExample();
    }

    SContainer createComboBoxExample() {
    	for( int i = min;i <=max;i++ )
    	comboBoxModel.addElement( "Option" + i );
    	
    	comboBox.setModel(comboBoxModel);
        comboBox.setShowAsFormComponent(true);
        comboBox.setToolTipText("CheckBox");
        comboBox.setName("Select");
        comboBox.addActionListener(action);

        final SButton modelSelectButton = new SButton( "Select ComboBox Model" );
        modelSelectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int random = min + (int)(Math.random() * ((max - min) + 1));
				comboBoxModel.setSelectedItem( "Option" + random );				
	            reportLabel.setText("<html>ComboBoxModel changed to <b>'" + comboBoxModel.getSelectedItem() + "'</b>");
			}
		} );
        final SButton clearSelectButton = new SButton( "Clear Select List Model" );
        clearSelectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				comboBoxModel.setSelectedItem(null);				
	            reportLabel.setText("<html>ListModel selection <b>'cleared'</b>");
			}
		} );
        final SButton modelDecreaseButton = new SButton( "Remove to ComboBox Model" );
        modelDecreaseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				comboBoxModel.removeElement( "Option" + min );				
	            reportLabel.setText("<html>ComboBoxModel rmoved <b>'Option" + min + "'</b>");
				min++;
			}
		} );
        final SButton modelIncreaseButton = new SButton( "Add to ComboBox Model" );
        modelIncreaseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				max++;
				comboBoxModel.addElement( "Option" + max );				
	            reportLabel.setText("<html>ComboBoxModel added <b>'Option" + max + "'</b>");
			}
		} );
        
        final SPanel panel = new SPanel(new SGridLayout(3, 1, 0, 20));
        panel.add(comboBox);
        panel.add(modelSelectButton);
        panel.add(clearSelectButton);
        panel.add(modelDecreaseButton);
        panel.add(modelIncreaseButton);
        panel.add(reportLabel);
        panel.add(reportSelectionLabel);

        return panel;
    }
}
