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
package com.pump.geom;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JComboBox;
import javax.swing.JLabel;

import com.pump.inspector.Inspector;

public class IntersectionsTest extends BasicTestElement {

	JLabel shapeLabel = new JLabel("Shape:");
	JComboBox shape = new JComboBox();
	PrintStream printStream;

	public IntersectionsTest(PrintStream stream) {
		printStream = stream;

		shape.addItem("All");
		shape.addItem("Lines");
		shape.addItem("Quads");
		shape.addItem("Cubics");
	}

	Random random = new Random();

	private Shape getShape(int i) {
		random.setSeed(i * 1000);
		String type = (String) shape.getSelectedItem();
		if (type.equals("All")) {
			int j = random.nextInt(3);
			if (j == 0) {
				type = "Lines";
			} else if (j == 1) {
				type = "Quads";
			} else if (j == 2) {
				type = "Cubics";
			}
		}
		if (type.equals("Lines")) {
			return new Line2D.Double(random.nextDouble() * 100,
					random.nextDouble() * 100, random.nextDouble() * 100,
					random.nextDouble() * 100);
		} else if (type.equals("Quads")) {
			return new QuadCurve2D.Double(random.nextDouble() * 100,
					random.nextDouble() * 100, random.nextDouble() * 100,
					random.nextDouble() * 100, random.nextDouble() * 100,
					random.nextDouble() * 100);
		} else if (type.equals("Cubics")) {
			return new CubicCurve2D.Double(random.nextDouble() * 100,
					random.nextDouble() * 100, random.nextDouble() * 100,
					random.nextDouble() * 100, random.nextDouble() * 100,
					random.nextDouble() * 100, random.nextDouble() * 100,
					random.nextDouble() * 100);
		}
		throw new RuntimeException("unrecognized type \"" + type + "\"");
	}

	@Override
	public void addControls(Inspector layout) {
		layout.addRow(shapeLabel, shape, false);
	}

	@Override
	public String getName() {
		return "Intersections Accuracy Tests";
	}

	@Override
	public void doTest() {
		BufferedImage image1 = new BufferedImage(500, 500,
				BufferedImage.TYPE_INT_ARGB);
		BufferedImage image2 = new BufferedImage(image1.getWidth(),
				image1.getHeight(), BufferedImage.TYPE_INT_ARGB);

		int failures = 0;
		int testCount = 1000;
		printStream.println("Beginning Intersections Accuracy Tests...");
		for (int a = 0; a < testCount; a++) {
			Shape shapeA = getShape(a);
			Rectangle2D boundsA = ShapeBounds.getBounds(shapeA);
			for (int b = a; b < testCount; b++) {

				float m = (a * testCount + b);
				m = m / ((testCount * testCount));
				progress.setValue((int) (m * (progress.getMaximum() - progress
						.getMinimum())));

				Shape shapeB = getShape(b);
				Rectangle2D boundsB = ShapeBounds.getBounds(shapeB);

				if (cancelled)
					return;

				Rectangle2D sumBounds = new Rectangle2D.Double(boundsA.getX(),
						boundsA.getY(), boundsA.getWidth(), boundsA.getHeight());
				sumBounds.add(boundsB);

				boolean observedIntersection = isIntersectionObserved(shapeA,
						shapeB, image1, image2, sumBounds, 1);
				boolean theoreticalIntersection = getTheoreticalIntersection(
						shapeA, shapeB);

				boolean passed = theoreticalIntersection == observedIntersection;
				if (!passed) {
					try {
						Rectangle2D imageBounds = new Rectangle2D.Double(0, 0,
								image1.getWidth(), image1.getHeight());
						// RectangularTransform transform = new
						// RectangularTransform(sumBounds, imageBounds);
						RectangularTransform transform = new RectangularTransform(
								new Rectangle(0, 0, 100, 100), imageBounds);
						AffineTransform at = transform.createAffineTransform();

						draw(shapeA, at, image1, true);
						draw(shapeB, at, image2, true);

						ImageIO.write(image1, "png", new File("lhs " + (ctr)
								+ ".png"));
						ImageIO.write(image2, "png", new File("rhs " + (ctr)
								+ ".png"));
					} catch (IOException e) {
						e.printStackTrace();
					}
					printStream.println("failed.  lhs = " + a + ", rhs = " + b
							+ ", theoretical = " + theoreticalIntersection
							+ ", observed = " + observedIntersection
							+ ", ctr = " + ctr + ", bounds = " + sumBounds);
					failures++;
					ctr++;

					String s1 = ShapeStringUtils.toString(shapeA);
					String s2 = ShapeStringUtils.toString(shapeB);
					printStream.println("\"" + s1 + "\"\n\"" + s2 + "\"");
				}
			}
		}
		printStream.println("failures: " + failures);
	}

