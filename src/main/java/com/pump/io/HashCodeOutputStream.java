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
package com.pump.io;

import java.io.IOException;
import java.io.OutputStream;

/**
 * This OutputStream generates a hash code based on each incoming byte.
 */
public class HashCodeOutputStream extends OutputStream {
	int hashCode = 0;

	@Override
	public void write(int b) throws IOException {
		hashCode = (hashCode << 8) + b;
	}

	@Override
	public int hashCode() {
		return hashCode;
	}

}