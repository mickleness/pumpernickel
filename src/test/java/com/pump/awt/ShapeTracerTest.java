package com.pump.awt;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.junit.Test;

import com.pump.geom.ShapeBounds;
import com.pump.geom.TransformUtils;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

public class ShapeTracerTest extends TestCase {

	Font font = new Font("sansserif", 0, 12);
	boolean writeFiles = true;

	/**
	 * Test that the ShapeTracer can identify the outline of a simple black
	 * square.
	 */
	public void testSquare() throws Exception {
		BufferedImage bi = new BufferedImage(10, 10,
				BufferedImage.TYPE_INT_ARGB);
		for (int y = 4; y <= 7; y++) {
			for (int x = 4; x <= 8; x++) {
				bi.setRGB(x, y, 0xff000000);
			}
		}
		testImage(bi, "small square");
	}

	/**
	 * Test that the ShapeTracer can identify the outlines of nearly 90 common
	 * English glyphs.
	 */
	@Test
	public void testGlyphs() throws Exception {
		String glyphs = "ABCDEFGHIJKLMNOPQRSTVUWXYZabcdefghijklmnopqrstuvwxyz01234567890,.<>?\":|}{!2#$%^&*()_~`";
		for (char ch : glyphs.toCharArray()) {
			BufferedImage bi = new BufferedImage(200, 200,
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = bi.createGraphics();
			GlyphVector gv = font.createGlyphVector(g.getFontRenderContext(),
					new char[] { ch });
			Shape outline = gv.getGlyphOutline(0);
			AffineTransform tx = TransformUtils.createAffineTransform(
					ShapeBounds.getBounds(outline),
					new Rectangle(1, 1, 200 - 2, 200 - 2));
			outline = tx.createTransformedShape(outline);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_OFF);
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
					RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			g.setColor(Color.black);
			g.fill(outline);
			g.dispose();

			testImage(bi, "char-" + ch);
		}
	}

	/**
	 * Test that the ShapeTracer can scan the pixels in the incoming image and
	 * produces the appropriate shape.
	 * 
	 * @param bi
	 *            an image that contains only black and transparent pixels
	 * @param id
	 *            a human-readable String for debugging identification
	 */
	private void testImage(BufferedImage bi, String id) throws IOException {
		// this is basic bounds checking
		assertSolidColor(0x000000, bi, id + "-base");

		ShapeTracer tracer = new ShapeTracer();
		Shape shape = tracer.trace(bi);

		BufferedImage copy = new BufferedImage(bi.getWidth(), bi.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = copy.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.black);
		g.fill(shape);
		g.dispose();

		BufferedImage overlap = new BufferedImage(bi.getWidth(), bi.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		g = overlap.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.drawImage(bi, 0, 0, null);
		g.setXORMode(Color.red);
		g.fill(shape);
		g.dispose();

		BufferedImage comparison = new BufferedImage(bi.getWidth() * 3,
				bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
		g = comparison.createGraphics();
		g.drawImage(bi, 0, 0, null);
		g.drawImage(copy, copy.getWidth(), 0, null);
		g.drawImage(overlap, 2 * overlap.getWidth(), 0, null);
		g.dispose();

		try {
			assertSolidColor(0x00FFFF, overlap, id);
		} catch (AssertionFailedError e) {
			if (writeFiles) {
				File dir = new File("ShapeTracerTest");
				if (!dir.exists())
					dir.mkdir();

				File file = new File(dir, id + ".png");
				ImageIO.write(comparison, "png", file);
			}
			throw e;
		}
	}

	private void assertSolidColor(int expectedRGB, BufferedImage bi,
			String id) {
		assertEquals(BufferedImage.TYPE_INT_ARGB, bi.getType());

		int[] row = new int[bi.getWidth()];
		for (int y = 0; y < bi.getHeight(); y++) {
			bi.getRaster().getDataElements(0, y, row.length, 1, row);
			for (int x = 0; x < row.length; x++) {
				int argb = row[x];
				int alpha = (argb >> 24) & 0xff;
				if (alpha == 0)
					continue;
				int actualRGB = argb & 0xffffff;
				assertEquals(
						"id \"" + id + "\", (" + x + ", " + y + "), expected 0x"
								+ Integer.toHexString(expectedRGB)
								+ ", found 0x" + Integer.toHexString(actualRGB),
						expectedRGB, actualRGB);
			}
		}
	}
}