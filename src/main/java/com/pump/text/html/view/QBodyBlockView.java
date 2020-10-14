package com.pump.text.html.view;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.text.Element;
import javax.swing.text.html.StyleSheet;
import javax.swing.text.html.StyleSheet.BoxPainter;

import com.pump.geom.ShapeBounds;

public class QBodyBlockView extends SwingBodyBlockView {

	QViewHelper helper;
	BoxPainter boxPainter;

	public QBodyBlockView(Element elem) {
		super(elem);

		helper = new QViewHelper(this, getStyleSheet());
	}

	@Override
	public void paint(Graphics g, Shape allocation) {
		Graphics2D g2 = helper.createGraphics((Graphics2D) g, allocation, true);
		Rectangle r = ShapeBounds.getBounds(allocation).getBounds();
		helper.paintBackground(g2, r);
		g2 = helper.createGraphicsWithoutBoxPainter(g2, r, boxPainter);
		super.paint(g2, allocation);
		g2.dispose();
	}

	@Override
	protected void setPropertiesFromAttributes() {
		super.setPropertiesFromAttributes();

		// what we really want is access to our super's BoxPainter,
		// but that's private so we'll create our own
		StyleSheet sheet = super.getStyleSheet();
		boxPainter = sheet.getBoxPainter(sheet.getViewAttributes(this));
	}
}