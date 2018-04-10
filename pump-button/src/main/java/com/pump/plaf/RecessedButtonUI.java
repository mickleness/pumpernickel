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
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

/**
 * This resembles the "recessed" button UI as seen in Mac OS 10.5.
 * <p>
 * <img src=
 * "https://raw.githubusercontent.com/mickleness/pumpernickel/master/pump-release/resources/filledbuttonui/RecessedButtonUI.png"
 * alt="RecessedButtonUI Screenshot">
 * <P>
 * It is not intended to be an exact replica, but it is very similar.
 * <P>
 * According to <a href="http://nadeausoftware.com/node/87">this</a> article,
 * the "recessed" and "roundRect" look is originally intended to indicate: <br>
 * "to indicate a choice in limiting the scope of an operation, such as the buttons at the top of a Finder when searching."
 * 
 * 
 * @see com.pump.showcase.FilledButtonUIDemo
 */
public class RecessedButtonUI extends FilledButtonUI {

	public static final ButtonShape RECESSED_SHAPE = new ButtonShape(8, 12);

	/**
	 * The <code>SimpleButtonFill</code> used to achieve the "recessed" look.
	 */
	public static final SimpleButtonFill RECESSED_FILL = new SimpleButtonFill() {
		private float[] fillWeights = new float[] { 0, .14f, 1 };
		private Color strokeColor = new Color(0xff6F6F6F);;

		private Color[] normalColors = new Color[] {
				new Color(0x97000000, true), new Color(0x58000000, true),
				new Color(0x58000000, true) };
		private Color[] darkerColors = new Color[] {
				new Color(0x98000000, true), new Color(0x80000000, true),
				new Color(0x80000000, true) };

		@Override
		public Paint getDarkerFill(Rectangle fillRect) {
			return PlafPaintUtils.getVerticalGradient("recessedUI.darker",
					fillRect.height, fillRect.y, fillWeights, darkerColors);
		}

		@Override
		public Paint getDarkestFill(Rectangle fillRect) {
			return null;
		}

		@Override
		public Paint getNormalFill(Rectangle fillRect) {
			return PlafPaintUtils.getVerticalGradient("recessedUI.normal",
					fillRect.height, fillRect.y, fillWeights, normalColors);
		}

		@Override
		public Paint getRolloverFill(Rectangle fillRect) {
			return null;
		}

		private float[] borderWeights = new float[] { 0, .5f, 1 };
		private Color[] borderColors = new Color[] { new Color(0xff5F5F5F),
				new Color(0xff979797), new Color(0xff979797) };

		@Override
		public Paint getStroke(AbstractButton button, Rectangle fillRect) {
			if (fillRect == null)
				return borderColors[1];
			int verticalPosition = getVerticalPosition(button);
			if (verticalPosition == POS_ONLY) {
				return PlafPaintUtils.getVerticalGradient("recessedUI.border",
						fillRect.height + 2, fillRect.y, borderWeights,
						borderColors);
			}
			return strokeColor;
		}
	};

	private static RecessedButtonUI recessedButtonUI = new RecessedButtonUI();

	/**
	 * Create a new instance of this ButtonUI for a component.
	 * <p>
	 * This method is required if you want to make this ButtonUI the default UI
	 * by invoking: <br>
	 * <code>UIManager.getDefaults().put("ButtonUI", "com.pump.plaf.RecessedButtonUI");</code>
	 */
	public static ComponentUI createUI(JComponent c) {
		return recessedButtonUI;
	}

	public RecessedButtonUI() {
		super(RECESSED_FILL, RECESSED_SHAPE);
	}

	@Override
	public void paintStroke(Graphics2D g, ButtonInfo info) {
		super.paintStroke(g, info);

		int verticalPosition = getVerticalPosition(info.button);
		int horizontalPosition = getVerticalPosition(info.button);
		if (verticalPosition == POS_ONLY
				&& (horizontalPosition == POS_RIGHT || horizontalPosition == POS_MIDDLE)) {
			g.setColor(new Color(0xff6F6F6F));
			g.drawLine(0, info.fillBounds.y + 1, 0, info.fillBounds.y
					+ info.fillBounds.height - 1);
		}
	}

	/** Returns false. This button is designed to be translucent. */
	@Override
	public boolean isFillOpaque() {
		return false;
	}
};