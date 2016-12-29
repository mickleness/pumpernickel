/*
 * @(#)TransformAnimation.java
 *
 * $Date: 2014-06-06 14:04:49 -0400 (Fri, 06 Jun 2014) $
 *
 * Copyright (c) 2012 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Jeremy Wood. For details see accompanying license terms.
 * 
 * This software is probably, but not necessarily, discussed here:
 * https://javagraphics.java.net/
 * 
 * That site should also contain the most recent official version
 * of this software.  (See the SVN repository for more details.)
 */
package com.pump.animation;

import java.awt.geom.AffineTransform;

/** This creates an AffineTransform based on an input time value from [0, 1].
 */
public interface TransformAnimation {
	
	/** A new transform based on the time, width and height.
	 * 
	 * @param progress a float within [0, 1].
	 * @param viewWidth the width of the animation.
	 * @param viewHeight the height of the animation.
	 * @return the AffineTransform for the argument.
	 */
	public AffineTransform getTransform(float progress,int viewWidth,int viewHeight);
}
