package com.pump.plaf.button.mixed;

import java.util.Objects;

import javax.swing.JCheckBox;
import javax.swing.plaf.basic.BasicRadioButtonUI;

import com.pump.util.JVM;

/**
 * This is a three-state enum describing the state of a JCheckBox. The new third state (MIXED) indicates
 * a checkbox is neither fully selected nor unselected. This probably is because the checkbox summarizes
 * several requirements that are only partially filled.
 * <p>
 * This is necessarily hacky, because the JCheckBox and ButtonModels only define the "selected" state as a
 * boolean. But as long as you consistently use {@link MixedState#setState(JCheckBox, MixedState)} and
 * {@link MixedState#getState(JCheckBox)} instead of {@link JCheckBox#isSelected()}, you should interact
 * with the appropriate state.
 * <p>
 * On Mac the UI is implemented so a MIXED state is rendered on top of a selected JCheckBox. On Windows
 * the UI is implemented so it is rendered on top of an unselected JCheckBox.
 */
public enum MixedState {
	UNSELECTED(), SELECTED(), MIXED();

	/**
	 * This client property on a JCheckBox resolves to a MixedStateUI. If this is non-null then the
	 * JCheckBox is currently displaying a mixed state.
	 */
	private static final String PROPERTY_MIXED_STATE_UI = "MixedState#ui";

	/**
	 * This client property on a JCheckBox resolves to a MixedState. This is updated with every
	 * call to {@link #setState(JCheckBox, MixedState)}. This is not an essential part of our
	 * data model, but it is updated so other parties can attach listeners.
	 */
	public static final String PROPERTY_MIXED_STATE = "MixedState#state";
	
	/**
	 * Assign the MixedState of a JCheckBox.
	 * 
	 * @param checkBox the JCheckBox to change the state of.
	 * @param newState the new state
	 * @return true if a change occurred.
	 */
	public static boolean set(JCheckBox checkBox, MixedState newState) {
		Objects.requireNonNull(checkBox);
		Objects.requireNonNull(newState);

		MixedState oldState = get(checkBox);
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
		checkBox.putClientProperty(PROPERTY_MIXED_STATE, newState);
		
		return oldState != newState;
	}
	
	/**
	 * Return the MixedState for a JCheckBox.
	 */
	public static MixedState get(JCheckBox checkBox) {
		MixedStateUI ui = (MixedStateUI) checkBox.getClientProperty(PROPERTY_MIXED_STATE_UI);
		if (ui != null)
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
		
		// We could probably use a VectorGraphics2D to render the JCheckBox and identify how
		// the icon is drawn?
		
		throw new UnsupportedOperationException();
	}

}
