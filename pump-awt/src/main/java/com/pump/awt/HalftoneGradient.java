/*
 * @(#)HalftoneGradient.java
 *
 * $Date: 2014-06-06 14:04:49 -0400 (Fri, 06 Jun 2014) $
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
package com.pump.awt;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

import com.pump.math.MathG;

/** A gradient that resembles halftoning.
 * <P>This creates an image as a tile and uses an underlying TexturePaint to
 * do most of the rendering.
 */
public class HalftoneGradient extends TransformedTexturePaint {

	public static final int TYPE_DIAMOND = 1;
	public static final int TYPE_CIRCLE = 2;
	public static final int TYPE_TRIANGLE = 3;
	
	/** The default suggested width for the tiled pattern. */
	public static final int DEFAULT_WIDTH = 16;
	
	protected boolean cycle;
	protected final double x1, y1;
	protected final double x2, y2;
	protected final int color1, color2;

	/** Create a new <code>HalftoneGradient</code> using circles and the default width.
	 * 
	 * @param p1 the first point.
	 * @param c1 the first color.
	 * @param p2 the second point.
	 * @param c2 the second color.
	 */
	public HalftoneGradient(Point2D p1,Color c1,Point2D p2,Color c2) {
		this(p1,c1,p2,c2,DEFAULT_WIDTH,TYPE_CIRCLE,false,0,0);
	}
	
	/** Create a new <code>HalftoneGradient</code>.
	 * 
	 * @param p1 the first point.
	 * @param c1 the first color.
	 * @param p2 the second point.
	 * @param c2 the second color.
	 * @param width the width of the repeating pattern.
	 * @param type the type (TYPE_DIAMOND, TYPE_CIRCLE or TYPE_TRIANGLE)
	 * @param cycle whether this gradient repeats or not.
	 * @param offset a float from [0,1] that can be used to animate this gradient.
	 * @param shear a float (default should be zero) that shears the transform.  In general this should be between [-2,2] for best results.
	 */
	public HalftoneGradient(Point2D p1,Color c1,Point2D p2,Color c2,int width,int type,boolean cycle,float offset,float shear) {
		this( (float)p1.getX(), (float)p1.getY(), c1, (float)p2.getX(), (float)p2.getY(), c2, width, type, cycle,offset,shear);
	}
	

	/** Create a new <code>HalftoneGradient</code>.
	 * 
	 * @param x1 the x-coordinate of the first point.
	 * @param y1 the y-coordinate of the first point.
	 * @param c1 the first color.
	 * @param x2 the x-coordinate of the second point.
	 * @param y2 the y-coordinate of the second point.
	 * @param c2 the second color.
	 * @param width the width of the repeating pattern.
	 * @param type the type (TYPE_DIAMOND, TYPE_CIRCLE or TYPE_TRIANGLE)
	 * @param cycle whether this gradient repeats or not.
	 * @param offset a float from [0,1] that can be used to animate this gradient.
	 * @param shear a float (default should be zero) that shears the transform.  In general this should be between [-2,2] for best results.
	 */
	public HalftoneGradient(float x1,float y1,Color c1,float x2,float y2,Color c2,int width,int type,boolean cycle,float offset,float shear) {
		super(createImage(x1,y1,c1,x2,y2,c2,width,type,cycle,offset), 
				createRectangle(x1,y1,x2,y2,width,cycle), 
				createTransform(x1,y1,x2,y2,shear));
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.cycle = cycle;
		color1 = c1.getRGB();
		color2 = c2.getRGB();
	}
	
	/** Returns a copy of the point P1 that anchors the first color.
	 * 
	 * @return a copy of the point that anchors the first color.
	 */
	public Point2D getPoint1() {
		return new Point2D.Double(x1, y1);
	}
	
	/** Returns a copy of the point P2 which anchors the second color.
	 * 
	 * @return a copy of the point that anchors the second color.
	 */
	public Point2D getPoint2() {
		return new Point2D.Double(x2, y2);
	}
	
	/** Returns the color C1 anchored by the point P1. 
	 * 
	 * @return the first color anchored by the point P1. 
	 */
	public Color getColor1() {
		return new Color(color1,true);
	}
	
	/** Returns the color C2 anchored by the point P2. 
	 * 
	 * @return the second color anchored by the point P2. 
	 */
	public Color getColor2() {
		return new Color(color2,true);
	}

	private static RenderingHints QUALITY_HINTS;
	
