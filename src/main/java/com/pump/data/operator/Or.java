package com.pump.data.operator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;

public class Or extends AbstractCompoundOperator {
	private static final long serialVersionUID = 1L;

	public Or(Collection<Operator> operands) {
		super(operands, "Or");
	}

	public Or(Operator... operands) {
		this(Arrays.asList(operands));
	}

	@Override
	public boolean evaluate(OperatorContext context, Object bean)
			throws Exception {
		for (int a = 0; a < getOperandCount(); a++) {
			if (getOperand(a).evaluate(context, bean))
				return true;
		}
		return false;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected Operator createSumOfProducts() {
		Collection<Operator> orTerms = new LinkedHashSet(getOperandCount());
		for (int a = 0; a < getOperandCount(); a++) {
			Operator op = getOperand(a).getSumOfProducts();
			if (Operator.TRUE.equals(op, true)) {
				return Operator.TRUE;
			} else if (Operator.FALSE.equals(op, true)) {
				// skip this term
			} else if (op instanceof Or) {
				Or or = (Or) op;
				for (int b = 0; b < or.getOperandCount(); b++) {
					Operator innerOp = or.getOperand(b);
					if (Operator.TRUE.equals(innerOp)) {
						return Operator.TRUE;
					} else if (Operator.FALSE.equals(innerOp)) {
						// skip this term
					} else {
						orTerms.add(innerOp);
					}
				}
			} else {
				orTerms.add(op);
			}
		}

		if (orTerms.size() == 0) {
			orTerms.add(FALSE);
		}
		if (orTerms.size() == 1)
			return orTerms.iterator().next();

		Operator[] array = new Operator[orTerms.size()];
		orTerms.toArray(array);
		Arrays.sort(array, toStringComparator);
		return new Or(array);
	}

	@Override
	public Collection<Operator> split() {
		Collection<Operator> returnValue = new ArrayList<>(getOperandCount());
		for (int a = 0; a < getOperandCount(); a++) {
			returnValue.addAll(getOperand(a).split());
		}
		return returnValue;
	}

	@Override
	protected boolean evaluateTestAtoms(Map<String, TestAtom> values) {
		for (int a = 0; a < getOperandCount(); a++) {
			Operator op = getOperand(a);
			boolean b = op.evaluateTestAtoms(values);
			if (b)
				return true;
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

		} else {
			throw new IOException("Unsupported internal version: " + version);
		}
	}
}