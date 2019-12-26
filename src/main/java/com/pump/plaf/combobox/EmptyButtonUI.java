package com.pump.plaf.combobox;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Rectangle;

import javax.swing.AbstractButton;
import javax.swing.JComponent;

import com.pump.plaf.button.ButtonFill;
import com.pump.plaf.button.ButtonState.Float;
import com.pump.plaf.button.QButtonUI;

/**
 * This ButtonUI will render the icon/text of a button with no accompanying
 * border, focus, fill, etc.
 */
class EmptyButtonUI extends QButtonUI {
	static ButtonFill EMPTY_BUTTON_FILL = new ButtonFill() {

		@Override
		public Color getShadowHighlight(Float buttonState) {
			return null;
		}

		@Override
		public Paint getStroke(Float buttonState, Rectangle fillRect) {
			return new Color(0, 0, 0, 0);
		}

		@Override
		public Paint getFill(Float buttonState, Rectangle fillRect) {
			return new Color(0, 0, 0, 0);
		}

	};

	{
		setCornerRadius(0);
		setFocusRingSize(0);
		setButtonFill(EMPTY_BUTTON_FILL);
	}

	@Override
	public void installUI(JComponent c) {
		((AbstractButton) c).setFocusPainted(false);
		super.installUI(c);
	}
}