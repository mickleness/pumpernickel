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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.AbstractButton;
import javax.swing.JToggleButton;

import com.pump.awt.TransformedTexturePaint;
import com.pump.plaf.QButtonUI.ButtonState;

public abstract class SimpleButtonFill extends ButtonFill {

	protected static final Color shadowHighlight = new Color(255, 255, 255, 120);

	/** Returns a translucent white. */
	@Override
	public Color getShadowHighlight(AbstractButton button) {
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
	 * This fill is slightly darker than the normal fill. Depending on whether
	 * the button being rendered is a <code>JToggleButton</code>: this may be
	 * the fill used for the pressed state or (in non-toggle-buttons) the
	 * selected state.
	 * 
	 * @param fillRect
	 *            the rectangle the fill applies to.
	 * @return the paint to use when this button is pressed or selected.
	 */
	public abstract Paint getDarkerFill(Rectangle fillRect);

	/**
	 * The rollover fill of this button, or null if there is no special fill for
	 * rollovers. If this is null, then <code>getDarkestFill()</code> may be
	 * used instead.
	 * 
	 * @param fillRect
	 *            the rectangle the fill applies to.
	 * @return the paint to use when this button is rolled over.
	 */
	public abstract Paint getRolloverFill(Rectangle fillRect);

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
	public Paint getFill(AbstractButton button, Rectangle fillRect) {
		ButtonState partialState = (ButtonState) button
				.getClientProperty(QButtonUI.PROPERTY_BUTTON_STATE);
		if (partialState == null) {
			partialState = new ButtonState(button);
		}

		BufferedImage bi = new BufferedImage(fillRect.width, fillRect.height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		g.translate(-fillRect.x, -fillRect.y);

		Paint darkestFill = getDarkestFill(fillRect);
		Paint darkerFill = getDarkerFill(fillRect);
		Paint rolloverFill = getRolloverFill(fillRect);
		Paint normalFill = getNormalFill(fillRect);
		if (normalFill == null)
			throw new NullPointerException(
					"The getNormalFill() method cannot return null.");

		paint(g, normalFill, 1, fillRect);

		if (partialState.getRollover() > 0 && rolloverFill != null) {
			paint(g, rolloverFill, partialState.getRollover(), fillRect);
		}

		if (button instanceof JToggleButton) {
			if (partialState.getSelected() > 0) {
				if (darkerFill != null) {
					paint(g, darkerFill, partialState.getSelected(), fillRect);
				} else if (darkestFill != null) {
					paint(g, darkestFill, partialState.getSelected(), fillRect);
				}
			}

			float armed = Math.max(partialState.getArmed(),
					partialState.getSpacebarPressed());
			if (armed > 0) {
				if (darkestFill != null) {
					paint(g, darkestFill, armed, fillRect);
				} else if (darkerFill != null) {
					paint(g, darkerFill, armed, fillRect);
				}
			}
		} else {
			float armed = Math.max(
					Math.max(partialState.getSelected(),
							partialState.getArmed()),
					partialState.getSpacebarPressed());
			if (armed > 0) {
				if (darkerFill != null) {
					paint(g, darkerFill, armed, fillRect);
				} else if (darkestFill != null) {
					paint(g, darkestFill, armed, fillRect);
				}
			}
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