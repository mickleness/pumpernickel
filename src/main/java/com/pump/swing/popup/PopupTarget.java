package com.pump.swing.popup;

import java.awt.Rectangle;

/**
 * This identifies the screen bounds of a popup's source.
 * <p>
 * If a popup uses a callout arrow to point to where it originated from: the
 * arrow should point to this portion of the screen.
 */
public interface PopupTarget {
	/**
	 * 
	 * @return where a popup should point to, or null if the popup shouldn't be
	 *         visible
	 */
	public Rectangle getScreenBounds();
}
