/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.text.html;

import java.awt.Color;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.CSS;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.StyleSheet;

import com.pump.text.html.css.CssColorParser;
import com.pump.text.html.css.CssHeightParser;
import com.pump.text.html.css.CssMultiMarginParser;
import com.pump.text.html.css.CssOverflowParser;
import com.pump.text.html.css.CssParser;
import com.pump.text.html.css.CssPropertyParser;
import com.pump.text.html.css.CssSingleMarginParser;
import com.pump.text.html.css.CssTextShadowParser;
import com.pump.text.html.css.CssWidthParser;
import com.pump.text.html.css.background.CssBackgroundAttachmentParser;
import com.pump.text.html.css.background.CssBackgroundClipParser;
import com.pump.text.html.css.background.CssBackgroundPositionParser;
import com.pump.text.html.css.background.CssBackgroundRepeatParser;
import com.pump.text.html.css.background.CssBackgroundSizeParser;
import com.pump.text.html.css.border.CssBorderBottomColorParser;
import com.pump.text.html.css.border.CssBorderBottomLeftRadiusParser;
import com.pump.text.html.css.border.CssBorderBottomParser;
import com.pump.text.html.css.border.CssBorderBottomRightRadiusParser;
import com.pump.text.html.css.border.CssBorderBottomStyleParser;
import com.pump.text.html.css.border.CssBorderBottomWidthParser;
import com.pump.text.html.css.border.CssBorderColorParser;
import com.pump.text.html.css.border.CssBorderLeftColorParser;
import com.pump.text.html.css.border.CssBorderLeftParser;
import com.pump.text.html.css.border.CssBorderLeftStyleParser;
import com.pump.text.html.css.border.CssBorderLeftWidthParser;
import com.pump.text.html.css.border.CssBorderParser;
import com.pump.text.html.css.border.CssBorderRadiusParser;
import com.pump.text.html.css.border.CssBorderRightColorParser;
import com.pump.text.html.css.border.CssBorderRightParser;
import com.pump.text.html.css.border.CssBorderRightStyleParser;
import com.pump.text.html.css.border.CssBorderRightWidthParser;
import com.pump.text.html.css.border.CssBorderStyleParser;
import com.pump.text.html.css.border.CssBorderTopColorParser;
import com.pump.text.html.css.border.CssBorderTopLeftRadiusParser;
import com.pump.text.html.css.border.CssBorderTopParser;
import com.pump.text.html.css.border.CssBorderTopRightRadiusParser;
import com.pump.text.html.css.border.CssBorderTopStyleParser;
import com.pump.text.html.css.border.CssBorderTopWidthParser;
import com.pump.text.html.css.border.CssBorderWidthParser;
import com.pump.text.html.css.border.CssOutlineColorParser;
import com.pump.text.html.css.border.CssOutlineOffsetParser;
import com.pump.text.html.css.border.CssOutlineParser;
import com.pump.text.html.css.border.CssOutlineStyleParser;
import com.pump.text.html.css.border.CssOutlineWidthParser;
import com.pump.util.CombinationIterator;

/**
 * This specialized StyleSheet offers (limited) support for additional CSS
 * attributes.
 */
public class QStyleSheet extends StyleSheet {
	private static final long serialVersionUID = 1L;

	protected Map<String, Map<Object, Object>> qRules = new HashMap<>();
	protected Map<String, CssPropertyParser<?>> propertyHandlers = new HashMap<>();

