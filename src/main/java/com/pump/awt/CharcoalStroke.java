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

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.GeneralPath;
import java.io.IOException;
import java.io.Serializable;

import com.pump.awt.serialization.AWTSerializationUtils;
import com.pump.geom.GeneralPathWriter;
import com.pump.io.serialization.FilteredObjectInputStream;
import com.pump.io.serialization.FilteredObjectOutputStream;

/**
 * This applies cracks using the {@link CharcoalEffect} to another
 * <code>Stroke</code>.
 * <p>
 * By default this is built on top of a <code>BasicStroke</code>, but you can
 * use any other relatively simple stroke. The nature of the charcoal effect
 * requires continuous areas of at least 2 pixels to really be visible.
 * <P>
 * It is not recommended to layer a <code>CharcoalStroke</code> on top of
 * another <code>CharcoalStroke</code>, because they are very complex.
 * 
 * @see <a href=
 *      "https://javagraphics.blogspot.com/2008/12/strokes-charcoal-stroke.html">Strokes:
 *      a Charcoal Stroke</a>
 */
public class CharcoalStroke implements FilteredStroke, Serializable {
	private static final long serialVersionUID = 1L;

	Stroke stroke;
	float crackSize, angle;
	int randomSeed;

	/**
	 * Create a new <code>CharcoalStroke</code> built on top of a
	 * <code>BasicStroke</code>.
	 * 
	 * @param width
	 *            the width of the <code>BasicStroke</code>.
	 * @param crackSize
	 *            a value from 0-1 indicating how deep the crack should be.
	 * @param angle
	 *            the angle, in radians.
	 */
	public CharcoalStroke(float width, float crackSize, float angle) {
		this(new BasicStroke(width), crackSize, angle, 0);
	}

	/**
	 * Create a new <code>CharcoalStroke</code> built on top of another
	 * <code>Stroke</code>.
	 * 
	 * @param s
	 *            the stroke to apply the charcoal effect to.
	 * @param crackSize
	 *            a value from 0-1 indicating how deep the crack should be.
	 * @param angle
	 *            the angle, in radians.
	 * @param randomSeed
	 *            the random seed to use.
	 */
	public CharcoalStroke(Stroke s, float crackSize, float angle,
			int randomSeed) {
		this.stroke = s;
		this.crackSize = crackSize;
		this.angle = angle;
		this.randomSeed = randomSeed;
	}

	/** @return the random seed this object uses. */
	public int getRandomSeed() {
		return randomSeed;
	}

	/** @return the angle (in radians) this effect uses. */
	public float getAngle() {
		return angle;
	}

	/**
	 * This creates a <code>CharcoalStroke</code> on top of a simple
	 * <code>BasicStroke</code>, with a fixed angle of 45 degrees.
	 * 
	 * @param width
	 *            the width of the <code>BasicStroke</code>
	 * @param crackSize
	 *            a value from 0-1 indicating how deep the crack should be.
	 */
	public CharcoalStroke(float width, float crackSize) {
		this(new BasicStroke(width), crackSize, (float) (Math.PI / 4), 0);
	}

	/**
	 * Creates a similar stroke where only the crack depth (0-1) is redefined.
	 * 
	 * @param cracks
	 *            the new crack depth.
	 * @return a new stroke.
	 */
	public CharcoalStroke deriveStroke(float cracks) {
		return new CharcoalStroke(stroke, cracks, angle, randomSeed);
	}

	/**
	 * Creates a similar stroke where the underlying stroke is redefined.
	 * 
	 * @param newStroke
	 *            the new underlying Stroke. This could be a
	 *            <code>BasicShape</code>, or your own stroke. It is not
	 *            recommended to layer a <code>CharcoalStroke</code> on top of
	 *            another <code>CharcoalStroke</code>, because they are very
	 *            complex.
	 */
	public FilteredStroke deriveStroke(Stroke newStroke) {
		return new CharcoalStroke(newStroke, crackSize, angle, randomSeed);
	}

	public Shape createStrokedShape(Shape p) {
		Shape shape = stroke.createStrokedShape(p);
		if (crackSize == 0)
			return shape;

		GeneralPath newShape = new GeneralPath(
				shape.getPathIterator(null).getWindingRule());
		GeneralPathWriter writer = new GeneralPathWriter(newShape);

		float maxDepth = Float.MAX_VALUE;
		if (stroke instanceof BasicStroke) {
			maxDepth = ((BasicStroke) stroke).getLineWidth() * 2;
		}

		CharcoalEffect charcoal = new CharcoalEffect(writer, crackSize, angle,
				randomSeed, maxDepth);

		charcoal.write(shape);

		return (newShape);
	}

	/**
	 * Returns the underlying stroke this <code>CharcoalStroke</code> is layered
	 * on top of.
	 */
	public Stroke getStroke() {
		return stroke;
	}

	/**
	 * Returns the crack size. This is a float from 0 to 1 indicating how deep
	 * the cracks in this effect run.
	 * 
	 * @return the crack size, as a float from [0, 1].
	 */
	public float getCrackSize() {
		return crackSize;
	}

	private void writeObject(java.io.ObjectOutputStream out)
			throws IOException {
		FilteredObjectOutputStream fout = AWTSerializationUtils
				.createFilteredObjectOutputStream(out);
		fout.writeInt(0);
		fout.writeFloat(crackSize);
		fout.writeFloat(angle);
		fout.writeInt(randomSeed);
		fout.writeObject(stroke);

	}

	private void readObject(java.io.ObjectInputStream in)
			throws IOException, ClassNotFoundException {
		FilteredObjectInputStream fin = new FilteredObjectInputStream(in);
		int internalVersion = fin.readInt();
		if (internalVersion == 0) {
			crackSize = fin.readFloat();
			angle = fin.readFloat();
			randomSeed = fin.readInt();
			stroke = (Stroke) fin.readObject();
		} else {
			throw new IOException(
					"Unsupported internal version: " + internalVersion);
		}
	}
}