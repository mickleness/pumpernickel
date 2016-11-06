/*
 * @(#)OptimizedGraphics2D.java
 *
 * $Date: 2014-03-13 04:15:48 -0400 (Thu, 13 Mar 2014) $
 *
 * Copyright (c) 2011 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Jeremy Wood. For details see accompanying license terms.
 * 
 * This software is probably, but not necessarily, discussed here:
 * https://javagraphics.java.net/
 * 
 * That site should also contain the most recent official version
 * of this software.  (See the SVN repository for more details.)
 */
package com.pump.graphics;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.PaintContext;
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
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.RenderableImage;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.AttributedCharacterIterator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.pump.geom.Clipper;
import com.pump.geom.RectangleReader;
import com.pump.geom.ShapeBounds;
import com.pump.math.MathG;
import com.pump.util.JVM;

/** This filter sits on top of a <code>Graphics2D</code> and helps to optimize
 * specific situations.
 *
 */
public class OptimizedGraphics2D extends Graphics2D {
	
	public enum Option {

		/** This flag uses the {@link com.bric.geom.Clipper} class to
		 * avoid using the <code>Area</code> class (if possible) when
		 * clipping this <code>Graphics2D</code>.
		 * <P>This will come into play when this graphics already has
		 * a clipping and you ask to further clip it.  If one of
		 * those two shapes is a rectangle, then the <code>Clipper</code>
		 * class can improve performance in calculating the new
		 * clip.
		 */
		OPTIMIZE_CLIPPING,
		
		/** This flag makes sure a shape that you've asked to
		 * fill touches the clipped area of this graphics
		 * object.
		 * <P>Surprisingly: a lot of work can occur if you've
		 * asked to fill a shape that lies completely outside
		 * the clipping.
		 * <P>This only approximates the current clipping via
		 * the bounds of the current clipping.  It's still possible
		 * with this flag that the incoming shape may not touch
		 * the clipped area, but it's still an improvement in
		 * several cases.
		 * <P>(This does not affect <code>draw()</code> methods,
		 * because it may not be safe to make assumptions
		 * about the size of the stroke without actually calculating
		 * it: and calculating the stroke may be unnecessarily
		 * expensive.)
		 */
		OPTIMIZE_CLIPPED_SHAPES,
		
		/** This optimizes the <code>getClipBounds()</code> method
		 * a little.
		 * <P>This is perhaps a very trivial optimization,
		 * performance-wise, but it was easy to implement.
		 */
		OPTIMIZE_CLIP_BOUNDS,
		
		/** When a custom <code>Paint</code> is being used, calls to
		 * <code>drawGlyphVector()</code> are very slow.  Slower than
		 * calling <code>fill(glyphVector.getOutline())</code>.
		 * <p>This only applies to Macs using Quartz: everywhere else
		 * this slows down performance.
		 * 
		 */
		OPTIMIZE_GLYPH_VECTORS,
		
		/** Painting an image is slower if a custom <code>Paint</code> is
		 * in use.  Strange, no?  So this flag may change the
		 * current paint to a <code>Color</code> before an image
		 * is drawn.
		 * <p>This only applies to Java 1.4; in other environments
		 * this makes no difference in performance.
		 */
		OPTIMIZE_IMAGE_BACKGROUND,

		/** When <code>.drawChars()</code> is used through a scaling transform
		 * the characters do not scale well.
		 * This flag will convert the characters to glyph/shape
		 * data, and just call <code>.fill()</code>.
		 */
		FIX_TEXT_RENDERING,
		
		/** Using quartz, the clipping offset is off (+.5,+.5).
		 * This fixes that offset.
		 */
		FIX_QUARTZ_CLIPPING_OFFSET,
		
		OPTIMIZE_QUARTZ_CUSTOM_PAINTS,
		
		/** A crash can occur painting rotated text using Java 1.5.
		 * See: http://osdir.com/ml/java-dev/2010-06/msg00415.html
		 * 
		 */
		FIX_GLYPH_VECTORS
	}
	
