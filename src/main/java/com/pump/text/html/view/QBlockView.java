package com.pump.text.html.view;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;

import javax.swing.text.Element;
import javax.swing.text.html.ParagraphView;

/**
 * This BlockView uses the QViewHelper to support text-shadows.
 */
public class QBlockView extends ParagraphView {
	QViewHelper helper;

	public QBlockView(Element elem) {
		super(elem);

		helper = new QViewHelper(this);
	}

	@Override
	public void paint(Graphics g, Shape a) {
		Graphics2D g2 = (Graphics2D) g.create();
		helper.install(g2);
		super.paint(g2, a);
		g2.dispose();
	}

}
