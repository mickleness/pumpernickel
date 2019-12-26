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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import com.pump.awt.TransformedTexturePaint;

public abstract class SimpleButtonFill extends ButtonFill {

	protected static final Color shadowHighlight = new Color(255, 255, 255, 120);

	/** Returns a translucent white. */
	@Override
	public Color getShadowHighlight(ButtonState.Float button) {
		return shadowHighlight;
	}

	/**
	 * This fill is the darkest shade for this button. This is used for
	 * <code>JToggleButtons</code> to depict a selected state. If this is null,
	 * then <code>getDarkerFill()</code> may be used instead.
	 * 
	 * @param fillRect
	 *            the rectangle the fill applies to.
	 * @return the paint to use when a toggle button is selected.
	 */
	public abstract Paint getDarkestFill(Rectangle fillRect);

	/**
	 * The normal fill of this button. Unlike the other <code>getter()</code>
	 * methods in this class: this cannot return null.
	 * 
	 * @param fillRect
	 *            the rectangle the fill applies to.
	 * @return the paint to use when this button is in its normal state.
	 */
	public abstract Paint getNormalFill(Rectangle fillRect);

	@Override
	public Paint getFill(ButtonState.Float state, Rectangle fillRect) {
		Paint darkestFill = getDarkestFill(fillRect);
		Paint normalFill = getNormalFill(fillRect);

		int imageType = BufferedImage.TYPE_INT_RGB;
		for (Paint p : new Paint[] { darkestFill, normalFill }) {
			if (p.getTransparency() != Transparency.OPAQUE)
				imageType = BufferedImage.TYPE_INT_ARGB;
		}
		int width = Math.max(1, fillRect.width);
		int height = Math.max(1, fillRect.height);
		BufferedImage bi = new BufferedImage(width, height, imageType);
		Graphics2D g = bi.createGraphics();
		g.translate(-fillRect.x, -fillRect.y);

		paint(g, normalFill, 1, fillRect);

		if (state.isArmed > 0) {
			paint(g, darkestFill, state.isArmed(), fillRect);
		}
		if (state.isSelected() > 0) {
			paint(g, darkestFill, state.isSelected() * .7f, fillRect);
		}
		if (state.isRollover() > 0) {
			paint(g, darkestFill, state.isRollover() * .4f, fillRect);
		}

		return new TransformedTexturePaint(bi, new Rectangle(0, 0,
				fillRect.width, fillRect.height),
				AffineTransform.getTranslateInstance(fillRect.x, fillRect.y));
	}

	private void paint(Graphics2D g, Paint paint, float alpha,
			Rectangle fillRect) {
		g.setPaint(paint);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				alpha));
		g.fillRect(fillRect.x, fillRect.y, fillRect.width, fillRect.height);
	}

}