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

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

/**
 * A polyline that adds curvature to all nodes.
 * <p>
 * TODO: this is loosely based on my perception of how the Google Docs freehand
 * shape tool works, but the magnitude of the tangents still requires some
 * adjusting.
 * 
 * @see com.pump.plaf.CurvedPolylineCreationUI
 */
public class CurvedPolyline extends AbstractShape {

	private static class SplinePoint2D extends Point2D {

		public double x, y, theta;

		SplinePoint2D(double x, double y, double theta) {
			this.x = x;
			this.y = y;
			this.theta = theta;
		}

		@Override
		public double getX() {
			return x;
		}

		@Override
		public double getY() {
			return y;
		}

		@Override
		public void setLocation(double x, double y) {
			this.x = x;
			this.y = y;
		}

	}

	/** A path iterator based on a series of points. */
	public static class CurvedPolylineIterator implements PathIterator {
		AffineTransform tx;
		Point2D[] points;
		int windingRule;
		int index = 0;

		/**
		 * 
		 * @param array
		 *            an array of points to create this shape from.
		 * @param windingRule
		 *            one of the winding constants (PathIterator.WIND_NONZERO or
		 *            PathIterator.WIND_EVEN_ODD)
		 * @param tx
		 *            an optional AffineTransform to transform this data (may be
		 *            null).
		 */
		public CurvedPolylineIterator(Point2D[] array, int windingRule,
				AffineTransform tx) {
			points = new Point2D[array.length];
			for (int a = 0; a < array.length; a++) {
				if (a == 0 || a == array.length - 1) {
					points[a] = new Point2D.Double(array[a].getX(),
							array[a].getY());
				} else {
					Point2D prev = array[a - 1];
					Point2D next = array[a + 1];
					double theta = Math.atan2(next.getY() - prev.getY(),
							next.getX() - prev.getX());
					points[a] = new SplinePoint2D(array[a].getX(),
							array[a].getY(), theta);
				}
			}
			this.tx = tx;
			this.windingRule = windingRule;
			if (!(windingRule == PathIterator.WIND_NON_ZERO || windingRule == PathIterator.WIND_EVEN_ODD))
				throw new IllegalArgumentException("windingRule = "
						+ windingRule);
		}

		public int getWindingRule() {
			return windingRule;
		}

		public boolean isDone() {
			return index >= points.length;
		}

		public void next() {
			index++;
		}

		public int currentSegment(double[] coords) {
			try {
				if (index == 0) {
					coords[0] = points[0].getX();
					coords[1] = points[0].getY();
					return PathIterator.SEG_MOVETO;
				}

				if (points[index - 1] instanceof SplinePoint2D
						&& points[index] instanceof SplinePoint2D) {
					SplinePoint2D prev = (SplinePoint2D) points[index - 1];
					SplinePoint2D current = (SplinePoint2D) points[index];
					double k = prev.distance(current) / 3;
					coords[0] = prev.getX() + k * Math.cos(prev.theta);
					coords[1] = prev.getY() + k * Math.sin(prev.theta);
					coords[2] = current.getX() - k * Math.cos(current.theta);
					coords[3] = current.getY() - k * Math.sin(current.theta);
					coords[4] = current.getX();
					coords[5] = current.getY();
					return PathIterator.SEG_CUBICTO;
				} else if (points[index - 1] instanceof SplinePoint2D) {
					SplinePoint2D prev = (SplinePoint2D) points[index - 1];
					Point2D current = points[index];
					double k = prev.distance(current) / 3;
					coords[0] = prev.getX() + k * Math.cos(prev.theta);
					coords[1] = prev.getY() + k * Math.sin(prev.theta);
					coords[2] = current.getX() * 2 / 3 + prev.getX() * 1 / 3;
					coords[3] = current.getY() * 2 / 3 + prev.getY() * 1 / 3;
					coords[4] = current.getX();
					coords[5] = current.getY();
					return PathIterator.SEG_CUBICTO;
				} else if (points[index] instanceof SplinePoint2D) {
					Point2D prev = points[index - 1];
					SplinePoint2D current = (SplinePoint2D) points[index];
					double k = prev.distance(current) / 3;
					coords[0] = prev.getX() * 2 / 3 + current.getX() * 1 / 3;
					coords[1] = prev.getY() * 2 / 3 + current.getY() * 1 / 3;
					coords[2] = current.getX() - k * Math.cos(current.theta);
					coords[3] = current.getY() - k * Math.sin(current.theta);
					coords[4] = current.getX();
					coords[5] = current.getY();
					return PathIterator.SEG_CUBICTO;
				} else {
					Point2D current = points[index];
					coords[0] = current.getX();
					coords[1] = current.getY();
					return PathIterator.SEG_LINETO;
				}
			} finally {
				if (tx != null) {
					tx.transform(coords, 0, coords, 0, 3);
				}
			}
		}

