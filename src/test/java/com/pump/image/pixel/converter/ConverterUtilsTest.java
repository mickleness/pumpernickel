/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.image.pixel.converter;

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
                -5, -51, -113,
                0, 0, 0, 0, 0, 0, 0};
        ConverterUtils.convert_XYZ_bytes_to_AXYZ_bytes(pixels, 0, pixels, 0, 7);
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

        assertEquals(-1, pixels[24]);
        assertEquals(-5, pixels[25]);
        assertEquals(-51, pixels[26]);
        assertEquals(-113, pixels[27]);
    }
    public void test_convert_XYZ_bytes_to_AZYX_bytes() {
        byte[] pixels = new byte[] {
                0x03, 0x05, 0x07,
                0x13, 0x17, 0x19,
                0x29, 0x31, 0x33,
                0x41, 0x43, 0x47,
                0x49, 0x51, 0x57,
                0x59, 0x61, 0x67,
                -5, -51, -113,
                0, 0, 0, 0, 0, 0, 0};
        ConverterUtils.convert_XYZ_bytes_to_AZYX_bytes(pixels, 0, pixels, 0, 7);
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

        assertEquals(-1, pixels[24]);
        assertEquals(-113, pixels[25]);
        assertEquals(-51, pixels[26]);
        assertEquals(-5, pixels[27]);
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
                0x59, 0x61, 0x67,
                -5, -51, -113 };
        int[] dest = new int[7];
        ConverterUtils.convert_XYZ_bytes_to_AXYZ_ints(dest, 0, pixels, 0, 7);
        assertEquals(Integer.toUnsignedString(dest[0], 16),0xff030507, dest[0]);
        assertEquals(Integer.toUnsignedString(dest[1], 16),0xff131719, dest[1]);
        assertEquals(Integer.toUnsignedString(dest[2], 16),0xff293133, dest[2]);
        assertEquals(Integer.toUnsignedString(dest[3], 16),0xff414347, dest[3]);
        assertEquals(Integer.toUnsignedString(dest[4], 16),0xff495157, dest[4]);
        assertEquals(Integer.toUnsignedString(dest[5], 16),0xff596167, dest[5]);
        assertEquals(Integer.toUnsignedString(dest[6], 16),0xfffbcd8f, dest[6]);
    }

    public void test_convert_XYZ_bytes_to_AZYX_ints() {
        byte[] pixels = new byte[] {
                0x03, 0x05, 0x07,
                0x13, 0x17, 0x19,
                0x29, 0x31, 0x33,
                0x41, 0x43, 0x47,
                0x49, 0x51, 0x57,
                0x59, 0x61, 0x67,
                -5, -51, -113 };
        int[] dest = new int[7];
        ConverterUtils.convert_XYZ_bytes_to_AZYX_ints(dest, 0, pixels, 0, 7);
        assertEquals(Integer.toUnsignedString(dest[0], 16),0xff070503, dest[0]);
        assertEquals(Integer.toUnsignedString(dest[1], 16),0xff191713, dest[1]);
        assertEquals(Integer.toUnsignedString(dest[2], 16),0xff333129, dest[2]);
        assertEquals(Integer.toUnsignedString(dest[3], 16),0xff474341, dest[3]);
        assertEquals(Integer.toUnsignedString(dest[4], 16),0xff575149, dest[4]);
        assertEquals(Integer.toUnsignedString(dest[5], 16),0xff676159, dest[5]);
        assertEquals(Integer.toUnsignedString(dest[6], 16),0xff8fcdfb, dest[6]);
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
                0x49, 0x51, 0x57, 0x59,
                -1, -5, -51, -113,
                -10, -110, -104, -99 };
        ConverterUtils.convert_AXYZ_bytes_to_AZYX_bytes(pixels, 0, pixels, 0, 8);

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

        assertEquals(-1, pixels[24]);
        assertEquals(-113, pixels[25]);
        assertEquals(-51, pixels[26]);
        assertEquals(-5, pixels[27]);

        assertEquals(-10, pixels[28]);
        assertEquals(-99, pixels[29]);
        assertEquals(-104, pixels[30]);
        assertEquals(-110, pixels[31]);
    }

    public void test_convert_XYZ_bytes_to_XYZ_ints() {

        byte[] pixels = new byte[] {
                0x03, 0x05, 0x07,
                0x13, 0x17, 0x19,
                0x29, 0x31, 0x33,
                0x41, 0x43, 0x47,
                0x49, 0x51, 0x57,
                -5, -51, -113  };

        int[] dest = new int[6];
        ConverterUtils.convert_XYZ_bytes_to_XYZ_ints(dest, 0, pixels, 0, 6);
        assertEquals(Integer.toUnsignedString(dest[0], 16),0x030507, dest[0]);
        assertEquals(Integer.toUnsignedString(dest[1], 16),0x131719, dest[1]);
        assertEquals(Integer.toUnsignedString(dest[2], 16),0x293133, dest[2]);
        assertEquals(Integer.toUnsignedString(dest[3], 16),0x414347, dest[3]);
        assertEquals(Integer.toUnsignedString(dest[4], 16),0x495157, dest[4]);
        assertEquals(Integer.toUnsignedString(dest[5], 16),0xfbcd8f, dest[5]);
    }

    public void test_convert_XYZ_bytes_to_ZYX_ints() {

        byte[] pixels = new byte[] {
                0x03, 0x05, 0x07,
                0x13, 0x17, 0x19,
                0x29, 0x31, 0x33,
                0x41, 0x43, 0x47,
                0x49, 0x51, 0x57,
                -5, -51, -113  };

        int[] dest = new int[6];
        ConverterUtils.convert_XYZ_bytes_to_ZYX_ints(dest, 0, pixels, 0, 6);
        assertEquals(Integer.toUnsignedString(dest[0], 16),0x070503, dest[0]);
        assertEquals(Integer.toUnsignedString(dest[1], 16),0x191713, dest[1]);
        assertEquals(Integer.toUnsignedString(dest[2], 16),0x333129, dest[2]);
        assertEquals(Integer.toUnsignedString(dest[3], 16),0x474341, dest[3]);
        assertEquals(Integer.toUnsignedString(dest[4], 16),0x575149, dest[4]);
        assertEquals(Integer.toUnsignedString(dest[5], 16),0x8fcdfb, dest[5]);
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
                0x49, 0x51, 0x57,
                -5, -51, -113 };
        ConverterUtils.convert_XYZ_bytes_to_G_bytes(pixels, 0, pixels, 0, 6);
        assertEquals((3 + 5 + 7)/3, pixels[0]);
        assertEquals((19 + 23 + 25)/3, pixels[1] & 0xff);
        assertEquals((41 + 49 + 51)/3, pixels[2] & 0xff);
        assertEquals((65 + 67 + 71)/3, pixels[3] & 0xff);
        assertEquals((73 + 81 + 87)/3, pixels[4] & 0xff);
        assertEquals(199, pixels[5] & 0xff);
    }

    public void test_convert_AXYZ_ints_to_AXYZ_bytes() {
        int[] pixels = new int[] {
                0xff556208,
                0x00156742,
                0x63030507,
                0x67131719,
                0x71293133,
                0x73414347,
                0x77495157 };
        byte[] dest = new byte[4 * 7];
        ConverterUtils.convert_AXYZ_ints_to_AXYZ_bytes(dest, 0, pixels, 0, 7);

        assertEquals(-1, dest[0]);
        assertEquals(0x55, dest[1]);
        assertEquals(0x62, dest[2]);
        assertEquals(0x08, dest[3]);

        assertEquals(0, dest[4]);
        assertEquals(0x15, dest[5]);
        assertEquals(0x67, dest[6]);
        assertEquals(0x42, dest[7]);

        assertEquals(0x63, dest[8]);
        assertEquals(0x03, dest[9]);
        assertEquals(0x05, dest[10]);
        assertEquals(0x07, dest[11]);

        assertEquals(0x67, dest[12]);
        assertEquals(0x13, dest[13]);
        assertEquals(0x17, dest[14]);
        assertEquals(0x19, dest[15]);

        assertEquals(0x71, dest[16]);
        assertEquals(0x29, dest[17]);
        assertEquals(0x31, dest[18]);
        assertEquals(0x33, dest[19]);

        assertEquals(0x73, dest[20]);
        assertEquals(0x41, dest[21]);
        assertEquals(0x43, dest[22]);
        assertEquals(0x47, dest[23]);

        assertEquals(0x77, dest[24]);
        assertEquals(0x49, dest[25]);
        assertEquals(0x51, dest[26]);
        assertEquals(0x57, dest[27]);
    }

    public void test_convert_AXYZ_ints_to_XYZA_bytes() {
        int[] pixels = new int[] {
                0xff556208,
                0x00156742,
                0x63030507,
                0x67131719,
                0x71293133,
                0x73414347,
                0x77495157 };
        byte[] dest = new byte[4 * 7];
        ConverterUtils.convert_AXYZ_ints_to_XYZA_bytes(dest, 0, pixels, 0, 7);

        assertEquals(0x55, dest[0]);
        assertEquals(0x62, dest[1]);
        assertEquals(0x08, dest[2]);
        assertEquals(-1, dest[3]);

        assertEquals(0x15, dest[4]);
        assertEquals(0x67, dest[5]);
        assertEquals(0x42, dest[6]);
        assertEquals(0, dest[7]);

        assertEquals(0x03, dest[8]);
        assertEquals(0x05, dest[9]);
        assertEquals(0x07, dest[10]);
        assertEquals(0x63, dest[11]);

        assertEquals(0x13, dest[12]);
        assertEquals(0x17, dest[13]);
        assertEquals(0x19, dest[14]);
        assertEquals(0x67, dest[15]);

        assertEquals(0x29, dest[16]);
        assertEquals(0x31, dest[17]);
        assertEquals(0x33, dest[18]);
        assertEquals(0x71, dest[19]);

        assertEquals(0x41, dest[20]);
        assertEquals(0x43, dest[21]);
        assertEquals(0x47, dest[22]);
        assertEquals(0x73, dest[23]);

        assertEquals(0x49, dest[24]);
        assertEquals(0x51, dest[25]);
        assertEquals(0x57, dest[26]);
        assertEquals(0x77, dest[27]);
    }

    public void test_convert_AXYZ_ints_to_AZYX_bytes() {
        int[] pixels = new int[] {
                0xff556208,
                0x00156742,
                0x63030507,
                0x67131719,
                0x71293133,
                0x73414347,
                0x77495157 };
        byte[] dest = new byte[4 * 7];
        ConverterUtils.convert_AXYZ_ints_to_AZYX_bytes(dest, 0, pixels, 0, 7);

        assertEquals(-1, dest[0]);
        assertEquals(0x08, dest[1]);
        assertEquals(0x62, dest[2]);
        assertEquals(0x55, dest[3]);

        assertEquals(0x00, dest[4]);
        assertEquals(0x42, dest[5]);
        assertEquals(0x67, dest[6]);
        assertEquals(0x15, dest[7]);

        assertEquals(0x63, dest[8]);
        assertEquals(0x07, dest[9]);
        assertEquals(0x05, dest[10]);
        assertEquals(0x03, dest[11]);

        assertEquals(0x67, dest[12]);
        assertEquals(0x19, dest[13]);
        assertEquals(0x17, dest[14]);
        assertEquals(0x13, dest[15]);

        assertEquals(0x71, dest[16]);
        assertEquals(0x33, dest[17]);
        assertEquals(0x31, dest[18]);
        assertEquals(0x29, dest[19]);

        assertEquals(0x73, dest[20]);
        assertEquals(0x47, dest[21]);
        assertEquals(0x43, dest[22]);
        assertEquals(0x41, dest[23]);

        assertEquals(0x77, dest[24]);
        assertEquals(0x57, dest[25]);
        assertEquals(0x51, dest[26]);
        assertEquals(0x49, dest[27]);
    }
    public void test_convert_AXYZ_bytes_to_AXYZ_ints() {
        byte[] pixels = new byte[] {
                -1, 0x55, 0x62, 0x08,
                0, 0x15, 0x67, 0x42,
                0x63, 0x03, 0x05, 0x07,
                0x67, 0x13, 0x17, 0x19,
                0x71, 0x29, 0x31, 0x33,
                0x73, 0x41, 0x43, 0x47,
                0x77, 0x49, 0x51, 0x57,
                -1, -5, -51, -113,
                -10, -110, -104, -99 };
        int[] dest = new int[9];
        ConverterUtils.convert_AXYZ_bytes_to_AXYZ_ints(dest, 0, pixels, 0, 9);

        assertEquals(Integer.toUnsignedString(dest[0], 16),0xff556208, dest[0]);
        assertEquals(Integer.toUnsignedString(dest[1], 16),0x00156742, dest[1]);
        assertEquals(Integer.toUnsignedString(dest[2], 16),0x63030507, dest[2]);
        assertEquals(Integer.toUnsignedString(dest[3], 16),0x67131719, dest[3]);
        assertEquals(Integer.toUnsignedString(dest[4], 16),0x71293133, dest[4]);
        assertEquals(Integer.toUnsignedString(dest[5], 16),0x73414347, dest[5]);
        assertEquals(Integer.toUnsignedString(dest[6], 16),0x77495157, dest[6]);
        assertEquals(Integer.toUnsignedString(dest[7], 16),0xfffbcd8f, dest[7]);
        assertEquals(Integer.toUnsignedString(dest[8], 16),0xf692989d, dest[8]);
    }

    public void test_convert_AXYZ_bytes_to_AZYX_ints() {
        byte[] pixels = new byte[] {
                -1, 0x55, 0x62, 0x08,
                0, 0x15, 0x67, 0x42,
                0x63, 0x03, 0x05, 0x07,
                0x67, 0x13, 0x17, 0x19,
                0x71, 0x29, 0x31, 0x33,
                0x73, 0x41, 0x43, 0x47,
                0x77, 0x49, 0x51, 0x57,
                -1, -5, -51, -113,
                -10, -110, -104, -99 };
        int[] dest = new int[9];
        ConverterUtils.convert_AXYZ_bytes_to_AZYX_ints(dest, 0, pixels, 0, 9);

        assertEquals(Integer.toUnsignedString(dest[0], 16),0xff086255, dest[0]);
        assertEquals(Integer.toUnsignedString(dest[1], 16),0x00426715, dest[1]);
        assertEquals(Integer.toUnsignedString(dest[2], 16),0x63070503, dest[2]);
        assertEquals(Integer.toUnsignedString(dest[3], 16),0x67191713, dest[3]);
        assertEquals(Integer.toUnsignedString(dest[4], 16),0x71333129, dest[4]);
        assertEquals(Integer.toUnsignedString(dest[5], 16),0x73474341, dest[5]);
        assertEquals(Integer.toUnsignedString(dest[6], 16),0x77575149, dest[6]);
        assertEquals(Integer.toUnsignedString(dest[7], 16),0xff8fcdfb, dest[7]);
        assertEquals(Integer.toUnsignedString(dest[8], 16),0xf69d9892, dest[8]);
    }

    public void test_convert_G_bytes_to_AXYZ_bytes() {
        byte[] pixels = new byte[] {
                0x00, (byte) 0xff,
                0x63, 0x03, 0x05, 0x07,
                0x67, 0x13, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        ConverterUtils.convert_G_bytes_to_AXYZ_bytes(pixels, 0, pixels, 0, 8);

        assertEquals(-1, pixels[0]);
        assertEquals(0, pixels[1]);
        assertEquals(0, pixels[2]);
        assertEquals(0, pixels[3]);

        assertEquals(-1, pixels[4]);
        assertEquals(-1, pixels[5]);
        assertEquals(-1, pixels[6]);
        assertEquals(-1, pixels[7]);

        assertEquals(-1, pixels[8]);
        assertEquals(0x63, pixels[9]);
        assertEquals(0x63, pixels[10]);
        assertEquals(0x63, pixels[11]);

        assertEquals(-1, pixels[12]);
        assertEquals(0x03, pixels[13]);
        assertEquals(0x03, pixels[14]);
        assertEquals(0x03, pixels[15]);

        assertEquals(-1, pixels[16]);
        assertEquals(0x05, pixels[17]);
        assertEquals(0x05, pixels[18]);
        assertEquals(0x05, pixels[19]);

        assertEquals(-1, pixels[20]);
        assertEquals(0x07, pixels[21]);
        assertEquals(0x07, pixels[22]);
        assertEquals(0x07, pixels[23]);

        assertEquals(-1, pixels[24]);
        assertEquals(0x67, pixels[25]);
        assertEquals(0x67, pixels[26]);
        assertEquals(0x67, pixels[27]);

        assertEquals(-1, pixels[28]);
        assertEquals(0x13, pixels[29]);
        assertEquals(0x13, pixels[30]);
        assertEquals(0x13, pixels[31]);
    }

    public void test_convert_G_bytes_to_XYZ_bytes() {
        byte[] pixels = new byte[] {
                0x00, (byte) 0xff,
                0x63, 0x03, 0x05, 0x07,
                0x67, 0x13, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        ConverterUtils.convert_G_bytes_to_XYZ_bytes(pixels, 0, pixels, 0, 8);

        assertEquals(0, pixels[0]);
        assertEquals(0, pixels[1]);
        assertEquals(0, pixels[2]);

        assertEquals(-1, pixels[3]);
        assertEquals(-1, pixels[4]);
        assertEquals(-1, pixels[5]);

        assertEquals(0x63, pixels[6]);
        assertEquals(0x63, pixels[7]);
        assertEquals(0x63, pixels[8]);

        assertEquals(0x03, pixels[9]);
        assertEquals(0x03, pixels[10]);
        assertEquals(0x03, pixels[11]);

        assertEquals(0x05, pixels[12]);
        assertEquals(0x05, pixels[13]);
        assertEquals(0x05, pixels[14]);

        assertEquals(0x07, pixels[15]);
        assertEquals(0x07, pixels[16]);
        assertEquals(0x07, pixels[17]);

        assertEquals(0x67, pixels[18]);
        assertEquals(0x67, pixels[19]);
        assertEquals(0x67, pixels[20]);

        assertEquals(0x13, pixels[21]);
        assertEquals(0x13, pixels[22]);
        assertEquals(0x13, pixels[23]);
    }

    public void test_convert_G_bytes_to_AXYZ_ints() {
        byte[] pixels = new byte[] {
                0x00, (byte) 0xff,
                0x63, 0x03, 0x05, 0x07,
                0x67, 0x13};
        int[] dest = new int[8];
        ConverterUtils.convert_G_bytes_to_AXYZ_ints(dest, 0, pixels, 0, 8);

        assertEquals(Integer.toUnsignedString(dest[0], 16),0xff000000, dest[0]);
        assertEquals(Integer.toUnsignedString(dest[1], 16),0xffffffff, dest[1]);
        assertEquals(Integer.toUnsignedString(dest[2], 16),0xff636363, dest[2]);
        assertEquals(Integer.toUnsignedString(dest[3], 16),0xff030303, dest[3]);
        assertEquals(Integer.toUnsignedString(dest[4], 16),0xff050505, dest[4]);
        assertEquals(Integer.toUnsignedString(dest[5], 16),0xff070707, dest[5]);
        assertEquals(Integer.toUnsignedString(dest[6], 16),0xff676767, dest[6]);
        assertEquals(Integer.toUnsignedString(dest[7], 16),0xff131313, dest[7]);
    }

    public void test_convert_G_bytes_to_XYZ_ints() {
        byte[] pixels = new byte[] {
                0x00, (byte) 0xff,
                0x63, 0x03, 0x05, 0x07,
                0x67, 0x13};
        int[] dest = new int[8];
        ConverterUtils.convert_G_bytes_to_XYZ_ints(dest, 0, pixels, 0, 8);

        assertEquals(Integer.toUnsignedString(dest[0], 16),0x000000, dest[0]);
        assertEquals(Integer.toUnsignedString(dest[1], 16),0xFFFFFF, dest[1]);
        assertEquals(Integer.toUnsignedString(dest[2], 16),0x636363, dest[2]);
        assertEquals(Integer.toUnsignedString(dest[3], 16),0x030303, dest[3]);
        assertEquals(Integer.toUnsignedString(dest[4], 16),0x050505, dest[4]);
        assertEquals(Integer.toUnsignedString(dest[5], 16),0x070707, dest[5]);
        assertEquals(Integer.toUnsignedString(dest[6], 16),0x676767, dest[6]);
        assertEquals(Integer.toUnsignedString(dest[7], 16),0x131313, dest[7]);
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
                0x49, 0x51, 0x57,
                -5, -51, -113 };
        ConverterUtils.convert_XYZ_bytes_to_ZYX_bytes(pixels, 0, pixels, 0, 6);

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

        assertEquals(-113, pixels[15]);
        assertEquals(-51, pixels[16]);
        assertEquals(-5, pixels[17]);
    }

    public void test_convert_AXYZ_ints_to_ZYXA_bytes() {
        int[] pixels = new int[] {
                0xff556208,
                0x00156742,
                0x61030507,
                0x67131719,
                0x71293133,
                0x73414347,
                0x79495157 };
        byte[] dest = new byte[4 * 7];

        ConverterUtils.convert_AXYZ_ints_to_ZYXA_bytes(dest, 0, pixels, 0, 7);

        assertEquals(0x08, dest[0]);
        assertEquals(0x62, dest[1]);
        assertEquals(0x55, dest[2]);
        assertEquals(-1, dest[3]);

        assertEquals(0x42, dest[4]);
        assertEquals(0x67, dest[5]);
        assertEquals(0x15, dest[6]);
        assertEquals(0x00, dest[7]);

        assertEquals(0x07, dest[8]);
        assertEquals(0x05, dest[9]);
        assertEquals(0x03, dest[10]);
        assertEquals(0x61, dest[11]);

        assertEquals(0x19, dest[12]);
        assertEquals(0x17, dest[13]);
        assertEquals(0x13, dest[14]);
        assertEquals(0x67, dest[15]);

        assertEquals(0x33, dest[16]);
        assertEquals(0x31, dest[17]);
        assertEquals(0x29, dest[18]);
        assertEquals(0x71, dest[19]);

        assertEquals(0x47, dest[20]);
        assertEquals(0x43, dest[21]);
        assertEquals(0x41, dest[22]);
        assertEquals(0x73, dest[23]);

        assertEquals(0x57, dest[24]);
        assertEquals(0x51, dest[25]);
        assertEquals(0x49, dest[26]);
        assertEquals(0x79, dest[27]);
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
                0x49, 0x51, 0x57, 0x73,
                -1, -5, -51, -113,
                -10, -110, -104, -99 };
        ConverterUtils.convert_AXYZ_bytes_to_ZYXA_bytes(pixels, 0, pixels, 0, 9);

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

        assertEquals(-113, pixels[28]);
        assertEquals(-51, pixels[29]);
        assertEquals(-5, pixels[30]);
        assertEquals(-1, pixels[31]);

        assertEquals(-99, pixels[32]);
        assertEquals(-104, pixels[33]);
        assertEquals(-110, pixels[34]);
        assertEquals(-10, pixels[35]);
    }

    public void test_convert_AXYZ_bytes_to_XYZA_bytes() {
        byte[] pixels = new byte[] {
                -1, 0x55, 0x62, 0x08,
                0, 0x15, 0x67, 0x42,
                0x03, 0x05, 0x07, 0x59,
                0x13, 0x17, 0x19, 0x61,
                0x29, 0x31, 0x33, 0x67,
                0x41, 0x43, 0x47, 0x71,
                0x49, 0x51, 0x57, 0x73,
                -1, -5, -51, -113,
                -10, -110, -104, -99 };
        ConverterUtils.convert_AXYZ_bytes_to_XYZA_bytes(pixels, 0, pixels, 0, 9);

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

        assertEquals(-5, pixels[28]);
        assertEquals(-51, pixels[29]);
        assertEquals(-113, pixels[30]);
        assertEquals(-1, pixels[31]);

        assertEquals(-110, pixels[32]);
        assertEquals(-104, pixels[33]);
        assertEquals(-99, pixels[34]);
        assertEquals(-10, pixels[35]);
    }

    public void test_convert_G_bytes_to_XYZA_bytes() {
        byte[] pixels = new byte[] {
                0x00,
                (byte) 0xff,
                0x03,
                0x13,
                0x29,
                0x41,
                0x49,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
        ConverterUtils.convert_G_bytes_to_XYZA_bytes(pixels, 0, pixels, 0, 7);

        assertEquals(0x00, pixels[0]);
        assertEquals(0x00, pixels[1]);
        assertEquals(0x00, pixels[2]);
        assertEquals(-1, pixels[3]);

        assertEquals(-1, pixels[4]);
        assertEquals(-1, pixels[5]);
        assertEquals(-1, pixels[6]);
        assertEquals(-1, pixels[7]);

        assertEquals(0x03, pixels[8]);
        assertEquals(0x03, pixels[9]);
        assertEquals(0x03, pixels[10]);
        assertEquals(-1, pixels[11]);

        assertEquals(0x13, pixels[12]);
        assertEquals(0x13, pixels[13]);
        assertEquals(0x13, pixels[14]);
        assertEquals(-1, pixels[15]);

        assertEquals(0x29, pixels[16]);
        assertEquals(0x29, pixels[17]);
        assertEquals(0x29, pixels[18]);
        assertEquals(-1, pixels[19]);

        assertEquals(0x41, pixels[20]);
        assertEquals(0x41, pixels[21]);
        assertEquals(0x41, pixels[22]);
        assertEquals(-1, pixels[23]);

        assertEquals(0x49, pixels[24]);
        assertEquals(0x49, pixels[25]);
        assertEquals(0x49, pixels[26]);
        assertEquals(-1, pixels[27]);
    }

    public void test_convert_XYZ_bytes_to_ZYXA_bytes() {
        byte[] pixels = new byte[] {
                0x03, 0x05, 0x07,
                0x13, 0x17, 0x19,
                0x29, 0x31, 0x33,
                0x41, 0x43, 0x47,
                0x49, 0x51, 0x57,
                -5, -51, -113,
                0, 0, 0, 0, 0, 0};
        ConverterUtils.convert_XYZ_bytes_to_ZYXA_bytes(pixels, 0, pixels, 0, 6);

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

        assertEquals(-113, pixels[20]);
        assertEquals(-51, pixels[21]);
        assertEquals(-5, pixels[22]);
        assertEquals(-1, pixels[23]);
    }

    public void test_convert_XYZ_bytes_to_XYZA_bytes() {
        byte[] pixels = new byte[] {
                0x03, 0x05, 0x07,
                0x13, 0x17, 0x19,
                0x29, 0x31, 0x33,
                0x41, 0x43, 0x47,
                0x49, 0x51, 0x57,
                -5, -51, -113,
                0, 0, 0, 0, 0, 0};
        ConverterUtils.convert_XYZ_bytes_to_XYZA_bytes(pixels, 0, pixels, 0, 6);

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

        assertEquals(-5, pixels[20]);
        assertEquals(-51, pixels[21]);
        assertEquals(-113, pixels[22]);
        assertEquals(-1, pixels[23]);
    }

    public void test_convert_XYZA_bytes_to_AXYZ_bytes() {
        byte[] pixels = new byte[] {
                0x55, 0x62, 0x08, (byte) 0xff,
                0x15, 0x67, 0x42, 0x00,
                0x03, 0x05, 0x07, 0x59,
                0x13, 0x17, 0x19, 0x61,
                0x29, 0x31, 0x33, 0x67,
                0x41, 0x43, 0x47, 0x71,
                0x49, 0x51, 0x57, 0x73,
                -5, -51, -113, -1,
                -110, -104, -99, -10 };
        ConverterUtils.convert_XYZA_bytes_to_AXYZ_bytes(pixels, 0, pixels, 0, 9);

        assertEquals(-1, pixels[0]);
        assertEquals(0x55, pixels[1]);
        assertEquals(0x62, pixels[2]);
        assertEquals(0x08, pixels[3]);

        assertEquals(0x00, pixels[4]);
        assertEquals(0x15, pixels[5]);
        assertEquals(0x67, pixels[6]);
        assertEquals(0x42, pixels[7]);

        assertEquals(0x59, pixels[8]);
        assertEquals(0x03, pixels[9]);
        assertEquals(0x05, pixels[10]);
        assertEquals(0x07, pixels[11]);

        assertEquals(0x61, pixels[12]);
        assertEquals(0x13, pixels[13]);
        assertEquals(0x17, pixels[14]);
        assertEquals(0x19, pixels[15]);

        assertEquals(0x67, pixels[16]);
        assertEquals(0x29, pixels[17]);
        assertEquals(0x31, pixels[18]);
        assertEquals(0x33, pixels[19]);

        assertEquals(0x71, pixels[20]);
        assertEquals(0x41, pixels[21]);
        assertEquals(0x43, pixels[22]);
        assertEquals(0x47, pixels[23]);

        assertEquals(0x73, pixels[24]);
        assertEquals(0x49, pixels[25]);
        assertEquals(0x51, pixels[26]);
        assertEquals(0x57, pixels[27]);

        assertEquals(-1, pixels[28]);
        assertEquals(-5, pixels[29]);
        assertEquals(-51, pixels[30]);
        assertEquals(-113, pixels[31]);

        assertEquals(-10, pixels[32]);
        assertEquals(-110, pixels[33]);
        assertEquals(-104, pixels[34]);
        assertEquals(-99, pixels[35]);
    }

    public void test_convert_XYZA_bytes_to_AZYX_bytes() {
        byte[] pixels = new byte[] {
                0x55, 0x62, 0x08, (byte) 0xff,
                0x15, 0x67, 0x42, 0x00,
                0x03, 0x05, 0x07, 0x59,
                0x13, 0x17, 0x19, 0x61,
                0x29, 0x31, 0x33, 0x67,
                0x41, 0x43, 0x47, 0x71,
                0x49, 0x51, 0x57, 0x73,
                -5, -51, -113, -1,
                -110, -104, -99, -10 };
        ConverterUtils.convert_XYZA_bytes_to_AZYX_bytes(pixels, 0, pixels, 0, 9);

        assertEquals(-1, pixels[0]);
        assertEquals(0x08, pixels[1]);
        assertEquals(0x62, pixels[2]);
        assertEquals(0x55, pixels[3]);

        assertEquals(0x00, pixels[4]);
        assertEquals(0x42, pixels[5]);
        assertEquals(0x67, pixels[6]);
        assertEquals(0x15, pixels[7]);

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

        assertEquals(-1, pixels[28]);
        assertEquals(-113, pixels[29]);
        assertEquals(-51, pixels[30]);
        assertEquals(-5, pixels[31]);

        assertEquals(-10, pixels[32]);
        assertEquals(-99, pixels[33]);
        assertEquals(-104, pixels[34]);
        assertEquals(-110, pixels[35]);
    }

    public void test_convert_XYZA_bytes_to_ZYXA_bytes() {
        byte[] pixels = new byte[] {
                0x55, 0x62, 0x08, (byte) 0xff,
                0x15, 0x67, 0x42, 0x00,
                0x03, 0x05, 0x07, 0x59,
                0x13, 0x17, 0x19, 0x61,
                0x29, 0x31, 0x33, 0x67,
                0x41, 0x43, 0x47, 0x71,
                0x49, 0x51, 0x57, 0x73,
                -5, -51, -113, -1,
                -110, -104, -99, -10 };
        ConverterUtils.convert_XYZA_bytes_to_ZYXA_bytes(pixels, 0, pixels, 0, 9);

        assertEquals(0x08, pixels[0]);
        assertEquals(0x62, pixels[1]);
        assertEquals(0x55, pixels[2]);
        assertEquals(-1, pixels[3]);

        assertEquals(0x42, pixels[4]);
        assertEquals(0x67, pixels[5]);
        assertEquals(0x15, pixels[6]);
        assertEquals(0x00, pixels[7]);

        assertEquals(0x07, pixels[8]);
        assertEquals(0x05, pixels[9]);
        assertEquals(0x03, pixels[10]);
        assertEquals(0x59, pixels[11]);

        assertEquals(0x19, pixels[12]);
        assertEquals(0x17, pixels[13]);
        assertEquals(0x13, pixels[14]);
        assertEquals(0x61, pixels[15]);

        assertEquals(0x33, pixels[16]);
        assertEquals(0x31, pixels[17]);
        assertEquals(0x29, pixels[18]);
        assertEquals(0x67, pixels[19]);

        assertEquals(0x47, pixels[20]);
        assertEquals(0x43, pixels[21]);
        assertEquals(0x41, pixels[22]);
        assertEquals(0x71, pixels[23]);

        assertEquals(0x57, pixels[24]);
        assertEquals(0x51, pixels[25]);
        assertEquals(0x49, pixels[26]);
        assertEquals(0x73, pixels[27]);

        assertEquals(-113, pixels[28]);
        assertEquals(-51, pixels[29]);
        assertEquals(-5, pixels[30]);
        assertEquals(-1, pixels[31]);

        assertEquals(-99, pixels[32]);
        assertEquals(-104, pixels[33]);
        assertEquals(-110, pixels[34]);
        assertEquals(-10, pixels[35]);
    }

    public void test_convert_XYZA_bytes_to_AZYX_ints() {
        byte[] pixels = new byte[] {
                0x55, 0x62, 0x08, (byte) 0xff,
                0x15, 0x67, 0x42, 0x00,
                0x03, 0x05, 0x07, 0x59,
                0x13, 0x17, 0x19, 0x61,
                0x29, 0x31, 0x33, 0x67,
                0x41, 0x43, 0x47, 0x71,
                0x49, 0x51, 0x57, 0x73,
                -5, -51, -113, -1,
                -110, -104, -99, -10  };
        int[] dest = new int[9];
        ConverterUtils.convert_XYZA_bytes_to_AZYX_ints(dest, 0, pixels, 0, 9);

        assertEquals(Integer.toUnsignedString(dest[0], 16),0xff086255, dest[0]);
        assertEquals(Integer.toUnsignedString(dest[1], 16),0x00426715, dest[1]);
        assertEquals(Integer.toUnsignedString(dest[2], 16),0x59070503, dest[2]);
        assertEquals(Integer.toUnsignedString(dest[3], 16),0x61191713, dest[3]);
        assertEquals(Integer.toUnsignedString(dest[4], 16),0x67333129, dest[4]);
        assertEquals(Integer.toUnsignedString(dest[5], 16),0x71474341, dest[5]);
        assertEquals(Integer.toUnsignedString(dest[6], 16),0x73575149, dest[6]);
        assertEquals(Integer.toUnsignedString(dest[7], 16),0xff8fcdfb, dest[7]);
        assertEquals(Integer.toUnsignedString(dest[8], 16),0xf69d9892, dest[8]);
    }

    public void test_convert_XYZA_bytes_to_AXYZ_ints() {
        byte[] pixels = new byte[] {
                0x55, 0x62, 0x08, (byte) 0xff,
                0x15, 0x67, 0x42, 0x00,
                0x03, 0x05, 0x07, 0x59,
                0x13, 0x17, 0x19, 0x61,
                0x29, 0x31, 0x33, 0x67,
                0x41, 0x43, 0x47, 0x71,
                0x49, 0x51, 0x57, 0x73,
                -5, -51, -113, -1,
                -110, -104, -99, -10 };
        int[] dest = new int[9];
        ConverterUtils.convert_XYZA_bytes_to_AXYZ_ints(dest, 0, pixels, 0, 9);

        assertEquals(Integer.toUnsignedString(dest[0], 16),0xff556208, dest[0]);
        assertEquals(Integer.toUnsignedString(dest[1], 16),0x00156742, dest[1]);
        assertEquals(Integer.toUnsignedString(dest[2], 16),0x59030507, dest[2]);
        assertEquals(Integer.toUnsignedString(dest[3], 16),0x61131719, dest[3]);
        assertEquals(Integer.toUnsignedString(dest[4], 16),0x67293133, dest[4]);
        assertEquals(Integer.toUnsignedString(dest[5], 16),0x71414347, dest[5]);
        assertEquals(Integer.toUnsignedString(dest[6], 16),0x73495157, dest[6]);
        assertEquals(Integer.toUnsignedString(dest[7], 16),0xfffbcd8f, dest[7]);
        assertEquals(Integer.toUnsignedString(dest[8], 16),0xf692989d, dest[8]);
    }

    public void test_convert_AXYZ_ints_to_XYZ_bytes() {
        int[] pixels = new int[] {
                0xff556208,
                0x00156742,
                0xE3030507,
                0xC3131719,
                0x89293137,
                0x41414347,
                0x09475157 };
        byte[] dest = new byte[3 * 7];

        ConverterUtils.convert_AXYZ_ints_to_XYZ_bytes(dest, 0, pixels, 0, 7);

        assertEquals(0x55, dest[0]);
        assertEquals(0x62, dest[1]);
        assertEquals(0x08, dest[2]);

        assertEquals(0x00, dest[3]);
        assertEquals(0x00, dest[4]);
        assertEquals(0x00, dest[5]);

        assertEquals(0x02, dest[6]);
        assertEquals(0x04, dest[7]);
        assertEquals(0x06, dest[8]);

        assertEquals(0x0E, dest[9]);
        assertEquals(0x11, dest[10]);
        assertEquals(0x13, dest[11]);

        assertEquals(0x16, dest[12]);
        assertEquals(0x1A, dest[13]);
        assertEquals(0x1D, dest[14]);

        assertEquals(0x10, dest[15]);
        assertEquals(0x11, dest[16]);
        assertEquals(0x12, dest[17]);

        assertEquals(0x02, dest[18]);
        assertEquals(0x02, dest[19]);
        assertEquals(0x03, dest[20]);
    }

    public void test_convert_AXYZ_bytes_to_XYZ_bytes() {
        byte[] pixels = new byte[] {
                -1, 0x55, 0x62, 0x08,
                0, 0x15, 0x67, 0x42,
                (byte) 0xE3, 0x03, 0x05, 0x07,
                (byte) 0xC3, 0x13, 0x17, 0x19,
                (byte) 0x89, 0x29, 0x31, 0x37,
                0x41, 0x41, 0x43, 0x47,
                0x09, 0x47, 0x51, 0x57,
                -1, -5, -51, -113,
                -10, -110, -104, -99 };

        ConverterUtils.convert_AXYZ_bytes_to_XYZ_bytes(pixels, 0, pixels, 0, 9);

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

        assertEquals(-5, pixels[21]);
        assertEquals(-51, pixels[22]);
        assertEquals(-113, pixels[23]);

        assertEquals(-116, pixels[24]);
        assertEquals(-110, pixels[25]);
        assertEquals(-105, pixels[26]);
    }

    public void test_convert_AXYZ_ints_to_ZYX_bytes() {
        int[] pixels = new int[] {
                0xff556208,
                0x00156742,
                0xE3030507,
                0xC3131719,
                0x89293137,
                0x41414347,
                0x09475157 };
        byte[] dest = new byte[3 * 7];

        ConverterUtils.convert_AXYZ_ints_to_ZYX_bytes(dest, 0, pixels, 0, 7);

        assertEquals(0x08, dest[0]);
        assertEquals(0x62, dest[1]);
        assertEquals(0x55, dest[2]);

        assertEquals(0x00, dest[3]);
        assertEquals(0x00, dest[4]);
        assertEquals(0x00, dest[5]);

        assertEquals(0x06, dest[6]);
        assertEquals(0x04, dest[7]);
        assertEquals(0x02, dest[8]);

        assertEquals(0x13, dest[9]);
        assertEquals(0x11, dest[10]);
        assertEquals(0x0E, dest[11]);

        assertEquals(0x1D, dest[12]);
        assertEquals(0x1A, dest[13]);
        assertEquals(0x16, dest[14]);

        assertEquals(0x12, dest[15]);
        assertEquals(0x11, dest[16]);
        assertEquals(0x10, dest[17]);

        assertEquals(0x03, dest[18]);
        assertEquals(0x02, dest[19]);
        assertEquals(0x02, dest[20]);
    }

    public void test_convert_AXYZ_bytes_to_ZYX_bytes() {
        byte[] pixels = new byte[] {
                -1, 0x55, 0x62, 0x08,
                0, 0x15, 0x67, 0x42,
                (byte) 0xE3, 0x03, 0x05, 0x07,
                (byte) 0xC3, 0x13, 0x17, 0x19,
                (byte) 0x89, 0x29, 0x31, 0x37,
                0x41, 0x41, 0x43, 0x47,
                0x09, 0x47, 0x51, 0x57,
                -1, -5, -51, -113,
                -10, -110, -104, -99 };

        ConverterUtils.convert_AXYZ_bytes_to_ZYX_bytes(pixels, 0, pixels, 0, 9);

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

        assertEquals(-113, pixels[21]);
        assertEquals(-51, pixels[22]);
        assertEquals(-5, pixels[23]);

        assertEquals(-105, pixels[24]);
        assertEquals(-110, pixels[25]);
        assertEquals(-116, pixels[26]);
    }

    public void test_convert_XYZA_bytes_to_XYZ_bytes() {
        byte[] pixels = new byte[] {
                0x55, 0x62, 0x08, (byte) 0xff,
                0x15, 0x67, 0x42, 0x00,
                0x03, 0x05, 0x07, (byte) 0xE3,
                0x13, 0x17, 0x19, (byte) 0xC3,
                0x29, 0x31, 0x37, (byte) 0x89,
                0x41, 0x43, 0x47, 0x41,
                0x47, 0x51, 0x57, 0x09,
                -5, -51, -113, -1,
                -110, -104, -99, -10 };

        ConverterUtils.convert_XYZA_bytes_to_XYZ_bytes(pixels, 0, pixels, 0, 9);

        assertEquals(0x55, pixels[0]);
        assertEquals(0x62, pixels[1]);
        assertEquals(0x08, pixels[2]);

        assertEquals(0x00, pixels[3]);
        assertEquals(0x00, pixels[4]);
        assertEquals(0x00, pixels[5]);

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

        assertEquals(-5, pixels[21]);
        assertEquals(-51, pixels[22]);
        assertEquals(-113, pixels[23]);

        assertEquals(-116, pixels[24]);
        assertEquals(-110, pixels[25]);
        assertEquals(-105, pixels[26]);
    }

    public void test_convert_XYZA_bytes_to_ZYX_bytes() {
        byte[] pixels = new byte[] {
                0x55, 0x62, 0x08, (byte) 0xff,
                0x15, 0x67, 0x42, 0x00,
                0x03, 0x05, 0x07, (byte) 0xE3,
                0x13, 0x17, 0x19, (byte) 0xC3,
                0x29, 0x31, 0x37, (byte) 0x89,
                0x41, 0x43, 0x47, 0x41,
                0x47, 0x51, 0x57, 0x09 };

        ConverterUtils.convert_XYZA_bytes_to_ZYX_bytes(pixels, 0, pixels, 0, 7);

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

    public void test_convert_AXYZ_ints_to_XYZ_ints() {
        int[] pixels = new int[] {
                0xff556208,
                0x00156742,
                0xE3030507,
                0xC3131719,
                0x89293137,
                0x41414347,
                0x09475157 };

        ConverterUtils.convert_AXYZ_ints_to_XYZ_ints(pixels, 0, pixels, 0, 7);

        assertEquals(Integer.toUnsignedString(pixels[0], 16),0x556208, pixels[0]);
        assertEquals(Integer.toUnsignedString(pixels[1], 16),0x000000, pixels[1]);
        assertEquals(Integer.toUnsignedString(pixels[2], 16),0x020406, pixels[2]);
        assertEquals(Integer.toUnsignedString(pixels[3], 16),0x0E1113, pixels[3]);
        assertEquals(Integer.toUnsignedString(pixels[4], 16),0x161A1D, pixels[4]);
        assertEquals(Integer.toUnsignedString(pixels[5], 16),0x101112, pixels[5]);
        assertEquals(Integer.toUnsignedString(pixels[6], 16),0x020203, pixels[6]);
    }

    public void test_convert_AXYZ_ints_to_ZYX_ints() {
        int[] pixels = new int[] {
                0xff556208,
                0x00156742,
                0xE3030507,
                0xC3131719,
                0x89293137,
                0x41414347,
                0x09475157 };

        ConverterUtils.convert_AXYZ_ints_to_ZYX_ints(pixels, 0, pixels, 0, 7);

        assertEquals(Integer.toUnsignedString(pixels[0], 16),0x086255, pixels[0]);
        assertEquals(Integer.toUnsignedString(pixels[1], 16),0x000000, pixels[1]);
        assertEquals(Integer.toUnsignedString(pixels[2], 16),0x060402, pixels[2]);
        assertEquals(Integer.toUnsignedString(pixels[3], 16),0x13110E, pixels[3]);
        assertEquals(Integer.toUnsignedString(pixels[4], 16),0x1D1A16, pixels[4]);
        assertEquals(Integer.toUnsignedString(pixels[5], 16),0x121110, pixels[5]);
        assertEquals(Integer.toUnsignedString(pixels[6], 16),0x030202, pixels[6]);
    }

    public void test_convert_AXYZ_bytes_to_XYZ_ints() {
        byte[] pixels = new byte[] {
                -1, 0x55, 0x62, 0x08,
                0, 0x15, 0x67, 0x42,
                (byte) 0xE3,0x03, 0x05, 0x07,
                (byte) 0xC3, 0x13, 0x17, 0x19,
                (byte) 0x89, 0x29, 0x31, 0x37,
                0x41, 0x41, 0x43, 0x47,
                0x09, 0x47, 0x51, 0x57,
                -1, -5, -51, -113,
                -10, -110, -104, -99 };

        int[] dest = new int[9];
        ConverterUtils.convert_AXYZ_bytes_to_XYZ_ints(dest, 0, pixels, 0, 9);

        assertEquals(Integer.toUnsignedString(dest[0], 16),0x556208, dest[0]);
        assertEquals(Integer.toUnsignedString(dest[1], 16),0, dest[1]);
        assertEquals(Integer.toUnsignedString(dest[2], 16),0x020406, dest[2]);
        assertEquals(Integer.toUnsignedString(dest[3], 16),0x0E1113, dest[3]);
        assertEquals(Integer.toUnsignedString(dest[4], 16),0x161a1d, dest[4]);
        assertEquals(Integer.toUnsignedString(dest[5], 16),0x101112, dest[5]);
        assertEquals(Integer.toUnsignedString(dest[6], 16),0x020203, dest[6]);
        assertEquals(Integer.toUnsignedString(dest[7], 16),0xfbcd8f, dest[7]);
        assertEquals(Integer.toUnsignedString(dest[8], 16),0x8c9297, dest[8]);
    }

    public void test_convert_AXYZ_bytes_to_ZYX_ints() {
        byte[] pixels = new byte[] {
                -1, 0x55, 0x62, 0x08,
                0, 0x15, 0x67, 0x42,
                (byte) 0xE3,0x03, 0x05, 0x07,
                (byte) 0xC3, 0x13, 0x17, 0x19,
                (byte) 0x89, 0x29, 0x31, 0x37,
                0x41, 0x41, 0x43, 0x47,
                0x09, 0x47, 0x51, 0x57,
                -1, -5, -51, -113,
                -10, -110, -104, -99 };

        int[] dest = new int[9];
        ConverterUtils.convert_AXYZ_bytes_to_ZYX_ints(dest, 0, pixels, 0, 9);

        assertEquals(Integer.toUnsignedString(dest[0], 16),0x086255, dest[0]);
        assertEquals(Integer.toUnsignedString(dest[1], 16),0x000000, dest[1]);
        assertEquals(Integer.toUnsignedString(dest[2], 16),0x060402, dest[2]);
        assertEquals(Integer.toUnsignedString(dest[3], 16),0x13110E, dest[3]);
        assertEquals(Integer.toUnsignedString(dest[4], 16),0x1D1A16, dest[4]);
        assertEquals(Integer.toUnsignedString(dest[5], 16),0x121110, dest[5]);
        assertEquals(Integer.toUnsignedString(dest[6], 16),0x030202, dest[6]);
        assertEquals(Integer.toUnsignedString(dest[7], 16),0x8fcdfb, dest[7]);
        assertEquals(Integer.toUnsignedString(dest[8], 16),0x97928c, dest[8]);
    }

    public void test_convert_XYZA_bytes_to_XYZ_ints() {
        byte[] pixels = new byte[] {
                0x55, 0x62, 0x08, (byte) 0xff,
                0x15, 0x67, 0x42, 0x00,
                0x03, 0x05, 0x07, (byte) 0xE3,
                0x13, 0x17, 0x19, (byte) 0xC3,
                0x29, 0x31, 0x37, (byte) 0x89,
                0x41, 0x43, 0x47, 0x41,
                0x47, 0x51, 0x57, 0x09,
                -5, -51, -113, -1,
                -110, -104, -99, -10 };

        int[] dest = new int[9];
        ConverterUtils.convert_XYZA_bytes_to_XYZ_ints(dest, 0, pixels, 0, 9);

        assertEquals(Integer.toUnsignedString(dest[0], 16),0x556208, dest[0]);
        assertEquals(Integer.toUnsignedString(dest[1], 16),0x000000, dest[1]);
        assertEquals(Integer.toUnsignedString(dest[2], 16),0x020406, dest[2]);
        assertEquals(Integer.toUnsignedString(dest[3], 16),0x0E1113, dest[3]);
        assertEquals(Integer.toUnsignedString(dest[4], 16),0x161A1D, dest[4]);
        assertEquals(Integer.toUnsignedString(dest[5], 16),0x101112, dest[5]);
        assertEquals(Integer.toUnsignedString(dest[6], 16),0x020203, dest[6]);
        assertEquals(Integer.toUnsignedString(dest[7], 16),0xfbcd8f, dest[7]);
        assertEquals(Integer.toUnsignedString(dest[8], 16),0x8c9297, dest[8]);
    }

    public void test_convert_XYZA_bytes_to_ZYX_ints() {
        byte[] pixels = new byte[] {
                0x55, 0x62, 0x08, (byte) 0xff,
                0x15, 0x67, 0x42, 0x00,
                0x03, 0x05, 0x07, (byte) 0xE3,
                0x13, 0x17, 0x19, (byte) 0xC3,
                0x29, 0x31, 0x37, (byte) 0x89,
                0x41, 0x43, 0x47, 0x41,
                0x47, 0x51, 0x57, 0x09,
                -5, -51, -113, -1,
                -110, -104, -99, -10 };

        int[] dest = new int[9];
        ConverterUtils.convert_XYZA_bytes_to_ZYX_ints(dest, 0, pixels, 0, 9);

        assertEquals(Integer.toUnsignedString(dest[0], 16),0x086255, dest[0]);
        assertEquals(Integer.toUnsignedString(dest[1], 16),0x000000, dest[1]);
        assertEquals(Integer.toUnsignedString(dest[2], 16),0x060402, dest[2]);
        assertEquals(Integer.toUnsignedString(dest[3], 16),0x13110E, dest[3]);
        assertEquals(Integer.toUnsignedString(dest[4], 16),0x1D1A16, dest[4]);
        assertEquals(Integer.toUnsignedString(dest[5], 16),0x121110, dest[5]);
        assertEquals(Integer.toUnsignedString(dest[6], 16),0x030202, dest[6]);
        assertEquals(Integer.toUnsignedString(dest[7], 16),0x8fcdfb, dest[7]);
        assertEquals(Integer.toUnsignedString(dest[8], 16),0x97928c, dest[8]);
    }

    public void test_convert_AXYZPre_bytes_to_ZYX_bytes() {
        byte[] pixels = new byte[] {
                (byte) 0xff, 0x55, 0x62, 0x08,
                0x00, 0x15, 0x67, 0x42,
                (byte) 0xE3, 0x06, 0x04, 0x02,
                (byte) 0xC3, 0x13, 0x11, 0x0E,
                (byte) 0x89, 0x1D, 0x1A, 0x15,
                (byte) 0x41, 0x12, 0x11, 0x10,
                (byte) 0x09, 0x03, 0x02, 0x02,
                -1, -5, -51, -113,
                -10, -110, -104, -99
        };
        ConverterUtils.convert_AXYZPre_bytes_to_ZYX_bytes(pixels, 0, pixels, 0, 9);

        assertEquals(0x08, pixels[0]);
        assertEquals(0x62, pixels[1]);
        assertEquals(0x55, pixels[2]);

        assertEquals(0x42, pixels[3]);
        assertEquals(0x67, pixels[4]);
        assertEquals(0x15, pixels[5]);

        assertEquals(0x02, pixels[6]);
        assertEquals(0x04, pixels[7]);
        assertEquals(0x06, pixels[8]);

        assertEquals(0x0E, pixels[9]);
        assertEquals(0x11, pixels[10]);
        assertEquals(0x13, pixels[11]);

        assertEquals(0x15, pixels[12]);
        assertEquals(0x1A, pixels[13]);
        assertEquals(0x1D, pixels[14]);

        assertEquals(0x10, pixels[15]);
        assertEquals(0x11, pixels[16]);
        assertEquals(0x12, pixels[17]);

        assertEquals(0x02, pixels[18]);
        assertEquals(0x02, pixels[19]);
        assertEquals(0x03, pixels[20]);

        assertEquals(-113, pixels[21]);
        assertEquals(-51, pixels[22]);
        assertEquals(-5, pixels[23]);

        assertEquals(-99, pixels[24]);
        assertEquals(-104, pixels[25]);
        assertEquals(-110, pixels[26]);
    }

    public void test_convert_XYZAPre_bytes_to_ZYX_bytes() {
        byte[] pixels = new byte[] {
                0x55, 0x62, 0x08, (byte) 0xff,
                0x15, 0x67, 0x42, 0x00,
                0x06, 0x04, 0x02, (byte) 0xE3,
                0x13, 0x11, 0x0E, (byte) 0xC3,
                0x1D, 0x1A, 0x15, (byte) 0x89,
                0x12, 0x11, 0x10, (byte) 0x41,
                0x03, 0x02, 0x02, (byte) 0x09,
                -5, -51, -113, -1,
                -110, -104, -99, -10
        };
        ConverterUtils.convert_XYZAPre_bytes_to_ZYX_bytes(pixels, 0, pixels, 0, 9);

        assertEquals(0x08, pixels[0]);
        assertEquals(0x62, pixels[1]);
        assertEquals(0x55, pixels[2]);

        assertEquals(0x42, pixels[3]);
        assertEquals(0x67, pixels[4]);
        assertEquals(0x15, pixels[5]);

        assertEquals(0x02, pixels[6]);
        assertEquals(0x04, pixels[7]);
        assertEquals(0x06, pixels[8]);

        assertEquals(0x0E, pixels[9]);
        assertEquals(0x11, pixels[10]);
        assertEquals(0x13, pixels[11]);

        assertEquals(0x15, pixels[12]);
        assertEquals(0x1A, pixels[13]);
        assertEquals(0x1D, pixels[14]);

        assertEquals(0x10, pixels[15]);
        assertEquals(0x11, pixels[16]);
        assertEquals(0x12, pixels[17]);

        assertEquals(0x02, pixels[18]);
        assertEquals(0x02, pixels[19]);
        assertEquals(0x03, pixels[20]);

        assertEquals(-113, pixels[21]);
        assertEquals(-51, pixels[22]);
        assertEquals(-5, pixels[23]);

        assertEquals(-99, pixels[24]);
        assertEquals(-104, pixels[25]);
        assertEquals(-110, pixels[26]);
    }

    public void test_convert_AXYZPre_bytes_to_XYZ_bytes() {
        byte[] pixels = new byte[] {
                (byte) 0xff, 0x55, 0x62, 0x08,
                0x00, 0x15, 0x67, 0x42,
                (byte) 0xE3, 0x06, 0x04, 0x02,
                (byte) 0xC3, 0x13, 0x11, 0x0E,
                (byte) 0x89, 0x1D, 0x1A, 0x15,
                (byte) 0x41, 0x12, 0x11, 0x10,
                (byte) 0x09, 0x03, 0x02, 0x02,
                -1, -5, -51, -113,
                -10, -110, -104, -99
        };
        ConverterUtils.convert_AXYZPre_bytes_to_XYZ_bytes(pixels, 0, pixels, 0, 9);

        assertEquals(0x55, pixels[0]);
        assertEquals(0x62, pixels[1]);
        assertEquals(0x08, pixels[2]);

        // in most tests we expect these to be zero, but for this test we'll make an exception.
        // Note: this is bad (corrupt) incoming data. A red channel premultiplied by zero should be
        // zero. Any non-zero value is already breaking the rules.
        assertEquals(0x15, pixels[3]);
        assertEquals(0x67, pixels[4]);
        assertEquals(0x42, pixels[5]);

        assertEquals(0x06, pixels[6]);
        assertEquals(0x04, pixels[7]);
        assertEquals(0x02, pixels[8]);

        assertEquals(0x13, pixels[9]);
        assertEquals(0x11, pixels[10]);
        assertEquals(0x0E, pixels[11]);

        assertEquals(0x1D, pixels[12]);
        assertEquals(0x1A, pixels[13]);
        assertEquals(0x15, pixels[14]);

        assertEquals(0x12, pixels[15]);
        assertEquals(0x11, pixels[16]);
        assertEquals(0x10, pixels[17]);

        assertEquals(0x03, pixels[18]);
        assertEquals(0x02, pixels[19]);
        assertEquals(0x02, pixels[20]);

        assertEquals(-5, pixels[21]);
        assertEquals(-51, pixels[22]);
        assertEquals(-113, pixels[23]);

        assertEquals(-110, pixels[24]);
        assertEquals(-104, pixels[25]);
        assertEquals(-99, pixels[26]);
    }

    public void test_convert_XYZAPre_bytes_to_XYZ_bytes() {
        byte[] pixels = new byte[] {
                0x55, 0x62, 0x08, (byte) 0xff,
                0x15, 0x67, 0x42, 0x00,
                0x06, 0x04, 0x02, (byte) 0xE3,
                0x13, 0x11, 0x0E, (byte) 0xC3,
                0x1D, 0x1A, 0x15, (byte) 0x89,
                0x12, 0x11, 0x10, (byte) 0x41,
                0x03, 0x02, 0x02, (byte) 0x09,
                -5, -51, -113, -1,
                -110, -104, -99, -10
        };
        ConverterUtils.convert_XYZAPre_bytes_to_XYZ_bytes(pixels, 0, pixels, 0, 9);

        assertEquals(0x55, pixels[0]);
        assertEquals(0x62, pixels[1]);
        assertEquals(0x08, pixels[2]);

        // in most tests we expect these to be zero, but for this test we'll make an exception.
        // Note: this is bad (corrupt) incoming data. A red channel premultiplied by zero should be
        // zero. Any non-zero value is already breaking the rules.
        assertEquals(0x15, pixels[3]);
        assertEquals(0x67, pixels[4]);
        assertEquals(0x42, pixels[5]);

        assertEquals(0x06, pixels[6]);
        assertEquals(0x04, pixels[7]);
        assertEquals(0x02, pixels[8]);

        assertEquals(0x13, pixels[9]);
        assertEquals(0x11, pixels[10]);
        assertEquals(0x0E, pixels[11]);

        assertEquals(0x1D, pixels[12]);
        assertEquals(0x1A, pixels[13]);
        assertEquals(0x15, pixels[14]);

        assertEquals(0x12, pixels[15]);
        assertEquals(0x11, pixels[16]);
        assertEquals(0x10, pixels[17]);

        assertEquals(0x03, pixels[18]);
        assertEquals(0x02, pixels[19]);
        assertEquals(0x02, pixels[20]);

        assertEquals(-5, pixels[21]);
        assertEquals(-51, pixels[22]);
        assertEquals(-113, pixels[23]);

        assertEquals(-110, pixels[24]);
        assertEquals(-104, pixels[25]);
        assertEquals(-99, pixels[26]);
    }

    public void test_convert_AXYZPre_ints_to_AZYX_bytes() {
        int[] pixels = new int[] {
                0xff556208,
                0x00156742,
                0xE3060402,
                0xC313110E,
                0x891D1A15,
                0x41121110,
                0x09030202 };
        byte[] dest = new byte[7 * 4];
        ConverterUtils.convert_AXYZPre_ints_to_AZYX_bytes(dest, 0, pixels, 0, 7);

        assertEquals((byte) 0xff, dest[0]);
        assertEquals(0x08, dest[1]);
        assertEquals(0x62, dest[2]);
        assertEquals(0x55, dest[3]);

        assertEquals(0x00, dest[4]);
        assertEquals(0x00, dest[5]);
        assertEquals(0x00, dest[6]);
        assertEquals(0x00, dest[7]);

        assertEquals((byte) 0xE3, dest[8]);
        assertEquals(0x02, dest[9]);
        assertEquals(0x04, dest[10]);
        assertEquals(0x06, dest[11]);

        assertEquals((byte) 0xC3, dest[12]);
        assertEquals(0x12, dest[13]);
        assertEquals(0x16, dest[14]);
        assertEquals(0x18, dest[15]);

        assertEquals((byte) 0x89, dest[16]);
        assertEquals(0x27, dest[17]);
        assertEquals(0x30, dest[18]);
        assertEquals(0x35, dest[19]);

        assertEquals(0x41, dest[20]);
        assertEquals(0x3E, dest[21]);
        assertEquals(0x42, dest[22]);
        assertEquals(0x46, dest[23]);

        assertEquals(0x09, dest[24]);
        assertEquals(0x38, dest[25]);
        assertEquals(0x38, dest[26]);
        assertEquals(0x55, dest[27]);
    }

    public void test_convert_AXYZPre_ints_to_AXYZ_bytes() {
        int[] pixels = new int[] {
                0xff556208,
                0x00156742,
                0xE3060402,
                0xC313110E,
                0x891D1A15,
                0x41121110,
                0x09030202 };
        byte[] dest = new byte[7 * 4];
        ConverterUtils.convert_AXYZPre_ints_to_AXYZ_bytes(dest, 0, pixels, 0, 7);

        assertEquals((byte) 0xff, dest[0]);
        assertEquals(0x55, dest[1]);
        assertEquals(0x62, dest[2]);
        assertEquals(0x08, dest[3]);

        assertEquals(0x00, dest[4]);
        assertEquals(0x00, dest[5]);
        assertEquals(0x00, dest[6]);
        assertEquals(0x00, dest[7]);

        assertEquals((byte) 0xE3, dest[8]);
        assertEquals(0x06, dest[9]);
        assertEquals(0x04, dest[10]);
        assertEquals(0x02, dest[11]);

        assertEquals((byte) 0xC3, dest[12]);
        assertEquals(0x18, dest[13]);
        assertEquals(0x16, dest[14]);
        assertEquals(0x12, dest[15]);

        assertEquals((byte) 0x89, dest[16]);
        assertEquals(0x35, dest[17]);
        assertEquals(0x30, dest[18]);
        assertEquals(0x27, dest[19]);

        assertEquals(0x41, dest[20]);
        assertEquals(0x46, dest[21]);
        assertEquals(0x42, dest[22]);
        assertEquals(0x3E, dest[23]);

        assertEquals(0x09, dest[24]);
        assertEquals(0x55, dest[25]);
        assertEquals(0x38, dest[26]);
        assertEquals(0x38, dest[27]);
    }

    public void test_convert_AXYZPre_ints_to_XYZA_bytes() {
        int[] pixels = new int[] {
                0xff556208,
                0x00156742,
                0xE3060402,
                0xC313110E,
                0x891D1A15,
                0x41121110,
                0x09030202 };
        byte[] dest = new byte[7 * 4];
        ConverterUtils.convert_AXYZPre_ints_to_XYZA_bytes(dest, 0, pixels, 0, 7);

        assertEquals(0x55, dest[0]);
        assertEquals(0x62, dest[1]);
        assertEquals(0x08, dest[2]);
        assertEquals((byte) 0xff, dest[3]);

        assertEquals(0x00, dest[4]);
        assertEquals(0x00, dest[5]);
        assertEquals(0x00, dest[6]);
        assertEquals(0x00, dest[7]);

        assertEquals(0x06, dest[8]);
        assertEquals(0x04, dest[9]);
        assertEquals(0x02, dest[10]);
        assertEquals((byte) 0xE3, dest[11]);

        assertEquals(0x18, dest[12]);
        assertEquals(0x16, dest[13]);
        assertEquals(0x12, dest[14]);
        assertEquals((byte) 0xC3, dest[15]);

        assertEquals(0x35, dest[16]);
        assertEquals(0x30, dest[17]);
        assertEquals(0x27, dest[18]);
        assertEquals((byte) 0x89, dest[19]);

        assertEquals(0x46, dest[20]);
        assertEquals(0x42, dest[21]);
        assertEquals(0x3E, dest[22]);
        assertEquals(0x41, dest[23]);

        assertEquals(0x55, dest[24]);
        assertEquals(0x38, dest[25]);
        assertEquals(0x38, dest[26]);
        assertEquals(0x09, dest[27]);
    }

    public void test_convert_AXYZPre_ints_to_ZYXA_bytes() {
        int[] pixels = new int[] {
                0xff556208,
                0x00156742,
                0xE3060402,
                0xC313110E,
                0x891D1A15,
                0x41121110,
                0x09030202 };
        byte[] dest = new byte[7 * 4];
        ConverterUtils.convert_AXYZPre_ints_to_ZYXA_bytes(dest, 0, pixels, 0, 7);

        assertEquals(0x08, dest[0]);
        assertEquals(0x62, dest[1]);
        assertEquals(0x55, dest[2]);
        assertEquals((byte) 0xff, dest[3]);

        assertEquals(0, dest[4]);
        assertEquals(0, dest[5]);
        assertEquals(0, dest[6]);
        assertEquals(0, dest[7]);

        assertEquals(0x02, dest[8]);
        assertEquals(0x04, dest[9]);
        assertEquals(0x06, dest[10]);
        assertEquals((byte) 0xE3, dest[11]);

        assertEquals(0x12, dest[12]);
        assertEquals(0x16, dest[13]);
        assertEquals(0x18, dest[14]);
        assertEquals((byte) 0xC3, dest[15]);

        assertEquals(0x27, dest[16]);
        assertEquals(0x30, dest[17]);
        assertEquals(0x35, dest[18]);
        assertEquals((byte) 0x89, dest[19]);

        assertEquals(0x3E, dest[20]);
        assertEquals(0x42, dest[21]);
        assertEquals(0x46, dest[22]);
        assertEquals(0x41, dest[23]);

        assertEquals(0x38, dest[24]);
        assertEquals(0x38, dest[25]);
        assertEquals(0x55, dest[26]);
        assertEquals(0x09, dest[27]);
    }

    public void test_convert_AXYZ_ints_to_AZYXPre_bytes() {
        int[] pixels = new int[] {
                0xff556208,
                0x00156742,
                0xE3060402,
                0xC3181612,
                0x89363027,
                0x4146423F,
                0x09553838};
        byte[] dest = new byte[7 * 4];
        ConverterUtils.convert_AXYZ_ints_to_AZYXPre_bytes(dest, 0, pixels, 0, 7);

        assertEquals((byte) 0xff, dest[0]);
        assertEquals(0x08, dest[1]);
        assertEquals(0x62, dest[2]);
        assertEquals(0x55, dest[3]);

        assertEquals(0, dest[4]);
        assertEquals(0, dest[5]);
        assertEquals(0, dest[6]);
        assertEquals(0, dest[7]);

        assertEquals((byte) 0xE3, dest[8]);
        assertEquals(0x01, dest[9]);
        assertEquals(0x03, dest[10]);
        assertEquals(0x05, dest[11]);

        assertEquals((byte) 0xC3, dest[12]);
        assertEquals(0x0D, dest[13]);
        assertEquals(0x10, dest[14]);
        assertEquals(0x12, dest[15]);

        assertEquals((byte)  0x89, dest[16]);
        assertEquals(0x14, dest[17]);
        assertEquals(0x19, dest[18]);
        assertEquals(0x1D, dest[19]);

        assertEquals(0x41, dest[20]);
        assertEquals(0x10, dest[21]);
        assertEquals(0x10, dest[22]);
        assertEquals(0x11, dest[23]);

        assertEquals(0x09, dest[24]);
        assertEquals(0x01, dest[25]);
        assertEquals(0x01, dest[26]);
        assertEquals(0x03, dest[27]);
    }

    public void test_convert_AXYZ_ints_to_AXYZPre_bytes() {
        int[] pixels = new int[] {
                0xff556208,
                0x00156742,
                0xE3060402, 0xC3181612, 0x89363027, 0x4146423F, 0x09553838};
        byte[] dest = new byte[7 * 4];
        ConverterUtils.convert_AXYZ_ints_to_AXYZPre_bytes(dest, 0, pixels, 0, 7);

        assertEquals( -1, dest[0]);
        assertEquals(0x55, dest[1]);
        assertEquals(0x62, dest[2]);
        assertEquals(0x08, dest[3]);

        assertEquals( 0, dest[4]);
        assertEquals(0, dest[5]);
        assertEquals(0, dest[6]);
        assertEquals(0, dest[7]);

        assertEquals((byte) 0xE3, dest[8]);
        assertEquals(0x05, dest[9]);
        assertEquals(0x03, dest[10]);
        assertEquals(0x01, dest[11]);

        assertEquals((byte) 0xC3, dest[12]);
        assertEquals(0x12, dest[13]);
        assertEquals(0x10, dest[14]);
        assertEquals(0x0D, dest[15]);

        assertEquals((byte)  0x89, dest[16]);
        assertEquals(0x1D, dest[17]);
        assertEquals(0x19, dest[18]);
        assertEquals(0x14, dest[19]);

        assertEquals(0x41, dest[20]);
        assertEquals(0x11, dest[21]);
        assertEquals(0x10, dest[22]);
        assertEquals(0x10, dest[23]);

        assertEquals(0x09, dest[24]);
        assertEquals(0x03, dest[25]);
        assertEquals(0x01, dest[26]);
        assertEquals(0x01, dest[27]);
    }

    public void test_convert_AXYZ_ints_to_XYZAPre_bytes() {
        int[] pixels = new int[] {
                0xff556208,
                0x00156742,
                0xE3060402, 0xC3181612, 0x89363027, 0x4146423F, 0x09553838};
        byte[] dest = new byte[7 * 4];
        ConverterUtils.convert_AXYZ_ints_to_XYZAPre_bytes(dest, 0, pixels, 0, 7);

        assertEquals(0x55, dest[0]);
        assertEquals(0x62, dest[1]);
        assertEquals(0x08, dest[2]);
        assertEquals( -1, dest[3]);

        assertEquals(0, dest[4]);
        assertEquals(0, dest[5]);
        assertEquals(0, dest[6]);
        assertEquals( 0, dest[7]);

        assertEquals(0x05, dest[8]);
        assertEquals(0x03, dest[9]);
        assertEquals(0x01, dest[10]);
        assertEquals((byte) 0xE3, dest[11]);

        assertEquals(0x12, dest[12]);
        assertEquals(0x10, dest[13]);
        assertEquals(0x0D, dest[14]);
        assertEquals((byte) 0xC3, dest[15]);

        assertEquals(0x1D, dest[16]);
        assertEquals(0x19, dest[17]);
        assertEquals(0x14, dest[18]);
        assertEquals((byte)  0x89, dest[19]);

        assertEquals(0x11, dest[20]);
        assertEquals(0x10, dest[21]);
        assertEquals(0x10, dest[22]);
        assertEquals(0x41, dest[23]);

        assertEquals(0x03, dest[24]);
        assertEquals(0x01, dest[25]);
        assertEquals(0x01, dest[26]);
        assertEquals(0x09, dest[27]);
    }

    public void test_convert_AXYZPre_bytes_to_AXYZ_ints() {
        byte[] pixels = new byte[] {
                (byte) 0xff, 0x55, 0x62, 0x08,
                0x00, 0x15, 0x67, 0x42,
                (byte) 0xE3, 0x05, 0x03, 0x01,
                (byte) 0xC3, 0x12, 0x10, 0x0D,
                (byte) 0x89, 0x1C, 0x19, 0x14,
                0x41, 0x11, 0x10, 0x0F,
                0x09, 0x02, 0x01, 0x01,
                -1, -5, -51, -113,
                -10, -110, -104, -99
        };

        int[] dest = new int[9];
        ConverterUtils.convert_AXYZPre_bytes_to_AXYZ_ints(dest, 0, pixels, 0, 9);

        assertEquals(Integer.toUnsignedString(dest[0], 16), 0xff556208, dest[0]);
        assertEquals(Integer.toUnsignedString(dest[1], 16), 0x00000000, dest[1]);
        assertEquals(Integer.toUnsignedString(dest[2], 16), 0xe3050301, dest[2]);
        assertEquals(Integer.toUnsignedString(dest[3], 16), 0xc3171411, dest[3]);
        assertEquals(Integer.toUnsignedString(dest[4], 16), 0x89342E25, dest[4]);
        assertEquals(Integer.toUnsignedString(dest[5], 16), 0x41423E3A, dest[5]);
        assertEquals(Integer.toUnsignedString(dest[6], 16), 0x09381C1C, dest[6]);
        assertEquals(Integer.toUnsignedString(dest[7], 16), 0xfffbcd8f, dest[7]);
        assertEquals(Integer.toUnsignedString(dest[8], 16), 0xf6979da2, dest[8]);
    }

    public void test_convert_XYZAPre_bytes_to_AXYZ_ints() {
        byte[] pixels = new byte[] {
                0x55, 0x62, 0x08, (byte) 0xff,
                0x15, 0x67, 0x42, 0x00,
                0x05, 0x03, 0x01, (byte) 0xE3,
                0x12, 0x10, 0x0D, (byte) 0xC3,
                0x1C, 0x19, 0x14, (byte) 0x89,
                0x11, 0x10, 0x0F, 0x41,
                0x02, 0x01, 0x01, 0x09,
                -5, -51, -113, -1,
                -110, -104, -99, -10
        };

        int[] dest = new int[9];
        ConverterUtils.convert_XYZAPre_bytes_to_AXYZ_ints(dest, 0, pixels, 0, 9);

        assertEquals(Integer.toUnsignedString(dest[0], 16), 0xff556208, dest[0]);
        assertEquals(Integer.toUnsignedString(dest[1], 16), 0x00000000, dest[1]);
        assertEquals(Integer.toUnsignedString(dest[2], 16), 0xe3050301, dest[2]);
        assertEquals(Integer.toUnsignedString(dest[3], 16), 0xc3171411, dest[3]);
        assertEquals(Integer.toUnsignedString(dest[4], 16), 0x89342E25, dest[4]);
        assertEquals(Integer.toUnsignedString(dest[5], 16), 0x41423E3A, dest[5]);
        assertEquals(Integer.toUnsignedString(dest[6], 16), 0x09381C1C, dest[6]);
        assertEquals(Integer.toUnsignedString(dest[7], 16), 0xfffbcd8f, dest[7]);
        assertEquals(Integer.toUnsignedString(dest[8], 16), 0xf6979da2, dest[8]);
    }

    public void test_convert_AXYZPre_bytes_to_AZYX_ints() {
        byte[] pixels = new byte[] {
                (byte) 0xff, 0x55, 0x62, 0x08,
                0x00, 0x15, 0x67, 0x42,
                (byte) 0xE3, 0x05, 0x03, 0x01,
                (byte) 0xC3, 0x12, 0x10, 0x0D,
                (byte) 0x89, 0x1C, 0x19, 0x14,
                0x41, 0x11, 0x10, 0x0F,
                0x09, 0x02, 0x01, 0x01,
                -1, -5, -51, -113,
                -10, -110, -104, -99
        };

        int[] dest = new int[9];
        ConverterUtils.convert_AXYZPre_bytes_to_AZYX_ints(dest, 0, pixels, 0, 9);

        assertEquals(Integer.toUnsignedString(dest[0], 16), 0xff086255, dest[0]);
        assertEquals(Integer.toUnsignedString(dest[1], 16), 0x00000000, dest[1]);
        assertEquals(Integer.toUnsignedString(dest[2], 16), 0xe3010305, dest[2]);
        assertEquals(Integer.toUnsignedString(dest[3], 16), 0xc3111417, dest[3]);
        assertEquals(Integer.toUnsignedString(dest[4], 16), 0x89252E34, dest[4]);
        assertEquals(Integer.toUnsignedString(dest[5], 16), 0x413A3E42, dest[5]);
        assertEquals(Integer.toUnsignedString(dest[6], 16), 0x091C1C38, dest[6]);
        assertEquals(Integer.toUnsignedString(dest[7], 16), 0xff8fcdfb, dest[7]);
        assertEquals(Integer.toUnsignedString(dest[8], 16), 0xf6a29d97, dest[8]);
    }

    public void test_convert_AXYZ_bytes_to_AXYZPre_ints() {
        byte[] pixels = new byte[] {
                -1, 0x55, 0x62, 0x08,
                0, 0x15, 0x67, 0x42,
                (byte) 0xE3, 0x06, 0x04, 0x02,
                (byte) 0xC3, 0x13, 0x11, 0x0E,
                (byte) 0x89, 0x1D, 0x1A, 0x15,
                (byte) 0x41, 0x12, 0x11, 0x10,
                (byte) 0x09, 0x03, 0x02, 0x02,
                -1, -5, -51, -113,
                -10, -110, -104, -99
        };
        int[] dest = new int[9];
        ConverterUtils.convert_AXYZ_bytes_to_AXYZPre_ints(dest, 0, pixels, 0, 9);

        assertEquals(Integer.toUnsignedString(dest[0], 16), 0xff556208, dest[0]);
        assertEquals(Integer.toUnsignedString(dest[1], 16), 0x00000000, dest[1]);
        assertEquals(Integer.toUnsignedString(dest[2], 16), 0xE3050301, dest[2]);
        assertEquals(Integer.toUnsignedString(dest[3], 16), 0xC30E0D0A, dest[3]);
        assertEquals(Integer.toUnsignedString(dest[4], 16), 0x890F0D0B, dest[4]);
        assertEquals(Integer.toUnsignedString(dest[5], 16), 0x41040404, dest[5]);
        assertEquals(Integer.toUnsignedString(dest[6], 16), 0x09000000, dest[6]);
        assertEquals(Integer.toUnsignedString(dest[7], 16), 0xfffbcd8f, dest[7]);
        assertEquals(Integer.toUnsignedString(dest[8], 16), 0xf68c9297, dest[8]);
    }

    public void test_convert_AXYZ_bytes_to_AZYXPre_ints() {
        byte[] pixels = new byte[] {
                -1, 0x55, 0x62, 0x08,
                0, 0x15, 0x67, 0x42,
                (byte) 0xE3, 0x06, 0x04, 0x02,
                (byte) 0xC3, 0x13, 0x11, 0x0E,
                (byte) 0x89, 0x1D, 0x1A, 0x15,
                (byte) 0x41, 0x12, 0x11, 0x10,
                (byte) 0x09, 0x03, 0x02, 0x02,
                -1, -5, -51, -113,
                -10, -110, -104, -99
        };
        int[] dest = new int[9];
        ConverterUtils.convert_AXYZ_bytes_to_AZYXPre_ints(dest, 0, pixels, 0, 9);

        assertEquals(Integer.toUnsignedString(dest[0], 16), 0xff086255, dest[0]);
        assertEquals(Integer.toUnsignedString(dest[1], 16), 0x00000000, dest[1]);
        assertEquals(Integer.toUnsignedString(dest[2], 16), 0xE3010305, dest[2]);
        assertEquals(Integer.toUnsignedString(dest[3], 16), 0xC30A0D0E, dest[3]);
        assertEquals(Integer.toUnsignedString(dest[4], 16), 0x890B0D0F, dest[4]);
        assertEquals(Integer.toUnsignedString(dest[5], 16), 0x41040404, dest[5]);
        assertEquals(Integer.toUnsignedString(dest[6], 16), 0x09000000, dest[6]);
        assertEquals(Integer.toUnsignedString(dest[7], 16), 0xff8fcdfb, dest[7]);
        assertEquals(Integer.toUnsignedString(dest[8], 16), 0xf697928c, dest[8]);
    }

    public void test_convert_XYZA_bytes_to_AZYXPre_ints() {
        byte[] pixels = new byte[] {
                0x55, 0x62, 0x08, (byte) 0xff,
                0x15, 0x67, 0x42, 0x00,
                0x06, 0x04, 0x02, (byte) 0xE3,
                0x13, 0x11, 0x0E, (byte) 0xC3,
                0x1D, 0x1A, 0x15, (byte) 0x89,
                0x12, 0x11, 0x10, (byte) 0x41,
                0x03, 0x02, 0x02, (byte) 0x09,
                -5, -51, -113, -1,
                -110, -104, -99, -10
        };
        int[] dest = new int[9];
        ConverterUtils.convert_XYZA_bytes_to_AZYXPre_ints(dest, 0, pixels, 0, 9);

        assertEquals(Integer.toUnsignedString(dest[0], 16), 0xFF086255, dest[0]);
        assertEquals(Integer.toUnsignedString(dest[1], 16), 0x00000000, dest[1]);
        assertEquals(Integer.toUnsignedString(dest[2], 16), 0xE3010305, dest[2]);
        assertEquals(Integer.toUnsignedString(dest[3], 16), 0xC30A0D0E, dest[3]);
        assertEquals(Integer.toUnsignedString(dest[4], 16), 0x890B0D0F, dest[4]);
        assertEquals(Integer.toUnsignedString(dest[5], 16), 0x41040404, dest[5]);
        assertEquals(Integer.toUnsignedString(dest[6], 16), 0x09000000, dest[6]);
        assertEquals(Integer.toUnsignedString(dest[7], 16), 0xff8fcdfb, dest[7]);
        assertEquals(Integer.toUnsignedString(dest[8], 16), 0xf697928c, dest[8]);
    }

    public void test_convert_XYZA_bytes_to_AXYZPre_ints() {
        byte[] pixels = new byte[] {
                0x55, 0x62, 0x08, (byte) 0xff,
                0x15, 0x67, 0x42, 0x00,
                0x06, 0x04, 0x02, (byte) 0xE3,
                0x13, 0x11, 0x0E, (byte) 0xC3,
                0x1D, 0x1A, 0x15, (byte) 0x89,
                0x12, 0x11, 0x10, (byte) 0x41,
                0x03, 0x02, 0x02, (byte) 0x09,
                -5, -51, -113, -1,
                -110, -104, -99, -10
        };
        int[] dest = new int[9];
        ConverterUtils.convert_XYZA_bytes_to_AXYZPre_ints(dest, 0, pixels, 0, 9);

        assertEquals(Integer.toUnsignedString(dest[0], 16), 0xFF556208, dest[0]);
        assertEquals(Integer.toUnsignedString(dest[1], 16), 0x00000000, dest[1]);
        assertEquals(Integer.toUnsignedString(dest[2], 16), 0xE3050301, dest[2]);
        assertEquals(Integer.toUnsignedString(dest[3], 16), 0xC30E0D0A, dest[3]);
        assertEquals(Integer.toUnsignedString(dest[4], 16), 0x890F0D0B, dest[4]);
        assertEquals(Integer.toUnsignedString(dest[5], 16), 0x41040404, dest[5]);
        assertEquals(Integer.toUnsignedString(dest[6], 16), 0x09000000, dest[6]);
        assertEquals(Integer.toUnsignedString(dest[7], 16), 0xfffbcd8f, dest[7]);
        assertEquals(Integer.toUnsignedString(dest[8], 16), 0xf68c9297, dest[8]);
    }

    public void test_convert_AXYZPre_ints_to_AXYZ_ints() {
        int[] pixels = new int[] {
                0xff556208,
                0x00156742,
                0xE3050301,
                0xC312100D,
                0x891C1914,
                0x4111100F,
                0x09020101
        };

        ConverterUtils.convert_AXYZPre_ints_to_AXYZ_ints(pixels, 0, pixels, 0, 7);

        assertEquals(Integer.toUnsignedString(pixels[0], 16), 0xff556208, pixels[0]);
        assertEquals(Integer.toUnsignedString(pixels[1], 16), 0x00000000, pixels[1]);
        assertEquals(Integer.toUnsignedString(pixels[2], 16), 0xe3050301, pixels[2]);
        assertEquals(Integer.toUnsignedString(pixels[3], 16), 0xc3171411, pixels[3]);
        assertEquals(Integer.toUnsignedString(pixels[4], 16), 0x89342e25, pixels[4]);
        assertEquals(Integer.toUnsignedString(pixels[5], 16), 0x41423E3A, pixels[5]);
        assertEquals(Integer.toUnsignedString(pixels[6], 16), 0x09381c1c, pixels[6]);
    }

    public void test_convert_AXYZ_ints_to_AXYZPre_ints() {
        int[] pixels = new int[] {
                0xff556208,
                0x00156742,
                0xe3050301,
                0xc3171511,
                0x89342e25,
                0x41423f3b,
                0x09381c1c
        };

        ConverterUtils.convert_AXYZ_ints_to_AXYZPre_ints(pixels, 0, pixels, 0, 7);

        assertEquals(Integer.toUnsignedString(pixels[0], 16), 0xff556208, pixels[0]);
        assertEquals(Integer.toUnsignedString(pixels[1], 16), 0, pixels[1]);
        assertEquals(Integer.toUnsignedString(pixels[2], 16), 0xE3040200, pixels[2]);
        assertEquals(Integer.toUnsignedString(pixels[3], 16), 0xC311100D, pixels[3]);
        assertEquals(Integer.toUnsignedString(pixels[4], 16), 0x891B1813, pixels[4]);
        assertEquals(Integer.toUnsignedString(pixels[5], 16), 0x4110100F, pixels[5]);
        assertEquals(Integer.toUnsignedString(pixels[6], 16), 0x09010000, pixels[6]);
    }

    public void test_convert_AXYZPre_ints_to_ZYX_ints() {
        int[] pixels = new int[] {
                0xff556208,
                0x00156742,
                0xe3050301,
                0xc3171511,
                0x89342e25,
                0x41423f3b,
                0x09381c1c
        };

        ConverterUtils.convert_AXYZPre_ints_to_ZYX_ints(pixels, 0, pixels, 0, 7);

        assertEquals(Integer.toUnsignedString(pixels[0], 16), 0x086255, pixels[0]);
        assertEquals(Integer.toUnsignedString(pixels[1], 16), 0x426715, pixels[1]);
        assertEquals(Integer.toUnsignedString(pixels[2], 16), 0x010305, pixels[2]);
        assertEquals(Integer.toUnsignedString(pixels[3], 16), 0x111517, pixels[3]);
        assertEquals(Integer.toUnsignedString(pixels[4], 16), 0x252E34, pixels[4]);
        assertEquals(Integer.toUnsignedString(pixels[5], 16), 0x3B3F42, pixels[5]);
        assertEquals(Integer.toUnsignedString(pixels[6], 16), 0x1C1C38, pixels[6]);
    }

    public void test_convert_AXYZPre_ints_to_XYZ_ints() {
        int[] pixels = new int[] {
                0xff556208,
                0x00156742,
                0xe3050301,
                0xc3171511,
                0x89342e25,
                0x41423f3b,
                0x09381c1c
        };

        ConverterUtils.convert_AXYZPre_ints_to_XYZ_ints(pixels, 0, pixels, 0, 7);

        assertEquals(Integer.toUnsignedString(pixels[0], 16), 0x556208, pixels[0]);

        // this is bad input data (you can't have an alpha of zero and have non-zero RGB channels)
        assertEquals(Integer.toUnsignedString(pixels[1], 16), 0x156742, pixels[1]);

        assertEquals(Integer.toUnsignedString(pixels[2], 16), 0x050301, pixels[2]);
        assertEquals(Integer.toUnsignedString(pixels[3], 16), 0x171511, pixels[3]);
        assertEquals(Integer.toUnsignedString(pixels[4], 16), 0x342E25, pixels[4]);
        assertEquals(Integer.toUnsignedString(pixels[5], 16), 0x423F3B, pixels[5]);
        assertEquals(Integer.toUnsignedString(pixels[6], 16), 0x381C1C, pixels[6]);
    }

    public void test_convert_AXYZPre_bytes_to_XYZ_ints() {
        byte[] pixels = new byte[] {
                (byte) 0xff, 0x55, 0x62, 0x08,
                0x00, 0x15, 0x67, 0x42,
                (byte) 0xe3, 0x05, 0x03, 0x01,
                (byte) 0xc3, 0x17, 0x15, 0x11,
                (byte) 0x89, 0x34, 0x2e, 0x25,
                0x41, 0x42, 0x3f, 0x3b,
                0x09, 0x38, 0x1c, 0x1c,
                -1, -5, -51, -113,
                -10, -110, -104, -99
        };

        int[] dest = new int[9];
        ConverterUtils.convert_AXYZPre_bytes_to_XYZ_ints(dest, 0, pixels, 0, 9);

        assertEquals(Integer.toUnsignedString(dest[0], 16), 0x556208, dest[0]);
        assertEquals(Integer.toUnsignedString(dest[1], 16), 0x156742, dest[1]);
        assertEquals(Integer.toUnsignedString(dest[2], 16), 0x050301, dest[2]);
        assertEquals(Integer.toUnsignedString(dest[3], 16), 0x171511, dest[3]);
        assertEquals(Integer.toUnsignedString(dest[4], 16), 0x342E25, dest[4]);
        assertEquals(Integer.toUnsignedString(dest[5], 16), 0x423F3B, dest[5]);
        assertEquals(Integer.toUnsignedString(dest[6], 16), 0x381C1C, dest[6]);
        assertEquals(Integer.toUnsignedString(dest[7], 16), 0xfbcd8f, dest[7]);
        assertEquals(Integer.toUnsignedString(dest[8], 16), 0x92989d, dest[8]);
    }

    public void test_convert_XYZAPre_bytes_to_XYZ_ints() {
        byte[] pixels = new byte[] {
                0x55, 0x62, 0x08, (byte) 0xff,
                0x15, 0x67, 0x42, 0x00,
                0x05, 0x03, 0x01, (byte) 0xe3,
                0x17, 0x15, 0x11, (byte) 0xc3,
                0x34, 0x2e, 0x25, (byte) 0x89,
                0x42, 0x3f, 0x3b, 0x41,
                0x38, 0x1c, 0x1c, 0x09,
                -5, -51, -113, -1,
                -110, -104, -99, -10
        };

        int[] dest = new int[9];
        ConverterUtils.convert_XYZAPre_bytes_to_XYZ_ints(dest, 0, pixels, 0, 9);

        assertEquals(Integer.toUnsignedString(dest[0], 16), 0x556208, dest[0]);
        assertEquals(Integer.toUnsignedString(dest[1], 16), 0x156742, dest[1]);
        assertEquals(Integer.toUnsignedString(dest[2], 16), 0x050301, dest[2]);
        assertEquals(Integer.toUnsignedString(dest[3], 16), 0x171511, dest[3]);
        assertEquals(Integer.toUnsignedString(dest[4], 16), 0x342E25, dest[4]);
        assertEquals(Integer.toUnsignedString(dest[5], 16), 0x423F3B, dest[5]);
        assertEquals(Integer.toUnsignedString(dest[6], 16), 0x381C1C, dest[6]);
        assertEquals(Integer.toUnsignedString(dest[7], 16), 0xfbcd8f, dest[7]);
        assertEquals(Integer.toUnsignedString(dest[8], 16), 0x92989d, dest[8]);
    }

    public void test_convert_AXYZPre_bytes_to_ZYX_ints() {
        byte[] pixels = new byte[] {
                (byte) 0xff, 0x55, 0x62, 0x08,
                0x00, 0x15, 0x67, 0x42,
                (byte) 0xe3, 0x05, 0x03, 0x01,
                (byte) 0xc3, 0x17, 0x15, 0x11,
                (byte) 0x89, 0x34, 0x2e, 0x25,
                0x41, 0x42, 0x3f, 0x3b,
                0x09, 0x38, 0x1c, 0x1c,
                -1, -5, -51, -113,
                -10, -110, -104, -99
        };

        int[] dest = new int[9];
        ConverterUtils.convert_AXYZPre_bytes_to_ZYX_ints(dest, 0, pixels, 0, 9);

        assertEquals(Integer.toUnsignedString(dest[0], 16), 0x086255, dest[0]);
        assertEquals(Integer.toUnsignedString(dest[1], 16), 0x426715, dest[1]);
        assertEquals(Integer.toUnsignedString(dest[2], 16), 0x010305, dest[2]);
        assertEquals(Integer.toUnsignedString(dest[3], 16), 0x111517, dest[3]);
        assertEquals(Integer.toUnsignedString(dest[4], 16), 0x252E34, dest[4]);
        assertEquals(Integer.toUnsignedString(dest[5], 16), 0x3B3F42, dest[5]);
        assertEquals(Integer.toUnsignedString(dest[6], 16), 0x1C1C38, dest[6]);
        assertEquals(Integer.toUnsignedString(dest[7], 16), 0x8fcdfb, dest[7]);
        assertEquals(Integer.toUnsignedString(dest[8], 16), 0x9d9892, dest[8]);
    }

    public void test_convert_XYZAPre_bytes_to_ZYX_ints() {
        byte[] pixels = new byte[] {
                0x55, 0x62, 0x08, (byte) 0xff,
                0x15, 0x67, 0x42, 0x00,
                0x05, 0x03, 0x01, (byte) 0xe3,
                0x17, 0x15, 0x11, (byte) 0xc3,
                0x34, 0x2e, 0x25, (byte) 0x89,
                0x42, 0x3f, 0x3b, 0x41,
                0x38, 0x1c, 0x1c, 0x09,
                -5, -51, -113, -1,
                -110, -104, -99, -10
        };

        int[] dest = new int[9];
        ConverterUtils.convert_XYZAPre_bytes_to_ZYX_ints(dest, 0, pixels, 0, 9);

        assertEquals(Integer.toUnsignedString(dest[0], 16), 0x086255, dest[0]);
        assertEquals(Integer.toUnsignedString(dest[1], 16), 0x426715, dest[1]);
        assertEquals(Integer.toUnsignedString(dest[2], 16), 0x010305, dest[2]);
        assertEquals(Integer.toUnsignedString(dest[3], 16), 0x111517, dest[3]);
        assertEquals(Integer.toUnsignedString(dest[4], 16), 0x252E34, dest[4]);
        assertEquals(Integer.toUnsignedString(dest[5], 16), 0x3B3F42, dest[5]);
        assertEquals(Integer.toUnsignedString(dest[6], 16), 0x1C1C38, dest[6]);
        assertEquals(Integer.toUnsignedString(dest[7], 16), 0x8fcdfb, dest[7]);
        assertEquals(Integer.toUnsignedString(dest[8], 16), 0x9d9892, dest[8]);
    }

    public void test_convert_AXYZPre_bytes_to_AXYZ_bytes() {
        byte[] pixels = new byte[] {
                (byte) 0xff, 0x55, 0x62, 0x08,
                0x00, 0x15, 0x67, 0x42,
                (byte) 0xe3, 0x05, 0x03, 0x01,
                (byte) 0xc3, 0x17, 0x15, 0x11,
                (byte) 0x89, 0x34, 0x2e, 0x25,
                0x41, 0x42, 0x3f, 0x3b,
                0x09, 0x38, 0x1c, 0x1c,
                -1, -5, -51, -113,
                -10, -110, -104, -99
        };

        ConverterUtils.convert_AXYZPre_bytes_to_AXYZ_bytes(pixels, 0, pixels, 0, 9);

        assertEquals( (byte) 0xff, pixels[0]);
        assertEquals( 0x55, pixels[1]);
        assertEquals( 0x62, pixels[2]);
        assertEquals( 0x08, pixels[3]);

        assertEquals( 0x00, pixels[4]);
        assertEquals( 0x00, pixels[5]);
        assertEquals( 0x00, pixels[6]);
        assertEquals( 0x00, pixels[7]);

        assertEquals( (byte) 0xe3, pixels[8]);
        assertEquals( 0x05, pixels[9]);
        assertEquals( 0x03, pixels[10]);
        assertEquals( 0x01, pixels[11]);

        assertEquals( (byte) 0xc3, pixels[12]);
        assertEquals( 0x1E, pixels[13]);
        assertEquals( 0x1B, pixels[14]);
        assertEquals( 0x16, pixels[15]);

        assertEquals( (byte) 0x89, pixels[16]);
        assertEquals( 0x60, pixels[17]);
        assertEquals( 0x55, pixels[18]);
        assertEquals( 0x44, pixels[19]);

        assertEquals( 0x41, pixels[20]);
        assertEquals( (byte) 0xff, pixels[21]);
        assertEquals( (byte) 0xf7, pixels[22]);
        assertEquals( (byte) 0xe7, pixels[23]);

        assertEquals( 0x09, pixels[24]);
        assertEquals( (byte) 0xff, pixels[25]);
        assertEquals( (byte) 0xff, pixels[26]);
        assertEquals( (byte) 0xff, pixels[27]);

        assertEquals( -1, pixels[28]);
        assertEquals( -5, pixels[29]);
        assertEquals( -51, pixels[30]);
        assertEquals( -113, pixels[31]);

        assertEquals( -10, pixels[32]);
        assertEquals( -105, pixels[33]);
        assertEquals( -99, pixels[34]);
        assertEquals( -94, pixels[35]);
    }

    public void test_convert_XYZAPre_bytes_to_AXYZ_bytes() {
        byte[] pixels = new byte[] {
                0x55, 0x62, 0x08, (byte) 0xff,
                0x15, 0x67, 0x42, 0x00,
                0x05, 0x03, 0x01, (byte) 0xe3,
                0x17, 0x15, 0x11, (byte) 0xc3,
                0x34, 0x2e, 0x25, (byte) 0x89,
                0x42, 0x3f, 0x3b, 0x41,
                0x38, 0x1c, 0x1c, 0x09,
                -5, -51, -113, -1,
                -110, -104, -99, -10
        };

        ConverterUtils.convert_XYZAPre_bytes_to_AXYZ_bytes(pixels, 0, pixels, 0, 9);

        assertEquals( (byte) 0xff, pixels[0]);
        assertEquals( 0x55, pixels[1]);
        assertEquals( 0x62, pixels[2]);
        assertEquals( 0x08, pixels[3]);

        assertEquals( 0x00, pixels[4]);
        assertEquals( 0x00, pixels[5]);
        assertEquals( 0x00, pixels[6]);
        assertEquals( 0x00, pixels[7]);

        assertEquals( (byte) 0xe3, pixels[8]);
        assertEquals( 0x05, pixels[9]);
        assertEquals( 0x03, pixels[10]);
        assertEquals( 0x01, pixels[11]);

        assertEquals( (byte) 0xc3, pixels[12]);
        assertEquals( 0x1E, pixels[13]);
        assertEquals( 0x1B, pixels[14]);
        assertEquals( 0x16, pixels[15]);

        assertEquals( (byte) 0x89, pixels[16]);
        assertEquals( 0x60, pixels[17]);
        assertEquals( 0x55, pixels[18]);
        assertEquals( 0x44, pixels[19]);

        assertEquals( 0x41, pixels[20]);
        assertEquals( (byte) 0xff, pixels[21]);
        assertEquals( (byte) 0xf7, pixels[22]);
        assertEquals( (byte) 0xe7, pixels[23]);

        assertEquals( 0x09, pixels[24]);
        assertEquals( (byte) 0xff, pixels[25]);
        assertEquals( (byte) 0xff, pixels[26]);
        assertEquals( (byte) 0xff, pixels[27]);

        assertEquals( -1, pixels[28]);
        assertEquals( -5, pixels[29]);
        assertEquals( -51, pixels[30]);
        assertEquals( -113, pixels[31]);

        assertEquals( -10, pixels[32]);
        assertEquals( -105, pixels[33]);
        assertEquals( -99, pixels[34]);
        assertEquals( -94, pixels[35]);
    }

    public void test_convert_AXYZPre_bytes_to_AZYX_bytes() {
        byte[] pixels = new byte[] {
                (byte) 0xff, 0x55, 0x62, 0x08,
                0x00, 0x15, 0x67, 0x42,
                (byte) 0xe3, 0x05, 0x03, 0x01,
                (byte) 0xc3, 0x17, 0x15, 0x11,
                (byte) 0x89, 0x34, 0x2e, 0x25,
                0x41, 0x42, 0x3f, 0x3b,
                0x09, 0x38, 0x1c, 0x1c,
                -1, -5, -51, -113,
                -10, -110, -104, -99
        };

        ConverterUtils.convert_AXYZPre_bytes_to_AZYX_bytes(pixels, 0, pixels, 0, 9);

        assertEquals( (byte) 0xff, pixels[0]);
        assertEquals( 0x08, pixels[1]);
        assertEquals( 0x62, pixels[2]);
        assertEquals( 0x55, pixels[3]);

        assertEquals( 0x00, pixels[4]);
        assertEquals( 0x00, pixels[5]);
        assertEquals( 0x00, pixels[6]);
        assertEquals( 0x00, pixels[7]);

        assertEquals( (byte) 0xe3, pixels[8]);
        assertEquals( 0x01, pixels[9]);
        assertEquals( 0x03, pixels[10]);
        assertEquals( 0x05, pixels[11]);

        assertEquals( (byte) 0xc3, pixels[12]);
        assertEquals( 0x16, pixels[13]);
        assertEquals( 0x1B, pixels[14]);
        assertEquals( 0x1E, pixels[15]);

        assertEquals( (byte) 0x89, pixels[16]);
        assertEquals( 0x44, pixels[17]);
        assertEquals( 0x55, pixels[18]);
        assertEquals( 0x60, pixels[19]);

        assertEquals( 0x41, pixels[20]);
        assertEquals( (byte) 0xe7, pixels[21]);
        assertEquals( (byte) 0xf7, pixels[22]);
        assertEquals( (byte) 0xff, pixels[23]);

        assertEquals( 0x09, pixels[24]);
        assertEquals( (byte) 0xff, pixels[25]);
        assertEquals( (byte) 0xff, pixels[26]);
        assertEquals( (byte) 0xff, pixels[27]);

        assertEquals( -1, pixels[28]);
        assertEquals( -113, pixels[29]);
        assertEquals( -51, pixels[30]);
        assertEquals( -5, pixels[31]);

        assertEquals( -10, pixels[32]);
        assertEquals( -94, pixels[33]);
        assertEquals( -99, pixels[34]);
        assertEquals( -105, pixels[35]);
    }

    public void test_convert_XYZAPre_bytes_to_AZYX_bytes() {
        byte[] pixels = new byte[] {
                0x55, 0x62, 0x08, (byte) 0xff,
                0x15, 0x67, 0x42, 0x00,
                0x05, 0x03, 0x01, (byte) 0xe3,
                0x17, 0x15, 0x11, (byte) 0xc3,
                0x34, 0x2e, 0x25, (byte) 0x89,
                0x42, 0x3f, 0x3b, 0x41,
                0x38, 0x1c, 0x1c, 0x09,
                -5, -51, -113, -1,
                -110, -104, -99, -10
        };

        ConverterUtils.convert_XYZAPre_bytes_to_AZYX_bytes(pixels, 0, pixels, 0, 9);

        assertEquals( (byte) 0xff, pixels[0]);
        assertEquals( 0x08, pixels[1]);
        assertEquals( 0x62, pixels[2]);
        assertEquals( 0x55, pixels[3]);

        assertEquals( 0x00, pixels[4]);
        assertEquals( 0x00, pixels[5]);
        assertEquals( 0x00, pixels[6]);
        assertEquals( 0x00, pixels[7]);

        assertEquals( (byte) 0xe3, pixels[8]);
        assertEquals( 0x01, pixels[9]);
        assertEquals( 0x03, pixels[10]);
        assertEquals( 0x05, pixels[11]);

        assertEquals( (byte) 0xc3, pixels[12]);
        assertEquals( 0x16, pixels[13]);
        assertEquals( 0x1B, pixels[14]);
        assertEquals( 0x1E, pixels[15]);

        assertEquals( (byte) 0x89, pixels[16]);
        assertEquals( 0x44, pixels[17]);
        assertEquals( 0x55, pixels[18]);
        assertEquals( 0x60, pixels[19]);

        assertEquals( 0x41, pixels[20]);
        assertEquals( (byte) 0xe7, pixels[21]);
        assertEquals( (byte) 0xf7, pixels[22]);
        assertEquals( (byte) 0xff, pixels[23]);

        assertEquals( 0x09, pixels[24]);
        assertEquals( (byte) 0xff, pixels[25]);
        assertEquals( (byte) 0xff, pixels[26]);
        assertEquals( (byte) 0xff, pixels[27]);

        assertEquals( -1, pixels[28]);
        assertEquals( -113, pixels[29]);
        assertEquals( -51, pixels[30]);
        assertEquals( -5, pixels[31]);

        assertEquals( -10, pixels[32]);
        assertEquals( -94, pixels[33]);
        assertEquals( -99, pixels[34]);
        assertEquals( -105, pixels[35]);
    }

    public void test_convert_AXYZPre_bytes_to_ZYXA_bytes() {
        byte[] pixels = new byte[] {
                (byte) 0xff, 0x55, 0x62, 0x08,
                0x00, 0x15, 0x67, 0x42,
                (byte) 0xe3, 0x05, 0x03, 0x01,
                (byte) 0xc3, 0x17, 0x15, 0x11,
                (byte) 0x89, 0x34, 0x2e, 0x25,
                0x41, 0x42, 0x3f, 0x3b,
                0x09, 0x38, 0x1c, 0x1c,
                -1, -5, -51, -113,
                -10, -110, -104, -99
        };

        ConverterUtils.convert_AXYZPre_bytes_to_ZYXA_bytes(pixels, 0, pixels, 0, 9);

        assertEquals( 0x08, pixels[0]);
        assertEquals( 0x62, pixels[1]);
        assertEquals( 0x55, pixels[2]);
        assertEquals( (byte) 0xff, pixels[3]);

        assertEquals( 0x00, pixels[4]);
        assertEquals( 0x00, pixels[5]);
        assertEquals( 0x00, pixels[6]);
        assertEquals( 0x00, pixels[7]);

        assertEquals( 0x01, pixels[8]);
        assertEquals( 0x03, pixels[9]);
        assertEquals( 0x05, pixels[10]);
        assertEquals( (byte) 0xe3, pixels[11]);

        assertEquals( 0x16, pixels[12]);
        assertEquals( 0x1B, pixels[13]);
        assertEquals( 0x1E, pixels[14]);
        assertEquals( (byte) 0xc3, pixels[15]);

        assertEquals( 0x44, pixels[16]);
        assertEquals( 0x55, pixels[17]);
        assertEquals( 0x60, pixels[18]);
        assertEquals( (byte) 0x89, pixels[19]);

        assertEquals( (byte) 0xe7, pixels[20]);
        assertEquals( (byte) 0xf7, pixels[21]);
        assertEquals( (byte) 0xff, pixels[22]);
        assertEquals( 0x41, pixels[23]);

        assertEquals( (byte) 0xff, pixels[24]);
        assertEquals( (byte) 0xff, pixels[25]);
        assertEquals( (byte) 0xff, pixels[26]);
        assertEquals( 0x09, pixels[27]);

        assertEquals( -113, pixels[28]);
        assertEquals( -51, pixels[29]);
        assertEquals( -5, pixels[30]);
        assertEquals( -1, pixels[31]);

        assertEquals( -94, pixels[32]);
        assertEquals( -99, pixels[33]);
        assertEquals( -105, pixels[34]);
        assertEquals( -10, pixels[35]);
    }

    public void test_convert_XYZAPre_bytes_to_ZYXA_bytes() {
        byte[] pixels = new byte[] {
                0x55, 0x62, 0x08, (byte) 0xff,
                0x15, 0x67, 0x42, 0x00,
                0x05, 0x03, 0x01, (byte) 0xe3,
                0x17, 0x15, 0x11, (byte) 0xc3,
                0x34, 0x2e, 0x25, (byte) 0x89,
                0x42, 0x3f, 0x3b, 0x41,
                0x38, 0x1c, 0x1c, 0x09,
                -5, -51, -113, -1,
                -110, -104, -99, -10
        };

        ConverterUtils.convert_XYZAPre_bytes_to_ZYXA_bytes(pixels, 0, pixels, 0, 9);

        assertEquals( 0x08, pixels[0]);
        assertEquals( 0x62, pixels[1]);
        assertEquals( 0x55, pixels[2]);
        assertEquals( (byte) 0xff, pixels[3]);

        assertEquals( 0x00, pixels[4]);
        assertEquals( 0x00, pixels[5]);
        assertEquals( 0x00, pixels[6]);
        assertEquals( 0x00, pixels[7]);

        assertEquals( 0x01, pixels[8]);
        assertEquals( 0x03, pixels[9]);
        assertEquals( 0x05, pixels[10]);
        assertEquals( (byte) 0xe3, pixels[11]);

        assertEquals( 0x16, pixels[12]);
        assertEquals( 0x1B, pixels[13]);
        assertEquals( 0x1E, pixels[14]);
        assertEquals( (byte) 0xc3, pixels[15]);

        assertEquals( 0x44, pixels[16]);
        assertEquals( 0x55, pixels[17]);
        assertEquals( 0x60, pixels[18]);
        assertEquals( (byte) 0x89, pixels[19]);

        assertEquals( (byte) 0xe7, pixels[20]);
        assertEquals( (byte) 0xf7, pixels[21]);
        assertEquals( (byte) 0xff, pixels[22]);
        assertEquals( 0x41, pixels[23]);

        assertEquals( (byte) 0xff, pixels[24]);
        assertEquals( (byte) 0xff, pixels[25]);
        assertEquals( (byte) 0xff, pixels[26]);
        assertEquals( 0x09, pixels[27]);

        assertEquals( -113, pixels[28]);
        assertEquals( -51, pixels[29]);
        assertEquals( -5, pixels[30]);
        assertEquals( -1, pixels[31]);

        assertEquals( -94, pixels[32]);
        assertEquals( -99, pixels[33]);
        assertEquals( -105, pixels[34]);
        assertEquals( -10, pixels[35]);
    }

    public void test_convert_AXYZPre_bytes_to_XYZA_bytes() {
        byte[] pixels = new byte[] {
                (byte) 0xff, 0x55, 0x62, 0x08,
                0x00, 0x15, 0x67, 0x42,
                (byte) 0xe3, 0x05, 0x03, 0x01,
                (byte) 0xc3, 0x17, 0x15, 0x11,
                (byte) 0x89, 0x34, 0x2e, 0x25,
                0x41, 0x42, 0x3f, 0x3b,
                0x09, 0x38, 0x1c, 0x1c,
                -1, -5, -51, -113,
                -10, -110, -104, -99
        };

        ConverterUtils.convert_AXYZPre_bytes_to_XYZA_bytes(pixels, 0, pixels, 0, 9);

        assertEquals( 0x55, pixels[0]);
        assertEquals( 0x62, pixels[1]);
        assertEquals( 0x08, pixels[2]);
        assertEquals( (byte) 0xff, pixels[3]);

        assertEquals( 0x00, pixels[4]);
        assertEquals( 0x00, pixels[5]);
        assertEquals( 0x00, pixels[6]);
        assertEquals( 0x00, pixels[7]);

        assertEquals( 0x05, pixels[8]);
        assertEquals( 0x03, pixels[9]);
        assertEquals( 0x01, pixels[10]);
        assertEquals( (byte) 0xe3, pixels[11]);

        assertEquals( 0x1E, pixels[12]);
        assertEquals( 0x1B, pixels[13]);
        assertEquals( 0x16, pixels[14]);
        assertEquals( (byte) 0xc3, pixels[15]);

        assertEquals( 0x60, pixels[16]);
        assertEquals( 0x55, pixels[17]);
        assertEquals( 0x44, pixels[18]);
        assertEquals( (byte) 0x89, pixels[19]);

        assertEquals( (byte) 0xff, pixels[20]);
        assertEquals( (byte) 0xf7, pixels[21]);
        assertEquals( (byte) 0xe7, pixels[22]);
        assertEquals( 0x41, pixels[23]);

        assertEquals( (byte) 0xff, pixels[24]);
        assertEquals( (byte) 0xff, pixels[25]);
        assertEquals( (byte) 0xff, pixels[26]);
        assertEquals( 0x09, pixels[27]);

        assertEquals( -5, pixels[28]);
        assertEquals( -51, pixels[29]);
        assertEquals( -113, pixels[30]);
        assertEquals( -1, pixels[31]);

        assertEquals( -105, pixels[32]);
        assertEquals( -99, pixels[33]);
        assertEquals( -94, pixels[34]);
        assertEquals( -10, pixels[35]);
    }

    public void test_convert_XYZAPre_bytes_to_XYZA_bytes() {
        byte[] pixels = new byte[] {
                0x55, 0x62, 0x08, (byte) 0xff,
                0x15, 0x67, 0x42, 0x00,
                0x05, 0x03, 0x01, (byte) 0xe3,
                0x17, 0x15, 0x11, (byte) 0xc3,
                0x34, 0x2e, 0x25, (byte) 0x89,
                0x42, 0x3f, 0x3b, 0x41,
                0x38, 0x1c, 0x1c, 0x09,
                -5, -51, -113, -1,
                -110, -104, -99, -10
        };

        ConverterUtils.convert_XYZAPre_bytes_to_XYZA_bytes(pixels, 0, pixels, 0, 9);

        assertEquals( 0x55, pixels[0]);
        assertEquals( 0x62, pixels[1]);
        assertEquals( 0x08, pixels[2]);
        assertEquals( (byte) 0xff, pixels[3]);

        assertEquals( 0x00, pixels[4]);
        assertEquals( 0x00, pixels[5]);
        assertEquals( 0x00, pixels[6]);
        assertEquals( 0x00, pixels[7]);

        assertEquals( 0x05, pixels[8]);
        assertEquals( 0x03, pixels[9]);
        assertEquals( 0x01, pixels[10]);
        assertEquals( (byte) 0xe3, pixels[11]);

        assertEquals( 0x1E, pixels[12]);
        assertEquals( 0x1B, pixels[13]);
        assertEquals( 0x16, pixels[14]);
        assertEquals( (byte) 0xc3, pixels[15]);

        assertEquals( 0x60, pixels[16]);
        assertEquals( 0x55, pixels[17]);
        assertEquals( 0x44, pixels[18]);
        assertEquals( (byte) 0x89, pixels[19]);

        assertEquals( (byte) 0xff, pixels[20]);
        assertEquals( (byte) 0xf7, pixels[21]);
        assertEquals( (byte) 0xe7, pixels[22]);
        assertEquals( 0x41, pixels[23]);

        assertEquals( (byte) 0xff, pixels[24]);
        assertEquals( (byte) 0xff, pixels[25]);
        assertEquals( (byte) 0xff, pixels[26]);
        assertEquals( 0x09, pixels[27]);

        assertEquals( -5, pixels[28]);
        assertEquals( -51, pixels[29]);
        assertEquals( -113, pixels[30]);
        assertEquals( -1, pixels[31]);

        assertEquals( -105, pixels[32]);
        assertEquals( -99, pixels[33]);
        assertEquals( -94, pixels[34]);
        assertEquals( -10, pixels[35]);
    }

    public void test_convert_AXYZ_bytes_to_AXYZPre_bytes() {
        byte[] pixels = new byte[] {
                -1, 0x55, 0x62, 0x08,
                0, 0x15, 0x67, 0x42,
                (byte) 0xe3, 0x05, 0x03, 0x01,
                (byte) 0xc3, 0x17, 0x15, 0x11,
                (byte) 0x89, 0x34, 0x2e, 0x25,
                0x41, 0x42, 0x3f, 0x3b,
                0x09, 0x38, 0x1c, 0x1c,
                -1, -5, -51, -113,
                -10, -110, -104, -99
        };

        ConverterUtils.convert_AXYZ_bytes_to_AXYZPre_bytes(pixels, 0, pixels, 0, 9);

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

        assertEquals( -1, pixels[28]);
        assertEquals( -5, pixels[29]);
        assertEquals( -51, pixels[30]);
        assertEquals( -113, pixels[31]);

        assertEquals( -10, pixels[32]);
        assertEquals( -116, pixels[33]);
        assertEquals( -110, pixels[34]);
        assertEquals( -105, pixels[35]);
    }

    public void test_convert_AXYZ_bytes_to_XYZAPre_bytes() {
        byte[] pixels = new byte[] {
                -1, 0x55, 0x62, 0x08,
                0, 0x15, 0x67, 0x42,
                (byte) 0xe3, 0x05, 0x03, 0x01,
                (byte) 0xc3, 0x17, 0x15, 0x11,
                (byte) 0x89, 0x34, 0x2e, 0x25,
                0x41, 0x42, 0x3f, 0x3b,
                0x09, 0x38, 0x1c, 0x1c,
                -1, -5, -51, -113,
                -10, -110, -104, -99
        };

        ConverterUtils.convert_AXYZ_bytes_to_XYZAPre_bytes(pixels, 0, pixels, 0, 9);

        assertEquals( 0x55, pixels[0]);
        assertEquals( 0x62, pixels[1]);
        assertEquals( 0x08, pixels[2]);
        assertEquals( -1, pixels[3]);

        assertEquals( 0, pixels[4]);
        assertEquals( 0, pixels[5]);
        assertEquals( 0, pixels[6]);
        assertEquals( 0, pixels[7]);

        assertEquals( 0x04, pixels[8]);
        assertEquals( 0x02, pixels[9]);
        assertEquals( 0x00, pixels[10]);
        assertEquals( (byte) 0xe3, pixels[11]);

        assertEquals( 0x11, pixels[12]);
        assertEquals( 0x10, pixels[13]);
        assertEquals( 0x0d, pixels[14]);
        assertEquals( (byte) 0xc3, pixels[15]);

        assertEquals( 0x1B, pixels[16]);
        assertEquals( 0x18, pixels[17]);
        assertEquals( 0x13, pixels[18]);
        assertEquals( (byte) 0x89, pixels[19]);

        assertEquals( 0x10, pixels[20]);
        assertEquals( 0x10, pixels[21]);
        assertEquals( 0x0f, pixels[22]);
        assertEquals( 0x41, pixels[23]);

        assertEquals( 0x01, pixels[24]);
        assertEquals( 0x00, pixels[25]);
        assertEquals( 0x00, pixels[26]);
        assertEquals( 0x09, pixels[27]);

        assertEquals( -5, pixels[28]);
        assertEquals( -51, pixels[29]);
        assertEquals( -113, pixels[30]);
        assertEquals( -1, pixels[31]);

        assertEquals( -116, pixels[32]);
        assertEquals( -110, pixels[33]);
        assertEquals( -105, pixels[34]);
        assertEquals( -10, pixels[35]);
    }

    public void test_convert_AXYZ_bytes_to_AZYXPre_bytes() {
        byte[] pixels = new byte[] {
                -1, 0x55, 0x62, 0x08,
                0, 0x15, 0x67, 0x42,
                (byte) 0xe3, 0x05, 0x03, 0x01,
                (byte) 0xc3, 0x17, 0x15, 0x11,
                (byte) 0x89, 0x34, 0x2e, 0x25,
                0x41, 0x42, 0x3f, 0x3b,
                0x09, 0x38, 0x1c, 0x1c,
                -1, -5, -51, -113,
                -10, -110, -104, -99
        };

        ConverterUtils.convert_AXYZ_bytes_to_AZYXPre_bytes(pixels, 0, pixels, 0, 9);

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

        assertEquals( -1, pixels[28]);
        assertEquals( -113, pixels[29]);
        assertEquals( -51, pixels[30]);
        assertEquals( -5, pixels[31]);

        assertEquals( -10, pixels[32]);
        assertEquals( -105, pixels[33]);
        assertEquals( -110, pixels[34]);
        assertEquals( -116, pixels[35]);
    }

    public void test_convert_AXYZ_bytes_to_ZYXAPre_bytes() {
        byte[] pixels = new byte[] {
                -1, 0x55, 0x62, 0x08,
                0, 0x15, 0x67, 0x42,
                (byte) 0xe3, 0x05, 0x03, 0x01,
                (byte) 0xc3, 0x17, 0x15, 0x11,
                (byte) 0x89, 0x34, 0x2e, 0x25,
                0x41, 0x42, 0x3f, 0x3b,
                0x09, 0x38, 0x1c, 0x1c,
                -1, -5, -51, -113,
                -10, -110, -104, -99
        };

        ConverterUtils.convert_AXYZ_bytes_to_ZYXAPre_bytes(pixels, 0, pixels, 0, 9);

        assertEquals( 0x08, pixels[0]);
        assertEquals( 0x62, pixels[1]);
        assertEquals( 0x55, pixels[2]);
        assertEquals( -1, pixels[3]);

        assertEquals( 0, pixels[4]);
        assertEquals( 0, pixels[5]);
        assertEquals( 0, pixels[6]);
        assertEquals( 0, pixels[7]);

        assertEquals( 0x00, pixels[8]);
        assertEquals( 0x02, pixels[9]);
        assertEquals( 0x04, pixels[10]);
        assertEquals( (byte) 0xe3, pixels[11]);

        assertEquals( 0x0d, pixels[12]);
        assertEquals( 0x10, pixels[13]);
        assertEquals( 0x11, pixels[14]);
        assertEquals( (byte) 0xc3, pixels[15]);

        assertEquals( 0x13, pixels[16]);
        assertEquals( 0x18, pixels[17]);
        assertEquals( 0x1B, pixels[18]);
        assertEquals( (byte) 0x89, pixels[19]);

        assertEquals( 0x0f, pixels[20]);
        assertEquals( 0x10, pixels[21]);
        assertEquals( 0x10, pixels[22]);
        assertEquals( 0x41, pixels[23]);

        assertEquals( 0x00, pixels[24]);
        assertEquals( 0x00, pixels[25]);
        assertEquals( 0x01, pixels[26]);
        assertEquals( 0x09, pixels[27]);

        assertEquals( -113, pixels[28]);
        assertEquals( -51, pixels[29]);
        assertEquals( -5, pixels[30]);
        assertEquals( -1, pixels[31]);

        assertEquals( -105, pixels[32]);
        assertEquals( -110, pixels[33]);
        assertEquals( -116, pixels[34]);
        assertEquals( -10, pixels[35]);
    }

    public void test_convert_XYZA_bytes_to_AXYZPre_bytes() {
        byte[] pixels = new byte[] {
                0x55, 0x62, 0x08, (byte) 0xff,
                0x15, 0x67, 0x42, 0x00,
                0x05, 0x03, 0x01, (byte) 0xe3,
                0x17, 0x15, 0x11, (byte) 0xc3,
                0x34, 0x2e, 0x25, (byte) 0x89,
                0x42, 0x3f, 0x3b, 0x41,
                0x38, 0x1c, 0x1c, 0x09,
                -5, -51, -113, -1,
                -110, -104, -99, -10
        };

        ConverterUtils.convert_XYZA_bytes_to_AXYZPre_bytes(pixels, 0, pixels, 0, 9);

        assertEquals( -1, pixels[0]);
        assertEquals( 0x55, pixels[1]);
        assertEquals( 0x62, pixels[2]);
        assertEquals( 0x08, pixels[3]);

        assertEquals( 0x00, pixels[4]);
        assertEquals( 0x00, pixels[5]);
        assertEquals( 0x00, pixels[6]);
        assertEquals( 0x00, pixels[7]);

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

        assertEquals( -1, pixels[28]);
        assertEquals( -5, pixels[29]);
        assertEquals( -51, pixels[30]);
        assertEquals( -113, pixels[31]);

        assertEquals( -10, pixels[32]);
        assertEquals( -116, pixels[33]);
        assertEquals( -110, pixels[34]);
        assertEquals( -105, pixels[35]);
    }

    public void test_convert_XYZA_bytes_to_XYZAPre_bytes() {
        byte[] pixels = new byte[] {
                0x55, 0x62, 0x08, (byte) 0xff,
                0x15, 0x67, 0x42, 0x00,
                0x05, 0x03, 0x01, (byte) 0xe3,
                0x17, 0x15, 0x11, (byte) 0xc3,
                0x34, 0x2e, 0x25, (byte) 0x89,
                0x42, 0x3f, 0x3b, 0x41,
                0x38, 0x1c, 0x1c, 0x09,
                -5, -51, -113, -1,
                -110, -104, -99, -10
        };

        ConverterUtils.convert_XYZA_bytes_to_XYZAPre_bytes(pixels, 0, pixels, 0, 9);

        assertEquals( 0x55, pixels[0]);
        assertEquals( 0x62, pixels[1]);
        assertEquals( 0x08, pixels[2]);
        assertEquals( -1, pixels[3]);

        assertEquals( 0x00, pixels[4]);
        assertEquals( 0x00, pixels[5]);
        assertEquals( 0x00, pixels[6]);
        assertEquals( 0x00, pixels[7]);

        assertEquals( 0x04, pixels[8]);
        assertEquals( 0x02, pixels[9]);
        assertEquals( 0x00, pixels[10]);
        assertEquals( (byte) 0xe3, pixels[11]);

        assertEquals( 0x11, pixels[12]);
        assertEquals( 0x10, pixels[13]);
        assertEquals( 0x0d, pixels[14]);
        assertEquals( (byte) 0xc3, pixels[15]);

        assertEquals( 0x1B, pixels[16]);
        assertEquals( 0x18, pixels[17]);
        assertEquals( 0x13, pixels[18]);
        assertEquals( (byte) 0x89, pixels[19]);

        assertEquals( 0x10, pixels[20]);
        assertEquals( 0x10, pixels[21]);
        assertEquals( 0x0f, pixels[22]);
        assertEquals( 0x41, pixels[23]);

        assertEquals( 0x01, pixels[24]);
        assertEquals( 0x00, pixels[25]);
        assertEquals( 0x00, pixels[26]);
        assertEquals( 0x09, pixels[27]);

        assertEquals( -5, pixels[28]);
        assertEquals( -51, pixels[29]);
        assertEquals( -113, pixels[30]);
        assertEquals( -1, pixels[31]);

        assertEquals( -116, pixels[32]);
        assertEquals( -110, pixels[33]);
        assertEquals( -105, pixels[34]);
        assertEquals( -10, pixels[35]);
    }

    public void test_convert_XYZA_bytes_to_AZYXPre_bytes() {
        byte[] pixels = new byte[] {
                0x55, 0x62, 0x08, (byte) 0xff,
                0x15, 0x67, 0x42, 0x00,
                0x05, 0x03, 0x01, (byte) 0xe3,
                0x17, 0x15, 0x11, (byte) 0xc3,
                0x34, 0x2e, 0x25, (byte) 0x89,
                0x42, 0x3f, 0x3b, 0x41,
                0x38, 0x1c, 0x1c, 0x09,
                -5, -51, -113, -1,
                -110, -104, -99, -10
        };

        ConverterUtils.convert_XYZA_bytes_to_AZYXPre_bytes(pixels, 0, pixels, 0, 9);

        assertEquals( (byte) 0xff, pixels[0]);
        assertEquals( 0x08, pixels[1]);
        assertEquals( 0x62, pixels[2]);
        assertEquals( 0x55, pixels[3]);

        assertEquals( 0x00, pixels[4]);
        assertEquals( 0x00, pixels[5]);
        assertEquals( 0x00, pixels[6]);
        assertEquals( 0x00, pixels[7]);

        assertEquals( (byte) 0xe3, pixels[8]);
        assertEquals( 0x00, pixels[9]);
        assertEquals( 0x02, pixels[10]);
        assertEquals( 0x04, pixels[11]);

        assertEquals( (byte) 0xc3, pixels[12]);
        assertEquals( 0x0c, pixels[13]);
        assertEquals( 0x0f, pixels[14]);
        assertEquals( 0x11, pixels[15]);

        assertEquals( (byte) 0x89, pixels[16]);
        assertEquals( 0x13, pixels[17]);
        assertEquals( 0x18, pixels[18]);
        assertEquals( 0x1B, pixels[19]);

        assertEquals( 0x41, pixels[20]);
        assertEquals( 0x0e, pixels[21]);
        assertEquals( 0x0f, pixels[22]);
        assertEquals( 0x10, pixels[23]);

        assertEquals( 0x09, pixels[24]);
        assertEquals( 0x00, pixels[25]);
        assertEquals( 0x00, pixels[26]);
        assertEquals( 0x01, pixels[27]);

        assertEquals( -1, pixels[28]);
        assertEquals( -113, pixels[29]);
        assertEquals( -51, pixels[30]);
        assertEquals( -5, pixels[31]);

        assertEquals( -10, pixels[32]);
        assertEquals( -106, pixels[33]);
        assertEquals( -110, pixels[34]);
        assertEquals( -116, pixels[35]);
    }

    public void test_convert_XYZA_bytes_to_ZYXAPre_bytes() {
        byte[] pixels = new byte[] {
                0x55, 0x62, 0x08, (byte) 0xff,
                0x15, 0x67, 0x42, 0x00,
                0x05, 0x03, 0x01, (byte) 0xe3,
                0x17, 0x15, 0x11, (byte) 0xc3,
                0x34, 0x2e, 0x25, (byte) 0x89,
                0x42, 0x3f, 0x3b, 0x41,
                0x38, 0x1c, 0x1c, 0x09,
                -5, -51, -113, -1,
                -110, -104, -99, -10
        };

        ConverterUtils.convert_XYZA_bytes_to_ZYXAPre_bytes(pixels, 0, pixels, 0, 9);

        assertEquals( 0x08, pixels[0]);
        assertEquals( 0x62, pixels[1]);
        assertEquals( 0x55, pixels[2]);
        assertEquals( (byte) 0xff, pixels[3]);

        assertEquals( 0x00, pixels[4]);
        assertEquals( 0x00, pixels[5]);
        assertEquals( 0x00, pixels[6]);
        assertEquals( 0x00, pixels[7]);

        assertEquals( 0x00, pixels[8]);
        assertEquals( 0x02, pixels[9]);
        assertEquals( 0x04, pixels[10]);
        assertEquals( (byte) 0xe3, pixels[11]);

        assertEquals( 0x0c, pixels[12]);
        assertEquals( 0x0f, pixels[13]);
        assertEquals( 0x11, pixels[14]);
        assertEquals( (byte) 0xc3, pixels[15]);

        assertEquals( 0x13, pixels[16]);
        assertEquals( 0x18, pixels[17]);
        assertEquals( 0x1B, pixels[18]);
        assertEquals( (byte) 0x89, pixels[19]);

        assertEquals( 0x0e, pixels[20]);
        assertEquals( 0x0f, pixels[21]);
        assertEquals( 0x10, pixels[22]);
        assertEquals( 0x41, pixels[23]);

        assertEquals( 0x00, pixels[24]);
        assertEquals( 0x00, pixels[25]);
        assertEquals( 0x01, pixels[26]);
        assertEquals( 0x09, pixels[27]);

        assertEquals( -113, pixels[28]);
        assertEquals( -51, pixels[29]);
        assertEquals( -5, pixels[30]);
        assertEquals( -1, pixels[31]);

        assertEquals( -106, pixels[32]);
        assertEquals( -110, pixels[33]);
        assertEquals( -116, pixels[34]);
        assertEquals( -10, pixels[35]);
    }

    public void test_convert_AXYZ_ints_to_G_bytes() {
        int[] pixels = new int[] {
                0xff556208,
                0x00156742,
                0xe3050301,
                0xc3171511,
                0x89342e25,
                0x41423f3b,
                0x09381c1c
        };

        byte[] dest = new byte[7];
        ConverterUtils.convert_AXYZ_ints_to_G_bytes(dest, 0, pixels, 0, 7);

        assertEquals(0x3F, dest[0]);
        assertEquals(0x00, dest[1]);
        assertEquals(0x02, dest[2]);
        assertEquals(0x0f, dest[3]);
        assertEquals(0x18, dest[4]);
        assertEquals(0x0f, dest[5]);
        assertEquals(0x01, dest[6]);
    }

    public void test_convert_AXYZPre_ints_to_G_bytes() {
        int[] pixels = new int[] {
                0xff556208,
                0x00156742,
                0xe3050301,
                0xc3171511,
                0x89342e25,
                0x41423f3b,
                0x09381c1c
        };

        byte[] dest = new byte[7];
        ConverterUtils.convert_AXYZPre_ints_to_G_bytes(dest, 0, pixels, 0, 7);

        assertEquals(0x3f, dest[0]);
        assertEquals(0x00, dest[1]);
        assertEquals(0x03, dest[2]);
        assertEquals(0x14, dest[3]);
        assertEquals(0x2d, dest[4]);
        assertEquals(0x3e, dest[5]);
        assertEquals(0x25, dest[6]);
    }

    public void test_convert_AXYZ_bytes_to_G_bytes() {
        byte[] pixels = new byte[] {
                -1, 0x55, 0x62, 0x08,
                0, 0x15, 0x67, 0x42,
                (byte) 0xe3, 0x05, 0x03, 0x01,
                (byte) 0xc3, 0x17, 0x15, 0x11,
                (byte) 0x89, 0x34, 0x2e, 0x25,
                0x41, 0x42, 0x3f, 0x3b,
                0x09, 0x38, 0x1c, 0x1c,
                -1, -5, -51, -113,
                -10, -110, -104, -99
        };

        ConverterUtils.convert_AXYZ_bytes_to_G_bytes(pixels, 0, pixels, 0, 9);

        assertEquals(0x3F, pixels[0]);
        assertEquals(0x0, pixels[1]);
        assertEquals(0x02, pixels[2]);
        assertEquals(0x0f, pixels[3]);
        assertEquals(0x18, pixels[4]);
        assertEquals(0x0f, pixels[5]);
        assertEquals(0x01, pixels[6]);
        assertEquals(-57, pixels[7]);
        assertEquals(-110, pixels[8]);
    }

    public void test_convert_AXYZPre_bytes_to_G_bytes() {
        byte[] pixels = new byte[] {
                (byte) 0xe3, 0x05, 0x03, 0x01,
                (byte) 0xc3, 0x17, 0x15, 0x11,
                (byte) 0x89, 0x34, 0x2e, 0x25,
                0x41, 0x42, 0x3f, 0x3b,
                0x09, 0x38, 0x1c, 0x1c,
                -1, -5, -51, -113,
                -10, -110, -104, -99
        };

        ConverterUtils.convert_AXYZPre_bytes_to_G_bytes(pixels, 0, pixels, 0, 7);

        assertEquals(0x03, pixels[0]);
        assertEquals(0x14, pixels[1]);
        assertEquals(0x2d, pixels[2]);
        assertEquals(0x3e, pixels[3]);
        assertEquals(0x25, pixels[4]);
        assertEquals(-57, pixels[5]);
        assertEquals(-105, pixels[6]);
    }

    public void test_convert_XYZAPre_bytes_to_G_bytes() {
        byte[] pixels = new byte[] {
                0x05, 0x03, 0x01, (byte) 0xe3,
                0x17, 0x15, 0x11, (byte) 0xc3,
                0x34, 0x2e, 0x25, (byte) 0x89,
                0x42, 0x3f, 0x3b, 0x41,
                0x38, 0x1c, 0x1c, 0x09,
                -5, -51, -113, -1,
                -110, -104, -99, -10
        };

        ConverterUtils.convert_XYZAPre_bytes_to_G_bytes(pixels, 0, pixels, 0, 7);

        assertEquals(0x03, pixels[0]);
        assertEquals(0x14, pixels[1]);
        assertEquals(0x2d, pixels[2]);
        assertEquals(0x3e, pixels[3]);
        assertEquals(0x25, pixels[4]);
        assertEquals(-57, pixels[5]);
        assertEquals(-105, pixels[6]);
    }

    public void test_convert_XYZA_bytes_to_G_bytes() {
        byte[] pixels = new byte[] {
                0x55, 0x62, 0x08, (byte) 0xff,
                0x15, 0x67, 0x42, 0x00,
                0x05, 0x03, 0x01, (byte) 0xe3,
                0x17, 0x15, 0x11, (byte) 0xc3,
                0x34, 0x2e, 0x25, (byte) 0x89,
                0x42, 0x3f, 0x3b, 0x41,
                0x38, 0x1c, 0x1c, 0x09,
                -5, -51, -113, -1,
                -110, -104, -99, -10
        };

        ConverterUtils.convert_XYZA_bytes_to_G_bytes(pixels, 0, pixels, 0, 9);

        assertEquals(0x3F, pixels[0]);
        assertEquals(0x00, pixels[1]);
        assertEquals(0x02, pixels[2]);
        assertEquals(0x0f, pixels[3]);
        assertEquals(0x18, pixels[4]);
        assertEquals(0x0f, pixels[5]);
        assertEquals(0x01, pixels[6]);
        assertEquals(-57, pixels[7]);
        assertEquals(-111, pixels[8]);
    }
}