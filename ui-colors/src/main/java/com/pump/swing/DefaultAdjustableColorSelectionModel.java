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
package com.pump.swing;

import javax.swing.colorchooser.DefaultColorSelectionModel;

public class DefaultAdjustableColorSelectionModel extends
		DefaultColorSelectionModel implements AdjustableColorSelectionModel {
	private static final long serialVersionUID = 1L;

	private boolean isAdjusting = false;

	@Override
	public void setValueIsAdjusting(boolean isAdjusting) {
		if (isAdjusting != getValueIsAdjusting()) {
			this.isAdjusting = isAdjusting;
			fireStateChanged();
		}
	}

	@Override
	public boolean getValueIsAdjusting() {
		return isAdjusting;
	}
}