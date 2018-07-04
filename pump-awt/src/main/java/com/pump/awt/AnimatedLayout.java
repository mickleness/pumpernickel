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
import java.awt.LayoutManager;
import java.awt.LayoutManager2;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * A layout that smoothly animates components as they need to move from one
 * location to another.
 * <p>
 * Each animated movement is proportional to the distance that the component has
 * to cover, giving the impression of a decelerating animation as the object
 * approaches its destination.
 *
 */
public abstract class AnimatedLayout implements LayoutManager2 {
	/**
	 * This AnimatedLayout relies on a client property {@link #DESTINATION}
	 */
	public static class ClientProperty extends AnimatedLayout {
		/**
		 * This client property for each child component maps to a Rectangle
		 * indicating the target bounds of the component.
		 */
		public static final String PROPERTY_DESTINATION = "AnimatedLayout.destinationRect";

		private PropertyChangeListener destinationListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				Component c = (JComponent) evt.getSource();
				JComponent parent = (JComponent) c.getParent();
				if (parent != null) {
					LayoutManager layout = parent.getLayout();
					if (layout == ClientProperty.this) {
						layout.layoutContainer(parent);
					} else {
						c.removePropertyChangeListener(PROPERTY_DESTINATION,
								this);
					}
				}
			}
		};

		@Override
		protected Map<JComponent, Rectangle> getDestinationMap(
				JComponent container) {
			Map<JComponent, Rectangle> map = new HashMap<>(
					container.getComponentCount());
			for (int a = 0; a < container.getComponentCount(); a++) {
				JComponent jc = (JComponent) container.getComponent(a);
				Rectangle rect = (Rectangle) jc
						.getClientProperty(PROPERTY_DESTINATION);
				if (rect == null) {
					Dimension d = jc.getPreferredSize();
					rect = new Rectangle(0, 0, d.width, d.height);
				}
				map.put(jc, rect);
			}
			return map;
		}

		@Override
		public void layoutContainer(Container parent) {
			super.layoutContainer(parent);
		}

		/**
		 * This client property for the container resolves to a
		 * ContainerListener.
		 */
		private static final String PROPERTY_CONTAINER_LISTENER = "animatedLayout.containerListener";

		protected boolean install(JComponent parent) {
			boolean returnValue = super.install(parent);
			if (returnValue) {
				ContainerListener l = (ContainerListener) parent
						.getClientProperty(PROPERTY_CONTAINER_LISTENER);
				if (l == null) {
					l = new ContainerAdapter() {
						@Override
						public void componentAdded(ContainerEvent e) {
							JComponent c = (JComponent) e.getComponent();
							registerChildren(c);
						}
					};
					parent.putClientProperty(PROPERTY_CONTAINER_LISTENER, l);
					parent.addContainerListener(l);
					registerChildren(parent);
				}
			}
			return returnValue;
		}

		@Override
		protected void uninstall(JComponent parent) {
			super.uninstall(parent);
			ContainerListener l = (ContainerListener) parent
					.getClientProperty(PROPERTY_CONTAINER_LISTENER);
			parent.putClientProperty(PROPERTY_CONTAINER_LISTENER, null);
			parent.removeContainerListener(l);
			for (Component c : parent.getComponents()) {
				((JComponent) c).putClientProperty(
						"animatedLayout.propertyListener", null);
				((JComponent) c).removePropertyChangeListener(
						PROPERTY_DESTINATION, destinationListener);
			}
		}

		private void registerChildren(JComponent c) {
			String key = "animatedLayout.propertyListener";
			for (int a = 0; a < c.getComponentCount(); a++) {
				JComponent child = (JComponent) c.getComponent(a);
				if (child.getClientProperty(key) == null) {
					child.putClientProperty(key, destinationListener);
					child.addPropertyChangeListener(PROPERTY_DESTINATION,
							destinationListener);
				}
			}
		}
	}

	/**
	 * This client property on the container resolves to a Timer used to animate
	 * the layout.
	 */
	private static final String PROPERTY_TIMER = "AnimatedLayout.timer";

	/**
	 * This client property for the parent maps to a Boolean indicating whether
	 * installation logic has ever been performed on the parent or not.
	 */
	private static final String PROPERTY_INITIALIZED = "AnimatedLayout#initialized";

	/**
	 * This client property for the parent maps to a Boolean indicating whether
	 * the layout should immediately (without animation) layout the container.
	 */
	private static final String PROPERTY_LAYOUT_IMMEDIATELY = "AnimatedLayout#layoutImmediately";

	/**
	 * This is the delay, in milliseconds, between adjustments.
	 */
	public static int DELAY = 25;

	ComponentListener componentListener = new ComponentAdapter() {

		@Override
		public void componentResized(ComponentEvent e) {
			JComponent jc = ((JComponent) e.getComponent());
			layoutContainerImmediately(jc);
		}

		@Override
		public void componentShown(ComponentEvent e) {
			JComponent jc = ((JComponent) e.getComponent());
			layoutContainerImmediately(jc);
		}

	};

	@Override
	public void addLayoutComponent(String name, Component comp) {
	}

	class AdjustListener implements ActionListener {
		JComponent container;

		public AdjustListener(JComponent container) {
			this.container = container;
		}

		public void actionPerformed(ActionEvent e) {
			Timer timer = (Timer) e.getSource();

			boolean workToDo = false;

			synchronized (container.getTreeLock()) {
				Map<JComponent, Rectangle> destinationMap = getDestinationMap(container);
				for (Entry<JComponent, Rectangle> entry : destinationMap
						.entrySet()) {
					if (nudge(entry.getKey(), entry.getValue()) == false) {
						workToDo = true;
					}
				}
			}
			// on Windows, Ladislav pointed out there's a repaint problem
			container.repaint();

			if (workToDo == false)
				timer.stop();
		}
	}

	private static double sign(double d) {
		if (d < 0)
			return -1;
		if (d > 0)
			return 1;
		return 0;
	}

	private static String PROPERTY_LAST_DX = "AnimatedLayout.lastDX";
	private static String PROPERTY_LAST_DY = "AnimatedLayout.lastDY";
	private static String PROPERTY_LAST_DW = "AnimatedLayout.lastDW";
	private static String PROPERTY_LAST_DH = "AnimatedLayout.lastDH";

	private static double getDouble(JComponent c, String key,
			double defaultValue) {
		Double d = (Double) c.getClientProperty(key);
		if (d == null)
			return defaultValue;
		return d.doubleValue();
	}

	private static double limit(double v1, double limit) {
		if (limit < 0) {
			return Math.max(limit, v1);
		}
		return Math.min(limit, v1);

	}

	/**
	 * Nudge a component towards the destination.
	 * 
	 * @param c
	 *            the component to nudge.
	 * @param dest
	 *            the target bounds for the component.
	 * @return true when the component is at the desired location
	 */
	protected static boolean nudge(JComponent c, Rectangle dest) {
		Rectangle bounds = c.getBounds();

		double lastDX = getDouble(c, PROPERTY_LAST_DX, 0);
		double lastDY = getDouble(c, PROPERTY_LAST_DY, 0);
		double lastDW = getDouble(c, PROPERTY_LAST_DW, 0);
		double lastDH = getDouble(c, PROPERTY_LAST_DH, 0);

		double dx = dest.x - bounds.x;
		double dy = dest.y - bounds.y;
		double dw = dest.width - bounds.width;
		double dh = dest.height - bounds.height;

		dx = limit(.5 * sign(dx) * Math.pow(Math.abs(dx), .7) + .5 * lastDX, dx);
		dy = limit(.5 * sign(dy) * Math.pow(Math.abs(dy), .7) + .5 * lastDY, dy);
		dw = limit(.5 * sign(dw) * Math.pow(Math.abs(dw), .7) + .5 * lastDW, dw);
		dh = limit(.5 * sign(dh) * Math.pow(Math.abs(dh), .7) + .5 * lastDH, dh);

		c.putClientProperty(PROPERTY_LAST_DX, new Double(dx));
		c.putClientProperty(PROPERTY_LAST_DY, new Double(dy));
		c.putClientProperty(PROPERTY_LAST_DW, new Double(dw));
		c.putClientProperty(PROPERTY_LAST_DH, new Double(dh));

		if (Math.abs(dx) < 1.2 && Math.abs(dy) < 1.2 && Math.abs(dw) < 1.2
				&& Math.abs(dh) < 1.2) {
			c.setBounds(dest);
			return true;
		}

		bounds.x += (int) (dx + .5);
		bounds.y += (int) (dy + .5);
		bounds.width += (int) (dw + .5);
		bounds.height += (int) (dh + .5);

		c.setBounds(bounds);

		return false;
	}

	@Override
	public void layoutContainer(Container parent) {
		JComponent jc = (JComponent) parent;
		install(jc);
		Timer timer = (Timer) jc.getClientProperty(PROPERTY_TIMER);
		Boolean layoutImmediately = (Boolean) jc
				.getClientProperty(PROPERTY_LAYOUT_IMMEDIATELY);
		if (layoutImmediately == null)
			layoutImmediately = false;
		if (layoutImmediately) {
			layoutContainerImmediately(jc);
			if (parent.isShowing())
				jc.putClientProperty(PROPERTY_LAYOUT_IMMEDIATELY, false);
		} else {
			if (!timer.isRunning())
				timer.start();
		}
	}

	protected void layoutContainerImmediately(JComponent parent) {
		synchronized (parent.getTreeLock()) {
			Map<JComponent, Rectangle> destMap = getDestinationMap(parent);
			for (Entry<JComponent, Rectangle> entry : destMap.entrySet()) {
				entry.getKey().setBounds(entry.getValue());
			}
		}
		parent.repaint();
	}

	protected boolean install(JComponent parent) {
		Boolean initialized = (Boolean) parent
				.getClientProperty(PROPERTY_INITIALIZED);
		if (initialized == null)
			initialized = Boolean.FALSE;
		if (!initialized) {
			Timer timer = new Timer(DELAY, new AdjustListener(parent));
			parent.putClientProperty(PROPERTY_TIMER, timer);
			parent.putClientProperty(PROPERTY_INITIALIZED, true);
			parent.addComponentListener(componentListener);
			parent.addHierarchyListener(new HierarchyListener() {

				@Override
				public void hierarchyChanged(HierarchyEvent e) {
					final JComponent parent = (JComponent) e.getComponent();
					if (parent.getLayout() == AnimatedLayout.this) {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								if (!parent.isShowing()) {
									// if we were hidden, then restore the
									// layout to a
									// "new" untouched state,
									// so the next time we're shown we don't
									// animate.
									parent.putClientProperty(
											PROPERTY_LAYOUT_IMMEDIATELY,
											Boolean.TRUE);
								}
							}
						});
					} else {
						// TODO: this isn't universally guaranteed to call
						// uninstall at the right time.
						uninstall(parent);
						parent.removeHierarchyListener(this);
					}
				}

			});
		}
		return !initialized;
	}

	protected void uninstall(JComponent parent) {
		parent.removeComponentListener(componentListener);
		parent.putClientProperty(PROPERTY_INITIALIZED, null);
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		return preferredLayoutSize(parent);
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		int width = 0;
		int height = 0;
		synchronized (parent.getTreeLock()) {
			Map<JComponent, Rectangle> destMap = getDestinationMap((JComponent) parent);
			for (Entry<JComponent, Rectangle> entry : destMap.entrySet()) {
				width = Math
						.max(width, (int) (entry.getValue().getMaxX() + .5));
				height = Math.max(height,
						(int) (entry.getValue().getMaxY() + .5));
			}
		}
		return new Dimension(width, height);
	}

	@Override
	public void removeLayoutComponent(Component comp) {
	}

	/**
	 * Return a map of components to their target size. This method should be
	 * called inside blocks of code synchronized against the container's tree
	 * lock.
	 * 
	 * @param container
	 * @return
	 */
	protected abstract Map<JComponent, Rectangle> getDestinationMap(
			JComponent container);

	@Override
	public void addLayoutComponent(Component comp, Object constraints) {
	}

	@Override
	public Dimension maximumLayoutSize(Container target) {
		return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}

	@Override
	public float getLayoutAlignmentX(Container target) {
		return .5f;
	}

	@Override
	public float getLayoutAlignmentY(Container target) {
		return .5f;
	}

	@Override
	public void invalidateLayout(Container target) {
	}
}