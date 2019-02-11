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
package com.pump.geom.intersect;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;

/**
 * This identifies intersections of parametric path segments. The intersections
 * should be sorted by ascending t1-values.
 * <P>
 * It is assumed that we're only interested with t=[0,1] for both segments, so
 * intersections outside of this range are ignored.
 *
 * TODO: This uses the {@link BinarySearchIntersectionIdentifier}, which
 * contains a warning.
 */
abstract public class IntersectionIdentifier {

	public static enum Return {
		/**
		 * This return type stores 2 double values: an x-coordinate and a
		 * y-coordinate.
		 */
		X_Y(2),

		/**
		 * This return type stores 4 double values: an x-coordinate, a
		 * y-coordinate, the t-value from the first segment, and the t-value
		 * from the second segment.
		 */
		X_Y_T1_T2(4),

		/**
		 * This return type stores 2 double values: the first is the t-value
		 * from the first segment, and the second is the t-value from the second
		 * segment.
		 */
		T1_T2(2);
		int length;

		Return(int length) {
			this.length = length;
		}

		public int getLength() {
			return length;
		}
	}

	static private IntersectionIdentifier i = new BinarySearchIntersectionIdentifier();

	public static IntersectionIdentifier get() {
		return i;
	}

	public static void set(IntersectionIdentifier i) {
		if (i == null)
			throw new NullPointerException();
		IntersectionIdentifier.i = i;
	}

	public static abstract class SimpleIntersectionListener implements
			IntersectionListener {

		public void lineLineIntersection(double[] results, int size,
				int segmentIndex1, int segmentIndex2, double lastX1,
				double lastY1, double x1, double y1, double lastX2,
				double lastY2, double x2, double y2) {
			intersection(results[0], results[1], results[2], results[3],
					segmentIndex1, segmentIndex2);
		}

		public void lineQuadraticIntersection(double[] results, int size,
				int segmentIndex1, int segmentIndex2, double lastX1,
				double lastY1, double x1, double y1, double lastX2,
				double lastY2, double cx2, double cy2, double x2, double y2) {
			intersection(results[0], results[1], results[2], results[3],
					segmentIndex1, segmentIndex2);
		}

		public void lineCubicIntersection(double[] results, int size,
				int segmentIndex1, int segmentIndex2, double lastX1,
				double lastY1, double x1, double y1, double lastX2,
				double lastY2, double cx2a, double cy2a, double cx2b,
				double cy2b, double x2, double y2) {
			intersection(results[0], results[1], results[2], results[3],
					segmentIndex1, segmentIndex2);
		}

		public void quadraticLineIntersection(double[] results, int size,
				int segmentIndex1, int segmentIndex2, double lastX1,
				double lastY1, double cx1, double cy1, double x1, double y1,
				double lastX2, double lastY2, double x2, double y2) {
			intersection(results[0], results[1], results[2], results[3],
					segmentIndex1, segmentIndex2);
		}

		public void quadraticQuadraticIntersection(double[] results, int size,
				int segmentIndex1, int segmentIndex2, double lastX1,
				double lastY1, double cx1, double cy1, double x1, double y1,
				double lastX2, double lastY2, double cx2, double cy2,
				double x2, double y2) {
			intersection(results[0], results[1], results[2], results[3],
					segmentIndex1, segmentIndex2);
		}

		public void quadraticCubicIntersection(double[] results, int size,
				int segmentIndex1, int segmentIndex2, double lastX1,
				double lastY1, double cx1, double cy1, double x1, double y1,
				double lastX2, double lastY2, double cx2a, double cy2a,
				double cx2b, double cy2b, double x2, double y2) {
			intersection(results[0], results[1], results[2], results[3],
					segmentIndex1, segmentIndex2);
		}

		public void cubicLineIntersection(double[] results, int size,
				int segmentIndex1, int segmentIndex2, double lastX1,
				double lastY1, double cx1a, double cy1a, double cx1b,
				double cy1b, double x1, double y1, double lastX2,
				double lastY2, double x2, double y2) {
			intersection(results[0], results[1], results[2], results[3],
					segmentIndex1, segmentIndex2);
		}

		public void cubicCubicIntersection(double[] results, int size,
				int segmentIndex1, int segmentIndex2, double lastX1,
				double lastY1, double cx1a, double cy1a, double cx1b,
				double cy1b, double x1, double y1, double lastX2,
				double lastY2, double cx2a, double cy2a, double cx2b,
				double cy2b, double x2, double y2) {
			intersection(results[0], results[1], results[2], results[3],
					segmentIndex1, segmentIndex2);
		}

		public void cubicQuadraticIntersection(double[] results, int size,
				int segmentIndex1, int segmentIndex2, double lastX1,
				double lastY1, double cx1a, double cy1a, double cx1b,
				double cy1b, double x1, double y1, double lastX2,
				double lastY2, double cx2, double cy2, double x2, double y2) {
			intersection(results[0], results[1], results[2], results[3],
					segmentIndex1, segmentIndex2);
		}

		public abstract void intersection(double x, double y, double t1,
				double t2, int segmentIndex1, int segmentIndex2);
	}

	/**
	 * An interface to receive intersection information as it is observed.
	 */
	public static interface IntersectionListener {

		/**
		 * This method is invoked for intersections where the segment from the
		 * first shape is a line segment and the segment from the second shape
		 * is a line segment.
		 * 
		 * @param results
		 *            the results array. Every 4 numbers in this array contain
		 *            information about a single intersection: { x-value,
		 *            y-value, t1, t2}
		 * @param size
		 *            the number of intersections presented in the results
		 *            array.
		 * @param segmentIndex1
		 *            the index of the segment within the first shape.
		 * @param segmentIndex2
		 *            the index of the segment within the second shape.
		 * @param lastX1
		 *            the starting x-value of the the first segment.
		 * @param lastY1
		 *            the starting y-value of the the first segment.
		 * @param x1
		 *            the ending x-value of the first segment.
		 * @param y1
		 *            the ending y-value of the first segment.
		 * @param lastX2
		 *            the starting x-value of the the second segment.
		 * @param lastY2
		 *            the starting y-value of the the second segment.
		 * @param x2
		 *            the ending x-value of the second segment.
		 * @param y2
		 *            the ending y-value of the second segment.
		 */
		void lineLineIntersection(double[] results, int size,
				int segmentIndex1, int segmentIndex2, double lastX1,
				double lastY1, double x1, double y1, double lastX2,
				double lastY2, double x2, double y2);

		/**
		 * This method is invoked for intersections where the segment from the
		 * first shape is a line segment and the segment from the second shape
		 * is a quadratic segment.
		 * 
		 * @param results
		 *            the results array. Every 4 numbers in this array contain
		 *            information about a single intersection: { x-value,
		 *            y-value, t1, t2}
		 * @param size
		 *            the number of intersections presented in the results
		 *            array.
		 * @param segmentIndex1
		 *            the index of the segment within the first shape.
		 * @param segmentIndex2
		 *            the index of the segment within the second shape.
		 * @param lastX1
		 *            the starting x-value of the the first segment.
		 * @param lastY1
		 *            the starting y-value of the the first segment.
		 * @param x1
		 *            the ending x-value of the first segment.
		 * @param y1
		 *            the ending y-value of the first segment.
		 * @param lastX2
		 *            the starting x-value of the the second segment.
		 * @param lastY2
		 *            the starting y-value of the the second segment.
		 * @param cx2
		 *            the x-value of the bezier control point in the second
		 *            segment.
		 * @param cy2
		 *            the y-value of the bezier control point in the second
		 *            segment.
		 * @param x2
		 *            the ending x-value of the second segment.
		 * @param y2
		 *            the ending y-value of the second segment.
		 */
		void lineQuadraticIntersection(double[] results, int size,
				int segmentIndex1, int segmentIndex2, double lastX1,
				double lastY1, double x1, double y1, double lastX2,
				double lastY2, double cx2, double cy2, double x2, double y2);

