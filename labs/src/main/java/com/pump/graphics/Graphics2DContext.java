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
package com.pump.graphics;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

import com.pump.data.converter.ConverterUtils;
import com.pump.geom.Clipper;
import com.pump.geom.ImmutableShape;
import com.pump.geom.ShapeBounds;
import com.pump.geom.ShapeStringUtils;
import com.pump.geom.ShapeUtils;

/**
 * This contains all the contextual information for a Graphics2D that affects
 * new incoming rendering instructions. This includes: the composite, transform,
 * background color, font, stroke, paint, rendering hints, clipping and xor
 * color.
 * <p>
 * The majority of getters/setters exactly match methods from the Graphics2D
 * class. The exceptions include: {@link #install(Graphics2D)},
 * {@link #clone()}, {@link #isDisposed()}.
 */
public class Graphics2DContext implements Serializable {
	private static final long serialVersionUID = 1L;

	private static final Font DEFAULT_FONT = new Font("Dialog", 0, 12);

	protected boolean isDisposed = false;
	protected Composite composite;
	protected AffineTransform transform;

	/**
	 * The optional background color.
	 */
	protected Color backgroundColor;
	protected Font font;
	protected Stroke stroke;

	/**
	 * If this is non-null it is used to set the XOR-mode color and the paint
	 * field is ignored. <br>
	 * If this is null then we're assumed to be in a Paint mode where the paint
	 * field is used.
	 */
	protected Color xorColor;
	protected Color color;
	protected Paint paint;
	protected RenderingHints renderingHints;

	/**
	 * The optional clipping without any AffineTransform applied. For example:
	 * if this context is already asked to scale everything 50%, and you call
	 * <code>context.setClip(new Rectangle(0,0,100,100))</code>, then this field
	 * should become <code>new Rectangle(0,0,50,50)</code> because that's what
	 * it becomes after the transform is applied.
	 */
	protected Shape clip;

	/**
	 * Create a blank Graphics2DContext.
	 */
	public Graphics2DContext() {
		composite = AlphaComposite.SrcOver;
		transform = new AffineTransform();

		// I'd like backgroundColor to start out transparent so
		// Graphics2D#clearRect works more intuitively, but instead I'm focusing
		// on mimicking what SunGraphics2D appears to do by default.
		backgroundColor = Color.black;

		font = DEFAULT_FONT;
		stroke = new BasicStroke(1);
		xorColor = null;
		color = Color.black;
		paint = Color.black;
		renderingHints = new RenderingHints(null);
		clip = null;
	}

	/**
	 * Create a clone of an existing Graphics2DContext.
	 */
	protected Graphics2DContext(Graphics2DContext original) {
		composite = original.getComposite();
		transform = original.getTransform();
		backgroundColor = original.getBackground();
		font = original.getFont();
		stroke = original.getStroke();
		xorColor = original.xorColor;
		color = original.getColor();
		paint = original.getPaint();
		renderingHints = original.getRenderingHints();
		clip = ShapeUtils.clone(original.clip);
		isDisposed = original.isDisposed;
	}

	/**
	 * Create a Graphics2DContext based on the settings of a Graphics2D.
	 * <p>
	 * This assumes the incoming Graphics2D is in paint mode. (Because there
	 * isn't a getter for that property.)
	 */
	public Graphics2DContext(Graphics2D g) {
		composite = g.getComposite();
		transform = g.getTransform();
		backgroundColor = g.getBackground();
		font = g.getFont();
		stroke = g.getStroke();
		color = g.getColor();
		paint = g.getPaint();
		renderingHints = (RenderingHints) g.getRenderingHints().clone();
		Shape s = g.getClip();
		if (s != null)
			clip = transform.createTransformedShape(s);
	}

	@Override
	public Graphics2DContext clone() {
		return new Graphics2DContext(this);
	}

	/**
	 * Configure a Graphics2D with this context.
	 * <p>
	 * This adds to (but does not replace) the incoming Graphics2D's existing
	 * transform and clipping. All other attributes (rendering hints, composite,
	 * etc) are replaced.
	 */
	public void install(Graphics2D g) {
		g.setRenderingHints(renderingHints);
		g.setColor(getColor());
		g.setPaint(getPaint());
		g.setStroke(stroke);
		g.setFont(font);
		if (backgroundColor != null)
			g.setBackground(backgroundColor);
		if (clip != null) {
			g.clip(clip);
		}
		AffineTransform tx = g.getTransform();
		tx.concatenate(getTransform());
		g.setTransform(tx);

		if (xorColor != null) {
			g.setXORMode(xorColor);
		} else {
			g.setPaintMode();
			g.setComposite(getComposite());
		}
	}

	@Override
	public String toString() {
		return toString(false);
	}

