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
package com.pump.image.thumbnail;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;

import com.pump.image.ImageSize;
import com.pump.image.pixel.ImageType;
import com.pump.image.pixel.Scaling;
import com.pump.image.thumbnail.BasicThumbnail.Layer;

/**
 * This is an abstract model to format thumbnails.
 * <p>
 * Here "format thumbnails" refers to adding small tasteful decorations to a
 * scaled image to make a professional looking graphic. This class uses the
 * {@link com.pump.image.pixel.Scaling} class to implement the actual scaling.
 * (If all you're interested in in scaling a large image, then please refer to
 * that class, at the <a
 * href="https://javagraphics.blogspot.com/2010/06/images-scaling-down.html">
 * accompanying discussion</a>.)
 */
public abstract class Thumbnail {

	/**
	 * A thumbnail format with no decorations.
	 */
	public static Thumbnail Plain = new BasicThumbnail(new Layer[] {}, 0);

	/**
	 * A set of high-quality rendering hints for the keys ANTIALIASING,
	 * INTERPOLATION, COLOR_RENDER, STROKE, and RENDER.
	 */
	protected static final RenderingHints qualityHints = new RenderingHints(
			RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

	static {
		qualityHints.put(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		qualityHints.put(RenderingHints.KEY_COLOR_RENDERING,
				RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		qualityHints.put(RenderingHints.KEY_STROKE_CONTROL,
				RenderingHints.VALUE_STROKE_PURE);
		qualityHints.put(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
	}

	/**
	 * An image source that can scale to a specific size. This abstract class
	 * may rely on a BufferedImage, Image, File, or URL.
	 */
	protected abstract static class ImageSource {
		/**
		 * Scale the source image to a specific size
		 * 
		 * @param destinationSize
		 *            the exact size to scale the image source to. The image may
		 *            be stretched as needed to fit this size.
		 *            <p>
		 *            It is assumed (but not enforced) that the destinationSize
		 *            will be smaller than the source width/height.
		 * @return a scaled copy of this size.
		 */
		public abstract BufferedImage scale(Dimension destinationSize);

		/** Return the width of the source image. */
		public abstract int getSourceWidth();

		/** Return the height of the source image. */
		public abstract int getSourceHeight();
	}

	private static class BufferedImageSource extends ImageSource {
		BufferedImage bi;

		BufferedImageSource(BufferedImage bi) {
			this.bi = bi;
		}

		@Override
		public int getSourceWidth() {
			return bi.getWidth();
		}

		@Override
		public int getSourceHeight() {
			return bi.getHeight();
		}

		@Override
		public BufferedImage scale(Dimension destinationSize) {
			return Scaling.scale(bi, destinationSize);
		}
	}

	private static class URLImageSource extends ImageSource {
		URL url;
		Dimension size;

		URLImageSource(URL url) {
			this.url = url;
			size = ImageSize.get(url);
		}

		@Override
		public int getSourceWidth() {
			return size.width;
		}

		public int getSourceHeight() {
			return size.height;
		}

		@Override
		public BufferedImage scale(Dimension destinationSize) {
			return Scaling.scale(url, ImageType.INT_ARGB,
					destinationSize);
		}
	}

	private static class ImageImageSource extends ImageSource {
		Image image;
		Dimension size;

		ImageImageSource(Image image) {
			this.image = image;
			size = ImageSize.get(image);
		}

		@Override
		public int getSourceWidth() {
			return size.width;
		}

		@Override
		public int getSourceHeight() {
			return size.height;
		}

		@Override
		public BufferedImage scale(Dimension destinationSize) {
			return Scaling.scale(image, null, destinationSize);
		}
	}

	private static class FileImageSource extends ImageSource {
		File file;
		Dimension size;

		FileImageSource(File file) {
			this.file = file;
			size = ImageSize.get(file);
		}

		@Override
		public int getSourceWidth() {
			return size.width;
		}

		@Override
		public int getSourceHeight() {
			return size.height;
		}

		@Override
		public BufferedImage scale(Dimension destinationSize) {
			return Scaling.scale(file, ImageType.INT_ARGB,
					destinationSize);
		}
	}

	/**
	 * Create a thumbnail from a URL.
	 * 
	 * @param source
	 *            a url for a PNG, JPG, GIF, or BMP image.
	 * @param maxSize
	 *            the maximum dimensions of the thumbnail. Note the returned
	 *            thumbnail may be smaller than these bounds. For example: if
	 *            you have a 4:3 landscape-oriented picture and the maximum size
	 *            is 64x64 pixels, then the return image will be 64 pixels wide
	 *            and approximately 48 pixels tall. (It may not be exactly 48
	 *            pixels tall, depending on the decorations this thumbnail
	 *            format may add.)
	 *            <p>
	 *            If the source image is already smaller than the maxSize, then
	 *            the source image is not scaled to create this thumbnail.
	 * @return an image that is at most maxSize dimensions.
	 */
	public BufferedImage create(URL source, Dimension maxSize) {
		return create(new URLImageSource(source), maxSize);
	}

	/**
	 * Create a thumbnail from an image.
	 * 
	 * @param source
	 *            an image to create a thumbnail for.
	 * @param maxSize
	 *            the maximum dimensions of the thumbnail. Note the returned
	 *            thumbnail may be smaller than these bounds. For example: if
	 *            you have a 4:3 landscape-oriented picture and the maximum size
	 *            is 64x64 pixels, then the return image will be 64 pixels wide
	 *            and approximately 48 pixels tall. (It may not be exactly 48
	 *            pixels tall, depending on the decorations this thumbnail
	 *            format may add.)
	 *            <p>
	 *            If the source image is already smaller than the maxSize, then
	 *            the source image is not scaled to create this thumbnail.
	 * @return an image that is at most maxSize dimensions.
	 */
	public BufferedImage create(Image source, Dimension maxSize) {
		return create(new ImageImageSource(source), maxSize);
	}

	/**
	 * Create a thumbnail from a file.
	 * 
	 * @param source
	 *            a file for a PNG, JPG, GIF, or BMP image.
	 * @param maxSize
	 *            the maximum dimensions of the thumbnail. Note the returned
	 *            thumbnail may be smaller than these bounds. For example: if
	 *            you have a 4:3 landscape-oriented picture and the maximum size
	 *            is 64x64 pixels, then the return image will be 64 pixels wide
	 *            and approximately 48 pixels tall. (It may not be exactly 48
	 *            pixels tall, depending on the decorations this thumbnail
	 *            format may add.)
	 *            <p>
	 *            If the source image is already smaller than the maxSize, then
	 *            the source image is not scaled to create this thumbnail.
	 * @return an image that is at most maxSize dimensions.
	 */
	public BufferedImage create(File source, Dimension maxSize) {
		return create(new FileImageSource(source), maxSize);
	}

	/**
	 * Create a thumbnail from a BufferedImage.
	 * 
	 * @param source
	 *            an image to create a thumbnail for.
	 * @param maxSize
	 *            the maximum dimensions of the thumbnail. Note the returned
	 *            thumbnail may be smaller than these bounds. For example: if
	 *            you have a 4:3 landscape-oriented picture and the maximum size
	 *            is 64x64 pixels, then the return image will be 64 pixels wide
	 *            and approximately 48 pixels tall. (It may not be exactly 48
	 *            pixels tall, depending on the decorations this thumbnail
	 *            format may add.)
	 *            <p>
	 *            If the source image is already smaller than the maxSize, then
	 *            the source image is not scaled to create this thumbnail.
	 * @return an image that is at most maxSize dimensions.
	 */
	public BufferedImage create(BufferedImage source, Dimension maxSize) {
		return create(new BufferedImageSource(source), maxSize);
	}

	/**
	 * Create a thumbnail from an <code>ImageSource</code>.
	 * 
	 * @param source
	 *            the image source.
	 * @param maxSize
	 *            the maximum dimensions of the thumbnail. Note the returned
	 *            thumbnail may be smaller than these bounds. For example: if
	 *            you have a 4:3 landscape-oriented picture and the maximum size
	 *            is 64x64 pixels, then the return image will be 64 pixels wide
	 *            and approximately 48 pixels tall. (It may not be exactly 48
	 *            pixels tall, depending on the decorations this thumbnail
	 *            format may add.)
	 *            <p>
	 *            If the source image is already smaller than the maxSize, then
	 *            the source image is not scaled to create this thumbnail.
	 * @return an image that is at most maxSize dimensions.
	 */
	protected abstract BufferedImage create(ImageSource source,
			Dimension maxSize);
}