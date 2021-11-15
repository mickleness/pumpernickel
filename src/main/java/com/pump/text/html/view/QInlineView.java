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
package com.pump.text.html.view;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;

import javax.swing.text.Element;
import javax.swing.text.html.InlineView;

/**
 * This InlineView uses the QViewHelper to support text-shadows.
 */
public class QInlineView extends InlineView implements LegacyCssView {

	public QInlineView(Element elem) {
		super(elem);
	}

	@Override
	public void paint(Graphics g, Shape allocation) {
		QViewRenderer.paint((Graphics2D) g, allocation, this, this,
				getStyleSheet(), null, false);
	}

	@Override
	public void paintLegacyCss2(Graphics g, Shape allocation) {
		super.paint(g, allocation);
	}

}