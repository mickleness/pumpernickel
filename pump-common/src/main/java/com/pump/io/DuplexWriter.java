/*
 * @(#)DuplexWriter.java
 *
 * $Date: 2014-11-27 01:50:51 -0500 (Thu, 27 Nov 2014) $
 *
 * Copyright (c) 2014 by Jeremy Wood.
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
package com.pump.io;

import java.io.IOException;
import java.io.Writer;

/** This Writer channels data to multiple writers.
 * <p>The original incentive for this class is so one Writer object can
 * be used that outputs data both to a file and to the console.
 *
 */
public class DuplexWriter extends Writer {

	Writer[] writers;
	
	public DuplexWriter(Writer... w) {
		for(int a = 0; a<w.length; a++) {
			if(w[a]==null) throw new NullPointerException("index="+a);
		}
		writers = w;
	}
	
	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		for(Writer w : writers) {
			w.write(cbuf, off, len);
		}
	}

	@Override
	public void flush() throws IOException {
		for(Writer w : writers) {
			w.flush();
		}
	}

	@Override
	public void close() throws IOException {
		for(Writer w : writers) {
			w.close();
		}
	}

}
