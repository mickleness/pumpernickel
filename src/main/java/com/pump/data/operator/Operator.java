package com.pump.data.operator;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

import com.pump.util.CombinationIterator;

/**
 * All Operators are immutable.
 * 
 * TODO: Implement a validation model. For example: if I set up an Operator that
 * compares "gradeLevel" against the integer 3 and during runtime we observe
 * that the actual attribute is a String, then we should throw a
 * RuntimeException pointing out the discrepancy. (Instead of simply saying,
 * 'the grade level of the String "3" is not equal to the integer 3, so this
 * evaluates to false.
 */
public abstract class Operator implements Serializable {
	private static final long serialVersionUID = 1L;

	static Comparator<Operator> toStringComparator = new Comparator<Operator>() {

		@Override
		public int compare(Operator o1, Operator o2) {
			return o1.toString().compareTo(o2.toString());
		}

	};

	public static final Operator TRUE = new ConstantOperator(true);
	public static final Operator FALSE = new ConstantOperator(false);

	public abstract int getOperandCount();

	public abstract Object getOperand(int index);

	public abstract boolean evaluate(OperatorContext context, Object bean)
			throws Exception;

	protected abstract String toString(boolean negated);

	@Override
	public String toString() {
		return toString(false);
	}

	@Override
	public int hashCode() {
		return getTestAtoms().hashCode();
	}

	/**
	 * Return true if the argument is Operator that is functionally equivalent
	 * to this operator.
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Operator))
			return false;
		return equals((Operator) obj, false);
	}

	/**
	 * Evaluate whether two Operators are equal.
	 * 
	 * @param operator
	 *            the operator to compare this object against.
	 * @param strictEquivalency
	 *            if true the the Operator argument must be exactly like this
	 *            Operator in its order of operations. (Another way to think of
	 *            this is: the toString() method of each operator should return
	 *            exactly the same thing.) If false then two operators are
	 *            considered equal if they are functionally the same.
	 *            <p>
	 *            For example "a || b" and "b || a" are functionally equal, but
	 *            are not equivalent from an order-of-operations point of view.
	 * 
	 * @return
	 */
	public boolean equals(Operator operator, boolean strictEquivalency) {
		Objects.requireNonNull(operator);
		if (strictEquivalency) {
			if (!operator.getClass().equals(getClass()))
				return false;
			int c1 = getOperandCount();
			int c2 = operator.getOperandCount();
			if (c1 != c2)
				return false;
			for (int a = 0; a < c1; a++) {
				Object operand1 = getOperand(a);
				Object operand2 = operator.getOperand(a);
				if (operand1 instanceof Operator
						&& operand2 instanceof Operator) {
					Operator z1 = (Operator) operand1;
					Operator z2 = (Operator) operand2;
					if (!z1.equals(z2, true))
						return false;
				} else {
					if (!Objects.equals(operand1, operand2))
						return false;
				}
			}
			return true;
		} else {
			Map<String, Collection<TestAtom>> testAtoms = new HashMap<>();
			for (Entry<String, Collection<TestAtom>> entry : operator
					.getTestAtoms().entrySet()) {
				Collection<TestAtom> c = new HashSet<>();
				testAtoms.put(entry.getKey(), c);
				c.addAll(entry.getValue());
			}
			for (Entry<String, Collection<TestAtom>> entry : getTestAtoms()
					.entrySet()) {
				Collection<TestAtom> c = testAtoms.get(entry.getKey());
				if (c == null) {
					c = new HashSet<>();
					testAtoms.put(entry.getKey(), c);
				}
				c.addAll(entry.getValue());
			}

			List<String> keyList = new ArrayList<>(testAtoms.size());
			List<TestAtom[]> testAtomList = new ArrayList<>(testAtoms.size());
			for (Entry<String, Collection<TestAtom>> entry : testAtoms
					.entrySet()) {
				keyList.add(entry.getKey());
				testAtomList.add(entry.getValue().toArray(
						new TestAtom[entry.getValue().size()]));
			}

			Iterator<List<TestAtom>> iter = new CombinationIterator<>(
					testAtomList);
			Map<String, TestAtom> values = new HashMap<>();
			while (iter.hasNext()) {
				List<TestAtom> l = iter.next();
				values.clear();
				for (int a = 0; a < keyList.size(); a++) {
					values.put(keyList.get(a), l.get(a));
				}
				boolean myValue = evaluateTestAtoms(values);
				boolean otherValue = operator.evaluateTestAtoms(values);
				if (myValue != otherValue)
					return false;
			}
			return true;
		}
	}

	private transient Map<String, Collection<TestAtom>> testAtoms;

	protected Map<String, Collection<TestAtom>> getTestAtoms() {
		if (testAtoms == null) {
			testAtoms = createTestAtoms();
		}
		return testAtoms;
	}

	private transient Operator canonicalOperator = null;

	private transient Boolean isCanonical = null;

	public boolean isCanonical() {
		if (isCanonical == null) {
			isCanonical = calculateIsCanonical();
		}
		return isCanonical;
	}

