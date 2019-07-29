package com.pump.data.operator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.pump.util.CombinationIterator;

public class And extends AbstractCompoundOperator {
	private static final long serialVersionUID = 1L;

	public static Operator create(Operator... operands) {
		return create(Arrays.asList(operands));
	}

	public static Operator create(List<Operator> operands) {
		validateOperands(operands, "And");

		List<Operator> newOperands = new ArrayList<>(operands.size());
		for (Operator op : operands) {
			if (TRUE.equals(op)) {
				// skip this operator
			} else if (FALSE.equals(op)) {
				return FALSE;
			} else if (!newOperands.contains(op)) {
				newOperands.add(op);
			}
		}

		if (newOperands.size() == 0) {
			newOperands.add(TRUE);
		}
		if (newOperands.size() == 1)
			return newOperands.get(0);
		return new And(newOperands);
	}

	public And(List<Operator> operands) {
		super(operands, "And");
	}

	@Override
	public boolean evaluate(OperatorContext context, Object bean)
			throws Exception {
		for (int a = 0; a < getOperandCount(); a++) {
			if (getOperand(a).evaluate(context, bean) == false)
				return false;
		}
		return true;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Operator createCanonicalOperator() {
		List andTerms = new ArrayList();
		List<Operator[]> orGroups = new ArrayList();
		for (int a = 0; a < getOperandCount(); a++) {
			Operator op = getOperand(a).getCanonicalOperator();
			if (op instanceof And) {
				And and = (And) op;
				andTerms.addAll(and.getOperands());
			} else if (op instanceof Or) {
				Or or = (Or) op;
				Collection z = or.getOperands();
				Operator[] y = new Operator[z.size()];
				z.toArray(y);
				orGroups.add(y);
			} else {
				andTerms.add(op);
			}
		}
		if (orGroups.isEmpty()) {
			Collections.sort(andTerms, toStringComparator);
			return And.create(andTerms);
		}

		for (Object andTerm : andTerms) {
			orGroups.add(new Operator[] { (Operator) andTerm });
		}

		List newOrTerms = new ArrayList();
		CombinationIterator<Operator> iter = new CombinationIterator(orGroups);
		while (iter.hasNext()) {
			List<Operator> newAndTerms = iter.next();
			Collections.sort(newAndTerms, toStringComparator);
			newOrTerms.add(And.create(newAndTerms).getCanonicalOperator());
		}

		return Or.create(newOrTerms).getCanonicalOperator();
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Collection<Operator> split() {
		List<Operator[]> operandVariations = new ArrayList<>(getOperandCount());
		int z = 1;
		for (int a = 0; a < getOperandCount(); a++) {
			Collection<Operator> e = getOperand(a).split();
			Operator[] e2 = e.toArray(new Operator[e.size()]);
			operandVariations.add(e2);
			z *= e2.length;
		}

		CombinationIterator<Operator> iter = new CombinationIterator(
				operandVariations);
		List<Operator> returnValue = new ArrayList<>(z);
		while (iter.hasNext()) {
			List<Operator> w = iter.next();
			returnValue.add(And.create(w));
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