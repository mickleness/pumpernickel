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
package com.pump.graphics.vector;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.io.IOException;
import java.util.Objects;

import com.pump.geom.ShapeBounds;
import com.pump.graphics.Graphics2DContext;

/**
 * This is an Operation for
 * {@link Graphics2D#drawGlyphVector(GlyphVector, float, float)}.
 * <p>
 * The GlyphVector is not cloned.
 */
public class GlyphVectorOperation extends Operation {

	private static final long serialVersionUID = 1L;

	protected static final String PROPERTY_GLYPH_VECTOR = "glyphVector";
	protected static final String PROPERTY_X = "x";
	protected static final String PROPERTY_Y = "y";

	public GlyphVectorOperation(Graphics2DContext context, GlyphVector gv,
			float x, float y) {
		super(context);
		setGlyphVector(gv);
		setX(x);
		setY(y);
	}

	/**
	 * Return the GlyphVector to render.
	 */
	public GlyphVector getGlyphVector() {
		return (GlyphVector) coreProperties.get(PROPERTY_GLYPH_VECTOR);
	}

	/**
	 * Return the x-coordinate of the location to render the GlyphVector.
	 */
	public float getX() {
		return ((Number) coreProperties.get(PROPERTY_X)).floatValue();
	}

	/**
	 * Return the y-coordinate of the location to render the GlyphVector.
	 */
	public float getY() {
		return ((Number) coreProperties.get(PROPERTY_Y)).floatValue();
	}

	/**
	 * Set the GlyphVector to render.
	 */
	public void setGlyphVector(GlyphVector gv) {
		Objects.requireNonNull(gv);
		coreProperties.put(PROPERTY_GLYPH_VECTOR, gv);
	}

	/**
	 * Assign the x-coordinate of the location to render the GlyphVector.
	 */
	public void setX(float x) {
		coreProperties.put(PROPERTY_X, x);
	}

	/**
	 * Assign the t-coordinate of the location to render the GlyphVector.
	 */
	public void setY(float y) {
		coreProperties.put(PROPERTY_Y, y);
	}

	@Override
	protected void paintOperation(Graphics2D g) {
		g.drawGlyphVector(getGlyphVector(), getX(), getY());
	}

	private void writeObject(java.io.ObjectOutputStream out)
			throws IOException {
		out.writeInt(0);
	}

	private void readObject(java.io.ObjectInputStream in)
			throws IOException, ClassNotFoundException {
		int version = in.readInt();
		if (version == 0) {
			// do nothing
		} else {
			throw new IOException("unsupported internal version " + version);
		}
	}

	@Override
	public Shape getUnclippedOutline() {
		Shape s0 = getGlyphVector().getOutline(getX(), getY());
		Shape s1 = getContext().getTransform().createTransformedShape(s0);
		// we could return s1 itself, but do we really want the "outline"
		// to show the exact contour of lots of text? I'd rather it show
		// a rectangle. (I think?)
		return ShapeBounds.getBounds(s1);
	}

	/**
	 * Convert this GlyphVectorOperation to a FillOperation by invoking
	 * {@link GlyphVector#getOutline(float, float)}.
	 */
	public FillOperation toFillOperation() {
		Shape fillShape = getGlyphVector().getOutline(getX(), getY());
		return new FillOperation(getContext(), fillShape);

	}

	@Override
	public Operation[] toSoftClipOperation(Shape clippingShape) {
		FillOperation fillOp = toFillOperation();
		return fillOp.toSoftClipOperation(clippingShape);
	}

}