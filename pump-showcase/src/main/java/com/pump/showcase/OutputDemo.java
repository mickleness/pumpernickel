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
package com.pump.showcase;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.pump.swing.BasicConsole;
import com.pump.swing.JThrobber;

public abstract class OutputDemo extends JPanel implements Runnable {
	private static final long serialVersionUID = 1L;

	protected JButton runButton;
	protected JThrobber throbber = new JThrobber();
	protected BasicConsole console;
	
	public OutputDemo(String runButtonText,boolean resizeConsoleTabSpacing) {
		runButton = new JButton(runButtonText);
		console = new BasicConsole(false, resizeConsoleTabSpacing);
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0; c.weightx = 1; c.weighty = 0;
		c.insets = new Insets(3,3,3,3);
		c.anchor = GridBagConstraints.WEST;
		add(runButton, c);
		c.anchor = GridBagConstraints.EAST;
		c.gridx++;
		add(throbber, c);
		c.gridy++; c.gridx = 0; c.weighty = 1; c.gridwidth = GridBagConstraints.REMAINDER;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(3,0,0,0);
		add(new JScrollPane(console), c);
		
		throbber.setVisible(false);
		
		runButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				runButton.setEnabled(false);
				throbber.setVisible(true);
				Thread thread = new Thread() {
					public void run() {
						try {
							OutputDemo.this.run();
						} finally {
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									runButton.setEnabled(true);
									throbber.setVisible(false);
								}
							});
						}
					}
				};
				thread.start();
			}
		});
		
		addHierarchyListener(new HierarchyListener() {
			boolean wasShowing = false;
			boolean needToRun = true;

			@Override
			public void hierarchyChanged(HierarchyEvent e) {
				boolean isShowing = isShowing();
				if(isShowing && (!wasShowing) && needToRun) {
					needToRun = false;
					runButton.doClick();
				}
				wasShowing = isShowing;
			}
			
		});
	}
}