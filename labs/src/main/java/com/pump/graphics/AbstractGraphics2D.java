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
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.UIManager;

/**
 * This implements several methods to get <code>Graphics2D</code>'s off the
 * ground. Methods that are implemented here may not be especially efficient.
 * 
 */
public abstract class AbstractGraphics2D extends Graphics2D {
	/** The current RenderingHints. */
	protected RenderingHints hints;
	/** The current Composite. */
	protected Composite composite;
	/** The current Paint. */
	protected Paint paint;
	/** The current background Color. */
	protected Color background;
	/** The current Stroke. */
	protected Stroke stroke;
	/** The current AffineTransform. */
	protected AffineTransform transform;
	/** The current Font. */
	protected Font font;
	/**
	 * This is the area of the destination clipped <i>before</i> any
	 * transformations are applied. This will be an Area, a Rectangle, or a
	 * Rectangle2D.
	 */
	protected Shape clipping = null;
	/**
	 * The optional name. Used for debugging, or naming groups in vector
	 * graphics.
	 */
	protected String name = "Untitled";

	/**
	 * Creates a new <code>AbstractGraphics2D</code> object.
	 */
	public AbstractGraphics2D() {
		reset();
	}

	public void reset() {
		hints = new RenderingHints(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_DEFAULT);
		composite = AlphaComposite.SrcOver;
		paint = Color.white;
		background = Color.white;
		stroke = new BasicStroke(1);
		transform = new AffineTransform();
		font = UIManager.getFont("Label.font") == null ? new Font("Default", 0,
				12) : UIManager.getFont("Label.font");
		clipping = null;
	}

	/** Clones the properties of the <code>AbstractGraphics2D</code> argument. */
	public AbstractGraphics2D(AbstractGraphics2D g) {
		hints = new RenderingHints(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_DEFAULT);
		hints.remove(RenderingHints.KEY_RENDERING);
		hints.putAll(g.hints);
		composite = g.composite;
		paint = g.paint;
		background = g.background;
		stroke = g.stroke;
		transform = new AffineTransform();
		setClip(g.clipping); // set *before* transform
		transform.setTransform(g.transform);
		font = g.font;

		int i = g.name.lastIndexOf(' ');
		if (i == -1) {
			name = g.name + " " + 2;
		} else {
			String end = g.name.substring(i + 1);
			try {
				int ctr = Integer.parseInt(end);
				ctr++;
				name = g.name.substring(0, i) + " " + ctr;
			} catch (NumberFormatException e) {
				name = g.name + " " + 2;
			}
		}
	}

	/**
	 * Returns the name of this Graphics2D. The default name is "Untitled".
	 * 
	 * @return the name of this Graphics2D.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the name of this Graphics2D.
	 * 
	 * @param s
	 *            the new name.
	 */
	public void setName(String s) {
		if (s == null)
			throw new NullPointerException();
		name = s;
	}

	@Override
	public void clip(Shape s) {
		if (s == null)
			return;

		if (clipping == null) {
			setClip(s);
			return;
		}

		Area a1 = new Area(clipping);
		Area a2 = new Area(s);
		a2.transform(transform);
		a1.intersect(a2);

		if (a1.isRectangular()) {
			clipping = a1.getBounds2D();
		} else {
			clipping = a1;
		}
	}

	@Override
	public Shape getClip() {
		if (clipping == null)
			return null;

		try {
			Area area = new Area(clipping);
			area.transform(transform.createInverse());
			if (area.isRectangular())
				return area.getBounds2D();
			return area;
		} catch (NoninvertibleTransformException t) {
			RuntimeException e2 = new RuntimeException();
			e2.initCause(t);
			throw e2;
		}
	}

	@Override
	public void setClip(Shape newClip) {
		if (newClip == null) {
			clipping = null;
			return;
		}

		Area area = new Area(newClip);
		area.transform(transform);
		if (area.isRectangular()) {
			clipping = area.getBounds2D();
		} else {
			clipping = area;
		}
	}

