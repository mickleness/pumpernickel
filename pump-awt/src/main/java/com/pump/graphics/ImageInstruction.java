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
package com.pump.graphics;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.Serializable;
import java.util.Enumeration;

import javax.swing.tree.TreeNode;

import com.pump.geom.ShapeBounds;
import com.pump.geom.ShapeStringUtils;

/**
 * An instruction to draw an image.
 *
 * This instruction should be equivalent to calling this code: <br>
 * <code>g = (Graphics2D)g.create();</code> <br>
 * <code>g.clip(clipping);</code> <br>
 * <code>g.setTransform(transform);</code> <br>
 * <code>g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,opacity));</code>
 * <br>
 * <code>g.drawImage(bi, destRect.x, destRect.y, </code> <br>
 * <code> &nbsp; destRect.x+destRect.width, destRect.y+destRect.height, </code> <br>
 * <code> &nbsp; sourceRect.x, sourceRect.y, </code> <br>
 * <code> &nbsp; sourceRect.x+sourceRect.width, sourceRect.y+sourceRect.height,</code>
 * <br>
 * <code> &nbsp; null);</code>
 * 
 */
public class ImageInstruction implements GraphicInstruction, Serializable {
	private static final long serialVersionUID = 1L;

	Rectangle sourceRect;
	Rectangle destRect;
	AffineTransform transform;
	BufferedImage bi;
	Shape clipping;
	float opacity;

	String source;
	GraphicsWriter parent = null;

	/**
	 * Creates a new <code>ImageInstruction</code>.
	 * 
	 * @param i
	 *            the image to draw.
	 * @param t
	 *            the transform to draw with.
	 * @param dest
	 *            the destination rectangle.
	 * @param src
	 *            the source rectangle.
	 * @param clip
	 *            the optional clipping.
	 * @param opacity
	 *            the opacity.
	 */
	public ImageInstruction(Image i, AffineTransform t, Rectangle dest,
			Rectangle src, Shape clip, float opacity) {
		bi = getBufferedImage(i);
		if (t == null) {
			transform = new AffineTransform();
		} else {
			transform = new AffineTransform(t);
		}
		sourceRect = new Rectangle(src);
		if (sourceRect.x + sourceRect.width > bi.getWidth())
			throw new IllegalArgumentException("the source width ("
					+ (sourceRect.x + sourceRect.width)
					+ ") exceeds the image width (" + bi.getWidth() + ").");
		if (sourceRect.y + sourceRect.height > bi.getHeight())
			throw new IllegalArgumentException("the source height ("
					+ (sourceRect.y + sourceRect.height)
					+ ") exceeds the image height (" + bi.getHeight() + ").");
		destRect = new Rectangle(dest);
		if (clip instanceof Rectangle) {
			clipping = new Rectangle((Rectangle) clip);
		} else if (clip instanceof Rectangle2D) {
			Rectangle2D r = (Rectangle2D) clip;
			clipping = new Rectangle2D.Double(r.getX(), r.getY(), r.getWidth(),
					r.getHeight());
		} else if (clip != null) {
			clipping = new Area(clip);
		}
		this.opacity = opacity;

		source = GraphicsWriter.getCaller();
	}

	/**
	 * Returns the opacity used to render this image.
	 * 
	 */
	public float getOpacity() {
		return opacity;
	}

	/**
	 * Returns the clipping used to render this image.
	 */
	public Shape getClipping() {
		return clipping;
	}

	/**
	 * The source rectangle. This is anchored at (0,0) and should not exceed the
	 * width/height of the original image.
	 */
	public Rectangle getSourceRect() {
		return new Rectangle(sourceRect);
	}

	/** Returns the transform this instruction is viewed through. */
	public AffineTransform getTransform() {
		return new AffineTransform(transform);
	}

	/** Returns the destination rectangle this image is painted to. */
	public Rectangle getDestRect() {
		return new Rectangle(destRect);
	}

	/**
	 * Returns a BufferedImage of the argument. If the argument is already a
	 * BufferedImage, it is immediately returned. Otherwise a new BufferedImage
	 * is created and the argument is rendered in it.
	 * 
	 * @param i
	 * @return
	 */
	static BufferedImage getBufferedImage(Image i) {
		if (i instanceof BufferedImage)
			return (BufferedImage) i;
		AbstractGraphics2D.loadImage(i);
		int w = i.getWidth(null);
		int h = i.getHeight(null);
		BufferedImage bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		g.drawImage(i, 0, 0, null);
		g.dispose();
		return bi;
	}

