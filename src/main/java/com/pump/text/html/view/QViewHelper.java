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

import java.util.Objects;

import javax.swing.text.AttributeSet;
import javax.swing.text.View;
import javax.swing.text.html.CSS;

import com.pump.text.html.css.CssColorParser;
import com.pump.text.html.css.CssLength;
import com.pump.text.html.css.image.CssImageParser;

/**
 * This is a helper class
 */
public abstract class QViewHelper {

	protected final View view;

	public QViewHelper(View view) {
		Objects.requireNonNull(view);
		this.view = view;
	}

	/**
	 * Return an attribute associated with this object's View.
	 */
	public Object getAttribute(Object attrKey, boolean canConsultParent) {
		// get value from tag declaration
		Object value = null;
		if (canConsultParent
				|| view.getElement().getAttributes().isDefined(attrKey)) {
			value = view.getElement().getAttributes().getAttribute(attrKey);
		}

		if (value == null) {
			// get value from css rule
			AttributeSet attrs = view.getAttributes();
			value = attrs == null ? null : attrs.getAttribute(attrKey);
		}

		if (attrKey == CSS.Attribute.BACKGROUND_IMAGE && value != null) {
			String str = value.toString();
			return str == null ? null : new CssImageParser().parse(str);
		} else if (attrKey == CSS.Attribute.BACKGROUND_COLOR && value != null) {
			String str = value.toString();
			return new CssColorParser(CSS.Attribute.BACKGROUND_COLOR)
					.parse(str);
		}

		return value;
	}

	/**
	 * Return the View associated with this QViewHelper.
	 */
	public View getView() {
		return view;
	}

	/**
	 * Return the length (in pixels) of an attribute. If the attribute is
	 * undefined this returns 0.
	 * <p>
	 * If this returns -1 then the value could not be determined. For example:
	 * if the range provided is negative, and the length is expressed as a
	 * percent, then we can't calculate that percentage.
	 * 
	 * @param attributeKey
	 *            the attribute key to consult.
	 * @param range
	 *            the range used with percents. For example if this value is 25
	 *            and the length we're calculating is "50%", then this will
	 *            return 12.5f.
	 *            <p>
	 *            This argument may be negative, which indicates we don't yet
	 *            know this information.
	 */
	protected float getLength(Object attributeKey, double range) {
		Object attr = view.getAttributes().getAttribute(attributeKey);
		if (attr == null)
			return 0;

		CssLength l = new CssLength(attr.toString().toString());
		return range >= 0 ? l.getValue(range) : l.getValue();
	}
}