	private boolean calculateIsCanonical() {
		int myOrder = getCanonicalOrder();
		Operator lastOperator = null;
		for (int a = 0; a < getOperandCount(); a++) {
			Object operand = getOperand(a);
			if (operand instanceof Operator) {
				Operator z = (Operator) operand;
				int zOrder = z.getCanonicalOrder();
				if (zOrder <= myOrder) {
					return false;
				}
				if (!z.isCanonical())
					return false;

				if (lastOperator != null
						&& toStringComparator.compare(lastOperator, z) > 0)
					return false;
				lastOperator = z;
			}
		}
		return true;
	}

	protected abstract int getCanonicalOrder();

	public Operator getCanonicalOperator() {
		if (canonicalOperator == null) {
			Operator op = createCanonicalOperator();
			op = CanonicalSimplifier.simplify(op);

			// just to avoid any extra calculations:
			op.canonicalOperator = op.canonicalOperator;

			canonicalOperator = op;
		}
		return canonicalOperator;
	}

	protected abstract Operator createCanonicalOperator();

	protected abstract Map<String, Collection<TestAtom>> createTestAtoms();

	protected abstract boolean evaluateTestAtoms(Map<String, TestAtom> values);

	protected static class TestAtom {
		enum Type {
			EQUAL_TO("="), LESSER_THAN("<"), GREATER_THAN(">"), LIKE("~");

			String shorthand;

			Type(String shorthand) {
				this.shorthand = shorthand;
			}

			@Override
			public String toString() {
				return shorthand;
			}
		};

		Object type;
		Object value;

		public TestAtom(Object type, Object value) {
			Objects.requireNonNull(type);
			this.type = type;
			this.value = value;
		}

