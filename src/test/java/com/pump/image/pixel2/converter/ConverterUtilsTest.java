package com.pump.image.pixel2.converter;

import junit.framework.TestCase;

public class ConverterUtilsTest extends TestCase {
    public void test_swapFirstAndThirdSamples() {
        // test storing it to the left of where our source pixels start
        int[] pixels = new int[] {0x02030507, 0x11131719, 0x23293133, 0x37414347};
        ConverterUtils.swapFirstAndThirdSamples(pixels, 0, pixels, 0, 4);
        assertEquals(0x02070503, pixels[0]);
        assertEquals(0x11191713, pixels[1]);
        assertEquals(0x23333129, pixels[2]);
        assertEquals(0x37474341, pixels[3]);
    }



    public void test_prependAlpha() {
        byte[] pixels = new byte[] {
                0x03, 0x05, 0x07,
                0x13, 0x17, 0x19,
                0x29, 0x31, 0x33,
                0x41, 0x43, 0x47,
                0x49, 0x51, 0x57,
                0x59, 0x61, 0x67,
                0, 0, 0, 0, 0, 0};
        ConverterUtils.prependAlpha(pixels, 0, pixels, 0, 6);
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

    public void test_convert3samples() {

        byte[] pixels = new byte[] {
                0x03, 0x05, 0x07,
                0x13, 0x17, 0x19,
                0x29, 0x31, 0x33,
                0x41, 0x43, 0x47,
                0x49, 0x51, 0x57 };

        int[] dest = new int[5];
        ConverterUtils.convert3samples(dest, 0, pixels, 0, 5);
        assertEquals(0x030507, dest[0]);
        assertEquals(0x131719, dest[1]);
        assertEquals(0x293133, dest[2]);
        assertEquals(0x414347, dest[3]);
        assertEquals(0x495157, dest[4]);
    }

    public void test_convert3samples_swapFirstAndThirdSamples() {

        byte[] pixels = new byte[] {
                0x03, 0x05, 0x07,
                0x13, 0x17, 0x19,
                0x29, 0x31, 0x33,
                0x41, 0x43, 0x47,
                0x49, 0x51, 0x57 };

        int[] dest = new int[5];
        ConverterUtils.convert3samples_swapFirstAndThirdSamples(dest, 0, pixels, 0, 5);
        assertEquals(0x070503, dest[0]);
        assertEquals(0x191713, dest[1]);
        assertEquals(0x333129, dest[2]);
        assertEquals(0x474341, dest[3]);
        assertEquals(0x575149, dest[4]);
    }

    public void test_average3Samples_int_to_byte() {
        int[] pixels = new int[] {
                0x030507,
                0x131719,
                0x293133,
                0x414347,
                0x495157 };
        byte[] dest = new byte[3 * 5];
        ConverterUtils.average3Samples(dest, 0, pixels, 0, 5);
        assertEquals((3 + 5 + 7)/3, dest[0]);
        assertEquals((19 + 23 + 25)/3, dest[1]);
        assertEquals((41 + 49 + 51)/3, dest[2]);
        assertEquals((65 + 67 + 71)/3, dest[3]);
        assertEquals((73 + 81 + 87)/3, dest[4]);
    }

    public void test_average3Samples_byte_to_byte() {
        byte[] pixels = new byte[] {
                0x03, 0x05, 0x07,
                0x13, 0x17, 0x19,
                0x29, 0x31, 0x33,
                0x41, 0x43, 0x47,
                0x49, 0x51, 0x57 };
        ConverterUtils.average3Samples(pixels, 0, pixels, 0, 5);
        assertEquals((3 + 5 + 7)/3, pixels[0]);
        assertEquals((19 + 23 + 25)/3, pixels[1]);
        assertEquals((41 + 49 + 51)/3, pixels[2]);
        assertEquals((65 + 67 + 71)/3, pixels[3]);
        assertEquals((73 + 81 + 87)/3, pixels[4]);
    }
}