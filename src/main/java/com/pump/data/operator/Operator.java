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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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
 * RuntimeException pointing out the discrepancy. (If we don't check this: then
 * when we ask "is the number 3 equal to the String '3'?" the answer will come
 * back no/false, which may be very misleading.)
 */
public abstract class Operator implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * This sorts Operators according to their toString output.
	 */
	static Comparator<Operator> toStringComparator = new Comparator<Operator>() {

		@Override
		public int compare(Operator o1, Operator o2) {
			return o1.toString().compareTo(o2.toString());
		}

	};

	/**
	 * This operator always evaluates to TRUE and has no input operands.
	 */
	public static final Operator TRUE = new ConstantOperator(true);

	/**
	 * This operator always evaluates to FALSE and has no input operands.
	 */
	public static final Operator FALSE = new ConstantOperator(false);

	/**
	 * This is the value used in templates.
	 */
	static final Comparable<String> TEMPLATE_VALUE = "?";

	/**
	 * Return the number of operands this Operator uses.
	 */
	public abstract int getOperandCount();

	/**
	 * Returns a specific operand.
	 * 
	 * @param index
	 *            an integer between 0 and {@link #getOperandCount()}.
	 */
	public abstract Object getOperand(int index);

	/**
	 * Evaluate whether this operator is true of false for a given context.
	 * 
	 * @param context
	 *            the context used to extract fields from data sources.
	 * @param dataSource
	 *            the data source (such as a bean) to extract data from.
	 * @return whether this operator evaluated as true or false.
	 * 
	 * @throws Exception
	 */
	public abstract boolean evaluate(OperatorContext context, Object dataSource)
			throws Exception;

	/**
	 * @param negated
	 *            if true then the String representation should represent a
	 *            negated operation. For example an EqualTo operator may
	 *            normally return "x == 5", but if negated is true it will
	 *            return "x != 5".
	 */
	protected abstract String toString(boolean negated);

	@Override
	public String toString() {
		return toString(false);
	}

	private transient int hashCode = Integer.MIN_VALUE;

	@Override
	public int hashCode() {
		if (hashCode == Integer.MIN_VALUE) {
			hashCode = createTestAtoms(getAttributeTypes()).hashCode();
		}
		return hashCode;
	}

	/**
	 * Return true if the argument is identical to this Operator.
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Operator))
			return false;
		return equals((Operator) obj, true);
	}

	/**
	 * Evaluate whether two Operators are equivalent.
	 * 
	 * @param operator
	 *            the operator to compare this object against.
	 * @param strictEquivalency
	 *            if true the the Operator argument must be exactly like this
	 *            Operator. (Another way to think of this is: the toString()
	 *            method of each operator should return exactly the same thing.)
	 *            If false then two operators are considered equal if they are
	 *            functionally the same.
	 *            <p>
	 *            For example "a || b" and "b || a" are functionally equal, but
	 *            not identical.
	 *            <p>
	 *            When determining "functional equivalency" we use TestAtoms to
	 *            evaluate whether two Operators would produce the same output
	 *            given the same input. This is basically the same as generating
	 *            a truth table for the two Operators, except TestAtoms support
	 *            a couple of additional states beyond true/false.
	 */
	@SuppressWarnings("rawtypes")
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
			Map<String, Collection<Class>> attributeTypes = getAttributeTypes();
			addAttributeTypes(attributeTypes, operator.getAttributeTypes());
			Map<String, Collection<TestAtom>> testAtoms = new HashMap<>();
			for (Entry<String, Collection<TestAtom>> entry : operator
					.createTestAtoms(attributeTypes).entrySet()) {
				Collection<TestAtom> c = new HashSet<>();
				testAtoms.put(entry.getKey(), c);
				c.addAll(entry.getValue());
			}
			for (Entry<String, Collection<TestAtom>> entry : createTestAtoms(
					attributeTypes).entrySet()) {
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

	/**
	 * Merge two attribute type maps together into the first argument.
	 */
	@SuppressWarnings("rawtypes")
	protected static void addAttributeTypes(
			Map<String, Collection<Class>> primaryMap,
			Map<String, Collection<Class>> incomingMap) {
		for (Entry<String, Collection<Class>> entry : incomingMap.entrySet()) {
			Collection<Class> w = primaryMap.get(entry.getKey());
			if (w == null) {
				w = new HashSet<>();
				primaryMap.put(entry.getKey(), w);
			}
			w.addAll(entry.getValue());
		}
	}

	/**
	 * Return all the configurations we should test a field against to evaluate
	 * whether two Operators are equivalent.
	 * 
	 * @param attributeTypes the {@link #getAttributeTypes()} of the root
	 * 			Operator. This will map an attribute to possible Classes
	 * 			it is known to use in this tree. (Each collection may also
	 * 			use null to indicate the value may be null at some point.)
	 * 			<p>
	 * 			Generally this will be 1 or 2 elements, such as
	 * 		    {String.class, null} or {Integer.class}.
	 */
	@SuppressWarnings("rawtypes")
	protected abstract Map<String, Collection<TestAtom>> createTestAtoms(
			Map<String, Collection<Class>> attributeTypes);

	/**
	 * Evaluate whether this Operator should pass or fail given a set of input
	 * TestAtoms.
	 */
	protected abstract boolean evaluateTestAtoms(Map<String, TestAtom> values);

	/**
	 * This represents a small testable unit of information, such as "x is 3" or
	 * "x is just under 3". (I considered naming this a "TestUnit", but that
	 * sounds confusingly similar to "unit test")
	 * <p>
	 * Each operation can identify the TestAtoms that it would need to evaluate
	 * an input.
	 */
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

	private transient Operator canonicalOperator = null;
	private transient Operator templateOperator = null;
	private transient Boolean isCanonical = null;

	/**
	 * Return true if this Operator is canonical.
	 * <p>
	 * In this architecture a "canonical" expression is one that meets these
	 * critiera:
	 * <ul>
	 * <li>A tree representation of an Operator must present nodes in this
	 * order: Or, And, Not, other.</li>
	 * <li>Within Or and And operators: operands must be sorted in alphabetical
	 * order.</li>
	 * </ul>
	 */
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

	/**
	 * This helps validate whether operations are nested in a canonical
	 * structure.
	 * <p>
	 * ORs return 0, AND returns 1, NOT returns 2, and everything else returns
	 * 3.
	 * <p>
	 * An operator with an order of N can only contain operands that are of
	 * order (N+1) or higher.
	 */
	protected abstract int getCanonicalOrder();

	/**
	 * Return an operator that is functionally equivalent to this Operator, but
	 * it may be restructured and simplified.
	 */
	public Operator getCanonicalOperator() {
		if (canonicalOperator == null) {
			Operator op = createCanonicalOperator();

			// this is necessary to allow the unit tests to complete in a few
			// seconds:
			op = CanonicalSimplifier.simplify(op);

			// just to avoid any extra calculations:
			op.canonicalOperator = op.canonicalOperator;

			canonicalOperator = op;
		}
		return canonicalOperator;
	}

	/**
	 * Create a canonical Operator based on this Operator. In simple cases this
	 * may return this object.
	 */
	protected abstract Operator createCanonicalOperator();

	/**
	 * Return an Operator that is similar to this object, but it replaces fixed
	 * values with a constant value.
	 * <p>
	 * For example if the original Operator was "a==3 || b==true", then the
	 * template operator would resemble "a==? || b==?". If two operators use the
	 * same template then they consult the same properties in similar ways, but
	 * they may be different queries.
	 */
	public Operator getTemplateOperator() {
		if (templateOperator == null) {
			templateOperator = createTemplateOperator();
			templateOperator.templateOperator = templateOperator;
		}
		return templateOperator;
	}

	/**
	 * Create an Operator that is similar to this object, but it replaces fixed
	 * values with a constant value.
	 */
	protected abstract Operator createTemplateOperator();

	public List<Object> getOperands() {
		List<Object> returnValue = new ArrayList<>(getOperandCount());
		for (int a = 0; a < getOperandCount(); a++) {
			returnValue.add(getOperand(a));
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
			// subclasses write data; we don't write data here
		} else {
			throw new IOException("Unsupported internal version: " + version);
		}
	}

	/**
	 * Convert this Operator into a new expression that does not include any
	 * Ors.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public final Collection<Operator> split() {
		// this should return something that's mutable

		Operator c = getCanonicalOperator();
		if (c instanceof Or) {
			return new ArrayList<Operator>((List) c.getOperands());
		}

		List<Operator> l = new ArrayList<>(1);
		l.add(c);
		return l;
	}

	/**
	 * Return all the attributes/fields this Operator (and its descendants)
	 * consult during evaluation.
	 */
	public final Collection<String> getAttributes() {
		return new HashSet<String>(getAttributeTypes().keySet());
	}

	/**
	 * Return a map of all the attributes this Operator (and its descendants)
	 * use and their possible values. The collection may contain null,
	 * which is used to indicate a null value may be used.
	 * <p>
	 * Generally each collection will be 1 or 2 elements, such as
	 * {String.class, null} or {Integer.class}.
	 */
	@SuppressWarnings("rawtypes")
	protected abstract Map<String, Collection<Class>> getAttributeTypes();

	@SuppressWarnings("rawtypes")
	private static Comparator NULL_SAFE_COMPARATOR = new Comparator() {

		@SuppressWarnings("unchecked")
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

	/**
	 * Join operators together in an Or.
	 * <p>
	 * This also analyzes the expression to convert a series of EqualTo
	 * operators to In operators.
	 * 
	 * @param operators
	 *            a series of Operators, probably previously generated by
	 *            calling {@link #split()}.
	 * @return an Operator that is functionally equivalent to OR'ing all the
	 *         input Operators.
	 */
	public static Operator join(Collection<Operator> operators) {
		return join(operators.toArray(new Operator[operators.size()]));
	}

	/**
	 * Join operators together in an Or.
	 * <p>
	 * This also analyzes the expression to convert a series of EqualTo
	 * operators to In operators.
	 * 
	 * @param operators
	 *            a series of Operators, probably previously generated by
	 *            calling {@link #split()}.
	 * @return an Operator that is functionally equivalent to OR'ing all the
	 *         input Operators.
	 */
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

		// step 3: factor out ANDed expressions

		Collection<String> attributes = new LinkedHashSet<>();
		for (Operator op : orOperands) {
			attributes.addAll(op.getAttributes());
		}

		for (String attr : attributes) {
			Map<Operator, Collection<EqualTo>> values = new LinkedHashMap<>();
			iter = orOperands.iterator();
			while (iter.hasNext()) {
				Operator z = iter.next();
				if (z instanceof And) {
					And and = (And) z;
					Collection<Operator> w = and.getOperandsFor(attr);
					if (w.size() == 1 && w.iterator().next() instanceof EqualTo) {
						EqualTo equalTo = (EqualTo) w.iterator().next();
						List other = and.getOperands();
						other.remove(equalTo);
						Operator otherOp = other.size() == 1 ? (Operator) other
								.iterator().next() : new And(other);

						Collection<EqualTo> c = values.get(otherOp);
						if (c == null) {
							c = new LinkedHashSet<>();
							values.put(otherOp, c);
						}
						c.add(equalTo);
					}
				}
			}

			for (Entry<Operator, Collection<EqualTo>> e : values.entrySet()) {
				if (e.getValue().size() > 1) {
					Collection v = new LinkedHashSet<>();
					for (EqualTo j : e.getValue()) {
						v.add(j.getValue());
					}
					Operator inTerm = In.create(attr, v);
					Operator newAndTerm;
					if (e.getKey() instanceof And) {
						List k = e.getKey().getOperands();
						k.add(inTerm);
						newAndTerm = new And(k);
					} else {
						newAndTerm = new And(inTerm, e.getKey());
					}

					orOperands.add(newAndTerm);

					for (EqualTo y : e.getValue()) {
						Operator oldAndTerm;
						if (e.getKey() instanceof And) {
							List k = e.getKey().getOperands();
							k.add(y);
							oldAndTerm = new And(k);
						} else {
							oldAndTerm = new And(y, e.getKey());
						}

						iter = orOperands.iterator();
						while (iter.hasNext()) {
							Operator z = iter.next();
							if (z.equals(oldAndTerm, false))
								iter.remove();
						}
					}
				}
			}
		}

		// process results:

		if (orOperands.size() == 1)
			return orOperands.iterator().next();

		return new Or(orOperands);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
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