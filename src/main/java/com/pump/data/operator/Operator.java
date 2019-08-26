package com.pump.data.operator;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
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
 * An Operator is something that can be evaluated as true or false.
 * <p>
 * Nearly all Operators are composed of operands. Sometimes these operands are
 * other Operators. For example: the {@link And}, {@link Or} and {@link Not}
 * operators are all composed of other simpler operators like {@link EqualTo},
 * {@link GreaterThan}, {@link LesserThan}, {@link In} and {@link Like}.
 * <p>
 * (The {@link Operator#TRUE} and {@link Operator#FALSE} are special operators
 * that are constant and do not require any input, so they have zero operands.)
 * <p>
 * The core function of an Operator is its
 * {@link #evaluate(OperatorContext, Object)} method.
 * <p>
 * For example: suppose set up a hundred beans that represent different people.
 * You can set up Operators to identify whether a person bean is an adult, or
 * whether their last name starts with "S", etc. Then you can call
 * {@link #evaluate(OperatorContext, Object)} on each of the hundred beans to
 * identify which person beans match your requirements.
 * <p>
 * You can compose any layered Operators however you want to. But each Operator
 * can also be converted to a standardized canonical form to help certain
 * comparison calculations. In this architecture a "canonical" operator is one
 * that meets the following criteria:
 * <ul>
 * <li>A tree representation of an Operator must present nodes in this order:
 * Or, And, Not, other. (This is a slightly specialized "sum of products"
 * expression.)</li>
 * <li>Within Or and And operators: operands must be sorted in alphabetical
 * order.</li>
 * </ul>
 * <p>
 * Any Operator that meets those criteria will return true when you consult
 * {@link #isCanonical()}, but the method {@link #getCanonicalOperator()} goes
 * one step further and also applies a few optional boolean algebra approaches
 * to try to simplify an Operation.
 * <p>
 * All Operators are immutable.
 * <p>
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
		return equals((Operator) obj, true);
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
			EXACTLY("="), BARELY_SMALLER_THAN("<"), BARELY_BIGGER_THAN(">"), LIKE(
					"~");

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

	private static Comparator NULL_SAFE_COMPARATOR = new Comparator() {

		@Override
		public int compare(Object o1, Object o2) {
			if (o1 == null && o2 == null)
				return 0;
			if (o1 == null)
				return -1;
			if (o2 == null)
				return 1;
			return ((Comparable) o1).compareTo(o2);
		}

	};

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Operator join(Operator... operators) {
		for (int a = 0; a < operators.length; a++) {
			operators[a] = operators[a].getCanonicalOperator();
		}

		// step 1: identify plain equal-tos, and consolidate them

		Map<String, SortedSet<Object>> equalTos = new HashMap<>();
		List<Operator> orOperands = new ArrayList<>(Arrays.asList(operators));
		Iterator<Operator> iter = orOperands.iterator();
		while (iter.hasNext()) {
			Operator z = iter.next();
			if (z instanceof EqualTo) {
				EqualTo equalTo = (EqualTo) z;
				SortedSet<Object> c = equalTos.get(equalTo.getAttribute());
				if (c == null) {
					c = new TreeSet<>(NULL_SAFE_COMPARATOR);
					equalTos.put(equalTo.getAttribute(), c);
				}
				c.add(equalTo.getValue());
				iter.remove();
			}
		}

		for (Entry<String, SortedSet<Object>> entry : equalTos.entrySet()) {
			orOperands.add(In.create(entry.getKey(), entry.getValue()));
		}

		// step 2: identify anded not-equal-tos, which can be the result of a
		// not-in

		iter = orOperands.iterator();
		List<Operator> newElements = new ArrayList<>();
		while (iter.hasNext()) {
			Operator z = iter.next();
			if (z instanceof And) {
				Operator z2 = convertJoinedAnd((And) z);
				if (z2 != null) {
					iter.remove();
					newElements.add(z2);
				}
			}
		}
		orOperands.addAll(newElements);

		// process results:

		if (orOperands.size() == 1)
			return orOperands.iterator().next();

		return new Or(orOperands);
	}

	private static Operator convertJoinedAnd(And and) {
		List<Operator> andOperands = new ArrayList(and.getOperands());
		Map<String, SortedSet<Object>> notEqualTos = new HashMap<>();
		Iterator<Operator> iter = andOperands.iterator();
		while (iter.hasNext()) {
			Operator andOperand = iter.next();
			if (andOperand instanceof Not
					&& andOperand.getOperand(0) instanceof EqualTo) {
				iter.remove();
				EqualTo equalTo = (EqualTo) andOperand.getOperand(0);
				SortedSet<Object> c = notEqualTos.get(equalTo.getAttribute());
				if (c == null) {
					c = new TreeSet<>(NULL_SAFE_COMPARATOR);
					notEqualTos.put(equalTo.getAttribute(), c);
				}
				c.add(equalTo.getValue());
			}
		}

		for (Entry<String, SortedSet<Object>> entry : notEqualTos.entrySet()) {
			andOperands
					.add(new Not(In.create(entry.getKey(), entry.getValue())));
		}

		if (andOperands.size() == 1)
			return andOperands.iterator().next();
		return new And(andOperands);
	}
}
