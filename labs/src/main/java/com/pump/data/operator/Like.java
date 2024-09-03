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
package com.pump.data.operator;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

import com.pump.text.WildcardPattern;
import com.pump.text.WildcardPattern.FixedCharacter;

/**
 * This operator evaluates whether a String field matches a WildcardPattern.
 * <p>
 * Among other things: this can be used to see if a String starts or ends with
 * certain characters, or to ignore case sensitivity when checking equality.
 */
public class Like extends AbstractValueOperator<WildcardPattern> {
	private static final long serialVersionUID = 1L;

	public static String FUNCTION_NAME = "matches";

	public Like(String attribute, WildcardPattern pattern) {
		super(attribute, pattern);
		Objects.requireNonNull(pattern);
	}

	@Override
	public boolean evaluate(OperatorContext context, Object bean)
			throws Exception {
		Object value = context.getValue(bean, getAttribute());
		if (value == null)
			return false;
		String str = String.valueOf(value);
		return getValue().matches(str);
	}

	@Override
	protected String toString(boolean negated) {
		StringBuilder sb = new StringBuilder();
		if (negated)
			sb.append("!");
		sb.append(FUNCTION_NAME);
		sb.append("(");
		sb.append(getAttribute());
		sb.append(", \"");
		sb.append(getValue().getPatternText());
		sb.append("\")");
		return sb.toString();
	}

	@Override
	protected Map<String, Collection<Class>> getAttributeTypes() {
		Map<String, Collection<Class>> returnValue = new HashMap<>(1);
		Collection<Class> c = new HashSet<>();
		c.add(String.class);
		returnValue.put(getAttribute(), c);
		return returnValue;
	}

	@Override
	protected Map<String, Collection<TestAtom>> createTestAtoms(
			Map<String, Collection<Class>> attributeTypes) {
		Collection<TestAtom> c = new HashSet<>();
		String exactMatch = getExactStringMatch();
		if (exactMatch == null) {
			c.add(new TestAtom(TestAtom.Type.LIKE, getValue()));
		} else {
			c.add(new TestAtom(TestAtom.Type.EXACTLY, exactMatch));
		}
		c.add(new TestAtom(TestAtom.Type.EXACTLY, null));

		Map<String, Collection<TestAtom>> map = new HashMap<>(1);
		map.put(getAttribute(), c);
		return map;
	}

	/**
	 * If this Like operator is used to match to exactly one String, then this
	 * returns that String. Otherwise this returns null.
	 */
	private String getExactStringMatch() {
		if (!getValue().getFormat().caseSensitive)
			return null;

		StringBuilder sb = new StringBuilder();
		for (int a = 0; a < getValue().getPlaceholderCount(); a++) {
			WildcardPattern.Placeholder p = getValue().getPlaceholder(a);
			if (p instanceof FixedCharacter) {
				FixedCharacter fc = (FixedCharacter) p;
				sb.append(fc.ch);
			} else {
				return null;
			}
		}
		return sb.toString();
	}

	@Override
	protected boolean evaluateTestAtom(TestAtom atom) {
		if (atom.getType() == TestAtom.Type.EXACTLY) {
			if (atom.getValue() instanceof CharSequence) {
				CharSequence s = (CharSequence) atom.getValue();
				return getValue().matches(s);
			}
		}
		if (atom.getType() != TestAtom.Type.LIKE)
			return false;
		return getValue().equals(atom.getValue());
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeInt(0);
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		int version = in.readInt();
		if (version == 0) {
			// the class AbstractValueOperator writes our data
		} else {
			throw new IOException("Unsupported internal version: " + version);
		}
	}

	private static WildcardPattern TEMPLATE_WILDCARD_PATTERN = new WildcardPattern(
			"*");

	@Override
	protected Operator createTemplateOperator() {
		return new Like(getAttribute(), TEMPLATE_WILDCARD_PATTERN);
	}
}