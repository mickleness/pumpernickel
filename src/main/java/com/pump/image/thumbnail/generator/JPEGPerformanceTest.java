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

import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import com.pump.awt.Dimension2D;
import com.pump.image.pixel.ImagePixelIterator;
import com.pump.image.pixel.ImageType;
import com.pump.image.pixel.Scaling;

/**
 * This mainly exists to show off the Python/pillow jpeg performance.
 */
public class JPEGPerformanceTest implements AutoCloseable {

    enum Goal {
        CREATE_BUFFERED_IMAGE, CREATE_JPEG_FILE
    };

    interface Model {
        /**
         * Create a thumbnail for every JPG file in a directory.
         * <p>
         * If this creates Files they should exist in the directory given, and
         * they should have "thumbnail" in the filename (so we can delete them
         * between tests).
         *
         * @param jpgDir
         *            a directory of JPG files to create thumbnails for
         * @param goal
         *            whether we should focus on creating a BufferedImage (with
         *            or without a File) or on creating a File (with or without
         *            a BufferedImage).
         */
        void createThumbnails(File jpgDir, Goal goal) throws Exception;
    }

    static class PumpScalingModel implements Model {

        @Override
        public void createThumbnails(File jpgDir, Goal goal) throws Exception {
            int ctr = 0;
            for (File jpgFile : jpgDir.listFiles()) {
                if (goal == Goal.CREATE_JPEG_FILE) {
                    BufferedImage bi = Scaling.scaleProportionally(jpgFile,
                            new Dimension(128, 128), ImageType.BYTE_BGR, null);
                    File thumbnailFile = new File(jpgDir,
                            "thumbnail" + (ctr++) + ".jpg");
                    if (!ImageIO.write(bi, "jpg", thumbnailFile))
                        throw new IOException("ImageIO.write failed for "
                                + thumbnailFile.getAbsolutePath());
                } else {
                    BufferedImage bi = Scaling.scaleProportionally(jpgFile,
                            new Dimension(128, 128), null, null);
                }
            }
        }

    }

    static class PythonPillowModel implements Model {

        @Override
        public void createThumbnails(File jpgDir, Goal goal) throws Exception {
            // @formatter:off
            String pyStr =
                    "from PIL import Image\n" +
                            "import glob, os\n" +
                            "\n" +
                            "os.chdir( \"" + jpgDir.getAbsolutePath() + "\" )\n" +
                            "\n" +
                            "size = 128, 128\n" +
                            "\n" +
                            "for infile in glob.glob(\"*.jpg\"):\n" +
                            "    file, ext = os.path.splitext(infile)\n" +
                            "    with Image.open(infile) as im:\n" +
                            "        im.thumbnail(size)\n" +
                            "        im.save(file + \".thumbnail.jpg\", \"JPEG\")\n" +
                            "";
            // @formatter:on

            File pyFile = new File(jpgDir, "thumbnail.py");
            try (FileOutputStream fileOut = new FileOutputStream(pyFile)) {
                try (OutputStreamWriter writer = new OutputStreamWriter(fileOut,
                        "UTF-8")) {
                    writer.write(pyStr);
                }
            }

            ProcessBuilder pb = new ProcessBuilder(
                    "/usr/local/Cellar/python@3.9/3.9.9/Frameworks/Python.framework/Versions/3.9/bin/python3.9",
                    pyFile.getAbsolutePath());
            Process process = pb.start();
            int exitCode = process.waitFor();
            log(System.out, process.getInputStream());
            log(System.err, process.getErrorStream());
            if (exitCode != 0)
                throw new RuntimeException("exit code = " + exitCode);

            if (goal == Goal.CREATE_BUFFERED_IMAGE) {
                for (File child : jpgDir.listFiles()) {
                    if (child.getName().contains("thumbnail.jpg")) {
                        ImageIO.read(child);
                    }
                }
            }
        }

