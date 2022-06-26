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
package com.pump.image.gif.block;

import java.awt.Color;
import java.awt.image.IndexColorModel;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

public abstract class GifColorTable extends GifBlock {
	private byte[] data;
	private transient IndexColorModel icm;

	/**
	 * Creates a color table from an array of bytes. This byte array is NOT
	 * cloned.
	 * 
	 * @param data
	 */
	protected GifColorTable(byte[] data) {
		this.data = data;
		if (data.length == 0)
			throw new RuntimeException();
	}

	/**
	 * @param transparentIndex
	 *            if this is not the last value that was used in this method,
	 *            then we create a new <code>IndexColorModel</code>
	 * @return a cached <code>IndexColorModel</code>.
	 */
	public IndexColorModel getIndexColorModel(int transparentIndex) {
		if (icm == null || icm.getTransparentPixel() != transparentIndex) {
			byte[] r = new byte[data.length / 3];
			byte[] g = new byte[data.length / 3];
			byte[] b = new byte[data.length / 3];

			for (int a = 0; a < r.length; a++) {
				r[a] = data[a * 3 + 0];
				g[a] = data[a * 3 + 1];
				b[a] = data[a * 3 + 2];
			}
			if (transparentIndex == -1) {
				icm = new IndexColorModel(8, data.length / 3, r, g, b);
			} else {
				icm = new IndexColorModel(8, data.length / 3, r, g, b,
						transparentIndex);
			}
		}
		return icm;
	}

	/**
	 * Creates a color table from a <code>ColorIndexModel</code>.
	 * 
	 * @param i
	 *            this needs to be a byte-based <code>ColorIndexModel</code>.
	 */
	public GifColorTable(IndexColorModel i) {
		this.icm = i;
		int size = i.getMapSize();
		if (size == 0)
			throw new RuntimeException();

		// if size is not an exact power
		// of 2, we must round it up to one...
		int realSize;
		if (size == 2) {
			realSize = 2;
		} else if (size <= 4) {
			realSize = 4;
		} else if (size <= 8) {
			realSize = 8;
		} else if (size <= 16) {
			realSize = 16;
		} else if (size <= 32) {
			realSize = 32;
		} else if (size <= 64) {
			realSize = 64;
		} else if (size <= 128) {
			realSize = 128;
		} else if (size <= 256) {
			realSize = 256;
		} else {
			throw new RuntimeException(
					"The number of colors in a GIF color table must be between 2 and 256.");
		}

		data = new byte[3 * realSize];
		for (int a = 0; a < size; a++) {
			int rgb = i.getRGB(a);
			data[3 * a] = (byte) ((rgb >> 16) & 0xFF);
			data[3 * a + 1] = (byte) ((rgb >> 8) & 0xFF);
			data[3 * a + 2] = (byte) (rgb & 0xFF);
		}
	}

	public void write(Writer out) throws IOException {
		out.write("com.pump.gif.parser.GifColorTable[");
		for (int a = 0; a < data.length / 3; a++) {
			if (a != 0)
				out.write(", ");
			out.write("(" + (data[a * 3 + 0] & 0xff) + ", "
					+ (data[a * 3 + 1] & 0xff) + ", "
					+ (data[a * 3 + 2] & 0xff) + ")");
		}
		out.write("]");
	}

	public String toString() {
		StringWriter w = new StringWriter(data.length * 6 + 15);
		try {
			write(w);
		} catch (IOException e) {
			// this can't happen, right?
		}
		return w.toString();
	}

	public GifColorTable(String s) {
		if (s.startsWith("com.pump.gif.parser.GifColorTable[") == false) {
			System.err.println("s=\"" + s + "\"");
			throw new IllegalArgumentException(
					"This string does not begin with \"GifColorTable[\"");
		}
		int i = s.indexOf('(');
		int i2;
		int r, g, b;
		while (s.indexOf(')', i) != -1) {
			i2 = s.indexOf(',', i);
			r = Integer.parseInt(s.substring(i + 1, i2));
			i = i2 + 2;
			i2 = s.indexOf(',', i);
			g = Integer.parseInt(s.substring(i + 1, i2));
			i = i2 + 2;
			i2 = s.indexOf(')', i);
			b = Integer.parseInt(s.substring(i + 1, i2));
			i = i2 + 4;
		}
	}

	/**
	 * This is equivalent to calling <code>getColor(x).getRGB()</code>.
	 * 
	 * @return the RGB value of the i-th color.
	 * @param i
	 *            the color index to retrieve.
	 */
	public int getRGB(int i) {
		if (3 * i + 2 > data.length) {
			return (255 << 24);
		}
		try {
			return (255 << 24) + ((data[3 * i] & 0xFF) << 16)
					+ ((data[3 * i + 1] & 0xFF) << 8)
					+ ((data[3 * i + 2] & 0xFF));
		} catch (RuntimeException e) {
			System.err.println("data.length=" + data.length + " i=" + i);
			throw e;
		}
	}

	/**
	 * @return the a-th color
	 * @param a
	 *            the index of the color to retrieve
	 * */
	public Color getColor(int a) {
		return new Color(data[3 * a] & 0xFF, data[3 * a + 1] & 0xFF,
				data[3 * a + 2] & 0xFF);
	}

	/** @return all the colors, in order */
	public Color[] getColors() {
		Color[] c = new Color[getColorCount()];
		for (int a = 0; a < c.length; a++) {
			c[a] = new Color(data[3 * a] & 0xFF, data[3 * a + 1] & 0xFF,
					data[3 * a + 2] & 0xFF);
			;
		}
		return c;
	}

	/** @return the number of colors */
	public int getColorCount() {
		return data.length / 3;
	}

	public byte[] getBytes() {
		byte[] b = new byte[data.length];
		System.arraycopy(data, 0, b, 0, data.length);
		return b;
	}

	public int getByteCount() {
		return data.length;
	}

	/** @return the log base 2 of <code>getColorCount()</code> */
	public int getDepth() {
		int i = getColorCount();
		return getDepth(i);
	}

	/**
	 * @return the log base 2 of <code>i</code>
	 * @param i
	 *            the number of colors.
	 */
	public static int getDepth(int i) {
		if (i <= 2)
			return 1;
		if (i <= 4)
			return 2;
		if (i <= 8)
			return 3;
		if (i <= 16)
			return 4;
		if (i <= 32)
			return 5;
		if (i <= 64)
			return 6;
		if (i <= 128)
			return 7;
		return 8;
	}

}