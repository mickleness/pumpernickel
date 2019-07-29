package com.pump.data.operator;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import com.pump.text.WildcardPattern;

public class Like extends AbstractValueOperator<WildcardPattern> {
	private static final long serialVersionUID = 1L;

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
		sb.append("matches(");
		sb.append(getAttribute());
		sb.append(", \"");
		sb.append(getValue().getPatternText());
		sb.append("\")");
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