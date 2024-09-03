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

import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.awt.geom.QuadCurve2D;
import java.util.Arrays;
import java.util.Stack;

/**
 * A collection of static methods to identify if specific curves intersect each
 * other. All curves are defined in terms of their original bezier control
 * points.
 */
public class Intersections {

	private static Stack<double[]> doubleArrays = new Stack<double[]>();

	public static boolean lineLine(double x0a, double y0a, double x1a,
			double y1a, double x0b, double y0b, double x1b, double y1b) {
		if (x0a == x0b && y0a == y0b && x1a == x1b && y1a == y1b)
			return true;

		return Line2D.linesIntersect(x0a, y0a, x1a, y1a, x0b, y0b, x1b, y1b);
	}

	public static boolean lineQuad(double x0a, double y0a, double x1a,
			double y1a, double x0b, double y0b, double cxb, double cyb,
			double x1b, double y1b) {
		double[] array;
		synchronized (doubleArrays) {
			if (doubleArrays.size() == 0) {
				array = new double[12];
			} else {
				array = doubleArrays.pop();
			}
		}
		try {
			int polyBSize = definePolygon(array, x0b, y0b, cxb, cyb, x1b, y1b);
			if (linePolygon(x0a, y0a, x1a, y1a, array, polyBSize) == false)
				return false;

			double theta = -Math.atan2(y1a - y0a, x1a - x0a);
			array[0] = x0b;
			array[1] = y0b;
			array[2] = cxb;
			array[3] = cyb;
			array[4] = x1b;
			array[5] = y1b;
			array[6] = x0a;
			array[7] = y0a;
			array[8] = x1a;
			array[9] = y1a;

			double sin = Math.sin(theta);
			double cos = Math.cos(theta);

			double m00 = cos;
			double m01 = -sin;
			double m10 = sin;
			double m11 = cos;

			for (int a = 0; a < 5; a++) {
				double x = m00 * array[2 * a] + m01 * array[2 * a + 1];
				double y = m10 * array[2 * a] + m11 * array[2 * a + 1];
				array[2 * a] = x;
				array[2 * a + 1] = y;
			}

			x0b = array[0];
			y0b = array[1];
			cxb = array[2];
			cyb = array[3];
			x1b = array[4];
			y1b = array[5];
			x0a = array[6];
			y0a = array[7];
			x1a = array[8];
			y1a = array[9];

			double minX = x0a < x1a ? x0a : x1a;
			double maxX = x0a < x1a ? x1a : x0a;

			double ax = x0b - 2 * cxb + x1b;
			double bx = -2 * x0b + 2 * cxb;
			double cx = x0b;

			double ay = y0b - 2 * cyb + y1b;
			double by = -2 * y0b + 2 * cyb;
			double cy = y0b;

			array[2] = ay;
			array[1] = by;
			array[0] = cy - y1a;

			int results = QuadCurve2D.solveQuadratic(array);
			for (int a = 0; a < results; a++) {
				if (array[a] >= 0 && array[a] <= 1) {
					double x = (ax * array[a] + bx) * array[a] + cx;
					if (minX <= x && x <= maxX)
						return true;
				}
			}

			return false;
		} finally {
			doubleArrays.push(array);
		}
	}

	/**
	 * Adds a value to a sorted list. If the value already exists: it is
	 * ignored.
	 * 
	 * @param array
	 *            the array the list is stored in
	 * @param size
	 *            the number of elements in the list
	 * @param newValue
	 *            the new value to insert into the list.
	 * @return the size of the list. This may not change if you asked to add a
	 *         value that already exists.
	 */
	private static int add(double[] array, int size, double newValue) {
		// TODO: improve this
		array[size] = newValue;
		size++;
		Arrays.sort(array, 0, size);
		return size;
	}

