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
package com.pump.plaf.button;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import com.pump.plaf.PlafPaintUtils;

/**
 * This resembles the "square" button UI as originally seen in Mac OS 10.5, but
 * the colors are more subtle now to reflect the modern transition to more
 * subtle UI's. Apple no longer encourages the use of most custom button looks.
 * <p>
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/filledbuttonui/SquareButtonUI.png"
 * alt="SquareButtonUI Screenshot">
 * <P>
 * It is not intended to be an exact replica, but it is very similar.
 * <P>
 * According to <a href="http://nadeausoftware.com/node/87">this</a> article,
 * the "square" look is often used for: <BR>
 * "Buttons on a tool bar, or buttons smaller than a standard button".
 */
public class SquareButtonUI extends QButtonUI {

	/**
	 * The <code>SimpleButtonFill</code> used to achieve the "Square" look.
	 */
	public static final SimpleButtonFill SQUARE_FILL = new SimpleButtonFill() {
		private float[] fillWeights = new float[] { 0, .25f, 1 };
		protected Color strokeColor = new Color(0xff838383);

		private Color[] normalColors = new Color[] { new Color(0xFFE1E1E1),
				new Color(0xFFE5E5E5), new Color(0xFFF9F9F9) };

		private Color[] darkestColors = new Color[] { new Color(0xFFB0B0B0),
				new Color(0xFFB2B2B2), new Color(0xFFCBCBCB) };

		@Override
		public Paint getDarkestFill(Rectangle fillRect) {
			return PlafPaintUtils.getVerticalGradient("square.darkest",
					fillRect.height, fillRect.y, fillWeights, darkestColors);
		}

		@Override
		public Paint getNormalFill(Rectangle fillRect) {
			return PlafPaintUtils.getVerticalGradient("square.normal",
					fillRect.height, fillRect.y, fillWeights, normalColors);
		}

		@Override
		public Paint getStroke(ButtonState.Float state, Rectangle fillRect) {
			return strokeColor;
		}
	};

	private static SquareButtonUI squareButtonUI = new SquareButtonUI();

	/**
	 * Create a new instance of this ButtonUI for a component.
	 * <p>
	 * This method is required if you want to make this ButtonUI the default UI
	 * by invoking: <br>
	 * <code>UIManager.getDefaults().put("ButtonUI", "com.pump.plaf.SquareButtonUI");</code>
	 */
	public static ComponentUI createUI(JComponent c) {
		return squareButtonUI;
	}

	public SquareButtonUI() {
		setButtonFill(SQUARE_FILL);
		setCornerRadius(0);
	}
};