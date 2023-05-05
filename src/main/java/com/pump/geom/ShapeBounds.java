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

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;

/**
 * This class features an efficient and accurate <code>getBounds()</code>
 * method. The <code>java.awt.Shape</code> API clearly states that the
 * <code>Shape.getBounds2D()</code> method may return a rectangle larger than
 * the bounds of the actual shape, so here I present a method to get the bounds
 * without resorting to the very-accurate-but-very-slow
 * <code>java.awt.geom.Area</code> class.
 *
 * @see <a href=
 *      "https://javagraphics.blogspot.com/2007/05/shapes-calculating-bounds.html">Shapes:
 *      Calculating Bounds</a>
 */
public class ShapeBounds {
	/**
	 * This calculates the precise bounds of a shape.
	 * 
	 * @param shape
	 *            the shape you want the bounds of. This method throws a
	 *            NullPointerException if this is null.
	 * @return the bounds of <code>shape</code>.
	 * 
	 * @throws EmptyPathException
	 *             if the shape argument is empty.
	 */
	public static Rectangle2D getBounds(Shape... shapes) {
		Rectangle2D r = null;
		for (int a = 0; a < shapes.length; a++) {
			try {
				Rectangle2D t = getBounds(shapes[a], null);
				if (r == null) {
					r = t;
				} else {
					r.add(t);
				}
			} catch (EmptyPathException e) {
			}
		}
		return r;
	}

	/**
	 * This calculates the precise bounds of a shape.
	 * 
	 * @param shape
	 *            the shape you want the bounds of. This method throws a
	 *            NullPointerException if this is null.
	 * @param transform
	 *            if this is non-null, then this method returns the bounds of
	 *            <code>shape</code> as seen through <code>t</code>.
	 * @return the bounds of <code>shape</code>, as seen through
	 *         <code>transform</code>.
	 * 
	 * @throws EmptyPathException
	 *             if the shape argument is empty.
	 */
	public static Rectangle2D getBounds(Shape shape, AffineTransform transform)
			throws EmptyPathException {
		return getBounds(shape.getPathIterator(transform));
	}

	/**
	 * Returns a high precision bounding box of the specified PathIterator.
	 * <p>
	 * This method provides a basic facility for implementors of the
	 * {@link Shape} interface to implement support for the
	 * {@link Shape#getBounds2D()} method.
	 * </p>
	 * <p>
	 * This method is copied and pasted from the revised OpenJDK implementation
	 * in Path2D
	 *
	 * @param pi
	 *            the specified {@code PathIterator}
	 * @return an instance of {@code Rectangle2D} that is a high-precision
	 *         bounding box of the {@code PathIterator}.
	 * @see Shape#getBounds2D()
	 */
	public static Rectangle2D getBounds(final PathIterator pi) {
		final double[] coeff = new double[4];
		final double[] deriv_coeff = new double[3];

		final double[] coords = new double[6];

		// bounds are stored as {leftX, rightX, topY, bottomY}
		double[] bounds = null;
		double lastX = 0.0;
		double lastY = 0.0;
		double endX = 0.0;
		double endY = 0.0;

		for (; !pi.isDone(); pi.next()) {
			final int type = pi.currentSegment(coords);
			switch (type) {
			case PathIterator.SEG_MOVETO:
				if (bounds == null) {
					bounds = new double[] { coords[0], coords[0], coords[1],
							coords[1] };
				}
				endX = coords[0];
				endY = coords[1];
				break;
			case PathIterator.SEG_LINETO:
				endX = coords[0];
				endY = coords[1];
				break;
			case PathIterator.SEG_QUADTO:
				endX = coords[2];
				endY = coords[3];
				break;
			case PathIterator.SEG_CUBICTO:
				endX = coords[4];
				endY = coords[5];
				break;
			case PathIterator.SEG_CLOSE:
			default:
				continue;
			}

			if (endX < bounds[0])
				bounds[0] = endX;
			if (endX > bounds[1])
				bounds[1] = endX;
			if (endY < bounds[2])
				bounds[2] = endY;
			if (endY > bounds[3])
				bounds[3] = endY;

			switch (type) {
			case PathIterator.SEG_QUADTO:
				accumulateExtremaBoundsForQuad(bounds, 0, lastX, coords[0],
						coords[2], coeff, deriv_coeff);
				accumulateExtremaBoundsForQuad(bounds, 2, lastY, coords[1],
						coords[3], coeff, deriv_coeff);
				break;
			case PathIterator.SEG_CUBICTO:
				accumulateExtremaBoundsForCubic(bounds, 0, lastX, coords[0],
						coords[2], coords[4], coeff, deriv_coeff);
				accumulateExtremaBoundsForCubic(bounds, 2, lastY, coords[1],
						coords[3], coords[5], coeff, deriv_coeff);
				break;
			default:
				break;
			}

			lastX = endX;
			lastY = endY;
		}
		if (bounds != null) {
			return new Rectangle2D.Double(bounds[0], bounds[2],
					bounds[1] - bounds[0], bounds[3] - bounds[2]);
		}

		// there's room to debate what should happen here, but historically we
		// return a zeroed
		// out rectangle here. So for backwards compatibility let's keep doing
		// that:
		return new Rectangle2D.Double();
	}

