/*
 * @(#)GridBagDebugPanel.java
 *
 * $Date: 2016-01-30 19:07:08 -0500 (Sat, 30 Jan 2016) $
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
package com.pump.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;

import com.pump.awt.ModifierTracker;
import com.pump.graphics.BaselineGraphics2D;

/** As you mouse over components inside this panel,
 * this panel will repaint itself to show positioning
 * information.
 * <P>This in no way manipulates the components or their
 * layout: it only changes how they are rendered.
 */
public class GridBagDebugPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	List<Component> indicatedComponents = new ArrayList<Component>();
	MouseInputAdapter mouseListener = new MouseInputAdapter() {
		@Override
		public void mouseEntered(MouseEvent e) {
			mouseMoved(e);
		}
		@Override
		public void mouseExited(MouseEvent e) {
			mouseMoved(e);
		}
		@Override
		public void mouseDragged(MouseEvent e) {
			mouseMoved(e);
		}
		@Override
		public void mouseMoved(MouseEvent e) {
			Point p = e.getPoint();
			p = SwingUtilities.convertPoint(e.getComponent(), p, GridBagDebugPanel.this);
			for(int a = 0; a<getComponentCount(); a++) {
				Component child = getComponent(a);
				Point p2 = SwingUtilities.convertPoint(GridBagDebugPanel.this, p, child);
				boolean inside = child.contains(p2);
				if(inside && indicatedComponents.contains(child)==false) {
					indicatedComponents.add(child);
					repaint();
				} else if(inside==false && indicatedComponents.contains(child)==true) {
					indicatedComponents.remove(child);
					repaint();
				}
			}
		}
	};
	ContainerListener containerListener = new ContainerListener() {

		public void componentAdded(ContainerEvent e) {
			Component child = e.getChild();
			child.addMouseMotionListener(mouseListener);
			child.addMouseListener(mouseListener);
		}

		public void componentRemoved(ContainerEvent e) {}
	};

	public GridBagDebugPanel() {
		super();
		init();
	}
	private void init() {
		addMouseListener(mouseListener);
		addMouseMotionListener(mouseListener);
		addContainerListener(containerListener);
		ModifierTracker.addShiftChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				repaint();
			}
		});
	}

	public GridBagDebugPanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		init();
	}

	public GridBagDebugPanel(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		init();
	}

	public GridBagDebugPanel(LayoutManager layout) {
		super(layout);
		init();
	}
	
	public static void setOpacity(float f) {
		if(f<0 || f>1)
			throw new IllegalArgumentException("f ("+f+") must be between [0,1]");
		
		int alpha = (int)(f*255);
		lightRed = new Color(
				lightRed.getRed(),
				lightRed.getGreen(),
				lightRed.getBlue(),
				alpha
		);
		lightGreen = new Color(
				lightGreen.getRed(),
				lightGreen.getGreen(),
				lightGreen.getBlue(),
				alpha
		);
		lightBlue = new Color(
				lightBlue.getRed(),
				lightBlue.getGreen(),
				lightBlue.getBlue(),
				alpha
		);
	}

	static Color lightRed = new Color(255,200,200,130);
	static Color lightGreen = new Color(0,180,0,130);
	static Color lightBlue = new Color(0,0,255,130);
	static BasicStroke dottedStroke1 = new BasicStroke(1,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,10,new float[] {2,2},0);
	static BasicStroke dottedStroke2 = new BasicStroke(1,BasicStroke.CAP_BUTT,BasicStroke.JOIN_BEVEL,10,new float[] {2,2},1);
	@Override
	protected void paintChildren(Graphics g0) {
		Graphics2D g = (Graphics2D)g0;
		
		//paint a reddish background behind indicated components
		//and record the baselines used in the indicatedComponents
		BaselineGraphics2D bg = new BaselineGraphics2D();
		g.setColor(lightRed);
		for(int a = 0; a<getComponentCount(); a++) {
			Component c = getComponent(a);
			if(indicatedComponents.contains(c)) {
				g.fill(c.getBounds());
				bg.translate(c.getX(), c.getY());
				c.paint(bg);
				bg.translate(-c.getX(), -c.getY());
			}
		}
		super.paintChildren(g);

		//paint the blue dotted outlines showing the grid
		g.setStroke(dottedStroke1);
		try {
			//use reflection to get the grid structure:
			GridBagLayout layout = (GridBagLayout)getLayout();
			Object info = get(layout,"layoutInfo");
			int[] widths = (int[])get(info,"minWidth");
			int[] heights = (int[])get(info,"minHeight");
			Line2D line = new Line2D.Float();
			
			//which rows/columns need to be highlighted?
			HashSet<Integer> columns = new HashSet<Integer>();
			HashSet<Integer> rows = new HashSet<Integer>();
			for(int a = 0; a<indicatedComponents.size(); a++) {
				Component comp = indicatedComponents.get(a);
				GridBagConstraints c = layout.getConstraints(comp);
				int width = c.gridwidth;
				if(width==GridBagConstraints.REMAINDER)
					width = 100;
				for(int i = 0; i<width+1; i++) {
					columns.add(new Integer(c.gridx+i));
				}
				int height = c.gridheight;
				if(height==GridBagConstraints.REMAINDER)
					height = 100;
				for(int i = 0; i<height+1; i++) {
					rows.add(new Integer(c.gridy+i));
				}
			}

			//actually paint the grid lines:
			g.setColor(lightBlue);
			boolean exists, paint;
			int x = ((Integer)get(info,"startx")).intValue();
			for(int a = 0; a<widths.length; a++) {
				line.setLine(x, 0, x, getHeight());
				exists = a==0 || widths[a-1]!=0;
				Integer col = new Integer(a);
				paint = ModifierTracker.isShiftDown() || columns.contains(col);
				if(exists && paint)
					g.draw(line);
				x+= widths[a];
			}
			int y = ((Integer)get(info,"starty")).intValue();
			for(int a = 0; a<heights.length; a++) {
				line.setLine(0, y, getWidth(), y);
				exists = a==0 || heights[a-1]!=0;
				Integer row = new Integer(a);
				paint = ModifierTracker.isShiftDown() || rows.contains(row);
				if(exists && paint)
					g.draw(line);
				y+= heights[a];
			}
			
		} catch(Throwable t) {
			t.printStackTrace();
		}

		//paint the baselines:
		g.setStroke(dottedStroke2);
		g.setColor(lightGreen);
		int[] baselines = bg.getBaselines();
		for(int a = 0; a<baselines.length; a++) {
			g.drawLine(0,baselines[a],getWidth(),baselines[a]);
		}
	}
	
	private static Map<String, Field> fields = new HashMap<>();
	private static Object get(Object obj,String fieldName) throws IllegalArgumentException, IllegalAccessException {
		Field cachedField = fields.get(fieldName);
		if(cachedField!=null)
			return cachedField.get(obj);
		
		Class<?> c = obj.getClass();
		while(c!=null) {
			Field[] f = c.getDeclaredFields();
			for(int a = 0; a<f.length; a++) {
				if(f[a].getName().equals(fieldName)) {
					f[a].setAccessible(true);
					fields.put(fieldName,f[a]);
					return f[a].get(obj);
				}
			}
			c = c.getSuperclass();
		}
		throw new NullPointerException();
	}
}
