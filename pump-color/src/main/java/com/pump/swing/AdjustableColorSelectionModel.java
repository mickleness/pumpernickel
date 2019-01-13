package com.pump.swing;

import javax.swing.colorchooser.ColorSelectionModel;

/**
 * This is a ColorSelectionModel that also adds the isAdjusting property (which
 * is a useful attribute identified with sliders and scrollbars).
 *
 */
public interface AdjustableColorSelectionModel extends ColorSelectionModel {

	/**
	 * This attribute indicates that any upcoming changes to the value of the
	 * model should be considered a single event. This attribute will be set to
	 * true at the start of a series of changes to the value, and will be set to
	 * false when the value has finished changing. Normally this allows a
	 * listener to only take action when the final value change in committed,
	 * instead of having to do updates for all intermediate values.
	 * <p>
	 * This is primarily used to indicate the mouse is dragging.
	 *
	 * @param b
	 *            true if the upcoming changes to the value property are part of
	 *            a series
	 */
	void setValueIsAdjusting(boolean b);

	/**
	 * Returns true if the current changes to the value property are part of a
	 * series of changes.
	 *
	 * @return the valueIsAdjustingProperty.
	 * @see #setValueIsAdjusting
	 */
	boolean getValueIsAdjusting();
}
