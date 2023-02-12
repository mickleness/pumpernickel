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

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.util.ArrayList;
import java.util.List;

/**
 * This is a collection of static methods relating to java.awt.Shapes.
 */
public class ShapeUtils {

	/**
	 * Return false if any coordinates in this shape contain NaN or Infinite
	 * values.
	 */
	public static boolean isValid(Shape shape) {
		PathIterator i = shape.getPathIterator(null);
		float[] coords = new float[6];
		while (!i.isDone()) {
			int k = i.currentSegment(coords);
			int s = 0;
			if (k == PathIterator.SEG_MOVETO || k == PathIterator.SEG_LINETO) {
				s = 2;
			} else if (k == PathIterator.SEG_QUADTO) {
				s = 4;
			} else if (k == PathIterator.SEG_CUBICTO) {
				s = 6;
			}
			for (int a = 0; a < s; a++) {
				if (Float.isNaN(coords[a]))
					return false;
				if (Float.isInfinite(coords[a]))
					return false;
			}
			i.next();
		}
		return true;
	}

	/**
	 * This traces the shape provided. This can be used to create a "drawing"
	 * effect.
	 * <P>
	 * This assumes every segment is equally important/long, which is not always
	 * the case.
	 * 
	 * @param shape
	 * @param progress
	 *            a float from [0,1], indicating what fraction of the shape
	 *            provided should be traced.
	 * @return a portion of the shape provided
	 */
	public static GeneralPath traceShape(Shape shape, float progress) {
		if (progress < 0 || progress > 1) {
			// be a little forgiving; sometimes progress may be
			// "1.000001" due to rounding errors...
			if (progress < -.01) {
				throw new IllegalArgumentException(
						"progress cannot be less than zero (" + progress + ")");
			} else if (progress > 1.01) {
				throw new IllegalArgumentException(
						"progress cannot be greater than one (" + progress
								+ ")");
			}
			if (progress < 0)
				progress = 0;
			if (progress > 1)
				progress = 1;
		}
		float[] f = new float[6];
		PathIterator i = shape.getPathIterator(null);
		float ctr = 0;
		int k;
		while (i.isDone() == false) {
			k = i.currentSegment(f);
			if (k != PathIterator.SEG_MOVETO && k != PathIterator.SEG_CLOSE)
				ctr++;
			i.next();
		}

		GeneralPath path = new GeneralPath(i.getWindingRule());
		i = shape.getPathIterator(null);
		float lastX = 0;
		float lastY = 0;
		float ctr2 = 0;
		while (i.isDone() == false) {
			k = i.currentSegment(f);

			float t = (progress - ctr2 / ctr) * ctr;
			if (t <= 0)
				return path;
			if (t >= 1)
				t = 1;

			if (k == PathIterator.SEG_MOVETO) {
				path.moveTo(f[0], f[1]);
			} else if (k == PathIterator.SEG_LINETO) {
				path.lineTo(lastX * (1 - t) + t * f[0],
						lastY * (1 - t) + t * f[1]);
			} else if (k == PathIterator.SEG_QUADTO) {
				if (t > .999999) {
					path.quadTo(f[0], f[1], f[2], f[3]);
				} else {
					double t0 = 0;
					double t1 = t;

					double ay = lastY - 2 * f[1] + f[3];
					double by = -2 * lastY + 2 * f[1];
					double cy = lastY;

					double ax = lastX - 2 * f[0] + f[2];
					double bx = -2 * lastX + 2 * f[0];
					double cx = lastX;

					double tZ = (t0 + t1) / 2.0;

					double f0 = ay * t0 * t0 + by * t0 + cy;
					double f1 = ay * tZ * tZ + by * tZ + cy;
					double f2 = ay * t1 * t1 + by * t1 + cy;

					double ay2 = 2 * f2 - 4 * f1 + 2 * f0;
					double cy2 = f0;
					double by2 = f2 - cy2 - ay2;

					f0 = ax * t0 * t0 + bx * t0 + cx;
					f1 = ax * tZ * tZ + bx * tZ + cx;
					f2 = ax * t1 * t1 + bx * t1 + cx;

					double ax2 = 2 * f2 - 4 * f1 + 2 * f0;
					double cx2 = f0;
					double bx2 = f2 - cx2 - ax2;

					double ctrlY = (2 * cy2 + by2) / 2;
					double y1 = ay2 - cy2 + 2 * ctrlY;

					double ctrlX = (2 * cx2 + bx2) / 2;
					double x1 = ax2 - cx2 + 2 * ctrlX;

					path.quadTo((float) ctrlX, (float) ctrlY, (float) x1,
							(float) y1);
				}
			} else if (k == PathIterator.SEG_CUBICTO) {
				if (t > .999999) {
					path.curveTo(f[0], f[1], f[2], f[3], f[4], f[5]);
				} else {
					double t0 = 0;
					double t1 = t;
					double ay = -lastY + 3 * f[1] - 3 * f[3] + f[5];
					double by = 3 * lastY - 6 * f[1] + 3 * f[3];
					double cy = -3 * lastY + 3 * f[1];
					double dy = lastY;

					double ax = -lastX + 3 * f[0] - 3 * f[2] + f[4];
					double bx = 3 * lastX - 6 * f[0] + 3 * f[2];
					double cx = -3 * lastX + 3 * f[0];
					double dx = lastX;

					double tW = 2.0 * t0 / 3.0 + t1 / 3.0;
					double tZ = t0 / 3.0 + 2.0 * t1 / 3.0;

					double f0 = ay * t0 * t0 * t0 + by * t0 * t0 + cy * t0 + dy;
					double f1 = ay * tW * tW * tW + by * tW * tW + cy * tW + dy;
					double f2 = ay * tZ * tZ * tZ + by * tZ * tZ + cy * tZ + dy;
					double f3 = ay * t1 * t1 * t1 + by * t1 * t1 + cy * t1 + dy;

					double dy2 = f0;
					double cy2 = (-11 * f0 + 18 * f1 - 9 * f2 + 2 * f3) / 2.0;
					double by2 = (-19 * f0 + 27 * f2 - 8 * f3 - 10 * cy2) / 4;
					double ay2 = f3 - by2 - cy2 - f0;

					f0 = ax * t0 * t0 * t0 + bx * t0 * t0 + cx * t0 + dx;
					f1 = ax * tW * tW * tW + bx * tW * tW + cx * tW + dx;
					f2 = ax * tZ * tZ * tZ + bx * tZ * tZ + cx * tZ + dx;
					f3 = ax * t1 * t1 * t1 + bx * t1 * t1 + cx * t1 + dx;

					double dx2 = f0;
					double cx2 = (-11 * f0 + 18 * f1 - 9 * f2 + 2 * f3) / 2.0;
					double bx2 = (-19 * f0 + 27 * f2 - 8 * f3 - 10 * cx2) / 4;
					double ax2 = f3 - bx2 - cx2 - f0;

					double cy0 = (3 * dy2 + cy2) / 3;
					double cy1 = (by2 - 3 * dy2 + 6 * cy0) / 3;
					double y1 = ay2 + dy2 - 3 * cy0 + 3 * cy1;

					double cx0 = (3 * dx2 + cx2) / 3;
					double cx1 = (bx2 - 3 * dx2 + 6 * cx0) / 3;
					double x1 = ax2 + dx2 - 3 * cx0 + 3 * cx1;

					path.curveTo((float) cx0, (float) cy0, (float) cx1,
							(float) cy1, (float) x1, (float) y1);
				}
			}

			if (k != PathIterator.SEG_MOVETO && k != PathIterator.SEG_CLOSE)
				ctr2++;
			i.next();
			if (k == PathIterator.SEG_MOVETO || k == PathIterator.SEG_LINETO) {
				lastX = f[0];
				lastY = f[1];
			} else if (k == PathIterator.SEG_QUADTO) {
				lastX = f[2];
				lastY = f[3];
			} else if (k == PathIterator.SEG_CUBICTO) {
				lastX = f[4];
				lastY = f[5];
			}
		}
		return path;
	}

