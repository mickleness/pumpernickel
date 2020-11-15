package com.pump.graphics.vector;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.pump.graphics.Graphics2DContext;

/**
 * This is an Operation for
 * {@link java.awt.Graphics2D#drawString(AttributedCharacterIterator, float, float)}
 * and
 * {@link java.awt.Graphics2D#drawString(AttributedCharacterIterator, int, int)}.
 * <p>
 * If you don't want to support AttributedCharacterIterators, you can replace a
 * call to <code>myGraphics.drawString(aci, x, y)</code> with: <br>
 * <code>
 * TextLayout layout = new TextLayout(aci, myGraphics.getFontRenderContext());
 * layout.draw(myGraphics, x, y);
 * </code> <br>
 * (... this ultimately calls <code>myGraphics.drawGlyphVector(..)</code>.)
 */
public class AttributedCharacterIteratorOperation extends Operation {
	private static final long serialVersionUID = 1L;

	protected static final String PROPERTY_ITERATOR = "iterator";
	protected static final String PROPERTY_X = "x";
	protected static final String PROPERTY_Y = "y";

	public AttributedCharacterIteratorOperation(Graphics2DContext context,
			AttributedCharacterIterator iter, float x, float y) {
		super(context);
		setAttributedCharacterIterator(iter);
		setX(x);
		setY(y);
	}

	/**
	 * Return the x-coordinate to draw the AttributedCharacterIterator at.
	 */
	public float getX() {
		return ((Number) coreProperties.get(PROPERTY_X)).floatValue();
	}

	/**
	 * Return the y-coordinate to draw the AttributedCharacterIterator at.
	 */
	public float getY() {
		return ((Number) coreProperties.get(PROPERTY_Y)).floatValue();
	}

	/**
	 * Assign the x-coordinate to draw the AttributedCharacterIterator at.
	 */
	public void setX(float x) {
		coreProperties.put(PROPERTY_X, x);
	}

	/**
	 * Assign the x-coordinate to draw the AttributedCharacterIterator at.
	 */
	public void setY(float y) {
		coreProperties.put(PROPERTY_Y, y);
	}

	/**
	 * Return the AttributedCharacterIterator to render.
	 */
	public AttributedCharacterIterator getAttributedCharacterIterator() {
		return (AttributedCharacterIterator) coreProperties
				.get(PROPERTY_ITERATOR);
	}

	/**
	 * Assign the AttributedCharacterIterator to render.
	 */
	public void setAttributedCharacterIterator(
			AttributedCharacterIterator iter) {
		Objects.requireNonNull(iter);
		coreProperties.put(PROPERTY_ITERATOR, iter);
	}

	@Override
	protected void paintOperation(Graphics2D g) {
		g.drawString(getAttributedCharacterIterator(), getX(), getY());
	}

	@Override
	public Shape getUnclippedOutline() {
		TextLayout layout = new TextLayout(getAttributedCharacterIterator(),
				getContext().getFontRenderContext());
		Rectangle2D bounds = layout.getBounds();
		bounds.setFrame(bounds.getX() + getX(), bounds.getY() + getY(),
				bounds.getWidth(), bounds.getHeight());
		return bounds;
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

	/**
	 * Convert this AttributedCharacterIteratorOperation to zero or more
	 * Operations by asking a java.awt.font.TextLayout to render this
	 * AttributedCharacterIterator.
	 */
	public Operation[] toTextLayoutOperations() {
		VectorImage vi = new VectorImage();
		VectorGraphics2D g = vi.createGraphics();

		context.install(g);
		TextLayout layout = new TextLayout(getAttributedCharacterIterator(),
				g.getFontRenderContext());
		layout.draw(g, getX(), getY());

		g.dispose();
		return vi.getOperations()
				.toArray(new Operation[vi.getOperations().size()]);
	}

	@Override
	public Operation[] toSoftClipOperation(Shape clippingShape) {
		List<Operation> returnValue = new ArrayList<>();
		for (Operation op : toTextLayoutOperations()) {
			returnValue.addAll(
					Arrays.asList(op.toSoftClipOperation(clippingShape)));
		}
		return returnValue.toArray(new Operation[returnValue.size()]);
	}

}