		public int currentSegment(float[] coords) {
			double[] d = new double[6];
			int k = currentSegment(d);
			for (int a = 0; a < 6; a++) {
				coords[a] = (float) d[a];
			}
			return k;
		}
	}

	protected List<Point2D> points = new ArrayList<Point2D>();
	protected int windingRule = PathIterator.WIND_EVEN_ODD;

	/** Add a point to this shape. */
	public synchronized void addPoint(Point2D p) {
		points.add(new Point2D.Double(p.getX(), p.getY()));
	}

	/** Add a point to this shape. */
	public synchronized void addPoint(double x, double y) {
		addPoint(new Point2D.Double(x, y));
	}

	/** Return the number of points. */
	public synchronized int getPointCount() {
		return points.size();
	}

	/** Returns a specified point. */
	public synchronized Point2D getPoint(int index) {
		return new Point2D.Double(points.get(index).getX(), points.get(index)
				.getY());
	}

	/**
	 * Redefine an existing point.
	 * 
	 * @param index
	 *            the index of the point to redefine.
	 * @param x
	 *            the new x-coordinate.
	 * @param y
	 *            the new y-coordinate.
	 * @return true if a change occurred.
	 */
	public synchronized boolean setPoint(int index, double x, double y) {
		if (index == points.size()) {
			addPoint(x, y);
			return true;
		}
		Point2D p = points.get(index);
		if (Math.abs(p.getX() - x) < .0001 && Math.abs(p.getY() - y) < .0001) {
			return false;
		}
		p.setLocation(x, y);
		return true;
	}

	/**
	 * This expresses this shape as a series of points, written as
	 * "m [X] [Y] l [X] [Y] l [X] [Y] ..." Note that although this shape
	 * includes cubic data, it is expressed simply as points (linear data).
	 */
	@Override
	public synchronized String toString() {
		StringBuffer sb = new StringBuffer();
		for (int a = 0; a < points.size(); a++) {
			if (a == 0) {
				sb.append("m ");
			} else {
				sb.append("l ");
			}
			sb.append(points.get(a).getX() + " ");
			sb.append(points.get(a).getY() + " ");
		}
		return "CurvedPolyline[ " + sb.toString().trim() + " ]";
	}

	/**
	 * Return a PathIterator representing this shape. This is thread-safe
	 */
	public synchronized PathIterator getPathIterator(AffineTransform at) {
		Point2D[] pointArray = points.toArray(new Point2D[points.size()]);
		for (int a = 0; a < pointArray.length; a++) {
			pointArray[a] = new Point2D.Double(pointArray[a].getX(),
					pointArray[a].getY());
		}
		return new CurvedPolylineIterator(pointArray, getWindingRule(), at);
	}

	/**
	 * Set a winding rule.
	 * 
	 * @param newWindingRule
	 *            <code>PathIterator.WIND_EVEN_ODD</code> or
	 *            <code>PathIterator.WIND_NON_ZERO</code>.
	 * @return true if a change occurred.
	 */
	public synchronized boolean setWindingRule(int newWindingRule) {
		if (newWindingRule == PathIterator.WIND_EVEN_ODD
				|| newWindingRule == PathIterator.WIND_NON_ZERO) {
			throw new IllegalArgumentException();
		}
		if (windingRule == newWindingRule)
			return false;
		windingRule = newWindingRule;
		return true;
	}

	@Override
	public synchronized int getWindingRule() {
		return windingRule;
	}
}