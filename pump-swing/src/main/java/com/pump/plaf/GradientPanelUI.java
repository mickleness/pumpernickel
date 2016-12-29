/*
 * @(#)GradientPanelUI.java
 *
 * $Date: 2014-03-13 04:15:48 -0400 (Thu, 13 Mar 2014) $
 *
 * Copyright (c) 2012 by Jeremy Wood.
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
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;

import javax.swing.JComponent;
import javax.swing.plaf.PanelUI;

/** This is a PanelUI with a two-color vertical gradient.
 * 
 */
public class GradientPanelUI extends PanelUI {
	Color[] colors;
	float[] fractions;
	public GradientPanelUI(Color color1,Color color2) {
		fractions = new float[] { 0, 1 };
		colors = new Color[] { color1, color2 };
	}

	int cachedHeight = -11111;
	LinearGradientPaint paint;
	
	@Override
	public void paint(Graphics g0, JComponent c) {
		int width = c.getWidth();
		int height = c.getHeight();
		if(height!=cachedHeight) {
			paint = new LinearGradientPaint(0, 0, 0, height, fractions, colors);
			cachedHeight = height;
		}
		Graphics2D g = (Graphics2D)g0;
		g.setPaint(paint);
		g.fillRect(0,0,width,height);
	}
}
