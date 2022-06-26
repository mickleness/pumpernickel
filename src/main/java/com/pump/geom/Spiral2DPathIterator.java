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
import java.awt.geom.GeneralPath;

/**
 * The iterates over a spiral shape.
 * <p>
 * This path can be modeled by:
 * <code><br> &nbsp; centerX+coilGap*(t+coilOffset)*Math.cos(2*Math.PI*t*m+angularOffset);</code>
 * <code><br> &nbsp; centerY+coilGap*(t+coilOffset)*Math.sin(2*Math.PI*t*m+angularOffset);</code>
 * 
 * @see com.pump.geom.Spiral2D
 */
public class Spiral2DPathIterator extends ParametricPathIterator {
	/**
	 * Create a spiral stored in a <code>GeneralPath</code>.
	 * 
	 * @param centerX
	 *            the x-coordinate of the center.
	 * @param centerY
	 *            the y-coordinate of the center.
	 * @param coilGap
	 *            the space between coils.
	 * @param coils
	 *            the number of coils in this spiral.
	 * @param angularOffset
	 *            the angle at the center of this spiral.
	 * @param coilOffset
	 *            the space from the center of the spiral to where the path
	 *            begins painting.
	 * @param clockwise
	 *            whether this spiral spins out from the center
	 * @param outward
	 *            whether this spiral spins out from the center, or inwards
	 *            towards it. clockwise (true) or counter-clockwise (false).
	 *            This argument only matters if you're going to closely study
	 *            the path this iterator returns. If you simply render it: this
	 *            argument is meaningless.
	 */
	public static GeneralPath createSpiral(double centerX, double centerY,
			double coilGap, double coils, double angularOffset,
			double coilOffset, boolean clockwise, boolean outward) {
		GeneralPath path = new GeneralPath();
		path.append(new Spiral2DPathIterator(centerX, centerY, coilGap, coils,
				angularOffset, coilOffset, clockwise, outward), false);
		return path;
	}

	final double centerX, centerY, coilGap, coils, angularOffset, coilOffset;
	final boolean clockwise, outward;

	/**
	 * Create a new <code>Spiral2DPathIterator</code>.
	 * 
	 * @param centerX
	 *            the x-coordinate of the center.
	 * @param centerY
	 *            the y-coordinate of the center.
	 * @param coilGap
	 *            the space between coils.
	 * @param coils
	 *            the number of coils in this spiral.
	 * @param angularOffset
	 *            the angle at the center of this spiral.
	 * @param coilOffset
	 *            the space from the center of the spiral to where the path
	 *            begins painting.
	 * @param clockwise
	 *            whether this spiral spins out from the center
	 * @param outward
	 *            whether this spiral spins out from the center, or inwards
	 *            towards it. clockwise (true) or counter-clockwise (false).
	 *            This argument only matters if you're going to closely study
	 *            the path this iterator returns. If you simply render it: this
	 *            argument is meaningless.
	 */
	public Spiral2DPathIterator(double centerX, double centerY, double coilGap,
			double coils, double angularOffset, double coilOffset,
			boolean clockwise, boolean outward) {
		this(centerX, centerY, coilGap, coils, angularOffset, coilOffset,
				clockwise, outward, null);
	}

	/**
	 * Create a new <code>Spiral2DPathIterator</code>.
	 * 
	 * @param centerX
	 *            the x-coordinate of the center.
	 * @param centerY
	 *            the y-coordinate of the center.
	 * @param coilGap
	 *            the space between coils.
	 * @param coils
	 *            the number of coils in this spiral.
	 * @param angularOffset
	 *            the angle at the center of this spiral.
	 * @param coilOffset
	 *            the space from the center of the spiral to where the path
	 *            begins painting.
	 * @param clockwise
	 *            whether this spiral spins out from the center clockwise (true)
	 *            or counter-clockwise (false).
	 */
	public Spiral2DPathIterator(double centerX, double centerY, double coilGap,
			double coils, double angularOffset, double coilOffset,
			boolean clockwise) {
		this(centerX, centerY, coilGap, coils, angularOffset, coilOffset,
				clockwise, true, null);
	}

	/**
	 * Create a new <code>Spiral2DPathIterator</code>.
	 * 
	 * @param centerX
	 *            the x-coordinate of the center.
	 * @param centerY
	 *            the y-coordinate of the center.
	 * @param coilGap
	 *            the space between coils.
	 * @param coils
	 *            the number of coils in this spiral.
	 * @param angularOffset
	 *            the angle at the center of this spiral.
	 * @param coilOffset
	 *            the space from the center of the spiral to where the path
	 *            begins painting.
	 * @param clockwise
	 *            whether this spiral spins out from the center clockwise (true)
	 *            or counter-clockwise (false).
	 * @param outward
	 *            whether this spiral spins out from the center, or inwards
	 *            towards it. This argument only matters if you're going to
	 *            closely study the path this iterator returns. If you simply
	 *            render it: this argument is meaningless.
	 * @param transform
	 *            an optional AffineTransform to apply to this path.
	 */
	public Spiral2DPathIterator(double centerX, double centerY, double coilGap,
			double coils, double angularOffset, double coilOffset,
			boolean clockwise, boolean outward, AffineTransform transform) {
		super(transform);
		this.centerX = centerX;
		this.centerY = centerY;
		this.coilGap = coilGap;
		this.angularOffset = angularOffset;
		this.coils = coils;
		this.clockwise = clockwise;
		this.outward = outward;
		this.coilOffset = coilOffset;
	}

	@Override
	protected double getDX(double t) {
		if (outward == false) {
			t = coils - t;
		}
		double m = clockwise ? 1 : -1;
		double dx = coilGap * Math.cos(2 * Math.PI * t * m + angularOffset) - 2
				* Math.PI * coilGap * (t + coilOffset) * m
				* Math.sin(2 * Math.PI * t * m + angularOffset);
		if (outward == false)
			return -dx;
		return dx;
	}

	@Override
	protected double getDY(double t) {
		if (outward == false) {
			t = coils - t;
		}
		double m = clockwise ? 1 : -1;
		double dy = coilGap * Math.sin(2 * Math.PI * t * m + angularOffset) + 2
				* Math.PI * coilGap * (t + coilOffset) * m
				* Math.cos(2 * Math.PI * t * m + angularOffset);
		if (outward == false)
			return -dy;
		return dy;
	}

	@Override
	protected double getX(double t) {
		if (outward == false) {
			t = coils - t;
		}
		double m = clockwise ? 1 : -1;
		return centerX + coilGap * (t + coilOffset)
				* Math.cos(2 * Math.PI * t * m + angularOffset);
	}

	@Override
	protected double getY(double t) {
		if (outward == false) {
			t = coils - t;
		}
		double m = clockwise ? 1 : -1;
		return centerY + coilGap * (t + coilOffset)
				* Math.sin(2 * Math.PI * t * m + angularOffset);
	}

	@Override
	protected double getMaxT() {
		return coils;
	}

	@Override
	protected double getNextT(double t) {
		// 8 partitions in a coil
		return t + 1.0 / 8.0;
	}
}