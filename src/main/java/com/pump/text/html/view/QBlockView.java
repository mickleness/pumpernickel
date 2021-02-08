package com.pump.text.html.view;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;

import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.html.BlockView;
import javax.swing.text.html.StyleSheet;
import javax.swing.text.html.StyleSheet.BoxPainter;

/**
 * This BlockView uses the QViewHelper to support text-shadows.
 */
public class QBlockView extends BlockView implements LegacyCssView {
	BoxPainter boxPainter;

	public QBlockView(Element elem, int axis) {
		super(elem, axis);
	}

	@Override
	public void paint(Graphics g, Shape allocation) {
		QViewRenderer.paint((Graphics2D) g, allocation, this, this,
				getStyleSheet(), boxPainter, false);
	}

	@Override
	public void paintLegacyCss2(Graphics g, Shape allocation) {
		super.paint(g, allocation);

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
		QViewSizeHelper q = new QViewSizeHelper(this);
		return q.getPreferredSpan(this, axis, super.getPreferredSpan(axis));
	}

	@Override
	public float getMinimumSpan(int axis) {
		QViewSizeHelper q = new QViewSizeHelper(this);
		return q.getMinimumSpan(this, axis, super.getMinimumSpan(axis));
	}

	@Override
	public float getMaximumSpan(int axis) {
		QViewSizeHelper q = new QViewSizeHelper(this);
		return q.getMaximumSpan(this, axis, super.getMaximumSpan(axis));
	}

	@Override
	public void setSize(float width, float height) {
		QViewSizeHelper q = new QViewSizeHelper(this);
		Float predefinedWidth = q.getPredefinedSize(this, View.X_AXIS);
		Float predefinedHeight = q.getPredefinedSize(this, View.Y_AXIS);
		if (predefinedWidth != null)
			width = predefinedWidth;
		if (predefinedHeight != null)
			height = predefinedHeight;
		super.setSize(width, height);
	}
}
