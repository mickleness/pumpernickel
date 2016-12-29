/*
 * @(#)AquaAngleSliderUI.java
 *
 * $Date: 2014-03-16 18:30:29 -0400 (Sun, 16 Mar 2014) $
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

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.geom.Ellipse2D;

import javax.swing.JComponent;
import javax.swing.JSlider;

/** This <code>AngleSliderUI</code> is designed to resemble the similar control
 * in Aqua.  This is not a pixel-perfect emulation; feel free to adjust it as needed.
 */
public class AquaAngleSliderUI extends AngleSliderUI {
	
	private static final String MY_CACHED_FILLS = "com.pump.plaf.AquaSliderUI.fills";
	
	private FocusListener myFocusListener = new FocusListener() {
		public void focusGained(FocusEvent e) {
			JSlider slider = (JSlider)e.getComponent();
			slider.putClientProperty(MY_CACHED_FILLS,null);
		}
		
		public void focusLost(FocusEvent e) {
			JSlider slider = (JSlider)e.getComponent();
			slider.putClientProperty(MY_CACHED_FILLS,null);
		}
	};
	@Override
	protected Dimension getPreferredBaseDimension(JComponent c) {
		return new Dimension(39,39);
	}
	
	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		c.addFocusListener(myFocusListener);
		
		Data data = getData( (JSlider)c );
		data.insets.left = 4;
		data.insets.right = 4;
		data.insets.top = 5;
		data.insets.bottom = 5;
	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);
		c.removeFocusListener(myFocusListener);
	}

	private static final Color gray1 = new Color(215,215,215);
	private static final Color gray2 = new Color(250,250,250);
	private static final Color borderColor = new Color(88,88,88);
	private static final Color highlight2 = new Color(255,255,255,0);
	private static final Color highlight1 = new Color(255,255,255,230);
	private static final Color gShadow1 = new Color(0,0,0,50);
	private static final Color gShadow2 = new Color(0,0,0,45);
	private static final Color gShadow3 = new Color(0,0,0,0);
	private static final Color bShadow1 = new Color(27,59,120,80);
	private static final Color bShadow2 = new Color(50,111,185,130);
	private static final Color bShadow3 = new Color(27,59,120,0);
	private static final Color blue1 = new Color(81,145,214);
	private static final Color blue2 = new Color(206,238,255);
	
	@Override
	protected void calculateGeometry(JSlider slider) {
		super.calculateGeometry(slider);
		slider.putClientProperty(MY_CACHED_FILLS, null); //force these to recalculate
	}

	/** The dial is simply a series of translucent linear gradients places on top of each other.
	 * This method calculates those gradients.
	 */
	private Paint[] getFills(JSlider slider) {
		Paint[] p = new Paint[6];
		
		Data data = getData(slider);
		
		float y = (float)data.dial.getY();
		float x = (float)data.dial.getX();
		float w = (float)data.dial.getWidth();
		float h = (float)data.dial.getHeight();
		
		boolean dark = data.mousePressed;
		if(dark) {
			p[0] = new GradientPaint(0,y+1f/5f*h,blue1,0,y+h-h/4,blue2);
		} else {
			p[0] = new GradientPaint(0,y+1f/5f*h,gray1,0,y+h-h/4,gray2);
		}
		
		Color shadow1, shadow2, shadow3;
		if(dark) {
			shadow1 = bShadow1;
			shadow2 = bShadow2;
			shadow3 = bShadow3;
		} else {
			shadow1 = gShadow1;
			shadow2 = gShadow2;
			shadow3 = gShadow3;
		}
		
		p[1] = new GradientPaint(x,y+h/2,shadow1,x+w/4,y+h/2,shadow3);
		p[2] = new GradientPaint(x+w,y+h/2,shadow1,x+w-w/4,y+h/2,shadow3);
		
		p[3] = new GradientPaint(0,y+1f/10f*h,highlight1,0,y+1f/7f*h,highlight2);

		p[4] = new GradientPaint(x,y+h/2-h/5,shadow2,x+w/4,y+h/2,shadow3);
		p[5] = new GradientPaint(x+w,y+h/2-h/5,shadow2,x+w-w/4,y+h/2,shadow3);
		
		return p;
	}
	
	@Override
	public void paint(Graphics g0, JComponent c) {
		Graphics2D g = (Graphics2D)g0;
		JSlider slider = (JSlider)c;
		if(slider.isOpaque()) {
			g.setColor(slider.getBackground());
			g.fillRect(0,0,slider.getWidth(), slider.getHeight());
		}
		
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		Composite oldComposite = null;
		if(slider.isEnabled()==false) {
			oldComposite = g.getComposite();
			g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,.5f));
		}
		
		Data data = getData(slider);
		
		
		// paint a tiny highlight:
		g.setStroke(new BasicStroke(1.5f));
		g.translate(0,1);
		g.setColor(Color.white);
		g.draw(data.dial);
		g.translate(0,-1);
		
		if(slider.hasFocus()) {
			PlafPaintUtils.paintFocus(g, data.dial,4);
		}
		
		Paint[] p = (Paint[])slider.getClientProperty(MY_CACHED_FILLS);
		if(p==null) {
			p = getFills(slider);
			slider.putClientProperty(MY_CACHED_FILLS, p);
		}
		
		// paint the dial:
		
		for(int a = 0; a<p.length; a++) {
			g.setPaint(p[a]);
			g.fill(data.dial);
		}
				
		// paint the knob:
		
		float angle = (float)(slider.getValue()-slider.getMinimum())/(((float)(slider.getMaximum()-slider.getMinimum())));
		angle = angle*(float)(2*Math.PI);

		float centerX = (float)data.dial.getCenterX();
		float centerY = (float)data.dial.getCenterY();
		float r = (float)Math.min(centerX-data.dial.getX(), centerY-data.dial.getY())-4;
		float kx = (float)(centerX+r*Math.cos(angle));
		float ky = (float)(centerY+r*Math.sin(angle));
		Ellipse2D knob = new Ellipse2D.Float();
		
		float kr = 2.5f;
		float z = .85f;
		Shape oldClip = g.getClip();
		knob.setFrame(kx-kr-.5,ky-kr-.5,2*kr+1,2*kr+1);
		g.clip(knob);
		
		knob.setFrame(kx-kr,ky-kr,2*kr,2*kr);
		g.setColor(Color.black);
		g.fill(knob);
		g.setColor(Color.gray);
		knob.setFrame(kx-kr+z,ky-kr+z,2*kr,2*kr);
		g.fill(knob);
		g.setColor(Color.white);
		knob.setFrame(kx-kr+2*z,ky-kr+2*z,2*kr,2*kr);
		g.fill(knob);
		g.setClip(oldClip);
		
		//paint the border:
		
		g.setColor(borderColor);
		g.draw(data.dial);
		
		if(oldComposite!=null) {
			g.setComposite(oldComposite);
		}
	}

}
