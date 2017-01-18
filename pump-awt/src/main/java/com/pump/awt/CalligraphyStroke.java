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
package com.pump.awt;

import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;

import com.pump.blog.Blurb;
import com.pump.geom.GeneralPathWriter;

/**
 * This <code>Stroke</code> resembles calligraphy.
 * <P>
 * The angle of the pen (or nib) is fixed.
 * 
 * @see CalligraphyPathWriter
 */
@Blurb (
title = "Strokes: a Calligraphy Stroke",
releaseDate = "May 2009",
summary = "A stroke that resembles a nib at a fixed angle.",
article = "http://javagraphics.blogspot.com/2009/05/strokes-calligraphy-stroke.html",
imageName = "CalligraphyStroke.png"
)
public class CalligraphyStroke implements Stroke {

	/** The width of this stroke in pixels. */
	public final float width;

	/** The angle of the pen in radians. */
	public final float theta;
	

	/**
	 * Create a simple CalligraphyStroke with an angle of 3*pi/4.
	 * 
	 * @param width
	 *            the width of the stroke (in pixels).
	 */
	public CalligraphyStroke(float width) {
		this(width, (float) (Math.PI / 4.0 * 3.0));
	}

	/**
	 * Creates a new CalligraphyStroke
	 * 
	 * @param width
	 *            the width of the pen (in pixels)
	 * @param angle
	 *            the angle of the pen (in radians)
	 */
	public CalligraphyStroke(float width, float angle) {
		this.width = width;
		this.theta = angle;
	}

	/**
	 * Returns the width of this stroke.
	 * 
	 * @return the width of this stroke.
	 */
	public float getWidth() {
		return width;
	}

	/**
	 * Returns the angle of the pen (in radians).
	 * 
	 * @return the angle of the pen (in radians).
	 */
	public float getTheta() {
		return theta;
	}

	/**
	 * Creates the calligraphic outline of the argument shape.
	 * 
	 */
	public Shape createStrokedShape(Shape p) {
		GeneralPath dest = new GeneralPath();
		GeneralPathWriter writer = new GeneralPathWriter(dest);
		CalligraphyPathWriter cpw = new CalligraphyPathWriter(theta, width/2, -width/2, writer, writer);
		cpw.write(p);
		cpw.flush();
		return dest;
	}
}