        private static void log(PrintStream out, InputStream in)
                throws UnsupportedEncodingException, IOException {
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(in, "UTF-8"))) {
                String str = br.readLine();
                while (str != null) {
                    out.println(str);
                    str = br.readLine();
                }
            }
        }
    }

    static class ScaledInstanceModel implements Model {

        @Override
        public void createThumbnails(File jpgDir, Goal goal) throws Exception {
            Dimension maxSize = new Dimension(128, 128);
            int ctr = 0;
            for (File jpgFile : jpgDir.listFiles()) {
                BufferedImage bi = ImageIO.read(jpgFile);
                Dimension imgSize = new Dimension(bi.getWidth(),
                        bi.getHeight());
                Dimension destSize = Dimension2D.scaleProportionally(imgSize,
                        maxSize);
                Image img = bi.getScaledInstance(destSize.width,
                        destSize.height, Image.SCALE_SMOOTH);
                BufferedImage thumbnail = ImagePixelIterator
                        .createBufferedImage(img, ImageType.BYTE_BGR);

                if (goal == Goal.CREATE_JPEG_FILE) {
                    File thumbnailFile = new File(jpgDir,
                            "thumbnail" + (ctr++) + ".jpg");
                    if (!ImageIO.write(thumbnail, "jpg", thumbnailFile))
                        throw new IOException("ImageIO.write failed for "
                                + thumbnailFile.getAbsolutePath());
                }
                img.flush();
            }
        }

    }

    /**
     * The number of JPG files to convert to thumbnails. These images are all
     * identical, but we use several just to increase the execution time. (And
     * we use duplicates just in case some engine is tempted to cache data
     * behind our backs; hopefully using unique files will trick any potential
     * caching optimization.)
     */
    static final int FILE_COUNT = 200;

    /**
     * The number of times we repeat the test. We'll only report the median
     * sample time. (In case some tests happen to execute in an unusually long
     * or short period of time.)
     */
    static final int SAMPLE_COUNT = 10;

    public static void main(String[] args) throws IOException, Exception {
        FileDialog fd = new FileDialog(new Frame(), "Open JPG");
        fd.setFilenameFilter(new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                name = name.toLowerCase();
                return name.endsWith(".jpeg") || name.endsWith(".jpg");
            }

        });
        fd.pack();
        fd.setVisible(true);

        File jpgFile = new File(fd.getDirectory() + fd.getFile());

        System.out.println("I created " + FILE_COUNT + " copies of "
                + jpgFile.getName() + ".");

        Model[] models = new Model[] { new ScaledInstanceModel(),
                new PumpScalingModel(), new PythonPillowModel() };

        try (JPEGPerformanceTest test = new JPEGPerformanceTest(jpgFile)) {
            for (Goal goal : Goal.values()) {
                System.out.println();
                System.out.println("Current goal: " + goal);
                long[][] samples = new long[models.length][SAMPLE_COUNT];
                StringBuilder sb = new StringBuilder();
                for (int modelIndex = 0; modelIndex < models.length; modelIndex++) {
                    sb.append(models[modelIndex].getClass().getSimpleName()
                            + "\t");
                }
                System.out.println(sb.toString());

                sb = new StringBuilder();
                for (int modelIndex = 0; modelIndex < models.length; modelIndex++) {
                    for (int sampleIndex = 0; sampleIndex < SAMPLE_COUNT; sampleIndex++) {

                        // delete any leftover old thumbnails:
                        for (File child : test.dir.listFiles()) {
                            if (child.getName().contains("thumbnail")) {
                                delete(child);
                            }
                        }

                        // time our actual op:
                        long startTime = System.currentTimeMillis();
                        models[modelIndex].createThumbnails(test.dir, goal);
                        samples[modelIndex][sampleIndex] = System
                                .currentTimeMillis() - startTime;
                    }
                    Arrays.sort(samples[modelIndex]);
                    double msTime = ((double) samples[modelIndex][SAMPLE_COUNT
                            / 2]) / ((double) FILE_COUNT);
                    sb.append(msTime + "\t");
                }
                System.out.println(sb.toString());
            }
        }
        System.exit(0);
    }

    /**
     * This contains FILE_COUNT copies of our sample image
     */
    File dir;

    public JPEGPerformanceTest(File jpgFile) throws IOException {
        dir = File.createTempFile("jpg", "dir");
        if (dir.exists()) {
            if (!dir.delete())
                throw new IOException(
                        "File.delete() failed for " + dir.getAbsolutePath());
            if (!dir.mkdirs())
                throw new IOException(
                        "File.mkdirs() failed for " + dir.getAbsolutePath());
        }
        for (int a = 0; a < FILE_COUNT; a++) {
            copy(jpgFile, new File(dir, "sample" + a + ".jpg"));
        }
    }

    private void copy(File src, File dst) throws IOException {
        if (!dst.createNewFile())
            throw new IOException(
                    "File.createNewFile() failed for " + dst.getAbsolutePath());
        byte[] block = new byte[65536];
        try (FileInputStream fileIn = new FileInputStream(src)) {
            try (FileOutputStream fileOut = new FileOutputStream(dst)) {
                int t = fileIn.read(block);
                while (t != -1) {
                    fileOut.write(block, 0, t);
                    t = fileIn.read(block);
                }
            }
        }
    }

    @Override
    public void close() throws Exception {
        delete(dir);
    }

    private static void delete(File file) throws IOException {
        // I vaguely recall deleting directories having issues sometimes? just
        // to be safe:
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                delete(child);
            }
        }

        if (!file.delete())
            throw new IOException("File.createNewFile() failed for "
                    + file.getAbsolutePath());
    }
}