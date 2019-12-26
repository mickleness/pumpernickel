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
package com.pump.plaf.button;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import com.pump.plaf.PlafPaintUtils;

/**
 * This was originally based on the "bevel" button in Mac OS 10.5, but the
 * colors are more subtle now to reflect the modern transition to more subtle
 * UI's. Apple no longer encourages the use of most custom button looks.
 * <p>
 * <img src=
 * "https://raw.githubusercontent.com/mickleness/pumpernickel/master/resources/filledbuttonui/BevelButtonUI.png"
 * alt="BevelButtonUI Screenshot">
 * <P>
 * It is not intended to be an exact replica, but it is very similar.
 * <P>
 * According to <a href="http://nadeausoftware.com/node/87">this</a> article,
 * the "bevel" look was often used for: <BR>
 * "Buttons with icons, or buttons sized larger than a standard Mac button".
 */
public class BevelButtonUI extends QButtonUI {

	/**
	 * The <code>SimpleButtonFill</code> used to achieve the "bevel" look.
	 */
	public static final SimpleButtonFill BEVEL_FILL = new SimpleButtonFill() {
		private float[] fillWeights = new float[] { 0, .15f, .151f, .8f, 1 };
		private float[] borderWeights = new float[] { 0, .2f, .8f, 1 };
		private Color[] strokeColors = new Color[] { new Color(0xffAFAFAF),
				new Color(0xff838383), new Color(0xff838383),
				new Color(0xff6C6C6C) };
		private Color[] normalColors = new Color[] { new Color(0xffFCFCFC),
				new Color(0xffFAFAFA), new Color(0xffF6F6F6),
				new Color(0xffF9F9F9), new Color(0xffFCFCFC) };
		private Color[] darkestColors = new Color[] { new Color(0xffCFCFCF),
				new Color(0xffCFCFCF), new Color(0xffC8C8C8),
				new Color(0xffCBCBCB), new Color(0xffCDCDCD) };

		@Override
		public Paint getDarkestFill(Rectangle fillRect) {
			return PlafPaintUtils.getVerticalGradient("bevelUI.darkest",
					fillRect.height, fillRect.y, fillWeights, darkestColors);
		}

		@Override
		public Paint getNormalFill(Rectangle fillRect) {
			return PlafPaintUtils.getVerticalGradient("bevelUI.normal",
					fillRect.height, fillRect.y, fillWeights, normalColors);
		}

		@Override
		public Paint getStroke(ButtonState.Float state, Rectangle fillRect) {
			if (fillRect == null)
				return strokeColors[1];
			return PlafPaintUtils.getVerticalGradient("bevelUI.border",
					fillRect.height + 1, fillRect.y, borderWeights,
					strokeColors);
		}

		@Override
		public Color getShadowHighlight(ButtonState.Float state) {
			return null;
		}
	};

	private static BevelButtonUI bevelButtonUI = new BevelButtonUI();

	/**
	 * This method has to exist in order for to make this UI the button default
	 * by calling: <br>
	 * <code>UIManager.getDefaults().put("ButtonUI", "com.pump.plaf.BevelButtonUI");</code>
	 */
	public static ComponentUI createUI(JComponent c) {
		return bevelButtonUI;
	}

	public BevelButtonUI() {
		setCornerRadius(4);
		setButtonFill(BEVEL_FILL);
	}
}