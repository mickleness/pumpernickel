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
package com.pump.image;

import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.*;
import java.util.Random;

import static org.junit.Assert.*;

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

	// TODO: maybe make this a JDK test for JDK-4200096
	@Test
	public void testGetWidth() {
		PrintStream origErr = System.err;
		System.setErr(new PrintStream(origErr) {
			@Override
			public void println(Object x) {
				super.println(x);
				if (x instanceof Object)
					fail("Exception printed to System.err");
			}
		});
		try {
			BufferedImage rainbowImage = createSampleImage(500, 500, BufferedImage.TYPE_INT_RGB);
			Image img = rainbowImage.getScaledInstance(80, 60,
					Image.SCALE_SMOOTH);
			ImageObserver observer = new ImageObserver() {
				int width, height;
				@Override
				public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
					System.out.println("imageUpdate " + img + " " + x + " " + y + " " + width + " " + height + " " + toString(infoflags));
					if (width > 0)
						this.width = width;
					if (height > 0)
						this.height = height;
					return !(this.width > 0 && this.height > 0);
				}

				private String toString(int infoflags) {
					StringBuilder sb = new StringBuilder();
					if ( (infoflags & ImageObserver.WIDTH) > 0)
						sb.append("width ");
					if ( (infoflags & ImageObserver.HEIGHT) > 0)
						sb.append("height ");
					if ( (infoflags & ImageObserver.PROPERTIES) > 0)
						sb.append("properties ");
					if ( (infoflags & ImageObserver.SOMEBITS) > 0)
						sb.append("somebits ");
					if ( (infoflags & ImageObserver.FRAMEBITS) > 0)
						sb.append("framebits ");
					if ( (infoflags & ImageObserver.ALLBITS) > 0)
						sb.append("allbits ");
					if ( (infoflags & ImageObserver.ERROR) > 0)
						sb.append("error ");
					if ( (infoflags & ImageObserver.ABORT) > 0)
						sb.append("abort ");
					return sb.toString() + infoflags;
				}
			};
			int w = img.getWidth(observer);
			int h = img.getHeight(observer);
		} finally {
			System.setErr(origErr);
		}
	}
}