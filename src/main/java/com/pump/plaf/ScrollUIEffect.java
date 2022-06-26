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
			int x = (int) (xFunction.evaluate(f) + .5);
			int y = (int) (yFunction.evaluate(f) + .5);

			int scrollWidth = scrollPane.getViewport().getWidth();
			int scrollHeight = scrollPane.getViewport().getHeight();
			int viewWidth = scrollPane.getViewport().getViewSize().width;
			int viewHeight = scrollPane.getViewport().getViewSize().height;
			boolean deadspace = (x < 0) || (y < 0)
					|| (x + viewWidth > scrollWidth)
					|| (x + viewHeight > scrollHeight);

			if (deadspace) {
				// scrollPane.getViewport().setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
				scrollPane.getViewport().setScrollMode(
						JViewport.SIMPLE_SCROLL_MODE);
			} else {
				scrollPane.getViewport().setScrollMode(
						JViewport.BLIT_SCROLL_MODE);
			}
			scrollPane.getViewport().setViewPosition(new Point(x, y));
		}
	};

	public ScrollUIEffect(JScrollPane scrollPane, Function xFunction,
			Function yFunction, int totalDuration) {
		super(scrollPane, totalDuration, 20);
		this.scrollPane = scrollPane;
		this.xFunction = xFunction;
		this.yFunction = yFunction;

		addChangeListener(changeListener);
	}

}