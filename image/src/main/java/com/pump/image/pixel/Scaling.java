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
import java.io.*;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;

import com.pump.image.jpeg.JPEGMetaData;
import com.pump.image.jpeg.JPEGMetaDataListener;
import com.pump.io.FileInputStreamSource;
import com.pump.io.InputStreamSource;
import com.pump.io.URLInputStreamSource;
import com.pump.image.QBufferedImage;
import com.pump.image.bmp.BmpDecoderIterator;

/**
 * This contains a few static methods for scaling images using the
 * {@link ScalingIterator}.
 *
 * @see <a href=
 *      "https://javagraphics.blogspot.com/2010/06/images-scaling-down.html">Images:
 *      Scaling Down</a>
 */
public class Scaling {

	private static class ProportionalSizeFunction implements BiFunction<Dimension, Boolean, Dimension> {

		/**
		 * This is a convenience method to calculate how to scale down an image
		 * proportionally.
		 *
		 * @param originalSize
		 *            the original image dimensions.
		 * @param maxSize
		 *            the maximum new dimensions.
		 * @return dimensions that are <code>maxSize</code> or smaller.
		 */
		private static Dimension scaleProportionally(Dimension originalSize,
													Dimension maxSize) {
			float widthRatio = ((float) maxSize.width)
					/ ((float) originalSize.width);
			float heightRatio = ((float) maxSize.height)
					/ ((float) originalSize.height);
			int w, h;
			if (widthRatio < heightRatio) {
				w = maxSize.width;
				h = (int) (widthRatio * originalSize.height);
			} else {
				h = maxSize.height;
				w = (int) (heightRatio * originalSize.width);
			}

			return new Dimension(w, h);
		}

		private final int maxWidth, maxHeight;

		private ProportionalSizeFunction(Dimension size) {
			maxWidth = size.width;
			maxHeight = size.height;
		}

		@Override
		public Dimension apply(Dimension srcSize, Boolean isEmbeddedThumbnail) {
			if (isEmbeddedThumbnail && srcSize.width < maxWidth && srcSize.height < maxHeight)
				return null;

			return scaleProportionally(srcSize, new Dimension(maxWidth, maxHeight));
		}
	}

	private static class ConstantSizeFunction implements BiFunction<Dimension, Boolean, Dimension> {
		private final int width, height;

		private ConstantSizeFunction(Dimension size) {
			width = size.width;
			height = size.height;
		}

		@Override
		public Dimension apply(Dimension srcSize, Boolean isEmbeddedThumbnail) {
			if (isEmbeddedThumbnail && srcSize.width < width && srcSize.height < height)
				return null;

			return new Dimension(width, height);
		}
	}

	/**
	 * Scale an image file. If possible this may scale an embedded thumbnail instead of the whole image.
	 *
	 * @param imageFile the image file to scale.
	 *
	 * @param sizeFunction the optional size function.
	 *                     <p>
	 *                     The first argument is the size of an available source image. The second argument
	 *                     is a boolean indicating whether this source image is an embedded thumbnail or not.
	 *                     </p>
	 *                     <p>
	 *                     This may return null, which indicates that the source image under consideration
	 *                     should be ignored. As soon as this returns a non-null value: that candidate is used
	 *                     and any future candidates are ignored, and this function will be not be called again.
	 *                     </p>
	 *                     <p>
	 *                     This may be called any number of times (including zero times) where the Boolean argument
	 *                     is true. If all of those attempts are rejected (because this function returned null), then
	 *                     this function is guaranteed to be consulted exactly once where the Boolean argument
	 *                     is false.
	 *                     </p>
	 *                     <p>
	 *                     For example: consider a large JPEG file with an embedded thumbnail that is 100 x 75.
	 *                     If you want to create thumbnails that are 40 x 30, then this function
	 *                     should return 40 x 30. Now the embedded thumbnail will be scaled, and the source image will
	 *                     not be read further. But if you want to create thumbnails that are 200 x 150, then this
	 *                     function should return null to skip this candidate. This function will then be called again
	 *                     (later) with the full dimensions of the source image (maybe 4000 x 3000), and it can return
	 *                     200 x 150 to request a scaled image.
	 *                     </p>
	 *                     <p>
	 *                     If this function returns null for ALL candidates, then this method returns null.
	 *                     <p>
	 *                     If this argument is null then the source image size is used as-is. This is an unlikely
	 *                     usage of this method, but you could use it to change the image type or rewrite the source
	 *                     image to another target BufferedImage.
	 *                     </p>
	 *
	 * @param destType the optional output image type.
	 *                 <p>
	 *                 If this is null then the returned image type may be the same as the
	 *                 source image type, or it may be whatever is fastest/easiest to compute.
	 *
	 * @param dest the optional BufferedImage to write the scaled image data to.
	 *             <p>
	 *             If this is non-null then it must be large enough to contain the scaled image
	 *             (or else an exception is thrown). This may be larger than the scaled image.
	 *             </p>
	 *             <p>
	 *             If this is non-null AND destType is non-null AND the two conflict: then
	 *             an exception is thrown.
	 *             </p>
	 *
	 * @return a QBufferedImage that is a scaled copy of the source image. If <code>dest</code>
	 * was non-null then this QBufferedImage will use the same raster as  <code>dest</code>.
	 *
	 * @throws IOException if an IOException occurs while reading the file.
	 */
	public static QBufferedImage scale(File imageFile, BiFunction<Dimension, Boolean, Dimension> sizeFunction, ImageType destType, BufferedImage dest) throws IOException {
		Scaling helper = new Scaling(imageFile);
		helper.initialize(sizeFunction, destType, dest);
		return helper.scaleImage();
	}

