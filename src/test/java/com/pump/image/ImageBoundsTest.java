package com.pump.image;

import junit.framework.TestCase;
import org.junit.Test;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class ImageBoundsTest extends TestCase {

    @Test
    public void testScenario_empty() {
        BufferedImage bi = new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB);
        assertNull(ImageBounds.getBounds(bi));
    }

    @Test
    public void testScenario_1px() {
        // test every corner to make sure we're handling (literal) edge cases well:
        for(int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                System.out.println("x = " + x + ", y = "+ y);
                BufferedImage bi = new BufferedImage(3, 3, BufferedImage.TYPE_INT_ARGB);
                bi.setRGB(x, y, 0xff000000);
                assertEquals(new Rectangle(x, y,1,1), ImageBounds.getBounds(bi));
            }
        }
    }

    @Test
    public void testScenario_2px_a() {
        BufferedImage bi = new BufferedImage(5, 5, BufferedImage.TYPE_INT_ARGB);
        assertNull(ImageBounds.getBounds(bi));
        bi.setRGB(1, 1, 0xff000000);
        bi.setRGB(3, 2, 0xff000000);
        assertEquals(new Rectangle(1,1,3,2), ImageBounds.getBounds(bi));
    }

    @Test
    public void testScenario_2px_b() {
        BufferedImage bi = new BufferedImage(5, 5, BufferedImage.TYPE_INT_ARGB);
        assertNull(ImageBounds.getBounds(bi));
        bi.setRGB(1, 2, 0xff000000);
        bi.setRGB(3, 1, 0xff000000);
        assertEquals(new Rectangle(1,1,3,2), ImageBounds.getBounds(bi));
    }

    @Test
    public void testScenario_3px() {
        BufferedImage bi = new BufferedImage(5, 5, BufferedImage.TYPE_INT_ARGB);
        assertNull(ImageBounds.getBounds(bi));
        bi.setRGB(1, 1, 0xff000000);
        bi.setRGB(2, 4, 0xff000000);
        assertEquals(new Rectangle(1,1,2,4), ImageBounds.getBounds(bi));
    }
}
