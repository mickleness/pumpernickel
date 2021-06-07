package com.pump.graphics.vector;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.TexturePaint;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.AttributedString;
import java.util.Random;

import javax.imageio.ImageIO;

import org.junit.Test;

import com.pump.awt.BristleStroke;
import com.pump.awt.BrushStroke;
import com.pump.awt.CalligraphyStroke;
import com.pump.awt.CharcoalStroke;
import com.pump.awt.TransformedTexturePaint;
import com.pump.graphics.DualGraphics2D;

import junit.framework.TestCase;

/**
 * This tests the VectorImage and all known Operations for serialization and
 * visual accuracy.
 */
public class VectorImageTest extends TestCase {
	static int imageFileCtr = 0;

	/**
	 * This debugging options writes image files for developers to inspect.
	 * (It's useful to double-check that the graphics are generally what we
	 * expect and that we aren't, for example, writing blank images.)
	 */
	static boolean writeImageFiles = false;

	abstract class RenderTest {
		public void test() throws Exception {
			BufferedImage bi = new BufferedImage(width, height,
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D big = bi.createGraphics();
			VectorImage img = new VectorImage();
			VectorGraphics2D vg = img.createGraphics();
			DualGraphics2D g = new DualGraphics2D(big, vg);

			paint(g);
			g.dispose();

			BufferedImage bi2 = new BufferedImage(width, height,
					BufferedImage.TYPE_INT_ARGB);
			img.paint(bi2.createGraphics());

			if (writeImageFiles) {
				File f1 = new File("img" + (imageFileCtr++) + ".png");
				File f2 = new File("img" + (imageFileCtr++) + ".png");
				ImageIO.write(bi, "png", f1);
				ImageIO.write(bi2, "png", f2);
				System.out.println("Wrote " + f1.getAbsolutePath());
				System.out.println("Wrote " + f2.getAbsolutePath());
			}

			assertImageEquals(bi, bi2);

			byte[] bytes;
			try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream()) {
				try (ObjectOutputStream objOut = new ObjectOutputStream(
						byteOut)) {
					objOut.writeObject(img);
				}
				bytes = byteOut.toByteArray();
			}
			try (ByteArrayInputStream byteIn = new ByteArrayInputStream(
					bytes)) {
				try (ObjectInputStream objIn = new ObjectInputStream(byteIn)) {
					VectorImage copy = (VectorImage) objIn.readObject();

					BufferedImage bi3 = new BufferedImage(width, height,
							BufferedImage.TYPE_INT_ARGB);
					copy.paint(bi3.createGraphics());

					if (writeImageFiles) {
						File f3 = new File("img" + (imageFileCtr++) + ".png");
						ImageIO.write(bi3, "png", f3);
						System.out.println("Wrote " + f3.getAbsolutePath());
					}

					assertImageEquals(bi, bi3);

					for (int a = 0; a < copy.getOperations().size(); a++) {
						Operation op1 = vg.getOperations().get(a);
						Operation op2 = copy.getOperations().get(a);
						String msg = "a = " + a + " " + op1.getClass();

						assertTrue(msg, op1.equals(op2));

						assertEquals(msg, op1.hashCode(), op2.hashCode());

						for (int b = 0; b < copy.getOperations().size(); b++) {
							if (a != b) {
								Operation op1b = vg.getOperations().get(b);
								Operation op2b = copy.getOperations().get(b);
								assertFalse("a = " + a + " b = " + b,
										op1.equals(op1b));
								assertFalse("a = " + a + " b = " + b,
										op2.equals(op1b));
								assertFalse("a = " + a + " b = " + b,
										op1.equals(op2b));
								assertFalse("a = " + a + " b = " + b,
										op2.equals(op2b));
							}
						}
					}
				}
			}
		}

		public abstract void paint(Graphics2D g);
	}

	int width = 200;
	int height = 200;

