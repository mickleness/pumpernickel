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
import java.awt.Insets;

import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import com.pump.swing.DashedBorder;

public interface CellRendererConstants {
	public static final Color background = Color.white;
	public static final Color foreground = Color.black;
	public static final Insets iconPadding = new Insets(2, 10, 2, 4);

	/** A one-pixel empty border. */
	public static final Border EMPTY_BORDER = new EmptyBorder(1, 1, 1, 1);
	/**
	 * A one-pixel dark gray dotted border, used to indicate focus in some
	 * environments.
	 */
	public static final Border FOCUS_BORDER = new DashedBorder(1, 1, 1,
			Color.darkGray, 0);
}