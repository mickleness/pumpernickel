package com.pump.swing.popover;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;

import com.pump.awt.DescendantListener;

/**
 * This calculates whether a popover should be visible.
 * <p>
 * The default implementation takes into account whether the owner or the
 * content has the keyboard focus or mouse rollover.
 * <p>
 * Also for JComboBoxes we restrict visibility when a component is expanded.
 */
public class BasicPopoverVisibility<T extends JComponent> implements
		PopoverVisibility<T> {

	static class PopoverFocusListener extends FocusAdapter {
		JPopover<?> popover;
		DescendantListener descendantListener;

		PopoverFocusListener(JPopover<?> popover) {
			this.popover = popover;

			descendantListener = DescendantListener.addFocusListener(
					popover.getOwner(), this, false);
		}

		@Override
		public void focusGained(FocusEvent e) {
			popover.refreshVisibility(false);
		}

		public void uninstall() {
			descendantListener.uninstall();
		}
	}

	static class PopoverMouseListener extends MouseInputAdapter {
		JPopover<?> popover;
		DescendantListener descendantListener;

		PopoverMouseListener(JPopover<?> popover) {
			this.popover = popover;

			descendantListener = DescendantListener.addMouseListener(
					popover.getOwner(), this, false);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			popover.refreshVisibility(false);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			popover.refreshVisibility(false);
		}

		public void uninstall() {
			descendantListener.uninstall();
		}
	}

	/**
	 * Calculate whether a popover should be visible or not.
	 * <p>
	 * The default implementation takes into account the keybord focus, mouse
	 * position, and expanded state.
	 * 
	 * @param popover
	 *            the JPopover to evaluate
	 * @return true if the popover should be visible.
	 */
	@Override
	public boolean isVisible(JPopover<T> popover) {
		JComponent owner = popover.getOwner();
		JComponent contents = popover.getContents();

		boolean newVisible;

		if (owner.isShowing() && owner.isEnabled()) {
			newVisible = isFocusOwnerOrAncestor(owner) || isRollover(owner);
			if (!newVisible && popover.isRolloverContents()) {
				newVisible = isFocusOwnerOrAncestor(contents)
						|| (isRollover(contents));
			}
		} else {
			newVisible = false;
		}

		if (isExpanded(owner)) {
			newVisible = false;
		}

		return newVisible;
	}

	/**
	 * Return true if the argument is the focus owner or is an ancestor of the
	 * focus owner.
	 */
	protected boolean isFocusOwnerOrAncestor(JComponent jc) {
		Component focusOwner = KeyboardFocusManager
				.getCurrentKeyboardFocusManager().getFocusOwner();
		if (focusOwner == null)
			return false;
		return jc == focusOwner
				|| SwingUtilities.isDescendingFrom(focusOwner, jc);
	}

	/**
	 * Return true if the mouse is currently over the argument.
	 */
	protected boolean isRollover(JComponent jc) {
		if (!jc.isShowing())
			return false;
		Point p = jc.getLocationOnScreen();
		int w = jc.getWidth();
		int h = jc.getHeight();

		Point mouse = MouseInfo.getPointerInfo().getLocation();

		return mouse.x >= p.x && mouse.y >= p.y && mouse.x < p.x + w
				&& mouse.y < p.y + h;
	}

	/**
	 * Return true if the component is expanded, such as when you open a
	 * JComboBox.
	 */
	protected boolean isExpanded(JComponent jc) {
		boolean expanded = false;
		AccessibleContext c = jc.getAccessibleContext();
		if (c != null) {
			AccessibleStateSet axSet = c.getAccessibleStateSet();
			if (axSet.contains(AccessibleState.EXPANDED)) {
				expanded = true;
			}
		}
		return expanded;

	}

	Map<JPopover<?>, PopoverFocusListener> focusListenerMap = new HashMap<>();
	Map<JPopover<?>, PopoverMouseListener> mouseListenerMap = new HashMap<>();

	@Override
	public void install(JPopover<T> popover) {
		PopoverFocusListener focusListener = focusListenerMap.get(popover);
		if (focusListener == null) {
			focusListener = new PopoverFocusListener(popover);
			focusListenerMap.put(popover, focusListener);
		}

		PopoverMouseListener mouseListener = mouseListenerMap.get(popover);
		if (mouseListener == null) {
			mouseListener = new PopoverMouseListener(popover);
			mouseListenerMap.put(popover, mouseListener);
		}
	}

	@Override
	public void uninstall(JPopover<T> popover) {
		PopoverFocusListener focusListener = focusListenerMap.remove(popover);
		if (focusListener != null)
			focusListener.uninstall();

		PopoverMouseListener mouseListener = mouseListenerMap.remove(popover);
		if (mouseListener != null)
			mouseListener.uninstall();
	}
}
