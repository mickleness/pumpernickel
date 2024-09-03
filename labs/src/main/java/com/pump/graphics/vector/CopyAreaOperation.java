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
package com.pump.graphics.vector;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.io.IOException;
import java.util.Objects;

import com.pump.graphics.Graphics2DContext;

/**
 * This is an Operation for
 * {@link java.awt.Graphics#copyArea(int, int, int, int, int, int)}.
 */
public class CopyAreaOperation extends Operation {

	private static final long serialVersionUID = 1L;

	protected static final String PROPERTY_SOURCE_RECT = "srcRect";
	protected static final String PROPERTY_DX = "dx";
	protected static final String PROPERTY_DY = "dy";

	public CopyAreaOperation(Graphics2DContext context, int x, int y, int width,
			int height, int dx, int dy) {
		super(context);
		setSourceRect(new Rectangle(x, y, width, height));
		setDX(dx);
		setDY(dy);
	}

	/**
	 * Return a copy of the source rectangle to copy pixels from.
	 */
	public Rectangle getSourceRect() {
		return new Rectangle(
				(Rectangle) coreProperties.get(PROPERTY_SOURCE_RECT));
	}

	/**
	 * Set the source rectangle to copy pixels from. The rectangle is cloned.
	 */
	public void setSourceRect(Rectangle rect) {
		Objects.requireNonNull(rect);
		coreProperties.put(PROPERTY_SOURCE_RECT, new Rectangle(rect));
	}

	/**
	 * Return the horizontal offset of this operation.
	 */
	public int getDX() {
		return ((Number) coreProperties.get(PROPERTY_DX)).intValue();
	}

	/**
	 * Return the vertical offset of this operation.
	 */
	public int getDY() {
		return ((Number) coreProperties.get(PROPERTY_DY)).intValue();
	}

	/**
	 * Set the horizontal offset of this operation.
	 */
	public void setDX(int dx) {
		coreProperties.put(PROPERTY_DX, dx);
	}

	/**
	 * Set the vertical offset of this operation.
	 */
	public void setDY(int dy) {
		coreProperties.put(PROPERTY_DY, dy);
	}

	@Override
	protected void paintOperation(Graphics2D g) {
		Rectangle r = getSourceRect();
		g.copyArea(r.x, r.y, r.width, r.height, getDX(), getDY());
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
		Rectangle src = getSourceRect();
		Rectangle dst = new Rectangle(src.x + getDX(), src.y + getDY(),
				src.width, src.height);
		return getContext().getTransform().createTransformedShape(dst);
	}

	@Override
	public Operation[] toSoftClipOperation(Shape clippingShape) {
		// honestly I'm a little surprised this one worked so easily. I'm a
		// little curious why that is, but I also don't want to put much
		// time/energy into copyArea without a concrete need. I personally don't
		// use this method much (or ever); I'm not sure if there are common use
		// cases out there I just don't know about?

		Rectangle src = getSourceRect();
		Graphics2DContext context = getContext();
		context.clip(clippingShape);
		return new Operation[] { new CopyAreaOperation(context, src.x, src.y,
				src.width, src.height, getDX(), getDY()) };
	}

}