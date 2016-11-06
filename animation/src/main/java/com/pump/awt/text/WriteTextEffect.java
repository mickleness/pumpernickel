/*
 * @(#)WriteTextEffect.java
 *
 * $Date: 2014-11-27 02:09:41 -0500 (Thu, 27 Nov 2014) $
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
package com.pump.awt.text;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.pump.awt.text.writing.WritingFont;
import com.pump.awt.text.writing.WritingTextLayout;

/** A TextEffect that draws text using a {@link com.pump.awt.text.writing.WritingFont}.
 */
public class WriteTextEffect implements TextEffect {
	Dimension size;
	WritingFont font;
	String text;
	WritingTextLayout layout;

	public WriteTextEffect(WritingFont font, String text, int width,
			int height) {
		this.font = font;
		this.text = text;
		size = new Dimension(width, height);
		layout = new WritingTextLayout(font, 36, text);
	}

	@Override
	public void paint(Graphics2D g, float fraction) {
		layout.paint(g, new Rectangle(0,0,size.width,size.height), fraction*layout.getDuration());
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(size.width,size.height);
	}

}
