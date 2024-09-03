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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

/**
 * This accepts an arbitrary number of operands which are all Operators. This is
 * intended for AND and OR operators.
 */
public abstract class AbstractCompoundOperator extends Operator {
	private static final long serialVersionUID = 1L;

	/**
	 * Throw an exception if any operand is null, or if the list of operands is
	 * less than 2.
	 * 
	 * @param operands
	 *            t he operands to evaluate
	 * @param operationName
	 *            this is used when formatting the exception message.
	 */
	protected static void validateOperands(Collection<Operator> operands,
			String operationName) {
		Objects.requireNonNull(operands);
		Iterator<Operator> iter = operands.iterator();
		int index = 0;
		while (iter.hasNext()) {
			Operator op = iter.next();
			if (op == null)
				throw new NullPointerException("index = " + index);
			index++;
		}
		if (operands.size() == 0)
			throw new IllegalArgumentException();
		if (operands.size() == 1)
			throw new IllegalArgumentException(
					"2 or more operands are required for a " + operationName
							+ " operator");
	}

	private List<Operator> operands;

	/**
	 * Create a new AbstractCompoundOperator.
	 * 
	 * @param operands
	 *            the operators that act as operands.
	 */
	protected AbstractCompoundOperator(Collection<Operator> operands,
			String name) {
		validateOperands(operands, name);
		this.operands = Collections.unmodifiableList(new ArrayList<Operator>(
				operands));
	}

	@Override
	public int getOperandCount() {
		return operands.size();
	}

	@Override
	public Operator getOperand(int index) {
		return operands.get(index);
	}

	@Override
	protected String toString(boolean negated) {
		return toString(false, negated);
	}

	private String toString(boolean encloseWithParentheses, boolean negated) {
		StringBuilder sb = new StringBuilder();

		boolean negateElements = false;
		if (getOperandCount() > 1) {
			if (negated) {
				encloseWithParentheses = true;
				sb.append("!");
			}
		} else {
			// we don't lead with the "!", but we ask our only child to show
			// negation
			if (negated)
				negateElements = true;
		}

		if (encloseWithParentheses)
			sb.append("(");

		String opStr;
		if (this instanceof And) {
			opStr = " && ";
		} else if (this instanceof Or) {
			opStr = " || ";
		} else {
			throw new RuntimeException("Unsupported operator: "
					+ getClass().getName());
		}

		for (int a = 0; a < getOperandCount(); a++) {
			if (a != 0)
				sb.append(opStr);
			Operator op = getOperand(a);
			String z;
			if (op instanceof AbstractCompoundOperator) {
				AbstractCompoundOperator compoundOp = (AbstractCompoundOperator) op;
				z = compoundOp.toString(true, negateElements);
			} else {
				z = op.toString(negateElements);
			}
			sb.append(z);
		}
		if (encloseWithParentheses)
			sb.append(")");
		return sb.toString();
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Map<String, Collection<Class>> getAttributeTypes() {
		Map<String, Collection<Class>> returnValue = new HashMap<>();
		for (int a = 0; a < getOperandCount(); a++) {
			Operator op = getOperand(a);
			addAttributeTypes(returnValue, op.getAttributeTypes());
		}
		return returnValue;
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Map<String, Collection<TestAtom>> createTestAtoms(
			Map<String, Collection<Class>> attributeTypes) {
		Map<String, Collection<TestAtom>> map = new HashMap<>();
		for (int a = 0; a < getOperandCount(); a++) {
			Operator op = getOperand(a);
			for (Entry<String, Collection<TestAtom>> entry : op
					.createTestAtoms(attributeTypes).entrySet()) {
				Collection<TestAtom> c = map.get(entry.getKey());
				if (c == null) {
					c = new HashSet<>();
					map.put(entry.getKey(), c);
				}
				c.addAll(entry.getValue());
			}
		}
		return map;
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeInt(0);
		out.writeObject(operands);
	}

	@SuppressWarnings("unchecked")
	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		int version = in.readInt();
		if (version == 0) {
			operands = (List<Operator>) in.readObject();
		} else {
			throw new IOException("Unsupported internal version: " + version);
		}
	}

	/**
	 * Return all the operands that refer to the given attribute.
	 */
	public List<Operator> getOperandsFor(String attr) {
		List<Operator> returnValue = new ArrayList<>();
		for (int a = 0; a < getOperandCount(); a++) {
			Operator op = getOperand(a);
			if (op.getAttributes().contains(attr))
				returnValue.add(op);
		}
		return returnValue;
	}
}