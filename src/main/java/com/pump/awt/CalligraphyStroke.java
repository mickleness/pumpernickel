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
package com.pump.awt;

import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.io.IOException;
import java.io.Serializable;

import com.pump.geom.GeneralPathWriter;

/**
 * This <code>Stroke</code> resembles calligraphy.
 * <P>
 * The angle of the pen (or nib) is fixed.
 * 
 * @see CalligraphyPathWriter
 * 
 * @see <a href=
 *      "https://javagraphics.blogspot.com/2009/05/strokes-calligraphy-stroke.html">Strokes:
 *      a Calligraphy Stroke</a>
 */
public class CalligraphyStroke implements Stroke, Serializable {
	private static final long serialVersionUID = 1L;

	/** The width of this stroke in pixels. */
	private float width;

	/** The angle of the pen in radians. */
	private float theta;

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
	 */
	@Override
	public Shape createStrokedShape(Shape p) {
		GeneralPath dest = new GeneralPath();
		GeneralPathWriter writer = new GeneralPathWriter(dest);
		CalligraphyPathWriter cpw = new CalligraphyPathWriter(theta, width / 2,
				-width / 2, writer, writer);
		cpw.write(p);
		cpw.flush();
		return dest;
	}

	private void writeObject(java.io.ObjectOutputStream out)
			throws IOException {
		out.writeInt(0);
		out.writeFloat(width);
		out.writeFloat(theta);

	}

	private void readObject(java.io.ObjectInputStream in)
			throws IOException, ClassNotFoundException {
		int internalVersion = in.readInt();
		if (internalVersion == 0) {
			width = in.readFloat();
			theta = in.readFloat();
		} else {
			throw new IOException(
					"Unsupported internal version: " + internalVersion);
		}
	}

	@Override
	public int hashCode() {
		return Float.hashCode(width + theta);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CalligraphyStroke))
			return false;
		CalligraphyStroke cs = (CalligraphyStroke) obj;
		if (cs.getWidth() != getWidth())
			return false;
		if (cs.getTheta() != getTheta())
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "CalligraphyStroke[ width=" + width + ", theta=" + theta + "]";
	}

}