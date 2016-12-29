/*
 * @(#)ColorSet.java
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
package com.pump.plaf;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Random;

import javax.swing.JComponent;

import com.pump.awt.Scribbler;
import com.pump.image.MutableBufferedImage;
import com.pump.image.thumbnail.BasicThumbnail;

/** 
 * <p>This is a 2D image of colors.
 * <p>Here are possible customizations the {@link #PALETTE_CELL_STYLE_PROPERTY} and {@link #PALETTE_STYLE_PROPERTY} allow:
 * <p>
 * <table summary="Sample Color Set Configurations"><tr><td></td><td>Default Cell</td><td>Shadow Cell</td><td>Scribble Cell</td></tr>
 * <tr><td>Default Palette</td><td><img src="https://javagraphics.java.net/resources/ColorPaletteDemo/style_default_default.png" alt="Default Palette Style, Default Cell Style"></td>
 * <td><img src="https://javagraphics.java.net/resources/ColorPaletteDemo/style_default_shadow.png" alt="Default Palette Style, Shadow Cell Style"></td>
 * <td><img src="https://javagraphics.java.net/resources/ColorPaletteDemo/style_default_scribble.png" alt="Default Palette Style, Scribble Cell Style"></td></tr>
 * <tr><td>Gradient Palette</td><td><img src="https://javagraphics.java.net/resources/ColorPaletteDemo/style_gradient_default.png" alt="Gradient Palette Style, Default Cell Style"></td>
 * <td><img src="https://javagraphics.java.net/resources/ColorPaletteDemo/style_gradient_shadow.png" alt="Gradient Palette Style, Shadow Cell Style"></td>
 * <td><img src="https://javagraphics.java.net/resources/ColorPaletteDemo/style_gradient_scribble.png" alt="Gradient Palette Style, Scribble Cell Style"></td></tr>
 * <tr><td>Streaks Palette</td><td><img src="https://javagraphics.java.net/resources/ColorPaletteDemo/style_streaks_default.png" alt="Streak Palette Style, Default Cell Style"></td>
 * <td><img src="https://javagraphics.java.net/resources/ColorPaletteDemo/style_streaks_shadow.png" alt="Streak Palette Style, Shadow Cell Style"></td>
 * <td><img src="https://javagraphics.java.net/resources/ColorPaletteDemo/style_streaks_scribble.png" alt="Streak Palette Style, Scribble Cell Style"></td></tr></table>
 * 
 * TODO: rename to distinguish from <code>com.pump.pixel.image.quantize.ColorSet</code>
 */
public abstract class ColorSet {
	
	/** A possible palette style value.
	 * <p><img src="https://javagraphics.java.net/resources/ColorPaletteDemo/style_streaks_default.png" alt="Streaks">
	 * 
	 * @see #PALETTE_STYLE_PROPERTY
	 */
	public static final String PALETTE_STYLE_STREAKS = "streaks";

	/** A possible palette style value.
	 * <p><img src="https://javagraphics.java.net/resources/ColorPaletteDemo/style_gradient_default.png" alt="Gradient">
	 * 
	 * @see #PALETTE_STYLE_PROPERTY
	 */
	public static final String PALETTE_STYLE_GRADIENT = "gradient";

	/** A possible palette style value.
	 * <p><img src="https://javagraphics.java.net/resources/ColorPaletteDemo/style_default_default.png" alt="Default">
	 * 
	 * @see #PALETTE_STYLE_PROPERTY
	 */
	public static final String PALETTE_STYLE_DEFAULT = "default";

	/** A possible palette cell style value.
	 * <p><img src="https://javagraphics.java.net/resources/ColorPaletteDemo/style_default_shadow.png" alt="Shadow">
	 * 
	 * @see #PALETTE_CELL_STYLE_PROPERTY
	 */
	public static final String PALETTE_CELL_STYLE_SHADOW = "shadow";

	/** A possible palette cell style value.
	 * <p><img src="https://javagraphics.java.net/resources/ColorPaletteDemo/style_default_scribble.png" alt="Scribble">
	 * 
	 * @see #PALETTE_CELL_STYLE_PROPERTY
	 */
	public static final String PALETTE_CELL_STYLE_SCRIBBLE = "scribble";

