package com.pump.data.operator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import com.pump.util.CombinationIterator;

public class And extends AbstractCompoundOperator {
	private static final long serialVersionUID = 1L;

	public And(Operator... operands) {
		this(Arrays.asList(operands));
	}

	public And(Collection<Operator> operands) {
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
		Collection<Operator> andTerms = new LinkedHashSet<>();
		List<Operator[]> orGroups = new ArrayList();
		for (int a = 0; a < getOperandCount(); a++) {
			Operator op = getOperand(a).getCanonicalOperator();
			if (Operator.FALSE.equals(op, true)) {
				return Operator.FALSE;
			} else if (Operator.TRUE.equals(op, true)) {
				// skip this term
			} else if (op instanceof And) {
				And and = (And) op;
				for (int b = 0; b < and.getOperandCount(); b++) {
					Operator innerOp = and.getOperand(b);
					if (Operator.FALSE.equals(innerOp, true)) {
						return Operator.FALSE;
					} else if (Operator.TRUE.equals(innerOp, true)) {
						// skip this term
					} else {
						andTerms.add(innerOp);
					}
				}
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
			if (andTerms.isEmpty())
				return Operator.TRUE;
			if (andTerms.size() == 1)
				return (Operator) andTerms.iterator().next();

			Operator[] array = new Operator[andTerms.size()];
			andTerms.toArray(array);
			Arrays.sort(array, toStringComparator);
			return new And(array);
		}

		for (Operator andTerm : andTerms) {
			orGroups.add(new Operator[] { andTerm });
		}

		Collection<Operator> newOrTerms = new HashSet<>();
		CombinationIterator<Operator> iter = new CombinationIterator(orGroups);
		while (iter.hasNext()) {
			List<Operator> newAndTerms = iter.next();
			Collections.sort(newAndTerms, toStringComparator);
			newOrTerms.add(new And(newAndTerms).getCanonicalOperator());
		}

		if (newOrTerms.size() == 1)
			return newOrTerms.iterator().next().getCanonicalOperator();

		return new Or(newOrTerms).getCanonicalOperator();
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

	@Override
	protected boolean evaluateTestAtoms(Map<String, TestAtom> values) {
		for (int a = 0; a < getOperandCount(); a++) {
			Operator op = getOperand(a);
			boolean b = op.evaluateTestAtoms(values);
			if (!b)
				return false;
		}
		return true;
	}
}