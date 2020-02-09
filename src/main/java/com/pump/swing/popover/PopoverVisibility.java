package com.pump.swing.popover;

import javax.swing.JComponent;

public interface PopoverVisibility<T extends JComponent> {
	public boolean isVisible(JPopover<T> popover);

	public void install(JPopover<T> popover);

	public void uninstall(JPopover<T> popover);
}