	/**
	 * TSimilar to tracing, this progresses a dot from the beginning to the end
	 * of this path.
	 * 
	 * @param shape
	 * @param progress
	 *            a float from [0,1]
	 */
	public static Point2D getPoint(Shape shape, float progress) {
		if (progress < 0 || progress > 1) {
			// be a little forgiving; sometimes progress may be
			// "1.000001" due to rounding errors...
			if (progress < -.01) {
				throw new IllegalArgumentException(
						"progress cannot be less than zero (" + progress + ")");
			} else if (progress > 1.01) {
				throw new IllegalArgumentException(
						"progress cannot be greater than one (" + progress
								+ ")");
			}
			if (progress < 0)
				progress = 0;
			if (progress > 1)
				progress = 1;
		}
		float[] f = new float[6];
		PathIterator i = shape.getPathIterator(null);
		float ctr = 0;
		int k;
		while (i.isDone() == false) {
			k = i.currentSegment(f);
			if (k != PathIterator.SEG_MOVETO && k != PathIterator.SEG_CLOSE)
				ctr++;
			i.next();
		}

		i = shape.getPathIterator(null);
		float lastX = 0;
		float lastY = 0;
		float ctr2 = 0;

		while (i.isDone() == false) {
			k = i.currentSegment(f);

			float t = (progress - ctr2 / ctr) * ctr;
			if (t <= 0)
				return new Point2D.Double(lastX, lastY);
			if (t >= 1)
				t = 1;

			if (k == PathIterator.SEG_MOVETO) {
				lastX = f[0];
				lastY = f[1];
			} else if (k == PathIterator.SEG_LINETO) {
				lastX = lastX * (1 - t) + t * f[0];
				lastY = lastY * (1 - t) + t * f[1];
			} else if (k == PathIterator.SEG_QUADTO) {
				if (t > .999999) {
					lastX = f[2];
					lastY = f[3];
				} else {
					double ay = lastY - 2 * f[1] + f[3];
					double by = -2 * lastY + 2 * f[1];
					double cy = lastY;

					double ax = lastX - 2 * f[0] + f[2];
					double bx = -2 * lastX + 2 * f[0];
					double cx = lastX;

					lastX = (float) (ax * t * t + bx * t + cx);
					lastY = (float) (ay * t * t + by * t + cy);
				}
			} else if (k == PathIterator.SEG_CUBICTO) {
				if (t > .999999) {
					lastX = f[4];
					lastY = f[5];
				} else {
					double ay = -lastY + 3 * f[1] - 3 * f[3] + f[5];
					double by = 3 * lastY - 6 * f[1] + 3 * f[3];
					double cy = -3 * lastY + 3 * f[1];
					double dy = lastY;

					double ax = -lastX + 3 * f[0] - 3 * f[2] + f[4];
					double bx = 3 * lastX - 6 * f[0] + 3 * f[2];
					double cx = -3 * lastX + 3 * f[0];
					double dx = lastX;

					lastX = (float) (ax * t * t * t + bx * t * t + cx * t + dx);
					lastY = (float) (ay * t * t * t + by * t * t + cy * t + dy);
				}
			}

			if (k != PathIterator.SEG_MOVETO && k != PathIterator.SEG_CLOSE)
				ctr2++;
			i.next();
		}
		return new Point2D.Double(lastX, lastY);
	}

