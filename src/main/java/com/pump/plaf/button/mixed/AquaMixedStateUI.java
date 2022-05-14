package com.pump.plaf.button.mixed;

import javax.swing.JCheckBox;

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