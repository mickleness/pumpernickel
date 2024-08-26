package com.pump.geom;

import com.pump.math.Equations;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.Serial;
import java.io.UnsupportedEncodingException;

/**
 * This is a 4-point coordinate transformation matrix.
 * <p>
 * This class acts as a replacement for the <code>javax.media.jai.PerspectiveTransform</code>. I'm unclear
 * where the most up-to-date licensing and source code are available for that JAI class, so I wrote this
 * replacement from scratch. (I consulted parts of the java.awt.geom.AffineTransform in some places, though.)
 * </p>
 */
public class PerspectiveTransform {


    public static PerspectiveTransform getQuadToQuad(double origP1x, double origP1y,
                                                     double origP2x, double origP2y,
                                                     double origP3x, double origP3y,
                                                     double origP4x, double origP4y,
                                                     double transfomedP1x, double transfomedP1y,
                                                     double transfomedP2x, double transfomedP2y,
                                                     double transfomedP3x, double transfomedP3y,
                                                     double transfomedP4x, double transfomedP4y) {
        double[][] matrix = new double[][] {
                {origP1x, origP1y, 1, 0, 0, 0, -origP1x * transfomedP1x, -origP1y * transfomedP1x, - transfomedP1x},
                {0, 0, 0, origP1x, origP1y, 1, -origP1x * transfomedP1y, -origP1y * transfomedP1y, - transfomedP1y},
                {origP2x, origP2y, 1, 0, 0, 0, -origP2x * transfomedP2x, -origP1y * transfomedP2x, - transfomedP2x},
                {0, 0, 0, origP2x, origP2y, 1, -origP2x * transfomedP2y, -origP2y * transfomedP2y, - transfomedP2y},
                {origP3x, origP3y, 1, 0, 0, 0, -origP3x * transfomedP3x, -origP3y * transfomedP3x, - transfomedP3x},
                {0, 0, 0, origP3x, origP3y, 1, -origP3x * transfomedP3y, -origP3y * transfomedP3y, - transfomedP3y},
                {origP4x, origP4y, 1, 0, 0, 0, -origP4x * transfomedP4x, -origP4y * transfomedP4x, - transfomedP4x},
                {0, 0, 0, origP4x, origP4y, 1, -origP4x * transfomedP4y, -origP4y * transfomedP4y, - transfomedP4y},
        };
        Equations.solve(matrix, true);
        return new PerspectiveTransform(new double[][] {
                {-matrix[0][8], -matrix[1][8], -matrix[2][8]},
                {-matrix[3][8], -matrix[4][8], -matrix[5][8]},
                {-matrix[6][8], -matrix[7][8], 1}
        });
    }

    /**
     * The X coordinate scaling element of the 3x3
     * affine transformation matrix.
     *
     * @serial
     */
    private double m00;

    /**
     * The Y coordinate shearing element of the 3x3
     * affine transformation matrix.
     *
     * @serial
     */
    private double m10;

    /**
     * The X coordinate shearing element of the 3x3
     * affine transformation matrix.
     *
     * @serial
     */
    private double m01;

    /**
     * The Y coordinate scaling element of the 3x3
     * affine transformation matrix.
     *
     * @serial
     */
    private double m11;

    /**
     * The X coordinate of the translation element of the
     * 3x3 affine transformation matrix.
     *
     * @serial
     */
    private double m02;

    /**
     * The Y coordinate of the translation element of the
     * 3x3 affine transformation matrix.
     *
     * @serial
     */
    private double m12;

    private double m20;
    private double m21;
    private double m22;
    
    public PerspectiveTransform(double[][] matrix) {
        m00 = matrix[0][0];
        m01 = matrix[0][1];
        m02 = matrix[0][2];
        m10 = matrix[1][0];
        m11 = matrix[1][1];
        m12 = matrix[1][2];
        m20 = matrix[2][0];
        m21 = matrix[2][1];
        m22 = matrix[2][2];
    }

    /**
     * Create a new identify PerspectiveTransform.
     */
    public PerspectiveTransform() {
        m00 = m11 = m22 = 1.0;
    }

    public double[][] getMatrix(double[][] matrix) {
        if (matrix == null)
            matrix = new double[3][3];
        matrix[0][0] = m00;
        matrix[0][1] = m01;
        matrix[0][2] = m02;
        matrix[1][0] = m10;
        matrix[1][1] = m11;
        matrix[1][2] = m12;
        matrix[2][0] = m20;
        matrix[2][1] = m21;
        matrix[2][2] = m22;
        return matrix;
    }

    public PerspectiveTransform createInverse() {
        Point2D p1 = new Point2D.Double(0,0);
        Point2D p2 = new Point2D.Double(0,1);
        Point2D p3 = new Point2D.Double(1,1);
        Point2D p4 = new Point2D.Double(1,0);
        transform(p1, p1);
        transform(p2, p2);
        transform(p3, p3);
        transform(p4, p4);
        return getQuadToQuad(p1.getX(), p1.getY(), p2.getX(), p2.getY(), p3.getX(), p3.getY(), p4.getX(), p4.getY(),
                0, 0, 0, 1, 1, 1, 1, 0);
    }

