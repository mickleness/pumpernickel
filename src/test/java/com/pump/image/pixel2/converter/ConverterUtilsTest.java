package com.pump.image.pixel2.converter;

import junit.framework.TestCase;

public class ConverterUtilsTest extends TestCase {
    public void test_swapFirstAndThirdSamples() {
        // test storing it to the left of where our source pixels start
        int[] pixels = new int[] {0, 0, 0x02030507, 0x11131719, 0x23293133, 0x37414347};
        ConverterUtils.swapFirstAndThirdSamples(pixels, 0, pixels, 2, 4);
        assertEquals(0x02070503, pixels[0]);
        assertEquals(0x11191713, pixels[1]);
        assertEquals(0x23333129, pixels[2]);
        assertEquals(0x37474341, pixels[3]);

        // test storing it to the right of where our source pixels start
        pixels = new int[] {0x02030507, 0x11131719, 0x23293133, 0x37414347, 0, 0};
        ConverterUtils.swapFirstAndThirdSamples(pixels, 2, pixels, 0, 4);
        assertEquals(0x02070503, pixels[2]);
        assertEquals(0x11191713, pixels[3]);
        assertEquals(0x23333129, pixels[4]);
        assertEquals(0x37474341, pixels[5]);
    }



    public void test_prependAlpha() {
        byte[] pixels = new byte[] {0, 0, 0,
                0x03, 0x05, 0x07,
                0x13, 0x17, 0x19,
                0x29, 0x31, 0x33,
                0x41, 0x43, 0x47,
                0x49, 0x51, 0x57,
                0x59, 0x61, 0x67,
                0, 0, 0, 0, 0, 0, 0, 0, 0};
        ConverterUtils.prependAlpha(pixels, 0, pixels, 3, 6);
        assertEquals(-1, pixels[0]);
        assertEquals(0x03, pixels[1]);
        assertEquals(0x05, pixels[2]);
        assertEquals(0x07, pixels[3]);

        assertEquals(-1, pixels[4]);
        assertEquals(0x13, pixels[5]);
        assertEquals(0x17, pixels[6]);
        assertEquals(0x19, pixels[7]);

        assertEquals(-1, pixels[8]);
        assertEquals(0x29, pixels[9]);
        assertEquals(0x31, pixels[10]);
        assertEquals(0x33, pixels[11]);

        assertEquals(-1, pixels[12]);
        assertEquals(0x41, pixels[13]);
        assertEquals(0x43, pixels[14]);
        assertEquals(0x47, pixels[15]);

        assertEquals(-1, pixels[16]);
        assertEquals(0x49, pixels[17]);
        assertEquals(0x51, pixels[18]);
        assertEquals(0x57, pixels[19]);

        assertEquals(-1, pixels[20]);
        assertEquals(0x59, pixels[21]);
        assertEquals(0x61, pixels[22]);
        assertEquals(0x67, pixels[23]);

        pixels = new byte[] {0, 0, 0,
                0x03, 0x05, 0x07,
                0x13, 0x17, 0x19,
                0x29, 0x31, 0x33,
                0x41, 0x43, 0x47,
                0x49, 0x51, 0x57,
                0x59, 0x61, 0x67,
                0, 0, 0, 0, 0, 0, 0, 0, 0};
        ConverterUtils.prependAlpha(pixels, 6, pixels, 3, 6);
        assertEquals(-1, pixels[6]);
        assertEquals(0x03, pixels[7]);
        assertEquals(0x05, pixels[8]);
        assertEquals(0x07, pixels[9]);

        assertEquals(-1, pixels[10]);
        assertEquals(0x13, pixels[11]);
        assertEquals(0x17, pixels[12]);
        assertEquals(0x19, pixels[13]);

        assertEquals(-1, pixels[14]);
        assertEquals(0x29, pixels[15]);
        assertEquals(0x31, pixels[16]);
        assertEquals(0x33, pixels[17]);

        assertEquals(-1, pixels[18]);
        assertEquals(0x41, pixels[19]);
        assertEquals(0x43, pixels[20]);
        assertEquals(0x47, pixels[21]);

        assertEquals(-1, pixels[22]);
        assertEquals(0x49, pixels[23]);
        assertEquals(0x51, pixels[24]);
        assertEquals(0x57, pixels[25]);

        assertEquals(-1, pixels[26]);
        assertEquals(0x59, pixels[27]);
        assertEquals(0x61, pixels[28]);
        assertEquals(0x67, pixels[29]);

    }

    public void test_swapFirstAndThirdSamples_4samples() {
        byte[] pixels = new byte[] {
                0x03, 0x05, 0x07, 0x13,
                0x17, 0x19, 0x29, 0x31,
                0x33, 0x41, 0x43, 0x47,
                0x49, 0x51, 0x57, 0x59 };
        ConverterUtils.swapFirstAndThirdSamples_4samples(pixels, 0, pixels, 0, 4);

        assertEquals(0x03, pixels[0]);
        assertEquals(0x13, pixels[1]);
        assertEquals(0x07, pixels[2]);
        assertEquals(0x05, pixels[3]);

        assertEquals(0x17, pixels[4]);
        assertEquals(0x31, pixels[5]);
        assertEquals(0x29, pixels[6]);
        assertEquals(0x19, pixels[7]);

        assertEquals(0x33, pixels[8]);
        assertEquals(0x47, pixels[9]);
        assertEquals(0x43, pixels[10]);
        assertEquals(0x41, pixels[11]);

        assertEquals(0x49, pixels[12]);
        assertEquals(0x59, pixels[13]);
        assertEquals(0x57, pixels[14]);
        assertEquals(0x51, pixels[15]);

    }
}