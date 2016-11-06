/*
 * @(#)MutedColors.java
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
package com.pump.plaf;

import java.awt.Color;
import java.awt.geom.Point2D;

public class MutedColors extends ColorSet {
	
	public MutedColors(boolean grid) {
		super(grid);
	}
	
	public MutedColors(boolean grid,int rows,int columns) {
		super(grid, rows, columns);
	}

	@Override
	public int getRGB(float xFraction, float yFraction) {
		float saturation;
		float brightness;
		saturation = yFraction * .5f + .4f;
		brightness = 1 - .7f * yFraction * yFraction;
		float columnWidth = 1f/(columns);
		float hue = xFraction-columnWidth/2;
		return Color.HSBtoRGB(hue, saturation, brightness);
	}

	@Override
	public Point2D getRelativePoint(int rgb) {
		int red = (rgb >> 16) & 0xff;
		int green = (rgb >> 8) & 0xff;
		int blue = (rgb >> 0) & 0xff;
		float[] hsb = new float[3];
		Color.RGBtoHSB(red, green, blue, hsb);

		float hue = hsb[0];
		float saturation = hsb[1];
		float brightness = hsb[2];
		
		float xFraction, yFraction;

		float yFraction1 = (saturation-.4f)/.5f;
		if(yFraction1<0)
			yFraction1 = 0;
		if(yFraction1>1)
			yFraction1 = 1;

		float yFraction2 = (float)Math.sqrt( (brightness-1)/(-.7f) );
		if(yFraction2<0)
			yFraction2 = 0;
		if(yFraction2>1)
			yFraction2 = 1;
		
		float columnWidth = 1f/(columns);
		xFraction = hue+columnWidth/2;
		
		if(xFraction>1)
			xFraction = xFraction-1;
		
		if(yFraction2<.25) {
			//it becomes especially unreliable as it becomes smaller
			yFraction = yFraction1;
		} else {
			yFraction = (yFraction1+yFraction2)/2;
		}

		return new Point2D.Float(xFraction, yFraction);
	}	

	@Override
	protected float getHighlightAlpha() {
		return 1;
	}
}
