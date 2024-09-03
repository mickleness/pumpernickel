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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeListener;
import java.util.StringTokenizer;

import com.pump.geom.TransformUtils;
import com.pump.util.ObservableProperties;
import com.pump.util.ObservableProperties.Key;
import com.pump.util.list.ObservableList;

/**
 * A series of related {@link WritingStroke} objects to paint sequentially.
 * <p>
 * This is used to give the impression of painting strokes over time.
 */
public class WritingShape {
	/**
	 * The key that relates to the bounds of this shape. Note these are not the
	 * literal bounds; this is the expected frame. Strokes do not need to fill
	 * these bounds, and they can liberally step outside of these bounds for
	 * occasional flourishes.
	 */
	public final static Key<Rectangle2D> BOUNDS_KEY = new Key<Rectangle2D>(
			"bounds", Rectangle2D.class);

	/** The <code>WritingStrokes</code> in this shape. */
	public final ObservableList<WritingStroke> strokes = new ObservableList<WritingStroke>();

	/**
	 * The properties this shape manages. Currently this only manages the
	 * BOUNDS_KEY.
	 */
	protected final ObservableProperties properties = new ObservableProperties();

	/**
	 * Create an empty WritingShape with a unit rectangle as the bounds.
	 */
	public WritingShape() {
		reset();
	}

	/**
	 * Create a copy of a WritingShape
	 * 
	 * @param source
	 *            the shape to clone.
	 */
	public WritingShape(WritingShape source) {
		this(source.toString());
	}

	/**
	 * Create a WritingShape based on output from a previous call to toString().
	 * 
	 * @param s
	 *            previous output from <code>WritingShape.toString()</code>
	 */
	public WritingShape(String s) {
		assign(s);
	}

	/**
	 * Reset this WritingShape to have no strokes and a unit rectangle as
	 * bounds.
	 */
	public void reset() {
		setBounds(new Rectangle2D.Float(0, 0, 1, 1));
		strokes.clear();
	}

	/**
	 * Invoke <code>reset()</code> and reformat this shape from the string
	 * provided.
	 * 
	 * @param s
	 *            previous output from WritingShape.toString()
	 */
	public void assign(String s) {
		reset();
		int lastIndex = 0;
		for (int a = 0; a <= s.length(); a++) {
			int i = s.indexOf('w', a + 1);
			if (i == -1) {
				String instruction = s.substring(lastIndex).trim();
				parseInstruction(instruction);
				return;
			}
			String instruction = s.substring(a, i).trim();
			parseInstruction(instruction);
			lastIndex = i;
			a = i - 1; // the loop iteration will increment this
		}
	}

	private void parseInstruction(String instruction) {
		if (instruction.startsWith("b ")) {
			StringTokenizer tokenizer = new StringTokenizer(instruction);
			tokenizer.nextToken(); // consume the "b"
			setBounds(new Rectangle2D.Float(Float.parseFloat(tokenizer
					.nextToken()), Float.parseFloat(tokenizer.nextToken()),
					Float.parseFloat(tokenizer.nextToken()),
					Float.parseFloat(tokenizer.nextToken())));
		} else if (instruction.startsWith("w ")) {
			strokes.add(new WritingStroke(instruction));
		} else {
			throw new RuntimeException("Unrecognized instruction: "
					+ instruction);
		}
	}