	public QStyleSheet() {
		addCssPropertyHandler(new CssTextShadowParser());
		addCssPropertyHandler(new CssColorParser(CSS.Attribute.COLOR));
		addCssPropertyHandler(
				new CssColorParser(CSS.Attribute.BACKGROUND_COLOR));
		addCssPropertyHandler(new CssBackgroundRepeatParser());
		addCssPropertyHandler(new CssBackgroundPositionParser());
		addCssPropertyHandler(new CssBackgroundAttachmentParser());
		addCssPropertyHandler(new CssBackgroundSizeParser());
		addCssPropertyHandler(new CssBackgroundClipParser());
		addCssPropertyHandler(new CssOverflowParser());

		addCssPropertyHandler(new CssBorderBottomColorParser());
		addCssPropertyHandler(new CssBorderBottomWidthParser());
		addCssPropertyHandler(new CssBorderBottomStyleParser());
		addCssPropertyHandler(new CssBorderLeftColorParser());
		addCssPropertyHandler(new CssBorderLeftWidthParser());
		addCssPropertyHandler(new CssBorderLeftStyleParser());
		addCssPropertyHandler(new CssBorderTopColorParser());
		addCssPropertyHandler(new CssBorderTopWidthParser());
		addCssPropertyHandler(new CssBorderTopStyleParser());
		addCssPropertyHandler(new CssBorderRightColorParser());
		addCssPropertyHandler(new CssBorderRightWidthParser());
		addCssPropertyHandler(new CssBorderRightStyleParser());
		addCssPropertyHandler(new CssBorderColorParser());
		addCssPropertyHandler(new CssBorderWidthParser());
		addCssPropertyHandler(new CssBorderStyleParser());
		addCssPropertyHandler(new CssBorderParser());
		addCssPropertyHandler(new CssBorderBottomParser());
		addCssPropertyHandler(new CssBorderLeftParser());
		addCssPropertyHandler(new CssBorderTopParser());
		addCssPropertyHandler(new CssBorderRightParser());

		addCssPropertyHandler(new CssBorderRadiusParser());
		addCssPropertyHandler(new CssBorderTopLeftRadiusParser());
		addCssPropertyHandler(new CssBorderTopRightRadiusParser());
		addCssPropertyHandler(new CssBorderBottomLeftRadiusParser());
		addCssPropertyHandler(new CssBorderBottomRightRadiusParser());

		addCssPropertyHandler(new CssOutlineParser());
		addCssPropertyHandler(new CssOutlineOffsetParser());
		addCssPropertyHandler(new CssOutlineColorParser());
		addCssPropertyHandler(new CssOutlineStyleParser());
		addCssPropertyHandler(new CssOutlineWidthParser());

		addCssPropertyHandler(new CssWidthParser());
		addCssPropertyHandler(new CssHeightParser());
		addCssPropertyHandler(new CssSingleMarginParser(
				CssSingleMarginParser.PROPERTY_MARGIN_BOTTOM));
		addCssPropertyHandler(new CssSingleMarginParser(
				CssSingleMarginParser.PROPERTY_MARGIN_LEFT));
		addCssPropertyHandler(new CssSingleMarginParser(
				CssSingleMarginParser.PROPERTY_MARGIN_RIGHT));
		addCssPropertyHandler(new CssSingleMarginParser(
				CssSingleMarginParser.PROPERTY_MARGIN_TOP));
		addCssPropertyHandler(new CssMultiMarginParser());
	}

	/**
	 * Add a CssPropertyHandler that is consulted in
	 * {@link #getSupportedProperties(Map)}
	 */
	public void addCssPropertyHandler(CssPropertyParser<?> handler) {
		CssPropertyParser<?> oldHandler = propertyHandlers
				.put(handler.getPropertyName(), handler);
		if (oldHandler != null)
			throw new RuntimeException(
					"Two competing handlers for \"" + handler.getPropertyName()
							+ "\": " + oldHandler + ", " + handler);
	}

	public void removeCssPropertyHandler(CssPropertyParser<?> handler) {
		propertyHandlers.remove(handler.getPropertyName());
	}

	public Map<String, CssPropertyParser<?>> getCssPropertyHandlers() {
		return Collections.unmodifiableMap(propertyHandlers);
	}

	/**
	 * This is copied and pasted from the super class (with minimal changes). We
	 * aren't interested in changing this method, but we do want to implement
	 * our own getResolvedStyle().
	 */
	@Override
	public Style getRule(HTML.Tag t, Element e) {
		final Style originalStyle = super.getRule(t, e);

		String selector = getSelector(t, e);
		return createStyleWrapper(originalStyle, selector);
	}

