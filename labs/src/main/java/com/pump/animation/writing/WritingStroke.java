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
package com.pump.animation.writing;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.io.Serializable;
import java.util.StringTokenizer;

import com.pump.geom.MeasuredShape;
import com.pump.geom.ShapeStringUtils;
import com.pump.geom.ShapeUtils;

/**
 * A single segment of a hand-drawn graphic. This includes a path to trace and a
 * pause.
 * <p>
 * The pause measured in "beats", which are an arbitrary construct. (And they
 * can be accelerated or decelerated, just like the writing rate.) A pause beat
 * should correspond to the amount of time used to leave a space between words.
 * So if that corresponds to a unit of 1, then you might want to add a pause of
 * length .05 or .1 when you lift up your pen and write the straight line in a
 * "Q". Or you might put a pause of .25 or .5 when writing the three dots an
 * ellipses.
 */
public class WritingStroke implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * The pause before writing this stroke. The unit of 1 should correspond
	 * approximately to the pause of writing a space between words.
	 */
	protected float pauseBeats;

	/** The shape being drawn in this stroke. */
	protected Shape shape;

	/**
	 * A copy of the <code>shape</code> field, stored as a
	 * <code>MeasuredShape</code>.
	 */
	protected transient MeasuredShape measuredShape;

	/**
	 * 
	 * @param pauseBeats
	 *            may not be less than zero.
	 * @param shape
	 *            an optional shape (may be null for spaces/pauses).
	 */
	public WritingStroke(float pauseBeats, Shape shape) {
		if (pauseBeats < 0)
			throw new IllegalArgumentException(
					"pauseBeats must be zero or greater");

		this.pauseBeats = pauseBeats;
		this.shape = shape;
		if (shape != null) {
			this.measuredShape = new MeasuredShape(shape);
		}
	}

	/**
	 * Create a new WritingStroke based on output from a previous call to
	 * toString()
	 * 
	 * @param s
	 *            previous output from <code>WritingStroke.toString()</code>
	 */
	public WritingStroke(String s) {
		StringTokenizer tokenizer = new StringTokenizer(s);
		String token = (!tokenizer.hasMoreElements()) ? null : tokenizer
				.nextToken();
		GeneralPath path = new GeneralPath();
		boolean empty = true;
		while (token != null) {
			if (token.equals("w")) {
				pauseBeats = Float.parseFloat(tokenizer.nextToken());
			} else if (token.equals("m")) {
				empty = false;
				path.moveTo(Float.parseFloat(tokenizer.nextToken()),
						Float.parseFloat(tokenizer.nextToken()));
			} else if (token.equals("l")) {
				path.lineTo(Float.parseFloat(tokenizer.nextToken()),
						Float.parseFloat(tokenizer.nextToken()));
			} else if (token.equals("q")) {
				path.quadTo(Float.parseFloat(tokenizer.nextToken()),
						Float.parseFloat(tokenizer.nextToken()),
						Float.parseFloat(tokenizer.nextToken()),
						Float.parseFloat(tokenizer.nextToken()));
			} else if (token.equals("c")) {
				path.curveTo(Float.parseFloat(tokenizer.nextToken()),
						Float.parseFloat(tokenizer.nextToken()),
						Float.parseFloat(tokenizer.nextToken()),
						Float.parseFloat(tokenizer.nextToken()),
						Float.parseFloat(tokenizer.nextToken()),
						Float.parseFloat(tokenizer.nextToken()));
			} else if (token.equals("z")) {
				path.closePath();
			}
			token = (!tokenizer.hasMoreElements()) ? null : tokenizer
					.nextToken();
		}
		if (!empty) {
			shape = path;
			this.measuredShape = new MeasuredShape(shape);
		} else {
			shape = null;
		}
	}

	/**
	 * Serialize this stroke as a String. This mimics the standard shape output
	 * (m, l, q, c, z), except it also includes the instruction "w" (for the
	 * pause beat), which includes 1 argument.
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("w " + pauseBeats + " ");
		if (shape != null) {
			sb.append(ShapeStringUtils.toString(shape));
		}
		return sb.toString().trim();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof WritingStroke))
			return false;
		WritingStroke other = (WritingStroke) obj;
		if (pauseBeats != other.pauseBeats)
			return false;
		if (!ShapeUtils.equals(shape, other.shape))
			return false;

		return true;
	}

	/**
	 * Create a WritingStroke with the same shape but a new pause.
	 * <p>
	 * WritingStrokes are immutable, so this method is necessary to create
	 * similar-but-different copy.
	 * 
	 * @param newPauseBeats
	 *            the new pause to create a stroke for.
	 * @return a similar WritingStroke using the same shape but a new initial
	 *         pause.
	 */
	public WritingStroke deriveWStroke(float newPauseBeats) {
		return new WritingStroke(newPauseBeats, shape);
	}

	/**
	 * Create a WritingStroke with the same pause but a new shape.
	 * <p>
	 * WritingStrokes are immutable, so this method is necessary to create
	 * similar-but-different copy.
	 * 
	 * @param newShape
	 *            the new shape to create a stroke for
	 * @return a similar WritingStroke using the same pause but a new shape.
	 */
	public WritingStroke deriveWStroke(Shape newShape) {
		return new WritingStroke(pauseBeats, newShape);
	}

	/**
	 * Return the Shape this stroke represents.
	 * 
	 * @return the shape this stroke represents.
	 */
	public Shape getShape() {
		return shape;
	}

	/**
	 * Return the number of pixels this shape covers.
	 * 
	 * @return the number of pixels this shape covers.
	 */
	public float getPixels() {
		if (measuredShape == null)
			return 0;
		return measuredShape.getOriginalDistance();
	}

	/**
	 * Returns the pause before writing this stroke.
	 * <p>
	 * A unit of 1 is approximately the gap used to add a space between words.
	 * 
	 * @return the pause before writing this stroke.
	 */
	public float getPauseBeats() {
		return pauseBeats;
	}

	/**
	 * Return the duration (in seconds) of this stroke at the given speeds.
	 * 
	 * @param beatsPerSecond
	 *            the number of beats per second.
	 * @param pixelsPerSecond
	 *            the number of pixels per second.
	 * @return the duration (in seconds) of this stroke.
	 */
	public float getDuration(float beatsPerSecond, float pixelsPerSecond) {
		if (beatsPerSecond <= 0)
			throw new IllegalArgumentException(
					"beatsPerSecond must be greater than zero");
		if (pixelsPerSecond <= 0)
			throw new IllegalArgumentException(
					"pixelsPerSecond must be greater than zero");

		return getPauseBeats() / beatsPerSecond + getPixels() / pixelsPerSecond;
	}

	/**
	 * Paint this stroke, or a portion of this stroke.
	 * 
	 * @param g
	 *            the Graphics2D to paint to.
	 * @param t
	 *            the time (in seconds), where t=0 is the beginning of rendering
	 *            this stroke.
	 * @param beatsPerSecond
	 *            the number of beats per second.
	 * @param pixelsPerSecond
	 *            the number of pixels per second.
	 * @param transform
	 *            an optional AffineTransform to apply to this stroke.
	 * @return whether the stroke is completely rendered. This should be
	 *         equivalent to
	 *         <code>(t &lt; getDuration( beatsPerSecond, pixelsPerSecond)</code>
	 *         .
	 */
	public boolean paint(Graphics2D g, float t, float beatsPerSecond,
			float pixelsPerSecond, AffineTransform transform) {
		if (beatsPerSecond <= 0)
			throw new IllegalArgumentException(
					"beatsPerSecond must be greater than zero");
		if (pixelsPerSecond <= 0)
			throw new IllegalArgumentException(
					"pixelsPerSecond must be greater than zero");
		if (t <= 0)
			return false;

		float delay = pauseBeats / beatsPerSecond;
		if (t < delay)
			return false;
		t = t - delay;
		float totalPixels = getPixels();
		float elapsedPixels = t * pixelsPerSecond;

		if (elapsedPixels >= totalPixels) {
			if (shape != null) {
				Shape toRender = shape;
				if (transform != null) {
					GeneralPath gp = new GeneralPath(shape);
					gp.transform(transform);
					toRender = gp;
				}
				g.draw(toRender);
			}
			return true;
		}
		Path2D subshape = measuredShape.getShape(0, elapsedPixels
				/ totalPixels * measuredShape.getOriginalDistance()
				/ measuredShape.getClosedDistance());
		if (transform != null)
			subshape.transform(transform);
		g.draw(subshape);
		return false;
	}
}