	/**
	 * Scale an image file. If possible this may scale an embedded thumbnail instead of the whole image.
	 *
	 * @param imageFile the image file to scale.
	 *
	 * @param destSize the optional size to scale the source image to.
	 *                     <p>
	 *                     If this is null then the source image's original dimensions are used.
	 *                     </p>
	 *
	 * @param destType the optional output image type.
	 *                 <p>
	 *                 If this is null then the returned image type may be the same as the
	 *                 source image type, or it may be whatever is fastest/easiest to compute.
	 *
	 * @param dest the optional BufferedImage to write the scaled image data to.
	 *             <p>
	 *             If this is non-null then it must be large enough to contain the scaled image
	 *             (or else an exception is thrown). This may be larger than the scaled image.
	 *             </p>
	 *             <p>
	 *             If this is non-null AND destType is non-null AND the two conflict: then
	 *             an exception is thrown.
	 *             </p>
	 *
	 * @return a QBufferedImage that is a scaled copy of the source image. If <code>dest</code>
	 * was non-null then this QBufferedImage will use the same raster as  <code>dest</code>.
	 *
	 * @throws IOException if an IOException occurs while reading the file.
	 */
	public static QBufferedImage scale(File imageFile, Dimension destSize, ImageType destType, BufferedImage dest) throws IOException {
		if (destSize == null && dest != null)
			destSize = new Dimension(dest.getWidth(), dest.getHeight());
		BiFunction<Dimension, Boolean, Dimension> sizeFunction = destSize == null ? null : new ConstantSizeFunction(destSize);
		return scale(imageFile, sizeFunction, destType, dest);
	}