	/**
	 * This is copied and pasted from {@link StyleSheet#getRule(Tag, Element)}.
	 * We only changed the return type so it identifies the selector.
	 */
	protected String getSelector(HTML.Tag t, Element e) {

		// Build an array of all the parent elements.
		List<Element> searchContext = new ArrayList<>();

		for (Element p = e; p != null; p = p.getParentElement()) {
			searchContext.add(p);
		}

		// Build a fully qualified selector.
		int n = searchContext.size();
		StringBuffer cacheLookup = new StringBuffer();
		AttributeSet attr;
		String eName;
		Object name;

		// >= 1 as the HTML.Tag for the 0th element is passed in.
		for (int counter = n - 1; counter >= 1; counter--) {
			e = searchContext.get(counter);
			attr = e.getAttributes();
			name = attr.getAttribute(StyleConstants.NameAttribute);
			eName = name.toString();
			cacheLookup.append(eName);
			if (attr != null) {
				if (attr.isDefined(HTML.Attribute.ID)) {
					cacheLookup.append('#');
					cacheLookup.append(attr.getAttribute(HTML.Attribute.ID));
				} else if (attr.isDefined(HTML.Attribute.CLASS)) {
					cacheLookup.append('.');
					cacheLookup.append(attr.getAttribute(HTML.Attribute.CLASS));
				}
			}
			cacheLookup.append(' ');
		}
		cacheLookup.append(t.toString());
		e = searchContext.get(0);
		attr = e.getAttributes();
		if (e.isLeaf()) {
			// For leafs, we use the second tier attributes.
			Object testAttr = attr.getAttribute(t);
			if (testAttr instanceof AttributeSet) {
				attr = (AttributeSet) testAttr;
			} else {
				attr = null;
			}
		}
		if (attr != null) {
			if (attr.isDefined(HTML.Attribute.ID)) {
				cacheLookup.append('#');
				cacheLookup.append(attr.getAttribute(HTML.Attribute.ID));
			} else if (attr.isDefined(HTML.Attribute.CLASS)) {
				cacheLookup.append('.');
				cacheLookup.append(attr.getAttribute(HTML.Attribute.CLASS));
			}
		}

		return cacheLookup.toString();
	}

	@Override
	public Style getRule(String selector) {
		Style originalStyle = super.getRule(selector);

		return createStyleWrapper(originalStyle, selector);
	}

	/**
	 * Create a custom Style object that consults our enhanced CSS support when
	 * {@link Style#getAttribute(Object)} is called.
	 * 
	 * @param originalStyle
	 *            the Style to delegate most of the calls to
	 * @param selector
	 *            the selector used to identify what attributes to consult
	 * @return a new Style that intercepts requests for the attributes this
	 *         class supports.
	 */
	private Style createStyleWrapper(Style originalStyle, String selector) {

		final Collection<String> allSelectors = getSubSelectors(selector);

		InvocationHandler handler = new InvocationHandler() {

			@Override
			public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {
				if (method.getName().equals("getAttribute")) {
					for (String selector : allSelectors) {
						Map<Object, Object> qProperties = qRules.get(selector);
						if (qProperties != null) {
							Object value = qProperties.get(args[0]);
							if (value != null)
								return value;
						}
					}
				}
				return method.invoke(originalStyle, args);
			}

		};
		return (Style) Proxy.newProxyInstance(
				QStyleSheet.class.getClassLoader(), new Class[] { Style.class },
				handler);
	}

