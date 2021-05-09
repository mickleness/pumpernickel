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
package com.pump.plaf.button;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Objects;

import javax.swing.JCheckBox;

/**
 * This synchronizes one master control button with several subordinate buttons.
 * If all of the subordinate buttons are selected: the master button is
 * selected. If none of the subordinate buttons are selected: the master button
 * is unselected. Otherwise the master button renders in a mixed state to
 * indicate that the group is only partially selected.
 * <p>
 * If you interact with the master button you can toggle all subordinate buttons
 * on/off.
 */
public class MixedCheckBoxButtonGroup {
	JCheckBox masterButton;
	JCheckBox[] subordinateButtons;
	int adjusting = 0;

	ItemListener masterButtonListener = new ItemListener() {

		@Override
		public void itemStateChanged(ItemEvent e) {
			if (adjusting > 0)
				return;
			adjusting++;
			try {
				MixedCheckBoxState.setMixed(masterButton, false);
				for (JCheckBox subordinateButton : subordinateButtons) {
					subordinateButton.setSelected(masterButton.isSelected());
				}
			} finally {
				adjusting--;
			}
		}

	};

	ItemListener subordinateButtonListener = new ItemListener() {

		@Override
		public void itemStateChanged(ItemEvent e) {
			if (adjusting > 0)
				return;
			adjusting++;
			try {
				boolean anySelected = false;
				boolean anyUnselected = false;
				for (JCheckBox b : subordinateButtons) {
					if (b.isVisible() && b.isSelected())
						anySelected = true;
					if (b.isVisible() && !b.isSelected())
						anyUnselected = true;
				}

				if (!anyUnselected) {
					MixedCheckBoxState.setMixed(masterButton, false);
					masterButton.setSelected(true);
				} else if (anySelected) {
					MixedCheckBoxState.setMixed(masterButton, true);
					masterButton.setSelected(false);
				} else {
					MixedCheckBoxState.setMixed(masterButton, false);
					masterButton.setSelected(false);
				}
			} finally {
				adjusting--;
			}
		}

	};

	public MixedCheckBoxButtonGroup(JCheckBox masterButton,
			JCheckBox... subordinateButtons) {
		Objects.requireNonNull(masterButton);
		Objects.requireNonNull(subordinateButtons);
		this.masterButton = masterButton;
		this.subordinateButtons = subordinateButtons;
		masterButton.getModel().addItemListener(masterButtonListener);

		for (JCheckBox subordinateButton : subordinateButtons) {
			subordinateButton.getModel()
					.addItemListener(subordinateButtonListener);
		}

		subordinateButtonListener.itemStateChanged(null);
	}
}