	/** If this is <code>true</code>, then all optimizations are always applied.
	 * However some optimizations are only improvements under certain settings,
	 * so they aren't applied everywhere.  (For example, the <code>OPTIMIZE_GLYPH_VECTOR</code>
	 * mask only improves performance when Quartz is used on Mac.)
	 * <p>By default this boolean is <code>false</code>, so you should be getting
	 * the best performance possible.  When running the <code>OptimizedGraphics2DTests</code>
	 * class this is set to <code>true</code> so the flags can all be
	 * explored in more detail.
	 */
	public static boolean testingOptimizations = false;
	
	/** The <code>Graphics2D</code>this delegates to. */
	public final Graphics2D g;
	Set<Option> options = new HashSet<Option>();
	protected Paint currentPaint;
	
	/** Create an OptimizedGraphics2D that uses all possible options.
	 * 
	 * @param g
	 */
	public OptimizedGraphics2D(Graphics2D g) {
		this(g,Option.values());
	}
	
	/** Create an OptimizedGraphics2D with a certain set of options.
	 * 
	 * @param g
	 * @param options the options to use. If null: then no options are
	 * active.
	 */
	public OptimizedGraphics2D(Graphics2D g,Option... options) {
		this.g = g;
		if(options==null) options = new Option[] {};
		
		for(Option option : options) {
			this.options.add(option);
		}
		currentPaint = g.getPaint();
	}

	@Override
	public void addRenderingHints(Map<?, ?> hints) {
		g.addRenderingHints(hints);
	}

	@Override
	public void clearRect(int x, int y, int width, int height) {
		g.clearRect(x, y, width, height);
	}

	private int insideClip = 0;
	@Override
	public synchronized void clip(Shape s) {
		if(s==null)
			return; 
		
		double dx = 0;
		double dy = 0;
		if( options.contains(Option.FIX_QUARTZ_CLIPPING_OFFSET) && JVM.usingQuartz) {
			/** This used to be .5 in Aug of 09.
			 * Then in Sept Apple came out with updates, and apparently
			 * this should now be .25.
			 */
			dx = -.25;
			dy = -.25;
			Rectangle2D rect = RectangleReader.convert(s);
			if(rect!=null) {
				s = rect;
			}
		}
		if(dx!=0 || dy!=0) {
			g.translate(dy, dy);
		}
		try {
			innerClipDefined = false;
			if( options.contains(Option.OPTIMIZE_CLIPPING) ) {
				if(insideClip==0) {
					insideClip++;
					try {
						Clipper.clip(this, s);
						return;
					} finally {
						 insideClip--;
					}
				}
			}
		g.clip(s);
		} finally {
			if(dx!=0 || dy!=0) {
				g.translate(-dx, -dy);
			}
		}
	}

	@Override
	public synchronized void clipRect(int x, int y, int width, int height) {
		g.clipRect(x, y, width, height);
		innerClipDefined = false;
	}

	@Override
	public void copyArea(int x, int y, int width, int height, int dx, int dy) {
		g.copyArea(x, y, width, height, dx, dy);
	}

	@Override
	public Graphics create() {
		return new OptimizedGraphics2D( (Graphics2D)g.create(), options.toArray(new Option[options.size()]) );
	}

	@Override
	public void dispose() {
		g.dispose();
	}

	@Override
	public void draw(Shape s) {
		g.draw(s);
	}

	@Override
	public void draw3DRect(int x, int y, int width, int height, boolean raised) {
		g.draw3DRect(x, y, width, height, raised);
	}

	@Override
	public void drawArc(int x, int y, int width, int height, int startAngle,
			int arcAngle) {
		g.drawArc(x, y, width, height, startAngle, arcAngle);
	}

	@Override
	public void drawBytes(byte[] data, int offset, int length, int x, int y) {
		g.drawBytes(data, offset, length, x, y);
	}

