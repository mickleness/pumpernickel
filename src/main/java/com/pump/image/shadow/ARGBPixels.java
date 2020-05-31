package com.pump.image.shadow;

import java.awt.image.BufferedImage;

import com.pump.image.pixel.BufferedImageIterator;
import com.pump.image.pixel.IntARGBConverter;

public class ARGBPixels {
	int width, height;
	int[] pixels;

	public ARGBPixels(int width, int height) {
		if (width <= 0)
			throw new IllegalArgumentException(
					"width (" + width + ") must be greater than zero");
		if (height <= 0)
			throw new IllegalArgumentException(
					"height (" + height + ") must be greater than zero");
		this.width = width;
		this.height = height;
		pixels = new int[width * height];
	}

	public ARGBPixels(BufferedImage srcImage) {
		this(srcImage.getWidth(), srcImage.getHeight());
		int[] row = new int[width];
		IntARGBConverter c = new IntARGBConverter(
				BufferedImageIterator.get(srcImage));
		int y = 0;
		while (!c.isDone()) {
			c.next(row);
			System.arraycopy(row, 0, pixels, y * width, width);
			y++;
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int[] getPixels() {
		return pixels;
	}
}
