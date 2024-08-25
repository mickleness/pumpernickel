package com.pump.geom;

import junit.framework.TestCase;

import java.awt.geom.Point2D;
import java.util.Random;

public class PerspectiveTransformTest extends TestCase {
    /**
     * This confirms that getQuadToQuad creates a PerspectiveTransform that transforms
     * points as expected, and then inverts that transform to return to the original points.
     */
    public void testGetQuadToQuad_and_inverse() {
        for (int a = 0; a < 10_000; a++) {
            try {
                Random random = new Random(a);
                double x0 = 0 + 20 * random.nextDouble();
                double y0 = 0 + 20 * random.nextDouble();
                double x1 = 100 + 20 * random.nextDouble();
                double y1 = 0 + 20 * random.nextDouble();
                double x2 = 100 + 20 * random.nextDouble();
                double y2 = 100 + 20 * random.nextDouble();
                double x3 = 0 + 20 * random.nextDouble();
                double y3 = 100 + 20 * random.nextDouble();
                PerspectiveTransform tx = PerspectiveTransform.getQuadToQuad(0, 0,
                        100, 0,
                        100, 100,
                        0, 100,
                        x0, y0, x1, y1, x2, y2, x3, y3);
                Point2D p0 = new Point2D.Double(0, 0);
                Point2D p1 = new Point2D.Double(100, 0);
                Point2D p2 = new Point2D.Double(100, 100);
                Point2D p3 = new Point2D.Double(0, 100);
                tx.transform(p0, p0);
                tx.transform(p1, p1);
                tx.transform(p2, p2);
                tx.transform(p3, p3);
                assertTrue(x0 + " vs " + p0.getX(), Math.abs(p0.getX() - x0) < .00001);
                assertTrue(y0 + " vs " + p0.getY(), Math.abs(p0.getY() - y0) < .00001);
                assertTrue(x1 + " vs " + p1.getX(), Math.abs(p1.getX() - x1) < .00001);
                assertTrue(y1 + " vs " + p1.getY(), Math.abs(p1.getY() - y1) < .00001);
                assertTrue(x2 + " vs " + p2.getX(), Math.abs(p2.getX() - x2) < .00001);
                assertTrue(y2 + " vs " + p2.getY(), Math.abs(p2.getY() - y2) < .00001);
                assertTrue(x3 + " vs " + p3.getX(), Math.abs(p3.getX() - x3) < .00001);
                assertTrue(y3 + " vs " + p3.getY(), Math.abs(p3.getY() - y3) < .00001);

                tx = tx.createInverse();
                tx.transform(p0, p0);
                tx.transform(p1, p1);
                tx.transform(p2, p2);
                tx.transform(p3, p3);
                assertTrue(Math.abs(p0.getX() - 0) < .00001);
                assertTrue(Math.abs(p0.getY() - 0) < .00001);
                assertTrue(Math.abs(p1.getX() - 100) < .00001);
                assertTrue(Math.abs(p1.getY() - 0) < .00001);
                assertTrue(Math.abs(p2.getX() - 100) < .00001);
                assertTrue(Math.abs(p2.getY() - 100) < .00001);
                assertTrue(Math.abs(p3.getX() - 0) < .00001);
                assertTrue(Math.abs(p3.getY() - 100) < .00001);
            } catch(Error e) {
                System.err.println("a = " + a);
                throw e;
            }
        }
    }
}
