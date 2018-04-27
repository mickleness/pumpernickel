package com.pump.awt;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.LayoutManager;
import java.awt.MouseInfo;
import java.awt.Point;
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
		public synchronized void actionPerformed(ActionEvent e) {
			long elapsedTime = System.currentTimeMillis() - startTime;
			float f = ((float) elapsedTime) / ANIMATION_DURATION;
			if (f >= 1)
				f = 1;
			for (Entry<JComponent, Rectangle> entry : endingPositions
					.entrySet()) {
				Rectangle oldBounds = startingPositions.get(entry.getKey());
				Rectangle tweenBounds = tween(oldBounds, entry.getValue(), f);
				JComponent jc = entry.getKey();
				if (!jc.getBounds().equals(tweenBounds)) {
					jc.setBounds(tweenBounds);
					if (f == 1) {
						jc.invalidate();
						jc.revalidate();
					}
				}
			}
			if (f == 1 && e != null) {
				((Timer) e.getSource()).stop();
				container.putClientProperty(PROPERTY_TIMER, null);
				container.putClientProperty(PROPERTY_TIMER_LISTENER, null);
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
		synchronized boolean install(Plan plan, boolean installImmediately) {
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
						jc.putClientProperty(PROPERTY_IS_KNOWN, Boolean.TRUE);
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
				for (Entry<JComponent, Rectangle> entry : plan.map.entrySet()) {
					JComponent jc = entry.getKey();
					jc.setBounds(entry.getValue());
				}
				return false;
			}

			endingPositions.clear();
			endingPositions.putAll(plan.map);

			boolean noChange = startingPositions.equals(endingPositions);
			if (newComponent || !noChange) {
				// give it one iteration, especially for new components to jump
				// into place
				actionPerformed(null);
			}
			return !noChange;
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
		this.separatorSize = separatorSize == null ? new Dimension(0, 0)
				: new Dimension(separatorSize);
	}

	public void addLayoutComponent(String name, Component comp) {
	}

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
			plan = layoutContainerAtFixedSize((JComponent) parent,
					preferredSize, x, y, width, height);
		}
		plan.install();
	}

	/**
	 * This method is called when we don't have enough space to fit all our
	 * components and we have to try to arrange them in a fixed width.
	 */
	protected Plan layoutContainerAtFixedSize(JComponent parent,
			LinkedHashMap<JComponent, Dimension> preferredSize, int x, int y,
			int width, int height) {
		Collection<JComponent> emphasizedComponents = getEmphasizedComponents(parent);
		int separators = preferredSize.size() - 1;
		int emphasizedTotal = horizontal ? separators * separatorSize.width
				: separators * separatorSize.height;
		for (JComponent e : emphasizedComponents) {
			Dimension d = preferredSize.get(e);
			emphasizedTotal += horizontal ? d.width : d.height;
		}
		int leftover = Math.max(0, horizontal ? width - emphasizedTotal
				: height - emphasizedTotal);

		int minimumSum = 0;
		Map<JComponent, Dimension> minimumSize = new HashMap<>(
				preferredSize.size() - emphasizedComponents.size());
		for (JComponent jc : preferredSize.keySet()) {
			if (!emphasizedComponents.contains(jc)) {
				Dimension m = jc.getMinimumSize();
				minimumSize.put(jc, m);
				minimumSum += horizontal ? m.width : m.height;
			}
		}

		Plan plan = new Plan(parent, horizontal);

		// ideally we'll be have enough space to make sure every component gets
		// its minimum size
		boolean useMinimum = minimumSum < leftover;
		if (useMinimum)
			leftover = leftover - minimumSum;

		int ctr = 0;
		for (Entry<JComponent, Dimension> entry : preferredSize.entrySet()) {
			JComponent jc = entry.getKey();
			if (emphasizedComponents.contains(jc)) {
				if (horizontal) {
					int x2 = x + entry.getValue().width;
					plan.add(jc, x, y, x2, y + height);
					x = x2 + separatorSize.width;
				} else {
					int y2 = y + entry.getValue().height;
					plan.add(jc, x, y, x + width, y2);
					y = y2 + separatorSize.height;
				}
			} else {
				int z = leftover
						/ (preferredSize.size() - emphasizedComponents.size() - ctr);
				if (horizontal) {
					int x2 = x + z
							+ (useMinimum ? minimumSize.get(jc).width : 0);
					plan.add(jc, x, y, x2, y + height);
					x = x2 + separatorSize.width;
				} else {
					int y2 = y + z
							+ (useMinimum ? minimumSize.get(jc).height : 0);
					plan.add(jc, x, y, x + width, y2);
					y = y2 + separatorSize.height;
				}
				leftover -= z;
				ctr++;
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
			Point p = MouseInfo.getPointerInfo().getLocation();
			SwingUtilities.convertPointFromScreen(p, container);
			if (container.contains(p)) {
				Component child = container.getComponentAt(p);
				while (child != null) {
					if (child.getParent() == container) {
						returnValue.add((JComponent) child);
						break;
					}
					child = child.getParent();
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
			Component c = e.getComponent();
			if (c.getParent() == null
					|| c.getParent().getLayout() != SplayedLayout.this) {
				c.removeMouseListener(this);
			} else if (prioritizeRollover) {
				c.getParent().revalidate();
			}
		}

		@Override
		public void mouseExited(MouseEvent e) {
			mouseEntered(e);
		}

	};

	private FocusListener focusListener = new FocusListener() {

		@Override
		public void focusGained(FocusEvent e) {
			Component c = e.getComponent();
			if (c.getParent() == null
					|| c.getParent().getLayout() != SplayedLayout.this) {
				c.removeFocusListener(this);
			} else if (prioritizeFocus) {
				c.getParent().revalidate();
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
		}

		for (JComponent child : children) {
			if (!Arrays.asList(child.getMouseListeners()).contains(
					mouseListener))
				child.addMouseListener(mouseListener);
			if (!Arrays.asList(child.getFocusListeners()).contains(
					focusListener))
				child.addFocusListener(focusListener);
		}

		return children;
	}

}