	/**
	 * Scale an image. If possible this may scale an embedded thumbnail instead of the whole image.
	 *
	 * @param imageURL the URL of the image to scale.
	 *
	 * @param sizeFunction the optional size function.
	 *                     <p>
	 *                     The first argument is the size of an available source image. The second argument
	 *                     is a boolean indicating whether this source image is an embedded thumbnail or not.
	 *                     </p>
	 *                     <p>
	 *                     This may return null, which indicates that the source image under consideration
	 *                     should be ignored. As soon as this returns a non-null value: that candidate is used
	 *                     and any future candidates are ignored, and this function will be not be called again.
	 *                     </p>
	 *                     <p>
	 *                     This may be called any number of times (including zero times) where the Boolean argument
	 *                     is true. If all of those attempts are rejected (because this function returned null), then
	 *                     this function is guaranteed to be consulted exactly once where the Boolean argument
	 *                     is false.
	 *                     </p>
	 *                     <p>
	 *                     For example: consider a large JPEG file with an embedded thumbnail that is 100 x 75.
	 *                     If you want to create thumbnails that are 40 x 30, then this function
	 *                     should return 40 x 30. Now the embedded thumbnail will be scaled, and the source image will
	 *                     not be read further. But if you want to create thumbnails that are 200 x 150, then this
	 *                     function should return null to skip this candidate. This function will then be called again
	 *                     (later) with the full dimensions of the source image (maybe 4000 x 3000), and it can return
	 *                     200 x 150 to request a scaled image.
	 *                     </p>
	 *                     <p>
	 *                     If this function returns null for ALL candidates, then this method returns null.
	 *                     <p>
	 *                     If this argument is null then the source image file size is used as-is. This is an unlikely
	 *                     usage of this method, but you could use it to change the image type or rewrite the source
	 *                     image to another target BufferedImage.
	 *                     </p>
	 *
	 * @param destType the optional output image type.
	 *                 <p>
	 *                 If this is null then the returned image type may be the same as the
	 *                 source image type, or it may be whatever is fastest/easiest to compute.
	 *
	 * @param dest the optional BufferedImage to write the scaled image data to.
	 *             <p>
	 *             If this is non-null then it must be large enough to contain the scaled image
	 *             (or else an exception is thrown). This may be larger than the scaled image.
	 *             </p>
	 *             <p>
	 *             If this is non-null AND destType is non-null AND the two conflict: then
	 *             an exception is thrown.
	 *             </p>
	 *
	 * @return a QBufferedImage that is a scaled copy of the source image. If <code>dest</code>
	 * was non-null then this QBufferedImage will use the same raster as  <code>dest</code>.
	 *
	 * @throws IOException if an IOException occurs while reading the URL.
	 */
	public static QBufferedImage scale(URL imageURL, BiFunction<Dimension, Boolean, Dimension> sizeFunction, ImageType destType, BufferedImage dest) throws IOException {
		Scaling helper = new Scaling(imageURL);
		helper.initialize(sizeFunction, destType, dest);
		return helper.scaleImage();
	}

	/**
	 * Scale an image. If possible this may scale an embedded thumbnail instead of the whole image.
	 *
	 * @param imageURL the URL of the image to scale.
	 *
	 * @param destSize the optional size to scale the source image to.
	 *                     <p>
	 *                     If this is null then the source image's original dimensions are used.
	 *                     </p>
	 *
	 * @param destType the optional output image type.
	 *                 <p>
	 *                 If this is null then the returned image type may be the same as the
	 *                 source image type, or it may be whatever is fastest/easiest to compute.
	 *
	 * @param dest the optional BufferedImage to write the scaled image data to.
	 *             <p>
	 *             If this is non-null then it must be large enough to contain the scaled image
	 *             (or else an exception is thrown). This may be larger than the scaled image.
	 *             </p>
	 *             <p>
	 *             If this is non-null AND destType is non-null AND the two conflict: then
	 *             an exception is thrown.
	 *             </p>
	 *
	 * @return a QBufferedImage that is a scaled copy of the source image. If <code>dest</code>
	 * was non-null then this QBufferedImage will use the same raster as  <code>dest</code>.
	 *
	 * @throws IOException if an IOException occurs while reading the URL.
	 */
	public static QBufferedImage scale(URL imageURL, Dimension destSize, ImageType destType, BufferedImage dest) throws IOException {
		if (destSize == null && dest != null)
			destSize = new Dimension(dest.getWidth(), dest.getHeight());
		BiFunction<Dimension, Boolean, Dimension> sizeFunction = destSize == null ? null : new ConstantSizeFunction(destSize);
		return scale(imageURL, sizeFunction, destType, dest);
	}

	/**
	 * Scale an image.
	 *
	 * @param image the image to scale.
	 *
	 * @param sizeFunction the optional size function.
	 *                     <p>
	 *                     The first argument is the size of an available source image. The second argument
	 *                     is a boolean indicating whether this source image is an embedded thumbnail or not.
	 *                     </p>
	 *                     <p>
	 *                     The second argument of this function will always be <code>false</code> when you use
	 *                     this method, because this method refers to an existing Image. Embedded thumbnails
	 *                     can only be consulted when the image source if a File or URL.
	 *                     </p>
	 *                     <p>
	 *                     This may return null, which indicates that this method will return null
	 *                     because the source image under consideration isn't acceptable.
	 *                     <p>
	 *                     If this argument is null then the source image size is used as-is. This is an unlikely
	 *                     usage of this method, but you could use it to change the image type or rewrite the source
	 *                     image to another target BufferedImage.
	 *                     </p>
	 *
	 * @param destType the optional output image type.
	 *                 <p>
	 *                 If this is null then the returned image type may be the same as the
	 *                 source image type, or it may be whatever is fastest/easiest to compute.
	 *
	 * @param dest the optional BufferedImage to write the scaled image data to.
	 *             <p>
	 *             If this is non-null then it must be large enough to contain the scaled image
	 *             (or else an exception is thrown). This may be larger than the scaled image.
	 *             </p>
	 *             <p>
	 *             If this is non-null AND destType is non-null AND the two conflict: then
	 *             an exception is thrown.
	 *             </p>
	 *
	 * @return a QBufferedImage that is a scaled copy of the source image. If <code>dest</code>
	 * was non-null then this QBufferedImage will use the same raster as  <code>dest</code>.
	 */
	public static QBufferedImage scale(Image image, BiFunction<Dimension, Boolean, Dimension> sizeFunction, ImageType destType, BufferedImage dest) {
		Scaling helper = new Scaling(image);
		helper.initialize(sizeFunction, destType, dest);
		try {
			return helper.scaleImage();
		} catch (IOException e) {
			// We shouldn't get an IOException if the Image already exists
			throw new RuntimeException(e);
		}
	}

