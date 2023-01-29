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



    public void test_prependAlpha_byte_to_byte() {
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
    public void test_prependAlpha_swapFirstAndThirdSamples_byte_to_byte() {
        byte[] pixels = new byte[] {
                0x03, 0x05, 0x07,
                0x13, 0x17, 0x19,
                0x29, 0x31, 0x33,
                0x41, 0x43, 0x47,
                0x49, 0x51, 0x57,
                0x59, 0x61, 0x67,
                0, 0, 0, 0, 0, 0};
        ConverterUtils.prependAlpha_swapFirstAndThirdSamples(pixels, 0, pixels, 0, 6);
        assertEquals(-1, pixels[0]);
        assertEquals(0x07, pixels[1]);
        assertEquals(0x05, pixels[2]);
        assertEquals(0x03, pixels[3]);

        assertEquals(-1, pixels[4]);
        assertEquals(0x19, pixels[5]);
        assertEquals(0x17, pixels[6]);
        assertEquals(0x13, pixels[7]);

        assertEquals(-1, pixels[8]);
        assertEquals(0x33, pixels[9]);
        assertEquals(0x31, pixels[10]);
        assertEquals(0x29, pixels[11]);

        assertEquals(-1, pixels[12]);
        assertEquals(0x47, pixels[13]);
        assertEquals(0x43, pixels[14]);
        assertEquals(0x41, pixels[15]);

        assertEquals(-1, pixels[16]);
        assertEquals(0x57, pixels[17]);
        assertEquals(0x51, pixels[18]);
        assertEquals(0x49, pixels[19]);

        assertEquals(-1, pixels[20]);
        assertEquals(0x67, pixels[21]);
        assertEquals(0x61, pixels[22]);
        assertEquals(0x59, pixels[23]);
    }
    public void test_prependAlpha_swapFirstAndThirdSamples_int_to_int() {
        int[] pixels = new int[] {
                0x030507,
                0x131719,
                0x293133,
                0x414347,
                0x495157,
                0x596167,
                0, 0, 0, 0, 0, 0};
        ConverterUtils.prependAlpha_swapFirstAndThirdSamples(pixels, 0, pixels, 0, 6);
        assertEquals(0xff070503, pixels[0]);
        assertEquals(0xff191713, pixels[1]);
        assertEquals(0xff333129, pixels[2]);
        assertEquals(0xff474341, pixels[3]);
        assertEquals(0xff575149, pixels[4]);
        assertEquals(0xff676159, pixels[5]);
    }
    public void test_prependAlpha_int_to_int() {
        int[] pixels = new int[] {
                0x030507,
                0x131719,
                0x293133,
                0x414347,
                0x495157,
                0x596167,
                0, 0, 0, 0, 0, 0};
        ConverterUtils.prependAlpha(pixels, 0, pixels, 0, 6);
        assertEquals(0xff030507, pixels[0]);
        assertEquals(0xff131719, pixels[1]);
        assertEquals(0xff293133, pixels[2]);
        assertEquals(0xff414347, pixels[3]);
        assertEquals(0xff495157, pixels[4]);
        assertEquals(0xff596167, pixels[5]);
    }
    public void test_prependAlpha_byte_to_int() {
        byte[] pixels = new byte[] {
                0x03, 0x05, 0x07,
                0x13, 0x17, 0x19,
                0x29, 0x31, 0x33,
                0x41, 0x43, 0x47,
                0x49, 0x51, 0x57,
                0x59, 0x61, 0x67};
        int[] dest = new int[6];
        ConverterUtils.prependAlpha(dest, 0, pixels, 0, 6);
        assertEquals(0xff030507, dest[0]);
        assertEquals(0xff131719, dest[1]);
        assertEquals(0xff293133, dest[2]);
        assertEquals(0xff414347, dest[3]);
        assertEquals(0xff495157, dest[4]);
        assertEquals(0xff596167, dest[5]);
    }

    public void test_prependAlpha_swapFirstAndThirdSamples_byte_to_int() {
        byte[] pixels = new byte[] {
                0x03, 0x05, 0x07,
                0x13, 0x17, 0x19,
                0x29, 0x31, 0x33,
                0x41, 0x43, 0x47,
                0x49, 0x51, 0x57,
                0x59, 0x61, 0x67};
        int[] dest = new int[6];
        ConverterUtils.prependAlpha_swapFirstAndThirdSamples(dest, 0, pixels, 0, 6);
        assertEquals(0xff070503, dest[0]);
        assertEquals(0xff191713, dest[1]);
        assertEquals(0xff333129, dest[2]);
        assertEquals(0xff474341, dest[3]);
        assertEquals(0xff575149, dest[4]);
        assertEquals(0xff676159, dest[5]);
    }

    public void test_prependAlpha_int_to_byte() {
        int[] pixels = new int[] {
                0x030507,
                0x131719,
                0x293133,
                0x414347,
                0x495157,
                0x596167};
        byte[] dest = new byte[4 * 6];
        ConverterUtils.prependAlpha(dest, 0, pixels, 0, 6);
        assertEquals(-1, dest[0]);
        assertEquals(0x03, dest[1]);
        assertEquals(0x05, dest[2]);
        assertEquals(0x07, dest[3]);

        assertEquals(-1, dest[4]);
        assertEquals(0x13, dest[5]);
        assertEquals(0x17, dest[6]);
        assertEquals(0x19, dest[7]);

        assertEquals(-1, dest[8]);
        assertEquals(0x29, dest[9]);
        assertEquals(0x31, dest[10]);
        assertEquals(0x33, dest[11]);

        assertEquals(-1, dest[12]);
        assertEquals(0x41, dest[13]);
        assertEquals(0x43, dest[14]);
        assertEquals(0x47, dest[15]);

        assertEquals(-1, dest[16]);
        assertEquals(0x49, dest[17]);
        assertEquals(0x51, dest[18]);
        assertEquals(0x57, dest[19]);

        assertEquals(-1, dest[20]);
        assertEquals(0x59, dest[21]);
        assertEquals(0x61, dest[22]);
        assertEquals(0x67, dest[23]);
    }

    public void test_prependAlpha_swapFirstAndThirdSamples_int_to_byte() {
        int[] pixels = new int[] {
                0x030507,
                0x131719,
                0x293133,
                0x414347,
                0x495157,
                0x596167};
        byte[] dest = new byte[4 * 6];
        ConverterUtils.prependAlpha_swapFirstAndThirdSamples(dest, 0, pixels, 0, 6);
        assertEquals(-1, dest[0]);
        assertEquals(0x07, dest[1]);
        assertEquals(0x05, dest[2]);
        assertEquals(0x03, dest[3]);

        assertEquals(-1, dest[4]);
        assertEquals(0x19, dest[5]);
        assertEquals(0x17, dest[6]);
        assertEquals(0x13, dest[7]);

        assertEquals(-1, dest[8]);
        assertEquals(0x33, dest[9]);
        assertEquals(0x31, dest[10]);
        assertEquals(0x29, dest[11]);

        assertEquals(-1, dest[12]);
        assertEquals(0x47, dest[13]);
        assertEquals(0x43, dest[14]);
        assertEquals(0x41, dest[15]);

        assertEquals(-1, dest[16]);
        assertEquals(0x57, dest[17]);
        assertEquals(0x51, dest[18]);
        assertEquals(0x49, dest[19]);

        assertEquals(-1, dest[20]);
        assertEquals(0x67, dest[21]);
        assertEquals(0x61, dest[22]);
        assertEquals(0x59, dest[23]);
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

    public void test_convert4samples_int_to_byte() {
        int[] pixels = new int[] {
                0x63030507,
                0x67131719,
                0x71293133,
                0x73414347,
                0x77495157 };
        byte[] dest = new byte[4 * 5];
        ConverterUtils.convert4samples(dest, 0, pixels, 0, 5);

        assertEquals(0x63, dest[0]);
        assertEquals(0x03, dest[1]);
        assertEquals(0x05, dest[2]);
        assertEquals(0x07, dest[3]);

        assertEquals(0x67, dest[4]);
        assertEquals(0x13, dest[5]);
        assertEquals(0x17, dest[6]);
        assertEquals(0x19, dest[7]);

        assertEquals(0x71, dest[8]);
        assertEquals(0x29, dest[9]);
        assertEquals(0x31, dest[10]);
        assertEquals(0x33, dest[11]);

        assertEquals(0x73, dest[12]);
        assertEquals(0x41, dest[13]);
        assertEquals(0x43, dest[14]);
        assertEquals(0x47, dest[15]);

        assertEquals(0x77, dest[16]);
        assertEquals(0x49, dest[17]);
        assertEquals(0x51, dest[18]);
        assertEquals(0x57, dest[19]);
    }

    public void test_convert4samples_swapFirstAndThird_int_to_byte() {
        int[] pixels = new int[] {
                0x63030507,
                0x67131719,
                0x71293133,
                0x73414347,
                0x77495157 };
        byte[] dest = new byte[4 * 5];
        ConverterUtils.convert4samples_swapFirstAndThird(dest, 0, pixels, 0, 5);

        assertEquals(0x63, dest[0]);
        assertEquals(0x07, dest[1]);
        assertEquals(0x05, dest[2]);
        assertEquals(0x03, dest[3]);

        assertEquals(0x67, dest[4]);
        assertEquals(0x19, dest[5]);
        assertEquals(0x17, dest[6]);
        assertEquals(0x13, dest[7]);

        assertEquals(0x71, dest[8]);
        assertEquals(0x33, dest[9]);
        assertEquals(0x31, dest[10]);
        assertEquals(0x29, dest[11]);

        assertEquals(0x73, dest[12]);
        assertEquals(0x47, dest[13]);
        assertEquals(0x43, dest[14]);
        assertEquals(0x41, dest[15]);

        assertEquals(0x77, dest[16]);
        assertEquals(0x57, dest[17]);
        assertEquals(0x51, dest[18]);
        assertEquals(0x49, dest[19]);
    }
    public void test_convert4samples_byte_to_int() {
        byte[] pixels = new byte[] {
                0x63, 0x03, 0x05, 0x07,
                0x67, 0x13, 0x17, 0x19,
                0x71, 0x29, 0x31, 0x33,
                0x73, 0x41, 0x43, 0x47,
                0x77, 0x49, 0x51, 0x57 };
        int[] dest = new int[5];
        ConverterUtils.convert4samples(dest, 0, pixels, 0, 5);

        assertEquals(0x63030507, dest[0]);
        assertEquals(0x67131719, dest[1]);
        assertEquals(0x71293133, dest[2]);
        assertEquals(0x73414347, dest[3]);
        assertEquals(0x77495157, dest[4]);
    }

    public void test_convert4samples_swapFirstAndThird_byte_to_int() {
        byte[] pixels = new byte[] {
                0x63, 0x03, 0x05, 0x07,
                0x67, 0x13, 0x17, 0x19,
                0x71, 0x29, 0x31, 0x33,
                0x73, 0x41, 0x43, 0x47,
                0x77, 0x49, 0x51, 0x57 };
        int[] dest = new int[5];
        ConverterUtils.convert4samples_swapFirstAndThird(dest, 0, pixels, 0, 5);

        assertEquals(0x63070503, dest[0]);
        assertEquals(0x67191713, dest[1]);
        assertEquals(0x71333129, dest[2]);
        assertEquals(0x73474341, dest[3]);
        assertEquals(0x77575149, dest[4]);
    }
}