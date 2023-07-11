package com.pump.image.jpeg;

import java.util.List;
import com.pump.UserCancelledException;
import junit.framework.TestCase;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;
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
        URL jpegFile = UserCancelledException.class.getResource("showcase/resourcegenerator/IMG-20171107-WA0002.jpg");
        Iterator iterator = ImageIO.getImageReadersBySuffix("jpeg");
        while(iterator.hasNext()) {
            ImageReader reader = (ImageReader)iterator.next();
            try {
                reader.setInput( ImageIO.createImageInputStream(jpegFile.openStream()) );
                BufferedImage thumbnail = reader.readThumbnail(0, 0);
                if (thumbnail != null) {
                    fail("ImageIO DID have ImageReaders capable of reading the JPEG thumbnail. This means the pump classes may be obsolete.");
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

    List<Dimension> thumbnailSizes = null;
    List<BufferedImage> thumbnails = null;

    protected JPEGThumbnailImageReader(ImageReaderSpi originatingProvider) {
        super(originatingProvider);
    }

    @Override
    public int getNumImages(boolean allowSearch) throws IOException {
        return 0;
    }

    @Override
    public int getWidth(int imageIndex) throws IOException {
        return 0;
    }

    @Override
    public int getHeight(int imageIndex) throws IOException {
        return 0;
    }

    @Override
    public Iterator<ImageTypeSpecifier> getImageTypes(int imageIndex) throws IOException {
        return null;
    }

    @Override
    protected void processThumbnailStarted(int imageIndex, int thumbnailIndex) {
        super.processThumbnailStarted(imageIndex, thumbnailIndex);
    }

    @Override
    protected void processThumbnailProgress(float percentageDone) {
        super.processThumbnailProgress(percentageDone);
    }

    @Override
    protected void processThumbnailComplete() {
        super.processThumbnailComplete();
    }

    @Override
    protected void processThumbnailPassStarted(BufferedImage theThumbnail, int pass, int minPass, int maxPass, int minX, int minY, int periodX, int periodY, int[] bands) {
        super.processThumbnailPassStarted(theThumbnail, pass, minPass, maxPass, minX, minY, periodX, periodY, bands);
    }

    @Override
    protected void processThumbnailUpdate(BufferedImage theThumbnail, int minX, int minY, int width, int height, int periodX, int periodY, int[] bands) {
        super.processThumbnailUpdate(theThumbnail, minX, minY, width, height, periodX, periodY, bands);
    }

    @Override
    protected void processThumbnailPassComplete(BufferedImage theThumbnail) {
        super.processThumbnailPassComplete(theThumbnail);
    }

    @Override
    public IIOMetadata getStreamMetadata() throws IOException {
        return null;
    }

    @Override
    public IIOMetadata getImageMetadata(int imageIndex) throws IOException {
        return null;
    }

    @Override
    public BufferedImage read(int imageIndex, ImageReadParam param) throws IOException {
        return null;
    }

    @Override
    public boolean readerSupportsThumbnails() {
        return true;
    }

    @Override
    public boolean hasThumbnails(int imageIndex) throws IOException {
        return super.hasThumbnails(imageIndex);
    }

    @Override
    public int getNumThumbnails(int imageIndex) throws IOException {
        return super.getNumThumbnails(imageIndex);
    }

    @Override
    public int getThumbnailWidth(int imageIndex, int thumbnailIndex) throws IOException {
        return super.getThumbnailWidth(imageIndex, thumbnailIndex);
    }

    @Override
    public int getThumbnailHeight(int imageIndex, int thumbnailIndex) throws IOException {
        return super.getThumbnailHeight(imageIndex, thumbnailIndex);
    }

    @Override
    public synchronized BufferedImage readThumbnail(int imageIndex,
                                       int thumbnailIndex) throws IOException {
        if (input instanceof ImageInputStream) {
            ImageInputStream iis = (ImageInputStream) input;
            iis.reset();
            AtomicReference<BufferedImage> returnValue = new AtomicReference<>();
            JPEGMetaDataListener listener = new JPEGMetaDataListener() {
                int thumbnailCtr = 0;

                @Override
                public boolean isThumbnailAccepted(String markerName, int width, int height) {
                    return returnValue.get() == null;
                }

                @Override
                public void addProperty(String markerName, String propertyName, Object value) {

                }

                @Override
                public void addThumbnail(String markerName, BufferedImage bi) {
                    if (thumbnailCtr == thumbnailIndex) {
                        returnValue.set(bi);
                    }
                    thumbnailCtr++;
                }

                @Override
                public void addComment(String markerName, String comment) {

                }

                @Override
                public void startFile() {

                }

                @Override
                public void endFile() {

                }

                @Override
                public void imageDescription(int bitsPerPixel, int width, int height, int numberOfComponents) {

                }

                @Override
                public void processException(Exception e, String markerCode) {

                }
            };
            JPEGMetaData.read(new InputStreamConverter(iis), listener);
            return returnValue.get();
        } else {
            // we shouldn't reach this condition, because calling setInput(Object)
            // should have already thrown an exception
            throw new IllegalStateException("unsupported input: " + input);
        }
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