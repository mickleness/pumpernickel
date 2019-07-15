package com.pump.data.operator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.pump.text.WildcardPattern;
import com.pump.util.CombinationIterator;

public abstract class Operator implements Serializable {
	private static final long serialVersionUID = 1L;

	private static Comparator<Operator> toStringComparator = new Comparator<Operator>() {

		@Override
		public int compare(Operator o1, Operator o2) {
			return o1.toString().compareTo(o2.toString());
		}

	};

	public interface Context {
		public Object getValue(Object bean, String attributeName);
	}

	private static class ConstantOperator extends Operator {
		private static final long serialVersionUID = 1L;

		private final boolean value;

		private ConstantOperator(boolean value) {
			this.value = value;
		}

		@Override
		public int getOperandCount() {
			return 0;
		}

		@Override
		public Object getOperand(int index) {
			return null;
		}

		@Override
		public boolean evaluate(Context context, Object bean) throws Exception {
			return value;
		}

		@Override
		protected String toString(boolean negated) {
			boolean v = negated ? !value : value;
			return Boolean.valueOf(v).toString();
		}

		@Override
		protected Operator createCanonicalOperator() {
			return this;
		}

		@Override
		protected boolean canonicalEquals(Operator canonicalOther) {
			if (!(canonicalOther instanceof ConstantOperator))
				return false;
			ConstantOperator other = (ConstantOperator) canonicalOther;
			return value == other.value;
		}

		@Override
		public Collection<Operator> split() {
			return Collections.singleton(this);
		}

		@Override
		public int hashCode() {
			return Boolean.valueOf(value).hashCode();
		}

		@Override
		public Operator getCanonicalOperator() {
			return this;
		}
	}

	public static final Operator TRUE = new ConstantOperator(true);
	public static final Operator FALSE = new ConstantOperator(false);

	/**
	 * This accepts an arbitrary number of operands. This is intended for AND
	 * and OR operators.
	 */
	public static abstract class AbstractCompoundOperator extends Operator {
		private static final long serialVersionUID = 1L;

		protected static void validateOperands(List<Operator> operands,
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
						"2 or more operands are required for a "
								+ operationName + " operator");
		}

		private final List<Operator> operands;
		private final String operatorString;

