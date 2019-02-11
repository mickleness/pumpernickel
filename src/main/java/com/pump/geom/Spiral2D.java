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
import java.awt.geom.Point2D;

/**
 * The <code>Spiral2D</code> class defines a spiral shape.
 * <p>
 * This shape can be modeled by:
 * <code><br> &nbsp; centerX+coilGap*(t+coilOffset)*Math.cos(2*Math.PI*t*m+angularOffset);</code>
 * <code><br> &nbsp; centerY+coilGap*(t+coilOffset)*Math.sin(2*Math.PI*t*m+angularOffset);</code>
 * 
 * @see com.pump.geom.Spiral2DPathIterator
 * @see <a
 *      href="https://javagraphics.blogspot.com/2010/03/shapes-parametric-equations-and-spirals.html">Shapes:
 *      Parametric Equations (and Spirals!)</a>
 */
public class Spiral2D extends AbstractShape {

	/**
	 * Creates a new <code>Spiral2D</code> with a center point, an end point,
	 * and a fixed gap between coils.
	 * 
	 * @param center
	 *            the center of this spiral.
	 * @param end
	 *            the outer tip of the spiral.
	 * @param coilGap
	 *            the gap between coils.
	 * @return a new <code>Spiral2D</code>
	 */
	public static Spiral2D createWithFixedCoilGap(Point2D center, Point2D end,
			double coilGap) {
		return createWithFixedCoilGap(center.getX(), center.getY(), end.getX(),
				end.getY(), coilGap);
	}

	/**
	 * Creates a new <code>Spiral2D</code> with a center point, an end point,
	 * and a fixed gap between coils.
	 * 
	 * @param centerX
	 *            the x-coordinate of the center of the spiral.
	 * @param centerY
	 *            the y-coordinate of the center of the spiral.
	 * @param endX
	 *            the x-coordinate of the outer tip of the spiral.
	 * @param endY
	 *            the y-coordinate of the outer tip of the spiral.
	 * @param coilGap
	 *            the gap between coils.
	 * @return a new <code>Spiral2D</code>
	 */
	public static Spiral2D createWithFixedCoilGap(double centerX,
			double centerY, double endX, double endY, double coilGap) {
		double r = Point2D.distance(centerX, centerY, endX, endY);
		double coils = r / coilGap;
		double angle = (coils % 1.0) * (2 * Math.PI)
				+ Math.atan2(endY - centerY, endX - centerX);
		return new Spiral2D(centerX, centerY, // center coordinates (x, y)
				coilGap, // coil gap in pixels
				coils, // number of coils
				angle, // center angle
				0, // coil offset
				false, // clockwise (or not, which is counter-clockwise)
				true); // inward (or not, which means spiraling outward)));
	}

	/**
	 * Creates a new <code>Spiral2D</code> with a center point, an end point,
	 * and a fixed number of coils.
	 * 
	 * @param center
	 *            the center of the spiral.
	 * @param end
	 *            the outer tip of the spiral.
	 * @param numCoils
	 *            the number of coils in this spiral.
	 * @return a new <code>Spiral2D</code>
	 */
	public static Spiral2D createWithFixedCoilCount(Point2D center,
			Point2D end, double numCoils) {
		return createWithFixedCoilCount(center.getX(), center.getY(),
				end.getX(), end.getY(), numCoils);
	}

	/**
	 * Creates a new <code>Spiral2D</code> with a center point, an end point,
	 * and a fixed number of coils.
	 * 
	 * @param centerX
	 *            the x-coordinate of the center of the spiral.
	 * @param centerY
	 *            the y-coordinate of the center of the spiral.
	 * @param endX
	 *            the x-coordinate of the outer tip of the spiral.
	 * @param endY
	 *            the y-coordinate of the outer tip of the spiral.
	 * @param numCoils
	 *            the number of coils in the spiral.
	 * @return a new <code>Spiral2D</code>
	 */
	public static Spiral2D createWithFixedCoilCount(double centerX,
			double centerY, double endX, double endY, double numCoils) {
		if (numCoils <= 0)
			throw new IllegalArgumentException("numCoils (" + numCoils
					+ ") must be greater than zero.");

		double coilGap = Point2D.distance(centerX, centerY, endX, endY)
				/ numCoils;
		double angle = (numCoils % 1.0) * (2 * Math.PI)
				+ Math.atan2(endY - centerY, endX - centerX);
		return new Spiral2D(centerX, centerY, // center coordinates (x, y)
				coilGap, // coil gap in pixels
				numCoils, // number of coils
				angle, // center angle
				0, // coil offset
				false, // clockwise (or not, which is counter-clockwise)
				true); // inward (or not, which means spiraling outward)));
	}

