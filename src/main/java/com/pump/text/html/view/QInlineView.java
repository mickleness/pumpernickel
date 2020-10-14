package com.pump.text.html.view;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;

import javax.swing.text.Element;
import javax.swing.text.html.InlineView;

/**
 * This InlineView uses the QViewHelper to support text-shadows.
 */
public class QInlineView extends InlineView {
	QViewHelper helper;

	public QInlineView(Element elem) {
		super(elem);

		helper = new QViewHelper(this, getStyleSheet());
	}

	@Override
	public void paint(Graphics g, Shape allocation) {
		Graphics2D g2 = helper.createGraphics((Graphics2D) g, allocation,
				false);
		super.paint(g2, allocation);
		g2.dispose();
	}

}
