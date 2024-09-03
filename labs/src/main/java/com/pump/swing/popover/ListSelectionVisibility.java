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

/**
 * This PopoverVisibility shows a popover for a specific JList selection.
 */
public class ListSelectionVisibility<T extends JComponent>
		extends AbstractComponentVisibility<T> {
	static class PopoverListSelectionListener implements ListSelectionListener {
		JPopover<?> popover;

		PopoverListSelectionListener(JPopover<?> popover) {
			this.popover = popover;
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			popover.refreshVisibility(false);
		}

	};

	JList<?> list;
	Collection<Object> expectedSelection;
	Map<JPopover<T>, PopoverListSelectionListener> selectionListenerMap = new HashMap<>();

	/**
	 * Create a new ListSelectionVisibility.
	 * 
	 * @param list
	 *            the list to listen to.
	 * @param expectedSelection
	 *            the list's selection to listen for.
	 */
	public ListSelectionVisibility(JList<?> list, Object... expectedSelection) {
		this.list = list;
		this.expectedSelection = new HashSet<>(
				Arrays.asList(expectedSelection));
	}

	@Override
	public boolean isVisible(JPopover<T> popover) {
		Collection<Object> currentSelection2 = new HashSet<>(
				list.getSelectedValuesList());
		return currentSelection2.equals(expectedSelection)
				&& popover.getOwner().isShowing() && isActiveWindow(popover);
	}

	@Override
	public void install(JPopover<T> popover) {
		super.install(popover);
		PopoverListSelectionListener l1 = selectionListenerMap.get(popover);
		if (l1 == null) {
			l1 = new PopoverListSelectionListener(popover);
			selectionListenerMap.put(popover, l1);
			list.getSelectionModel().addListSelectionListener(l1);
		}
	}

	@Override
	public void uninstall(JPopover<T> popover) {
		PopoverListSelectionListener l1 = selectionListenerMap.remove(popover);
		if (l1 != null) {
			list.getSelectionModel().removeListSelectionListener(l1);
		}
		super.uninstall(popover);
	}

}