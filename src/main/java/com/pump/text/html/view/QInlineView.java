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
	QViewHelper helper;

	public QInlineView(Element elem) {
		super(elem);

		helper = new QViewHelper(this, this, getStyleSheet());
	}

	@Override
	public void paint(Graphics g, Shape allocation) {
		helper.paint((Graphics2D) g, allocation, null, false);
	}

	@Override
	public void paintLegacyCss2(Graphics g, Shape allocation) {
		super.paint(g, allocation);
	}

}
