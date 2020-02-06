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
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.CellRendererPane;
import javax.swing.event.MouseInputAdapter;

import com.pump.util.WeakSet;

/**
 * This helps add a listener to a component and all its descendants.
 */
public abstract class DescendantListener {

	public static DescendantListener addFocusListener(Component c,
			final FocusListener focusListener, boolean includeParent) {
		return new DescendantListener(c, includeParent) {

			@Override
			public void register(Component c) {
				c.addFocusListener(focusListener);
			}

			@Override
			public void unregister(Component c) {
				c.removeFocusListener(focusListener);
			}

		};
	}

	/**
	 * Add MouseListener to a component and all of its descendants.
	 * 
	 * @param classesToIgnore
	 *            an optional set of Component classes for which we will NOT add
	 *            the mouse listener.
	 */
	public static DescendantListener addMouseListener(Component c,
			final MouseListener mouseListener, boolean includeParent,
			final Class... classesToIgnore) {
		return addMouseListener(c, mouseListener, null, includeParent,
				classesToIgnore);
	}

	/**
	 * Add MouseInputAdapter to a component and all of its descendants.
	 * 
	 * @param classesToIgnore
	 *            an optional set of Component classes for which we will NOT add
	 *            the mouse listener.
	 */
	public static DescendantListener addMouseListener(Component c,
			final MouseInputAdapter mouseListener, boolean includeParent,
			final Class... classesToIgnore) {
		return addMouseListener(c, mouseListener, mouseListener, includeParent,
				classesToIgnore);
	}

	/**
	 * Add MouseMotionListener to a component and all of its descendants.
	 * 
	 * @param classesToIgnore
	 *            an optional set of Component classes for which we will NOT add
	 *            the mouse listener.
	 */
	public static DescendantListener addMouseListener(Component c,
			final MouseMotionListener mouseListener, boolean includeParent,
			final Class... classesToIgnore) {
		return addMouseListener(c, null, mouseListener, includeParent,
				classesToIgnore);
	}

	private static DescendantListener addMouseListener(Component c,
			final MouseListener mouseListener,
			final MouseMotionListener mouseMotionListener,
			boolean includeParent, final Class... classesToIgnore) {
		return new DescendantListener(c, includeParent) {

			@Override
			public void register(Component c) {
				for (Class classToIgnore : classesToIgnore) {
					if (classToIgnore.isInstance(c))
						return;
				}
				if (mouseListener != null)
					c.addMouseListener(mouseListener);
				if (mouseMotionListener != null)
					c.addMouseMotionListener(mouseMotionListener);
			}

			@Override
			public void unregister(Component c) {
				for (Class classToIgnore : classesToIgnore) {
					if (classToIgnore.isInstance(c))
						return;
				}
				if (mouseListener != null)
					c.removeMouseListener(mouseListener);
				if (mouseMotionListener != null)
					c.removeMouseMotionListener(mouseMotionListener);
			}

		};
	}

	WeakSet<Component> components = new WeakSet<>();

	ContainerListener containerListener = new ContainerListener() {

		@Override
		public void componentAdded(ContainerEvent e) {
			if (!(e.getContainer() instanceof CellRendererPane))
				processAddition(e.getChild());
		}

		@Override
		public void componentRemoved(ContainerEvent e) {
			processRemoval(e.getChild());
		}

	};

	public DescendantListener(final Component c, boolean includeParent) {
		processAddition(c);

		if (includeParent) {
			c.addHierarchyListener(new HierarchyListener() {
				@Override
				public void hierarchyChanged(HierarchyEvent e) {
					Component parent = c.getParent();
					if (parent != null) {
						processAddition(parent);
					} else {
						processRemoval(e.getChangedParent());
					}
				}
			});
			Component parent = c.getParent();
			if (parent != null) {
				processAddition(parent);
			}
		}
	}

	private void processRemoval(Component c) {
		if (components.remove(c)) {
			unregister(c);
			if (c instanceof Container) {
				Container container = (Container) c;
				container.removeContainerListener(containerListener);
				for (Component child : container.getComponents()) {
					processRemoval(child);
				}
			}
		}
	}

	private void processAddition(Component c) {
		if (components.add(c)) {
			register(c);
			if (c instanceof Container) {
				Container container = (Container) c;
				container.addContainerListener(containerListener);
				for (Component child : container.getComponents()) {
					processAddition(child);
				}
			}
		}
	}

	public abstract void register(Component c);

	public abstract void unregister(Component c);

	/**
	 * Uninstall this listener from all Components.
	 * <p>
	 * This calls {@link #unregister(Component)} for every component
	 * that is currently registered.
	 */
	public void uninstall() {
		Component[] array = components
				.toArray(new Component[components.size()]);
		for (Component c : array) {
			processRemoval(c);
		}
	}
}