		/**
		 * This method is invoked for intersections where the segment from the
		 * first shape is a line segment and the segment from the second shape
		 * is a cubic segment.
		 * 
		 * @param results
		 *            the results array. Every 4 numbers in this array contain
		 *            information about a single intersection: { x-value,
		 *            y-value, t1, t2}
		 * @param size
		 *            the number of intersections presented in the results
		 *            array.
		 * @param segmentIndex1
		 *            the index of the segment within the first shape.
		 * @param segmentIndex2
		 *            the index of the segment within the second shape.
		 * @param lastX1
		 *            the starting x-value of the the first segment.
		 * @param lastY1
		 *            the starting y-value of the the first segment.
		 * @param x1
		 *            the ending x-value of the first segment.
		 * @param y1
		 *            the ending y-value of the first segment.
		 * @param lastX2
		 *            the starting x-value of the the second segment.
		 * @param lastY2
		 *            the starting y-value of the the second segment.
		 * @param cx2a
		 *            the x-value of the first bezier control point in the
		 *            second segment.
		 * @param cy2a
		 *            the y-value of the first bezier control point in the
		 *            second segment.
		 * @param cx2b
		 *            the x-value of the second bezier control point in the
		 *            second segment.
		 * @param cy2b
		 *            the y-value of the second bezier control point in the
		 *            second segment.
		 * @param x2
		 *            the ending x-value of the second segment.
		 * @param y2
		 *            the ending y-value of the second segment.
		 */
		void lineCubicIntersection(double[] results, int size,
				int segmentIndex1, int segmentIndex2, double lastX1,
				double lastY1, double x1, double y1, double lastX2,
				double lastY2, double cx2a, double cy2a, double cx2b,
				double cy2b, double x2, double y2);

		/**
		 * This method is invoked for intersections where the segment from the
		 * first shape is a quadratic segment and the segment from the second
		 * shape is a line segment.
		 * 
		 * @param results
		 *            the results array. Every 4 numbers in this array contain
		 *            information about a single intersection: { x-value,
		 *            y-value, t1, t2}
		 * @param size
		 *            the number of intersections presented in the results
		 *            array.
		 * @param segmentIndex1
		 *            the index of the segment within the first shape.
		 * @param segmentIndex2
		 *            the index of the segment within the second shape.
		 * @param lastX1
		 *            the starting x-value of the the first segment.
		 * @param lastY1
		 *            the starting y-value of the the first segment.
		 * @param cx1
		 *            the x-value of the bezier control point in the first
		 *            segment.
		 * @param cy1
		 *            the y-value of the bezier control point in the first
		 *            segment.
		 * @param x1
		 *            the ending x-value of the first segment.
		 * @param y1
		 *            the ending y-value of the first segment.
		 * @param lastX2
		 *            the starting x-value of the the second segment.
		 * @param lastY2
		 *            the starting y-value of the the second segment.
		 * @param x2
		 *            the ending x-value of the second segment.
		 * @param y2
		 *            the ending y-value of the second segment.
		 */
		void quadraticLineIntersection(double[] results, int size,
				int segmentIndex1, int segmentIndex2, double lastX1,
				double lastY1, double cx1, double cy1, double x1, double y1,
				double lastX2, double lastY2, double x2, double y2);

		/**
		 * This method is invoked for intersections where the segment from the
		 * first shape is a quadratic segment and the segment from the second
		 * shape is a quadratic segment.
		 * 
		 * @param results
		 *            the results array. Every 4 numbers in this array contain
		 *            information about a single intersection: { x-value,
		 *            y-value, t1, t2}
		 * @param size
		 *            the number of intersections presented in the results
		 *            array.
		 * @param segmentIndex1
		 *            the index of the segment within the first shape.
		 * @param segmentIndex2
		 *            the index of the segment within the second shape.
		 * @param lastX1
		 *            the starting x-value of the the first segment.
		 * @param lastY1
		 *            the starting y-value of the the first segment.
		 * @param cx1
		 *            the x-value of the bezier control point in the first
		 *            segment.
		 * @param cy1
		 *            the y-value of the bezier control point in the first
		 *            segment.
		 * @param x1
		 *            the ending x-value of the first segment.
		 * @param y1
		 *            the ending y-value of the first segment.
		 * @param lastX2
		 *            the starting x-value of the the second segment.
		 * @param lastY2
		 *            the starting y-value of the the second segment.
		 * @param cx2
		 *            the x-value of the bezier control point in the second
		 *            segment.
		 * @param cy2
		 *            the y-value of the bezier control point in the second
		 *            segment.
		 * @param x2
		 *            the ending x-value of the second segment.
		 * @param y2
		 *            the ending y-value of the second segment.
		 */
		void quadraticQuadraticIntersection(double[] results, int size,
				int segmentIndex1, int segmentIndex2, double lastX1,
				double lastY1, double cx1, double cy1, double x1, double y1,
				double lastX2, double lastY2, double cx2, double cy2,
				double x2, double y2);

		/**
		 * This method is invoked for intersections where the segment from the
		 * first shape is a quadratic segment and the segment from the second
		 * shape is a cubic segment.
		 * 
		 * @param results
		 *            the results array. Every 4 numbers in this array contain
		 *            information about a single intersection: { x-value,
		 *            y-value, t1, t2}
		 * @param size
		 *            the number of intersections presented in the results
		 *            array.
		 * @param segmentIndex1
		 *            the index of the segment within the first shape.
		 * @param segmentIndex2
		 *            the index of the segment within the second shape.
		 * @param lastX1
		 *            the starting x-value of the the first segment.
		 * @param lastY1
		 *            the starting y-value of the the first segment.
		 * @param cx1
		 *            the x-value of the bezier control point in the first
		 *            segment.
		 * @param cy1
		 *            the y-value of the bezier control point in the first
		 *            segment.
		 * @param x1
		 *            the ending x-value of the first segment.
		 * @param y1
		 *            the ending y-value of the first segment.
		 * @param lastX2
		 *            the starting x-value of the the second segment.
		 * @param lastY2
		 *            the starting y-value of the the second segment.
		 * @param cx2a
		 *            the x-value of the first bezier control point in the
		 *            second segment.
		 * @param cy2a
		 *            the y-value of the first bezier control point in the
		 *            second segment.
		 * @param cx2b
		 *            the x-value of the second bezier control point in the
		 *            second segment.
		 * @param cy2b
		 *            the y-value of the second bezier control point in the
		 *            second segment.
		 * @param x2
		 *            the ending x-value of the second segment.
		 * @param y2
		 *            the ending y-value of the second segment.
		 */
		void quadraticCubicIntersection(double[] results, int size,
				int segmentIndex1, int segmentIndex2, double lastX1,
				double lastY1, double cx1, double cy1, double x1, double y1,
				double lastX2, double lastY2, double cx2a, double cy2a,
				double cx2b, double cy2b, double x2, double y2);

