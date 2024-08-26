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
package com.pump.image;

import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A factory for {@link ImageContext} objects to render images.
 */
public abstract class ImageContextFactory {
	private static ImageContextFactory factory = new ImageContextFactory() {

		ExecutorService staticService;

		public ImageContext create(BufferedImage bi) {
			synchronized (this) {
				if (staticService == null) {
					staticService = Executors.newFixedThreadPool(8);
				}
			}
			return new BasicImageContext(bi, staticService);
		}
	};

	/**
	 * Return the ImageContextFactory in use. By default this factory creates
	 * {@link BasicImageContext} contexts, but in some environments it might
	 * return alternative models.
	 */
	public static ImageContextFactory get() {
		return factory;
	}

	/**
	 * Assign a new ImageContextFactory.
	 */
	public static void set(ImageContextFactory f) {
		factory = f;
	}

	/**
	 * Create an {@link ImageContext} for a <code>BufferedImage</code>.
	 */
	public abstract ImageContext create(BufferedImage bi);
}