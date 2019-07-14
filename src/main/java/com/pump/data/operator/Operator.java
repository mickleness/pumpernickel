package com.pump.data.operator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;

import com.pump.text.WildcardPattern;
import com.pump.util.CombinationIterator;

public abstract class Operator implements Serializable {
	private static final long serialVersionUID = 1L;

	public interface Context {
		public Object getValue(Object bean, String attributeName);
	}

	/**
	 * This accepts an arbitrary number of operands. This is intended for AND
	 * and OR operators.
	 */
	public static abstract class AbstractCompoundOperator extends Operator {
		private static final long serialVersionUID = 1L;

		private final Operator[] operands;
		private final String operatorString;

		/**
		 * 
		 * @param operatorString
		 *            this is placed between operands when converting this to a
		 *            String. This should be " && " or " || ".
		 * @param operands
		 */
		protected AbstractCompoundOperator(String operatorString,
				Operator... operands) {
			Objects.requireNonNull(operands);
			Objects.requireNonNull(operatorString);
			this.operatorString = operatorString;
			for (int a = 0; a < operands.length; a++) {
				if (operands[a] == null)
					throw new NullPointerException("a = " + a);
			}
			if (operands.length == 0)
				throw new IllegalArgumentException();
			if (operands.length == 1)
				throw new IllegalArgumentException(
						"2 or more operands are required for a "
								+ getClass().getSimpleName() + " operator");
			this.operands = operands;
		}

		@Override
		public int getOperandCount() {
			return operands.length;
		}

		@Override
		public Operator getOperand(int index) {
			return operands[index];
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

		protected Collection<Operator> getFlattenedCanonicalOperands() {
			Comparator<Operator> comparator = new Comparator<Operator>() {

				@Override
				public int compare(Operator o1, Operator o2) {
					return o1.toString().compareTo(o2.toString());
				}

			};
			Collection<Operator> flattenedOperands = new TreeSet<>(comparator);
			loadFlattenedOperands(this, flattenedOperands);
			return flattenedOperands;
		}

		private void loadFlattenedOperands(AbstractCompoundOperator compoundOp,
				Collection<Operator> dest) {
			for (int a = 0; a < compoundOp.getOperandCount(); a++) {
				Operator op = compoundOp.getOperand(a).getCanonicalOperator();
				if (op.getClass() == compoundOp.getClass()) {
					loadFlattenedOperands((AbstractCompoundOperator) op, dest);
				} else {
					dest.add(op);
				}
			}
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

		private transient Collection<Class<?>> validatedClasses;

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

		Operator operand;

		public Not(Operator operand) {
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
		protected Operator createCanonicalOperator() {
			if (operand instanceof Not) {
				Not n = (Not) operand;
				return n.operand.createCanonicalOperator();
			} else if (operand instanceof AbstractCompoundOperator) {
				AbstractCompoundOperator compoundOp = (AbstractCompoundOperator) operand;
				Collection<Operator> c = new ArrayList<>(
						compoundOp.getOperandCount());
				for (int i = 0; i < compoundOp.getOperandCount(); i++) {
					c.add(new Not(compoundOp.getOperand(i)));
				}
				if (operand instanceof And) {
					return new Or(c.toArray(new Operator[c.size()]))
							.createCanonicalOperator();
				} else if (operand instanceof Or) {
					return new And(c.toArray(new Operator[c.size()]))
							.createCanonicalOperator();
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
						returnValue.add(new Not(op));
					}
				}
				return returnValue;
			}

			Collection<Operator> z = getOperand(0).split();

			Collection<Operator> returnValue = new ArrayList<Operator>(z.size());
			for (Operator op : z) {
				returnValue.add(new Not(op));
			}

			return returnValue;
		}
	}

	public static class And extends AbstractCompoundOperator {
		private static final long serialVersionUID = 1L;

		public And(Operator... operands) {
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

		@Override
		protected Operator createCanonicalOperator() {
			Collection<Operator> c = getFlattenedCanonicalOperands();
			return new And(c.toArray(new Operator[c.size()]));
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
				Operator[] w2 = w.toArray(new Operator[w.size()]);
				returnValue.add(new And(w2));
			}

			return returnValue;
		}
	}

	public static class Or extends AbstractCompoundOperator {
		private static final long serialVersionUID = 1L;

		public Or(Operator... operands) {
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

		@Override
		protected Operator createCanonicalOperator() {
			Collection<Operator> c = getFlattenedCanonicalOperands();
			return new Or(c.toArray(new Operator[c.size()]));
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
			// "!(a > 50 || a==50)"
			return new Not(new Or(new EqualTo(getAttribute(), getValue()),
					new GreaterThan(getAttribute(), getValue())))
					.createCanonicalOperator();
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
		@SuppressWarnings({ "unchecked", "rawtypes" })
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
		return getCanonicalOperator().doHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Operator))
			return false;
		Operator canonicalOther = ((Operator) obj).getCanonicalOperator();
		return getCanonicalOperator().doEquals(canonicalOther);
	}

	public Operator getCanonicalOperator() {
		if (canonicalOperator == null)
			canonicalOperator = createCanonicalOperator();
		return canonicalOperator;
	}

	protected int doHashCode() {
		int z = 0;
		for (int a = 0; a < getOperandCount(); a++) {
			z += Objects.hashCode(getOperand(a));
		}
		return z;
	}

	protected boolean doEquals(Operator canonicalOther) {
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

	protected abstract Operator createCanonicalOperator();

	public abstract Collection<Operator> split();
}
