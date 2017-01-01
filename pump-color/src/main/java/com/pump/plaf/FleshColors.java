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
package com.pump.plaf;

import java.awt.Color;
import java.awt.geom.Point2D;

public class FleshColors extends ColorSet {
	
	public FleshColors(boolean grid) {
		super(grid);
	}
	
	public FleshColors(boolean grid,int rows,int columns) {
		super(grid, rows, columns);
	}
	
	@Override
	public int getRGB(float xFraction, float yFraction) {
		float saturation = (yFraction + .2f - .15f * xFraction) / .75f;
		float max = (float) (.8 - xFraction * .3);
		if (saturation > max)
			saturation = max;
		float brightness = (float) (1 - Math.pow(yFraction, xFraction * 2 + 1)
				* ((1 - xFraction) * .5f + .5f));
		float hue = (float) (Math.pow(xFraction, .5) * .08);
		return Color.HSBtoRGB(hue, saturation, brightness);
	}

	@Override
	public Point2D getRelativePoint(int rgb) {
		int red = (rgb >> 16) & 0xff;
		int green = (rgb >> 8) & 0xff;
		int blue = (rgb >> 0) & 0xff;
		float[] hsb = new float[3];
		Color.RGBtoHSB(red, green, blue, hsb);

		double xFraction, yFraction;

		/*double hue = hsb[0];
		double brightness = hsb[2];
		
		xFraction = (float)Math.pow( hue/.08, 1.0/.4 );
		
		if(xFraction>1) xFraction = 1;

		//we can't look at saturation, because it may have a max capped on it

		yFraction = (float)Math.pow( (1.0-brightness)/((1.0 - xFraction) * .2 + .7), 1.0/(xFraction * 2.0 + 1.0));
		
		*/
		

		xFraction = Math.pow(hsb[0] / .08, 2);

		yFraction = Math.pow( (1 - hsb[2])/((1 - xFraction) * .5f + .5f) , 1.0/(xFraction * 2 + 1));

		return new Point2D.Double(xFraction, yFraction);
	}

	@Override
	protected float getHighlightAlpha() {
		return 1;
	}
}