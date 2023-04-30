package com.pump.image.pixel;

import com.pump.image.ImageSize;
import com.pump.image.QBufferedImage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.Objects;

/**
 * This PixelIterator iterates over a <code>java.awt.Image</code>.
 * <p>
 * This may, depending on the underlying Image, pipe pixel data directly from the Image
 * without caching any pixel data in memory.
 * </p>
 * <p>
 * This has special optimized behavior for BufferedImages. (Because {@link BufferedImage#getSource()} is
 * inefficient.)
 *
 * <a href=
 * "https://javagraphics.blogspot.com/2011/05/images-scaling-jpegs-and-pngs.html"
 * >Images: Scaling JPEGs and PNGs</a>
 */
public class ImagePixelIterator<T> implements PixelIterator<T> {

    public static class Source<T> implements PixelIterator.Source<T> {
        private final Image image;
        private final ImageType type;

        private Dimension size;

        private String errorDescriptor;

        public Source(File file, ImageType imageType) {
            this( Toolkit.getDefaultToolkit().createImage(file.getAbsolutePath()), imageType, file.getAbsolutePath());
            // I would like to (optionally) add meta data from Files and URLs too
            // (for ex: timestamps, comments, etc.). Any metadata could be added
            // as a property in a QBufferedImage.
            // ... but: I'd be lying if I said I had a real use case for that presently.
            // So maybe someday I'll get to that. Later.
        }

        public Source(URL url, ImageType imageType) {
            this( Toolkit.getDefaultToolkit().createImage(url), imageType, url.toString());
        }

        public Source(Image image, ImageType imageType) {
            this(image, imageType, null);
        }

        private Source(Image image, ImageType imageType, String errorDescriptor) {
            this.image = Objects.requireNonNull(image);
            this.errorDescriptor = errorDescriptor;

            if (!(imageType == null
                    || imageType.getCode() == BufferedImage.TYPE_INT_ARGB
                    || imageType.getCode() == BufferedImage.TYPE_INT_ARGB_PRE
                    || imageType.getCode() == BufferedImage.TYPE_INT_RGB
                    || imageType.getCode() == BufferedImage.TYPE_INT_BGR
                    || imageType.getCode() == BufferedImage.TYPE_3BYTE_BGR
                    || imageType.getCode() == BufferedImage.TYPE_BYTE_GRAY
                    || imageType.getCode() == BufferedImage.TYPE_4BYTE_ABGR
                    || imageType.getCode() == BufferedImage.TYPE_4BYTE_ABGR_PRE)) {
                throw new IllegalArgumentException(
                        "illegal iterator type: " + imageType);
            }
            this.type = imageType;
        }

        @Override
        public ImagePixelIterator createPixelIterator() {
            ImagePixelIterator returnValue = new ImagePixelIterator(image, type, false, errorDescriptor);
            if (size == null) {
                size = new Dimension(returnValue.getWidth(), returnValue.getHeight());
            }
            return returnValue;
        }

        @Override
        public int getWidth() {
            if (size == null) {
                size = ImageSize.get(image);
            }
            return size.width;
        }

        @Override
        public int getHeight() {
            if (size == null) {
                size = ImageSize.get(image);
            }
            return size.height;
        }
    }

    public static BufferedImage createBufferedImage(File file) {
        return createBufferedImage(file, null);
    }

    public static QBufferedImage createBufferedImage(File file, ImageType<?> imageType) {
        Image image = Toolkit.getDefaultToolkit().createImage(file.getAbsolutePath());
        return createBufferedImage(image, imageType, file.getAbsolutePath());
    }

    public static QBufferedImage createBufferedImage(Image image) {
        return createBufferedImage(image, null);
    }

    public static QBufferedImage createBufferedImage(Image image, ImageType<?> imageType) {
        return createBufferedImage(image, imageType, null);
    }

    private static QBufferedImage createBufferedImage(Image image, ImageType<?> imageType, String errorDescription) {
        if (image instanceof QBufferedImage) {
            return (QBufferedImage) image;
        } else if (image instanceof BufferedImage) {
            return new QBufferedImage( (BufferedImage) image);
        }
        return new ImagePixelIterator.Source(image, imageType, errorDescription).createBufferedImage(null);
    }

    public static QBufferedImage createBufferedImage(URL resource) {
        return createBufferedImage(resource, null);
    }

