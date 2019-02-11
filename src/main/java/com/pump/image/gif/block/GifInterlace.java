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
package com.pump.image.gif.block;

/**
 * This contains some static methods to interlace/uninterlace data according to
 * the GIF definition of interlacing.
 * <P>
 * <P>
 * The GIF file format specification describes the GIF interlacing scheme as
 * follows:
 * <P>
 * <P>
 * "The rows of an Interlaced images are arranged in the following order: <BR>
 * <BR>
 * Group 1 : Every 8th. row, starting with row 0. (Pass 1) <BR>
 * Group 2 : Every 8th. row, starting with row 4. (Pass 2) <BR>
 * Group 3 : Every 4th. row, starting with row 2. (Pass 3) <BR>
 * Group 4 : Every 2nd. row, starting with row 1. (Pass 4) <BR>
 * <P>
 * The Following example illustrates how the rows of an interlaced image are
 * ordered.
 * 
 * <br>
 * <code>    Row &nbsp; &nbsp; &nbsp; Interlace</code> <br>
 * <code>    Number &nbsp; &nbsp; Pass</code> <br>
 * <code>00    -------- 1</code> <br>
 * <code>01    --------&nbsp;&nbsp;&nbsp; 4</code> <br>
 * <code>02    --------&nbsp;&nbsp; 3</code> <br>
 * <code>03    --------&nbsp;&nbsp;&nbsp; 4</code> <br>
 * <code>04    --------&nbsp; 2</code> <br>
 * <code>05    --------&nbsp;&nbsp;&nbsp; 4</code> <br>
 * <code>06    --------&nbsp;&nbsp; 3</code> <br>
 * <code>07    --------&nbsp;&nbsp;&nbsp; 4</code> <br>
 * <code>08    -------- 1</code> <br>
 * <code>09    --------&nbsp;&nbsp;&nbsp; 4</code> <br>
 * <code>10   --------&nbsp;&nbsp; 3</code> <br>
 * <code>11   --------&nbsp;&nbsp;&nbsp; 4</code> <br>
 * <code>12   --------&nbsp; 2</code> <br>
 * <code>13   --------&nbsp;&nbsp;&nbsp; 4</code> <br>
 * <code>14   --------&nbsp;&nbsp; 3</code> <br>
 * <code>15   --------&nbsp;&nbsp;&nbsp; 4</code> <br>
 * <code>16   -------- 1</code> <br>
 * <code>17   --------&nbsp;&nbsp;&nbsp; 4</code> <br>
 * <code>18   --------&nbsp;&nbsp; 3</code> <br>
 * <code>19   --------&nbsp;&nbsp;&nbsp; 4</code> "
 */
public class GifInterlace {
	/**
	 * This takes an array of data that is interlaced, and returns it to normal.
	 * 
	 * @param data
	 *            the array of data
	 * @param width
	 *            the width of the image
	 * @param height
	 *            the height of the image
	 */
	public static void decode(int[] data, int width, int height) {
		int[] map = createMap(height);
		map(map, data, width);
	}

	/**
	 * This takes an array of data that is interlaced, and returns it to normal.
	 * 
	 * @param data
	 *            the array of data
	 * @param width
	 *            the width of the image
	 * @param height
	 *            the height of the image
	 */
	public static void decode(byte[] data, int width, int height) {
		int[] map = createMap(height);
		map(map, data, width);
	}

	/**
	 * This takes an array of data that is not interlaced, and interlaces it.
	 * 
	 * @param data
	 *            the array of data
	 * @param width
	 *            the width of the image
	 * @param height
	 *            the height of the image
	 */
	public static void encode(int[] data, int width, int height) {
		int[] map = createMap(height);
		map = invert(map);
		map(map, data, width);
	}

	/**
	 * This takes an array of data that is not interlaced, and interlaces it.
	 * 
	 * @param data
	 *            the array of data
	 * @param width
	 *            the width of the image
	 * @param height
	 *            the height of the image
	 */
	public static void encode(byte[] data, int width, int height) {
		int[] map = createMap(height);
		map = invert(map);
		map(map, data, width);
	}

	/**
	 * This takes an array of data that is not interlaced, and interlaces it.
	 * 
	 * @param data
	 *            the array of data
	 * @param width
	 *            the width of the image
	 * @param height
	 *            the height of the image
	 */
	public static void encode(byte[][] data, int width, int height) {
		int[] map = createMap(height);
		map = invert(map);
		map(map, data, width);
	}

	private static void map(int[] m, int[] array, int width) {
		int[] t = new int[width];
		int[] t2 = new int[width];
		int[] swap;
		int ctr = 0;
		while (ctr < m.length) {
			System.arraycopy(array, width * ctr, t, 0, width);
			while (m[ctr] != ctr) {
				System.arraycopy(array, width * m[ctr], t2, 0, width);
				System.arraycopy(t, 0, array, width * m[ctr], width);

				int i = m[m[ctr]];
				m[m[ctr]] = m[ctr];
				m[ctr] = i;

				swap = t;
				t = t2;
				t2 = swap;
			}
			// this should be "t2", but we just swapped them out
			System.arraycopy(t, 0, array, width * ctr, width);
			ctr++;
		}
	}

	private static void map(int[] m, byte[] array, int width) {
		byte[] t = new byte[width];
		byte[] t2 = new byte[width];
		byte[] swap;
		int ctr = 0;
		while (ctr < m.length) {
			try {
				System.arraycopy(array, width * ctr, t, 0, width);
			} catch (RuntimeException e) {
				System.err.println("width=" + width + " ctr=" + ctr
						+ " width*ctr=" + (width * ctr) + " array.length="
						+ array.length + " t.length=" + t.length + " m.length="
						+ m.length);
				throw e;
			}
			while (m[ctr] != ctr) {
				System.arraycopy(array, width * m[ctr], t2, 0, width);
				System.arraycopy(t, 0, array, width * m[ctr], width);

				int i = m[m[ctr]];
				m[m[ctr]] = m[ctr];
				m[ctr] = i;

				swap = t;
				t = t2;
				t2 = swap;
			}
			// this should be "t2", but we just swapped them out
			System.arraycopy(t, 0, array, width * ctr, width);
			ctr++;
		}
	}

	private static void map(int[] m, byte[][] array, int width) {
		byte[][] t = new byte[width][];
		byte[][] t2 = new byte[width][];
		byte[][] swap;
		int ctr = 0;
		while (ctr < m.length) {
			System.arraycopy(array, width * ctr, t, 0, width);
			while (m[ctr] != ctr) {
				System.arraycopy(array, width * m[ctr], t2, 0, width);
				System.arraycopy(t, 0, array, width * m[ctr], width);

				int i = m[m[ctr]];
				m[m[ctr]] = m[ctr];
				m[ctr] = i;

				swap = t;
				t = t2;
				t2 = swap;
			}
			// this should be "t2", but we just swapped them out
			System.arraycopy(t, 0, array, width * ctr, width);
			ctr++;
		}
	}

	private static int[] invert(int[] m) {
		int[] k = new int[m.length];
		for (int a = 0; a < m.length; a++) {
			k[m[a]] = a;
		}
		return k;
	}

	private static int[] createMap(int height) {
		int[] map = new int[height];
		int y = 0;
		int ctr = 0;
		while (y < height) {
			map[ctr++] = y;
			y += 8;
		}
		y = 4;
		while (y < height) {
			map[ctr++] = y;
			y += 8;
		}
		y = 2;
		while (y < height) {
			map[ctr++] = y;
			y += 4;
		}
		y = 1;
		while (y < height) {
			map[ctr++] = y;
			y += 2;
		}
		return map;
	}
}