/*
 * @(#)XPOpenLocationPaneUI.java
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.io.location.IOLocation;
import com.pump.io.location.LocationFactory;
import com.pump.swing.io.LocationBrowser;
import com.pump.swing.io.LocationPane;
import com.pump.swing.io.OpenLocationPane;

public class XPOpenLocationPaneUI extends OpenLocationPaneUI {

	public XPOpenLocationPaneUI(LocationPane p) {
		super(p);
		browser.setUI(new AquaListLocationBrowserUI(browser));
		commitButton.setText("Open");
		cancelButton.setText("Cancel");
	}

	@Override
	public IOLocation getDefaultDirectory() {
		return LocationFactory.get().create(new File(System.getProperty("user.home")));
	}

	protected LocationBrowser createBrowser(OpenLocationPane p) {
		LocationBrowser browser = new LocationBrowser(p.getSelectionModel(), p.getLocationHistory(), p.getGraphicCache());
		browser.setPreferredSize(new Dimension(200,200));
		return browser;
	}

	@Override
	protected void installGUI(JComponent comp) {
		comp.removeAll();
		comp.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0;
		c.weightx = 0; c.weighty = 0;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(5,5,5,5);
		comp.add(new JLabel("Look In:"),c);
		c.gridx++; c.weightx = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		comp.add( new XPDirectoryControls(this), c);
		
		c.gridy++; c.gridx = 0; c.weightx = 0;
		c.fill = GridBagConstraints.BOTH;
		c.gridheight = GridBagConstraints.REMAINDER;
		JPanel sidebar = new JPanel();
		sidebar.setBackground(Color.lightGray);
		sidebar.setOpaque(true);
		comp.add(sidebar, c);
		
		c.gridx++;
		c.fill = GridBagConstraints.BOTH;
		c.weighty = 1; c.weightx = 1;
		c.gridheight = 1;
		comp.add(browser,c);
		
		c.gridy++; c.weighty = 0;
		comp.add( new FileControls(), c);
	}

	@Override
	protected String getNewFolderName() {
		IOLocation directory = locationPane.getLocationHistory().getLocation();
		if(directory.contains("New Folder")!=IOLocation.Contains.DOES_NOT_CONTAIN) {
			return "New Folder";
		}
		int ctr = 2;
		while(directory.contains("New Folder "+ctr)!=IOLocation.Contains.DOES_NOT_CONTAIN) {
			ctr++;
		}
		return "New Folder "+ctr;
	}

	class FileControls extends JPanel {
		private static final long serialVersionUID = 1L;

		JLabel fileLabel = new JLabel("File name:");
		JLabel filterLabel = new JLabel("Files of type:");
		JComboBox fileName = new JComboBox();
		JComboBox filter = new JComboBox();
		
		public FileControls() {
			setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0; c.gridy = 0; c.weightx = 0; c.weighty = 0;
			add(fileLabel,c);
			c.gridy++;
			add(filterLabel,c);
			c.gridx++; c.gridy = 0;
			c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
			c.insets = new Insets(0,25,0,25);
			add(fileName, c);
			c.gridy++;
			add(filter, c);
			c.gridx++; c.weightx = 0;
			c.gridy = 0;
			c.insets = new Insets(0,0,0,0);
			add(commitButton, c);
			c.gridy++;
			add(cancelButton, c);
			
			
			fileName.setEditable(true);
			
			XPButtonUI buttonUI = new XPButtonUI();
			commitButton.setUI(buttonUI);
			cancelButton.setUI(buttonUI);
			
			locationPane.getSelectionModel().addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					IOLocation[] loc = locationPane.getSelectionModel().getSelection();
					if(loc.length>0) {
						String name = loc[0].getName();
						name = removeSuffix(name);
						fileName.setSelectedItem( name );
					}
				}
			});
			
			filter.putClientProperty("JComboBox.isSquare", Boolean.TRUE);
			fileName.putClientProperty("JComboBox.isSquare", Boolean.TRUE);
		}
		
		public String removeSuffix(String s) {
			int i = s.lastIndexOf('.');
			if(i==-1)
				return s;
			return s.substring(0,i);
		}
	}
}
