/*
 * @(#)BaselineGraphics2D.java
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
package com.pump.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Shape;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

/** This simply records the baselines that text is drawn at.
 * These values are stored as Integers in the <code>baselines</code>
 * field of this object.
 */
public class BaselineGraphics2D extends AbstractGraphics2D {
	//(only the baselines field of the root object is used)
	private BaselineGraphics2D root = null;
	public HashSet<Integer> baselines = null;
	
	public BaselineGraphics2D() {
		root = this;
		baselines = new HashSet<Integer>();
	}
	
	private BaselineGraphics2D(BaselineGraphics2D g) {
		super(g);
		root = g.root;
	}

	@Override
	public void draw(Shape s) {}

	@Override
	public void fill(Shape s) {}

	@Override
	public GraphicsConfiguration getDeviceConfiguration() {
		return null;
	}

	@Override
	public Graphics create() {
		return new BaselineGraphics2D(this);
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2,
			int sx1, int sy1, int sx2, int sy2, Color bgcolor,
			ImageObserver observer) {
		return true;
	}

	@Override
	public void drawString(String s, float x, float y) {
		addBaseline(y);
	}
	
	@Override
	public void drawString(AttributedCharacterIterator aci, float x,float y) {
		addBaseline(y);
	}
	
	@Override
	public void drawString(AttributedCharacterIterator aci, int x, int y) {
		addBaseline(y);
	}

	@Override
	public void drawString(String s, int x, int y) {
		addBaseline(y);
	}

	@Override
	public void drawBytes(byte[] data, int offset, int length, int x, int y) {
		addBaseline(y);
	}

	@Override
	public void drawChars(char[] data, int offset, int length, int x, int y) {
		addBaseline(y);
	}
	
	/** Returns all the baselines used for this Graphics2D.
	 * <p>This class is very basic, and a baseline is recorded
	 * whether or not it falls inside the clipped area.  Also
	 * rotated baselines are not recorded, because, well, they
	 * make things complicated.
	 * 
	 * @return all the baselines used for this Graphics2D.
	 */
	public int[] getBaselines() {
		int[] array = new int[root.baselines.size()];
		Iterator<Integer> i = baselines.iterator();
		int ctr = 0;
		while(i.hasNext()) {
			array[ctr++] = (i.next()).intValue();
		}
		Arrays.sort(array);
		return array;
	}

	private synchronized void addBaseline(double y) {
		//what?  don't hurt my head.  no rotated text, thank you!
		if(Math.abs(transform.getShearY())>.0001)
			return;
		y = y*transform.getScaleY()+transform.getTranslateY();
		Integer key = new Integer( (int)(y+.5) );
		root.baselines.add(key);
	}
}
