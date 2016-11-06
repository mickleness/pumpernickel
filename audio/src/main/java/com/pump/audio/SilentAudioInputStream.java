/*
 * @(#)SilentAudioInputStream.java
 *
 * $Date: 2014-03-13 04:15:48 -0400 (Thu, 13 Mar 2014) $
 *
 * Copyright (c) 2012 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Jeremy Wood. For details see accompanying license terms.
 * 
 * This software is probably, but not necessarily, discussed here:
 * https://javagraphics.java.net/
 * 
 * That site should also contain the most recent official version
 * of this software.  (See the SVN repository for more details.)
 */
package com.pump.audio;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.sound.sampled.AudioInputStream;

/** A silent AudioInputStream.
 * 
 */
public class SilentAudioInputStream extends AudioInputStream {
	
	long sampleCount;
	
	/** A silent AudioInputStream that lasts a fixed number of samples.
	 * 
	 * @param format
	 * @param sampleCount the number of samples to read.
	 */
	public SilentAudioInputStream(AudioFormat format,long sampleCount) {
		super(new InnerSilentInputStream( (format.getSampleSizeInBits()==8 && format.getEncoding().equals(Encoding.PCM_UNSIGNED)) ?
				(byte)127 : (byte)0 ), 
				format, sampleCount);
		this.sampleCount = sampleCount;
	}
	
	/** A stream that returns infinite zeroes. */
	private static class InnerSilentInputStream extends InputStream {	
		long bytesRead = 0;
		byte value;
		
		InnerSilentInputStream(byte value) {
			this.value = value;
		}
		
		@Override
		public int read() throws IOException {
			bytesRead++;
			return value;
		}

		@Override
		public int read(byte[] b) throws IOException {
			return read(b, 0, b.length);
		}

		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			Arrays.fill(b, off, off+len, value);
			bytesRead+=len;
			return len;
		}

		@Override
		public long skip(long n) throws IOException {
			bytesRead += n;
			return n;
		}

		@Override
		public int available() throws IOException {
			return Integer.MAX_VALUE;
		}

		@Override
		public void close() throws IOException {}

		@Override
		public synchronized void mark(int readlimit) {}

		@Override
		public synchronized void reset() throws IOException {}

		@Override
		public boolean markSupported() {
			return true;
		}
	}
}
