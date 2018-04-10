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
import java.io.Writer;

/**
 * This Writer channels data to multiple writers.
 * <p>
 * The original incentive for this class is so one Writer object can be used
 * that outputs data both to a file and to the console.
 *
 */
public class DuplexWriter extends Writer {

	Writer[] writers;

	public DuplexWriter(Writer... w) {
		for (int a = 0; a < w.length; a++) {
			if (w[a] == null)
				throw new NullPointerException("index=" + a);
		}
		writers = w;
	}

	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		for (Writer w : writers) {
			w.write(cbuf, off, len);
		}
	}

	@Override
	public void flush() throws IOException {
		for (Writer w : writers) {
			w.flush();
		}
	}

	@Override
	public void close() throws IOException {
		for (Writer w : writers) {
			w.close();
		}
	}

}