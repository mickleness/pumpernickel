package com.pump.plaf.decorate;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.JTree;

/**
 * This is a simple 3-state <code>TreeDecoration</code> and
 * <code>ListDecoration</code> that stores a fixed "normal", "rollover" and
 * "pressed" icon state.
 * <p>
 * The default implementation of <code>isVisible(..)</code> will only render
 * this decoration when a tree node is selected. This is just an aesthetic
 * choice you are free to override. (The original motivation for this decision
 * was: too many decorations might become visual clutter.)
 * <p>
 * It is assumed that if a rollover or pressed icon is provided that it will be
 * the same size as the normal icon.
 */
public class BasicDecoration implements ListTreeDecoration {

	Icon normalIcon, rolloverIcon, pressedIcon;
	ActionListener actionListener;

	/**
	 * Create a decoration with only one icon and no ActionListener.
	 * 
	 * @param normalIcon
	 */
	public BasicDecoration(Icon normalIcon) {
		if (normalIcon == null)
			throw new NullPointerException();
		this.normalIcon = normalIcon;
	}

	/**
	 * Create a clickable decoration with 3 states and an ActionListener.
	 * 
	 * @param normalIcon
	 *            the default icon. This may not be null.
	 * @param rolloverIcon
	 *            an optional icon to display when the mouse hovers over this
	 *            decoration.
	 * @param pressedIcon
	 *            an optional icon to display when the mouse clicks this
	 *            decoration.
	 * @param actionListener
	 *            an optional ActionListener to receive events when this
	 *            decoration is clicked.
	 */
	public BasicDecoration(Icon normalIcon, Icon rolloverIcon,
			Icon pressedIcon, ActionListener actionListener) {
		this(normalIcon);
		this.rolloverIcon = rolloverIcon;
		this.pressedIcon = pressedIcon;
		this.actionListener = actionListener;
	}

	@Override
	public Icon getIcon(JTree tree, Object value, boolean selected,
			boolean expanded, boolean leaf, int row, boolean isRollover,
			boolean isPressed) {
		return getIcon(isRollover, isPressed);
	}

	/** Returns true if the node is selected. */
	@Override
	public boolean isVisible(JTree tree, Object value, boolean selected,
			boolean expanded, boolean leaf, int row, boolean hasFocus) {
		return selected;
	}

	@Override
	public ActionListener getActionListener(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		return actionListener;
	}

	@Override
	public Icon getIcon(JList list, Object value, int row, boolean isSelected,
			boolean cellHasFocus, boolean isRollover, boolean isPressed) {
		return getIcon(isRollover, isPressed);
	}

	@Override
	public boolean isVisible(JList list, Object value, int row,
			boolean isSelected, boolean cellHasFocus) {
		return isSelected;
	}

	@Override
	public ActionListener getActionListener(JList list, Object value, int row,
			boolean isSelected, boolean cellHasFocus) {
		return actionListener;
	}

	@Override
	public Point getLocation(JList list, Object value, int row,
			boolean isSelected, boolean cellHasFocus) {
		Rectangle r = list.getUI().getCellBounds(list, row, row);
		Icon icon = getIcon(false, false);
		Point p = new Point(r.x + r.width - icon.getIconWidth(), r.height / 2
				- icon.getIconHeight() / 2);
		return p;
	}

	protected Icon getIcon(boolean isRollover, boolean isPressed) {
		if (isPressed && pressedIcon != null)
			return pressedIcon;
		if (isRollover && rolloverIcon != null)
			return rolloverIcon;
		return normalIcon;
	}
}