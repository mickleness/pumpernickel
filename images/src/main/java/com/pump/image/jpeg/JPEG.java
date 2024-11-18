package com.pump.image.jpeg;

import com.pump.awt.DimensionUtils;
import com.pump.image.pixel.Scaling;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;

/**
 * Static helper methods related exclusively to reading & writing JPEGs.
 */
public class JPEG {

    /**
     * Write an image to an output stream.
     * <p>
     * Normally ImageIO writes JPEG thumbnails using a JFIF APP0 marker segment, which guarantees
     * a lossless thumbnail but it constrains the thumbnail to 255x255. This method will switch to
     * a JFIF *extension* APP0 marker segment for larger thumbnails. This lets us embed a thumbnail
     * of arbitrary size as another JPEG. This offers better compression, but it uses the default
     * JPEG compression level (see com.sun.imageio.plugins.jpeg.JPEG.DEFAULT_QUALITY).
     * </p>
     *
     * @param out the stream to write the JPEG image to.
     * @param bufferedImage the large image to write.
     * @param thumbnail the thumbnail to encode with the image.
     * @param jpegQuality the JPEG quality from 0-1. Where 1 is lossless.
     */
    public static void write(OutputStream out, BufferedImage bufferedImage, BufferedImage thumbnail, float jpegQuality) throws IOException {
        Iterator<ImageWriter> iter = ImageIO.getImageWritersBySuffix("jpg");
        ImageWriter jpegWriter = iter.next();

        IIOImage iioImage = new IIOImage(bufferedImage,
                thumbnail == null ? Collections.emptyList() : Arrays.asList(thumbnail),
                null);

        ImageWriteParam param = jpegWriter.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        param.setCompressionQuality(jpegQuality);

        if (thumbnail != null && (thumbnail.getWidth() > 255 || thumbnail.getHeight() > 255) ) {
            // this complex / cryptic code lets us activate the JFIFthumbJPEG logic that lets us
            // embed larger thumbnails:
            ImageTypeSpecifier imageType = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);
            IIOMetadata metadata = jpegWriter.getDefaultImageMetadata(imageType, param);

            // String formatName = com.sun.imageio.plugins.jpeg.JPEG.nativeStreamMetadataFormatName
            String formatName = "javax_imageio_jpeg_image_1.0";

            IIOMetadataNode rootNode = new IIOMetadataNode(formatName);
            IIOMetadataNode JPEGvariety = new IIOMetadataNode();
            IIOMetadataNode markerSequenceNode = new IIOMetadataNode();
            rootNode.appendChild(JPEGvariety);
            rootNode.appendChild(markerSequenceNode);

            IIOMetadataNode jfifMarkerSegment = new IIOMetadataNode();
            JPEGvariety.appendChild(jfifMarkerSegment);

            IIOMetadataNode jfxx = new IIOMetadataNode("JFXX");
            jfifMarkerSegment.appendChild(jfxx);

            IIOMetadataNode app0JFXX = new IIOMetadataNode();
            // see https://en.wikipedia.org/wiki/JPEG_File_Interchange_Format#JFIF_extension_APP0_marker_segment
            app0JFXX.setAttribute("extensionCode", "16");
            jfxx.appendChild(app0JFXX);

            IIOMetadataNode JFIFthumbJPEG = new IIOMetadataNode("JFIFthumbJPEG");
            app0JFXX.appendChild(JFIFthumbJPEG);

            metadata.setFromTree(formatName, rootNode);
            iioImage.setMetadata(metadata);
        }

        ImageOutputStream stream = ImageIO.createImageOutputStream(out);
        jpegWriter.setOutput(stream);
        jpegWriter.write(iioImage);
    }

    /**
     * Return the dimensions of a JPEG image, or null if the dimensions couldn't be identified (which should never
     * happen for a valid JPEG file)
     *
     * @param applyOrientation if true then this method may take {@link JPEGMetaData#getOrientation()} into
     *                         account and return a value that is rotated 90 degrees. If false
     *                         then this ALWAYS returns the raw image size, even if that is not
     *                         how the image should be presented to the user.
     */
    public static Dimension getSize(File file, boolean applyOrientation) throws IOException {
        Dimension returnValue = new Dimension(-1, -1);
        try (FileInputStream in = new FileInputStream(file)) {
            AtomicReference<Orientation> orientationRef = new AtomicReference<>(Orientation.NONE);
            JPEGMetaData.read(in, new JPEGMetaDataListener() {

                @Override
                public void addProperty(String markerName, String propertyName, Object value) {
                    if (value instanceof Orientation orientation)
                        orientationRef.set(orientation);
                }

                @Override
                public void imageDescription(int bitsPerPixel, int width, int height, int numberOfComponents) {
                    returnValue.width = width;
                    returnValue.height = height;
                }
            });
            if (returnValue.width == -1)
                return null;
            if (applyOrientation)
                return orientationRef.get().apply(returnValue);
            return returnValue;
        }
    }

    /**
     * Read a JPEG image file. There are 3 main reasons to use this method instead of
     * {@link ImageIO#read(File)}:
     * <ul><li>This takes into account a JPEG's orientation. So this may rotate or flip what ImageIO would normally return.</li>
     * <li>This may use the {@link Scaling} class to avoid loading the entire JPEG image into memory.</li>
     * <li>This may automatically grab embedded thumbnails (if they match or exceed the `maxSize` argument).</li></ul>
     *
     * @param maxSize the optional maximum size to constrain the returned image to.
     *                For example: if the JPEG image file is 6000x4000, but the
     *                maximum size is 200x200, then this method will return an
     *                image that is 200x133. When non-null this uses the {@link Scaling}
     *                class, which tries (when possible) to scale an image on the fly
     *                (instead of reading the whole image into memory).
     */
    public static BufferedImage read(File file, Dimension maxSize) throws IOException {
        if (maxSize == null) {
            BufferedImage bi = ImageIO.read(file);
            Orientation orientation;
            try (FileInputStream fileIn = new FileInputStream(file)) {
                orientation = JPEGMetaData.getOrientation(fileIn);
            }
            return orientation.apply(bi);
        }

        BiFunction<Dimension, Boolean, Dimension> sizeFunction = (imageSize, isThumbnail) -> {
            Dimension reducedSize = DimensionUtils.scaleProportionally(imageSize, maxSize, true);
            if (!isThumbnail && reducedSize == null)
                return imageSize;

            return reducedSize;
        };
        return Scaling.scale(file, sizeFunction, null, null);
    }
}
