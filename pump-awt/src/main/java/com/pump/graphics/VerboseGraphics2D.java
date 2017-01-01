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

import com.pump.geom.ShapeStringUtils;

/** This sits on top of another Graphics2D and relays information to the console
 * about operations are being performed.
 *
 */
public class VerboseGraphics2D extends Graphics2D {
	public final Graphics2D g;
	public boolean output = true;

	private static int indent = 0;
	
	private static int idCtr = 0;
	public final int id = idCtr++;
	
	
	public VerboseGraphics2D(Graphics2D g) {
		this(g,true);
	}

	public VerboseGraphics2D(Graphics2D g,boolean output) {
		this.g = g;
		this.output = output;
	}

	private String i() {
		StringBuffer sb = new StringBuffer();
		sb.append(id+": ");
		for(int a = 0; a<indent; a++) {
			sb.append("  ");
		}
		return sb.toString();
	}

	private String s(Shape shape) {
		String text = ShapeStringUtils.toString(shape);
		if(text.length()>80) {
			text = text.substring(0,80);
			int i = text.lastIndexOf(' ');
			text = text.substring(0,i);
			text = text+" ...";
		}
		return text;
	}

	private String s(char[] array) {
		//TODO:
		return "...";
	}

	private String s(byte[] array) {
		//TODO:
		return "...";
	}

	private String s(AttributedCharacterIterator aci) {
		//TODO:
		return "...";
	}

	public void addRenderingHints(Map<?, ?> hints) {
		if(output) {
			System.out.println(i()+"addRenderingHints( ... )");
		}
		indent++;
		try {
			g.addRenderingHints(hints);
		} finally {
			indent--;
		}
	}

	public void clearRect(int x, int y, int width, int height) {
		if(output) {
			System.out.println(i()+"clearRect("+x+", "+y+", "+width+", "+height+" )");
		}
		indent++;
		try {
			g.clearRect(x, y, width, height);
		} finally {
			indent--;
		}
	}

	public void clip(Shape s) {
		if(output) {
			System.out.println(i()+"clip( "+s(s)+" )");
		}
		indent++;
		try {
			g.clip(s);
		} finally {
			indent--;
		}
	}