	protected static boolean isIntersectionObserved(Shape shapeA, Shape shapeB,
			BufferedImage bi1, BufferedImage bi2, Rectangle2D rect, int depth) {
		Rectangle2D imageBounds = new Rectangle2D.Double(0, 0, bi1.getWidth(),
				bi2.getHeight());
		RectangularTransform transform = new RectangularTransform(rect,
				imageBounds);

		AffineTransform at = transform.createAffineTransform();
		draw(shapeA, at, bi1, false);
		draw(shapeB, at, bi2, false);

		try {
			// ImageIO.write(bi1, "png", new File("lhs live.png"));
			// ImageIO.write(bi2, "png", new File("rhs live.png"));

			Graphics2D g = bi2.createGraphics();
			g.setComposite(AlphaComposite.SrcIn);
			g.drawImage(bi1, 0, 0, null);
			g.dispose();

			// ImageIO.write(bi2, "png", new File("sum live.png"));
		} catch (Exception e) {
			throw new RuntimeException();
		}

		List<Rectangle2D> rects = new ArrayList<Rectangle2D>();

		int[] row = new int[bi1.getWidth()];
		RectangularTransform inverseTransform = transform.createInverse();
		boolean stopSearchingNow = Math.max(transform.getScaleX(),
				transform.getScaleY()) > 10000000;
		for (int y = 0; y < bi2.getHeight(); y++) {
			bi2.getRaster().getDataElements(0, y, row.length, 1, row);
			for (int x = 0; x < row.length; x++) {
				int alpha = (row[x] >> 24) & 0xff;

				if (alpha > 20) {
					if (stopSearchingNow) {
						return true;
					}
					Rectangle2D r = new Rectangle2D.Double(x - 1, y - 1, 3, 3);
					r = inverseTransform.transform(r);
					rects.add(r);
				}
			}
		}
		if (rects.size() == 0) {
			if (depth > 1 && isEmpty(bi1)) {
				// how did we possibly get here? We picked this spot because the
				// two shapes intersected somewhere in this rectangle, but one
				// shape
				// didn't render ANYTHING in this rectangle? Fishy business.
				throw new MachineErrorException();
			} else if (depth > 1) {
				draw(shapeB, at, bi2, false);
				if (isEmpty(bi2))
					throw new MachineErrorException();
			}
			return false;
		}

		for (int a = 0; a < rects.size(); a++) {
			Rectangle2D r = rects.get(a);
			try {
				if (isIntersectionObserved(shapeA, shapeB, bi1, bi2, r,
						depth + 1))
					return true;
			} catch (MachineErrorException e) {
				// we asked for too severe a degree of accuracy, but the odds
				// are we did find something:
				return true;
			}
		}

		return false;
	}

