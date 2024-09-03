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
package com.pump.graphics.vector;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Objects;

import com.pump.awt.Paintable;

/**
 * This paints a VectorImage and translates it so the top-left corner is at
 * (0,0).
 */
public class VectorImagePaintable implements Paintable {
	protected final VectorImage vectorImage;
	private Rectangle bounds;

	public VectorImagePaintable(VectorImage vectorImage) {
		Objects.requireNonNull(vectorImage);
		this.vectorImage = vectorImage;
		bounds = vectorImage.getBounds().getBounds();
	}

	@Override
	public int getWidth() {
		return bounds.width;
	}

	@Override
	public int getHeight() {
		return bounds.height;
	}

	@Override
	public void paint(Graphics2D g) {
		g = (Graphics2D) g.create();
		g.translate(-bounds.x, -bounds.y);
		vectorImage.paint(g);
		g.dispose();
	}
}