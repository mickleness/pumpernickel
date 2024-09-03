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
package com.pump.geom;

import com.pump.image.pixel.ImagePixelIterator;
import com.pump.image.pixel.ImageType;
import junit.framework.TestCase;
import org.junit.Test;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

public class ClipperTest extends TestCase {

    /**
     * This tests a specific quadratic curve that failed. It turns out the problem was the y quadratic
     * actually degenerated into a line, so we were dividing by zero.
     *
     * This is named after a specific scenario in an old Clipper demo GUI that doesn't exist anymore.
     */
    @Test
    public void testExample35_quadratic() {

        // this is the problem snippet where a quad curve degenerates into a line:
        test("m 52.0 248.0 q 201.0 201.0 169.0 154.0 z");

        // this the original whole shape
        test("m 90.0 109.0 q 254.0 299.0 293.0 57.0 q 283.0 105.0 29.0 196.0 q 244.0 60.0 195.0 115.0 q 81.0 178.0 185.0 23.0 q 40.0 4.0 221.0 26.0 q 149.0 84.0 16.0 150.0 q 111.0 259.0 254.0 90.0 q 38.0 284.0 52.0 248.0 q 201.0 201.0 169.0 154.0 q 210.0 154.0 157.0 177.0 q 136.0 299.0 133.0 42.0 q 135.0 117.0 50.0 238.0 q 45.0 124.0 287.0 9.0 q 89.0 183.0 257.0 29.0 q 179.0 62.0 137.0 61.0 q 242.0 78.0 270.0 235.0 q 239.0 279.0 168.0 124.0 q 15.0 244.0 57.0 18.0 q 52.0 136.0 152.0 65.0 q 154.0 41.0 35.0 199.0 z");
    }

    private void test(String str) {
        Path2D p = new Path2D.Float(Path2D.WIND_EVEN_ODD);
        p.append(ShapeStringUtils.createPathIterator(str), true);

        testClipping(p, new Rectangle(100, 100, 100, 100));
    }

    private void testClipping(Shape shape, Rectangle rectangle) {
        Shape clippedShape = Clipper.clipToRect(shape, rectangle);

        Area area = new Area(shape);
        area.intersect(new Area(rectangle));

        Rectangle imageBounds = new Rectangle(rectangle);
        imageBounds.grow(5, 5);

        BufferedImage expected = paint(area, imageBounds);
        BufferedImage actual = paint(clippedShape, imageBounds);

        int diff = getMaxDifference(expected, actual);
        assertTrue( diff < 25);
    }

    private int getMaxDifference(BufferedImage imageA, BufferedImage imageB) {
        ImagePixelIterator iter1 = new ImagePixelIterator(imageA, ImageType.INT_ARGB, false);
        ImagePixelIterator iter2 = new ImagePixelIterator(imageB, ImageType.INT_ARGB, false);
        int[] row1 = new int[imageA.getWidth()];
        int[] row2 = new int[imageB.getWidth()];
        int maxDiff = 0;
        while (!iter1.isDone()) {
            iter1.next(row1, 0);
            iter2.next(row2, 0);
            for (int x = 0; x < row1.length; x++) {
                int alpha1 = (row1[x] >> 24) & 0xff;
                int alpha2 = (row2[x] >> 24) & 0xff;
                maxDiff = Math.max(maxDiff, Math.abs(alpha1 - alpha2));
            }
        }
        return maxDiff;
    }

    private BufferedImage paint(Shape shape, Rectangle bounds) {
        BufferedImage bi = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        g.setColor(Color.red);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.translate(-bounds.x, -bounds.y);
        g.fill(shape);
        g.dispose();
        return bi;
    }
}