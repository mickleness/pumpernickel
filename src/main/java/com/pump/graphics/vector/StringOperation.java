package com.pump.graphics.vector;

import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.Objects;

import com.pump.graphics.Graphics2DContext;

/**
 * This is an Operation for {@link Graphics2D#drawString(String, float, float)}
 * or {@link Graphics2D#drawString(String, int, int)} (depending on whether the
 * coordinates are equivalent to integers).
 */
public class StringOperation extends Operation {
	private static final long serialVersionUID = 1L;

	protected static final String PROPERTY_X = "x";
	protected static final String PROPERTY_Y = "y";
	protected static final String PROPERTY_STRING = "string";

	public StringOperation(Graphics2DContext context, String str, float x,
			float y) {
		super(context);
		setString(str);
		setX(x);
		setY(y);
	}

	/**
	 * Return the String to draw.
	 */
	public String getString() {
		return (String) coreProperties.get(PROPERTY_STRING);
	}

	/**
	 * Return the x-coordinate of the location to draw the String.
	 */
	public float getX() {
		return ((Number) coreProperties.get(PROPERTY_X)).floatValue();
	}

	/**
	 * Return the y-coordinate of the location to draw the String.
	 */
	public float getY() {
		return ((Number) coreProperties.get(PROPERTY_Y)).floatValue();
	}

	/**
	 * Assign the String to draw.
	 */
	public void setString(String str) {
		Objects.requireNonNull(str);
		coreProperties.put(PROPERTY_STRING, str);
	}

	/**
	 * Set the x-coordinate of the location to draw the String.
	 */
	public void setX(float x) {
		coreProperties.put(PROPERTY_X, x);
	}

	/**
	 * Set the y-coordinate of the location to draw the String.
	 */
	public void setY(float y) {
		coreProperties.put(PROPERTY_Y, y);
	}

	@Override
	protected void paintOperation(Graphics2D g) {
		float x = getX();
		float y = getY();
		int ix = (int) (x + .5);
		int iy = (int) (y + .5);
		if (Math.abs(x - ix) < .00001 && Math.abs(y - iy) < .00001) {
			g.drawString(getString(), ix, iy);
		} else {
			g.drawString(getString(), x, y);
		}
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
		Graphics2DContext ctx = getContext();
		FontRenderContext frc = ctx.getFontRenderContext();
		Rectangle2D r = ctx.getFont().getStringBounds(getString(), frc);
		r.setFrame(r.getX() + getX(), r.getY() + getY(), r.getWidth(),
				r.getHeight());
		return ctx.getTransform().createTransformedShape(r);
	}
}
