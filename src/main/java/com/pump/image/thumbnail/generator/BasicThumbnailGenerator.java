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
package com.pump.image.thumbnail.generator;

import java.awt.image.BufferedImage;
import java.io.File;

import com.pump.util.JVM;

/**
 * This ThumbnailGenerator multiplexes to several more specific
 * ThumbnailGenerators.
 */
public class BasicThumbnailGenerator implements ThumbnailGenerator {

	protected ThumbnailGenerator[] DEFAULT_GENERATORS = new ThumbnailGenerator[] {
			new JPEGMetaDataThumbnailGenerator(),
			new ScalingThumbnailGenerator(),
			new MacQuickLookThumbnailGenerator() };

	/**
	 * The generators we'll attempt to use, sorted by preference.
	 */
	protected ThumbnailGenerator[] generators;

	public BasicThumbnailGenerator() {
		setGenerators((ThumbnailGenerator[]) null);
	}

	/**
	 * Assign the generators this BasicThumbnailGenerator consults.
	 * 
	 * @param generators
	 *            if this is null the default set is used.
	 */
	public void setGenerators(ThumbnailGenerator... generators) {
		if (generators == null)
			generators = DEFAULT_GENERATORS;
		this.generators = generators;
	}

	/**
	 * Return the generators this consults, in order.
	 */
	public ThumbnailGenerator[] getGenerators() {
		ThumbnailGenerator[] copy = new ThumbnailGenerator[generators.length];
		System.arraycopy(generators, 0, copy, 0, copy.length);
		return copy;
	}

	@Override
	public BufferedImage createThumbnail(File file, int requestedMaxImageSize)
			throws Exception {

		String filename = file.getName().toLowerCase();

		for (ThumbnailGenerator generator : generators) {
			try {
				BufferedImage returnValue = null;
				if (generator instanceof JPEGMetaDataThumbnailGenerator) {
					if (filename.endsWith(".jpg")
							|| filename.endsWith(".jpeg")) {
						returnValue = generator.createThumbnail(file,
								requestedMaxImageSize);
					}
				} else if (generator instanceof MacQuickLookThumbnailGenerator) {
					if (JVM.isMac)
						returnValue = generator.createThumbnail(file,
								requestedMaxImageSize);
				} else {
					// ScalingThumbnailGenerator will land here:

					returnValue = generator.createThumbnail(file,
							requestedMaxImageSize);
				}
				if (returnValue != null) {
					return returnValue;
				}
			} catch (Exception e) {
				// do nothing, try other generators
			}
		}

		return null;
	}
}