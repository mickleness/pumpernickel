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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Random;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

import com.pump.graphics.vector.VectorImage;
import com.pump.showcase.chart.LineChartRenderer;
import com.pump.showcase.demo.ImageLoaderDemo;
import com.pump.showcase.demo.ImageLoaderDemo.LoaderModel;

/**
 * This creates a line chart showing the performance of 3 different image
 * loading approaches as the width of an image increases.
 * <p>
 * This was originally meant to explore why ImageIO is mysteriously bad for some
 * JPGs but not others. (Unfortunately: that question is still unanswered.)
 */
public class ImageLoaderDemoResourceGenerator extends DemoResourceGenerator {
	double jpegQuality;

	public ImageLoaderDemoResourceGenerator(double jpegQuality) {
		this.jpegQuality = jpegQuality;
	}

	@Override
	public void run(DemoResourceContext context) throws Exception {
		ImageLoaderProfiler profiler = new ImageLoaderProfiler(jpegQuality);
		profiler.run();
		LineChartRenderer renderer = new LineChartRenderer(
				profiler.results.data, "Image Width",
				"Execution Time (ms) to Read 10 Times");
		VectorImage img = new VectorImage();
		renderer.paint(img.createGraphics(), 600, 400);
		writeImage(img, "ImageLoaderDemo-" + jpegQuality);
	}
}

/**
 * This compares the performance of different ShadowRenderers as the kernel
 * radius increases.
 * <p>
 * This class includes the UI and the comparison logic.
 */
class ImageLoaderProfiler {

	static class RunSample implements Runnable {
		ImageLoaderDemo.LoaderModel model;
		int imageWidth;
		ProfileResults profileResults;
		double jpegQuality;

		public RunSample(ProfileResults profileResults,
				ImageLoaderDemo.LoaderModel model, int imageWidth,
				double jpegQuality) {
			this.model = model;
			this.profileResults = profileResults;
			this.imageWidth = imageWidth;
			this.jpegQuality = jpegQuality;
		}

		public void run() {
			try {
				long[] times = new long[12];
				File imageFile = createImageFile();
				try {
					for (int a = 0; a < times.length; a++) {
						System.gc();
						System.runFinalization();
						System.gc();
						System.runFinalization();

						times[a] = System.currentTimeMillis();
						for (int b = 0; b < 10; b++) {
							model.load(imageFile);
						}
						times[a] = System.currentTimeMillis() - times[a];
					}
					Arrays.sort(times);
					profileResults.store(model.toString(), imageWidth,
							times[times.length / 2]);
				} finally {
					imageFile.delete();
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		private File createImageFile() throws Exception {
			BufferedImage bi = new BufferedImage(imageWidth, 1000,
					BufferedImage.TYPE_INT_RGB);
			Random random = new Random(0);
			Graphics2D g = bi.createGraphics();
			double scaleX = ((double) bi.getWidth()) / 1000.0;
			double scaleY = ((double) bi.getHeight()) / 1000.0;
			g.scale(scaleX, scaleY);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g.setStroke(new BasicStroke(15));

			for (int a = 0; a < 200; a++) {
				Ellipse2D e = new Ellipse2D.Double(random.nextInt(1000),
						random.nextInt(1000), 40 + random.nextInt(200),
						40 + random.nextInt(200));
				int rgb1 = random.nextInt(0xffffff);
				int rgb2 = random.nextInt(0xffffff);
				g.setPaint(new GradientPaint((float) e.getMinX(),
						(float) e.getMinY(), new Color(rgb1),
						(float) e.getMaxX(), (float) e.getMaxY(),
						new Color(rgb2)));
				g.draw(e);
			}

			g.dispose();

			File file = File.createTempFile("profiler", ".jpg");

			ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpg")
					.next();
			ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();
			jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			jpgWriteParam.setCompressionQuality((float) jpegQuality);

			ImageOutputStream outputStream = new FileImageOutputStream(file);
			jpgWriter.setOutput(outputStream);
			IIOImage outputImage = new IIOImage(bi, null, null);
			jpgWriter.write(null, outputImage, jpgWriteParam);
			jpgWriter.dispose();

			return file;
		}
	}

	ProfileResults results;
	double jpegQuality;

	public ImageLoaderProfiler(double jpegQuality) {
		this.jpegQuality = jpegQuality;
	}

	public ProfileResults run() {
		if (results == null) {
			results = createResults();
		}
		return results;
	}

	protected ProfileResults createResults() {
		ProfileResults returnValue = new ProfileResults();
		profile(returnValue);
		return returnValue;
	}

	private void profile(ProfileResults profileResults) {
		boolean outputResultsToConsole = true;
		System.out.println("## jpegQuality = " + jpegQuality);

		try {
			for (int imageWidth = 200; imageWidth <= 4000; imageWidth += 200) {
				System.out.println("imageWidth = " + imageWidth);
				for (LoaderModel model : LoaderModel.values()) {
					new RunSample(profileResults, model, imageWidth,
							jpegQuality).run();
				}
			}

			if (outputResultsToConsole)
				profileResults.printTable();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
}