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
package com.pump.showcase.demo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Label;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.imageio.ImageIO;

import com.pump.desktop.temp.TempFileManager;
import com.pump.image.ImageLoader;
import com.pump.image.ImageSize;
import com.pump.image.pixel.PixelIterator;
import com.pump.showcase.chart.ChartDataGenerator;
import com.pump.showcase.chart.PerformanceChartPanel;

public class ImageLoaderDemo extends ShowcaseResourceExampleDemo<File> {

	private static final long serialVersionUID = 1L;

	public static String PARAMETER_MODEL = "model";

	public enum LoaderModel {
		IMAGE_IO("ImageIO") {

			@Override
			public BufferedImage load(File file) throws Exception {
				return ImageIO.read(file);
			}
		},
		MEDIA_TRACKER("MediaTracker") {
			int idCtr = 0;

			@Override
			public BufferedImage load(File file) throws Exception {
				MediaTracker mediaTracker = new MediaTracker(new Label());
				Image image = Toolkit.getDefaultToolkit()
						.createImage(file.getAbsolutePath());
				mediaTracker.addImage(image, idCtr++);
				mediaTracker.waitForAll();
				image.flush();

				return null;
			}
		},
		IMAGE_LOADER("ImageLoader") {

			@Override
			public BufferedImage load(File file) throws Exception {
				return ImageLoader.createImage(file);
			}
		};

		final String name;

		LoaderModel(String name) {
			this.name = name;
		}

		public abstract BufferedImage load(File file) throws Exception;

		@Override
		public String toString() {
			return name;
		}
	}

	static class ImageLoaderChartDataGenerator implements ChartDataGenerator {
		final File file;

		// how many times we load the image to measure the time it takes
		// it's important this figure is small when the image is large (so the
		// samples don't take forever to collect), and large when the image is
		// small (so they can show statistically significant differences).
		final int loopCount;

		public ImageLoaderChartDataGenerator(File file, int loopCount) {
			this.file = file;
			this.loopCount = loopCount;
		}

		@Override
		public List<Map<String, Object>> getParameters() {
			List<Map<String, Object>> returnValue = new ArrayList<>();

			for (LoaderModel m : LoaderModel.values()) {
				Map<String, Object> p = new HashMap<>();
				p.put(PARAMETER_MODEL, m);
				p.put(PerformanceChartPanel.PARAMETER_NAME, m.toString());
				p.put(PerformanceChartPanel.PARAMETER_CHART_NAME, "Time");
				returnValue.add(p);
			}

			return returnValue;

		}


		public void runSample(Map<String, Object> parameters)
				throws Exception {
			LoaderModel model = (LoaderModel) parameters.get(PARAMETER_MODEL);

			boolean multithreadTest = false;

			if (multithreadTest) {
				// TODO: figure out why this performs so differently.

				// when multithreadTest = true, my results resemble:

				// *** Time
				// ImageLoader = 577
				// ImageIO = 612
				// MediaTracker = 732

				// when multithreadTest = false, my results resemble:

				// *** Time
				// ImageLoader = 989
				// ImageIO = 1357
				// MediaTracker = 883

				if (model == LoaderModel.MEDIA_TRACKER) {
					MediaTracker mediaTracker = new MediaTracker(new Label());
					Image[] images = new Image[loopCount];
					for (int a = 0; a < loopCount; a++) {
						images[a] = Toolkit.getDefaultToolkit()
								.createImage(file.getAbsolutePath());
						mediaTracker.addImage(images[a], a);
					}
					mediaTracker.waitForAll();
					for (Image image : images) {
						image.flush();
					}
				} else {
					// the default Toolkit code uses 4 "Image Fetcher" threads,
					// so to compare performance we should have 4 threads too.
					ExecutorService executor = Executors.newFixedThreadPool(4);
					List<Future<?>> futures = new ArrayList<>(100);
					for (int a = 0; a < loopCount; a++) {
						futures.add(executor.submit(new Runnable() {
							public void run() {

								try {
									model.load(file);
								} catch (Exception e) {
									throw new RuntimeException(e);
								}

							}
						}));
					}
					executor.shutdown();
					for (Future<?> future : futures) {
						future.get();
					}
				}
			} else {
				for (int i = 0; i < loopCount; i++) {
					model.load(file);
				}
			}
		}

	}

	public static BufferedImage createSampleImage(boolean includeAlphaChannel) {
		BufferedImage bi = new BufferedImage(800, 600,
				includeAlphaChannel ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bi.createGraphics();
		Random r = new Random(0);
		Color[] colors = new Color[] { Color.red, Color.gray, Color.green,
				Color.blue, Color.pink, Color.magenta, Color.cyan, Color.blue,
				Color.white };
		for (int a = 0; a < 100; a++) {
			Color color = colors[r.nextInt(colors.length)];
			g.setColor(color);
			int radius = r.nextInt(20) + 20;
			Ellipse2D circle = new Ellipse2D.Float(r.nextInt(800 + radius) - radius,
					r.nextInt(600 + radius) - radius,
					radius, radius);
			g.fill(circle);
		}
		// a visual marker of the top-left corner:
		g.setColor(Color.orange);
		g.fillRect(0,0,20,20);

		g.dispose();
		return bi;
	}

	PerformanceChartPanel perfChartPanel;
	File sampleFile;

	public ImageLoaderDemo() {
		super(File.class, false, "png", "jpg", "jpeg");

		perfChartPanel = new PerformanceChartPanel(
				"ImageLoader Performance Results");

		examplePanel.add(perfChartPanel);

	}

	@Override
	protected void setDefaultResourcePath() {
		try {
			if (sampleFile == null) {
				BufferedImage sampleImage = createSampleImage(false);
				sampleFile = TempFileManager.get().createFile("sampleImage",
						"jpg");
				ImageIO.write(sampleImage, "jpg", sampleFile);
			}
			resourcePathField.setText(sampleFile.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
			super.setDefaultResourcePath();
		}
	}

	@Override
	public String getTitle() {
		return "ImageLoader Demo";
	}

	@Override
	public String getSummary() {
		return "The ImageLoader converts Images to BufferedImages.";
	}

	@Override
	public URL getHelpURL() {
		return VectorImageDemo.class.getResource("imageLoader.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "ImageLoader", "MediaTracker", "PixelIterator" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { ImageLoader.class, PixelIterator.class };
	}

	@Override
	protected void refreshFile(File file, String resourceStr) {

		Dimension imgSize = ImageSize.get(file);
		int area = imgSize.width * imgSize.height;

		int loopCount;
		if (area > 3000000) {
			loopCount = 10;
		} else if (area > 500000) {
			loopCount = 25;
		} else {
			loopCount = 100;
		}

		perfChartPanel.setChartDescription(
				"The \"Time\" chart shows the median time it took each model to prepare this file "
						+ loopCount
						+ " times. The \"Memory\" chart shows the memory allocated to read this file once.");
		ChartDataGenerator dataGenerator = new ImageLoaderChartDataGenerator(
				file, loopCount);
		perfChartPanel.reset(dataGenerator);
	}

}