/*
 * @(#)ScrollUIEffect.java
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
package com.pump.plaf;

import java.awt.Point;

import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.math.function.Function;

public class ScrollUIEffect extends UIEffect {
	
	final Function xFunction, yFunction;
	final JScrollPane scrollPane;

	ChangeListener changeListener = new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
			float f = getProgress();
			int x = (int)(xFunction.evaluate(f) + .5);
			int y = (int)(yFunction.evaluate(f) + .5);
			
			int scrollWidth = scrollPane.getViewport().getWidth();
			int scrollHeight = scrollPane.getViewport().getHeight();
			int viewWidth = scrollPane.getViewport().getViewSize().width;
			int viewHeight = scrollPane.getViewport().getViewSize().height;
			boolean deadspace = (x<0) || (y<0) || (x+viewWidth>scrollWidth) || (x+viewHeight>scrollHeight);
			
			if(deadspace) {
				//scrollPane.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
				scrollPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
			} else {
				scrollPane.getViewport().setScrollMode(JViewport.BLIT_SCROLL_MODE);
			}
			scrollPane.getViewport().setViewPosition(new Point(x, y));
		}
	};
	
	public ScrollUIEffect(JScrollPane scrollPane, Function xFunction,Function yFunction,
			int totalDuration) {
		super(scrollPane, totalDuration, 20);
		this.scrollPane = scrollPane;
		this.xFunction = xFunction;
		this.yFunction = yFunction;
		
		addChangeListener(changeListener);
	}

}
