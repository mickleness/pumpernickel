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

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;

/**
 * This iterates over a path defined by a parametric equation. To subclass it
 * you need to be able to evaluate x(t), y(t), dx(t) and dy(t).
 * <P>
 * Also subclasses are responsible for knowing how much to increment t by for
 * each path segment.
 * <P>
 * This object will translate the parametric function into cubic bezier curves.
 *
 */
public abstract class ParametricPathIterator implements PathIterator {
	private boolean done = false;
	private double t = 0;
	private double lastT = 0;
	private double lastX = 0;
	private double lastY = 0;
	private double lastDX = 0;
	private double lastDY = 0;
	private final int windingRule;
	private final AffineTransform transform;

	/**
	 * Create a new <code>ParametricPathIterator</code> with a WIND_NON_ZERO
	 * winding rule.
	 * 
	 * @param transform
	 *            an optional transform to pass these segments through.
	 */
	public ParametricPathIterator(AffineTransform transform) {
		this(WIND_EVEN_ODD, transform);
	}

	/**
	 * Create a new <code>ParametricPathIterator</code>.
	 * 
	 * @param windingRule
	 *            the winding rule. This must be <code>WIND_EVEN_ODD</code> or
	 *            <code>WIND_NON_ZERO</code>
	 * @param transform
	 *            an optional transform to pass these segments through.
	 */
	public ParametricPathIterator(int windingRule, AffineTransform transform) {
		if (!(windingRule == WIND_EVEN_ODD || windingRule == WIND_NON_ZERO))
			throw new IllegalArgumentException(
					"the winding rule must be WIND_EVEN_ODD or WIND_NON_ZERO");
		this.windingRule = windingRule;
		if (transform == null) {
			this.transform = null;
		} else {
			this.transform = (AffineTransform) transform.clone();
		}
	}

	public final synchronized int currentSegment(float[] coords) {
		if (t == 0) {
			lastX = getX(0);
			lastY = getY(0);
			coords[0] = (float) lastX;
			coords[1] = (float) lastY;
			lastDX = getDX(0);
			lastDY = getDY(0);
			if (transform != null)
				transform.transform(coords, 0, coords, 0, 1);
			return PathIterator.SEG_MOVETO;
		} else {
			double endX = getX(t);
			double endY = getY(t);
			double endDX = getDX(t);
			double endDY = getDY(t);

			double k = t - lastT;

			double dx0 = lastDX * k;
			double dy0 = lastDY * k;
			double dx1 = endDX * k;
			double dy1 = endDY * k;

			coords[0] = (float) ((dx0 + 3 * lastX) / 3);
			coords[2] = (float) ((3 * endX - dx1) / 3);
			coords[4] = (float) endX;
			coords[1] = (float) ((dy0 + 3 * lastY) / 3);
			coords[3] = (float) ((3 * endY - dy1) / 3);
			coords[5] = (float) endY;

			if (transform != null)
				transform.transform(coords, 0, coords, 0, 3);

			lastX = endX;
			lastY = endY;
			lastDX = endDX;
			lastDY = endDY;
			lastT = t;

			return PathIterator.SEG_CUBICTO;
		}
	}

	protected abstract double getX(double t);

	protected abstract double getY(double t);

	protected abstract double getDX(double t);

	protected abstract double getDY(double t);

	public final synchronized int currentSegment(double[] coords) {
		if (t == 0) {
			lastX = getX(0);
			lastY = getY(0);
			coords[0] = lastX;
			coords[1] = lastY;
			lastDX = getDX(0);
			lastDY = getDY(0);
			if (transform != null)
				transform.transform(coords, 0, coords, 0, 1);
			return PathIterator.SEG_MOVETO;
		} else {
			double endX = getX(t);
			double endY = getY(t);
			double endDX = getDX(t);
			double endDY = getDY(t);

			double k = t - lastT;

			double dx0 = lastDX * k;
			double dy0 = lastDY * k;
			double dx1 = endDX * k;
			double dy1 = endDY * k;

			coords[0] = (dx0 + 3 * lastX) / 3;
			coords[2] = (3 * endX - dx1) / 3;
			coords[4] = endX;
			coords[1] = (dy0 + 3 * lastY) / 3;
			coords[3] = (3 * endY - dy1) / 3;
			coords[5] = endY;

			if (transform != null)
				transform.transform(coords, 0, coords, 0, 3);

			lastX = endX;
			lastY = endY;
			lastDX = endDX;
			lastDY = endDY;
			lastT = t;

			return PathIterator.SEG_CUBICTO;
		}
	}

	public final int getWindingRule() {
		return windingRule;
	}

	public final synchronized boolean isDone() {
		return done;
	}

	/**
	 * Iterates to the next segment. This will consult <code>getNextT()</code>
	 * and <code>getMaxT()</code>
	 */
	public final synchronized void next() {
		double max = getMaxT();
		if (Math.abs(t - max) < .0000001) {
			done = true;
		}
		t = getNextT(t);
		if (t > max) {
			t = max;
		}
	}

	/**
	 * The highest value for t.
	 * 
	 * @return the max value for t. Once this value is used this PathIterator is
	 *         finished.
	 */
	protected abstract double getMaxT();

	/**
	 * The t-value that we should use next. If this is greater than
	 * <code>getMaxT()</code>, then the maximum is used in the next segment and
	 * this PathIterator is finished.
	 * 
	 * @param t
	 *            the current t-value
	 * @return the next t-value
	 */
	protected abstract double getNextT(double t);

}