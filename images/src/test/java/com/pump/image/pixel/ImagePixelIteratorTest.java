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
package com.pump.image.pixel;

import com.pump.image.QBufferedImage;
import com.pump.image.QBufferedImageTest;
import org.junit.Test;

import java.awt.image.BufferedImage;
import java.util.Random;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ImagePixelIteratorTest {

    @Test
    public void testEqualPixels_differentImageTypes() {
        Random random = new Random(0);
        for (ImageType typeA : ImageType.values(true)) {
            if (typeA == ImageType.BYTE_GRAY)
                continue;

            for (ImageType typeB : ImageType.values(true)) {
                if (typeB == ImageType.BYTE_GRAY)
                    continue;

                System.out.println("Testing " + typeA + " vs " + typeB);

                QBufferedImage bi1 = QBufferedImageTest.createSampleQImage(80, 60, typeA.getCode());
                BufferedImage bi2 = QBufferedImageTest.createSampleBufferedImage(80, 60, typeB.getCode());

                assertTrue(ImagePixelIterator.equalPixels(bi1, bi2));

                int x = random.nextInt(80);
                int y = random.nextInt(60);
                int newRGB = random.nextInt(0xffffff) | 0xff000000;
                // now change one pixel
                if (random.nextBoolean()) {
                    bi1.setRGB(x, y, newRGB);
                } else {
                    bi2.setRGB(x, y, newRGB);
                }
                assertFalse(ImagePixelIterator.equalPixels(bi1, bi2));
            }
        }
    }
}