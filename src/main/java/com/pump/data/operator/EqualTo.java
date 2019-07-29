package com.pump.data.operator;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
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
	protected Operator createCanonicalOperator() {
		return this;
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