	/**
	 * Convert a literal (real) selector into all the abstract selectors that
	 * might be applied.
	 */
	private Collection<String> getSubSelectors(String selector) {

		// this still feels like fuzzy hand-waving to me, but so far
		// it passes the (limited) unit tests I've dreamed up:

		Collection<String> returnValue = new LinkedHashSet<>();

		String[] selectorTerms = selector.split(" ");

		List<String[]> selectorTermsConfig = new ArrayList<>(
				selectorTerms.length);
		for (int a = 0; a < selectorTerms.length; a++) {
			List<String> z = new LinkedList<>();
			z.add(selectorTerms[a]);
			if (!(a == 0 || a == 1 || a == selectorTerms.length - 1)) {
				// always include leading "html body" and trailing selector
				// term, but all other terms are optional (so they may be null)
				z.add(null);
			}

			int i = selectorTerms[a].indexOf("#");
			if (i != -1 && a >= 2) {
				z.add(selectorTerms[a].substring(i));
			}

			selectorTermsConfig.add(z.toArray(new String[z.size()]));
		}

		CombinationIterator<String> comboIter = new CombinationIterator<>(
				selectorTermsConfig);
		while (comboIter.hasNext()) {
			StringBuffer sb = new StringBuffer();
			List<String> combo = comboIter.next();
			for (String str : combo) {
				if (str != null) {
					if (sb.length() > 0) {
						sb.append(' ');
					}
					sb.append(str);
				}
			}
			String subSelector = sb.toString();
			if (!subSelector.isEmpty() && qRules.containsKey(subSelector))
				returnValue.add(subSelector);
		}

		return returnValue;
	}

	@Override
	public void addRule(String rule) {
		super.addRule(rule);

		List<CssParser.Rule> rules = new CssParser().parseRules(rule);
		for (CssParser.Rule r : rules) {
			for (String selector : r.getSelectors()) {
				Map<String, String> allProperties = r.getProperties();
				Map<Object, Object> customProperties = getSupportedProperties(
						allProperties);

				if (customProperties != null) {
					selector = selector.equals("body") ? "html body"
							: "html body " + selector;
					Map<Object, Object> properties = qRules.get(selector);
					if (properties == null) {
						properties = new LinkedHashMap<>();
						qRules.put(selector, properties);
					}
					properties.putAll(customProperties);
				}
			}
		}
	}

	/**
	 * Parse all the available CSS attributes into a smaller subset of supported
	 * features.
	 * 
	 * @param incomingProperties
	 *            a series of text-based properties, like "color"->"#FFFFFF"
	 * @return a map of parsed properties which includes only the targeted
	 *         properties this class supports. So if the incoming map is 20
	 *         elements, the returned map may be 0-5 elements.
	 */
	protected Map<Object, Object> getSupportedProperties(
			Map<String, String> incomingProperties) {
		Map<Object, Object> returnValue = null;

		for (Map.Entry<String, String> entry : incomingProperties.entrySet()) {
			CssPropertyParser<?> handler = propertyHandlers.get(entry.getKey());
			if (handler != null) {
				try {
					Object value = handler.parse(entry.getValue());
					if (value != null) {
						if (returnValue == null)
							returnValue = new HashMap<>();
						returnValue.put(handler.getAttributeKey(), value);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		return returnValue;
	}

	@Override
	public AttributeSet getDeclaration(String decl) {
		AttributeSet returnValue = super.getDeclaration(decl);
		if (decl != null) {
			Map<String, String> declarationProperties = new CssParser()
					.parseDeclaration(decl);
			Map<Object, Object> supportedProperties = getSupportedProperties(
					declarationProperties);
			if (supportedProperties != null) {
				for (Map.Entry<Object, Object> entry : supportedProperties
						.entrySet()) {
					if (!(returnValue instanceof SimpleAttributeSet)) {
						returnValue = new SimpleAttributeSet(returnValue);
					}
					((SimpleAttributeSet) returnValue)
							.addAttribute(entry.getKey(), entry.getValue());
				}
			}
		}
		return returnValue;
	}

	@Override
	public Color getForeground(AttributeSet a) {
		Object t = a.getAttribute(CSS.Attribute.COLOR);
		if (t instanceof Color)
			return (Color) t;
		return super.getForeground(a);
	}

	@Override
	public Color getBackground(AttributeSet a) {
		Object t = a.getAttribute(CSS.Attribute.BACKGROUND_COLOR);
		if (t instanceof Color)
			return (Color) t;
		return super.getBackground(a);
	}
}