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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.util.Objects;

import javax.swing.Icon;

/** An icon that pads another icon with <code>Insets</code>. */
public class PaddedIcon implements Icon {
	final Icon icon;
	final Insets i;

	/**
	 * Create a PaddedIcon that fills extra space to fill the target dimensions
	 * provided.
	 * <p>
	 * This throws an exception if the target size is smaller than the Icon
	 * provided.
	 * 
	 * @param orig
	 *            an optional Icon to pad.
	 * @param targetSize
	 *            the dimensions this PaddedIcon.
	 */
	public PaddedIcon(Icon orig, Dimension targetSize) {
		Objects.requireNonNull(targetSize);

		if (orig == null) {
			icon = new EmptyIcon(targetSize.width, targetSize.height);
			i = new Insets(0, 0, 0, 0);
		} else {
			icon = orig;
			int x = targetSize.width - icon.getIconWidth();
			int y = targetSize.height - icon.getIconHeight();

			if (x < 0)
				throw new IllegalArgumentException(
						"Icon too wide to fit in target size. "
								+ targetSize.width + "<" + icon.getIconWidth());
			if (y < 0)
				throw new IllegalArgumentException(
						"Icon too tall to fit in target size. "
								+ targetSize.height + "<"
								+ icon.getIconHeight());

			int top = y / 2;
			int bottom = y - top;
			int left = x / 2;
			int right = x - left;
			i = new Insets(top, left, bottom, right);
		}
	}

	public PaddedIcon(Icon orig, int padding) {
		this(orig, new Insets(padding, padding, padding, padding));
	}

	public PaddedIcon(Icon orig, Insets i) {
		Objects.requireNonNull(orig);
		Objects.requireNonNull(i);
		this.icon = orig;
		this.i = i;
	}

	public int getIconHeight() {
		return icon.getIconHeight() + i.top + i.bottom;
	}

	public int getIconWidth() {
		return icon.getIconWidth() + i.left + i.right;
	}

	public void paintIcon(Component c, Graphics g, int x, int y) {
		icon.paintIcon(c, g, x + i.left, y + i.top);
	}

}