	@Override
	public Rectangle getClipBounds() {
		if (clipping == null)
			return null;

		// the value returned here will be either an area or a Rectangle2D,
		// so we can safely call .getBounds() on it.
		return getClip().getBounds();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void addRenderingHints(Map h) {
		hints.putAll(h);
	}

	@Override
	public Composite getComposite() {
		return composite;
	}

	@Override
	public FontRenderContext getFontRenderContext() {
		return new FontRenderContext(new AffineTransform(), true, true);
	}

	@Override
	public Paint getPaint() {
		return paint;
	}

	@Override
	public Object getRenderingHint(Key hintKey) {
		return hints.get(hintKey);
	}

	@Override
	public RenderingHints getRenderingHints() {
		return (RenderingHints) hints.clone();
	}

	@Override
	public Stroke getStroke() {
		return stroke;
	}

	@Override
	public AffineTransform getTransform() {
		return new AffineTransform(transform);
	}

	@Override
	public Color getBackground() {
		return background;
	}

	@Override
	public void setBackground(Color c) {
		background = c;
	}

	@Override
	public void setComposite(Composite comp) {
		composite = comp;
	}

	@Override
	public void setPaint(Paint p) {
		paint = p;
	}

	@Override
	public void setRenderingHint(Key hintKey, Object hintValue) {
		if (hintValue == null) {
			hints.remove(hintKey);
		} else {
			hints.put(hintKey, hintValue);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void setRenderingHints(Map h) {
		hints.clear();
		hints.putAll(h);
	}

	@Override
	public void setStroke(Stroke s) {
		stroke = s;
	}

	@Override
	public void setTransform(AffineTransform Tx) {
		if (Tx == null) {
			transform = new AffineTransform();
		} else {
			transform = new AffineTransform(Tx);
		}
	}

	@Override
	public void clearRect(int x, int y, int width, int height) {
		Paint oldFill = getPaint();
		setPaint(background);
		fillRect(x, y, width, height);
		setPaint(oldFill);
	}

	@Override
	public Font getFont() {
		return font;
	}

	@Override
	public Color getColor() {
		if (paint instanceof Color) {
			return (Color) paint;
		}
		throw new ClassCastException("The current paint is a "
				+ paint.getClass().getName());
	}

	@Override
	public void setClip(int x, int y, int width, int height) {
		setClip(new Rectangle(x, y, width, height));
	}

	@Override
	public void clipRect(int x, int y, int width, int height) {
		clip(new Rectangle(x, y, width, height));
	}

	/**
	 * This changes the transform of this <code>Graphics2D</code>, renderings
	 * the image, and restores the transform of this image.
	 */
	@Override
	public boolean drawImage(Image i, AffineTransform t, ImageObserver obs) {
		AffineTransform t2 = getTransform();
		transform(t);
		boolean b = drawImage(i, 0, 0, obs);
		setTransform(t2);
		return b;
	}

	/**
	 * This does nothing, because setXORMode is unsupported. Therefore we're
	 * always in paint mode.
	 */
	@Override
	public void setPaintMode() {
		// Does nothing, because there is no other supported mode.
	}

	@Override
	public void setFont(Font f) {
		font = f;
	}

	/**
	 * This throws an <code>UnsupportedOperationException</code> XOR is not
	 * supported.
	 */
	@Override
	public void setXORMode(Color c1) {
		throw new UnsupportedOperationException("setXORMode is not supported");
	}

	/**
	 * This creates a new image from this operation, renders that image, and
	 * flushes it.
	 */
	@Override
	public void drawImage(BufferedImage bi, BufferedImageOp op, int x, int y) {
		if (op == null) {
			drawImage(bi, new AffineTransform(1f, 0f, 0f, 1f, x, y), null);
		} else {
			BufferedImage img1 = op.filter(bi, null);
			drawImage(img1, new AffineTransform(1f, 0f, 0f, 1f, x, y), null);
			img1.flush();
		}
	}

	/**
	 * If <code>i</code> is not a <code>BufferedImage</code>, then a new
	 * <code>BufferedImage</code> is created just to render this image.
	 */
	@Override
	public void drawRenderedImage(RenderedImage i, AffineTransform t) {
		if (i instanceof BufferedImage) {
			BufferedImage bi = (BufferedImage) i;
			drawImage(bi, t, null);
		} else {
			WritableRaster r = i.copyData(null);
			ColorModel cm = i.getColorModel();
			Hashtable<String, Object> properties = new Hashtable<>();
			String[] s = i.getPropertyNames();
			for (int a = 0; a < s.length; a++) {
				properties.put(s[a], i.getProperty(s[a]));
			}
			BufferedImage bi = new BufferedImage(cm, r,
					cm.isAlphaPremultiplied(), properties);
			drawImage(bi, t, null);
			bi.flush();
		}
	}

	@Override
	public void drawRect(int x, int y, int width, int height) {
		draw(new Rectangle(x, y, width, height));
	}

	/**
	 * Calls: <code>drawRenderedImage(i.createDefaultRendering(),t)</code>
	 */
	@Override
	public void drawRenderableImage(RenderableImage i, AffineTransform t) {
		drawRenderedImage(i.createDefaultRendering(), t);
	}

	/**
	 * Calls: <code>drawString(s,((float)x),((float)y))</code>
	 */
	@Override
	public void drawString(String s, int x, int y) {
		drawString(s, ((float) x), ((float) y));
	}

	/**
	 * Creates a <code>GlyphVector</code> and calls
	 * <code>drawGlyphVector()</code>.
	 */
	@Override
	public void drawString(String s, float x, float y) {
		GlyphVector gv = getFont().createGlyphVector(getFontRenderContext(), s);
		drawGlyphVector(gv, x, y);
	}

	/**
	 * Calls: <code>drawString(aci,((float)x),((float)y))</code>
	 */
	@Override
	public void drawString(AttributedCharacterIterator aci, int x, int y) {
		drawString(aci, ((float) x), ((float) y));
	}

	/**
	 * Creates a <code>TextLayout</code> and calls
	 * <code>TextLayout.draw(g,x,y)</code>.
	 * <P>
	 * This way if the AttributedCharacterIterator represents several different
	 * fills, they'll be handled correctly.
	 */
	@Override
	public void drawString(AttributedCharacterIterator aci, float x, float y) {
		TextLayout tl = new TextLayout(aci, getFontRenderContext());
		tl.draw(this, x, y);
	}

	/**
	 * Calls <code>fill(gv.getOutline(x,y));</code>
	 */
	@Override
	public void drawGlyphVector(GlyphVector gv, float x, float y) {
		fill(gv.getOutline(x, y));
	}

	/** Calls <code>transform(AffineTransform)</code> */
	@Override
	public void translate(int x, int y) {
		AffineTransform t = new AffineTransform();
		t.translate(x, y);
		transform(t);
	}

	/** Calls <code>transform(AffineTransform)</code> */
	@Override
	public void translate(double x, double y) {
		AffineTransform t = new AffineTransform();
		t.translate(x, y);
		transform(t);
	}

	/** Calls <code>transform(AffineTransform)</code> */
	@Override
	public void rotate(double theta) {
		AffineTransform t = new AffineTransform();
		t.rotate(theta);
		transform(t);
	}

	/** Calls <code>transform(AffineTransform)</code> */
	@Override
	public void rotate(double theta, double centerX, double centerY) {
		AffineTransform t = new AffineTransform();
		t.rotate(theta, centerX, centerY);
		transform(t);
	}

	/** Calls <code>transform(AffineTransform)</code> */
	@Override
	public void scale(double scaleX, double scaleY) {
		AffineTransform t = new AffineTransform();
		t.scale(scaleX, scaleY);
		transform(t);
	}

	/** Calls <code>transform(AffineTransform)</code> */
	@Override
	public void shear(double shx, double shy) {
		AffineTransform t = new AffineTransform();
		t.shear(shx, shy);
		transform(t);
	}

	/** Calls <code>setTransform(getTransform().concatenate(t))</code> */
	@Override
	public void transform(AffineTransform t) {
		AffineTransform t2 = getTransform();
		t2.concatenate(t);
		setTransform(t2);
	}

	/**
	 * Calls <code>setPaint(c)</code>
	 */
	@Override
	public void setColor(Color c) {
		setPaint(c);
	}

	/**
	 * Calls <code>draw(Line2D)</code>
	 */
	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		Line2D l = new Line2D.Float(x1, y1, x2, y2);
		draw(l);
	}

	/**
	 * Calls <code>fill(Rectangle)</code>
	 */
	@Override
	public void fillRect(int x, int y, int w, int h) {
		Rectangle r = new Rectangle(x, y, w, h);
		fill(r);
	}

	/**
	 * Calls <code>draw(RoundRectangle2D)</code>
	 */
	@Override
	public void drawRoundRect(int x, int y, int w, int h, int arcW, int arcH) {
		RoundRectangle2D r = new RoundRectangle2D.Float(x, y, w, h, arcW, arcH);
		draw(r);
	}

	/**
	 * Calls <code>fill(RoundRectangle2D)</code>
	 */
	@Override
	public void fillRoundRect(int x, int y, int w, int h, int arcW, int arcH) {
		RoundRectangle2D r = new RoundRectangle2D.Float(x, y, w, h, arcW, arcH);
		fill(r);
	}

	/**
	 * Calls <code>draw(Ellipse2D)</code>
	 */
	@Override
	public void drawOval(int x, int y, int w, int h) {
		Ellipse2D e = new Ellipse2D.Float(x, y, w, h);
		draw(e);
	}

	/**
	 * Calls <code>fill(Ellipse2D)</code>
	 */
	@Override
	public void fillOval(int x, int y, int w, int h) {
		Ellipse2D e = new Ellipse2D.Float(x, y, w, h);
		fill(e);
	}

	/**
	 * Calls <code>draw(Arc2D)</code>
	 */
	@Override
	public void drawArc(int x, int y, int w, int h, int startAngle, int endAngle) {
		Arc2D a = new Arc2D.Float(x, y, w, h, startAngle, endAngle, Arc2D.OPEN);
		draw(a);
	}

	/**
	 * Calls <code>fill(Arc2D)</code>
	 */
	@Override
	public void fillArc(int x, int y, int w, int h, int startAngle, int endAngle) {
		Arc2D a = new Arc2D.Float(x, y, w, h, startAngle, endAngle, Arc2D.OPEN);
		fill(a);
	}

	/**
	 * Makes several calls to <code>drawLine()</code>
	 */
	@Override
	public void drawPolyline(int[] x, int[] y, int n) {
		for (int a = 0; a < n - 1; a++) {
			drawLine(x[a], y[a], x[a + 1], y[a + 1]);
		}
	}

	/**
	 * Calls <code>draw(Polygon)</code>
	 */
	@Override
	public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		Polygon p = new Polygon(xPoints, yPoints, nPoints);
		draw(p);
	}

	/**
	 * Calls <code>fill(Polygon)</code>
	 */
	@Override
	public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		Polygon p = new Polygon(xPoints, yPoints, nPoints);
		fill(p);
	}

