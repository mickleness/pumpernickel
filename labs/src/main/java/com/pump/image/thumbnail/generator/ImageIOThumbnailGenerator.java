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
package com.pump.image.thumbnail.generator;

import com.pump.awt.Dimension2D;
import com.pump.image.pixel.Scaling;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * This ThumbnailGenerator sometimes uses some subsampling to skip levels of detail we don't need.
 */
public class ImageIOThumbnailGenerator implements ThumbnailGenerator {
    boolean allowDownsampling;

    /**
     * Create a new ImageIOThumbnailGenerator that uses downsampling
     */
    public ImageIOThumbnailGenerator() {
        this(true);
    }

    public ImageIOThumbnailGenerator(boolean allowDownsampling) {
        this.allowDownsampling = allowDownsampling;
    }

    public boolean isAllowDownsampling() {
        return allowDownsampling;
    }

    @Override
    public BufferedImage createThumbnail(File file, int requestedMaxImageSize) throws Exception {
        try(ImageInputStream iis = ImageIO.createImageInputStream(file)) {
            Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
            while (iter.hasNext()) {
                ImageReader reader = iter.next();
                reader.setInput(iis);
                BufferedImage thumb = readThumbnail(reader, requestedMaxImageSize);
                if (thumb != null)
                    return thumb;
            }
        }

        try(ImageInputStream iis = ImageIO.createImageInputStream(file)) {
            Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
            while (iter.hasNext()) {
                ImageReader reader = iter.next();
                reader.setInput(iis);
                BufferedImage thumb = createDownsampledThumbnail(reader, requestedMaxImageSize);
                if (thumb != null)
                    return thumb;
            }
        }

        return null;
    }

    /**
     * This attempts to read an existing thumbnail from an ImageReader. If it can find a thumbnail
     * that is the same size as (or larger than) the requested size: then this grabs that
     * image and the Scaling class further reduces it to the requested size.
     *
     * I'm unable to test this method, because I never get an ImageReader that offers
     * to read a thumbnail.
     */
    private BufferedImage readThumbnail(ImageReader reader, int requestedMaxSize) throws Exception {
        // there's probably only one thumbnail, but just in case we'll be thorough:
        int numThumbnails = reader.getNumThumbnails(0);

        int bestThumbnailIndex = -1;
        int smallestSize = Integer.MAX_VALUE;
        int maxSize = 0;
        for (int thumbIndex = 0; thumbIndex < numThumbnails; thumbIndex++) {
            int w = reader.getThumbnailWidth(0, thumbIndex);
            int h = reader.getThumbnailHeight(0, thumbIndex);

            int k = Math.max(w, h);
            if (requestedMaxSize > 0) {
                if (k >= requestedMaxSize) {
                    // OK, this is viable. Now (if there are multiple thumbnails) we want the
                    // *smallest* viable thumbnail
                    if (k < smallestSize) {
                        bestThumbnailIndex = thumbIndex;
                        smallestSize = k;
                    }
                }
            } else {
                // if there is no requested size, let's return the largest thumbnail possible:
                if (k > maxSize) {
                    bestThumbnailIndex = thumbIndex;
                    maxSize = k;
                }
            }
        }

        if (bestThumbnailIndex != -1) {
            int w = reader.getThumbnailWidth(0, bestThumbnailIndex);
            int h = reader.getThumbnailHeight(0, bestThumbnailIndex);
            BufferedImage img = reader.readThumbnail(0, bestThumbnailIndex);

            if (requestedMaxSize > 0) {
                Dimension thumbSize = Dimension2D.scaleProportionally(new Dimension(w, h),
                        new Dimension(requestedMaxSize, requestedMaxSize));
                img = Scaling.scale(img, thumbSize, null, null);
            }
            return img;
        }

        return null;
    }

    private BufferedImage createDownsampledThumbnail(ImageReader reader, int requestedMaxSize) throws IOException {
        if (requestedMaxSize == ThumbnailGenerator.MAX_SIZE_UNDEFINED)
            requestedMaxSize = ThumbnailGenerator.MAX_SIZE_DEFAULT;

        int w = reader.getWidth(0);
        int h = reader.getHeight(0);

        Dimension thumbSize = Dimension2D.scaleProportionally(new Dimension(w, h),
                new Dimension(requestedMaxSize, requestedMaxSize));

        ImageReadParam param = reader.getDefaultReadParam();
        if (allowDownsampling) {
            int sourceXSubsampling = (int) (((double) w) / ((double) thumbSize.width));
            int sourceYSubsampling = (int) (((double) h) / ((double) thumbSize.height));

            // the higher the subsampling values: the more pixelated artifacts we see.
            // If we reduce the subsampling a little then the artifacts are reduced.
            // (But they're never gone completely.) To test: try using an image file with
            // lots of texture. Subtracting 2 is my compromise: it's not too pixelated,
            // but it should benefit from a reasonable amount of optimization.
            if (sourceXSubsampling > 2)
                sourceXSubsampling -= 2;
            if (sourceYSubsampling > 2)
                sourceYSubsampling -= 2;

            param.setSourceSubsampling(sourceXSubsampling, sourceYSubsampling, 0, 0);
        }
        BufferedImage img = reader.read(0, param);

        return Scaling.scale(img, thumbSize, null, null);
    }
}