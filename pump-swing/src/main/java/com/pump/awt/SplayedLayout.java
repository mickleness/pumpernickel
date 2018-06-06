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
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.Border;

/**
 * This is an animated layout that, when space is limited, will prioritize a few
 * components and deprioritize others. Which components are prioritized is
 * usually a function of which components the user has positioned the mouse over
 * or which components have the keyboard focus. (Although you can override
 * {@link #getEmphasizedComponents(JComponent)} to implement your own behavior.)
 */
public class SplayedLayout implements LayoutManager {

	private static final String PROPERTY_TIMER = SplayedLayout.class.getName()
			+ "#timer";
	private static final String PROPERTY_TIMER_LISTENER = SplayedLayout.class
			.getName() + "#timerListener";
	private static final String PROPERTY_LAST_PLAN_INFO = SplayedLayout.class
			.getName() + "#lastPlanInfo";
	private static final String PROPERTY_IS_KNOWN = SplayedLayout.class
			.getName() + "#isKnown";
	private static final String PROPERTY_ROLLOVER_CHILD = SplayedLayout.class
			.getName() + "#rolloverChild";

	private static class RemoveKnownAttributeListener implements
			ComponentListener, ContainerListener {

		JComponent c;
		Container parent;

		RemoveKnownAttributeListener(JComponent c) {
			this.c = c;
			parent = c.getParent();
		}

		@Override
		public void componentAdded(ContainerEvent e) {
		}

		@Override
		public void componentRemoved(ContainerEvent e) {
			if (e.getChild() == c)
				uninstall();
		}

		@Override
		public void componentResized(ComponentEvent e) {
		}

		@Override
		public void componentMoved(ComponentEvent e) {
		}

		@Override
		public void componentShown(ComponentEvent e) {
		}

		@Override
		public void componentHidden(ComponentEvent e) {
			uninstall();
		}

		private void uninstall() {
			c.putClientProperty(PROPERTY_IS_KNOWN, null);
			c.removeComponentListener(this);
			parent.removeContainerListener(this);
		}

	}

	private static class TimerListener implements ActionListener {
		/** The duration in milliseconds of the animations */
		final float ANIMATION_DURATION = 50;
		Map<JComponent, Rectangle> startingPositions = new HashMap<>();
		Map<JComponent, Rectangle> endingPositions = new HashMap<>();
		JComponent container;
		long startTime = -1;

		public TimerListener(JComponent container) {
			this.container = container;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			synchronized (container.getTreeLock()) {
				long elapsedTime = System.currentTimeMillis() - startTime;
				float f = ((float) elapsedTime) / ANIMATION_DURATION;
				if (f >= 1)
					f = 1;
				boolean containerDirty = false;
				for (Entry<JComponent, Rectangle> entry : endingPositions
						.entrySet()) {
					Rectangle oldBounds = startingPositions.get(entry.getKey());
					Rectangle tweenBounds = tween(oldBounds, entry.getValue(),
							f);
					JComponent jc = entry.getKey();
					if (!jc.getBounds().equals(tweenBounds)) {
						jc.setBounds(tweenBounds);
						if (f == 1) {
							jc.invalidate();
							jc.revalidate();
						}
						containerDirty = true;
					}
				}
				if (containerDirty)
					container.repaint();
				if (f == 1 && e != null) {
					((Timer) e.getSource()).stop();
					container.putClientProperty(PROPERTY_TIMER, null);
					container.putClientProperty(PROPERTY_TIMER_LISTENER, null);
				}
			}
		}

		private Rectangle tween(Rectangle r1, Rectangle r2, float f) {
			return new Rectangle((int) ((r1.x) * (1 - f) + (r2.x * f)),
					(int) ((r1.y) * (1 - f) + (r2.y * f)), (int) ((r1.width)
							* (1 - f) + (r2.width * f)), (int) ((r1.height)
							* (1 - f) + (r2.height * f)));
		}

