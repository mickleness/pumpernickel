/*
 * @(#)BufferedPipe.java
 *
 * $Date$
 *
 * Copyright (c) 2015 by Jeremy Wood.
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
package com.pump.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

/** This pipes data from an InputStream to a PrintStream.
 * <p>The original intended usage for this class is:
 * <p><pre>Process process = ...;
 * new BufferedPipe(process.getInputStream(), System.out);
 * new BufferedPipe(process.getErrorStream(), System.err);</pre>
 * <p>On construction this immediately creates a new thread that
 * buffers the InputStream until no more data is available.
 * <p>(I'm surprised/confused the PipedInputStream and PipedOutputStream
 * are so narrowly defined that they don't already support this functionality?)
 */
public class BufferedPipe {
	static int threadCtr = 0;
	InputStream in;
	PrintStream out;
	String prefix;
	
	public BufferedPipe(InputStream in,PrintStream out) {
		this(in, out, "");
	}
	
	public BufferedPipe(InputStream in,PrintStream out,String prefix) {
		this.in = in;
		this.out = out;
		this.prefix = prefix;
		Thread thread = new Thread("BufferedPipe-"+(threadCtr++)) {
			@Override
			public void run() {
				String total = "";
				try {
					BufferedReader br = new BufferedReader(new InputStreamReader(BufferedPipe.this.in));
					String s = br.readLine();
					while(s!=null) {
						process(s);
						s = br.readLine();
						total = total+s+"\n";
					}
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}
	
	/** Process a buffered line of text as it is becomes available.
	 * Subclasses can override this to provide additional functionality.
	 * The default implementation here simply relays this text to
	 * the PrintStream provided on construction.
	 * 
	 * @param s the new line of text to process.
	 */
	protected void process(String s) {
		if(out!=null)
			out.println(prefix+s);
	}
}
