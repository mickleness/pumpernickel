package com.pump.swing.popover;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class ListSelectionVisibility<T extends JComponent> implements
		PopoverVisibility<T> {
	static class PopoverListSelectionListener implements ListSelectionListener {
		JPopover<?> popover;

		PopoverListSelectionListener(JPopover<?> popover) {
			this.popover = popover;
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			popover.refreshVisibility();
		}

	};

	JList<?> list;
	Collection<Object> expectedSelection;
	Map<JPopover<T>, PopoverListSelectionListener> listenerMap = new HashMap<>();

	public ListSelectionVisibility(JList<?> list, Object... expectedSelection) {
		this.list = list;
		this.expectedSelection = new HashSet<>(Arrays.asList(expectedSelection));
	}

	@Override
	public boolean isVisible(JPopover<T> popover) {
		Collection<Object> currentSelection2 = new HashSet<>(
				list.getSelectedValuesList());
		return currentSelection2.equals(expectedSelection);
	}

	@Override
	public void install(JPopover<T> popover) {
		PopoverListSelectionListener l = listenerMap.get(popover);
		if (l == null) {
			l = new PopoverListSelectionListener(popover);
			listenerMap.put(popover, l);
			list.getSelectionModel().addListSelectionListener(l);
		}
	}

	@Override
	public void uninstall(JPopover<T> popover) {
		PopoverListSelectionListener l = listenerMap.remove(popover);
		if (l == null) {
			list.getSelectionModel().removeListSelectionListener(l);
		}
	}

}
