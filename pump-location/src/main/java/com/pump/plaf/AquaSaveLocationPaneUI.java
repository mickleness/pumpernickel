/*
 * @(#)AquaSaveLocationPaneUI.java
 *
 * $Date: 2014-03-13 04:15:48 -0400 (Thu, 13 Mar 2014) $
 *
 * Copyright (c) 2011 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Jeremy Wood. For details see accompanying license terms.
 * 
 * This software is probably, but not necessarily, discussed here:
 * https://javagraphics.java.net/
 * 
 * That site should also contain the most recent official version
 * of this software.  (See the SVN repository for more details.)
 */
package com.pump.plaf;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.pump.io.location.IOLocation;
import com.pump.swing.io.SaveLocationPane;

public class AquaSaveLocationPaneUI extends BasicSaveLocationPaneUI {
	

	private AquaOpenLocationPaneUI openPane;
	
	private JLabel whereLabel = new JLabel("Where:");
	private JComboBox whereComboBox = new JComboBox();
	
	public AquaSaveLocationPaneUI(SaveLocationPane p) {
		super(p);
		
	}
	
	private AquaOpenLocationPaneUI getOpenPane() {
		if(openPane==null)
			openPane = new AquaOpenLocationPaneUI(locationPane);
		return openPane;
	}
	
	@Override
	protected JComponent[] getLeftFooterControls() {
		return new JComponent[] {newFolderButton};
	}

	@Override
	public IOLocation getDefaultDirectory() {
		return getOpenPane().getDefaultDirectory();
	}
	
	@Override
	protected String getNewFolderName() {
		return getOpenPane().getNewFolderName();
	}
	

	@Override
	public void setExpanded(boolean b) {
		super.setExpanded(b);
		whereLabel.setVisible(!b);
		whereComboBox.setVisible(!b);
	}

	@Override
	protected void populateLowerPanel(JPanel lower) {
		getOpenPane().installGUI(lower);
	}

	@Override
	protected void populateUpperPanel(JPanel upper) {
		upper.setLayout(new GridBagLayout());

		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0; c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(3,3,3,3);
		upper.add(saveLabel, c);
		c.gridy++;
		upper.add(whereLabel, c);
		c.gridy--; c.gridx++;
		c.anchor = GridBagConstraints.WEST;
		upper.add(saveField, c);
		c.gridy++;
		upper.add(whereComboBox, c);
		c.gridy--; c.gridx++;
		c.fill = GridBagConstraints.NONE;
		upper.add(expandButton, c);
	}
	
	
}
