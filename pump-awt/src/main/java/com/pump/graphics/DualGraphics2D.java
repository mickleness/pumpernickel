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

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
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
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.Map;

/**
 * This delegates to two underlying Graphics2Ds, so you can send the same
 * drawing instructions to multiple destinations.
 * <P>
 * All methods that modify the destination are sent to both Graphics2D objects.
 * All methods that only return properties about the Graphics2D objects talk
 * only to the first Graphics2D object.
 * 
 */
public class DualGraphics2D extends Graphics2D {
	/**
	 * Creates a binary tree structure of DualGraphics2Ds so every array
	 * receives the same drawing instructions.
	 * 
	 * @param array
	 *            an array of 2 or more Graphics2D objects.
	 * @return one Graphics2D to control them all.
	 */
	public DualGraphics2D create(Graphics2D[] array) {
		if (array.length < 2)
			throw new IllegalArgumentException("array length (" + array.length
					+ ") must be greater than 1.");

		if (array.length == 2) {
			return new DualGraphics2D(array[0], array[1]);
		}
		ArrayList<Graphics2D> newList = new ArrayList<Graphics2D>();
		for (int a = 0; a < array.length; a++) {
			if (a + 1 < array.length) {
				newList.add(new DualGraphics2D(array[a], array[a + 1]));
				a++;
			} else {
				newList.add(array[a]);
			}
		}
		return create(newList.toArray(new Graphics2D[newList.size()]));
	}

	public final Graphics2D g1, g2;

	public DualGraphics2D(Graphics2D g1, Graphics2D g2) {
		this.g1 = g1;
		this.g2 = g2;
	}

	@Override
	public void addRenderingHints(Map<?, ?> hints) {
		g1.addRenderingHints(hints);
		g2.addRenderingHints(hints);
	}

	@Override
	public void clearRect(int x, int y, int width, int height) {
		g1.clearRect(x, y, width, height);
		g2.clearRect(x, y, width, height);
	}

	@Override
	public void clip(Shape s) {
		g1.clip(s);
		g2.clip(s);
	}

	@Override
	public void clipRect(int x, int y, int width, int height) {
		g1.clipRect(x, y, width, height);
		g2.clipRect(x, y, width, height);
	}

