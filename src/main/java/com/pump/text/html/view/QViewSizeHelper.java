/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://raw.githubusercontent.com/mickleness/pumpernickel/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.text.html.view;

import java.util.Collection;
import java.util.HashSet;

import javax.swing.text.View;
import javax.swing.text.html.CSS;

import com.pump.text.html.css.CssDimensionValue;
import com.pump.text.html.css.CssHeightParser;
import com.pump.text.html.css.CssLength;
import com.pump.text.html.css.CssWidthParser;

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

	private static ThreadLocal<Collection<View>> activeViewCalculations = new ThreadLocal<>();

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

		Collection<View> activeViews = activeViewCalculations.get();
		if (activeViews == null) {
			activeViews = new HashSet<>();
			activeViewCalculations.set(activeViews);
		}
		if (!activeViews.add(view))
			return null;

		try {
			CssDimensionValue d;
			if (axis == View.X_AXIS) {
				d = (CssDimensionValue) getAttribute(
						CssWidthParser.PROPERTY_WIDTH, false);
			} else if (axis == View.Y_AXIS) {
				d = (CssDimensionValue) getAttribute(
						CssHeightParser.PROPERTY_HEIGHT, false);
			} else {
				throw new IllegalArgumentException(
						"unrecognized axis: " + axis);
			}

			if (d != null) {
				BorderRenderingConfiguration borderConfig = BorderRenderingConfiguration
						.forBorder(this);

				float totalMargins;
				if (axis == View.X_AXIS) {
					float leftMargin = getLength(CSS.Attribute.MARGIN_LEFT, -1);
					float rightMargin = getLength(CSS.Attribute.MARGIN_RIGHT,
							-1);
					leftMargin += borderConfig.leftWidth == null ? 0
							: borderConfig.leftWidth.getValue();
					rightMargin += borderConfig.rightWidth == null ? 0
							: borderConfig.rightWidth.getValue();
					totalMargins = leftMargin + rightMargin;
				} else {
					float topMargin = getLength(CSS.Attribute.MARGIN_TOP, -1);
					float bottomMargin = getLength(CSS.Attribute.MARGIN_BOTTOM,
							-1);
					topMargin += borderConfig.topWidth == null ? 0
							: borderConfig.topWidth.getValue();
					bottomMargin += borderConfig.bottomWidth == null ? 0
							: borderConfig.bottomWidth.getValue();
					totalMargins = topMargin + bottomMargin;
				}

				CssDimensionValue.Type type = d.getType();
				if (type == CssDimensionValue.Type.MAX_CONTENT) {
					return view.getPreferredSpan(axis) + totalMargins;
				} else if (type == CssDimensionValue.Type.MIN_CONTENT) {
					return view.getMinimumSpan(axis) + totalMargins;
				} else if (type == CssDimensionValue.Type.LENGTH) {
					CssLength l = d.getLength();
					return l.getValue() + totalMargins;
				}
			}
			return null;
		} finally {
			activeViews.remove(view);
			if (activeViews.isEmpty())
				activeViewCalculations.remove();
		}
	}
}