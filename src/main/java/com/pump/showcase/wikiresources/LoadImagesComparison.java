package com.pump.showcase.wikiresources;

import com.pump.image.ImageSize;
import com.pump.image.QBufferedImage;
import com.pump.image.pixel.ImagePixelIterator;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.net.URL;
import java.util.Arrays;

/**
 * This compares the time it takes to load a certain JPG.
 * (Where "load" mostly means "make sure the image is fully loaded". It doesn't
 * necessarily mean we end up with a BufferedImage.)
 */
public class LoadImagesComparison {

    public enum LoaderModel {
        IMAGE_IO("ImageIO") {

            @Override
            public BufferedImage load(URL url) throws Exception {
                return ImageIO.read(url);
            }
        },
        MEDIA_TRACKER("MediaTracker") {
            int idCtr = 0;

            @Override
            public BufferedImage load(URL url) throws Exception {
                Image image = Toolkit.getDefaultToolkit()
                        .createImage(url);

                MediaTracker mediaTracker = new MediaTracker(new Label());
                mediaTracker.addImage(image, idCtr++);
                mediaTracker.waitForAll();
                image.flush();

                return null;
            }
        },

        PIXEL_GRABBER("PixelGrabber") {

            @Override
            public BufferedImage load(URL url) throws Exception {
                Image image = Toolkit.getDefaultToolkit()
                        .createImage(url);
                Dimension size = ImageSize.get(image);
                PixelGrabber grabber = new PixelGrabber(image, 0, 0, size.width, size.height, false);
                grabber.grabPixels();
                Object pixels = grabber.getPixels();
                if (pixels instanceof int[]) {
                    int[] intPixels = (int[]) pixels;
                    return new QBufferedImage(grabber.getColorModel(), size.width, size.height, intPixels);
                } else {
                    byte[] bytePixels = (byte[]) pixels;
                    return new QBufferedImage(grabber.getColorModel(), size.width, size.height, bytePixels);
                }
            }
        },

        PUMPERNICKEL("ImagePixelIterator") {

            @Override
            public BufferedImage load(URL url) throws Exception {
                return ImagePixelIterator.createBufferedImage(url);
            }
        };

        final String name;

        LoaderModel(String name) {
            this.name = name;
        }

        public abstract BufferedImage load(URL url) throws Exception;

        @Override
        public String toString() {
            return name;
        }
    }

    public static void main(String[] args) throws Exception {
        System.out.println("Running " + LoadImagesComparison.class.getSimpleName());
        System.out.println("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version"));
        long[] samples = new long[20];

        URL url = LoadImagesComparison.class.getResource("pexels-irina-iriser-1408221.jpg");

        for (LoaderModel model : LoaderModel.values()) {
            System.currentTimeMillis();
            for (int sampleIndex = 0; sampleIndex < samples.length; sampleIndex++) {
                samples[sampleIndex] = System.currentTimeMillis();
                for (int ctr = 0; ctr < 50; ctr++) {
                    model.load(url);
                }
                samples[sampleIndex] = System.currentTimeMillis() - samples[sampleIndex];
            }
            Arrays.sort(samples);
//                System.out.println(model +"-" + opaqueStr + "\t" + samples[samples.length/2]);
            System.out.println(model + "\t" + samples[samples.length/2]);
        }
    }
}