		/**
		 * This method is invoked for intersections where the segment from the
		 * first shape is a cubic segment and the segment from the second shape
		 * is a line segment.
		 * 
		 * @param results
		 *            the results array. Every 4 numbers in this array contain
		 *            information about a single intersection: { x-value,
		 *            y-value, t1, t2}
		 * @param size
		 *            the number of intersections presented in the results
		 *            array.
		 * @param segmentIndex1
		 *            the index of the segment within the first shape.
		 * @param segmentIndex2
		 *            the index of the segment within the second shape.
		 * @param lastX1
		 *            the starting x-value of the the first segment.
		 * @param lastY1
		 *            the starting y-value of the the first segment.
		 * @param cx1a
		 *            the x-value of the first bezier control point in the first
		 *            segment.
		 * @param cy1a
		 *            the y-value of the first bezier control point in the first
		 *            segment.
		 * @param cx1b
		 *            the x-value of the second bezier control point in the
		 *            first segment.
		 * @param cy1b
		 *            the y-value of the second bezier control point in the
		 *            first segment.
		 * @param x1
		 *            the ending x-value of the first segment.
		 * @param y1
		 *            the ending y-value of the first segment.
		 * @param lastX2
		 *            the starting x-value of the the second segment.
		 * @param lastY2
		 *            the starting y-value of the the second segment.
		 * @param x2
		 *            the ending x-value of the second segment.
		 * @param y2
		 *            the ending y-value of the second segment.
		 */
		void cubicLineIntersection(double[] results, int size,
				int segmentIndex1, int segmentIndex2, double lastX1,
				double lastY1, double cx1a, double cy1a, double cx1b,
				double cy1b, double x1, double y1, double lastX2,
				double lastY2, double x2, double y2);

		/**
		 * This method is invoked for intersections where the segment from the
		 * first shape is a cubic segment and the segment from the second shape
		 * is a cubic segment.
		 * 
		 * @param results
		 *            the results array. Every 4 numbers in this array contain
		 *            information about a single intersection: { x-value,
		 *            y-value, t1, t2}
		 * @param size
		 *            the number of intersections presented in the results
		 *            array.
		 * @param segmentIndex1
		 *            the index of the segment within the first shape.
		 * @param segmentIndex2
		 *            the index of the segment within the second shape.
		 * @param lastX1
		 *            the starting x-value of the the first segment.
		 * @param lastY1
		 *            the starting y-value of the the first segment.
		 * @param cx1a
		 *            the x-value of the first bezier control point in the first
		 *            segment.
		 * @param cy1a
		 *            the y-value of the first bezier control point in the first
		 *            segment.
		 * @param cx1b
		 *            the x-value of the second bezier control point in the
		 *            first segment.
		 * @param cy1b
		 *            the y-value of the second bezier control point in the
		 *            first segment.
		 * @param x1
		 *            the ending x-value of the first segment.
		 * @param y1
		 *            the ending y-value of the first segment.
		 * @param lastX2
		 *            the starting x-value of the the second segment.
		 * @param lastY2
		 *            the starting y-value of the the second segment.
		 * @param cx2a
		 *            the x-value of the first bezier control point in the
		 *            second segment.
		 * @param cy2a
		 *            the y-value of the first bezier control point in the
		 *            second segment.
		 * @param cx2b
		 *            the x-value of the second bezier control point in the
		 *            second segment.
		 * @param cy2b
		 *            the y-value of the second bezier control point in the
		 *            second segment.
		 * @param x2
		 *            the ending x-value of the second segment.
		 * @param y2
		 *            the ending y-value of the second segment.
		 */
		void cubicCubicIntersection(double[] results, int size,
				int segmentIndex1, int segmentIndex2, double lastX1,
				double lastY1, double cx1a, double cy1a, double cx1b,
				double cy1b, double x1, double y1, double lastX2,
				double lastY2, double cx2a, double cy2a, double cx2b,
				double cy2b, double x2, double y2);

		/**
		 * This method is invoked for intersections where the segment from the
		 * first shape is a cubic segment and the segment from the second shape
		 * is a quadratic segment.
		 * 
		 * @param results
		 *            the results array. Every 4 numbers in this array contain
		 *            information about a single intersection: { x-value,
		 *            y-value, t1, t2}
		 * @param size
		 *            the number of intersections presented in the results
		 *            array.
		 * @param segmentIndex1
		 *            the index of the segment within the first shape.
		 * @param segmentIndex2
		 *            the index of the segment within the second shape.
		 * @param lastX1
		 *            the starting x-value of the the first segment.
		 * @param lastY1
		 *            the starting y-value of the the first segment.
		 * @param cx1a
		 *            the x-value of the first bezier control point in the first
		 *            segment.
		 * @param cy1a
		 *            the y-value of the first bezier control point in the first
		 *            segment.
		 * @param cx1b
		 *            the x-value of the second bezier control point in the
		 *            first segment.
		 * @param cy1b
		 *            the y-value of the second bezier control point in the
		 *            first segment.
		 * @param x1
		 *            the ending x-value of the first segment.
		 * @param y1
		 *            the ending y-value of the first segment.
		 * @param lastX2
		 *            the starting x-value of the the second segment.
		 * @param lastY2
		 *            the starting y-value of the the second segment.
		 * @param cx2
		 *            the x-value of the bezier control point in the second
		 *            segment.
		 * @param cy2
		 *            the y-value of the bezier control point in the second
		 *            segment.
		 * @param x2
		 *            the ending x-value of the second segment.
		 * @param y2
		 *            the ending y-value of the second segment.
		 */
		void cubicQuadraticIntersection(double[] results, int size,
				int segmentIndex1, int segmentIndex2, double lastX1,
				double lastY1, double cx1a, double cy1a, double cx1b,
				double cy1b, double x1, double y1, double lastX2,
				double lastY2, double cx2, double cy2, double x2, double y2);

	}

	public void getIntersections(Shape shape1, Shape shape2,
			IntersectionListener listener) {
		getIntersections(shape1, null, shape2, null, listener);
	}

