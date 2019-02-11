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
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;

/**
 * This listens for KeyEvents and automatically selects the combobox items that
 * start with a given letter, if possible.
 */
public class ListKeyListener extends KeyListenerNavigator {

	JComboBox comboBox;
	ListCellRenderer renderer;
	JList list;

	public ListKeyListener(JList list) {
		super(true);
		if (list == null)
			throw new NullPointerException();
		this.list = list;
	}

	public ListKeyListener(JComboBox comboBox) {
		super(true);
		if (comboBox == null)
			throw new NullPointerException();
		this.comboBox = comboBox;
		// TODO: use a ListDataListener to cache info about which items map to a
		// given first letter
		// TODO: if the user presses "S" and then "S" again: if no matches are
		// found for "SS" then cycle
		// through hits that do start with S.
		// TODO: add option to invoke popup and show selection. In cases where
		// the combobox functions more
		// like a button and selecting something causes major changes, we
		// shouldn't rush to make that selection
		// but we should help the user out.
	}

	public ListCellRenderer getCellRenderer() {
		if (comboBox != null) {
			return comboBox.getRenderer();
		} else if (list != null) {
			return list.getCellRenderer();
		}
		throw new IllegalStateException();
	}

	public ListModel getListModel() {
		if (comboBox != null) {
			return comboBox.getModel();
		} else if (list != null) {
			return list.getModel();
		}
		throw new IllegalStateException();
	}

	public int getSelectedIndex() {
		if (comboBox != null) {
			return comboBox.getSelectedIndex();
		} else if (list != null) {
			return list.getSelectedIndex();
		}
		throw new IllegalStateException();
	}

	public void setSelectedIndex(int newIndex) {
		if (comboBox != null) {
			comboBox.setSelectedIndex(newIndex);
			return;
		} else if (list != null) {
			list.setSelectedIndex(newIndex);
			return;
		}
		throw new IllegalStateException();
	}

	@Override
	protected boolean changeSelectionUsingText(KeyEvent e, String inputStr) {
		inputStr = inputStr.toUpperCase();
		ListCellRenderer renderer = getCellRenderer();

		List<Integer> itemsStartingWithText = new ArrayList<Integer>();

		ListModel model = getListModel();
		for (int a = 0; a < model.getSize(); a++) {
			Object element = model.getElementAt(a);
			Component rendered = renderer.getListCellRendererComponent(list,
					element, a, false, false);
			String text = getText(rendered);
			if (text == null)
				continue;
			text = text.toUpperCase();

			if (text.startsWith(inputStr)) {
				itemsStartingWithText.add(a);
			}
		}

		int selIndex = getSelectedIndex();

		int indexOf = itemsStartingWithText.indexOf(selIndex);
		int newSelIndex = selIndex;
		if (indexOf == -1) {
			if (itemsStartingWithText.size() > 0) {
				newSelIndex = itemsStartingWithText.get(0);
			}
		} else {
			int newIndex = (indexOf + 1) % itemsStartingWithText.size();
			newSelIndex = itemsStartingWithText.get(newIndex);
		}
		if (newSelIndex != selIndex) {
			setSelectedIndex(newSelIndex);
		}
		return newSelIndex != selIndex;
	}
}