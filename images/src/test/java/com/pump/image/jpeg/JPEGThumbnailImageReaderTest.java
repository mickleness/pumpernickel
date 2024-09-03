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
package com.pump.image.jpeg;

import java.util.*;

import com.pump.image.QBufferedImage;
import com.pump.image.QBufferedImageTest;
import junit.framework.TestCase;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;

public class JPEGThumbnailImageReaderTest extends TestCase {

    public void testThumbnail() throws Exception {
        URL jpegFile = QBufferedImageTest.class.getResource("IMG-20171107-WA0002.jpg");
        try (InputStream in = jpegFile.openStream()) {
            BufferedImage thumbnail = JPEGMetaData.getThumbnail(in);
            Objects.requireNonNull(thumbnail);
        }
    }

    public void testImageIOFails() {
        URL jpegFile = QBufferedImageTest.class.getResource("IMG-20171107-WA0002.jpg");
        Iterator iterator = ImageIO.getImageReadersBySuffix("jpeg");
        while(iterator.hasNext()) {
            ImageReader reader = (ImageReader)iterator.next();

            try {
                reader.setInput( ImageIO.createImageInputStream(jpegFile.openStream()) );
                BufferedImage thumbnail = reader.readThumbnail(0, 0);
                if (thumbnail != null) {
                    fail("ImageIO had ImageReaders capable of reading the JPEG thumbnail. This means the pump classes may be obsolete.");
                }
            } catch(Exception e) {
                // we expect this to fail
            }
        }
    }
}