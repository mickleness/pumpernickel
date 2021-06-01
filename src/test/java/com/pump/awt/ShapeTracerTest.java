package com.pump.awt;

import java.awt.AlphaComposite;
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

/**
 * These makes sure the ShapeTracer correctly outlines several different shapes
 * with pixel precision.
 */
public class ShapeTracerTest extends TestCase {

	static Font font = new Font("sansserif", 0, 12);
	static boolean writeFiles = true;

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
	 * Test every possible 3x3 configuration (512 total).
	 */
	@Test
	public void test3x3Grids() throws IOException {
		int[] colors = new int[] { 0, 0xff000000 };
		for (int i0 : colors) {
			for (int i1 : colors) {
				for (int i2 : colors) {
					for (int i3 : colors) {
						for (int i4 : colors) {
							for (int i5 : colors) {
								for (int i6 : colors) {
									for (int i7 : colors) {
										for (int i8 : colors) {

											String id = "3x3-";
											id += (i0 == 0 ? "0" : "1");
											id += (i1 == 0 ? "0" : "1");
											id += (i2 == 0 ? "0" : "1");
											id += (i3 == 0 ? "0" : "1");
											id += (i4 == 0 ? "0" : "1");
											id += (i5 == 0 ? "0" : "1");
											id += (i6 == 0 ? "0" : "1");
											id += (i7 == 0 ? "0" : "1");
											id += (i8 == 0 ? "0" : "1");

											BufferedImage bi = new BufferedImage(
													3, 3,
													BufferedImage.TYPE_INT_ARGB);
											bi.setRGB(0, 0, i0);
											bi.setRGB(1, 0, i1);
											bi.setRGB(2, 0, i2);
											bi.setRGB(0, 1, i3);
											bi.setRGB(1, 1, i4);
											bi.setRGB(2, 1, i5);
											bi.setRGB(0, 2, i6);
											bi.setRGB(1, 2, i7);
											bi.setRGB(2, 2, i8);

											testImage(bi, id);
										}
									}
								}
							}
						}
					}
				}
			}
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
		g.setComposite(AlphaComposite.Xor);
		g.drawImage(copy, 0, 0, null);
		g.dispose();

		BufferedImage comparison = new BufferedImage(bi.getWidth() * 3,
				bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
		g = comparison.createGraphics();
		g.drawImage(bi, 0, 0, null);
		g.drawImage(copy, copy.getWidth(), 0, null);
		g.drawImage(overlap, 2 * overlap.getWidth(), 0, null);
		g.dispose();

		try {
			assertEmpty(overlap, id);
		} catch (AssertionFailedError e) {
			if (writeFiles) {
				File dir = new File("ShapeTracerTest");
				if (!dir.exists())
					dir.mkdir();

				File file = new File(dir, id + ".png");
				ImageIO.write(comparison, "png", file);
				System.out.println("Wrote " + file.getAbsolutePath());
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

	private void assertEmpty(BufferedImage bi, String id) {
		assertEquals(BufferedImage.TYPE_INT_ARGB, bi.getType());

		int[] row = new int[bi.getWidth()];
		for (int y = 0; y < bi.getHeight(); y++) {
			bi.getRaster().getDataElements(0, y, row.length, 1, row);
			for (int x = 0; x < row.length; x++) {
				int argb = row[x];
				int alpha = (argb >> 24) & 0xff;
				if (alpha == 0)
					continue;
				fail("id \"" + id + "\", (" + x + ", " + y
						+ "), expected empty pixel but was 0x"
						+ Integer.toHexString(argb));
			}
		}
	}
}