	double centerX = 0;
	double centerY = 0;
	double coilGap = 10;
	double coils = 0;
	double angularOffset = 0;
	double coilOffset = 0;
	boolean clockwise;
	boolean outward;

	/**
	 * Creates a new <code>Spiral2D</code>
	 * 
	 * @param centerX
	 *            the x-coordinate of the center of this spiral.
	 * @param centerY
	 *            the y-coordinate of the center of this spiral.
	 * @param coilGap
	 *            the space between any two coils.
	 * @param coils
	 *            the number of coils in this spiral.
	 * @param angularOffset
	 *            the angle at the center of this spiral.
	 * @param coilOffset
	 *            the space from the center of the spiral to where the path
	 *            begins painting.
	 * @param clockwise
	 *            whether this spiral moves clockwise or counterclockwise from
	 *            the center.
	 */
	public Spiral2D(double centerX, double centerY, double coilGap,
			double coils, double angularOffset, double coilOffset,
			boolean clockwise) {
		this(centerX, centerY, coilGap, coils, angularOffset, coilOffset,
				clockwise, true);
	}

	/**
	 * Creates a new <code>Spiral2D</code>
	 * 
	 * @param centerX
	 *            the x-coordinate of the center of this spiral.
	 * @param centerY
	 *            the y-coordinate of the center of this spiral.
	 * @param coilGap
	 *            the space between any two coils.
	 * @param coils
	 *            the number of coils in this spiral.
	 * @param angularOffset
	 *            the angle at the center of this spiral.
	 * @param coilOffset
	 *            the space from the center of the spiral to where the path
	 *            begins painting.
	 * @param clockwise
	 *            whether this spiral moves clockwise or counterclockwise from
	 *            the center.
	 * @param outward
	 *            whether this spiral spins outward from the center, or inward
	 *            toward the center. This argument only matters if you are going
	 *            to study the <code>PathIterator</code> this shape produces.
	 */
	public Spiral2D(double centerX, double centerY, double coilGap,
			double coils, double angularOffset, double coilOffset,
			boolean clockwise, boolean outward) {
		setCenter(centerX, centerY);
		setCoilGap(coilGap);
		setCoils(coils);
		setClockwise(clockwise);
		setAngularOffset(angularOffset);
		setCoilOffset(coilOffset);
		setOutward(outward);
	}

	public void setOutward(boolean b) {
		outward = b;
	}

	public boolean isOutward() {
		return outward;
	}

	public double getCenterX() {
		return centerX;
	}

	public boolean isClockwise() {
		return clockwise;
	}

	public double getCenterY() {
		return centerY;
	}

	public double getCoilGap() {
		return coilGap;
	}

	public double getCoils() {
		return coils;
	}

	public void setAngularOffset(double v) {
		angularOffset = v;
	}

	public double getAngularOffset() {
		return angularOffset;
	}

	public void setCoilOffset(double v) {
		coilOffset = v;
	}

	public double getCoilOffset() {
		return coilOffset;
	}

	public void setClockwise(boolean b) {
		clockwise = b;
	}

	public void setCenter(double centerX, double centerY) {
		this.centerX = centerX;
		this.centerY = centerY;
	}

	public void setCoilGap(double coilGap) {
		if (coilGap <= 0)
			throw new IllegalArgumentException(
					"the coil gap must be greater than zero");
		this.coilGap = coilGap;
	}

	public void setCoils(double coils) {
		if (coils <= 0)
			throw new IllegalArgumentException(
					"the coils must be greater than zero");
		this.coils = coils;
	}

	@Override
	public int getWindingRule() {
		return PathIterator.WIND_NON_ZERO;
	}

	public PathIterator getPathIterator(AffineTransform at) {
		return new Spiral2DPathIterator(centerX, centerY, coilGap, coils,
				angularOffset, coilOffset, clockwise, outward, at);
	}
}