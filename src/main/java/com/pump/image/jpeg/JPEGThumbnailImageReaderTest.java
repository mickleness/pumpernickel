package com.pump.image.jpeg;

import java.util.*;

import com.pump.UserCancelledException;
import junit.framework.TestCase;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.IIORegistry;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class JPEGThumbnailImageReaderTest extends TestCase {

    public void testThumbnail() throws Exception {
        URL jpegFile = UserCancelledException.class.getResource("showcase/resourcegenerator/IMG-20171107-WA0002.jpg");
        try (InputStream in = jpegFile.openStream()) {
            BufferedImage thumbnail = JPEGMetaData.getThumbnail(in);
            Objects.requireNonNull(thumbnail);
        }
    }

    public void testThumbnailReader() throws Exception {
        ImageReader reader = new JPEGThumbnailImageReaderSpi().createReaderInstance(null);
        URL jpegFile = UserCancelledException.class.getResource("showcase/resourcegenerator/IMG-20171107-WA0002.jpg");
        reader.setInput( ImageIO.createImageInputStream(jpegFile.openStream()) );
        BufferedImage thumbnail = reader.readThumbnail(0, 0);
        Objects.requireNonNull(thumbnail);
    }

    public void testImageIOFails() {
        IIORegistry.getDefaultInstance().registerServiceProvider(new JPEGThumbnailImageReaderSpi());

        URL jpegFile = UserCancelledException.class.getResource("showcase/resourcegenerator/IMG-20171107-WA0002.jpg");
        Iterator iterator = ImageIO.getImageReadersBySuffix("jpeg");
        while(iterator.hasNext()) {
            ImageReader reader = (ImageReader)iterator.next();

            if (reader instanceof JPEGThumbnailImageReader)
                continue;

            try {
                reader.setInput( ImageIO.createImageInputStream(jpegFile.openStream()) );
                BufferedImage thumbnail = reader.readThumbnail(0, 0);
                if (thumbnail != null) {
                    fail("ImageIO had ImageReaders capable of reading the JPEG thumbnail. This means the pump classes may be obsolete.");
                }
            } catch(Exception e) {
                // we expect this to fail
            }
        }
    }
}

class JPEGThumbnailImageReaderSpi extends ImageReaderSpi {

    public JPEGThumbnailImageReaderSpi() {
        inputTypes = new Class<?>[] { ImageInputStream.class };
        pluginClassName = JPEGThumbnailImageReader.class.getName();

        vendorName = "Pumpernickel";
        version = "1.0";

        // these are copied from the JPEGImageReaderSpi
        suffixes = new String[] {"jpg", "jpeg"};
        MIMETypes = new String[] {"image/jpeg"};
        names = new String[] {"JPEG", "jpeg", "JPG", "jpg"};
    }

    @Override
    public boolean canDecodeInput(Object source) throws IOException {
        return false;
    }

    @Override
    public ImageReader createReaderInstance(Object extension) throws IOException {
        return new JPEGThumbnailImageReader(this);
    }

    @Override
    public String getDescription(Locale locale) {
        return null;
    }
}

class JPEGThumbnailImageReader extends ImageReader {

    boolean isInitialized = false;
    List<Dimension> thumbnailDimensions = new ArrayList<>();
    List<BufferedImage> thumbnails = null;
    Dimension imageSize = new Dimension(0,0);

    protected JPEGThumbnailImageReader(ImageReaderSpi originatingProvider) {
        super(originatingProvider);
    }

    @Override
    public void reset() {
        _reset();
        super.reset();
    }

    @Override
    public void setInput(Object input,
                                      boolean seekForwardOnly,
                                      boolean ignoreMetadata) {
        _reset();
        super.setInput(input, seekForwardOnly, ignoreMetadata);
    }

    private synchronized void _reset() {
        thumbnails = null;
        isInitialized = false;
        thumbnailDimensions.clear();
    }

    @Override
    public void dispose() {
        _reset();
        super.dispose();
    }

