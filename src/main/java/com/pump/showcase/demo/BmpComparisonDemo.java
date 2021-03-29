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
package com.pump.showcase.demo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.pump.awt.DemoPaintable;
import com.pump.image.bmp.BmpDecoder;
import com.pump.image.bmp.BmpEncoder;
import com.pump.image.pixel.PixelIterator;

/**
 * This demos the BmpEncoder and BmpDecoder.
 * <p>
 * Here is a sample screenshot of this showcase demo:
 * <p>
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/showcase/BmpComparisonDemo.png"
 * alt="A screenshot of the BmpComparisonDemo.">
 */
public class BmpComparisonDemo extends ShowcaseChartDemo {
	private static final long serialVersionUID = 1L;

	static final int SAMPLE_COUNT = 10;

	private static final String OPERATION_ENCODE = "Encode";
	private static final String OPERATION_DECODE = "Decode";
	private static final String IMPLEMENTATION_IMAGEIO = "ImageIO";
	private static final String IMPLEMENTATION_PUMP = "com.pump";

	private static BufferedImage SAMPLE_IMAGE;
	private static File SAMPLE_FILE;

	private static BufferedImage getSampleImage() {
		if (SAMPLE_IMAGE == null) {
			SAMPLE_IMAGE = new BufferedImage(800, 600,
					BufferedImage.TYPE_INT_RGB);
			Graphics2D g = SAMPLE_IMAGE.createGraphics();
			Color[] colors = new Color[] { new Color(0xffeaa7),
					new Color(0x55efc4) };
			DemoPaintable.paint(g, SAMPLE_IMAGE.getWidth(),
					SAMPLE_IMAGE.getHeight(), colors, "BMP");
			g.dispose();
		}
		return SAMPLE_IMAGE;
	}

	private static byte[] getSampleBytes() throws Exception {
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream()) {
			BmpEncoder.write(getSampleImage(), byteOut);
			return byteOut.toByteArray();
		}
	}

	static class MeasurementRunnable extends TimeMemoryMeasurementRunnable {
		boolean usePumpClasses;
		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		byte[] sampleFileBytes;

		public MeasurementRunnable(Map<String, Map<String, SampleSet>> data,
				String operation, String implementation) throws Exception {
			super(data, operation, implementation);
			usePumpClasses = implementation.equals(IMPLEMENTATION_PUMP);
			if (operation.equals(OPERATION_DECODE)) {
				sampleFileBytes = getSampleBytes();
			}
		}

		private void encodeBMP() throws Exception {
			bOut.reset();
			BufferedImage bi = getSampleImage();
			if (usePumpClasses) {
				BmpEncoder.write(bi, bOut);
			} else {
				ImageIO.write(bi, "bmp", bOut);
			}
		}

		private void decodeBMP() throws Exception {
			try (InputStream in = new ByteArrayInputStream(sampleFileBytes)) {
				if (usePumpClasses) {
					BmpDecoder.readImage(in);
				} else {
					ImageIO.read(in);
				}
			}
		}

		@Override
		protected void runSample() {
			try {
				for (int a = 0; a < 30; a++) {
					if (operation.equals(OPERATION_DECODE)) {
						decodeBMP();
					} else {
						encodeBMP();
					}
				}
			} catch (RuntimeException e) {
				throw e;
			} catch (Throwable t) {
				throw new RuntimeException(t);
			}
		}
	}

	@Override
	protected Collection<Runnable> getMeasurementRunnables(
			Map<String, Map<String, SampleSet>> data) {
		String[] operations = new String[] { OPERATION_ENCODE,
				OPERATION_DECODE };
		String[] implementations = new String[] { IMPLEMENTATION_IMAGEIO,
				IMPLEMENTATION_PUMP };
		List<Runnable> returnValue = new ArrayList<>(
				SAMPLE_COUNT * operations.length * implementations.length);

		try {
			for (String operation : operations) {
				for (String implementation : implementations) {
					Runnable r = new MeasurementRunnable(data, operation,
							implementation);
					for (int sample = 0; sample < SAMPLE_COUNT; sample++) {
						returnValue.add(r);
					}
				}
			}
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return returnValue;
	}

	@Override
	public String getTitle() {
		return "BmpEncoder, BmpDecoder Demo";
	}

	@Override
	public String getSummary() {
		return "This compares the performance of a new BmpEncoder and BmpDecoder class with the analogous ImageIO encoder and decoder.\n\nAs of this writing the pump classes significantly outperform ImageIO classes in speed. Regarding memory usage: encoding shows a significant improvement, but decoding is nearly identical.";
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