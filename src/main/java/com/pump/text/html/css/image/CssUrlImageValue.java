package com.pump.text.html.css.image;

import java.awt.Graphics2D;
import java.util.Objects;

public class CssUrlImageValue implements CssImageValue {
	private final String url, cssStr;

	public CssUrlImageValue(String cssStr, String url) {
		Objects.requireNonNull(cssStr);
		this.cssStr = cssStr;
		this.url = url;
		// TODO: also support data URI's
		// https://css-tricks.com/data-uris/
	}

	@Override
	public void paintRectangle(Graphics2D g, int x, int y, int width,
			int height) {

		// TODO Auto-generated method stub

	}

	@Override
	public String toCSSString() {
		return cssStr;
	}

}