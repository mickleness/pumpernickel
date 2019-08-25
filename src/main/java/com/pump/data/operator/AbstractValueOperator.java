package com.pump.data.operator;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import com.pump.io.parser.java.JavaEncoding;

/**
 * This evaluates an attribute from a bean.
 *
 * @param <String>
 * @param <DataType>
 */
public abstract class AbstractValueOperator<DataType> extends Operator {
	private static final long serialVersionUID = 1L;

	private String attributeName;
	private DataType fixedValue;

	/**
	 * @param valueName
	 *            the value to inspect in incoming beans. This may not be null.
	 * @param fixedValue
	 *            the data type this operator compares the value against.
	 */
	protected AbstractValueOperator(String attributeName, DataType fixedValue) {
		Objects.requireNonNull(attributeName);
		this.attributeName = attributeName;
		this.fixedValue = fixedValue;
	}

	@Override
	protected Operator createCanonicalOperator() {
		return this;
	}

	@Override
	public final int getOperandCount() {
		return 2;
	}

	public String getAttribute() {
		return (String) getOperand(0);
	}

	@SuppressWarnings("unchecked")
	public DataType getValue() {
		return (DataType) getOperand(1);
	}

	public Object getOperand(int index) {
		if (index == 0)
			return attributeName;
		if (index == 1)
			return fixedValue;
		throw new IllegalArgumentException("illegal operand index: " + index);
	}

	@Override
	public Collection<String> getAttributes() {
		return Collections.singleton(getAttribute());
	}

	protected String toString(Object value) {
		StringBuilder sb = new StringBuilder();
		if (value instanceof Integer) {
			sb.append(value.toString());
		} else if (value instanceof Float) {
			sb.append(value.toString());
			sb.append('f');
		} else if (value instanceof Double) {
			sb.append(value.toString());
			if (sb.indexOf(".") == -1) {
				sb.append(".0");
			}
		} else if (value instanceof Long) {
			sb.append(value.toString());
			sb.append('L');
		} else if (value instanceof Character) {
			sb.append('\'');
			sb.append(JavaEncoding.encode(fixedValue.toString()));
			sb.append('\'');
		} else if (value == null) {
			sb.append("null");
		} else {
			sb.append('\"');
			sb.append(JavaEncoding.encode(value.toString()));
			sb.append('\"');
		}
		return sb.toString();
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeInt(0);
		out.writeObject(getAttribute());
		out.writeObject(getValue());
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		int version = in.readInt();
		if (version == 0) {
			attributeName = (String) in.readObject();
			fixedValue = (DataType) in.readObject();
		} else {
			throw new IOException("Unsupported internal version: " + version);
		}
	}

	@Override
	protected final boolean evaluateTestAtoms(Map<String, TestAtom> values) {
		TestAtom atom = values.get(getAttribute());
		if (atom == null)
			throw new IllegalArgumentException("Missing TestAtom for \""
					+ getAttribute() + "\"");
		return evaluateTestAtom(atom);
	}

	protected abstract boolean evaluateTestAtom(TestAtom atom);

	@Override
	protected int getCanonicalOrder() {
		return 3;
	}
}