	/** Returns the number of separate paths in the shape provided. */
	public static int getSubPathCount(Shape s) {
		PathIterator i = s.getPathIterator(null);
		int ctr = 0;
		float[] coords = new float[6];
		while (i.isDone() == false) {
			if (i.currentSegment(coords) == PathIterator.SEG_MOVETO) {
				ctr++;
			}
			i.next();
		}
		return ctr++;
	}

	/** Returns each path in s as a separate Path2D */
	public static Path2D[] getSubPaths(Shape s) {
		String s2 = ShapeStringUtils.toString(s);
		int ctr = 0;
		int i = 0;
		while (i < s2.length()) {
			int k = s2.indexOf('m', i);
			if (k == -1) {
				i = s2.length();
			} else {
				ctr++;
				i = k + 1;
			}
		}
		int[] indices = new int[ctr];
		ctr = 0;
		i = 0;
		while (i < s2.length()) {
			int k = s2.indexOf('m', i);
			if (k == -1) {
				i = s2.length();
			} else {
				indices[ctr++] = k;
				i = k + 1;
			}
		}

		Path2D[] p = new GeneralPath[ctr];
		for (i = 0; i < indices.length; i++) {
			String text;
			if (i < indices.length - 1) {
				text = s2.substring(indices[i], indices[i + 1] - 1);
			} else {
				text = s2.substring(indices[i]);
			}
			p[i] = ShapeStringUtils.createPath(text);
		}
		return p;
	}