	/** A possible palette cell style value.
	 * <p><img src="https://javagraphics.java.net/resources/ColorPaletteDemo/style_default_default.png" alt="Default">
	 * 
	 * @see #PALETTE_CELL_STYLE_PROPERTY
	 */
	public static final String PALETTE_CELL_STYLE_DEFAULT = "default";
	
	/** The client property name that maps to one of the PALETTE_STYLE constants.
	 * 
	 * @see #PALETTE_STYLE_STREAKS
	 * @see #PALETTE_STYLE_DEFAULT
	 * @see #PALETTE_STYLE_GRADIENT
	 */
	public static final String PALETTE_STYLE_PROPERTY = "paletteStyle";
	
	/** The client property name that maps to one of the PALETTE_CELL_STYLE constants.
	 * 
	 * @see #PALETTE_CELL_STYLE_DEFAULT
	 * @see #PALETTE_CELL_STYLE_SHADOW
	 * @see #PALETTE_CELL_STYLE_SCRIBBLE
	 */
	public static final String PALETTE_CELL_STYLE_PROPERTY = "paletteCellStyle";
	
	final boolean grid;
	final int columns, rows;
	
	Reference<MutableBufferedImage> imageReference = null;
	
	/** Create a 10x15 color grid.
	 * 
	 * @param grid whether this is rendered with a grid or not.
	 */
	public ColorSet(boolean grid) {
		this(grid,10,15);
	}
	
	public ColorSet(boolean grid,int rows,int columns) {
		this.grid = grid;
		this.columns = columns;
		this.rows = rows;
	}

	/** Render a simple sample of this ColorSet. 
	 * @param size the dimensions of the image to paint
	 * @param applyBorder whether a simple border should be applied to the new image 
	 * @return a sample rendering of this object. */
	public BufferedImage paintSample(Dimension size,boolean applyBorder) {
		if(size==null) size = new Dimension(320, 240);
		BufferedImage bi = getImage(size.width, size.height, null);
		if(applyBorder)
			bi = BasicThumbnail.Aqua.create(bi, null);
		return bi;
	}
	
	public void flush() {
		if(imageReference!=null) {
			BufferedImage image = imageReference.get();
			if(image!=null)
				image.flush();
			imageReference = null;
		}
	}
	
	public BufferedImage getImage(int w, int h, JComponent jc) {
		MutableBufferedImage image = null;
		if(imageReference!=null) {
			image = imageReference.get();
		}
		String style =  jc==null ? null : (String)jc.getClientProperty(PALETTE_STYLE_PROPERTY);
		if(style==null)
			style = PALETTE_STYLE_DEFAULT;

		String cellStyle =  jc==null ? null : (String)jc.getClientProperty(PALETTE_CELL_STYLE_PROPERTY);
		if(cellStyle==null)
			cellStyle = PALETTE_CELL_STYLE_DEFAULT;
		
		if(image!=null && image.getWidth()==w && image.getHeight()==h && 
				style.equals(image.getProperty(PALETTE_STYLE_PROPERTY)) &&
				cellStyle.equals(image.getProperty(PALETTE_CELL_STYLE_PROPERTY)) ) {
			return image;
		}

		MutableBufferedImage bi = new MutableBufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		bi.setProperty(PALETTE_STYLE_PROPERTY, style);
		paint(bi, style, cellStyle);
		
		imageReference = new SoftReference<MutableBufferedImage>(bi);
		
		return bi;
	}
	
	/** A method for debugging.  This identifies all duplicate pixels in an image.
	 * 
	 * @param bi
	 */
	/*private void findDuplicates(BufferedImage bi) {
		int w = bi.getWidth();
		int h = bi.getHeight();
		int[] data = new int[w*h];
		bi.getRaster().getDataElements(0, 0, w, h, data);
		int size = w*h;
		for(int a = 0; a<size; a++) {
			int argb = (int)(0xff000000+Math.random()*0xffffff);
			for(int b = a+1; b<size; b++) {
				if(data[b]==data[a]) {
					data[b] = argb;
					data[a] = argb;
				}
			}
		}
		bi.getRaster().setDataElements(0, 0, w, h, data);
	}*/
	