	public void getIntersections(Shape shape1, AffineTransform transform1,
			Shape shape2, AffineTransform transform2,
			IntersectionListener listener) {
		IntersectionIdentifier i = get();
		if (shape1 == null)
			throw new NullPointerException();
		if (shape2 == null)
			throw new NullPointerException();
		if (listener == null)
			throw new NullPointerException();
		double[] results = new double[36]; // (4 pieces of data) x (9 possible
											// intersections)
		double[] coords1 = new double[6];
		double[] coords2 = new double[6];
		PathIterator iter1 = shape1.getPathIterator(transform1);
		int segmentIndex1 = 0;
		double moveX1 = 0;
		double moveY1 = 0;
		double lastX1 = 0;
		double lastY1 = 0;
		double moveX2 = 0;
		double moveY2 = 0;
		double lastX2 = 0;
		double lastY2 = 0;
		while (!iter1.isDone()) {
			int k1 = iter1.currentSegment(coords1);
			if (k1 == PathIterator.SEG_MOVETO) {
				moveX1 = coords1[0];
				moveY1 = coords1[1];
			} else {
				PathIterator iter2 = shape2.getPathIterator(transform2);
				int segmentIndex2 = 0;
				while (!iter2.isDone()) {
					int k2 = iter2.currentSegment(coords2);
					if (k2 == PathIterator.SEG_MOVETO) {
						moveX2 = coords2[0];
						moveY2 = coords2[1];
					} else {
						boolean skip = false;
						if (k1 == PathIterator.SEG_CLOSE) {
							if (Math.abs(lastX1 - moveX1) < .0001
									&& Math.abs(lastY1 - moveY1) < .0001) {
								skip = true;
							} else {
								coords1[0] = moveX1;
								coords1[1] = moveY1;
								k1 = PathIterator.SEG_LINETO;
							}
						}
						if (k2 == PathIterator.SEG_CLOSE) {
							if (Math.abs(lastX2 - moveX2) < .0001
									&& Math.abs(lastY2 - moveY2) < .0001) {
								skip = true;
							} else {
								coords2[0] = moveX2;
								coords2[1] = moveY2;
								k2 = PathIterator.SEG_LINETO;
							}
						}

						if (!skip) {
							if (k1 == PathIterator.SEG_LINETO
									&& k2 == PathIterator.SEG_LINETO) {
								int size = i.lineLineBezier(lastX1, lastY1,
										coords1[0], coords1[1], lastX2, lastY2,
										coords2[0], coords2[1], results, 0,
										Return.X_Y_T1_T2);
								if (size > 0) {
									listener.lineLineIntersection(results,
											size, segmentIndex1, segmentIndex2,
											lastX1, lastY1, coords1[0],
											coords1[1], lastX2, lastY2,
											coords2[0], coords2[1]);
								}
							} else if (k1 == PathIterator.SEG_LINETO
									&& k2 == PathIterator.SEG_QUADTO) {
								int size = i.lineQuadraticBezier(lastX1,
										lastY1, coords1[0], coords1[1], lastX2,
										lastY2, coords2[0], coords2[1],
										coords2[2], coords2[3], results, 0,
										Return.X_Y_T1_T2);
								if (size > 0) {
									listener.lineQuadraticIntersection(results,
											size, segmentIndex1, segmentIndex2,
											lastX1, lastY1, coords1[0],
											coords1[1], lastX2, lastY2,
											coords2[0], coords2[1], coords2[2],
											coords2[3]);
								}
							} else if (k1 == PathIterator.SEG_LINETO
									&& k2 == PathIterator.SEG_CUBICTO) {
								int size = i.lineCubicBezier(lastX1, lastY1,
										coords1[0], coords1[1], lastX2, lastY2,
										coords2[0], coords2[1], coords2[2],
										coords2[3], coords2[4], coords2[5],
										results, 0, Return.X_Y_T1_T2);
								if (size > 0) {
									listener.lineCubicIntersection(results,
											size, segmentIndex1, segmentIndex2,
											lastX1, lastY1, coords1[0],
											coords1[1], lastX2, lastY2,
											coords2[0], coords2[1], coords2[2],
											coords2[3], coords2[4], coords2[5]);
								}
							} else if (k1 == PathIterator.SEG_QUADTO
									&& k2 == PathIterator.SEG_LINETO) {
								int size = i.lineQuadraticBezier(lastX2,
										lastY2, coords2[0], coords2[1], lastX1,
										lastY1, coords1[0], coords1[1],
										coords1[2], coords1[3], results, 0,
										Return.X_Y_T1_T2);
								if (size > 0) {
									swapTValues(results, size);
									listener.quadraticLineIntersection(results,
											size, segmentIndex1, segmentIndex2,
											lastX1, lastY1, coords1[0],
											coords1[1], coords1[2], coords1[3],
											lastX2, lastY2, coords2[0],
											coords2[1]);
								}
							} else if (k1 == PathIterator.SEG_QUADTO
									&& k2 == PathIterator.SEG_QUADTO) {
								int size = i.quadraticQuadraticBezier(lastX1,
										lastY1, coords1[0], coords1[1],
										coords1[2], coords1[3], lastX2, lastY2,
										coords2[0], coords2[1], coords2[2],
										coords2[3], results, 0,
										Return.X_Y_T1_T2);
								if (size > 0) {
									listener.quadraticQuadraticIntersection(
											results, size, segmentIndex1,
											segmentIndex2, lastX1, lastY1,
											coords1[0], coords1[1], coords1[2],
											coords1[3], lastX2, lastY2,
											coords2[0], coords2[1], coords2[2],
											coords2[3]);
								}
							} else if (k1 == PathIterator.SEG_QUADTO
									&& k2 == PathIterator.SEG_CUBICTO) {
								int size = i.quadraticCubicBezier(lastX1,
										lastY1, coords1[0], coords1[1],
										coords1[2], coords1[3], lastX2, lastY2,
										coords2[0], coords2[1], coords2[2],
										coords2[3], coords2[4], coords2[5],
										results, 0, Return.X_Y_T1_T2);
								if (size > 0) {
									listener.quadraticCubicIntersection(
											results, size, segmentIndex1,
											segmentIndex2, lastX1, lastY1,
											coords1[0], coords1[1], coords1[2],
											coords1[3], lastX2, lastY2,
											coords2[0], coords2[1], coords2[2],
											coords2[3], coords2[4], coords2[5]);
								}
							} else if (k1 == PathIterator.SEG_CUBICTO
									&& k2 == PathIterator.SEG_LINETO) {
								int size = i.lineCubicBezier(lastX2, lastY2,
										coords2[0], coords2[1], lastX1, lastY1,
										coords1[0], coords1[1], coords1[2],
										coords1[3], coords1[4], coords1[5],
										results, 0, Return.X_Y_T1_T2);
								if (size > 0) {
									swapTValues(results, size);
									listener.cubicLineIntersection(results,
											size, segmentIndex1, segmentIndex2,
											lastX1, lastY1, coords1[0],
											coords1[1], coords1[2], coords1[3],
											coords1[4], coords1[5], lastX2,
											lastY2, coords2[0], coords2[1]);
								}
							} else if (k1 == PathIterator.SEG_CUBICTO
									&& k2 == PathIterator.SEG_QUADTO) {
								int size = i.quadraticCubicBezier(lastX2,
										lastY2, coords2[0], coords2[1],
										coords2[2], coords2[3], lastX1, lastY1,
										coords1[0], coords1[1], coords1[2],
										coords1[3], coords1[4], coords1[5],
										results, 0, Return.X_Y_T1_T2);
								if (size > 0) {
									swapTValues(results, size);
									listener.cubicQuadraticIntersection(
											results, size, segmentIndex1,
											segmentIndex2, lastX1, lastY1,
											coords1[0], coords1[1], coords1[2],
											coords1[3], coords1[4], coords1[5],
											lastX2, lastY2, coords2[0],
											coords2[1], coords2[2], coords2[3]);
								}
							} else if (k1 == PathIterator.SEG_CUBICTO
									&& k2 == PathIterator.SEG_CUBICTO) {
								int size = i.cubicCubicBezier(lastX1, lastY1,
										coords1[0], coords1[1], coords1[2],
										coords1[3], coords1[4], coords1[5],
										lastX2, lastY2, coords2[0], coords2[1],
										coords2[2], coords2[3], coords2[4],
										coords2[5], results, 0,
										Return.X_Y_T1_T2);
								if (size > 0) {
									listener.cubicCubicIntersection(results,
											size, segmentIndex1, segmentIndex2,
											lastX1, lastY1, coords1[0],
											coords1[1], coords1[2], coords1[3],
											coords1[4], coords1[5], lastX2,
											lastY2, coords2[0], coords2[1],
											coords2[2], coords2[3], coords2[4],
											coords2[5]);
								}
							}
						}
					}

					if (k2 == PathIterator.SEG_CUBICTO) {
						lastX2 = coords2[4];
						lastY2 = coords2[5];
					} else if (k2 == PathIterator.SEG_CUBICTO) {
						lastX2 = coords2[2];
						lastY2 = coords2[3];
					} else if (k2 == PathIterator.SEG_LINETO
							|| k2 == PathIterator.SEG_MOVETO) {
						lastX2 = coords2[0];
						lastY2 = coords2[1];
					}
					iter2.next();
					segmentIndex2++;
				}
			}

			if (k1 == PathIterator.SEG_CUBICTO) {
				lastX1 = coords1[4];
				lastY1 = coords1[5];
			} else if (k1 == PathIterator.SEG_CUBICTO) {
				lastX1 = coords1[2];
				lastY1 = coords1[3];
			} else if (k1 == PathIterator.SEG_LINETO
					|| k1 == PathIterator.SEG_MOVETO) {
				lastX1 = coords1[0];
				lastY1 = coords1[1];
			}
			iter1.next();
			segmentIndex1++;
		}
	}

