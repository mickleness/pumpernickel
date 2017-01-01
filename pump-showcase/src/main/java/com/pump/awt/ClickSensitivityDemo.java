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
package com.pump.awt;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.blog.Blurb;
import com.pump.swing.BasicConsole;

/** A simple demo program showing off the {@link ClickSensitivityControl}. 
 *
 **/
@Blurb (
filename = "ClickEventEnabler",
title = "Mouse Click Events: Adding Wiggle Room",
releaseDate = "February 2011",
summary = "This triggers mouseClicked events even if the mouse moves a few pixels between click and release.",
instructions = "This applet demonstrates how the <code>ClickEventEnabler.html</code> redefines "+
"mouse click events to allow for a little bit of mouse movement.\n"+
"<p>When you click a label: if a MouseEvent.MOUSE_CLICKED event was received then it will "+
"pulse with a color. So if you click either label and don't move the mouse at all: they will pulse.\n"+
"<p>However if you press the mouse button, move the cursor a pixel or two, and then release: only "+
"the second label will pulse.\n"+
"<p>The tolerance spinner controls the number of pixels you can move the mouse and still receive "+
"a <code>mouseClicked()</code> notification.",
link = "http://javagraphics.blogspot.com/2011/02/mouse-click-events-adding-wiggle-room.html",
sandboxDemo = true
)
public class ClickSensitivityDemo extends JPanel {
	private static final long serialVersionUID = 1L;

	
	MouseListener mouseListener = new MouseAdapter() {
		@Override
		public void mouseClicked(MouseEvent e) {
			console.createPrintStream(new Color(0x220000ff, true)).println(e);
		}
		@Override
		public void mousePressed(MouseEvent e) {
			console.createPrintStream(false).println(e);
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			console.createPrintStream(false).println(e);
		}
	};
	
	JSpinner spinner = new JSpinner(new SpinnerNumberModel( ClickSensitivityControl.CLICK_EVENT_TOLERANCE, 0, 20, 1 ));
	
	JComponent target1 = new TextBlock("Click Me (original)").createComponent();
	JComponent target2 = new TextBlock("Click Me (improved sensitivity)").createComponent();
	
	JTextArea description = new JTextArea("By default MouseEvent.MOUSE_CLICKED events are only triggered when the mouse is pressed and released at the same point.\n\nThe ClickSensitivityControl class allows a few pixels of tolerance for movement.\n\nIn this example, press the mouse on each component below and move the mouse a few pixels before releasing the mouse button. The console text below shows when MOUSE_PRESSED, MOUSE_RELEASED, and MOUSE_CLICKED events are issued.\n\nIn this example the sensitivity is applied to one label and not the other, but there is a static method to turn this on for all components in a runtime.\n\n(It's also worth noting that buttons have their own complex model for triggering ActionListeners which is NOT affected by this change.)");
	BasicConsole console = new BasicConsole(false,false);
	JScrollPane scrollPane = new JScrollPane(console);
	
	public ClickSensitivityDemo() {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridy++; c.gridx = 0; c.weightx = 1; c.weighty = 0; c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(6,6,6,6);
		c.gridwidth = GridBagConstraints.REMAINDER;
		add(description, c);

		c.gridx = 0; c.gridy++; c.weightx = 1; c.weighty = 0;
		c.gridy++; c.gridx = 0;
		JPanel targetRow = new JPanel(new FlowLayout());
		add(targetRow, c);
		
		targetRow.add(target1);
		targetRow.add(target2);
		
		c.gridy++; c.gridwidth = 1;
		c.anchor = GridBagConstraints.EAST;
		c.gridx = 0; c.fill = GridBagConstraints.NONE;
		add(new JLabel("Tolerance:"), c);
		c.gridx++; 
		c.anchor = GridBagConstraints.WEST;
		add(spinner, c);
		
		c.gridx = 0;
		c.gridy++; c.weighty = 1; c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.BOTH;
		add(scrollPane, c);
		
		description.setWrapStyleWord(true);
		description.setOpaque(false);
		description.setEditable(false);
		description.setLineWrap(true);
		
		//Note: normally you should not try to create a ClickMonitor. Instead
		//call ClickSensitivityControl.install() to globally install this change for 
		//ALL components. Here we only modify 1 component just so the user can
		//contrast two side-by-side components that will behave differently.
		ClickSensitivityControl.ClickMonitor fixer = new ClickSensitivityControl.ClickMonitor();
		
		target2.addMouseListener(fixer);
		target2.addMouseMotionListener(fixer);
		
		target1.addMouseListener(mouseListener);
		target2.addMouseListener(mouseListener);
		
		spinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				refreshTolerance();
			}
			
		});
		
		refreshTolerance();
	}
	
	private void refreshTolerance() {
		ClickSensitivityControl.CLICK_EVENT_TOLERANCE = ((Number)spinner.getValue()).doubleValue();
	}
}