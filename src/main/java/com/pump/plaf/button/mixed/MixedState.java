package com.pump.plaf.button.mixed;

import java.util.Objects;

import javax.swing.JCheckBox;
import javax.swing.plaf.basic.BasicRadioButtonUI;

import com.pump.util.JVM;

public enum MixedState {
	UNSELECTED(), SELECTED(), MIXED();

	/**
	 * This client property on a JCheckBox resolves to a MixedStateUI. If this is non-null then the
	 * JCheckBox is currently displaying a mixed state.
	 */
	private static final String PROPERTY_MIXED_STATE_UI = "MixedState#ui";
	
	public static boolean setState(JCheckBox checkBox, MixedState newState) {
		Objects.requireNonNull(checkBox);
		Objects.requireNonNull(newState);

		MixedState oldState = getState(checkBox);
		MixedStateUI ui = (MixedStateUI) checkBox.getClientProperty(PROPERTY_MIXED_STATE_UI);
		
		switch (newState) {
			case MIXED:
				if (ui == null) {
					ui = createMixedStateUI(checkBox);
					checkBox.putClientProperty(PROPERTY_MIXED_STATE_UI, ui);
					ui.install();
				}
				break;
			case UNSELECTED:
				if (ui != null) {
					ui.uninstall();
					checkBox.putClientProperty(PROPERTY_MIXED_STATE_UI, null);
					ui = null;
				}
				checkBox.setSelected(false);
				break;
			case SELECTED:
				if (ui != null) {
					ui.uninstall();
					checkBox.putClientProperty(PROPERTY_MIXED_STATE_UI, null);
					ui = null;
				}
				checkBox.setSelected(true);
				break;
		}
		
		return oldState != newState;
	}
	
	/**
	 * Return the MixedState for a JCheckBox.
	 * <p>
	 * If a MixedState has never been assigned then this will return {@link #UNSELECTED} or {@link #SELECTED} based on the checkbox's button model.
	 */
	public static MixedState getState(JCheckBox checkBox) {
		MixedStateUI ui = (MixedStateUI) checkBox.getClientProperty(PROPERTY_MIXED_STATE_UI);
		if (ui !=null)
			return MixedState.MIXED;
		return checkBox.isSelected() ? MixedState.SELECTED : MixedState.UNSELECTED;
	}

	static MixedStateUI createMixedStateUI(JCheckBox checkBox) {
		if (checkBox.getUI() instanceof BasicRadioButtonUI) {
			return new BasicMixedStateUI(checkBox);
		}
		if (JVM.isMac) {
			return new AquaMixedStateUI(checkBox);
		}
		
		// I need to inspect the setup where this happens to find a good solution
		throw new UnsupportedOperationException();
	}

}
