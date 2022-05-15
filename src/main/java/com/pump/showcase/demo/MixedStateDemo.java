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
package com.pump.showcase.demo;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.inspector.Inspector;
import com.pump.plaf.button.mixed.MixedState;

public class MixedStateDemo extends ShowcaseExampleDemo {
	private static final long serialVersionUID = 1L;

	JComboBox<String> stateComboBox = new JComboBox<String>(
			new String[] { MixedState.UNSELECTED.name(), MixedState.SELECTED.name(), MixedState.MIXED.name() });
	JRadioButton selectedButton = new JRadioButton("True");
	JRadioButton unselectedButton = new JRadioButton("False");

	JCheckBox sampleCheckBox = new JCheckBox("Sample Checkbox");

	ActionListener actionListener = new ActionListener() {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == stateComboBox) {
				int i = stateComboBox.getSelectedIndex();
				MixedState state = MixedState.values()[i];
				MixedState.set(sampleCheckBox, state);
			} else if (e.getSource() == selectedButton) {
				sampleCheckBox.setSelected(true);
			} else if (e.getSource() == unselectedButton) {
				sampleCheckBox.setSelected(false);
			}
		}
	};

	public MixedStateDemo() {
		sampleCheckBox.setOpaque(false);

		Inspector inspector = new Inspector(configurationPanel);
		inspector.addRow(new JLabel("MixedState:"), stateComboBox);
		inspector.addRow(new JLabel("Selected:"), unselectedButton, selectedButton);

		examplePanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.insets = new Insets(2, 2, 2, 2);

		c.fill = GridBagConstraints.HORIZONTAL;
		examplePanel.add(sampleCheckBox, c);

		refreshConfigControls();

		sampleCheckBox.addPropertyChangeListener(MixedState.PROPERTY_MIXED_STATE, new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				refreshConfigControls();
			}
		});
		sampleCheckBox.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				refreshConfigControls();
			}
		});
	}

	private void refreshConfigControls() {
		stateComboBox.removeActionListener(actionListener);
		selectedButton.removeActionListener(actionListener);
		unselectedButton.removeActionListener(actionListener);

		MixedState state = MixedState.get(sampleCheckBox);
		stateComboBox.setSelectedIndex(state.ordinal());
		selectedButton.setSelected(sampleCheckBox.isSelected());
		unselectedButton.setSelected(!sampleCheckBox.isSelected());

		stateComboBox.addActionListener(actionListener);
		selectedButton.addActionListener(actionListener);
		unselectedButton.addActionListener(actionListener);
	}

	@Override
	public String getTitle() {
		return "MixedState Demo";
	}

	@Override
	public String getSummary() {
		return "This demonstrates the MixedState's ability to render a JCheckBox in a mixed state.";
	}

	@Override
	public URL getHelpURL() {
		return getClass().getResource("mixedStateDemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "mixed state", "checkbox", "ui", "ux", "indeterminate" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { MixedState.class };
	}

}