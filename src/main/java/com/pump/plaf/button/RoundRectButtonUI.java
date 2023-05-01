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
 * This resembles the "roundRect" button UI as originally seen in Mac OS 10.5,
 * but the colors are more subtle now to reflect the modern transition to more
 * subtle UI's. Apple no longer encourages the use of most custom button looks.
 * <p>
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/filledbuttonui/RoundRectButtonUI.png"
 * alt="RoundRectButtonUI Screenshot">
 * <P>
 * It is not intended to be an exact replica, but it is very similar.
 * <P>
 * According to <a href="http://nadeausoftware.com/node/87">this</a> article,
 * the "recessed" and "roundRect" look is originally intended to indicate: <br>
 * "a choice in limiting the scope of an operation, such as the buttons at the top of a Finder when searching."
 */
public class RoundRectButtonUI extends QButtonUI {

	/**
	 * The <code>SimpleButtonFill</code> used to achieve the "RoundRect" look.
	 */
	public static final SimpleButtonFill ROUNDRECT_FILL = new SimpleButtonFill() {
		private float[] fillWeights = new float[] { 0, 1 };
		protected Color strokeColor = new Color(0xff989898);

		private Color[] normalColors = new Color[] { new Color(0xffFCFCFC),
				new Color(0xffE7E7E7) };

		private Color[] darkestColors = new Color[] { new Color(0xffC0C0C0),
				new Color(0xffCaCaCa) };

		private VerticalGradientMaker darkestFill = new VerticalGradientMaker(fillWeights, darkestColors, "roundRect.darker");
		private VerticalGradientMaker normalFill = new VerticalGradientMaker(fillWeights, normalColors, "roundRect.normal");

		@Override
		public VerticalGradientMaker getDarkestFill(Rectangle fillRect) {
			return darkestFill;
		}

		@Override
		public VerticalGradientMaker getNormalFill(Rectangle fillRect) {
			return normalFill;
		}

		@Override
		public Paint getStroke(ButtonState.Float state, Rectangle fillRect) {
			return strokeColor;
		}
	};

	private static RoundRectButtonUI roundRectButtonUI = new RoundRectButtonUI();

	/**
	 * Create a new instance of this ButtonUI for a component.
	 * <p>
	 * This method is required if you want to make this ButtonUI the default UI
	 * by invoking: <br>
	 * <code>UIManager.getDefaults().put("ButtonUI", "com.pump.plaf.RoundRectButtonUI");</code>
	 */
	public static ComponentUI createUI(JComponent c) {
		return roundRectButtonUI;
	}

	/** Creates a RoundRectButtonUI with the preferred radius of 8 pixels. */
	public RoundRectButtonUI() {
		this(8);
	}

	/** Creates a RoundRectButtonUI. */
	public RoundRectButtonUI(int cornerRadius) {
		setButtonFill(ROUNDRECT_FILL);
		setCornerRadius(cornerRadius);
	}
};