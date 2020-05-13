package com.pump.graphics.vector;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.io.IOException;
import java.util.Objects;

import com.pump.graphics.Graphics2DContext;

/**
 * This is an operation for all of <code>java.awt.Graphics#drawImage(..)</code>
 * methods.
 * <p>
 * The Image is not cloned. When asked to paint this always passed in a null
 * ImageObserver.
 */
public class ImageOperation extends Operation {

	private static final long serialVersionUID = 1L;

	protected static final String PROPERTY_IMAGE = "image";
	protected static final String PROPERTY_SRC_RECT = "sourceRect";
	protected static final String PROPERTY_DEST_RECT = "destRect";
	protected static final String PROPERTY_BKGND_COLOR = "backgroundColor";

	public ImageOperation(Graphics2DContext context, Image img, int dx1,
			int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2,
			Color bgcolor) {
		super(context);
		setImage(img);
		setDestRect(new Rectangle(dx1, dy1, dx2 - dx1, dy2 - dy1));
		setSourceRect(new Rectangle(sx1, sy1, sx2 - sx1, sy2 - sy1));
		setBackgroundColor(bgcolor);
	}

	/**
	 * Return the optional background color.
	 */
	public void setBackgroundColor(Color bgcolor) {
		coreProperties.put(PROPERTY_BKGND_COLOR, bgcolor);
	}

	/**
	 * Assign the optional background color.
	 */
	public Color getBackgroundColor() {
		return (Color) coreProperties.get(PROPERTY_BKGND_COLOR);
	}

	/**
	 * Return a copy of the rectangle relative to the image to draw.
	 */
	public Rectangle getSourceRect() {
		return new Rectangle((Rectangle) coreProperties.get(PROPERTY_SRC_RECT));
	}

	/**
	 * Return a copy of the rectangle relative to the destination to paint to.
	 */
	public Rectangle getDestRect() {
		return new Rectangle(
				(Rectangle) coreProperties.get(PROPERTY_DEST_RECT));
	}

	/**
	 * Assign the rectangle relative to the image to draw. The Rectangle is
	 * cloned.
	 */
	public void setSourceRect(Rectangle rect) {
		Objects.requireNonNull(rect);
		coreProperties.put(PROPERTY_SRC_RECT, new Rectangle(rect));
	}

	/**
	 * Assign the rectangle relative to the destination to paint to. The
	 * Rectangle is cloned.
	 */
	public void setDestRect(Rectangle rect) {
		Objects.requireNonNull(rect);
		coreProperties.put(PROPERTY_DEST_RECT, new Rectangle(rect));
	}

	/**
	 * Return the image to draw.
	 */
	public Image getImage() {
		return (Image) coreProperties.get(PROPERTY_IMAGE);
	}

	/**
	 * Assign the image to draw.
	 */
	public void setImage(Image newImage) {
		Objects.requireNonNull(newImage);
		coreProperties.put(PROPERTY_IMAGE, newImage);
	}

	@Override
	protected void paintOperation(Graphics2D g) {
		Rectangle d = getDestRect();
		Rectangle s = getSourceRect();
		g.drawImage(getImage(), d.x, d.y, d.x + d.width, d.y + d.height, s.x,
				s.y, s.x + s.width, s.y + s.height, getBackgroundColor(), null);
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
				.createTransformedShape(getDestRect());
	}
}
