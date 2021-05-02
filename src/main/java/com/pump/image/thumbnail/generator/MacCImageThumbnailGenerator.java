package com.pump.image.thumbnail.generator;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import com.pump.awt.Dimension2D;

/**
 * This ThumbnailGenerator uses reflection to access
 * <code>sun.lwawt.macosx.CImage</code> methods on Mac. This will throw an
 * exception if you attempt to call this on a non-Mac. (Or even on a Mac: this
 * may throw an exception over time if classes/methods change.)
 */
public class MacCImageThumbnailGenerator implements ThumbnailGenerator {

	private static boolean INITIALIZED = false;
	private static Throwable INITIALIZATION_ERROR;

	private static Method method_nativeCreateNSImageFromFileContents,
			method_nativeGetNSImageSize, method_resize, method_toImage;
	private static Constructor<?> constructor_cimage;

	@SuppressWarnings("unchecked")
	private synchronized static void initialize() {
		try {
			if (INITIALIZED)
				return;
			INITIALIZED = true;

			Class<?> cimage = Class.forName("sun.lwawt.macosx.CImage");
			method_nativeCreateNSImageFromFileContents = cimage
					.getDeclaredMethod("nativeCreateNSImageFromFileContents",
							new Class[] { String.class });
			method_nativeGetNSImageSize = cimage.getDeclaredMethod(
					"nativeGetNSImageSize", new Class[] { Long.TYPE });
			method_resize = cimage.getDeclaredMethod("resize",
					new Class[] { Double.TYPE, Double.TYPE });
			method_toImage = cimage.getDeclaredMethod("toImage", new Class[] {
					Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE });

			constructor_cimage = cimage
					.getDeclaredConstructor(new Class[] { Long.TYPE });

			method_nativeCreateNSImageFromFileContents.setAccessible(true);
			method_nativeGetNSImageSize.setAccessible(true);
			method_resize.setAccessible(true);
			method_toImage.setAccessible(true);
			constructor_cimage.setAccessible(true);

		} catch (Throwable t) {
			INITIALIZATION_ERROR = t;
		}
	}

	/**
	 * Return true if this class can access all the Methods required to attempt
	 * to create thumbnails.
	 */
	public static boolean isInitialized() {
		initialize();
		return INITIALIZATION_ERROR == null;
	}

	public MacCImageThumbnailGenerator() {
		initialize();
	}

	@Override
	public BufferedImage createThumbnail(File file, int requestedMaxImageSize)
			throws Exception {
		if (INITIALIZATION_ERROR != null) {
			if (INITIALIZATION_ERROR instanceof Exception)
				throw (Exception) INITIALIZATION_ERROR;
			throw new RuntimeException(INITIALIZATION_ERROR);
		}

		long nsimagePtr = (Long) method_nativeCreateNSImageFromFileContents
				.invoke(null, file.getAbsolutePath());

		if (requestedMaxImageSize <= 0)
			requestedMaxImageSize = MAX_SIZE_DEFAULT;

		Object cimg = constructor_cimage.newInstance(nsimagePtr);

		java.awt.geom.Dimension2D size = (java.awt.geom.Dimension2D) method_nativeGetNSImageSize
				.invoke(null, nsimagePtr);
		if (size.getWidth() <= 0 || size.getHeight() <= 0)
			return null;

		Dimension imageSize = new Dimension(Math.round((float) size.getWidth()),
				Math.round((float) size.getHeight()));
		Dimension maxSize = new Dimension(requestedMaxImageSize,
				requestedMaxImageSize);
		Dimension thumbnailSize = Dimension2D.scaleProportionally(imageSize,
				maxSize, true);

		// thumbnailSize will be null if the image is smaller than maxSize:
		if (thumbnailSize == null) {
			thumbnailSize = imageSize;
		}
		Object z = method_toImage.invoke(cimg, new Object[] { imageSize.width,
				imageSize.height, thumbnailSize.width, thumbnailSize.height });
		return (BufferedImage) z;
	}

}
