package com.pump.image.jpeg;

import com.pump.image.pixel.ImagePixelIterator;
import com.pump.image.pixel.ImageType;
import junit.framework.TestCase;
import org.junit.Test;

import javax.imageio.*;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

public class JPEGMetaDataTest extends TestCase {

    /**
     * Test the APP0DataReader's ability to parse thumbnails that are smaller than 255x255
     */
    @Test
    public void testAPP0Thumbnail_smallThumbnails() throws IOException {
        for (ImageType imageType : ImageType.values(true)) {
            if (!imageType.isOpaque())
                continue;
            for (ImageType thumbnailType : ImageType.values(true)) {
                byte[] jpegData = writeJPEG(imageType, thumbnailType);
                BufferedImage thumbnail = JPEGMetaData.getThumbnail(new ByteArrayInputStream(jpegData));
                try {
                    assertNotNull(thumbnail);
                } catch(Throwable t) {
                    System.err.println("imageType = " + imageType + " thumbnailType = " + thumbnailType);
                    throw t;
                }
            }
        }
    }

    private byte[] writeJPEG(ImageType imageType, ImageType thumbnailImageType) throws IOException {
        BufferedImage bi = new BufferedImage(1000, 1000, imageType.getCode());
        Graphics2D g = bi.createGraphics();
        g.setColor(Color.white);
        Path2D triangle = new Path2D.Float();
        triangle.moveTo(1000, 0);
        triangle.lineTo(1000, 1000);
        triangle.lineTo(0, 1000);
        g.fill(triangle);
        g.setColor(Color.green);
        g.setStroke(new BasicStroke(20));
        g.draw(new Line2D.Float(0,0,1000,1000));
        g.setColor(Color.red);
        g.fill(new Ellipse2D.Float(750-100, 250-100, 200, 200));
        g.setColor(Color.blue);
        g.fill(new Ellipse2D.Float(250-100, 750-100, 200, 200));
        g.dispose();

        Iterator<ImageWriter> iter = ImageIO.getImageWritersBySuffix("jpg");
        ImageWriter w = iter.next();
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream()) {
            BufferedImage thumbnail_argb = ImagePixelIterator.createBufferedImage(bi.getScaledInstance(200, 200, Image.SCALE_AREA_AVERAGING));
            BufferedImage thumbnail = thumbnailImageType.create(thumbnail_argb.getWidth(), thumbnail_argb.getHeight());
            Graphics2D g2 = thumbnail.createGraphics();
            g2.drawImage(thumbnail_argb, 0, 0, null);
            g2.dispose();

            IIOImage iioImage = new IIOImage(bi, Arrays.asList(thumbnail), null);

            ImageOutputStream stream = ImageIO.createImageOutputStream(byteOut);
            w.setOutput(stream);
            w.write(iioImage);
            return byteOut.toByteArray();
        }
    }

    /**
     * This tests jpeg thumbnails that are larger than 255x255px. This requires* using a JFIF APP0 extension marker,
     * so this method will test how we parse that info back in.
     *
     * *by "requires" I mean: it is required as long as we stick to ImageIO's default implementation classes.
     * If we supplement ImageIO with 3rd party libraries we may get different/better results.
     */
    @Test
    public void testAPP0Extension_largeThumbnails() throws IOException {
        BufferedImage largeImage = createImage(1000);

        // a value smaller than 255 works without any special intervention:
        BufferedImage simpleThumbnail = createImage(200);

        // the smaller thumbnail should be encoded losslessly, so we can use an RGB delta of 0
        testThumbnail(largeImage, simpleThumbnail, 0);
        System.out.println("PASSED: thumbnail size " + simpleThumbnail.getWidth() + "x" + simpleThumbnail.getHeight());

        for (int size = 300; size <= 1500; size += 100) {
            BufferedImage biggerThumbnail = createImage(size);

            // the larger thumbnail will have compression artifacts, so allow some RGB delta
            testThumbnail(largeImage, biggerThumbnail, 10);
            System.out.println("PASSED: thumbnail size " + biggerThumbnail.getWidth() + "x" + biggerThumbnail.getHeight());
        }

        // at size = 1600 we get a IllegalThumbException because the encoded thumbnail exceeds ~65536 . That limit
        // is based on the allocation of bytes, so it's possible to predict exactly what *dimension* will trigger
        // that condition. (Since the byte size of a JPEG will vary depending on how well the image data compresses.)
    }

    /**
     * This creates a HSB gradient image. This will be easy to scan later in
     * {@link #assertSimilar(BufferedImage, BufferedImage, int)}, and because it's a gradient it
     * should compress well without clunky compression artifacts.
     */
    private BufferedImage createImage(int size) {
        BufferedImage bi = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < bi.getHeight(); y++) {
            for (int x = 0; x < bi.getWidth(); x++) {
                int rgb = Color.HSBtoRGB( (float)x / ((float) size), 1, (float)y / ((float) size) );
                bi.setRGB(x, y, rgb);
            }
        }
        return bi;
    }

    private void testThumbnail(BufferedImage largeImage, BufferedImage thumbnail, int allowedRGBDelta) throws IOException {
        byte[] jpegData;
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream()) {
            JPEGMetaData.writeJPEG(byteOut, largeImage, thumbnail, .8f);
            jpegData = byteOut.toByteArray();
        }

        testReadingThumbnail(jpegData, thumbnail, allowedRGBDelta);
    }

    /**
     * This reads back a JPEG and asserts that the embedded thumbnail matches what we expect.
     */
    private void testReadingThumbnail(byte[] jpegData, BufferedImage expectedThumbnail, int allowedRGBDelta) throws IOException {
        try (ByteArrayInputStream byteIn = new ByteArrayInputStream(jpegData)) {
            BufferedImage actualThumbnail = JPEGMetaData.getThumbnail(byteIn);
            assertSimilar(expectedThumbnail, actualThumbnail, allowedRGBDelta);
        }
    }

    /**
     * Make sure two images are nearly the same. This examines the RGB of each pixel and
     * asserts that each channel of both images is at least `allowedRGBDelta` units similar.
     */
    private void assertSimilar(BufferedImage expectedImg, BufferedImage actualImg, int allowedRGBDelta) {
        assertEquals(expectedImg.getWidth(), actualImg.getWidth());
        assertEquals(expectedImg.getHeight(), actualImg.getHeight());
        for (int y = 0; y <expectedImg.getHeight(); y++) {
            for (int x = 0; x < expectedImg.getWidth(); x++) {
                int argb1 = expectedImg.getRGB(x,y);
                int argb2 = actualImg.getRGB(x,y);

                int r1 = (argb1 >> 16) & 0xff;
                int g1 = (argb1 >> 8) & 0xff;
                int b1 = (argb1 >> 0) & 0xff;

                int r2 = (argb2 >> 16) & 0xff;
                int g2 = (argb2 >> 8) & 0xff;
                int b2 = (argb2 >> 0) & 0xff;

                assertTrue("(" + x + "," + y+") r1 = " + r1 + ", r2 = " + r2, Math.abs(r1 - r2) <= allowedRGBDelta);
                assertTrue("(" + x + "," + y+") g1 = " + g1 + ", g2 = " + g2, Math.abs(g1 - g2) <= allowedRGBDelta);
                assertTrue("(" + x + "," + y+") b1 = " + b1 + ", b2 = " + b2, Math.abs(b1 - b2) <= allowedRGBDelta);
            }
        }
    }

    @Test
    public void testNullThumbnail() throws IOException {
        BufferedImage bi = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream()) {
            JPEGMetaData.writeJPEG(byteOut,bi,null,1f);
            assertTrue(byteOut.toByteArray().length > 100);
        }
    }
}
