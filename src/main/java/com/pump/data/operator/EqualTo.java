package com.pump.data.operator;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

public class EqualTo extends AbstractValueOperator<Object> {
	private static final long serialVersionUID = 1L;

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
	protected Map<String, Collection<TestAtom>> createTestAtoms() {
		Map<String, Collection<TestAtom>> map = new HashMap<>();
		Collection<TestAtom> atoms = new HashSet<>(3);
		Object v = getValue();
		atoms.add(new TestAtom(TestAtom.Type.EQUAL_TO, v));
		if (v instanceof Boolean) {
			Boolean b = (Boolean) v;
			atoms.add(new TestAtom(TestAtom.Type.EQUAL_TO, !b.booleanValue()));
		} else if (v != null) {
			atoms.add(new TestAtom(TestAtom.Type.LESSER_THAN, v));
			atoms.add(new TestAtom(TestAtom.Type.GREATER_THAN, v));
		}
		map.put(getAttribute(), atoms);
		return map;
	}

	@Override
	protected boolean evaluateTestAtom(TestAtom atom) {
		if (atom.getType() == TestAtom.Type.EQUAL_TO) {
			return Objects.equals(getValue(), atom.getValue());
		}
		return false;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Collection<Operator> split() {
		return (Collection) Collections.singleton(this);
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeInt(0);
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		int version = in.readInt();
		if (version == 0) {
		} else {
			throw new IOException("Unsupported internal version: " + version);
		}
	}
}