	public String toString(boolean multiline) {
		String separator = multiline ? "\n" : " ";
		StringBuilder sb = new StringBuilder("Graphics2DContext[");
		sb.append("hints=" + renderingHints + "," + separator);
		sb.append("paint=" + paint + "," + separator);
		sb.append("stroke=" + stroke + "," + separator);
		sb.append("font=" + font + "," + separator);
		sb.append("backgroundColor=" + backgroundColor + "," + separator);

		Shape myClip = null;
		if (clip != null) {
			myClip = ShapeUtils.getRectangle(clip);
			if (myClip == null) {
				myClip = ShapeUtils.getRectangle2D(clip);
			}
			if (myClip == null) {
				myClip = clip;
			}
		}

		if (myClip == null) {
			sb.append("clip=null," + separator);
		} else if (myClip instanceof Rectangle2D) {
			sb.append("clip=" + myClip + "," + separator);
		} else {
			sb.append("clip=" + ShapeStringUtils.toString(myClip) + ","
					+ separator);
		}
		sb.append("transform=" + transform + "," + separator);
		String modeStr = xorColor != null ? "xor (" + xorColor + ")" : "paint";
		sb.append("mode=" + modeStr);
		if (xorColor == null) {
			sb.append("," + separator + "composite=" + composite);
		}

		sb.append("]");
		return sb.toString();
	}

	/**
	 * Return true if {@link #dispose()} has been called.
	 */
	public boolean isDisposed() {
		return isDisposed;
	}

	/**
	 * This indicates a Graphics2D has been disposed. Once this method has been
	 * called {@link #isDisposed()} will always return true.
	 * <p>
	 * 
	 * @see java.awt.Graphics2D#dispose()
	 */
	public void dispose() {
		isDisposed = true;
	}

	/**
	 * @see java.awt.Graphics2D#getComposite()
	 */
	public Composite getComposite() {
		return composite;
	}

	/**
	 * @see java.awt.Graphics2D#setComposite(Composite)
	 */
	public void setComposite(Composite comp) {

		// SunGraphics2D#setComposite will throw an IAE for a null arg
		Objects.requireNonNull(comp);

		composite = comp;
	}

	// transform-related methods:

	/**
	 * @see java.awt.Graphics2D#getTransform()
	 */
	public AffineTransform getTransform() {
		return new AffineTransform(transform);
	}

	/**
	 * @see java.awt.Graphics2D#scale(double, double)
	 */
	public void scale(double sx, double sy) {
		AffineTransform tx = getTransform();
		tx.scale(sx, sy);
		setTransform(tx);
	}

	/**
	 * @see java.awt.Graphics2D#shear(double, double)
	 */
	public void shear(double shx, double shy) {
		AffineTransform tx = getTransform();
		tx.shear(shx, shy);
		setTransform(tx);
	}

	/**
	 * @see java.awt.Graphics2D#transform(AffineTransform)
	 */
	public void transform(AffineTransform tx) {
		AffineTransform newTransform = getTransform();
		newTransform.concatenate(tx);
		setTransform(newTransform);
	}

	/**
	 * @see java.awt.Graphics2D#setTransform(AffineTransform)
	 */
	public void setTransform(AffineTransform tx) {

		// SunGraphics2D will indirectly throw a NPE exception if tx is null
		Objects.requireNonNull(tx);

		try {
			tx.createInverse();
		} catch (NoninvertibleTransformException e) {
			throw new RuntimeException(e);
		}
		transform = new AffineTransform(tx);
	}

	/**
	 * @see java.awt.Graphics2D#translate(int, int)
	 */
	public void translate(int x, int y) {
		AffineTransform tx = getTransform();
		tx.translate(x, y);
		setTransform(tx);
	}

	/**
	 * @see java.awt.Graphics2D#translate(double, double)
	 */
	public void translate(double tx, double ty) {
		AffineTransform newTransform = getTransform();
		newTransform.translate(tx, ty);
		setTransform(newTransform);
	}

	/**
	 * @see java.awt.Graphics2D#rotate(double)
	 */
	public void rotate(double theta) {
		AffineTransform tx = getTransform();
		tx.rotate(theta);
		setTransform(tx);
	}

	/**
	 * @see java.awt.Graphics2D#rotate(double, double, double)
	 */
	public void rotate(double theta, double x, double y) {
		AffineTransform tx = getTransform();
		tx.rotate(theta, x, y);
		setTransform(tx);
	}

	// backgroundColor-related methods:

	/**
	 * @see java.awt.Graphics2D#setBackground(Color)
	 */
	public void setBackground(Color color) {
		backgroundColor = color;
	}

	/**
	 * @see java.awt.Graphics2D#getBackground()
	 */
	public Color getBackground() {
		return backgroundColor;
	}

	// paint-related methods:

	/**
	 * @see java.awt.Graphics2D#getPaint()
	 */
	public Paint getPaint() {
		return paint;
	}

	/**
	 * @see java.awt.Graphics2D#getColor()
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @see java.awt.Graphics2D#setColor(Color)
	 */
	public void setColor(Color color) {
		if (color == null) {
			// SunGraphics2D quietly ignores null arguments, so I'll do the
			// same.
			return;
		}

		this.color = color;
		setPaint(color);
	}

	/**
	 * @see java.awt.Graphics2D#setPaint(Paint)
	 */
	public void setPaint(Paint paint) {
		if (paint == null) {
			// SunGraphics2D quietly ignores null arguments, so I'll do the
			// same.
			return;
		} else if (paint instanceof Color) {
			color = (Color) paint;
		}
		this.paint = paint;
	}

	/**
	 * @see java.awt.Graphics2D#setPaintMode()
	 */
	public void setPaintMode() {
		xorColor = null;
	}

	/**
	 * @see java.awt.Graphics2D#setXORMode(Color)
	 */
	public void setXORMode(Color xorColor) {
		// SunGraphics2D will throw an exception if xorColor is null
		Objects.requireNonNull(xorColor, "use setPaintMode()");

		this.xorColor = xorColor;
	}

	// stroke-related methods:

	/**
	 * @see java.awt.Graphics2D#getStroke()
	 */
	public Stroke getStroke() {
		return stroke;
	}

	/**
	 * @see java.awt.Graphics2D#setStroke(Stroke)
	 */
	public void setStroke(Stroke s) {
		// SunGraphics2D will throw an IAE exception if s is null
		Objects.requireNonNull(s);

		stroke = s;
	}

	// clip-related methods:

	/**
	 * @see java.awt.Graphics2D#clip(Shape)
	 */
	public void clip(Shape incomingClip) {
		incomingClip = transform.createTransformedShape(incomingClip);
		if (clip == null) {
			clip = incomingClip;
		} else {
			clip = Clipper.intersect(clip, incomingClip, false, false);
		}
	}

	/**
	 * @see java.awt.Graphics2D#getClipBounds()
	 */
	public Rectangle getClipBounds() {
		if (clip == null)
			return null;
		if (ShapeUtils.isEmpty(clip))
			return clip.getBounds();
		return ShapeBounds.getBounds(getClip(true)).getBounds();
	}

	/**
	 * @see java.awt.Graphics2D#clipRect(int, int, int, int)
	 */
	public void clipRect(int x, int y, int width, int height) {
		clip(new Rectangle(x, y, width, height));
	}

	/**
	 * Return the current clip of this context, or null if no clip has been
	 * defined.
	 * 
	 * @param withTransform
	 *            when true then this is analogous to
	 *            {@link Graphics2D#getClip()}. This will return a clipping
	 *            shape relative to the current AffineTransform. When this is
	 *            false then this returns the clipping that is independent of
	 *            the current transform.
	 * 
	 * @see java.awt.Graphics2D#getClip()
	 */
	public Shape getClip(boolean withTransform) {
		if (clip == null)
			return null;

		if (withTransform) {
			AffineTransform itx;
			try {
				itx = transform.createInverse();
			} catch (NoninvertibleTransformException e) {
				// this shouldn't happen, because when we call setTransform()
				// we make sure the transform is invertible
				throw new RuntimeException(e);
			}
			Shape iclip = itx.createTransformedShape(clip);
			return iclip;
		}

		return new ImmutableShape(clip);
	}

	/**
	 * @see java.awt.Graphics2D#setClip(Shape)
	 */
	public void setClip(Shape newClip) {
		if (newClip == null) {
			clip = null;
		} else {
			clip = transform.createTransformedShape(newClip);
		}
	}

	/**
	 * @see java.awt.Graphics2D#setClip(int, int, int, int)
	 */
	public void setClip(int x, int y, int width, int height) {
		setClip(new Rectangle(x, y, width, height));
	}

	// font-related methods:

	/**
	 * @see java.awt.Graphics2D#getFont()
	 */
	public Font getFont() {
		return font;
	}

	/**
	 * @see java.awt.Graphics2D#setFont(Font)
	 */
	public void setFont(Font font) {
		if (font == null) {
			// SunGraphics2D quietly ignores null arguments, so I'll do the
			// same.
			return;
		}

		this.font = font;
	}

	/**
	 * @see java.awt.Graphics2D#getFontRenderContext()
	 */
	public FontRenderContext getFontRenderContext() {
		Object aaHint = getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
		Object fmHint = getRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS);
		if (aaHint == null)
			aaHint = RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT;
		if (fmHint == null)
			fmHint = RenderingHints.VALUE_FRACTIONALMETRICS_DEFAULT;

