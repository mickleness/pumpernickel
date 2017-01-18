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
package com.pump.image.thumbnail;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import com.pump.awt.Dimension2D;
import com.pump.blog.Blurb;
import com.pump.math.MathG;

/** This provides a simple thumbnail format offering shadows, borders,
 * and rotation. This object and its members are immutable.
 * <P>A <code>BasicThumbnail</code> consists of a series of z-ordered layers,
 * where each layer is rendered above the previous. Each layer is a
 * {@link Layer} object, and has an associated set of insets. Although most
 * layers support a variable degree of curvature (for rounded corners),
 * the insets reflect the official size of the layer. When scaling an image
 * to fit within a maximum thumbnail size: these insets are combined to 
 * calculate the size the source image has to be scaled to. (For example:
 * if the maximum thumbnail size is 64x64, but each side of this
 * thumbnail has 5 pixels of padding, then the image will be scaled to
 * fit within a 54x54 area.)
 */
@Blurb (
imageName = "BasicThumbnail.png",
title = "Thumbnails: Formatting Thumbnails",
releaseDate = "August 2012",
summary = "This presents a simple model to decorate thumbnails.\n"+
"<p>Each thumbnail is rendered as a series of consecutive layers, including shadows "+
" and borders. The thumbnail is created by passing a source image (as a File, URL, or Image) "+
"and a maximum dimension size for the formatted thumbnail.",
article = "http://javagraphics.blogspot.com/2012/08/thumbnails-formatting-thumbnails.html"
)
public class BasicThumbnail extends Thumbnail {

	/** A thumbnail format that adds no details.
	 */
	public static BasicThumbnail None = new BasicThumbnail(
			new BasicThumbnail.Layer[] {}, 0.0f );
	
	/** A thumbnail format that resembles the icon observed on Mac 10.7.4 in
	 * the Finder for image thumbnails.
	 * <p>Specifically: this consists of a light 2-pixel shadow, 1-pixel a gray frame, then 2 pixels of
	 * white, then another 1-pixel frame. This is not intended to  be a perfect
	 * replica, but it is similar in appearance.
	 */
	public static BasicThumbnail Aqua = new BasicThumbnail(
			new BasicThumbnail.Layer[] {
					new BasicThumbnail.Shadow( new int[] { 8, 8 }, 1, 4 ),
					new BasicThumbnail.Border( 1, new Color(203, 203, 203), 5, 5 ),
					new BasicThumbnail.Border( 2, new Color(255, 255, 255), 0, 0 ),
					new BasicThumbnail.Image( new Color(255, 255, 255), 0 ),
					new BasicThumbnail.Border( 1, new Color(128, 128, 128), 0, 0 )
				}, 0.0f );
	
	/** Create a very simple thumbnail with a subtle shadow.
	 * 
	 * @param thickness the width of the shadow.
	 * @return a thumbnail object with a simple shadow.
	 */
	public static BasicThumbnail getShadow(int thickness) {
		int[] i = new int[thickness];
		for(int a = 0; a<i.length; a++) {
			i[a] = 6;
		}
		return new BasicThumbnail(new Layer[] {
			new Shadow(i, thickness, thickness*2 ),
			new Image()
		}, 0);
	}
	
	/** A layer of this thumbnail.
	 */
	public static interface Layer {
		/** Return the size of this layer.
		 * Note that rounded corners may fall outside of these insets;
		 * that is why it is important that these layers always be painted
		 * in the correct z-order to achieve the right result.
		 */
		public Insets getInsets();
		
		/** Paint this layer.
		 * 
		 * @param g the graphics to paint to.
		 * @param x the left edge of the area to paint.
		 * @param y the top edge of the area to paint.
		 * @param width the width of the area to paint.
		 * @param height the height of the area to paint.
		 */
		public void paint(Graphics2D g,int x,int y,int width,int height);
		
		/** 
		 * @return a <code>String</code> capable of constructing this object.
		 */
		public String getConstructionString();
	}
	
