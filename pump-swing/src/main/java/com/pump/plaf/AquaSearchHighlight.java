/*
 * @(#)AquaSearchHighlight.java
 *
 * $Date: 2015-06-20 07:48:46 -0400 (Sat, 20 Jun 2015) $
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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.RoundRectangle2D;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.text.JTextComponent;

/** This is a highlight effect designed to emulate Aqua's yellow
 * search highlight.
 */
public class AquaSearchHighlight extends AbstractSearchHighlight {
	static final Color gradientColor1 = new Color(244, 239, 0);
	static final Color gradientColor2 = new Color(246, 208, 0);
	static final Color borderColor = new Color(240, 237, 0);
	static final Color shadow1 = new Color(0,0,0,40);
	static final Color shadow2 = new Color(0,0,0,20);
	static final Stroke border1 = new BasicStroke(3);
	static final Stroke border2 = new BasicStroke(2);
	static final Stroke normalStroke = new BasicStroke(1);
	
	public AquaSearchHighlight(JTable table,int selectedRow,int selectedColumn) {
		super(table, selectedRow, selectedColumn);
	}
	
	public AquaSearchHighlight(JTextComponent jtc, int startIndex, int endIndex) {
		super(jtc, startIndex, endIndex);
	}

	@Override
	protected Insets getHighlightInsets() {
		return new Insets(8,8,8,8);
	}

	@Override
	protected void paintHighlightBackground(Graphics2D g, Rectangle textRect) {
		RoundRectangle2D shape = new RoundRectangle2D.Float(textRect.x-6,
				textRect.y-6,
				textRect.width+12,
				textRect.height+8, //yes, I used 8 not 12 on purpose
				12, 12);
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		
		g.translate(0, 1);
		g.setColor(shadow1);
		g.setStroke(border1);
		g.draw(shape);
		g.translate(0, -1);
		
		g.setColor(shadow2);
		g.setStroke(border2);
		g.draw(shape);
		
		g.setPaint(new GradientPaint(0, 0, gradientColor1, 0,
				(float)shape.getHeight(), gradientColor2));
		g.fill(shape);
		g.setStroke(normalStroke);
		g.setColor(borderColor);
		g.draw(shape);
	}



	@Override
	protected void updateAnimation(JComponent[] highlights,float fraction) {
		
		if (fraction < .5) {
			double sizeScale = .9+.2*Math.sin(fraction / .5 * Math.PI)+.1*fraction/.5;
			AffineTransform transform = AffineTransform.getScaleInstance(sizeScale, sizeScale);
			for(int a = 0; a<highlights.length; a++) {
				highlights[a].putClientProperty("transform", transform);
			}
		} else {
			for(int a = 0; a<highlights.length; a++) {
				highlights[a].putClientProperty("transform", null);
			}
		}
	}
}
