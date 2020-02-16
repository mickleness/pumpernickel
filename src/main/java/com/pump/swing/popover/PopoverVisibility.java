package com.pump.swing.popover;

import javax.swing.JComponent;

/**
 * This is used to determine if a popover should remain visible.
 * 
 * @param <T>
 *            the type of component the popover shows.
 */
public interface PopoverVisibility<T extends JComponent> {
	/**
	 * Return true if a popover should be visible.
	 * <p>
	 * Once a popover is shown this will be continually pinged on the EDT until
	 * the popover is hidden. When this returns false the popover may linger for
	 * a fixed duration. For example: if this is tied to the mouse rolling over
	 * a component then it's OK if the mouse exits the component very briefly
	 * but quickly returns.
	 * 
	 * @param popover
	 *            the popover to determine the visibility of.
	 * @return true if a popover should be visible.
	 */
	public boolean isVisible(JPopover<T> popover);

	/**
	 * Install any appropriate listeners. This is called as a JPopover is about
	 * to become active/visible.
	 */
	public void install(JPopover<T> popover);

	/**
	 * Tear down everything {@link #install(JPopover)} did. This is called as a
	 * JPopover is deactivated/hidden.
	 */
	public void uninstall(JPopover<T> popover);
}
