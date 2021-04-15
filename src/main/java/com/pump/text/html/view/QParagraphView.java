package com.pump.text.html.view;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;

import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.html.ParagraphView;
import javax.swing.text.html.StyleSheet;
import javax.swing.text.html.StyleSheet.BoxPainter;

/**
 * This ParagraphView uses the QViewHelper to support text-shadows.
 */
public class QParagraphView extends ParagraphView implements LegacyCssView {
	BoxPainter boxPainter;

	public QParagraphView(Element elem) {
		super(elem);
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
		// but that's private so we'll create our own
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

	@Override
	public float getAlignment(int axis) {
		if (axis == View.X_AXIS) {
			// TODO: make default for h1 max-content to be left-aligned, like
			// jsfiddle

			// The original Swing HTML model interprets margins pretty well,
			// but it doesn't support "auto". So if a margin is something like
			// "5px", then architecture outside of this method is already
			// going to do the right thing. This method only needs to push
			// things to be left, center or right-aligned based on the auto
			// option.

			QViewSizeHelper q = new QViewSizeHelper(this);
			MarginConfiguration mc = new MarginConfiguration(q);
			if (mc.left != null && mc.right != null && mc.left.isAuto()
					&& mc.right.isAuto()) {
				return .5f;
			} else if (mc.left != null && mc.left.isAuto()) {
				return 1;
			} else if (mc.right != null && mc.right.isAuto()) {
				return 0;
			}
		} else if (axis == View.Y_AXIS) {
			QViewSizeHelper q = new QViewSizeHelper(this);
			MarginConfiguration mc = new MarginConfiguration(q);
			if (mc.top != null && mc.bottom != null && mc.top.isAuto()
					&& mc.bottom.isAuto()) {
				return .5f;
			} else if (mc.top != null && mc.top.isAuto()) {
				return 1;
			} else if (mc.bottom != null && mc.bottom.isAuto()) {
				return 0;
			}
		}
		return super.getAlignment(axis);
	}
}
