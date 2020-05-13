package com.pump.graphics.vector;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
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

}
