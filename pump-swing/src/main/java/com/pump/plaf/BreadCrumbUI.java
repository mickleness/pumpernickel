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
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.plaf.ComponentUI;

import com.pump.awt.SplayedLayout;
import com.pump.icon.PaddedIcon;
import com.pump.icon.TriangleIcon;
import com.pump.swing.JBreadCrumb;
import com.pump.swing.JBreadCrumb.BreadCrumbFormatter;
import com.pump.swing.NavigationListener;
import com.pump.swing.NavigationListener.ListSelectionType;

/**
 * The ComponentUI for {@link com.pump.swing.JBreadCrumb}.
 */
public class BreadCrumbUI extends ComponentUI {
	protected static final String PATH_NODE_KEY = BreadCrumbUI.class.getName()
			+ ".pathNode";
	protected static final String PATH_NODE_INDEX_KEY = BreadCrumbUI.class
			.getName() + ".pathNodeIndex";

	/**
	 * This client property of a <code>JBreadCrumb</code> defines the icon used
	 * as a separator.
	 */
	public static final String SEPARATOR_ICON_KEY = BreadCrumbUI.class
			.getName() + ".separatorIcon";

	protected Icon defaultSeparatorIcon = new PaddedIcon(new TriangleIcon(
			SwingConstants.EAST, 6, 6), new Insets(2, 4, 2, 6));

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

	class LabelMouseListener<T> extends MouseAdapter {

		private JLabel getLabel(Container parent, Point p) {
			for (int a = 0; a < parent.getComponentCount(); a++) {
				Component comp = parent.getComponent(a);
				if (comp.getBounds().contains(p) && comp instanceof JLabel)
					return (JLabel) comp;
			}
			return null;
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
		Icon icon = (Icon) comp.getClientProperty(SEPARATOR_ICON_KEY);
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
		comp.setLayout(new SplayedLayout(SwingConstants.HORIZONTAL, iconSize,
				true, true, false) {

			@Override
			protected Collection<JComponent> getEmphasizedComponents(
					JComponent container) {
				Collection<JComponent> returnValue = super
						.getEmphasizedComponents(container);
				if (returnValue.isEmpty() && container.getComponentCount() > 0) {
					returnValue = Arrays.asList((JComponent) container
							.getComponent(container.getComponentCount() - 1));
				}
				return returnValue;
			}

		});

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

		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				comp.revalidate();
				comp.repaint();
			}
		});
	}

	public static ComponentUI createUI(JComponent c) {
		return new BreadCrumbUI();
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);

		c.addMouseListener(new LabelMouseListener());

		c.addPropertyChangeListener(JBreadCrumb.PATH_KEY, refreshUIListener);
		c.addPropertyChangeListener(JBreadCrumb.FORMATTER_KEY,
				refreshUIListener);
		c.addPropertyChangeListener(SEPARATOR_ICON_KEY, refreshUIListener);
		refreshUI((JBreadCrumb<?>) c);
	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);

		c.removePropertyChangeListener(JBreadCrumb.PATH_KEY, refreshUIListener);
		c.removePropertyChangeListener(JBreadCrumb.FORMATTER_KEY,
				refreshUIListener);
		c.removePropertyChangeListener(SEPARATOR_ICON_KEY, refreshUIListener);
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