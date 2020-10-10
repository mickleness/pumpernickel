package com.pump.text.html.css.image;

import java.awt.Graphics2D;

import com.pump.text.html.css.CssValue;

public interface CssImageValue extends CssValue {

	public void paintRectangle(Graphics2D g, int x, int y, int width,
			int height);
}