	/**
	 * Serialize this shape as a String. This mimics the standard shape output
	 * (m, l, q, c, z), except it also includes the instruction "b" (4 arguments
	 * for the bounds), and "w" (1 argument for the pause beat). The "w"
	 * instruction happens to separate each unique WritingStroke.
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		Rectangle2D r = getBounds();
		sb.append("b ");
		sb.append(r.getMinX());
		sb.append(" ");
		sb.append(r.getMinY());
		sb.append(" ");
		sb.append(r.getMaxX() - r.getMinX());
		sb.append(" ");
		sb.append(r.getMaxY() - r.getMinY());
		sb.append(" ");
		for (int a = 0; a < strokes.size(); a++) {
			sb.append(strokes.get(a).toString());
			sb.append(' ');
		}
		return sb.toString().trim();
	}

	/**
	 * Add a PropertyChangeListener that will be notified when the bounds of
	 * this WritingShape change.
	 * 
	 * @param pcl
	 *            a new listener to receive notifications when the bounds
	 *            change.
	 */
	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		properties.addListener(pcl);
	}

	/**
	 * Remove a PropertyChangeListener.
	 * 
	 * @param pcl
	 *            the listener to remove.
	 */
	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		properties.removeListener(pcl);
	}

	/**
	 * Return the strokes in this shape.
	 * 
	 * @return a list of al the strokes in this shape.
	 */
	public ObservableList<WritingStroke> getStrokes() {
		return strokes;
	}

	/**
	 * Assign the bounds of this shape.
	 * <p>
	 * Note these are not the literal bounds; this is the expected frame.
	 * Strokes do not need to fill these bounds, and they can liberally step
	 * outside of these bounds for occasional flourishes.
	 * 
	 * @param r
	 *            the new bounds for this shape.
	 */
	public void setBounds(Rectangle2D r) {
		if (r == null)
			throw new NullPointerException();
		properties.set(BOUNDS_KEY, r);
	}

	/**
	 * Return the bounds of this shape.
	 * <p>
	 * Note these are not the literal bounds; this is the expected frame.
	 * Strokes do not need to fill these bounds, and they can liberally step
	 * outside of these bounds for occasional flourishes.
	 * 
	 * @return the bounds of this shape.
	 */
	public Rectangle2D getBounds() {
		Rectangle2D r = properties.get(BOUNDS_KEY);
		Rectangle2D copy = new Rectangle2D.Float();
		copy.setFrame(r);
		return copy;
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
		float sum = 0;
		for (WritingStroke s : strokes) {
			sum += s.getDuration(beatsPerSecond, pixelsPerSecond);
		}
		return sum;
	}

	public float getPixels() {
		float sum = 0;
		for (WritingStroke s : strokes) {
			sum += s.getPixels();
		}
		return sum;
	}

	/**
	 * Paint this shape, or a portion of this shape.
	 * 
	 * @param g
	 *            the Graphics to paint to.
	 * @param bounds
	 *            the bounds of this shape to draw to. The Graphics2D will be
	 *            transformed so every stroke will fit in this argument as if it
	 *            were <code>WritingShape.getBounds()</code>.
	 * @param time
	 *            the time (in seconds), where t=0 is the beginning of rendering
	 *            this shape.
	 * @param beatsPerSecond
	 *            the number of beats per second.
	 * @param pixelsPerSecond
	 *            the number of pixels per second.
	 * @return whether the shape is completely rendered. This should be
	 *         equivalent to
	 *         <code>(t &lt; getDuration( beatsPerSecond, pixelsPerSecond)</code>
	 *         .
	 */
	public boolean paint(Graphics g, Rectangle2D bounds, float time,
			float beatsPerSecond, float pixelsPerSecond) {
		if (beatsPerSecond <= 0)
			throw new IllegalArgumentException(
					"beatsPerSecond must be greater than zero");
		if (pixelsPerSecond <= 0)
			throw new IllegalArgumentException(
					"pixelsPerSecond must be greater than zero");

		Graphics2D g2 = (Graphics2D) g.create();
		try {
			AffineTransform tx = TransformUtils.createAffineTransform(
					getBounds(), bounds);
			// if this WritingShape is based on a unit rectangle, the caller
			// probably configured a
			// better looking stroke...

			for (int a = 0; a < strokes.size(); a++) {
				WritingStroke stroke = strokes.get(a);
				if (stroke.paint(g2, time, beatsPerSecond, pixelsPerSecond, tx)) {
					time -= stroke.getDuration(beatsPerSecond, pixelsPerSecond);
				} else {
					return false;
				}
			}
			return true;
		} finally {
			g2.dispose();
		}
	}
}