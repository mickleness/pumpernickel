/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.awt.text;

import java.awt.Dimension;
import java.awt.Graphics2D;

/**
 * This is a common interface for visual text effects.
 * 
 * @see <a
 *      href="https://javagraphics.blogspot.com/2011/02/text-effects-and-block-shadows.html">Text:
 *      Effects and Block Shadows</a>
 */
public interface TextEffect {

	/**
	 * Paint this effect.
	 * 
	 * @param g
	 *            the Graphics2D to paint to.
	 * @param fraction
	 *            a float from [0, 1] indicating how far along this effect it.
	 */
	public void paint(Graphics2D g, float fraction);

	/**
	 * An optional method to retrieve the preferred size. May return null.
	 */
	public Dimension getPreferredSize();
}