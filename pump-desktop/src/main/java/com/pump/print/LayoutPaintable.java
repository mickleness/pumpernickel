/*
 * @(#)LayoutPaintable.java
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
package com.pump.print;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import com.pump.awt.Paintable;

/** This arranges a series of smaller paintables into a series of
 * cells.  If a cell bound has a negative width or height, that
 * means that cell will be flipped horizontally/vertically.
 * 
 */
public class LayoutPaintable implements Paintable {
	final int width, height;
	final Rectangle2D[] rects;
	final Paintable[] cells;
	final boolean scaleProportionally;
	
	/** Creates a LayoutPaintable.
	 * 
	 * @param width the width of this entire Paintable.  This must be
	 * greater than the rightmost edge of all inner cells.
	 * @param height the height of this entire Paintable.  This must be
	 * greater than the bottom-most edge of all inner cells.
	 * @param rects an array of cell bounds.  Negative widths and heights are
	 * used to indicate a cell should be flipped horizontally/vertically.
	 * @param cells an array of cells, each element corresponding to the
	 * previous array of destination rectangles.
	 * @param scaleProportionally whether each paintable should be scaled
	 * to full fit the cell bounds, or whether it should be scaled
	 * proportionally.  It is recommended this value be true, although
	 * this may result in "dead space" in each cell.
	 */
	LayoutPaintable(int width,int height,Rectangle2D[] rects,Paintable[] cells,boolean scaleProportionally) {
		if(rects.length!=cells.length)
			throw new IllegalArgumentException("the number of rects ("+rects.length+") must equal the number of cells ("+cells.length+")");
		
		this.width = width;
		this.height = height;
		this.rects = new Rectangle2D[rects.length];
		this.cells = new Paintable[rects.length];
		this.scaleProportionally = scaleProportionally;
		System.arraycopy(rects,0,this.rects,0,rects.length);
		System.arraycopy(cells,0,this.cells,0,rects.length);
		
		Rectangle2D sum = null;
		for(int a = 0; a<rects.length; a++) {
			if(sum==null) {
				sum = new Rectangle2D.Double(rects[a].getX(), rects[a].getY(), rects[a].getWidth(), rects[a].getHeight());
			} else {
				sum = sum.createUnion(rects[a]);
			}
		}
		if(sum.getX()+sum.getWidth()>width || sum.getY()+sum.getHeight()>height) {
			for(int a = 0; a<rects.length; a++) {
				System.err.println("rects["+a+"] = "+rects[a]);
			}
			throw new IllegalArgumentException("the width and height specified ("+width+"x"+height+") do not enclose the sum of all the cell bounds ( "+sum+" )");
		}
	}
	
	public Paintable[] getPaintables() {
		Paintable[] copy = new Paintable[cells.length];
		System.arraycopy(cells, 0, copy, 0, copy.length);
		return copy;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}

	public void paint(Graphics2D g) {
		for(int a = 0; a<rects.length; a++) {
			//don't trust the other cells to restore this graphics correctly
			Graphics2D workingGraphics = (Graphics2D)g.create();
			
			double cellWidth = cells[a] == null ? 0 : cells[a].getWidth();
			double cellHeight = cells[a] == null ? 0 : cells[a].getHeight();
			
			Rectangle2D clip = rects[a];
			if(clip.getWidth()<0) {
				clip = new Rectangle2D.Double(clip.getX()+clip.getWidth(),clip.getY(),-clip.getWidth(),clip.getHeight());
			}
			if(clip.getHeight()<0) {
				clip = new Rectangle2D.Double(clip.getX(),clip.getY()+clip.getHeight(),clip.getWidth(),-clip.getHeight());
			}
			workingGraphics.clip(clip);
			
			double wRatio = Math.abs(rects[a].getWidth()/cellWidth);
			double hRatio = Math.abs(rects[a].getHeight()/cellHeight);
			if(scaleProportionally==false) {
				workingGraphics.translate( rects[a].getX(), rects[a].getY() );
				workingGraphics.scale( wRatio, hRatio );
			} else {
				double zoom = Math.min(wRatio, hRatio);

				if(rects[a].getWidth()>0 && rects[a].getHeight()>0) {
					workingGraphics.translate( rects[a].getX()+rects[a].getWidth()/2-cellWidth*zoom/2, 
							rects[a].getY()+rects[a].getHeight()/2-cellHeight*zoom/2 );
					workingGraphics.scale( zoom, zoom );
				} else if(rects[a].getWidth()<0 && rects[a].getHeight()>0) {
					workingGraphics.translate( rects[a].getX()+rects[a].getWidth()/2+cellWidth*zoom/2, 
							rects[a].getY()+rects[a].getHeight()/2-cellHeight*zoom/2 );
					workingGraphics.scale( -zoom, zoom );
				} else if(rects[a].getWidth()>0 && rects[a].getHeight()<0) {
					workingGraphics.translate( rects[a].getX()+rects[a].getWidth()/2-cellWidth*zoom/2, 
							rects[a].getY()+rects[a].getHeight()/2+cellHeight*zoom/2 );
					workingGraphics.scale( zoom, -zoom );
				} else { //width<0 && height<0
					workingGraphics.translate( rects[a].getX()+rects[a].getWidth()/2+cellWidth*zoom/2, 
							rects[a].getY()+rects[a].getHeight()/2+cellHeight*zoom/2 );
					workingGraphics.scale( -zoom, -zoom );
				}
			}
			if(cells[a] != null) cells[a].paint(workingGraphics);
		}
	}
}
