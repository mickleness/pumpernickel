/*
 * @(#)BasicColors.java
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

public class BasicColors extends ColorSet {
	static float[] popularHues = new float[] {0, 1.0f/12.0f, 1.0f/6.0f, 1.0f/3.0f, .5f, 2.0f/3.0f, 5.0f/6.0f};
	static int[] browns = new int[] { 0xffFFF2D5, 0xff1C1200 };
	static int[] grays = new int[] { 0xffffffff, 0xff000000 };
	
	final float[] hues;
	final int[][] specialColumns;
	
	public BasicColors(boolean grid) {
		this(grid,6,BasicColors.popularHues,popularHues.length,new int[][] { grays, browns });
	}
	public BasicColors(boolean grid,int rowCount,float[] hues,int hueColumns,int[][] columns) {
		super(grid, rowCount, hueColumns+columns.length);
		this.hues = hues;
		this.specialColumns = columns;
	}

	@Override
	protected float getHighlightAlpha() {
		return 1;
	}

	@Override
	public int getRGB(float xFraction, float yFraction) {
		float cutOff = 1-((float)specialColumns.length)/((float)columns);
		if(xFraction<cutOff) {
			//hues
			xFraction = xFraction/cutOff;
			xFraction = (xFraction-.5f/(columns)+1)%1f;
			
			int index = (int)(xFraction*hues.length);
			if(index==hues.length)
				index = hues.length-1;
			
			float p = (xFraction-((float)index)/((float)hues.length))*hues.length;
			int nextIndex = (index+1)%hues.length;
			
			float brightness, saturation;
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
			
			return Color.HSBtoRGB( tweenHues(hues[index],hues[nextIndex],p), saturation, brightness);
		} else {
			//special columns
			xFraction = (xFraction-cutOff)/(1-cutOff);
			int index = (int)(xFraction*specialColumns.length);
			if(index==specialColumns.length)
				index = specialColumns.length-1;
			
			float dy = .5f/(rows);
			float fraction = (yFraction-dy)/(1-2*dy);
			if(fraction<0) fraction = 0;
			if(fraction>1) fraction = 1;
			return tweenRGB( specialColumns[index][0], specialColumns[index][1], fraction);
		}
	}
	
	protected static int getRGBDistanceSquared(int rgb1,int rgb2) {
		int a1 = (rgb1 >> 24) & 0xff;
		int r1 = (rgb1 >> 16) & 0xff;
		int g1 = (rgb1 >> 8) & 0xff;
		int b1 = (rgb1 >> 0) & 0xff;
		int a2 = (rgb2 >> 24) & 0xff;
		int r2 = (rgb2 >> 16) & 0xff;
		int g2 = (rgb2 >> 8) & 0xff;
		int b2 = (rgb2 >> 0) & 0xff;
		int distanceSquared = (a1-a2)*(a1-a2)+(r1-r2)*(r1-r2)+(g1-g2)*(g1-g2)+(b1-b2)*(b1-b2);
		
		return distanceSquared;
	}

	@Override
	public Point2D getRelativePoint(int rgb) {
		int r = (rgb >> 16) & 0xff;
		int g = (rgb >> 8) & 0xff;
		int b = (rgb >> 0) & 0xff;
		float[] hsb = Color.RGBtoHSB(r, g, b, null);
		float hue = hsb[0];
		
		float xFraction = 0;
		float yFraction;
		

		int lightRGB = Color.HSBtoRGB(hue, hsb[1], 1);
		int darkRGB = Color.HSBtoRGB(hue, 1, hsb[2]);
		int lightError = getRGBDistanceSquared(lightRGB,rgb);
		int darkError = getRGBDistanceSquared(darkRGB,rgb);
		int minError;
		if(lightError<darkError) {
			yFraction = (hsb[1]-.1f)/1.8f;
			
			//but sometimes this will result in y<0
			yFraction = Math.max(0,Math.min(yFraction,1));
			float saturation = yFraction * 1.8f + .1f;
			lightRGB = Color.HSBtoRGB(hue, saturation, 1);
			lightError = getRGBDistanceSquared(lightRGB,rgb);
			minError = lightError;
		} else {
			yFraction = (float)( (-1-1*Math.sqrt(1-hsb[2]))/(-2) );

			//but sometimes this will result in y>1
			yFraction = Math.max(0,Math.min(yFraction,1));
			float t = (yFraction - .5f) * 2;
			float brightness = 1 - t * t;
			darkRGB = Color.HSBtoRGB(hue, 1, brightness);
			darkError = getRGBDistanceSquared(darkRGB,rgb);
			minError = darkError;
		}
		
		//much trickier: the xFraction:

		for(int index = 0; index<hues.length; index++) {
			int nextIndex = (index+1)%hues.length;
			float minHue;
			float maxHue;
			
			if(Math.abs(hues[index]-hues[nextIndex])>.5) {
				if(hues[index]<hues[nextIndex]) {
					minHue = hues[nextIndex];
					maxHue = hues[index]+1;
				} else { //if(hues[nextIndex]<hues[index]) {
					minHue = hues[index];
					maxHue = hues[nextIndex]+1;
				}
			} else {
				minHue = hues[index];
				maxHue = hues[nextIndex];
			}
			if(minHue<=hue && hue<=maxHue) {
				float t = (hue-minHue)/(maxHue-minHue);
				xFraction = ((float)index)/((float)hues.length)+t/(hues.length);
				xFraction = (xFraction+.5f/(columns)+1)%1;

				float cutOff = 1-((float)specialColumns.length)/((float)columns);
				xFraction = xFraction*cutOff;
			}
		}
		

		int[] columnRGBs = new int[specialColumns.length];
		
		for(int a = 0; a<columnRGBs.length; a++) {
			int rgb1 = specialColumns[a][0];
			int rgb2 = specialColumns[a][1];

			int r1 = (rgb1 >> 16) & 0xff;
			int g1 = (rgb1 >> 8) & 0xff;
			int b1 = (rgb1 >> 0) & 0xff;

			int r2 = (rgb2 >> 16) & 0xff;
			int g2 = (rgb2 >> 8) & 0xff;
			int b2 = (rgb2 >> 0) & 0xff;
			
			double[] quadratic = new double[] {
					r1*r1-2*r1*r2+r2*r2 + g1*g1-2*g1*g2+g2*g2 + b1*b1-2*b1*b2+b2*b2,
					2*r*r1-2*r*r2-2*r1*r1+2*r1*r2 + 2*g*g1-2*g*g2-2*g1*g1+2*g1*g2 + 2*b*b1-2*b*b2-2*b1*b1+2*b1*b2,
					r*r-2*r*r1+r1*r1 + g*g-2*g*g1+g1*g1 + r*r-2*b*b1+b1*b1
			};
			
			float minT = (float)(-quadratic[1] / (2*quadratic[0]));
			
			if(minT>=-0.000001 && minT<=1.000001) {
				int newRGB = tweenRGB(rgb1,rgb2,minT);
				int error = getRGBDistanceSquared(newRGB,rgb);
				if(error<=minError) {
					minError = error;
					xFraction = 1-((float)(specialColumns.length-a+.5-1))/(columns);
					float dy = .5f/(rows);
					yFraction = minT*(1-2*dy)+dy;
				}
			}
		}
		
		return new Point2D.Float(xFraction, yFraction);
	}
	
	private static final float tweenHues(float f1, float f2,float t) {
		if(Math.abs(f2-f1)<.5) {
			return f1*(1-t)+f2*t;
		} else if(f1<f2) {
			return tweenHues(f1+1,f2,t);
		} else {
			return tweenHues(f1, f2+1, t);
		}
	}
	
	private static final int tweenRGB(int rgb1,int rgb2,float t) {
		int a1 = (rgb1 >> 24) & 0xff;
		int r1 = (rgb1 >> 16) & 0xff;
		int g1 = (rgb1 >> 8) & 0xff;
		int b1 = (rgb1 >> 0) & 0xff;
		int a2 = (rgb2 >> 24) & 0xff;
		int r2 = (rgb2 >> 16) & 0xff;
		int g2 = (rgb2 >> 8) & 0xff;
		int b2 = (rgb2 >> 0) & 0xff;
		int a3 = (int)(a1*(1-t)+a2*t+.5) << 24;
		int r3 = (int)(r1*(1-t)+r2*t+.5) << 16;
		int g3 = (int)(g1*(1-t)+g2*t+.5) << 8;
		int b3 = (int)(b1*(1-t)+b2*t+.5);
		
		return (a3) + (r3) +(g3) + b3;
	}
	

}
