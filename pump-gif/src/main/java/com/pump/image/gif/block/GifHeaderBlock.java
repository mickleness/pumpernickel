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
 * This is the first block in of data in a GIF file. This parser only supports a
 * header of "GIF89a" or "GIF87a".
 */
public class GifHeaderBlock extends GifBlock {
	byte[] b;

	/** Creates a "GIF89a" header */
	public GifHeaderBlock() {
		this.b = new byte[] { 'G', 'I', 'F', '8', '9', 'a' };
	}

	/**
	 * This constructs a <code>GifHeaderBlock</code> from 6 bytes. This throws
	 * exceptions if the array does not read "GIF89a" or "GIF87a".
	 * 
	 * @param b
	 *            this array is not cloned, it is referenced directly.
	 */
	protected GifHeaderBlock(byte[] b) {
		if (b.length != 6)
			throw new IllegalArgumentException(
					"A GIF header must be 6 bytes.  Illegal array length: "
							+ b.length);
		this.b = b;
		if (!(b[0] == 'G' && b[1] == 'I' && b[2] == 'F')) {
			throw new IllegalArgumentException(
					"The header of this input stream does not begin with \"GIF\".");
		}
		if (b[3] == '8' && b[4] == '9' && b[5] == 'a')
			return; // all's well
		if (b[3] == '8' && b[4] == '7' && b[5] == 'a')
			return; // we support this too

		System.err.println((b[3] & 0xFF) + ", " + (b[4] & 0xFF) + ", "
				+ (b[5] & 0xFF));
		String s = new String(b, 3, 3);
		throw new IllegalArgumentException(
				"Unrecognized GIF file format: \""
						+ s
						+ "\".  This decoder only supports \"89a\" or \"87a\" encoded GIF.");
	}

	public byte[] getBytes() {
		return b;
	}

	public int getByteCount() {
		return getBytes().length;
	}
}