	public static boolean quadQuad(double x0a, double y0a, double cxa,
			double cya, double x1a, double y1a, double x0b, double y0b,
			double cxb, double cyb, double x1b, double y1b) {
		if (x0a == x0b && y0a == y0b && cxa == cxb && cya == cyb && x1a == x1b
				&& y1a == y1b)
			return true;

		double[] array1, array2;
		synchronized (doubleArrays) {
			if (doubleArrays.size() <= 1) {
				array1 = new double[12];
				array2 = new double[12];
			} else {
				array1 = doubleArrays.pop();
				array2 = doubleArrays.pop();
			}
		}
		try {
			int polyASize = definePolygon(array1, x0a, y0a, cxa, cya, x1a, y1a);
			int polyBSize = definePolygon(array2, x0b, y0b, cxb, cyb, x1b, y1b);
			if (polygonPolygon(array1, polyASize, array2, polyBSize) == false)
				return false;

			double ax0 = x0a - 2 * cxa + x1a;
			double bx0 = -2 * x0a + 2 * cxa;
			double cx0 = x0a;

			double ay0 = y0a - 2 * cya + y1a;
			double by0 = -2 * y0a + 2 * cya;
			double cy0 = y0a;

			double ax1 = x0b - 2 * cxb + x1b;
			double bx1 = -2 * x0b + 2 * cxb;
			double cx1 = x0b;

			double ay1 = y0b - 2 * cyb + y1b;
			double by1 = -2 * y0b + 2 * cyb;
			double cy1 = y0b;

			int size1 = getTimes(array1, ax0, bx0, cx0, ay0, by0, cy0);
			int size2 = getTimes(array2, ax1, bx1, cx1, ay1, by1, cy1);

			for (int i1 = 0; i1 < size1 - 1; i1++) {
				for (int i2 = 0; i2 < size2 - 1; i2++) {
					if (binary_search(0, ax0, bx0, cx0, 0, ay0, by0, cy0,
							array1[i1], array1[i1 + 1], 0, ax1, bx1, cx1, 0,
							ay1, by1, cy1, array2[i2], array2[i2 + 1]))
						return true;
				}
			}
			return false;
		} finally {
			doubleArrays.push(array1);
			doubleArrays.push(array2);
		}
	}

	/**
	 * This calculates the t-values a curve needs to be separated into to
	 * isolate all critical points. For quadratic curves this may return a list
	 * of 4 items (2 times will always be zero and one).
	 * 
	 * @param dest
	 *            the array the list is stored in.
	 * @return the number of items in this list.
	 */
	private static int getTimes(double[] dest, double ax, double bx, double cx,
			double ay, double by, double cy) {
		dest[0] = 0;
		dest[1] = 1;
		int size = 2;

		double t = -bx / (2 * ax);

		if (t > 0 && t < 1)
			size = add(dest, size, t);

		t = -by / (2 * ay);

		if (t > 0 && t < 1)
			size = add(dest, size, t);

		return size;
	}

	/**
	 * This calculates the t-values a curve needs to be separated into to
	 * isolate all critical points. For cubic curves this may return a list of 6
	 * items (2 times will always be zero and one).
	 * 
	 * @param dest
	 *            the array the list is stored in.
	 * @return the number of items in this list.
	 */
	private static int getTimes(double[] dest, double ax, double bx, double cx,
			double dx, double ay, double by, double cy, double dy) {
		dest[0] = 0;
		dest[1] = 1;
		int size = 2;

		double det = 4 * bx * bx - 12 * ax * cx;
		if (det > 0) {
			det = Math.sqrt(det);
			double t = (-2 * bx + det) / (6 * ax);
			if (t > 0 && t < 1)
				size = add(dest, size, t);

			t = (-2 * bx - det) / (6 * ax);
			if (t > 0 && t < 1)
				size = add(dest, size, t);
		} else if (det == 0) {
			double t = (-2 * bx) / (6 * ax);

			if (t > 0 && t < 1)
				size = add(dest, size, t);
		}

		det = 4 * by * by - 12 * ay * cy;
		if (det > 0) {
			det = Math.sqrt(det);
			double t = (-2 * by + det) / (6 * ay);
			if (t > 0 && t < 1)
				size = add(dest, size, t);

			t = (-2 * by - det) / (6 * ay);
			if (t > 0 && t < 1)
				size = add(dest, size, t);
		} else if (det == 0) {
			double t = (-2 * by) / (6 * ay);

			if (t > 0 && t < 1)
				size = add(dest, size, t);
		}

		return size;
	}

