package com.pump.showcase.wikiresources;

import com.pump.desktop.temp.TempFileManager;
import com.pump.image.bmp.BmpDecoder;
import com.pump.image.bmp.BmpEncoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Random;

/**
 * This compares the time it takes for ImageIO vs Pumpernickel classes to read/write BMP images.
 */
public class BmpComparison {

    public enum Model {
        IMAGE_IO("ImageIO") {
            @Override
            public void encode(BufferedImage image, File file) throws Exception {
                ImageIO.write(image, "bmp", file);
            }

            @Override
            public BufferedImage decode(File file) throws Exception {
                return ImageIO.read(file);
            }
        },
        PUMPERNICKEL("Pumpernickel") {
            @Override
            public void encode(BufferedImage image, File file) throws Exception {
                BmpEncoder.write(image, file);
            }

            @Override
            public BufferedImage decode(File file) throws Exception {
                return BmpDecoder.read(file);
            }
        };

        final String name;

        Model(String name) {
            this.name = name;
        }

        public abstract void encode(BufferedImage image, File file) throws Exception;
        public abstract BufferedImage decode(File file) throws Exception;

        @Override
        public String toString() {
            return name;
        }
    }

    public static void main(String[] args) throws Exception {
        TempFileManager.initialize("BmpComparison");
        System.out.println("Running " + BmpComparison.class.getSimpleName());
        System.out.println("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version"));
        long[] samples = new long[20];

//        ImageIO can't handle reading or writing ARGB images, so there's no point in
//        comparing performance here
//        for (boolean opaque : new boolean[] {true, false} ) {

        for (boolean opaque : new boolean[] { true } ) {
            String opaqueStr = opaque ? "RGB" : "ARGB";

            BufferedImage bi = createBufferedImage(4000, 3000, opaque);
            File bmpFile = TempFileManager.get().createFile("sample", "bmp");

            System.out.println("\nReading:");
            for (Model model : Model.values()) {
                for (int sampleIndex = 0; sampleIndex < samples.length; sampleIndex++) {
                    model.encode(bi, bmpFile);
                    samples[sampleIndex] = System.currentTimeMillis();
                    for (int ctr = 0; ctr < 50; ctr++) {
                        model.decode(bmpFile);
                    }
                    samples[sampleIndex] = System.currentTimeMillis() - samples[sampleIndex];
                }
                Arrays.sort(samples);
//                System.out.println(model +"-" + opaqueStr + "\t" + samples[samples.length/2]);
                System.out.println(model + "\t" + samples[samples.length/2]);
            }

            System.out.println("\nWriting:");
            for (Model model : Model.values()) {
                for (int sampleIndex = 0; sampleIndex < samples.length; sampleIndex++) {
                    samples[sampleIndex] = System.currentTimeMillis();
                    for (int ctr = 0; ctr < 50; ctr++) {
                        model.encode(bi, bmpFile);
                    }
                    samples[sampleIndex] = System.currentTimeMillis() - samples[sampleIndex];
                }
                Arrays.sort(samples);
//                System.out.println(model +"-" + opaqueStr + "\t" + samples[samples.length/2]);
                System.out.println(model + "\t" + samples[samples.length/2]);
            }
        }
    }

    private static BufferedImage createBufferedImage(int width, int height, boolean opaque) {
        int imageType = opaque ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        BufferedImage bi = new BufferedImage(width, height, imageType);
        int[] row = new int[width];
        Random random = new Random(0);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                row[x] = random.nextInt(0xffffff) + (random.nextInt(0xff) << 24);
            }
            bi.getRaster().setDataElements(0, y, width, 1, row);
        }
        return bi;
    }
}
