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
package com.pump.showcase.resourcegenerator;

import com.pump.desktop.temp.TempFileManager;
import com.pump.geom.StarPolygon;
import com.pump.image.bmp.BmpDecoderIterator;
import com.pump.image.pixel.ImagePixelIterator;
import com.pump.image.pixel.ImageType;
import com.pump.image.pixel.Scaling;
import com.pump.io.FileInputStreamSource;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class ScalingComparison extends DemoResourceGenerator {

    public enum Model {
        /**
         * See https://bugs.openjdk.org/browse/JDK-6196792
         */
        GET_SCALED_INSTANCE("Image.getScaledInstance") {
            @Override
            public BufferedImage createThumbnail(File srcFile, int thumbnailWidth, int thumbnailHeight) {
                Image srcImage = createImage(srcFile);
                try {
                    Image scaledImage = srcImage.getScaledInstance(thumbnailWidth, thumbnailHeight, Image.SCALE_SMOOTH);
                    return ImagePixelIterator.createBufferedImage(scaledImage);
                } finally {
                    srcImage.flush();
                }
            }

            @Override
            public BufferedImage createThumbnail(BufferedImage srcImage, int thumbnailWidth, int thumbnailHeight) throws Exception {
                Image scaledImage = srcImage.getScaledInstance(thumbnailWidth, thumbnailHeight, Image.SCALE_SMOOTH);
                return ImagePixelIterator.createBufferedImage(scaledImage);
            }

            private Image createImage(File srcFile) {
                if (srcFile.getAbsolutePath().toLowerCase().endsWith("bmp")) {
                    return new BmpDecoderIterator.Source(new FileInputStreamSource(srcFile)).createImage();
                }
                return Toolkit.getDefaultToolkit().createImage(srcFile.getAbsolutePath());
            }
        },

        // the next two models are commented out because they require additional jars.
        // Feel free to uncomment them and re-add the jars for occasional testing/reporting.

//        /**
//         * See https://github.com/rkalla/imgscalr/
//         */
//        IMGSCALR("imgscalr") {
//            @Override
//            public BufferedImage createThumbnail(File srcFile, int thumbnailWidth, int thumbnailHeight) throws Exception {
//                BufferedImage bi = ImageIO.read(srcFile);
//                return createThumbnail(bi, thumbnailWidth, thumbnailHeight);
//            }
//
//            @Override
//            public BufferedImage createThumbnail(BufferedImage srcImage, int thumbnailWidth, int thumbnailHeight) throws Exception {
//                return Scalr.resize(srcImage, thumbnailWidth);
//            }
//        },
//
//        /**
//         * See https://github.com/coobird/thumbnailator
//         */
//        THUMBNAILATOR("Thumbnailator") {
//            @Override
//            public BufferedImage createThumbnail(File srcFile, int thumbnailWidth, int thumbnailHeight) throws Exception {
//                return Thumbnails.of(srcFile)
//                        .size(thumbnailWidth, thumbnailHeight).asBufferedImage();
//            }
//
//            @Override
//            public BufferedImage createThumbnail(BufferedImage srcImage, int thumbnailWidth, int thumbnailHeight) throws Exception {
//                return Thumbnails.of(srcImage)
//                        .size(thumbnailWidth, thumbnailHeight).asBufferedImage();
//            }
//        },
        GRAPHICS_UTILITIES("GraphicsUtilities") {

            /**
             * <p>
             * Returns a thumbnail of a source image.
             * </p>
             * <p>
             * The source and javadoc for this method are copied from
             * GraphicsUtilities.java, licensed under LGPL. I want to compare this
             * method against other methods in this class.
             * </p>
             *
             * @param image
             *            the source image
             * @param newWidth
             *            the width of the thumbnail
             * @param newHeight
             *            the height of the thumbnail
             * @return a new compatible <code>BufferedImage</code> containing a
             *         thumbnail of <code>image</code>
             * @throws IllegalArgumentException
             *             if <code>newWidth</code> is larger than the width of
             *             <code>image</code> or if code>newHeight</code> is larger than
             *             the height of
             *             <code>image or if one the dimensions is not &gt; 0</code>
             */
            @Override
            public BufferedImage createThumbnail(BufferedImage image, int newWidth, int newHeight) {
                int width = image.getWidth();
                int height = image.getHeight();

                if (newWidth >= width || newHeight >= height) {
                    throw new IllegalArgumentException("newWidth and newHeight cannot"
                            + " be greater than the image" + " dimensions");
                } else if (newWidth <= 0 || newHeight <= 0) {
                    throw new IllegalArgumentException(
                            "newWidth and newHeight must" + " be greater than 0");
                }

                BufferedImage thumb = image;

                do {
                    if (width > newWidth) {
                        width /= 2;
                        if (width < newWidth) {
                            width = newWidth;
                        }
                    }

                    if (height > newHeight) {
                        height /= 2;
                        if (height < newHeight) {
                            height = newHeight;
                        }
                    }

                    GraphicsConfiguration gc = GraphicsEnvironment
                            .getLocalGraphicsEnvironment().getDefaultScreenDevice()
                            .getDefaultConfiguration();
                    BufferedImage temp = gc.createCompatibleImage(width, height);

                    Graphics2D g2 = temp.createGraphics();
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2.drawImage(thumb, 0, 0, temp.getWidth(), temp.getHeight(), null);
                    g2.dispose();

                    thumb = temp;
                } while (width != newWidth || height != newHeight);

                return thumb;
            }

            /**
             * This is an adaptation of image that accepts a BufferedImage.
             * @param srcFile
             * @param thumbnailWidth
             * @param thumbnailHeight
             * @return
             * @throws Exception
             */
            @Override
            public BufferedImage createThumbnail(File srcFile, int thumbnailWidth, int thumbnailHeight) throws Exception {
                Image image = Toolkit.getDefaultToolkit().createImage(srcFile.getAbsolutePath());

                MediaTracker tracker = new MediaTracker(new Label());
                tracker.addImage(image, 0);
                tracker.waitForAll();

                int width = image.getWidth(null);
                int height = image.getHeight(null);

                if (thumbnailWidth >= width || thumbnailHeight >= height) {
                    throw new IllegalArgumentException("newWidth and newHeight cannot"
                            + " be greater than the image" + " dimensions");
                } else if (thumbnailWidth <= 0 || thumbnailHeight <= 0) {
                    throw new IllegalArgumentException(
                            "newWidth and newHeight must" + " be greater than 0");
                }

                Image thumb = image;

                do {
                    if (width > thumbnailWidth) {
                        width /= 2;
                        if (width < thumbnailWidth) {
                            width = thumbnailWidth;
                        }
                    }

                    if (height > thumbnailHeight) {
                        height /= 2;
                        if (height < thumbnailHeight) {
                            height = thumbnailHeight;
                        }
                    }

                    GraphicsConfiguration gc = GraphicsEnvironment
                            .getLocalGraphicsEnvironment().getDefaultScreenDevice()
                            .getDefaultConfiguration();
                    BufferedImage temp = gc.createCompatibleImage(width, height);

                    Graphics2D g2 = temp.createGraphics();
                    g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                            RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2.drawImage(thumb, 0, 0, temp.getWidth(), temp.getHeight(), null);
                    g2.dispose();

                    thumb = temp;
                } while (width != thumbnailWidth || height != thumbnailHeight);

                return (BufferedImage) thumb;
            }
        },
        PUMPERNICKEL("Pumpernickel") {
            @Override
            public BufferedImage createThumbnail(File srcFile, int thumbnailWidth, int thumbnailHeight) throws IOException {
                Dimension size = new Dimension(thumbnailWidth, thumbnailHeight);
                return Scaling.scale(srcFile, size, null, null);
            }

            @Override
            public BufferedImage createThumbnail(BufferedImage srcImage, int thumbnailWidth, int thumbnailHeight) throws Exception {
                Dimension size = new Dimension(thumbnailWidth, thumbnailHeight);
                return Scaling.scale(srcImage, size, null, null);
            }
        };

        final String name;

        Model(String name) {
            this.name = name;
        }

        public abstract BufferedImage createThumbnail(File srcFile, int thumbnailWidth, int thumbnailHeight) throws Exception;

        public abstract BufferedImage createThumbnail(BufferedImage srcImage, int thumbnailWidth, int thumbnailHeight) throws Exception;

        @Override
        public String toString() {
            return name;
        }
    }

    public static void main(String[] args) throws Exception {
        TempFileManager.initialize("ScalingComparison");
        System.out.println("Running " + ScalingComparison.class.getSimpleName());
        System.out.println("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version"));
        new ScalingComparison().run(null);
    }

    @Override
    public void run(DemoResourceContext context) throws Exception {
        long[] samples = new long[20];

        for (Model model : ScalingComparison.Model.values()) {
            System.out.print("\t" + model.name);
        }
        System.out.println();

        BufferedImage bi = createBufferedImage(2000, 1500);
        if (getAntialiasedPixels(bi) > 0)
            throw new AssertionError("There should be no antialiased pixels in the source image. (fraction = " + getAntialiasedPixels(bi) +")" ) ;

        BufferedImage lastImage = null;

        int[] imageTypes = new int[]{
                BufferedImage.TYPE_INT_BGR, BufferedImage.TYPE_INT_RGB, BufferedImage.TYPE_INT_ARGB, BufferedImage.TYPE_INT_ARGB_PRE,
                BufferedImage.TYPE_3BYTE_BGR, BufferedImage.TYPE_4BYTE_ABGR, BufferedImage.TYPE_4BYTE_ABGR_PRE
        };
        for (int imageType : imageTypes) {
            BufferedImage copy = createCopy(bi, imageType);
            System.out.print(ImageType.toString(imageType));
            for (ScalingComparison.Model model : ScalingComparison.Model.values()) {
                for (int sampleIndex = 0; sampleIndex < samples.length; sampleIndex++) {
                    samples[sampleIndex] = System.currentTimeMillis();
                    try {
                        for (int ctr = 0; ctr < 10; ctr++) {
                            lastImage = model.createThumbnail(copy, 80, 60);
                        }
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                    samples[sampleIndex] = System.currentTimeMillis() - samples[sampleIndex];
                }
                Arrays.sort(samples);
                System.out.print("\t" + samples[samples.length / 2]);

                if (getAntialiasedPixels(lastImage) < .5)
                    throw new AssertionError("There should be over 50% antialiased pixels in the scaled image. (fraction = " + getAntialiasedPixels(lastImage) +", model = " + model + ")" ) ;
            }

            System.out.println();
        }


        File pngFile = TempFileManager.get().createFile("sample", "png");
        File jpgFile = TempFileManager.get().createFile("sample", "jpg");
        File bmpFile = TempFileManager.get().createFile("sample", "bmp");
        try {
            ImageIO.write(bi, "png", pngFile);
            ImageIO.write(bi, "jpg", jpgFile);
            ImageIO.write(bi, "bmp", bmpFile);

            // bmpFiles fail for GET_SCALED_INSTANCE for multiple reasons:
            // 1. They are bottom-to-top, so the scaling filter (AreaAveragingScaleFilter)
            // automatically aborts to its "passthrough" default (ReplicateScaleFilter).
            // 2. That default does the wrong thing (I think). My code ends up throwing
            // an exception because it tries to produce a row of data where
            // the scansize = the width (for RGB byte data). I'm 90% sure this is a OpenJDK bug,
            // but as a rule I usually only worry about / report Java bugs that come up
            // in the real world...
            //                for (File file : new File[] { pngFile, jpgFile, bmpFile}) {

            for (File file : new File[]{pngFile, jpgFile}) {
                System.out.print(getFileExtension(file));
                for (ScalingComparison.Model model : ScalingComparison.Model.values()) {
                    for (int sampleIndex = 0; sampleIndex < samples.length; sampleIndex++) {
                        samples[sampleIndex] = System.currentTimeMillis();
                        try {
                            for (int ctr = 0; ctr < 10; ctr++) {
                                lastImage = model.createThumbnail(file, 80, 60);
                            }
                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                        samples[sampleIndex] = System.currentTimeMillis() - samples[sampleIndex];
                    }

                    if (getAntialiasedPixels(lastImage) < .5)
                        throw new AssertionError("There should be over 50% antialiased pixels in the scaled image. (fraction = " + getAntialiasedPixels(lastImage) +", model = " + model + ")" ) ;

                    Arrays.sort(samples);
                    System.out.print("\t" + samples[samples.length / 2]);
                }
                System.out.println();
            }
        } finally {
            pngFile.delete();
            jpgFile.delete();
            bmpFile.delete();
        }
    }

    /**
     * Return a fraction (from 0-1) of pixels in the argument that are antialiased.
     * <p>
     * The original source image in this test uses fewer than 10 colors, so any pixel in the thumbnail
     * that isn't one of those 10 colors is considered an "antialiased" pixel that must be the result of
     * smooth scaling.
     * </p>
     */
    private static float getAntialiasedPixels(BufferedImage bi) {
        if (bi.getType() != BufferedImage.TYPE_INT_RGB)
            bi = createCopy(bi, BufferedImage.TYPE_INT_RGB);
        int[] row = new int[bi.getWidth()];
        int[] rgbs = new int[COLORS.length];
        for (int a = 0; a < rgbs.length; a++) {
            rgbs[a] = COLORS[a].getRGB() & 0xffffff;
        }
        int antialiasedPixels = 0;
        for (int y = 0; y < bi.getHeight(); y++) {
            bi.getRaster().getDataElements(0, y, row.length, 1, row);
            for (int x = 0; x < row.length; x++) {
                int rgb = row[x] & 0xffffff;
                boolean match = false;
                for (int j = 0; j < rgbs.length && !match; j++) {
                    if (rgb == rgbs[j]) {
                        match = true;
                    }
                }
                if (!match)
                    antialiasedPixels++;
            }
        }

        return ((float)antialiasedPixels) / ((float)( bi.getWidth() * bi.getHeight() ));
    }

    private static Color[] COLORS = new Color[] {
            Color.white, Color.black, Color.red, Color.orange, Color.yellow, Color.green, Color.cyan, Color.blue
    };

    private static BufferedImage createBufferedImage(int width, int height) {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bi.createGraphics();

        // we'll draw as if we're drawing to a 100x100 rectangle:
        g.scale( ((double)width) / 100.0, ((double) height) / 100.0);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        g.setStroke(new BasicStroke(2));
        Random r = new Random(0);

        for (int a = 0; a < 1000; a++) {
            Color c = COLORS[r.nextInt(COLORS.length)];
            g.setColor(c);
            int x = r.nextInt(90);
            int y = r.nextInt(90);

            switch (r.nextInt(4)) {
                case 0:
                    g.drawRect(x, y, 10, 10);
                    break;
                case 1:
                    g.drawOval(x, y, 10, 10);
                    break;
                case 2:
                    g.draw(new RoundRectangle2D.Double(x, y, 10, 10, 4, 4));
                    break;
                case 3:
                    StarPolygon star = new StarPolygon(5);
                    star.setCenter(x + 5, y + 5);
                    g.draw(star);
                    break;
            }
        }
        g.dispose();
        return bi;
    }

    private static BufferedImage createCopy(BufferedImage bi, int imageType) {
        BufferedImage copy = new BufferedImage(bi.getWidth(), bi.getHeight(), imageType);
        Graphics2D g = copy.createGraphics();
        g.drawImage(bi, 0, 0, null);
        g.dispose();
        return copy;
    }

    private static String getFileExtension(File file) {
        String s = file.getAbsolutePath();
        int i = s.lastIndexOf('.');
        return s.substring(i + 1);
    }
}