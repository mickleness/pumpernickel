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

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.io.PrintStream;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JScrollPane;
import javax.swing.RootPaneContainer;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import com.pump.debug.AWTMonitor;
import com.pump.debug.AWTPanicListener;
import com.pump.swing.BasicConsole;
import com.pump.swing.DialogFooter;
import com.pump.swing.DialogFooter.EscapeKeyBehavior;
import com.pump.swing.JFancyBox;
import com.pump.swing.JThrobber;
import com.pump.swing.QDialog;

/**
 * This is a simple app demoing the {@link AWTMonitor}
 * 
 */
public class AWTMonitorDemo extends ShowcaseDemo {
	private static final long serialVersionUID = 1L;

	JButton lockButton = new JButton("Lock for 5 seconds");
	BasicConsole console = new BasicConsole(false, false);
	JCheckBox stopThreadCheckBox = new JCheckBox("Stop EDT When Locked");
	Thread blockingThread = null;
	boolean stopThread = false;

	public AWTMonitorDemo() {

		lockButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				try (PrintStream ps = console.createPrintStream(false)) {
					ps.println();
					ps.println("--------------------------------");
					ps.println("I'm about to block the event dispatch thread for 5 seconds.");
					ps.println("You won't be able to interact with this application during this time...");
					ps.println();
				}
				RootPaneContainer rpc = (RootPaneContainer) SwingUtilities
						.getWindowAncestor(lockButton);
				JThrobber throbber = new JThrobber();
				throbber.setPreferredSize(new Dimension(30, 30));
				final JFancyBox box = new JFancyBox(rpc, throbber);
				box.setCloseable(false);
				box.setVisible(true);

				Timer timer = new Timer(1000, new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						long start = System.currentTimeMillis();
						blockingThread = Thread.currentThread();
						try {
							synchronized (lockButton.getTreeLock()) {
								while (System.currentTimeMillis() - start < 5000) {
									try {
										Thread.sleep(100);
									} catch (Exception e2) {
										Thread.yield();
									}
								}
							}
							try (PrintStream ps = console
									.createPrintStream(false)) {
								ps.println("The thread that was blocking exited normally. (It wasn't aborted.)");
							}
						} catch (ThreadDeath td) {
							try (PrintStream ps = console
									.createPrintStream(false)) {
								ps.println("The thread that was blocking was forcibly stopped.");
							}
						} finally {
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									Window w = SwingUtilities
											.getWindowAncestor(box);
									box.getParent().remove(box);
									w.repaint();
								}
							});
							blockingThread = null;
						}
					}
				});
				timer.setRepeats(false);
				timer.start();
			}
		});
		stopThreadCheckBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				stopThread = stopThreadCheckBox.isSelected();
				Frame frame = (Frame) SwingUtilities
						.getWindowAncestor(stopThreadCheckBox);
				if (stopThread)
					QDialog.showDialog(
							frame,
							"Warning",
							QDialog.WARNING_MESSAGE,
							"This may lock the application.",
							"When this checkbox is selected and you click \""
									+ lockButton.getText()
									+ "\" this application may become unusable. If you can't interact with the application after 10 seconds, you should force quit.",
							null, null, DialogFooter.OK_OPTION,
							DialogFooter.OK_OPTION, null, null,
							EscapeKeyBehavior.TRIGGERS_DEFAULT);
			}

		});

		// initialize AWTMonitor once the user opened this panel
		// so we aren't triggered by, say, a slow launch.
		addHierarchyListener(new HierarchyListener() {
			boolean wasShowing = false;

			@Override
			public void hierarchyChanged(HierarchyEvent e) {
				if (isShowing() && !wasShowing) {
					wasShowing = true;
					initialize();
				}
			}

		});

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		c.insets = new Insets(5, 5, 5, 5);
		add(lockButton, c);
		c.gridy++;
		add(stopThreadCheckBox, c);
		c.weighty = 1;
		c.gridy++;
		c.fill = GridBagConstraints.BOTH;
		c.gridy++;
		add(new JScrollPane(console), c);
	}

	private void initialize() {

		AWTMonitor.createAWTMonitorThread("Showcase App", 2000, 4000,
				"AWT Monitor Demo", console.createPrintStream(false)).start();

		AWTMonitor.addPanicListener(new AWTPanicListener() {

			@Override
			public void AWTPanic(String applicationName) {
				if (blockingThread != null && stopThread) {
					try (PrintStream ps = console.createPrintStream(false)) {
						ps.println("Calling Thread.stop on the event dispatch thread...");
						ps.println("(If you see this message, then everything probably worked!");
					}
					blockingThread.stop();
					blockingThread = null;
					try {
						Thread.sleep(500);
						// put something in the event queue to jolt
						// it back to life
						AWTMonitorDemo.this.repaint();
					} catch (Exception e) {
					}
				}
			}

		});
	}

	@Override
	public String getTitle() {
		return "AWTMonitor Demo";
	}

	@Override
	public String getSummary() {
		return "This demonstrates a class to log or respond to a locked event dispatch thread.";
	}

	@Override
	public URL getHelpURL() {
		return AWTMonitorDemo.class.getResource("awtMonitorDemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "lock", "deadlock", "freeze", "hang", "UI", "ux",
				"edt", "thread", "synchronization", "log" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { AWTMonitor.class, AWTPanicListener.class };
	}
}