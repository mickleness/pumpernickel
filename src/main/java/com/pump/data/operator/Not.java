package com.pump.data.operator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Not extends Operator {
	private static final long serialVersionUID = 1L;

	public static Operator create(Operator op) {
		if (op == TRUE)
			return FALSE;
		if (op == FALSE)
			return TRUE;
		if (op instanceof Not) {
			return ((Not) op).getOperand(0);
		}
		return new Not(op);
	}

	Operator operand;

	Not(Operator operand) {
		Objects.requireNonNull(operand);
		this.operand = operand;
	}

	@Override
	public boolean evaluate(OperatorContext context, Object bean)
			throws Exception {
		return !getOperand(0).evaluate(context, bean);
	}

	@Override
	public int getOperandCount() {
		return 1;
	}

	@Override
	protected String toString(boolean negated) {
		return operand.toString(!negated);
	}

	@Override
	public Operator getOperand(int index) {
		if (index == 0)
			return operand;
		throw new IllegalArgumentException("index = " + index);
	}

	@Override
	public Collection<String> getAttributes() {
		return getOperand(0).getAttributes();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected Operator createCanonicalOperator() {
		Operator op = getOperand(0).getCanonicalOperator();

		if (op instanceof AbstractCompoundOperator) {
			AbstractCompoundOperator compoundOp = (AbstractCompoundOperator) op;
			List<Operator> negatedTerms = new ArrayList<>(
					compoundOp.getOperandCount());
			List operands = (List) compoundOp.getOperands();
			for (Operator member : (List<Operator>) operands) {
				negatedTerms.add(Not.create(member).getCanonicalOperator());
			}

			if (op instanceof And) {
				op = Or.create(negatedTerms);
			} else if (op instanceof Or) {
				op = And.create(negatedTerms);
			} else {
				throw new IllegalStateException("Unsupported operator "
						+ op.toString() + " (" + op.getClass().getName() + ")");
			}
		} else {
			op = Not.create(op);
		}

		if (op instanceof AbstractCompoundOperator)
			op = op.getCanonicalOperator();
		return op;
	}

	@Override
	public Collection<Operator> split() {
		if (getOperand(0) instanceof AbstractCompoundOperator) {
			AbstractCompoundOperator aco = (AbstractCompoundOperator) getOperand(0);
			List<Operator> returnValue = new ArrayList<>();
			for (int a = 0; a < aco.getOperandCount(); a++) {
				Collection<Operator> c = aco.getOperand(a).split();
				for (Operator op : c) {
					returnValue.add(Not.create(op));
				}
			}
			if (getOperand(0) instanceof And)
				return returnValue;
			if (getOperand(0) instanceof Or)
				return Collections.singleton(And.create(returnValue));

			throw new IllegalStateException("Unsupported operator "
					+ operand.toString() + " (" + operand.getClass().getName()
					+ ")");
		}

		Collection<Operator> z = getOperand(0).split();

		Collection<Operator> returnValue = new ArrayList<Operator>(z.size());
		for (Operator op : z) {
			returnValue.add(Not.create(op));
		}

		return returnValue;
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeInt(0);
		out.writeObject(getOperand(0));
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		int version = in.readInt();
		if (version == 0) {
			operand = (Operator) in.readObject();
		} else {
			throw new IOException("Unsupported internal version: " + version);
		}
	}
}