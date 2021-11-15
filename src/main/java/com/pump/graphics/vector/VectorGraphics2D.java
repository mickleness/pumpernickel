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
package com.pump.graphics.vector;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.pump.graphics.Graphics2DContext;
import com.pump.image.ImageSize;

/**
 * This is a Graphics2D that stores every drawing instruction as an
 * {@link Operation}. You can create a VectorGraphics2D independently, but it
 * also works well with the {@link VectorImage} class.
 * <p>
 * Graphics2D methods can generally be divided into two groups:
 * <ul>
 * <li>Methods that actually draw something. (For example: they change pixels,
 * like {@link Graphics2D#fill(Shape)}.) These are stored as Operations.</li>
 * <li>Methods that relate to properties for future Operations. (For example see
 * {@link Graphics2D#setComposite(AlphaComposite)} /
 * {@link Graphics2D#getComposite()}). These calls are all directed to the
 * {@link Graphics2DContext} class. Each new Operation records a snapshot
 * (clone) of the Graphics2DContext of this VectorGraphics2D when it was
 * created.</li>
 * </ul>
 */
public class VectorGraphics2D extends Graphics2D {

	protected Graphics2DContext context;
	protected List<Operation> operations;

	/**
	 * Create a new empty VectorGraphics2D.
	 */
	public VectorGraphics2D() {
		this(new Graphics2DContext(), new ArrayList<Operation>());
	}

	/**
	 * Create a VectorGraphics2D that appends all its Operations to the given
	 * argument.
	 * 
	 * @param operations
	 */
	public VectorGraphics2D(Graphics2DContext context,
			List<Operation> operations) {
		Objects.requireNonNull(context);
		Objects.requireNonNull(operations);
		this.operations = operations;
		this.context = context;
	}

	/**
	 * Add an Operation to this VectorGraphics2D.
	 * <p>
	 * This method immediately returns if this Graphics2D has been disposed.
	 * 
	 * @param operation
	 *            the Operation to add.
	 */
	protected void addOperation(Operation operation) {
		if (context.isDisposed())
			return;

		operations.add(operation);
	}

	/**
	 * Return the underlying list of all Operations.
	 */
	public List<Operation> getOperations() {
		return operations;
	}

	// context-related methods;

	/**
	 * Return a copy of the current Graphics2DContext.
	 */
	public Graphics2DContext getContext() {
		return context.clone();
	}

	@Override
	public void setComposite(Composite comp) {
		context.setComposite(comp);
	}

	@Override
	public void setPaint(Paint paint) {
		context.setPaint(paint);
	}

	@Override
	public void setStroke(Stroke s) {
		context.setStroke(s);
	}

	@Override
	public void setRenderingHint(Key hintKey, Object hintValue) {
		context.setRenderingHint(hintKey, hintValue);
	}

	@Override
	public Object getRenderingHint(Key hintKey) {
		return context.getRenderingHint(hintKey);
	}

	@Override
	public void setRenderingHints(Map<?, ?> hints) {
		context.setRenderingHints(hints);
	}

	@Override
	public void addRenderingHints(Map<?, ?> hints) {
		context.addRenderingHints(hints);
	}

	@Override
	public RenderingHints getRenderingHints() {
		return context.getRenderingHints();
	}

	@Override
	public void translate(int x, int y) {
		context.translate(x, y);
	}

	@Override
	public void translate(double tx, double ty) {
		context.translate(tx, ty);
	}

	@Override
	public void rotate(double theta) {
		context.rotate(theta);
	}

	@Override
	public void rotate(double theta, double x, double y) {
		context.rotate(theta, x, y);
	}

	@Override
	public void scale(double sx, double sy) {
		context.scale(sx, sy);
	}

	@Override
	public void shear(double shx, double shy) {
		context.shear(shx, shy);
	}

	@Override
	public void transform(AffineTransform Tx) {
		context.transform(Tx);
	}

	@Override
	public void setTransform(AffineTransform Tx) {
		context.setTransform(Tx);
	}

	@Override
	public AffineTransform getTransform() {
		return context.getTransform();
	}

	@Override
	public Paint getPaint() {
		return context.getPaint();
	}

	@Override
	public Composite getComposite() {
		return context.getComposite();
	}

	@Override
	public void setBackground(Color color) {
		context.setBackground(color);
	}

	@Override
	public Color getBackground() {
		return context.getBackground();
	}

