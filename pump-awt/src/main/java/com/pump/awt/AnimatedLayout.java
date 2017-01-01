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
package com.pump.awt;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/** A layout that smoothly animates components as they need to
 * move from one location to another.
 * <P>This is achieved by calling:
 * <BR><code>myComponent.putClientProperty(DESTINATION, newDestination)</code>
 * <P>This layout then nudges the component until it reaches that destination.
 * The nudge is proportional to the distance that the component has to cover,
 * giving the impression of a decelerating animation as the object approaches
 * its destination.
 *
 */
public class AnimatedLayout implements LayoutManager2 {
	/** This is the delay, in milliseconds, between adjustments.
	 * I find 30 to be almost "too fast" and 50 almost "too slow".
	 */
	public static int DELAY = 35;
	private boolean calculateMinimumSize = false;
	
	public AnimatedLayout(boolean calculateMinimumSize) {
		this.calculateMinimumSize = calculateMinimumSize;
	}
	
	public void addLayoutComponent(Component comp, Object constraints) {}

	public float getLayoutAlignmentX(Container target) {
		return .5f;
	}

	public float getLayoutAlignmentY(Container target) {
		return .5f;
	}

	public void invalidateLayout(Container target) {}

	public Dimension maximumLayoutSize(Container target) {
		return preferredLayoutSize(target);
	}

	public void addLayoutComponent(String name, Component comp) {}

	static class AdjustListener implements ActionListener {
		JComponent container;

		public AdjustListener(JComponent container) {
			this.container = container;
		}

		public void actionPerformed(ActionEvent e) {
			Timer timer = (Timer)e.getSource();

			boolean workToDo = false;
			
			for(int a = 0; a<container.getComponentCount(); a++) {
				JComponent c = (JComponent)container.getComponent(a);
				Rectangle dest = (Rectangle)c.getClientProperty(DESTINATION);
				if(dest==null) {
					Dimension d = c.getPreferredSize();
					dest = new Rectangle(0,0,d.width,d.height);
				}
				if( nudge(c,dest)==false ) {
					workToDo = true;
				}
			}
			//on Windows, Ladislav pointed out there's a repaint problem
			container.repaint(); 
			
			if(workToDo==false)
				timer.stop();
		}
	}
	
	private static double sign(double d) {
		if(d<0) return -1;
		if(d>0) return 1;
		return 0;
	}
	
	private static String PROPERTY_LAST_DX = "AnimatedLayout.lastDX";
	private static String PROPERTY_LAST_DY = "AnimatedLayout.lastDY";
	private static String PROPERTY_LAST_DW = "AnimatedLayout.lastDW";
	private static String PROPERTY_LAST_DH = "AnimatedLayout.lastDH";
	
	private static double getDouble(JComponent c,String key,double defaultValue) {
		Double d = (Double)c.getClientProperty(key);
		if(d==null) return defaultValue;
		return d.doubleValue();
	}
	
	private static double limit(double v1,double limit) {
		if(limit<0) {
			return Math.max(limit,v1);
		}
		return Math.min(limit,v1);
		
	}
	
