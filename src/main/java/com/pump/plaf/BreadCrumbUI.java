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
package com.pump.plaf;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;

import com.pump.awt.SplayedLayout;
import com.pump.icon.IconUtils;
import com.pump.icon.TriangleIcon;
import com.pump.swing.JBreadCrumb;
import com.pump.swing.JBreadCrumb.BreadCrumbFormatter;
import com.pump.swing.NavigationListener;
import com.pump.swing.NavigationListener.ListSelectionType;

/**
 * The ComponentUI for {@link com.pump.swing.JBreadCrumb}.
 */
public class BreadCrumbUI extends ComponentUI {

	private static class BreadCrumbLayout extends SplayedLayout {
		BreadCrumbLayout(Dimension iconSize) {
			super(SwingConstants.HORIZONTAL, iconSize, true, true, false);
		}

		@Override
		public Dimension preferredLayoutSize(Container parent) {
			JBreadCrumb<?> jbc = (JBreadCrumb<?>) parent;
			Icon icon = jbc.getUI().getSeparatorIcon(jbc);
			Dimension d = super.preferredLayoutSize(parent);
			d.height = Math.max(d.height, icon.getIconHeight());
			return d;
		}

		@Override
		public Dimension minimumLayoutSize(Container parent) {
			JBreadCrumb<?> jbc = (JBreadCrumb<?>) parent;
			Icon icon = jbc.getUI().getSeparatorIcon(jbc);
			Dimension d = super.minimumLayoutSize(parent);
			d.height = Math.max(d.height, icon.getIconHeight());
			return d;
		}

		@Override
		protected Collection<JComponent> getEmphasizedComponents(
				JComponent container) {
			Collection<JComponent> returnValue = super
					.getEmphasizedComponents(container);
			if (returnValue.isEmpty()) {
				// when the super has nothing to emphasize, try to emphasize
				// the last thing the user touched. For ex: if they rolled
				// the mouse over a label and then left the container, keep
				// the last thing they rolled over emphasized, just for
				// continuity.
				List<JComponent> lastEmphasized = (List<JComponent>) container
						.getClientProperty(PROPERTY_LAST_EMPHASIZED);
				if (lastEmphasized != null) {
					returnValue = new ArrayList<>();
					List children = Arrays.asList(container.getComponents());
					for (JComponent jc : lastEmphasized) {
						if (children.contains(jc)) {
							returnValue.add(jc);
						}
					}
				}

				// if that failed, grab the last node in the path.
				if (returnValue.isEmpty() && container.getComponentCount() > 0) {
					JComponent last = (JComponent) container
							.getComponent(container.getComponentCount() - 1);
					returnValue = Arrays.asList(last);
				}
			}
			container.putClientProperty(PROPERTY_LAST_EMPHASIZED,
					new ArrayList<>(returnValue));
			return returnValue;
		}

	};

	protected static final String PATH_NODE_KEY = BreadCrumbUI.class.getName()
			+ ".pathNode";
	protected static final String PATH_NODE_INDEX_KEY = BreadCrumbUI.class
			.getName() + ".pathNodeIndex";

	/**
	 * This client property of a <code>JBreadCrumb</code> defines the icon used
	 * as a separator.
	 */
	public static final String PROPERTY_SEPARATOR_ICON = BreadCrumbUI.class
			.getName() + ".separatorIcon";

	private static final String PROPERTY_LAST_EMPHASIZED = BreadCrumbUI.class
			.getName() + ".lastEmphasized";

	public static final Icon DEFAULT_SEPARATOR_ICON = IconUtils
			.createPaddedIcon(new TriangleIcon(SwingConstants.EAST, 6, 6),
					new Insets(2, 4, 2, 6));

	protected Icon defaultSeparatorIcon = DEFAULT_SEPARATOR_ICON;
	ContainerMouseListener mouseListener = new ContainerMouseListener();

	/** Create a new BreadCrumbUI. */
	public BreadCrumbUI() {
	}

