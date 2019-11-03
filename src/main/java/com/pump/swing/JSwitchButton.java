package com.pump.swing;

import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * Similar to a JCheckBox, except this renders as a switch that toggles from
 * left to right.
 */
public class JSwitchButton extends JToggleButton {
	private static final long serialVersionUID = 1L;

	private static final String uiClassID = "SwitchButton";

	public JSwitchButton(String text) {
		super(text);
	}

	@Override
	public String getUIClassID() {
		return uiClassID;
	}

	@Override
	public void updateUI() {
		if (UIManager.getDefaults().get(uiClassID) == null) {
			UIManager.getDefaults().put(uiClassID,
					"com.pump.plaf.SwitchButtonUI");
		}
		BasicButtonUI sui = (BasicButtonUI) UIManager.getUI(this);
		super.setUI(sui);
	}

	// I tried to add my own AccessibleContext and used a custom AccessibleRole.
	// I was able to get VoiceOver on Mac to identify this as "switch", but it
	// seemed like the normal options to select/deselect the button were NOT
	// automatically identified by VoiceOver. I think it's better to err on
	// the side of VoiceOver calling this a "checkbox, checked" than calling
	// it a "switch" with no other accompanying state info.

}
