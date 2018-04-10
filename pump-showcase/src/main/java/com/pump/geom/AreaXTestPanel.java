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

			JComponent tab = (JComponent) tabs.getSelectedComponent();
			TestElement test = (TestElement) tab
					.getClientProperty(KEY_TEST_ELEMENT);
			if (test != lastTest) {
				if (lastTest != null) {
					lastTest.cancel();
				}
				lastTest = test;
			}
		}

	};

	BasicConsole console = new BasicConsole(false, true);
	JScrollPane consoleScrollPane = new JScrollPane(console);

	public AreaXTestPanel() {
		MathG.acos(0); // jog this into existence so it doesn't affect profiles

		addTab(new AccuracyTest(console.createPrintStream(false)), "Accuracy");
		addTab(new TransformTest(console.createPrintStream(false)), "Transform");
		// addTab(new IntersectionsTest(console.createPrintStream(false)),
		// "Intersections");
		addTab(new RelationshipTest(console.createPrintStream(false)),
				"Relationship");
		addTab(new AddRulesTest(console.createPrintStream(false)), "Rules");

		tabs.addChangeListener(tabChangeListener);
		// set up the "lastTest" correctly
		tabChangeListener.stateChanged(null);

		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 0;
		c.insets = new Insets(3, 3, 3, 3);
		c.fill = GridBagConstraints.HORIZONTAL;
		add(tabs, c);
		c.gridy++;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		add(consoleScrollPane, c);
	}

	private static final String KEY_TEST_ELEMENT = AreaXTestPanel.class
			.getName() + "#testElement";

	private void addTab(TestElement e, String tabName) {
		e.getComponent().putClientProperty(KEY_TEST_ELEMENT, e);
		e.getComponent().setOpaque(false);
		tabs.add(e.getComponent(), tabName);
	}
}