	@Override
	public PaintContext createContext(ColorModel cm, Rectangle deviceBounds,
			Rectangle2D userBounds, AffineTransform xform, RenderingHints hints) {
		
		if(QUALITY_HINTS==null) {
			QUALITY_HINTS = new RenderingHints(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			QUALITY_HINTS.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			QUALITY_HINTS.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		}
		
		PaintContext context = super.createContext(cm, deviceBounds, userBounds, xform, QUALITY_HINTS);
		
		if(cycle) {
			return context;
		}
		AffineTransform newCopy = new AffineTransform();
		if(xform!=null)
			newCopy.concatenate(xform);
		
		context = new CoveredContext(context,x1,y1,color1,x2,y2,color2,newCopy);
		return context;
	}
	
	/** A convenience method to define a diamond centered around a fixed point. */
	private static void defineDiamond(GeneralPath path,float centerX,float centerY,float r) {
		path.moveTo(centerX,centerY-r);
		path.lineTo(centerX+r,centerY);
		path.lineTo(centerX,centerY+r);
		path.lineTo(centerX-r,centerY);
		path.closePath();
	}

	/** A convenience method to define a triangle centered around a fixed point. */
	private static void defineTriangle(GeneralPath path,float centerX,float centerY,float r) {
		path.moveTo(centerX,centerY-r);
		path.lineTo(centerX+r,centerY);
		path.lineTo(centerX-r,centerY);
		path.closePath();
	}
	
	/** Creates the image that is tiled. */
	private static BufferedImage createImage(float x1,float y1,Color c1,float x2,float y2,Color c2,int width,int type,boolean cycle,float offset) {
		
		int height = MathG.ceilInt(java.awt.geom.Point2D.distance(x1,y1,x2,y2));
		
		BufferedImage image;
		if(cycle) {
			image = new BufferedImage(width,height*2,BufferedImage.TYPE_INT_ARGB);
		} else {
			image = new BufferedImage(width,height,BufferedImage.TYPE_INT_ARGB);
		}
		Graphics2D g = image.createGraphics();
		g.setColor(c1);
		g.fillRect(0,0,image.getWidth(),image.getHeight());
		int w = image.getWidth();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, 
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		if(offset<0) {
			offset = 1-(-offset)%1f;
		} else if(offset>1) {
			offset = offset%1f;
		}

		g.setColor(c2);
		g.setComposite(AlphaComposite.Src);

		GeneralPath path = new GeneralPath();
		
		paintOneSide(g, path, type, height, offset, w);
		
		if(cycle) {
			AffineTransform transform = new AffineTransform();
			transform.translate(0, 2*height);
			transform.scale(1, -1);
			g.transform(transform);
			paintOneSide(g, path, type, height, 1-offset, w);
		}
		
		g.dispose();
		
		return image;
	}

	/** Paints a halftone effect from [0,0] to [0,height], where the
	 * tiled image is w-pixels wide.
	 * <P>When g is transformed correctly this can be used to transform
	 * backwards as well as forwards.
	 * 
	 * @param g
	 * @param path
	 * @param type
	 * @param height
	 * @param offset
	 * @param w
	 */
	private static void paintOneSide(Graphics2D g,GeneralPath path, int type, int height,float offset, int w) {
		path.reset();
		
		if(type==TYPE_TRIANGLE) {
			for(float y = height+offset*w; y>-w; y-=w) {
				float fraction = 1-y / (height);
				defineTriangle(path,w/2,y,fraction*w);
				g.fill(path);
				path.reset();
			}
			for(float y = height-w/2+offset*w; y>-w; y-=w) {
				float fraction = 1-y / (height);
				defineTriangle(path,w/2+w/2,y,fraction*w);
				defineTriangle(path,w/2-w/2,y,fraction*w);
				g.fill(path);
				path.reset();
			}
		} else if(type==TYPE_DIAMOND) {
			for(float y = height+offset*w; y>-w; y-=w) {
				float fraction = 1-y / (height);
				defineDiamond(path,w/2,y,fraction*w);
				g.fill(path);
				path.reset();
			}
		} else if(type==TYPE_CIRCLE) {
			Ellipse2D ellipse = new Ellipse2D.Float();
			float space = w;
			for(float y = height+w*offset; y>-w/2; y-=space) {
				float fraction = 1 - y/ (height);
				float r = (float)(Math.pow(fraction,.80)*w/2);
				ellipse.setFrame(-r,y-r,2*r,2*r); //right side
				path.append(ellipse,false);
				ellipse.setFrame(w-r,y-r,2*r,2*r); //left side
				path.append(ellipse,false);
				g.fill(path);
				path.reset();
			}
			for(float y = height-space/2+w*offset; y>-w/2; y-=space) {
				float fraction = 1 - y / (height);
				float r = (float)(Math.pow(fraction,.80)*w/2);
				ellipse.setFrame(w/2-r,y-r,2*r,2*r); //middle
				path.append(ellipse,false);
				g.fill(path);
				path.reset();
			}
		} else {
			throw new IllegalArgumentException("type must be TYPE_CIRCLE or TYPE_DIAMOND");
		}
	}

	/** Creates the rectangle this tile occupies. */
	private static Rectangle createRectangle(float x1,float y1,float x2,float y2,int width,boolean cycle) {
		int height = MathG.ceilInt(java.awt.geom.Point2D.distance(x1,y1,x2,y2));
		
		if(cycle) {
			return new Rectangle(0,-height,width,2*height);
		}
		return new Rectangle(0,0,width,height);
	}
	
	/** Creates the transform this tile uses. */
	private static AffineTransform createTransform(float x1,float y1,float x2,float y2,float shear) {
		AffineTransform transform = new AffineTransform();
		double angle = Math.atan2(y2-y1,x2-x1);
		transform.translate(x1, y1);
		transform.rotate(angle+Math.PI/2);
		transform.shear(shear, 0);
		return transform;
	}	
}
