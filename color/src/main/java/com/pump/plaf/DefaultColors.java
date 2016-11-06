/*
 * @(#)DefaultColors.java
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

public class DefaultColors extends ColorSet {
	
	public DefaultColors(boolean grid) {
		super(grid);
	}
	
	public DefaultColors(boolean grid,int rows,int columns) {
		super(grid, rows, columns);
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

		float rgbMax = ((float)(columns-1))/((float)columns);
		
		/** This palette is really a combination of 3 palettes.
		* Which one is closest to the value we're looking for?
		*/

		int gray = (red+green+blue)/3;
		int grayError = (red-gray)*(red-gray)+(green-gray)*(green-gray)+(blue-gray)*(blue-gray);
		
		//the other 2 palettes use the same definition of hue:
		
		int lightRGB = Color.HSBtoRGB(hue, saturation, 1);
		int lightRed = (lightRGB >> 16) & 0xff;
		int lightGreen = (lightRGB >> 8) & 0xff;
		int lightBlue = (lightRGB >> 0) & 0xff;
		int lightError = (red-lightRed)*(red-lightRed)+(green-lightGreen)*(green-lightGreen)+(blue-lightBlue)*(blue-lightBlue);
		
		int darkRGB = Color.HSBtoRGB(hue, 1, brightness);
		int darkRed = (darkRGB >> 16) & 0xff;
		int darkGreen = (darkRGB >> 8) & 0xff;
		int darkBlue = (darkRGB >> 0) & 0xff;
		int darkError = (red-darkRed)*(red-darkRed)+(green-darkGreen)*(green-darkGreen)+(blue-darkBlue)*(blue-darkBlue);
		
		float xFraction, yFraction;

		float halfColumn = (.5f)/(columns);
		if(grayError<=lightError && grayError<=darkError) {
			float whiteLimit = ((float)(1))/((float)rows);
			float blackLimit = ((float)(rows-1))/((float)rows);
			xFraction = rgbMax + halfColumn;
			if(red==255 && green==255 && blue==255) {
				yFraction = whiteLimit/2;
			} else if(red==0 && green==0 && blue==0) {
				yFraction = blackLimit+whiteLimit/2;
			} else {
				yFraction = whiteLimit+(1-(gray)/255f)*(blackLimit-whiteLimit);
			}
		} else {
			//our definition of hue is the same for the light & dark palettes
			xFraction = ((hue+halfColumn)*rgbMax);
			if(xFraction>rgbMax)
				xFraction = xFraction-rgbMax;
			//in extreme cases, err on the side of being too far left
			//instead of too far right.
			//If the user is dragging over the far left of the palette,
			//this prevents nasty twitching.
			if(Math.abs(xFraction-rgbMax)<.001)
				xFraction = 0;
			
			if(lightError<darkError) {
				//float t = yFraction * 2;
				//t = t * .9f + .1f;
				//saturation = t;
				
				//reverse engineer:
				yFraction = (saturation-.1f) / 1.8f;
			} else {
				//float t = (yFraction - .5f) * 2;
				//brightness = 1 - t * t;
				//float t = (2*yFraction - 1);
				// leads to:
				//0 = -4*yFraction*yFraction + 4*yFraction-brightness;

				//reverse engineer:
				yFraction = (float)( (-1-1*Math.sqrt(1-brightness))/(-2) );

			}
		}
		return new Point2D.Float(xFraction, yFraction);
	}

	@Override
	public int getRGB(float xFraction, float yFraction) {
		float rgbMax = ((float)(columns-1))/((float)columns);
		if(xFraction<rgbMax) {
			float hue = (xFraction/rgbMax-(.5f)/(columns))%2;
			float saturation, brightness;
			if (yFraction < .5f) {
				brightness = 1;
				float t = yFraction * 2;
				t = t * .9f + .1f;
				saturation = t;
			} else {
				saturation = 1;
				float t = (yFraction - .5f) * 2;
				brightness = 1 - t * t;
			}
			return Color.HSBtoRGB(hue, saturation, brightness);
		}
		float whiteLimit = ((float)(1))/((float)rows);
		float blackLimit = ((float)(rows-1))/((float)rows);
		
		if(yFraction<whiteLimit) {
			return 0xffffffff;
		} else if(yFraction>blackLimit) {
			return 0xff000000;
		}
		float height = blackLimit-whiteLimit;
		float t = (yFraction-whiteLimit)/height;
		
		int gray = (int)(255*(1-t));
		int rgb = (gray << 16) + (gray << 8) + gray+(0xff000000);
		return rgb;
	}

	@Override
	protected float getHighlightAlpha() {
		return 1;
	}
}