	/** Return true if two shapes are equal. */
	public static boolean equals(Shape shape, Shape shape2) {
		PathIterator iter1 = shape.getPathIterator(null);
		PathIterator iter2 = shape.getPathIterator(null);
		double[] coords1 = new double[6];
		double[] coords2 = new double[6];
		while ((!iter1.isDone()) && (!iter2.isDone())) {
			int k1 = iter1.currentSegment(coords1);
			int k2 = iter2.currentSegment(coords2);
			if (k1 != k2)
				return false;
			if (k1 == PathIterator.SEG_MOVETO
					|| k1 == PathIterator.SEG_LINETO) {
				if (!equals(coords1, coords2, 2))
					return false;
			} else if (k1 == PathIterator.SEG_QUADTO) {
				if (!equals(coords1, coords2, 4))
					return false;
			} else if (k1 == PathIterator.SEG_CUBICTO) {
				if (!equals(coords1, coords2, 6))
					return false;
			} else if (k1 == PathIterator.SEG_CLOSE) {
				// do nothing
			} else {
				throw new RuntimeException("unrecognized segment " + k1);
			}

			iter1.next();
			iter2.next();
		}
		return iter1.isDone() && iter2.isDone();
	}

	private static boolean equals(double[] array1, double[] array2,
			int length) {
		for (int a = 0; a < length; a++) {
			if (array1[a] != array2[a])
				return false;
		}
		return true;
	}

	/**
	 * Return true if every subpath in the shape provided is closed.
	 * 
	 * @param shape
	 *            the shape to inspect
	 * @return true if every subpath in the shape provided ends with a
	 *         SEG_CLOSE.
	 */
	public static boolean isClosed(Shape shape) {
		PathIterator i = shape.getPathIterator(null);
		float[] coords = new float[6];
		int ctr = 0;
		int lastSegmentType = -1;
		while (!i.isDone()) {
			int segmentType = i.currentSegment(coords);
			if (segmentType == PathIterator.SEG_MOVETO) {
				if (ctr != 0) {
					if (lastSegmentType != PathIterator.SEG_CLOSE)
						return false;
				}
			}
			i.next();
			ctr++;
			lastSegmentType = segmentType;
		}
		if (lastSegmentType != PathIterator.SEG_CLOSE)
			return false;
		return ctr > 0;
	}

	/**
	 * Return true if a shape has no path data.
	 */
	public static boolean isEmpty(Shape shape) {
		PathIterator i = shape.getPathIterator(null);
		return i.isDone();
	}

	public static Shape clone(Shape shape) {
		if (shape == null)
			return null;

		if (shape instanceof RectangularShape)
			return (Shape) ((RectangularShape) shape).clone();
		if (shape instanceof Line2D)
			return (Shape) ((Line2D) shape).clone();

		PathIterator pi = shape.getPathIterator(null);
		Path2D p = new Path2D.Float(pi.getWindingRule());
		p.append(pi, false);
		return p;
	}

	/**
	 * Convert the argument to an int-based Rectangle, if possible.
	 * 
	 * @param shape
	 * @return a Rectangle that exactly matches the argument provided, or null
	 *         if the argument is not an int-based Rectangle.
	 */
	public static Rectangle getRectangle(Shape shape) {
		if (shape instanceof Rectangle)
			return (Rectangle) shape;

		Rectangle2D r2 = getRectangle2D(shape);
		if (r2 == null)
			return null;

		int xi = (int) (r2.getX() + .5);
		int wi = (int) (r2.getWidth() + .5);
		int yi = (int) (r2.getY() + .5);
		int hi = (int) (r2.getHeight() + .5);
		if (Math.abs(xi - r2.getX()) < .00001
				&& Math.abs(yi - r2.getY()) < .00001
				&& Math.abs(wi - r2.getWidth()) < .00001
				&& Math.abs(hi - r2.getHeight()) < .00001)
			return new Rectangle(xi, yi, wi, hi);
		return null;
	}

