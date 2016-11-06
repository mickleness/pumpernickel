/*
 * @(#)FirefoxSearchHighlight.java
 *
 * $Date: 2015-06-20 07:48:46 -0400 (Sat, 20 Jun 2015) $
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
package com.pump.plaf;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.text.JTextComponent;

import com.pump.swing.TextHighlightSheet;

public class FirefoxSearchHighlight extends AbstractSearchHighlight {

	protected static Color tackyGreenish = new Color(0x38D878);
	
	public FirefoxSearchHighlight(JTextComponent jtc, int startIndex,
			int endIndex) {
		super(jtc, startIndex, endIndex);
	}
	
	public FirefoxSearchHighlight(JTable table, int rowIndex, int columnIndex)
	{
		super(table, rowIndex, columnIndex);
	}

	@Override
	protected float getDuration() {
		return Float.MAX_VALUE;
	}

	@Override
	protected void paintHighlightBackground(Graphics2D g, Rectangle textRect) {
		g.setColor(tackyGreenish);
		int i = TextHighlightSheet.FIREFOX_PADDING;
		g.fillRect(textRect.x-i, textRect.y-i, textRect.width+2*i, textRect.height+2*i);
	}



	@Override
	protected void updateAnimation(JComponent[] highlights, float fraction) {}

}
