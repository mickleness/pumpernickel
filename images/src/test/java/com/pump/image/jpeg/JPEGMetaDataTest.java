package com.pump.image.jpeg;

import com.pump.image.pixel.ImagePixelIterator;
import com.pump.image.pixel.ImageType;
import junit.framework.TestCase;
import org.junit.Test;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class JPEGMetaDataTest extends TestCase {

    /**
     * Test the APP0DataReader's ability to parse thumbnails
     */
    @Test
    public void testAPP0Thumbnail() throws IOException {
        for (ImageType imageType : ImageType.values(true)) {
            if (!imageType.isOpaque())
                continue;
            for (ImageType thumbnailType : ImageType.values(true)) {
                byte[] jpegData = writeJPEG(imageType, thumbnailType);
                BufferedImage thumbnail = JPEGMetaData.getThumbnail(new ByteArrayInputStream(jpegData));
                try {
                    assertNotNull(thumbnail);
                } catch(Throwable t) {
                    System.err.println("imageType = " + imageType + " thumbnailType = " + thumbnailType);
                    throw t;
                }
            }
        }
    }

    private byte[] writeJPEG(ImageType imageType, ImageType thumbnailImageType) throws IOException {
        BufferedImage bi = new BufferedImage(1000, 1000, imageType.getCode());
        Graphics2D g = bi.createGraphics();
        g.setColor(Color.white);
        Path2D triangle = new Path2D.Float();
        triangle.moveTo(1000, 0);
        triangle.lineTo(1000, 1000);
        triangle.lineTo(0, 1000);
        g.fill(triangle);
        g.setColor(Color.green);
        g.setStroke(new BasicStroke(20));
        g.draw(new Line2D.Float(0,0,1000,1000));
        g.setColor(Color.red);
        g.fill(new Ellipse2D.Float(750-100, 250-100, 200, 200));
        g.setColor(Color.blue);
        g.fill(new Ellipse2D.Float(250-100, 750-100, 200, 200));
        g.dispose();

        Iterator<ImageWriter> iter = ImageIO.getImageWritersBySuffix("jpg");
        ImageWriter w = iter.next();
        try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream()) {
            BufferedImage thumbnail_argb = ImagePixelIterator.createBufferedImage(bi.getScaledInstance(200, 200, Image.SCALE_AREA_AVERAGING));
            BufferedImage thumbnail = thumbnailImageType.create(thumbnail_argb.getWidth(), thumbnail_argb.getHeight());
            Graphics2D g2 = thumbnail.createGraphics();
            g2.drawImage(thumbnail_argb, 0, 0, null);
            g2.dispose();

            IIOImage iioImage = new IIOImage(bi, Arrays.asList(thumbnail), null);

            ImageOutputStream stream = ImageIO.createImageOutputStream(byteOut);
            w.setOutput(stream);
            w.write(iioImage);
            return byteOut.toByteArray();
        }
    }
}
