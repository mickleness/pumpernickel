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
package com.pump.plaf.button.mixed;

import javax.swing.JCheckBox;

/**
 * This installs an Aqua-based mixed state UI. To render the mixed state this sets the
 * JCheckBox to selected and turns on a special client property the Aqua L&F supports:
 * <br><code>checkBox.putClientProperty("JButton.selectedState", "indeterminate");</code>
 */
public class AquaMixedStateUI extends MixedStateUI {

	public AquaMixedStateUI(JCheckBox checkBox) {
		super(checkBox);
	}

	@Override
	protected void doInstall() {
		checkBox.setSelected(true);
		checkBox.putClientProperty("JButton.selectedState", "indeterminate");
		checkBox.repaint();
	}

	@Override
	protected void doUninstall() {
		checkBox.putClientProperty("JButton.selectedState", null);
		checkBox.repaint();
	}
}