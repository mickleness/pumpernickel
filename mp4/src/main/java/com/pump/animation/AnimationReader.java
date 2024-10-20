/**
 * This software is released as part of the Pumpernickel project.
 * <p>
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * <p>
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.animation;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * This is an iterator for an animation. Once a frame has been loaded, all
 * previous frames should be discarded by this object.
 * 
 */
public interface AnimationReader {

	/**
	 * This iterates through the file to the next frame image, or
	 * <code>null</code> if no more image data is available.
	 * 
	 * @param cloneImage
	 *            if this is <code>true</code>, this method will always return a
	 *            new <code>BufferedImage</code>. If this is <code>false</code>,
	 *            then this method <i>may</i> constantly return the same
	 *            <code>BufferedImage</code>, updated for each frame.
	 * @return the next frame image, or <code>null</code> if there are no more
	 *         frames.
	 * @throws IOException
	 *             if a problem occurs reading the frame data
	 */
	BufferedImage getNextFrame(boolean cloneImage) throws IOException;

	/**
	 * This method will return <code>-1</code> if the total duration of the
	 * animation is not yet known. Otherwise it returns the duration (in
	 * seconds) if this animation.
	 * 
	 * @return the total duration of this animation, or -1 if it is not yet
	 *         known.
	 */
	double getDuration();

	/**
	 * If this can be determined, this returns the number of frames this reader
	 * will read.
	 * <P>
	 * Otherwise this returns -1. Note that since this pipes images through
	 * consecutively (without caching anything), this may return -1 more often
	 * than you'd like.
	 * 
	 * @return the number of frames, or -1 if it is not yet known.
	 */
	int getFrameCount();

	/**
	 * Added primarily for GIF files, this returns the number of times an
	 * animation should loop. If a file format, such as MOV, does not directly
	 * specify this value, it should be 1 (indicating that this animation should
	 * play exactly once). Note this may also return <code>LOOP_FOREVER</code>.
	 * <P>
	 * This method may return -1 until this reader is completely finished, this
	 * indicates that this information is not yet known.
	 *
	 * @return the number loops, or <code>LOOP_FOREVER</code>, or -1 if it is
	 *         not yet known.
	 */
	int getLoopCount();

	/**
	 * @return the duration of the last frame provided by
	 *         <code>getNextFrame()</code> in seconds. In rare cases this may be
	 *         zero (indicating that a frame should be played as fast as
	 *         possible, or maybe skipped), but it should never be negative.
	 */
	double getFrameDuration();

	/**
	 * @return the width of this image/animation, in pixels.
	 *         <P>
	 *         This information needs to always be immediately available; it
	 *         should not matter where in the iteration you are.
	 */
	int getWidth();

	/**
	 * @return the height of this image/animation, in pixels.
	 *         <P>
	 *         This information needs to always be immediately available; it
	 *         should not matter where in the iteration you are.
	 */
	int getHeight();
}