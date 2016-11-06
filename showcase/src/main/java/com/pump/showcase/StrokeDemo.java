/*
 * @(#)StrokeDemo.java
 *
 * $Date: 2016-01-30 18:40:21 -0500 (Sat, 30 Jan 2016) $
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
package com.pump.showcase;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.geom.MeasuredShape;

/** An abstract model to demo a stroke.
 * 
 */
public abstract class StrokeDemo extends JPanel implements ChangeListener {
	private static final long serialVersionUID = 1L;

	/** Creates a wiggleish curve to demo a stroke. 
	 * 
	 * @param dest a shape to write to.
	 * @return the bounds of the shape.
	 */
	public static Rectangle2D defineWiggle(GeneralPath dest) {
		float x1, y1, x2, y2, x3, y3, x4, y4, x5, y5, s;
		x1 = 0;
		y1 = 1;
		float segments = 4;
		dest.moveTo(x1, y1);
		float minY = 1;
		float maxY = 1;
		for(int a = 0; a<segments; a++) {
			x1 = 4*a+0;
			y1 = 1;
			s = (.25f*(a+1)/segments);
			x2 = 4*a+1;
			y2 = 1+1*s;
			dest.curveTo(x1+.5f*s, y1+.5f*s, x2-.5f*s, y2, x2, y2);
			s = (.5f*(a+1)/segments);
			x3 = 4*a+2;
			y3 = 1;
			dest.curveTo(x2+.5f*s, y2, x3-.5f*s, y3+.5f*s, x3, y3);
			s = (.75f*(a+1)/segments);
			x4 = 4*a+3;
			y4 = 1-1*s;
			dest.curveTo(x3+.5f*s, y3-.5f*s, x4-.5f*s, y4, x4, y4);
			s = (1f*(a+1)/segments);
			x5 = 4*a+4;
			y5 = 1;
			dest.curveTo(x4+.5f*s, y4, x5-.5f*s, y5-.5f*s, x5, y5);
			minY = Math.min(minY, Math.min(y1, Math.min(y2, Math.min(y3, Math.min(y4, y5)))));
			maxY = Math.max(maxY, Math.max(y1, Math.max(y2, Math.max(y3, Math.max(y4, y5)))));
		}
		Rectangle2D bounds = new Rectangle2D.Float(0, minY, segments*4, maxY-minY);
		return bounds;
	}
	
	Class<?> theStrokeClass;
	Constructor<?> theConstructor;
	JSpinner[] spinners;
	JCheckBox rectangles = new JCheckBox("Use Rectangles");
	
	JPanel previewPanel = new JPanel() {
		private static final long serialVersionUID = 1L;
		@Override
		public Dimension getPreferredSize() {
			return new Dimension(250,250);
		}
		@Override
		protected void paintComponent(Graphics g0) {
			super.paintComponent(g0);
			Graphics2D g = (Graphics2D)g0;
			Dimension d = getSize();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
			
			RectangularShape e;
			if(rectangles.isSelected()) {
				e = new Rectangle2D.Float();
			} else {
				e = new Ellipse2D.Float();
			}
			int k = Math.min(d.width/2,d.height/2);
			List<GeneralPath> v = new ArrayList<GeneralPath>();
			float width = 10;

			for(float a = 0; a<k; a+=20+width) {
				e.setFrame(10+a,10+a,d.width-20-2*a,d.height-20-2*a);
				if(e.getWidth()>0 && e.getHeight()>0) {
					MeasuredShape m = new MeasuredShape(e);
					float z = (a)/(k);
					v.add( m.getShape( 1.4f*z, (k-a)/(k) ) );
				}
			}
			
			GeneralPath[] rings = v.toArray(new GeneralPath[v.size()]);

			g.setColor(Color.black);
			Stroke[] strokes = new Stroke[rings.length];
			for(int a = 0; a<rings.length; a++) {
				Object[] obj = new Object[spinners.length];
				Class<?>[] p = theConstructor.getParameterTypes();
				for(int b = 0; b<p.length; b++) {
					int extra = 0;
					if(b==0)
						extra = a;
					if(p[b].equals(Integer.TYPE) || p[b].equals(Integer.class)) {
						obj[b] = new Integer( ((Number)spinners[b].getValue()).intValue()+extra );
					} else if(p[b].equals(Float.TYPE) || p[b].equals(Float.class)) {
						obj[b] = new Float( ((Number)spinners[b].getValue()).floatValue()+extra );
					} else if(p[b].equals(Double.TYPE) || p[b].equals(Double.class)) {
						obj[b] = new Double( ((Number)spinners[b].getValue()).doubleValue()+extra );
					} else if(p[b].equals(Long.TYPE) || p[b].equals(Long.class)) {
						obj[b] = new Long( ((Number)spinners[b].getValue()).longValue()+extra );
					}
				}
				try {
					strokes[a] = (Stroke)theConstructor.newInstance(obj);
				} catch(Throwable t) {
					t.printStackTrace();
				}
			}
			long t = System.currentTimeMillis();
			for(int a = 0; a<rings.length; a++) {
				g.setStroke(strokes[a]);
				g.draw(rings[a]);
			}
			t = System.currentTimeMillis()-t;
			
			/*g.setColor(Color.red);
			
			for(int a = 0; a<rings.length; a++) {
				g.setStroke(new BasicStroke(.5f));
				g.draw(strokes[a].createStrokedShape(rings[a]));
			}*/
			
			g.setFont(new Font("Default",0,10));
			g.setColor(Color.blue);
			g.drawString(Long.toString(t)+" ms", 3, 10);
		}
	};
	
	/**
	 * 
	 * @param theStrokeClass the type of stroke.
	 * @param theConstructor the constructor to invoke.
	 * @param labels the textual descriptions of the spinners.
	 * @param spinners one spinner for every argument the constructor needs.
	 */
	public StrokeDemo(Class<?> theStrokeClass,Constructor<?> theConstructor,JLabel[] labels,JSpinner[] spinners) {
		
		setLayout(new GridBagLayout());
		this.theStrokeClass = theStrokeClass;
		this.theConstructor = theConstructor;
		this.spinners = spinners;
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0;
		c.weightx = 1; c.weighty = 0;
		c.anchor = GridBagConstraints.EAST;
		c.insets = new Insets(5,5,5,5);
		for(int a = 0; a<labels.length; a++) {
			c.gridy = a;
			add(labels[a],c);
		}
		c.gridx = 1;
		c.gridy = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.WEST;
		for(int a = 0; a<spinners.length; a++) {
			c.gridy = a;
			add(spinners[a],c);
			spinners[a].addChangeListener(this);
		}
		
		c.gridx = 0; c.gridwidth = GridBagConstraints.REMAINDER;
		c.weightx = 1; c.weighty = 0;
		c.gridy = Math.max(labels.length,spinners.length)+1;
		add(rectangles,c);
		
		c.gridy++; c.weighty++;
		add(previewPanel,c);
		
		rectangles.addChangeListener(this);

		rectangles.setBackground(Color.white);
		previewPanel.setBackground(Color.white);
	}
	
	public void stateChanged(ChangeEvent e) {
		previewPanel.repaint();
	}
}
