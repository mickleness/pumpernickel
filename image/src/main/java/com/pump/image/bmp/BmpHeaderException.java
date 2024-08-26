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
package com.pump.image.bmp;

import java.io.IOException;

/**
 * Thrown when an input stream does not begin with "BM", which signals this
 * isn't a valid BMP image.
 *
 */
public class BmpHeaderException extends IOException {
	private static final long serialVersionUID = 1L;

	public BmpHeaderException() {
		super();
	}

	public BmpHeaderException(String s) {
		super(s);
	}
}