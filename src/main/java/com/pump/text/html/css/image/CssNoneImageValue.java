package com.pump.text.html.css.image;

import java.awt.Graphics2D;

import javax.swing.text.View;

/**
 * This CssImageValue renders nothing.
 *
 */
public class CssNoneImageValue implements CssImageValue {

	@Override
	public String toCSSString() {
		return "none";
	}

	@Override
	public void paintRectangle(Graphics2D g, View view, int x, int y, int width,
			int height) {
		// intentionally empty
	}

}
