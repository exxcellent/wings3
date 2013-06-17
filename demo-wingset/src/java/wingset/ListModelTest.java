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

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.wings.SButton;
import org.wings.SComponent;
import org.wings.SContainer;
import org.wings.SDefaultListModel;
import org.wings.SDefaultListSelectionModel;
import org.wings.SGridLayout;
import org.wings.SLabel;
import org.wings.SList;
import org.wings.SListSelectionModel;
import org.wings.SPanel;

/**
 * @author <a href="mailto:haaf@mercatis.de">Armin Haaf</a>
 */
public class ListModelTest
    extends WingSetPane
{
	private final SDefaultListModel listModel = new SDefaultListModel();
	private final SListSelectionModel listSelectionModel = new SDefaultListSelectionModel();
    private final SList list = new SList();

    private final SLabel reportLabel = new SLabel("No button pressed");
    private final SLabel reportSelectionLabel = new SLabel("Nothing selected");
    protected ListSelectionListener selection = new ListSelectionListener() {
    	public void valueChanged(ListSelectionEvent e) {
    		StringBuilder builder = new StringBuilder();
    		final Object[] selection = list.getSelectedValues();
    		if( selection == null || selection.length < 1 )
    			builder.append( "Nothing selected" );
    		else {
    			builder.append( "<html>List <b>'" );
    			builder.append( selection[ 0 ].toString() );
    			for( int i = 1;i < selection.length;i++ ) {
    				builder.append( "," );
    				builder.append( selection[ i ].toString() );
    			}
        		builder.append( "'</b> selected" );
    		}
    		
    		reportSelectionLabel.setText( builder.toString() );
        }
    };
    
    private int min = 1;
    private int max = 10;

    protected SComponent createControls() {
        return null;
    }

    public SComponent createExample() {
        return createListMultSelExample();
    }

    public SContainer createListMultSelExample() {
    	for( int i = min;i <=max;i++ )
    		listModel.addElement( "Option" + i );
    	
        list.setName("multiple");
        list.setSelectionMode(SList.MULTIPLE_SELECTION);
        list.setModel(listModel);
        list.setSelectionModel( listSelectionModel );
        list.setShowAsFormComponent(true);
        list.setToolTipText("CheckBox");
        list.setName("Select");
        list.addListSelectionListener( selection );

        final SButton modelSelectButton = new SButton( "Select List Model" );
        modelSelectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int random = min + (int)(Math.random() * ((max - min) + 1));
				listSelectionModel.setSelectionInterval( random - min -1,random - min - 1 );				
	            reportLabel.setText("<html>ListModel changed to <b>'" + list.getSelectedValue().toString() + "'</b>");
			}
		} );
        final SButton clearSelectButton = new SButton( "Clear Select List Model" );
        clearSelectButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				listSelectionModel.clearSelection();				
	            reportLabel.setText("<html>ListModel selection <b>'cleared'</b>");
			}
		} );

        final SButton modelDecreaseButton = new SButton( "Remove to ComboBox Model" );
        modelDecreaseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				listModel.removeElement( "Option" + min );				
	            reportLabel.setText("<html>ListModel removed <b>'Option" + min + "'</b>");
				min++;
			}
		} );
        final SButton modelIncreaseButton = new SButton( "Add to ComboBox Model" );
        modelIncreaseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				max++;
				listModel.addElement( "Option" + max );				
	            reportLabel.setText("<html>ListModel added <b>'Option" + max + "'</b>");
			}
		} );
        
        final SPanel panel = new SPanel(new SGridLayout(3, 1, 0, 20));
        panel.add(list);
        panel.add(modelSelectButton);
        panel.add(clearSelectButton);
        panel.add(modelDecreaseButton);
        panel.add(modelIncreaseButton);
        panel.add(reportLabel);
        panel.add(reportSelectionLabel);

        return panel;    	
    }
}
