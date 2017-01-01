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

/** This <code>OutputStream</code> does not write any data.
 * (All the methods in this object are empty.)
 * <P>This can be used in combination with a <code>MeasuredOutputStream</code>
 * to measure the length of something being written.
 */
public class NullOutputStream extends OutputStream {

	@Override
	public void close() {}

	@Override
	public void flush() {}

	@Override
	public void write(byte[] b, int off, int len) {}

	@Override
	public void write(byte[] b) {}

	@Override
	public void write(int b) throws IOException {}

}