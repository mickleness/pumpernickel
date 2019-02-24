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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

/**
 * This resembles the L&amp;F used on XP.
 * <p>
 * <img src=
 * "https://raw.githubusercontent.com/mickleness/pumpernickel/master/resources/filledbuttonui/XPButtonUI.png"
 * alt="XPButtonUI Screenshot">
 * <P>
 * This relies on careful borders and highlights more than most other ButtonUIs
 * in this package.
 **/
public class XPButtonUI extends QButtonUI {
	private static final Color strokeColor = new Color(0, 60, 116);
	private static final Color shadowHighlight = new Color(255, 255, 255, 120);
	private static final Color[] normalColors = new Color[] {
			new Color(255, 255, 255), new Color(240, 240, 240) };
	private static final Color[] pressedColors = new Color[] {
			new Color(230, 230, 224), new Color(224, 224, 215) };

	public static final ButtonShape XP_SHAPE = new ButtonShape(3, 3);
	public static final ButtonFill XP_FILL = new ButtonFill() {
		@Override
		public Paint getStroke(AbstractButton button, Rectangle fillRect) {
			return strokeColor;
		}

		@Override
		public Paint getFill(AbstractButton button, Rectangle fillRect) {
			return PlafPaintUtils.getVerticalGradient("xp.normal",
					fillRect.height, fillRect.y, weights, normalColors);
		}

		@Override
		public Color getShadowHighlight(AbstractButton button) {
			return shadowHighlight;
		}
	};

	private static XPButtonUI xpButtonUI = new XPButtonUI();

	/**
	 * Create a new instance of this ButtonUI for a component.
	 * <p>
	 * This method is required if you want to make this ButtonUI the default UI
	 * by invoking: <br>
	 * <code>UIManager.getDefaults().put("ButtonUI", "com.pump.plaf.XPButtonUI");</code>
	 */
	public static ComponentUI createUI(JComponent c) {
		return xpButtonUI;
	}

	public XPButtonUI() {
		super(XP_FILL, XP_SHAPE);
	}

	private static final Color[] vLine1Colors = new Color[] {
			new Color(255, 255, 255), new Color(240, 239, 233),
			new Color(234, 232, 225) };
	private static final Color[] vLine2Colors = new Color[] {
			new Color(255, 255, 255), new Color(235, 232, 226),
			new Color(224, 221, 212) };
	private static final Color[] focusColors = new Color[] {
			new Color(188, 212, 246), new Color(137, 173, 228) };
	private static final Color[] rolloverColors = new Color[] {
			new Color(253, 216, 137), new Color(248, 179, 49) };
	private static final float[] vLineWeights = new float[] { 0, .2f, 1 };
	private static final float[] weights = new float[] { 0, 1 };
	private static final Color hShadow1 = new Color(226, 223, 214);
	private static final Color hShadow2 = new Color(214, 208, 197);

	private static final Color focusLine1 = new Color(206, 231, 255);
	private static final Color focusLine2 = new Color(105, 130, 238);

	private static final Color rolloverLine1 = new Color(255, 240, 207);
	private static final Color rolloverLine2 = new Color(229, 151, 0);

	private static final Color[] shadow = new Color[] { new Color(0, 0, 0, 25),
			new Color(0, 0, 0, 18), new Color(0, 0, 0, 10) };

	private static final Color[] highlight = new Color[] {
			new Color(234, 233, 227), new Color(242, 241, 238) };

	/**
	 * This returns PaintFocus.NONE, because paintBackground() handles rendering
	 * the focus.
	 */
	@Override
	public PaintFocus getFocusPainting(AbstractButton button) {
		return PaintFocus.NONE;
	}

