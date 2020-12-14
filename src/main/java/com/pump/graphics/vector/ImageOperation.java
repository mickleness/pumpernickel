package com.pump.graphics.vector;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

import com.pump.graphics.Graphics2DContext;
import com.pump.image.ImageLoader;
import com.pump.image.ImageSize;

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

	/**
	 * Create an ImageOperation using the source and dest rectangles.
	 */
	public ImageOperation(Graphics2DContext context, Image img,
			Rectangle dstRect, Rectangle srcRect, Color bgcolor) {
		super(context);
		setImage(img);
		setDestRect(dstRect);
		setSourceRect(srcRect);
		setBackgroundColor(bgcolor);
	}

	/**
	 * Create an ImageOperation using the source and dest rectangles.
	 * <p>
	 * These rectangles are expressed with edges, not with a width/height.
	 */
	public ImageOperation(Graphics2DContext context, Image img, int dx1,
			int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2,
			Color bgcolor) {
		this(context, img, new Rectangle(dx1, dy1, dx2 - dx1, dy2 - dy1),
				new Rectangle(sx1, sy1, sx2 - sx1, sy2 - sy1), bgcolor);
	}

	/**
	 * Create an ImageOperation.
	 * <p>
	 * This constructor assumes the source dest is the whole image.
	 */
	public ImageOperation(Graphics2DContext context, Image img, int x, int y) {
		super(context);
		setImage(img);
		Dimension size = ImageSize.get(img);
		setDestRect(new Rectangle(x, y, size.width, size.height));
		setSourceRect(new Rectangle(0, 0, size.width, size.height));
		setBackgroundColor(null);
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

	/**
	 * Convert this ImageOperation to a FillOperation that uses a TexturePaint.
	 */
	public FillOperation toFillOperation() {
		Graphics2DContext context = getContext();
		BufferedImage bi = ImageLoader.createImage(getImage());
		Rectangle srcRect = getSourceRect();
		if (!srcRect
				.equals(new Rectangle(0, 0, bi.getWidth(), bi.getHeight()))) {
			bi = bi.getSubimage(srcRect.x, srcRect.y, srcRect.width,
					srcRect.height);
		}
		TexturePaint texturePaint = new TexturePaint(bi, getDestRect());
		context.setPaint(texturePaint);
		FillOperation returnValue = new FillOperation(context, getDestRect());
		return returnValue;
	}

	@Override
	public Operation[] toSoftClipOperation(Shape clippingShape) {
		return toFillOperation().toSoftClipOperation(clippingShape);
	}
}