	/**
	 * This includes two ovals with unique fills.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testOvals() throws Exception {
		RenderTest t = new RenderTest() {

			@Override
			public void paint(Graphics2D g) {
				g.setColor(Color.orange);
				g.fillOval(width / 5, height / 4, width / 2, height / 2);
				g.setColor(new Color(0, 255, 40, 100));
				g.fillOval(width * 2 / 5, height * 1 / 4, width / 2,
						height / 2);
			}

		};
		t.test();
	}

	/**
	 * This includes lines, a couple of simple BasicStrokes, and a DST_OVER
	 * AlphaComposite.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testLines() throws Exception {
		RenderTest t = new RenderTest() {

			@Override
			public void paint(Graphics2D g) {
				g.setColor(Color.orange);
				g.setStroke(new BasicStroke(13f));
				g.drawLine(50, 3, 89, 20);
				g.setColor(Color.cyan);
				g.setComposite(AlphaComposite
						.getInstance(AlphaComposite.DST_OVER, .8f));
				g.setStroke(new BasicStroke(3f, BasicStroke.CAP_SQUARE,
						BasicStroke.JOIN_ROUND));
				g.drawLine(123, 187, 40, 14);
			}

		};
		t.test();
	}

	/**
	 * This includes polylines, a rounded stroke, antialiasing, and xor mode.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testPolyline() throws Exception {
		RenderTest t = new RenderTest() {

			@Override
			public void paint(Graphics2D g) {
				g.setColor(Color.red);
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				int[] xPoints = new int[] { 0, 100, 50, 25 };
				int[] yPoints = new int[] { 100, 100, 0, 50 };
				g.setStroke(new BasicStroke(3f, BasicStroke.CAP_BUTT,
						BasicStroke.JOIN_ROUND));
				g.drawPolyline(xPoints, yPoints, xPoints.length);
				g.translate(20, 5);
				g.setPaint(Color.green);
				g.drawPolyline(xPoints, yPoints, xPoints.length);
				g.translate(-10, -3);
				g.setXORMode(Color.blue);
				g.drawPolyline(xPoints, yPoints, xPoints.length);
			}

		};
		t.test();
	}

	/**
	 * This tests clipping different Graphics2Ds, drawing arcs, a SrcOut
	 * composite to clear pixels.
	 */
	public void testContext1() throws Exception {
		RenderTest t = new RenderTest() {

			@Override
			public void paint(Graphics2D g) {
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				g.setStroke(new BasicStroke(15));

				g.clipRect(20, 20, 160, 160);
				g.setColor(Color.cyan);
				g.fillRect(0, 0, 200, 200);

				Graphics2D g2 = (Graphics2D) g.create(40, 40, 100, 100);
				g2.setColor(Color.pink);
				g2.drawArc(20, 20, 120, 120, 0, 190);

				Graphics2D g3 = (Graphics2D) g.create(60, 0, 100, 100);
				g3.setComposite(AlphaComposite.SrcOut);
				g3.setColor(Color.magenta);
				g3.drawArc(10, 10, 120, 120, 180, 340);
			}

		};
		t.test();
	}

	/**
	 * This tests clipping different Graphics2Ds, drawing arcs, a SrcOut
	 * composite to clear pixels.
	 */
	public void testContext2() throws Exception {
		RenderTest t = new RenderTest() {

			@Override
			public void paint(Graphics2D g) {
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				g.setStroke(new BasicStroke(15));
				g.setColor(Color.darkGray);
				g.drawLine(10, 10, 20, 20);
				g.scale(1.3, 1.3);
				g.drawLine(20, 10, 10, 20);
			}

		};
		t.test();
	}

	static class Triangle {
		Path2D shape;

		public Triangle(int x, int y, int w, int h) {
			shape = new Path2D.Float();
			shape.moveTo(x, y + h);
			shape.lineTo(x + w / 2, y);
			shape.lineTo(x + w, y + h);
			shape.closePath();
		}
	}

