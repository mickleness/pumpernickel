/*
 * @(#)AreaXTestApp.java
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
package com.pump.geom;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.math.MathG;
import com.pump.swing.BasicConsole;

public class AreaXTestPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	JTabbedPane tabs = new JTabbedPane();
	
	ChangeListener tabChangeListener = new ChangeListener() {
		TestElement lastTest = null;
		
		@Override
		public void stateChanged(ChangeEvent e) {
			
			JComponent tab = (JComponent)tabs.getSelectedComponent();
			TestElement test = (TestElement) tab.getClientProperty(KEY_TEST_ELEMENT);
			if(test!=lastTest) {
				if(lastTest!=null) {
					lastTest.cancel();
				}
				lastTest = test;
			}
		}
		
	};
	
	BasicConsole console = new BasicConsole(false, true);
	JScrollPane consoleScrollPane = new JScrollPane(console);
	
	public AreaXTestPanel() {
		MathG.acos(0); //jog this into existence so it doesn't affect profiles
		
		addTab(new AccuracyTest(console.createPrintStream(false)), "Accuracy");
		addTab(new TransformTest(console.createPrintStream(false)), "Transform");
		//addTab(new IntersectionsTest(console.createPrintStream(false)), "Intersections");
		addTab(new RelationshipTest(console.createPrintStream(false)), "Relationship");
		addTab(new AddRulesTest(console.createPrintStream(false)), "Rules");
		
		tabs.addChangeListener(tabChangeListener);
		//set up the "lastTest" correctly
		tabChangeListener.stateChanged(null);
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0; c.weightx = 1; c.weighty = 0;
		c.insets = new Insets(3,3,3,3);
		c.fill = GridBagConstraints.HORIZONTAL;
		add(tabs, c);
		c.gridy++; c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		add(consoleScrollPane, c);
	}
	
	private static final String KEY_TEST_ELEMENT = AreaXTestPanel.class.getName()+"#testElement";
	private void addTab(TestElement e,String tabName) {
		e.getComponent().putClientProperty(KEY_TEST_ELEMENT, e);
		e.getComponent().setOpaque(false);
		tabs.add(e.getComponent(), tabName);
	}
}
