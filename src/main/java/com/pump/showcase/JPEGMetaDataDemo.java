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
package com.pump.showcase;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;

import com.pump.image.ImageLoader;
import com.pump.image.jpeg.JPEGMetaData;
import com.pump.image.thumbnail.Thumbnail;
import com.pump.util.Warnings;

/**
 * A simple demo for the {@link JPEGMetaData} class.
 * 
 */
public class JPEGMetaDataDemo extends ShowcaseChartDemo {
	private static final long serialVersionUID = 1L;

	private static final int SAMPLE_COUNT = 10;

	@Override
	public String getTitle() {
		return "JPEGMetaData Demo";
	}

	@Override
	public String getSummary() {
		return "This compares the new JPEGMetaData with ImageIO when reading JPG thumbnails.\n\nNote by default this trial is rigged, because the default installation of ImageIO does not support parsing JPEG thumbnails unless you have JAI also installed. (In this demo: we use ImageIO to load the full image and then scale it down to a thumbnail.)\n\nThe option to request a thumbnail fails with an exception.\n\nSo ... the broad point still stands that this class helps read thumbnails better than Java's default ImageIO classes alone, but these charts are also unfair and biased.";
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

	String GROUP_TIME = "Time";
	String GROUP_MEMORY = "Memory";
	long[] sampleTimes = new long[SAMPLE_COUNT];
	long[] sampleMemory = new long[SAMPLE_COUNT];
	Map<String, Map<String, Long>> data;
	List<ImageReader> readers;

	URL url = ImageLoader.class.getResource("bridge3.jpg");

	@Override
	protected Map<String, Map<String, Long>> collectData(int... params)
			throws Exception {
		if (data == null) {
			data = new LinkedHashMap<>();
			data.put(GROUP_TIME, new HashMap<String, Long>());
			data.put(GROUP_MEMORY, new HashMap<String, Long>());
		}

		int testType = params[1];
		int sampleIndex = params[0];

		System.runFinalization();
		System.gc();
		System.runFinalization();
		System.gc();
		sampleTimes[sampleIndex] = System.currentTimeMillis();
		sampleMemory[sampleIndex] = Runtime.getRuntime().freeMemory();

		String dataType = null;
		try {
			if (testType == 0) {
				dataType = "JPEGMetaData";
				try (InputStream in = url.openStream()) {
					if (JPEGMetaData.getThumbnail(in) == null)
						throw new UnsupportedOperationException(
								"JPEGMetaData could not read a thumbnail for \""
										+ url + "\"");
				}
			} else {
				int k = testType - 1;
				boolean useThumbnail = (k / readers.size()) >= 1;
				k = k % readers.size();
				ImageReader reader = readers.get(k);

				dataType = "ImageIO " + reader.getClass().getSimpleName();
				dataType += useThumbnail ? " as thumbnail" : " as full image";
				try (InputStream in = url.openStream()) {
					reader.setInput(ImageIO.createImageInputStream(in));
					if (useThumbnail) {
						BufferedImage thumbnail = reader.readThumbnail(0, 0);
						if (thumbnail == null) {
							throw new UnsupportedOperationException(
									"ImageIO could not read a thumbnail for \""
											+ url.toString() + "\"");
						}
					} else {
						BufferedImage image = reader.read(0);
						if (image == null) {
							throw new UnsupportedOperationException(
									"ImageIO could not read an image for \""
											+ url.toString() + "\"");
						}
						Thumbnail.Plain.create(image, new Dimension(128, 128));
					}
				}
			}

			sampleTimes[sampleIndex] = System.currentTimeMillis()
					- sampleTimes[sampleIndex];
			sampleMemory[sampleIndex] = sampleMemory[sampleIndex]
					- Runtime.getRuntime().freeMemory();

			if (sampleIndex == SAMPLE_COUNT - 1) {
				Arrays.sort(sampleTimes);
				Arrays.sort(sampleMemory);

				data.get(GROUP_TIME).put(dataType,
						sampleTimes[sampleTimes.length / 2]);
				data.get(GROUP_MEMORY).put(dataType,
						sampleMemory[sampleMemory.length / 2]);
			}
		} catch (Exception e) {
			// this shouldn't happen
			if (dataType == null)
				throw new IllegalStateException(e);

			String msg = Warnings.getStackTrace(e);
			msg = "An error occurred processing " + dataType + ":\n" + msg;
			Warnings.printOnce(msg);

			data.get(GROUP_TIME).put(dataType, BarChartRenderer.ERROR_CODE);
			data.get(GROUP_MEMORY).put(dataType, BarChartRenderer.ERROR_CODE);
		}
		return data;
	}

	@Override
	protected int[] getCollectDataParamLimits() {
		if (readers == null) {
			readers = new ArrayList<>();
			Iterator<ImageReader> iterator = ImageIO
					.getImageReadersBySuffix("jpeg");
			while (iterator.hasNext()) {
				ImageReader reader = iterator.next();
				readers.add(reader);
			}
		}
		return new int[] { SAMPLE_COUNT, 1 + 2 * readers.size() };
	}
}