	/**
	 * Return the labels that exactly correspond to <code>jbc.getPath()</code>
	 * in the analogous order.
	 */
	protected static JLabel[] getCrumbs(JBreadCrumb<?> jbc) {
		TreeMap<Integer, JLabel> map = new TreeMap<Integer, JLabel>();
		for (int a = 0; a < jbc.getComponentCount(); a++) {
			Component c = jbc.getComponent(a);
			if (c instanceof JLabel) {
				JLabel label = (JLabel) c;
				Integer i = (Integer) label
						.getClientProperty(PATH_NODE_INDEX_KEY);
				if (i != null) {
					map.put(i, label);
				}
			}
		}
		JLabel[] array = new JLabel[map.size()];
		int ctr = 0;
		for (Integer key : map.keySet()) {
			array[ctr++] = map.get(key);
		}
		return array;
	}

	PropertyChangeListener refreshUIListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			JBreadCrumb<?> comp = (JBreadCrumb<?>) evt.getSource();
			refreshUI(comp);
		}
	};

	class ContainerMouseListener<T> extends MouseAdapter {

		private JLabel getLabel(Container parent, Point p) {
			for (int a = 0; a < parent.getComponentCount(); a++) {
				Component comp = parent.getComponent(a);
				if (comp.getBounds().contains(p) && comp instanceof JLabel)
					return (JLabel) comp;
			}
			return null;
		}

		@Override
		public void mouseExited(MouseEvent e) {
			JBreadCrumb jbc = (JBreadCrumb) e.getComponent();
			jbc.putClientProperty(PROPERTY_LAST_EMPHASIZED, null);
			refreshUI(jbc);
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			JBreadCrumb jbc = (JBreadCrumb) e.getComponent();
			JLabel label = getLabel(jbc, e.getPoint());
			if (label == null)
				return;

			List<NavigationListener<T>> listeners = jbc
					.getNavigationListeners();
			for (int a = 0; a < listeners.size(); a++) {
				NavigationListener<T> listener = listeners.get(a);
				ListSelectionType type = e.getClickCount() == 2 ? ListSelectionType.DOUBLE_CLICK
						: ListSelectionType.SINGLE_CLICK;
				T element = (T) label.getClientProperty(PATH_NODE_KEY);

				// stupid ClassCastException and varargs require a little fancy
				// footwork here:
				T[] array = (T[]) Array.newInstance(element.getClass(), 1);
				array[0] = element;

				listener.elementsSelected(type, array);
			}

		}
	};

	public void setDefaultSeparatorIcon(Icon icon) {
		if (icon == null)
			throw new NullPointerException();
		defaultSeparatorIcon = icon;
	}

	/** Return the icon the argument wants to use for separators. */
	protected Icon getSeparatorIcon(JBreadCrumb<?> comp) {
		Icon icon = (Icon) comp.getClientProperty(PROPERTY_SEPARATOR_ICON);
		if (icon == null)
			return defaultSeparatorIcon;
		return icon;
	}

	/**
	 * Return the crumb at a given location.
	 * 
	 * @param comp
	 *            the JBreadCrumb
	 * @param p
	 *            a point relative to the JBreadCrumb
	 * @return the crumb at the given point, or null.
	 */
	public <E> E getCrumb(JBreadCrumb<E> comp, Point p) {
		for (int a = 0; a < comp.getComponentCount(); a++) {
			Component c = comp.getComponent(a);
			if (c.getBounds().contains(p) && c instanceof JLabel
					&& ((JLabel) c).getClientProperty(PATH_NODE_KEY) != null) {
				JLabel l = (JLabel) c;
				return (E) l.getClientProperty(PATH_NODE_KEY);
			}
		}
		return null;
	}

	protected <E> void refreshUI(final JBreadCrumb<E> comp) {
		E[] path = comp.getPath();
		Icon separatorIcon = getSeparatorIcon(comp);
		Dimension iconSize = new Dimension(separatorIcon.getIconWidth(),
				separatorIcon.getIconHeight());
		BreadCrumbLayout bcl;
		if (comp.getLayout() instanceof BreadCrumbLayout) {
			bcl = (BreadCrumbLayout) comp.getLayout();
			bcl.setSeparatorSize(iconSize);
		} else {
			bcl = new BreadCrumbLayout(iconSize);
			comp.setLayout(bcl);
		}

		BreadCrumbFormatter<E> formatter = comp.getFormatter();

		int ctr = 0;
		Set<JLabel> componentsToRemove = new HashSet<JLabel>();
		for (int a = 0; a < comp.getComponentCount(); a++) {
			Component c = comp.getComponent(a);
			if (c instanceof JLabel
					&& ((JLabel) c).getClientProperty(PATH_NODE_KEY) != null) {
				JLabel l = (JLabel) c;
				if (path != null && ctr < path.length) {
					l.putClientProperty(PATH_NODE_KEY, path[ctr]);
					l.putClientProperty(PATH_NODE_INDEX_KEY, ctr);
					formatter.format(comp, l, path[ctr], ctr);
					ctr++;
				} else {
					l.setSize(l.getPreferredSize());
					componentsToRemove.add(l);
				}
			}
		}

		for (JLabel l : componentsToRemove) {
			comp.remove(l);
		}
		while (path != null && ctr < path.length) {
			JLabel newLabel = new JLabel();
			newLabel.putClientProperty(PATH_NODE_KEY, path[ctr]);
			newLabel.putClientProperty(PATH_NODE_INDEX_KEY, ctr);
			formatter.format(comp, newLabel, path[ctr], ctr);
			comp.add(newLabel);
			newLabel.setSize(newLabel.getPreferredSize());
			ctr++;
		}

		comp.invalidate();
		comp.revalidate();
		comp.repaint();
	}

	@Override
	public Dimension getPreferredSize(JComponent c) {
		Dimension d = super.getPreferredSize(c);
		if (d != null)
			d.height = Math.max(d.height, defaultSeparatorIcon.getIconHeight());
		return d;
	}

	public static ComponentUI createUI(JComponent c) {
		return new BreadCrumbUI();
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);

		c.addMouseListener(mouseListener);
		c.addPropertyChangeListener(JBreadCrumb.PATH_KEY, refreshUIListener);
		c.addPropertyChangeListener(JBreadCrumb.FORMATTER_KEY,
				refreshUIListener);
		c.addPropertyChangeListener(PROPERTY_SEPARATOR_ICON, refreshUIListener);
		refreshUI((JBreadCrumb<?>) c);
	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);

		c.removeMouseListener(mouseListener);
		c.removePropertyChangeListener(JBreadCrumb.PATH_KEY, refreshUIListener);
		c.removePropertyChangeListener(JBreadCrumb.FORMATTER_KEY,
				refreshUIListener);
		c.removePropertyChangeListener(PROPERTY_SEPARATOR_ICON,
				refreshUIListener);
	}

	/**
	 * Paint a triangle between every label.
	 * 
	 */
	@Override
	public void paint(Graphics g, JComponent c) {
		super.paint(g, c);
		JBreadCrumb<?> jbc = (JBreadCrumb<?>) c;
		Icon separatorIcon = getSeparatorIcon(jbc);
		JLabel[] crumbs = getCrumbs(jbc);
		Insets insets = new Insets(0, 0, 0, 0);
		Border b = c.getBorder();
		if (b != null)
			insets = b.getBorderInsets(c);
		for (int a = 0; a < crumbs.length - 1; a++) {
			Rectangle r = crumbs[a].getBounds();

			int x = r.x + r.width;
			int y = (c.getHeight() - insets.bottom - insets.top) / 2
					+ insets.top - separatorIcon.getIconHeight() / 2;

			separatorIcon.paintIcon(c, g.create(), x, y);
		}
	}
}