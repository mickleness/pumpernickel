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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;

import javax.imageio.ImageIO;

import com.pump.desktop.temp.TempFileManager;
import com.pump.image.ImageSize;
import com.pump.image.bmp.BmpDecoder;
import com.pump.image.bmp.BmpEncoder;
import com.pump.image.pixel.PixelIterator;
import com.pump.showcase.chart.ChartDataGenerator;
import com.pump.showcase.chart.PerformanceChartPanel;

/**
 * This demos the BmpEncoder and BmpDecoder.
 * <p>
 * Here is a sample screenshot of this showcase demo:
 * <p>
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/showcase/BmpComparisonDemo.png"
 * alt="A screenshot of the BmpComparisonDemo.">
 */
public class BmpComparisonDemo extends ShowcaseResourceExampleDemo<File> {

	// TODO: I'm seeing erratic results with this. Is there a way to gain confidence in the comparison?

	private static final long serialVersionUID = 1L;

	public static String PARAMETER_MODEL = "model";

	public static String PARAMETER_OPERATION = "operation";

	public static String OPERATION_ENCODE = "Encode";
	public static String OPERATION_DECODE = "Decode";

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

	static class BmpComparisonChartDataGenerator implements ChartDataGenerator {
		final File file;
		final BufferedImage image;
		final int loopCount;

		public BmpComparisonChartDataGenerator(File file, int loopCount) throws IOException {
			this.file = file;
			this.image = BmpDecoder.read(file);
			this.loopCount = loopCount;
		}

		@Override
		public List<Map<String, Object>> getParameters() {
			List<Map<String, Object>> returnValue = new ArrayList<>();

			for (String operation : new String[] {OPERATION_ENCODE, OPERATION_DECODE}) {
				for (Model m : Model.values()) {
					Map<String, Object> p = new HashMap<>();
					p.put(PARAMETER_MODEL, m);
					p.put(PARAMETER_OPERATION, operation);
					p.put(PerformanceChartPanel.PARAMETER_CHART_NAME, operation+" Time");
					p.put(PerformanceChartPanel.PARAMETER_NAME, m.toString());
					returnValue.add(p);
				}
			}

			return returnValue;

		}

		@Override
		public void runSample(Map<String, Object> parameters) throws Exception {
			Model model = (Model) parameters.get(PARAMETER_MODEL);
			String operation = (String) parameters.get(PARAMETER_OPERATION);

			for (int a = 0; a < loopCount; a++) {
				if (operation.equals(OPERATION_DECODE)) {
					model.decode(file);
				} else {
					File tmpFile = File.createTempFile("encode-test", ".bmp");
					try {
						model.encode(image, tmpFile);
					} finally {
						tmpFile.delete();
					}
				}
			}
		}

	}

	PerformanceChartPanel perfChartPanel;
	File sampleFile;

	public BmpComparisonDemo() {
		super(File.class, false, "bmp");

		perfChartPanel = new PerformanceChartPanel(
				"BMP Performance Results");

		examplePanel.add(perfChartPanel);
		perfChartPanel.setBackground(Color.orange);

	}

	@Override
	protected void setDefaultResourcePath() {
		try {
			if (sampleFile == null) {
				BufferedImage sampleImage = ImageLoaderDemo.createSampleImage(false);
				sampleFile = TempFileManager.get().createFile("sampleImage",
						"bmp");
				BmpEncoder.write(sampleImage, sampleFile);
			}
			resourcePathField.setText(sampleFile.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
			super.setDefaultResourcePath();
		}
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
		loopCount *= 3;

		perfChartPanel.setChartDescription(
				"These charts show the median time it took each model to prepare this image/file "
						+ NumberFormat.getInstance().format(loopCount)
						+ " times.");
		try {
			ChartDataGenerator dataGenerator = new BmpComparisonChartDataGenerator(
					file, loopCount);
			perfChartPanel.reset(dataGenerator);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getTitle() {
		return "BmpEncoder, BmpDecoder Demo";
	}

	@Override
	public String getSummary() {
		return "This compares the performance of a new BmpEncoder and BmpDecoder class with the analogous ImageIO encoder and decoder.";
	}

	@Override
	public URL getHelpURL() {
		return BmpComparisonDemo.class.getResource("BmpComparisonDemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "bmp", "image", "encoding", "decoding",
				"bufferedimage", "performance" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { BmpEncoder.class, BmpDecoder.class,
				PixelIterator.class };
	}
}