	/**
	 * Checks if the rectangles formed by 2 line segments intersect.
	 * 
	 * @return whether the rectangle (x0, y0)->(x1, y1) intersects the rectangle
	 *         (x2, y2)->(x3, y3)
	 */
	private static boolean intersects(double x0, double y0, double x1,
			double y1, double x2, double y2, double x3, double y3) {
		double minXA, maxXA, minYA, maxYA;
		if (x0 < x1) {
			minXA = x0;
			maxXA = x1;
		} else {
			minXA = x1;
			maxXA = x0;
		}
		if (y0 < y1) {
			minYA = y0;
			maxYA = y1;
		} else {
			minYA = y1;
			maxYA = y0;
		}

		double minXB, maxXB, minYB, maxYB;
		if (x2 < x3) {
			minXB = x2;
			maxXB = x3;
		} else {
			minXB = x3;
			maxXB = x2;
		}
		if (y2 < y3) {
			minYB = y2;
			maxYB = y3;
		} else {
			minYB = y3;
			maxYB = y2;
		}

		return (maxXA > minXB && maxYA > minYB && minXA < maxXB && minYA < maxYB);
	}

	private static double TOLERANCE = .0000000001;

	/**
	 * This searches two cubic curves for a possible intersection. It is
	 * essential that the values from [startT, endT] move in only 1 direction:
	 * it is the caller's responsibility to separate critical points in the
	 * curve so this method is used correctly.
	 * <p>
	 * This method is also applied to quadratic curves by defining the a terms
	 * as zero.
	 * 
	 * @param ax0
	 *            the coefficient of the t^3 term in the first x parametric
	 *            equation.
	 * @param bx0
	 *            the coefficient of the t^2 term in the first x parametric
	 *            equation.
	 * @param cx0
	 *            the coefficient of the t^1 term in the first x parametric
	 *            equation.
	 * @param dx0
	 *            the coefficient of the t^0 term in the first x parametric
	 *            equation.
	 * @param ay0
	 *            the coefficient of the t^3 term in the first y parametric
	 *            equation.
	 * @param by0
	 *            the coefficient of the t^2 term in the first y parametric
	 *            equation.
	 * @param cy0
	 *            the coefficient of the t^1 term in the first y parametric
	 *            equation.
	 * @param dy0
	 *            the coefficient of the t^0 term in the first y parametric
	 *            equation.
	 * @param startT0
	 *            the min t-value to use in the first parametric curve.
	 * @param endT0
	 *            the max t-value to use in the first parametric curve.
	 * @param ax1
	 *            the coefficient of the t^3 term in the second x parametric
	 *            equation.
	 * @param bx1
	 *            the coefficient of the t^2 term in the second x parametric
	 *            equation.
	 * @param cx1
	 *            the coefficient of the t^1 term in the second x parametric
	 *            equation.
	 * @param dx1
	 *            the coefficient of the t^0 term in the second x parametric
	 *            equation.
	 * @param ay1
	 *            the coefficient of the t^3 term in the second y parametric
	 *            equation.
	 * @param by1
	 *            the coefficient of the t^2 term in the second y parametric
	 *            equation.
	 * @param cy1
	 *            the coefficient of the t^1 term in the second y parametric
	 *            equation.
	 * @param dy1
	 *            the coefficient of the t^0 term in the second y parametric
	 *            equation.
	 * @param startT1
	 *            the min t-value to use in the second parametric curve.
	 * @param endT1
	 *            the max t-value to use in the second parametric curve.
	 * @return whether the two curves intersect.
	 */
	private static boolean binary_search(double ax0, double bx0, double cx0,
			double dx0, double ay0, double by0, double cy0, double dy0,
			double startT0, double endT0, double ax1, double bx1, double cx1,
			double dx1, double ay1, double by1, double cy1, double dy1,
			double startT1, double endT1) {

		double startX0 = ((ax0 * startT0 + bx0) * startT0 + cx0) * startT0
				+ dx0;
		double startY0 = ((ay0 * startT0 + by0) * startT0 + cy0) * startT0
				+ dy0;
		double endX0 = ((ax0 * endT0 + bx0) * endT0 + cx0) * endT0 + dx0;
		double endY0 = ((ay0 * endT0 + by0) * endT0 + cy0) * endT0 + dy0;

		double startX1 = ((ax1 * startT1 + bx1) * startT1 + cx1) * startT1
				+ dx1;
		double startY1 = ((ay1 * startT1 + by1) * startT1 + cy1) * startT1
				+ dy1;
		double endX1 = ((ax1 * endT1 + bx1) * endT1 + cx1) * endT1 + dx1;
		double endY1 = ((ay1 * endT1 + by1) * endT1 + cy1) * endT1 + dy1;

		boolean intersects = intersects(startX0, startY0, endX0, endY0,
				startX1, startY1, endX1, endY1);

		if (!intersects)
			return false;

		/**
		 * When possible: use an iterative approach to refine t's. If that's not
		 * possible, use a recursive approach to explore entire quadrants.
		 */
		while (true) {
			double kx0 = startX0 - endX0;
			double ky0 = startY0 - endY0;
			double kx1 = startX1 - endX1;
			double ky1 = startY1 - endY1;
			if (kx0 < 0)
				kx0 = -kx0;
			if (ky0 < 0)
				ky0 = -ky0;
			if (kx1 < 0)
				kx1 = -kx1;
			if (ky1 < 0)
				ky1 = -ky1;

			// can we break each path into halves?
			boolean split0 = kx0 > TOLERANCE || ky0 > TOLERANCE;
			boolean split1 = kx1 > TOLERANCE || ky1 > TOLERANCE;

			if ((!split0) && (!split1)) {
				// don't split any more: we've really burrowed deep
				// and it's safe to assume there IS an intersection:
				return true;
			} else if (split0 && split1) {
				double midT0 = (startT0 + endT0) / 2;
				double midX0 = ((ax0 * midT0 + bx0) * midT0 + cx0) * midT0
						+ dx0;
				double midY0 = ((ay0 * midT0 + by0) * midT0 + cy0) * midT0
						+ dy0;

				double midT1 = (startT1 + endT1) / 2;
				double midX1 = ((ax1 * midT1 + bx1) * midT1 + cx1) * midT1
						+ dx1;
				double midY1 = ((ay1 * midT1 + by1) * midT1 + cy1) * midT1
						+ dy1;

				boolean intersects1 = intersects(startX0, startY0, midX0,
						midY0, startX1, startY1, midX1, midY1);
				boolean intersects2 = intersects(midX0, midY0, endX0, endY0,
						startX1, startY1, midX1, midY1);
				boolean intersects3 = intersects(startX0, startY0, midX0,
						midY0, midX1, midY1, endX1, endY1);
				boolean intersects4 = intersects(midX0, midY0, endX0, endY0,
						midX1, midY1, endX1, endY1);

				if ((intersects1) && (!intersects2) && (!intersects3)
						&& (!intersects4)) {
					endT0 = midT0;
					endX0 = midX0;
					endY0 = midY0;
					endT1 = midT1;
					endX1 = midX1;
					endY1 = midY1;
				} else if ((!intersects1) && (intersects2) && (!intersects3)
						&& (!intersects4)) {
					startT0 = midT0;
					startX0 = midX0;
					startY0 = midY0;
					endT1 = midT1;
					endX1 = midX1;
					endY1 = midY1;
				} else if ((!intersects1) && (!intersects2) && (intersects3)
						&& (!intersects4)) {
					endT0 = midT0;
					endX0 = midX0;
					endY0 = midY0;
					startT1 = midT1;
					startX1 = midX1;
					startY1 = midY1;
				} else if ((!intersects1) && (!intersects2) && (!intersects3)
						&& (intersects4)) {
					startT0 = midT0;
					startX0 = midX0;
					startY0 = midY0;
					startT1 = midT1;
					startX1 = midX1;
					startY1 = midY1;
				} else {
					if (intersects1
							&& binary_search(ax0, bx0, cx0, dx0, ay0, by0, cy0,
									dy0, startT0, midT0, ax1, bx1, cx1, dx1,
									ay1, by1, cy1, dy1, startT1, midT1))
						return true;

					if (intersects2
							&& binary_search(ax0, bx0, cx0, dx0, ay0, by0, cy0,
									dy0, midT0, endT0, ax1, bx1, cx1, dx1, ay1,
									by1, cy1, dy1, startT1, midT1))
						return true;

					if (intersects3
							&& binary_search(ax0, bx0, cx0, dx0, ay0, by0, cy0,
									dy0, startT0, midT0, ax1, bx1, cx1, dx1,
									ay1, by1, cy1, dy1, midT1, endT1))
						return true;

					if (intersects4
							&& binary_search(ax0, bx0, cx0, dx0, ay0, by0, cy0,
									dy0, midT0, endT0, ax1, bx1, cx1, dx1, ay1,
									by1, cy1, dy1, midT1, endT1))
						return true;

					return false;
				}
			} else if (split0) {
				double midT0 = (startT0 + endT0) / 2;
				double midX0 = ((ax0 * midT0 + bx0) * midT0 + cx0) * midT0
						+ dx0;
				double midY0 = ((ay0 * midT0 + by0) * midT0 + cy0) * midT0
						+ dy0;

				boolean intersects1 = intersects(startX0, startY0, midX0,
						midY0, startX1, startY1, endX1, endY1);
				boolean intersects2 = intersects(midX0, midY0, endX0, endY0,
						startX1, startY1, endX1, endY1);

				if ((intersects1) && (!intersects2)) {
					endT0 = midT0;
					endX0 = midX0;
					endY0 = midY0;
				} else if ((!intersects1) && (intersects2)) {
					startT0 = midT0;
					startX0 = midX0;
					startY0 = midY0;
				} else {
					if (intersects1
							&& binary_search(ax0, bx0, cx0, dx0, ay0, by0, cy0,
									dy0, startT0, midT0, ax1, bx1, cx1, dx1,
									ay1, by1, cy1, dy1, startT1, endT1))
						return true;

					if (intersects2
							&& binary_search(ax0, bx0, cx0, dx0, ay0, by0, cy0,
									dy0, midT0, endT0, ax1, bx1, cx1, dx1, ay1,
									by1, cy1, dy1, startT1, endT1))
						return true;

					return false;
				}
			} else if (split1) {
				double midT1 = (startT1 + endT1) / 2;
				double midX1 = ((ax1 * midT1 + bx1) * midT1 + cx1) * midT1
						+ dx1;
				double midY1 = ((ay1 * midT1 + by1) * midT1 + cy1) * midT1
						+ dy1;

				boolean intersects1 = intersects(startX0, startY0, endX0,
						endY0, startX1, startY1, midX1, midY1);
				boolean intersects2 = intersects(startX0, startY0, endX0,
						endY0, midX1, midY1, endX1, endY1);

				if ((intersects1) && (!intersects2)) {
					endT1 = midT1;
					endX1 = midX1;
					endY1 = midY1;
				} else if ((!intersects1) && (intersects2)) {
					startT1 = midT1;
					startX1 = midX1;
					startY1 = midY1;
				} else {
					if (intersects1
							&& binary_search(ax0, bx0, cx0, dx0, ay0, by0, cy0,
									dy0, startT0, endT0, ax1, bx1, cx1, dx1,
									ay1, by1, cy1, dy1, startT1, midT1))
						return true;

					if (intersects2
							&& binary_search(ax0, bx0, cx0, dx0, ay0, by0, cy0,
									dy0, startT0, endT0, ax1, bx1, cx1, dx1,
									ay1, by1, cy1, dy1, midT1, endT1))
						return true;

					return false;
				}
			}
		}
	}

