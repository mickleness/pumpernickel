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
import java.util.Map;

import com.pump.text.ColoredIterator;


/** A Graphics2D that only delegates methods that might
 * relate to drawing text.  Other drawing instructions
 * are dropped.
 * <P>Optionally you can also specifically one magic color
 * that all text should be rendered in.  If this is
 * not provided then text will render in its default color.
 *
 */
public class TextOnlyGraphics2D extends Graphics2D {
	
	public final Graphics2D realGraphics;
	public final Color textColor;

	/**
	 * 
	 * @param g the Graphics2D to filter.
	 * @param textColor an optional color that can override the
	 * default color for rendered text.
	 */
	public TextOnlyGraphics2D(Graphics2D g, Color textColor) {
		realGraphics = (Graphics2D)g.create();
		this.textColor = textColor;
		if(textColor!=null) {
			realGraphics.setColor(textColor);
		}
	}

	@Override
	public Graphics create() {
		return new TextOnlyGraphics2D( (Graphics2D)realGraphics.create(), textColor );
	}

	@Override
	public void fill(Shape s) {
		//weeeelll... sometimes text could be rendered
		//via shapes... but how often, really?
		//if(RectangleReader.isRectangle(s))
		//	return;
		
		return;
	}

	@Override
	public void fillRect(int x, int y, int width, int height) {
		return;
	}

	@Override
	public void draw(Shape s) {
		return;
	}

	@Override
	public void draw3DRect(int x, int y, int width, int height, boolean raised) {
		return;
	}

