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
package com.pump.text.html.css.image;

import java.awt.Graphics2D;

import com.pump.text.html.css.AbstractCssValue;
import com.pump.text.html.view.QViewHelper;

/**
 * This CssImageValue renders nothing.
 *
 */
public class CssNoneImageValue extends AbstractCssValue
		implements CssImageValue {

	@Override
	public String toCSSString() {
		return "none";
	}

	@Override
	public void paintRectangle(Graphics2D g, QViewHelper viewHelper,
			int layerIndex, int x, int y, int width, int height) {
		// intentionally empty
	}

}