	/**
	 * This tests transformed clipping
	 */
	public void testContext_transformedClipping() throws Exception {
		RenderTest t = new RenderTest() {

			@Override
			public void paint(Graphics2D g) {
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				g.setStroke(new BasicStroke(2));
				g.scale(.75, .75);
				g.clip(new Ellipse2D.Float(0, 0, 100, 100));
				g.setColor(new Color(255, 0, 0, 100));
				g.fillRect(0, 0, 200, 200);
				g.scale(2, 2);
				g.clip(new Triangle(0, 0, 100, 100).shape);
				g.setColor(new Color(0, 0, 100, 100));
				g.fillRect(0, 0, 200, 200);
			}

		};
		t.test();

		// reverse order of clipping
		RenderTest t2 = new RenderTest() {

			@Override
			public void paint(Graphics2D g) {
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				g.setStroke(new BasicStroke(2));
				g.scale(.75, .75);
				g.clip(new Triangle(0, 0, 100, 100).shape);
				g.setColor(new Color(255, 0, 0, 100));
				g.fillRect(0, 0, 200, 200);
				g.scale(2, 2);
				g.clip(new Ellipse2D.Float(0, 0, 100, 100));
				g.setColor(new Color(0, 0, 100, 100));
				g.fillRect(0, 0, 200, 200);
			}

		};
		t2.test();

		// replace clipping (instead of adding to it)

		RenderTest t3 = new RenderTest() {

			@Override
			public void paint(Graphics2D g) {
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				g.setStroke(new BasicStroke(2));
				g.scale(.75, .75);
				g.clip(new Ellipse2D.Float(0, 0, 100, 100));
				g.setColor(new Color(255, 0, 0, 100));
				g.fillRect(0, 0, 200, 200);
				g.scale(2, 2);
				g.setClip(new Triangle(0, 0, 100, 100).shape);
				g.setColor(new Color(0, 0, 100, 100));
				g.fillRect(0, 0, 200, 200);
			}

		};
		t3.test();

		// reverse order of clipping
		RenderTest t4 = new RenderTest() {

			@Override
			public void paint(Graphics2D g) {
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				g.setStroke(new BasicStroke(2));
				g.scale(.75, .75);
				g.clip(new Triangle(0, 0, 100, 100).shape);
				g.setColor(new Color(255, 0, 0, 100));
				g.fillRect(0, 0, 200, 200);
				g.scale(2, 2);
				g.setClip(new Ellipse2D.Float(0, 0, 100, 100));
				g.setColor(new Color(0, 0, 100, 100));
				g.fillRect(0, 0, 200, 200);
			}

		};
		t4.test();
	}

