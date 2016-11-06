/*
 * @(#)NullPathWriter.java
 *
 * $Date: 2014-03-13 04:15:48 -0400 (Thu, 13 Mar 2014) $
 *
 * Copyright (c) 2013 by Jeremy Wood.
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
package com.pump.geom;


/** A PathWriter that does not write anything. */
public class NullPathWriter extends PathWriter {

	@Override
	public void moveTo(float x, float y) {	}

	@Override
	public void lineTo(float x, float y) {	}

	@Override
	public void quadTo(float cx, float cy, float x, float y) {	}

	@Override
	public void curveTo(float cx1, float cy1, float cx2, float cy2, float x,
			float y) {	}

	@Override
	public void closePath() {	}

	@Override
	public void flush() {	}

}
