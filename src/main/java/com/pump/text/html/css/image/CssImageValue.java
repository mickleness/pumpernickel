package com.pump.text.html.css.image;

import java.awt.Graphics2D;

import com.pump.text.html.css.CssValue;
import com.pump.text.html.view.QViewHelper;

public interface CssImageValue extends CssValue {

	/**
	 * 
	 * @param g
	 * @param viewHelper
	 *            the QViewHelper painting this image
	 * @param layerIndex
	 *            the layer index that is being painted. The highest index is
	 *            painted first, and the zeroeth index is painted last.
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 */
	public void paintRectangle(Graphics2D g, QViewHelper viewHelper,
			int layerIndex, int x, int y, int width, int height);
}