		/**
		 * Return true if the timer should restart because an animation is
		 * appropriate.
		 */
		boolean install(Plan plan, boolean installImmediately) {
			synchronized (container.getTreeLock()) {
				startTime = System.currentTimeMillis();
				startingPositions.clear();
				Component[] components = container.getComponents();
				boolean newComponent = false;
				for (int a = 0; a < components.length; a++) {
					Component component = components[a];
					if (component instanceof JComponent) {
						JComponent jc = (JComponent) component;
						Boolean isKnown = (Boolean) jc
								.getClientProperty(PROPERTY_IS_KNOWN);
						if (isKnown == null) {
							// completely new components just snap into place
							// (to tween them we'd need a starting and ending
							// position: where would they start from?)
							Rectangle newBounds = plan.map.get(jc);
							startingPositions.put(jc, newBounds);
							jc.putClientProperty(PROPERTY_IS_KNOWN,
									Boolean.TRUE);
							RemoveKnownAttributeListener z = new RemoveKnownAttributeListener(
									jc);
							jc.addComponentListener(z);
							container.addContainerListener(z);
							newComponent = true;
						} else {
							startingPositions.put(jc, component.getBounds());
						}
					}
				}

				if (installImmediately) {
					for (Entry<JComponent, Rectangle> entry : plan.map
							.entrySet()) {
						JComponent jc = entry.getKey();
						jc.setBounds(entry.getValue());
					}
					return false;
				}

				endingPositions.clear();
				endingPositions.putAll(plan.map);

				boolean noChange = startingPositions.equals(endingPositions);
				if (newComponent || !noChange) {
					// give it one iteration, especially for new components to
					// jump into place
					actionPerformed(null);
				}
				return !noChange;
			}
		}

	}

	/**
	 * This contains crucial information about a Container. Only small changes
	 * (like when the user rolls the mouse over a component) should be animated;
	 * big changes (like changes made to this object) don't require animation.
	 */
	private static class PlanInfo {
		boolean horizontal;
		Rectangle containerBounds;

		PlanInfo(boolean horizontal, Rectangle containerBounds) {
			this.horizontal = horizontal;
			this.containerBounds = containerBounds;
		}