	/**
	 * Convert the argument to a double-based Rectangle2D, if possible.
	 * 
	 * @param shape
	 * @return a Rectangle2D that exactly matches the argument provided, or null
	 *         if the argument is not an int-based Rectangle.
	 */
	public static Rectangle2D getRectangle2D(Shape shape) {
		if (shape instanceof Rectangle2D)
			return (Rectangle2D) shape;

		if (shape == null)
			return null;

		List<Point2D> points = new ArrayList<>(4);
		Rectangle2D returnValue = null;
		PathIterator iter = shape.getPathIterator(null);
		double[] coords = new double[6];
		while (!iter.isDone()) {
			int k = iter.currentSegment(coords);
			if (k == PathIterator.SEG_MOVETO && points.isEmpty()) {
				points.add(new Point2D.Double(coords[0], coords[1]));
				returnValue = new Rectangle2D.Double(coords[0], coords[1], 0,
						0);
			} else if (k == PathIterator.SEG_LINETO && !points.isEmpty()) {
				if (points.size() > 4)
					return null;
				Point2D pt = new Point2D.Double(coords[0], coords[1]);
				points.add(pt);
				returnValue.add(pt);
			} else if (k == PathIterator.SEG_CLOSE) {
				// do nothing
			} else {
				return null;
			}
			iter.next();
		}

		// if the last point is the same as the first point: remove the last
		// point
		if (!points.isEmpty()
				&& points.get(points.size() - 1).equals(points.get(0)))
			points.remove(points.size() - 1);

		if (points.size() != 4)
			return null;

		Point2D topLeft = new Point2D.Double(returnValue.getMinX(),
				returnValue.getMinY());
		Point2D topRight = new Point2D.Double(returnValue.getMaxX(),
				returnValue.getMinY());
		Point2D bottomLeft = new Point2D.Double(returnValue.getMinX(),
				returnValue.getMaxY());
		Point2D bottomRight = new Point2D.Double(returnValue.getMaxX(),
				returnValue.getMaxY());

		// there's a more elegant way to do this, but we only have 8
		// combinations so let's just enumerate them:

		// If the points are ordered clockwise:

		if (points.get(0).equals(topLeft) && points.get(1).equals(topRight)
				&& points.get(2).equals(bottomRight)
				&& points.get(3).equals(bottomLeft))
			return returnValue;
		if (points.get(0).equals(topRight) && points.get(1).equals(bottomRight)
				&& points.get(2).equals(bottomLeft)
				&& points.get(3).equals(topLeft))
			return returnValue;
		if (points.get(0).equals(bottomRight)
				&& points.get(1).equals(bottomLeft)
				&& points.get(2).equals(topLeft)
				&& points.get(3).equals(topRight))
			return returnValue;
		if (points.get(0).equals(bottomLeft) && points.get(1).equals(topLeft)
				&& points.get(2).equals(topRight)
				&& points.get(3).equals(bottomRight))
			return returnValue;

		// If they're counter-clockwise:

		if (points.get(0).equals(topLeft) && points.get(1).equals(bottomLeft)
				&& points.get(2).equals(bottomRight)
				&& points.get(3).equals(topRight))
			return returnValue;
		if (points.get(0).equals(topRight) && points.get(1).equals(topLeft)
				&& points.get(2).equals(bottomLeft)
				&& points.get(3).equals(bottomRight))
			return returnValue;
		if (points.get(0).equals(bottomRight) && points.get(1).equals(topRight)
				&& points.get(2).equals(topLeft)
				&& points.get(3).equals(bottomLeft))
			return returnValue;
		if (points.get(0).equals(bottomLeft)
				&& points.get(1).equals(bottomRight)
				&& points.get(2).equals(topRight)
				&& points.get(3).equals(topLeft))
			return returnValue;

		return null;
	}

	/**
	 * Create a flattened derivative of a shape.
	 * 
	 * @param shape
	 *            the shape to flatten
	 * @param flatness
	 *            the maximum distance that the line segments used to
	 *            approximate the curved segments are allowed to deviate from
	 *            any point on the original curve
	 */
	public static Shape flatten(Shape shape, float flatness) {
		PathIterator pi = shape.getPathIterator(null, flatness);
		Path2D.Float returnValue = new Path2D.Float(pi.getWindingRule());
		returnValue.append(pi, false);
		return returnValue;
	}

	public static List<Point2D> getEndPoints(Shape path) {
		List<Point2D> returnValue = new ArrayList<>();

		float[] coords = new float[6];
		PathIterator pi = path.getPathIterator(null);
		Point2D move = null;
		Point2D last = null;
		while (!pi.isDone()) {
			int k = pi.currentSegment(coords);
			if (k == PathIterator.SEG_MOVETO) {
				if (move != null) {
					returnValue.add(move);
					returnValue.add(last);
				}

				move = new Point2D.Float(coords[0], coords[1]);
				last = new Point2D.Float(coords[0], coords[1]);
			} else if (k == PathIterator.SEG_LINETO) {
				last.setLocation(coords[0], coords[1]);
			} else if (k == PathIterator.SEG_QUADTO) {
				last.setLocation(coords[2], coords[3]);
			} else if (k == PathIterator.SEG_CUBICTO) {
				last.setLocation(coords[4], coords[5]);
			} else if (k == PathIterator.SEG_CLOSE) {
				move = null;
			}
			pi.next();
		}

		if (move != null) {
			returnValue.add(move);
			returnValue.add(last);
		}

		return returnValue;
	}
}