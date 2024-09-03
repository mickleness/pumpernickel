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

import com.pump.io.parser.java.JavaEncoding;

/**
 * This evaluates an attribute from a bean / data source.
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

	/**
	 * Return the attribute name, which is the first operand.
	 */
	public String getAttribute() {
		return (String) getOperand(0);
	}

	/**
	 * Return the value the attribute must match, which is the second operand.
	 */
	@SuppressWarnings("unchecked")
	public DataType getValue() {
		return (DataType) getOperand(1);
	}

	@Override
	public Object getOperand(int index) {
		if (index == 0)
			return attributeName;
		if (index == 1)
			return fixedValue;
		throw new IllegalArgumentException("illegal operand index: " + index);
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Map<String, Collection<Class>> getAttributeTypes() {
		Map<String, Collection<Class>> returnValue = new HashMap<>(1);
		Collection<Class> c = new HashSet<>();
		Object value = getValue();
		if (value == null) {
			c.add(null);
		} else {
			c.add(value.getClass());
		}
		returnValue.put(getAttribute(), c);
		return returnValue;
	}

	/**
	 * Convert a value to a String.
	 * <p>
	 * This helps format objects in a Java-friendly format. For example the
	 * float 1 will be converted to "1f". The double 4 will be "4.0". Strings
	 * and characters will be encoded using Java's escape character conventions.
	 * <p>
	 * This formatting is important because the {@link OperationParser} uses a
	 * parser designed to pick up on these differences.
	 */
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

	@SuppressWarnings("unchecked")
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

	/**
	 * Evaluate the one TestAtom associated with {@link #getAttribute()}.
	 */
	protected abstract boolean evaluateTestAtom(TestAtom atom);

	@Override
	protected int getCanonicalOrder() {
		return 3;
	}
}