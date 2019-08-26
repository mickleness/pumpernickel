package com.pump.data.operator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.pump.math.Range;

/**
 * This consolidates logic related to simplifying Operators. This includes some
 * common-sense simplification strategies and some simple boolean algebra.
 */
class CanonicalSimplifier {

	/**
	 * This standardizes the operator and returns an equivalent operator (or the
	 * original argument).
	 * <p>
	 * To evaluate equivalency and hashcodes: it's important that two operators
	 * can recognize each other as equal. For example "x < 10" and "!(x >= 10)"
	 * should convert to a standard form.
	 * <p>
	 * Sometimes what this method does may be considered "simplifying" (it may
	 * remove redundancies, consolidate certain terms, etc.), but sometimes it
	 * may make an operator more verbose/complex.
	 * 
	 * @param op
	 *            the incoming canonical sum of products expression. If this is
	 *            a tree structure of operands, the nodes should be in this
	 *            order: OR, AND, NOT, AbstractValueOperators.
	 * @return an equivalent operator, or the original argument if no change was
	 *         necessary.
	 */
	static Operator simplify(Operator op) {
		if (!op.isCanonical())
			throw new IllegalArgumentException(
					"The argument must be canonical: " + op);
		boolean runAgain;
		do {
			Operator original = op;
			if (op instanceof Or) {
				op = simplifyOr((Or) op);
			} else if (op instanceof And) {
				op = simplifyAnd((And) op);
			}
			runAgain = original != op;
		} while (runAgain);

		return op;
	}

	static class AndProfile {
		@SuppressWarnings("rawtypes")
		Map<String, Range> variableRanges = new HashMap<>();
		Collection<Operator> andElementsProfiled = new HashSet<>();
		boolean negated = false;

		AndProfile(And and) {
			this(and.getOperands());
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
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

		@SuppressWarnings("rawtypes")
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
						returnValue.add(new Not(new GreaterThan(attr, r
								.getMax())));
						returnValue.add(new Not(new EqualTo(attr, r.getMax())));
					} else {
						// something like "x <= max"
						returnValue.add(new Not(new GreaterThan(attr, r
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	static Operator simplifyOr(final Or or) {
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
					Collections.sort(z, Operator.toStringComparator);
					if (z.size() > 0) {
						Operator t = z.size() == 1 ? z.get(0) : new And(z);
						newOrTerms.add(t);
					}
				}
			}

			Collections.sort(newOrTerms, Operator.toStringComparator);

			if (newOrTerms.size() == 0)
				return Operator.TRUE;

			Operator z = newOrTerms.size() == 1 ? newOrTerms.get(0) : new Or(
					newOrTerms);
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
					return Operator.TRUE;
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
							Operator notEqualTo = new Not(new EqualTo(
									g.getAttribute(), g.getValue()));
							if (aOperands.contains(notEqualTo))
								bOperands.add(notEqualTo);
						}
					}
					for (int c = 0; c < aOperands.size(); c++) {
						if (aOperands.get(c) instanceof GreaterThan) {
							GreaterThan g = (GreaterThan) aOperands.get(c);
							Operator notEqualTo = new Not(new EqualTo(
									g.getAttribute(), g.getValue()));
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
								.get(0) : new And(aOperands);
						Operator bLeftover = bOperands.size() == 1 ? (Operator) bOperands
								.get(0) : new And(bOperands);
						if (aLeftover.equals(new Not(bLeftover))) {
							Operator shared = commonOperands.size() == 1 ? (Operator) commonOperands
									.get(0) : new And(commonOperands);
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
						if (bOperands.remove(new Not((Operator) aOperands
								.get(0)))) {
							bOperands.addAll(commonOperands);
							orTerms.set(b,
									bOperands.size() == 1 ? bOperands.get(0)
											: new And(bOperands));
							break scanOrTerms;
						}
					} else if (aOperands.size() == 2 && bOperands.size() == 1) {
						if (aOperands.remove(new Not((Operator) bOperands
								.get(0)))) {
							aOperands.addAll(commonOperands);
							orTerms.set(a,
									aOperands.size() == 1 ? aOperands.get(0)
											: new And(aOperands));
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

		Collections.sort(orTerms, Operator.toStringComparator);
		return orTerms.size() == 1 ? (Operator) orTerms.get(0)
				: new Or(orTerms);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	static Operator simplifyAnd(final And and) {
		List andTerms = new ArrayList(and.getOperands());

		Iterator andTermsIter = andTerms.iterator();
		while (andTermsIter.hasNext()) {
			Operator andOp = (Operator) andTermsIter.next();

			// if we have (A * !A), condense to FALSE
			if (andOp instanceof Not) {
				Not n = (Not) andOp;
				Operator inner = n.getOperand(0);
				if (andTerms.contains(inner))
					return Operator.FALSE;
			}
		}

		AndProfile profile = new AndProfile(and);
		if (profile.negated)
			return Operator.FALSE;
		andTerms.removeAll(profile.andElementsProfiled);

		andTerms.addAll(profile.createAndOperators());

		if (and.getOperands().size() == andTerms.size()
				&& and.getOperands().containsAll(andTerms))
			return and;

		Collections.sort(andTerms, Operator.toStringComparator);

		return andTerms.size() == 1 ? (Operator) andTerms.get(0) : new And(
				andTerms);
	}

	@SuppressWarnings("rawtypes")
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
			if (not.getOperand(0) instanceof ConstantOperator)
				System.currentTimeMillis();
			AbstractValueOperator v = (AbstractValueOperator) not.getOperand(0);
			if (v.getAttribute().equals(attr)) {
				attrTerms.add(not);
			} else {
				otherTerms.add(not);
			}
		} else if (op instanceof AbstractValueOperator) {
			AbstractValueOperator v = (AbstractValueOperator) op;
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
}
