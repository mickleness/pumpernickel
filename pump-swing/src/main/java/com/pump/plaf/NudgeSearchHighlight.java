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

import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.geom.AffineTransform;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.text.JTextComponent;

/** This is a subtle highlight effect that moves a translucent
 * copy of the text around vertically.  Just a little.
 */
public class NudgeSearchHighlight extends AbstractSearchHighlight {
	private static Insets i = new Insets(3,3,3,3);
	
	public NudgeSearchHighlight(JTextComponent jtc, int startIndex, int endIndex) {
		super(jtc, startIndex, endIndex);
	}

	public NudgeSearchHighlight(JTable table, int rowIndex, int columnIndex)
	{
		super(table, rowIndex, columnIndex);
	}

	@Override
	protected Insets getHighlightInsets() {
		return new Insets(14,3,7,3);
	}

	@Override
	protected void paintHighlightBackground(Graphics2D g, Rectangle textRect) {
		g.setPaint(SystemColor.textHighlight);
		g.fillRect(textRect.x-i.left, textRect.y-i.top, textRect.width+i.left+i.right, textRect.height+i.top+i.bottom);
		g.setPaint(SystemColor.controlShadow);
		g.drawRect(textRect.x-i.left, textRect.y-i.top, textRect.width+i.left+i.right-1, textRect.height+i.top+i.bottom-1);
	}

	@Override
	protected void updateAnimation(JComponent[] highlights,float fraction) {
		AffineTransform transform = null;
		float k = .3f;
		if(fraction<k) {
			fraction = fraction/k;
			double nudge = (1-fraction*fraction*fraction)*Math.cos(fraction * Math.PI);
			transform = new AffineTransform();
			transform.translate(0, nudge*5);
		}
		for(int a = 0; a<highlights.length; a++) {
			highlights[a].putClientProperty("transform", transform);
		}
	}
}