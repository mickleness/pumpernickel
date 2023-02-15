package com.pump.image.pixel2.converter;

import junit.framework.TestCase;

public class ConverterUtilsTest extends TestCase {
    public void test_convert_XYZ_ints_to_ZYX_ints() {
        // test storing it to the left of where our source pixels start
        int[] pixels = new int[] {0x02030507, 0x11131719, 0x23293133, 0x37414347};
        ConverterUtils.convert_XYZ_ints_to_ZYX_ints(pixels, 0, pixels, 0, 4);
        assertEquals(Integer.toUnsignedString(pixels[0], 16),0x02070503, pixels[0]);
        assertEquals(Integer.toUnsignedString(pixels[1], 16),0x11191713, pixels[1]);
        assertEquals(Integer.toUnsignedString(pixels[2], 16),0x23333129, pixels[2]);
        assertEquals(Integer.toUnsignedString(pixels[3], 16),0x37474341, pixels[3]);
    }

    public void test_convert_XYZ_bytes_to_AXYZ_bytes() {
        byte[] pixels = new byte[] {
                0x03, 0x05, 0x07,
                0x13, 0x17, 0x19,
                0x29, 0x31, 0x33,
                0x41, 0x43, 0x47,
                0x49, 0x51, 0x57,
                0x59, 0x61, 0x67,
                0, 0, 0, 0, 0, 0};
        ConverterUtils.convert_XYZ_bytes_to_AXYZ_bytes(pixels, 0, pixels, 0, 6);
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
    public void test_convert_XYZ_bytes_to_AZYX_bytes() {
        byte[] pixels = new byte[] {
                0x03, 0x05, 0x07,
                0x13, 0x17, 0x19,
                0x29, 0x31, 0x33,
                0x41, 0x43, 0x47,
                0x49, 0x51, 0x57,
                0x59, 0x61, 0x67,
                0, 0, 0, 0, 0, 0};
        ConverterUtils.convert_XYZ_bytes_to_AZYX_bytes(pixels, 0, pixels, 0, 6);
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
    public void test_convert_XYZ_ints_to_AZYX_ints() {
        int[] pixels = new int[] {
                0x030507,
                0x131719,
                0x293133,
                0x414347,
                0x495157,
                0x596167,
                0, 0, 0, 0, 0, 0};
        ConverterUtils.convert_XYZ_ints_to_AZYX_ints(pixels, 0, pixels, 0, 6);
        assertEquals(Integer.toUnsignedString(pixels[0], 16),0xff070503, pixels[0]);
        assertEquals(Integer.toUnsignedString(pixels[1], 16),0xff191713, pixels[1]);
        assertEquals(Integer.toUnsignedString(pixels[2], 16),0xff333129, pixels[2]);
        assertEquals(Integer.toUnsignedString(pixels[3], 16),0xff474341, pixels[3]);
        assertEquals(Integer.toUnsignedString(pixels[4], 16),0xff575149, pixels[4]);
        assertEquals(Integer.toUnsignedString(pixels[5], 16),0xff676159, pixels[5]);
    }
    public void test_convert_XYZ_ints_to_AXYZ_ints() {
        int[] pixels = new int[] {
                0x030507,
                0x131719,
                0x293133,
                0x414347,
                0x495157,
                0x596167,
                0, 0, 0, 0, 0, 0};
        ConverterUtils.convert_XYZ_ints_to_AXYZ_ints(pixels, 0, pixels, 0, 6);
        assertEquals(Integer.toUnsignedString(pixels[0], 16),0xff030507, pixels[0]);
        assertEquals(Integer.toUnsignedString(pixels[1], 16),0xff131719, pixels[1]);
        assertEquals(Integer.toUnsignedString(pixels[2], 16),0xff293133, pixels[2]);
        assertEquals(Integer.toUnsignedString(pixels[3], 16),0xff414347, pixels[3]);
        assertEquals(Integer.toUnsignedString(pixels[4], 16),0xff495157, pixels[4]);
        assertEquals(Integer.toUnsignedString(pixels[5], 16),0xff596167, pixels[5]);
    }
    public void test_convert_XYZ_bytes_to_AXYZ_ints() {
        byte[] pixels = new byte[] {
                0x03, 0x05, 0x07,
                0x13, 0x17, 0x19,
                0x29, 0x31, 0x33,
                0x41, 0x43, 0x47,
                0x49, 0x51, 0x57,
                0x59, 0x61, 0x67};
        int[] dest = new int[6];
        ConverterUtils.convert_XYZ_bytes_to_AXYZ_ints(dest, 0, pixels, 0, 6);
        assertEquals(Integer.toUnsignedString(dest[0], 16),0xff030507, dest[0]);
        assertEquals(Integer.toUnsignedString(dest[1], 16),0xff131719, dest[1]);
        assertEquals(Integer.toUnsignedString(dest[2], 16),0xff293133, dest[2]);
        assertEquals(Integer.toUnsignedString(dest[3], 16),0xff414347, dest[3]);
        assertEquals(Integer.toUnsignedString(dest[4], 16),0xff495157, dest[4]);
        assertEquals(Integer.toUnsignedString(dest[5], 16),0xff596167, dest[5]);
    }

    public void test_convert_XYZ_bytes_to_AZYX_ints() {
        byte[] pixels = new byte[] {
                0x03, 0x05, 0x07,
                0x13, 0x17, 0x19,
                0x29, 0x31, 0x33,
                0x41, 0x43, 0x47,
                0x49, 0x51, 0x57,
                0x59, 0x61, 0x67};
        int[] dest = new int[6];
        ConverterUtils.convert_XYZ_bytes_to_AZYX_ints(dest, 0, pixels, 0, 6);
        assertEquals(Integer.toUnsignedString(dest[0], 16),0xff070503, dest[0]);
        assertEquals(Integer.toUnsignedString(dest[1], 16),0xff191713, dest[1]);
        assertEquals(Integer.toUnsignedString(dest[2], 16),0xff333129, dest[2]);
        assertEquals(Integer.toUnsignedString(dest[3], 16),0xff474341, dest[3]);
        assertEquals(Integer.toUnsignedString(dest[4], 16),0xff575149, dest[4]);
        assertEquals(Integer.toUnsignedString(dest[5], 16),0xff676159, dest[5]);
    }

    public void test_convert_XYZ_ints_to_AXYZ_bytes() {
        int[] pixels = new int[] {
                0x030507,
                0x131719,
                0x293133,
                0x414347,
                0x495157,
                0x596167};
        byte[] dest = new byte[4 * 6];
        ConverterUtils.convert_XYZ_ints_to_AXYZ_bytes(dest, 0, pixels, 0, 6);
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

    public void test_convert_XYZ_ints_to_AZYX_bytes() {
        int[] pixels = new int[] {
                0x030507,
                0x131719,
                0x293133,
                0x414347,
                0x495157,
                0x596167};
        byte[] dest = new byte[4 * 6];
        ConverterUtils.convert_XYZ_ints_to_AZYX_bytes(dest, 0, pixels, 0, 6);
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

    public void test_convert_AXYZ_bytes_to_AZYX_bytes() {
        byte[] pixels = new byte[] {
                -1, 0x55, 0x62, 0x08,
                0, 0x15, 0x67, 0x42,
                0x03, 0x05, 0x07, 0x13,
                0x17, 0x19, 0x29, 0x31,
                0x33, 0x41, 0x43, 0x47,
                0x49, 0x51, 0x57, 0x59 };
        ConverterUtils.convert_AXYZ_bytes_to_AZYX_bytes(pixels, 0, pixels, 0, 6);

        assertEquals(-1, pixels[0]);
        assertEquals(0x08, pixels[1]);
        assertEquals(0x62, pixels[2]);
        assertEquals(0x55, pixels[3]);

        assertEquals(0, pixels[4]);
        assertEquals(0x42, pixels[5]);
        assertEquals(0x67, pixels[6]);
        assertEquals(0x15, pixels[7]);

        assertEquals(0x03, pixels[8]);
        assertEquals(0x13, pixels[9]);
        assertEquals(0x07, pixels[10]);
        assertEquals(0x05, pixels[11]);

        assertEquals(0x17, pixels[12]);
        assertEquals(0x31, pixels[13]);
        assertEquals(0x29, pixels[14]);
        assertEquals(0x19, pixels[15]);

        assertEquals(0x33, pixels[16]);
        assertEquals(0x47, pixels[17]);
        assertEquals(0x43, pixels[18]);
        assertEquals(0x41, pixels[19]);

        assertEquals(0x49, pixels[20]);
        assertEquals(0x59, pixels[21]);
        assertEquals(0x57, pixels[22]);
        assertEquals(0x51, pixels[23]);

    }

    public void test_convert_XYZ_bytes_to_XYZ_ints() {

        byte[] pixels = new byte[] {
                0x03, 0x05, 0x07,
                0x13, 0x17, 0x19,
                0x29, 0x31, 0x33,
                0x41, 0x43, 0x47,
                0x49, 0x51, 0x57 };

        int[] dest = new int[5];
        ConverterUtils.convert_XYZ_bytes_to_XYZ_ints(dest, 0, pixels, 0, 5);
        assertEquals(Integer.toUnsignedString(dest[0], 16),0x030507, dest[0]);
        assertEquals(Integer.toUnsignedString(dest[1], 16),0x131719, dest[1]);
        assertEquals(Integer.toUnsignedString(dest[2], 16),0x293133, dest[2]);
        assertEquals(Integer.toUnsignedString(dest[3], 16),0x414347, dest[3]);
        assertEquals(Integer.toUnsignedString(dest[4], 16),0x495157, dest[4]);
    }

    public void test_convert_XYZ_bytes_to_ZYX_ints() {

        byte[] pixels = new byte[] {
                0x03, 0x05, 0x07,
                0x13, 0x17, 0x19,
                0x29, 0x31, 0x33,
                0x41, 0x43, 0x47,
                0x49, 0x51, 0x57 };

        int[] dest = new int[5];
        ConverterUtils.convert_XYZ_bytes_to_ZYX_ints(dest, 0, pixels, 0, 5);
        assertEquals(Integer.toUnsignedString(dest[0], 16),0x070503, dest[0]);
        assertEquals(Integer.toUnsignedString(dest[1], 16),0x191713, dest[1]);
        assertEquals(Integer.toUnsignedString(dest[2], 16),0x333129, dest[2]);
        assertEquals(Integer.toUnsignedString(dest[3], 16),0x474341, dest[3]);
        assertEquals(Integer.toUnsignedString(dest[4], 16),0x575149, dest[4]);
    }

    public void test_convert_XYZ_ints_to_G_bytes() {
        int[] pixels = new int[] {
                0x030507,
                0x131719,
                0x293133,
                0x414347,
                0x495157 };
        byte[] dest = new byte[3 * 5];
        ConverterUtils.convert_XYZ_ints_to_G_bytes(dest, 0, pixels, 0, 5);
        assertEquals((3 + 5 + 7)/3, dest[0]);
        assertEquals((19 + 23 + 25)/3, dest[1]);
        assertEquals((41 + 49 + 51)/3, dest[2]);
        assertEquals((65 + 67 + 71)/3, dest[3]);
        assertEquals((73 + 81 + 87)/3, dest[4]);
    }

    public void test_convert_XYZ_bytes_to_G_bytes() {
        byte[] pixels = new byte[] {
                0x03, 0x05, 0x07,
                0x13, 0x17, 0x19,
                0x29, 0x31, 0x33,
                0x41, 0x43, 0x47,
                0x49, 0x51, 0x57 };
        ConverterUtils.convert_XYZ_bytes_to_G_bytes(pixels, 0, pixels, 0, 5);
        assertEquals((3 + 5 + 7)/3, pixels[0]);
        assertEquals((19 + 23 + 25)/3, pixels[1]);
        assertEquals((41 + 49 + 51)/3, pixels[2]);
        assertEquals((65 + 67 + 71)/3, pixels[3]);
        assertEquals((73 + 81 + 87)/3, pixels[4]);
    }

    public void test_convert_AXYZ_ints_to_AXYZ_bytes() {
        int[] pixels = new int[] {
                0x63030507,
                0x67131719,
                0x71293133,
                0x73414347,
                0x77495157 };
        byte[] dest = new byte[4 * 5];
        ConverterUtils.convert_AXYZ_ints_to_AXYZ_bytes(dest, 0, pixels, 0, 5);

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

    public void test_convert_AXYZ_ints_to_AZYX_bytes() {
        int[] pixels = new int[] {
                0x63030507,
                0x67131719,
                0x71293133,
                0x73414347,
                0x77495157 };
        byte[] dest = new byte[4 * 5];
        ConverterUtils.convert_AXYZ_ints_to_AZYX_bytes(dest, 0, pixels, 0, 5);

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
    public void test_convert_AXYZ_bytes_to_AXYZ_ints() {
        byte[] pixels = new byte[] {
                -1, 0x55, 0x62, 0x08,
                0, 0x15, 0x67, 0x42,
                0x63, 0x03, 0x05, 0x07,
                0x67, 0x13, 0x17, 0x19,
                0x71, 0x29, 0x31, 0x33,
                0x73, 0x41, 0x43, 0x47,
                0x77, 0x49, 0x51, 0x57};
        int[] dest = new int[7];
        ConverterUtils.convert_AXYZ_bytes_to_AXYZ_ints(dest, 0, pixels, 0, 7);

        assertEquals(Integer.toUnsignedString(dest[0], 16),0xff556208, dest[0]);
        assertEquals(Integer.toUnsignedString(dest[1], 16),0x00156742, dest[1]);
        assertEquals(Integer.toUnsignedString(dest[2], 16),0x63030507, dest[2]);
        assertEquals(Integer.toUnsignedString(dest[3], 16),0x67131719, dest[3]);
        assertEquals(Integer.toUnsignedString(dest[4], 16),0x71293133, dest[4]);
        assertEquals(Integer.toUnsignedString(dest[5], 16),0x73414347, dest[5]);
        assertEquals(Integer.toUnsignedString(dest[6], 16),0x77495157, dest[6]);
    }

    public void test_convert_AXYZ_bytes_to_AZYX_ints() {
        byte[] pixels = new byte[] {
                -1, 0x55, 0x62, 0x08,
                0, 0x15, 0x67, 0x42,
                0x63, 0x03, 0x05, 0x07,
                0x67, 0x13, 0x17, 0x19,
                0x71, 0x29, 0x31, 0x33,
                0x73, 0x41, 0x43, 0x47,
                0x77, 0x49, 0x51, 0x57 };
        int[] dest = new int[7];
        ConverterUtils.convert_AXYZ_bytes_to_AZYX_ints(dest, 0, pixels, 0, 7);

        assertEquals(Integer.toUnsignedString(dest[0], 16),0xff086255, dest[0]);
        assertEquals(Integer.toUnsignedString(dest[1], 16),0x00426715, dest[1]);
        assertEquals(Integer.toUnsignedString(dest[2], 16),0x63070503, dest[2]);
        assertEquals(Integer.toUnsignedString(dest[3], 16),0x67191713, dest[3]);
        assertEquals(Integer.toUnsignedString(dest[4], 16),0x71333129, dest[4]);
        assertEquals(Integer.toUnsignedString(dest[5], 16),0x73474341, dest[5]);
        assertEquals(Integer.toUnsignedString(dest[6], 16),0x77575149, dest[6]);
    }

    public void test_convert_G_bytes_to_AXYZ_bytes() {
        byte[] pixels = new byte[] {
                0x63, 0x03, 0x05, 0x07,
                0x67, 0x13, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        ConverterUtils.convert_G_bytes_to_AXYZ_bytes(pixels, 0, pixels, 0, 6);

        assertEquals(-1, pixels[0]);
        assertEquals(0x63, pixels[1]);
        assertEquals(0x63, pixels[2]);
        assertEquals(0x63, pixels[3]);

        assertEquals(-1, pixels[4]);
        assertEquals(0x03, pixels[5]);
        assertEquals(0x03, pixels[6]);
        assertEquals(0x03, pixels[7]);

        assertEquals(-1, pixels[8]);
        assertEquals(0x05, pixels[9]);
        assertEquals(0x05, pixels[10]);
        assertEquals(0x05, pixels[11]);

        assertEquals(-1, pixels[12]);
        assertEquals(0x07, pixels[13]);
        assertEquals(0x07, pixels[14]);
        assertEquals(0x07, pixels[15]);

        assertEquals(-1, pixels[16]);
        assertEquals(0x67, pixels[17]);
        assertEquals(0x67, pixels[18]);
        assertEquals(0x67, pixels[19]);

        assertEquals(-1, pixels[20]);
        assertEquals(0x13, pixels[21]);
        assertEquals(0x13, pixels[22]);
        assertEquals(0x13, pixels[23]);
    }

    public void test_convert_G_bytes_to_XYZ_bytes() {
        byte[] pixels = new byte[] {
                0x63, 0x03, 0x05, 0x07,
                0x67, 0x13, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        ConverterUtils.convert_G_bytes_to_XYZ_bytes(pixels, 0, pixels, 0, 6);

        assertEquals(0x63, pixels[0]);
        assertEquals(0x63, pixels[1]);
        assertEquals(0x63, pixels[2]);

        assertEquals(0x03, pixels[3]);
        assertEquals(0x03, pixels[4]);
        assertEquals(0x03, pixels[5]);

        assertEquals(0x05, pixels[6]);
        assertEquals(0x05, pixels[7]);
        assertEquals(0x05, pixels[8]);

        assertEquals(0x07, pixels[9]);
        assertEquals(0x07, pixels[10]);
        assertEquals(0x07, pixels[11]);

        assertEquals(0x67, pixels[12]);
        assertEquals(0x67, pixels[13]);
        assertEquals(0x67, pixels[14]);

        assertEquals(0x13, pixels[15]);
        assertEquals(0x13, pixels[16]);
        assertEquals(0x13, pixels[17]);
    }

    public void test_convert_G_bytes_to_AXYZ_ints() {
        byte[] pixels = new byte[] {
                0x63, 0x03, 0x05, 0x07,
                0x67, 0x13};
        int[] dest = new int[6];
        ConverterUtils.convert_G_bytes_to_AXYZ_ints(dest, 0, pixels, 0, 6);

        assertEquals(Integer.toUnsignedString(dest[0], 16),0xff636363, dest[0]);
        assertEquals(Integer.toUnsignedString(dest[1], 16),0xff030303, dest[1]);
        assertEquals(Integer.toUnsignedString(dest[2], 16),0xff050505, dest[2]);
        assertEquals(Integer.toUnsignedString(dest[3], 16),0xff070707, dest[3]);
        assertEquals(Integer.toUnsignedString(dest[4], 16),0xff676767, dest[4]);
        assertEquals(Integer.toUnsignedString(dest[5], 16),0xff131313, dest[5]);
    }

    public void test_convert_G_bytes_to_XYZ_ints() {
        byte[] pixels = new byte[] {
                0x63, 0x03, 0x05, 0x07,
                0x67, 0x13};
        int[] dest = new int[6];
        ConverterUtils.convert_G_bytes_to_XYZ_ints(dest, 0, pixels, 0, 6);

        assertEquals(Integer.toUnsignedString(dest[0]), 16,0x636363, dest[0]);
        assertEquals(Integer.toUnsignedString(dest[1], 16),0x030303, dest[1]);
        assertEquals(Integer.toUnsignedString(dest[2], 16),0x050505, dest[2]);
        assertEquals(Integer.toUnsignedString(dest[3], 16),0x070707, dest[3]);
        assertEquals(Integer.toUnsignedString(dest[4], 16),0x676767, dest[4]);
        assertEquals(Integer.toUnsignedString(dest[5], 16),0x131313, dest[5]);
    }

    public void test_convert_XYZ_ints_to_XYZ_bytes() {
        int[] pixels = new int[] {0x030507, 0x131719, 0x293133, 0x414347, 0x495157 };
        byte[] dest = new byte[3 * 5];

        ConverterUtils.convert_XYZ_ints_to_XYZ_bytes(dest, 0, pixels, 0, 5);

        assertEquals(0x03, dest[0]);
        assertEquals(0x05, dest[1]);
        assertEquals(0x07, dest[2]);

        assertEquals(0x13, dest[3]);
        assertEquals(0x17, dest[4]);
        assertEquals(0x19, dest[5]);

        assertEquals(0x29, dest[6]);
        assertEquals(0x31, dest[7]);
        assertEquals(0x33, dest[8]);

        assertEquals(0x41, dest[9]);
        assertEquals(0x43, dest[10]);
        assertEquals(0x47, dest[11]);

        assertEquals(0x49, dest[12]);
        assertEquals(0x51, dest[13]);
        assertEquals(0x57, dest[14]);
    }

    public void test_convert_XYZ_ints_to_ZYX_bytes() {
        int[] pixels = new int[] {0x030507, 0x131719, 0x293133, 0x414347, 0x495157 };
        byte[] dest = new byte[3 * 5];

        ConverterUtils.convert_XYZ_ints_to_ZYX_bytes(dest, 0, pixels, 0, 5);

        assertEquals(0x07, dest[0]);
        assertEquals(0x05, dest[1]);
        assertEquals(0x03, dest[2]);

        assertEquals(0x19, dest[3]);
        assertEquals(0x17, dest[4]);
        assertEquals(0x13, dest[5]);

        assertEquals(0x33, dest[6]);
        assertEquals(0x31, dest[7]);
        assertEquals(0x29, dest[8]);

        assertEquals(0x47, dest[9]);
        assertEquals(0x43, dest[10]);
        assertEquals(0x41, dest[11]);

        assertEquals(0x57, dest[12]);
        assertEquals(0x51, dest[13]);
        assertEquals(0x49, dest[14]);
    }

    public void test_convert_XYZ_bytes_to_ZYX_bytes() {
        byte[] pixels = new byte[] {
                0x03, 0x05, 0x07,
                0x13, 0x17, 0x19,
                0x29, 0x31, 0x33,
                0x41, 0x43, 0x47,
                0x49, 0x51, 0x57 };
        ConverterUtils.convert_XYZ_bytes_to_ZYX_bytes(pixels, 0, pixels, 0, 5);

        assertEquals(0x07, pixels[0]);
        assertEquals(0x05, pixels[1]);
        assertEquals(0x03, pixels[2]);

        assertEquals(0x19, pixels[3]);
        assertEquals(0x17, pixels[4]);
        assertEquals(0x13, pixels[5]);

        assertEquals(0x33, pixels[6]);
        assertEquals(0x31, pixels[7]);
        assertEquals(0x29, pixels[8]);

        assertEquals(0x47, pixels[9]);
        assertEquals(0x43, pixels[10]);
        assertEquals(0x41, pixels[11]);

        assertEquals(0x57, pixels[12]);
        assertEquals(0x51, pixels[13]);
        assertEquals(0x49, pixels[14]);
    }

    public void test_convert_AXYZ_ints_to_ZYXA_bytes() {
        int[] pixels = new int[] {0x61030507, 0x67131719, 0x71293133, 0x73414347, 0x79495157 };
        byte[] dest = new byte[4 * 5];

        ConverterUtils.convert_AXYZ_ints_to_ZYXA_bytes(dest, 0, pixels, 0, 5);

        assertEquals(0x07, dest[0]);
        assertEquals(0x05, dest[1]);
        assertEquals(0x03, dest[2]);
        assertEquals(0x61, dest[3]);

        assertEquals(0x19, dest[4]);
        assertEquals(0x17, dest[5]);
        assertEquals(0x13, dest[6]);
        assertEquals(0x67, dest[7]);

        assertEquals(0x33, dest[8]);
        assertEquals(0x31, dest[9]);
        assertEquals(0x29, dest[10]);
        assertEquals(0x71, dest[11]);

        assertEquals(0x47, dest[12]);
        assertEquals(0x43, dest[13]);
        assertEquals(0x41, dest[14]);
        assertEquals(0x73, dest[15]);

        assertEquals(0x57, dest[16]);
        assertEquals(0x51, dest[17]);
        assertEquals(0x49, dest[18]);
        assertEquals(0x79, dest[19]);
    }

    public void test_convert_XYZ_ints_to_XYZA_bytes() {
        int[] pixels = new int[] {0x030507, 0x131719, 0x293133, 0x414347, 0x495157 };
        byte[] dest = new byte[4 * 5];

        ConverterUtils.convert_XYZ_ints_to_XYZA_bytes(dest, 0, pixels, 0, 5);

        assertEquals(0x03, dest[0]);
        assertEquals(0x05, dest[1]);
        assertEquals(0x07, dest[2]);
        assertEquals(-1, dest[3]);

        assertEquals(0x13, dest[4]);
        assertEquals(0x17, dest[5]);
        assertEquals(0x19, dest[6]);
        assertEquals(-1, dest[7]);

        assertEquals(0x29, dest[8]);
        assertEquals(0x31, dest[9]);
        assertEquals(0x33, dest[10]);
        assertEquals(-1, dest[11]);

        assertEquals(0x41, dest[12]);
        assertEquals(0x43, dest[13]);
        assertEquals(0x47, dest[14]);
        assertEquals(-1, dest[15]);

        assertEquals(0x49, dest[16]);
        assertEquals(0x51, dest[17]);
        assertEquals(0x57, dest[18]);
        assertEquals(-1, dest[19]);
    }

    public void test_convert_XYZ_ints_to_ZYXA_bytes() {
        int[] pixels = new int[] {0x030507, 0x131719, 0x293133, 0x414347, 0x495157 };
        byte[] dest = new byte[4 * 5];

        ConverterUtils.convert_XYZ_ints_to_ZYXA_bytes(dest, 0, pixels, 0, 5);

        assertEquals(0x07, dest[0]);
        assertEquals(0x05, dest[1]);
        assertEquals(0x03, dest[2]);
        assertEquals(-1, dest[3]);

        assertEquals(0x19, dest[4]);
        assertEquals(0x17, dest[5]);
        assertEquals(0x13, dest[6]);
        assertEquals(-1, dest[7]);

        assertEquals(0x33, dest[8]);
        assertEquals(0x31, dest[9]);
        assertEquals(0x29, dest[10]);
        assertEquals(-1, dest[11]);

        assertEquals(0x47, dest[12]);
        assertEquals(0x43, dest[13]);
        assertEquals(0x41, dest[14]);
        assertEquals(-1, dest[15]);

        assertEquals(0x57, dest[16]);
        assertEquals(0x51, dest[17]);
        assertEquals(0x49, dest[18]);
        assertEquals(-1, dest[19]);
    }

    public void test_convert_AXYZ_bytes_to_ZYXA_bytes() {
        byte[] pixels = new byte[] {
                -1, 0x55, 0x62, 0x08,
                0, 0x15, 0x67, 0x42,
                0x03, 0x05, 0x07, 0x59,
                0x13, 0x17, 0x19, 0x61,
                0x29, 0x31, 0x33, 0x67,
                0x41, 0x43, 0x47, 0x71,
                0x49, 0x51, 0x57, 0x73 };
        ConverterUtils.convert_AXYZ_bytes_to_ZYXA_bytes(pixels, 0, pixels, 0, 7);

        assertEquals(0x08, pixels[0]);
        assertEquals(0x62, pixels[1]);
        assertEquals(0x55, pixels[2]);
        assertEquals(-1, pixels[3]);

        assertEquals(0x42, pixels[4]);
        assertEquals(0x67, pixels[5]);
        assertEquals(0x15, pixels[6]);
        assertEquals(0x00, pixels[7]);

        assertEquals(0x59, pixels[8]);
        assertEquals(0x07, pixels[9]);
        assertEquals(0x05, pixels[10]);
        assertEquals(0x03, pixels[11]);

        assertEquals(0x61, pixels[12]);
        assertEquals(0x19, pixels[13]);
        assertEquals(0x17, pixels[14]);
        assertEquals(0x13, pixels[15]);

        assertEquals(0x67, pixels[16]);
        assertEquals(0x33, pixels[17]);
        assertEquals(0x31, pixels[18]);
        assertEquals(0x29, pixels[19]);

        assertEquals(0x71, pixels[20]);
        assertEquals(0x47, pixels[21]);
        assertEquals(0x43, pixels[22]);
        assertEquals(0x41, pixels[23]);

        assertEquals(0x73, pixels[24]);
        assertEquals(0x57, pixels[25]);
        assertEquals(0x51, pixels[26]);
        assertEquals(0x49, pixels[27]);
    }

    public void test_convert_AXYZ_bytes_to_XYZA_bytes() {
        byte[] pixels = new byte[] {
                -1, 0x55, 0x62, 0x08,
                0, 0x15, 0x67, 0x42,
                0x03, 0x05, 0x07, 0x59,
                0x13, 0x17, 0x19, 0x61,
                0x29, 0x31, 0x33, 0x67,
                0x41, 0x43, 0x47, 0x71,
                0x49, 0x51, 0x57, 0x73 };
        ConverterUtils.convert_AXYZ_bytes_to_XYZA_bytes(pixels, 0, pixels, 0, 7);

        assertEquals(0x55, pixels[0]);
        assertEquals(0x62, pixels[1]);
        assertEquals(0x08, pixels[2]);
        assertEquals(-1, pixels[3]);

        assertEquals(0x15, pixels[4]);
        assertEquals(0x67, pixels[5]);
        assertEquals(0x42, pixels[6]);
        assertEquals(0, pixels[7]);

        assertEquals(0x05, pixels[8]);
        assertEquals(0x07, pixels[9]);
        assertEquals(0x59, pixels[10]);
        assertEquals(0x03, pixels[11]);

        assertEquals(0x17, pixels[12]);
        assertEquals(0x19, pixels[13]);
        assertEquals(0x61, pixels[14]);
        assertEquals(0x13, pixels[15]);

        assertEquals(0x31, pixels[16]);
        assertEquals(0x33, pixels[17]);
        assertEquals(0x67, pixels[18]);
        assertEquals(0x29, pixels[19]);

        assertEquals(0x43, pixels[20]);
        assertEquals(0x47, pixels[21]);
        assertEquals(0x71, pixels[22]);
        assertEquals(0x41, pixels[23]);

        assertEquals(0x51, pixels[24]);
        assertEquals(0x57, pixels[25]);
        assertEquals(0x73, pixels[26]);
        assertEquals(0x49, pixels[27]);
    }

    public void test_convert_G_bytes_to_XYZA_bytes() {
        byte[] pixels = new byte[] {
                0x03,
                0x13,
                0x29,
                0x41,
                0x49,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        ConverterUtils.convert_G_bytes_to_XYZA_bytes(pixels, 0, pixels, 0, 5);

        assertEquals(0x03, pixels[0]);
        assertEquals(0x03, pixels[1]);
        assertEquals(0x03, pixels[2]);
        assertEquals(-1, pixels[3]);

        assertEquals(0x13, pixels[4]);
        assertEquals(0x13, pixels[5]);
        assertEquals(0x13, pixels[6]);
        assertEquals(-1, pixels[7]);

        assertEquals(0x29, pixels[8]);
        assertEquals(0x29, pixels[9]);
        assertEquals(0x29, pixels[10]);
        assertEquals(-1, pixels[11]);

        assertEquals(0x41, pixels[12]);
        assertEquals(0x41, pixels[13]);
        assertEquals(0x41, pixels[14]);
        assertEquals(-1, pixels[15]);

        assertEquals(0x49, pixels[16]);
        assertEquals(0x49, pixels[17]);
        assertEquals(0x49, pixels[18]);
        assertEquals(-1, pixels[19]);
    }

    public void test_convert_XYZ_bytes_to_ZYXA_bytes() {
        byte[] pixels = new byte[] {
                0x03, 0x05, 0x07,
                0x13, 0x17, 0x19,
                0x29, 0x31, 0x33,
                0x41, 0x43, 0x47,
                0x49, 0x51, 0x57,
                0, 0, 0, 0, 0};
        ConverterUtils.convert_XYZ_bytes_to_ZYXA_bytes(pixels, 0, pixels, 0, 5);

        assertEquals(0x07, pixels[0]);
        assertEquals(0x05, pixels[1]);
        assertEquals(0x03, pixels[2]);
        assertEquals(-1, pixels[3]);

        assertEquals(0x19, pixels[4]);
        assertEquals(0x17, pixels[5]);
        assertEquals(0x13, pixels[6]);
        assertEquals(-1, pixels[7]);

        assertEquals(0x33, pixels[8]);
        assertEquals(0x31, pixels[9]);
        assertEquals(0x29, pixels[10]);
        assertEquals(-1, pixels[11]);

        assertEquals(0x47, pixels[12]);
        assertEquals(0x43, pixels[13]);
        assertEquals(0x41, pixels[14]);
        assertEquals(-1, pixels[15]);

        assertEquals(0x57, pixels[16]);
        assertEquals(0x51, pixels[17]);
        assertEquals(0x49, pixels[18]);
        assertEquals(-1, pixels[19]);
    }

    public void test_convert_XYZ_bytes_to_XYZA_bytes() {
        byte[] pixels = new byte[] {
                0x03, 0x05, 0x07,
                0x13, 0x17, 0x19,
                0x29, 0x31, 0x33,
                0x41, 0x43, 0x47,
                0x49, 0x51, 0x57,
                0, 0, 0, 0, 0};
        ConverterUtils.convert_XYZ_bytes_to_XYZA_bytes(pixels, 0, pixels, 0, 5);

        assertEquals(0x03, pixels[0]);
        assertEquals(0x05, pixels[1]);
        assertEquals(0x07, pixels[2]);
        assertEquals(-1, pixels[3]);

        assertEquals(0x13, pixels[4]);
        assertEquals(0x17, pixels[5]);
        assertEquals(0x19, pixels[6]);
        assertEquals(-1, pixels[7]);

        assertEquals(0x29, pixels[8]);
        assertEquals(0x31, pixels[9]);
        assertEquals(0x33, pixels[10]);
        assertEquals(-1, pixels[11]);

        assertEquals(0x41, pixels[12]);
        assertEquals(0x43, pixels[13]);
        assertEquals(0x47, pixels[14]);
        assertEquals(-1, pixels[15]);

        assertEquals(0x49, pixels[16]);
        assertEquals(0x51, pixels[17]);
        assertEquals(0x57, pixels[18]);
        assertEquals(-1, pixels[19]);
    }

    public void test_convert_XYZA_bytes_to_AXYZ_bytes() {
        byte[] pixels = new byte[] {
                0x03, 0x05, 0x07, 0x59,
                0x13, 0x17, 0x19, 0x61,
                0x29, 0x31, 0x33, 0x67,
                0x41, 0x43, 0x47, 0x71,
                0x49, 0x51, 0x57, 0x73 };
        ConverterUtils.convert_XYZA_bytes_to_AXYZ_bytes(pixels, 0, pixels, 0, 5);

        assertEquals(0x59, pixels[0]);
        assertEquals(0x03, pixels[1]);
        assertEquals(0x05, pixels[2]);
        assertEquals(0x07, pixels[3]);

        assertEquals(0x61, pixels[4]);
        assertEquals(0x13, pixels[5]);
        assertEquals(0x17, pixels[6]);
        assertEquals(0x19, pixels[7]);

        assertEquals(0x67, pixels[8]);
        assertEquals(0x29, pixels[9]);
        assertEquals(0x31, pixels[10]);
        assertEquals(0x33, pixels[11]);

        assertEquals(0x71, pixels[12]);
        assertEquals(0x41, pixels[13]);
        assertEquals(0x43, pixels[14]);
        assertEquals(0x47, pixels[15]);

        assertEquals(0x73, pixels[16]);
        assertEquals(0x49, pixels[17]);
        assertEquals(0x51, pixels[18]);
        assertEquals(0x57, pixels[19]);
    }

    public void test_convert_XYZA_bytes_to_AZYX_bytes() {
        byte[] pixels = new byte[] {
                0x03, 0x05, 0x07, 0x59,
                0x13, 0x17, 0x19, 0x61,
                0x29, 0x31, 0x33, 0x67,
                0x41, 0x43, 0x47, 0x71,
                0x49, 0x51, 0x57, 0x73 };
        ConverterUtils.convert_XYZA_bytes_to_AZYX_bytes(pixels, 0, pixels, 0, 5);

        assertEquals(0x59, pixels[0]);
        assertEquals(0x07, pixels[1]);
        assertEquals(0x05, pixels[2]);
        assertEquals(0x03, pixels[3]);

        assertEquals(0x61, pixels[4]);
        assertEquals(0x19, pixels[5]);
        assertEquals(0x17, pixels[6]);
        assertEquals(0x13, pixels[7]);

        assertEquals(0x67, pixels[8]);
        assertEquals(0x33, pixels[9]);
        assertEquals(0x31, pixels[10]);
        assertEquals(0x29, pixels[11]);

        assertEquals(0x71, pixels[12]);
        assertEquals(0x47, pixels[13]);
        assertEquals(0x43, pixels[14]);
        assertEquals(0x41, pixels[15]);

        assertEquals(0x73, pixels[16]);
        assertEquals(0x57, pixels[17]);
        assertEquals(0x51, pixels[18]);
        assertEquals(0x49, pixels[19]);
    }

    public void test_convert_XYZA_bytes_to_AZYX_ints() {
        byte[] pixels = new byte[] {
                0x03, 0x05, 0x07, 0x59,
                0x13, 0x17, 0x19, 0x61,
                0x29, 0x31, 0x33, 0x67,
                0x41, 0x43, 0x47, 0x71,
                0x49, 0x51, 0x57, 0x73 };
        int[] dest = new int[5];
        ConverterUtils.convert_XYZA_bytes_to_AZYX_ints(dest, 0, pixels, 0, 5);

        assertEquals(Integer.toUnsignedString(dest[0], 16),0x59070503, dest[0]);
        assertEquals(Integer.toUnsignedString(dest[1], 16),0x61191713, dest[1]);
        assertEquals(Integer.toUnsignedString(dest[2], 16),0x67333129, dest[2]);
        assertEquals(Integer.toUnsignedString(dest[3], 16),0x71474341, dest[3]);
        assertEquals(Integer.toUnsignedString(dest[4], 16),0x73575149, dest[4]);
    }

    public void test_convert_AXYZ_ints_to_XYZ_bytes() {
        int[] pixels = new int[] {0xE3030507, 0xC3131719, 0x89293137, 0x41414347, 0x09475157 };
        byte[] dest = new byte[3 * 5];

        ConverterUtils.convert_AXYZ_ints_to_XYZ_bytes(dest, 0, pixels, 0, 5);

        assertEquals(0x02, dest[0]);
        assertEquals(0x04, dest[1]);
        assertEquals(0x06, dest[2]);

        assertEquals(0x0E, dest[3]);
        assertEquals(0x11, dest[4]);
        assertEquals(0x13, dest[5]);

        assertEquals(0x15, dest[6]);
        assertEquals(0x1A, dest[7]);
        assertEquals(0x1D, dest[8]);

        assertEquals(0x10, dest[9]);
        assertEquals(0x11, dest[10]);
        assertEquals(0x12, dest[11]);

        assertEquals(0x02, dest[12]);
        assertEquals(0x02, dest[13]);
        assertEquals(0x03, dest[14]);
    }

    public void test_convert_AXYZ_bytes_to_XYZ_bytes() {
        byte[] pixels = new byte[] {
                -1, 0x55, 0x62, 0x08,
                0, 0x15, 0x67, 0x42,
                (byte) 0xE3, 0x03, 0x05, 0x07,
                (byte) 0xC3, 0x13, 0x17, 0x19,
                (byte) 0x89, 0x29, 0x31, 0x37,
                0x41, 0x41, 0x43, 0x47,
                0x09, 0x47, 0x51, 0x57 };

        ConverterUtils.convert_AXYZ_bytes_to_XYZ_bytes(pixels, 0, pixels, 0, 7);

        assertEquals(0x55, pixels[0]);
        assertEquals(0x62, pixels[1]);
        assertEquals(0x08, pixels[2]);

        assertEquals(0, pixels[3]);
        assertEquals(0, pixels[4]);
        assertEquals(0, pixels[5]);

        assertEquals(0x02, pixels[6]);
        assertEquals(0x04, pixels[7]);
        assertEquals(0x06, pixels[8]);

        assertEquals(0x0E, pixels[9]);
        assertEquals(0x11, pixels[10]);
        assertEquals(0x13, pixels[11]);

        assertEquals(0x16, pixels[12]);
        assertEquals(0x1A, pixels[13]);
        assertEquals(0x1D, pixels[14]);

        assertEquals(0x10, pixels[15]);
        assertEquals(0x11, pixels[16]);
        assertEquals(0x12, pixels[17]);

        assertEquals(0x02, pixels[18]);
        assertEquals(0x02, pixels[19]);
        assertEquals(0x03, pixels[20]);
    }

    public void test_convert_AXYZ_ints_to_ZYX_bytes() {
        int[] pixels = new int[] {0xE3030507, 0xC3131719, 0x89293137, 0x41414347, 0x09475157 };
        byte[] dest = new byte[3 * 5];

        ConverterUtils.convert_AXYZ_ints_to_ZYX_bytes(dest, 0, pixels, 0, 5);

        assertEquals(0x06, dest[0]);
        assertEquals(0x04, dest[1]);
        assertEquals(0x02, dest[2]);

        assertEquals(0x13, dest[3]);
        assertEquals(0x11, dest[4]);
        assertEquals(0x0E, dest[5]);

        assertEquals(0x1D, dest[6]);
        assertEquals(0x1A, dest[7]);
        assertEquals(0x15, dest[8]);

        assertEquals(0x12, dest[9]);
        assertEquals(0x11, dest[10]);
        assertEquals(0x10, dest[11]);

        assertEquals(0x03, dest[12]);
        assertEquals(0x02, dest[13]);
        assertEquals(0x02, dest[14]);
    }

    public void test_convert_AXYZ_bytes_to_ZYX_bytes() {
        byte[] pixels = new byte[] {
                -1, 0x55, 0x62, 0x08,
                0, 0x15, 0x67, 0x42,
                (byte) 0xE3, 0x03, 0x05, 0x07,
                (byte) 0xC3, 0x13, 0x17, 0x19,
                (byte) 0x89, 0x29, 0x31, 0x37,
                0x41, 0x41, 0x43, 0x47,
                0x09, 0x47, 0x51, 0x57 };

        ConverterUtils.convert_AXYZ_bytes_to_ZYX_bytes(pixels, 0, pixels, 0, 7);

        assertEquals(0x08, pixels[0]);
        assertEquals(0x62, pixels[1]);
        assertEquals(0x55, pixels[2]);

        assertEquals(0x00, pixels[3]);
        assertEquals(0x00, pixels[4]);
        assertEquals(0x00, pixels[5]);

        assertEquals(0x06, pixels[6]);
        assertEquals(0x04, pixels[7]);
        assertEquals(0x02, pixels[8]);

        assertEquals(0x13, pixels[9]);
        assertEquals(0x11, pixels[10]);
        assertEquals(0x0E, pixels[11]);

        assertEquals(0x1D, pixels[12]);
        assertEquals(0x1A, pixels[13]);
        assertEquals(0x16, pixels[14]);

        assertEquals(0x12, pixels[15]);
        assertEquals(0x11, pixels[16]);
        assertEquals(0x10, pixels[17]);

        assertEquals(0x03, pixels[18]);
        assertEquals(0x02, pixels[19]);
        assertEquals(0x02, pixels[20]);
    }

    public void test_convert_XYZA_bytes_to_XYZ_bytes() {
        byte[] pixels = new byte[] {0x03, 0x05, 0x07, (byte) 0xE3,
                0x13, 0x17, 0x19, (byte) 0xC3,
                0x29, 0x31, 0x37, (byte) 0x89,
                0x41, 0x43, 0x47, 0x41,
                0x47, 0x51, 0x57, 0x09 };

        ConverterUtils.convert_XYZA_bytes_to_XYZ_bytes(pixels, 0, pixels, 0, 5);

        assertEquals(0x02, pixels[0]);
        assertEquals(0x04, pixels[1]);
        assertEquals(0x06, pixels[2]);

        assertEquals(0x0E, pixels[3]);
        assertEquals(0x11, pixels[4]);
        assertEquals(0x13, pixels[5]);

        assertEquals(0x15, pixels[6]);
        assertEquals(0x1A, pixels[7]);
        assertEquals(0x1D, pixels[8]);

        assertEquals(0x10, pixels[9]);
        assertEquals(0x11, pixels[10]);
        assertEquals(0x12, pixels[11]);

        assertEquals(0x02, pixels[12]);
        assertEquals(0x02, pixels[13]);
        assertEquals(0x03, pixels[14]);
    }

    public void test_convert_XYZA_bytes_to_ZYX_bytes() {
        byte[] pixels = new byte[] {0x03, 0x05, 0x07, (byte) 0xE3,
                0x13, 0x17, 0x19, (byte) 0xC3,
                0x29, 0x31, 0x37, (byte) 0x89,
                0x41, 0x43, 0x47, 0x41,
                0x47, 0x51, 0x57, 0x09 };

        ConverterUtils.convert_XYZA_bytes_to_ZYX_bytes(pixels, 0, pixels, 0, 5);

        assertEquals(0x06, pixels[0]);
        assertEquals(0x04, pixels[1]);
        assertEquals(0x02, pixels[2]);

        assertEquals(0x13, pixels[3]);
        assertEquals(0x11, pixels[4]);
        assertEquals(0x0E, pixels[5]);

        assertEquals(0x1D, pixels[6]);
        assertEquals(0x1A, pixels[7]);
        assertEquals(0x15, pixels[8]);

        assertEquals(0x12, pixels[9]);
        assertEquals(0x11, pixels[10]);
        assertEquals(0x10, pixels[11]);

        assertEquals(0x03, pixels[12]);
        assertEquals(0x02, pixels[13]);
        assertEquals(0x02, pixels[14]);
    }

    public void test_convert_AXYZ_ints_to_XYZ_ints() {
        int[] pixels = new int[] { 0xE3030507,
                            0xC3131719,
                            0x89293137,
                            0x41414347,
                            0x09475157 };

        ConverterUtils.convert_AXYZ_ints_to_XYZ_ints(pixels, 0, pixels, 0, 5);

        assertEquals(Integer.toUnsignedString(pixels[0], 16),0x020406, pixels[0]);
        assertEquals(Integer.toUnsignedString(pixels[1], 16),0x0E1113, pixels[1]);
        assertEquals(Integer.toUnsignedString(pixels[2], 16),0x151A1D, pixels[2]);
        assertEquals(Integer.toUnsignedString(pixels[3], 16),0x101112, pixels[3]);
        assertEquals(Integer.toUnsignedString(pixels[4], 16),0x020203, pixels[4]);
    }

    public void test_convert_AXYZ_ints_to_ZYX_ints() {
        int[] pixels = new int[] { 0xE3030507,
                0xC3131719,
                0x89293137,
                0x41414347,
                0x09475157 };

        ConverterUtils.convert_AXYZ_ints_to_ZYX_ints(pixels, 0, pixels, 0, 5);

        assertEquals(Integer.toUnsignedString(pixels[0], 16),0x060402, pixels[0]);
        assertEquals(Integer.toUnsignedString(pixels[1], 16),0x13110E, pixels[1]);
        assertEquals(Integer.toUnsignedString(pixels[2], 16),0x1D1A15, pixels[2]);
        assertEquals(Integer.toUnsignedString(pixels[3], 16),0x121110, pixels[3]);
        assertEquals(Integer.toUnsignedString(pixels[4], 16),0x030202, pixels[4]);
    }

    public void test_convert_AXYZ_bytes_to_XYZ_ints() {
        byte[] pixels = new byte[] {
                -1, 0x55, 0x62, 0x08,
                0, 0x15, 0x67, 0x42,
                (byte) 0xE3,0x03, 0x05, 0x07,
                (byte) 0xC3, 0x13, 0x17, 0x19,
                (byte) 0x89, 0x29, 0x31, 0x37,
                0x41, 0x41, 0x43, 0x47,
                0x09, 0x47, 0x51, 0x57 };

        int[] dest = new int[7];
        ConverterUtils.convert_AXYZ_bytes_to_XYZ_ints(dest, 0, pixels, 0, 7);

        assertEquals(Integer.toUnsignedString(dest[0], 16),0x556208, dest[0]);
        assertEquals(Integer.toUnsignedString(dest[1], 16),0, dest[1]);
        assertEquals(Integer.toUnsignedString(dest[2], 16),0x020406, dest[2]);
        assertEquals(Integer.toUnsignedString(dest[3], 16),0x0E1113, dest[3]);
        assertEquals(Integer.toUnsignedString(dest[4], 16),0x161a1d, dest[4]);
        assertEquals(Integer.toUnsignedString(dest[5], 16),0x101112, dest[5]);
        assertEquals(Integer.toUnsignedString(dest[6], 16),0x020203, dest[6]);
    }

    public void test_convert_AXYZ_bytes_to_ZYX_ints() {
        byte[] pixels = new byte[] {
                -1, 0x55, 0x62, 0x08,
                0, 0x15, 0x67, 0x42,
                (byte) 0xE3,0x03, 0x05, 0x07,
                (byte) 0xC3, 0x13, 0x17, 0x19,
                (byte) 0x89, 0x29, 0x31, 0x37,
                0x41, 0x41, 0x43, 0x47,
                0x09, 0x47, 0x51, 0x57 };

        int[] dest = new int[7];
        ConverterUtils.convert_AXYZ_bytes_to_ZYX_ints(dest, 0, pixels, 0, 7);

        assertEquals(Integer.toUnsignedString(dest[0], 16),0x086255, dest[0]);
        assertEquals(Integer.toUnsignedString(dest[1], 16),0x000000, dest[1]);
        assertEquals(Integer.toUnsignedString(dest[2], 16),0x060402, dest[2]);
        assertEquals(Integer.toUnsignedString(dest[3], 16),0x13110E, dest[3]);
        assertEquals(Integer.toUnsignedString(dest[4], 16),0x1D1A16, dest[4]);
        assertEquals(Integer.toUnsignedString(dest[5], 16),0x121110, dest[5]);
        assertEquals(Integer.toUnsignedString(dest[6], 16),0x030202, dest[6]);
    }

    public void test_convert_XYZA_bytes_to_XYZ_ints() {
        byte[] pixels = new byte[] {0x03, 0x05, 0x07, (byte) 0xE3,
                0x13, 0x17, 0x19, (byte) 0xC3,
                0x29, 0x31, 0x37, (byte) 0x89,
                0x41, 0x43, 0x47, 0x41,
                0x47, 0x51, 0x57, 0x09 };

        int[] dest = new int[5];
        ConverterUtils.convert_XYZA_bytes_to_XYZ_ints(dest, 0, pixels, 0, 5);

        assertEquals(Integer.toUnsignedString(dest[0], 16),0x020406, dest[0]);
        assertEquals(Integer.toUnsignedString(dest[1], 16),0x0E1113, dest[1]);
        assertEquals(Integer.toUnsignedString(dest[2], 16),0x151A1D, dest[2]);
        assertEquals(Integer.toUnsignedString(dest[3], 16),0x101112, dest[3]);
        assertEquals(Integer.toUnsignedString(dest[4], 16),0x020203, dest[4]);
    }

    public void test_convert_XYZA_bytes_to_ZYX_ints() {
        byte[] pixels = new byte[] {0x03, 0x05, 0x07, (byte) 0xE3,
                0x13, 0x17, 0x19, (byte) 0xC3,
                0x29, 0x31, 0x37, (byte) 0x89,
                0x41, 0x43, 0x47, 0x41,
                0x47, 0x51, 0x57, 0x09 };

        int[] dest = new int[5];
        ConverterUtils.convert_XYZA_bytes_to_ZYX_ints(dest, 0, pixels, 0, 5);

        assertEquals(Integer.toUnsignedString(dest[0], 16),0x060402, dest[0]);
        assertEquals(Integer.toUnsignedString(dest[1], 16),0x13110E, dest[1]);
        assertEquals(Integer.toUnsignedString(dest[2], 16),0x1D1A15, dest[2]);
        assertEquals(Integer.toUnsignedString(dest[3], 16),0x121110, dest[3]);
        assertEquals(Integer.toUnsignedString(dest[4], 16),0x030202, dest[4]);
    }

    public void test_convert_AXYZPre_bytes_to_ZYX_bytes() {
        byte[] pixels = new byte[] {
                (byte) 0xE3, 0x06, 0x04, 0x02,
                (byte) 0xC3, 0x13, 0x11, 0x0E,
                (byte) 0x89, 0x1D, 0x1A, 0x15,
                (byte) 0x41, 0x12, 0x11, 0x10,
                (byte) 0x09, 0x03, 0x02, 0x02
        };
        ConverterUtils.convert_AXYZPre_bytes_to_ZYX_bytes(pixels, 0, pixels, 0, 5);

        assertEquals(0x02, pixels[0]);
        assertEquals(0x04, pixels[1]);
        assertEquals(0x06, pixels[2]);

        assertEquals(0x0E, pixels[3]);
        assertEquals(0x11, pixels[4]);
        assertEquals(0x13, pixels[5]);

        assertEquals(0x15, pixels[6]);
        assertEquals(0x1A, pixels[7]);
        assertEquals(0x1D, pixels[8]);

        assertEquals(0x10, pixels[9]);
        assertEquals(0x11, pixels[10]);
        assertEquals(0x12, pixels[11]);

        assertEquals(0x02, pixels[12]);
        assertEquals(0x02, pixels[13]);
        assertEquals(0x03, pixels[14]);
    }

    public void test_convert_AXYZPre_bytes_to_XYZ_bytes() {
        byte[] pixels = new byte[] {
                (byte) 0xE3, 0x06, 0x04, 0x02,
                (byte) 0xC3, 0x13, 0x11, 0x0E,
                (byte) 0x89, 0x1D, 0x1A, 0x15,
                (byte) 0x41, 0x12, 0x11, 0x10,
                (byte) 0x09, 0x03, 0x02, 0x02
        };
        ConverterUtils.convert_AXYZPre_bytes_to_XYZ_bytes(pixels, 0, pixels, 0, 5);

        assertEquals(0x06, pixels[0]);
        assertEquals(0x04, pixels[1]);
        assertEquals(0x02, pixels[2]);

        assertEquals(0x13, pixels[3]);
        assertEquals(0x11, pixels[4]);
        assertEquals(0x0E, pixels[5]);

        assertEquals(0x1D, pixels[6]);
        assertEquals(0x1A, pixels[7]);
        assertEquals(0x15, pixels[8]);

        assertEquals(0x12, pixels[9]);
        assertEquals(0x11, pixels[10]);
        assertEquals(0x10, pixels[11]);

        assertEquals(0x03, pixels[12]);
        assertEquals(0x02, pixels[13]);
        assertEquals(0x02, pixels[14]);
    }

    public void test_convert_AXYZPre_ints_to_AZYX_bytes() {
        int[] pixels = new int[] { 0xE3060402, 0xC313110E, 0x891D1A15,
                0x41121110, 0x09030202 };
        byte[] dest = new byte[5 * 4];
        ConverterUtils.convert_AXYZPre_ints_to_AZYX_bytes(dest, 0, pixels, 0, 5);

        assertEquals((byte) 0xE3, dest[0]);
        assertEquals(0x02, dest[1]);
        assertEquals(0x04, dest[2]);
        assertEquals(0x06, dest[3]);

        assertEquals((byte) 0xC3, dest[4]);
        assertEquals(0x12, dest[5]);
        assertEquals(0x16, dest[6]);
        assertEquals(0x18, dest[7]);

        assertEquals((byte) 0x89, dest[8]);
        assertEquals(0x27, dest[9]);
        assertEquals(0x30, dest[10]);
        assertEquals(0x36, dest[11]);

        assertEquals(0x41, dest[12]);
        assertEquals(0x3F, dest[13]);
        assertEquals(0x42, dest[14]);
        assertEquals(0x46, dest[15]);

        assertEquals(0x09, dest[16]);
        assertEquals(0x38, dest[17]);
        assertEquals(0x38, dest[18]);
        assertEquals(0x55, dest[19]);
    }

    public void test_convert_AXYZPre_ints_to_AXYZ_bytes() {
        int[] pixels = new int[] { 0xE3060402, 0xC313110E, 0x891D1A15,
                0x41121110, 0x09030202 };
        byte[] dest = new byte[5 * 4];
        ConverterUtils.convert_AXYZPre_ints_to_AXYZ_bytes(dest, 0, pixels, 0, 5);

        assertEquals((byte) 0xE3, dest[0]);
        assertEquals(0x06, dest[1]);
        assertEquals(0x04, dest[2]);
        assertEquals(0x02, dest[3]);

        assertEquals((byte) 0xC3, dest[4]);
        assertEquals(0x18, dest[5]);
        assertEquals(0x16, dest[6]);
        assertEquals(0x12, dest[7]);

        assertEquals((byte) 0x89, dest[8]);
        assertEquals(0x36, dest[9]);
        assertEquals(0x30, dest[10]);
        assertEquals(0x27, dest[11]);

        assertEquals(0x41, dest[12]);
        assertEquals(0x46, dest[13]);
        assertEquals(0x42, dest[14]);
        assertEquals(0x3F, dest[15]);

        assertEquals(0x09, dest[16]);
        assertEquals(0x55, dest[17]);
        assertEquals(0x38, dest[18]);
        assertEquals(0x38, dest[19]);
    }

    public void test_convert_AXYZPre_ints_to_ZYXA_bytes() {
        int[] pixels = new int[] { 0xE3060402, 0xC313110E, 0x891D1A15,
                0x41121110, 0x09030202 };
        byte[] dest = new byte[5 * 4];
        ConverterUtils.convert_AXYZPre_ints_to_ZYXA_bytes(dest, 0, pixels, 0, 5);

        assertEquals(0x02, dest[0]);
        assertEquals(0x04, dest[1]);
        assertEquals(0x06, dest[2]);
        assertEquals((byte) 0xE3, dest[3]);

        assertEquals(0x12, dest[4]);
        assertEquals(0x16, dest[5]);
        assertEquals(0x18, dest[6]);
        assertEquals((byte) 0xC3, dest[7]);

        assertEquals(0x27, dest[8]);
        assertEquals(0x30, dest[9]);
        assertEquals(0x36, dest[10]);
        assertEquals((byte) 0x89, dest[11]);

        assertEquals(0x3F, dest[12]);
        assertEquals(0x42, dest[13]);
        assertEquals(0x46, dest[14]);
        assertEquals(0x41, dest[15]);

        assertEquals(0x38, dest[16]);
        assertEquals(0x38, dest[17]);
        assertEquals(0x55, dest[18]);
        assertEquals(0x09, dest[19]);
    }

    public void test_convert_AXYZ_ints_to_AZYXPre_bytes() {
        int[] pixels = new int[] { 0xE3060402, 0xC3181612, 0x89363027, 0x4146423F, 0x09553838};
        byte[] dest = new byte[5 * 4];
        ConverterUtils.convert_AXYZ_ints_to_AZYXPre_bytes(dest, 0, pixels, 0, 5);

        assertEquals((byte) 0xE3, dest[0]);
        assertEquals(0x01, dest[1]);
        assertEquals(0x03, dest[2]);
        assertEquals(0x05, dest[3]);

        assertEquals((byte) 0xC3, dest[4]);
        assertEquals(0x0D, dest[5]);
        assertEquals(0x10, dest[6]);
        assertEquals(0x12, dest[7]);

        assertEquals((byte)  0x89, dest[8]);
        assertEquals(0x14, dest[9]);
        assertEquals(0x19, dest[10]);
        assertEquals(0x1C, dest[11]);

        assertEquals(0x41, dest[12]);
        assertEquals(0x0F, dest[13]);
        assertEquals(0x10, dest[14]);
        assertEquals(0x11, dest[15]);

        assertEquals(0x09, dest[16]);
        assertEquals(0x01, dest[17]);
        assertEquals(0x01, dest[18]);
        assertEquals(0x02, dest[19]);
    }

    public void test_convert_AXYZ_ints_to_AXYZPre_bytes() {
        int[] pixels = new int[] { 0xE3060402, 0xC3181612, 0x89363027, 0x4146423F, 0x09553838};
        byte[] dest = new byte[5 * 4];
        ConverterUtils.convert_AXYZ_ints_to_AXYZPre_bytes(dest, 0, pixels, 0, 5);

        assertEquals((byte) 0xE3, dest[0]);
        assertEquals(0x05, dest[1]);
        assertEquals(0x03, dest[2]);
        assertEquals(0x01, dest[3]);

        assertEquals((byte) 0xC3, dest[4]);
        assertEquals(0x12, dest[5]);
        assertEquals(0x10, dest[6]);
        assertEquals(0x0D, dest[7]);

        assertEquals((byte)  0x89, dest[8]);
        assertEquals(0x1C, dest[9]);
        assertEquals(0x19, dest[10]);
        assertEquals(0x14, dest[11]);

        assertEquals(0x41, dest[12]);
        assertEquals(0x11, dest[13]);
        assertEquals(0x10, dest[14]);
        assertEquals(0x0F, dest[15]);

        assertEquals(0x09, dest[16]);
        assertEquals(0x02, dest[17]);
        assertEquals(0x01, dest[18]);
        assertEquals(0x01, dest[19]);
    }

    public void test_convert_AXYZPre_bytes_to_AXYZ_ints() {
        byte[] pixels = new byte[] {
                (byte) 0xE3, 0x05, 0x03, 0x01,
                (byte) 0xC3, 0x12, 0x10, 0x0D,
                (byte) 0x89, 0x1C, 0x19, 0x14,
                0x41, 0x11, 0x10, 0x0F,
                0x09, 0x02, 0x01, 0x01
        };

        int[] dest = new int[5];
        ConverterUtils.convert_AXYZPre_bytes_to_AXYZ_ints(dest, 0, pixels, 0, 5);

        assertEquals(Integer.toUnsignedString(dest[0], 16), 0xe3050301, dest[0]);
        assertEquals(Integer.toUnsignedString(dest[1], 16), 0xc3171511, dest[1]);
        assertEquals(Integer.toUnsignedString(dest[2], 16), 0x89342e25, dest[2]);
        assertEquals(Integer.toUnsignedString(dest[3], 16), 0x41423f3b, dest[3]);
        assertEquals(Integer.toUnsignedString(dest[4], 16), 0x09381c1c, dest[4]);
    }

    public void test_convert_AXYZPre_bytes_to_AZYX_ints() {
        byte[] pixels = new byte[] {
                (byte) 0xE3, 0x05, 0x03, 0x01,
                (byte) 0xC3, 0x12, 0x10, 0x0D,
                (byte) 0x89, 0x1C, 0x19, 0x14,
                0x41, 0x11, 0x10, 0x0F,
                0x09, 0x02, 0x01, 0x01
        };

        int[] dest = new int[5];
        ConverterUtils.convert_AXYZPre_bytes_to_AZYX_ints(dest, 0, pixels, 0, 5);

        assertEquals(Integer.toUnsignedString(dest[0], 16), 0xe3010305, dest[0]);
        assertEquals(Integer.toUnsignedString(dest[1], 16), 0xc3111517, dest[1]);
        assertEquals(Integer.toUnsignedString(dest[2], 16), 0x89252e34, dest[2]);
        assertEquals(Integer.toUnsignedString(dest[3], 16), 0x413b3f42, dest[3]);
        assertEquals(Integer.toUnsignedString(dest[4], 16), 0x091c1c38, dest[4]);
    }

    public void test_convert_AXYZ_bytes_to_AXYZPre_ints() {
        byte[] pixels = new byte[] {
                -1, 0x55, 0x62, 0x08,
                0, 0x15, 0x67, 0x42,
                (byte) 0xE3, 0x06, 0x04, 0x02,
                (byte) 0xC3, 0x13, 0x11, 0x0E,
                (byte) 0x89, 0x1D, 0x1A, 0x15,
                (byte) 0x41, 0x12, 0x11, 0x10,
                (byte) 0x09, 0x03, 0x02, 0x02
        };
        int[] dest = new int[7];
        ConverterUtils.convert_AXYZ_bytes_to_AXYZPre_ints(dest, 0, pixels, 0, 7);

        assertEquals(Integer.toUnsignedString(dest[0], 16), 0xff556208, dest[0]);
        assertEquals(Integer.toUnsignedString(dest[1], 16), 0x00000000, dest[1]);
        assertEquals(Integer.toUnsignedString(dest[2], 16), 0xE3050301, dest[2]);
        assertEquals(Integer.toUnsignedString(dest[3], 16), 0xC30E0D0A, dest[3]);
        assertEquals(Integer.toUnsignedString(dest[4], 16), 0x890F0D0B, dest[4]);
        assertEquals(Integer.toUnsignedString(dest[5], 16), 0x41040404, dest[5]);
        assertEquals(Integer.toUnsignedString(dest[6], 16), 0x09000000, dest[6]);
    }

    public void test_convert_AXYZ_bytes_to_AZYXPre_ints() {
        byte[] pixels = new byte[] {
                -1, 0x55, 0x62, 0x08,
                0, 0x15, 0x67, 0x42,
                (byte) 0xE3, 0x06, 0x04, 0x02,
                (byte) 0xC3, 0x13, 0x11, 0x0E,
                (byte) 0x89, 0x1D, 0x1A, 0x15,
                (byte) 0x41, 0x12, 0x11, 0x10,
                (byte) 0x09, 0x03, 0x02, 0x02
        };
        int[] dest = new int[7];
        ConverterUtils.convert_AXYZ_bytes_to_AZYXPre_ints(dest, 0, pixels, 0, 7);

        assertEquals(Integer.toUnsignedString(dest[0], 16), 0xff086255, dest[0]);
        assertEquals(Integer.toUnsignedString(dest[1], 16), 0x00000000, dest[1]);
        assertEquals(Integer.toUnsignedString(dest[2], 16), 0xE3010305, dest[2]);
        assertEquals(Integer.toUnsignedString(dest[3], 16), 0xC30A0D0E, dest[3]);
        assertEquals(Integer.toUnsignedString(dest[4], 16), 0x890B0D0F, dest[4]);
        assertEquals(Integer.toUnsignedString(dest[5], 16), 0x41040404, dest[5]);
        assertEquals(Integer.toUnsignedString(dest[6], 16), 0x09000000, dest[6]);
    }

    public void test_convert_XYZA_bytes_to_AZYXPre_ints() {
        byte[] pixels = new byte[] {
                0x06, 0x04, 0x02, (byte) 0xE3,
                0x13, 0x11, 0x0E, (byte) 0xC3,
                0x1D, 0x1A, 0x15, (byte) 0x89,
                0x12, 0x11, 0x10, (byte) 0x41,
                0x03, 0x02, 0x02, (byte) 0x09
        };
        int[] dest = new int[5];
        ConverterUtils.convert_XYZA_bytes_to_AZYXPre_ints(dest, 0, pixels, 0, 5);

        assertEquals(Integer.toUnsignedString(dest[0], 16), 0xE3010305, dest[0]);
        assertEquals(Integer.toUnsignedString(dest[1], 16), 0xC30A0C0E, dest[1]);
        assertEquals(Integer.toUnsignedString(dest[2], 16), 0x890B0D0F, dest[2]);
        assertEquals(Integer.toUnsignedString(dest[3], 16), 0x41040404, dest[3]);
        assertEquals(Integer.toUnsignedString(dest[4], 16), 0x09000000, dest[4]);
    }

    public void test_convert_AXYZPre_ints_to_AXYZ_ints() {
        int[] pixels = new int[] {
                0xE3050301,
                0xC312100D,
                0x891C1914,
                0x4111100F,
                0x09020101
        };

        ConverterUtils.convert_AXYZPre_ints_to_AXYZ_ints(pixels, 0, pixels, 0, 5);

        assertEquals(Integer.toUnsignedString(pixels[0], 16), 0xe3050301, pixels[0]);
        assertEquals(Integer.toUnsignedString(pixels[1], 16), 0xc3171511, pixels[1]);
        assertEquals(Integer.toUnsignedString(pixels[2], 16), 0x89342e25, pixels[2]);
        assertEquals(Integer.toUnsignedString(pixels[3], 16), 0x41423f3b, pixels[3]);
        assertEquals(Integer.toUnsignedString(pixels[4], 16), 0x09381c1c, pixels[4]);
    }

    public void test_convert_AXYZ_ints_to_AXYZPre_ints() {
        int[] pixels = new int[] {
                0xe3050301,
                0xc3171511,
                0x89342e25,
                0x41423f3b,
                0x09381c1c
        };

        ConverterUtils.convert_AXYZ_ints_to_AXYZPre_ints(pixels, 0, pixels, 0, 5);

        assertEquals(Integer.toUnsignedString(pixels[0], 16), 0xE3040200, pixels[0]);
        assertEquals(Integer.toUnsignedString(pixels[1], 16), 0xC3110F0C, pixels[1]);
        assertEquals(Integer.toUnsignedString(pixels[2], 16), 0x891B1813, pixels[2]);
        assertEquals(Integer.toUnsignedString(pixels[3], 16), 0x41100F0E, pixels[3]);
        assertEquals(Integer.toUnsignedString(pixels[4], 16), 0x09010000, pixels[4]);
    }

    public void test_convert_AXYZPre_ints_to_ZYX_ints() {
        int[] pixels = new int[] {
                0xe3050301,
                0xc3171511,
                0x89342e25,
                0x41423f3b,
                0x09381c1c
        };

        ConverterUtils.convert_AXYZPre_ints_to_ZYX_ints(pixels, 0, pixels, 0, 5);

        assertEquals(Integer.toUnsignedString(pixels[0], 16), 0x010305, pixels[0]);
        assertEquals(Integer.toUnsignedString(pixels[1], 16), 0x111517, pixels[1]);
        assertEquals(Integer.toUnsignedString(pixels[2], 16), 0x252E34, pixels[2]);
        assertEquals(Integer.toUnsignedString(pixels[3], 16), 0x3B3F42, pixels[3]);
        assertEquals(Integer.toUnsignedString(pixels[4], 16), 0x1C1C38, pixels[4]);
    }

    public void test_convert_AXYZPre_ints_to_XYZ_ints() {
        int[] pixels = new int[] {
                0xe3050301,
                0xc3171511,
                0x89342e25,
                0x41423f3b,
                0x09381c1c
        };

        ConverterUtils.convert_AXYZPre_ints_to_XYZ_ints(pixels, 0, pixels, 0, 5);

        assertEquals(Integer.toUnsignedString(pixels[0], 16), 0x050301, pixels[0]);
        assertEquals(Integer.toUnsignedString(pixels[1], 16), 0x171511, pixels[1]);
        assertEquals(Integer.toUnsignedString(pixels[2], 16), 0x342E25, pixels[2]);
        assertEquals(Integer.toUnsignedString(pixels[3], 16), 0x423F3B, pixels[3]);
        assertEquals(Integer.toUnsignedString(pixels[4], 16), 0x381C1C, pixels[4]);
    }

    public void test_convert_AXYZPre_bytes_to_XYZ_ints() {
        byte[] pixels = new byte[] {
                (byte) 0xe3, 0x05, 0x03, 0x01,
                (byte) 0xc3, 0x17, 0x15, 0x11,
                (byte) 0x89, 0x34, 0x2e, 0x25,
                0x41, 0x42, 0x3f, 0x3b,
                0x09, 0x38, 0x1c, 0x1c
        };

        int[] dest = new int[5];
        ConverterUtils.convert_AXYZPre_bytes_to_XYZ_ints(dest, 0, pixels, 0, 5);

        assertEquals(Integer.toUnsignedString(dest[0], 16), 0x050301, dest[0]);
        assertEquals(Integer.toUnsignedString(dest[1], 16), 0x171511, dest[1]);
        assertEquals(Integer.toUnsignedString(dest[2], 16), 0x342E25, dest[2]);
        assertEquals(Integer.toUnsignedString(dest[3], 16), 0x423F3B, dest[3]);
        assertEquals(Integer.toUnsignedString(dest[4], 16), 0x381C1C, dest[4]);
    }

    public void test_convert_AXYZPre_bytes_to_ZYX_ints() {
        byte[] pixels = new byte[] {
                (byte) 0xe3, 0x05, 0x03, 0x01,
                (byte) 0xc3, 0x17, 0x15, 0x11,
                (byte) 0x89, 0x34, 0x2e, 0x25,
                0x41, 0x42, 0x3f, 0x3b,
                0x09, 0x38, 0x1c, 0x1c
        };

        int[] dest = new int[5];
        ConverterUtils.convert_AXYZPre_bytes_to_ZYX_ints(dest, 0, pixels, 0, 5);

        assertEquals(Integer.toUnsignedString(dest[0], 16), 0x010305, dest[0]);
        assertEquals(Integer.toUnsignedString(dest[1], 16), 0x111517, dest[1]);
        assertEquals(Integer.toUnsignedString(dest[2], 16), 0x252E34, dest[2]);
        assertEquals(Integer.toUnsignedString(dest[3], 16), 0x3B3F42, dest[3]);
        assertEquals(Integer.toUnsignedString(dest[4], 16), 0x1C1C38, dest[4]);
    }

    public void test_convert_AXYZPre_bytes_to_AXYZ_bytes() {
        byte[] pixels = new byte[] {
                (byte) 0xe3, 0x05, 0x03, 0x01,
                (byte) 0xc3, 0x17, 0x15, 0x11,
                (byte) 0x89, 0x34, 0x2e, 0x25,
                0x41, 0x42, 0x3f, 0x3b,
                0x09, 0x38, 0x1c, 0x1c
        };

        ConverterUtils.convert_AXYZPre_bytes_to_AXYZ_bytes(pixels, 0, pixels, 0, 5);

        assertEquals( (byte) 0xe3, pixels[0]);
        assertEquals( 0x05, pixels[1]);
        assertEquals( 0x03, pixels[2]);
        assertEquals( 0x01, pixels[3]);

        assertEquals( (byte) 0xc3, pixels[4]);
        assertEquals( 0x1E, pixels[5]);
        assertEquals( 0x1B, pixels[6]);
        assertEquals( 0x16, pixels[7]);

        assertEquals( (byte) 0x89, pixels[8]);
        assertEquals( 0x61, pixels[9]);
        assertEquals( 0x55, pixels[10]);
        assertEquals( 0x45, pixels[11]);

        assertEquals( 0x41, pixels[12]);
        assertEquals( (byte) 0xff, pixels[13]);
        assertEquals( (byte) 0xf8, pixels[14]);
        assertEquals( (byte) 0xe8, pixels[15]);

        assertEquals( 0x09, pixels[16]);
        assertEquals( (byte) 0xff, pixels[17]);
        assertEquals( (byte) 0xff, pixels[18]);
        assertEquals( (byte) 0xff, pixels[19]);
    }

    public void test_convert_AXYZPre_bytes_to_AZYX_bytes() {
        byte[] pixels = new byte[] {
                (byte) 0xe3, 0x05, 0x03, 0x01,
                (byte) 0xc3, 0x17, 0x15, 0x11,
                (byte) 0x89, 0x34, 0x2e, 0x25,
                0x41, 0x42, 0x3f, 0x3b,
                0x09, 0x38, 0x1c, 0x1c
        };

        ConverterUtils.convert_AXYZPre_bytes_to_AZYX_bytes(pixels, 0, pixels, 0, 5);

        assertEquals( (byte) 0xe3, pixels[0]);
        assertEquals( 0x01, pixels[1]);
        assertEquals( 0x03, pixels[2]);
        assertEquals( 0x05, pixels[3]);

        assertEquals( (byte) 0xc3, pixels[4]);
        assertEquals( 0x16, pixels[5]);
        assertEquals( 0x1B, pixels[6]);
        assertEquals( 0x1E, pixels[7]);

        assertEquals( (byte) 0x89, pixels[8]);
        assertEquals( 0x45, pixels[9]);
        assertEquals( 0x55, pixels[10]);
        assertEquals( 0x61, pixels[11]);

        assertEquals( 0x41, pixels[12]);
        assertEquals( (byte) 0xe8, pixels[13]);
        assertEquals( (byte) 0xf8, pixels[14]);
        assertEquals( (byte) 0xff, pixels[15]);

        assertEquals( 0x09, pixels[16]);
        assertEquals( (byte) 0xff, pixels[17]);
        assertEquals( (byte) 0xff, pixels[18]);
        assertEquals( (byte) 0xff, pixels[19]);
    }

    public void test_convert_AXYZPre_bytes_to_ZYXA_bytes() {
        byte[] pixels = new byte[] {
                (byte) 0xe3, 0x05, 0x03, 0x01,
                (byte) 0xc3, 0x17, 0x15, 0x11,
                (byte) 0x89, 0x34, 0x2e, 0x25,
                0x41, 0x42, 0x3f, 0x3b,
                0x09, 0x38, 0x1c, 0x1c
        };

        ConverterUtils.convert_AXYZPre_bytes_to_ZYXA_bytes(pixels, 0, pixels, 0, 5);

        assertEquals( 0x01, pixels[0]);
        assertEquals( 0x03, pixels[1]);
        assertEquals( 0x05, pixels[2]);
        assertEquals( (byte) 0xe3, pixels[3]);

        assertEquals( 0x16, pixels[4]);
        assertEquals( 0x1B, pixels[5]);
        assertEquals( 0x1E, pixels[6]);
        assertEquals( (byte) 0xc3, pixels[7]);

        assertEquals( 0x45, pixels[8]);
        assertEquals( 0x55, pixels[9]);
        assertEquals( 0x61, pixels[10]);
        assertEquals( (byte) 0x89, pixels[11]);

        assertEquals( (byte) 0xe8, pixels[12]);
        assertEquals( (byte) 0xf8, pixels[13]);
        assertEquals( (byte) 0xff, pixels[14]);
        assertEquals( 0x41, pixels[15]);

        assertEquals( (byte) 0xff, pixels[16]);
        assertEquals( (byte) 0xff, pixels[17]);
        assertEquals( (byte) 0xff, pixels[18]);
        assertEquals( 0x09, pixels[19]);
    }

    public void test_convert_AXYZPre_bytes_to_XYZA_bytes() {
        byte[] pixels = new byte[] {
                (byte) 0xe3, 0x05, 0x03, 0x01,
                (byte) 0xc3, 0x17, 0x15, 0x11,
                (byte) 0x89, 0x34, 0x2e, 0x25,
                0x41, 0x42, 0x3f, 0x3b,
                0x09, 0x38, 0x1c, 0x1c
        };

        ConverterUtils.convert_AXYZPre_bytes_to_XYZA_bytes(pixels, 0, pixels, 0, 5);

        assertEquals( 0x05, pixels[0]);
        assertEquals( 0x03, pixels[1]);
        assertEquals( 0x01, pixels[2]);
        assertEquals( (byte) 0xe3, pixels[3]);

        assertEquals( 0x1E, pixels[4]);
        assertEquals( 0x1B, pixels[5]);
        assertEquals( 0x16, pixels[6]);
        assertEquals( (byte) 0xc3, pixels[7]);

        assertEquals( 0x61, pixels[8]);
        assertEquals( 0x55, pixels[9]);
        assertEquals( 0x45, pixels[10]);
        assertEquals( (byte) 0x89, pixels[11]);

        assertEquals( (byte) 0xff, pixels[12]);
        assertEquals( (byte) 0xf8, pixels[13]);
        assertEquals( (byte) 0xe8, pixels[14]);
        assertEquals( 0x41, pixels[15]);

        assertEquals( (byte) 0xff, pixels[16]);
        assertEquals( (byte) 0xff, pixels[17]);
        assertEquals( (byte) 0xff, pixels[18]);
        assertEquals( 0x09, pixels[19]);
    }

    public void test_convert_AXYZ_bytes_to_AXYZPre_bytes() {
        byte[] pixels = new byte[] {
                -1, 0x55, 0x62, 0x08,
                0, 0x15, 0x67, 0x42,
                (byte) 0xe3, 0x05, 0x03, 0x01,
                (byte) 0xc3, 0x17, 0x15, 0x11,
                (byte) 0x89, 0x34, 0x2e, 0x25,
                0x41, 0x42, 0x3f, 0x3b,
                0x09, 0x38, 0x1c, 0x1c
        };

        ConverterUtils.convert_AXYZ_bytes_to_AXYZPre_bytes(pixels, 0, pixels, 0, 7);

        assertEquals( -1, pixels[0]);
        assertEquals( 0x55, pixels[1]);
        assertEquals( 0x62, pixels[2]);
        assertEquals( 0x08, pixels[3]);

        assertEquals( 0, pixels[4]);
        assertEquals( 0, pixels[5]);
        assertEquals( 0, pixels[6]);
        assertEquals( 0, pixels[7]);

        assertEquals( (byte) 0xe3, pixels[8]);
        assertEquals( 0x04, pixels[9]);
        assertEquals( 0x02, pixels[10]);
        assertEquals( 0x00, pixels[11]);

        assertEquals( (byte) 0xc3, pixels[12]);
        assertEquals( 0x11, pixels[13]);
        assertEquals( 0x10, pixels[14]);
        assertEquals( 0x0d, pixels[15]);

        assertEquals( (byte) 0x89, pixels[16]);
        assertEquals( 0x1B, pixels[17]);
        assertEquals( 0x18, pixels[18]);
        assertEquals( 0x13, pixels[19]);

        assertEquals( 0x41, pixels[20]);
        assertEquals( 0x10, pixels[21]);
        assertEquals( 0x10, pixels[22]);
        assertEquals( 0x0f, pixels[23]);

        assertEquals( 0x09, pixels[24]);
        assertEquals( 0x01, pixels[25]);
        assertEquals( 0x00, pixels[26]);
        assertEquals( 0x00, pixels[27]);
    }

    public void test_convert_AXYZ_bytes_to_AZYXPre_bytes() {
        byte[] pixels = new byte[] {
                -1, 0x55, 0x62, 0x08,
                0, 0x15, 0x67, 0x42,
                (byte) 0xe3, 0x05, 0x03, 0x01,
                (byte) 0xc3, 0x17, 0x15, 0x11,
                (byte) 0x89, 0x34, 0x2e, 0x25,
                0x41, 0x42, 0x3f, 0x3b,
                0x09, 0x38, 0x1c, 0x1c
        };

        ConverterUtils.convert_AXYZ_bytes_to_AZYXPre_bytes(pixels, 0, pixels, 0, 7);

        assertEquals( -1, pixels[0]);
        assertEquals( 0x08, pixels[1]);
        assertEquals( 0x62, pixels[2]);
        assertEquals( 0x55, pixels[3]);

        assertEquals( 0, pixels[4]);
        assertEquals( 0, pixels[5]);
        assertEquals( 0, pixels[6]);
        assertEquals( 0, pixels[7]);

        assertEquals( (byte) 0xe3, pixels[8]);
        assertEquals( 0x00, pixels[9]);
        assertEquals( 0x02, pixels[10]);
        assertEquals( 0x04, pixels[11]);

        assertEquals( (byte) 0xc3, pixels[12]);
        assertEquals( 0x0d, pixels[13]);
        assertEquals( 0x10, pixels[14]);
        assertEquals( 0x11, pixels[15]);

        assertEquals( (byte) 0x89, pixels[16]);
        assertEquals( 0x13, pixels[17]);
        assertEquals( 0x18, pixels[18]);
        assertEquals( 0x1B, pixels[19]);

        assertEquals( 0x41, pixels[20]);
        assertEquals( 0x0f, pixels[21]);
        assertEquals( 0x10, pixels[22]);
        assertEquals( 0x10, pixels[23]);

        assertEquals( 0x09, pixels[24]);
        assertEquals( 0x00, pixels[25]);
        assertEquals( 0x00, pixels[26]);
        assertEquals( 0x01, pixels[27]);
    }

    public void test_convert_XYZA_bytes_to_AXYZPre_bytes() {
        byte[] pixels = new byte[] {
                0x05, 0x03, 0x01, (byte) 0xe3,
                0x17, 0x15, 0x11, (byte) 0xc3,
                0x34, 0x2e, 0x25, (byte) 0x89,
                0x42, 0x3f, 0x3b, 0x41,
                0x38, 0x1c, 0x1c, 0x09
        };

        ConverterUtils.convert_XYZA_bytes_to_AXYZPre_bytes(pixels, 0, pixels, 0, 5);

        assertEquals( (byte) 0xe3, pixels[0]);
        assertEquals( 0x04, pixels[1]);
        assertEquals( 0x02, pixels[2]);
        assertEquals( 0x00, pixels[3]);

        assertEquals( (byte) 0xc3, pixels[4]);
        assertEquals( 0x11, pixels[5]);
        assertEquals( 0x0f, pixels[6]);
        assertEquals( 0x0c, pixels[7]);

        assertEquals( (byte) 0x89, pixels[8]);
        assertEquals( 0x1B, pixels[9]);
        assertEquals( 0x18, pixels[10]);
        assertEquals( 0x13, pixels[11]);

        assertEquals( 0x41, pixels[12]);
        assertEquals( 0x10, pixels[13]);
        assertEquals( 0x0f, pixels[14]);
        assertEquals( 0x0e, pixels[15]);

        assertEquals( 0x09, pixels[16]);
        assertEquals( 0x01, pixels[17]);
        assertEquals( 0x00, pixels[18]);
        assertEquals( 0x00, pixels[19]);
    }

    public void test_convert_XYZA_bytes_to_AZYXPre_bytes() {
        byte[] pixels = new byte[] {
                0x05, 0x03, 0x01, (byte) 0xe3,
                0x17, 0x15, 0x11, (byte) 0xc3,
                0x34, 0x2e, 0x25, (byte) 0x89,
                0x42, 0x3f, 0x3b, 0x41,
                0x38, 0x1c, 0x1c, 0x09
        };

        ConverterUtils.convert_XYZA_bytes_to_AZYXPre_bytes(pixels, 0, pixels, 0, 5);

        assertEquals( (byte) 0xe3, pixels[0]);
        assertEquals( 0x00, pixels[1]);
        assertEquals( 0x02, pixels[2]);
        assertEquals( 0x04, pixels[3]);

        assertEquals( (byte) 0xc3, pixels[4]);
        assertEquals( 0x0c, pixels[5]);
        assertEquals( 0x0f, pixels[6]);
        assertEquals( 0x11, pixels[7]);

        assertEquals( (byte) 0x89, pixels[8]);
        assertEquals( 0x13, pixels[9]);
        assertEquals( 0x18, pixels[10]);
        assertEquals( 0x1B, pixels[11]);

        assertEquals( 0x41, pixels[12]);
        assertEquals( 0x0e, pixels[13]);
        assertEquals( 0x0f, pixels[14]);
        assertEquals( 0x10, pixels[15]);

        assertEquals( 0x09, pixels[16]);
        assertEquals( 0x00, pixels[17]);
        assertEquals( 0x00, pixels[18]);
        assertEquals( 0x01, pixels[19]);
    }

    public void test_convert_AXYZ_ints_to_G_bytes() {
        int[] pixels = new int[] {
                0xe3050301,
                0xc3171511,
                0x89342e25,
                0x41423f3b,
                0x09381c1c
        };

        byte[] dest = new byte[5];
        ConverterUtils.convert_AXYZ_ints_to_G_bytes(dest, 0, pixels, 0, 5);

        assertEquals(0x02, dest[0]);
        assertEquals(0x0f, dest[1]);
        assertEquals(0x18, dest[2]);
        assertEquals(0x0f, dest[3]);
        assertEquals(0x01, dest[4]);
    }

    public void test_convert_AXYZPre_ints_to_G_bytes() {
        int[] pixels = new int[] {
                0xe3050301,
                0xc3171511,
                0x89342e25,
                0x41423f3b,
                0x09381c1c
        };

        byte[] dest = new byte[5];
        ConverterUtils.convert_AXYZPre_ints_to_G_bytes(dest, 0, pixels, 0, 5);

        assertEquals(0x03, dest[0]);
        assertEquals(0x14, dest[1]);
        assertEquals(0x2d, dest[2]);
        assertEquals(0x3e, dest[3]);
        assertEquals(0x25, dest[4]);
    }

    public void test_convert_AXYZ_bytes_to_G_bytes() {
        byte[] pixels = new byte[] {
                -1, 0x55, 0x62, 0x08,
                0, 0x15, 0x67, 0x42,
                (byte) 0xe3, 0x05, 0x03, 0x01,
                (byte) 0xc3, 0x17, 0x15, 0x11,
                (byte) 0x89, 0x34, 0x2e, 0x25,
                0x41, 0x42, 0x3f, 0x3b,
                0x09, 0x38, 0x1c, 0x1c
        };

        ConverterUtils.convert_AXYZ_bytes_to_G_bytes(pixels, 0, pixels, 0, 7);

        assertEquals(0x3F, pixels[0]);
        assertEquals(0x0, pixels[1]);
        assertEquals(0x02, pixels[2]);
        assertEquals(0x0f, pixels[3]);
        assertEquals(0x18, pixels[4]);
        assertEquals(0x0f, pixels[5]);
        assertEquals(0x01, pixels[6]);
    }

    public void test_convert_AXYZPre_bytes_to_G_bytes() {
        byte[] pixels = new byte[] {
                (byte) 0xe3, 0x05, 0x03, 0x01,
                (byte) 0xc3, 0x17, 0x15, 0x11,
                (byte) 0x89, 0x34, 0x2e, 0x25,
                0x41, 0x42, 0x3f, 0x3b,
                0x09, 0x38, 0x1c, 0x1c
        };

        ConverterUtils.convert_AXYZPre_bytes_to_G_bytes(pixels, 0, pixels, 0, 5);

        assertEquals(0x03, pixels[0]);
        assertEquals(0x14, pixels[1]);
        assertEquals(0x2d, pixels[2]);
        assertEquals(0x3e, pixels[3]);
        assertEquals(0x25, pixels[4]);
    }

    public void test_convert_XYZA_bytes_to_G_bytes() {
        byte[] pixels = new byte[] {
                0x05, 0x03, 0x01, (byte) 0xe3,
                0x17, 0x15, 0x11, (byte) 0xc3,
                0x34, 0x2e, 0x25, (byte) 0x89,
                0x42, 0x3f, 0x3b, 0x41,
                0x38, 0x1c, 0x1c, 0x09
        };

        ConverterUtils.convert_XYZA_bytes_to_G_bytes(pixels, 0, pixels, 0, 5);

        assertEquals(0x02, pixels[0]);
        assertEquals(0x0f, pixels[1]);
        assertEquals(0x18, pixels[2]);
        assertEquals(0x0f, pixels[3]);
        assertEquals(0x01, pixels[4]);
    }
}