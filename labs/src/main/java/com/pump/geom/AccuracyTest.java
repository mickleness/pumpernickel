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
package com.pump.geom;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.io.PrintStream;
import java.util.Random;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import com.pump.inspector.Inspector;

public class AccuracyTest extends BasicTestElement {

	private Area getArea(int index) {
		GeneralPath path = getShape(index);
		return new Area(path);
	}

	private AreaX getAreaX(int index) {
		GeneralPath path = getShape(index);
		return new AreaX(path);
	}

	private GeneralPath getShape(int index) {
		Random r = new Random(index * 100000);
		GeneralPath path = new GeneralPath(
				r.nextBoolean() ? Path2D.WIND_EVEN_ODD : Path2D.WIND_NON_ZERO);
		path.moveTo(r.nextFloat() * 100, r.nextFloat() * 100);
		for (int a = 0; a < 3; a++) {
			int k;
			if (type.getSelectedIndex() == 0) {
				k = r.nextInt(3);
			} else {
				k = type.getSelectedIndex() - 1;
			}

			if (k == 0) {
				path.lineTo(r.nextFloat() * 100, r.nextFloat() * 100);
			} else if (k == 1) {
				path.quadTo(r.nextFloat() * 100, r.nextFloat() * 100,
						r.nextFloat() * 100, r.nextFloat() * 100);
			} else {
				path.curveTo(r.nextFloat() * 100, r.nextFloat() * 100,
						r.nextFloat() * 100, r.nextFloat() * 100,
						r.nextFloat() * 100, r.nextFloat() * 100);
			}
		}
		return path;
	}

	@Override
	public String getDescription() {
		return "This creates several random shapes "
				+ "and applies different geometric operations (add, subtract, xor, intersect).  These operations are performed "
				+ "graphically on an image that is "
				+ image1.getWidth()
				+ "x"
				+ image1.getHeight()
				+ " pixels.  The image is then scanned "
				+ "for visual confirmation that each operation executed as expected.  (A margin of error is allowed for "
				+ "antialiasing issues, but if a 3x3 square of pixels is significantly different: then that operation is flagged "
				+ "as a failure.)";
	}

	JLabel shapeLabel = new JLabel("Shape:");
	JComboBox shape = new JComboBox();
	JLabel typeLabel = new JLabel("Type:");
	JComboBox type = new JComboBox();
	JLabel operationLabel = new JLabel("Operation:");
	JComboBox operation = new JComboBox();
	JLabel skipLabel = new JLabel("Skip:");
	JSpinner skip = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
	PrintStream printStream;

	public AccuracyTest(PrintStream stream) {
		printStream = stream;

		shape.addItem("AreaX");
		shape.addItem("Area");

		type.addItem("All");
		type.addItem("Linear");
		type.addItem("Quadratic");
		type.addItem("Cubic");

		operation.addItem("All");
		operation.addItem("Add");
		operation.addItem("Subtract");
		operation.addItem("Intersect");
		operation.addItem("ExclusiveOr");
	}

	@Override
	public void addControls(Inspector layout) {
		layout.addRow(shapeLabel, shape, false);
		layout.addRow(typeLabel, type, false);
		layout.addRow(operationLabel, operation, false);
		layout.addRow(skipLabel, skip, false);
	}

	@Override
	public String getName() {
		return "Accuracy Tests";
	}