	@Override
	public void drawArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		return;
	}

	@Override
	public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
		return;
	}

	@Override
	public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
		return true;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, Color bgcolor,
			ImageObserver observer) {
		return true;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
		return true;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height,
			Color bgcolor, ImageObserver observer) {
		return true;
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height,
			ImageObserver observer) {
		return true;
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
			int sx1, int sy1, int sx2, int sy2, Color bgcolor,
			ImageObserver observer) {
		return true;
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
			int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
		return true;
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		return;
	}

	@Override
	public void drawOval(int x, int y, int width, int height) {
		return;
	}

	@Override
	public void drawPolygon(int[] points, int[] points2, int points3) {
		return;
	}

	@Override
	public void drawPolygon(Polygon p) {
		return;
	}

	@Override
	public void drawPolyline(int[] points, int[] points2, int points3) {
		return;
	}

	@Override
	public void drawRect(int x, int y, int width, int height) {
		return;
	}

	@Override
	public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
		return;
	}

	@Override
	public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
		return;
	}

	@Override
	public void drawRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		return;
	}

	@Override
	public void fill3DRect(int x, int y, int width, int height, boolean raised) {
		return;
	}

	@Override
	public void fillArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		return;
	}

	@Override
	public void fillOval(int x, int y, int width, int height) {
		return;
	}

	@Override
	public void fillPolygon(int[] points, int[] points2, int points3) {
		return;
	}

	@Override
	public void fillPolygon(Polygon p) {
		return;
	}

	@Override
	public void fillRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		return;
	}

	@Override
	public void addRenderingHints(Map<?, ?> hints) {
		realGraphics.addRenderingHints(hints);
	}

	@Override
	public void clip(Shape s) {
		realGraphics.clip(s);
	}

	@Override
	public void drawGlyphVector(GlyphVector g, float x, float y) {
		realGraphics.drawGlyphVector(g, x, y);
	}

	@Override
	public void drawString(String str, int x, int y) {
		realGraphics.drawString(str, x, y);
	}

	@Override
	public void drawString(String s, float x, float y) {
		realGraphics.drawString(s, x, y);
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		if(textColor!=null) {
			iterator = new ColoredIterator(iterator, textColor);
		}
		realGraphics.drawString(iterator, x, y);
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, float x,
			float y) {
		if(textColor!=null) {
			iterator = new ColoredIterator(iterator, textColor);
		}
		realGraphics.drawString(iterator, x, y);
	}

	@Override
	public Color getBackground() {
		return realGraphics.getBackground();
	}

	@Override
	public Composite getComposite() {
		return realGraphics.getComposite();
	}

	@Override
	public GraphicsConfiguration getDeviceConfiguration() {
		return realGraphics.getDeviceConfiguration();
	}

	@Override
	public FontRenderContext getFontRenderContext() {
		return realGraphics.getFontRenderContext();
	}

	@Override
	public Paint getPaint() {
		return realGraphics.getPaint();
	}

	@Override
	public Object getRenderingHint(Key hintKey) {
		return realGraphics.getRenderingHint(hintKey);
	}

	@Override
	public RenderingHints getRenderingHints() {
		return realGraphics.getRenderingHints();
	}

	@Override
	public Stroke getStroke() {
		return realGraphics.getStroke();
	}

	@Override
	public AffineTransform getTransform() {
		return realGraphics.getTransform();
	}

	@Override
	public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
		return realGraphics.hit(rect, s, onStroke);
	}

	@Override
	public void rotate(double theta) {
		realGraphics.rotate(theta);
	}

	@Override
	public void rotate(double theta, double x, double y) {
		realGraphics.rotate(theta, x, y);
	}

	@Override
	public void scale(double sx, double sy) {
		realGraphics.scale(sx, sy);
	}

	@Override
	public void setBackground(Color color) {
		realGraphics.setBackground(color);
	}

	@Override
	public void setComposite(Composite comp) {
		realGraphics.setComposite(comp);
	}

	@Override
	public void setPaint(Paint paint) {
		if(textColor==null)
			realGraphics.setPaint(paint);
	}

	@Override
	public void setRenderingHint(Key hintKey, Object hintValue) {
		realGraphics.setRenderingHint(hintKey, hintValue);
	}

	@Override
	public void setRenderingHints(Map<?, ?> hints) {
		realGraphics.setRenderingHints(hints);
	}

	@Override
	public void setStroke(Stroke s) {
		realGraphics.setStroke(s);
	}

	@Override
	public void setTransform(AffineTransform Tx) {
		realGraphics.setTransform(Tx);
	}

	@Override
	public void shear(double shx, double shy) {
		realGraphics.shear(shx, shy);
	}

	@Override
	public void transform(AffineTransform Tx) {
		realGraphics.transform(Tx);
	}

	@Override
	public void translate(int x, int y) {
		realGraphics.translate(x, y);
	}

	@Override
	public void translate(double tx, double ty) {
		realGraphics.translate(tx, ty);
	}

	@Override
	public void clearRect(int x, int y, int width, int height) {
		realGraphics.clearRect(x, y, width, height);
	}

	@Override
	public void clipRect(int x, int y, int width, int height) {
		realGraphics.clipRect(x, y, width, height);
	}

	@Override
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		realGraphics.copyArea(x, y, width, height, dx, dy);
	}

	@Override
	public void dispose() {
		realGraphics.dispose();
	}

	@Override
	public Shape getClip() {
		return realGraphics.getClip();
	}

	@Override
	public Rectangle getClipBounds() {
		return realGraphics.getClipBounds();
	}

	@Override
	public Color getColor() {
		return realGraphics.getColor();
	}

	@Override
	public Font getFont() {
		return realGraphics.getFont();
	}

	@Override
	public FontMetrics getFontMetrics(Font f) {
		return realGraphics.getFontMetrics();
	}

	@Override
	public void setClip(Shape clip) {
		realGraphics.setClip(clip);
	}

	@Override
	public void setClip(int x, int y, int width, int height) {
		realGraphics.setClip(x, y, width, height);
	}

	@Override
	public void setColor(Color c) {
		if(textColor==null)
			realGraphics.setColor(c);
	}

	@Override
	public void setFont(Font font) {
		realGraphics.setFont(font);
	}

	@Override
	public void setPaintMode() {
		realGraphics.setPaintMode();
	}

	@Override
	public void setXORMode(Color c1) {
		realGraphics.setXORMode(c1);
	}
}