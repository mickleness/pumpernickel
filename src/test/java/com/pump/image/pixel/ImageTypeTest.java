package com.pump.image.pixel;

import junit.framework.TestCase;

import java.awt.image.BufferedImage;

public class ImageTypeTest extends TestCase {

    public void testTestToString() {
        assertEquals("INT_ARGB", ImageType.toString(BufferedImage.TYPE_INT_ARGB));
        assertEquals("3BYTE_BGR", ImageType.toString(BufferedImage.TYPE_3BYTE_BGR));
        assertEquals("BYTE_GRAY", ImageType.toString(BufferedImage.TYPE_BYTE_GRAY));
        assertEquals("USHORT_565_RGB", ImageType.toString(BufferedImage.TYPE_USHORT_565_RGB));

        assertEquals("3BYTE_RGB", ImageType.toString(ImageType.TYPE_3BYTE_RGB));
        assertEquals("4BYTE_ARGB", ImageType.toString(ImageType.TYPE_4BYTE_ARGB));
        assertEquals("4BYTE_BGRA", ImageType.toString(ImageType.TYPE_4BYTE_BGRA));
        assertEquals("4BYTE_ARGB_PRE", ImageType.toString(ImageType.TYPE_4BYTE_ARGB_PRE));
    }
}