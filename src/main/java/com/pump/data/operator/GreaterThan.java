package com.pump.data.operator;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;

public class GreaterThan extends AbstractValueOperator<Comparable<?>> {
	private static final long serialVersionUID = 1L;

	public GreaterThan(String attribute, Comparable<?> value) {
		super(attribute, value);
		Objects.requireNonNull(value);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public boolean evaluate(OperatorContext context, Object bean)
			throws Exception {
		Comparable value = (Comparable) context.getValue(bean, getAttribute());
		return value.compareTo(getValue()) > 0;
	}

	@Override
	protected String toString(boolean negated) {
		StringBuilder sb = new StringBuilder();
		sb.append(getAttribute());
		sb.append(negated ? " <= " : " > ");
		sb.append(toString(getValue()));
		return sb.toString();
	}

	@Override
	protected Map<String, Collection<TestAtom>> createTestAtoms() {
		Map<String, Collection<TestAtom>> map = new HashMap<>();
		TestAtom greaterThan = new TestAtom(TestAtom.Type.GREATER_THAN,
				getValue());
		TestAtom equalTo = new TestAtom(TestAtom.Type.EQUAL_TO, getValue());

		Collection<TestAtom> t = new HashSet<>(2);
		t.add(greaterThan);
		t.add(equalTo);
		map.put(getAttribute(), t);
		return map;
	}

	@Override
	protected boolean evaluateTestAtom(TestAtom atom) {
		Object atomValue = atom.getValue();

		if (atomValue == null || atom.getType() == TestAtom.Type.LIKE)
			return false;

		Comparable k = getValue();
		int z = k.compareTo(atomValue);
		if (z == 0)
			return atom.getType() == TestAtom.Type.GREATER_THAN;
		return z < 0;
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