	/** Nudge a component towards the destination.
	 * @param c the component to nudge.
	 * @param dest the target bounds for the component.
	 * @return true when the component is at the desired location
	 */
	protected static boolean nudge(JComponent c,Rectangle dest) {
		Rectangle bounds = c.getBounds();
		
		double lastDX = getDouble(c,PROPERTY_LAST_DX,0);
		double lastDY = getDouble(c,PROPERTY_LAST_DY,0);
		double lastDW = getDouble(c,PROPERTY_LAST_DW,0);
		double lastDH = getDouble(c,PROPERTY_LAST_DH,0);
		
		double dx = dest.x-bounds.x;
		double dy = dest.y-bounds.y;
		double dw = dest.width-bounds.width;
		double dh = dest.height-bounds.height;
		
		dx = limit(.5*sign(dx)*Math.sqrt(Math.abs(dx))+.5*lastDX,dx);
		dy = limit(.5*sign(dy)*Math.sqrt(Math.abs(dy))+.5*lastDY,dy);
		dw = limit(.5*sign(dw)*Math.sqrt(Math.abs(dw))+.5*lastDW,dw);
		dh = limit(.5*sign(dh)*Math.sqrt(Math.abs(dh))+.5*lastDH,dh);

		c.putClientProperty(PROPERTY_LAST_DX, new Double(dx));
		c.putClientProperty(PROPERTY_LAST_DY, new Double(dy));
		c.putClientProperty(PROPERTY_LAST_DW, new Double(dw));
		c.putClientProperty(PROPERTY_LAST_DH, new Double(dh));
		
		if(Math.abs(dx)<1.2 && 
				Math.abs(dy)<1.2 &&
				Math.abs(dw)<1.2 && 
				Math.abs(dh)<1.2 ) {
			c.setBounds(dest);
			return true;
		}
		
		bounds.x += (int)(dx+.5);
		bounds.y += (int)(dy+.5);
		bounds.width += (int)(dw+.5);
		bounds.height += (int)(dh+.5);
	
		c.setBounds(bounds);
	
		return false;
	}
	
	public void layoutContainer(Container parent) {
		runLayout( (JComponent)parent);
		registerContainer(parent);
	}
	
	private static PropertyChangeListener destinationListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			Component c = (JComponent)evt.getSource();
			JComponent parent = (JComponent)c.getParent();
			if(parent!=null) {
				runLayout(parent);
			}
		}
	};
	
	private static void registerContainer(Container c) {
		synchronized(AnimatedLayout.class) {
			ContainerListener l = (ContainerListener)((JComponent)c).getClientProperty("animatedLayout.containerListener");
			if(l==null) {
				l = new ContainerListener() {
					public void componentAdded(ContainerEvent e) {
						JComponent c = (JComponent)e.getComponent();
						registerChildren(c);
					}

					public void componentRemoved(ContainerEvent e) {}
				};
				((JComponent)c).putClientProperty("animatedLayout.containerListener",l);
				c.addContainerListener(l);
				registerChildren((JComponent)c);
			}
		}
	}
	
	private static void registerChildren(JComponent c) {
		for(int a = 0; a<c.getComponentCount(); a++) {
			JComponent child = (JComponent)c.getComponent(a);
			if(child.getClientProperty("animatedLayout.propertyListener")==null) {
				child.putClientProperty("aniamtedLayout.propertyListener",destinationListener);
				child.addPropertyChangeListener(DESTINATION, destinationListener);
			}
		}
	}
	
	protected static void runLayout(final JComponent parent) {
		if(SwingUtilities.isEventDispatchThread()==false) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					runLayout(parent);
				}
			});
			return;
		}
		
		Timer timer = (Timer)parent.getClientProperty(LAYOUT_TIMER);
		if(timer==null) {
			timer = new Timer(DELAY,new AdjustListener(parent));
			parent.putClientProperty(LAYOUT_TIMER,timer);
		}
		timer.start();
	}

	public Dimension minimumLayoutSize(Container parent) {
		return preferredLayoutSize(parent);
	}

	public static final String DESTINATION = "AnimatedLayout.destinationRect";
	private static final String LAYOUT_TIMER = "AnimatedLayout.thread";
	public Dimension preferredLayoutSize(Container parent) {
		if(calculateMinimumSize==false)
			return new Dimension(5,5);
		Rectangle r = null;
		for(int a = 0; a<parent.getComponentCount(); a++) {
			JComponent c = (JComponent)parent.getComponent(a);
			Rectangle dest = (Rectangle)c.getClientProperty(AnimatedLayout.DESTINATION);
			if(dest==null) {
				Dimension d = c.getPreferredSize();
				//meh.  what can you do?
				dest = new Rectangle(0,0,d.width,d.height);
			}
			
			if(r==null) {
				r = (Rectangle)dest.clone();
			} else {
				r = r.union(dest);
			}
		}
		if(r==null) {
			return new Dimension(0,0);
		}
		return new Dimension(r.width,r.height);
	}

	public void removeLayoutComponent(Component comp) {}

}