	public static TexturePaint getCheckerBoard(int checkerSize, Color color1,
			Color color2) {
		BufferedImage bi = new BufferedImage(2 * checkerSize, 2 * checkerSize,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bi.createGraphics();
		g.setColor(color1);
		g.fillRect(0, 0, 2 * checkerSize, 2 * checkerSize);
		g.setColor(color2);
		g.fillRect(0, 0, checkerSize, checkerSize);
		g.fillRect(checkerSize, checkerSize, checkerSize, checkerSize);
		g.dispose();
		return new TexturePaint(bi,
				new Rectangle(0, 0, bi.getWidth(), bi.getHeight()));
	}

	/**
	 * This tests transformed Paints
	 * 
	 * @throws Exception
	 */
	public void testContext_transformedPaint() throws Exception {
		RenderTest t = new RenderTest() {

			@Override
			public void paint(Graphics2D g) {
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				g.setStroke(new BasicStroke(2));
				g.scale(.75, .75);
				g.setPaint(getCheckerBoard(6, Color.white, Color.lightGray));
				g.fill(new Ellipse2D.Float(0, 0, 100, 100));
				g.scale(2, 2);
				g.setPaint(new GradientPaint(0, 0, Color.green, 0, 15,
						Color.blue, true));
				g.fill(new Triangle(0, 0, 100, 100).shape);
			}

		};
		t.test();

		// reverse shapes
		RenderTest t2 = new RenderTest() {

			@Override
			public void paint(Graphics2D g) {
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				g.setStroke(new BasicStroke(2));
				g.scale(.75, .75);
				g.setPaint(getCheckerBoard(6, Color.white, Color.lightGray));
				g.fill(new Triangle(0, 0, 100, 100).shape);
				g.scale(2, 2);
				g.setPaint(new GradientPaint(0, 0, Color.green, 0, 15,
						Color.blue, true));
				g.fill(new Ellipse2D.Float(0, 0, 100, 100));
			}

		};
		t2.test();
	}

	public void testClear() throws Exception {
		RenderTest t2 = new RenderTest() {

			@Override
			public void paint(Graphics2D g) {
				g.setPaint(Color.red);
				g.fill(new Triangle(0, 0, 100, 100).shape);
				g.clearRect(25, 25, 50, 50);
			}

		};
		t2.test();
	}

	public void testCopyArea() throws Exception {
		RenderTest t2 = new RenderTest() {

			@Override
			public void paint(Graphics2D g) {
				g.setPaint(Color.red);
				g.fill(new Triangle(0, 0, 100, 100).shape);
				g.copyArea(25, 25, 50, 50, 30, 30);
			}

		};
		t2.test();
	}

	/**
	 * This tests several different drawImage methods.
	 */
	public void testDrawImage() throws Exception {
		final BufferedImage bi = new BufferedImage(100, 100,
				BufferedImage.TYPE_INT_ARGB);
		Random r = new Random(0);
		for (int y = 0; y < bi.getHeight(); y++) {
			for (int x = 0; x < bi.getWidth(); x++) {
				int rgb = r.nextInt(0xffffff);
				int alpha = 255 * x / bi.getWidth();
				int argb = (alpha << 24) + rgb;
				bi.setRGB(x, y, argb);
			}
		}

		RenderTest t1 = new RenderTest() {

			@Override
			public void paint(Graphics2D g) {
				g.drawImage(bi, 10, 10, Color.green, null);
			}

		};
		t1.test();

		RenderTest t2 = new RenderTest() {

			@Override
			public void paint(Graphics2D g) {
				g.drawImage(bi, 10, 10, null);
			}

		};
		t2.test();

		RenderTest t3 = new RenderTest() {

			@Override
			public void paint(Graphics2D g) {
				g.drawImage(bi, 10, 10, 50, 50, null);
			}

		};
		t3.test();

		RenderTest t4 = new RenderTest() {

			@Override
			public void paint(Graphics2D g) {
				g.drawImage(bi, 10, 10, 50, 50, Color.cyan, null);
			}

		};
		t4.test();

		RenderTest t5 = new RenderTest() {

			@Override
			public void paint(Graphics2D g) {
				g.drawImage(bi, 110, 110, 150, 150, 20, 20, 50, 50, null);
			}

		};
		t5.test();

		RenderTest t6 = new RenderTest() {

			@Override
			public void paint(Graphics2D g) {
				g.drawImage(bi, 110, 110, 150, 150, 20, 20, 50, 50, Color.red,
						null);
			}

		};
		t6.test();

		RenderTest tx = new RenderTest() {

			@Override
			public void paint(Graphics2D g) {
				AffineTransform tx = AffineTransform.getRotateInstance(.35, 40,
						40);
				g.drawImage(bi, tx, null);
			}

		};
		tx.test();
	}

	public void testDrawString() throws Exception {
		RenderTest t2 = new RenderTest() {

			@Override
			public void paint(Graphics2D g) {
				g.setPaint(Color.red);
				g.setFont(new Font("Dialog", 0, 14));
				g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
						RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
						RenderingHints.VALUE_FRACTIONALMETRICS_ON);
				g.transform(AffineTransform.getShearInstance(0, .1));
				g.drawString("Shakespeare in the park", 50, 50);
			}

		};
		t2.test();
	}

