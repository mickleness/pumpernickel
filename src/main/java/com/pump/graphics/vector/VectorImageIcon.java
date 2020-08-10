package com.pump.graphics.vector;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Objects;

import javax.swing.Icon;

/**
 * This Icon renders a VectorImage.
 */
public class VectorImageIcon implements Icon {
	protected VectorImage image;

	public VectorImageIcon(VectorImage image) {
		Objects.requireNonNull(image);
		this.image = image;
	}

	public VectorImage getImage() {
		return image;
	}

	@Override
	public void paintIcon(Component c, Graphics g, int x, int y) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.translate(x, y);
		g2.clipRect(0, 0, getIconWidth(), getIconHeight());
		image.paint(g2);
	}

	@Override
	public int getIconWidth() {
		return (int) (image.getBounds().getWidth() + .5);
	}

	@Override
	public int getIconHeight() {
		return (int) (image.getBounds().getHeight() + .5);
	}

}