		@Override
		public int hashCode() {
			return containerBounds.hashCode() + (horizontal ? 1 : 0);
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof PlanInfo))
				return false;
			PlanInfo other = (PlanInfo) obj;
			if (other.horizontal != horizontal)
				return false;
			if (!containerBounds.equals(other.containerBounds))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "PlanInfo[" + (horizontal ? "horizontal" : "vertical")
					+ ", " + containerBounds + "]";
		}
	}

	private static class Plan {
		PlanInfo info;
		JComponent container;
		Map<JComponent, Rectangle> map = new HashMap<>();

		Plan(JComponent container, boolean horizontal) {
			this.container = container;
			info = new PlanInfo(horizontal, container.getBounds());
		}

		public void install() {
			PlanInfo lastPlanInfo = (PlanInfo) container
					.getClientProperty(PROPERTY_LAST_PLAN_INFO);
			boolean immediateRefresh = (!container.isShowing())
					|| !info.equals(lastPlanInfo);
			container.putClientProperty(PROPERTY_LAST_PLAN_INFO, info);

			Timer timer = (Timer) container.getClientProperty(PROPERTY_TIMER);
			TimerListener timerListener = (TimerListener) container
					.getClientProperty(PROPERTY_TIMER_LISTENER);
			if (timer == null) {
				timerListener = new TimerListener(container);
				timer = new Timer(5, timerListener);
				container.putClientProperty(PROPERTY_TIMER, timer);
				container.putClientProperty(PROPERTY_TIMER_LISTENER,
						timerListener);
			}

			if (timerListener.install(this, immediateRefresh))
				timer.restart();
		}

		public void add(JComponent component, int x, int y, int x2, int y2) {
			map.put(component, new Rectangle(x, y, x2 - x, y2 - y));
		}
	}

	/**
	 * The amount of padding that is always between components.
	 */
	Dimension separatorSize;

	/**
	 * True if this layout is oriented horizontally, false if its is oriented
	 * vertically.
	 */
	boolean horizontal;
	boolean prioritizeRollover;
	boolean prioritizeFocus;
	boolean stretchToFill;

	/**
	 * Create a new horizontal SplayedLayout with no padding between components
	 * that prioritizes focused or rolled-over components.
	 * 
	 * @param stretchToFill
	 *            when the children at their preferred size do not fill all the
	 *            available space in the container, then boolean ins used to
	 *            decide how to use the extra space. If this is false:
	 *            components are pushed the left/top of the container (depending
	 *            on the orientation). If this is true then components are
	 *            stretched wider/taller to fill all available space.
	 */
	public SplayedLayout(boolean stretchToFill) {
		this(SwingConstants.HORIZONTAL, null, true, true, stretchToFill);
	}

	/**
	 * 
	 * @param orientation
	 *            SwingConstants.HORIZONTAL or SwingConstants.VERTICAL
	 * @param separatorSize
	 *            an optional padding to insert between all splayed elements.
	 *            This may be used in JBreadCrumbs to paint a separator arrow or
	 *            pipe.
	 * @param prioritizeRollover
	 * @param prioritizeFocus
	 * @param stretchToFill
	 *            when the children at their preferred size do not fill all the
	 *            available space in the container, then boolean ins used to
	 *            decide how to use the extra space. If this is false:
	 *            components are pushed the left/top of the container (depending
	 *            on the orientation). If this is true then components are
	 *            stretched wider/taller to fill all available space.
	 */
	public SplayedLayout(int orientation, Dimension separatorSize,
			boolean prioritizeRollover, boolean prioritizeFocus,
			boolean stretchToFill) {
		setOrientation(null, orientation);
		this.prioritizeRollover = prioritizeRollover;
		this.prioritizeFocus = prioritizeFocus;
		this.stretchToFill = stretchToFill;
		setSeparatorSize(separatorSize);
	}

	/**
	 * @param newSeparatorSize
	 *            an optional padding to insert between all splayed elements.
	 *            This may be used in JBreadCrumbs to paint a separator arrow or
	 *            pipe.
	 * @return true if this changed the separator size, false otherwise.
	 */
	public boolean setSeparatorSize(Dimension newSeparatorSize) {
		if (newSeparatorSize == null)
			newSeparatorSize = new Dimension(0, 0);
		if (newSeparatorSize.equals(separatorSize))
			return false;

		separatorSize = new Dimension(newSeparatorSize);
		return true;
	}

	@Override
	public void addLayoutComponent(String name, Component comp) {
	}

	@Override
	public void removeLayoutComponent(Component comp) {
	}

	/**
	 * Set the orientation of this layout.
	 * 
	 * @param container
	 *            this optional Container will be revalidated.
	 * @param orientation
	 *            the new orientation (SwingConstants.HORIZONTAL or
	 *            SwingConstants.VERTICAL)
	 */
	public void setOrientation(Container container, int orientation) {
		if (!(orientation == SwingConstants.HORIZONTAL || orientation == SwingConstants.VERTICAL))
			throw new IllegalArgumentException(
					"Orientation must be SwingConstants.HORIZONTAL or SwingConstants.VERTICAL.");
		boolean h = orientation == SwingConstants.HORIZONTAL;
		if (horizontal != h) {
			horizontal = h;
			if (container != null)
				container.revalidate();
		}
	}

	public int getOrientation() {
		return horizontal ? SwingConstants.HORIZONTAL : SwingConstants.VERTICAL;
	}

	@Override
	public Dimension preferredLayoutSize(Container parent) {
		return getPreferredOrMinimumLayoutSize((JComponent) parent, true);
	}

	@Override
	public Dimension minimumLayoutSize(Container parent) {
		return getPreferredOrMinimumLayoutSize((JComponent) parent, false);
	}

	/**
	 * Calculate the preferred or minimum size of a container.
	 * 
	 * @param parent
	 *            the container to examine the contents of.
	 * @param preferred
	 *            if true we calculated preferred size, if false we calculate
	 *            minimum size.
	 * @return the preferred or minimum size of the parent argument.
	 */
	private Dimension getPreferredOrMinimumLayoutSize(JComponent parent,
			boolean preferred) {
		JComponent[] components = getComponents(parent);

		Dimension d;
		if (horizontal) {
			d = new Dimension(components.length * separatorSize.width, 0);
			for (Component component : components) {
				Dimension size = preferred ? component.getPreferredSize()
						: component.getMinimumSize();
				d.width += size.width;
				d.height = Math.max(d.height, size.height);
			}
			d.height = Math.max(d.height, separatorSize.height);
		} else {
			d = new Dimension(0, components.length * separatorSize.height);
			for (Component component : components) {
				Dimension size = preferred ? component.getPreferredSize()
						: component.getMinimumSize();
				d.height += size.height;
				d.width = Math.max(d.width, size.width);
			}
			d.width = Math.max(d.width, separatorSize.width);
		}

		Border border = ((JComponent) parent).getBorder();
		if (border != null) {
			Insets i = border.getBorderInsets(parent);
			d.width += i.left + i.right;
			d.height += i.top + i.bottom;
		}
		return d;
	}

	@Override
	public void layoutContainer(Container parent) {
		JComponent[] components = getComponents(parent);
		if (components.length == 0) {
			return;
		}

		Border border = ((JComponent) parent).getBorder();
		Insets i = border == null ? new Insets(0, 0, 0, 0) : border
				.getBorderInsets(parent);
		int x = i.left;
		int y = i.top;
		int width = parent.getWidth() - i.left - i.right;
		int height = parent.getHeight() - i.top - i.bottom;

		int separators = components.length - 1;
		int requestedSize = horizontal ? separators * separatorSize.width
				: separators * separatorSize.height;
		LinkedHashMap<JComponent, Dimension> preferredSize = new LinkedHashMap<>(
				components.length);
		for (JComponent c : components) {
			Dimension d = c.getPreferredSize();
			preferredSize.put(c, d);
			requestedSize += horizontal ? d.width : d.height;
		}

		int max = horizontal ? width : height;
		Plan plan;
		if (requestedSize <= max) {
			plan = layoutContainerWithExtra((JComponent) parent, preferredSize,
					requestedSize, x, y, width, height);
		} else {
			plan = layoutContainerWithConstrainedSize((JComponent) parent,
					preferredSize, x, y, width, height);
		}
		plan.install();
	}

	/**
	 * This method is called when we don't have enough space to fit all our
	 * components and we have to try to arrange them in a fixed width.
	 */
	protected Plan layoutContainerWithConstrainedSize(JComponent parent,
			LinkedHashMap<JComponent, Dimension> preferredSize, int x, int y,
			int width, int height) {
		Collection<JComponent> emphasizedComponents = getEmphasizedComponents(parent);
		int separatorCount = preferredSize.size() - 1;
		int remainingSpace = horizontal ? width - separatorCount
				* separatorSize.width : height - separatorCount
				* separatorSize.height;

		Plan plan = new Plan(parent, horizontal);

		Map<JComponent, Dimension> finalSize = new HashMap<>(
				preferredSize.size());
		Collection<JComponent> remainingComponents = new HashSet<>(
				preferredSize.keySet());

		// these chosen few get their requested size
		for (JComponent jc : emphasizedComponents) {
			Dimension d = preferredSize.get(jc);
			remainingSpace -= horizontal ? d.width : d.height;
			finalSize.put(jc, d);
			remainingComponents.remove(jc);
		}

		// now see which other components are conveniently small enough to
		// get by on their own:
		boolean repeat;
		do {
			int maxSpace = remainingSpace / remainingComponents.size();
			repeat = false;

			Iterator<JComponent> iter = remainingComponents.iterator();
			while (iter.hasNext()) {
				JComponent jc = iter.next();
				Dimension d = preferredSize.get(jc);
				int requestedSpace = horizontal ? d.width : d.height;
				if (requestedSpace < maxSpace) {
					remainingSpace -= horizontal ? d.width : d.height;
					finalSize.put(jc, d);
					iter.remove();
					repeat = true;
				}
			}
		} while (repeat);

		// and lastly: some components may just not get all the size they want.
		Iterator<JComponent> iter = remainingComponents.iterator();
		while (iter.hasNext()) {
			JComponent jc = iter.next();
			int maxSpace = remainingSpace / remainingComponents.size();
			Dimension d = preferredSize.get(jc);
			int requestedSpace = horizontal ? d.width : d.height;
			int componentSpace = Math.min(requestedSpace, maxSpace);

			Dimension d2 = horizontal ? new Dimension(componentSpace, d.height)
					: new Dimension(d.width, componentSpace);

			remainingSpace -= componentSpace;
			finalSize.put(jc, d2);
			iter.remove();
		}

		// now finalSize covers everything, we just have to lay them out
		// consecutively:

		// remember preferredSize is ordered, so that helps:
		for (JComponent jc : preferredSize.keySet()) {
			Dimension d = finalSize.get(jc);
			if (horizontal) {
				int x2 = x + d.width;
				plan.add(jc, x, y, x2, y + height);
				x = x2 + separatorSize.width;
			} else {
				int y2 = y + d.height;
				plan.add(jc, x, y, x + width, y2);
				y = y2 + separatorSize.height;
			}
		}

		return plan;
	}

	protected Collection<JComponent> getEmphasizedComponents(
			JComponent container) {
		Collection<JComponent> returnValue = new HashSet<>();
		if (prioritizeFocus) {
			Component focusOwner = KeyboardFocusManager
					.getCurrentKeyboardFocusManager().getFocusOwner();
			while (focusOwner != null) {
				if (focusOwner.getParent() == container) {
					returnValue.add((JComponent) focusOwner);
					break;
				}
				focusOwner = focusOwner.getParent();
			}
		}
		if (prioritizeRollover) {
			JComponent child = (JComponent) container
					.getClientProperty(PROPERTY_ROLLOVER_CHILD);
			if (child != null) {
				if (SwingUtilities.isDescendingFrom(child, container)) {
					while (child != null) {
						if (child.getParent() == container) {
							returnValue.add((JComponent) child);
							break;
						}
						child = (JComponent) child.getParent();
					}
				} else {
					// I haven't seen this happen, but just in case:
					container.putClientProperty(PROPERTY_ROLLOVER_CHILD, null);
				}
			}
		}
		return returnValue;
	}

	/**
	 * This method is called when we have extra space leftover to distribute
	 * among components.
	 */
	protected Plan layoutContainerWithExtra(JComponent parent,
			LinkedHashMap<JComponent, Dimension> preferredSize,
			int requestedSize, int x, int y, int width, int height) {
		Plan plan = new Plan(parent, horizontal);

		if (!stretchToFill) {
			// scenario #1: we just dump everything in. This is the easiest case
			for (Entry<JComponent, Dimension> entry : preferredSize.entrySet()) {
				if (horizontal) {
					plan.add(entry.getKey(), x, y, x + entry.getValue().width,
							y + height);
					x += entry.getValue().width;
					x += separatorSize.width;
				} else {
					plan.add(entry.getKey(), x, y, x + width,
							y + entry.getValue().height);
					y += entry.getValue().height;
					y += separatorSize.height;
				}
			}
			return plan;
		}

		// we have to stretch to fill the available space.

		int largestPreferredSize = 0;
		int totalPreferredSize = horizontal ? (preferredSize.size() - 1)
				* separatorSize.width : (preferredSize.size() - 1)
				* separatorSize.height;
		for (Dimension d : preferredSize.values()) {
			largestPreferredSize = Math.max(largestPreferredSize,
					horizontal ? d.width : d.height);
			totalPreferredSize += horizontal ? d.width : d.height;
		}

		int max = horizontal ? width - (preferredSize.size() - 1)
				* separatorSize.width : height - (preferredSize.size() - 1)
				* separatorSize.height;
		double equalDistribution = ((double) max)
				/ ((double) preferredSize.size());

		if (largestPreferredSize < equalDistribution) {
			// scenario #2: everything can fit in equally-sized slices

			int ctr = 0;
			for (Entry<JComponent, Dimension> entry : preferredSize.entrySet()) {
				if (horizontal) {
					int x1 = (int) (x + equalDistribution * ctr + .5) + ctr
							* separatorSize.width;
					int x2 = (int) (x + equalDistribution * (ctr + 1) + .5)
							+ ctr * separatorSize.width;
					plan.add(entry.getKey(), x1, y, x2, y + height);
				} else {
					int y1 = (int) (y + equalDistribution * ctr + .5) + ctr
							* separatorSize.height;
					int y2 = (int) (y + equalDistribution * (ctr + 1) + .5)
							+ ctr * separatorSize.height;
					plan.add(entry.getKey(), x, y1, x + width, y2);
				}
				ctr++;
			}
			return plan;
		}

		// scenario #3: everyone gets a few extra pixels, but components may be
		// unique sizes
		int extra = max - totalPreferredSize;

		int ctr = 0;
		for (Entry<JComponent, Dimension> entry : preferredSize.entrySet()) {
			int z = extra / (preferredSize.size() - ctr);
			if (horizontal) {
				int x2 = x + entry.getValue().width + z;
				plan.add(entry.getKey(), x, y, x2, y + height);
				x = x2 + separatorSize.width;
			} else {
				int y2 = y + entry.getValue().height + z;
				plan.add(entry.getKey(), x, y, x + width, y2);
				y = y2 + separatorSize.height;
			}
			extra -= z;
			ctr++;
		}

		return plan;
	}

	private MouseListener mouseListener = new MouseAdapter() {

		@Override
		public void mouseEntered(MouseEvent e) {
			process(e);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			process(e);
		}

		private void process(MouseEvent e) {
			Component c = e.getComponent();
			JComponent splayedAncestor = (JComponent) getSplayedAncestor(c);
			if (splayedAncestor == null) {
				c.removeMouseListener(this);
			} else if (prioritizeRollover) {
				if (e.getID() == MouseEvent.MOUSE_ENTERED
						&& splayedAncestor
								.getClientProperty(PROPERTY_ROLLOVER_CHILD) != c) {
					splayedAncestor.putClientProperty(PROPERTY_ROLLOVER_CHILD,
							c);
					splayedAncestor.revalidate();
				} else if (e.getID() == MouseEvent.MOUSE_EXITED
						&& splayedAncestor
								.getClientProperty(PROPERTY_ROLLOVER_CHILD) == c) {
					splayedAncestor.putClientProperty(PROPERTY_ROLLOVER_CHILD,
							null);
					splayedAncestor.revalidate();
				}
			}
		}
	};

	private FocusListener focusListener = new FocusListener() {

		@Override
		public void focusGained(FocusEvent e) {
			Component c = e.getComponent();
			Container splayedAncestor = getSplayedAncestor(c);
			if (splayedAncestor == null) {
				c.removeFocusListener(this);
			} else if (prioritizeFocus) {
				splayedAncestor.revalidate();
			}
		}

		@Override
		public void focusLost(FocusEvent e) {
			focusGained(e);
		}

	};

	protected JComponent[] getComponents(Container container) {
		JComponent[] children = new JComponent[container.getComponentCount()];
		for (int a = 0; a < children.length; a++) {
			children[a] = (JComponent) container.getComponent(a);
			addListeners(children[a]);
		}
		return children;
	}

	private void addListeners(Component c) {
		if (!Arrays.asList(c.getMouseListeners()).contains(mouseListener))
			c.addMouseListener(mouseListener);
		if (!Arrays.asList(c.getFocusListeners()).contains(focusListener))
			c.addFocusListener(focusListener);

		if (c instanceof Container) {
			Container c2 = (Container) c;
			for (int a = 0; a < c2.getComponentCount(); a++) {
				addListeners(c2.getComponent(a));
			}
		}
	}

	/**
	 * Return the ancestor of the argument that uses this SplayedLayout.
	 */
	protected Container getSplayedAncestor(Component c) {
		while (c != null) {
			if (c.getParent() != null
					&& c.getParent().getLayout() == SplayedLayout.this)
				return c.getParent();
			c = c.getParent();
		}
		return null;
	}
}