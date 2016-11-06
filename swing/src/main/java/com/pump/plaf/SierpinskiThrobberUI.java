/*
 * @(#)SierpinskiThrobberUI.java
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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import javax.swing.JComponent;

/** A <code>ThrobberUI</code> that paints an inner and outer Sierpinski
 * triangle rotating clockwise and spiraling inward.
 * <p><table summary="Sample Animations of SierpinskiThrobberUI" cellpadding="10"><tr>
 * <td><img src="https://javagraphics.java.net/resources/throbber/SierpinskiThrobberUI.gif" alt="SierpinskiThrobberUI"></td>
 * <td><img src="https://javagraphics.java.net/resources/throbber/SierpinskiThrobberUIx2.gif" alt="SierpinskiThrobberUI, Magnified 2x"></td>
 * <td><img src="https://javagraphics.java.net/resources/throbber/SierpinskiThrobberUIx4.gif" alt="SierpinskiThrobberUI, Magnified 4x"></td>
 * </tr></table>
 * <p>On installation: the component's foreground is set to black,
 * but if that is changed then that color is used to render this animation.
 * <P>The default period for this animation is 2000, but you can modify
 * this with the period client properties {@link ThrobberUI#PERIOD_KEY} or
 * {@link ThrobberUI#PERIOD_MULTIPLIER_KEY}.
 */
public class SierpinskiThrobberUI extends ThrobberUI {
	
	public static final int DEFAULT_PERIOD = 2000;

	public SierpinskiThrobberUI() {
		super(1000/100);
	}

	@Override
	protected void paintForeground(Graphics2D g, JComponent jc, Dimension size,Float fixedFraction) {
		float w = size.width;
		float h = size.height;

		float f;
		if(fixedFraction!=null) {
			f = fixedFraction;
		} else {
			int p = getPeriod(jc, DEFAULT_PERIOD);
			float t = System.currentTimeMillis()%p;
			f = t / p;
		}
		f = 1-f;
		
		Color color = jc==null ? getDefaultForeground() : jc.getForeground();
		paintTriangle(g, color, w, h, f);
		paintTriangle(g, color, w, h, (f+.5f)%1f);
	}
	
	private void paintTriangle(Graphics2D g,Color c,float w,float h,float f) {
		GeneralPath path = new GeneralPath();
		float r = 1;
		int sides = 3;
		for(int a = 0; a<sides; a++) {
			double x = w/2 + r*Math.cos(a*2*Math.PI/sides);
			double y = h/2 + r*Math.sin(a*2*Math.PI/sides);
			if(a==0) {
				path.moveTo( (float)x, (float)y );
			} else {
				path.lineTo( (float)x, (float)y );
			}
		}
		path.closePath();
		
		AffineTransform t = new AffineTransform();
		t.translate(w/2, h/2);
		t.scale( f*w/2, f*w/2);
		t.rotate(-f*2*Math.PI);
		t.translate(-w/2, -h/2);
		
		float z = (2*(f-.5f));
		z = -z*z+1;
		g.setStroke(new BasicStroke(.5f+.5f*z));
		
		path.transform(t);
		int alpha = (int)(255*z);
		g.setColor( new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha) );
		g.draw(path);
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
