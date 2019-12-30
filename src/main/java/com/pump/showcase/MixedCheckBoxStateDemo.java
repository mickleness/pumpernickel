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
import java.net.URL;

import javax.swing.JCheckBox;

import com.pump.plaf.button.MixedCheckBoxButtonGroup;
import com.pump.plaf.button.MixedCheckBoxState;

public class MixedCheckBoxStateDemo extends ShowcaseExampleDemo {
	private static final long serialVersionUID = 1L;
	
	
	JCheckBox allCheckBox = new JCheckBox("All condiments");
	JCheckBox lettuceCheckBox = new JCheckBox("Lettuce");
	JCheckBox tomatoCheckBox = new JCheckBox("Tomato", true);
	JCheckBox mustardCheckBox = new JCheckBox("Mustard", true);

	public MixedCheckBoxStateDemo() {
		
		lettuceCheckBox.setOpaque(false);
		tomatoCheckBox.setOpaque(false);
		mustardCheckBox.setOpaque(false);
		allCheckBox.setOpaque(false);

		configurationPanel.setVisible(false);
		configurationLabel.setVisible(false);

		examplePanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1;
		c.weighty = 1;
		c.insets = new Insets(2, 2, 2, 2);
		c.fill = GridBagConstraints.HORIZONTAL;
		examplePanel.add(allCheckBox, c);
		c.insets = new Insets(2, 20, 2, 2);
		c.gridy++;
		examplePanel.add(lettuceCheckBox, c);
		c.gridy++;
		examplePanel.add(tomatoCheckBox, c);
		c.gridy++;
		examplePanel.add(mustardCheckBox, c);

		new MixedCheckBoxButtonGroup(allCheckBox, lettuceCheckBox, tomatoCheckBox, mustardCheckBox);
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