/*
 * @(#)PivotingCirclesThrobberUI.java
 *
 * $Date: 2014-06-06 14:04:49 -0400 (Fri, 06 Jun 2014) $
 *
 * Copyright (c) 2014 by Jeremy Wood.
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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;

import javax.swing.JComponent;

/** A <code>ThrobberUI</code> showing 3 pivoting circles.
 * <p><table summary="Sample Animations of PivotingCirclesThrobberUI" cellpadding="10"><tr>
 * <td><img src="https://javagraphics.java.net/resources/throbber/PivotingCirclesThrobberUI.gif" alt="PivotingCirclesThrobberUI"></td>
 * <td><img src="https://javagraphics.java.net/resources/throbber/PivotingCirclesThrobberUIx2.gif" alt="PivotingCirclesThrobberUI, Magnified 2x"></td>
 * <td><img src="https://javagraphics.java.net/resources/throbber/PivotingCirclesThrobberUIx4.gif" alt="PivotingCirclesThrobberUI, Magnified 4x"></td>
 * </tr></table>
 * <p>On installation: the component's foreground is set to black,
 * but if that is changed then that color is used to render this animation.
 * <P>The default period for this animation is 2000, but you can modify
 * this with the period client properties {@link ThrobberUI#PERIOD_KEY} or
 * {@link ThrobberUI#PERIOD_MULTIPLIER_KEY}.
 */
public class PivotingCirclesThrobberUI extends ThrobberUI {
	
	public static final int DEFAULT_PERIOD = 2000;

	public PivotingCirclesThrobberUI() {
		super(1000/100);
	}

	@Override
	protected void paintForeground(Graphics2D g, JComponent jc, Dimension size,Float fixedFraction) {
		float w = size.width;
		float h = size.height;

		Color color = jc==null ? getDefaultForeground() : jc.getForeground();
		g.setColor(color);
		float f;
		if(fixedFraction!=null) {
			f = fixedFraction;
		} else {
			int p = getPeriod(jc, DEFAULT_PERIOD);
			float t = System.currentTimeMillis()%p;
			f = t / p;
		}
		
		float k = 3;
		for(int a = 0; a<k; a++) {
			float z = ((f + a/3f)%1);
			double dx = 3*Math.cos(2*k*Math.PI*z-2*Math.PI*z)*z;
			double dy = 3*Math.sin(2*k*Math.PI*z-2*Math.PI*z)*z;

			float alpha = (2*(z-.5f));
			alpha = -alpha*alpha+1;
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha ));
			double r = 4.5*z;
			Ellipse2D orbit = new Ellipse2D.Double(w/2-r+dx, h/2-r+dy, 2*r, 2*r);
			g.setStroke(new BasicStroke(2*z));
			g.draw(orbit);
		}
	}
		

	@Override
	public Color getDefaultForeground() {
		return Color.black;
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(16, 16);
	}

}
