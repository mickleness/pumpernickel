package com.pump.text.html.view;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.text.Element;
import javax.swing.text.View;

import com.pump.image.shadow.ShadowAttributes;
import com.pump.text.html.style.CssTextShadowPropertyHandler;

/**
 * This helper class may be used by multiple View subclasses to help manage HTML
 * enhancements. Currently it only supports certain text-shadows, but hopefully
 * it will be scaled up to support additional features.
 * <p>
 * This sets up certain properties that the QHtmlBlockView later addresses.
 */
public class QViewHelper {
	/**
	 * This RenderingHint.Key resolves to a List of ShadowAttributes (or null)
	 */
	public static final RenderingHints.Key HINT_KEY_TEXT_SHADOW = new RenderingHints.Key(
			592393) {

		@Override
		public boolean isCompatibleValue(Object val) {
			if (val == null)
				return true;

			if (!(val instanceof java.util.List))
				return false;

			for (Object e : ((List) val)) {
				if (!(e instanceof ShadowAttributes))
					return false;
			}

			return true;
		}

		@Override
		public String toString() {
			return CssTextShadowPropertyHandler.PROPERTY_TEXT_SHADOW;
		}
	};

	/**
	 * This RenderingHint.Key resolves to the javax.swing.text.Element currently
	 * being rendered (or null).
	 */
	public static final RenderingHints.Key HINT_KEY_ELEMENT = new RenderingHints.Key(
			592394) {

		@Override
		public boolean isCompatibleValue(Object val) {
			if (val == null)
				return true;

			return val instanceof Element;
		}

		@Override
		public String toString() {
			return "element";
		}
	};

	Map<RenderingHints.Key, Object> renderingHints = new HashMap<>();

	@SuppressWarnings("unchecked")
	public QViewHelper(View view) {
		List<ShadowAttributes> textShadow = (List<ShadowAttributes>) view
				.getElement().getAttributes().getAttribute(
						CssTextShadowPropertyHandler.PROPERTY_TEXT_SHADOW);

		if (textShadow == null)
			textShadow = (List<ShadowAttributes>) view.getAttributes()
					.getAttribute(
							CssTextShadowPropertyHandler.PROPERTY_TEXT_SHADOW);

		renderingHints.put(HINT_KEY_TEXT_SHADOW, textShadow);
		renderingHints.put(HINT_KEY_ELEMENT, view.getElement());
	}

	/**
	 * Install appropriate RenderingHints in a Graphics2D.
	 */
	public void install(Graphics2D g) {
		for (Map.Entry<RenderingHints.Key, Object> entry : renderingHints
				.entrySet()) {
			if (entry.getValue() != null)
				g.setRenderingHint(entry.getKey(), entry.getValue());
		}
	}
}
