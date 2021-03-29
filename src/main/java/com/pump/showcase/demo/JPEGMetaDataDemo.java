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

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

import com.pump.image.ImageLoader;
import com.pump.image.jpeg.JPEGMetaData;
import com.pump.image.thumbnail.Thumbnail;

/**
 * A simple demo for the {@link JPEGMetaData} class.
 * <p>
 * Here is a sample screenshot of this showcase demo:
 * <p>
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/showcase/JPEGMetaDataDemo.png"
 * alt="A screenshot of the JPEGMetaDataDemo.">
 */
public class JPEGMetaDataDemo extends ShowcaseChartDemo {
	private static final long serialVersionUID = 1L;

	private static final int SAMPLE_COUNT = 10;

	private final static URL url = ImageLoader.class.getResource("bridge3.jpg");

	private static final String IMPLEMENTATION_IMAGEIO_SCALED = "ImageIO Scaled";
	private static final String IMPLEMENTATION_JPEGMETADATA = "JPEGMetaData";
	private static final String IMPLEMENTATION_IMAGEIO_READER = "ImageIO Reader";

	static class MeasurementRunnable extends TimeMemoryMeasurementRunnable {

		ImageReader imageReader;

		public MeasurementRunnable(Map<String, Map<String, SampleSet>> data,
				String implementation) {
			super(data, null, implementation);
		}

		@Override
		protected void runSample() {
			try (InputStream in = url.openStream()) {
				if (implementation.equals(IMPLEMENTATION_JPEGMETADATA)) {
					if (JPEGMetaData.getThumbnail(in) == null)
						throw new UnsupportedOperationException(
								"JPEGMetaData could not read a thumbnail for \""
										+ url + "\"");
				} else if (implementation
						.equals(IMPLEMENTATION_IMAGEIO_SCALED)) {
					ImageReader reader = ALL_READERS
							.get(ALL_READERS.size() - 1);
					reader.setInput(ImageIO.createImageInputStream(in));
					BufferedImage image = reader.read(0);
					if (image == null) {
						throw new UnsupportedOperationException(
								"ImageIO could not read an image for \""
										+ url.toString() + "\"");
					}
					Thumbnail.Plain.create(image, new Dimension(128, 128));
				} else {
					readImageIOThumbnail(in);
				}
			} catch (RuntimeException e) {
				throw e;
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static void readImageIOThumbnail(InputStream in) throws Exception {
		ImageReader reader = ALL_READERS.get(ALL_READERS.size() - 1);
		reader.setInput(ImageIO.createImageInputStream(in));
		BufferedImage thumbnail = reader.readThumbnail(0, 0);
		if (thumbnail == null) {
			throw new UnsupportedOperationException(
					"ImageIO could not read a thumbnail for \"" + url.toString()
							+ "\"");
		}
	}

	static List<ImageReader> ALL_READERS = new ArrayList<>();
	static {
		Iterator<ImageReader> iterator = ImageIO
				.getImageReadersBySuffix("jpeg");
		while (iterator.hasNext()) {
			ImageReader reader = iterator.next();
			ALL_READERS.add(reader);
		}
	}

	@Override
	public String getTitle() {
		return "JPEGMetaData Demo";
	}

	@Override
	public String getSummary() {
		return "This compares the new JPEGMetaData with ImageIO when reading JPG thumbnails.";
	}

	@Override
	public URL getHelpURL() {
		return JPEGMetaDataDemo.class.getResource("jpegMetaDataDemo.html");
	}

	@Override
	public String[] getKeywords() {
		return new String[] { "jpeg", "jpg", "thumbnail", "preview", "exif",
				"performance" };
	}

	@Override
	public Class<?>[] getClasses() {
		return new Class[] { JPEGMetaData.class };
	}

	@Override
	protected Collection<Runnable> getMeasurementRunnables(
			Map<String, Map<String, SampleSet>> data) {
		String[] implementations = new String[] { IMPLEMENTATION_IMAGEIO_SCALED,
				IMPLEMENTATION_JPEGMETADATA, IMPLEMENTATION_IMAGEIO_READER };

		try (InputStream in = url.openStream()) {
			readImageIOThumbnail(in);
		} catch (Throwable t) {
			// this is what we expect most of the time: thumbnails aren't
			// supported by default
			implementations = new String[] { IMPLEMENTATION_IMAGEIO_SCALED,
					IMPLEMENTATION_JPEGMETADATA };
		}

		List<Runnable> returnValue = new ArrayList<>(
				SAMPLE_COUNT * implementations.length);
		for (String implementation : implementations) {
			Runnable r = new MeasurementRunnable(data, implementation);
			for (int sample = 0; sample < SAMPLE_COUNT; sample++) {
				returnValue.add(r);
			}
		}
		return returnValue;
	}
}