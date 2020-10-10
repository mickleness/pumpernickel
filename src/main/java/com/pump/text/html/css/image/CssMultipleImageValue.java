package com.pump.text.html.css.image;

import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CssMultipleImageValue implements CssImageValue {

	private final String cssStr;
	protected final List<CssImageValue> images;

	public CssMultipleImageValue(String cssStr, List<CssImageValue> images) {
		this.cssStr = cssStr;
		this.images = new ArrayList<>(images);
	}

	public List<CssImageValue> getImageValues() {
		return Collections.unmodifiableList(images);
	}

	@Override
	public void paintRectangle(Graphics2D g, int x, int y, int width,
			int height) {
		for (int a = images.size() - 1; a >= 0; a--) {
			Graphics2D g2 = (Graphics2D) g.create();
			images.get(a).paintRectangle(g2, x, y, width, height);
			g2.dispose();
		}
	}

	@Override
	public String toCSSString() {
		return cssStr;
	}
}