	private static MediaTracker mediaTracker = null;

	static synchronized void loadImage(Image i) {
		if (i instanceof BufferedImage)
			return;
		if (mediaTracker == null) {
			mediaTracker = new MediaTracker(new JPanel());
		}
		mediaTracker.addImage(i, 0);
		try {
			mediaTracker.waitForAll();
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new RuntimeException();
		}
		mediaTracker.removeImage(i, 0);
	}

	/** Calls another <code>drawImage</code> method */
	@Override
	public boolean drawImage(Image i, int x, int y, ImageObserver obs) {
		if (i instanceof BufferedImage) {
			BufferedImage bi = (BufferedImage) i;
			return drawImage(bi, x, y, x + bi.getWidth(), y + bi.getHeight(),
					obs);
		}
		loadImage(i);
		return drawImage(i, x, y, x + i.getWidth(null), y + i.getHeight(null),
				obs);
	}

	/** Calls another <code>drawImage</code> method */
	@Override
	public boolean drawImage(Image i, int x, int y, int w, int h,
			ImageObserver obs) {
		if (i instanceof BufferedImage) {
			BufferedImage bi = (BufferedImage) i;
			return drawImage(bi, x, y, w, h, 0, 0, bi.getWidth(),
					bi.getHeight(), obs);
		}
		loadImage(i);
		return drawImage(i, x, y, w, h, 0, 0, i.getWidth(null),
				i.getHeight(null), obs);
	}