		@Override
		public int hashCode() {
			int i = type.hashCode();
			if (value != null)
				i += value.hashCode();
			return i;

		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof TestAtom))
				return false;
			TestAtom other = (TestAtom) obj;
			return Objects.equals(type, other.type)
					&& Objects.equals(value, other.value);
		}

		@Override
		public String toString() {
			return type.toString() + " " + value;
		}

		public Object getType() {
			return type;
		}

		public Object getValue() {
			return value;
		}

	}

	public List<Object> getOperands() {
		List<Object> returnValue = new ArrayList<>(getOperandCount());
		for (int a = 0; a < getOperandCount(); a++) {
			returnValue.add(getOperand(a));
		}
		return Collections.unmodifiableList(returnValue);
	}

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.writeInt(0);
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException {
		int version = in.readInt();
		if (version == 0) {
			// subclasses write data; we don't write data here
		} else {
			throw new IOException("Unsupported internal version: " + version);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final Collection<Operator> split() {
		Operator c = getCanonicalOperator();
		if (c instanceof Or) {
			return new ArrayList<Operator>((List) c.getOperands());
		}
		return Collections.singleton(c);
	}

	public abstract Collection<String> getAttributes();

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Operator join(Operator... operators) {
		Operator op = operators.length == 1 ? operators[0] : new Or(operators);
		op = op.getCanonicalOperator();
		if (!(op instanceof Or))
			return op;
		Or or = (Or) op;

		// let's guarantee consistency regarding the order in which we try
		// to factor out terms by sorting our attributes:
		SortedSet<String> sortedAttributes = new TreeSet<>();
		sortedAttributes.addAll(or.getAttributes());

		// step 1: convert plain EqualTo statements to In statements:

		for (String attr : sortedAttributes) {
			Collection<EqualTo> plainEquals = new HashSet<>();

			for (int a = 0; a < or.getOperandCount(); a++) {
				Operator orOp = or.getOperand(a);
				if (orOp instanceof EqualTo
						&& attr.equals(((EqualTo) orOp).getAttribute())) {
					plainEquals.add((EqualTo) orOp);
				}
			}

			if (plainEquals.size() > 1) {
				List<Operator> newOperands = new ArrayList<>(
						(List) or.getOperands());
				Collection<Object> values = getValues(plainEquals);
				newOperands.removeAll(plainEquals);
				newOperands.add(In.create(attr, values));
				op = newOperands.size() == 1 ? (Operator) newOperands.get(0)
						: new Or(newOperands);
				if (op instanceof Or) {
					or = (Or) op;
				} else {
					return op;
				}
			}
		}

		// step 2: convert plain negated EqualTo statements to In statements:

		for (String attr : sortedAttributes) {
			Collection<EqualTo> notPlainEquals = new HashSet<>();

			for (int a = 0; a < or.getOperandCount(); a++) {
				Operator orOp = or.getOperand(a);
				if (orOp instanceof Not) {
					Not not = (Not) orOp;
					Operator notOp = not.getOperand(0);
					if (notOp instanceof EqualTo
							&& attr.equals(((EqualTo) notOp).getAttribute())) {
						notPlainEquals.add((EqualTo) notOp);
					}
				}
			}

			if (notPlainEquals.size() > 1) {
				List<Operator> newOperands = new ArrayList<>(
						(List) or.getOperands());
				Collection<Object> values = getValues(notPlainEquals);
				for (EqualTo et : notPlainEquals) {
					newOperands.remove(new Not(et));
				}
				newOperands.add(new Not(In.create(attr, values)));
				op = newOperands.size() == 1 ? (Operator) newOperands.get(0)
						: new Or(newOperands);
				if (op instanceof Or) {
					or = (Or) op;
				} else {
					return op;
				}
			}
		}

		// step 3: reach inside ANDed statements and search for equal-tos and
		// not-equal-tos to similarly rewrite as IN statements. This is much
		// more complex and involves trying to find common factors for
		// clusters of operands.

		boolean continueScanning;
		do {
			continueScanning = false;

			scanAttributes: for (String attr : sortedAttributes) {

				Map<List<Operator>, Map<EqualTo, And>> andEqualTos = new HashMap<>();
				Map<List<Operator>, Map<EqualTo, And>> andNotEqualTos = new HashMap<>();

				List<Operator> biggestKey = null;
				int biggestKeySize = -1;
				boolean biggestKeyEqualTo = false;

				for (int a = 0; a < or.getOperandCount(); a++) {
					Operator orOp = or.getOperand(a);
					if (orOp instanceof And) {
						And and = (And) orOp;
						for (int b = 0; b < and.getOperandCount(); b++) {
							Operator andOp = and.getOperand(b);
							if (andOp instanceof EqualTo
									&& attr.equals(((EqualTo) andOp)
											.getAttribute())) {
								List<Operator> andOperandsWithoutEqualTo = new ArrayList<>();
								andOperandsWithoutEqualTo
										.addAll((Collection) and.getOperands());
								andOperandsWithoutEqualTo.remove(andOp);

								Map<EqualTo, And> map = andEqualTos
										.get(andOperandsWithoutEqualTo);
								if (map == null) {
									map = new HashMap<>();
									andEqualTos.put(andOperandsWithoutEqualTo,
											map);
								}

								map.put((EqualTo) andOp, and);

								if (map.size() > biggestKeySize) {
									biggestKeySize = map.size();
									biggestKey = andOperandsWithoutEqualTo;
									biggestKeyEqualTo = true;
								}
							} else if (andOp instanceof Not) {
								Not not = (Not) andOp;
								Operator notOp = not.getOperand(0);
								if (notOp instanceof EqualTo
										&& attr.equals(((EqualTo) notOp)
												.getAttribute())) {

									List<Operator> andOperandsWithoutNotEqualTo = new ArrayList<>();
									andOperandsWithoutNotEqualTo
											.addAll((Collection) and
													.getOperands());
									andOperandsWithoutNotEqualTo.remove(andOp);

									Map<EqualTo, And> map = andNotEqualTos
											.get(andOperandsWithoutNotEqualTo);
									if (map == null) {
										map = new HashMap<>();
										andNotEqualTos.put(
												andOperandsWithoutNotEqualTo,
												map);
									}

									map.put((EqualTo) notOp, and);

									if (map.size() > biggestKeySize) {
										biggestKeySize = map.size();
										biggestKey = andOperandsWithoutNotEqualTo;
										biggestKeyEqualTo = false;
									}
								}
							}
						}
					}
				}

				if (biggestKeySize > 1) {
					List<Operator> newOrTerms = new ArrayList<>();
					if (biggestKeyEqualTo) {
						Map<EqualTo, And> equalTos = andEqualTos
								.get(biggestKey);
						Collection<Object> values = getValues(equalTos.keySet());
						List<Operator> newAndTerms = new ArrayList<Operator>();
						newAndTerms.addAll(biggestKey);
						newAndTerms.add(In.create(attr, values));

						Operator newAndOp = new And(newAndTerms);

						newOrTerms.addAll((List) or.getOperands());
						newOrTerms.removeAll(equalTos.values());
						newOrTerms.add(newAndOp);
					} else {
						Map<EqualTo, And> notEqualTos = andNotEqualTos
								.get(biggestKey);
						Collection<Object> values = getValues(notEqualTos
								.keySet());
						List<Operator> newAndTerms = new ArrayList<Operator>();
						newAndTerms.addAll(biggestKey);
						newAndTerms.add(new Not(In.create(attr, values)));

						Operator newAndOp = new And(newAndTerms);

						newOrTerms.addAll((List) or.getOperands());
						newOrTerms.removeAll(notEqualTos.values());
						newOrTerms.add(newAndOp);
					}

					op = newOrTerms.size() == 1 ? (Operator) newOrTerms.get(0)
							: new Or(newOrTerms);
					if (op instanceof Or) {
						or = (Or) op;
						continueScanning = true;
						break scanAttributes;
					} else {
						return op;
					}
				}

			}
		} while (continueScanning);

		return op;
	}

	private static Collection<Object> getValues(
			Collection<EqualTo> valueOperators) {
		Collection<Object> returnValue = new HashSet<>();
		for (EqualTo op : valueOperators) {
			returnValue.add(op.getValue());
		}
		return returnValue;
	}
}
