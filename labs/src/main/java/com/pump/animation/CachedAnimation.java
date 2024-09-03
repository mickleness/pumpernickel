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

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.pump.image.bmp.BmpDecoder;
import com.pump.image.bmp.BmpEncoder;
import com.pump.io.IOUtils;

/**
 * This stores an animation in memory as a series of cached BMP images.
 * <p>
 * Saving these images to the disk may be slightly expensive, but this model
 * lets you store animations that are arbitrarily large. (That is: they are
 * limited by your hard disk space and not your RAM.)
 * 
 * @see BufferedAnimation
 */
public class CachedAnimation extends AbstractAnimation {

	class FileFrame extends Frame {
		File file;

		FileFrame(File directory, BufferedImage bi, int duration)
				throws IOException {
			super(duration);
			file = directory == null ? File.createTempFile("frame", ".bmp")
					: File.createTempFile("frame", ".bmp", directory);
			file.deleteOnExit();
			myFiles.add(file);
			BmpEncoder.write(bi, file);
		}

		public BufferedImage getImage() throws IOException {
			synchronized (CachedAnimation.this) {
				if (myFiles == null)
					throw new IllegalStateException(
							"this animation has already been disposed");
				bi = BmpDecoder.read(file, bi);
				return bi;
			}
		}
	}

	private static File createTempDirectory() {
		File tempDir = new File(System.getProperty("tmp.dir"));
		File file = IOUtils.getUniqueFile(tempDir, "cached-animation", false,
				true);
		if (!file.mkdir()) {
			// while this isn't the end of the world, it strongly suggests
			// we could have major issues soon:
			System.err.println("CachedAnimation: could not make a directory \""
					+ file.getAbsolutePath() + "\"");
			return null;
		}
		return file;
	}

	File directory;
	BufferedImage bi;
	Set<File> myFiles = new HashSet<File>();

	/**
	 * @param d
	 *            the dimensions of this animation.
	 */
	public CachedAnimation(Dimension d) {
		this(createTempDirectory(), d);
	}

	public CachedAnimation(AnimationReader reader) throws IOException {
		super(new Dimension(reader.getWidth(), reader.getHeight()));
		addFrames(reader);
	}

	public void dispose() {
		synchronized (this) {
			if (directory != null) {
				IOUtils.delete(directory);
			}
			for (File f : myFiles) {
				if (f.exists())
					f.delete();
			}
			myFiles = null;
		}
	}

	private transient double remainder = 0;

	public void addFrames(AnimationReader reader) throws IOException {
		BufferedImage bi = reader.getNextFrame(false);
		while (bi != null) {
			double d = reader.getFrameDuration() + remainder;
			int k = (int) (d * 1000);
			remainder = d - ((double) k) / 1000.0;
			addFrame(bi, k);
			bi = reader.getNextFrame(false);
		}
	}

	/**
	 * 
	 * @param directory
	 *            an optional temp directory to store all of the frames inside.
	 * @param d
	 *            the dimensions of this animation.
	 */
	public CachedAnimation(File directory, Dimension d) {
		super(d);
		this.directory = directory;
	}

	@Override
	protected void finalize() throws Throwable {
		dispose();
	}

	@Override
	protected Frame createFrame(BufferedImage bi, int duration)
			throws IOException {
		synchronized (this) {
			if (myFiles == null)
				throw new IllegalStateException(
						"this animation has already been disposed");
			return new FileFrame(directory, bi, duration);
		}
	}
}