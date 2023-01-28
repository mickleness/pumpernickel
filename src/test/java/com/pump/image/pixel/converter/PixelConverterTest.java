package com.pump.image.pixel.converter;

import com.pump.image.pixel2.converter.ByteRGBConverter;
import junit.framework.TestCase;

public class PixelConverterTest extends TestCase {
    public void test_swapFirstAndThirdSamples() {
        // test storing it to the left of where our source pixels start
        int[] pixels = new int[] {0, 0, 0x02030507, 0x11131719, 0x23293133, 0x37414347};
        new ByteRGBConverter()._swapFirstAndThirdSamples(pixels, 0, pixels, 2, 4);
        assertEquals(0x02070503, pixels[0]);
        assertEquals(0x11191713, pixels[1]);
        assertEquals(0x23333129, pixels[2]);
        assertEquals(0x37474341, pixels[3]);

        // test storing it to the right of where our source pixels start
        pixels = new int[] {0x02030507, 0x11131719, 0x23293133, 0x37414347, 0, 0};
        new ByteRGBConverter()._swapFirstAndThirdSamples(pixels, 2, pixels, 0, 4);
        assertEquals(0x02070503, pixels[2]);
        assertEquals(0x11191713, pixels[3]);
        assertEquals(0x23333129, pixels[4]);
        assertEquals(0x37474341, pixels[5]);
    }
}