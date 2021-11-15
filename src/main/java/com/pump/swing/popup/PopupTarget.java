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