	@Override
	public void drawChars(char[] data, int offset, int length, int x, int y) {
		if( options.contains(Option.FIX_TEXT_RENDERING) ) {
			AffineTransform t = getTransform();
			if(t.getScaleX()!=1 || t.getScaleY()!=1) {
				Font font = getFont();
				if(offset!=0 || length!=data.length) {
					char[] c = new char[length];
					System.arraycopy(data,offset,c,0,length);
					data = c;
				}
				GlyphVector gv = font.createGlyphVector(getFontRenderContext(), data);
				Shape shape = gv.getOutline(x, y);
				
				if(getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING).equals(RenderingHints.VALUE_TEXT_ANTIALIAS_ON)) {
					Object oldHint = getRenderingHint(RenderingHints.KEY_ANTIALIASING);
					setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					fill(shape);
					setRenderingHint(RenderingHints.KEY_ANTIALIASING, oldHint);
				} else {
					fill(shape);
				}
				return;
			}
		}
		g.drawChars(data,offset,length,x,y);
	}

	@Override
	public void drawGlyphVector(GlyphVector gv, float x, float y) {
		if(( options.contains(Option.OPTIMIZE_GLYPH_VECTORS) && JVM.usingQuartz) || testingOptimizations) {
			if((currentPaint instanceof Color)==false) {
				fill(gv.getOutline(x,y));
				return;
			}
		}
		if((options.contains(Option.FIX_GLYPH_VECTORS) && (JVM.usingQuartz==false)) || testingOptimizations) {
			if(innerTransform==null) innerTransform = getTransform();
			
			if( JVM.getMajorJavaVersion()==1.5f && (innerTransform.getShearX())<-.01) {
				fill(gv.getOutline(x,y));
				return;
			}
		}
		g.drawGlyphVector(gv, x, y);
	}

	private static Color EMPTY_COLOR = new Color(0,0,0,0);
	
	@Override
	public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
		if( ( options.contains(Option.OPTIMIZE_IMAGE_BACKGROUND) && (JVM.javaVersion>0 && JVM.javaVersion<=1.4f)) || testingOptimizations) {
			g.setPaint(EMPTY_COLOR);
			g.drawImage(img, op, x, y);
			g.setPaint(currentPaint);
			return;
		}
		g.drawImage(img, op, x, y);
	}

	@Override
	public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
		if( ( options.contains(Option.OPTIMIZE_IMAGE_BACKGROUND) && (JVM.javaVersion>0 && JVM.javaVersion<=1.4f)) || testingOptimizations) {
			try {
				g.setPaint(EMPTY_COLOR);
				return g.drawImage(img, xform, obs);
			} finally {
				g.setPaint(currentPaint);
			}
		}
		return g.drawImage(img, xform, obs);
	}

	@Override
	public boolean drawImage(Image img, int x, int y, Color bgcolor,
			ImageObserver observer) {
		if( ( options.contains(Option.OPTIMIZE_IMAGE_BACKGROUND) && (JVM.javaVersion>0 && JVM.javaVersion<=1.4f)) || testingOptimizations) {
			try {
				g.setPaint(EMPTY_COLOR);
				return g.drawImage(img, x, y, bgcolor, observer);
			} finally {
				g.setPaint(currentPaint);
			}
		}
		return g.drawImage(img, x, y, bgcolor, observer);
	}

	@Override
	public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
		if( ( options.contains(Option.OPTIMIZE_IMAGE_BACKGROUND) && (JVM.javaVersion>0 && JVM.javaVersion<=1.4f)) || testingOptimizations) {
			try {
				g.setPaint(EMPTY_COLOR);
				return g.drawImage(img, x, y, observer);
			} finally {
				g.setPaint(currentPaint);
			}
		}
		return g.drawImage(img, x, y, observer);
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height,
			Color bgcolor, ImageObserver observer) {
		if( ( options.contains(Option.OPTIMIZE_IMAGE_BACKGROUND) && (JVM.javaVersion>0 && JVM.javaVersion<=1.4f)) || testingOptimizations) {
			try {
				g.setPaint(EMPTY_COLOR);
				return g.drawImage(img, x, y, width, height, bgcolor, observer);
			} finally {
				g.setPaint(currentPaint);
			}
		}
		return g.drawImage(img, x, y, width, height, bgcolor, observer);
	}

	@Override
	public boolean drawImage(Image img, int x, int y, int width, int height,
			ImageObserver observer) {
		if( ( options.contains(Option.OPTIMIZE_IMAGE_BACKGROUND) && (JVM.javaVersion>0 && JVM.javaVersion<=1.4f)) || testingOptimizations) {
			try {
				g.setPaint(EMPTY_COLOR);
				return g.drawImage(img, x, y, width, height, observer);
			} finally {
				g.setPaint(currentPaint);
			}
		}
		return g.drawImage(img, x, y, width, height, observer);
	}

	@Override
	public synchronized boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
			int sx1, int sy1, int sx2, int sy2, Color bgcolor,
			ImageObserver observer) {
		if( ( options.contains(Option.OPTIMIZE_IMAGE_BACKGROUND) && (JVM.javaVersion>0 && JVM.javaVersion<=1.4f)) || testingOptimizations) {
			try {
				g.setPaint(EMPTY_COLOR);
				return g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2,
						bgcolor, observer);
			} finally {
				g.setPaint(currentPaint);
			}
		}
		return g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2,
				bgcolor, observer);
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
			int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
		if( ( options.contains(Option.OPTIMIZE_IMAGE_BACKGROUND) && (JVM.javaVersion>0 && JVM.javaVersion<=1.4f)) || testingOptimizations) {
			try {
				g.setPaint(EMPTY_COLOR);
				return g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2,
					observer);
			} finally {
				g.setPaint(currentPaint);
			}
		}
		return g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2,
				observer);
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2) {
		g.drawLine(x1, y1, x2, y2);
	}

	@Override
	public void drawOval(int x, int y, int width, int height) {
		g.drawOval(x, y, width, height);
	}

	@Override
	public void drawPolygon(int[] points, int[] points2, int points3) {
		g.drawPolygon(points, points2, points3);
	}

	@Override
	public void drawPolygon(Polygon p) {
		g.drawPolygon(p);
	}

	@Override
	public void drawPolyline(int[] points, int[] points2, int points3) {
		g.drawPolyline(points, points2, points3);
	}

	@Override
	public void drawRect(int x, int y, int width, int height) {
		g.drawRect(x, y, width, height);
	}

	@Override
	public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
		g.drawRenderableImage(img, xform);
	}

	@Override
	public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
		g.drawRenderedImage(img, xform);
	}

	@Override
	public void drawRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		g.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, float x,
			float y) {
		g.drawString(iterator, x, y);
	}

	@Override
	public void drawString(AttributedCharacterIterator iterator, int x, int y) {
		g.drawString(iterator, x, y);
	}

	@Override
	public void drawString(String s, float x, float y) {
		g.drawString(s, x, y);
	}

	@Override
	public void drawString(String str, int x, int y) {
		g.drawString(str, x, y);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj==this)
			return true;
		
		if(obj instanceof OptimizedGraphics2D) {
			OptimizedGraphics2D g2 = (OptimizedGraphics2D)obj;
			if(g2.g.equals(g))
				return true;
		} else if(obj instanceof Graphics2D) {
			Graphics2D g2 = (Graphics2D)obj;
			if(g.equals(g2))
				return true;
		}

		return false;
	}

	private Rectangle2D.Float innerClip = new Rectangle2D.Float();
	private boolean innerClipDefined = false;
	private boolean innerClipExists = false;
	private AffineTransform innerTransform;
	
	private Rectangle2D.Float getInnerClipRect() {
		if(innerClipDefined==false) {
			Shape clip = getClip();
			if(clip==null) {
				innerClipExists = false;
			} else {
				if(innerClip.width==0 || innerClip.height==0) {
					innerClipExists = false;
				} else {
					innerClipExists = true;
					ShapeBounds.getBounds(clip,innerClip);
					innerClipDefined = true;
				}
			}
		}
		if(innerClipExists==false)
			return null;
		return innerClip;
	}
	
	@Override
	public synchronized void fill(Shape s) {
		if(options.contains(Option.OPTIMIZE_CLIPPED_SHAPES)) {
			Rectangle2D clipRect = getInnerClipRect();
			if(clipRect!=null && s.intersects(clipRect)==false) {
				return;
			}
		}
		if(( options.contains(Option.OPTIMIZE_QUARTZ_CUSTOM_PAINTS) && JVM.usingQuartz) || testingOptimizations) {
			if(!(currentPaint instanceof Color)) {
				fillPaintForQuartz(s);
				return;
			}
		}
		g.fill(s);
	}

	private static Rectangle2D rect2D;
	private static BufferedImage scratchImage = null;
	private static AffineTransform identityTransform = new AffineTransform();
	
	/** This is already called from within a synchronized block.
	 * 
	 * @param s
	 */
	private void fillPaintForQuartz(Shape s) {
		if(rect2D==null)
			rect2D = new Rectangle2D.Float();
		
		//TODO: uncomment this, and decide whether this method
		//is appropriate to use
		/*Rectangle clipBounds = getClipBounds();
		if(clipBounds!=null) {
			System.out.println("reducing");
			s = Clipper.clipToRect(s, null, clipBounds);
		}*/
		AffineTransform myTransform = getTransform();
		
		ShapeBounds.getBounds(s, myTransform, rect2D);
		Rectangle rect = rect2D.getBounds();
		
		if(rect.width==0 || rect.height==0)
			return;
		
		RenderingHints oldHints = getRenderingHints();

		PaintContext context = currentPaint.createContext(ColorModel.getRGBdefault(), 
				rect, rect, getTransform(), oldHints);
		
		/** We MUST set the paint to black.
		 * Otherwise this lets loose an insane memory leak
		 * under Quartz.
		 */
		Paint oldPaint = currentPaint;
		setPaint(Color.black);
		setTransform(identityTransform);
		
		try {
			BufferedImage scratchImage = getScratchImage(rect.width, rect.height);
			WritableRaster paintRaster = (WritableRaster)context.getRaster(rect.x, rect.y, rect.width, rect.height);
			BufferedImage newImage = new BufferedImage(context.getColorModel(), paintRaster, false, null);
			Graphics2D g = scratchImage.createGraphics();
			g.setComposite(AlphaComposite.Clear);
			g.fillRect(0, 0, rect.width, rect.height);
			g.translate(-rect.x, -rect.y);
			g.transform( myTransform );
			g.setColor(Color.black);
			g.setComposite(AlphaComposite.SrcOver);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
					RenderingHints.VALUE_ANTIALIAS_ON);
			g.fill(s);
			g.setTransform(identityTransform);
			g.setComposite(AlphaComposite.SrcIn);
			g.drawImage(newImage, 0, 0, null);
			g.dispose();
			drawImage(scratchImage, 
					rect.x, rect.y, rect.x+rect.width, rect.y+rect.height, 
					0, 0, rect.width, rect.height, 
					null, null);
			newImage.flush();
		} finally {
			context.dispose();
			setTransform(myTransform);
			setPaint(oldPaint);
			setRenderingHints(oldHints);
			if(scratchImage!=null)
				scratchImage.flush();
		}
	}
	
	private static BufferedImage getScratchImage(int w,int h) {
		if(scratchImage==null || w>scratchImage.getWidth() || h>scratchImage.getHeight()) {
			int width = scratchImage==null ? w : Math.max(w, (scratchImage.getWidth()));
			int height = scratchImage==null ? h : Math.max(h, (scratchImage.getHeight()));
			scratchImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		}
		return scratchImage;
	}
	
	private Arc2D scratchArc2D = null;
	@Override
	public synchronized void fillArc(int x, int y, int width, int height, int startAngle,
			int endAngle) {
		if( options.contains(Option.OPTIMIZE_CLIPPED_SHAPES) ) {
			if(scratchArc2D==null)
				scratchArc2D = new Arc2D.Float();
			scratchArc2D.setArc(x, y, width, height, startAngle, endAngle, Arc2D.OPEN);
			fill(scratchArc2D);
			return;
		}
		g.fillArc(x, y, width, height, startAngle, endAngle);
	}

	private Ellipse2D scratchEllipse = null;
	@Override
	public synchronized void fillOval(int x, int y, int width, int height) {
		if( options.contains(Option.OPTIMIZE_CLIPPED_SHAPES) ) {
			if(scratchEllipse==null)
				scratchEllipse = new Ellipse2D.Float();
			scratchEllipse.setFrame(x, y, width, height);
			fill(scratchEllipse);
			return;
		}
		g.fillOval(x, y, width, height);
	}

	@Override
	public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
		if( options.contains(Option.OPTIMIZE_CLIPPED_SHAPES) ) {
			Polygon p = new Polygon(xPoints,yPoints,nPoints);
			fill(p);
			return;
		}
		g.fillPolygon(xPoints, yPoints, nPoints);
	}

	@Override
	public void fillPolygon(Polygon p) {
		if( options.contains(Option.OPTIMIZE_CLIPPED_SHAPES) ) {
			fill(p);
			return;
		}
		g.fillPolygon(p);
	}

	private Rectangle scratchRectangle = null;
	@Override
	public synchronized void fillRect(int x, int y, int width, int height) {
		if( options.contains(Option.OPTIMIZE_CLIPPED_SHAPES) ) {
			if(scratchRectangle==null)
				scratchRectangle = new Rectangle();
			scratchRectangle.setBounds(x, y, width, height);
			fill(scratchRectangle);
			return;
		}
		g.fillRect(x, y, width, height);
	}

	private RoundRectangle2D scratchRoundRectangle2D = null;
	@Override
	public synchronized void fillRoundRect(int x, int y, int width, int height,
			int arcWidth, int arcHeight) {
		if( options.contains(Option.OPTIMIZE_CLIPPED_SHAPES) ) {
			if(scratchRoundRectangle2D==null)
				scratchRoundRectangle2D = new RoundRectangle2D.Float();
			scratchRoundRectangle2D.setRoundRect(x, y, width, height, arcWidth, arcHeight);
			fill(scratchRoundRectangle2D);
			return;
		}
		g.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
	}

	@Override
	public void finalize() {
		g.finalize();
	}

	@Override
	public Color getBackground() {
		return g.getBackground();
	}

	@Override
	public Shape getClip() {
		return g.getClip();
	}

	@Override
	public Rectangle getClipBounds() {
		return getClipBounds(new Rectangle());
	}

	@Override
	public Rectangle getClipBounds(Rectangle r) {
		if( options.contains(Option.OPTIMIZE_CLIP_BOUNDS) ) {
			Rectangle2D.Float clipRect = getInnerClipRect();
			if(clipRect!=null) {
				if(r==null) r = new Rectangle();
				
				float width = clipRect.width;
				float height = clipRect.height;
				if (width < 0 || height < 0) {
					r.x = 0;
					r.y = 0;
					r.width = 0;
					r.height = 0;
				    return r;
				}
				double x = clipRect.x;
				double y = clipRect.y;
				int x1 = MathG.floorInt(x);
				int y1 = MathG.floorInt(y);
				int x2 = MathG.ceilInt(x + width);
				int y2 = MathG.ceilInt(y + height);
				r.x = x1;
				r.y = y1;
				r.width = x2-x1;
				r.height = y2-y1;
				return r;
			}
		}
		
		
		return g.getClipBounds(r);
	}

	@SuppressWarnings("deprecation")
	@Override
	public Rectangle getClipRect() {
		return g.getClipRect();
	}

	@Override
	public Color getColor() {
		return g.getColor();
	}

	@Override
	public Composite getComposite() {
		return g.getComposite();
	}

	@Override
	public GraphicsConfiguration getDeviceConfiguration() {
		return g.getDeviceConfiguration();
	}

	@Override
	public Font getFont() {
		return g.getFont();
	}

	@Override
	public FontMetrics getFontMetrics() {
		return g.getFontMetrics();
	}

	@Override
	public FontMetrics getFontMetrics(Font f) {
		return g.getFontMetrics(f);
	}

	@Override
	public FontRenderContext getFontRenderContext() {
		return g.getFontRenderContext();
	}

	@Override
	public Paint getPaint() {
		return g.getPaint();
	}

	@Override
	public Object getRenderingHint(Key hintKey) {
		return g.getRenderingHint(hintKey);
	}

	@Override
	public RenderingHints getRenderingHints() {
		return g.getRenderingHints();
	}

	@Override
	public Stroke getStroke() {
		return g.getStroke();
	}

	@Override
	public AffineTransform getTransform() {
		return g.getTransform();
	}

	@Override
	public int hashCode() {
		return g.hashCode();
	}

	@Override
	public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
		return g.hit(rect, s, onStroke);
	}

	@Override
	public boolean hitClip(int x, int y, int width, int height) {
		return g.hitClip(x, y, width, height);
	}

	@Override
	public synchronized void rotate(double theta, double x, double y) {
		g.rotate(theta, x, y);
		if(innerTransform!=null)
			innerTransform.rotate(theta, x, y);
		innerClipDefined = false;
	}

	@Override
	public synchronized void rotate(double theta) {
		g.rotate(theta);
		if(innerTransform!=null)
			innerTransform.rotate(theta);
		innerClipDefined = false;
	}

	@Override
	public synchronized void scale(double sx, double sy) {
		g.scale(sx, sy);
		if(innerTransform!=null)
			innerTransform.scale(sx, sy);
		innerClipDefined = false;
	}

	@Override
	public void setBackground(Color color) {
		g.setBackground(color);
	}

	@Override
	public synchronized void setClip(int x, int y, int width, int height) {
		g.setClip(x, y, width, height);
		innerClipDefined = false;
	}

	@Override
	public synchronized void setClip(Shape clip) {
		g.setClip(clip);
		innerClipDefined = false;
	}

	@Override
	public void setColor(Color c) {
		g.setColor(c);
		currentPaint = c;
	}

	@Override
	public void setComposite(Composite comp) {
		g.setComposite(comp);
	}

	@Override
	public void setFont(Font font) {
		g.setFont(font);
	}

	@Override
	public void setPaint(Paint paint) {
		g.setPaint(paint);
		currentPaint = paint;
	}

	@Override
	public void setPaintMode() {
		g.setPaintMode();
	}

	@Override
	public void setRenderingHint(Key hintKey, Object hintValue) {
		g.setRenderingHint(hintKey, hintValue);
	}

	@Override
	public void setRenderingHints(Map<?, ?> hints) {
		g.setRenderingHints(hints);
	}

	@Override
	public void setStroke(Stroke s) {
		g.setStroke(s);
	}

	@Override
	public synchronized void setTransform(AffineTransform Tx) {
		g.setTransform(Tx);
		innerTransform = null;
		innerClipDefined = false;
	}

	@Override
	public void setXORMode(Color c1) {
		g.setXORMode(c1);
	}

	@Override
	public synchronized void shear(double shx, double shy) {
		g.shear(shx, shy);
		if(innerTransform!=null)
			innerTransform.shear(shx, shy);
		innerClipDefined = false;
	}

	@Override
	public String toString() {
		return "OptimizedGraphics2D[ g = "+g.toString()+", options = "+options+"]";
	}

	@Override
	public synchronized void transform(AffineTransform Tx) {
		g.transform(Tx);
		innerTransform = null;
		innerClipDefined = false;
	}

	@Override
	public synchronized void translate(double tx, double ty) {
		g.translate(tx, ty);
		if(innerTransform!=null)
			innerTransform.translate(tx, ty);
		innerClipDefined = false;
	}

	@Override
	public synchronized void translate(int x, int y) {
		g.translate(x, y);
		if(innerTransform!=null)
			innerTransform.translate(x, y);
		innerClipDefined = false;
	}
	
	/** Generates a text description of the argument.
	 * This uses reflection to see which masking fields from this
	 * class are used to make up the argument "mask".
	 * May return "all" or "none" if appropriate.
	 * @param mask a combination of the public static fields
	 * in this class.
	 * @return a text description of the argument.
	 */
	private static String toString(long mask) {
		Field[] f = OptimizedGraphics2D.class.getFields();
		StringBuffer sb = new StringBuffer();
		int ctr = 0;
		int max = 0;
		for(int a = 0; a<f.length; a++) {
			if(f[a].getType().equals(Long.TYPE) && 
					(f[a].getModifiers() & Modifier.STATIC)>0 &&
					(f[a].getModifiers() & Modifier.PUBLIC)>0) {
				try {
					long value = f[a].getLong(null);
					max++;
					if((mask & value) > 0) {
						if(sb.length()>0)
							sb.append(", ");
						sb.append(f[a].getName().toLowerCase());
						ctr++;
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
		if(ctr==max)
			return "all";
		if(sb.length()==0)
			return "none";
		return sb.toString();
	}
}