	@Override
	public Stroke getStroke() {
		return context.getStroke();
	}

	@Override
	public void clip(Shape s) {
		context.clip(s);
	}

	@Override
	public FontRenderContext getFontRenderContext() {
		return context.getFontRenderContext();
	}

	@Override
	public VectorGraphics2D create() {
		return new VectorGraphics2D(getContext(), operations);
	}

	@Override
	public Color getColor() {
		return context.getColor();
	}

	@Override
	public void setColor(Color c) {
		context.setColor(c);
	}

	@Override
	public void setPaintMode() {
		context.setPaintMode();
	}

	@Override
	public void setXORMode(Color c1) {
		context.setXORMode(c1);
	}

	@Override
	public Font getFont() {
		return context.getFont();
	}

	@Override
	public void setFont(Font font) {
		context.setFont(font);
	}

	@Override
	public Rectangle getClipBounds() {
		return context.getClipBounds();
	}

	@Override
	public void clipRect(int x, int y, int width, int height) {
		context.clipRect(x, y, width, height);
	}

	@Override
	public void setClip(int x, int y, int width, int height) {
		context.setClip(x, y, width, height);
	}

	@Override
	public Shape getClip() {
		return context.getClip(true);
	}

	@Override
	public void setClip(Shape clip) {
		context.setClip(clip);
	}

	@Override
	public void dispose() {
		context.dispose();
	}

	// shape-related methods:

	@Override
	public void draw(Shape s) {
		addOperation(new DrawOperation(getContext(), s));
	}

	@Override
	public void fill(Shape s) {
		addOperation(new FillOperation(getContext(), s));
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		Line2D l = new Line2D.Float(x1, y1, x2, y2);
		draw(l);
	}

	@Override
	public void fillRect(int x, int y, int width, int height) {
		Rectangle r = new Rectangle(x, y, width, height);
		fill(r);
	}

	@Override
	public void drawRoundRect(int x, int y, int width, int height, int arcWidth,
			int arcHeight) {
		RoundRectangle2D r = new RoundRectangle2D.Float(x, y, width, height,
				arcWidth, arcHeight);
		draw(r);
	}

	@Override
	public void fillRoundRect(int x, int y, int width, int height, int arcWidth,
			int arcHeight) {
		RoundRectangle2D r = new RoundRectangle2D.Float(x, y, width, height,
				arcWidth, arcHeight);
		fill(r);
	}

	@Override
	public void drawOval(int x, int y, int width, int height) {
		Ellipse2D e = new Ellipse2D.Float(x, y, width, height);
		draw(e);
	}

	@Override
	public void drawRect(int x, int y, int width, int height) {
		// if we use super.drawRect(..) we'll get 4 lines,
		// which (depending on the opacity of the stroke) may be the wrong
		// behavior
		Rectangle r = new Rectangle(x, y, width, height);
		draw(r);
	}

	@Override
	public void fillOval(int x, int y, int width, int height) {
		Ellipse2D e = new Ellipse2D.Float(x, y, width, height);
		fill(e);
	}

