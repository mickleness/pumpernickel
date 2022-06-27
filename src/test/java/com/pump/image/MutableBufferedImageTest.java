package com.pump.image;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Random;

import org.junit.Test;

public class MutableBufferedImageTest {

	@Test
	public void testEquals() {
		MutableBufferedImage bi1 = createSampleImage(60, 40,
				BufferedImage.TYPE_INT_ARGB);
		MutableBufferedImage bi2 = createSampleImage(60, 40,
				BufferedImage.TYPE_INT_ARGB);
		assertEquals(bi1, bi2);
		assertEquals(bi1.hashCode(), bi2.hashCode());

		// change the image type:
		bi2 = createSampleImage(60, 40, BufferedImage.TYPE_INT_RGB);
		assertFalse(bi1.equals(bi2));

		// change pixels
		for (int y = 0; y < bi1.getHeight(); y++) {
			for (int x = 0; x < bi1.getWidth(); x++) {
				bi2 = createSampleImage(60, 40, BufferedImage.TYPE_INT_ARGB);
				int argb = bi2.getRGB(x, y);
				argb++;
				bi2.setRGB(x, y, argb);
				assertFalse(bi1.equals(bi2));
			}
		}

		bi2 = createSampleImage(60, 40, BufferedImage.TYPE_INT_ARGB);
		bi2.setProperty("x", "y");
		assertFalse(bi1.equals(bi2));
	}

	@Test
	public void testSerialization() throws Exception {
		MutableBufferedImage bi = createSampleImage(60, 40,
				BufferedImage.TYPE_INT_ARGB);
		byte[] bytes = serialize(bi);
		MutableBufferedImage bi2 = (MutableBufferedImage) deserialize(bytes);
		assertEquals(bi, bi2);
	}

	private byte[] serialize(Serializable obj) throws IOException {
		try (ByteArrayOutputStream byteOut = new ByteArrayOutputStream()) {
			try (ObjectOutputStream objOut = new ObjectOutputStream(byteOut)) {
				objOut.writeObject(obj);
			}
			byteOut.flush();
			return byteOut.toByteArray();
		}
	}

	private Serializable deserialize(byte[] bytes) throws Exception {
		try (ByteArrayInputStream byteIn = new ByteArrayInputStream(bytes)) {
			try (ObjectInputStream objIn = new ObjectInputStream(byteIn)) {
				return (Serializable) objIn.readObject();
			}
		}
	}

	private MutableBufferedImage createSampleImage(int width, int height,
			int type) {
		MutableBufferedImage returnValue = new MutableBufferedImage(width,
				height, type);
		Random r = new Random(0);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int argb = 0xff000000 + r.nextInt(0xffffff);
				returnValue.setRGB(x, y, argb);
			}
		}
		return returnValue;
	}

}
