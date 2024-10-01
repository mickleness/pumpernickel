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
package com.pump.animation.quicktime;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CancellationException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import javax.swing.ProgressMonitor;

import com.pump.animation.AnimationReader;
import com.pump.animation.quicktime.atom.VideoSampleDescriptionEntry;

/**
 * A MovWriter that encodes frames as a series of JPEG images.
 */
public class JPEGMovWriter extends MovWriter {

	private static final float DEFAULT_JPG_QUALITY = .85f;

	/**
	 * This property is used to determine the JPG image quality. It is a float
	 * between [0, 1], where 1 is a lossless image. This value should be the key
	 * in a key/value pair in the Map passed to <code>addFrame(..)</code>.
	 */
	public static final String PROPERTY_QUALITY = "jpeg-quality";

	float defaultQuality;

	public JPEGMovWriter(File file) throws IOException {
		this(file, DEFAULT_JPG_QUALITY);
	}

	/**
	 * 
	 * @param file
	 *            the destination file to write to.
	 * @param defaultQuality
	 *            the default JPEG quality (from [0,1]) to use if a frame is
	 *            added without otherwise specifying this value.
	 * @throws IOException
	 */
	public JPEGMovWriter(File file, float defaultQuality) throws IOException {
		super(file);
		this.defaultQuality = defaultQuality;
	}

	/**
	 * Write a JPEG-encoded animation with no audio to a MOV file.
	 */
	public static void write(File movFile, float defaultQuality, AnimationReader animation) throws IOException {
		try (JPEGMovWriter writer = new JPEGMovWriter(movFile, defaultQuality)) {
			writer.add(animation);
		}
	}

	@Override
	protected VideoSampleDescriptionEntry getVideoSampleDescriptionEntry() {
		return VideoSampleDescriptionEntry.createJPEGDescription(videoTrack.w,
				videoTrack.h);
	}

	/**
	 * Add an image to this animation using a specific jpeg compression quality.
	 * 
	 * @param duration
	 *            the duration (in seconds) of this frame
	 * @param bi
	 *            the image to add
	 * @param jpegQuality
	 *            a value from [0,1] indicating the quality of this image. A
	 *            value of 1 represents a losslessly encoded image.
	 * @throws IOException
	 */
	public synchronized void addFrame(float duration, BufferedImage bi,
			float jpegQuality) throws IOException {
		Map<String, Object> settings = new HashMap<String, Object>(1);
		settings.put(PROPERTY_QUALITY, Float.valueOf(jpegQuality));
		super.addFrame(duration, bi, settings);
	}

	private static boolean printWarning = false;

	@Override
	protected void writeFrame(OutputStream out, BufferedImage image,
			Map<String, Object> settings) throws IOException {
		if (image.getType() == BufferedImage.TYPE_INT_ARGB
				|| image.getType() == BufferedImage.TYPE_INT_ARGB_PRE) {
			if (printWarning == false) {
				printWarning = true;
				System.err
						.println("JPEGMovWriter Warning: a BufferedImage of type TYPE_INT_ARGB may produce unexpected results. The recommended type is TYPE_INT_RGB.");
			}
		}
		float quality;
		if (settings != null
				&& settings.get(PROPERTY_QUALITY) instanceof Number) {
			quality = ((Number) settings.get(PROPERTY_QUALITY)).floatValue();
		} else if (settings != null
				&& settings.get(PROPERTY_QUALITY) instanceof String) {
			quality = Float.parseFloat((String) settings.get(PROPERTY_QUALITY));
		} else {
			quality = defaultQuality;
		}

		MemoryCacheImageOutputStream iOut = new MemoryCacheImageOutputStream(
				out);
		ImageWriter iw = ImageIO.getImageWritersByMIMEType("image/jpeg").next();
		ImageWriteParam iwParam = iw.getDefaultWriteParam();
		iwParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
		iwParam.setCompressionQuality(quality);
		iw.setOutput(iOut);
		IIOImage img = new IIOImage(image, null, null);
		iw.write(null, img, iwParam);
	}

	/**
	 * Add all the frames from an AnimationReader.
	 * 
	 * @param r
	 *            the animation to read.
	 * @param monitor
	 *            an optional ProgressMonitor to update
	 * @throws IOException
	 *             if an error occurs copying frames.
	 */
	public void addFrames(AnimationReader r, ProgressMonitor monitor)
			throws IOException {
		if (monitor != null)
			monitor.setMaximum(r.getFrameCount());
		BufferedImage bi = r.getNextFrame(false);
		int ctr = 1;
		while (bi != null) {
			if (monitor != null) {
				if (monitor.isCanceled()) {
					throw new CancellationException();
				}
				monitor.setProgress(ctr);
			}
			float d;
			try {
				d = (float) r.getFrameDuration();
			} catch (Exception e) {
				e.printStackTrace();
				d = 1;
			}
			addFrame(d, bi, .98f);
			bi = r.getNextFrame(false);
			ctr++;
		}
	}
}