	protected static boolean getTheoreticalIntersection(Shape shapeA,
			Shape shapeB) {

		if (shapeA instanceof Line2D) {
			Line2D la = (Line2D) shapeA;
			if (shapeB instanceof Line2D) {
				Line2D lb = (Line2D) shapeB;
				return Intersections.lineLine(la.getX1(), la.getY1(),
						la.getX2(), la.getY2(), lb.getX1(), lb.getY1(),
						lb.getX2(), lb.getY2());
			} else if (shapeB instanceof QuadCurve2D) {
				QuadCurve2D qb = (QuadCurve2D) shapeB;
				return Intersections.lineQuad(la.getX1(), la.getY1(),
						la.getX2(), la.getY2(), qb.getX1(), qb.getY1(),
						qb.getCtrlX(), qb.getCtrlY(), qb.getX2(), qb.getY2());
			} else if (shapeB instanceof CubicCurve2D) {
				CubicCurve2D cb = (CubicCurve2D) shapeB;
				return Intersections.lineCubic(la.getX1(), la.getY1(),
						la.getX2(), la.getY2(), cb.getX1(), cb.getY1(),
						cb.getCtrlX1(), cb.getCtrlY1(), cb.getCtrlX2(),
						cb.getCtrlY2(), cb.getX2(), cb.getY2());
			} else {
				throw new RuntimeException();
			}
		} else if (shapeA instanceof QuadCurve2D) {
			QuadCurve2D qa = (QuadCurve2D) shapeA;
			if (shapeB instanceof Line2D) {
				Line2D lb = (Line2D) shapeB;
				return Intersections.lineQuad(lb.getX1(), lb.getY1(),
						lb.getX2(), lb.getY2(), qa.getX1(), qa.getY1(),
						qa.getCtrlX(), qa.getCtrlY(), qa.getX2(), qa.getY2());
			} else if (shapeB instanceof QuadCurve2D) {
				QuadCurve2D qb = (QuadCurve2D) shapeB;
				return Intersections.quadQuad(qa.getX1(), qa.getY1(),
						qa.getCtrlX(), qa.getCtrlY(), qa.getX2(), qa.getY2(),
						qb.getX1(), qb.getY1(), qb.getCtrlX(), qb.getCtrlY(),
						qb.getX2(), qb.getY2());
			} else if (shapeB instanceof CubicCurve2D) {
				CubicCurve2D cb = (CubicCurve2D) shapeB;
				return Intersections.quadCubic(qa.getX1(), qa.getY1(),
						qa.getCtrlX(), qa.getCtrlY(), qa.getX2(), qa.getY2(),
						cb.getX1(), cb.getY1(), cb.getCtrlX1(), cb.getCtrlY1(),
						cb.getCtrlX2(), cb.getCtrlY2(), cb.getX2(), cb.getY2());
			} else {
				throw new RuntimeException();
			}
		} else if (shapeA instanceof CubicCurve2D) {
			CubicCurve2D ca = (CubicCurve2D) shapeA;
			if (shapeB instanceof Line2D) {
				Line2D lb = (Line2D) shapeB;
				return Intersections.lineCubic(lb.getX1(), lb.getY1(),
						lb.getX2(), lb.getY2(), ca.getX1(), ca.getY1(),
						ca.getCtrlX1(), ca.getCtrlY1(), ca.getCtrlX2(),
						ca.getCtrlY2(), ca.getX2(), ca.getY2());
			} else if (shapeB instanceof QuadCurve2D) {
				QuadCurve2D qb = (QuadCurve2D) shapeB;
				return Intersections.quadCubic(qb.getX1(), qb.getY1(),
						qb.getCtrlX(), qb.getCtrlY(), qb.getX2(), qb.getY2(),
						ca.getX1(), ca.getY1(), ca.getCtrlX1(), ca.getCtrlY1(),
						ca.getCtrlX2(), ca.getCtrlY2(), ca.getX2(), ca.getY2());
			} else if (shapeB instanceof CubicCurve2D) {
				CubicCurve2D cb = (CubicCurve2D) shapeB;
				return Intersections.cubicCubic(ca.getX1(), ca.getY1(),
						ca.getCtrlX1(), ca.getCtrlY1(), ca.getCtrlX2(),
						ca.getCtrlY2(), ca.getX2(), ca.getY2(), cb.getX1(),
						cb.getY1(), cb.getCtrlX1(), cb.getCtrlY1(),
						cb.getCtrlX2(), cb.getCtrlY2(), cb.getX2(), cb.getY2());
			} else {
				throw new RuntimeException();
			}
		} else {
			throw new RuntimeException();
		}
	}

	public static final void draw(Shape shape, AffineTransform at,
			BufferedImage bi, boolean showControlPoints) {
		clear(bi);
		Graphics2D g = bi.createGraphics();
		g.setColor(Color.black);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.fill(new BasicStroke(1).createStrokedShape(at
				.createTransformedShape(shape)));
		if (showControlPoints) {
			PathIterator i;
			double[] coords = new double[6];
			i = shape.getPathIterator(at);
			while (i.isDone() == false) {
				int k = i.currentSegment(coords);
				if (k == PathIterator.SEG_QUADTO
						|| k == PathIterator.SEG_CUBICTO) {
					g.setColor(Color.blue);
					g.fill(new Rectangle2D.Double(coords[0] - 1, coords[1] - 1,
							2, 2));
				}
				if (k == PathIterator.SEG_CUBICTO) {
					g.setColor(Color.green);
					g.fill(new Rectangle2D.Double(coords[2] - 1, coords[3] - 1,
							2, 2));
				}
				i.next();
			}
		}
		g.dispose();
	}

	static int ctr = 0;

	@Override
	public String getDescription() {
		// TODO: explore these 5 failures more. If it is confirmed that it is
		// the test that is failing,
		// then the test needs to be improved to be more accurate.
		return "Test the accuracy of the Intersections class.\n\nNote as of this writing: when set to \"All\" there are 5 tests that fail.  I think these are failures of this test, and not the Intersections class.";
	}

	static class MachineErrorException extends RuntimeException {
		private static final long serialVersionUID = 1L;

	}
}