	/**
	 * 
	 * @param bi
	 * @param style the palette style. This should be one of the PALETTE_STYLE constants,
	 * and when defined by a JComponent: it should be the value of the PALETTE_STYLE_PROPERTY key.
	 * @param cellStyle the palette cell style. This should be one of the PALETTE_CELL_STYLE constants,
	 * and when defined by a JComponent: it should be the value of the PALETTE_CELL_STYLE_PROPERTY key.
	 */
	public void paint(BufferedImage bi,String style,String cellStyle) {
		if(!(bi.getType()==BufferedImage.TYPE_INT_ARGB ||
				bi.getType()==BufferedImage.TYPE_INT_RGB))
			throw new IllegalArgumentException("an image of type RGB or ARGB is required");
		
		if(grid) {
			if(style!=null && style.equals(PALETTE_STYLE_STREAKS)) {
				paintStylizedGrid(bi);
			} else if(style!=null && style.equals(PALETTE_STYLE_GRADIENT)) {
				paintGridWithVerticalGradients(bi);
			} else {
				paintGrid(bi);
			}
			if(cellStyle!=null && cellStyle.equals(PALETTE_CELL_STYLE_SHADOW)) {
				paintGridShadowHighlights(bi);
			} else if(cellStyle!=null && cellStyle.equals(PALETTE_CELL_STYLE_SCRIBBLE)) {
				paintGridScribbleHighlights(bi);
			}
		} else {
			if(style!=null && style.equals(PALETTE_STYLE_STREAKS)) {
				paintStylized(bi);
			} else {
				paint(bi);
			}
		}
	}
	
	protected void paint(BufferedImage bi) {
		WritableRaster raster = bi.getRaster();
		int h = bi.getHeight();
		int w = bi.getWidth();
		
		int[] data = new int[w];
		for(int y = 0; y<h; y++) {
			float yFraction = ((float)y)/((float)h);
			for(int x = 0; x<w; x++) {
				float xFraction = ((float)x)/((float)w);
				int rgb = getRGB(xFraction, yFraction);
				data[x] = rgb;
			}
			raster.setDataElements(0, y, w, 1, data);
		}
	}
	
	protected void paintGridWithVerticalGradients(BufferedImage bi) {
		int w = bi.getWidth();
		int h = bi.getHeight();
		WritableRaster raster = bi.getRaster();
		
		int[] data = new int[h];
		for(int col = 0; col<columns; col++) {
			float xFraction = (col+.5f)/(columns);
			int x0 = (w-1)*col/columns;
			int x1 = (w-1)*(col+1)/columns;
			for(int y = 0; y<h; y++) {
				//get the middle of the row
				float yFraction = (y)/((float)h-1);
				int rgb = getRGB(xFraction, yFraction);
				data[y] = rgb;
			}
			for(int x = x0; x<=x1; x++) {
				raster.setDataElements(x, 0, 1, h, data);
			}
		}
	}
	
	protected void paintGrid(BufferedImage bi) {
		int w = bi.getWidth();
		int h = bi.getHeight();
		WritableRaster raster = bi.getRaster();
		
		int[] data = new int[w];
		 for(int row = 0; row<rows; row++) {
			//get the middle of the row
			float yFraction = (row+.5f)/(rows);
			for(int col = 0; col<columns; col++) {
				float xFraction = (col+.5f)/(columns);
				int x0 = (w-1)*col/columns;
				int x1 = (w-1)*(col+1)/columns;
				int rgb = getRGB(xFraction, yFraction);
				for(int x = x0; x<=x1; x++) {
					data[x] = rgb;
				}
			}
			int y0 = (h-1)*row/rows;
			int y1 = (h-1)*(row+1)/rows;
			for(int y = y0; y<=y1; y++) {
				raster.setDataElements(0, y, w, 1, data);
			}
		}
	}
	
