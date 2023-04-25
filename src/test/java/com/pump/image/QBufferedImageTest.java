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

import com.pump.geom.StarPolygon;
import com.pump.image.pixel.ImageType;
import org.junit.Test;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.*;
import java.io.*;
import java.util.Random;

import static org.junit.Assert.*;

public class QBufferedImageTest {

	@Test
	public void testEquals() {
		QBufferedImage bi1 = createSampleQImage(60, 40,
				BufferedImage.TYPE_INT_ARGB);
		QBufferedImage bi2 = createSampleQImage(60, 40,
				BufferedImage.TYPE_INT_ARGB);
		assertEquals(bi1, bi2);
		assertEquals(bi1.hashCode(), bi2.hashCode());

		// change the image type:
		bi2 = createSampleQImage(60, 40, BufferedImage.TYPE_INT_RGB);
		assertFalse(bi1.equals(bi2));

		// change pixels
		for (int y = 0; y < bi1.getHeight(); y++) {
			for (int x = 0; x < bi1.getWidth(); x++) {
				bi2 = createSampleQImage(60, 40, BufferedImage.TYPE_INT_ARGB);
				int argb = bi2.getRGB(x, y);
				argb++;
				bi2.setRGB(x, y, argb);
				assertFalse(bi1.equals(bi2));
			}
		}

		bi2 = createSampleQImage(60, 40, BufferedImage.TYPE_INT_ARGB);
		bi2.setProperty("x", "y");
		assertFalse(bi1.equals(bi2));
	}

	@Test
	public void testSerialization() throws Exception {
		QBufferedImage bi = createSampleQImage(60, 40,
				BufferedImage.TYPE_INT_ARGB);
		byte[] bytes = serialize(bi);
		QBufferedImage bi2 = (QBufferedImage) deserialize(bytes);
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

	private QBufferedImage createSampleQImage(int width, int height,
													int type) {
		return new QBufferedImage(createSampleBufferedImage(width, height, type));
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

	/**
	 * This basically confirms JDK-4200096 does NOT reproduce in QBufferedImageSource.
	 * I proposed a change for this to the OpenJDK project (in Apr 2023), but even if accepted it will
	 * take years for us to adapt the latest JDK release and benefit from it.
	 */
	@Test
	public void testGetWidth_JDK_4200096() {
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
			// if we use a plain BufferedImage it fails, but a QBufferedImage passes:
			BufferedImage sampleImage = createSampleQImage(500, 500, BufferedImage.TYPE_INT_RGB);
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
					if ((infoflags & ImageObserver.WIDTH) > 0)
						sb.append("width ");
					if ((infoflags & ImageObserver.HEIGHT) > 0)
						sb.append("height ");
					if ((infoflags & ImageObserver.PROPERTIES) > 0)
						sb.append("properties ");
					if ((infoflags & ImageObserver.SOMEBITS) > 0)
						sb.append("somebits ");
					if ((infoflags & ImageObserver.FRAMEBITS) > 0)
						sb.append("framebits ");
					if ((infoflags & ImageObserver.ALLBITS) > 0)
						sb.append("allbits ");
					if ((infoflags & ImageObserver.ERROR) > 0)
						sb.append("error ");
					if ((infoflags & ImageObserver.ABORT) > 0)
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
//			QBufferedImage bi2 = createSampleMutableImage(2000,2000, type.getCode());
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

	/**
	 * Test the constructors that accept byte[] and int[]
	 */
	@Test
	public void testArrayConstructors() {
		for (int imageType : new int[] {
				BufferedImage.TYPE_INT_RGB,
				BufferedImage.TYPE_INT_ARGB,
				BufferedImage.TYPE_INT_BGR,
				BufferedImage.TYPE_INT_ARGB_PRE,
				BufferedImage.TYPE_BYTE_GRAY,
				BufferedImage.TYPE_BYTE_INDEXED,
				BufferedImage.TYPE_3BYTE_BGR,
				BufferedImage.TYPE_4BYTE_ABGR,
				BufferedImage.TYPE_4BYTE_ABGR_PRE,

		}) {
			System.out.println("Testing " + ImageType.toString(imageType));
			BufferedImage bi = new BufferedImage(80, 60, imageType);
			drawRandomShapes(bi);
			DataBuffer db = bi.getRaster().getDataBuffer();
			QBufferedImage copy;
			if (db instanceof DataBufferInt) {
				copy = new QBufferedImage(bi.getColorModel(), bi.getWidth(), bi.getHeight(), ((DataBufferInt)db).getData());
			} else if (db instanceof DataBufferByte) {
				copy = new QBufferedImage(bi.getColorModel(), bi.getWidth(), bi.getHeight(), ((DataBufferByte)db).getData());
			} else {
				throw new UnsupportedOperationException("DataBuffer = "+ db);
			}
			assertEquals(imageType, copy.getType());
			assertImageEquals(bi, copy);
		}
	}

	private void assertImageEquals(BufferedImage bi1, BufferedImage bi2) {
		QBufferedImage biA = createARGBCopy(bi1);
		QBufferedImage biB = createARGBCopy(bi2);
		assertTrue(biA.equals(biB));
	}

	private QBufferedImage createARGBCopy(BufferedImage bi) {
		QBufferedImage returnValue = new QBufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = returnValue.createGraphics();
		g.drawImage(bi, 0, 0, null);
		g.dispose();
		return returnValue;
	}

	private void drawRandomShapes(BufferedImage bi) {
		Random r = new Random(0);
		Graphics2D g = bi.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		for (int a = 0; a < 100; a++) {
			Color c = new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256), r.nextInt(256));
			g.setColor(c);
			int x = r.nextInt(bi.getWidth() - 10);
			int y = r.nextInt(bi.getHeight() - 10);
			if (r.nextBoolean()) {
				switch (r.nextInt(4)) {
					case 0:
						g.drawRect(x, y, 10, 10);
						break;
					case 1:
						g.drawOval(x, y, 10, 10);
						break;
					case 2:
						g.draw(new RoundRectangle2D.Double(x, y, 10, 10, 4, 4));
						break;
					case 3:
						StarPolygon star = new StarPolygon(5);
						star.setCenter(x + 5, y + 5);
						g.draw(star);
						break;
				}
			} else {
				switch (r.nextInt(4)) {
					case 0:
						g.fillRect(x, y, 10, 10);
						break;
					case 1:
						g.fillOval(x, y, 10, 10);
						break;
					case 2:
						g.fill(new RoundRectangle2D.Double(x, y, 10, 10, 4, 4));
						break;
					case 3:
						StarPolygon star = new StarPolygon(5);
						star.setCenter(x + 5, y + 5);
						g.fill(star);
						break;
				}
			}
		}
		g.dispose();
	}
}