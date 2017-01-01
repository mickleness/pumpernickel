/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://raw.githubusercontent.com/mickleness/pumpernickel/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
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