	/**
	 * Scale an image.
	 *
	 * @param image the image to scale.
	 *
	 * @param destSize the optional size to scale the source image to.
	 *                     <p>
	 *                     If this is null then the source image's original dimensions are used.
	 *                     </p>
	 *
	 * @param destType the optional output image type.
	 *                 <p>
	 *                 If this is null then the returned image type may be the same as the
	 *                 source image type, or it may be whatever is fastest/easiest to compute.
	 *
	 * @param dest the optional BufferedImage to write the scaled image data to.
	 *             <p>
	 *             If this is non-null then it must be large enough to contain the scaled image
	 *             (or else an exception is thrown). This may be larger than the scaled image.
	 *             </p>
	 *             <p>
	 *             If this is non-null AND destType is non-null AND the two conflict: then
	 *             an exception is thrown.
	 *             </p>
	 *
	 * @return a QBufferedImage that is a scaled copy of the source image. If <code>dest</code>
	 * was non-null then this QBufferedImage will use the same raster as  <code>dest</code>.
	 */
	public static QBufferedImage scale(Image image, Dimension destSize, ImageType destType, BufferedImage dest) {
		if (destSize == null && dest != null)
			destSize = new Dimension(dest.getWidth(), dest.getHeight());
		BiFunction<Dimension, Boolean, Dimension> sizeFunction = destSize == null ? null : new ConstantSizeFunction(destSize);
		return scale(image, sizeFunction, destType, dest);
	}

	/**
	 * Scale an image from a file proportionally.
	 *
	 * @param imageFile the image file to scale.
	 * @param maxDestSize the optional maximum size of the thumbnail. For example: if this is 100 x 100
	 *                    and the image is a 4:3 landscape image, then it will return 100 x 75.
	 *                     <p>
	 *                     If this is null then the source image's original dimensions are used.
	 *                     </p>
	 *
	 * @param destType the optional output image type.
	 *                 <p>
	 *                 If this is null then the returned image type may be the same as the
	 *                 source image type, or it may be whatever is fastest/easiest to compute.
	 *
	 * @param dest the optional BufferedImage to write the scaled image data to.
	 *             <p>
	 *             If this is non-null then it must be large enough to contain the scaled image
	 *             (or else an exception is thrown). This may be larger than the scaled image.
	 *             </p>
	 *             <p>
	 *             If this is non-null AND destType is non-null AND the two conflict: then
	 *             an exception is thrown.
	 *             </p>
	 *
	 * @return a QBufferedImage that is a scaled copy of the source image. If <code>dest</code>
	 * was non-null then this QBufferedImage will use the same raster as  <code>dest</code>.
	 *
	 * @throws IOException if an IOException occurs while reading the file.
	 */
	public static QBufferedImage scaleProportionally(File imageFile, Dimension maxDestSize, ImageType destType, BufferedImage dest) throws IOException {
		BiFunction<Dimension, Boolean, Dimension> sizeFunction = maxDestSize == null ? null : new ProportionalSizeFunction(maxDestSize);
		return scale(imageFile, sizeFunction, destType, dest);
	}


