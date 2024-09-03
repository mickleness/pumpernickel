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
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import java.io.IOException;
import java.util.Objects;
import java.util.Vector;

import com.pump.graphics.Graphics2DContext;

/**
 * This is an Operation for
 * {@link Graphics2D#drawRenderableImage(RenderableImage, AffineTransform)}.
 * <p>
 * The RenderableImage is not cloned.
 * 
 * <h3>Serialization</h3> The serialization implementation for this operation
 * may be disappointing.
 * <p>
 * This will serialize/deserialize as a static image, and in some cases it may
 * come back as an ARGB-based image.
 *
 */
public class RenderableImageOperation extends Operation {

	private static class BasicRenderableImage implements RenderableImage {
		RenderedImage img;

		public BasicRenderableImage(RenderedImage img) {
			Objects.requireNonNull(img);
			this.img = img;
		}

		@Override
		public Vector<RenderableImage> getSources() {
			Vector<RenderableImage> returnValue = new Vector<>();
			return returnValue;
		}

		@Override
		public Object getProperty(String name) {
			return img.getProperty(name);
		}

		@Override
		public String[] getPropertyNames() {
			return img.getPropertyNames();
		}

		@Override
		public boolean isDynamic() {
			return false;
		}

		@Override
		public float getWidth() {
			return img.getWidth();
		}

		@Override
		public float getHeight() {
			return img.getHeight();
		}

		@Override
		public float getMinX() {
			return img.getMinX();
		}

		@Override
		public float getMinY() {
			return img.getMinY();
		}

		@Override
		public RenderedImage createScaledRendering(int w, int h,
				RenderingHints hints) {
			double sx = ((double) w) / ((double) img.getWidth());
			double sy = ((double) h) / ((double) img.getHeight());
			AffineTransform tx = AffineTransform.getScaleInstance(sx, sy);

			return createRendering(new RenderContext(tx));
		}

		@Override
		public RenderedImage createDefaultRendering() {
			return img;
		}

		@Override
		public RenderedImage createRendering(RenderContext renderContext) {
			Rectangle bounds = new Rectangle(0, 0, img.getWidth(),
					img.getHeight());
			Rectangle newBounds = renderContext.getTransform()
					.createTransformedShape(bounds).getBounds();

			BufferedImage bi = new BufferedImage(newBounds.width,
					newBounds.height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = bi.createGraphics();
			g.translate(-newBounds.x, -newBounds.y);
			if (renderContext.getRenderingHints() != null)
				g.addRenderingHints(renderContext.getRenderingHints());
			g.drawRenderedImage(img, renderContext.getTransform());
			g.dispose();
			return bi;
		}
	}

	/**
	 * Convert a RenderableImage to an ARGB BufferedImage.
	 */
	private static BufferedImage toBufferedImage(RenderableImage ri) {
		int width = (int) (ri.getWidth() + .5);
		int height = (int) (ri.getHeight() + .5);
		if (width <= 0 || height <= 0)
			throw new IllegalArgumentException(
					"Unsupported RenderableImage size: " + ri.getWidth() + "x"
							+ ri.getHeight());
		BufferedImage bi = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		g.drawRenderableImage(ri, new AffineTransform());
		g.dispose();
		return bi;
	}

	private static final long serialVersionUID = 1L;

	protected static final String PROPERTY_IMAGE = "image";
	protected static final String PROPERTY_TRANSFORM = "transform";

	public RenderableImageOperation(Graphics2DContext context,
			RenderableImage img, AffineTransform tx) {
		super(context);
		setImage(img);
		setTransform(tx);
	}

	/**
	 * Return the image to draw.
	 */
	public RenderableImage getImage() {
		Object img = coreProperties.get(PROPERTY_IMAGE);
		if (img instanceof RenderedImage) {
			// this may have been deserialized as a RenderedImage, see
			// how the Operation class serializes maps for details
			RenderedImage r = (RenderedImage) img;
			img = new BasicRenderableImage(r);
			coreProperties.put(PROPERTY_IMAGE, img);
		}
		return (RenderableImage) img;
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
	public void setImage(RenderableImage img) {
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
		coreProperties.put(PROPERTY_TRANSFORM, new AffineTransform(tx));
	}

	@Override
	protected void paintOperation(Graphics2D g) {
		g.drawRenderableImage(getImage(), getTransform());
	}

	@Override
	public Shape getUnclippedOutline() {
		RenderableImage i = getImage();
		Rectangle2D r = new Rectangle2D.Float(0, 0, i.getWidth(),
				i.getHeight());
		AffineTransform tx = getContext().getTransform();
		tx.concatenate(getTransform());
		return tx.createTransformedShape(r);
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
	 * Convert this RenderableImageOperation to an ImageOperation using an ARGB
	 * BufferedImage.
	 */
	public ImageOperation toImageOperation() {
		RenderableImage ri = getImage();
		BufferedImage bi = toBufferedImage(ri);
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