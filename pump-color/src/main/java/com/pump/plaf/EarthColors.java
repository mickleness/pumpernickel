/*
 * @(#)EarthColors.java
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

public class EarthColors extends ColorSet {
	
	public EarthColors(boolean grid) {
		super(grid);
	}
	
	public EarthColors(boolean grid,int rows,int columns) {
		super(grid, rows, columns);
	}

	@Override
	public int getRGB(float xFraction, float yFraction) {
		float saturation;
		float brightness;
		saturation = (float) (yFraction * .5 + .4 + .25 * Math.sin(xFraction
				* Math.PI));
		if (saturation > 1)
			saturation = 1;
		brightness = (float) (1 - .8f * yFraction * yFraction - .2f * Math.pow(
				1 - (.8 * xFraction + .2), 2));
		float hue = (float) (Math.pow(xFraction, .7) * .5 + .1);
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
		float brightness = hsb[2];

		float xFraction, yFraction;

		if(hue>.1) {
			xFraction = (float)(Math.pow( (hue-.1)*2, 1.0/.7 ) );
		} else {
			xFraction = 0;
		}
		if(xFraction>1) xFraction = 1;

		//we can't look at saturation, because it may have a max capped on it

		yFraction = (float)Math.sqrt((1 - .2f * Math.pow(
				1 - (.8 * xFraction + .2), 2)-brightness)/.8);
		
		return new Point2D.Float(xFraction, yFraction);
	}

	@Override
	protected float getHighlightAlpha() {
		return 1;
	}
}
