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
package com.pump.debug;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Point;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import com.pump.awt.PaddingInfo;
import com.pump.graphics.BaselineGraphics2D;

public class DebugPanel extends JPanel {
	
	private static final long serialVersionUID = 1L;
	
	MouseInputAdapter mouseListener = new MouseInputAdapter() {
		@Override
		public void mouseMoved(MouseEvent e) {
			Point p = e.getPoint();
			SwingUtilities.convertPointToScreen(p, e.getComponent());
			SwingUtilities.convertPointFromScreen(p, DebugPanel.this);
			setMouseLocation( p );
		}
		
		@Override
		public void mouseDragged(MouseEvent e) {
			mouseMoved(e);
		}
		
		@Override
		public void mouseExited(MouseEvent e) {
			mouseMoved(e);
		}
		
		@Override
		public void mouseEntered(MouseEvent e) {
			mouseMoved(e);
		}
	};

	public DebugPanel() {
		super();
		init();
	}

	public DebugPanel(boolean isDoubleBuffered) {
		super(isDoubleBuffered);
		init();
	}

	public DebugPanel(LayoutManager layout, boolean isDoubleBuffered) {
		super(layout, isDoubleBuffered);
		init();
	}

	public DebugPanel(LayoutManager layout) {
		super(layout);
		init();
	}
	
	private void init() {
		addContainerListener(new ContainerListener() {

			public void componentAdded(ContainerEvent e) {
				Component c = e.getComponent();
				c.addMouseListener(mouseListener);
				c.addMouseMotionListener(mouseListener);
			}

			public void componentRemoved(ContainerEvent e) {
				Component c = e.getComponent();
				c.removeMouseListener(mouseListener);
				c.removeMouseMotionListener(mouseListener);
			}
		});
	}
	
	Point mouseLoc = new Point(-1,-1);
	
	private void setMouseLocation(Point p) {
		mouseLoc = p;
		repaint();
	}
	
	@Override
	protected void paintChildren(Graphics g0) {
		super.paintChildren(g0);
		Graphics2D g = (Graphics2D)g0;
		Component c = getComponentAt(mouseLoc);
		if(c!=null && c!=this) {
			
			//paint baselines:
			BaselineGraphics2D bg = new BaselineGraphics2D();
			bg.translate(c.getX(),c.getY());
			c.paint(bg);
			bg.dispose();
			int[] baselines = bg.getBaselines();
			g.setColor(Color.green);
			g.setStroke(new BasicStroke(1,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER,10,new float[] {4,4},0));
			for(int i = 0; i<baselines.length; i++) {
				g.drawLine(0, baselines[i], getWidth(), baselines[i]);
			}
			g.setStroke(new BasicStroke(1,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER,10,new float[] {4,4},4));
			for(int i = 0; i<getComponentCount(); i++) {
				Component c2 = getComponent(i);
				if(c2!=null && c2!=c) {
					boolean intercepts = false;
					for(int j = 0; j<baselines.length; j++) {
						if(baselines[j]>=c2.getY() && baselines[j]<=c2.getY()+c2.getHeight()) {
							intercepts = true;
						}
					}
					if(intercepts) {
						bg = new BaselineGraphics2D();
						bg.translate(c2.getX(),c2.getY());
						c2.paint(bg);
						bg.dispose();
						int[] baselines2 = bg.getBaselines();
						for(int j = 0; j<baselines2.length; j++) {
							g.setColor(Color.red);
							if(contains(baselines,baselines2[j])==false) {
								g.drawLine(0, baselines2[j], getWidth(), baselines2[j]);
							}
						}
					}
				}
			}
			
			//paint padding:
			Insets i = PaddingInfo.staticInfo.get(c);
			if(i!=null) {
				g.setColor(new Color(255,0,255,40));
				g.fillRect(c.getX(), c.getY(), c.getWidth(), i.top);
				g.fillRect(c.getX(), c.getY()+c.getHeight()-i.bottom, c.getWidth(), i.bottom);
				g.fillRect(c.getX(), c.getY()+i.top, i.left, c.getHeight()-i.top-i.bottom);
				g.fillRect(c.getX()+c.getWidth()-i.right, c.getY()+i.top, i.right, c.getHeight()-i.top-i.bottom);
			}
		}
	}
	
	private boolean contains(int[] array,int value) {
		for(int a = 0; a<array.length; a++) {
			if(array[a]==value)
				return true;
		}
		return false;
	}
}