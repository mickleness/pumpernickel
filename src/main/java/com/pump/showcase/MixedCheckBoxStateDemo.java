package com.pump.showcase;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.net.URL;

import javax.swing.JCheckBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.plaf.button.MixedCheckBoxState;

public class MixedCheckBoxStateDemo extends ShowcaseExampleDemo {

	JCheckBox allCheckBox = new JCheckBox("All condiments");
	JCheckBox lettuceCheckBox = new JCheckBox("Lettuce");
	JCheckBox tomatoCheckBox = new JCheckBox("Tomato", true);
	JCheckBox mustardCheckBox = new JCheckBox("Mustard", true);

	ChangeListener allListener = new ChangeListener() {

		@Override
		public void stateChanged(ChangeEvent e) {
			lettuceCheckBox.removeChangeListener(individualListener);
			tomatoCheckBox.removeChangeListener(individualListener);
			mustardCheckBox.removeChangeListener(individualListener);
			try {
				if (allCheckBox.isSelected()) {
					lettuceCheckBox.setSelected(true);
					tomatoCheckBox.setSelected(true);
					mustardCheckBox.setSelected(true);
				} else {
					lettuceCheckBox.setSelected(false);
					tomatoCheckBox.setSelected(false);
					mustardCheckBox.setSelected(false);
					MixedCheckBoxState.setMixed(allCheckBox, false);
				}
			} finally {
				lettuceCheckBox.addChangeListener(individualListener);
				tomatoCheckBox.addChangeListener(individualListener);
				mustardCheckBox.addChangeListener(individualListener);
			}
		}
	};

	ChangeListener individualListener = new ChangeListener() {

		@Override
		public void stateChanged(ChangeEvent e) {
			allCheckBox.removeChangeListener(allListener);
			try {
				int selected = 0;
				if (lettuceCheckBox.isSelected())
					selected++;
				if (tomatoCheckBox.isSelected())
					selected++;
				if (mustardCheckBox.isSelected())
					selected++;
				if (selected == 3) {
					allCheckBox.setSelected(true);
				} else {
					allCheckBox.setSelected(false);
					MixedCheckBoxState.setMixed(allCheckBox, selected > 0);
				}
			} finally {
				allCheckBox.addChangeListener(allListener);
			}
		}

	};

	public MixedCheckBoxStateDemo() {

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

		lettuceCheckBox.addChangeListener(individualListener);
		tomatoCheckBox.addChangeListener(individualListener);
		mustardCheckBox.addChangeListener(individualListener);

		individualListener.stateChanged(null);
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