	public static boolean quadCubic(double x0a, double y0a, double cxa,
			double cya, double x1a, double y1a, double x0b, double y0b,
			double cx0b, double cy0b, double cx1b, double cy1b, double x1b,
			double y1b) {
		double[] array1, array2;
		synchronized (doubleArrays) {
			if (doubleArrays.size() <= 1) {
				array1 = new double[12];
				array2 = new double[12];
			} else {
				array1 = doubleArrays.pop();
				array2 = doubleArrays.pop();
			}
		}
		try {
			int polyASize = definePolygon(array1, x0a, y0a, cxa, cya, x1a, y1a);
			int polyBSize = definePolygon(array2, x0b, y0b, cx0b, cy0b, cx1b,
					cy1b, x1b, y1b);
			if (polygonPolygon(array1, polyASize, array2, polyBSize) == false)
				return false;

			double ax0 = x0a - 2 * cxa + x1a;
			double bx0 = -2 * x0a + 2 * cxa;
			double cx0 = x0a;

			double ay0 = y0a - 2 * cya + y1a;
			double by0 = -2 * y0a + 2 * cya;
			double cy0 = y0a;

			double ax1 = -x0b + 3 * cx0b - 3 * cx1b + x1b;
			double bx1 = 3 * x0b - 6 * cx0b + 3 * cx1b;
			double cx1 = -3 * x0b + 3 * cx0b;
			double dx1 = x0b;

			double ay1 = -y0b + 3 * cy0b - 3 * cy1b + y1b;
			double by1 = 3 * y0b - 6 * cy0b + 3 * cy1b;
			double cy1 = -3 * y0b + 3 * cy0b;
			double dy1 = y0b;

			int size1 = getTimes(array1, ax0, bx0, cx0, ay0, by0, cy0);
			int size2 = getTimes(array2, ax1, bx1, cx1, dx1, ay1, by1, cy1, dy1);

			for (int i1 = 0; i1 < size1 - 1; i1++) {
				for (int i2 = 0; i2 < size2 - 1; i2++) {
					if (binary_search(0, ax0, bx0, cx0, 0, ay0, by0, cy0,
							array1[i1], array1[i1 + 1], ax1, bx1, cx1, dx1,
							ay1, by1, cy1, dy1, array2[i2], array2[i2 + 1]))
						return true;
				}
			}
			return false;
		} finally {
			doubleArrays.push(array1);
			doubleArrays.push(array2);
		}
	}

