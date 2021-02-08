package com.pump.text.html.view;

import javax.swing.text.View;
import javax.swing.text.html.CSS;

import com.pump.text.html.css.CssLength;

/**
 * This class helps with span size calculations.
 * <p>
 * This takes into account the possibility that a tag has a predefined
 * width/height. For example: if Swing's legacy renderer would normally size a
 * View as 10x10 px, but the HTML indicates the dimensions should be 50x50: then
 * this class returns 50x50 as the appropriate size.
 * <p>
 * However if Swing's legacy renderer would normally size a View as 100x100 px,
 * but the HTML requests 50x50: then this returns 100x100.
 */
public class QViewSizeHelper extends QViewHelper {

	public QViewSizeHelper(View view) {
		super(view);
	}

	public float getPreferredSpan(View view, int axis,
			float defaultLegacyPreferredSpan) {
		float returnValue = defaultLegacyPreferredSpan;

		Float prefinedSpanSize = getPredefinedSize(view, axis);
		if (prefinedSpanSize != null) {
			returnValue = prefinedSpanSize.floatValue();
		}
		return returnValue;
	}

	public float getMinimumSpan(View view, int axis,
			float defaultLegacyMinimumSpan) {
		float returnValue = defaultLegacyMinimumSpan;

		Float prefinedSpanSize = getPredefinedSize(view, axis);
		if (prefinedSpanSize != null) {
			returnValue = prefinedSpanSize.floatValue();
		}
		return returnValue;
	}

	public float getMaximumSpan(View view, int axis,
			float defaultLegacyMaximumSpan) {
		float returnValue = defaultLegacyMaximumSpan;

		Float prefinedSpanSize = getPredefinedSize(view, axis);
		if (prefinedSpanSize != null) {
			returnValue = prefinedSpanSize.floatValue();
		}
		return returnValue;
	}

	java.lang.Float getPredefinedSize(View view, int axis) {
		// TODO: percents require incorporating parent size

		// TODO: consider other unit sizes

		float topMargin = getLength(CSS.Attribute.MARGIN_TOP, -1);
		float bottomMargin = getLength(CSS.Attribute.MARGIN_BOTTOM, -1);
		float leftMargin = getLength(CSS.Attribute.MARGIN_LEFT, -1);
		float rightMargin = getLength(CSS.Attribute.MARGIN_RIGHT, -1);

		BorderRenderingConfiguration borderConfig = BorderRenderingConfiguration
				.forBorder(this);
		topMargin += borderConfig.topWidth == null ? 0
				: borderConfig.topWidth.getValue();
		bottomMargin += borderConfig.bottomWidth == null ? 0
				: borderConfig.bottomWidth.getValue();
		leftMargin += borderConfig.leftWidth == null ? 0
				: borderConfig.leftWidth.getValue();
		rightMargin += borderConfig.rightWidth == null ? 0
				: borderConfig.rightWidth.getValue();

		if (axis == View.X_AXIS) {
			Object z = (Object) view.getAttributes()
					.getAttribute(CSS.Attribute.WIDTH);
			if (z != null) {
				CssLength l = new CssLength(z.toString());
				return l.getValue() + leftMargin + rightMargin;
			}
			return null;
		} else if (axis == View.Y_AXIS) {
			Object z = (Object) view.getAttributes()
					.getAttribute(CSS.Attribute.HEIGHT);
			if (z != null) {
				CssLength l = new CssLength(z.toString());
				return l.getValue() + topMargin + bottomMargin;
			}
			return null;
		}
		throw new IllegalArgumentException("unrecognized axis: " + axis);
	}
}