	@Override
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		g1.copyArea(x, y, width, height, dx, dy);
		g2.copyArea(x, y, width, height, dx, dy);
	}

	@Override
	public Graphics create() {
		return new DualGraphics2D((Graphics2D) g1.create(), (Graphics2D) g2
				.create());
	}

	@Override
	public Graphics create(int x, int y, int width, int height) {
		return new DualGraphics2D((Graphics2D) g1.create(x, y, width, height),
				(Graphics2D) g2.create(x, y, width, height));
	}

	@Override
	public void dispose() {
		g1.dispose();
		g2.dispose();
	}

	@Override
	public void draw(Shape s) {
		g1.draw(s);
		g2.draw(s);
	}

	@Override
	public void draw3DRect(int x, int y, int width, int height, boolean raised) {
		g1.draw3DRect(x, y, width, height, raised);
		g2.draw3DRect(x, y, width, height, raised);
	}

	@Override
	public void drawArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		g1.drawArc(x, y, width, height, startAngle, arcAngle);
		g2.drawArc(x, y, width, height, startAngle, arcAngle);
	}

	@Override
	public void drawBytes(byte[] data, int offset, int length, int x, int y) {
		g1.drawBytes(data, offset, length, x, y);
		g2.drawBytes(data, offset, length, x, y);
	}

	@Override
	public void drawChars(char[] data, int offset, int length, int x, int y) {
		g1.drawChars(data, offset, length, x, y);
		g2.drawChars(data, offset, length, x, y);
	}

	@Override
	public void drawGlyphVector(GlyphVector g, float x, float y) {
		g1.drawGlyphVector(g, x, y);
		g2.drawGlyphVector(g, x, y);
	}

	@Override
	public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
		g1.drawImage(img, op, x, y);
		g2.drawImage(img, op, x, y);
	}

	@Override
	public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
		boolean b1 = g1.drawImage(img, xform, obs);
		boolean b2 = g2.drawImage(img, xform, obs);
		return b1 && b2;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, Color bgcolor,
			ImageObserver observer) {
		boolean b1 = g1.drawImage(img, x, y, bgcolor, observer);
		boolean b2 = g2.drawImage(img, x, y, bgcolor, observer);
		return b1 && b2;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
		boolean b1 = g1.drawImage(img, x, y, observer);
		boolean b2 = g2.drawImage(img, x, y, observer);
		return b1 && b2;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height,
			Color bgcolor, ImageObserver observer) {
		boolean b1 = g1.drawImage(img, x, y, width, height, bgcolor, observer);
		boolean b2 = g2.drawImage(img, x, y, width, height, bgcolor, observer);
		return b1 && b2;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height,
			ImageObserver observer) {
		boolean b1 = g1.drawImage(img, x, y, width, height, observer);
		boolean b2 = g2.drawImage(img, x, y, width, height, observer);
		return b1 && b2;
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
			int sx1, int sy1, int sx2, int sy2, Color bgcolor,
			ImageObserver observer) {
		boolean b1 = g1.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2,
				bgcolor, observer);
		boolean b2 = g2.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2,
				bgcolor, observer);
		return b1 && b2;
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
			int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
		boolean b1 = g1.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2,
				observer);
		boolean b2 = g2.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2,
				observer);
		return b1 && b2;
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		g1.drawLine(x1, y1, x2, y2);
		g2.drawLine(x1, y1, x2, y2);
	}

	@Override
	public void drawOval(int x, int y, int width, int height) {
		g1.drawOval(x, y, width, height);
		g2.drawOval(x, y, width, height);
	}

	@Override
	public void drawPolygon(int[] points, int[] points2, int points3) {
		g1.drawPolygon(points, points2, points3);
		g2.drawPolygon(points, points2, points3);
	}

	@Override
	public void drawPolygon(Polygon p) {
		g1.drawPolygon(p);
		g2.drawPolygon(p);
	}

	@Override
	public void drawPolyline(int[] points, int[] points2, int points3) {
		g1.drawPolyline(points, points2, points3);
		g2.drawPolyline(points, points2, points3);
	}

	@Override
	public void drawRect(int x, int y, int width, int height) {
		g1.drawRect(x, y, width, height);
		g2.drawRect(x, y, width, height);
	}

	@Override
	public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
		g1.drawRenderableImage(img, xform);
		g2.drawRenderableImage(img, xform);
	}

	@Override
	public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
		g1.drawRenderedImage(img, xform);
		g2.drawRenderedImage(img, xform);
	}

	@Override
	public void drawRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		g1.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
		g2.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, float x,
			float y) {
		int index = iterator.getIndex();
		g1.drawString(iterator, x, y);
		iterator.setIndex(index);
		g2.drawString(iterator, x, y);
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		int index = iterator.getIndex();
		g1.drawString(iterator, x, y);
		iterator.setIndex(index);
		g2.drawString(iterator, x, y);
	}

	@Override
	public void drawString(String s, float x, float y) {
		g1.drawString(s, x, y);
		g2.drawString(s, x, y);
	}

	@Override
	public void drawString(String str, int x, int y) {
		g1.drawString(str, x, y);
		g2.drawString(str, x, y);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (obj == this)
			return true;
		if (!(obj instanceof DualGraphics2D))
			return false;
		DualGraphics2D g = (DualGraphics2D) obj;
		return (g.g1.equals(g1) && g.g2.equals(g2));
	}

	@Override
	public void fill(Shape s) {
		g1.fill(s);
		g2.fill(s);
	}

	@Override
	public void fill3DRect(int x, int y, int width, int height, boolean raised) {
		g1.fill3DRect(x, y, width, height, raised);
		g2.fill3DRect(x, y, width, height, raised);
	}

	@Override
	public void fillArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		g1.fillArc(x, y, width, height, startAngle, arcAngle);
		g2.fillArc(x, y, width, height, startAngle, arcAngle);
	}

	@Override
	public void fillOval(int x, int y, int width, int height) {
		g1.fillOval(x, y, width, height);
		g2.fillOval(x, y, width, height);
	}

	@Override
	public void fillPolygon(int[] points, int[] points2, int points3) {
		g1.fillPolygon(points, points2, points3);
		g2.fillPolygon(points, points2, points3);
	}

	@Override
	public void fillPolygon(Polygon p) {
		g1.fillPolygon(p);
		g2.fillPolygon(p);
	}

	@Override
	public void fillRect(int x, int y, int width, int height) {
		g1.fillRect(x, y, width, height);
		g2.fillRect(x, y, width, height);
	}

	@Override
	public void fillRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		g1.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
		g2.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
	}

	@Override
	public void finalize() {
		g1.finalize();
		g2.finalize();
	}

	@Override
	public Color getBackground() {
		return g1.getBackground();
	}

	@Override
	public Shape getClip() {
		return g1.getClip();
	}

	@Override
	public Rectangle getClipBounds() {
		return g1.getClipBounds();
	}

	@Override
	public Rectangle getClipBounds(Rectangle r) {
		return g1.getClipBounds(r);
	}

	@SuppressWarnings("deprecation")
	@Override
	public Rectangle getClipRect() {
		return g1.getClipRect();
	}

	@Override
	public Color getColor() {
		return g1.getColor();
	}

	@Override
	public Composite getComposite() {
		return g1.getComposite();
	}

	@Override
	public GraphicsConfiguration getDeviceConfiguration() {
		return g1.getDeviceConfiguration();
	}

	@Override
	public Font getFont() {
		return g1.getFont();
	}

	@Override
	public FontMetrics getFontMetrics() {
		return g1.getFontMetrics();
	}

	@Override
	public FontMetrics getFontMetrics(Font f) {
		return g1.getFontMetrics(f);
	}

	@Override
	public FontRenderContext getFontRenderContext() {
		return g1.getFontRenderContext();
	}

	@Override
	public Paint getPaint() {
		return g1.getPaint();
	}

	@Override
	public Object getRenderingHint(Key hintKey) {
		return g1.getRenderingHint(hintKey);
	}

	@Override
	public RenderingHints getRenderingHints() {
		return g1.getRenderingHints();
	}

	@Override
	public Stroke getStroke() {
		return g1.getStroke();
	}

	@Override
	public AffineTransform getTransform() {
		return g1.getTransform();
	}

	@Override
	public int hashCode() {
		return g1.hashCode();
	}

	@Override
	public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
		return g1.hit(rect, s, onStroke);
	}

	@Override
	public boolean hitClip(int x, int y, int width, int height) {
		return g1.hitClip(x, y, width, height);
	}

	@Override
	public void rotate(double theta, double x, double y) {
		g1.rotate(theta, x, y);
		g2.rotate(theta, x, y);
	}

	@Override
	public void rotate(double theta) {
		g1.rotate(theta);
		g2.rotate(theta);
	}

	@Override
	public void scale(double sx, double sy) {
		g1.scale(sx, sy);
		g2.scale(sx, sy);
	}

	@Override
	public void setBackground(Color color) {
		g1.setBackground(color);
		g2.setBackground(color);
	}

	@Override
	public void setClip(int x, int y, int width, int height) {
		g1.setClip(x, y, width, height);
		g2.setClip(x, y, width, height);
	}

	@Override
	public void setClip(Shape clip) {
		g1.setClip(clip);
		g2.setClip(clip);
	}

	@Override
	public void setColor(Color c) {
		g1.setColor(c);
		g2.setColor(c);
	}

	@Override
	public void setComposite(Composite comp) {
		g1.setComposite(comp);
		g2.setComposite(comp);
	}

	@Override
	public void setFont(Font font) {
		g1.setFont(font);
		g2.setFont(font);
	}

	@Override
	public void setPaint(Paint paint) {
		g1.setPaint(paint);
		g2.setPaint(paint);
	}

	@Override
	public void setPaintMode() {
		g1.setPaintMode();
		g2.setPaintMode();
	}

	@Override
	public void setRenderingHint(Key hintKey, Object hintValue) {
		g1.setRenderingHint(hintKey, hintValue);
		g2.setRenderingHint(hintKey, hintValue);
	}

	@Override
	public void setRenderingHints(Map<?, ?> hints) {
		g1.setRenderingHints(hints);
		g2.setRenderingHints(hints);
	}

	@Override
	public void setStroke(Stroke s) {
		g1.setStroke(s);
		g2.setStroke(s);
	}

	@Override
	public void setTransform(AffineTransform Tx) {
		g1.setTransform(Tx);
		g2.setTransform(Tx);
	}

	@Override
	public void setXORMode(Color c1) {
		g1.setXORMode(c1);
		g2.setXORMode(c1);
	}

	@Override
	public void shear(double shx, double shy) {
		g1.shear(shx, shy);
		g2.shear(shx, shy);
	}

	@Override
	public String toString() {
		return "DualGraphics2D[ g1 = " + g1.toString() + ", g2 = "
				+ g2.toString() + "]";
	}

	@Override
	public void transform(AffineTransform Tx) {
		g1.transform(Tx);
		g2.transform(Tx);
	}

	@Override
	public void translate(double tx, double ty) {
		g1.translate(tx, ty);
		g2.translate(tx, ty);
	}

	@Override
	public void translate(int x, int y) {
		g1.translate(x, y);
		g2.translate(x, y);
	}
}