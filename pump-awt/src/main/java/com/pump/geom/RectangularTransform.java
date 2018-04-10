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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * This is a transform that only scales and translates. It is a subset of an
 * <code>AffineTransform</code>, except with no rotation/shearing.
 */
public class RectangularTransform {
	double translateX = 0;
	double translateY = 0;
	double scaleX = 1;
	double scaleY = 1;

	/** Creates an identity transform. */
	public RectangularTransform() {
	}

	/**
	 * Creates a <code>RectangularTransform</code> that transforms from one
	 * rectangle to another.
	 * 
	 * @param oldRect
	 *            the initial rectangle.
	 * @param newRect
	 *            the final (transformed) rectangle.
	 */
	public RectangularTransform(Rectangle2D oldRect, Rectangle2D newRect) {
		setTransform(oldRect, newRect);
	}

	/**
	 * Creates a <code>RectangularTransform</code>.
	 * 
	 * @param sx
	 *            the scaleX factor
	 * @param sy
	 *            the scaleY factor
	 * @param tx
	 *            the x-translation
	 * @param ty
	 *            the y-translation
	 */
	public RectangularTransform(double sx, double sy, double tx, double ty) {
		scaleX = sx;
		scaleY = sy;
		translateX = tx;
		translateY = ty;
	}

	/** Transforms the source argument. */
	public Rectangle2D transform(Rectangle2D src) {
		return transform(src, null);
	}

	/**
	 * Transforms the source argument.
	 * 
	 * @param src
	 *            the initial rectangle.
	 * @param dst
	 *            the Rectangle2D to store the results in.
	 */
	public Rectangle2D transform(Rectangle2D src, Rectangle2D dst) {
		if (dst == null)
			dst = new Rectangle2D.Double();

		dst.setFrame(src.getX() * scaleX + translateX, src.getY() * scaleY
				+ translateY, src.getWidth() * scaleX, src.getHeight() * scaleY);

		return dst;
	}

	/**
	 * Transforms the source argument.
	 * 
	 * @param src
	 *            the initial point.
	 * @param dst
	 *            the Point2D to store the results in.
	 */
	public Point2D transform(Point2D src, Point2D dst) {
		if (dst == null)
			dst = new Point2D.Double();

		dst.setLocation(src.getX() * scaleX + translateX, src.getY() * scaleY
				+ translateY);

		return dst;
	}

	/**
	 * Creates an <code>AffineTransform</code> that maps one argument to
	 * another.
	 * 
	 * @param oldRect
	 *            the initial rectangle.
	 * @param newRect
	 *            the final (transformed) rectangle.
	 * @return an <code>AffineTransform</code> that maps from the old to the new
	 *         rectangle.
	 */
	public static AffineTransform create(Rectangle2D oldRect,
			Rectangle2D newRect) {
		double scaleX = newRect.getWidth() / oldRect.getWidth();
		double scaleY = newRect.getHeight() / oldRect.getHeight();

		double translateX = -oldRect.getX() * scaleX + newRect.getX();
		double translateY = -oldRect.getY() * scaleY + newRect.getY();
		return new AffineTransform(scaleX, 0, 0, scaleY, translateX, translateY);
	}

	/**
	 * Defines this transform.
	 * 
	 * @param oldRect
	 *            the initial rect.
	 * @param newRect
	 *            what this transform should turn the initial rectangle into.
	 */
	public void setTransform(Rectangle2D oldRect, Rectangle2D newRect) {
		scaleX = newRect.getWidth() / oldRect.getWidth();
		scaleY = newRect.getHeight() / oldRect.getHeight();

		translateX = -oldRect.getX() * scaleX + newRect.getX();
		translateY = -oldRect.getY() * scaleY + newRect.getY();
	}

	/**
	 * Translates this transform.
	 * 
	 * @param tx
	 *            the x-translation.
	 * @param ty
	 *            the y-translation.
	 */
	public void translate(double tx, double ty) {
		translateX = tx * scaleX + translateX;
		translateY = ty * scaleY + translateY;
	}

	public double getScaleX() {
		return scaleX;
	}

	public double getScaleY() {
		return scaleY;
	}

	public double getTranslateX() {
		return translateX;
	}

	public double getTranslateY() {
		return translateY;
	}

	/**
	 * Scales this transform.
	 * 
	 * @param sx
	 *            the factor to scale X-values by.
	 * @param sy
	 *            the factor to scale Y-values by.
	 */
	public void scale(double sx, double sy) {
		scaleX = scaleX * sx;
		scaleY = scaleY * sy;
	}

	/**
	 * Converts this to an <code>AffineTransform</code>.
	 */
	public AffineTransform createAffineTransform() {
		return new AffineTransform(scaleX, 0, 0, scaleY, translateX, translateY);
	}

	/**
	 * Creates a transform that is the inverse of this one.
	 */
	public RectangularTransform createInverse() {
		return new RectangularTransform(1.0 / scaleX, 1.0 / scaleY, -translateX
				/ scaleX, -translateY / scaleY);
	}

}