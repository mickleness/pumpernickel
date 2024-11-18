package com.pump.image.jpeg;

import junit.framework.TestCase;
import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class JPEGTest extends TestCase {

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

    @Test
    public void testNullThumbnail() throws IOException {
        BufferedImage bi = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream()) {
            JPEG.write(byteOut,bi,null,1f);
            assertTrue(byteOut.toByteArray().length > 100);
        }
    }

    private void testThumbnail(BufferedImage largeImage, BufferedImage thumbnail, int allowedRGBDelta) throws IOException {
        byte[] jpegData;
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream()) {
            JPEG.write(byteOut, largeImage, thumbnail, .8f);
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
}
