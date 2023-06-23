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

import com.pump.image.pixel.ImagePixelIterator;

import java.util.List;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.LinkedList;

/**
 * This uses the Python "pillow" project to scale JPG images. This object uses another Process to execute a Python
 * script.
 * </p>
 * <p>
 * I'm keeping this class around as a reference, but I'm not going to encourage anyone to use it for two reasons:
 * <ul><li>The performance of this class is bad. To be clear: the performance of the Python script itself can be great.
 * When I asked this script (even from within Java) to create a thumbnail for 200 JPG images: it took an average of
 * 4.035 ms per file (and this included converting the resulting file to a BufferedImage). But the
 * ThumbnailGenerator interface isn't designed to convert entire directories. When I
 * ask it to convert only one file at a time: it took an average of 67 ms per file. (The Scaling class in this
 * project, by comparison, consistently averaged around 7.495 ms for the same file.)</li>
 * <li>This class requires a lot of dependencies (Python, Pillow), so it's not going to work on some
 * machines.</li></ul>
 *
 * <p>
 * The performance I observed when I converted an entire directory of images is so inspiring I want to
 * keep this around as a reference, though.
 * </p>
 *
 * See https://pillow.readthedocs.io/en/stable/reference/Image.html
 * See https://github.com/mickleness/pumpernickel/issues/83
 */
public class PythonPillowThumbnailGenerator implements ThumbnailGenerator {
    @Override
    public BufferedImage createThumbnail(File file, int requestedMaxImageSize) throws Exception {
        String filename = file.getName().toLowerCase();
        if (!(filename.endsWith(".jpg") || filename.endsWith(".jpeg")))
            return null;

        int i = filename.lastIndexOf(".");
        List<File> filesToDelete = new LinkedList<>();
        File thumbnailFile = createUniqueFile(file.getParentFile(), filename.substring(0,i) + "-thumbnail.jpg");
        filesToDelete.add(thumbnailFile);

        if (requestedMaxImageSize == ThumbnailGenerator.MAX_SIZE_UNDEFINED)
            requestedMaxImageSize = ThumbnailGenerator.MAX_SIZE_DEFAULT;

        try {
            // @formatter:off
            String pyStr =
                    "from PIL import Image\n" +
                            "import glob, os\n" +
                            "\n" +
                            "os.chdir( \"" + file.getParentFile().getAbsolutePath() + "\" )\n" +
                            "\n" +
                            "size = " + requestedMaxImageSize + ", " + requestedMaxImageSize + "\n" +
                            "\n" +
                            "for infile in glob.glob(\"" + file.getName() + "\"):\n" +
                            "    file, ext = os.path.splitext(infile)\n" +
                            "    with Image.open(infile) as im:\n" +
                            "        im.thumbnail(size)\n" +
                            "        im.save(\"" + thumbnailFile.getName() + "\", \"JPEG\")\n" +
                            "";
            // @formatter:on


            File pyFile = createUniqueFile(file.getParentFile(), "thumbnail.py");
            try (FileOutputStream fileOut = new FileOutputStream(pyFile)) {
                try (OutputStreamWriter writer = new OutputStreamWriter(fileOut,
                        "UTF-8")) {
                    writer.write(pyStr);
                }
            }
            filesToDelete.add(pyFile);

            ProcessBuilder pb = new ProcessBuilder(
                    "/usr/local/Cellar/python@3.9/3.9.9/Frameworks/Python.framework/Versions/3.9/bin/python3.9",
                    pyFile.getAbsolutePath());
            Process process = pb.start();
            int exitCode = process.waitFor();
            log(System.out, process.getInputStream());
            log(System.err, process.getErrorStream());
            if (exitCode != 0)
                throw new RuntimeException("exit code = " + exitCode);

            return ImagePixelIterator.createBufferedImage(thumbnailFile);
        } finally {
            for (File f : filesToDelete) {
                if (f.exists())
                    f.delete();
            }
        }
    }

    private File createUniqueFile(File dir, String filename) {
        int i = filename.lastIndexOf(".");
        if (i == -1)
            throw new IllegalArgumentException(filename + " should include file extension");
        String filenameWithoutExt = filename.substring(0, i);
        String filenameExt = filename.substring(i);
        File newFile = new File(dir, filename);
        if (newFile.exists()) {
            int ctr = 2;
            while (newFile.exists()) {
                newFile = new File(filenameWithoutExt + " " + ctr + filenameExt);
                ctr++;
            }
        }
        return newFile;
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