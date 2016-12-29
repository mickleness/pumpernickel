/*
 * @(#)PerspectivePathWriter.java
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
package com.pump.geom;

import java.awt.geom.GeneralPath;

import javax.media.jai.PerspectiveTransform;

/** Transform all the points through a perspective transform as they
 * are passed to a path writer. Note this simply transforms bezier control
 * points.
 */
public class PerspectivePathWriter extends PathWriter {
	
	PathWriter dest;
	PerspectiveTransform tx;
	float[] array = new float[6];

	public PerspectivePathWriter(GeneralPath dest, PerspectiveTransform tx) {
		this(new GeneralPathWriter(dest), tx);
	}
	
	public PerspectivePathWriter(PathWriter dest, PerspectiveTransform tx) {
		this.dest = dest;
		if(tx==null)
			tx = new PerspectiveTransform();
		this.tx = tx;
	}
	
	@Override
	public synchronized void moveTo(float x, float y) {
		array[0] = x;
		array[1] = y;
		tx.transform(array, 0, array, 0, 1);
		dest.moveTo(array[0], array[1]);
	}

	@Override
	public synchronized void lineTo(float x, float y) {
		array[0] = x;
		array[1] = y;
		tx.transform(array, 0, array, 0, 1);
		dest.lineTo(array[0], array[1]);
	}

	@Override
	public synchronized void quadTo(float cx, float cy, float x, float y) {
		array[0] = cx;
		array[1] = cy;
		array[2] = x;
		array[3] = y;
		tx.transform(array, 0, array, 0, 2);
		dest.quadTo(array[0], array[1], array[2], array[3]);
	}

	@Override
	public synchronized void curveTo(float cx1, float cy1, float cx2, float cy2, float x,
			float y) {
		array[0] = cx1;
		array[1] = cy1;
		array[2] = cx2;
		array[3] = cy2;
		array[4] = x;
		array[5] = y;
		tx.transform(array, 0, array, 0, 3);
		dest.curveTo(array[0], array[1], array[2], array[3], array[4], array[5]);
	}

	@Override
	public void closePath() {
		dest.closePath();
	}

	@Override
	public void flush() {
		dest.flush();
	}
}
