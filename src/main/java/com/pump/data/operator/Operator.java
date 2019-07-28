package com.pump.data.operator;

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

import com.pump.io.parser.java.JavaEncoding;
import com.pump.math.Range;
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

		@Override
		public Collection<String> getAttributes() {
			return Collections.emptySet();
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
				List<Operator> operands, String name) {
			Objects.requireNonNull(operatorString);
			this.operatorString = operatorString;
			validateOperands(operands, name);
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

		@Override
		public Collection<String> getAttributes() {
			Collection<String> returnValue = new HashSet<>();
			for (int a = 0; a < getOperandCount(); a++) {
				Operator op = getOperand(a);
				returnValue.addAll(op.getAttributes());
			}
			return returnValue;
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

		@Override
		public Collection<String> getAttributes() {
			return Collections.singleton(getAttribute());
		}

		protected String toString(Object value) {
			StringBuilder sb = new StringBuilder();
			if (value instanceof Integer) {
				sb.append(value.toString());
			} else if (value instanceof Float) {
				sb.append(value.toString());
				sb.append('f');
			} else if (value instanceof Double) {
				sb.append(value.toString());
				if (sb.indexOf(".") == -1) {
					sb.append(".0");
				}
			} else if (value instanceof Long) {
				sb.append(value.toString());
				sb.append('L');
			} else if (value instanceof Character) {
				sb.append('\'');
				sb.append(JavaEncoding.encode(fixedValue.toString()));
				sb.append('\'');
			} else if (value == null) {
				sb.append("null");
			} else {
				sb.append('\"');
				sb.append(JavaEncoding.encode(fixedValue.toString()));
				sb.append('\"');
			}
			return sb.toString();
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

		@Override
		public Collection<String> getAttributes() {
			return getOperand(0).getAttributes();
		}

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
							+ op.toString() + " (" + op.getClass().getName()
							+ ")");
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
						+ operand.toString() + " ("
						+ operand.getClass().getName() + ")");
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

			if (newOperands.size() == 0) {
				newOperands.add(TRUE);
			}
			if (newOperands.size() == 1)
				return newOperands.get(0);
			return new And(newOperands);
		}

		public And(List<Operator> operands) {
			super(" && ", operands, "And");
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

			if (newOperands.size() == 0) {
				newOperands.add(FALSE);
			}
			if (newOperands.size() == 1)
				return newOperands.get(0);
			return new Or(newOperands);
		}

		public Or(List<Operator> operands) {
			super(" || ", operands, "Or");
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
			sb.append(getAttribute());
			sb.append(negated ? " != " : " == ");
			if (fixedValue == null) {
				sb.append("null");
			} else {
				sb.append(toString(getValue()));
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
			sb.append(getAttribute());
			sb.append(negated ? " <= " : " > ");
			sb.append(toString(getValue()));
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
			sb.append(getAttribute());
			sb.append(negated ? " >= " : " < ");
			sb.append(getValue());
			return sb.toString();
		}

		@Override
		protected Operator createCanonicalOperator() {
			// we can't support "lesserThan" and "greaterThan" at the same time
			// in an unambiguous canonical representation.
			// so instead we'll convert an expression like "a < 50" to
			// "!(a > 50) && a!=50)"
			List<Operator> k = new ArrayList<>(2);
			k.add(Not.create(new GreaterThan(getAttribute(), getValue())));
			k.add(Not.create(new EqualTo(getAttribute(), getValue())));
			Collections.sort(k, toStringComparator);
			return And.create(k);
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
			sb.append(", \"");
			sb.append(fixedValue.getPatternText());
			sb.append("\")");
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

		public static Operator create(String attribute, Collection<?> values) {
			Objects.requireNonNull(values);
			if (values.size() == 0)
				return FALSE;
			if (values.size() == 1)
				return new EqualTo(attribute, values.iterator().next());
			return new In(attribute, values);
		}

		private In(String attribute, Collection<?> value) {
			super(attribute, new HashSet<>(value));
		}

		@Override
		public boolean evaluate(Context context, Object bean) throws Exception {
			Object value = context.getValue(bean, getAttribute());
			return getValue().contains(value);
		}

		@Override
		protected String toString(boolean negated) {
			StringBuilder sb = new StringBuilder();
			if (negated)
				sb.append("!");
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
					sb.append(toString(e));
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
	public synchronized Operator getCanonicalOperator() {
		if (canonicalOperator == null) {
			Operator op = createCanonicalOperator();
			op = simplifyCanonicalOperator(op);
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
	protected static Operator simplifyCanonicalOperator(Operator op) {

		boolean runAgain;
		do {
			Operator original = op;
			if (op instanceof Or) {
				op = simplifyCanonicalOr((Or) op);
			} else if (op instanceof And) {
				op = simplifyCanonicalAnd((And) op);
			}
			runAgain = original != op;
		} while (runAgain);

		return op;
	}

	private static void separateAttributeTerms(String attr, Operator op,
			List<Operator> attrTerms, List<Operator> otherTerms) {
		if (op instanceof And) {
			And and = (And) op;
			for (int a = 0; a < and.getOperandCount(); a++) {
				separateAttributeTerms(attr, and.getOperand(a), attrTerms,
						otherTerms);
			}
		} else if (op instanceof Not) {
			Not not = (Not) op;
			ValueOperator v = (ValueOperator) not.getOperand(0);
			if (v.getAttribute().equals(attr)) {
				attrTerms.add(not);
			} else {
				otherTerms.add(not);
			}
		} else if (op instanceof ValueOperator) {
			ValueOperator v = (ValueOperator) op;
			if (v.getAttribute().equals(attr)) {
				attrTerms.add(v);
			} else {
				otherTerms.add(v);
			}
		} else {
			// this shouldn't happen if our input was canonical
			throw new IllegalStateException(op.toString());
		}
	}

	protected static Operator simplifyCanonicalOr(final Or or) {
		Or orOp = or;

		class Term {
			String attr;
			List<Operator> andTerms;
			AndProfile andProfile;

			Term(String attr, List<Operator> andTerms) {
				this.attr = attr;
				this.andTerms = andTerms;
				andProfile = new AndProfile(andTerms);
			}

			Term(String attr, Range range) {
				this.attr = attr;
				andProfile = new AndProfile(new ArrayList());
				andProfile.variableRanges.put(attr, range);
				andTerms = andProfile.createAndOperators();
			}

			public String toString() {
				return "Term[attr=" + attr + ", terms=\"" + andTerms
						+ "\", profile=" + andProfile.variableRanges.values()
						+ "]";
			}
		}

		for (String attr : or.getAttributes().toArray(
				new String[or.getAttributes().size()])) {
			// step 1: catalog everything into "factored":
			Map<List<Operator>, List<Term>> factored = new HashMap<>();

			for (int a = 0; a < orOp.getOperandCount(); a++) {
				// z must be AND, NOT, or a ValueOperator
				Operator z = orOp.getOperand(a);
				List<Operator> attrTerms = new ArrayList<>();
				List<Operator> nonAttrTerms = new ArrayList<>();
				separateAttributeTerms(attr, z, attrTerms, nonAttrTerms);

				List<Term> attrTermList = factored.get(nonAttrTerms);
				if (attrTermList == null) {
					attrTermList = new ArrayList<>();
					factored.put(nonAttrTerms, attrTermList);
				}
				attrTermList.add(new Term(attr, attrTerms));
			}

			// step 2: now that we've factored everything, purge any redundant
			// Terms
			Iterator<Entry<List<Operator>, List<Term>>> iter = factored
					.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<List<Operator>, List<Term>> entry = iter.next();
				List<Term> attrTermList = entry.getValue();

				for (int a = 0; a < attrTermList.size(); a++) {
					for (int b = a + 1; b < attrTermList.size(); b++) {
						Term termA = attrTermList.get(a);
						Term termB = attrTermList.get(b);
						Range rangeA = termA == null
								|| termA.andProfile.variableRanges.isEmpty() ? null
								: termA.andProfile.variableRanges.values()
										.iterator().next();
						Range rangeB = termB == null
								|| termB.andProfile.variableRanges.isEmpty() ? null
								: termB.andProfile.variableRanges.values()
										.iterator().next();

						if (rangeA != null && rangeB != null) {
							Range newRange = rangeA.or(rangeB);
							if (newRange != null) {
								try {
									Term newTerm = new Term(attr, newRange);
									attrTermList.set(a, null);
									attrTermList.set(b, newTerm);
								} catch (Exception e) {
									// this range can't be converted to a
									// canonical operator
								}
							}
						}
					}
				}

				// actually flatten the data structure now that we finished
				// swapping elements:
				Iterator<Term> iter2 = attrTermList.iterator();
				while (iter2.hasNext()) {
					Term t = iter2.next();
					if (t == null)
						iter2.remove();
				}
			}

			// step 3: recreate all the OR terms
			List<Operator> newOrTerms = new ArrayList<>();

			iter = factored.entrySet().iterator();
			while (iter.hasNext()) {
				Entry<List<Operator>, List<Term>> entry = iter.next();
				List<Operator> nonAttrTerms = entry.getKey();
				List<Term> attrTermList = entry.getValue();
				for (Term term : attrTermList) {
					List<Operator> z = new ArrayList<>(nonAttrTerms.size()
							+ term.andTerms.size());
					z.addAll(nonAttrTerms);
					z.addAll(term.andTerms);
					Collections.sort(z, toStringComparator);
					if (z.size() > 0) {
						Operator t = z.size() == 1 ? z.get(0) : And.create(z);
						newOrTerms.add(t);
					}
				}
			}

			Collections.sort(newOrTerms, toStringComparator);

			if (newOrTerms.size() == 0)
				return TRUE;

			Operator z = newOrTerms.size() == 1 ? newOrTerms.get(0) : Or
					.create(newOrTerms);
			if (z instanceof Or) {
				orOp = (Or) z;
			} else {
				return z;
			}
		}

		List orTerms = new ArrayList(orOp.getOperands());

		scanOrTerms: for (int a = 0; a < orTerms.size(); a++) {

			Operator opA = (Operator) orTerms.get(a);
			if (opA instanceof Not) {
				// if we have (A || !A), condense to TRUE

				Not n = (Not) opA;
				Operator notOp = n.getOperand(0);
				if (orTerms.contains(notOp))
					return TRUE;
			}

			// apply redundancy laws to reduced the number/complexity of
			// or terms
			//
			// 1. "A + (A * B) -> A"
			// 2. "(A * B) + (A * ~B) -> A"
			// 3. "A + (~A * B) -> A + B"
			for (int b = a + 1; b < orTerms.size(); b++) {
				Operator opB = (Operator) orTerms.get(b);
				if (opA != null && opB != null) {
					List aOperands = new ArrayList(
							opA instanceof And ? ((And) opA).getOperands()
									: Arrays.asList(opA));
					List bOperands = new ArrayList(
							opB instanceof And ? ((And) opB).getOperands()
									: Arrays.asList(opB));

					// if we know "x > y", then we can AND that with
					// "x != y". This extra info will get purged
					// soon if it's not necessary, but it helps us
					// identify certain patterns in the redundancy
					// rules. (If you comment out this code: unit tests
					// will fail.)
					for (int c = 0; c < bOperands.size(); c++) {
						if (bOperands.get(c) instanceof GreaterThan) {
							GreaterThan g = (GreaterThan) bOperands.get(c);
							Operator notEqualTo = Not.create(new EqualTo(g
									.getAttribute(), g.getValue()));
							if (aOperands.contains(notEqualTo))
								bOperands.add(notEqualTo);
						}
					}
					for (int c = 0; c < aOperands.size(); c++) {
						if (aOperands.get(c) instanceof GreaterThan) {
							GreaterThan g = (GreaterThan) aOperands.get(c);
							Operator notEqualTo = Not.create(new EqualTo(g
									.getAttribute(), g.getValue()));
							if (bOperands.contains(notEqualTo))
								aOperands.add(notEqualTo);
						}
					}

					List commonOperands = new ArrayList<>();
					for (Object aElement : aOperands) {
						if (bOperands.contains(aElement)) {
							commonOperands.add(aElement);
						}
					}

					// rule #1: "A + (A * B) -> A"

					if (aOperands.size() > commonOperands.size()
							&& bOperands.size() == commonOperands.size()) {
						// opB is a subset of opA, so opA can be purged
						orTerms.set(a, null);
						break scanOrTerms;
					} else if (bOperands.size() > commonOperands.size()
							&& aOperands.size() == commonOperands.size()) {
						// opA is a subset of opB, so opB can be purged
						orTerms.set(b, null);
						break scanOrTerms;
					}

					// rule #2: apply "AB + A(!B) -> A"
					aOperands.removeAll(commonOperands);
					bOperands.removeAll(commonOperands);

					if (!(commonOperands.isEmpty() || aOperands.isEmpty() || bOperands
							.isEmpty())) {
						Operator aLeftover = aOperands.size() == 1 ? (Operator) aOperands
								.get(0) : And.create(aOperands);
						Operator bLeftover = bOperands.size() == 1 ? (Operator) bOperands
								.get(0) : And.create(bOperands);
						if (aLeftover.equals(Not.create(bLeftover))) {
							Operator shared = commonOperands.size() == 1 ? (Operator) commonOperands
									.get(0) : And.create(commonOperands);
							orTerms.set(a, null);
							orTerms.set(b, shared);
							break scanOrTerms;
						}
					}

					// rule #3: "AZ + (!A)BZ -> AZ + BZ"

					// remember at this point aOperands and
					// bOperands already have stripped away
					// commonOperands
					if (aOperands.size() == 1 && bOperands.size() == 2) {
						if (bOperands.remove(Not.create((Operator) aOperands
								.get(0)))) {
							bOperands.addAll(commonOperands);
							orTerms.set(b,
									bOperands.size() == 1 ? bOperands.get(0)
											: And.create(bOperands));
							break scanOrTerms;
						}
					} else if (aOperands.size() == 2 && bOperands.size() == 1) {
						if (aOperands.remove(Not.create((Operator) bOperands
								.get(0)))) {
							aOperands.addAll(commonOperands);
							orTerms.set(a,
									aOperands.size() == 1 ? aOperands.get(0)
											: And.create(aOperands));
							break scanOrTerms;
						}
					}
				}
			}
		}

		Iterator iter = orTerms.iterator();
		while (iter.hasNext()) {
			Object element = iter.next();
			if (element == null)
				iter.remove();
		}

		if (or.getOperands().equals(orTerms))
			return or;

		Collections.sort(orTerms, toStringComparator);
		return orTerms.size() == 1 ? (Operator) orTerms.get(0) : Or
				.create(orTerms);
	}

	static class AndProfile {
		Map<String, Range> variableRanges = new HashMap<>();
		Collection<Operator> andElementsProfiled = new HashSet<>();
		boolean negated = false;

		AndProfile(And and) {
			this(and.getOperands());
		}

		AndProfile(List andOperands) {
			// we'll revisit not equal to statements in a second pass:
			Map<String, List<Not>> notEqualTos = new HashMap<>();

			for (int a = 0; a < andOperands.size(); a++) {
				Operator andOp = (Operator) andOperands.get(a);

				Range newRange = null;
				String attr = null;

				if (andOp instanceof EqualTo
						&& andOp.getOperand(1) instanceof Comparable) {
					EqualTo et = (EqualTo) andOp;
					attr = et.getAttribute();
					Comparable value = (Comparable) et.getValue();
					newRange = new Range(value, value, true, true);
					andElementsProfiled.add(andOp);
				} else if (andOp instanceof GreaterThan) {
					GreaterThan gt = (GreaterThan) andOp;
					attr = gt.getAttribute();
					Comparable value = gt.getValue();
					newRange = new Range(value, null, false, false);
					andElementsProfiled.add(andOp);
				} else if (andOp instanceof Not) {
					Not not = (Not) andOp;
					Operator notOp = not.getOperand(0);
					if (notOp instanceof GreaterThan) {
						GreaterThan gt = (GreaterThan) notOp;
						attr = gt.getAttribute();
						Comparable value = gt.getValue();
						newRange = new Range(null, value, false, true);
						andElementsProfiled.add(andOp);
					} else if (notOp instanceof EqualTo
							&& ((EqualTo) notOp).getValue() instanceof Comparable) {
						EqualTo et = (EqualTo) notOp;
						attr = et.getAttribute();
						List<Not> nots = notEqualTos.get(attr);
						if (nots == null) {
							nots = new ArrayList<>();
							notEqualTos.put(attr, nots);
						}
						nots.add(not);
					}
				}

				if (newRange != null) {
					Range oldRange = variableRanges.get(attr);
					if (oldRange == null) {
						variableRanges.put(attr, newRange);
					} else {
						Range andedRange = oldRange.and(newRange);
						if (andedRange == null) {
							negated = true;
						} else {
							variableRanges.put(attr, andedRange);
						}
					}
				}
			}

			for (Entry<String, Range> rangeEntry : variableRanges.entrySet()) {
				List<Not> l = notEqualTos.get(rangeEntry.getKey());
				if (l != null) {
					Iterator<Not> iter = l.iterator();
					while (iter.hasNext()) {
						Not not = iter.next();
						EqualTo et = (EqualTo) not.getOperand(0);
						Comparable v = (Comparable) et.getValue();

						if (et.getAttribute().equals(rangeEntry.getKey())
								&& !rangeEntry.getValue().contains(v)) {
							// c isn't close to our range so we can ignore it.
							// if "0 <= x <= 10" and "x != 20", the not-equal-to
							// statement is irrelevant.
							iter.remove();
							andElementsProfiled.add(not);
						} else {
							Range r = rangeEntry.getValue();
							if (v.equals(r.getMax()) && r.isIncludeMax()) {
								r = new Range(r.getMin(), r.getMax(),
										r.isIncludeMin(), false);
								andElementsProfiled.add(not);
							}
							if (v.equals(r.getMin()) && r.isIncludeMin()) {
								r = new Range(r.getMin(), r.getMax(), false,
										r.isIncludeMin());
								andElementsProfiled.add(not);
							}
							rangeEntry.setValue(r);
						}
					}
				}
			}
		}

		public List<Operator> createAndOperators() {
			List<Operator> returnValue = new ArrayList<>();
			for (Entry<String, Range> entry : this.variableRanges.entrySet()) {
				String attr = entry.getKey();
				Range r = entry.getValue();
				if (r.getMin() == null && r.getMax() == null) {
					// useless range
				}

				if (r.getMax() != null && r.getMin() != null
						&& r.getMax() == r.getMin()) {
					if (r.isIncludeMax() && r.isIncludeMin()) {
						returnValue.add(new EqualTo(attr, r.getMin()));
						continue;
					} else {
						throw new IllegalStateException(r.toString(attr));
					}
				}

				if (r.getMax() != null) {
					if (!r.isIncludeMax()) {
						// something like "x < max"
						returnValue.add(Not.create(new GreaterThan(attr, r
								.getMax())));
						returnValue
								.add(Not.create(new EqualTo(attr, r.getMax())));
					} else {
						// something like "x <= max"
						returnValue.add(Not.create(new GreaterThan(attr, r
								.getMax())));
					}
				}
				if (r.getMin() != null) {
					if (r.isIncludeMin()) {
						// something like "min <= x"
						throw new IllegalStateException(r.toString(attr));
					}
					// something like "min < x"
					returnValue.add(new GreaterThan(attr, r.getMin()));
				}
			}
			return returnValue;
		}
	}

	protected static Operator simplifyCanonicalAnd(final And and) {
		List andTerms = new ArrayList(and.getOperands());

		Iterator andTermsIter = andTerms.iterator();
		while (andTermsIter.hasNext()) {
			Operator andOp = (Operator) andTermsIter.next();

			// if we have (A * !A), condense to FALSE
			if (andOp instanceof Not) {
				Not n = (Not) andOp;
				Operator inner = n.getOperand(0);
				if (andTerms.contains(inner))
					return FALSE;
			}
		}

		AndProfile profile = new AndProfile(and);
		if (profile.negated)
			return FALSE;
		andTerms.removeAll(profile.andElementsProfiled);

		andTerms.addAll(profile.createAndOperators());

		if (and.getOperands().size() == andTerms.size()
				&& and.getOperands().containsAll(andTerms))
			return and;

		Collections.sort(andTerms, toStringComparator);

		return andTerms.size() == 1 ? (Operator) andTerms.get(0) : And
				.create(andTerms);
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
			Object operand1 = getOperand(a);
			Object operand2 = canonicalOther.getOperand(a);
			if (!Objects.equals(operand1, operand2))
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

	public abstract Collection<String> getAttributes();

	public static Operator join(Operator... operators) {
		Operator op = operators.length == 1 ? operators[0] : Or
				.create(operators);
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
						: Or.create(newOperands);
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
				newOperands.add(Not.create(In.create(attr, values)));
				op = newOperands.size() == 1 ? (Operator) newOperands.get(0)
						: Or.create(newOperands);
				if (op instanceof Or) {
					or = (Or) op;
				} else {
					return op;
				}
			}
		}

		// step 3: reach inside ANDed statements and search for equal-tos and
		// not-equal-tos to similarly rewrite as IN statements. This is much
		// more complex and involves trying to find common factors for clusters
		// of operands.

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

						Operator newAndOp = And.create(newAndTerms);

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
						newAndTerms.add(Not.create(In.create(attr, values)));

						Operator newAndOp = And.create(newAndTerms);

						newOrTerms.addAll((List) or.getOperands());
						newOrTerms.removeAll(notEqualTos.values());
						newOrTerms.add(newAndOp);
					}

					op = newOrTerms.size() == 1 ? (Operator) newOrTerms.get(0)
							: Or.create(newOrTerms);
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
		Collection returnValue = new HashSet();
		for (ValueOperator op : valueOperators) {
			returnValue.add(op.getValue());
		}
		return returnValue;
	}

}
