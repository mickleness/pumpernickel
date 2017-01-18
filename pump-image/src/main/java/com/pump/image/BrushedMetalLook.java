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
package com.pump.image;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.imageio.ImageIO;

import com.pump.blog.Blurb;
import com.pump.geom.GeneralPathWriter;
import com.pump.geom.InsetPathWriter;
import com.pump.geom.MeasuredShape;
import com.pump.geom.ShapeBounds;
import com.pump.image.pixel.BufferedImageIterator;
import com.pump.image.pixel.IntRGBConverter;

/**
 * This contains static methods related to a brushed metal look.
 * 
 * @see com.pump.showcase.BrushedMetalDemo
 */
@Blurb (
title = "Brushed Metal",
releaseDate = "April 2013",
summary = "The <a href=\"https://javagraphics.java.net/doc/com/bric/image/BrushedMetalLook.html\">BrushedMetalLook</a> class provides a few static methods to create BufferedImages "
		+ "that resemble brushed metal.",
		imageName = "BrushedMetal.png"
)
public class BrushedMetalLook {
	
	/** Return a 200x200 tiling image with horizontal brushed metal tinted to a particular color.
	 * 
	 * @param color if null then the default gray is returned.
	 * @return a 200x200 image with horizontal brushed metal tinted to a particular color.
	 */
	public static BufferedImage getImage(Color color) {
		try {
			BufferedImage bi = ImageIO.read(BrushedMetalLook.class.getResource("grayMetal.png"));
			if(color==null) return bi;
			BufferedImage bi2 = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_RGB);
			IntRGBConverter iter = new IntRGBConverter( BufferedImageIterator.get(bi) );
			int[] rgb = new int[bi.getWidth()];
			int y = 0;
			int w = bi.getWidth();
			
			int red = color.getRed();
			int green = color.getGreen();
			int blue = color.getBlue();
			int redI = 255-red;
			int greenI = 255-green;
			int blueI = 255-blue;
			
			int newRed, newGreen, newBlue;
			
			while(!iter.isDone()) {
				iter.next(rgb);
				for(int x = 0; x<w; x++) {
					int gray = rgb[x] & 0xff;
					if(gray<128) {
						newRed = red * gray / 128;
						newGreen = green * gray / 128;
						newBlue = blue * gray / 128;
					} else {
						gray = gray - 128;
						newRed = red + redI * gray / 128;
						newGreen = green + greenI * gray / 128;
						newBlue = blue + blueI * gray / 128;
					}
					rgb[x] = 0xff000000+(newRed << 16) + (newGreen << 8) + (newBlue);
				}
				bi2.getRaster().setDataElements(0,y,w,1,rgb);
				y++;
			}
			
			return bi2;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * @param includeShadowsAndHighlights add a bevel around the perimeter of the shape
	 */
	public static BufferedImage paint(Shape shape,float width,Rectangle imageBounds,Color color,boolean includeShadowsAndHighlights) {
		Color[] shades = new Color[100];
		for(int a = 0; a<shades.length; a++) {
			int rgb = a<50 ? 0xff : 0x0;
			int alpha = 255*Math.abs(a-50)/50;
			shades[a] = new Color(rgb, rgb, rgb, alpha/15);
		}
		BasicStroke mainStroke = new BasicStroke(width+2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
		Area body = new Area(mainStroke.createStrokedShape(shape));
		if(imageBounds==null)
			imageBounds = ShapeBounds.getBounds(body).getBounds();
		
		Random random = new Random(0);
		BufferedImage bi = new BufferedImage(imageBounds.width, imageBounds.height, BufferedImage.TYPE_INT_ARGB_PRE);
		Graphics2D g = bi.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		g.translate(-imageBounds.x, -imageBounds.y);
		if(color==null)
			color = new Color(0x888899);
		g.setColor(color);
		g.fill(body);

		g.setStroke(new BasicStroke(.7f));
		GeneralPath insetSegment = new GeneralPath(Path2D.WIND_NON_ZERO);
		MeasuredShape ms = new MeasuredShape(shape);
		for(float a = 0; a<width/2; a+=.5f) {
			for(float theta = (float)(Math.PI/2); theta<2*Math.PI; theta += Math.PI) {

				for(float k = 0; k<ms.getOriginalDistance();) {
					insetSegment.reset();
					float gapDistance = 0+20*random.nextFloat();
					k += gapDistance;
					float streakDistance = 10+20*random.nextFloat();
					int shade = random.nextInt(shades.length);
					
					g.setColor(shades[shade]);
					Shape segment = ms.getShape(k / ms.getOriginalDistance(), streakDistance / ms.getOriginalDistance()); 
					
					GeneralPathWriter writer = new GeneralPathWriter(insetSegment);
					InsetPathWriter insetWriter = new InsetPathWriter(writer,a,theta);
					insetWriter.write(segment);
					g.draw( insetSegment );
					
					k += streakDistance;
				}
			}
		}

		if(includeShadowsAndHighlights) {
			g.setStroke(new BasicStroke(1f));
			Graphics2D g2 = (Graphics2D)g.create();
			for(int a = 0; a<5; a++) {
				g2.translate(0,1);
				g2.setColor(new Color(255,255,255,200-a*40));
				g2.draw(body);
			}
			g2.dispose();
	
			g2 = (Graphics2D)g.create();
			for(int a = 0; a<5; a++) {
				g2.translate(0,-1);
				g2.setColor(new Color(0,0,0,100-a*20));
				g2.draw(body);
			}
			g2.dispose();
		}

		BasicStroke otherStroke = new BasicStroke(width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER);
		Area otherBody = new Area(otherStroke.createStrokedShape(shape));
		Area outside = new Area(imageBounds);
		outside.subtract(otherBody);
		
		//erase the nasty frayed edges the shadows introduced:
		g.setClip(null);
		g.setColor(Color.black);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR, 1f));
		g.fill(outside);
		
		g.dispose();
		
		return bi;
	}
}