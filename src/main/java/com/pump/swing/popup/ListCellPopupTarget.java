package com.pump.swing.popup;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Objects;

import javax.swing.JList;
import javax.swing.SwingUtilities;

/**
 * This PopupTarget points to a specific cell of a list.
 */
public class ListCellPopupTarget implements PopupTarget {

	int selectedIndex;
	JList<?> list;

	public ListCellPopupTarget(JList<?> list, int selectedIndex) {
		Objects.requireNonNull(list);
		if (selectedIndex < 0 || selectedIndex >= list.getModel().getSize())
			throw new IllegalArgumentException("selectedIndex ("
					+ selectedIndex + ") should an index with this list");
		this.list = list;
		this.selectedIndex = selectedIndex;
	}

	@Override
	public Rectangle getScreenBounds() {
		Rectangle r = list.getUI().getCellBounds(list, selectedIndex,
				selectedIndex);
		Point p = new Point(0, 0);
		SwingUtilities.convertPointToScreen(p, list);
		r.x += p.x;
		r.y += p.y;

		int z = 0;
		if (r.width > 30) {
			z = 10;
		} else if (r.width > 15) {
			z = 4;
		} else if (r.width > 8) {
			z = 1;
		}
		r.x += z;
		r.y += z;
		r.width -= 2 * z;
		r.height -= 2 * z;

		return r;
	}
}
