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
package com.pump.animation;

import java.awt.geom.AffineTransform;

/**
 * This creates an AffineTransform based on an input time value from [0, 1].
 */
public interface TransformAnimation {

	/**
	 * A new transform based on the time, width and height.
	 * 
	 * @param progress
	 *            a float within [0, 1].
	 * @param viewWidth
	 *            the width of the animation.
	 * @param viewHeight
	 *            the height of the animation.
	 * @return the AffineTransform for the argument.
	 */
	public AffineTransform getTransform(float progress, int viewWidth,
			int viewHeight);
}