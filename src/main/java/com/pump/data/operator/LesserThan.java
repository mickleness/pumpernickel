package com.pump.data.operator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * This operator evaluates whether a field is smaller than a given value.
 */
public class LesserThan extends AbstractValueOperator<Comparable<?>> {
	private static final long serialVersionUID = 1L;

	public LesserThan(String attribute, Comparable<?> value) {
		super(attribute, value);
		Objects.requireNonNull(value);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public boolean evaluate(OperatorContext context, Object bean)
			throws Exception {
		Comparable value = (Comparable) context.getValue(bean, getAttribute());
		return value.compareTo(getValue()) < 0;
	}

	@Override
	protected String toString(boolean negated) {
		StringBuilder sb = new StringBuilder();
		sb.append(getAttribute());
		sb.append(negated ? " >= " : " < ");
		sb.append(getValue());
		return sb.toString();
	}

	@Override
	protected Map<String, Collection<TestAtom>> createTestAtoms() {
		Map<String, Collection<TestAtom>> map = new HashMap<>();
		TestAtom lessThan = new TestAtom(TestAtom.Type.BARELY_SMALLER_THAN,
				getValue());
		TestAtom equalTo = new TestAtom(TestAtom.Type.EXACTLY, getValue());

		Collection<TestAtom> t = new HashSet<>(2);
		t.add(lessThan);
		t.add(equalTo);
		map.put(getAttribute(), t);
		return map;
	}

	@Override
	protected Operator createCanonicalOperator() {
		// we can't support "lesserThan" and "greaterThan" at the same time
		// in an unambiguous canonical representation.
		// so instead we'll convert an expression like "a < 50" to
		// "!(a > 50) && a!=50)"
		List<Operator> k = new ArrayList<>(2);
		k.add(new Not(new GreaterThan(getAttribute(), getValue())));
		k.add(new Not(new EqualTo(getAttribute(), getValue())));
		Collections.sort(k, toStringComparator);
		return new And(k);
	}

	@Override
	protected boolean evaluateTestAtom(TestAtom atom) {
		Object atomValue = atom.getValue();

		if (atomValue == null || atom.getType() == TestAtom.Type.LIKE)
			return false;

		Comparable k = getValue();
		int z = k.compareTo(atomValue);
		if (z == 0)
			return atom.getType() == TestAtom.Type.BARELY_SMALLER_THAN;
		return z > 0;
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
}