	private static void swapTValues(double[] results, int size) {
		for (int a = 0; a < size; a++) {
			double t = results[4 * a + 2];
			results[4 * a + 2] = results[4 * a + 3];
			results[4 * a + 3] = t;
		}
	}

	/**
	 * Identify the intersection(s) of two lines, expressed as parametric
	 * equations.
	 * 
	 * @param ax1
	 *            the coefficient "ax1" in the equation "x1(t1) = ax1*t1+bx1"
	 * @param bx1
	 *            the coefficient "bx1" in the equation "x1(t1) = ax1*t1+bx1"
	 * @param ay1
	 *            the coefficient "ay1" in the equation "y1(t1) = ay1*t1+by1"
	 * @param by1
	 *            the coefficient "by1" in the equation "y1(t1) = ay1*t1+by1"
	 * @param ax2
	 *            the coefficient "ax2" in the equation "x2(t2) = ax2*t2+bx2"
	 * @param bx2
	 *            the coefficient "bx2" in the equation "x2(t2) = ax2*t2+bx2"
	 * @param ay2
	 *            the coefficient "ay2" in the equation "y2(t2) = ay2*t2+by2"
	 * @param by2
	 *            the coefficient "by2" in the equation "y2(t2) = ay2*t2+by2"
	 * 
	 * @param dest
	 *            the destination to write the time intersections in.
	 * @param offset
	 *            the offset in the array to start writing at.
	 * @param returnType
	 *            the type of data to store in <code>dest</code>.
	 * @return the number of intersections found.
	 */
	public int lineLine(double ax1, double bx1, double ay1, double by1,
			double ax2, double bx2, double ay2, double by2, double[] dest,
			int offset, Return returnType) {
		double det = (ay1 * ax2 - ay2 * ax1);
		if (det == 0)
			return 0;
		double t1 = (ay2 * bx1 - ay2 * bx2 + by2 * ax2 - by1 * ax2) / det;
		double t2 = -(ay1 * bx2 - ay1 * bx1 + by1 * ax1 - by2 * ax1) / det;
		if ((t1 < 0 || t1 > 1) || (t2 < 0 && t2 > 1)) {
			return 0;
		}

		switch (returnType) {
		case T1_T2:
			dest[offset] = t1;
			dest[offset + 1] = t2;
			break;
		case X_Y_T1_T2:
			dest[offset] = ax1 * t1 + bx1;
			dest[offset + 1] = ay1 * t1 + by1;
			dest[offset + 2] = t1;
			dest[offset + 3] = t2;
			break;
		case X_Y:
			dest[offset] = ax1 * t1 + bx1;
			dest[offset + 1] = ay1 * t1 + by1;
			break;
		}

		return 1;
	}

	/**
	 * Identify the intersection(s) of two lines, expressed as bezier control
	 * points.
	 * 
	 * @param x0a
	 *            the x-coordinate of the first end point of the first line
	 * @param y0a
	 *            the y-coordinate of the first end point of the first line
	 * @param x1a
	 *            the x-coordinate of the second end point of the first line
	 * @param y1a
	 *            the y-coordinate of the second end point of the first line
	 * @param x0b
	 *            the x-coordinate of the first end point of the second line
	 * @param y0b
	 *            the y-coordinate of the first end point of the second line
	 * @param x1b
	 *            the x-coordinate of the second end point of the second line
	 * @param y1b
	 *            the y-coordinate of the second end point of the second line
	 * 
	 * @param dest
	 *            the destination to write the time intersections in.
	 * @param offset
	 *            the offset in the array to start writing at.
	 * @param returnType
	 *            the type of data to store in <code>dest</code>.
	 * @return the number of intersections found.
	 */
	public int lineLineBezier(double x0a, double y0a, double x1a, double y1a,
			double x0b, double y0b, double x1b, double y1b, double[] dest,
			int offset, Return returnType) {
		return lineLine(x1a - x0a, x0a, y1a - y0a, y0a, x1b - x0b, x0b, y1b
				- y0b, y0b, dest, offset, returnType);
	}

	/**
	 * Identify the intersection(s) of a line and a parabola, expressed as
	 * parametric equations.
	 * 
	 * @param ax1
	 *            the coefficient "ax1" in the equation "x1(t1) = ax1*t1+bx1"
	 * @param bx1
	 *            the coefficient "bx1" in the equation "x1(t1) = ax1*t1+bx1"
	 * @param ay1
	 *            the coefficient "ay1" in the equation "y1(t1) = ay1*t1+by1"
	 * @param by1
	 *            the coefficient "by1" in the equation "y1(t1) = ay1*t1+by1"
	 * @param ax2
	 *            the coefficient "ax2" in the equation
	 *            "x2(t2) = ax2*t2*t2+bx2*t2+cx2"
	 * @param bx2
	 *            the coefficient "bx2" in the equation
	 *            "x2(t2) = ax2*t2*t2+bx2*t2+cx2"
	 * @param cx2
	 *            the coefficient "cx2" in the equation
	 *            "x2(t2) = ax2*t2*t2+bx2*t2+cx2"
	 * @param ay2
	 *            the coefficient "ay2" in the equation
	 *            "y2(t2) = ay2*t2*t2+by2*t2+cy2"
	 * @param by2
	 *            the coefficient "by2" in the equation
	 *            "y2(t2) = ay2*t2*t2+by2*t2+cy2"
	 * @param cy2
	 *            the coefficient "cy2" in the equation
	 *            "y2(t2) = ay2*t2*t2+by2*t2+cy2"
	 * @param dest
	 *            the destination to write the time intersections in.
	 * @param offset
	 *            the offset in the array to start writing at.
	 * @param returnType
	 *            the type of data to store in <code>dest</code>.
	 * @return the number of intersections found.
	 */
	public abstract int lineQuadratic(double ax1, double bx1, double ay1,
			double by1, double ax2, double bx2, double cx2, double ay2,
			double by2, double cy2, double[] dest, int offset, Return returnType);