	public static boolean cubicCubic(double x0a, double y0a, double cx0a,
			double cy0a, double cx1a, double cy1a, double x1a, double y1a,
			double x0b, double y0b, double cx0b, double cy0b, double cx1b,
			double cy1b, double x1b, double y1b) {
		if (x0a == x0b && y0a == y0b && cx0a == cx0b && cy0a == cy0b
				&& cx1a == cx1b && cy1a == cy1b && x1a == x1b && y1a == y1b)
			return true;

		double[] array1, array2;
		synchronized (doubleArrays) {
			if (doubleArrays.size() <= 1) {
				array1 = new double[12];
				array2 = new double[12];
			} else {
				array1 = doubleArrays.pop();
				array2 = doubleArrays.pop();
			}
		}
		try {
			int polyASize = definePolygon(array1, x0a, y0a, cx0a, cy0a, cx1a,
					cy1a, x1a, y1a);
			int polyBSize = definePolygon(array2, x0b, y0b, cx0b, cy0b, cx1b,
					cy1b, x1b, y1b);
			if (polygonPolygon(array1, polyASize, array2, polyBSize) == false)
				return false;

			double ax0 = -x0a + 3 * cx0a - 3 * cx1a + x1a;
			double bx0 = 3 * x0a - 6 * cx0a + 3 * cx1a;
			double cx0 = -3 * x0a + 3 * cx0a;
			double dx0 = x0a;

			double ay0 = -y0a + 3 * cy0a - 3 * cy1a + y1a;
			double by0 = 3 * y0a - 6 * cy0a + 3 * cy1a;
			double cy0 = -3 * y0a + 3 * cy0a;
			double dy0 = y0a;

			double ax1 = -x0b + 3 * cx0b - 3 * cx1b + x1b;
			double bx1 = 3 * x0b - 6 * cx0b + 3 * cx1b;
			double cx1 = -3 * x0b + 3 * cx0b;
			double dx1 = x0b;

			double ay1 = -y0b + 3 * cy0b - 3 * cy1b + y1b;
			double by1 = 3 * y0b - 6 * cy0b + 3 * cy1b;
			double cy1 = -3 * y0b + 3 * cy0b;
			double dy1 = y0b;

			int size1 = getTimes(array1, ax0, bx0, cx0, dx0, ay0, by0, cy0, dy0);
			int size2 = getTimes(array2, ax1, bx1, cx1, dx1, ay1, by1, cy1, dy1);

			for (int i1 = 0; i1 < size1 - 1; i1++) {
				for (int i2 = 0; i2 < size2 - 1; i2++) {
					if (binary_search(ax0, bx0, cx0, dx0, ay0, by0, cy0, dy0,
							array1[i1], array1[i1 + 1], ax1, bx1, cx1, dx1,
							ay1, by1, cy1, dy1, array2[i2], array2[i2 + 1]))
						return true;
				}
			}
			return false;
		} finally {
			doubleArrays.push(array1);
			doubleArrays.push(array2);
		}
	}

