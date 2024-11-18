package com.pump.image.jpeg;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * These are possible values of the {@link JPEGPropertyConstants#PROPERTY_ORIENTATION} metadata property.
 * These indicate that you should take the incoming pixel data and transform it
 * before presenting it to the user.
 */
public enum Orientation {
    NONE(1, false) {
        @Override
        public AffineTransform getTransform(Dimension d) {
            return new AffineTransform();
        }
    },
    FLIP_HORIZONTAL(2, false) {
        @Override
        public AffineTransform getTransform(Dimension d) {
            AffineTransform tx = new AffineTransform();
            double cx = ((double)d.width) / 2.0;
            tx.translate(cx,0);
            tx.scale(-1, 1);
            tx.translate(-cx, 0);
            return tx;
        }
    },
    ROTATE_180(3, false) {
        @Override
        public AffineTransform getTransform(Dimension d) {
            AffineTransform tx = new AffineTransform();
            double cx = ((double)d.width) / 2.0;
            double cy = ((double)d.height) / 2.0;
            tx.setToQuadrantRotation(2, cx, cy);
            return tx;
        }
    },
    FLIP_VERTICAL(4, false) {
        @Override
        public AffineTransform getTransform(Dimension d) {
            AffineTransform tx = new AffineTransform();
            double cy = ((double)d.height) / 2.0;
            tx.translate(0, cy);
            tx.scale(1, -1);
            tx.translate(0, -cy);
            return tx;
        }
    },
    ROTATE_COUNTERCLOCKWISE_FLIP_VERTICAL(5, true) {
        @Override
        public AffineTransform getTransform(Dimension d) {
            AffineTransform tx = new AffineTransform();
            tx.translate(0, d.width);
            tx.quadrantRotate(3);

            double cx = ((double)d.width) / 2.0;
            tx.translate(cx, 0);
            tx.scale(-1, 1);
            tx.translate(-cx, 0);

            return tx;
        }
    },
    ROTATE_CLOCKWISE(6, true) {
        @Override
        public AffineTransform getTransform(Dimension d) {
            AffineTransform tx = new AffineTransform();
            tx.translate(d.height, 0);
            tx.quadrantRotate(1);
            return tx;
        }
    },
    ROTATE_CLOCKWISE_FLIP_VERTICAL(7, true) {
        @Override
        public AffineTransform getTransform(Dimension d) {
            AffineTransform tx = new AffineTransform();
            tx.translate(d.height, 0);
            tx.quadrantRotate(1);

            double cx = ((double)d.width) / 2.0;
            tx.translate(cx, 0);
            tx.scale(-1, 1);
            tx.translate(-cx, 0);

            return tx;
        }
    },
    ROTATE_COUNTERCLOCKWISE(8, true) {
        @Override
        public AffineTransform getTransform(Dimension d) {
            AffineTransform tx = new AffineTransform();
            tx.translate(0, d.width);
            tx.quadrantRotate(3);
            return tx;
        }
    };

    public final int exifOrientationValue;

    /**
     * True if this Orientation switches the width and height.
     */
    public final boolean isTransposed;

    Orientation(int exifOrientationValue, boolean transposeSize) {
        this.exifOrientationValue = exifOrientationValue;
        this.isTransposed = transposeSize;
    }

    /**
     * Return the size an image will become after applying this Orientation.
     */
    public Dimension apply(Dimension imageSize) {
        if (isTransposed)
            return new Dimension(imageSize.height, imageSize.width);
        return new Dimension(imageSize.width, imageSize.height);
    }

    /**
     * Return an AffineTransform that transforms an image of a given size for this Orientation.
     */
    public abstract AffineTransform getTransform(Dimension imageSize);

    /**
     * Apply this Orientation to a given image.
     *
     * @param src the image we need to apply this orientation to.
     * @return an image that complies with this Orientation. This may return the incoming argument.
     */
    public BufferedImage apply(BufferedImage src) {
        return apply(src, null);
    }

    /**
     * Apply this Orientation to a given image.
     *
     * @param src the image we need to apply this orientation to.
     * @param dst the optional image we'll store the modified src image in. If this Orientation does not
     *            require writing a new (dst) image: then this argument is ignored.
     * @return an image that complies with this Orientation. This may return the incoming argument.
     */
    public BufferedImage apply(BufferedImage src, BufferedImage dst) {
        AffineTransform tx = getTransform(new Dimension(src.getWidth(),src.getHeight()));
        if (tx.isIdentity())
            return src;

        if (isTransposed) {
            if (dst == null) {
                dst = new BufferedImage(src.getHeight(), src.getWidth(), src.getType());
            } else if (dst.getWidth() < src.getHeight() || dst.getHeight() < src.getWidth()) {
                throw new IllegalArgumentException("the dst image was " + dst.getWidth() + "x" + dst.getHeight()+", but it needed to be at least " + src.getHeight() + "x"+ src.getWidth());
            }
        } else {
            // TODO: it would be nice if we didn't always allocate a new image here.
            // FLIP_HORIZONTAL works if we reuse src, but ROTATE_180 does not. We could
            // try a pixel conversion here and see if it's faster. (Maybe multithread, too?)
            if (dst == null) {
                dst = new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
            } else if (dst.getWidth() < src.getWidth() || dst.getHeight() < src.getHeight()) {
                throw new IllegalArgumentException("the dst image was " + dst.getWidth() + "x" + dst.getHeight()+", but it needed to be at least " + src.getWidth() + "x"+ src.getHeight());
            }
        }
        Graphics2D g = dst.createGraphics();
        g.transform(tx);
        g.drawImage(src, 0, 0, null);
        g.dispose();
        return dst;
    }
}