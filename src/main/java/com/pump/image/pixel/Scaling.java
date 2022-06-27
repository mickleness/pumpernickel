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
package com.pump.image.pixel;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.pump.awt.Dimension2D;
import com.pump.image.ImageSize;
import com.pump.image.bmp.BmpDecoderIterator;
import com.pump.image.pixel.converter.IntPixelConverter;
import com.pump.io.FileInputStreamSource;
import com.pump.io.InputStreamSource;
import com.pump.io.URLInputStreamSource;

/**
 * This contains a few static methods for scaling BufferedImages using the
 * {@link com.pump.image.pixel.ScalingIterator}.
 * 
 * @see com.pump.awt.Dimension2D#scaleProportionally(Dimension, Dimension)
 * @see <a href=
 *      "https://javagraphics.blogspot.com/2010/06/images-scaling-down.html">Images:
 *      Scaling Down</a>
 */
public class Scaling {

	/**
	 * This is an image type alternative that indicates we should return
	 * whatever is simplest/most expedient.
	 */
	public static final int TYPE_DEFAULT = GenericImageSinglePassIterator.TYPE_DEFAULT;

	/**
	 * Scales the source image into the destination.
	 * 
	 * @param source
	 *            the source image.
	 * @param dest
	 *            the destination image. This must be smaller than the source
	 *            image or an exception will be thrown. Also this need to be of
	 *            type <code>BufferedImage.TYPE_INT_ARGB</code>.
	 */
	public static void scale(BufferedImage source, BufferedImage dest) {
		scale(source, dest, new Dimension(dest.getWidth(), dest.getHeight()));
	}

	/**
	 * Scales the source image to a new, smaller size.
	 * 
	 * @param source
	 *            the source image.
	 * @param w
	 *            the new width. This must be less than the width of the source
	 *            image, or an exception will be thrown.
	 * @param h
	 *            the new height. This must be less than the height of the
	 *            source image, or an exception will be thrown.
	 * @return a new scaled image of type
	 *         <code>BufferedImage.TYPE_INT_ARGB</code>.
	 */
	public static BufferedImage scale(BufferedImage source, int w, int h) {
		return scale(source, null, new Dimension(w, h));
	}

	/**
	 * Scales the source image to a new, smaller size.
	 * 
	 * @param source
	 *            the source image.
	 * @param destSize
	 *            the size of the new image. This must be smaller than the size
	 *            of the source image, or an exception will be thrown.
	 * @return a new scaled image of type
	 *         <code>BufferedImage.TYPE_INT_ARGB</code> or
	 *         <code>BufferedImage.TYPE_INT_RGB</code>.
	 */
	public static BufferedImage scale(BufferedImage source,
			Dimension destSize) {
		return scale(source, null, destSize);
	}

	/**
	 * Scales the source image file to a new, smaller size.
	 * 
	 * @param source
	 *            the source image file.
	 * @param imageType
	 *            <code>TYPE_INT_RGB</code>, <code>TYPE_INT_ARGB</code>,
	 *            <code>TYPE_3BYTE_BGR</code>, <code>TYPE_4BYTE_ABGR</code>.
	 * @param destSize
	 *            the size of the new image.
	 * @return a new scaled image of type
	 *         <code>BufferedImage.TYPE_INT_ARGB</code> or
	 *         <code>BufferedImage.TYPE_INT_RGB</code>.
	 */
	public static BufferedImage scale(File source, int imageType,
			Dimension destSize) {
		return scale(source.getName(), new FileInputStreamSource(source),
				imageType, destSize);
	}

	/**
	 * Scales the source image file to a new size.
	 * 
	 * @param source
	 *            the source image file.
	 * @param preferredType
	 *            <code>TYPE_INT_RGB</code>, <code>TYPE_INT_ARGB</code>,
	 *            <code>TYPE_3BYTE_BGR</code>, <code>TYPE_4BYTE_ABGR</code>.
	 * @param destSize
	 *            the size of the new image.
	 * @return a new scaled image of type
	 *         <code>BufferedImage.TYPE_INT_ARGB</code> or
	 *         <code>BufferedImage.TYPE_INT_RGB</code>.
	 */
	public static BufferedImage scale(URL source, int imageType,
			Dimension destSize) {
		return scale(source.toString(), new URLInputStreamSource(source),
				imageType, destSize);
	}