	/** This is a set of concentric rounded rectangles used to paint a shadow.
	 * These are painted as strokes of varying thickness around the same
	 * target rectangle, so each new ring overlaps all previous rings as the
	 * thickness grows.
	 */
	public static class Shadow implements Layer {
		
		/** The opacities of each ring, starting with the innermost ring.
		 * <p>Each ring is solid black with a varying level of opacity.
		 */
		public final int[] ringOpacities;
		
		/** The curvature if the innermost ring. */
		public final int innerCurvature;

		/** The curvature if the outermost ring. */
		public final int outerCurvature;
		
		/** Create a new Shadow object.
		 * 
		 * @param ringOpacities the opacities of each ring, starting with the innermost ring, from [0, 255]
		 * @param innerCurvature the curvature of the innermost ring.
		 * @param outerCurvature the curvature of the outermost ring.
		 */
		public Shadow(int[] ringOpacities,int innerCurvature,int outerCurvature) {
			if(innerCurvature<0) throw new IllegalArgumentException("innerCurvature ("+innerCurvature+") must be zero or greater");
			if(outerCurvature<0) throw new IllegalArgumentException("outerCurvature ("+outerCurvature+") must be zero or greater");
			for(int a = 0; a<ringOpacities.length; a++) {
				if(ringOpacities[a]<0 || ringOpacities[a]>255) {
					throw new IllegalArgumentException("ringOpacities["+a+"] ("+ringOpacities[a]+") must be within [0, 255]");
				}
			}
			this.innerCurvature = innerCurvature;
			this.outerCurvature = outerCurvature;
			this.ringOpacities = new int[ringOpacities.length];
			System.arraycopy(ringOpacities, 0, this.ringOpacities, 0, ringOpacities.length);
		}
		
		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof Shadow))
				return false;
			Shadow s = (Shadow)obj;
			if(innerCurvature!=s.innerCurvature)
				return false;
			if(outerCurvature!=s.outerCurvature)
				return false;
			if(ringOpacities.length!=s.ringOpacities.length)
				return false;
			for(int a = 0; a<ringOpacities.length; a++) {
				if(ringOpacities[a]!=s.ringOpacities[a])
					return false;
			}
			return true;
		}

		@Override
		public int hashCode() {
			return ringOpacities.length*100 + innerCurvature*10 + outerCurvature;
		}

		@Override
		public String toString() {
			StringBuffer k = new StringBuffer("{");
			for(int a = 0; a<ringOpacities.length; a++) {
				if(a!=0) {
					k.append(", ");
				} 
				k.append( ringOpacities[a]+" ");
			}
			k.append("}");
			
			return "Shadow[ ringOpacities="+k+", innerCurvature="+innerCurvature+", outerCurvature="+outerCurvature+"]";
		}

		/** The number of ring opacities is the thickness on
		 * all sides of this layer.
		 */
		public Insets getInsets() {
			int k = ringOpacities.length;
			return new Insets(k, k, k, k);
		}

		public void paint(Graphics2D g, int x, int y, int width, int height) {
			g.setRenderingHints( Thumbnail.qualityHints );
			for(int a = 0; a<ringOpacities.length; a++) {
				int thickness = 2*a + 1;
				g.setStroke(new BasicStroke( thickness ));
				g.setColor(new Color(0,0,0,ringOpacities[a]));
				float c;
				if(ringOpacities.length==1) {
					c = ((float)(innerCurvature+outerCurvature))/2f;
				} else {
					float f = ((float)a)/((float)(ringOpacities.length-1));
					c = ( innerCurvature*(1-f) + outerCurvature*f);
				}
				RoundRectangle2D roundRect = new RoundRectangle2D.Float(x + ringOpacities.length, 
					y + ringOpacities.length, 
					width - 2*ringOpacities.length,
					height - 2*ringOpacities.length, 
					c, c);
				g.draw(roundRect);
			}
		}
		
		public String getConstructionString() {
			StringBuffer arrayString = new StringBuffer();
			arrayString.append("{ ");
			for(int a = 0; a<ringOpacities.length; a++) {
				if(a!=0) {
					arrayString.append(", ");
				}
				arrayString.append( Integer.toString( ringOpacities[a] ) );
			}
			arrayString.append(" }");
			return "new BasicThumbnail.Shadow( new int[] "+arrayString+", "+innerCurvature+", "+outerCurvature + " )";
		}
	}
	
	/** This layer paints the actual scaled source image.
	 * This layer has no insets, but it does have an optional background
	 * color and curvature.
	 */
	public static class Image implements Layer {

		/** An optional color to paint below this image. */
		public final Color color;
		
		/** The curvature to apply to the borders of the scaled image.
		 */
		public final int curvature;
		
		/** Create an Image with a white background and zero curvature. */
		public Image() {
			this(Color.white, 0);
		}
		
		/** Create an Image.
		 * 
		 * @param color the optional background color (may be null)
		 * @param curvature the curvature.
		 */
		public Image(Color color,int curvature) {
			if(curvature<0) throw new IllegalArgumentException("curvature ("+curvature+") must be zero or greater");
			this.color = color;
			this.curvature = curvature;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof Image))
				return false;
			Image i = (Image)obj;
			if(curvature!=i.curvature)
				return false;
			if( (color==null)!=(i.color==null) ) {
				return false;
			} else if(color!=null && (!color.equals(i.color))) {
				return false;
			}
			return true;
		}

		@Override
		public int hashCode() {
			return curvature;
		}

		@Override
		public String toString() {
			return "Image[ color="+color+", curvature="+curvature+" ]";
		}

		public Insets getInsets() {
			return new Insets(0,0,0,0);
		}

		/** If a background color was provided: this paints that background.
		 * This method is not capable of painting the scaled image, though.
		 * 
		 */
		public void paint(Graphics2D g, int x, int y, int width, int height) {
			if(color!=null) {
				g.setColor(color);
				g.setRenderingHints( Thumbnail.qualityHints );
				if(curvature==0) {
					g.fillRect(x, y, width, height);
				} else {
					RoundRectangle2D r = new RoundRectangle2D.Float(x, y, width, height, curvature, curvature);
					g.fill(r);
				}
			}
		}
		
		public String getConstructionString() {
			StringBuffer sb = new StringBuffer();
			sb.append("new BasicThumbnail.Image( ");
			if(color==null) {
				sb.append("null");
			} else {
				sb.append("new Color("+color.getRed()+", "+color.getGreen()+", "+color.getBlue()+")");
			}
			sb.append( ", "+curvature+" )");
			return sb.toString();
		}
	}
	
	/** A is a border of varying thickness and curvature.
	 */
	public static class Border implements Layer {
		/** The thickness (in pixels) of this border. */
		public final int thickness;
		/** The color of this border. */
		public final Color color;
		/** The inner curvature of this border. */
		public final int innerCurvature;
		/** The outer curvature of this border. */
		public final int outerCurvature;
		
		/** Create a new Border.
		 * 
		 * @param thickness the thickness (in pixels) of this border.
		 * @param color the color of this border (may not be null).
		 * @param innerCurvature the inner curvature of this border.
		 * @param outerCurvature the outer curvature of this border.
		 */
		public Border(int thickness,Color color,int innerCurvature,int outerCurvature) {
			if(color==null) throw new NullPointerException("null color provided");
			if(thickness<=0) throw new IllegalArgumentException("the border thickness ("+thickness+") must be greater than zero.");
			if(innerCurvature<0) throw new IllegalArgumentException("the innerCurvature ("+innerCurvature+") must be zero or greater.");
			if(outerCurvature<0) throw new IllegalArgumentException("the outerCurvature ("+outerCurvature+") must be zero or greater.");
			this.thickness = thickness;
			this.color = color;
			this.innerCurvature = innerCurvature;
			this.outerCurvature = outerCurvature;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof Border))
				return false;
			Border b = (Border)obj;
			if(thickness!=b.thickness)
				return false;
			if(innerCurvature!=b.innerCurvature)
				return false;
			if(outerCurvature!=b.outerCurvature)
				return false;
			if(!color.equals(b.color))
				return false;
			return true;
		}

		@Override
		public int hashCode() {
			return thickness*100 + outerCurvature;
		}

		@Override
		public String toString() {
			return "Border[ thickness="+thickness+", color="+color+", innerCurvature="+innerCurvature+", outerCurvature="+outerCurvature+"]";
		}

		public String getConstructionString() {
			return "new BasicThumbnail.Border( "+thickness+", new Color("+color.getRed()+", "+color.getGreen()+", "+color.getBlue()+"), "+innerCurvature+", "+outerCurvature+" )";
		}

		public Insets getInsets() {
			return new Insets(thickness, thickness, thickness, thickness);
		}

		public void paint(Graphics2D g, int x, int y, int width, int height) {
			g.setRenderingHints( Thumbnail.qualityHints );
			Shape outer, inner;
			g.setColor(color);
			if(innerCurvature>0 || outerCurvature>0) {
				outer = new RoundRectangle2D.Float(x, y, width, height, outerCurvature, outerCurvature);
				inner = new RoundRectangle2D.Float(x + thickness, y + thickness, width - thickness*2, height - thickness*2, innerCurvature, innerCurvature);
			} else {
				outer = new Rectangle(x, y, width, height);
				inner = new Rectangle(x + thickness, y + thickness, width - thickness*2, height - thickness*2);
			}
			
			AffineTransform tx = g.getTransform();
			if(tx.getShearX()==0 && tx.getShearY()==0) {
				//this should be faster, but produces pixelated artifacts when rotated
				GeneralPath path = new GeneralPath( GeneralPath.WIND_EVEN_ODD );
				path.append(outer, false);
				path.append(inner, false);
				g.fill(path);
			} else {
				Area area = new Area(outer);
				area.subtract(new Area(inner));
				g.fill(area);
			}
		}
	}
	
	/** The layers of this thumbnail. */
	protected final Layer[] layers;

	/** A cached record of the total insets of all the layers. */
	protected final Insets totalInsets = new Insets(0,0,0,0);
	
	/** The rotation (in radians) of this thumbnail. */
	public final float theta;
	
	/** Create a BasicThumbnail with no rotation.
	 * <p>If there isn't an Image layer in the list of layers
	 * provided: then a plain Image layer (with no curvature and
	 * a white background) is added.
	 */
	public BasicThumbnail(Layer[] e) {
		this(e, 0);
	}
	
	/** Create a BasicThumbnail.
	 * <p>If there isn't an Image layer in the list of layers
	 * provided: then a plain Image layer (with no curvature and
	 * a white background) is added.
	 * @param theta the rotation of this thumbnail.
	 */
	public BasicThumbnail(Layer[] e,float theta) {
		this(e, theta, true);
	}
	
	/** Create a BasicThumbnail.
	 * 
	 * @param theta the rotation of this thumbnail.
	 * @param addImageIfMissing if an Image layer is missing
	 * and this boolean is true: a plain Image layer is added.
	 * This is generally to be true except in certain (rare)
	 * debugging instances.
	 */
	public BasicThumbnail(Layer[] e,float theta,boolean addImageIfMissing) {
		boolean foundImage = false;
		for(int a = 0; a<e.length; a++) {
			if(e[a]==null)
				throw new NullPointerException();
			if(e[a] instanceof Image) {
				foundImage = true;
			}
			Insets i = e[a].getInsets();
			totalInsets.left += i.left;
			totalInsets.top += i.top;
			totalInsets.bottom += i.bottom;
			totalInsets.right += i.right;
		}
		if(foundImage || (!addImageIfMissing)) {
			layers = new Layer[e.length];
			System.arraycopy(e, 0, layers, 0, e.length);
		} else {
			layers = new Layer[e.length+1];
			System.arraycopy(e, 0, layers, 0, e.length);
			layers[layers.length -1] = new Image();
		}
		this.theta = theta;
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof BasicThumbnail))
			return false;
		BasicThumbnail bt = (BasicThumbnail)obj;
		if(theta!=bt.theta)
			return false;
		if(layers.length!=bt.layers.length)
			return false;
		for(int a = 0; a<layers.length; a++) {
			if(!layers[a].equals(bt.layers[a]))
				return false;
		}
		return true;
	}

	@Override
	protected BufferedImage create(ImageSource source, Dimension maxSize) {
		if(source==null) throw new NullPointerException();
		
		BufferedImage scaledSource, destImage;
		if(theta==0) {
			if(maxSize==null) {
				maxSize = new Dimension( source.getSourceWidth() + totalInsets.left + totalInsets.right,
						source.getSourceHeight() + totalInsets.top + totalInsets.bottom );
			}
			
			int maxImageWidth = maxSize.width - totalInsets.left - totalInsets.right;
			int maxImageHeight = maxSize.height - totalInsets.top - totalInsets.bottom;
			Dimension maxThumbnailImageSize = new Dimension(maxImageWidth, maxImageHeight);
			Dimension imageSize = new Dimension(source.getSourceWidth(), source.getSourceHeight());
			Dimension thumbnailImageSize = Dimension2D.scaleProportionally( imageSize, maxThumbnailImageSize );
			if(thumbnailImageSize.width > maxThumbnailImageSize.width || thumbnailImageSize.height > maxThumbnailImageSize.height) {
				thumbnailImageSize.width = imageSize.width;
				thumbnailImageSize.height = imageSize.height;
			}
			thumbnailImageSize.width = Math.max(1, thumbnailImageSize.width);
			thumbnailImageSize.height = Math.max(1, thumbnailImageSize.height);
			scaledSource = source.scale(thumbnailImageSize);
			destImage = new BufferedImage( thumbnailImageSize.width + totalInsets.left + totalInsets.right,
					thumbnailImageSize.height + totalInsets.top + totalInsets.bottom, BufferedImage.TYPE_INT_ARGB);
		} else {
			double maxScale = 1;
			double minScale = 0;
			double scale = maxScale;
			double srcW = source.getSourceWidth();
			double srcH = source.getSourceHeight();
			Point2D p1 = new Point2D.Double();
			Point2D p2 = new Point2D.Double();
			Point2D p3 = new Point2D.Double();
			Point2D p4 = new Point2D.Double();
			AffineTransform tx = new AffineTransform();
			Rectangle2D rect = new Rectangle2D.Double();
			int destWidth = maxSize.width;
			int destHeight = maxSize.height;
			
			while(maxScale - minScale>.00001) {
				int scaledWidth = MathG.ceilInt( srcW * scale );
				int scaledHeight = MathG.ceilInt( srcH * scale );
				
				p1.setLocation( 0, 0);
				p2.setLocation( scaledWidth + totalInsets.left + totalInsets.right, 0);
				p3.setLocation( 0, scaledHeight + totalInsets.top + totalInsets.bottom );
				p4.setLocation( scaledWidth + totalInsets.left + totalInsets.right, 
						scaledHeight + totalInsets.top + totalInsets.bottom );

				tx.setToIdentity();
				tx.rotate(theta);

				tx.transform(p1, p1);
				tx.transform(p2, p2);
				tx.transform(p3, p3);
				tx.transform(p4, p4);
				
				rect.setFrame( p1.getX(), p1.getY(), 0, 0);
				rect.add(p2);
				rect.add(p3);
				rect.add(p4);
				
				destWidth = MathG.ceilInt( rect.getWidth() );
				destHeight = MathG.ceilInt( rect.getHeight() );
				
				boolean passed = destWidth<=maxSize.width && destHeight<=maxSize.height;
				if(passed) {
					minScale = scale;
				} else {
					maxScale = scale;
				}
				scale = (minScale + maxScale)/2;
			}

			Dimension thumbnailImageSize = new Dimension(
				MathG.ceilInt(source.getSourceWidth()*scale),
				MathG.ceilInt(source.getSourceHeight()*scale)
			);
			scaledSource = source.scale(thumbnailImageSize);
			destImage = new BufferedImage( destWidth, destHeight, BufferedImage.TYPE_INT_ARGB);
		}
		
		Graphics2D g = destImage.createGraphics();
		
		g.transform( AffineTransform.getRotateInstance( 
				theta, 
				((double)destImage.getWidth())/2.0, 
				((double)destImage.getHeight())/2.0 ));
		
		int initialX =  (destImage.getWidth() - (scaledSource.getWidth() + totalInsets.left + totalInsets.right))/2;;
		int initialY = (destImage.getHeight() - (scaledSource.getHeight() + totalInsets.top + totalInsets.bottom))/2;
		int x = initialX;
		int y = initialY;
		int width = scaledSource.getWidth() + totalInsets.left + totalInsets.right;
		int height = scaledSource.getHeight() + totalInsets.top + totalInsets.bottom;
		
		for(Layer e : layers) {
			Graphics2D g2 = (Graphics2D)g.create();
			e.paint( g2, x, y, width, height);
			
			Insets i = e.getInsets();
			x += i.left;
			y += i.top;
			width -= i.left + i.right;
			height -= i.top + i.bottom;

			if(e instanceof Image) {
				Image image = (Image)e;
				if(image.curvature!=0) {
					Area erasedArea = new Area(new Rectangle(0,0,scaledSource.getWidth(),scaledSource.getHeight()) );
					erasedArea.subtract( new Area(
							new RoundRectangle2D.Float(0, 0,
									scaledSource.getWidth(), scaledSource.getHeight(), 
									image.curvature, image.curvature) ) );
					Graphics2D g3 = scaledSource.createGraphics();
					g3.setComposite(AlphaComposite.Clear);
					g3.setRenderingHints(Thumbnail.qualityHints);
					g3.fill(erasedArea);
					g3.dispose();
				}
				if(theta==0) {
					g2.drawImage(scaledSource, initialX + totalInsets.left, initialY + totalInsets.top, null);
				} else {
					BufferedImage paddedScaledThumbnail = pad( scaledSource, 2);
					g2.drawImage(paddedScaledThumbnail, initialX + totalInsets.left - 2, initialY + totalInsets.top - 2, null);
				}
				g2.dispose();
			}

			g2.dispose();
		}
		g.dispose();
		
		return destImage;
	}
	
	/** Padding an image with an extra couple of rows/columns of
	 * empty pixels fixes nasty antialiasing artifacts when rendering
	 * the borders.
	 */
	private static BufferedImage pad(BufferedImage in,int padding) {
		BufferedImage out = new BufferedImage( in.getWidth() + 2*padding, in.getHeight() + 2*padding, BufferedImage.TYPE_INT_ARGB );
		Graphics2D g = out.createGraphics();
		g.drawImage(in, 2, 2, null);
		g.dispose();
		return out;
	}

	@Override
	public int hashCode() {
		return layers.length*100 + totalInsets.left;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("BasicThumbnail[ theta="+theta+", elements={");
		for(int a = 0; a<layers.length; a++) {
			if(a!=0) {
				sb.append(", ");
			}
			sb.append( layers[a].toString() );
		}
		sb.append("}");
		return sb.toString();
	}
	
	/** Return the number of layers in this thumbnail. */
	public int getLayerCount() {
		return layers.length;
	}
	
	/** Returns a specific layer. */
	public Layer getElement(int index) {
		return layers[index];
	}

	/** Returns a copy of all the layers in this object. */
	public Layer[] getLayers() {
		Layer[] copy = new Layer[layers.length];
		System.arraycopy(layers, 0, copy, 0, copy.length);
		return copy;
	}
}