	protected void paintGridShadowHighlights(BufferedImage bi) {
		Graphics2D g = bi.createGraphics();
		Rectangle2D r = new Rectangle2D.Float();
		int w = bi.getWidth();
		int h = bi.getHeight();
		Color dark1 = new Color(0,0,0,(int)(10*getHighlightAlpha()));
		Color dark2 = new Color(0,0,0,(int)(20*getHighlightAlpha()));
		Color light1 = new Color(255,255,255,(int)(10*getHighlightAlpha()));
		Color light2 = new Color(255,255,255,(int)(20*getHighlightAlpha()));
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		for(int row = 0; row<rows; row++) {

			int y0 = (bi.getHeight()-1)*row/rows;
			int y1 = (bi.getHeight())*(row+1)/rows;
			
			r.setFrame(0, y0, bi.getWidth(), y1-y0);
			for (int b = 0; b < 2; b++) {
				g.setColor(dark2);
				g.drawLine(0, y0, w, y0);
				g.setColor(dark1);
				g.drawLine(0, y0+1, w, y0+1);
				g.setColor(light2);
				g.drawLine(0, y1, w, y1);
				g.setColor(light1);
				g.drawLine(0, y1-1, w, y1-1);
			}
		}
		for(int col = 0; col<columns; col++) {

			int x0 = (bi.getWidth()-1)*col/columns;
			int x1 = (bi.getWidth())*(col+1)/columns;
			
			r.setFrame(x0, 0, x1-x0, bi.getHeight());
			for (int b = 0; b < 2; b++) {
				g.setColor(dark2);
				g.drawLine(x0, 0, x0, h);
				g.setColor(dark1);
				g.drawLine(x0+1, 0, x0+1, h);
				g.setColor(light2);
				g.drawLine(x1-2, 0, x1-2, h);
				g.setColor(light1);
				g.drawLine(x1-3, 0, x1-3, h);
			}
		}
		g.dispose();
	}
	
	
	protected void paintGridScribbleHighlights(BufferedImage bi) {
		Graphics2D g = bi.createGraphics();
		Rectangle2D r = new Rectangle2D.Float();
		g.setColor(new Color(255, 255, 255, (int)(20*getHighlightAlpha())));
		for(int row = 0; row<rows; row++) {

			int y0 = (bi.getHeight()-1)*row/rows;
			int y1 = (bi.getHeight())*(row+1)/rows;
			
			r.setFrame(0, y0, bi.getWidth(), y1-y0);
			for (int b = 0; b < 2; b++) {
				GeneralPath p = Scribbler.create(r, 1, 1, b * 50);
				g.draw(p);
			}
		}
		for(int col = 0; col<columns; col++) {

			int x0 = (bi.getWidth()-1)*col/columns-1;
			int x1 = (bi.getWidth())*(col+1)/columns-1;
			
			r.setFrame(x0, 0, x1-x0, bi.getHeight());
			for (int b = 0; b < 2; b++) {
				GeneralPath p = Scribbler.create(r, 1, 1, b * 500);
				g.draw(p);
			}
		}
		g.dispose();
	}
	
