package com.pump.graphics.vector;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.io.IOException;
import java.util.Objects;

import com.pump.graphics.Graphics2DContext;

/**
 * This is an Operation for
 * {@link java.awt.Graphics#clearRect(int, int, int, int)}.
 */
public class ClearRectOperation extends Operation {
	private static final long serialVersionUID = 1L;

	// originally I tried implementing this by mimicking what
	// SunGraphics2D does: use the background color, a SRC composite, and a
	// fillRect. But that got tricky because our background color can be
	// null. A simple one-off Operation is subclass guaranteed to get the right
	// result, though.

	protected static final String PROPERTY_RECT = "rect";

	public ClearRectOperation(Graphics2DContext context, int x, int y,
			int width, int height) {
		super(context);
		setRectangle(new Rectangle(x, y, width, height));
	}

	/**
	 * Return a copy of the Rectangle to clear.
	 */
	public Rectangle getRectangle() {
		return new Rectangle((Rectangle) coreProperties.get(PROPERTY_RECT));
	}

	/**
	 * Set the Rectangle to clear. The rectangle is cloned.
	 */
	public void setRectangle(Rectangle rect) {
		Objects.requireNonNull(rect);
		coreProperties.put(PROPERTY_RECT, new Rectangle(rect));
	}

	@Override
	protected void paintOperation(Graphics2D g) {
		Rectangle r = getRectangle();
		g.clearRect(r.x, r.y, r.width, r.height);
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
		return getContext().getTransform()
				.createTransformedShape(getRectangle());
	}
}