	/**
	 * Returns the area affected by this instruction. This takes into account
	 * the transform and clipping.
	 */
	public Rectangle2D getBounds() {
		if (clipping == null) {
			return ShapeBounds.getBounds(destRect, transform);
		}

		Area clipArea = new Area(clipping);
		Area imageArea = new Area(destRect);
		imageArea.transform(transform);
		clipArea.intersect(imageArea);
		return clipArea.getBounds2D();
	}

	/** Renders this instruction. */
	public void paint(Graphics2D g) {
		g = (Graphics2D) g.create();
		if (clipping != null)
			g.clip(clipping);
		g.transform(transform);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
				opacity));
		g.drawImage(bi, destRect.x, destRect.y, destRect.x + destRect.width,
				destRect.y + destRect.height, sourceRect.x, sourceRect.y,
				sourceRect.x + sourceRect.width, sourceRect.y
						+ sourceRect.height, null);
	}

	/**
	 * This is the outline of the area that this instruction actually affects.
	 * This may return an empty area if the clipping and destination rect don't
	 * intersect.
	 */
	public Area getTransformedShape() {
		Area dst = new Area(transform.createTransformedShape(destRect));
		if (clipping == null) {
			return dst;
		}

		Area clip = new Area(transform.createTransformedShape(clipping));
		dst.intersect(clip);
		return dst;
	}

	/** Returns the image that is rendered. */
	public BufferedImage getImage() {
		return bi;
	}

	/**
	 * This returns the destination rectangle that is resized so it should
	 * render the entire buffered image. That is: the entire buffered image
	 * should be stretched so (0,0) and (w,h) fit within the rectangle returned
	 * by this method. This eliminates the need for the sourceRect data, because
	 * it is now implied that the sourceRect is (0,0,w,h).
	 */
	public Rectangle2D getResizedDestRect() {
		int w = bi.getWidth();
		int h = bi.getHeight();
		double rs = (w - sourceRect.getX()) / (sourceRect.getWidth());
		double ls = (sourceRect.getX() + sourceRect.getWidth())
				/ (sourceRect.getWidth());
		double ts = (h - sourceRect.getY()) / (sourceRect.getHeight());
		double bs = (sourceRect.getY() + sourceRect.getHeight())
				/ (sourceRect.getHeight());
		double x2 = destRect.getX() + destRect.getWidth();
		double y2 = destRect.getY() + destRect.getHeight();
		Rectangle2D r = new Rectangle2D.Double(x2 - destRect.getWidth() * ls,
				y2 - destRect.getHeight() * ts, destRect.getWidth() * rs,
				destRect.getHeight() * bs);
		return r;
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		if (clipping == null) {
			out.writeObject(null);
		} else {
			out.writeObject(ShapeStringUtils.toString(clipping));
		}
		out.writeObject(transform);
		out.writeObject(destRect);
		out.writeObject(sourceRect);
		out.writeInt(bi.getWidth());
		out.writeInt(bi.getHeight());

		BufferedImage argbImage;
		if (bi.getType() == BufferedImage.TYPE_INT_ARGB) {
			argbImage = bi;
		} else {
			argbImage = new BufferedImage(bi.getWidth(), bi.getHeight(),
					BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = argbImage.createGraphics();
			g.drawImage(bi, 0, 0, null);
			g.dispose();
		}
		int[] data = new int[bi.getWidth() * bi.getHeight()];
		argbImage.getRaster().getDataElements(0, 0, bi.getWidth(),
				bi.getHeight(), data);
		out.writeObject(data);

	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		Object obj = in.readObject();
		if (obj instanceof String) {
			String str = (String) obj;
			clipping = new Area(ShapeStringUtils.createGeneralPath(str));
		}
		transform = (AffineTransform) in.readObject();
		destRect = (Rectangle) in.readObject();
		sourceRect = (Rectangle) in.readObject();
		int w = in.readInt();
		int h = in.readInt();
		bi = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		int[] data = (int[]) in.readObject();
		bi.getRaster().setDataElements(0, 0, w, h, data);
	}

	public void setParent(GraphicsWriter parent) {
		this.parent = parent;
	}

	public Enumeration<?> children() {
		return GraphicsWriter.EMPTY_ENUMERATION;
	}

	public boolean getAllowsChildren() {
		return false;
	}

	public TreeNode getChildAt(int childIndex) {
		return null;
	}

	public int getChildCount() {
		return 0;
	}

	public int getIndex(TreeNode node) {
		return -1;
	}

	public TreeNode getParent() {
		return parent;
	}

	public boolean isLeaf() {
		return true;
	}

	public String getSource() {
		return source;
	}
}