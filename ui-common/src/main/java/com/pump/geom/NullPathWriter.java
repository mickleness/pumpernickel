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

/** A PathWriter that does not write anything. */
public class NullPathWriter extends PathWriter {

	@Override
	public void moveTo(float x, float y) {
	}

	@Override
	public void lineTo(float x, float y) {
	}

	@Override
	public void quadTo(float cx, float cy, float x, float y) {
	}

	@Override
	public void curveTo(float cx1, float cy1, float cx2, float cy2, float x,
			float y) {
	}

	@Override
	public void closePath() {
	}

	@Override
	public void flush() {
	}

}