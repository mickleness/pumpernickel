/*
 * @(#)BufferedAnimation.java
 *
 * $Date: 2014-06-06 14:04:49 -0400 (Fri, 06 Jun 2014) $
 *
 * Copyright (c) 2014 by Jeremy Wood.
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

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

/** This stores an animation in memory as a series of <code>BufferedImages</code>.
 * <p>If applies to indefinitely large animations: this will result in
 * an <code>OutOfMemoryError</code>.
 * @see CachedAnimation
 */
public class BufferedAnimation extends AbstractAnimation {

	static class ImageFrame extends Frame {
		BufferedImage bi;
		
		ImageFrame(BufferedImage bi,int duration) {
			super(duration);
			this.bi = bi;
		}
		
		public BufferedImage getImage() {
			return bi;
		}
	}
	
	public BufferedAnimation(Dimension d) {
		super(d);
	}
	
	/** Add a frame to this animation.
	 * 
	 * @param bi the image to append. This must be the width and height of this
	 * animation.
	 * @param cloneImage if true then this image will be cloned. Because this
	 * animation is represented as a series of <code>BufferedImages</code> stored
	 * in memory: you must clone the image if you want to continually pass the
	 * same <code>BufferedImage</code> to this method. (Otherwise all previous frames
	 * will be corrupt.) If you always pass a unique <code>BufferedImage</code> to
	 * this method: then this argument can be false.
	 * @param duration the duration (in ms) of this frame.
	 * 
	 * @throws IOException if a problem occurs writing the frame data.
	 */
	public synchronized void addFrame(BufferedImage bi,boolean cloneImage,int duration) throws IOException {
		if(cloneImage) {
			BufferedImage clone = new BufferedImage(bi.getWidth(), bi.getHeight(), bi.getType());
			Graphics2D g = clone.createGraphics();
			g.drawImage(bi, 0, 0, null);
			g.dispose();
		}
		addFrame(bi, duration);
	}

	protected Frame createFrame(BufferedImage bi, int duration) {
		return new ImageFrame(bi, duration);
	}
}
