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
import java.net.URL;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.plaf.basic.BasicCheckBoxUI;

import com.pump.inspector.Inspector;
import com.pump.plaf.button.MixedCheckBoxState;
import com.pump.plaf.button.mixed.MixedState;

public class MixedCheckBoxStateDemo extends ShowcaseExampleDemo {
	private static final long serialVersionUID = 1L;

	JComboBox<String> stateComboBox = new JComboBox<String>(new String[] {
			MixedState.UNSELECTED.name(),
			MixedState.SELECTED.name(),
			MixedState.MIXED.name() }
			);
	
	JCheckBox condimentsCheckBox = new JCheckBox("All condiments");
	JCheckBox lettuceCheckBox = new JCheckBox("Lettuce");
	JCheckBox tomatoCheckBox = new JCheckBox("Tomato", true);
	JCheckBox mustardCheckBox = new JCheckBox("Mustard", true);

	public MixedCheckBoxStateDemo() {

		lettuceCheckBox.setOpaque(false);
		tomatoCheckBox.setOpaque(false);
		mustardCheckBox.setOpaque(false);
		condimentsCheckBox.setOpaque(false);


		Inspector inspector = new Inspector(configurationPanel);
		inspector.addRow(new JLabel("State:"), stateComboBox);
		
		stateComboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				int i = stateComboBox.getSelectedIndex();
				MixedState state = MixedState.values()[i];
				MixedState.setState(condimentsCheckBox, state);
			}});

		examplePanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.insets = new Insets(2, 2, 2, 2);
		
		
		c.fill = GridBagConstraints.HORIZONTAL;
		examplePanel.add(condimentsCheckBox, c);
		c.insets = new Insets(2, 20, 2, 2);
		c.gridy++;
		examplePanel.add(lettuceCheckBox, c);
		c.gridy++;
		examplePanel.add(tomatoCheckBox, c);
		c.gridy++;
		examplePanel.add(mustardCheckBox, c);

		
		/////
		condimentsCheckBox.setUI(new BasicCheckBoxUI());
		
//		new MixedCheckBoxButtonGroup(allCheckBox, lettuceCheckBox,
//				tomatoCheckBox, mustardCheckBox);
	}

	@Override
	public String getTitle() {
		return "MixedCheckBoxState Demo";
	}

	@Override
	public String getSummary() {
		return "This demonstrates the MixedCheckBoxState's ability to render a JCheckBox in a mixed state.";
	}

	@Override
	public URL getHelpURL() {
		return getClass().getResource("mixedCheckBoxDemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "mixed state", "checkbox", "ui", "ux" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { MixedCheckBoxState.class };
	}

}