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
package com.pump.icon;

import java.awt.Insets;

import javax.swing.Icon;

/**
 * An icon that pads another icon with <code>Insets</code>.
 * <p>
 * This is provided as an interface so {@link IconUtils} can create PaddedIcons
 * that still identify as other interfaces.
 * <p>
 * For example: if you start off with an Icon that also happens to implement
 * AccessibleIcon or InvertableIcon, then you can use IconUtils to create a new
 * PaddedIcon that is still identifiable as those original interfaces too.
 */
public interface PaddedIcon extends Icon {
	public Insets getIconInsets();

	public Icon getPaddedIcon();
}