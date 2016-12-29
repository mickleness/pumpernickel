/*
 * @(#)BufferedImagePaintable.java
 *
 * $Date: 2014-03-13 04:15:48 -0400 (Thu, 13 Mar 2014) $
 *
 * Copyright (c) 2012 by Jeremy Wood.
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
package com.pump.awt;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/** A Paintable that paints a BufferedImage. */
public class BufferedImagePaintable implements Paintable {
	BufferedImage image;
	
	public BufferedImagePaintable(BufferedImage img) {
		if(img==null) throw new NullPointerException();
		image = img;
	}
	
	public BufferedImage getImage() {
		return image;
	}

	public int getWidth() {
		return image.getWidth();
	}

	public int getHeight() {
		return image.getHeight();
	}

	public void paint(Graphics2D g) {
		g.drawImage(image, 0, 0, null);
	}
}
