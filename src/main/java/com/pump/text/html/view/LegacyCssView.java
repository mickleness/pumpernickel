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
package com.pump.text.html.view;

import java.awt.Graphics;
import java.awt.Shape;

/**
 * This offers a method to paint the original/legacy Swing implementation of a
 * View.
 */
public interface LegacyCssView {

	/**
	 * This paints the legacy Swing CSS v2 implementation of a View and nothing
	 * else.
	 */
	void paintLegacyCss2(Graphics g, Shape alloc);

}