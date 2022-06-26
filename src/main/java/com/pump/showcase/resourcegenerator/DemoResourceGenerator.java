/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://raw.githubusercontent.com/mickleness/pumpernickel/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.showcase.resourcegenerator;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;

import com.pump.graphics.vector.VectorImage;

/**
 * Create files/data used in the HTML documentation for several showcase demos.
 */
public abstract class DemoResourceGenerator {

	public static void main(String[] args) throws Exception {
		DemoResourceContext context = new DemoResourceContext();

		try {
			DemoResourceGenerator[] gs = new DemoResourceGenerator[] {
					new VectorImageDemoResourceGenerator(),
					new ThumbnailGeneratorDemoResourceGenerator(),
					new ShadowRendererDemoResourceGenerator(),
					new Transition2DDemoResourceGenerator(),
					new Transition3DDemoResourceGenerator() };

			for (DemoResourceGenerator g : gs) {
				long time = System.currentTimeMillis();
				System.out.println(g.getClass().getSimpleName() + " Running:");
				g.run(context);
				time = System.currentTimeMillis() - time;
				System.out.println("(" + time + " ms)");
				System.out.println();
			}
		} finally {
			System.out.println("Finished");
			System.exit(0);
		}
	}

	public abstract void run(DemoResourceContext context) throws Exception;

	/**
	 * Split a String into several rows of text. This is intended to break up a
	 * large base64 block of text into multiple rows.
	 * 
	 * @param str
	 *            a large String
	 * @param charLimit
	 *            the maximum number of characters in a row
	 * @return a list of Strings that can be combined together to recreate the
	 *         input.
	 */
	protected List<String> splitRows(String str, int charLimit) {
		List<String> returnValue = new LinkedList<>();
		while (str.length() > 0) {
			String row = str.substring(0, Math.min(str.length(), charLimit));
			returnValue.add(row);
			str = str.substring(row.length());
		}
		return returnValue;
	}

	/**
	 * Write a VectorImage to the console in Base64 encoding. This method also
	 * includes an internal debugger flag to write PNG and JVG files, but that
	 * should only be used in testing.
	 */
	protected void writeImage(VectorImage image, String name) throws Exception {
		boolean writeFiles = false;

		if (writeFiles) {
			File jvgFile = new File(name + ".jvg");
			try (FileOutputStream fileOut = new FileOutputStream(jvgFile)) {
				image.save(fileOut);
			}
			System.out.println("Chart: " + jvgFile.getAbsolutePath());

			// make a PNG version just for quick human reference:
			BufferedImage bi = image.toBufferedImage();
			File pngFile = new File(name + ".png");
			ImageIO.write(bi, "png", pngFile);
			System.out.println("Chart: " + pngFile.getAbsolutePath());
		}

		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream()) {
			image.save(byteOut);
			byte[] bytes = byteOut.toByteArray();
			String str = new String(Base64.getEncoder().encode(bytes));
			System.out.println("Base64 encoding of \"" + name + "\" jvg:");
			System.out.println(str);
		}
	}
}