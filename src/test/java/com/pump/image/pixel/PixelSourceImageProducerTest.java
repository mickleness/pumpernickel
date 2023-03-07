package com.pump.image.pixel;

import junit.framework.TestCase;

import java.awt.image.BufferedImage;

public class PixelSourceImageProducerTest extends TestCase {

    /**
     * This starts with a BufferedImage then recreates it via a {@link PixelIterator.Source#createBufferedImage()}
     * and makes sure the output matches the input.
     */
    public void testPixelIteratorImageProducer_recreateBufferedImage() {
        for (ImageType imageType : ImageType.values(true)) {
            System.out.println("Testing " + imageType);
            BufferedImage original = ScalingTest.createRainbowImage(12, 12, imageType.getCode(), true);
            BufferedImage copy = new BufferedImageIterator.Source(original).createBufferedImage();
            assertImageEquals(original, copy);
        }
    }

    private void assertImageEquals(BufferedImage img1, BufferedImage img2) {
        assertEquals(img1.getWidth(), img2.getWidth());
        assertEquals(img1.getHeight(), img2.getHeight());
        assertEquals(img1.getType(), img2.getType());
        for (int y = 0; y < img1.getHeight(); y++) {
            for (int x = 0; x < img1.getWidth(); x++) {
                assertEquals(Integer.toUnsignedString(img1.getRGB(x, y), 16) + " != " +
                        Integer.toUnsignedString(img2.getRGB(x, y), 16), img1.getRGB(x, y), img2.getRGB(x, y));
            }
        }
    }
}