	@Override
	public void drawArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		Arc2D a = new Arc2D.Float(x, y, width, height, startAngle, arcAngle,
				Arc2D.OPEN);
		draw(a);
	}

	@Override
	public void fillArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		Arc2D a = new Arc2D.Float(x, y, width, height, startAngle, arcAngle,
				Arc2D.OPEN);
		fill(a);
	}

	@Override
	public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
		if (nPoints == 0)
			return;
		Path2D p = new Path2D.Float();
		p.moveTo(xPoints[0], yPoints[0]);
		for (int a = 1; a < nPoints; a++) {
			p.lineTo(xPoints[a], yPoints[a]);
		}
		draw(p);
	}

	@Override
	public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		Polygon p = new Polygon(xPoints, yPoints, nPoints);
		draw(p);
	}

	@Override
	public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		Polygon p = new Polygon(xPoints, yPoints, nPoints);
		fill(p);
	}

	// image-related methods:

	@Override
	public boolean drawImage(Image img, AffineTransform xform,
			ImageObserver obs) {
		Graphics2D g = create();
		g.transform(xform);
		boolean returnValue = g.drawImage(img, 0, 0, obs);
		g.dispose();
		return returnValue;
	}

	/**
	 * This is shorthand for: <br>
	 * <code>
	 * BufferedImage img1 = op.filter(img, null);
	 * drawImage(img1, new AffineTransform(1f, 0f, 0f, 1f, x, y), null);
	 * </code>
	 */
	@Override
	public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
		// the Graphics2D javadoc for this method says its equivalent to:
		BufferedImage img1 = op.filter(img, null);
		drawImage(img1, new AffineTransform(1f, 0f, 0f, 1f, x, y), null);
	}

	@Override
	public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
		addOperation(new RenderedImageOperation(getContext(), img, xform));

	}

	@Override
	public void drawRenderableImage(RenderableImage img,
			AffineTransform xform) {
		addOperation(new RenderableImageOperation(getContext(), img, xform));
	}

	@Override
	public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
		Dimension size = ImageSize.get(img);
		return drawImage(img, x, y, x + size.width, y + size.height, 0, 0,
				size.width, size.height, null, observer);
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height,
			ImageObserver observer) {
		Dimension size = ImageSize.get(img);
		return drawImage(img, x, y, x + width, y + height, 0, 0, size.width,
				size.height, null, observer);
	}

	@Override
	public boolean drawImage(Image img, int x, int y, Color bgcolor,
			ImageObserver observer) {
		Dimension size = ImageSize.get(img);
		return drawImage(img, x, y, size.width + x, size.height + y, 0, 0,
				size.width, size.height, bgcolor, observer);
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height,
			Color bgcolor, ImageObserver observer) {
		Dimension size = ImageSize.get(img);
		return drawImage(img, x, y, x + width, y + height, 0, 0, size.width,
				size.height, bgcolor, null);
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
			int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
		return drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null,
				observer);
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
			int sx1, int sy1, int sx2, int sy2, Color bgcolor,
			ImageObserver observer) {

		// this is what SunGraphics2D does for null images:
		if (img == null)
			return true;

		addOperation(new ImageOperation(getContext(), img, dx1, dy1, dx2, dy2,
				sx1, sy1, sx2, sy2, bgcolor));
		return true;
	}

	// string-related methods:

	@Override
	public void drawString(String str, int x, int y) {
		drawString(str, (float) x, (float) y);
	}

	@Override
	public void drawString(String str, float x, float y) {
		addOperation(new StringOperation(getContext(), str, x, y));

		// this is equivalent to:
		// TextLayout layout = new TextLayout(iterator, getFontRenderContext());
		// layout.draw(this, x, y);

		// using a GlyphVector may work for English, but limited experience
		// suggests it may fail for Arabic or other more complex languages.
		// (TextLayouts do support Arabic well, though.)

		// GlyphVector glyphVector = getFont().createGlyphVector(
		// getFontRenderContext(), str);
		// drawGlyphVector(glyphVector, x, y);
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		drawString(iterator, (float) x, (float) y);
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, float x,
			float y) {
		addOperation(new AttributedCharacterIteratorOperation(getContext(),
				iterator, x, y));

		// this is equivalent to:
		// TextLayout layout = new TextLayout(iterator, getFontRenderContext());
		// layout.draw(this, x, y);
	}

	@Override
	public void drawGlyphVector(GlyphVector g, float x, float y) {
		addOperation(new GlyphVectorOperation(getContext(), g, x, y));
	}

	// other:

	@Override
	public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
		if (onStroke) {
			return hit(rect, getStroke().createStrokedShape(s), false);
		}

		Area area = new Area(s);
		return area.intersects(rect);
	}

	@Override
	public GraphicsConfiguration getDeviceConfiguration() {
		return new VectorGraphicsConfiguration();
	}

	@Override
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		addOperation(new CopyAreaOperation(getContext(), x, y, width, height,
				dx, dy));
	}

	@Override
	public void clearRect(int x, int y, int width, int height) {
		// this is based on SunGraphics2D's implementation of clearRect
		Graphics2DContext context = getContext();
		context.setComposite(AlphaComposite.Src);
		context.setColor(getBackground());
		Rectangle rect = new Rectangle(x, y, width, height);
		addOperation(new FillOperation(context, rect));
	}

	@Override
	public FontMetrics getFontMetrics(Font f) {
		// This one stumped me; I'm not sure how we're supposed to handle this?
		BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bi.createGraphics();
		context.install(g);
		FontMetrics returnValue = g.getFontMetrics(f);
		g.dispose();
		return returnValue;
	}
}