package com.pump.image.pixel;

import junit.framework.TestCase;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * This confirms that we're parsing BufferedImages as expected.
 */
public class BufferedImageIteratorTest extends TestCase {


    public void test_INT_RGB() {
        BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        bi.setRGB(0, 0, new Color(10, 20, 30).getRGB());

        PixelIterator<int[]> iter = new BufferedImageIterator.BufferedImageIntIterator_FromRaster(bi, true);
        int[] pixels = new int[1];
        iter.next(pixels);
        assertEquals(BufferedImage.TYPE_INT_RGB, iter.getType());
        assertEquals(0x000A141E, pixels[0]);
    }

    public void test_INT_ARGB() {
        BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        bi.setRGB(0, 0, new Color(10, 20, 30, 40).getRGB());

        PixelIterator<int[]> iter = new BufferedImageIterator.BufferedImageIntIterator_FromRaster(bi, true);
        int[] pixels = new int[1];
        iter.next(pixels);
        assertEquals(BufferedImage.TYPE_INT_ARGB, iter.getType());
        assertEquals(0x280A141E, pixels[0]);
    }

    public void test_INT_ARGB_PRE() {
        BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB_PRE);
        bi.setRGB(0, 0, new Color(20, 40, 80, 64).getRGB());

        PixelIterator<int[]> iter = new BufferedImageIterator.BufferedImageIntIterator_FromRaster(bi, true);
        int[] pixels = new int[1];
        iter.next(pixels);
        assertEquals(BufferedImage.TYPE_INT_ARGB_PRE, iter.getType());
        assertEquals(0x40050a14, pixels[0]);
    }

    public void test_INT_BGR() {
        BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_INT_BGR);
        bi.setRGB(0, 0, new Color(10, 20, 30).getRGB());

        PixelIterator<int[]> iter = new BufferedImageIterator.BufferedImageIntIterator_FromRaster(bi, true);
        int[] pixels = new int[1];
        iter.next(pixels);
        assertEquals(BufferedImage.TYPE_INT_BGR, iter.getType());
        assertEquals(0x001E140A, pixels[0]);
    }

    public void test_3BYTE_BGR() {
        BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_3BYTE_BGR);
        bi.setRGB(0, 0, new Color(10, 20, 30).getRGB());

        PixelIterator<byte[]> iter = new BufferedImageIterator.BufferedImageByteIterator_FromRaster(bi, true);
        byte[] pixels = new byte[3];
        iter.next(pixels);
        assertEquals(ImageType.TYPE_3BYTE_RGB, iter.getType());
        assertEquals(10, pixels[0]);
        assertEquals(20, pixels[1]);
        assertEquals(30, pixels[2]);
    }

    public void test_4BYTE_ABGR() {
        BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
        bi.setRGB(0, 0, new Color(10, 20, 30, 40).getRGB());

        PixelIterator<byte[]> iter = new BufferedImageIterator.BufferedImageByteIterator_FromRaster(bi, true);
        byte[] pixels = new byte[4];
        iter.next(pixels);

        assertEquals(ImageType.TYPE_4BYTE_RGBA, iter.getType());
        assertEquals(10, pixels[0]);
        assertEquals(20, pixels[1]);
        assertEquals(30, pixels[2]);
        assertEquals(40, pixels[3]);
    }

    // TODO: support RGBA_PRE
//    public void test_4BYTE_ABGR_PRE() {
//        BufferedImage bi = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR_PRE);
//        bi.setRGB(0, 0, new Color(20, 40, 80, 64).getRGB());
//
//        PixelIterator<byte[]> iter = new BufferedImageIterator.BufferedImageByteIterator_FromRaster(bi, true);
//        byte[] pixels = new byte[4];
//        iter.next(pixels);
//
//        assertEquals(ImageType.TYPE_4BYTE_RGBA_PRE, iter.getType());
//        assertEquals(5, pixels[0]);
//        assertEquals(10, pixels[1]);
//        assertEquals(20, pixels[2]);
//        assertEquals(64, pixels[3]);
//    }

    public void test_BYTE_GRAY() {
        BufferedImage bi = new BufferedImage(256, 1, BufferedImage.TYPE_BYTE_GRAY);
        for (int x = 0; x < 256; x++) {
            bi.setRGB(x, 0, new Color(x,x,x).getRGB());
        }

        PixelIterator<byte[]> iter = new BufferedImageIterator.BufferedImageByteIterator_FromRaster(bi, true);
        byte[] pixels = new byte[256];
        iter.next(pixels);

        assertEquals(BufferedImage.TYPE_BYTE_GRAY, iter.getType());
        assertEquals(0, pixels[0]);
        assertEquals(-1, pixels[pixels.length-1]);

        // yikes. Why is ComponentColorModel converting shades of gray this way? Some day we might (?) want
        // to explore this further.
//        for (int x = 0; x < pixels.length; x++) {
//            assertEquals(x, pixels[x]);
//        }
//        for (int x = 0; x < pixels.length; x++) {
//            System.out.println("x = " + x + ", g = " + (pixels[x] & 0xff) );
//        }
    }
}
