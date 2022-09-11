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

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultCaret;

import com.pump.inspector.Inspector;
import com.pump.thread.ThreadProfiler;

public class ThreadProfilerDemo extends ShowcaseExampleDemo {

	private static final long serialVersionUID = 1L;
	JRadioButton onButton = new JRadioButton("Active", false);
	JRadioButton offButton = new JRadioButton("Inactive", true);
	ThreadProfiler profiler = new ThreadProfiler();
	JTextArea textArea = new JTextArea();
	JScrollPane scrollPane = new JScrollPane(textArea);
	JSpinner pollIntervalSpinner = new JSpinner(
			new SpinnerNumberModel(50, 50, 5000, 50));
	JSpinner refreshUIIntervalSpinner = new JSpinner(
			new SpinnerNumberModel(500, 50, 5000, 50));

	ChangeListener profilerListener = new ChangeListener() {

		@Override
		public void stateChanged(ChangeEvent e) {
			final String output = profiler.getOutput();
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					scrollPane.getViewport().setViewPosition(new Point(0, 0));
					textArea.setText(output);
				}
			});
		}
	};

	public ThreadProfilerDemo() {
		super(true, true, true, false, false);
		Inspector inspector = new Inspector(configurationPanel);
		inspector.addRow(new JLabel("State:"), onButton, offButton);
		inspector.addRow(new JLabel("Poll Interval:"), pollIntervalSpinner);
		inspector.addRow(new JLabel("Refresh UI Interval:"),
				refreshUIIntervalSpinner);

		onButton.setToolTipText("Reset the profiler and collection samples.");
		offButton.setToolTipText(
				"Stop the profiler and stop collecting samples.");
		refreshUIIntervalSpinner.setToolTipText(
				"The number of milliseconds before this UI refreshes with profiler information.");
		pollIntervalSpinner.setToolTipText(
				"The number of milliseconds between profiler samples.");

		ButtonGroup bg = new ButtonGroup();
		bg.add(onButton);
		bg.add(offButton);

		ActionListener buttonListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (onButton.isSelected()) {
					profiler.reset();
					profiler.setActive(true);
				} else {
					profiler.setActive(false);
				}
			}

		};

		onButton.addActionListener(buttonListener);
		offButton.addActionListener(buttonListener);

		textArea.setAutoscrolls(false);
		textArea.setFont(
				new Font("Monospaced", 0, textArea.getFont().getSize()));

		DefaultCaret caret = (DefaultCaret) textArea.getCaret();
		caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);

		textArea.setEditable(false);
		examplePanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		examplePanel.add(scrollPane, c);

		pollIntervalSpinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				refreshPollInterval();
			}
		});
		refreshPollInterval();

		refreshUIIntervalSpinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				refreshRefreshUIIntervalListener();
			}
		});
		refreshRefreshUIIntervalListener();
	}

	private void refreshRefreshUIIntervalListener() {
		int millis = ((Integer) refreshUIIntervalSpinner.getValue());
		profiler.removeDataListener(profilerListener);
		profiler.addDataListener(millis, profilerListener);
	}

	private void refreshPollInterval() {
		int millis = ((Integer) pollIntervalSpinner.getValue());
		profiler.setSampleInterval(millis);
	}

	@Override
	public String getTitle() {
		return "ThreadProfiler Demo";
	}

	@Override
	public String getSummary() {
		return "This demonstrates a minimal pure-Java thread profiler tool.";
	}

	@Override
	public URL getHelpURL() {
		return getClass().getResource("threadProfilerDemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "thread", "debug", "performance", "time" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { ThreadProfiler.class };
	}

}