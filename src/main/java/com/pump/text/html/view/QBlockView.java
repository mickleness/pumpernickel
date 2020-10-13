package com.pump.text.html.view;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;

import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.html.BlockView;
import javax.swing.text.html.StyleSheet;
import javax.swing.text.html.StyleSheet.BoxPainter;

import com.pump.geom.ShapeBounds;

/**
 * This BlockView uses the QViewHelper to support text-shadows.
 */
public class QBlockView extends BlockView {
	QViewHelper helper;
	BoxPainter boxPainter;

	public QBlockView(Element elem, int axis) {
		super(elem, axis);

		helper = new QViewHelper(this);
	}

	@Override
	public void paint(Graphics g, Shape allocation) {
		Graphics2D g2 = helper.createGraphics((Graphics2D) g, allocation,
				false);
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
		// but that's private so we'll create our own that's just like it.
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