	/**
	 * Identify the intersection(s) of a line and a parabola, expressed as
	 * bezier control points.
	 * 
	 * @param x0a
	 *            the x-coordinate of the first end point of the line
	 * @param y0a
	 *            the y-coordinate of the first end point of the line
	 * @param x1a
	 *            the x-coordinate of the second end point of the line
	 * @param y1a
	 *            the y-coordinate of the second end point of the line
	 * 
	 * @param x0b
	 *            the x-coordinate of the first end point of the quadratic curve
	 * @param y0b
	 *            the y-coordinate of the first end point of the quadratic curve
	 * @param cxb
	 *            the x-coordinate of the control point of the quadratic curve
	 * @param cyb
	 *            the y-coordinate of the control point of the quadratic curve
	 * @param x1b
	 *            the x-coordinate of the second end point of the quadratic
	 *            curve
	 * @param y1b
	 *            the y-coordinate of the second end point of the quadratic
	 *            curve
	 * 
	 * @param dest
	 *            the destination to write the time intersections in.
	 * @param offset
	 *            the offset in the array to start writing at.
	 * @param returnType
	 *            the type of data to store in <code>dest</code>.
	 * @return the number of intersections found.
	 */
	public int lineQuadraticBezier(double x0a, double y0a, double x1a,
			double y1a, double x0b, double y0b, double cxb, double cyb,
			double x1b, double y1b, double[] dest, int offset, Return returnType) {
		return lineQuadratic(x1a - x0a, x0a, y1a - y0a, y0a, x0b - 2 * cxb
				+ x1b, -2 * x0b + 2 * cxb, x0b, y0b - 2 * cyb + y1b, -2 * y0b
				+ 2 * cyb, y0b, dest, offset, returnType);
	}

	/**
	 * Identify the intersection(s) of a line and a cubic curve, expressed as
	 * parametric equations.
	 * 
	 * @param ax1
	 *            the coefficient "ax1" in the equation "x1(t1) = ax1*t1+bx1"
	 * @param bx1
	 *            the coefficient "bx1" in the equation "x1(t1) = ax1*t1+bx1"
	 * @param ay1
	 *            the coefficient "ay1" in the equation "y1(t1) = ay1*t1+by1"
	 * @param by1
	 *            the coefficient "by1" in the equation "y1(t1) = ay1*t1+by1"
	 * 
	 * @param ax2
	 *            the coefficient "ax2" in the equation
	 *            "x2(t2) = ax2*t2*t2*t2+bx2*t2*t2+cx2*t2+dx2"
	 * @param bx2
	 *            the coefficient "bx2" in the equation
	 *            "x2(t2) = ax2*t2*t2*t2+bx2*t2*t2+cx2*t2+dx2"
	 * @param cx2
	 *            the coefficient "cx2" in the equation
	 *            "x2(t2) = ax2*t2*t2*t2+bx2*t2*t2+cx2*t2+dx2"
	 * @param dx2
	 *            the coefficient "dx2" in the equation
	 *            "x2(t2) = ax2*t2*t2*t2+bx2*t2*t2+cx2*t2+dx2"
	 * @param ay2
	 *            the coefficient "ay2" in the equation
	 *            "y2(t2) = ay2*t2*t2*t2+by2*t2*t2+cy2*t2+dy2"
	 * @param by2
	 *            the coefficient "by2" in the equation
	 *            "y2(t2) = ay2*t2*t2*t2+by2*t2*t2+cy2*t2+dy2"
	 * @param cy2
	 *            the coefficient "by2" in the equation
	 *            "y2(t2) = ay2*t2*t2*t2+by2*t2*t2+cy2*t2+dy2"
	 * @param dy2
	 *            the coefficient "dy2" in the equation
	 *            "y2(t2) = ay2*t2*t2*t2+by2*t2*t2+cy2*t2+dy2"
	 * 
	 * @param dest
	 *            the destination to write the time intersections in.
	 * @param offset
	 *            the offset in the array to start writing at.
	 * @param returnType
	 *            the type of data to store in <code>dest</code>.
	 * @return the number of intersections found.
	 */
	public abstract int lineCubic(double ax1, double bx1, double ay1,
			double by1, double ax2, double bx2, double cx2, double dx2,
			double ay2, double by2, double cy2, double dy2, double[] dest,
			int offset, Return returnType);

	/**
	 * Identify the intersection(s) of a line and a cubic curve, expressed as
	 * bezier control points.
	 * 
	 * @param x0a
	 *            the x-coordinate of the first end point of the line
	 * @param y0a
	 *            the y-coordinate of the first end point of the line
	 * @param x1a
	 *            the x-coordinate of the second end point of the line
	 * @param y1a
	 *            the y-coordinate of the second end point of the line
	 * 
	 * @param x0b
	 *            the x-coordinate of the first end point of the cubic curve
	 * @param y0b
	 *            the y-coordinate of the first end point of the cubic curve
	 * @param cx0b
	 *            the x-coordinate of the first control point of the cubic curve
	 * @param cy0b
	 *            the y-coordinate of the first control point of the cubic curve
	 * @param cx1b
	 *            the x-coordinate of the second control point of the cubic
	 *            curve
	 * @param cy1b
	 *            the y-coordinate of the second control point of the cubic
	 *            curve
	 * @param x1b
	 *            the x-coordinate of the second end point of the cubic curve
	 * @param y1b
	 *            the y-coordinate of the second end point of the cubic curve
	 * 
	 * @param dest
	 *            the destination to write the time intersections in.
	 * @param offset
	 *            the offset in the array to start writing at.
	 * @param returnType
	 *            the type of data to store in <code>dest</code>.
	 * @return the number of intersections found.
	 */
	public int lineCubicBezier(double x0a, double y0a, double x1a, double y1a,
			double x0b, double y0b, double cx0b, double cy0b, double cx1b,
			double cy1b, double x1b, double y1b, double[] dest, int offset,
			Return returnType) {
		return lineCubic(x1a - x0a, x0a, y1a - y0a, y0a, -x0b + 3 * cx0b - 3
				* cx1b + x1b, 3 * x0b - 6 * cx0b + 3 * cx1b, -3 * x0b + 3
				* cx0b, x0b, -y0b + 3 * cy0b - 3 * cy1b + y1b, 3 * y0b - 6
				* cy0b + 3 * cy1b, -3 * y0b + 3 * cy0b, y0b, dest, offset,
				returnType);
	}

	/**
	 * Identify the intersection(s) of two parabolas, expressed as parametric
	 * equations.
	 * 
	 * @param ax1
	 *            the coefficient "ax1" in the equation
	 *            "x1(t1) = ax1*t1*t1+bx1*t1+cx1"
	 * @param bx1
	 *            the coefficient "bx1" in the equation
	 *            "x1(t1) = ax1*t1*t1+bx1*t1+cx1"
	 * @param cx1
	 *            the coefficient "cx1" in the equation
	 *            "x1(t1) = ax1*t1*t1+bx1*t1+cx1"
	 * @param ay1
	 *            the coefficient "ay1" in the equation
	 *            "y1(t1) = ay1*t1*t1+by1*t1+cy1"
	 * @param by1
	 *            the coefficient "by1" in the equation
	 *            "y1(t1) = ay1*t1*t1+by1*t1+cy1"
	 * @param cy1
	 *            the coefficient "by1" in the equation
	 *            "y1(t1) = ay1*t1*t1+by1*t1+cy1"
	 * 
	 * @param ax2
	 *            the coefficient "ax2" in the equation
	 *            "x2(t2) = ax2*t2*t2+bx2*t2+cx2"
	 * @param bx2
	 *            the coefficient "bx2" in the equation
	 *            "x2(t2) = ax2*t2*t2+bx2*t2+cx2"
	 * @param cx2
	 *            the coefficient "cx2" in the equation
	 *            "x2(t2) = ax2*t2*t2+bx2*t2+cx2"
	 * @param ay2
	 *            the coefficient "ay2" in the equation
	 *            "y2(t2) = ay2*t2*t2+by2*t2+cy2"
	 * @param by2
	 *            the coefficient "by2" in the equation
	 *            "y2(t2) = ay2*t2*t2+by2*t2+cy2"
	 * @param cy2
	 *            the coefficient "cy2" in the equation
	 *            "y2(t2) = ay2*t2*t2+by2*t2+cy2"
	 * 
	 * @param dest
	 *            the destination to write the time intersections in.
	 * @param offset
	 *            the offset in the array to start writing at.
	 * @param returnType
	 *            the type of data to store in <code>dest</code>.
	 * @return the number of intersections found.
	 */
	public abstract int quadraticQuadratic(double ax1, double bx1, double cx1,
			double ay1, double by1, double cy1, double ax2, double bx2,
			double cx2, double ay2, double by2, double cy2, double[] dest,
			int offset, Return returnType);

