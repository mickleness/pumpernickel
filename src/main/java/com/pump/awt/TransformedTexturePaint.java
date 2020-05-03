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
package com.pump.awt;

import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.IOException;
import java.io.Serializable;
import java.util.Objects;

import com.pump.awt.serialization.AWTSerializationUtils;
import com.pump.io.serialization.FilteredObjectInputStream;
import com.pump.io.serialization.FilteredObjectOutputStream;

/**
 * This effectively extends <code>TexturePaint</code> to support arbitrary
 * <code>AffineTransforms</code> inside this paint object.
 * <P>
 * This does not technically extend the <code>TexturePaint</code> class,
 * although it does relay most calls to an internal <code>TexturePaint</code>.
 * This is because some graphics pipelines (notably Java 1.4 on Mac) will fail
 * to work the paint context correctly if this class officially extends
 * <code>TexturePaint</code>.
 *
 * @see <a href=
 *      "https://javagraphics.blogspot.com/2008/06/texturepaints-and-affinetransforms.html">TexturePaints
 *      and AffineTransforms</a>
 */
public class TransformedTexturePaint implements Paint, Serializable {
	private static final long serialVersionUID = 1L;

	TexturePaint texturePaint;
	AffineTransform transform;

	public TransformedTexturePaint(TexturePaint texturePaint,
			AffineTransform transform) {
		Objects.requireNonNull(texturePaint);
		this.texturePaint = texturePaint;

		if (transform == null)
			transform = new AffineTransform();

		this.transform = new AffineTransform(transform);
	}

	public TransformedTexturePaint(BufferedImage txtr, Rectangle2D anchor,
			AffineTransform transform) {
		this(new TexturePaint(txtr, anchor), transform);
	}

	public AffineTransform getTransform() {
		return transform;
	}

	public PaintContext createContext(ColorModel cm, Rectangle deviceBounds,
			Rectangle2D userBounds, AffineTransform xform,
			RenderingHints hints) {
		AffineTransform newTransform = new AffineTransform(xform);
		newTransform.concatenate(transform);
		// this is necessary on Java 1.4 to avoid a NullPointerException
		// from:
		// java.awt.TexturePaintContext.getContext(TexturePaintContext.java:57)
		if (hints == null)
			hints = new RenderingHints(RenderingHints.KEY_COLOR_RENDERING,
					RenderingHints.VALUE_COLOR_RENDER_DEFAULT);
		return texturePaint.createContext(cm, deviceBounds, userBounds,
				newTransform, hints);
	}

	public Rectangle2D getAnchorRect() {
		return texturePaint.getAnchorRect();
	}

	public BufferedImage getImage() {
		return texturePaint.getImage();
	}

	public int getTransparency() {
		return texturePaint.getTransparency();
	}

	private void writeObject(java.io.ObjectOutputStream out)
			throws IOException {
		FilteredObjectOutputStream fout = AWTSerializationUtils
				.createFilteredObjectOutputStream(out);
		fout.writeInt(0);
		fout.writeObject(transform);
		fout.writeObject(texturePaint);

	}

	private void readObject(java.io.ObjectInputStream in)
			throws IOException, ClassNotFoundException {
		FilteredObjectInputStream fin = new FilteredObjectInputStream(in);

		int internalVersion = fin.readInt();
		if (internalVersion == 0) {
			transform = (AffineTransform) fin.readObject();
			texturePaint = (TexturePaint) fin.readObject();
		} else {
			throw new IOException(
					"Unsupported internal version: " + internalVersion);
		}
	}
}