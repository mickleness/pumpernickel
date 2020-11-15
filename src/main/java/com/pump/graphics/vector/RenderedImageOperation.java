package com.pump.graphics.vector;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.util.Objects;

import com.pump.graphics.Graphics2DContext;

/**
 * This is an Operation for
 * {@link Graphics2D#drawRenderedImage(RenderedImage, AffineTransform)}.
 * <p>
 * The RenderedImage is not cloned.
 */
public class RenderedImageOperation extends Operation {

	private static final long serialVersionUID = 1L;

	/**
	 * Convert a RenderedImage to an ARGB BufferedImage.
	 */
	private static BufferedImage toBufferedImage(RenderedImage ri) {
		BufferedImage bi = new BufferedImage(ri.getWidth(), ri.getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		g.drawRenderedImage(ri, new AffineTransform());
		g.dispose();
		return bi;
	}

	protected static final String PROPERTY_IMAGE = "image";
	protected static final String PROPERTY_TRANSFORM = "transform";

	public RenderedImageOperation(Graphics2DContext context, RenderedImage img,
			AffineTransform tx) {
		super(context);
		setImage(img);
		setTransform(tx);
	}

	/**
	 * Return the image to draw.
	 */
	public RenderedImage getImage() {
		return (RenderedImage) coreProperties.get(PROPERTY_IMAGE);
	}

	/**
	 * Return a copy of the AffineTransform used to place the image.
	 */
	public AffineTransform getTransform() {
		AffineTransform tx = (AffineTransform) coreProperties
				.get(PROPERTY_TRANSFORM);
		return new AffineTransform(tx);
	}

	/**
	 * Assign the image to draw.
	 */
	public void setImage(RenderedImage img) {
		Objects.requireNonNull(img);
		coreProperties.put(PROPERTY_IMAGE, img);
	}

	/**
	 * Set the AffineTransform.
	 * 
	 * @param tx
	 *            the new AffineTransform. This may be null, in which case an
	 *            identity transform is used.
	 */
	public void setTransform(AffineTransform tx) {
		if (tx == null)
			tx = new AffineTransform();
		tx = new AffineTransform(tx);
		coreProperties.put(PROPERTY_TRANSFORM, tx);
	}

	@Override
	protected void paintOperation(Graphics2D g) {
		g.drawRenderedImage(getImage(), getTransform());
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
		RenderedImage i = getImage();
		Rectangle r = new Rectangle(0, 0, i.getWidth(), i.getHeight());
		AffineTransform tx = getContext().getTransform();
		tx.concatenate(getTransform());
		return tx.createTransformedShape(r);
	}

	/**
	 * Convert this RenderedImageOperation to an ImageOperation.
	 * <p>
	 * If the RenderedImage is not already a BufferedImage: this converts it to
	 * an ARGB BufferedImage.
	 */
	public ImageOperation toImageOperation() {
		RenderedImage ri = getImage();
		BufferedImage bi;
		if (ri instanceof BufferedImage) {
			bi = (BufferedImage) ri;
		} else {
			bi = toBufferedImage(ri);
		}
		Graphics2DContext context = getContext();
		context.transform(getTransform());
		return new ImageOperation(context, bi, 0, 0, bi.getWidth(),
				bi.getHeight(), 0, 0, bi.getWidth(), bi.getHeight(), null);
	}

	@Override
	public Operation[] toSoftClipOperation(Shape clippingShape) {
		return toImageOperation().toSoftClipOperation(clippingShape);
	}

}