	/**
	 * Identify the intersection(s) of two parabolas, expressed as bezier
	 * control points.
	 * 
	 * 
	 * @param x0a
	 *            the x-coordinate of the first end point of the first quadratic
	 *            curve
	 * @param y0a
	 *            the y-coordinate of the first end point of the first quadratic
	 *            curve
	 * @param cxa
	 *            the x-coordinate of the control point of the first quadratic
	 *            curve
	 * @param cya
	 *            the y-coordinate of the control point of the first quadratic
	 *            curve
	 * @param x1a
	 *            the x-coordinate of the second end point of the first
	 *            quadratic curve
	 * @param y1a
	 *            the y-coordinate of the second end point of the first
	 *            quadratic curve
	 * 
	 * @param x0b
	 *            the x-coordinate of the first end point of the second
	 *            quadratic curve
	 * @param y0b
	 *            the y-coordinate of the first end point of the second
	 *            quadratic curve
	 * @param cxb
	 *            the x-coordinate of the control point of the second quadratic
	 *            curve
	 * @param cyb
	 *            the y-coordinate of the control point of the second quadratic
	 *            curve
	 * @param x1b
	 *            the x-coordinate of the second end point of the second
	 *            quadratic curve
	 * @param y1b
	 *            the y-coordinate of the second end point of the second
	 *            quadratic curve
	 * 
	 * @param dest
	 *            the destination to write the time intersections in.
	 * @param offset
	 *            the offset in the array to start writing at.
	 * @param returnType
	 *            the type of data to store in <code>dest</code>.
	 * @return the number of intersections found.
	 */
	public int quadraticQuadraticBezier(double x0a, double y0a, double cxa,
			double cya, double x1a, double y1a, double x0b, double y0b,
			double cxb, double cyb, double x1b, double y1b, double[] dest,
			int offset, Return returnType) {
		return quadraticQuadratic(x0a - 2 * cxa + x1a, -2 * x0a + 2 * cxa, x0a,
				y0a - 2 * cya + y1a, -2 * y0a + 2 * cya, y0a, x0b - 2 * cxb
						+ x1b, -2 * x0b + 2 * cxb, x0b, y0b - 2 * cyb + y1b, -2
						* y0b + 2 * cyb, y0b, dest, offset, returnType);
	}

	/**
	 * Identify the intersection(s) of a parabola and a cubic curve, expressed
	 * as parametric equations.
	 * 
	 * @param ax1
	 *            the coefficient "ax1" in the equation
	 *            "x1(t1) = ax1*t1*t1+bx1*t1+cx1"
	 * @param bx1
	 *            the coefficient "bx1" in the equation
	 *            "x1(t1) = ax1*t1*t1+bx1*t1+cx1"
	 * @param cx1
	 *            the coefficient "cx1" in the equation
	 *            "x1(t1) = ax1*t1*t1+bx1*t1+cx1"
	 * @param ay1
	 *            the coefficient "ay1" in the equation
	 *            "y1(t1) = ay1*t1*t1+by1*t1+cy1"
	 * @param by1
	 *            the coefficient "by1" in the equation
	 *            "y1(t1) = ay1*t1*t1+by1*t1+cy1"
	 * @param cy1
	 *            the coefficient "by1" in the equation
	 *            "y1(t1) = ay1*t1*t1+by1*t1+cy1"
	 * 
	 * @param ax2
	 *            the coefficient "ax2" in the equation
	 *            "x2(t2) = ax2*t2*t2*t2+bx2*t2*t2+cx2*t2+dx2"
	 * @param bx2
	 *            the coefficient "bx2" in the equation
	 *            "x2(t2) = ax2*t2*t2*t2+bx2*t2*t2+cx2*t2+dx2"
	 * @param cx2
	 *            the coefficient "cx2" in the equation
	 *            "x2(t2) = ax2*t2*t2*t2+bx2*t2*t2+cx2*t2+dx2"
	 * @param dx2
	 *            the coefficient "dx2" in the equation
	 *            "x2(t2) = ax2*t2*t2*t2+bx2*t2*t2+cx2*t2+dx2"
	 * @param ay2
	 *            the coefficient "ay2" in the equation
	 *            "y2(t2) = ay2*t2*t2*t2+by2*t2*t2+cy2*t2+dy2"
	 * @param by2
	 *            the coefficient "by2" in the equation
	 *            "y2(t2) = ay2*t2*t2*t2+by2*t2*t2+cy2*t2+dy2"
	 * @param cy2
	 *            the coefficient "by2" in the equation
	 *            "y2(t2) = ay2*t2*t2*t2+by2*t2*t2+cy2*t2+dy2"
	 * @param dy2
	 *            the coefficient "dy2" in the equation
	 *            "y2(t2) = ay2*t2*t2*t2+by2*t2*t2+cy2*t2+dy2"
	 * 
	 * @param dest
	 *            the destination to write the time intersections in.
	 * @param offset
	 *            the offset in the array to start writing at.
	 * @param returnType
	 *            the type of data to store in <code>dest</code>.
	 * @return the number of intersections found.
	 */
	public abstract int quadraticCubic(double ax1, double bx1, double cx1,
			double ay1, double by1, double cy1, double ax2, double bx2,
			double cx2, double dx2, double ay2, double by2, double cy2,
			double dy2, double[] dest, int offset, Return returnType);

	/**
	 * Identify the intersection(s) of a parabola and a cubic curve, expressed
	 * as bezier control points.
	 * 
	 * @param x0a
	 *            the x-coordinate of the first end point of the quadratic curve
	 * @param y0a
	 *            the y-coordinate of the first end point of the quadratic curve
	 * @param cxa
	 *            the x-coordinate of the control point of the quadratic curve
	 * @param cya
	 *            the y-coordinate of the control point of the quadratic curve
	 * @param x1a
	 *            the x-coordinate of the second end point of the quadratic
	 *            curve
	 * @param y1a
	 *            the y-coordinate of the second end point of the quadratic
	 *            curve
	 * 
	 * @param x0b
	 *            the x-coordinate of the first end point of the cubic curve
	 * @param y0b
	 *            the y-coordinate of the first end point of the cubic curve
	 * @param cx0b
	 *            the x-coordinate of the first control point of the cubic curve
	 * @param cy0b
	 *            the y-coordinate of the first control point of the cubic curve
	 * @param cx1b
	 *            the x-coordinate of the second control point of the cubic
	 *            curve
	 * @param cy1b
	 *            the y-coordinate of the second control point of the cubic
	 *            curve
	 * @param x1b
	 *            the x-coordinate of the second end point of the cubic curve
	 * @param y1b
	 *            the y-coordinate of the second end point of the cubic curve
	 * 
	 * @param dest
	 *            the destination to write the time intersections in.
	 * @param offset
	 *            the offset in the array to start writing at.
	 * @param returnType
	 *            the type of data to store in <code>dest</code>.
	 * @return the number of intersections found.
	 */
	public int quadraticCubicBezier(double x0a, double y0a, double cxa,
			double cya, double x1a, double y1a, double x0b, double y0b,
			double cx0b, double cy0b, double cx1b, double cy1b, double x1b,
			double y1b, double[] dest, int offset, Return returnType) {
		return quadraticCubic(x0a - 2 * cxa + x1a, -2 * x0a + 2 * cxa, x0a, y0a
				- 2 * cya + y1a, -2 * y0a + 2 * cya, y0a, -x0b + 3 * cx0b - 3
				* cx1b + x1b, 3 * x0b - 6 * cx0b + 3 * cx1b, -3 * x0b + 3
				* cx0b, x0b, -y0b + 3 * cy0b - 3 * cy1b + y1b, 3 * y0b - 6
				* cy0b + 3 * cy1b, -3 * y0b + 3 * cy0b, y0b, dest, offset,
				returnType);
	}

