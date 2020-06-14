package com.pump.image.shadow;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import org.junit.Test;

import com.pump.showcase.ShadowRendererDemo;
import com.pump.showcase.ShadowRendererDemo.OriginalGaussianShadowRenderer;

import junit.framework.TestCase;

public class GaussianShadowRendererTest extends TestCase {

	/**
	 * This tests the optimized GaussianShadowRenderer against the original
	 * (unoptimized) renderer to confirm it's a pixel-perfect replica.
	 */
	@Test
	public void testShadowImage() throws Exception {
		ShadowRenderer renderer1 = new OriginalGaussianShadowRenderer();
		ShadowRenderer renderer2 = new GaussianShadowRenderer();

		BufferedImage bi = ShadowRendererDemo.createTestImage();

		ShadowAttributes attr = new ShadowAttributes(15, .5f);
		BufferedImage result1 = renderer1.createShadow(bi, attr);
		BufferedImage result2 = renderer2.createShadow(bi, attr);

		// if zeroes are trimmed off the optimized kernel, the images
		// may show the same thing but be padded differently
		if (result2.getWidth() < result1.getWidth()) {
			BufferedImage resized = new BufferedImage(result1.getWidth(),
					result1.getHeight(), result2.getType());
			Graphics2D g = resized.createGraphics();
			int dx = (result1.getWidth() - result2.getWidth()) / 2;
			int dy = (result1.getHeight() - result2.getHeight()) / 2;
			g.drawImage(result2, dx, dy, null);
			g.dispose();
			result2 = resized;
		}

		String msg = equals(result1, result2);
		assertTrue(msg, msg == null);
	}

	private String equals(BufferedImage bi1, BufferedImage bi2) {
		if (bi1.getType() != bi2.getType())
			return "types: " + bi1.getType() + " != " + bi2.getType();
		if (bi1.getWidth() != bi2.getWidth())
			return "widths: " + bi1.getWidth() + " != " + bi2.getWidth();
		if (bi1.getHeight() != bi2.getHeight())
			return "heights: " + bi1.getHeight() + " != " + bi2.getHeight();
		for (int y = 0; y < bi1.getHeight(); y++) {
			for (int x = 0; x < bi1.getWidth(); x++) {
				int rgb1 = bi1.getRGB(x, y);
				int rgb2 = bi2.getRGB(x, y);
				if (rgb1 != rgb2)
					return "colors: " + toString(rgb1) + " != " + toString(rgb2)
							+ " at " + x + ", " + y;
			}
		}
		return null;
	}

	private String toString(int rgb) {
		String s = Integer.toHexString(rgb);
		while (s.length() < 8) {
			s = "0" + s;
		}
		return "0x" + s;
	}
}
