/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://raw.githubusercontent.com/mickleness/pumpernickel/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.graphics.vector;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.font.TextAttribute;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.AttributedString;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import junit.framework.TestCase;

/**
 * This tests the {@link Operation#toSoftClipOperation(Shape)} method for all
 * known Operations.
 */
public class SoftClipTest extends TestCase {
	private static final Dimension SIZE = new Dimension(100, 100);
	private static final Ellipse2D CIRCLE = new Ellipse2D.Float(0, 0,
			SIZE.width, SIZE.height);
	private static final Path2D TRIANGLE = createTriangle(0, 0, SIZE.width,
			SIZE.height);
	private static final Color[] COLORS = new Color[] { new Color(0x33a8c7),
			new Color(0x52e3e1), new Color(0xa0e426), new Color(0xfdf148),
			new Color(0xffab00), new Color(0xf77976), new Color(0xf050ae),
			new Color(0xd883ff), new Color(0x9336fd) };

	private static Path2D createTriangle(int x, int y, int width, int height) {
		Path2D triangle = new Path2D.Float();
		triangle.moveTo(x, y + height);
		triangle.lineTo(x + width / 2, y);
		triangle.lineTo(x + width, y + height);
		triangle.closePath();
		return triangle;
	}

	@Test
	public void testFill() throws IOException {
		VectorImage img = new VectorImage();
		Graphics2D g = img.createGraphics();
		g.setColor(Color.blue);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.fill(TRIANGLE);

		testSoftClip(img, true, "fill");
	}

	@Test
	public void testStroke() throws IOException {
		VectorImage img = new VectorImage();
		Graphics2D g = img.createGraphics();
		g.setColor(Color.blue);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_PURE);
		g.setStroke(new BasicStroke(8, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_ROUND, 5, new float[] { 5, 10, 2 }, 0));
		g.draw(TRIANGLE);

