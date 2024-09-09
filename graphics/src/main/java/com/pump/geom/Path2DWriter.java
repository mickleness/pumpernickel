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

import java.awt.geom.Path2D;

/**
 * This writes path data to an underlying <code>Path2D</code>.
 * <P>
 * This also omits redundant path information, such as two consecutive calls to
 * lineTo() that go to the same point.
 * <P>
 * Also this is safe to make several consecutive calls to
 * <code>closePath()</code> (the Path2D will only be closed once, unless
 * data has been written in the meantime.)
 * 
 */
public class Path2DWriter extends PathWriter {
	Path2D p;
	float lastX, lastY;
	boolean dataWritten = false;
	boolean eliminateRedundantLines = true;

	public Path2DWriter(Path2D p) {
		this.p = p;
	}

	/**
	 * 
	 * @param eliminateRedundantLines
	 *            if true then calls to <code>lineTo()</code> that only repeat
	 *            the current pen position are ignored.
	 */
	public void setEliminateRedundantLines(boolean eliminateRedundantLines) {
		this.eliminateRedundantLines = eliminateRedundantLines;
	}

	@Override
	public String toString() {
		return "Path2DWriter[ data = " + ShapeStringUtils.toString(p)
				+ " ]";
	}

	@Override
	public void flush() {
	}

	/** This resets the underlying <code>Path2D</code>. */
	public void reset() {
		p.reset();
		dataWritten = false;
	}

	@Override
	public void curveTo(float cx1, float cy1, float cx2, float cy2, float x,
			float y) {
		p.curveTo(cx1, cy1, cx2, cy2, x, y);
		lastX = x;
		lastY = y;
		dataWritten = true;
	}

	@Override
	public void lineTo(float x, float y) {
		if (eliminateRedundantLines && equals(lastX, x) && equals(lastY, y))
			return;
		p.lineTo(x, y);
		lastX = x;
		lastY = y;
		dataWritten = true;
	}

	@Override
	public void moveTo(float x, float y) {
		p.moveTo(x, y);
		lastX = x;
		lastY = y;
		dataWritten = true;
	}

	@Override
	public void quadTo(float cx, float cy, float x, float y) {
		p.quadTo(cx, cy, x, y);
		lastX = x;
		lastY = y;
		dataWritten = true;
	}

	@Override
	public void closePath() {
		if (dataWritten) {
			p.closePath();
			dataWritten = false;
		}
	}

	private static boolean equals(float z1, float z2) {
		float d = z2 - z1;
		if (d < 0)
			d = -d;
		if (d < .001f)
			return true;
		return false;
	}
}