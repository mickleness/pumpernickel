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