    /**
     * Transform a series of point.
     *
     * @param src a series of (x,y) points
     * @param srcOffset the offset in `src` where the first point is read
     * @param dst an array to store a series of (x,y) points in
     * @param dstOffset the offset in `dst` to write the first point
     * @param numberOfPoints the number of points to read. (So this method will read/write `2 * numberOfPoints` elements).
     */
    public void transform(float[] src, int srcOffset, float[] dst, int dstOffset, int numberOfPoints) {
        for (int a = 0; a < numberOfPoints; a++) {
            double x = src[srcOffset + 2 * a];
            double y = src[srcOffset + 2 * a + 1];
            double w = m20 * x + m21 * y + m22;
            dst[dstOffset + 2 * a] = (float)((m00 * x + m01 * y + m02) / w);
            dst[dstOffset + 2 * a + 1] = (float)((m10 * x + m11 * y + m12) / w);
        }
    }

    /**
     * Transform the source point into the dest point.
     *
     * @param src the point this PerspectiveTransform is applied to. This must not
     *            be null. It may be the same as `dst`.
     * @param dst the object to store the transformed point in, or null.
     * @return the transformed point, which will be the same as `dst` if `dst` was non-null.
     */
    public Point2D transform(Point2D src, Point2D dst) {
        if (dst == null)
            dst = new Point2D.Double();
        double x = src.getX();
        double y = src.getY();
        double w = m20 * x + m21 * y + m22;
        dst.setLocation((m00 * x + m01 * y + m02) / w,
                        (m10 * x + m11 * y + m12) / w);
        return dst;
    }

    // the following is based on AffineTransform:

    // Round values to sane precision for printing
    // Note that Math.sin(Math.PI) has an error of about 10^-16
    private static double _matround(double matval) {
        return Math.rint(matval * 1E15) / 1E15;
    }

    /**
     * Returns a {@code String} that represents the value of this
     * {@link Object}.
     * @return a {@code String} representing the value of this
     * {@code Object}.
     * @since 1.2
     */
    public String toString() {
        return ("AffineTransform[["
                + _matround(m00) + ", "
                + _matround(m01) + ", "
                + _matround(m02) + "], ["
                + _matround(m10) + ", "
                + _matround(m11) + ", "
                + _matround(m12) + "], ["
                + _matround(m20) + ", "
                + _matround(m21) + ", "
                + _matround(m22) + "]]");
    }

    /**
     * Returns a copy of this {@code PerspectiveTransform} object.
     * @return an {@code Object} that is a copy of this
     * {@code PerspectiveTransform} object.
     * @since 1.2
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            // this shouldn't happen, since we are Cloneable
            throw new InternalError(e);
        }
    }
    /**
     * Returns the hashcode for this transform.
     * @return      a hash code for this transform.
     * @since 1.2
     */
    public int hashCode() {
        long bits = Double.doubleToLongBits(m00);
        bits = bits * 31 + Double.doubleToLongBits(m01);
        bits = bits * 31 + Double.doubleToLongBits(m02);
        bits = bits * 31 + Double.doubleToLongBits(m10);
        bits = bits * 31 + Double.doubleToLongBits(m11);
        bits = bits * 31 + Double.doubleToLongBits(m12);
        bits = bits * 31 + Double.doubleToLongBits(m20);
        bits = bits * 31 + Double.doubleToLongBits(m21);
        bits = bits * 31 + Double.doubleToLongBits(m22);
        return (((int) bits) ^ ((int) (bits >> 32)));
    }

    /**
     * Returns {@code true} if this {@code AffineTransform}
     * represents the same affine coordinate transform as the specified
     * argument.
     * @param obj the {@code Object} to test for equality with this
     * {@code AffineTransform}
     * @return {@code true} if {@code obj} equals this
     * {@code AffineTransform} object; {@code false} otherwise.
     * @since 1.2
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof PerspectiveTransform)) {
            return false;
        }

        PerspectiveTransform a = (PerspectiveTransform)obj;

        return ((m00 == a.m00) && (m01 == a.m01) && (m02 == a.m02) &&
                (m10 == a.m10) && (m11 == a.m11) && (m12 == a.m12) &&
                (m10 == a.m20) && (m21 == a.m21) && (m22 == a.m22));
    }
    /**
     * Use serialVersionUID from JDK 1.2 for interoperability.
     */
    @Serial
    private static final long serialVersionUID = 1330973210523860834L;

    /**
     * Writes default serializable fields to stream.
     *
     * @param  s the {@code ObjectOutputStream} to write
     * @throws IOException if an I/O error occurs
     */
    @Serial
    private void writeObject(java.io.ObjectOutputStream s)
            throws java.io.IOException
    {
        s.writeInt(0);
        s.writeDouble(m00);
        s.writeDouble(m01);
        s.writeDouble(m02);
        s.writeDouble(m10);
        s.writeDouble(m11);
        s.writeDouble(m12);
        s.writeDouble(m20);
        s.writeDouble(m21);
        s.writeDouble(m22);
    }

    /**
     * Reads the {@code ObjectInputStream}.
     *
     * @param  s the {@code ObjectInputStream} to read
     * @throws ClassNotFoundException if the class of a serialized object could
     *         not be found
     * @throws IOException if an I/O error occurs
     */
    @Serial
    private void readObject(java.io.ObjectInputStream s)
            throws java.lang.ClassNotFoundException, java.io.IOException
    {
        int internalVersion = s.readInt();
        if (internalVersion == 0) {
            m00 = s.readDouble();
            m01 = s.readDouble();
            m02 = s.readDouble();
            m10 = s.readDouble();
            m11 = s.readDouble();
            m12 = s.readDouble();
            m20 = s.readDouble();
            m21 = s.readDouble();
            m22 = s.readDouble();
        } else {
            // we can't parse this object because it was written with newer (or forked) code
            throw new UnsupportedEncodingException("Unsupported internal version: " + internalVersion);
        }
    }
}