	/**
	 * Identify the intersection(s) of two cubic curves, expressed as parametric
	 * equations.
	 * 
	 * @param ax1
	 *            the coefficient "ax1" in the equation
	 *            "x1(t1) = ax1*t1*t1*t1+bx1*t1*t1+cx1*t1+dx1"
	 * @param bx1
	 *            the coefficient "bx1" in the equation
	 *            "x1(t1) = ax1*t1*t1*t1+bx1*t1*t1+cx1*t1+dx1"
	 * @param cx1
	 *            the coefficient "cx1" in the equation
	 *            "x1(t1) = ax1*t1*t1*t1+bx1*t1*t1+cx1*t1+dx1"
	 * @param dx1
	 *            the coefficient "dx1" in the equation
	 *            "x1(t1) = ax1*t1*t1*t1+bx1*t1*t1+cx1*t1+dx1"
	 * @param ay1
	 *            the coefficient "ay1" in the equation
	 *            "y1(t1) = ay1*t1*t1*t1+by1*t1*t1+cy1*t1+dy1"
	 * @param by1
	 *            the coefficient "by1" in the equation
	 *            "y1(t1) = ay1*t1*t1*t1+by1*t1*t1+cy1*t1+dy1"
	 * @param cy1
	 *            the coefficient "by1" in the equation
	 *            "y1(t1) = ay1*t1*t1*t1+by1*t1*t1+cy1*t1+dy1"
	 * @param dy1
	 *            the coefficient "dy1" in the equation
	 *            "y1(t1) = ay1*t1*t1*t1+by1*t1*t1+cy1*t1+dy1"
	 * 
	 * @param ax2
	 *            the coefficient "ax2" in the equation
	 *            "x2(t2) = ax2*t2*t2*t2+bx2*t2*t2+cx2*t2+dx2"
	 * @param bx2
	 *            the coefficient "bx2" in the equation
	 *            "x2(t2) = ax2*t2*t2*t2+bx2*t2*t2+cx2*t2+dx2"
	 * @param cx2
	 *            the coefficient "cx2" in the equation
	 *            "x2(t2) = ax2*t2*t2*t2+bx2*t2*t2+cx2*t2+dx2"
	 * @param dx2
	 *            the coefficient "dx2" in the equation
	 *            "x2(t2) = ax2*t2*t2*t2+bx2*t2*t2+cx2*t2+dx2"
	 * @param ay2
	 *            the coefficient "ay2" in the equation
	 *            "y2(t2) = ay2*t2*t2*t2+by2*t2*t2+cy2*t2+dy2"
	 * @param by2
	 *            the coefficient "by2" in the equation
	 *            "y2(t2) = ay2*t2*t2*t2+by2*t2*t2+cy2*t2+dy2"
	 * @param cy2
	 *            the coefficient "by2" in the equation
	 *            "y2(t2) = ay2*t2*t2*t2+by2*t2*t2+cy2*t2+dy2"
	 * @param dy2
	 *            the coefficient "dy2" in the equation
	 *            "y2(t2) = ay2*t2*t2*t2+by2*t2*t2+cy2*t2+dy2"
	 * 
	 * @param dest
	 *            the destination to write the time intersections in.
	 * @param offset
	 *            the offset in the array to start writing at.
	 * @param returnType
	 *            the type of data to store in <code>dest</code>.
	 * @return the number of intersections found.
	 */
	public abstract int cubicCubic(double ax1, double bx1, double cx1,
			double dx1, double ay1, double by1, double cy1, double dy1,
			double ax2, double bx2, double cx2, double dx2, double ay2,
			double by2, double cy2, double dy2, double[] dest, int offset,
			Return returnType);

	/**
	 * Identify the intersection(s) of two cubic curves, expressed as bezier
	 * control points.
	 * 
	 * @param x0a
	 *            the x-coordinate of the first end point of the first cubic
	 *            curve
	 * @param y0a
	 *            the y-coordinate of the first end point of the first cubic
	 *            curve
	 * @param cx0a
	 *            the x-coordinate of the first control point of the first cubic
	 *            curve
	 * @param cy0a
	 *            the y-coordinate of the first control point of the first cubic
	 *            curve
	 * @param cx1a
	 *            the x-coordinate of the second control point of the first
	 *            cubic curve
	 * @param cy1a
	 *            the y-coordinate of the second control point of the first
	 *            cubic curve
	 * @param x1a
	 *            the x-coordinate of the second end point of the first cubic
	 *            curve
	 * @param y1a
	 *            the y-coordinate of the second end point of the first cubic
	 *            curve
	 * 
	 * @param x0b
	 *            the x-coordinate of the first end point of the second cubic
	 *            curve
	 * @param y0b
	 *            the y-coordinate of the first end point of the second cubic
	 *            curve
	 * @param cx0b
	 *            the x-coordinate of the first control point of the second
	 *            cubic curve
	 * @param cy0b
	 *            the y-coordinate of the first control point of the second
	 *            cubic curve
	 * @param cx1b
	 *            the x-coordinate of the second control point of the second
	 *            cubic curve
	 * @param cy1b
	 *            the y-coordinate of the second control point of the second
	 *            cubic curve
	 * @param x1b
	 *            the x-coordinate of the second end point of the second cubic
	 *            curve
	 * @param y1b
	 *            the y-coordinate of the second end point of the second cubic
	 *            curve
	 * 
	 * @param dest
	 *            the destination to write the time intersections in.
	 * @param offset
	 *            the offset in the array to start writing at.
	 * @param returnType
	 *            the type of data to store in <code>dest</code>.
	 * @return the number of intersections found.
	 */
	public int cubicCubicBezier(double x0a, double y0a, double cx0a,
			double cy0a, double cx1a, double cy1a, double x1a, double y1a,
			double x0b, double y0b, double cx0b, double cy0b, double cx1b,
			double cy1b, double x1b, double y1b, double[] dest, int offset,
			Return returnType) {
		return cubicCubic(-x0a + 3 * cx0a - 3 * cx1a + x1a, 3 * x0a - 6 * cx0a
				+ 3 * cx1a, -3 * x0a + 3 * cx0a, x0a, -y0a + 3 * cy0a - 3
				* cy1a + y1a, 3 * y0a - 6 * cy0a + 3 * cy1a, -3 * y0a + 3
				* cy0a, y0a, -x0b + 3 * cx0b - 3 * cx1b + x1b, 3 * x0b - 6
				* cx0b + 3 * cx1b, -3 * x0b + 3 * cx0b, x0b, -y0b + 3 * cy0b
				- 3 * cy1b + y1b, 3 * y0b - 6 * cy0b + 3 * cy1b, -3 * y0b + 3
				* cy0b, y0b, dest, offset, returnType);
	}
}