	/** Defines a polygon for a quadratic curve. */
	private static int definePolygon(double[] array, double x0, double y0,
			double cx, double cy, double x1, double y1) {
		array[0] = x0;
		array[1] = y0;
		array[2] = cx;
		array[3] = cy;
		array[4] = x1;
		array[5] = y1;
		return 3;
	}

	/** Defines a polygon for a cubic curve. */
	private static int definePolygon(double[] array, double x0, double y0,
			double cx0, double cy0, double cx1, double cy1, double x1, double y1) {
		// first check to see if a triangle will do, instead of a quad:
		array[0] = x0;
		array[1] = y0;
		array[2] = cx1;
		array[3] = cy1;
		array[4] = x1;
		array[5] = y1;
		if (polygonContains(cx0, cy0, array, 3)) {
			return 3;
		}
		array[2] = cx0;
		array[3] = cy0;
		if (polygonContains(cx1, cy1, array, 3)) {
			return 3;
		}
		array[4] = cx1;
		array[5] = cy1;
		if (polygonContains(x1, y1, array, 3)) {
			return 3;
		}
		array[0] = x1;
		array[1] = y1;
		if (polygonContains(x0, y0, array, 3)) {
			return 3;
		}

		if (Line2D.relativeCCW(x0, y0, x1, y1, cx0, cy0) == Line2D.relativeCCW(
				x0, y0, x1, y1, cx1, cy1)) {
			if (Line2D.linesIntersect(x0, y0, cx0, cy0, cx1, cy1, x1, y1)) {
				array[0] = x0;
				array[1] = y0;
				array[2] = cx1;
				array[3] = cy1;
				array[4] = cx0;
				array[5] = cy0;
				array[6] = x1;
				array[7] = y1;
				return 4;
			}
			array[0] = x0;
			array[1] = y0;
			array[2] = cx0;
			array[3] = cy0;
			array[4] = cx1;
			array[5] = cy1;
			array[6] = x1;
			array[7] = y1;
			return 4;
		} else {
			array[0] = x0;
			array[1] = y0;
			array[2] = cx0;
			array[3] = cy0;
			array[4] = x1;
			array[5] = y1;
			array[6] = cx1;
			array[7] = cy1;
			return 4;
		}
	}