	/**
	 * Scale an image from a URL proportionally.
	 *
	 * @param imageURL the URL of the image to scale.
	 *
	 * @param maxDestSize the optional maximum size of the thumbnail. For example: if this is 100 x 100
	 *                    and the image is a 4:3 landscape image, then it will return 100 x 75.
	 *                     <p>
	 *                     If this is null then the source image's original dimensions are used.
	 *                     </p>
	 *
	 * @param destType the optional output image type.
	 *                 <p>
	 *                 If this is null then the returned image type may be the same as the
	 *                 source image type, or it may be whatever is fastest/easiest to compute.
	 *
	 * @param dest the optional BufferedImage to write the scaled image data to.
	 *             <p>
	 *             If this is non-null then it must be large enough to contain the scaled image
	 *             (or else an exception is thrown). This may be larger than the scaled image.
	 *             </p>
	 *             <p>
	 *             If this is non-null AND destType is non-null AND the two conflict: then
	 *             an exception is thrown.
	 *             </p>
	 *
	 * @return a QBufferedImage that is a scaled copy of the source image. If <code>dest</code>
	 * was non-null then this QBufferedImage will use the same raster as  <code>dest</code>.
	 *
	 * @throws IOException if an IOException occurs while reading the URL.
	 */
	public static QBufferedImage scaleProportionally(URL imageURL, Dimension maxDestSize, ImageType destType, BufferedImage dest) throws IOException {
		BiFunction<Dimension, Boolean, Dimension> sizeFunction = maxDestSize == null ? null : new ProportionalSizeFunction(maxDestSize);
		return scale(imageURL, sizeFunction, destType, dest);
	}


	/**
	 * Scale an image proportionally.
	 *
	 * @param image the image to scale.
	 *
	 * @param maxDestSize the optional maximum size of the thumbnail. For example: if this is 100 x 100
	 *                    and the image is a 4:3 landscape image, then it will return 100 x 75.
	 *                     <p>
	 *                     If this is null then the source image's original dimensions are used.
	 *                     </p>
	 *
	 * @param destType the optional output image type.
	 *                 <p>
	 *                 If this is null then the returned image type may be the same as the
	 *                 source image type, or it may be whatever is fastest/easiest to compute.
	 *
	 * @param dest the optional BufferedImage to write the scaled image data to.
	 *             <p>
	 *             If this is non-null then it must be large enough to contain the scaled image
	 *             (or else an exception is thrown). This may be larger than the scaled image.
	 *             </p>
	 *             <p>
	 *             If this is non-null AND destType is non-null AND the two conflict: then
	 *             an exception is thrown.
	 *             </p>
	 *
	 * @return a QBufferedImage that is a scaled copy of the source image. If <code>dest</code>
	 * was non-null then this QBufferedImage will use the same raster as  <code>dest</code>.
	 *
	 * @throws IOException
	 */
	public static QBufferedImage scaleProportionally(Image image, Dimension maxDestSize, ImageType destType, BufferedImage dest) {
		BiFunction<Dimension, Boolean, Dimension> sizeFunction = maxDestSize == null ? null : new ProportionalSizeFunction(maxDestSize);
		return scale(image, sizeFunction, destType, dest);
	}

	private final Image image;
	private final File imageFile;
	private final URL imageURL;
	private final boolean isBMP;

	private BiFunction<Dimension, Boolean, Dimension> sizeFunction;
	private ImageType destType;
	private BufferedImage dest;

	private Scaling(Image image) {
		this.image = Objects.requireNonNull(image);
		imageFile = null;
		imageURL = null;
		isBMP = false;
	}

	private void initialize(BiFunction<Dimension, Boolean, Dimension> sizeFunction, ImageType destType, BufferedImage dest) {
		this.dest = dest;
		this.sizeFunction = sizeFunction;
		this.destType = destType;
	}

	private Scaling(URL imageURL) {
		image = null;
		imageFile = null;
		this.imageURL = Objects.requireNonNull(imageURL);
		isBMP = imageURL.toString().toLowerCase().endsWith(".bmp");
	}

	private Scaling(File imageFile) {
		image = null;
		this.imageFile = Objects.requireNonNull(imageFile);
		imageURL = null;
		isBMP = imageFile.getAbsolutePath().toLowerCase().endsWith(".bmp");
	}

