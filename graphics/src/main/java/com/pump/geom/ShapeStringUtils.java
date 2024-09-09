/**
 * This software is released as part of the Pumpernickel project.
 * <p>
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * <p>
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.geom;

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;

/**
 * This is a small set of static methods that translate shape data into
 * <code>java.lang.Strings</code> and vice versa.
 * <p>
 *
 */
public class ShapeStringUtils {

	/**
	 * This describes a shape in a <code>String</code>. The model is used is
	 * based on how SVG encodes shape data.
	 * <P>
	 * The call: <BR>
	 * <code>ShapeUtils.createPathIterator(ShapeUtils.toString(shape));</code>
	 * <BR>
	 * should result in an identical shape.
	 * <P>
	 * The shape data is formatted as a single letter (m, l, q, c, z) followed
	 * by the appropriate number of points (2, 2, 4, 6, 0 respectively). This
	 * uses floats, not doubles, so it will not contain strings with an exponent
	 * (i.e. "1.3e-4").
	 * 
	 * @param s
	 *            the shape to describe
	 * @return textual representation of that shape.
	 */
	public static String toString(Shape s) {
		PathIterator i = s.getPathIterator(null);
		return toString(i);
	}

	public static String toString(PathIterator i) {
		float[] f = new float[6];
		StringBuilder sb = new StringBuilder();
		int k;
		int j = 0;
		while (!i.isDone()) {
			k = i.currentSegment(f);

			if (k == PathIterator.SEG_MOVETO) {
				sb.append('m');
				j = 2;
			} else if (k == PathIterator.SEG_LINETO) {
				sb.append('l');
				j = 2;
			} else if (k == PathIterator.SEG_QUADTO) {
				sb.append('q');
				j = 4;
			} else if (k == PathIterator.SEG_CUBICTO) {
				sb.append('c');
				j = 6;
			} else if (k == PathIterator.SEG_CLOSE) {
				sb.append('z');
				j = 0;
			}
			if (j != 0) {
				sb.append(' ');
				for (int a = 0; a < j; a++) {
					sb.append(f[a]);
					if (a < j - 1)
						sb.append(' ');
				}
			}

			i.next();
			if (!i.isDone())
				sb.append(' ');
		}
		return sb.toString();
	}

	/**
	 * This creates a <code>PathIterator</code> that iterates over the text in
	 * <code>s</code>.
	 * <P>
	 * The shape returned uses winding rule WIND_EVEN_ODD.
	 * 
	 * @param s
	 *            textual representation of a path.
	 *            <P>
	 *            This should be the output of
	 *            <code>ShapeUtils.toString()</code>, resembling: "m 1 2 l 3 4 q
	 *            5 6 7 8 c 9 10 11 12 13 14 z"
	 * @return a <code>PathIterator</code> that will iterate over the data in s.
	 */
	public static PathIterator createPathIterator(String s) {
		return createPathIterator(s, PathIterator.WIND_EVEN_ODD);
	}

	/**
	 * This creates a <code>Path2D</code> of rule WIND_EVEN_ODD that represents
	 * this shape data.
	 * <P>
	 * This method simply calls: <BR>
	 * <code>Path2D p = new Path2D.Float();</code> <BR>
	 * <code>p.append(createPathIterator(s),true);</code> <BR>
	 * <code>return p;</code>
	 * 
	 * @param s
	 *            textual representation of a path.
	 *            <P>
	 *            This should be the output of
	 *            <code>ShapeUtils.toString()</code>, resembling: "m 1 2 l 3 4 q
	 *            5 6 7 8 c 9 10 11 12 13 14 z"
	 * @return a <code>GeneralPath</code> that represents this shape.
	 */
	public static Path2D createPath(String s) {
		Path2D p = new Path2D.Float();
		p.append(createPathIterator(s), true);
		return p;
	}

	/**
	 * This creates a <code>PathIterator</code> that iterates over the text in
	 * <code>s</code>.
	 * 
	 * @param s
	 *            textual representation of a path.
	 * @param windingRule
	 *            the winding rule to use. (This should be
	 *            PathIterator.WIND_NON_ZERO or PathIterator.WIND_EVEN_ODD.)
	 *            <P>
	 *            This should be the output of
	 *            <code>ShapeUtils.toString()</code>, resembling: "m 1 2 l 3 4 q
	 *            5 6 7 8 c 9 10 11 12 13 14 z"
	 * @return a <code>PathIterator</code> that will iterate over the data in s.
	 */
	public static PathIterator createPathIterator(String s, int windingRule) {
		// if the string passed resembles: "Data[ m 34 20 l 20 10 z ]" then
		// we focus only on the part in brackets:
		int i1 = s.indexOf('[');
		int i2 = s.indexOf(']');
		if (i1 != -1 && i2 != -1 && i1 < i2) {
			s = s.substring(i1 + 1, i2);
		}

		return new SerializedPathIterator(s, windingRule);
	}
}