	protected static boolean linePolygon(double x0, double y0, double x1,
			double y1, double[] coords, int coordCount) {
		for (int a = 0; a < coordCount; a++) {
			int index1 = a;
			int index2 = (a + 1) % coordCount;
			if (Line2D.linesIntersect(x0, y0, x1, y1, coords[index1 * 2],
					coords[index1 * 2 + 1], coords[index2 * 2],
					coords[index2 * 2 + 1]))
				return true;
		}

		// OK, we confirmed there isn't an intersection. But the polygon might
		// still contain the original line:

		return polygonContains(x0, y0, coords, coordCount);
	}

	protected static boolean polygonPolygon(double[] coords1, int coordCount1,
			double[] coords2, int coordCount2) {
		for (int a = 0; a < coordCount1; a++) {
			int index1a = a;
			int index2a = (a + 1) % coordCount1;
			for (int b = 0; b < coordCount2; b++) {
				int index1b = b;
				int index2b = (b + 1) % coordCount2;
				if (Line2D.linesIntersect(coords1[index1a * 2],
						coords1[index1a * 2 + 1], coords1[index2a * 2],
						coords1[index2a * 2 + 1], coords2[index1b * 2],
						coords2[index1b * 2 + 1], coords2[index2b * 2],
						coords2[index2b * 2 + 1]))
					return true;
			}
		}

		// they don't intersect, but does one polygon contain the other?
		// We need to ask the bigger if it contains the smaller.
		// ... so first we identify the bigger:

		double minX1 = coords1[0];
		double maxX1 = coords1[0];
		double minX2 = coords2[0];
		double maxX2 = coords2[0];

		for (int a = 0; a < coordCount1; a++) {
			minX1 = Math.min(minX1, coords1[2 * a]);
			maxX1 = Math.max(maxX1, coords1[2 * a]);
		}
		for (int a = 0; a < coordCount2; a++) {
			minX2 = Math.min(minX2, coords2[2 * a]);
			maxX2 = Math.max(maxX2, coords2[2 * a]);
		}

		double width1 = maxX1 - minX1;
		double width2 = maxX2 - minX2;

		// now do the asking:
		if (width1 > width2) {
			return polygonContains(coords2[0], coords2[1], coords1, coordCount1);
		} else if (width2 > width1) {
			return polygonContains(coords1[0], coords1[1], coords2, coordCount2);
		}

		// they were equal? OK, do both:

		if (polygonContains(coords1[0], coords1[1], coords2, coordCount2))
			return true;
		if (polygonContains(coords2[0], coords2[1], coords1, coordCount1))
			return true;

		return false;
	}

