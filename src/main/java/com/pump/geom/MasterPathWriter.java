/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.geom;

public class MasterPathWriter extends PathWriter {

	PathWriter[] writers;

	public MasterPathWriter(PathWriter w1, PathWriter w2) {
		this(new PathWriter[] { w1, w2 });
	}

	public MasterPathWriter(PathWriter[] array) {
		writers = new PathWriter[array.length];
		System.arraycopy(array, 0, writers, 0, array.length);
	}

	@Override
	public void moveTo(float x, float y) {
		for (PathWriter w : writers) {
			if (w != null)
				w.moveTo(x, y);
		}
	}

	@Override
	public void lineTo(float x, float y) {
		for (PathWriter w : writers) {
			if (w != null)
				w.lineTo(x, y);
		}
	}

	@Override
	public void quadTo(float cx, float cy, float x, float y) {
		for (PathWriter w : writers) {
			if (w != null)
				w.quadTo(cx, cy, x, y);
		}
	}

	@Override
	public void curveTo(float cx1, float cy1, float cx2, float cy2, float x,
			float y) {
		for (PathWriter w : writers) {
			if (w != null)
				w.curveTo(cx1, cy1, cx2, cy2, x, y);
		}
	}

	@Override
	public void closePath() {
		for (PathWriter w : writers) {
			if (w != null)
				w.closePath();
		}
	}

	@Override
	public void flush() {
		for (PathWriter w : writers) {
			if (w != null)
				w.flush();
		}
	}
}