	public void testDrawPlainAttributedString() throws Exception {
		RenderTest t2 = new RenderTest() {

			@Override
			public void paint(Graphics2D g) {
				g.setPaint(Color.DARK_GRAY);
				g.setFont(new Font("Dialog", 0, 14));
				g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
						RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
						RenderingHints.VALUE_FRACTIONALMETRICS_ON);

				AttributedString attrStr = new AttributedString("plain string");
				g.drawString(attrStr.getIterator(), 50, 50);
			}

		};
		t2.test();
	}

	public void testDrawAttributedString() throws Exception {
		RenderTest t2 = new RenderTest() {

			@Override
			public void paint(Graphics2D g) {
				g.setPaint(Color.DARK_GRAY);
				g.setFont(new Font("Dialog", 0, 14));
				g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
						RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
						RenderingHints.VALUE_FRACTIONALMETRICS_ON);

				AttributedString attrStr = createAttributedString(
						new Font("Georgia", 0, 15));
				g.drawString(attrStr.getIterator(), 50, 50);
			}

		};
		t2.test();
	}

	private AttributedString createAttributedString(Font font) {
		AttributedString attrStr = new AttributedString("¿was it a rat I saw?");
		attrStr.addAttribute(TextAttribute.FONT, font, 1, 4);
		attrStr.addAttribute(TextAttribute.BACKGROUND, Color.cyan, 5, 7);
		attrStr.addAttribute(TextAttribute.FOREGROUND, Color.orange, 0, 1);
		Paint gradient = new GradientPaint(0, 0, Color.red, 0, 3, Color.yellow,
				true);
		attrStr.addAttribute(TextAttribute.FOREGROUND, gradient, 19, 20);
		attrStr.addAttribute(TextAttribute.UNDERLINE, 4, 10, 13);
		return attrStr;
	}

	public void testDrawGlyphVector() throws Exception {
		RenderTest t2 = new RenderTest() {

			@Override
			public void paint(Graphics2D g) {
				g.setPaint(Color.DARK_GRAY);
				g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
						RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
						RenderingHints.VALUE_FRACTIONALMETRICS_ON);

				// include different styles so we'll test serializing font
				// postures/weights:
				int dy = 0;
				for (boolean bold : new boolean[] { false, true }) {
					for (boolean italic : new boolean[] { false, true }) {
						int style = 0;
						if (bold)
							style += Font.BOLD;
						if (italic)
							style += Font.ITALIC;
						AttributedString attrStr = createAttributedString(
								new Font("Dialog", style, 14));
						TextLayout layout = new TextLayout(
								attrStr.getIterator(),
								g.getFontRenderContext());
						layout.draw(g, 10, 90 + dy);
						dy += 20;
					}
				}
			}

		};
		t2.test();
	}

	/**
	 * This tests custom java.awt.Strokes. Primarily this tests their
	 * serialization -- which is something they can have their own unique unit
	 * tests for. But it also tests the VectorGraphics2D ability to handle
	 * custom strokes.
	 * 
	 * @throws Exception
	 */
	public void testCustomStrokes() throws Exception {
		Stroke[] strokes = new Stroke[] { new BristleStroke(5, .5f),
				new BrushStroke(5, .5f), new CalligraphyStroke(5, .7857f),
				new CharcoalStroke(new CalligraphyStroke(4, .2f), .5f, 2, 0),
				new CharcoalStroke(new BasicStroke(5f), .5f, 2, 0) };
		for (Stroke stroke : strokes) {
			final Stroke fStroke = stroke;
			RenderTest t2 = new RenderTest() {

				@Override
				public void paint(Graphics2D g) {
					g.setPaint(Color.DARK_GRAY);
					g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
							RenderingHints.VALUE_ANTIALIAS_ON);
					g.setStroke(fStroke);
					g.draw(new Triangle(10, 10, 120, 120).shape);
				}

			};
			t2.test();
		}
	}

	public void testCustomPaints() throws Exception {
		Paint[] paints = new Paint[] { new TransformedTexturePaint(
				getCheckerBoard(8, Color.gray, Color.white),
				AffineTransform.getRotateInstance(.34f)) };
		for (Paint paint : paints) {
			final Paint fPaint = paint;
			RenderTest t2 = new RenderTest() {

				@Override
				public void paint(Graphics2D g) {
					g.setPaint(fPaint);
					g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
							RenderingHints.VALUE_ANTIALIAS_ON);
					g.setRenderingHint(RenderingHints.KEY_RENDERING,
							RenderingHints.VALUE_RENDER_QUALITY);
					g.fill(new Triangle(10, 10, 120, 120).shape);
				}

			};
			t2.test();
		}
	}

	public void assertImageEquals(BufferedImage bi1, BufferedImage bi2) {
		assertEquals(bi1.getWidth(), bi2.getWidth());
		assertEquals(bi1.getHeight(), bi2.getHeight());

		assertEquals(bi1.getType(), BufferedImage.TYPE_INT_ARGB);
		assertEquals(bi2.getType(), BufferedImage.TYPE_INT_ARGB);

		int[] row1 = new int[bi1.getWidth()];
		int[] row2 = new int[bi1.getWidth()];
		for (int y = 0; y < bi1.getHeight(); y++) {
			bi1.getRaster().getDataElements(0, y, row1.length, 1, row1);
			bi2.getRaster().getDataElements(0, y, row1.length, 1, row2);
			for (int x = 0; x < bi1.getWidth(); x++) {
				String hex1 = Integer.toHexString(row1[x]);
				String hex2 = Integer.toHexString(row2[x]);
				assertEquals("x = " + x + ", y = " + y, hex1, hex2);
			}
		}
	}
}
