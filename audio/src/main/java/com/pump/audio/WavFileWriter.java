/*
 * @(#)WavFileWriter.java
 *
 * $Date: 2014-03-13 04:15:48 -0400 (Thu, 13 Mar 2014) $
 *
 * Copyright (c) 2011 by Jeremy Wood.
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.pump.io.MeasuredOutputStream;

public class WavFileWriter {
	protected File file;
	private MeasuredOutputStream out;
	private boolean formatWritten = false;
	private long sizeAfterWritingFormat;
	private boolean dataHeaderWritten = false;
	
	public WavFileWriter(File file) throws IOException {
		this.file = file;
		out = new MeasuredOutputStream(new FileOutputStream(file));
		out.write(new byte[] {0x52, 0x49, 0x46, 0x46}); //RIFF
		out.write(new byte[4]); //chunk size -- written when .close() is called
		out.write(new byte[] {0x57, 0x41, 0x56, 0x45}); //WAVE
	}
	
	public void writeFormat(WavFormatChunk format) throws IOException {
		if(formatWritten)
			throw new IOException("the format was already written");
		format.write(out, true);
		formatWritten = true;
		sizeAfterWritingFormat = out.getBytesWritten();
	}
	
	public void writeSample(byte[] data,int offset,int len) throws IOException {
		if(formatWritten==false) {
			throw new IOException("the format was not yet written");
		} else if(dataHeaderWritten==false) {
			out.write(new byte[] { 0x64, 0x61, 0x74, 0x61 });
			out.write(new byte[] {});
			dataHeaderWritten = true;
		}
		out.write( data, offset, len );
	}
	
	@Override
	protected void finalize() throws Throwable {
		close();
		super.finalize();
	}
	
	public void close() throws IOException {
		if(out==null) return; //someone already called .close()
		
		try {
			out.close();
			long size = file.length();
			byte[] sizeArray = new byte[4];
			
			RandomAccessFile raf = new RandomAccessFile(file,"rw");
			raf.seek(4);
			writeLong(sizeArray, size-8, 4);
			raf.write(sizeArray);

			raf.seek(sizeAfterWritingFormat+4);
			writeLong(sizeArray, size-sizeAfterWritingFormat-9, 4);
			raf.write(sizeArray);
			
			raf.close();
		} finally {
			out = null;
		}
	}
	protected static void writeLong(byte[] bytes,long value,int len) throws IOException {
		if(len>8)
			throw new RuntimeException("len ("+len+") > 8");
		
		for(int a = 0; a<len; a++) {
			bytes[a] = (byte)((value >> (8*a)) & 0xff);
		}
	}

	protected static void writeInt(byte[] bytes,int value,int len) throws IOException {
		if(len>4)
			throw new RuntimeException("len ("+len+") > 4");
		
		for(int a = 0; a<len; a++) {
			bytes[a] = (byte)((value >> (8*a)) & 0xff);
		}
	}
}