		/**
		 * 
		 * @param operatorString
		 *            this is placed between operands when converting this to a
		 *            String. This should be " && " or " || ".
		 * @param operands
		 */
		protected AbstractCompoundOperator(String operatorString,
				List<Operator> operands) {
			Objects.requireNonNull(operatorString);
			this.operatorString = operatorString;
			validateOperands(operands, getClass().getSimpleName());
			this.operands = Collections.unmodifiableList(operands);
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
			for (int a = 0; a < getOperandCount(); a++) {
				if (a != 0)
					sb.append(operatorString);
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
	}

	/**
	 * This evaluates an attribute from a bean.
	 *
	 * @param <String>
	 * @param <DataType>
	 */
	public abstract static class ValueOperator<DataType> extends Operator {
		private static final long serialVersionUID = 1L;

		protected final String attributeName;
		protected final DataType fixedValue;

		/**
		 * @param valueName
		 *            the value to inspect in incoming beans. This may not be
		 *            null.
		 * @param fixedValue
		 *            the data type this operator compares the value against.
		 */
		protected ValueOperator(String attributeName, DataType fixedValue) {
			Objects.requireNonNull(attributeName);
			this.attributeName = attributeName;
			this.fixedValue = fixedValue;
		}

		@Override
		public final int getOperandCount() {
			return 2;
		}

		public String getAttribute() {
			return (String) getOperand(0);
		}

		@SuppressWarnings("unchecked")
		public DataType getValue() {
			return (DataType) getOperand(1);
		}

		public Object getOperand(int index) {
			if (index == 0)
				return attributeName;
			if (index == 1)
				return fixedValue;
			throw new IllegalArgumentException("illegal operand index: "
					+ index);
		}
	}

	public static class Not extends Operator {
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

		private Not(Operator operand) {
			Objects.requireNonNull(operand);
			this.operand = operand;
		}

		@Override
		public boolean evaluate(Context context, Object bean) throws Exception {
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

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		protected Operator createCanonicalOperator() {
			if (operand instanceof AbstractCompoundOperator) {
				AbstractCompoundOperator compoundOp = (AbstractCompoundOperator) operand;
				List<Operator> negatedTerms = new ArrayList<>(
						compoundOp.getOperandCount());
				List operands = (List) compoundOp.getOperands();
				for (Operator member : (List<Operator>) operands) {
					negatedTerms.add(Not.create(member));
				}

				if (operand instanceof And) {
					return Or.create(negatedTerms).getCanonicalOperator();
				} else if (operand instanceof Or) {
					return And.create(negatedTerms).getCanonicalOperator();
				}
				throw new IllegalStateException("Unsupported operator "
						+ operand.toString() + " ("
						+ operand.getClass().getName() + ")");
			}
			return this;
		}

		@Override
		public Collection<Operator> split() {
			if (getOperand(0) instanceof Not) {
				Not n = ((Not) getOperand(0));
				return n.getOperand(0).split();
			} else if (getOperand(0) instanceof And) {
				And and = (And) getOperand(0);
				// we can convert an And to an Or to get more narrowed options:
				Collection<Operator> returnValue = new ArrayList<>();
				for (int a = 0; a < and.getOperandCount(); a++) {
					Collection<Operator> c = and.getOperand(a).split();
					for (Operator op : c) {
						returnValue.add(Not.create(op));
					}
				}
				return returnValue;
			}

			Collection<Operator> z = getOperand(0).split();

			Collection<Operator> returnValue = new ArrayList<Operator>(z.size());
			for (Operator op : z) {
				returnValue.add(Not.create(op));
			}

			return returnValue;
		}
	}

	public static class And extends AbstractCompoundOperator {
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

			// if we have (A * !A), condense to FALSE
			for (int a = 0; a < newOperands.size(); a++) {
				Operator op = newOperands.get(a);
				if (op instanceof Not) {
					Not n = (Not) op;
					Operator inner = n.getOperand(0);
					if (newOperands.contains(inner))
						return FALSE;
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
			super(" && ", operands);
		}

		@Override
		public boolean evaluate(Context context, Object bean) throws Exception {
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
			CombinationIterator<Operator> iter = new CombinationIterator(
					orGroups);
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
			List<Operator[]> operandVariations = new ArrayList<>(
					getOperandCount());
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
	}

	public static class Or extends AbstractCompoundOperator {
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

			// if we have (A + !A), condense to TRUE
			for (int a = 0; a < newOperands.size(); a++) {
				Operator op = newOperands.get(a);
				if (op instanceof Not) {
					Not n = (Not) op;
					Operator inner = n.getOperand(0);
					if (newOperands.contains(inner))
						return TRUE;
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
			super(" || ", operands);
		}

		@Override
		public boolean evaluate(Context context, Object bean) throws Exception {
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
			Collection<Operator> returnValue = new ArrayList<>(
					getOperandCount());
			for (int a = 0; a < getOperandCount(); a++) {
				returnValue.addAll(getOperand(a).split());
			}
			return returnValue;
		}
	}

	public static class EqualTo extends ValueOperator<Object> {
		private static final long serialVersionUID = 1L;

		public EqualTo(String attribute, Object value) {
			super(attribute, value);
		}

		@Override
		public boolean evaluate(Context context, Object bean) throws Exception {
			Object value = context.getValue(bean, getAttribute());
			return Objects.equals(value, getValue());
		}

		@Override
		protected String toString(boolean negated) {
			StringBuilder sb = new StringBuilder();
			sb.append(attributeName);
			sb.append(negated ? " != " : " == ");
			if (fixedValue == null) {
				sb.append("null");
			} else {
				sb.append('\'');
				sb.append(fixedValue);
				sb.append('\'');
			}
			return sb.toString();
		}

		@Override
		protected Operator createCanonicalOperator() {
			return this;
		}

		@Override
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Collection<Operator> split() {
			return (Collection) Collections.singleton(this);
		}
	}

	public static class GreaterThan extends ValueOperator<Comparable<?>> {
		private static final long serialVersionUID = 1L;

		public GreaterThan(String attribute, Comparable<?> value) {
			super(attribute, value);
			Objects.requireNonNull(value);
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public boolean evaluate(Context context, Object bean) throws Exception {
			Comparable value = (Comparable) context.getValue(bean,
					getAttribute());
			return value.compareTo(getValue()) > 0;
		}

		@Override
		protected String toString(boolean negated) {
			StringBuilder sb = new StringBuilder();
			sb.append(attributeName);
			sb.append(negated ? " <= " : " > ");
			sb.append('\'');
			sb.append(fixedValue);
			sb.append('\'');
			return sb.toString();
		}

		@Override
		protected Operator createCanonicalOperator() {
			return this;
		}

		@Override
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Collection<Operator> split() {
			return (Collection) Collections.singleton(this);
		}
	}

	public static class LesserThan extends ValueOperator<Comparable<?>> {
		private static final long serialVersionUID = 1L;

		public LesserThan(String attribute, Comparable<?> value) {
			super(attribute, value);
			Objects.requireNonNull(value);
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public boolean evaluate(Context context, Object bean) throws Exception {
			Comparable value = (Comparable) context.getValue(bean,
					getAttribute());
			return value.compareTo(getValue()) < 0;
		}

		@Override
		protected String toString(boolean negated) {
			StringBuilder sb = new StringBuilder();
			sb.append(attributeName);
			sb.append(negated ? " >= " : " < ");
			sb.append('\'');
			sb.append(fixedValue);
			sb.append('\'');
			return sb.toString();
		}

		@Override
		protected Operator createCanonicalOperator() {
			// we can't support "lesserThan" and "greaterThan" at the same time
			// in an unambiguous canonical representation.
			// so instead we'll convert an expression like "a < 50" to
			// "!(a > 50) && a!=50)"
			return And.create(
					Not.create(new GreaterThan(getAttribute(), getValue())),
					Not.create(new EqualTo(getAttribute(), getValue())));
		}

		@Override
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Collection<Operator> split() {
			return (Collection) Collections.singleton(this);
		}
	}

	public static class Like extends ValueOperator<WildcardPattern> {
		private static final long serialVersionUID = 1L;

		public Like(String attribute, WildcardPattern pattern) {
			super(attribute, pattern);
			Objects.requireNonNull(pattern);
		}

		@Override
		public boolean evaluate(Context context, Object bean) throws Exception {
			Object value = context.getValue(bean, getAttribute());
			if (value == null)
				return false;
			String str = String.valueOf(value);
			return getValue().matches(str);
		}

		@Override
		protected String toString(boolean negated) {
			StringBuilder sb = new StringBuilder();
			if (negated)
				sb.append("!");
			sb.append("matches(");
			sb.append(attributeName);
			sb.append(", '");
			sb.append(fixedValue.getPatternText());
			sb.append("')");
			return sb.toString();
		}

		@Override
		protected Operator createCanonicalOperator() {
			return this;
		}

		@Override
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public Collection<Operator> split() {
			return (Collection) Collections.singleton(this);
		}
	}

	public static class In extends ValueOperator<Collection<?>> {
		private static final long serialVersionUID = 1L;

		public In(String attribute, Collection<?> value) {
			super(attribute, value);
		}

		@Override
		public boolean evaluate(Context context, Object bean) throws Exception {
			Object value = context.getValue(bean, getAttribute());
			return getValue().contains(value);
		}

		@Override
		protected String toString(boolean negated) {
			StringBuilder sb = new StringBuilder();
			sb.append("contains(");
			sb.append(attributeName);
			sb.append(", {");
			boolean first = true;
			for (Object e : fixedValue) {
				if (!first)
					sb.append(", ");
				first = false;
				if (e == null) {
					sb.append("null");
				} else {
					sb.append('\'');
					sb.append(e);
					sb.append('\'');
				}
			}
			sb.append("})");
			return sb.toString();
		}

		@Override
		protected Operator createCanonicalOperator() {
			return this;
		}

		@Override
		public Collection<Operator> split() {
			Collection<Operator> returnValue = new HashSet<>();
			for (Object e : getValue()) {
				returnValue.add(new EqualTo(getAttribute(), e));
			}
			return returnValue;
		}
	}

	private transient Operator canonicalOperator;

	public abstract int getOperandCount();

	public abstract Object getOperand(int index);

	public abstract boolean evaluate(Context context, Object bean)
			throws Exception;

	protected abstract String toString(boolean negated);

	@Override
	public String toString() {
		return toString(false);
	}

	@Override
	public int hashCode() {
		return getCanonicalOperator().canonicalHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Operator))
			return false;
		Operator canonicalOther = ((Operator) obj).getCanonicalOperator();
		return getCanonicalOperator().canonicalEquals(canonicalOther);
	}

	public List<Object> getOperands() {
		List<Object> returnValue = new ArrayList<>(getOperandCount());
		for (int a = 0; a < getOperandCount(); a++) {
			returnValue.add(getOperand(a));
		}
		return Collections.unmodifiableList(returnValue);
	}

	/**
	 * Return an Operator that is functionally equivalent to this Operator that
	 * is structured using OR, AND, and NOT nodes in that order (a specific type
	 * of "sum of products"). All ValueOperators are sorted alphabetically.
	 * <p>
	 * The canonical operator is used to compare equivalence or hash codes.
	 */
	public Operator getCanonicalOperator() {
		if (canonicalOperator == null) {
			Operator op = createCanonicalOperator();
			op = simplifyCanonicalOperator(op);
			op.canonicalOperator = op;
			canonicalOperator = op;
		}
		return canonicalOperator;
	}

	/**
	 * This simplifies an expression (if possible) and returns an equivalent
	 * canonical (SOP) expression.
	 * 
	 * @param op
	 *            the incoming canonical SOP expression
	 * @return an equivalent canonical SOP expression that may be simpler
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected static Operator simplifyCanonicalOperator(Operator op) {

		// apply redundancy laws to reduced the number/complexity of or terms
		//
		// 1. "A + (A * B) -> A"
		// 2. "(A * B) + (A * ~B) -> A"
		// 3. "A + (~A * B) -> A + B"

		boolean runAgain;
		if (op instanceof Or) {
			List orTerms = new ArrayList(op.getOperands());
			boolean modified = false;
			do {
				runAgain = false;
				for (int a = 0; a < orTerms.size(); a++) {
					for (int b = a + 1; b < orTerms.size(); b++) {
						Operator opA = (Operator) orTerms.get(a);
						Operator opB = (Operator) orTerms.get(b);
						if (opA != null && opB != null) {
							List aOperands = new ArrayList(
									opA instanceof And ? ((And) opA)
											.getOperands() : Arrays.asList(opA));
							List bOperands = new ArrayList(
									opB instanceof And ? ((And) opB)
											.getOperands() : Arrays.asList(opB));

							List commonOperands = new ArrayList<>();
							for (Object aElement : aOperands) {
								if (bOperands.contains(aElement)) {
									commonOperands.add(aElement);
								}
							}

							// rule #1: "A + (A * B) -> A"

							if (aOperands.size() > commonOperands.size()
									&& bOperands.size() == commonOperands
											.size()) {
								// opB is a subset of opA, so opA can be purged
								orTerms.set(a, null);
								runAgain = true;
								modified = true;
								continue;
							} else if (bOperands.size() > commonOperands.size()
									&& aOperands.size() == commonOperands
											.size()) {
								// opA is a subset of opB, so opB can be purged
								orTerms.set(b, null);
								runAgain = true;
								modified = true;
								continue;
							}

							// rule #2: apply "AB + A(!B) -> A"
							aOperands.removeAll(commonOperands);
							bOperands.removeAll(commonOperands);

							if (!(commonOperands.isEmpty()
									|| aOperands.isEmpty() || bOperands
										.isEmpty())) {
								Operator aLeftover = aOperands.size() == 1 ? (Operator) aOperands
										.get(0) : And.create(aOperands);
								Operator bLeftover = bOperands.size() == 1 ? (Operator) bOperands
										.get(0) : And.create(bOperands);
								if (aLeftover.equals(Not.create(bLeftover))) {
									Operator shared = commonOperands.size() == 1 ? (Operator) commonOperands
											.get(0) : And
											.create(commonOperands);
									orTerms.set(a, null);
									orTerms.set(b, shared);
									runAgain = true;
									modified = true;
									continue;
								}
							}

							// rule #3: "AZ + (!A)BZ -> AZ + BZ"

							if (commonOperands.size() > 0) {
								// remember at this point aOperands and
								// bOperands already have stripped away
								// commonOperands
								if (aOperands.size() == 1
										&& bOperands.size() == 2) {
									if (bOperands
											.remove(Not
													.create((Operator) aOperands
															.get(0)))) {
										bOperands.addAll(commonOperands);
										orTerms.set(b, And.create(bOperands));
										runAgain = true;
										modified = true;
									}
								} else if (aOperands.size() == 2
										&& bOperands.size() == 1) {
									if (aOperands
											.remove(Not
													.create((Operator) bOperands
															.get(0)))) {
										aOperands.addAll(commonOperands);
										orTerms.set(a, And.create(aOperands));
										runAgain = true;
										modified = true;
									}
								}
							}
						}
					}
				}
			} while (runAgain);

			if (modified) {
				Iterator iter = orTerms.iterator();
				while (iter.hasNext()) {
					Object element = iter.next();
					if (element == null)
						iter.remove();
				}

				Collections.sort(orTerms, toStringComparator);
				op = orTerms.size() == 1 ? (Operator) orTerms.get(0) : Or
						.create(orTerms);
			}
		}

		return op;
	}

	/**
	 * This method should only be called on a canonical Operator.
	 * 
	 * @return
	 */
	protected int canonicalHashCode() {
		int z = 0;
		for (int a = 0; a < getOperandCount(); a++) {
			z += Objects.hashCode(getOperand(a));
		}
		return z;
	}

	/**
	 * This method should only be called on a canonical Operator
	 * 
	 * @param canonicalOther
	 *            the other Operator to compare this to.
	 * @return
	 */
	protected boolean canonicalEquals(Operator canonicalOther) {
		if (!canonicalOther.getClass().equals(getClass()))
			return false;
		if (getOperandCount() != canonicalOther.getOperandCount())
			return false;

		for (int a = 0; a < getOperandCount(); a++) {
			if (!Objects.equals(getOperand(a), canonicalOther.getOperand(a)))
				return false;
		}
		return true;

	}

	/**
	 * Create an Operator that is functionally equivalent to this Operator that
	 * is structured using OR, AND, and NOT nodes in that order (a specific type
	 * of "sum of products"). All ValueOperators are sorted alphabetically.
	 * <p>
	 * This method is used to compare equivalence or hash codes.
	 */
	protected abstract Operator createCanonicalOperator();

	public abstract Collection<Operator> split();
}