	private static BufferedImage scale(String name, InputStreamSource src,
			int imageType, Dimension destSize) {
		String pathLower = name.toLowerCase();
		if (pathLower.endsWith(".bmp")) {
			try (InputStream in = src.createInputStream()) {
				PixelIterator iter = BmpDecoderIterator.get(in);
				PixelIterator scalingIter = destSize == null ? iter
						: ScalingIterator.get(iter, destSize.width,
								destSize.height);
				PixelIterator finalIter = ImageType.get(imageType)
						.createConverter(scalingIter);

				BufferedImage image = BufferedImageIterator.create(finalIter,
						null);
				return image;
			} catch (IOException e) {
				return null;
			}
		}

		Image image;
		if (src instanceof FileInputStreamSource) {
			File file = ((FileInputStreamSource) src).getFile();
			image = Toolkit.getDefaultToolkit()
					.createImage(file.getAbsolutePath());
		} else if (src instanceof URLInputStreamSource) {
			URL url = ((URLInputStreamSource) src).getURL();
			image = Toolkit.getDefaultToolkit().createImage(url);
		} else {
			throw new IllegalStateException(src.getClass().getName());
		}
		try {
			if (imageType == TYPE_DEFAULT) {
				return scale(image, null, destSize);
			} else {
				BufferedImage dest = new BufferedImage(destSize.width,
						destSize.height, imageType);
				return scale(image, dest, null);
			}
		} finally {
			if (image != null)
				image.flush();
		}
	}

	/**
	 * Scales the source image into the dest.
	 * 
	 * @param source
	 *            the source image. This may not be null.
	 * @param dest
	 *            the destination image. If non-null: this image must be at
	 *            least <code>destSize</code> pixels in size or an exception
	 *            will be thrown. If this is null: an image will be created that
	 *            is <code>destSize</code> pixels.
	 *            <p>
	 *            This argument can be the same as the <code>source</code>
	 *            argument. This may save some memory allocation, but it will
	 *            permanently alter the source image.
	 *            <p>
	 *            Also this need to be of type
	 *            <code>BufferedImage.TYPE_INT_ARGB</code>.
	 * @param destSize
	 *            the dimensions to write to. It is guaranteed that these pixels
	 *            will be replaced in the dest image.
	 * @return the <code>dest</code> argument, or a new image if no
	 *         <code>dest</code> argument was provided.
	 */
	public static BufferedImage scale(BufferedImage source, BufferedImage dest,
			Dimension destSize) {
		if (destSize == null && dest != null) {
			destSize = new Dimension(dest.getWidth(), dest.getHeight());
		}

		if (source == null) {
			throw new NullPointerException("no source image");
		} else if (destSize == null) {
			throw new NullPointerException("no dest size");
		} else if (dest == null) {
			if (source.getColorModel().hasAlpha()) {
				dest = new BufferedImage(destSize.width, destSize.height,
						BufferedImage.TYPE_INT_ARGB);
			} else {
				dest = new BufferedImage(destSize.width, destSize.height,
						BufferedImage.TYPE_INT_RGB);
			}
		}

		PixelIterator pi = ScalingIterator.get(
				BufferedImageIterator.get(source), destSize.width,
				destSize.height);
		if (pi instanceof BytePixelIterator) {
			pi = ImageType.INT_ARGB.createConverter(pi);
		}
		IntPixelIterator i = (IntPixelIterator) pi;
		int[] row = new int[i.getMinimumArrayLength()];
		if (i.isTopDown()) {
			for (int y = 0; y < destSize.height; y++) {
				i.next(row);
				dest.getRaster().setDataElements(0, y, destSize.width, 1, row);
			}
		} else {
			for (int y = destSize.height - 1; y >= 0; y--) {
				i.next(row);
				dest.getRaster().setDataElements(0, y, destSize.width, 1, row);
			}
		}
		return dest;
	}

