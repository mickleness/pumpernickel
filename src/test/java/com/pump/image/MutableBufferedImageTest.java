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

import com.pump.image.pixel.ImageType;
import org.junit.Test;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageObserver;
import java.io.*;
import java.util.Hashtable;
import java.util.Random;

import static org.junit.Assert.*;

public class MutableBufferedImageTest {

	@Test
	public void testEquals() {
		MutableBufferedImage bi1 = createSampleMutableImage(60, 40,
				BufferedImage.TYPE_INT_ARGB);
		MutableBufferedImage bi2 = createSampleMutableImage(60, 40,
				BufferedImage.TYPE_INT_ARGB);
		assertEquals(bi1, bi2);
		assertEquals(bi1.hashCode(), bi2.hashCode());

		// change the image type:
		bi2 = createSampleMutableImage(60, 40, BufferedImage.TYPE_INT_RGB);
		assertFalse(bi1.equals(bi2));

		// change pixels
		for (int y = 0; y < bi1.getHeight(); y++) {
			for (int x = 0; x < bi1.getWidth(); x++) {
				bi2 = createSampleMutableImage(60, 40, BufferedImage.TYPE_INT_ARGB);
				int argb = bi2.getRGB(x, y);
				argb++;
				bi2.setRGB(x, y, argb);
				assertFalse(bi1.equals(bi2));
			}
		}

		bi2 = createSampleMutableImage(60, 40, BufferedImage.TYPE_INT_ARGB);
		bi2.setProperty("x", "y");
		assertFalse(bi1.equals(bi2));
	}

	@Test
	public void testSerialization() throws Exception {
		MutableBufferedImage bi = createSampleMutableImage(60, 40,
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

	private MutableBufferedImage createSampleMutableImage(int width, int height,
			int type) {
		return new MutableBufferedImage(createSampleBufferedImage(width, height, type));
	}

	private BufferedImage createSampleBufferedImage(int width, int height,
													int type) {
		BufferedImage returnValue = new BufferedImage(width, height, type);
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
			// if we use a plain BufferedImage it fails, but a MutableBufferedImage passes:
			BufferedImage sampleImage = createSampleMutableImage(500, 500, BufferedImage.TYPE_INT_RGB);
			Image img = sampleImage.getScaledInstance(80, 60,
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

//	// TODO write this up somehow, or bundle it as a release action. This is worth noting, but not a unit test
	// use ImageIO.read(URL) (which produces a byte based image) + Image.getScaledInstace(..) (which uses
	// the BufferedImage's ImageProducer) to really highlight this bad performance
//	@Test
//	public void testPerformance() {
//		for (ImageType type : ImageType.values(true)) {
//			BufferedImage bi1 = createSampleBufferedImage(2000,2000, type.getCode());
//			MutableBufferedImage bi2 = createSampleMutableImage(2000,2000, type.getCode());
//			System.out.println(type+" " + measurePerformance(bi1)+" " + measurePerformance(bi2));
//		}
//	}
//
//	private Object measurePerformance(BufferedImage bi) {
//		ImageConsumer nullConsumer = new ImageConsumer() {
//			@Override
//			public void setDimensions(int width, int height) {
//
//			}
//
//			@Override
//			public void setProperties(Hashtable<?, ?> props) {
//
//			}
//
//			@Override
//			public void setColorModel(ColorModel model) {
//
//			}
//
//			@Override
//			public void setHints(int hintflags) {
//
//			}
//
//			@Override
//			public void setPixels(int x, int y, int w, int h, ColorModel model, byte[] pixels, int off, int scansize) {
//
//			}
//
//			@Override
//			public void setPixels(int x, int y, int w, int h, ColorModel model, int[] pixels, int off, int scansize) {
//
//			}
//
//			@Override
//			public void imageComplete(int status) {
//
//			}
//		};
//
//
//		long t = System.currentTimeMillis();
//		for (int a = 0; a < 1000; a++) {
//			bi.getSource().addConsumer(nullConsumer);
//			bi.getSource().removeConsumer(nullConsumer);
//		}
//		t = System.currentTimeMillis() - t;
//		return Long.toString(t);
//	}
}