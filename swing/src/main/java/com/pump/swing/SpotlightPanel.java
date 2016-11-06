/*
 * @(#)SpotlightPanel.java
 *
 * $Date: 2015-05-30 21:49:03 -0400 (Sat, 30 May 2015) $
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
package com.pump.swing;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

import com.pump.plaf.UIEffect;

public class SpotlightPanel extends JComponent {
	private static final long serialVersionUID = 1L;

	protected static final String TARGET_ALPHA = SpotlightPanel.class.getName()+".target-alpha";
	protected static final String TARGET_X = SpotlightPanel.class.getName()+".target-x";
	protected static final String TARGET_Y = SpotlightPanel.class.getName()+".target-y";
	protected static final String TARGET_WIDTH = SpotlightPanel.class.getName()+".target-width";
	protected static final String TARGET_HEIGHT = SpotlightPanel.class.getName()+".target-height";
	protected static final String REAL_ALPHA = SpotlightPanel.class.getName()+".real-alpha";
	protected static final String REAL_X = SpotlightPanel.class.getName()+".real-x";
	protected static final String REAL_Y = SpotlightPanel.class.getName()+".real-y";
	protected static final String REAL_WIDTH = SpotlightPanel.class.getName()+".real-width";
	protected static final String REAL_HEIGHT = SpotlightPanel.class.getName()+".real-height";

	JComponent span;
	JComponent[] highlightedComponents = new JComponent[] {};
	private int adjustingBounds = 0;
	
	public SpotlightPanel(JComponent span) {
		this.span = span;
		ComponentListener cl = new ComponentListener() {

			@Override
			public void componentResized(ComponentEvent e) {
				recalculateBounds();
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				recalculateBounds();
			}

			@Override
			public void componentShown(ComponentEvent e) {
				recalculateBounds();
			}

			@Override
			public void componentHidden(ComponentEvent e) {
				recalculateBounds();
			}
		};
		span.addComponentListener(cl);
		addComponentListener(cl);
		recalculateBounds();
		
		UIEffect.installTweenEffect(this,
				TARGET_X,
				REAL_X,
				10,
				10);
		UIEffect.installTweenEffect(this,
				TARGET_Y,
				REAL_Y,
				10,
				10);
		UIEffect.installTweenEffect(this,
				TARGET_WIDTH,
				REAL_WIDTH,
				10,
				10);
		UIEffect.installTweenEffect(this,
				TARGET_HEIGHT,
				REAL_HEIGHT,
				10,
				10);
		UIEffect.installTweenEffect(this,
				TARGET_ALPHA,
				REAL_ALPHA,
				.1f,
				10);
		
		PropertyChangeListener pcl = new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				repaint();
			}
		};
		addPropertyChangeListener(REAL_X, pcl);
		addPropertyChangeListener(REAL_Y, pcl);
		addPropertyChangeListener(REAL_WIDTH, pcl);
		addPropertyChangeListener(REAL_HEIGHT, pcl);
		addPropertyChangeListener(REAL_ALPHA, pcl);
		
	}
	
	protected void recalculateBounds() {
		if(adjustingBounds>0) return;
		
		adjustingBounds++;
		try {
			if(span.isVisible()==false || 
					span.getParent()==null || 
					getParent()==null) {
				setVisible(false);
				return;
			}
			
			setVisible(true);
			Rectangle bounds = span.getBounds();
			bounds = SwingUtilities.convertRectangle(span.getParent(),bounds,this.getParent());
			setBounds(bounds);
			recalculateHighlight(true);
		} finally {
			adjustingBounds--;
		}
	}
	
	public void setHighlight(JComponent... components) {
		if(components==null) components = new JComponent[] {};
		highlightedComponents = components;
		recalculateHighlight(false);
		repaint();
	}
	
	protected void recalculateHighlight(boolean forceImmediateUpdate) {
		Rectangle highlightSum = null;
		for(JComponent h : highlightedComponents) {
			if(h.isShowing() && h.getParent()!=null) {
				Rectangle r = h.getBounds();
				r = SwingUtilities.convertRectangle(h.getParent(),r,this);
				if(highlightSum==null) {
					highlightSum = r;
				} else {
					highlightSum.add(r);
				}
			}
		}
		
		if(highlightSum==null) {
			putClientProperty(TARGET_ALPHA, 0f);
		} else {
			putClientProperty(TARGET_ALPHA, 1f);
			putClientProperty(TARGET_X, (float)highlightSum.getX() );
			putClientProperty(TARGET_Y, (float)highlightSum.getY() );
			putClientProperty(TARGET_WIDTH, (float)highlightSum.getWidth() );
			putClientProperty(TARGET_HEIGHT, (float)highlightSum.getHeight() );
		}
		
		if(getClientProperty(REAL_X)==null) {
			putClientProperty(REAL_ALPHA, 0 );
			putClientProperty(REAL_X, getClientProperty(TARGET_X) );
			putClientProperty(REAL_Y, getClientProperty(TARGET_Y) );
			putClientProperty(REAL_WIDTH, getClientProperty(TARGET_WIDTH) );
			putClientProperty(REAL_HEIGHT, getClientProperty(TARGET_HEIGHT) );
		}
		
		if(forceImmediateUpdate) {
			putClientProperty(REAL_ALPHA, getClientProperty(TARGET_ALPHA) );
			putClientProperty(REAL_X, getClientProperty(TARGET_X) );
			putClientProperty(REAL_Y, getClientProperty(TARGET_Y) );
			putClientProperty(REAL_WIDTH, getClientProperty(TARGET_WIDTH) );
			putClientProperty(REAL_HEIGHT, getClientProperty(TARGET_HEIGHT) );
		}
		

		
	}
	
	protected void paintComponent(Graphics g0) {
		Area area = new Area(new Rectangle(0,0,getWidth(),getHeight()));
		
		Number x = (Number)getClientProperty(REAL_X);
		Number y = (Number)getClientProperty(REAL_Y);
		Number width = (Number)getClientProperty(REAL_WIDTH);
		Number height = (Number)getClientProperty(REAL_HEIGHT);
		Number alpha = (Number)getClientProperty(REAL_ALPHA);
		Rectangle2D highlightSum = x==null ? null : new Rectangle2D.Float(x.floatValue(), y.floatValue(), width.floatValue(), height.floatValue());
		
		if(highlightSum!=null) {
			area.subtract(new Area(highlightSum));
			Graphics2D g = (Graphics2D)g0;
			
			if(span.isOpaque()) {
				Color c = span.getBackground();
				g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(),100));
			} else { 
				g.setColor(new Color(255,255,255,100));
			}
			g.fill(area);
			
			int a = 255;
			if(alpha!=null) a = (int)(255*alpha.floatValue());
			
			DashedBorder.paintBorder(g,
					(float)highlightSum.getX(),
					(float)highlightSum.getY(),
					(float)highlightSum.getWidth(),
					(float)highlightSum.getHeight(),
					new Color(0,0,0,a),
					2,
					10,
					20);
		}
	}
	
}
