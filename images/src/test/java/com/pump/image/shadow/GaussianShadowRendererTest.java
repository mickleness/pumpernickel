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
package com.pump.image.shadow;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import org.junit.Test;

import junit.framework.TestCase;

public class GaussianShadowRendererTest extends TestCase {

	public static BufferedImage createTestImage() {
		BufferedImage bi = new BufferedImage(300, 100,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g.setColor(Color.white);
		g.setStroke(new BasicStroke(4));
		g.draw(new Ellipse2D.Float(-10, -10, 20, 20));
		g.draw(new Ellipse2D.Float(bi.getWidth() - 10, bi.getHeight() - 10, 20,
				20));
		g.draw(new Ellipse2D.Float(bi.getWidth() - 10, -10, 20, 20));
		g.draw(new Ellipse2D.Float(-10, bi.getHeight() - 10, 20, 20));

		// our modules don't include access to the StarPolygon class:
//		StarPolygon star = new StarPolygon(40);
//		star.setCenter(50, 50);

		Path2D star = new Path2D.Double();
		star.moveTo(50.0, 10.0);
		star.lineTo(58.934334, 37.70294);
		star.lineTo(88.04226, 37.63932);
		star.lineTo(64.456055, 54.69706);
		star.lineTo(73.51141, 82.36068);
		star.lineTo(50.0, 65.2);
		star.lineTo(26.48859, 82.36068);
		star.lineTo(35.54394, 54.69706);
		star.lineTo(11.957741, 37.63932);
		star.lineTo(41.065666, 37.70294);
		star.closePath();

		g.setColor(new Color(0x1BE7FF));
		g.fill(star);

		BufferedImage textureBI = new BufferedImage(20, 60,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = textureBI.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		for (int z = 0; z < 500; z++) {
			g2.setStroke(new BasicStroke(8));
			g2.setColor(new Color(0xFF5714));
			g2.drawLine(-100 + z * 20, 100, 100 + z * 20, -100);
			g2.setStroke(new BasicStroke(10));
			g2.setColor(new Color(0x6EEB83));
			g2.drawLine(200 - z * 20, 100, 0 - z * 20, -100);
		}
		g2.dispose();
		Rectangle r = new Rectangle(0, 0, textureBI.getWidth(),
				textureBI.getHeight());
		g.setPaint(new TexturePaint(textureBI, r));
		Shape roundRect = new RoundRectangle2D.Float(110, 10, 80, 80, 40, 40);
		g.fill(roundRect);

		return bi;
	}

	/**
	 * This tests the optimized GaussianShadowRenderer against the original
	 * (unoptimized) renderer to confirm it's a pixel-perfect replica.
	 */
	@Test
	public void testShadowImage() throws Exception {
		ShadowRenderer renderer1 = new SimpleGaussianShadowRenderer();
		ShadowRenderer renderer2 = new GaussianShadowRenderer();

		BufferedImage bi = createTestImage();

		ShadowAttributes attr = new ShadowAttributes(0, 0, 15,
				new Color(0, 0, 0, 128));
		BufferedImage result1 = renderer1.createShadow(bi,
				attr.getShadowKernelRadius(), attr.getShadowColor());
		BufferedImage result2 = renderer2.createShadow(bi,
				attr.getShadowKernelRadius(), attr.getShadowColor());

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

		String msg = equals(result1, result2, 0);
		assertTrue(msg, msg == null);
	}

	static String equals(BufferedImage bi1, BufferedImage bi2, int tolerance) {
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
				if (rgb1 != rgb2) {
					int a1 = (rgb1 >> 24) & 0xff;
					int r1 = (rgb1 >> 16) & 0xff;
					int g1 = (rgb1 >> 8) & 0xff;
					int b1 = (rgb1 >> 0) & 0xff;
					int a2 = (rgb2 >> 24) & 0xff;
					int r2 = (rgb2 >> 16) & 0xff;
					int g2 = (rgb2 >> 8) & 0xff;
					int b2 = (rgb2 >> 0) & 0xff;
					boolean failed = false;
					if (Math.abs(a1 - a2) > tolerance)
						failed = true;
					if (Math.abs(r1 - r2) > tolerance)
						failed = true;
					if (Math.abs(g1 - g2) > tolerance)
						failed = true;
					if (Math.abs(b1 - b2) > tolerance)
						failed = true;
					if (failed)
						return "colors: " + toString(rgb1) + " != "
								+ toString(rgb2) + " at " + x + ", " + y;
				}
			}
		}
		return null;
	}

	static String toString(int rgb) {
		String s = Integer.toHexString(rgb);
		while (s.length() < 8) {
			s = "0" + s;
		}
		return "0x" + s;
	}
}