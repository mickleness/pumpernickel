package com.pump.text.html.view;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.html.ParagraphView;
import javax.swing.text.html.StyleSheet;
import javax.swing.text.html.StyleSheet.BoxPainter;

import com.pump.geom.ShapeBounds;

/**
 * This ParagraphView uses the QViewHelper to support text-shadows.
 */
public class QParagraphView extends ParagraphView {
	QViewHelper helper;
	BoxPainter boxPainter;

	public QParagraphView(Element elem) {
		super(elem);

		helper = new QViewHelper(this, getStyleSheet());
	}

	@Override
	public void paint(Graphics g, Shape allocation) {
		Rectangle r = ShapeBounds.getBounds(allocation).getBounds();
		Graphics2D g2 = helper.createGraphics((Graphics2D) g, allocation,
				false);
		g2 = helper.createGraphicsWithoutBoxPainter(g2, r, boxPainter);
		helper.paintBackground(g2, r);
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

	// methods that enforce predefined size:

	@Override
	public float getPreferredSpan(int axis) {
		Float prefinedSpanSize = QViewHelper.getPredefinedSize(this, axis);
		if (prefinedSpanSize != null)
			return prefinedSpanSize.floatValue();
		return super.getPreferredSpan(axis);
	}

	@Override
	public float getMinimumSpan(int axis) {
		Float prefinedSpanSize = QViewHelper.getPredefinedSize(this, axis);
		if (prefinedSpanSize != null)
			return prefinedSpanSize.floatValue();
		return super.getMinimumSpan(axis);
	}

	@Override
	public float getMaximumSpan(int axis) {
		Float prefinedSpanSize = QViewHelper.getPredefinedSize(this, axis);
		if (prefinedSpanSize != null)
			return prefinedSpanSize.floatValue();
		return super.getMaximumSpan(axis);
	}

	@Override
	public void setSize(float width, float height) {
		Float predefinedWidth = QViewHelper.getPredefinedSize(this,
				View.X_AXIS);
		Float predefinedHeight = QViewHelper.getPredefinedSize(this,
				View.Y_AXIS);
		if (predefinedWidth != null)
			width = predefinedWidth;
		if (predefinedHeight != null)
			height = predefinedHeight;
		super.setSize(width, height);
	}
}
