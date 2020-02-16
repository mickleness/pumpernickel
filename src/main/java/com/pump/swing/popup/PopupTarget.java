package com.pump.swing.popup;

import java.awt.Rectangle;

/**
 * This identifies the screen bounds of a popup's source.
 * <p>
 * If a popup uses a callout arrow to point to where it originated from: the
 * arrow should point to this portion of the screen.
 */
public interface PopupTarget {
	public Rectangle getScreenBounds();
}
