/**
 * This software is released as part of the Pumpernickel project.
 * <p>
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * <p>
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * This creates an InputStream.
 * <p>
 * In earlier code I used the javax.activation.DataSource for this general
 * purpose, but that sometimes leads to module-related problems because it isn't
 * always available.
 */
public interface InputStreamSource {

	/**
	 * Create an InputStream from a data source.
	 */
	InputStream createInputStream() throws IOException;
}