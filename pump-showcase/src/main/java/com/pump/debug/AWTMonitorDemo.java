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
package com.pump.debug;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.pump.blog.Blurb;
import com.pump.swing.BasicConsole;

/**
 * This is a simple app demoing the {@link AWTMonitor}
 * 
 */
@Blurb (
filename = "AWTMonitor",
title = "Event Dispatch Thread: Responding to Deadlocks",
releaseDate = "June 2008",
summary = "What can you do when your event dispatch thread is blocked?\n"+
"<P>For starters: this article mentions how to automatically detect this situation and get great console output to help pinpoint the problem. "+
"Also this delves into a very murky partial solution.  (It has worked multiple times for me, but it uses unsafe deprecated black magic.)",
link = "http://javagraphics.blogspot.com/2008/06/event-dispatch-thread-responding-to.html",
sandboxDemo = false
)
public class AWTMonitorDemo extends JPanel {
	
	JButton lock = new JButton("Lock For 5 Seconds");
	BasicConsole console = new BasicConsole(false, false);
	JTextArea description = new JTextArea("The AWTMonitor class listends to the event dispatch thread (EDT) and notices when it has been unresponsive for several seconds. When this condition is detected, it can do two things:\n\n1. Dump all active threads to the console. If you can get your hands on the console output (hopefully it's in a log file somewhere), then you can use this information to debug the situation. The probably is probably that a task is taking too long (so it should either be optimized or moved to another thread), or that you have a deadlock that will never recover.\n\n2. There is a separate optional \"panic listener\", which after an even longer period of time is triggered. This can do any number of things, such as autosave your document (if possible), kill the EDT (which is dangerous, but what do you have to lose at this point?), or launch another process to present the user with options.\n\nThis demo only shows option #1. By locking the EDT for 5 seconds, the console below should print stack traces of all threads. (It will only become visible when the EDT becomes responsive again, though.)");

	public AWTMonitorDemo() {

		lock.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				long start = System.currentTimeMillis();
				while(System.currentTimeMillis()-start<5000) {
					 try {
						 Thread.sleep(100);
					 } catch(Exception e2) {
						 Thread.yield();
					 }
				}
			}
		});
		
		//don't show any output from the app starting up,
		//let the user click the "Lock" button to add output so there's
		//more of a cause-and-effect:
		
		console.addHierarchyListener(new HierarchyListener() {
			
			@Override
			public void hierarchyChanged(HierarchyEvent e) {
				if(console.isShowing()) {
					console.setText("");
					console.removeHierarchyListener(this);
				}
			}
			
		});
		
		description.setLineWrap(true);
		description.setWrapStyleWord(true);
		description.setEditable(false);
		description.setOpaque(false);
		
		AWTMonitor.createAWTMonitorThread("Showcase App", 2000, 50000, "AWT Monitor Demo", console.createPrintStream(false)).start();
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0; c.weightx = 1; c.weighty = 0;
		c.fill = GridBagConstraints.BOTH;
		add(description, c);
		c.gridy++; c.fill = GridBagConstraints.NONE;
		add(lock, c);
		c.weighty = 1;
		c.gridy++; c.fill = GridBagConstraints.BOTH;	
		add(new JScrollPane(console), c);
	}
}