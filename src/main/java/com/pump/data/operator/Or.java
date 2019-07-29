package com.pump.data.operator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Or extends AbstractCompoundOperator {
	private static final long serialVersionUID = 1L;

	public static Operator create(Operator... operands) {
		return create(Arrays.asList(operands));
	}

	public static Operator create(List<Operator> operands) {
		validateOperands(operands, "Or");

		List<Operator> newOperands = new ArrayList<>(operands.size());
		for (Operator op : operands) {
			if (TRUE.equals(op)) {
				return TRUE;
			} else if (FALSE.equals(op)) {
				// skip this operator
			} else if (!newOperands.contains(op)) {
				newOperands.add(op);
			}
		}

		if (newOperands.size() == 0) {
			newOperands.add(FALSE);
		}
		if (newOperands.size() == 1)
			return newOperands.get(0);
		return new Or(newOperands);
	}

	public Or(List<Operator> operands) {
		super(operands, "Or");
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
	protected Operator createCanonicalOperator() {
		List orTerms = new ArrayList(getOperandCount());
		for (int a = 0; a < getOperandCount(); a++) {
			Operator op = getOperand(a).getCanonicalOperator();
			if (op instanceof Or) {
				Or or = (Or) op;
				orTerms.addAll(or.getOperands());
			} else {
				orTerms.add(op);
			}
		}

		Collections.sort(orTerms, toStringComparator);
		return Or.create(orTerms);
	}

	@Override
	public Collection<Operator> split() {
		Collection<Operator> returnValue = new ArrayList<>(getOperandCount());
		for (int a = 0; a < getOperandCount(); a++) {
			returnValue.addAll(getOperand(a).split());
		}
		return returnValue;
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