	@Override
	public void paintBackground(Graphics2D g, ButtonInfo info) {
		super.paintBackground(g, info);
		int right = info.fillBounds.x + info.fillBounds.width;
		int bottom = info.fillBounds.y + info.fillBounds.height;

		g = (Graphics2D) g.create();

		g.clip(info.fill);

		boolean hasFocus = hasFocus(info.button);

		if (info.button.getModel().isPressed()
				|| info.button.getModel().isArmed()
				|| info.button.getModel().isSelected()
				|| QButtonUI.isSpacebarPressed(info.button)) {
			if (info.button.isContentAreaFilled()) {
				g.setPaint(PlafPaintUtils.getVerticalGradient("xp.pressed",
						info.fillBounds.height, info.fillBounds.y, weights,
						pressedColors));
				g.fill(info.fillBounds);
			}
			g.setStroke(new BasicStroke(1));
			g.setColor(shadow[0]);
			g.translate(0, 1);
			g.draw(info.stroke);
			g.setColor(shadow[1]);
			g.translate(1, 1);
			g.draw(info.stroke);
			g.setColor(shadow[2]);
			g.translate(1, 1);
			g.draw(info.stroke);
			g.translate(-2, -3);

			if (info.button.isContentAreaFilled()) {
				// this just looks weird if there's no content...
				g.setColor(highlight[0]);
				g.translate(0, -2);
				g.draw(info.stroke);
				g.setColor(highlight[1]);
				g.translate(0, 1);
				g.draw(info.stroke);
			}
		} else if (isRollover(info.button)) {
			g.setPaint(PlafPaintUtils.getVerticalGradient("XP.rollover",
					info.fillBounds.height, info.fillBounds.y, weights,
					rolloverColors));
			g.setStroke(new BasicStroke(5));
			g.draw(info.stroke);

			g.setStroke(new BasicStroke(1));
			g.setColor(rolloverLine1);
			g.translate(0, 1);
			g.draw(info.stroke);

			g.translate(0, -2);
			g.setColor(rolloverLine2);
			g.draw(info.stroke);
			g.translate(0, 1);
		} else if (hasFocus) {
			g.setPaint(PlafPaintUtils.getVerticalGradient("XP.focus",
					info.fillBounds.height, info.fillBounds.y, weights,
					focusColors));
			g.setStroke(new BasicStroke(5));
			g.draw(info.stroke);

			g.setStroke(new BasicStroke(1));
			g.setColor(focusLine1);
			g.translate(0, 1);
			g.draw(info.stroke);

			g.translate(0, -2);
			g.setColor(focusLine2);
			g.draw(info.stroke);
			g.translate(0, 1);
		} else {
			if (info.button.isContentAreaFilled()) {
				// lines just look weird without the content area
				boolean showSeparators = isShowingSeparators(info.button);
				int hPos = getHorizontalPosition(info.button);
				int vPos = getVerticalPosition(info.button);

				if ((showSeparators) || (hPos == POS_ONLY || hPos == POS_RIGHT)) {
					g.setPaint(PlafPaintUtils.getVerticalGradient("XP.vLine1",
							info.fillBounds.height, info.fillBounds.y,
							vLineWeights, vLine1Colors));
					g.drawLine(right - 2, info.fillBounds.y, right - 2,
							info.fillBounds.y + info.fillBounds.height);
					g.setPaint(PlafPaintUtils.getVerticalGradient("XP.vLine2",
							info.fillBounds.height, info.fillBounds.y,
							vLineWeights, vLine2Colors));
					g.drawLine(right - 1, info.fillBounds.y, right - 1,
							info.fillBounds.y + info.fillBounds.height);
				}
				if ((showSeparators)
						|| (vPos == POS_ONLY || vPos == POS_BOTTOM)) {
					g.setColor(hShadow1);
					g.drawLine(info.fillBounds.x, bottom - 2, info.fillBounds.x
							+ info.fillBounds.width, bottom - 2);
					g.setColor(hShadow2);
					g.drawLine(info.fillBounds.x, bottom - 1, info.fillBounds.x
							+ info.fillBounds.width, bottom - 1);
				}
			}
		}
		g.dispose();
	}

	@Override
	public boolean isFillOpaque() {
		return true;
	}
}