		return new FontRenderContext(getTransform(), aaHint, fmHint);
	}

	// RenderingHints-related methods:

	/**
	 * @see java.awt.Graphics2D#setRenderingHints(Map)
	 */
	public void setRenderingHints(Map<?, ?> hints) {
		renderingHints.clear();
		renderingHints.putAll(hints);
	}

	/**
	 * @see java.awt.Graphics2D#addRenderingHints(Map)
	 */
	public void addRenderingHints(Map<?, ?> hints) {
		renderingHints.putAll(hints);
	}

	/**
	 * @see java.awt.Graphics2D#getRenderingHints()
	 */
	public RenderingHints getRenderingHints() {
		return (RenderingHints) renderingHints.clone();
	}

	/**
	 * @see java.awt.Graphics2D#setRenderingHint(Key, Object)
	 */
	public void setRenderingHint(Key hintKey, Object hintValue) {
		renderingHints.put(hintKey, hintValue);
	}

	/**
	 * @see java.awt.Graphics2D#getRenderingHint(Key)
	 */
	public Object getRenderingHint(Key hintKey) {
		return renderingHints.get(hintKey);
	}

	// serialization-related methods:

	private void writeObject(java.io.ObjectOutputStream out)
			throws IOException {
		out.writeInt(1);
		out.writeBoolean(isDisposed);

		// these objects were initially serialized as-is, but that led to a
		// serialization error across platforms when I tried deserializing a
		// com.apple.laf.AquaFonts$DerivedUIResourceFont
		// on Windows. Technically any of these classes (AffineTransfornm,
		// Color, Font) could be extended to produce a similar problem
		ConverterUtils.writeObject(out, transform);
		ConverterUtils.writeObject(out, backgroundColor);
		ConverterUtils.writeObject(out, font);
		ConverterUtils.writeObject(out, xorColor);
		ConverterUtils.writeObject(out, color);

		// these objects we always knew we had to serialize with ConverterUtils:
		ConverterUtils.writeObject(out, paint);
		ConverterUtils.writeObject(out, composite);
		ConverterUtils.writeObject(out, stroke);
		ConverterUtils.writeObject(out, renderingHints);
		ConverterUtils.writeObject(out, clip);
	}

	private void readObject(java.io.ObjectInputStream in)
			throws IOException, ClassNotFoundException {
		int version = in.readInt();
		if (version == 1) {
			isDisposed = in.readBoolean();
			transform = (AffineTransform) ConverterUtils.readObject(in);
			backgroundColor = (Color) ConverterUtils.readObject(in);
			font = (Font) ConverterUtils.readObject(in);
			xorColor = (Color) ConverterUtils.readObject(in);
			color = (Color) ConverterUtils.readObject(in);

			paint = (Paint) ConverterUtils.readObject(in);
			composite = (Composite) ConverterUtils.readObject(in);
			stroke = (Stroke) ConverterUtils.readObject(in);
			renderingHints = (RenderingHints) ConverterUtils.readObject(in);
			clip = (Shape) ConverterUtils.readObject(in);
		} else if (version == 0) {
			isDisposed = in.readBoolean();
			transform = (AffineTransform) in.readObject();
			backgroundColor = (Color) in.readObject();
			font = (Font) in.readObject();
			xorColor = (Color) in.readObject();
			color = (Color) in.readObject();

			paint = (Paint) ConverterUtils.readObject(in);
			composite = (Composite) ConverterUtils.readObject(in);
			stroke = (Stroke) ConverterUtils.readObject(in);
			renderingHints = (RenderingHints) ConverterUtils.readObject(in);
			clip = (Shape) ConverterUtils.readObject(in);
		} else {
			throw new IOException("unsupported internal version " + version);
		}
	}

	@Override
	public int hashCode() {
		// sample properties that already have a well-defined hash code:
		return Objects.hash(isDisposed, transform, backgroundColor, font,
				xorColor, color, renderingHints);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj.getClass() != getClass())
			return false;
		if (obj == this)
			return true;

		Graphics2DContext other = (Graphics2DContext) obj;

		// check the easy properties first:

		if (isDisposed != other.isDisposed)
			return false;
		if (!Objects.equals(transform, other.transform))
			return false;
		if (!Objects.equals(backgroundColor, other.backgroundColor))
			return false;
		if (!Objects.equals(font, other.font))
			return false;
		if (!Objects.equals(xorColor, other.xorColor))
			return false;
		if (!Objects.equals(color, other.color))
			return false;

		if (!ConverterUtils.equals(renderingHints, other.renderingHints))
			return false;
		if (!ConverterUtils.equals(composite, other.composite))
			return false;
		if (!ConverterUtils.equals(stroke, other.stroke))
			return false;
		if (!ConverterUtils.equals(paint, other.paint))
			return false;
		if (!ConverterUtils.equals(clip, other.clip))
			return false;
		return true;
	}
}