	@Override
	public void doTest() {
		int[] operations;
		if (operation.getSelectedIndex() == 0) {
			operations = new int[] { ADD, SUBTRACT, INTERSECT, XOR };
		} else {
			operations = new int[] { operation.getSelectedIndex() };
		}

		int failures = 0;
		int incr = ((Number) skip.getValue()).intValue();
		AreaX[] areaXs = null;
		Area[] areas = null;
		int numShapes = 200;
		areaXs = new AreaX[numShapes];
		for (int a = 0; a < areaXs.length; a++) {
			areaXs[a] = getAreaX(a);
		}
		areas = new Area[numShapes];
		for (int a = 0; a < areas.length; a++) {
			areas[a] = getArea(a);
		}
		printStream.println("Beginning Accuracy Tests...");
		for (int a = 0; a < operations.length; a++) {
			for (int b = 0; b < numShapes; b += incr) {
				for (int c = 0; c < numShapes; c += incr) {
					if (cancelled)
						return;

					float m = b * numShapes + c;
					m = m / ((numShapes * numShapes));
					m = m / (operations.length) + ((float) a)
							/ ((float) operations.length);
					progress.setValue((int) (m * (progress.getMaximum() - progress
							.getMinimum())));

					Shape finalShape;
					if (shape.getSelectedIndex() == 0) {
						AreaX result = new AreaX(areaXs[b]);
						if (operations[a] == ADD) {
							result.add(areaXs[c]);
						} else if (operations[a] == SUBTRACT) {
							result.subtract(areaXs[c]);
						} else if (operations[a] == INTERSECT) {
							result.intersect(areaXs[c]);
						} else if (operations[a] == XOR) {
							result.exclusiveOr(areaXs[c]);
						}
						finalShape = result;
					} else {
						try {
							Area result = new Area(areaXs[b]);
							if (operations[a] == ADD) {
								result.add(areas[c]);
							} else if (operations[a] == SUBTRACT) {
								result.subtract(areas[c]);
							} else if (operations[a] == INTERSECT) {
								result.intersect(areas[c]);
							} else if (operations[a] == XOR) {
								result.exclusiveOr(areas[c]);
							}
							finalShape = result;
						} catch (Throwable t) {
							failures++;
							printStream.println("lhs = " + b + " rhs = " + c);
							t.printStackTrace();
							continue;
						}
					}

					Rectangle2D bounds = areas[b].getBounds2D();
					bounds.add(areas[c].getBounds2D());
					RectangularTransform rt = new RectangularTransform(bounds,
							new Rectangle(0, 0, image1.getWidth(),
									image1.getHeight()));
					AffineTransform transform = rt.createAffineTransform();

					synchronized (image1) {
						clear(image1);
						Graphics2D g = image1.createGraphics();
						g.setComposite(AlphaComposite.SrcOver);
						g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
								RenderingHints.VALUE_ANTIALIAS_ON);
						g.setColor(Color.black);
						g.setTransform(transform);

						g.fill(getShape(b));
						if (operations[a] == ADD) {
							g.fill(getShape(c));
						} else if (operations[a] == SUBTRACT) {
							g.setComposite(AlphaComposite.Clear);
							g.fill(getShape(c));
						} else if (operations[a] == XOR) {
							g.setComposite(AlphaComposite.SrcOut);
							g.fill(getShape(c));
						} else if (operations[a] == INTERSECT) {
							g.setComposite(AlphaComposite.SrcIn);
							clear(image2);
							Graphics2D g2 = image2.createGraphics();
							g2.setComposite(AlphaComposite.SrcOver);
							g2.setRenderingHint(
									RenderingHints.KEY_ANTIALIASING,
									RenderingHints.VALUE_ANTIALIAS_ON);
							g2.setColor(Color.black);
							g2.setTransform(transform);
							g2.fill(getShape(c));
							g2.dispose();
							g.setTransform(new AffineTransform());
							g.drawImage(image2, 0, 0, null);
						}
						g.dispose();

						clear(image2);
						g = image2.createGraphics();
						g.setComposite(AlphaComposite.SrcOver);
						g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
								RenderingHints.VALUE_ANTIALIAS_ON);
						g.setColor(Color.black);
						g.setTransform(transform);

						g.fill(finalShape);
						g.dispose();

						boolean passed = equals(image1, image2, printStream);
						if (!passed) {

							String opName;
							if (operations[a] == ADD) {
								opName = "add";
							} else if (operations[a] == SUBTRACT) {
								opName = "subtract";
							} else if (operations[a] == INTERSECT) {
								opName = "intersect";
							} else {
								opName = "exclusiveOr";
							}
							printStream.println("failed " + opName
									+ ".  lhs = " + b + ", rhs = " + c);
							failures++;
						}
					}
				}
			}
		}
		printStream.println("failures: " + failures);
	}
}