/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.plaf;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.MouseEvent;
import java.util.Objects;

import javax.swing.event.MouseInputAdapter;
import javax.swing.tree.DefaultMutableTreeNode;

import com.pump.data.BooleanProperty;

/**
 * This keeps track of whether the mouse is over a component or any of its
 * descendants.
 */
public class DescendantMouseListener {

	/**
	 * Return a tree node for the given component and all its descendants. The
	 * user object of each node is the java.awt.Component.
	 */
	public static DefaultMutableTreeNode getContainerDescendants(
			Component component) {
		DefaultMutableTreeNode n = new DefaultMutableTreeNode(component);
		if (component instanceof Container) {
			Container c = (Container) component;
			Component[] children = c.getComponents();
			for (Component child : children) {
				DefaultMutableTreeNode childNode = getContainerDescendants(child);
				n.add(childNode);
			}
		}
		return n;
	}

	/**
	 * This updates <code>parentRollover</code> every time the mouse is over any
	 * part of the <code>component's</code> parent.
	 * <p>
	 * (This updates as the component is added to new parents.)
	 * <p>
	 * The original use case for this is a button that is added to a row of
	 * controls. When the mouse is anywhere over the row of controls: the button
	 * shows a slightly indicated icon.
	 * 
	 * @param component
	 *            the component whose parent will be monitored.
	 * @param parentRollover
	 *            the property to update.
	 */
	public static void installForParentOf(final Component component,
			final BooleanProperty parentRollover) {
		HierarchyListener h = new HierarchyListener() {
			DescendantMouseListener lastListener = null;

			@Override
			public void hierarchyChanged(HierarchyEvent e) {
				Container parent = component.getParent();
				if (lastListener == null || parent != lastListener.container) {
					if (lastListener != null) {
						lastListener.uninstall();
						lastListener = null;
					}
					if (parent != null) {
						lastListener = new DescendantMouseListener(parent,
								parentRollover);
					}
				}
			}

		};
		component.addHierarchyListener(h);
		h.hierarchyChanged(null);
	}

	Component container;
	BooleanProperty rolloverProperty;
	DefaultMutableTreeNode containerDescendants;

	HierarchyListener hierarchyListener = new HierarchyListener() {

		@Override
		public void hierarchyChanged(HierarchyEvent e) {
			refreshListeners();
		}

	};

	ContainerListener containerListener = new ContainerListener() {

		@Override
		public void componentAdded(ContainerEvent e) {
			refreshListeners();
		}

		@Override
		public void componentRemoved(ContainerEvent e) {
			refreshListeners();
		}

	};

	MouseInputAdapter mouseListener = new MouseInputAdapter() {

		@Override
		public void mouseEntered(MouseEvent e) {
			getRolloverProperty().setValue(true);
		}

		@Override
		public void mouseExited(MouseEvent e) {
			getRolloverProperty().setValue(false);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			getRolloverProperty().setValue(true);
		}

	};

	/**
	 * Create a new DescendantMouseListener.
	 * 
	 * @param container
	 *            the container this listener listens to.
	 */
	public DescendantMouseListener(Component container) {
		this(container, new BooleanProperty("rollover"));
	}

	/**
	 * Create a new DescendantMouseListener.
	 * 
	 * @param container
	 *            the container this listener listens to.
	 * @param rolloverProperty
	 *            the property object that is updated as MouseEvents come in.
	 */
	public DescendantMouseListener(Component container,
			BooleanProperty rolloverProperty) {
		Objects.requireNonNull(container);
		Objects.requireNonNull(rolloverProperty);
		this.container = container;
		this.rolloverProperty = rolloverProperty;
		refreshListeners();
	}

	/**
	 * Return true if the mouse is over the container or any of its descendants.
	 * This is shorthand for
	 * <code>getRolloverProperty().getValue().booleanValue();</code>
	 */
	public boolean isRollover() {
		return getRolloverProperty().getValue().booleanValue();
	}

	/**
	 * Return the property that reflects the current rollover state.
	 */
	public BooleanProperty getRolloverProperty() {
		return rolloverProperty;
	}

	/**
	 * Remove and/or add listeners if the component's hierarchy has changed.
	 */
	private void refreshListeners() {
		DefaultMutableTreeNode newContainerDescendants = getContainerDescendants(container);
		if (containerDescendants != null) {
			removeListeners(containerDescendants);
		}
		containerDescendants = newContainerDescendants;
		addListeners(containerDescendants);
	}

	/**
	 * Uninstall this listener so it never updates
	 * {@link #getRolloverProperty()} again. Once this method is called this
	 * object does nothing.
	 */
	public void uninstall() {
		if (containerDescendants != null) {
			removeListeners(containerDescendants);
			containerDescendants = null;
		}
	}

	/**
	 * Remove all our internal listeners from a component and its descendants.
	 */
	private void removeListeners(DefaultMutableTreeNode componentNode) {
		Component c = (Component) componentNode.getUserObject();
		c.removeHierarchyListener(hierarchyListener);
		c.removeMouseListener(mouseListener);
		c.removeMouseMotionListener(mouseListener);
		for (int a = 0; a < componentNode.getChildCount(); a++) {
			removeListeners((DefaultMutableTreeNode) componentNode
					.getChildAt(a));
		}
		if (c instanceof Container)
			((Container) c).removeContainerListener(containerListener);
	}

	/**
	 * Add all our internal listeners from a component and its descendants.
	 */
	private void addListeners(DefaultMutableTreeNode componentNode) {
		Component c = (Component) componentNode.getUserObject();
		c.addHierarchyListener(hierarchyListener);
		c.addMouseListener(mouseListener);
		c.addMouseMotionListener(mouseListener);
		for (int a = 0; a < componentNode.getChildCount(); a++) {
			addListeners((DefaultMutableTreeNode) componentNode.getChildAt(a));
		}
		if (c instanceof Container)
			((Container) c).addContainerListener(containerListener);
	}
}