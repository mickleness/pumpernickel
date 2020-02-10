package com.pump.swing.popover;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.HierarchyBoundsListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

public abstract class AbstractComponentVisibility<T extends JComponent>
		implements PopoverVisibility<T> {

	static class PopoverHierarchyBoundsListener implements
			HierarchyBoundsListener {
		JPopover<?> popover;

		PopoverHierarchyBoundsListener(JPopover<?> popover) {
			this.popover = popover;
		}

		@Override
		public void ancestorMoved(HierarchyEvent e) {
			popover.refreshPopup();
		}

		@Override
		public void ancestorResized(HierarchyEvent e) {
			popover.refreshPopup();
		}
	}

	static class PopoverHierarchyListener implements HierarchyListener {
		JPopover<?> popover;

		PopoverHierarchyListener(JPopover<?> popover) {
			this.popover = popover;
		}

		@Override
		public void hierarchyChanged(HierarchyEvent e) {
			popover.refreshVisibility(false);
			popover.refreshPopup();
		}
	}

	static class PopoverComponentListener implements ComponentListener {
		JPopover<?> popover;

		PopoverComponentListener(JPopover<?> popover) {
			this.popover = popover;
		}

		@Override
		public void componentResized(ComponentEvent e) {
			popover.refreshPopup();
		}

		@Override
		public void componentMoved(ComponentEvent e) {
			popover.refreshPopup();
		}

		@Override
		public void componentShown(ComponentEvent e) {
			popover.refreshVisibility(false);
		}

		@Override
		public void componentHidden(ComponentEvent e) {
			popover.refreshVisibility(false);
		}
	}

	Map<JPopover<T>, PopoverHierarchyListener> hierarchyListenerMap = new HashMap<>();
	Map<JPopover<T>, PopoverHierarchyBoundsListener> hierarchyBoundsListenerMap = new HashMap<>();
	Map<JPopover<T>, PopoverComponentListener> componentListenerMap = new HashMap<>();

	@Override
	public void install(JPopover<T> popover) {
		PopoverHierarchyBoundsListener l1 = hierarchyBoundsListenerMap
				.get(popover);
		if (l1 == null) {
			l1 = new PopoverHierarchyBoundsListener(popover);
			hierarchyBoundsListenerMap.put(popover, l1);
			popover.getOwner().addHierarchyBoundsListener(l1);
		}

		PopoverHierarchyListener l2 = hierarchyListenerMap.get(popover);
		if (l2 == null) {
			l2 = new PopoverHierarchyListener(popover);
			hierarchyListenerMap.put(popover, l2);
			popover.getOwner().addHierarchyListener(l2);
		}

		PopoverComponentListener l3 = componentListenerMap.get(popover);
		if (l3 == null) {
			l3 = new PopoverComponentListener(popover);
			componentListenerMap.put(popover, l3);
			popover.getOwner().addComponentListener(l3);
		}
	}

	@Override
	public void uninstall(JPopover<T> popover) {
		PopoverHierarchyBoundsListener l1 = hierarchyBoundsListenerMap
				.remove(popover);
		if (l1 != null) {
			popover.getOwner().removeHierarchyBoundsListener(l1);
		}

		PopoverHierarchyListener l2 = hierarchyListenerMap.remove(popover);
		if (l2 != null) {
			popover.getOwner().removeHierarchyListener(l2);
		}

		PopoverComponentListener l3 = componentListenerMap.remove(popover);
		if (l3 != null) {
			popover.getOwner().removeComponentListener(l3);
		}
	}

}
