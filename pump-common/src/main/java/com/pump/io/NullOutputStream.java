/*
 * @(#)NullOutputStream.java
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
