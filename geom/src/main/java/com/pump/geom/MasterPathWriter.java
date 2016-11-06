/*
 * @(#)MasterPathWriter.java
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


public class MasterPathWriter extends PathWriter {

	PathWriter[] writers;
	
	public MasterPathWriter(PathWriter w1,PathWriter w2) {
		this(new PathWriter[] {w1, w2});
	}
	
	public MasterPathWriter(PathWriter[] array) {
		writers = new PathWriter[array.length];
		System.arraycopy(array, 0, writers, 0, array.length);
	}
	
	@Override
	public void moveTo(float x, float y) {
		for(PathWriter w : writers) {
			if(w!=null)
				w.moveTo(x, y);
		}
	}

	@Override
	public void lineTo(float x, float y) {
		for(PathWriter w : writers) {
			if(w!=null)
				w.lineTo(x, y);
		}
	}

	@Override
	public void quadTo(float cx, float cy, float x, float y) {
		for(PathWriter w : writers) {
			if(w!=null)
				w.quadTo(cx, cy, x, y);
		}
	}

	@Override
	public void curveTo(float cx1, float cy1, float cx2, float cy2, float x,
			float y) {
		for(PathWriter w : writers) {
			if(w!=null)
				w.curveTo(cx1, cy1, cx2, cy2, x, y);
		}
	}

	@Override
	public void closePath() {
		for(PathWriter w : writers) {
			if(w!=null)
				w.closePath();
		}
	}

	@Override
	public void flush() {
		for(PathWriter w : writers) {
			if(w!=null)
				w.flush();
		}
	}
}