	private QBufferedImage scaleImage() throws IOException {
		if (isBMP) {
			return scaleImage_BMP();
		}

		PixelIterator srcIter;
		Image flushableImage = null;
		try {
			if (image instanceof BufferedImage) {
				BufferedImage bi = (BufferedImage) image;
				srcIter = BufferedImageIterator.create(bi);
			} else if (image != null) {
				srcIter = new ImagePixelIterator(image);
			} else if (imageFile != null) {
				QBufferedImage scaledEmbeddedThumbnail = getScaledEmbeddedThumbnail(imageFile.getName(), new FileInputStreamSource(imageFile));
				if (scaledEmbeddedThumbnail != null)
					return scaledEmbeddedThumbnail;

				flushableImage = Toolkit.getDefaultToolkit().createImage(imageFile.getAbsolutePath());
				srcIter = new ImagePixelIterator(flushableImage);
			} else if (imageURL != null) {
				QBufferedImage scaledEmbeddedThumbnail = getScaledEmbeddedThumbnail(imageURL.getPath(), new URLInputStreamSource(imageURL));
				if (scaledEmbeddedThumbnail != null)
					return scaledEmbeddedThumbnail;

				flushableImage = Toolkit.getDefaultToolkit().createImage(imageURL);
				srcIter = new ImagePixelIterator(flushableImage);
			} else {
				// our constructors should safeguard us from this ever happening
				throw new IllegalStateException();
			}

			return scaleImage(srcIter);
		} finally {
			if (flushableImage != null)
				flushableImage.flush();
		}
	}

	private QBufferedImage getScaledEmbeddedThumbnail(String filename, InputStreamSource inputStreamSource) throws IOException {
		String str = filename.toLowerCase();
		if (!(str.endsWith(".jpg") || str.endsWith(".jpeg")))
			return null;
		if (sizeFunction == null)
			return null;

		AtomicReference<BufferedImage> thumbnail = new AtomicReference<>();
		JPEGMetaDataListener listener = new JPEGMetaDataListener() {

			@Override
			public boolean isThumbnailAccepted(String markerName, int width,
											   int height) {
				Dimension scaledSize = sizeFunction.apply(new Dimension(width, height), true);
				return scaledSize != null;
			}

			@Override
			public void addThumbnail(String markerName, BufferedImage bi) {
				thumbnail.set(bi);
			}

			@Override
			public void addProperty(String markerName, String propertyName,
									Object value) {
				// intentionally empty
			}

			@Override
			public void addComment(String markerName, String comment) {
				// intentionally empty
			}

			@Override
			public void endFile() {
				// intentionally empty
			}

			@Override
			public void imageDescription(int bitsPerPixel, int width, int height, int numberOfComponents) {
				// intentionally empty
			}

			@Override
			public void processException(Exception e, String markerCode) {
				// intentionally empty
			}

			@Override
			public void startFile() {
				// intentionally empty
			}
		};
		try (InputStream in = inputStreamSource.createInputStream()) {
			JPEGMetaData.read(in, listener);
			BufferedImage bi = thumbnail.get();
			if (bi == null)
				return null;
			QBufferedImage returnValue = scale(bi, sizeFunction, destType, dest);
			bi.flush();
			return returnValue;
		}
	}

	private QBufferedImage scaleImage(PixelIterator iter) {
		Dimension srcSize = new Dimension(iter.getWidth(), iter.getHeight());
		Dimension destSize = sizeFunction == null ? srcSize : sizeFunction.apply(srcSize, false);

		if (destSize == null)
			return null;

		if (dest != null) {
			ImageType k = ImageType.get(dest.getType());
			if (destType != null && k != destType)
				throw new IllegalStateException("Illegal attempt to scale to a BufferedImage of type " + k + " with a requested image type " + destType);
			destType = k;
		} else if (destType == null) {
			destType = ImageType.get(iter.getType());
		}

		PixelIterator pi = new ScalingIterator(destType, iter, destSize.width, destSize.height);
		return BufferedImageIterator.writeToImage(pi, dest);
	}

	private QBufferedImage scaleImage_BMP() throws IOException {
		// TODO: we could also fall back to ImageIO's BMP parser here.
		// To date I don't have a BMP in my possession that requires it, but maybe (?) one exists?

		InputStreamSource inSrc = null;
		if (imageFile != null) {
			inSrc = new FileInputStreamSource(imageFile);
		} else if (imageURL != null) {
			inSrc = new URLInputStreamSource(imageURL);
		} else {
			// our constructors should safeguard us from this ever happening
			throw new IllegalStateException();
		}

		try (InputStream in = inSrc.createInputStream()) {
			try (PixelIterator iter = BmpDecoderIterator.get(in)) {
				return scaleImage(iter);
			}
		}
	}
}