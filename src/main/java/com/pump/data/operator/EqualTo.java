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
package com.pump.data.operator;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

/**
 * This operator evaluates whether a field is equal to a fixed value, including
 * null.
 */
public class EqualTo extends AbstractValueOperator<Object> {
	private static final long serialVersionUID = 1L;

	/**
	 * Create a new EqualTo.
	 * 
	 * @param attribute
	 *            the name of the attribute to consult.
	 * @param value
	 *            the expected value. This can be null.
	 */
	public EqualTo(String attribute, Object value) {
		super(attribute, value);
	}

	@Override
	public boolean evaluate(OperatorContext context, Object bean)
			throws Exception {
		Object value = context.getValue(bean, getAttribute());
		return Objects.equals(value, getValue());
	}

	@Override
	protected String toString(boolean negated) {
		String attr = getAttribute();

		Object v = getValue();
		if (Boolean.TRUE.equals(v)) {
			if (negated) {
				return "!" + attr;
			}
			return attr;
		} else if (Boolean.FALSE) {
			if (negated) {
				return attr;
			}
			return "!" + attr;
		}

		StringBuilder sb = new StringBuilder();
		sb.append(attr);

		sb.append(negated ? " != " : " == ");
		if (v == null) {
			sb.append("null");
		} else {
			sb.append(toString(v));
		}
		return sb.toString();
	}

	@Override
	protected Map<String, Collection<TestAtom>> createTestAtoms(
			Map<String, Collection<Class>> attributeTypes) {
		Map<String, Collection<TestAtom>> map = new HashMap<>();
		Collection<TestAtom> atoms = new HashSet<>(3);
		Object v = getValue();
		atoms.add(new TestAtom(TestAtom.Type.EXACTLY, v));
		if (v instanceof Boolean) {
			Boolean b = (Boolean) v;
			atoms.add(new TestAtom(TestAtom.Type.EXACTLY, !b.booleanValue()));
		} else if (v != null) {
			atoms.add(new TestAtom(TestAtom.Type.BARELY_SMALLER_THAN, v));
			atoms.add(new TestAtom(TestAtom.Type.BARELY_BIGGER_THAN, v));
		} else if (v == null) {
			Collection<Class> sampleTypes = attributeTypes.get(getAttribute());
			boolean foundNonNullSample = false;
			if (sampleTypes != null) {
				for (Class sampleType : sampleTypes) {
					if (sampleType != null) {
						// great: it has a known non-null value.
						// some other entity will add a TestAtom for that value,
						// we don't have to do that here.
						foundNonNullSample = true;
						break;
					}
				}
			}

			if (!foundNonNullSample) {
				// we can't add this String ALL the time, because it we end up
				// comparing a Number or a Boolean to a String we'll have
				// problems. But since we already established *nobody* else
				// has a clearly defined type for our attribute: it should
				// be safe to check to see if it's a String:
				atoms.add(new TestAtom(TestAtom.Type.EXACTLY, "?"));
			}
		}
		map.put(getAttribute(), atoms);
		return map;
	}

	@Override
	protected boolean evaluateTestAtom(TestAtom atom) {
		if (atom.getType() == TestAtom.Type.EXACTLY) {
			return Objects.equals(getValue(), atom.getValue());
		}
		return false;
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

	@Override
	protected Operator createTemplateOperator() {
		return new EqualTo(getAttribute(), Operator.TEMPLATE_VALUE);
	}
}