	/**
	 * Scales the source image into the dest.
	 * 
	 * @param source
	 *            the source image. This may not be null.
	 * @param dest
	 *            the destination image. If non-null: this image must be at
	 *            least <code>destSize</code> pixels in size or an exception
	 *            will be thrown. If this is null: an image will be created that
	 *            is <code>destSize</code> pixels.
	 *            <p>
	 *            This argument can be the same as the <code>source</code>
	 *            argument. This may save some memory allocation, but it will
	 *            permanently alter the source image.
	 *            <p>
	 *            Also this need to be of type
	 *            <code>BufferedImage.TYPE_INT_ARGB</code>.
	 * @param destSize
	 *            the dimensions to write to. It is guaranteed that these pixels
	 *            will be replaced in the dest image. If this is null then the
	 *            image will not be scaled.
	 * @return the <code>dest</code> argument, or a new image if no
	 *         <code>dest</code> argument was provided.
	 */
	public static BufferedImage scale(Image source, BufferedImage dest,
			Dimension destSize) {
		if (source instanceof BufferedImage) {
			return scale((BufferedImage) source, dest, destSize);
		} else if (source == null) {
			throw new NullPointerException("no source image");
		}

		if (destSize == null) {
			if (dest != null) {
				destSize = new Dimension(dest.getWidth(), dest.getHeight());
			} else {
				throw new NullPointerException();
			}
		}

		Dimension sourceSize = ImageSize.get(source);

		if (destSize.width > sourceSize.width) {
			throw new IllegalArgumentException("dest width (" + destSize.width
					+ ") must be less than source width (" + sourceSize.width
					+ ")");
		} else if (destSize.height > sourceSize.height) {
			throw new IllegalArgumentException("dest height (" + destSize.height
					+ ") must be less than source height (" + sourceSize.height
					+ ")");
		} else if (dest != null && destSize.width > dest.getWidth()) {
			throw new IllegalArgumentException("dest width (" + destSize.width
					+ ") must not exceed the destination image width ("
					+ dest.getWidth() + ")");
		} else if (dest != null && destSize.height > dest.getHeight()) {
			throw new IllegalArgumentException("dest height (" + destSize.height
					+ ") must not exceed the destination image height ("
					+ dest.getHeight() + ")");
		}

		int destType = dest != null ? dest.getType()
				: GenericImageSinglePassIterator.TYPE_DEFAULT;
		PixelIterator iter = GenericImageSinglePassIterator.get(source,
				destType);
		PixelIterator scalingIter = destSize == null ? iter
				: ScalingIterator.get(iter, destSize.width, destSize.height);
		return BufferedImageIterator.create(scalingIter, null);
	}

	/**
	 * Scales the source image proportionally to a new, smaller size.
	 * <p>
	 * The new image will either have a width of <code>maxWidth</code> or a
	 * height of <code>maxHeight</code> (or both).
	 * 
	 * @param image
	 *            the source image to scale.
	 * @param maxWidth
	 *            the maximum width the scaled image can be.
	 * @param maxHeight
	 *            the maximum height the scaled image can be.
	 * @return a new scaled image.
	 */
	public static BufferedImage scaleProportionally(BufferedImage image,
			int maxWidth, int maxHeight) {
		return scaleProportionally(image, new Dimension(maxWidth, maxHeight));
	}

	/**
	 * Scales the source image proportionally to a new, smaller size.
	 * <p>
	 * The new image will either have a height of <code>maxWidth</code> or a
	 * height of <code>maxHeight</code> (or both).
	 * 
	 * @param image
	 *            the source image to scale.
	 * @param maxSize
	 *            the maximum dimensions the scaled image can be.
	 * @return a new scaled image.
	 */
	public static BufferedImage scaleProportionally(BufferedImage image,
			Dimension maxSize) {
		Dimension d = Dimension2D.scaleProportionally(
				new Dimension(image.getWidth(), image.getHeight()), maxSize);
		return scale(image, d.width, d.height);
	}

	/**
	 * Return true if a filename ends with PNG, BMP, JPG, JPEG or GIF.
	 * 
	 * @param file
	 * @return
	 */
	public static boolean isSupported(File file) {
		String filename = file.getName();
		int i = filename.lastIndexOf('.');
		if (i == -1)
			return false;
		filename = filename.toLowerCase().substring(i + 1);
		return filename.equals("png") || filename.equals("jpg")
				|| filename.equals("jpeg") || filename.equals("bmp")
				|| filename.endsWith("gif");
	}
}