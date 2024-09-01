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

import javax.imageio.ImageReader;
import javax.imageio.spi.ImageReaderSpi;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.util.Locale;

class JPEGThumbnailImageReaderSpi extends ImageReaderSpi {

    public JPEGThumbnailImageReaderSpi() {
        inputTypes = new Class<?>[] { ImageInputStream.class };
        pluginClassName = JPEGThumbnailImageReader.class.getName();

        vendorName = "Pumpernickel";
        version = "1.0";

        // these are copied from the JPEGImageReaderSpi
        suffixes = new String[] {"jpg", "jpeg"};
        MIMETypes = new String[] {"image/jpeg"};
        names = new String[] {"JPEG", "jpeg", "JPG", "jpg"};
    }

    @Override
    public boolean canDecodeInput(Object source) throws IOException {
        return false;
    }

    @Override
    public ImageReader createReaderInstance(Object extension) throws IOException {
        return new JPEGThumbnailImageReader(this);
    }

    @Override
    public String getDescription(Locale locale) {
        return null;
    }
}