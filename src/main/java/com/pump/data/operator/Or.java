/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://raw.githubusercontent.com/mickleness/pumpernickel/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.data.operator;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This ORs a series of other operators together.
 */
public class Or extends AbstractCompoundOperator {
	private static final long serialVersionUID = 1L;

	/**
	 * Create an Or operator.
	 * 
	 * @param operands
	 *            the operands (which are all Operators) this Or object
	 *            consults.
	 */
	public Or(Collection<Operator> operands) {
		super(operands, "Or");
	}

	/**
	 * Create an Or operator.
	 * 
	 * @param operands
	 *            the operands (which are all Operators) this Or object
	 *            consults.
	 */
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
	protected Operator createCanonicalOperator() {
		Collection<Operator> orTerms = new LinkedHashSet(getOperandCount());
		for (int a = 0; a < getOperandCount(); a++) {
			Operator op = getOperand(a).getCanonicalOperator();
			if (Operator.TRUE.equals(op, true)) {
				return Operator.TRUE;
			} else if (Operator.FALSE.equals(op, true)) {
				// skip this term
			} else if (op instanceof Or) {
				Or or = (Or) op;
				for (int b = 0; b < or.getOperandCount(); b++) {
					Operator innerOp = or.getOperand(b);
					if (Operator.TRUE.equals(innerOp, true)) {
						return Operator.TRUE;
					} else if (Operator.FALSE.equals(innerOp, true)) {
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
	protected TestAtom.AtomEvaluation evaluateTestAtoms(
			Map<String, TestAtom> values) {
		boolean foundTrue = false;
		for (int a = 0; a < getOperandCount(); a++) {
			Operator op = getOperand(a);
			TestAtom.AtomEvaluation b = op.evaluateTestAtoms(values);
			if (b == TestAtom.AtomEvaluation.UNKNOWN)
				return b;
			if (b == TestAtom.AtomEvaluation.TRUE)
				foundTrue = true;
		}
		if (foundTrue)
			return TestAtom.AtomEvaluation.TRUE;
		return TestAtom.AtomEvaluation.FALSE;
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeInt(0);
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		int version = in.readInt();
		if (version == 0) {
			// the AbstractCompoundOperator class writes all our data
		} else {
			throw new IOException("Unsupported internal version: " + version);
		}
	}

	@Override
	protected int getCanonicalOrder() {
		return 0;
	}

	@Override
	protected Operator createTemplateOperator() {
		List<Operator> orTerms = new LinkedList<>();
		// simplify a little, so if this term is "a==1 || a==2" we won't
		// return "a==? || a==?", we'll just return "a==?"
		for (int a = 0; a < getOperandCount(); a++) {
			Operator op = getOperand(a);
			op = op.getTemplateOperator();
			if (!orTerms.contains(op))
				orTerms.add(op);
		}
		if (orTerms.size() == 1)
			return orTerms.get(0);
		return new Or(orTerms);
	}
}