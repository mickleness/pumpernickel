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
package com.pump.animation.quicktime;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.imageio.ImageIO;

import com.pump.animation.quicktime.atom.VideoSampleDescriptionEntry;

/**
 * A MovWriter that encodes frames as a series of PNG images.
 */
public class PNGMovWriter extends MovWriter {

	public PNGMovWriter(File file) throws IOException {
		super(file);
	}

	@Override
	protected VideoSampleDescriptionEntry getVideoSampleDescriptionEntry() {
		return VideoSampleDescriptionEntry.createPNGDescription(videoTrack.w,
				videoTrack.h);
	}

	@Override
	protected void writeFrame(OutputStream out, BufferedImage image,
			Map<String, Object> settings) throws IOException {
		if (!ImageIO.write(image, "png", out))
			throw new IOException("writed failed");
	}
}