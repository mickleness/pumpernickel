/*
 * @(#)SummonMenuItem.java
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
package com.pump.window;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JCheckBoxMenuItem;

/** This menu item calls <code>Frame.toFront()</code> when
 * the item is selected.
 * 
 */
public class SummonMenuItem extends JCheckBoxMenuItem {

	private static final long serialVersionUID = 1L;

	Frame frame;
	
	ActionListener actionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			frame.toFront();
			if(frame.getExtendedState()==Frame.ICONIFIED)
				frame.setExtendedState(Frame.NORMAL);
			setSelected(true);
		}
	};
	
	/** Create a new <code>SummonMenuItem</code>.
	 * 
	 * @param f the frame to bring to front when this menu item is activated
	 */
	public SummonMenuItem(Frame f) {
		super();
		frame = f;
		addActionListener(actionListener);
		updateText();
		
		frame.addPropertyChangeListener("title",new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				updateText();
			}
		});
		
		//this UI is buggy, and has issues.
		//the main issue is that it won't even show up on Macs
		//if you use the screen menubar, and since the goal
		//is to emulate macs: why bother?
		//if(frame instanceof JFrame)
		//	setUI(new FrameMenuItemUI((JFrame)frame));
	}
	
	private void updateText() {
		String text = frame.getTitle();
		if(text==null || text.trim().length()==0)
			text = "Untitled";
		setText(text);
	}
}
