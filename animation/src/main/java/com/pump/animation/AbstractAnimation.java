/*
 * @(#)AbstractAnimation.java
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
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** This represents an animation that you can append
 * frames to.
 * 
 * @see #addFrame(BufferedImage, int)
 * @see #createReader()
 */
public abstract class AbstractAnimation {
	class MyReader implements ResettableAnimationReader {
		int ctr = 0;
		final int frameCount;
		
		MyReader(int frameCount) {
			this.frameCount = frameCount;
		}
		
		public void reset() {
			ctr = 0;
		}

		public BufferedImage getNextFrame(boolean cloneImage)
				throws IOException {
			if(ctr==frameCount) return null;
			
			BufferedImage returnValue = frames.get(ctr).getImage();
			ctr++;
			return returnValue;
		}

		public double getDuration() {
			int sum = 0;
			for(int a = 0; a<frameCount; a++) {
				sum += frames.get(a).duration;
			}
			return ((double)sum)/1000.0;
		}

		public int getFrameCount() {
			return frameCount;
		}

		public int getLoopCount() {
			return 1;
		}

		public double getFrameDuration() {
			return ((double)frames.get(ctr-1).duration)/1000.0;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}
	}
	
	/** An abstract representation of a frame from
	 * an animation.
	 *
	 */
	protected static abstract class Frame {
		/** The duration (in ms) of this frame. */
		int duration;
		
		/**
		 * 
		 * @param duration the duration (in ms) of this frame.
		 */
		Frame(int duration) {
			this.duration = duration;
		}
		
		/** Return the duration (in ms) of this frame.
		 */
		int getDuration() {
			return duration;
		}
		
		/** Return the image in this frame.
		 */
		abstract BufferedImage getImage() throws IOException;
	}
	
	List<Frame> frames = new ArrayList<Frame>();
	final int width, height;
	
	/** Create a new AbstractAnimation.
	 * 
	 * @param d the size of this animation.
	 */
	public AbstractAnimation(Dimension d) {
		width = d.width;
		height = d.height;
	}
	
	/** Add a frame to this animation.
	 * 
	 * @param bi the image to append. This must be the width and height of this
	 * animation.
	 * @param duration the duration (in ms) of this frame.
	 * @throws IOException if a problem occurs writing the image data
	 */
	public synchronized void addFrame(BufferedImage bi,int duration) throws IOException {
		if(bi.getWidth()!=width || bi.getHeight()!=height)
			throw new IllegalArgumentException("The incoming frame's dimensions do not match this animation's dimensions ("+bi.getWidth()+"x"+bi.getHeight()+" vs "+width+"x"+height+")");
		frames.add(createFrame(bi, duration));
	}
	
	/** Create a new <code>Frame</code> to append to this animation.
	 * 
	 * @param bi the image that depicts this frame.
	 * @param duration the duration (in ms) of this frame.
	 * @return a Frame object.
	 * @throws IOException if a problem occurs writing the image data.
	 */
	protected abstract Frame createFrame(BufferedImage bi,int duration) throws IOException;
	
	/** Create a new <code>ResettableAnimationReader</code> that iterates over
	 * this <code>AbstractAnimation</code>. Note the reader will only
	 * iterate over frames that exist as of the instant this method is
	 * invoked -- so if you add another 100 frames after calling this
	 * method then this reader will not include them.
	 * 
	 * @return a new ResettableAnimationReader for what currently exists
	 * in this animation.
	 */
	public synchronized ResettableAnimationReader createReader() {
		return new MyReader(frames.size());
	}
}
