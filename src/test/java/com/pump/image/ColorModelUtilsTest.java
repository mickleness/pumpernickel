package com.pump.image;

import java.awt.image.BufferedImage;
import java.util.Map;
import java.util.TreeMap;

import junit.framework.TestCase;

public class ColorModelUtilsTest extends TestCase {

	public void testGetImageType() {
		Map<String, Integer> imageTypes = new TreeMap<>();

		imageTypes.put("TYPE_INT_ARGB", BufferedImage.TYPE_INT_ARGB);
		imageTypes.put("TYPE_INT_ARGB_PRE", BufferedImage.TYPE_INT_ARGB_PRE);
		imageTypes.put("TYPE_INT_BGR", BufferedImage.TYPE_INT_BGR);
		imageTypes.put("TYPE_INT_RGB", BufferedImage.TYPE_INT_RGB);
		imageTypes.put("TYPE_3BYTE_BGR", BufferedImage.TYPE_3BYTE_BGR);
		imageTypes.put("TYPE_BYTE_GRAY", BufferedImage.TYPE_BYTE_GRAY);
		imageTypes.put("TYPE_4BYTE_ABGR", BufferedImage.TYPE_4BYTE_ABGR);
		imageTypes.put("TYPE_4BYTE_ABGR_PRE",
				BufferedImage.TYPE_4BYTE_ABGR_PRE);

		for (Map.Entry<String, Integer> imageType : imageTypes.entrySet()) {
			BufferedImage bi = new BufferedImage(100, 100,
					imageType.getValue());
			int actualResult = ColorModelUtils
					.getBufferedImageType(bi.getColorModel());
			assertEquals(imageType.getKey(), imageType.getValue().intValue(),
					actualResult);
		}
	}
}
