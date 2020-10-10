package com.pump.text.html.css;

import java.awt.Color;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
		InvocationHandler handler = new InvocationHandler() {

			@Override
			public Object invoke(Object proxy, Method method, Object[] args)
					throws Throwable {
				if (method.getName().equals("getAttribute")) {
					Map<Object, Object> qProperties = qRules.get(selector);
					if (qProperties != null) {
						Object value = qProperties.get(args[0]);
						if (value != null)
							return value;
					}
				}
				return method.invoke(originalStyle, args);
			}

		};
		return (Style) Proxy.newProxyInstance(
				QStyleSheet.class.getClassLoader(), new Class[] { Style.class },
				handler);
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
					selector = "html body " + selector;
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
}