	/**
	 * Accumulate the quadratic extrema into the pre-existing bounding array.
	 * <p>
	 * This method focuses on one dimension at a time, so to get both the x and
	 * y dimensions you'll need to call this method twice.
	 * </p>
	 * <p>
	 * Whenever we have to examine cubic or quadratic extrema that change our
	 * bounding box: we run the risk of machine error that may produce a box
	 * that is slightly too small. But the contract of
	 * {@link Shape#getBounds2D()} says we should err on the side of being too
	 * large. So to address this: we'll apply a margin based on the upper limit
	 * of numerical error caused by the polynomial evaluation (horner scheme).
	 * </p>
	 *
	 * @param bounds
	 *            the bounds to update, which are expressed as: { minX, maxX }
	 * @param boundsOffset
	 *            the index in boundsof the minimum value
	 * @param x1
	 *            the starting value of the bezier curve where t = 0.0
	 * @param ctrlX
	 *            the control value of the bezier curve
	 * @param x2
	 *            the ending value of the bezier curve where t = 1.0
	 * @param coeff
	 *            an array of at least 3 elements that will be overwritten and
	 *            reused
	 * @param deriv_coeff
	 *            an array of at least 2 elements that will be overwritten and
	 *            reused
	 */
	private static void accumulateExtremaBoundsForQuad(double[] bounds,
			int boundsOffset, double x1, double ctrlX, double x2,
			double[] coeff, double[] deriv_coeff) {
		if (ctrlX < bounds[boundsOffset] || ctrlX > bounds[boundsOffset + 1]) {

			final double dx21 = ctrlX - x1;
			coeff[2] = (x2 - ctrlX) - dx21; // A = P3 - P0 - 2 P2
			coeff[1] = 2.0 * dx21; // B = 2 (P2 - P1)
			coeff[0] = x1; // C = P1

			deriv_coeff[0] = coeff[1];
			deriv_coeff[1] = 2.0 * coeff[2];

			final double t = -deriv_coeff[0] / deriv_coeff[1];
			if (t > 0.0 && t < 1.0) {
				final double v = coeff[0] + t * (coeff[1] + t * coeff[2]);

				// error condition = sum ( abs (coeff) ):
				final double margin = Math.ulp(Math.abs(coeff[0])
						+ Math.abs(coeff[1]) + Math.abs(coeff[2]));

				if (v - margin < bounds[boundsOffset]) {
					bounds[boundsOffset] = v - margin;
				}
				if (v + margin > bounds[boundsOffset + 1]) {
					bounds[boundsOffset + 1] = v + margin;
				}
			}
		}
	}

	/**
	 * Accumulate the cubic extrema into the pre-existing bounding array.
	 * <p>
	 * This method focuses on one dimension at a time, so to get both the x and
	 * y dimensions you'll need to call this method twice.
	 * </p>
	 * <p>
	 * Whenever we have to examine cubic or quadratic extrema that change our
	 * bounding box: we run the risk of machine error that may produce a box
	 * that is slightly too small. But the contract of
	 * {@link Shape#getBounds2D()} says we should err on the side of being too
	 * large. So to address this: we'll apply a margin based on the upper limit
	 * of numerical error caused by the polynomial evaluation (horner scheme).
	 * </p>
	 *
	 * @param bounds
	 *            the bounds to update, which are expressed as: { minX, maxX }
	 * @param boundsOffset
	 *            the index in boundsof the minimum value
	 * @param x1
	 *            the starting value of the bezier curve where t = 0.0
	 * @param ctrlX1
	 *            the first control value of the bezier curve
	 * @param ctrlX1
	 *            the second control value of the bezier curve
	 * @param x2
	 *            the ending value of the bezier curve where t = 1.0
	 * @param coeff
	 *            an array of at least 3 elements that will be overwritten and
	 *            reused
	 * @param deriv_coeff
	 *            an array of at least 2 elements that will be overwritten and
	 *            reused
	 */
	private static void accumulateExtremaBoundsForCubic(double[] bounds,
			int boundsOffset, double x1, double ctrlX1, double ctrlX2,
			double x2, double[] coeff, double[] deriv_coeff) {
		if (ctrlX1 < bounds[boundsOffset] || ctrlX1 > bounds[boundsOffset + 1]
				|| ctrlX2 < bounds[boundsOffset]
				|| ctrlX2 > bounds[boundsOffset + 1]) {
			final double dx32 = 3.0 * (ctrlX2 - ctrlX1);
			final double dx21 = 3.0 * (ctrlX1 - x1);
			coeff[3] = (x2 - x1) - dx32; // A = P3 - P0 - 3 (P2 - P1) = (P3 -
											// P0) + 3 (P1 - P2)
			coeff[2] = (dx32 - dx21); // B = 3 (P2 - P1) - 3(P1 - P0) = 3 (P2 +
										// P0) - 6 P1
			coeff[1] = dx21; // C = 3 (P1 - P0)
			coeff[0] = x1; // D = P0

			deriv_coeff[0] = coeff[1];
			deriv_coeff[1] = 2.0 * coeff[2];
			deriv_coeff[2] = 3.0 * coeff[3];

			// reuse this array, give it a new name for readability:
			final double[] tExtrema = deriv_coeff;

			// solveQuadratic should be improved to get correct t extrema (1
			// ulp):
			final int tExtremaCount = QuadCurve2D.solveQuadratic(deriv_coeff,
					tExtrema);
			if (tExtremaCount > 0) {
				// error condition = sum ( abs (coeff) ):
				final double margin = Math
						.ulp(Math.abs(coeff[0]) + Math.abs(coeff[1])
								+ Math.abs(coeff[2]) + Math.abs(coeff[3]));

				for (int i = 0; i < tExtremaCount; i++) {
					final double t = tExtrema[i];
					if (t > 0.0 && t < 1.0) {
						final double v = coeff[0] + t
								* (coeff[1] + t * (coeff[2] + t * coeff[3]));
						if (v - margin < bounds[boundsOffset]) {
							bounds[boundsOffset] = v - margin;
						}
						if (v + margin > bounds[boundsOffset + 1]) {
							bounds[boundsOffset + 1] = v + margin;
						}
					}
				}
			}
		}
	}
}