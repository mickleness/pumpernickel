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

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.Icon;
import javax.swing.JList;

import com.pump.icon.CloseIcon;
import com.pump.plaf.DecoratedListUI.ListDecoration;
import com.pump.swing.JFancyBox;
import com.pump.swing.JFancyBox.FancyCloseIcon;

public abstract class CloseDecoration extends ListDecoration {
	Icon normalIcon;
	Icon pressedIcon;

	public CloseDecoration() {
		normalIcon = new JFancyBox.FancyCloseIcon();
		pressedIcon = new JFancyBox.FancyCloseIcon();
		((FancyCloseIcon) pressedIcon).setXColor(Color.gray);
		((FancyCloseIcon) pressedIcon).setBorderColor(Color.lightGray);
	}

	public CloseDecoration(int size) {
		normalIcon = new CloseIcon(size);
		pressedIcon = new CloseIcon(size);
	}

	@Override
	public boolean isVisible(JList list, Object value, int row,
			boolean isSelected, boolean cellHasFocus) {
		return isSelected;
	}

	@Override
	public Point getLocation(JList list, Object value, int row,
			boolean isSelected, boolean cellHasFocus) {
		Rectangle r = list.getCellBounds(row, row);
		return new Point(r.width - normalIcon.getIconWidth() - 2, 2);
	}

	@Override
	public Icon getIcon(JList list, Object value, int row, boolean isSelected,
			boolean cellHasFocus, boolean isRollover, boolean isPressed) {
		if (isPressed) {
			return pressedIcon;
		}
		return normalIcon;
	}
}