    /**
     * This does two things:
     * A. it scans the JPG image and catalogs all the possible thumbnail dimensions it offers.
     * B. If you request a specific thumbnail index, it returns that thumbnail as a BufferedImage.
     *
     * If the ImageInputStream can only be read once, then this method stores a reference to all
     * possible thumbnails.
     *
     * @param requestedThumbnailIndex the thumbnail index, or -1 if no specific thumbnail is needed
     * @return the specific thumbnail requested, or null if no thumbnail was requested.
     */
    private synchronized BufferedImage initialize(final int requestedThumbnailIndex) throws IOException {
        if (isInitialized) {
            // we've already initialized once

            if (requestedThumbnailIndex == -1)
                return null;

            // here the caller expects a specific thumbnail

            if (thumbnails != null)
                return thumbnails.get(requestedThumbnailIndex);

            // if we're here: that means we've cataloged the basic thumbnail sizes already, but we didn't keep a reference
            // to the BufferedImages themselves. So we'll need to scan again.
        }

        isInitialized = true;

        AtomicReference<BufferedImage> returnValue = new AtomicReference<>();

        thumbnails = isSeekForwardOnly() ? new ArrayList<>() : null;
        thumbnailDimensions.clear();

        ImageInputStream iis = (ImageInputStream) input;
        iis.reset();

        JPEGMetaDataListener listener = new JPEGMetaDataListener() {

            @Override
            public boolean isThumbnailAccepted(String markerName, int width, int height) {
                thumbnailDimensions.add(new Dimension(width, height));

                if (thumbnails != null)
                    return true;

                return thumbnailDimensions.size() == requestedThumbnailIndex + 1;
            }

            @Override
            public void addProperty(String markerName, String propertyName, Object value) {
                // intentionally empty
            }

            @Override
            public void addThumbnail(String markerName, BufferedImage bi) {
                if (thumbnailDimensions.size() == requestedThumbnailIndex + 1) {
                    returnValue.set(bi);
                }
                if (thumbnails != null)
                   thumbnails.add(bi);
            }

            @Override
            public void addComment(String markerName, String comment) {
                // intentionally empty
            }

            @Override
            public void startFile() {

            }

            @Override
            public void endFile() {

            }

            @Override
            public void imageDescription(int bitsPerPixel, int width, int height, int numberOfComponents) {
                imageSize.setSize(width, height);
            }

            @Override
            public void processException(Exception e, String markerCode) {
                // intentionally empty
            }
        };
        JPEGMetaData.read(new InputStreamConverter(iis), listener);

        return returnValue.get();
    }

    @Override
    public int getNumImages(boolean allowSearch) throws IOException {
        if (allowSearch) {
            initialize(-1);
            return 1;
        }
        return -1;
    }

    @Override
    public int getWidth(int imageIndex) throws IOException {
        validateImageIndex(imageIndex);

        // this ImageReader isn't really focused on the image size, but it's trivial to identify, so...

        initialize(-1);
        return imageSize.width;
    }

    @Override
    public int getHeight(int imageIndex) throws IOException {
        validateImageIndex(imageIndex);

        initialize(-1);
        return imageSize.width;
    }

    @Override
    public Iterator<ImageTypeSpecifier> getImageTypes(int imageIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IIOMetadata getStreamMetadata() {
        throw new UnsupportedOperationException();
    }

    @Override
    public IIOMetadata getImageMetadata(int imageIndex) {
        throw new UnsupportedOperationException();
    }

    @Override
    public BufferedImage read(int imageIndex, ImageReadParam param) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean readerSupportsThumbnails() {
        return true;
    }

    @Override
    public int getNumThumbnails(int imageIndex) throws IOException {
        validateImageIndex(imageIndex);
        initialize(-1);
        return thumbnailDimensions.size();
    }

    private void validateImageIndex(int imageIndex) {
        if (imageIndex != 0)
            throw new IndexOutOfBoundsException();
    }

    @Override
    public int getThumbnailWidth(int imageIndex, int thumbnailIndex) throws IOException {
        validateImageIndex(imageIndex);
        initialize(-1);
        return thumbnailDimensions.get(thumbnailIndex).width;
    }

    @Override
    public int getThumbnailHeight(int imageIndex, int thumbnailIndex) throws IOException {
        validateImageIndex(imageIndex);
        initialize(-1);
        return thumbnailDimensions.get(thumbnailIndex).height;
    }

    @Override
    public synchronized BufferedImage readThumbnail(int imageIndex,
                                       int thumbnailIndex) throws IOException {
        validateImageIndex(imageIndex);
        return initialize(thumbnailIndex);
    }
}

/**
 * This converts an ImageInputStream back to an InputStream.
 */
class InputStreamConverter extends InputStream {
    protected final ImageInputStream imageInputStream;
    private long read = 0;
    private long readAtMark = 0;

    public InputStreamConverter(ImageInputStream imageInputStream) {
        this.imageInputStream = Objects.requireNonNull(imageInputStream);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int returnValue = imageInputStream.read(b, off, len);
        if (returnValue > 0)
            read += returnValue;
        return returnValue;
    }

    @Override
    public long skip(long n) throws IOException {
        long returnValue = imageInputStream.skipBytes(n);
        if (returnValue > 0)
            read += returnValue;
        return returnValue;
    }

    @Override
    public void close() throws IOException {
        imageInputStream.close();
    }

    @Override
    public synchronized void mark(int readlimit) {
        imageInputStream.mark();
        readAtMark = read;
    }

    @Override
    public synchronized void reset() throws IOException {
        imageInputStream.reset();
        read = readAtMark;
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public int read() throws IOException {
        read++;
        return imageInputStream.read();
    }
}