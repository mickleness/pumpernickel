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
package com.pump.showcase.resourcegenerator;

import java.util.List;
import com.pump.geom.Clipper;
import com.pump.image.pixel.ImagePixelIterator;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * This compares the time it takes for the Area class vs the Clipper class to clip a Rectangle
 */
public class ClipperComparison extends DemoResourceGenerator {

    public enum Model {
        AREA("Area") {
            @Override
            public Shape clip(Shape shape, Rectangle2D rectangle) {
                Area area1 = new Area(shape);
                Area area2 = new Area(rectangle);
                area1.intersect(area2);
                return area1;
            }
        },
        FLATTENED_AREA("Flattened Area") {
            @Override
            public Shape clip(Shape shape, Rectangle2D rectangle) {
                Path2D flattenedShape = new Path2D.Float();
                flattenedShape.append(shape.getPathIterator(null, .1f), false);

                Area area1 = new Area(flattenedShape);
                Area area2 = new Area(rectangle);
                area1.intersect(area2);
                return area1;
            }
        },
        CLIPPER("Clipper") {
            @Override
            public Shape clip(Shape shape, Rectangle2D rectangle) {
                return Clipper.clipToRect(shape, rectangle);
            }
        };

        final String name;

        Model(String name) {
            this.name = name;
        }

        public abstract Shape clip(Shape shape, Rectangle2D rectangle);

        @Override
        public String toString() {
            return name;
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Running " + ClipperComparison.class.getSimpleName());
        System.out.println("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version"));
        new ClipperComparison().run(null);
    }

    @Override
    public void run(DemoResourceContext context) throws Exception {
        testShapes(1);
        testShapes(2);
        testShapes(3);
    }

    private static void testShapes(int degree) {
        Rectangle rect = new Rectangle(20, 20, 60, 60);

        System.out.println();
        System.out.println("Testing Degree: "+ degree);

        System.out.print("Segments");
        for (Model model : Model.values()) {
            System.out.print("\t"+model.name);
        }
        System.out.println();

        for (int segmentCount = 5; segmentCount <= 15; segmentCount ++) {
            List<Shape> shapes = createShapes(degree, segmentCount);
            long[] samples = new long[20];
            System.out.print(segmentCount);
            for (Model model : Model.values()) {
                for (int sampleIndex = 0; sampleIndex < samples.length; sampleIndex++) {
                    samples[sampleIndex] = System.currentTimeMillis();
                    for (Shape shape : shapes) {
                        model.clip(shape, rect);
                    }
                    samples[sampleIndex] = System.currentTimeMillis() - samples[sampleIndex];
                }
                Arrays.sort(samples);
                System.out.print("\t" + samples[samples.length/2]);
            }
            System.out.println();

            // now also test for accuracy:
            for (Shape shape : createShapes(degree, segmentCount)) {
                Shape expectedValue = Model.AREA.clip(shape, rect);

                for (Model model : Model.values()) {
                    if (model != Model.AREA) {
                        Shape actualValue = Model.CLIPPER.clip(shape, rect);
                        assertEquals(expectedValue, actualValue);
                    }
                }
            }
        }
    }

    private static void assertEquals(Shape expectedValue, Shape actualValue) {
        BufferedImage bi1 = createImage(expectedValue);
        BufferedImage bi2 = createImage(actualValue);
        if (!equals(bi1, bi2, 3)) {
            throw new AssertionError("the Clipper's results didn't match the Area's results");
        }
    }

    /**
     * Return true if the alpha channel of every pixel of the two images is within [tolerance] of the other.
     * <p>
     * I observed the two images are not *identical*, but if the alpha channel differs by, say, 5: I think that's
     * acceptable.
     * </p>
     */
    private static boolean equals(BufferedImage bi1, BufferedImage bi2, int tolerance) {
        int[] row1 = new int[bi1.getWidth()];
        int[] row2 = new int[bi2.getWidth()];
        try (ImagePixelIterator iter1 = new ImagePixelIterator(bi1)) {
            try (ImagePixelIterator iter2 = new ImagePixelIterator(bi2)) {
                iter1.next(row1, 0);
                iter2.next(row2, 0);
                for (int x = 0; x < row1.length; x++) {
                    int rgb1 = row1[x];
                    int rgb2 = row2[x];

                    int alpha1 = (rgb1 >> 24) & 0xff;
                    int alpha2 = (rgb2 >> 24) & 0xff;
                    if (Math.abs(alpha1 - alpha2) > tolerance)
                        return false;
                }
            }
        }
        return true;
    }

    private static BufferedImage createImage(Shape shape) {
        BufferedImage bi = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = bi.createGraphics();
        g.setColor(Color.black);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.fill(shape);
        g.dispose();
        return bi;
    }

    private static List<Shape> createShapes(int degree, int segmentCount) {
        Random random = new Random(0);
        List<Shape> returnValue = new ArrayList<>();

        for (int shapeIndex = 0; shapeIndex < 2000; shapeIndex++) {
            Path2D p = new Path2D.Float();
            for (int segmentIndex = 0; segmentIndex < segmentCount; segmentIndex++) {
                if (segmentIndex == 0) {
                    p.moveTo(random.nextInt(100), random.nextInt(100));
                }
                switch (degree) {
                    case 1:
                        p.lineTo(random.nextInt(100), random.nextInt(100));
                        break;
                    case 2:
                        p.quadTo(random.nextInt(100), random.nextInt(100),
                                random.nextInt(100), random.nextInt(100));
                        break;
                    case 3:
                        p.curveTo(random.nextInt(100), random.nextInt(100),
                                random.nextInt(100), random.nextInt(100),
                                random.nextInt(100), random.nextInt(100));
                        break;
                }
            }
            p.closePath();
            returnValue.add(p);
        }
        return returnValue;
    }
}