		testSoftClip(img, true, "stroke");
	}

	@Test
	public void testImage() throws IOException {
		VectorImage img = new VectorImage();
		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.drawImage(createBlueSquare(), 0, 0, null);

		testSoftClip(img, true, "image");
	}

	@Test
	public void testRenderedImage() throws IOException {
		VectorImage img = new VectorImage();
		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		AffineTransform tx = new AffineTransform();
		tx.translate(SIZE.width / 2, SIZE.height / 2);
		tx.rotate(Math.PI / 4);
		tx.scale(2, 2);
		tx.translate(-SIZE.width / 2, -SIZE.height / 2);
		g.drawRenderedImage(createOpaqueRandomShapes(), tx);

		testSoftClip(img, true, "renderedImage");
	}

	@Test
	public void testString() throws IOException {
		VectorImage img = new VectorImage();
		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		g.setFont(new Font("Arial", Font.BOLD, 48));
		Random random = new Random(0);
		for (int a = 0; a < 200; a++) {
			g.setColor(COLORS[random.nextInt(COLORS.length)]);
			String str = Character.toString('A' + random.nextInt(26));
			int x = random.nextInt(100) - 10;
			int y = random.nextInt(100) + 10;
			g.drawString(str, x, y);
		}

		testSoftClip(img, false, "string");
	}

	@Test
	public void testGlyphVector() throws IOException {
		VectorImage img = new VectorImage();
		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		g.setFont(new Font("Arial", Font.BOLD, 48));
		g.scale(2, 2);
		Random random = new Random(0);
		for (int a = 0; a < 200; a++) {
			g.setColor(COLORS[random.nextInt(COLORS.length)]);
			String str = Character.toString('A' + random.nextInt(26));
			str += Character.toString('a' + random.nextInt(26));
			str += Character.toString('0' + random.nextInt(10));
			int x = random.nextInt(100) - 10;
			int y = random.nextInt(100) + 10;
			GlyphVector gv = g.getFont()
					.createGlyphVector(g.getFontRenderContext(), str);
			g.drawGlyphVector(gv, x, y);
		}

		testSoftClip(img, false, "glyphVector");
	}

	@Test
	public void testAttributedCharacterIterator() throws IOException {
		VectorImage img = new VectorImage();
		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);

		g.setFont(new Font("Arial", Font.BOLD, 48));
		Random random = new Random(0);
		for (int a = 0; a < 200; a++) {
			String str = Character.toString('A' + random.nextInt(26));
			str += Character.toString('a' + random.nextInt(26));
			str += Character.toString('0' + random.nextInt(10));
			int x = random.nextInt(100) - 10;
			int y = random.nextInt(100) + 10;
			AttributedString as = new AttributedString(str);
			as.addAttribute(TextAttribute.FOREGROUND,
					COLORS[random.nextInt(COLORS.length)], 0, 1);
			as.addAttribute(TextAttribute.FOREGROUND,
					COLORS[random.nextInt(COLORS.length)], 1, 2);
			as.addAttribute(TextAttribute.FOREGROUND,
					COLORS[random.nextInt(COLORS.length)], 2, 3);
			as.addAttribute(TextAttribute.FONT, g.getFont(), 0, 3);
			g.drawString(as.getIterator(), x, y);
		}

		testSoftClip(img, false, "attributedCharacterIterator");
	}

	@Test
	public void testCopyArea() throws IOException {
		VectorImage img = new VectorImage();
		Graphics2D g = img.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.drawImage(createOpaqueRandomShapes(), 0, 0, null);
		g.copyArea(0, 0, SIZE.width / 2, SIZE.height / 2, 0, SIZE.height / 2);
		g.copyArea(0, 0, SIZE.width / 2, SIZE.height / 2, SIZE.width / 2, 0);
		g.copyArea(0, 0, SIZE.width / 2, SIZE.height / 2, SIZE.width / 2,
				SIZE.height / 2);

		testSoftClip(img, false, "copyArea");
	}

	private void testSoftClip(VectorImage img,
			boolean requireOpaquePixelsAreSame, String name)
			throws IOException {
		BufferedImage bi1 = render(img, CIRCLE);
		BufferedImage bi2 = renderSoftClip(img, CIRCLE);

		// ImageIO.write(bi1, "png", new File(name + "-clip.png"));
		// ImageIO.write(bi2, "png", new File(name + "-softclip.png"));

		int[] row1 = new int[bi1.getWidth()];
		int[] row2 = new int[bi2.getWidth()];

		int antialiasedPixels = 0;
		int identicalPixels = 0;
		int totalPixels = bi1.getWidth() * bi2.getHeight();
		for (int y = 0; y < bi2.getHeight(); y++) {
			bi1.getRaster().getDataElements(0, y, bi1.getWidth(), 1, row1);
			bi2.getRaster().getDataElements(0, y, bi1.getWidth(), 1, row2);
			for (int x = 0; x < bi2.getWidth(); x++) {
				int argb1 = row1[x];
				int argb2 = row2[x];

				int alpha2 = (argb2 >> 24) & 0xff;

				if (alpha2 > 0 && alpha2 < 255) {
					antialiasedPixels++;
				}

				if (argb1 == argb2) {
					identicalPixels++;
				} else {
					boolean isEdge = CIRCLE.intersects(x, y, 1, 1);
					if (!isEdge) {
						if (alpha2 == 255 && requireOpaquePixelsAreSame)
							assertEquals("x = " + x + ", y = " + y
									+ ", rgba1 = " + toRGBA(argb1)
									+ ", rgba2 = " + toRGBA(argb2), argb1,
									argb2);
						if (alpha2 == 0)
							assertEquals("x = " + x + ", y = " + y
									+ ", rgba1 = " + toRGBA(argb1)
									+ ", rgba2 = " + toRGBA(argb2), argb1,
									argb2);
					}
				}
			}
		}
		int percentAntialiasedPixels = antialiasedPixels * 100 / totalPixels;
		assertTrue(percentAntialiasedPixels + "% antialiased pixels",
				percentAntialiasedPixels < 5);
		assertTrue(percentAntialiasedPixels + "% antialiased pixels",
				percentAntialiasedPixels > 0);

		assertTrue(identicalPixels != totalPixels);
	}

	private String toRGBA(int argb) {
		int alpha = (argb >> 24) & 0xff;
		int red = (argb >> 16) & 0xff;
		int green = (argb >> 8) & 0xff;
		int blue = (argb >> 0) & 0xff;
		return "(" + red + ", " + green + ", " + blue + ", " + alpha + ")";
	}

	private BufferedImage renderSoftClip(VectorImage img, Shape clippingShape) {
		BufferedImage bi = new BufferedImage(SIZE.width, SIZE.height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		for (Operation operation : img.getOperations()) {
			for (Operation op2 : operation.toSoftClipOperation(clippingShape)) {
				for (Operation op3 : standardizeOperations(
						Arrays.asList(op2))) {
					op3.paint(g);
				}
			}
		}
		g.dispose();
		return bi;
	}

	private BufferedImage render(VectorImage img, Shape clippingShape) {
		BufferedImage bi = new BufferedImage(SIZE.width, SIZE.height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		g.clip(clippingShape);
		for (Operation operation : standardizeOperations(img.getOperations())) {
			operation.paint(g);
		}
		g.dispose();
		return bi;
	}

	/**
	 * Some Operations (like StringOperations) should be removed/standardized.
	 * The antialiased version of these operations will be converted to glyph
	 * shapes, so we need to always evaluate them as glyph shapes. Otherwise the
	 * pixel-by-pixel comparison in these unit tests will fail. (That is: we
	 * know that when we convert an operation to its
	 * clipped-antialias-derivative that it won't be pixel perfect, but that's
	 * beyond the scope of what these unit tests can cover.)
	 * 
	 * @param operations
	 *            an incoming list of Operations to filter
	 * @return a filtered (standardized) list of Operations.
	 */
	private List<Operation> standardizeOperations(List<Operation> operations) {
		List<Operation> returnValue = new LinkedList<>();
		for (Operation op : operations) {
			if (op instanceof AttributedCharacterIteratorOperation) {
				Operation[] newOps = ((AttributedCharacterIteratorOperation) op)
						.toTextLayoutOperations();
				returnValue
						.addAll(standardizeOperations(Arrays.asList(newOps)));
				continue;
			}
			if (op instanceof StringOperation)
				op = ((StringOperation) op).toFillOperation();
			if (op instanceof GlyphVectorOperation)
				op = ((GlyphVectorOperation) op).toFillOperation();
			if (op instanceof RenderedImageOperation)
				op = ((RenderedImageOperation) op).toImageOperation();
			returnValue.add(op);
		}
		return returnValue;
	}

	/**
	 * Create an opaque image covered with random rectangles, ellipsed and
	 * triangles.
	 */
	private BufferedImage createOpaqueRandomShapes() {
		BufferedImage bi = new BufferedImage(SIZE.width, SIZE.height,
				BufferedImage.TYPE_INT_RGB);
		Random random = new Random(0);
		Graphics2D g = bi.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		for (int a = 0; a < 20; a++) {
			g.setColor(COLORS[random.nextInt(COLORS.length)]);
			int w = random.nextInt(20) + 10;
			int h = random.nextInt(20) + 10;
			int x = random.nextInt(SIZE.width - w);
			int y = random.nextInt(SIZE.height - h);
			Shape shape = null;
			switch (random.nextInt(3)) {
			case 0:
				shape = new Rectangle(x, y, w, h);
				break;
			case 1:
				shape = new Ellipse2D.Float(x, y, w, h);
				break;
			case 2:
				shape = createTriangle(x, y, w, h);
				break;
			}
			g.fill(shape);
		}
		return bi;
	}

	private BufferedImage createBlueSquare() {
		BufferedImage bi = new BufferedImage(SIZE.width, SIZE.height,
				BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bi.createGraphics();
		g.setColor(new Color(0, 0, 255));
		g.fillRect(0, 0, bi.getWidth(), bi.getHeight());
		return bi;
	}
}