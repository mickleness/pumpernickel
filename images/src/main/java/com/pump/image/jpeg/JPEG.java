package com.pump.image.jpeg;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicReference;

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
                public boolean isThumbnailAccepted(String markerName, int width, int height) {
                    return false;
                }

                @Override
                public void addProperty(String markerName, String propertyName, Object value) {
                    if (value instanceof Orientation orientation)
                        orientationRef.set(orientation);
                }

                @Override
                public void addThumbnail(String markerName, BufferedImage bi) {
                    // intentionally empty
                }

                @Override
                public void addComment(String markerName, String comment) {
                    // intentionally empty
                }

                @Override
                public void startFile() {
                    // intentionally empty
                }

                @Override
                public void endFile() {
                    // intentionally empty
                }

                @Override
                public void imageDescription(int bitsPerPixel, int width, int height, int numberOfComponents) {
                    returnValue.width = width;
                    returnValue.height = height;
                }

                @Override
                public void processException(Exception e, String markerCode) {
                    // intentionally empty
                }
            });
            if (returnValue.width == -1)
                return null;
            if (applyOrientation)
                return orientationRef.get().apply(returnValue);
            return returnValue;
        }
    }
}