	/**
	 * Determines if the specified coordinates are inside this
	 * <code>Polygon</code>. For the definition of <i>insideness</i>, see the
	 * class comments of <code>Shape</code>.
	 * <p>
	 * Copied form java.awt.Polygon.java.
	 * 
	 * @param x
	 *            the specified x coordinate
	 * @param y
	 *            the specified y coordinate
	 * @return <code>true</code> if the <code>Polygon</code> contains the
	 *         specified coordinates; <code>false</code> otherwise.
	 */
	protected static boolean polygonContains(double x, double y,
			double[] coords, int npoints) {
		int hits = 0;

		double lastx = coords[2 * npoints - 2];
		double lasty = coords[2 * npoints - 1];
		double curx, cury;

		// Walk the edges of the polygon
		for (int i = 0; i < npoints; lastx = curx, lasty = cury, i++) {
			curx = coords[2 * i];
			cury = coords[2 * i + 1];

			if (cury == lasty) {
				continue;
			}

			double leftx;
			if (curx < lastx) {
				if (x >= lastx) {
					continue;
				}
				leftx = curx;
			} else {
				if (x >= curx) {
					continue;
				}
				leftx = lastx;
			}

			double test1, test2;
			if (cury < lasty) {
				if (y < cury || y >= lasty) {
					continue;
				}
				if (x < leftx) {
					hits++;
					continue;
				}
				test1 = x - curx;
				test2 = y - cury;
			} else {
				if (y < lasty || y >= cury) {
					continue;
				}
				if (x < leftx) {
					hits++;
					continue;
				}
				test1 = x - lastx;
				test2 = y - lasty;
			}

			if (test1 < (test2 / (lasty - cury) * (lastx - curx))) {
				hits++;
			}
		}

		return ((hits & 1) != 0);
	}

	public static boolean lineCubic(double x0a, double y0a, double x1a,
			double y1a, double x0b, double y0b, double cx0b, double cy0b,
			double cx1b, double cy1b, double x1b, double y1b) {
		double[] array;
		synchronized (doubleArrays) {
			if (doubleArrays.size() == 0) {
				array = new double[12];
			} else {
				array = doubleArrays.pop();
			}
		}
		try {
			int polySize = definePolygon(array, x0b, y0b, cx0b, cy0b, cx1b,
					cy1b, x1b, y1b);
			if (linePolygon(x0a, y0a, x1a, y1a, array, polySize) == false)
				return false;

			double theta = -Math.atan2(y1a - y0a, x1a - x0a);
			array[0] = x0b;
			array[1] = y0b;
			array[2] = cx0b;
			array[3] = cy0b;
			array[4] = cx1b;
			array[5] = cy1b;
			array[6] = x1b;
			array[7] = y1b;
			array[8] = x0a;
			array[9] = y0a;
			array[10] = x1a;
			array[11] = y1a;

			double sin = Math.sin(theta);
			double cos = Math.cos(theta);

			double m00 = cos;
			double m01 = -sin;
			double m10 = sin;
			double m11 = cos;

			for (int a = 0; a < 6; a++) {
				double x = m00 * array[2 * a] + m01 * array[2 * a + 1];
				double y = m10 * array[2 * a] + m11 * array[2 * a + 1];
				array[2 * a] = x;
				array[2 * a + 1] = y;
			}

			x0b = array[0];
			y0b = array[1];
			cx0b = array[2];
			cy0b = array[3];
			cx1b = array[4];
			cy1b = array[5];
			x1b = array[6];
			y1b = array[7];
			x0a = array[8];
			y0a = array[9];
			x1a = array[10];
			y1a = array[11];

			double minX = x0a < x1a ? x0a : x1a;
			double maxX = x0a < x1a ? x1a : x0a;

			double ax = -x0b + 3 * cx0b - 3 * cx1b + x1b;
			double bx = 3 * x0b - 6 * cx0b + 3 * cx1b;
			double cx = -3 * x0b + 3 * cx0b;
			double dx = x0b;

			double ay = -y0b + 3 * cy0b - 3 * cy1b + y1b;
			double by = 3 * y0b - 6 * cy0b + 3 * cy1b;
			double cy = -3 * y0b + 3 * cy0b;
			double dy = y0b;

			array[3] = ay;
			array[2] = by;
			array[1] = cy;
			array[0] = dy - y1a;

			int results = CubicCurve2D.solveCubic(array);
			for (int a = 0; a < results; a++) {
				if (array[a] >= 0 && array[a] <= 1) {
					double x = ((ax * array[a] + bx) * array[a] + cx)
							* array[a] + dx;
					if (minX <= x && x <= maxX)
						return true;
				}
			}

			return false;
		} finally {
			doubleArrays.push(array);
		}
	}
}