package com.pump.data.operator;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Objects;

public class In extends AbstractValueOperator<Collection<?>> {
	private static final long serialVersionUID = 1L;

	public static String FUNCTION_NAME = "contains";

	public static Operator create(String attribute, Collection<?> values) {
		Objects.requireNonNull(values);
		if (values.size() == 0)
			return FALSE;
		if (values.size() == 1)
			return new EqualTo(attribute, values.iterator().next());
		return new In(attribute, values);
	}

	private In(String attribute, Collection<?> value) {
		super(attribute, new LinkedHashSet<>(value));
	}

	@Override
	public boolean evaluate(OperatorContext context, Object bean)
			throws Exception {
		Object value = context.getValue(bean, getAttribute());
		return getValue().contains(value);
	}

	@Override
	protected String toString(boolean negated) {
		StringBuilder sb = new StringBuilder();
		if (negated)
			sb.append("!");
		sb.append(FUNCTION_NAME);
		sb.append("(");
		sb.append(getAttribute());
		sb.append(", {");
		boolean first = true;
		for (Object e : getValue()) {
			if (!first)
				sb.append(", ");
			first = false;
			if (e == null) {
				sb.append("null");
			} else {
				sb.append(toString(e));
			}
		}
		sb.append("})");
		return sb.toString();
	}

	@Override
	protected Operator createCanonicalOperator() {
		Collection<?> values = getValue();
		Operator[] operators = new Operator[values.size()];
		int ctr = 0;
		for (Object e : values) {
			operators[ctr++] = new EqualTo(getAttribute(), e);
		}
		return new Or(operators);
	}

	@Override
	public Collection<Operator> split() {
		Collection<Operator> returnValue = new HashSet<>();
		for (Object e : getValue()) {
			returnValue.add(new EqualTo(getAttribute(), e));
		}
		return returnValue;
	}

	@Override
	public boolean equals(Operator operator, boolean strictEquivalency) {
		if (operator instanceof In && strictEquivalency) {
			// the order of both collections matters (at least for unit tests)
			Collection<?> myC = (Collection<?>) getOperand(1);
			Collection<?> otherC = (Collection<?>) operator.getOperand(1);
			if (myC.size() != otherC.size())
				return false;
			Iterator<?> i1 = myC.iterator();
			Iterator<?> i2 = otherC.iterator();
			while (i1.hasNext()) {
				Object k1 = i1.next();
				Object k2 = i2.next();
				if (!Objects.equals(k1, k2))
					return false;
			}
			return true;
		}
		return super.equals(operator, strictEquivalency);
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