	protected void paintStylizedGrid(BufferedImage bi) {
		int[] data = null;
		int h = bi.getHeight();
		int w = bi.getWidth();
		int[] data1 = new int[h];
		int[] data2 = new int[h];
		int[] lastData;
		int streakMax = h/rows*3/3;
		int streakMin = h/rows*1/3;
		Random random = new Random();
		float xVariance = 1f/(columns)/4;
		WritableRaster raster = bi.getRaster();
		
		for(int col = 0; col<columns; col++) {
			float xFraction = (col+.5f)/(columns);
			int x0 = (w-1)*col/columns;
			int x1 = (w-1)*(col+1)/columns;
			lastData = null;
			
			for(int x = x0; x<=x1; x++) {
				data = (x%2==0) ? data1 : data2;
				
				if(x==x0) {
					for(int y = 0; y<h; y++) {
						float yFraction = (y)/((float)h-1);
						int rgb = getRGB(xFraction, yFraction);
						data[y] = rgb;
					}
				} else {
					random.setSeed(1000*x);
					int y = 0;
					//don't enforce the min the first time:
					int streakHeight = random.nextInt(streakMin);
					while(y<h) {
						float yFraction = (y)/((float)h-1);
						float xFraction2 = Math.max(0, xFraction+(random.nextFloat()*xVariance));
						int rgb = getRGB(xFraction2, yFraction);
						int max = Math.min(y+streakHeight,h);
						if(y!=0) {
							//smooth a little
							data[y] = tween(rgb,data[y-1],.85f);
						} else {
							data[y] = rgb;
						}
						for(int i = y+1; i<max; i++) {
							data[i] = rgb;
						}
						y += streakHeight;
						streakHeight = Math.max(random.nextInt(streakMax), streakMin);
					}
					if(lastData!=null) {
						for(y = 0; y<h; y++) {
							data[y] = tween(data[y], lastData[y], .75f);
						}
					}
				}

				lastData = data;
				raster.setDataElements(x, 0, 1, h, data);
			}
		}
	}

	
	protected void paintStylized(BufferedImage bi) {
		int[] data = null;
		int h = bi.getHeight();
		int w = bi.getWidth();
		int[] data1 = new int[h];
		int[] data2 = new int[h];
		int[] lastData = null;
		int streakMax = h/rows*3/3;
		int streakMin = h/rows*1/3;
		Random random = new Random();
		WritableRaster raster = bi.getRaster();
		
		for(int x = 0; x<w; x++) {
			float xFraction = (x)/(w-1f);
			
			data = (x%2==0) ? data1 : data2;
				
			if(x==0) {
				for(int y = 0; y<h; y++) {
					float yFraction = (y)/(h-1f);
					int rgb = getRGB(xFraction, yFraction);
					data[y] = rgb;
				}
			} else {
				random.setSeed(1000*x);
				int y = 0;
				//the first streak should be a special height
				int streakHeight = random.nextInt(streakMin);
				while(y<h) {
					float yFraction = (y)/((float)h-1);
					int rgb = getRGB(xFraction, yFraction);
					int max = Math.min(y+streakHeight,h);
					if(y!=0) {
						//smooth a little
						data[y] = tween(rgb,data[y-1],.85f);
					} else {
						data[y] = rgb;
					}
					for(int i = y+1; i<max; i++) {
						data[i] = rgb;
					}
					y += streakHeight;
					streakHeight = Math.max(random.nextInt(streakMax), streakMin);
				}
				if(lastData!=null) {
					for(y = 0; y<h; y++) {
						data[y] = tween(data[y], lastData[y], .8f);
					}
				}
			}

			lastData = data;
			raster.setDataElements(x, 0, 1, h, data);
		}
	}
	
	private static int tween(int rgb1,int rgb2,float p) {
		int a1 = (rgb1 >> 24) & 0xff;
		int r1 = (rgb1 >> 16) & 0xff;
		int g1 = (rgb1 >> 8) & 0xff;
		int b1 = (rgb1) & 0xff;
		int a2 = (rgb2 >> 24) & 0xff;
		int r2 = (rgb2 >> 16) & 0xff;
		int g2 = (rgb2 >> 8) & 0xff;
		int b2 = (rgb2) & 0xff;
		int a3 = (int)(a1*(1-p)+a2*p);
		int r3 = (int)(r1*(1-p)+r2*p);
		int g3 = (int)(g1*(1-p)+g2*p);
		int b3 = (int)(b1*(1-p)+b2*p);
		return (a3 << 24)+(r3 << 16)+(g3 << 8)+(b3);
	}

	public abstract int getRGB(float xFraction, float yFraction);
	
	public abstract Point2D getRelativePoint(int rgb);
	
	public Point getPoint(int rgb,int width,int height) {
		Point2D p = getRelativePoint(rgb);
		Point p2 = new Point( (int)(p.getX()*width), (int)(p.getY()*height));
		return p2;
	}
	
	protected abstract float getHighlightAlpha();
}
