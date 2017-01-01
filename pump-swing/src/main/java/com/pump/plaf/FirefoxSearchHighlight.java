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