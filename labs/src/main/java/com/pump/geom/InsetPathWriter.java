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

/**
 * This traces a path <i>next to</i> the path that is being written.
 * <P>
 * If you trace two new paths (one on either side of the original path) and then
 * join those two new paths together: you've just created an outline/stroke of
 * your original path.
 * <P>
 * To compare this with the BasicStroke class: this class uses a JOIN_BEVEL and
 * does not address caps (however if you just draw a line from one inset path to
 * another: that is a CAP_BUTT.)
 * 
 */
public class InsetPathWriter extends PathWriter {

	float moveX, moveY, lastX, lastY;
	boolean movePending = false;
	PathWriter writer;
	float inset;
	double theta;

	/**
	 * This creates a new InsetPathWriter.
	 * 
	 * @param w
	 *            the destination to write the new path data to.
	 * @param inset
	 *            the number of pixels to inset
	 * @param clockwise
	 *            where to draw the inset path. If this is true, then the path
	 *            is drawn at 90 degrees clockwise to the original path.
	 *            Otherwise this is drawn at 90 degrees counterclockwise.
	 */
	public InsetPathWriter(PathWriter w, float inset, boolean clockwise) {
		this(w, inset, clockwise ? (float) (Math.PI / 2.0)
				: (float) (-Math.PI / 2.0));
	}

	/**
	 * This creates a new InsetPathWriter.
	 * 
	 * @param w
	 *            the destination to write the new path data to.
	 * @param inset
	 *            the number of pixels to inset
	 * @param theta
	 *            the angle the path will be written at. That is: if you draw a
	 *            line tangent to the original path, and then move
	 *            <code>(+inset*cos(theta), +inset*sin(theta))</code>, that is
	 *            where this path will be written.
	 */
	public InsetPathWriter(PathWriter w, float inset, float theta) {
		this.writer = w;
		this.inset = inset;
		this.theta = theta;
	}

	@Override
	public void closePath() {
		if (movePending) {
			// just to be fair to the original intent, although
			// this is a pretty bizarre shape:

			// we can't apply insets here, since we can't calculate the
			// tangent slope:
			writer.moveTo(moveX, moveY);
			movePending = false;
		}
		writer.closePath();
	}

	@Override
	public void curveTo(float cx1, float cy1, float cx2, float cy2, float x,
			float y) {
		float ay = -lastY + 3 * cy1 - 3 * cy2 + y;
		float by = 3 * lastY - 6 * cy1 + 3 * cy2;
		float cy = -3 * lastY + 3 * cy1;
		float dy = lastY;

		float ax = -lastX + 3 * cx1 - 3 * cx2 + x;
		float bx = 3 * lastX - 6 * cx1 + 3 * cx2;
		float cx = -3 * lastX + 3 * cx1;
		float dx = lastX;

		/*
		 * Don't use t=0 and t=1 for the endpoints, use t=[a value near zero]
		 * and t=[a value near one]. This way if this "curve" really represents
		 * a line (where the first control point is the last point, and the
		 * second control point in the end point), we don't get wacky
		 * inappropriate angles.
		 */
		// double theta0 = Math.atan2(cy,cx);
		double t = .00001;
		double theta0 = Math.atan2((3 * ay * t + 2 * by) * t + cy,
				(3 * ax * t + 2 * bx) * t + cx);

		float x0 = (float) (dx + inset * Math.cos(theta0 + theta));
		float y0 = (float) (dy + inset * Math.sin(theta0 + theta));
		if (movePending) {
			writer.moveTo(x0, y0);
			movePending = false;
		} else {
			writer.lineTo(x0, y0);
		}

		// double theta1 = Math.atan2(3*ay+2*by+cy,3*ax+2*bx+cx);
		t = .9999;
		double theta1 = Math.atan2((3 * ay * t + 2 * by) * t + cy,
				(3 * ax * t + 2 * bx) * t + cx);

		float x2 = (float) (ax + bx + cx + dx + inset
				* Math.cos(theta1 + theta));
		float y2 = (float) (ay + by + cy + dy + inset
				* Math.sin(theta1 + theta));

		theta0 = Math.atan2(3.0 / 9.0 * ay + 2.0 / 3.0 * by + cy, 3.0 / 9.0
				* ax + 2.0 / 3.0 * bx + cx);
		theta1 = Math.atan2(3.0 * 4.0 / 9.0 * ay + 2.0 * 2.0 / 3.0 * by + cy,
				3.0 * 4.0 / 9.0 * ax + 2.0 * 2 / 3.0 * bx + cx);
		float newCX1 = (float) (cx1 + inset * Math.cos(theta0 + theta));
		float newCY1 = (float) (cy1 + inset * Math.sin(theta0 + theta));
		float newCX2 = (float) (cx2 + inset * Math.cos(theta1 + theta));
		float newCY2 = (float) (cy2 + inset * Math.sin(theta1 + theta));
		writer.curveTo(newCX1, newCY1, newCX2, newCY2, x2, y2);

		lastX = x;
		lastY = y;
	}

	@Override
	public void lineTo(float x, float y) {
		double theta0 = Math.atan2(y - lastY, x - lastX);
		float x0 = (float) (lastX + inset * Math.cos(theta0 + theta));
		float y0 = (float) (lastY + inset * Math.sin(theta0 + theta));
		float x1 = (float) (x + inset * Math.cos(theta0 + theta));
		float y1 = (float) (y + inset * Math.sin(theta0 + theta));
		if (movePending) {
			writer.moveTo(x0, y0);
			movePending = false;
		} else {
			writer.lineTo(x0, y0);
		}
		writer.lineTo(x1, y1);

		lastX = x;
		lastY = y;
	}

	@Override
	public void moveTo(float x, float y) {
		moveX = x;
		moveY = y;
		movePending = true;
		lastX = x;
		lastY = y;
	}

	@Override
	public void flush() {
		if (movePending) {
			// odd... but we can do it:
			// we can't apply insets here, since we can't calculate the
			// tangent slope:
			writer.moveTo(moveX, moveY);
			movePending = false;
		}
	}

	@Override
	public void quadTo(float ctrlX, float ctrlY, float x, float y) {
		float ay = lastY - 2 * ctrlY + y;
		float by = -2 * lastY + 2 * ctrlY;
		float cy = lastY;

		float ax = lastX - 2 * ctrlX + x;
		float bx = -2 * lastX + 2 * ctrlX;
		float cx = lastX;

		// see notes in curveTo()
		double t = .00001;
		double theta0 = Math.atan2(2 * ay * t + by, 2 * ax * t + bx);
		t = .9999;
		double theta1 = Math.atan2(2 * ay * t + by, 2 * ax * t + bx);

		float x0 = (float) (lastX + inset * Math.cos(theta0 + theta));
		float y0 = (float) (lastY + inset * Math.sin(theta0 + theta));
		if (movePending) {
			writer.moveTo(x0, y0);
			movePending = false;
		} else {
			writer.lineTo(x0, y0);
		}

		float x2 = (float) (ax + bx + cx + inset * Math.cos(theta1 + theta));
		float y2 = (float) (ay + by + cy + inset * Math.sin(theta1 + theta));

		theta1 = Math.atan2(2 * .5 * ay + by, 2 * .5 * ax + bx);
		float newCtrlX = (float) (ctrlX + inset * Math.cos(theta1 + theta));
		float newCtrlY = (float) (ctrlY + inset * Math.sin(theta1 + theta));
		writer.quadTo(newCtrlX, newCtrlY, x2, y2);

		lastX = x;
		lastY = y;
	}
}