	/** Calls another <code>drawImage</code> method */
	@Override
	public boolean drawImage(Image i, int x, int y, Color bgColor,
			ImageObserver obs) {
		if (i instanceof BufferedImage) {
			BufferedImage bi = (BufferedImage) i;
			return drawImage(bi, x, y, x + bi.getWidth(), y + bi.getHeight(),
					bgColor, obs);
		}
		loadImage(i);
		return drawImage(i, x, y, x + i.getWidth(null), y + i.getHeight(null),
				bgColor, obs);
	}

	/** Calls another <code>drawImage</code> method */
	@Override
	public boolean drawImage(Image i, int x, int y, int w, int h,
			Color bgColor, ImageObserver obs) {
		if (i instanceof BufferedImage) {
			BufferedImage bi = (BufferedImage) i;
			return drawImage(bi, x, y, w, h, 0, 0, bi.getWidth(),
					bi.getHeight(), bgColor, obs);
		}
		loadImage(i);
		return drawImage(i, x, y, w, y, 0, 0, i.getWidth(null),
				i.getHeight(null), bgColor, obs);
	}

	/**
	 * Calls
	 * <code>drawImage(img, dx1, dy1, dy1, dy2, sx1, sy1, sx2, sy2, null, observer)</code>
	 * .
	 */
	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
			int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
		return drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null,
				observer);
	}

	/** Throws an UnsupportedOperationException. */
	@Override
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		throw new UnsupportedOperationException("copyArea() is not supported");
	}

	@Override
	public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
		if (onStroke) {
			return hit(rect, getStroke().createStrokedShape(s), false);
		}

		Area area = new Area(s);
		return area.intersects(rect);
	}

	static private final BufferedImage tinyScratch = new BufferedImage(1, 1,
			BufferedImage.TYPE_INT_ARGB);

	@Override
	public FontMetrics getFontMetrics(Font f) {
		// TODO make this more better?
		Graphics2D g = tinyScratch.createGraphics();
		g.setFont(getFont());
		FontMetrics returnValue = g.getFontMetrics();
		g.dispose();
		return returnValue;
	}
}