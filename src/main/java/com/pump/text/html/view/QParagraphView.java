package com.pump.text.html.view;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;

import javax.swing.SizeRequirements;
import javax.swing.text.Element;
import javax.swing.text.View;
import javax.swing.text.html.CSS;
import javax.swing.text.html.ParagraphView;
import javax.swing.text.html.StyleSheet;
import javax.swing.text.html.StyleSheet.BoxPainter;

import com.pump.text.html.css.CssLineHeightValue;

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

	protected CssLineHeightValue getLineHeight() {
		Object z = getAttributes().getAttribute(CSS.Attribute.LINE_HEIGHT);
		if (z == null)
			return new CssLineHeightValue("normal");
		return new CssLineHeightValue(z.toString());
	}

	/**
	 * This is overridden to accommodate the "line-height" property
	 */
	@Override
	protected SizeRequirements calculateMajorAxisRequirements(int axis,
			SizeRequirements r) {
		SizeRequirements returnValue = super.calculateMajorAxisRequirements(
				axis, r);

		CssLineHeightValue lineHeight = getLineHeight();
		float lineHeightScale = -1;
		if (lineHeight.getValue().getUnit().equals("%")) {
			lineHeightScale = lineHeight.getValue().getValue() / 100f;
		} else if (lineHeight.getValue().getUnit().isEmpty()) {
			lineHeightScale = lineHeight.getValue().getValue();
		} else {
			float fixedHeight = lineHeight.getValue().getValue();
			int z = Math.round(fixedHeight * getViewCount());
			returnValue.preferred = z;
			returnValue.minimum = z;
			returnValue.maximum = z;
		}

		if (lineHeightScale >= 0) {
			if (lineHeightScale >= 0) {
				returnValue.preferred = Math
						.round(returnValue.preferred * lineHeightScale);
				returnValue.minimum = Math
						.round(returnValue.minimum * lineHeightScale);
				returnValue.maximum = Math
						.round(returnValue.maximum * lineHeightScale);
			}
		}
		return returnValue;
	}

	static class Sizes {
		final View view;
		final int axis;
		final float lineHeightScale;
		final int childCount;

		float[] childPrefSpan, childMinSpan, childMaxSpan;
		float spacedPrefSum = 0;
		float spacedMinSum = 0;
		float spacedMaxSum = 0;

		float minSum;

		Sizes(View view, int axis, float lineHeightScale) {
			this.view = view;
			this.axis = axis;
			childCount = view.getViewCount();
			this.lineHeightScale = lineHeightScale;
		}

		/**
		 * Populate childPrefSpan and spacedPrefSum
		 */
		void populatePreferred() {
			spacedPrefSum = 0;
			childPrefSpan = new float[childCount];
			for (int i = 0; i < childCount; i++) {
				View v = view.getView(i);
				childPrefSpan[i] = v.getPreferredSpan(axis);
				spacedPrefSum += childPrefSpan[i] * lineHeightScale;
			}
		}

		/**
		 * Populate childMinSpan and spacedMinSum
		 */
		void populateMinimum() {
			spacedMinSum = 0;
			childMinSpan = new float[childCount];
			for (int i = 0; i < childCount; i++) {
				View v = view.getView(i);
				childMinSpan[i] = v.getMinimumSpan(axis);
				minSum += childMinSpan[i];
				spacedMinSum += childMinSpan[i] * lineHeightScale;
			}
		}

		/**
		 * Populate childMaxSpan and spacedMaxSum
		 */
		void populateMaximum() {
			spacedMaxSum = 0;
			childMaxSpan = new float[childCount];
			for (int i = 0; i < childCount; i++) {
				View v = view.getView(i);
				childMaxSpan[i] = v.getMaximumSpan(axis);
				spacedMaxSum += childMaxSpan[i] * lineHeightScale;
			}
		}

		/**
		 * Populate offsets and spans by assigning every child element the exact
		 * size of childSizes.
		 */
		void layout(float[] childSizes, int[] offsets, int[] spans) {
			float rowOffset = 0;
			float emptyWeight = lineHeightScale - 1;
			for (int i = 0; i < childCount; i++) {
				float rowEmptyWeight = childSizes[i] * emptyWeight;
				offsets[i] = Math.round(rowOffset + rowEmptyWeight / 2);
				spans[i] = Math.round(childSizes[i]);
				rowOffset += childSizes[i] * lineHeightScale;
			}
		}

		public void layout(float[] lowerSizeBound, float[] upperSizeBound,
				float spaceToDistribute, int[] offsets, int[] spans) {
			if (spaceToDistribute <= 0) {
				// this is the simple case: we're just going to lay everything
				// out exclusively using the lowerSizeBound sizes:
				layout(lowerSizeBound, offsets, spans);
				return;
			}

			// this is trickier: we have leftoverSpace, and we have
			// to decide which children get how much extra space

			float totalCapacity = 0;
			for (int i = 0; i < childCount; i++) {
				float childCapacity = upperSizeBound[i] - lowerSizeBound[i];
				if (childCapacity > 0) {
					totalCapacity += childCapacity;
				}
			}

			float[] childSizes = new float[childCount];

			for (int i = 0; i < childCount; i++) {
				float childCapacity = upperSizeBound[i] - lowerSizeBound[i];
				if (childCapacity > 0) {
					float childAllowance = childCapacity / totalCapacity
							* spaceToDistribute / lineHeightScale;
					childSizes[i] = lowerSizeBound[i] + childAllowance;
				}
			}

			layout(childSizes, offsets, spans);
		}
	}

	/**
	 * This is overridden to accommodate the "line-height" property
	 */
	@Override
	protected void layoutMajorAxis(int targetSpan, int axis, int[] offsets,
			int[] spans) {
		CssLineHeightValue lineHeight = getLineHeight();
		float lineHeightScale = -1;
		if (lineHeight.getValue().getUnit().equals("%")) {
			lineHeightScale = lineHeight.getValue().getValue() / 100f;
		} else if (lineHeight.getValue().getUnit().isEmpty()) {
			lineHeightScale = lineHeight.getValue().getValue();
		} else {
			float fixedHeight = lineHeight.getValue().getValue();
			float offset = 0;
			for (int i = 0; i < getViewCount(); i++) {
				spans[i] = Math.round(offset);
				spans[i] = Math.round(fixedHeight);
				offset += fixedHeight;
			}
			return;
		}

		if (lineHeightScale == 1) {
			super.layoutMajorAxis(targetSpan, axis, offsets, spans);
			return;
		}

		Sizes sizes = new Sizes(this, axis, lineHeightScale);
		sizes.populatePreferred();

		int spacedPrefSumInt = (int) sizes.spacedPrefSum;
		if (spacedPrefSumInt == targetSpan) {
			sizes.layout(sizes.childPrefSpan, offsets, spans);
			return;
		}

		if (spacedPrefSumInt > targetSpan) {
			// our preferred size is too large, so let's consult min sizes:
			sizes.populateMinimum();
			sizes.layout(sizes.childMinSpan, sizes.childPrefSpan,
					targetSpan - sizes.spacedMinSum, offsets, spans);
			return;
		}

		// our preferred size is too small so let's consult max sizes:

		sizes.populateMaximum();

		int spacedMaxSumInt = (int) sizes.spacedMaxSum;
		if (spacedMaxSumInt <= targetSpan) {
			sizes.layout(sizes.childMaxSpan, offsets, spans);
			return;
		}

		sizes.layout(sizes.childPrefSpan, sizes.childMaxSpan,
				targetSpan - sizes.spacedPrefSum, offsets, spans);

		return;
	}
}