	public void clipRect(int x, int y, int width, int height) {
		if(output) {
			System.out.println(i()+"clipRect( "+x+", "+y+", "+width+", "+height+" )");
		}
		indent++;
		try {
			g.clipRect(x, y, width, height);
		} finally {
			indent--;
		}
	}

	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		if(output) {
			System.out.println(i()+"copyArea( "+x+", "+y+", "+width+", "+height+", "+dx+", "+dy+" )");
		}
		indent++;
		try {
			g.copyArea(x, y, width, height, dx, dy);
		} finally {
			indent--;
		}
	}

	public Graphics create() {
		if(output) {
			System.out.println(i()+"create()");
		}
		indent++;
		try {
			return new VerboseGraphics2D((Graphics2D)g.create(),output);
		} finally {
			indent--;
		}
	}

	public Graphics create(int x, int y, int width, int height) {
		if(output) {
			System.out.println(i()+"create( "+x+", "+y+", "+width+", "+height+" )");
		}
		indent++;
		try {
			return new VerboseGraphics2D((Graphics2D)g.create(x, y, width, height),output);
		} finally {
			indent--;
		}
	}

	public void dispose() {
		if(output) {
			System.out.println(i()+"dispose()");
		}
		indent++;
		try {
			g.dispose();
		} finally {
			indent--;
		}
	}

	public void draw(Shape s) {
		if(output) {
			System.out.println(i()+"draw( "+s(s)+" )");
		}
		indent++;
		try {
			g.draw(s);
		} finally {
			indent--;
		}
	}

	public void draw3DRect(int x, int y, int width, int height, boolean raised) {
		if(output) {
			System.out.println(i()+"draw3DRect( "+x+", "+y+", "+width+", "+height+", "+raised+" )");
		}
		indent++;
		try {
			g.draw3DRect(x, y, width, height, raised);
		} finally {
			indent--;
		}
	}

	public void drawArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		if(output) {
			System.out.println(i()+"drawArc( "+x+", "+y+", "+width+", "+height+", "+startAngle+", "+arcAngle+" )");
		}
		indent++;
		try {
			g.drawArc(x, y, width, height, startAngle, arcAngle);
		} finally {
			indent--;
		}
	}

	public void drawBytes(byte[] data, int offset, int length, int x, int y) {
		if(output) {
			System.out.println(i()+"drawBytes( "+s(data)+", "+offset+", "+length+", "+x+", "+y+" )");
		}
		indent++;
		try {
			g.drawBytes(data, offset, length, x, y);
		} finally {
			indent--;
		}
	}

	public void drawChars(char[] data, int offset, int length, int x, int y) {
		if(output) {
			System.out.println(i()+"drawChars( "+s(data)+", "+offset+", "+length+", "+x+", "+y+" )");
		}
		indent++;
		try {
			g.drawChars(data, offset, length, x, y);
		} finally {
			indent--;
		}
	}

	public void drawGlyphVector(GlyphVector gv, float x, float y) {
		if(output) {
			System.out.println(i()+"drawGlyphVector( ... )");
		}
		indent++;
		try {
			g.drawGlyphVector(gv, x, y);
		} finally {
			indent--;
		}
	}

	public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
		if(output) {
			System.out.println(i()+"drawImage( bi, "+op+", "+x+", "+y+" )");
		}
		indent++;
		try {
			g.drawImage(img, op, x, y);
		} finally {
			indent--;
		}
	}

	public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
		if(output) {
			System.out.println(i()+"drawImage( img, "+xform+", obs )");
		}
		indent++;
		try {
			boolean returnValue = g.drawImage(img, xform, obs);
			System.out.println(i()+" = "+returnValue);
			return returnValue;
		} finally {
			indent--;
		}
	}

	public boolean drawImage(Image img, int x, int y, Color bgcolor,
			ImageObserver observer) {
		if(output) {
			System.out.println(i()+"drawImage( img, "+x+", "+y+", "+bgcolor+", obs )");
		}
		indent++;
		try {
			boolean returnValue = g.drawImage(img, x, y, bgcolor, observer);
			System.out.println(i()+" = "+returnValue);
			return returnValue;
		} finally {
			indent--;
		}
	}

	public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
		if(output) {
			System.out.println(i()+"drawImage( img, "+x+", "+y+", observer )");
		}
		indent++;
		try {
			boolean returnValue = g.drawImage(img, x, y, observer);
			System.out.println(i()+" = "+returnValue);
			return returnValue;
		} finally {
			indent--;
		}
	}

	public boolean drawImage(Image img, int x, int y, int width, int height,
			Color bgcolor, ImageObserver observer) {
		if(output) {
			System.out.println(i()+"drawImage( img, "+x+", "+y+", "+width+", "+height+", "+bgcolor+", observer )");
		}
		indent++;
		try {
			boolean returnValue = g.drawImage(img, x, y, width, height, bgcolor, observer);
			System.out.println(i()+" = "+returnValue);
			return returnValue;
		} finally {
			indent--;
		}
	}

	public boolean drawImage(Image img, int x, int y, int width, int height,
			ImageObserver observer) {
		if(output) {
			System.out.println(i()+"drawImage( img, "+x+", "+y+", "+width+", "+height+", observer )");
		}
		indent++;
		try {
			boolean returnValue = g.drawImage(img, x, y, width, height, observer);
			System.out.println(i()+" = "+returnValue);
			return returnValue;
		} finally {
			indent--;
		}
	}

	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
			int sx1, int sy1, int sx2, int sy2, Color bgcolor,
			ImageObserver observer) {
		if(output) {
			System.out.println(i()+"drawImage( img, "+dx1+", "+dy1+", "+dx2+", "+dy2+", "+sx1+", "+sy1+", "+sx2+", "+sy2+", "+bgcolor+", observer )");
		}
		indent++;
		try {
			boolean returnValue = g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2,
					bgcolor, observer);
			System.out.println(i()+" = "+returnValue);
			return returnValue;
		} finally {
			indent--;
		}
	}

	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
			int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
		if(output) {
			System.out.println(i()+"drawImage( img, "+dx1+", "+dy1+", "+dx2+", "+dy2+", "+sx1+", "+sy1+", "+sx2+", "+sy2+", observer )");
		}
		indent++;
		try {
			boolean returnValue = g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2,
					observer);
			System.out.println(i()+" = "+returnValue);
			return returnValue;
		} finally {
			indent--;
		}
	}

	public void drawLine(int x1, int y1, int x2, int y2) {
		if(output) {
			System.out.println(i()+"drawLine( "+x1+", "+y1+", "+x2+", "+y2+" )");
		}
		indent++;
		try {
			g.drawLine(x1, y1, x2, y2);
		} finally {
			indent--;
		}
	}

	public void drawOval(int x, int y, int width, int height) {
		if(output) {
			System.out.println(i()+"drawOval( "+x+", "+y+", "+width+", "+height+" )");
		}
		indent++;
		try {
			g.drawOval(x, y, width, height);
		} finally {
			indent--;
		}
	}

	public void drawPolygon(int[] points, int[] points2, int points3) {
		if(output) {
			System.out.println(i()+"drawPolygon( ..., ..., "+points3+" )");
		}
		indent++;
		try {
			g.drawPolygon(points, points2, points3);
		} finally {
			indent--;
		}
	}

	public void drawPolygon(Polygon p) {
		if(output) {
			System.out.println(i()+"drawPolygon( "+s(p)+" )");
		}
		indent++;
		try {
			g.drawPolygon(p);
		} finally {
			indent--;
		}
	}

	public void drawPolyline(int[] points, int[] points2, int points3) {
		if(output) {
			System.out.println(i()+"drawPolyline( ..., ..., "+points3+" )");
		}
		indent++;
		try {
			g.drawPolyline(points, points2, points3);
		} finally {
			indent--;
		}
	}

	public void drawRect(int x, int y, int width, int height) {
		if(output) {
			System.out.println(i()+"drawRect( "+x+", "+y+", "+width+", "+height+" )");
		}
		indent++;
		try {
			g.drawRect(x, y, width, height);
		} finally {
			indent--;
		}
	}

	public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
		if(output) {
			System.out.println(i()+"drawRenderableImage( img, "+xform+" )");
		}
		indent++;
		try {
			g.drawRenderableImage(img, xform);
		} finally {
			indent--;
		}
	}

	public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
		if(output) {
			System.out.println(i()+"drawRenderedImage( img, "+xform+" )");
		}
		indent++;
		try {
			g.drawRenderedImage(img, xform);
		} finally {
			indent--;
		}
	}

	public void drawRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		if(output) {
			System.out.println(i()+"drawRoundRect( "+x+", "+y+", "+width+", "+height+", "+arcWidth+", "+arcHeight+" )");
		}
		indent++;
		try {
			g.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
		} finally {
			indent--;
		}
	}

	public void drawString(AttributedCharacterIterator iterator, float x,
			float y) {
		if(output) {
			System.out.println(i()+"drawString( "+s(iterator)+", "+x+", "+y+" )");
		}
		indent++;
		try {
			g.drawString(iterator, x, y);
		} finally {
			indent--;
		}
	}

	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		if(output) {
			System.out.println(i()+"drawString( "+s(iterator)+", "+x+", "+y+" )");
		}
		indent++;
		try {
			g.drawString(iterator, x, y);
		} finally {
			indent--;
		}
	}

	public void drawString(String s, float x, float y) {
		if(output) {
			System.out.println(i()+"drawString( \""+s+"\", "+x+", "+y+" )");
		}
		indent++;
		try {
			g.drawString(s, x, y);
		} finally {
			indent--;
		}
	}

	public void drawString(String str, int x, int y) {
		if(output) {
			System.out.println(i()+"drawString( \""+str+"\", "+x+", "+y+" )");
		}
		indent++;
		try {
			g.drawString(str, x, y);
		} finally {
			indent--;
		}
	}

	public boolean equals(Object obj) {
		if(output) {
			System.out.println(i()+"equals( "+obj+" )");
		}
		indent++;
		try {
			boolean returnValue = g.equals(obj);
			System.out.println(i()+" = "+returnValue);
			return returnValue;
		} finally {
			indent--;
		}
	}

	public void fill(Shape s) {
		if(output) {
			System.out.println(i()+"fill( "+s(s)+" )");
		}
		indent++;
		try {
			g.fill(s);
		} finally {
			indent--;
		}
	}

	public void fill3DRect(int x, int y, int width, int height, boolean raised) {
		if(output) {
			System.out.println(i()+"fill3DRect( "+x+", "+y+", "+width+", "+height+", "+raised+" )");
		}
		indent++;
		try {
			g.fill3DRect(x, y, width, height, raised);
		} finally {
			indent--;
		}
	}

	public void fillArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		if(output) {
			System.out.println(i()+"fillArc( "+x+", "+y+", "+width+", "+height+", "+startAngle+", "+arcAngle+" )");
		}
		indent++;
		try {
			g.fillArc(x, y, width, height, startAngle, arcAngle);
		} finally {
			indent--;
		}
	}

	public void fillOval(int x, int y, int width, int height) {
		if(output) {
			System.out.println(i()+"fillOval( "+x+", "+y+", "+width+", "+height+" )");
		}
		indent++;
		try {
			g.fillOval(x, y, width, height);
		} finally {
			indent--;
		}
	}

	public void fillPolygon(int[] points, int[] points2, int points3) {
		if(output) {
			System.out.println(i()+"fillPolygon( ..., ..., "+points3+" )");
		}
		indent++;
		try {
			g.fillPolygon(points, points2, points3);
		} finally {
			indent--;
		}
	}

	public void fillPolygon(Polygon p) {
		if(output) {
			System.out.println(i()+"fillPolygon( "+s(p)+" )");
		}
		indent++;
		try {
			g.fillPolygon(p);
		} finally {
			indent--;
		}
	}

	public void fillRect(int x, int y, int width, int height) {
		if(output) {
			System.out.println(i()+"fillRect( "+x+", "+y+", "+width+", "+height+" )");
		}
		indent++;
		try {
			g.fillRect(x, y, width, height);
		} finally {
			indent--;
		}
	}

	public void fillRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		if(output) {
			System.out.println(i()+"fillRoundRect( "+x+", "+y+", "+width+", "+height+", "+arcWidth+", "+arcHeight+" )");
		}
		indent++;
		try {
			g.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
		} finally {
			indent--;
		}
	}

	public void finalize() {
		if(output) {
			//System.out.println(i()+"finalize()");
		}
		indent++;
		try {
			g.finalize();
		} finally {
			indent--;
		}
	}

	public Color getBackground() {
		if(output) {
			System.out.println(i()+"getBackground()");
		}
		indent++;
		try {
			Color returnValue = g.getBackground();
			System.out.println(i()+" = "+returnValue);
			return returnValue;
		} finally {
			indent--;
		}
	}

	public Shape getClip() {
		if(output) {
			System.out.println(i()+"getClip()");
		}
		indent++;
		try {
			Shape returnValue = g.getClip();
			System.out.println(i()+" = "+s(returnValue));
			return returnValue;
		} finally {
			indent--;
		}
	}

	public Rectangle getClipBounds() {
		if(output) {
			System.out.println(i()+"getClipBounds()");
		}
		indent++;
		try {
			Rectangle returnValue = g.getClipBounds();
			System.out.println(i()+" = "+returnValue);
			return returnValue;
		} finally {
			indent--;
		}
	}

	public Rectangle getClipBounds(Rectangle r) {
		if(output) {
			System.out.println(i()+"getClipBounds(...)");
		}
		indent++;
		try {
			Rectangle returnValue = g.getClipBounds(r);
			System.out.println(i()+" = "+returnValue);
			return returnValue;
		} finally {
			indent--;
		}
	}

	public Rectangle getClipRect() {
		if(output) {
			System.out.println(i()+"getClipRect()");
		}
		indent++;
		try {
			@SuppressWarnings("deprecation")
			Rectangle returnValue = g.getClipRect();
			System.out.println(i()+" = "+returnValue);
			return returnValue;
		} finally {
			indent--;
		}
	}

	public Color getColor() {
		if(output) {
			System.out.println(i()+"getColor()");
		}
		indent++;
		try {
			Color returnValue = g.getColor();
			System.out.println(i()+" = "+returnValue);
			return returnValue;
		} finally {
			indent--;
		}
	}

	public Composite getComposite() {
		if(output) {
			System.out.println(i()+"getComposite()");
		}
		indent++;
		try {
			Composite returnValue = g.getComposite();
			System.out.println(i()+" = "+returnValue);
			return returnValue;
		} finally {
			indent--;
		}
	}

	public GraphicsConfiguration getDeviceConfiguration() {
		if(output) {
			System.out.println(i()+"getDeviceConfiguration()");
		}
		indent++;
		try {
			GraphicsConfiguration returnValue = g.getDeviceConfiguration();
			System.out.println(i()+" = "+returnValue);
			return returnValue;
		} finally {
			indent--;
		}
	}

	public Font getFont() {
		if(output) {
			System.out.println(i()+"getFont()");
		}
		indent++;
		try {
			Font returnValue = g.getFont();
			System.out.println(i()+" = "+returnValue);
			return returnValue;
		} finally {
			indent--;
		}
	}

	public FontMetrics getFontMetrics() {
		if(output) {
			System.out.println(i()+"getFontMetrics()");
		}
		indent++;
		try {
			FontMetrics returnValue = g.getFontMetrics();
			System.out.println(i()+" = "+returnValue);
			return returnValue;
		} finally {
			indent--;
		}
	}

	public FontMetrics getFontMetrics(Font f) {
		if(output) {
			System.out.println(i()+"getFontMetrics( "+f+" )");
		}
		indent++;
		try {
			FontMetrics returnValue = g.getFontMetrics(f);
			System.out.println(i()+" = "+returnValue);
			return returnValue;
		} finally {
			indent--;
		}
	}

	public FontRenderContext getFontRenderContext() {
		if(output) {
			System.out.println(i()+"getFontRenderContext()");
		}
		indent++;
		try {
			FontRenderContext returnValue = g.getFontRenderContext();
			System.out.println(i()+" = "+returnValue);
			return returnValue;
		} finally {
			indent--;
		}
	}

	public Paint getPaint() {
		if(output) {
			System.out.println(i()+"getPaint()");
		}
		indent++;
		try {
			Paint returnValue = g.getPaint();
			System.out.println(i()+" = "+returnValue);
			return returnValue;
		} finally {
			indent--;
		}
	}

	public Object getRenderingHint(Key hintKey) {
		if(output) {
			System.out.println(i()+"getRenderingHint( "+hintKey+" )");
		}
		indent++;
		try {
			Object returnValue = g.getRenderingHint(hintKey);
			System.out.println(i()+" = "+returnValue);
			return returnValue;
		} finally {
			indent--;
		}
	}

	public RenderingHints getRenderingHints() {
		if(output) {
			System.out.println(i()+"getRenderingHints()");
		}
		indent++;
		try {
			RenderingHints returnValue = g.getRenderingHints();
			System.out.println(i()+" = ...");
			return returnValue;
		} finally {
			indent--;
		}
	}

	public Stroke getStroke() {
		if(output) {
			System.out.println(i()+"getStroke()");
		}
		indent++;
		try {
			Stroke returnValue = g.getStroke();
			System.out.println(i()+" = "+returnValue);
			return returnValue;
		} finally {
			indent--;
		}
	}

	public AffineTransform getTransform() {
		if(output) {
			System.out.println(i()+"getTransform()");
		}
		indent++;
		try {
			AffineTransform returnValue = g.getTransform();
			System.out.println(i()+" = "+returnValue);
			return returnValue;
		} finally {
			indent--;
		}
	}

	public int hashCode() {
		if(output) {
			System.out.println(i()+"hashCode()");
		}
		indent++;
		try {
			int returnValue = g.hashCode();
			System.out.println(i()+" = "+returnValue);
			return returnValue;
		} finally {
			indent--;
		}
	}

	public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
		if(output) {
			System.out.println(i()+"hit( "+rect+", "+s(s)+", "+onStroke+" )");
		}
		indent++;
		try {
			boolean returnValue = g.hit(rect, s, onStroke);
			System.out.println(i()+" = "+returnValue);
			return returnValue;
		} finally {
			indent--;
		}
	}

	public boolean hitClip(int x, int y, int width, int height) {
		if(output) {
			System.out.println(i()+"hitClip( "+x+", "+y+", "+width+", "+height+" )");
		}
		indent++;
		try {
			boolean returnValue = g.hitClip(x, y, width, height);
			System.out.println(i()+" = "+returnValue);
			return returnValue;
		} finally {
			indent--;
		}
	}

	public void rotate(double theta, double x, double y) {
		if(output) {
			System.out.println(i()+"rotate( "+theta+", "+x+", "+y+" )");
		}
		indent++;
		try {
			g.rotate(theta, x, y);
		} finally {
			indent--;
		}
	}

	public void rotate(double theta) {
		if(output) {
			System.out.println(i()+"rotate( "+theta+" )");
		}
		indent++;
		try {
			g.rotate(theta);
		} finally {
			indent--;
		}
	}

	public void scale(double sx, double sy) {
		if(output) {
			System.out.println(i()+"scale( "+sx+", "+sy+" )");
		}
		indent++;
		try {
			g.scale(sx, sy);
		} finally {
			indent--;
		}
	}

	public void setBackground(Color color) {
		if(output) {
			System.out.println(i()+"setBackground( "+color+" )");
		}
		indent++;
		try {
			g.setBackground(color);
		} finally {
			indent--;
		}
	}

	public void setClip(int x, int y, int width, int height) {
		if(output) {
			System.out.println(i()+"setClip( "+x+", "+y+", "+width+", "+height+" )");
		}
		indent++;
		try {
			g.setClip(x, y, width, height);
		} finally {
			indent--;
		}
	}

	public void setClip(Shape clip) {
		if(output) {
			System.out.println(i()+"setClip( "+s(clip)+" )");
		}
		indent++;
		try {
			g.setClip(clip);
		} finally {
			indent--;
		}
	}

	public void setColor(Color c) {
		if(output) {
			System.out.println(i()+"setColor( "+c+" )");
		}
		indent++;
		try {
			g.setColor(c);
		} finally {
			indent--;
		}
	}

	public void setComposite(Composite comp) {
		if(output) {
			System.out.println(i()+"setComposite( "+comp+" )");
		}
		indent++;
		try {
			g.setComposite(comp);
		} finally {
			indent--;
		}
	}

	public void setFont(Font font) {
		if(output) {
			System.out.println(i()+"setFont( "+font+" )");
		}
		indent++;
		try {
			g.setFont(font);
		} finally {
			indent--;
		}
	}

	public void setPaint(Paint paint) {
		if(output) {
			System.out.println(i()+"setPaint( "+paint+" )");
		}
		indent++;
		try {
			g.setPaint(paint);
		} finally {
			indent--;
		}
	}

	public void setPaintMode() {
		if(output) {
			System.out.println(i()+"setPaintMode()");
		}
		indent++;
		try {
			g.setPaintMode();
		} finally {
			indent--;
		}
	}

	public void setRenderingHint(Key hintKey, Object hintValue) {
		if(output) {
			System.out.println(i()+"setRenderingHint( "+hintKey+", "+hintValue+" )");
		}
		indent++;
		try {
			g.setRenderingHint(hintKey, hintValue);
		} finally {
			indent--;
		}
	}

	public void setRenderingHints(Map<?, ?> hints) {
		if(output) {
			System.out.println(i()+"setRenderingHints( ... )");
		}
		indent++;
		try {
			g.setRenderingHints(hints);
		} finally {
			indent--;
		}
	}

	public void setStroke(Stroke s) {
		if(output) {
			System.out.println(i()+"setStroke( "+s+" )");
		}
		indent++;
		try {
			g.setStroke(s);
		} finally {
			indent--;
		}
	}

	public void setTransform(AffineTransform Tx) {
		if(output) {
			System.out.println(i()+"setTransform( "+Tx+" )");
		}
		indent++;
		try {
			g.setTransform(Tx);
		} finally {
			indent--;
		}
	}

	public void setXORMode(Color c1) {
		if(output) {
			System.out.println(i()+"setXORMode( "+c1+" )");
		}
		indent++;
		try {
			g.setXORMode(c1);
		} finally {
			indent--;
		}
	}

	public void shear(double shx, double shy) {
		if(output) {
			System.out.println(i()+"chear( "+shx+", "+shy+" )");
		}
		indent++;
		try {
			g.shear(shx, shy);
		} finally {
			indent--;
		}
	}

	public String toString() {
		if(output) {
			System.out.println(i()+"toString()");
		}
		indent++;
		try {
			return "DebugGraphics2D[ g = "+g.toString()+" ]";
		} finally {
			indent--;
		}
	}

	public void transform(AffineTransform Tx) {
		if(output) {
			System.out.println(i()+"transform( "+Tx+" )");
		}
		indent++;
		try {
			g.transform(Tx);
		} finally {
			indent--;
		}
	}

	public void translate(double tx, double ty) {
		if(output) {
			System.out.println(i()+"translate( "+tx+", "+ty+" )");
		}
		indent++;
		try {
			g.translate(tx, ty);
		} finally {
			indent--;
		}
	}

	public void translate(int x, int y) {
		if(output) {
			System.out.println(i()+"translate( "+x+", "+y+" )");
		}
		indent++;
		try {
			g.translate(x, y);
		} finally {
			indent--;
		}
	}
}