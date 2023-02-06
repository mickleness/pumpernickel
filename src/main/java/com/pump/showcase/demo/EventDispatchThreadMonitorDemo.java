/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.showcase.demo;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import com.pump.inspector.Inspector;
import com.pump.inspector.InspectorRow;
import com.pump.swing.BasicConsole;
import com.pump.thread.EventDispatchThreadMonitor;
import com.pump.thread.EventDispatchThreadMonitor.Listener;
import com.pump.thread.ThreadProfiler;

/**
 * This is a simple app demoing the {@link EventDispatchThreadMonitor}
 * 
 */
public class EventDispatchThreadMonitorDemo extends ShowcaseExampleDemo {
	private static final long serialVersionUID = 1L;

	static BasicConsole console = new BasicConsole(false, false);

	enum Behavior implements EventDispatchThreadMonitor.Listener {
		BEHAVIOR_DO_NOTHING("Do nothing") {
			@Override
			public void becameUnresponsive(Thread eventDispatchThread,
					long unresponsiveMillis, long lastSuccessfulPingMillis) {
				// intentionally empty
			}

			@Override
			public void becameResponsive(Thread eventDispatchThread,
					long lastSuccessfulPingMillis, long unresponsiveMillis) {
				// intentionally empty
			}
		},
		BEHAVIOR_PROFILER("Activate ThreadProfiler") {
			ThreadProfiler profiler = new ThreadProfiler();

			@Override
			public void becameUnresponsive(Thread eventDispatchThread,
					long unresponsiveMillis, long lastSuccessfulPingMillis) {
				profiler.setActive(true);
			}

			@Override
			public void becameResponsive(Thread eventDispatchThread,
					long lastSuccessfulPingMillis, long unresponsiveMillis) {
				profiler.setActive(false);
				String output = profiler.getOutput();
				try (PrintStream ps = console.createPrintStream(false)) {
					ps.println(
							"\nThis is ThreadProfiler output from this period:\n\n"
									+ output);
				}

			}
		},
		BEHAVIOR_INTERRUPT("Call Thread.interrupt()") {

			@Override
			public void becameUnresponsive(Thread eventDispatchThread,
					long unresponsiveMillis, long lastSuccessfulPingMillis) {
				eventDispatchThread.interrupt();
			}

			@Override
			public void becameResponsive(Thread eventDispatchThread,
					long lastSuccessfulPingMillis, long unresponsiveMillis) {
				// intentionally empty

			}
		},
		BEHAVIOR_STOP("Call Thread.stop()") {

			@Override
			public void becameUnresponsive(Thread eventDispatchThread,
					long unresponsiveMillis, long lastSuccessfulPingMillis) {
				eventDispatchThread.stop();
			}

			@Override
			public void becameResponsive(Thread eventDispatchThread,
					long lastSuccessfulPingMillis, long unresponsiveMillis) {
				// intentionally empty
			}
		};

		String name;

		Behavior(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}

		@Override
		public abstract void becameUnresponsive(Thread eventDispatchThread,
				long unresponsiveMillis, long lastSuccessfulPingMillis);

		@Override
		public abstract void becameResponsive(Thread eventDispatchThread,
				long lastSuccessfulPingMillis, long unresponsiveMillis);
	}

	JButton lockButton = new JButton("Lock for 5 seconds");

	JComboBox<Behavior> behaviorComboBox = new JComboBox<>(Behavior.values());

	Thread blockingThread = null;
	boolean stopThread = false;

	public EventDispatchThreadMonitorDemo() {
		super(true, true, false);

		Inspector layout = new Inspector(configurationPanel);
		layout.addRow(new JLabel("Behavior:"), behaviorComboBox);
		layout.addRow(new InspectorRow(null, lockButton, false, 1));

		examplePanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		examplePanel.add(new JScrollPane(console), c);

		lockButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread countDownThread = new Thread() {
					@Override
					public void run() {
						try {
							SwingUtilities.invokeLater(new Runnable() {

								@Override
								public void run() {
									println("\n------ STARTING\n\nThis app will temporarily become unresponsive in 3...");
								}
							});
							Thread.sleep(1000);
							SwingUtilities.invokeLater(new Runnable() {

								@Override
								public void run() {
									println("2...");
								}
							});
							Thread.sleep(1000);
							SwingUtilities.invokeLater(new Runnable() {

								@Override
								public void run() {
									println("1...");
								}

							});
							Thread.sleep(1000);

							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									runDemo();
								}
							});

							SwingUtilities.invokeLater(new Runnable() {

								@Override
								public void run() {
									lockButton.setEnabled(true);
								}

							});
						} catch (Throwable t) {
							t.printStackTrace();
						}
					}
				};
				countDownThread.start();
				lockButton.setEnabled(false);
			}
		});
	}

	private void println(String string) {
		try (PrintStream ps = console.createPrintStream(false)) {
			ps.println(string);
		}
	}

	private void runDemo() {
		Map<Long, Collection<Listener>> origListeners = EventDispatchThreadMonitor
				.get().removeAllListeners();
		try (PrintStream ps = console.createPrintStream(false)) {
			ps.println();
			Behavior b = (Behavior) behaviorComboBox.getSelectedItem();
			ps.println("Behavior: " + b.name);
			EventDispatchThreadMonitor.get().addListener(1000, b);

			long startTime = System.currentTimeMillis();
			try {
				while (true) {
					long elapsed = System.currentTimeMillis() - startTime;
					if (elapsed > 5000)
						return;
					for (int i = 0; i < 10_000_000; i++) {
						Math.random();
						if (Thread.interrupted())
							throw new InterruptedException();
					}
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} finally {
				double elapsedMillis = System.currentTimeMillis() - startTime;
				double ellapsedSeconds = elapsedMillis / 1000.0;
				ps.println("The event dispatch thread was blocked for about "
						+ new DecimalFormat("#.#").format(ellapsedSeconds)
						+ " seconds.");
			}
		} finally {
			for (Map.Entry<Long, Collection<Listener>> entry : origListeners
					.entrySet()) {
				for (Listener l : entry.getValue()) {
					EventDispatchThreadMonitor.get().addListener(entry.getKey(),
							l);
				}
			}
		}
	}

	@Override
	public String getTitle() {
		return "EventDispatchThreadMonitor Demo";
	}

	@Override
	public String getSummary() {
		return "This demonstrates a class to respond to an unresponsive event dispatch thread.";
	}

	@Override
	public URL getHelpURL() {
		return EventDispatchThreadMonitorDemo.class
				.getResource("eventDispatchThreadMonitorDemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "lock", "deadlock", "freeze", "hang", "UI", "ux",
				"edt", "thread", "synchronization", "log" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { EventDispatchThreadMonitor.class,
				ThreadProfiler.class };
	}
}