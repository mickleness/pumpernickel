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

	public JSwitchButton() {
		super();
	}

	public JSwitchButton(String text) {
		super(text);
	}

	public JSwitchButton(String text, boolean selected) {
		super(text, selected);
	}

	public JSwitchButton(boolean selected) {
		this("", selected);
	}

	@Override
	public String getUIClassID() {
		return uiClassID;
	}

	@Override
	public void updateUI() {
		if (UIManager.getDefaults().get(uiClassID) == null) {
			UIManager.getDefaults().put(uiClassID,
					"com.pump.plaf.button.SwitchButtonUI");
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