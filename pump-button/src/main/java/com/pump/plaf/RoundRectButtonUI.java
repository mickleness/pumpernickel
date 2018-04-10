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
package com.pump.plaf;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Rectangle;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

/**
 * This resembles the "roundRect" button UI as seen in Mac OS 10.5.
 * <p>
 * <img src=
 * "https://raw.githubusercontent.com/mickleness/pumpernickel/master/pump-release/resources/filledbuttonui/RoundRectButtonUI.png"
 * alt="RoundRectButtonUI Screenshot">
 * <P>
 * It is not intended to be an exact replica, but it is very similar.
 * <P>
 * According to <a href="http://nadeausoftware.com/node/87">this</a> article,
 * the "recessed" and "roundRect" look is originally intended to indicate: <br>
 * "a choice in limiting the scope of an operation, such as the buttons at the top of a Finder when searching."
 * 
 * @see com.pump.showcase.FilledButtonUIDemo
 */
public class RoundRectButtonUI extends FilledButtonUI {
	public final static ButtonShape ROUNDRECT_SHAPE = new ButtonShape(8,
			Short.MAX_VALUE);

	/**
	 * The <code>SimpleButtonFill</code> used to achieve the "RoundRect" look.
	 */
	public static final SimpleButtonFill ROUNDRECT_FILL = new SimpleButtonFill() {
		private float[] fillWeights = new float[] { 0, 1 };
		protected Color strokeColor = new Color(0xff989898);

		private Color[] normalColors = new Color[] { new Color(0xffFCFCFC),
				new Color(0xffDFDFDF) };

		private Color[] darkerColors = new Color[] { new Color(0xffC0C0C0),
				new Color(0xffFCFCFC) };

		@Override
		public Paint getDarkerFill(Rectangle fillRect) {
			return PlafPaintUtils.getVerticalGradient("roundRect.darker",
					fillRect.height, fillRect.y, fillWeights, darkerColors);
		}

		@Override
		public Paint getDarkestFill(Rectangle fillRect) {
			return null;
		}

		@Override
		public Paint getNormalFill(Rectangle fillRect) {
			return PlafPaintUtils.getVerticalGradient("roundRect.normal",
					fillRect.height, fillRect.y, fillWeights, normalColors);
		}

		@Override
		public Paint getRolloverFill(Rectangle fillRect) {
			return null;
		}

		@Override
		public Paint getStroke(AbstractButton button, Rectangle fillRect) {
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
		super(ROUNDRECT_FILL, ROUNDRECT_SHAPE);
	}

	/**
	 * Creates a RoundRectButtonUI.
	 * 
	 * @param preferredRadius
	 *            the preferred radius (in pixels).
	 */
	public RoundRectButtonUI(int preferredRadius) {
		super(ROUNDRECT_FILL, new ButtonShape(preferredRadius, Short.MAX_VALUE));
	}

	@Override
	public boolean isFillOpaque() {
		return true;
	}
};