    public static QBufferedImage createBufferedImage(URL url, ImageType<?> imageType) {
        Image image = Toolkit.getDefaultToolkit().createImage(url);
        return createBufferedImage(image, imageType, url.toString());
    }

    /**
     * Return true if two images have the same pixels.
     * <p>
     * This can compare Images with BufferedImages, and/or convert image types.
     * </p>
     */
    public static boolean equalPixels(Image image1, Image image2) {
        if (image1 == null && image2 == null)
            return true;
        if (image1 == null || image2 == null)
            return false;

        try (PixelIterator iter1 = new ImagePixelIterator(image1, null, false, null)) {
            ImageType type = ImageType.get(iter1.getType());
            int w = iter1.getWidth();
            int h = iter1.getHeight();
            try (PixelIterator iter2 = new ImagePixelIterator(image2, type, false, null)) {
                if (w != iter2.getWidth() || h != iter2.getHeight())
                    return false;

                int arrayLength = iter1.getWidth() * type.getSampleCount();
                if (type.isInt()) {
                    int[] row1 = new int[arrayLength];
                    int[] row2 = new int[arrayLength];

                    while (!iter1.isDone()) {
                        iter1.next(row1, 0);
                        iter2.next(row2, 0);
                        for (int j = 0; j < arrayLength; j++) {
                            if (row1[j] != row2[j])
                                return false;
                        }
                    }
                } else {
                    byte[] row1 = new byte[arrayLength];
                    byte[] row2 = new byte[arrayLength];

                    while (!iter1.isDone()) {
                        iter1.next(row1, 0);
                        iter2.next(row2, 0);
                        for (int j = 0; j < arrayLength; j++) {
                            if (row1[j] != row2[j])
                                return false;
                        }
                    }
                }

                return true;
            }
        }
    }

    protected final PixelIterator<T> delegate;
    protected final Image image;
    protected final boolean flushImageOnClose;

    public ImagePixelIterator(Image image) {
        this(image, null, false, null);
    }

    /**
     * Create a new ImagePixelIterator.
     *
     * @param image the Image to iterate over
     * @param type an optional ImageType for this iterator. If this is null then the image's default
     *             image type is used.
     * @param flushImageOnClose if true then {@link #close()} calls {@link Image#flush()}. If you are sure
     *                          nobody else is interested in the Image then this should be true.
     */
    public ImagePixelIterator(Image image, ImageType type, boolean flushImageOnClose) {
        this(image, type, flushImageOnClose, null);
    }

    /**
     * Create a new ImagePixelIterator.
     *
     * @param image the Image to iterate over
     * @param type an optional ImageType for this iterator. If this is null then the image's default
     *             image type is used.
     * @param flushImageOnClose if true then {@link #close()} calls {@link Image#flush()}. If you are sure
     *                          nobody else is interested in the Image then this should be true.
     * @param errorDescriptor an optional String that can appear in exception messages helping to identify this
     *                        image. For example: if the Image relates to a file, then this should include the filename
     *                        or file path.
     */
    ImagePixelIterator(Image image, ImageType type, boolean flushImageOnClose, String errorDescriptor) {
        if (image instanceof BufferedImage &&
                BufferedImageIterator.isSupportedType( ((BufferedImage)image).getType() )) {
            BufferedImage bi = (BufferedImage) image;
            PixelIterator imgIter = BufferedImageIterator.create(bi);
            delegate = type == null ? imgIter : type.createPixelIterator(imgIter);
        } else {
            delegate = new ImageProducerPixelIterator<T>(image.getSource(), type, errorDescriptor);
        }

        this.flushImageOnClose = flushImageOnClose;
        this.image = Objects.requireNonNull(image);
    }

    @Override
    public int getType() {
        return delegate.getType();
    }

    @Override
    public boolean isDone() {
        return delegate.isDone();
    }

    @Override
    public boolean isTopDown() {
        return delegate.isTopDown();
    }

    @Override
    public int getWidth() {
        return delegate.getWidth();
    }

    @Override
    public int getHeight() {
        return delegate.getHeight();
    }

    @Override
    public void skip() throws ClosedException {
        delegate.skip();

        if (isDone())
            close();
    }

    @Override
    public void next(T dest, int offset) throws ClosedException {
        delegate.next(dest, offset);

        if (isDone())
            close();
    }

    @Override
    public void close() throws ClosingException {
        delegate.close();
        if (flushImageOnClose)
            image.flush();
    }

    /**
     * Return the Image this PixelIterator iterates over.
     */
    public Image getImage() {
        return image;
    }
}
