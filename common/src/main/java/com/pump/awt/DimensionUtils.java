package com.pump.awt;

import java.awt.*;
import java.awt.geom.Dimension2D;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Objects;

/**
 * This contains static helper methods for scaling Dimensions, and the {@link DimensionUtils.Double} class
 * as a double-backed <code>Dimension2D</code> implementation.
 */
public class DimensionUtils {


    /**
     * This is a convenience method to calculate how to scale an image
     * proportionally.
     *
     * @param originalSize
     *            the original image dimensions.
     * @param maxSize
     *            the maximum new dimensions.
     * @return dimensions that are <code>maxSize</code> or smaller.
     */
    public static Dimension scaleProportionally(Dimension originalSize,
                                                Dimension maxSize) {
        return scaleProportionally(originalSize, maxSize, false);
    }

    /**
     * This is a convenience method to calculate how to scale down an image
     * proportionally.
     *
     * @param originalSize
     *            the original image dimensions.
     * @param maxSize
     *            the maximum new dimensions.
     * @param returnNullForScalingUp
     *            if true then this method will return null when the original
     *            size is smaller than the maximum size. If false then this
     *            method always returns a non-null value, even if the return
     *            value scales up the original size.
     * @return dimensions that are <code>maxSize</code> or smaller.
     */
    public static Dimension scaleProportionally(Dimension originalSize,
                                                Dimension maxSize, boolean returnNullForScalingUp) {
        float widthRatio = ((float) maxSize.width)
                / ((float) originalSize.width);
        float heightRatio = ((float) maxSize.height)
                / ((float) originalSize.height);
        int w, h;
        if (widthRatio < heightRatio) {
            w = maxSize.width;
            h = (int) (widthRatio * originalSize.height);
        } else {
            h = maxSize.height;
            w = (int) (heightRatio * originalSize.width);
        }

        if (returnNullForScalingUp
                && (w > originalSize.width || h > originalSize.height))
            return null;

        return new Dimension(w, h);
    }

    /**
     * This is a convenience method to calculate how to scale down an image
     * proportionally.
     *
     * @param originalSize
     *            the original image dimensions.
     * @param maxSize
     *            the maximum new dimensions.
     * @return dimensions that are <code>maxSize</code> or smaller.
     */
    public static Double scaleProportionally(
            Dimension2D originalSize,
            Dimension2D maxSize) {
        double widthRatio = maxSize.getWidth() / originalSize.getWidth();
        double heightRatio = maxSize.getHeight() / originalSize.getHeight();
        double w, h;
        if (widthRatio < heightRatio) {
            w = maxSize.getWidth();
            h = widthRatio * originalSize.getHeight();
        } else {
            h = maxSize.getHeight();
            w = heightRatio * originalSize.getWidth();
        }
        return new Double(w, h);
    }

    /**
     * This is a Dimension2D that is backed by <code>doubles</code>.
     */
    public static class Double extends Dimension2D implements Serializable {
        protected double width, height;

        public Double(double w, double h) {
            width = w;
            height = h;
        }

        @Override
        public double getWidth() {
            return width;
        }

        @Override
        public double getHeight() {
            return height;
        }

        @Override
        public void setSize(double width, double height) {
            this.width = width;
            this.height = height;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Double aDouble = (Double) o;
            return java.lang.Double.compare(aDouble.width, width) == 0 && java.lang.Double.compare(aDouble.height, height) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(width, height);
        }

        @Override
        public String toString() {
            return "Double{" +
                    "width=" + width +
                    ", height=" + height +
                    '}';
        }

        @Override
        public Object clone() {
            return new Double(width, height);
        }

        private void writeObject(java.io.ObjectOutputStream out)
                throws IOException {
            out.writeInt(0);
            out.writeDouble(width);
            out.writeDouble(height);

        }
        private void readObject(java.io.ObjectInputStream in)
                throws IOException, ClassNotFoundException {
            int version = in.readInt();
            if (version == 0) {
                width = in.readDouble();
                height = in.readDouble();
            } else {
                throw new UnsupportedEncodingException("unsupported internal version: " + version);
            }
        }
    }
}
