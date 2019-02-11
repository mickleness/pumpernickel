/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://raw.githubusercontent.com/mickleness/pumpernickel/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.image.pixel;

import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.ImageConsumer;
import java.awt.image.ImageProducer;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple <code>ImageProducer</code> based on a <code>PixelIterator</code>.
 *
 */
public class ARGBImageProducer implements ImageProducer {
	List<ImageConsumer> v = new ArrayList<ImageConsumer>();
	IntARGBConverter i;

	public ARGBImageProducer(PixelIterator i) {
		if (i instanceof IntARGBConverter) {
			this.i = (IntARGBConverter) i;
		} else {
			this.i = new IntARGBConverter(i);
		}
	}

	public void addConsumer(ImageConsumer c) {
		if (v.contains(c) == false)
			v.add(c);
	}

	public boolean isConsumer(ImageConsumer c) {
		return v.contains(c);
	}

	public void removeConsumer(ImageConsumer c) {
		v.remove(c);
	}

	public void requestTopDownLeftRightResend(ImageConsumer c) {
		startProduction(c);
	}

	/** Color model used for ARGB */
	static ColorModel cm = new DirectColorModel(24, 0x00ff0000, 0x0000ff00,
			0x000000ff);

	public void startProduction(ImageConsumer c) {
		addConsumer(c);
		int w = i.getWidth();
		int[] data = new int[w];
		c.setDimensions(w, i.getHeight());
		int y = 0;
		while (i.isDone() == false) {
			i.next(data);
			c.setPixels(0, y, w, 1, cm, data, 0, 1);
			y++;

		}
		c.imageComplete(ImageConsumer.STATICIMAGEDONE);
	}
}