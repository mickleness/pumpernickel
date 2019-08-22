package com.pump.data.operator;

import java.util.Arrays;
import java.util.Collection;

import junit.framework.TestCase;

import org.junit.Test;

import com.pump.text.WildcardPattern;

public class OperatorTest extends TestCase {

	static final String HOUSE_GRYFFINDOR = "Gryffindor";
	static final String HOUSE_HUFFLEPUFF = "Hufflepuff";
	static final String HOUSE_RAVENCLAW = "Ravenclaw";
	static final String HOUSE_SLYTHERIN = "Slytherin";

	public static class StudentBean {

		String firstName, lastName;
		int birthYear;
		String house;

		public StudentBean(String firstName, String lastName, String house,
				int birthYear) {
			this.firstName = firstName;
			this.lastName = lastName;
			this.house = house;
			this.birthYear = birthYear;
		}
	}

	static StudentBean harryPotter = new StudentBean("Harry", "Potter",
			HOUSE_GRYFFINDOR, 1980);
	static StudentBean ronWeasley = new StudentBean("Ron", "Weasley",
			HOUSE_GRYFFINDOR, 1980);
	static StudentBean hermioneGranger = new StudentBean("Hermione", "Granger",
			HOUSE_GRYFFINDOR, 1980);
	static StudentBean lavenderBrown = new StudentBean("Lavender", "Brown",
			HOUSE_GRYFFINDOR, 1979);
	static StudentBean pavartiPatil = new StudentBean("Pavarti", "Patil",
			HOUSE_GRYFFINDOR, 1980);
	static StudentBean padmaPatil = new StudentBean("Padma", "Patil",
			HOUSE_RAVENCLAW, 1980);
	static StudentBean ginnyWeasley = new StudentBean("Ginny", "Weasley",
			HOUSE_GRYFFINDOR, 1981);
	static StudentBean choChang = new StudentBean("Cho", "Chang",
			HOUSE_RAVENCLAW, 1979);
	static StudentBean lunaLovegood = new StudentBean("Luna", "Lovegood",
			HOUSE_RAVENCLAW, 1981);
	static StudentBean nullStudent = new StudentBean(null, null, null, -1);

	static OperatorContext context = new OperatorContext() {

		@Override
		public Object getValue(Object bean, String attributeName) {
			StudentBean s = (StudentBean) bean;
			switch (attributeName) {
			case "firstName":
				return s.firstName;
			case "lastName":
				return s.lastName;
			case "house":
				return s.house;
			case "birthYear":
				return s.birthYear;
			}
			throw new IllegalArgumentException("Unsupported attribute \""
					+ attributeName + "\"");
		}

	};

	@Test
	public void testValidation() {
		// TODO: reimplement validation
		// // // test EqualTo
		//
		// // this (invalid) operator can't be validated on construction...
		// Operator mismatchedDataType1 = new EqualTo("firstName", 1979);
		// try {
		// // ... but it can be validated when you feed it a bean
		// assertFalse(mismatchedDataType1.evaluate(context, choChang));
		// fail();
		// } catch (Exception e) {
		// // success
		// }
		//
		// // prove these do not throw validation errors:
		// new EqualTo(StudentBean.class, "lastName", "Weasley");
		// new EqualTo(StudentBean.class, "birthYear", 1979);
		//
		// // prove this throws a validation error:
		// try {
		// new EqualTo(StudentBean.class, "lastName", 1979);
		// fail();
		// } catch (Exception e) {
		// // success
		// }
		//
		// // prove these throw a validation error:
		// try {
		// new EqualTo(StudentBean.class, "lastName", true);
		// fail();
		// } catch (Exception e) {
		// // success
		// }
		//
		// try {
		// new EqualTo(StudentBean.class, "birthYear", "Weasley");
		// fail();
		// } catch (Exception e) {
		// // success
		// }
		//
		// // // test Like
		//
		// WildcardPattern pStar = new WildcardPattern("P*");
		//
		// // prove this doesn't throw a validation error:
		// new Like(StudentBean.class, "lastName", pStar);
		//
		// // prove this throws a validation error:
		// try {
		// new Like(StudentBean.class, "birthYear", pStar);
		// fail();
		// } catch (Exception e) {
		// // success
		// }
		//
		// // prove this throws a validation error/npe:
		// try {
		// new Like("lastName", null);
		// fail();
		// } catch (Exception e) {
		// // success
		// }
		//
		// // // test LesserThan
		//
		// try {
		// new LesserThan(StudentBean.class, "birthYear", "M");
		// fail();
		// } catch (Exception e) {
		// // success
		// }
		//
		// try {
		// new LesserThan(StudentBean.class, "lastName", 1980);
		// fail();
		// } catch (Exception e) {
		// // success
		// }
		//
		// // ANDs and ORs require at least 2 arguments:
		// try {
		// new And(new LesserThan(StudentBean.class, "birthYear", 1980));
		// fail();
		// } catch (Exception e) {
		// // success
		// }
		//
		// try {
		// new Or(new LesserThan(StudentBean.class, "birthYear", 1980));
		// fail();
		// } catch (Exception e) {
		// // success
		// }
	}

	@Test
	public void testLike() throws Exception {
		WildcardPattern pStar = new WildcardPattern("P*");
		Operator lastNameP = new Like("lastName", pStar);
		assertTrue(lastNameP.evaluate(context, pavartiPatil));
		assertTrue(lastNameP.evaluate(context, padmaPatil));
		assertTrue(lastNameP.evaluate(context, harryPotter));
		assertFalse(lastNameP.evaluate(context, lavenderBrown));
		assertFalse(lastNameP.evaluate(context, hermioneGranger));
		assertFalse(lastNameP.evaluate(context, nullStudent));
	}

	@Test
	public void testEqualTo() throws Exception {
		Operator lastNameWeasley = new EqualTo("lastName", "Weasley");
		assertTrue(lastNameWeasley.evaluate(context, ginnyWeasley));
		assertTrue(lastNameWeasley.evaluate(context, ronWeasley));
		assertFalse(lastNameWeasley.evaluate(context, pavartiPatil));
		assertFalse(lastNameWeasley.evaluate(context, harryPotter));
		assertFalse(lastNameWeasley.evaluate(context, lavenderBrown));
		assertFalse(lastNameWeasley.evaluate(context, hermioneGranger));
		assertFalse(lastNameWeasley.evaluate(context, nullStudent));

		Operator lastNameNull = new EqualTo("lastName", null);
		assertFalse(lastNameNull.evaluate(context, hermioneGranger));
		assertTrue(lastNameNull.evaluate(context, nullStudent));

		Operator year1979 = new EqualTo("birthYear", 1979);
		assertTrue(year1979.evaluate(context, choChang));
		assertFalse(year1979.evaluate(context, hermioneGranger));
		assertFalse(year1979.evaluate(context, nullStudent));
	}

	@Test
	public void testNot() throws Exception {
		Operator lastNameNotWeasley = new Not(
				new EqualTo("lastName", "Weasley"));
		assertFalse(lastNameNotWeasley.evaluate(context, ginnyWeasley));
		assertFalse(lastNameNotWeasley.evaluate(context, ronWeasley));
		assertTrue(lastNameNotWeasley.evaluate(context, pavartiPatil));
		assertTrue(lastNameNotWeasley.evaluate(context, harryPotter));

		Operator lastNameNotNull = new Not(new EqualTo("lastName", null));
		assertTrue(lastNameNotNull.evaluate(context, hermioneGranger));
		assertFalse(lastNameNotNull.evaluate(context, nullStudent));

		// nulls aren't allowed
		try {
			new Not(null);
			fail();
		} catch (Exception e) {
			// success
		}
	}

	@Test
	public void testGreaterThan() throws Exception {
		Operator above1980 = new GreaterThan("birthYear", 1980);
		assertTrue(above1980.evaluate(context, ginnyWeasley));
		assertFalse(above1980.evaluate(context, ronWeasley));
		assertFalse(above1980.evaluate(context, lavenderBrown));

		Operator afterM = new GreaterThan("lastName", "M");
		assertTrue(afterM.evaluate(context, ginnyWeasley));
		assertTrue(afterM.evaluate(context, pavartiPatil));
		assertFalse(afterM.evaluate(context, choChang));
		assertFalse(afterM.evaluate(context, lavenderBrown));
	}

	@Test
	public void testLesserThan() throws Exception {
		Operator before1980 = new LesserThan("birthYear", 1980);
		assertFalse(before1980.evaluate(context, ginnyWeasley));
		assertFalse(before1980.evaluate(context, ronWeasley));
		assertTrue(before1980.evaluate(context, lavenderBrown));

		Operator beforeM = new LesserThan("lastName", "M");
		assertFalse(beforeM.evaluate(context, ginnyWeasley));
		assertFalse(beforeM.evaluate(context, pavartiPatil));
		assertTrue(beforeM.evaluate(context, choChang));
		assertTrue(beforeM.evaluate(context, lavenderBrown));
	}

	@Test
	public void testIn() throws Exception {
		Operator ravenclaw = In.create("house", Arrays.asList(HOUSE_RAVENCLAW));
		assertTrue(ravenclaw.evaluate(context, padmaPatil));
		assertFalse(ravenclaw.evaluate(context, pavartiPatil));
		assertFalse(ravenclaw.evaluate(context, nullStudent));
	}

	@Test
	public void testToString_scenario1() throws Exception {
		Operator ravenclaw = In.create("house", Arrays.asList(HOUSE_RAVENCLAW));
		Operator above1980 = new GreaterThan("birthYear", 1980);
		Operator lastNameNotWeasley = new Not(
				new EqualTo("lastName", "Weasley"));

		Operator or = new Or(ravenclaw, above1980);
		Operator and = new And(lastNameNotWeasley, or);
		String str = "lastName != \"Weasley\" && (house == \"Ravenclaw\" || birthYear > 1980)";
		testToString(and, str);
	}

	@Test
	public void testToString_scenario2() throws Exception {
		Operator ravenclaw = In.create("house", Arrays.asList(HOUSE_RAVENCLAW));
		Operator above1980 = new GreaterThan("birthYear", 1980);
		Operator lastNameNotWeasley = new Not(
				new EqualTo("lastName", "Weasley"));

		Operator notOr = new Not(new Or(ravenclaw, above1980));
		Operator and = new And(lastNameNotWeasley, notOr);
		String str = "lastName != \"Weasley\" && !(house == \"Ravenclaw\" || birthYear > 1980)";
		testToString(and, str);
	}

	@Test
	public void testToString_scenario3() throws Exception {
		Operator ravenclaw = In.create("house", Arrays.asList(HOUSE_RAVENCLAW));
		Operator above1980 = new GreaterThan("birthYear", 1980);
		Operator lastNameWeasley = new EqualTo("lastName", "Weasley");

		Operator notOr = new Not(new Or(ravenclaw, above1980));
		Operator notAnd = new Not(new And(notOr, lastNameWeasley));
		String str = "!(!(house == \"Ravenclaw\" || birthYear > 1980) && lastName == \"Weasley\")";
		testToString(notAnd, str);
	}

	@Test
	public void testToString_scenario4() throws Exception {
		WildcardPattern pStar = new WildcardPattern("P*");
		Operator lastNameP = new Like("lastName", pStar);

		testToString(lastNameP, "matches(lastName, \"P*\")");
		testToString(new Not(lastNameP), "!matches(lastName, \"P*\")");
		testToString(new Not(new Not(lastNameP)),
				"!(!matches(lastName, \"P*\"))");
		testToString(new Not(new Not(new Not(lastNameP))),
				"!(!(!matches(lastName, \"P*\")))");
	}

	@Test
	public void testToString_scenario5() throws Exception {
		WildcardPattern lStar = new WildcardPattern("L*");
		Operator firstNameL = new Like("firstName", lStar);
		Operator ravenclaw = In.create("house", Arrays.asList(HOUSE_RAVENCLAW));
		Operator above1980 = new GreaterThan("birthYear", 1980);
		Operator lastNameWeasley = new EqualTo("lastName", "Weasley");

		Operator and1 = new And(firstNameL, ravenclaw);
		Operator and2 = new And(above1980, lastNameWeasley);
		Operator or = new Or(and1, and2);
		String str = "(matches(firstName, \"L*\") && house == \"Ravenclaw\") || (birthYear > 1980 && lastName == \"Weasley\")";
		testToString(or, str);
	}

	@Test
	public void testToString_scenario6() throws Exception {
		Operator op = In.create("house",
				Arrays.asList(HOUSE_HUFFLEPUFF, HOUSE_SLYTHERIN));
		testToString(op, "contains(house, {\"Hufflepuff\", \"Slytherin\"})");

		Operator op2 = new EqualTo("birthYear", 1980);
		Operator and = new And(op, op2);
		testToString(and,
				"contains(house, {\"Hufflepuff\", \"Slytherin\"}) && birthYear == 1980");

		Operator or = new Or(op2, op);
		testToString(or,
				"birthYear == 1980 || contains(house, {\"Hufflepuff\", \"Slytherin\"})");
	}

	/**
	 * Confirm that an Operator's toString() method produces certain output, and
	 * that that String can be parsed to recreate the original Operator.
	 */
	private void testToString(Operator op, String str) throws Exception {
		String z = op.toString();
		assertEquals(str, z);
		assertEquals(op, new OperatorParser().parse(str));
	}

	/**
	 * Confirm that the order we present AND operations in doesn't matter.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testEquals_scenario1() throws Exception {

		testEquals("(a && b) && (c && d)", "(a && d) && (c && b)");

		testEquals(
				"(matches(firstName, \"L*\") && house == \"Ravenclaw\") && (birthYear > 1980 && lastName == \"Weasley\")",
				"(matches(firstName, \"L*\") && lastName == \"Weasley\") && (birthYear > 1980 && house == \"Ravenclaw\")");

		testEquals(
				false,
				"(matches(firstName, \"L*\") && house == \"Ravenclaw\") && (birthYear > 1980 && lastName == \"Weasley\")",
				"(matches(firstName, \"L*\") && lastName == \"Granger\") && (birthYear > 1980 && house == \"Ravenclaw\")",
				null);

		testEquals(
				false,
				"(matches(firstName, \"L*\") && lastName == \"Weasley\") && (birthYear > 1980 && house == \"Ravenclaw\")",
				"(matches(firstName, \"L*\") && lastName == \"Granger\") && (birthYear > 1980 && house == \"Ravenclaw\")",
				null);
	}

	@Test
	public void testEquals_scenario2() throws Exception {
		WildcardPattern lStar = new WildcardPattern("L*");
		Operator firstNameL = new Like("firstName", lStar);
		Operator ravenclaw = In.create("house", Arrays.asList(HOUSE_RAVENCLAW));
		Operator above1980 = new GreaterThan("birthYear", 1980);
		Operator lastNameWeasley = new EqualTo("lastName", "Weasley");

		Operator a1 = new And(firstNameL, ravenclaw);
		Operator a2 = new Or(new Not(a1), above1980, lastNameWeasley);

		Operator b1 = new Or(new Not(firstNameL), new Not(ravenclaw));
		Operator b2 = new Or(b1, above1980, lastNameWeasley);

		assertEquals(a2, b2);
		assertEquals(b2, a2);

		Operator c1 = new Or(lastNameWeasley, new Not(firstNameL), new Not(
				ravenclaw));
		Operator c2 = new Or(c1, above1980);

		assertEquals(a2, c2);
		assertEquals(c2, a2);

		Operator d = new Or(above1980, lastNameWeasley, new Not(firstNameL),
				new Not(ravenclaw));
		assertEquals(a2, d);
		assertEquals(d, a2);

		Operator e = new Or(above1980, lastNameWeasley, firstNameL, new Not(
				ravenclaw));
		assertFalse(a2.equals(e));
		assertFalse(e.equals(a2));

		Operator f = new Or(above1980, new Not(firstNameL), new Not(ravenclaw));
		assertFalse(a2.equals(f));
		assertFalse(f.equals(a2));

		Operator g = new And(above1980, lastNameWeasley, new Not(firstNameL),
				new Not(ravenclaw));
		assertFalse(a2.equals(g));
		assertFalse(g.equals(a2));
	}

	@Test
	public void testEquals_scenario3() throws Exception {
		WildcardPattern lStar = new WildcardPattern("L*");
		WildcardPattern starL = new WildcardPattern("*l");

		Operator a_firstNameL = new Like("firstName", lStar);
		Operator b_firstNameL = new Like("firstName", lStar);

		assertEquals(a_firstNameL, b_firstNameL);
		assertEquals(b_firstNameL, a_firstNameL);

		Operator c_firstNameL = new Like("lastName", lStar);

		assertFalse(a_firstNameL.equals(c_firstNameL));
		assertFalse(c_firstNameL.equals(a_firstNameL));

		Operator d_firstNameL = new Like("firstName", starL);

		assertFalse(a_firstNameL.equals(d_firstNameL));
		assertFalse(d_firstNameL.equals(a_firstNameL));

		Operator e = new Not(a_firstNameL);

		assertFalse(a_firstNameL.equals(e));
		assertFalse(e.equals(a_firstNameL));

		Operator f = new Not(a_firstNameL);

		assertEquals(f, e);
		assertEquals(e, f);

		Operator g = new Not(new Not(a_firstNameL));

		assertEquals(a_firstNameL, g);
		assertEquals(g, a_firstNameL);

		assertFalse(a_firstNameL.equals(f));
		assertFalse(f.equals(a_firstNameL));
	}

	protected void testEquals(String expected, String value) throws Exception {
		testEquals(true, expected, value, null);
	}

	protected void testEquals(boolean equals, String expected, String value,
			WildcardPattern.Format format) throws Exception {
		if (format == null)
			format = new WildcardPattern.Format();

		// adding an AND or NOT-AND to the system may change a lot of
		// under-the-hood mechanics when we factor and simplify expressions to
		// determine equivalency, so we'll add a little variation here:
		String[][] dressings = new String[][] { { "", "" }, { "j && (", ")" },
				{ "j || (", ")" }, { "j && !(", ")" }, { "j || !(", ")" } };

		for (String[] dressing : dressings) {
			String expected_revised = dressing[0] + expected + dressing[1];
			String value_revised = dressing[0] + value + dressing[1];

			testOperatorString(expected_revised);
			testOperatorString(value_revised);

			Operator op1 = new OperatorParser(format).parse(expected_revised);
			Operator op1_copy = new OperatorParser(format)
					.parse(expected_revised);
			Operator op2 = new OperatorParser(format).parse(value_revised);
			Operator op2_copy = new OperatorParser(format).parse(value_revised);

			if (equals) {
				assertEquals(op1, op2);
				assertEquals(op2, op1);
			} else {
				assertFalse(op1.equals(op2));
			}

			// test strict equivalency
			assertTrue(op1.equals(op1_copy, true));
			assertTrue(op2.equals(op2_copy, true));

			assertFalse(op1.equals(op2, true));
			assertFalse(op2.equals(op1, true));
		}
	}

	/**
	 * This tests some basic identity properties of an expression. For ex:
	 * "A && !A" should equal "false". Also "!(!(A))" should equal A.
	 * 
	 * @param str
	 * @throws Exception
	 */
	protected void testOperatorString(String str) throws Exception {
		{
			Operator op = new OperatorParser().parse(str + "&& !(" + str + ")");
			assertEquals(Operator.FALSE, op);
		}

		{
			Operator op = new OperatorParser().parse(str);
			Operator not = new Not(op).getCanonicalOperator();
			Operator notNot = new Not(not).getCanonicalOperator();
			assertEquals(op, notNot);
		}

		// TODO this currently fails for one expression:
		// Operator op5 = new OperatorParser().parse(str + " || !("
		// + str + ")");
		// assertEquals(Operator.TRUE, op5);
	}

	@Test
	public void testEquals_scenario4() throws Exception {
		testEquals("x < 10", "!(x>10) && !(x==10)");
		testEquals("x > 10", "!(x < 10 || x==10)");
		testEquals("(x==10) || (x>10)", "!(x < 10)");
		testEquals("(x == 10) || (x < 10)", "!(x > 10)");
		testEquals("false", "x > 10 && x < 10");
		testEquals("x == 10", "!(x > 10) && !(x < 10)");
		testEquals("x != 10", "x > 10 || x < 10");
		testEquals("x > 20", "x > 10 && x > 20");
		testEquals("x > 10", "x > 10 || x > 20");
		testEquals("true", "x > 10 || !(x > 10)");
		testEquals("x > 10", "x != 0 && x > 10");
		testEquals("false", "x == 0 && x > 10");
		testEquals("x == 20", "x == 20 && x > 10");
		testEquals("false", "x == 20 && x < 10");
		testEquals("x < 10", "x != 20 && x < 10");
		testEquals("x < 10", "x < 10 && x < 20");
		testEquals("false", "x < 10 && x == 10");
		testEquals("false", "x > 10 && x == 10");
		testEquals("x > 10", "x > 10 && x != 10");
		testEquals("x < 20", "x < 10 || x < 20");
		testEquals("x > 10", "x > 10 || x > 20");
		testEquals("false", "x > 10 && x < -10");
		testEquals("!(!((a && b) || (c && d)))", "(a && b) || (c && d)");
	}

	@Test
	public void testEquals_scenario5_overlappingRanges() throws Exception {

		testEquals("x > 2 && x < 3", "(x > 0 && x < 3) && (x > 2 && x < 5)");
		testEquals("x > 0 && x < 5", "(x > 0 && x < 3) || (x > 2 && x < 5)");

		testEquals("x > 2 && x < 3", "(x > 0 && x < 3) && (x > 2 && x <= 5)");
		testEquals("x > 0 && x <= 5", "(x > 0 && x < 3) || (x > 2 && x <= 5)");

		testEquals("x >= 2 && x < 3", "(x > 0 && x < 3) && (x >= 2 && x < 5)");
		testEquals("x > 0 && x < 5", "(x > 0 && x < 3) || (x >= 2 && x < 5)");

		testEquals("x >= 2 && x < 3", "(x > 0 && x < 3) && (x >= 2 && x <= 5)");
		testEquals("x > 0 && x <= 5", "(x > 0 && x < 3) || (x >= 2 && x <= 5)");

		testEquals("x > 2 && x <= 3", "(x > 0 && x <= 3) && (x > 2 && x < 5)");
		testEquals("x > 0 && x < 5", "(x > 0 && x <= 3) || (x > 2 && x < 5)");

		testEquals("x > 2 && x <= 3", "(x > 0 && x <= 3) && (x > 2 && x <= 5)");
		testEquals("x > 0 && x <= 5", "(x > 0 && x <= 3) || (x > 2 && x <= 5)");

		testEquals("x >= 2 && x <= 3", "(x > 0 && x <= 3) && (x >= 2 && x < 5)");
		testEquals("x > 0 && x < 5", "(x > 0 && x <= 3) || (x >= 2 && x < 5)");

		testEquals("x >= 2 && x <= 3",
				"(x > 0 && x <= 3) && (x >= 2 && x <= 5)");
		testEquals("x > 0 && x <= 5", "(x > 0 && x <= 3) || (x >= 2 && x <= 5)");

		testEquals("x > 2 && x < 3", "(x >= 0 && x < 3) && (x > 2 && x < 5)");
		testEquals("x >= 0 && x < 5", "(x >= 0 && x < 3) || (x > 2 && x < 5)");

		testEquals("x > 2 && x < 3", "(x >= 0 && x < 3) && (x > 2 && x <= 5)");
		testEquals("x >= 0 && x <= 5", "(x >= 0 && x < 3) || (x > 2 && x <= 5)");

		testEquals("x >= 2 && x < 3", "(x >= 0 && x < 3) && (x >= 2 && x < 5)");
		testEquals("x >= 0 && x < 5", "(x >= 0 && x < 3) || (x >= 2 && x < 5)");

		testEquals("x >= 2 && x < 3", "(x >= 0 && x < 3) && (x >= 2 && x <= 5)");
		testEquals("x >= 0 && x <= 5",
				"(x >= 0 && x < 3) || (x >= 2 && x <= 5)");

		testEquals("x > 2 && x <= 3", "(x >= 0 && x <= 3) && (x > 2 && x < 5)");
		testEquals("x >= 0 && x < 5", "(x >= 0 && x <= 3) || (x > 2 && x < 5)");

		testEquals("x > 2 && x <= 3", "(x >= 0 && x <= 3) && (x > 2 && x <= 5)");
		testEquals("x >= 0 && x <= 5",
				"(x >= 0 && x <= 3) || (x > 2 && x <= 5)");

		testEquals("x >= 2 && x <= 3",
				"(x >= 0 && x <= 3) && (x >= 2 && x < 5)");
		testEquals("x >= 0 && x < 5", "(x >= 0 && x <= 3) || (x >= 2 && x < 5)");

		testEquals("x >= 2 && x <= 3",
				"(x >= 0 && x <= 3) && (x >= 2 && x <= 5)");
		testEquals("x >= 0 && x <= 5",
				"(x >= 0 && x <= 3) || (x >= 2 && x <= 5)");
	}

	@Test
	public void testEquals_scenario7_overlappingInfiniteRanges()
			throws Exception {

		// test the left interval spanning to -inf

		testEquals("x > 2 && x < 3", "(x < 3) && (x > 2 && x < 5)");
		testEquals("x < 5", "(x < 3) || (x > 2 && x < 5)");

		testEquals("x > 2 && x <= 3", "(x <= 3) && (x > 2 && x < 5)");
		testEquals("x < 5", "(x <= 3) || (x > 2 && x < 5)");

		testEquals("x >= 2 && x < 3", "(x < 3) && (x >= 2 && x < 5)");
		testEquals("x < 5", "(x < 3) || (x >= 2 && x < 5)");

		testEquals("x > 2 && x < 3", "(x < 3) && (x > 2 && x <= 5)");
		testEquals("x <= 5", "(x < 3) || (x > 2 && x <= 5)");

		testEquals("x >= 2 && x <= 3", "(x <= 3) && (x >= 2 && x < 5)");
		testEquals("x < 5", "(x <= 3) || (x >= 2 && x < 5)");

		testEquals("x > 2 && x <= 3", "(x <= 3) && (x > 2 && x <= 5)");
		testEquals("x <= 5", "(x <= 3) || (x > 2 && x <= 5)");

		testEquals("x >= 2 && x < 3", "(x < 3) && (x >= 2 && x <= 5)");
		testEquals("x <= 5", "(x < 3) || (x >= 2 && x <= 5)");

		testEquals("x >= 2 && x <= 3", "(x <= 3) && (x >= 2 && x <= 5)");
		testEquals("x <= 5", "(x <= 3) || (x >= 2 && x <= 5)");

		// now test the right interval spanning to +inf

		testEquals("x > 2 && x < 3", "(x > 0 && x < 3) && (x > 2)");
		testEquals("x > 0", "(x > 0 && x < 3) || (x > 2)");

		testEquals("x >= 2 && x < 3", "(x > 0 && x < 3) && (x >= 2)");
		testEquals("x > 0", "(x > 0 && x < 3) || (x >= 2)");

		testEquals("x > 2 && x <= 3", "(x > 0 && x <= 3) && (x > 2)");
		testEquals("x > 0", "(x > 0 && x <= 3) || (x > 2)");

		testEquals("x >= 2 && x <= 3", "(x > 0 && x <= 3) && (x >= 2)");
		testEquals("x > 0", "(x > 0 && x <= 3) || (x >= 2)");

		testEquals("x > 2 && x < 3", "(x >= 0 && x < 3) && (x > 2)");
		testEquals("x >= 0", "(x >= 0 && x < 3) || (x > 2)");

		testEquals("x >= 2 && x < 3", "(x >= 0 && x < 3) && (x >= 2)");
		testEquals("x >= 0", "(x >= 0 && x < 3) || (x >= 2)");

		testEquals("x > 2 && x <= 3", "(x >= 0 && x <= 3) && (x > 2)");
		testEquals("x >= 0", "(x >= 0 && x <= 3) || (x > 2)");

		testEquals("x >= 2 && x <= 3", "(x >= 0 && x <= 3) && (x >= 2)");
		testEquals("x >= 0", "(x >= 0 && x <= 3) || (x >= 2)");
	}

	@Test
	public void testEquals_scenario8_contains() throws Exception {
		testEquals(
				"contains(x, {\"Slytherin\", \"Gryffindor\", \"Ravenclaw\"})",
				"contains(x, {\"Ravenclaw\", \"Gryffindor\", \"Slytherin\"})");
		testEquals(
				"false",
				"contains(x, {\"Gryffindor\", \"Slytherin\"}) && contains(x, {\"Ravenclaw\", \"Hufflepuff\"})");
		testEquals(
				"x == \"Ravenclaw\"",
				"contains(x, {\"Gryffindor\", \"Ravenclaw\"}) && contains(x, {\"Ravenclaw\", \"Hufflepuff\"})");
		testEquals(
				"false",
				"contains(x, {\"Gryffindor\", \"Ravenclaw\"}) && !contains(x, {\"Ravenclaw\", \"Gryffindor\"})");
		testEquals(
				"x == \"Hufflepuff\"",
				"contains(x, {\"Gryffindor\", \"Ravenclaw\", \"Hufflepuff\"}) && !contains(x, {\"Ravenclaw\", \"Gryffindor\"})");

		testEquals(
				"contains(x, {\"Gryffindor\", \"Slytherin\", \"Hufflepuff\", \"Ravenclaw\"})",
				"contains(x, {\"Gryffindor\", \"Slytherin\"}) || contains(x, {\"Ravenclaw\", \"Hufflepuff\"})");
	}

	/**
	 * This confirms split() for Like operators are converted to EqualTo
	 * operators.
	 */
	@Test
	public void testSplit_scenario1() {
		Operator houseIn = In.create("house",
				Arrays.asList(HOUSE_RAVENCLAW, HOUSE_HUFFLEPUFF));

		Collection<Operator> n1 = houseIn.split();
		assertEquals(2, n1.size());
		assertTrue(n1.contains(new EqualTo("house", HOUSE_RAVENCLAW)));
		assertTrue(n1.contains(new EqualTo("house", HOUSE_HUFFLEPUFF)));

		Operator[] n2 = n1.toArray(new Operator[n1.size()]);
		Operator joined = Operator.join(n2);
		assertEquals(joined, houseIn);
	}

	/**
	 * This is a variation of scenario1 that includes a Not modifier.
	 */
	@Test
	public void testSplit_scenario2() {
		Operator houseIn = In.create("house",
				Arrays.asList(HOUSE_RAVENCLAW, HOUSE_HUFFLEPUFF));

		Operator op = new Not(houseIn);
		Collection<Operator> n1 = op.split();
		assertEquals(2, n1.size());
		assertTrue(n1.contains(new Not(new EqualTo("house", HOUSE_RAVENCLAW))));
		assertTrue(n1.contains(new Not(new EqualTo("house", HOUSE_HUFFLEPUFF))));

		Operator[] n2 = n1.toArray(new Operator[n1.size()]);
		Operator joined = Operator.join(n2);
		assertEquals(joined, op);
	}

	/**
	 * This tests split() for a a simple tree of 4 OR'ed operators.
	 */
	@Test
	public void testSplit_scenario3() {
		WildcardPattern lStar = new WildcardPattern("L*");
		Operator firstNameL = new Like("firstName", lStar);
		Operator ravenclawIn = In.create("house",
				Arrays.asList(HOUSE_RAVENCLAW));
		Operator ravenclawEqual = new EqualTo("house", HOUSE_RAVENCLAW);
		Operator above1980 = new GreaterThan("birthYear", 1980);
		Operator lastNameWeasley = new EqualTo("lastName", "Weasley");

		Operator or1 = new Or(new Not(firstNameL), ravenclawIn);
		Operator or2 = new Not(new Or(above1980, lastNameWeasley));
		Operator or3 = new Or(or1, or2);

		Collection<Operator> n1 = or3.split();
		assertEquals(3, n1.size());
		assertTrue(n1.contains(new Not(firstNameL)));
		assertTrue(n1.contains(ravenclawEqual));
		assertTrue(n1.contains(new And(new Not(above1980), new Not(
				lastNameWeasley))));

		Operator[] n2 = n1.toArray(new Operator[n1.size()]);
		Operator joined = Operator.join(n2);
		assertEquals(joined, or3);
	}

	/**
	 * This is a variation of scenario3 that uses four ANDs instead of ORs
	 */
	@Test
	public void testSplit_scenario4() {
		WildcardPattern lStar = new WildcardPattern("L*");
		Operator firstNameL = new Like("firstName", lStar);
		Operator ravenclawIn = In.create("house",
				Arrays.asList(HOUSE_RAVENCLAW));
		Operator ravenclawEqual = new EqualTo("house", HOUSE_RAVENCLAW);
		Operator above1980 = new GreaterThan("birthYear", 1980);
		Operator lastNameWeasley = new EqualTo("lastName", "Weasley");

		Operator and1 = new And(new Not(firstNameL), ravenclawIn);
		Operator and2 = new And(above1980, lastNameWeasley);
		Operator and3 = new And(and1, and2);

		Collection<Operator> n1 = and3.split();
		assertEquals(1, n1.size());
		assertTrue(n1.contains(new And(new Not(firstNameL), ravenclawEqual,
				above1980, lastNameWeasley)));

		Operator[] n2 = n1.toArray(new Operator[n1.size()]);
		Operator joined = Operator.join(n2);
		assertEquals(joined, and3);
	}

	/**
	 * This includes a negated AND which must be converted to 2 elements and an
	 * IN with 2 elements.
	 */
	@Test
	public void testSplit_scenario5() {
		WildcardPattern lStar = new WildcardPattern("L*");
		Operator firstNameL = new Like("firstName", lStar);
		Operator ravenclawSlytherinIn = In.create("house",
				Arrays.asList(HOUSE_RAVENCLAW, HOUSE_SLYTHERIN));
		Operator above1980 = new GreaterThan("birthYear", 1980);
		Operator lastNameWeasley = new EqualTo("lastName", "Weasley");

		Operator and1 = new And(new Not(firstNameL), ravenclawSlytherinIn);
		Operator and2 = new Not(new And(above1980, lastNameWeasley));
		Operator and3 = new And(and1, and2);

		Operator ravenclawEqual = new EqualTo("house", HOUSE_RAVENCLAW);
		Operator slytherinEqual = new EqualTo("house", HOUSE_SLYTHERIN);

		Collection<Operator> n1 = and3.split();
		assertEquals(4, n1.size());
		assertTrue(n1.contains(new And(new Not(firstNameL), ravenclawEqual,
				new Not(above1980))));
		assertTrue(n1.contains(new And(new Not(firstNameL), ravenclawEqual,
				new Not(lastNameWeasley))));
		assertTrue(n1.contains(new And(new Not(firstNameL), slytherinEqual,
				new Not(above1980))));
		assertTrue(n1.contains(new And(new Not(firstNameL), slytherinEqual,
				new Not(lastNameWeasley))));

		Operator[] n2 = n1.toArray(new Operator[n1.size()]);
		Operator joined = Operator.join(n2);
		assertEquals(and3, joined);
	}

	@Test
	public void testSplit_scenario6() {
		WildcardPattern lStar = new WildcardPattern("L*");
		Operator firstNameL = new Like("firstName", lStar);
		Operator ravenclawSlytherinIn = In.create("house",
				Arrays.asList(HOUSE_RAVENCLAW, HOUSE_SLYTHERIN));
		Operator above1980 = new GreaterThan("birthYear", 1980);
		Operator lastNameWeasley = new EqualTo("lastName", "Weasley");

		Operator x = new Or(firstNameL, ravenclawSlytherinIn);
		Operator y = new And(above1980, lastNameWeasley);
		Operator z = new Or(x, y);

		Operator ravenclawEqual = new EqualTo("house", HOUSE_RAVENCLAW);
		Operator slytherinEqual = new EqualTo("house", HOUSE_SLYTHERIN);

		Collection<Operator> n1 = z.split();
		assertEquals(4, n1.size());
		assertTrue(n1.contains(firstNameL));
		assertTrue(n1.contains(ravenclawEqual));
		assertTrue(n1.contains(slytherinEqual));
		assertTrue(n1.contains(new And(above1980, lastNameWeasley)));

		Operator[] n2 = n1.toArray(new Operator[n1.size()]);
		Operator joined = Operator.join(n2);
		assertEquals(joined, z);
	}

	@Test
	public void testSplit_scenario7() {
		WildcardPattern lStar = new WildcardPattern("L*");
		Operator firstNameL = new Like("firstName", lStar);
		Operator ravenclawSlytherinIn = In.create("house",
				Arrays.asList(HOUSE_RAVENCLAW, HOUSE_SLYTHERIN));
		Operator above1980 = new GreaterThan("birthYear", 1980);
		Operator lastNameWeasley = new EqualTo("lastName", "Weasley");

		Operator x = new And(firstNameL, ravenclawSlytherinIn);
		Operator y = new And(above1980, lastNameWeasley);
		Operator z = new Or(x, y);

		Operator ravenclawEqual = new EqualTo("house", HOUSE_RAVENCLAW);
		Operator slytherinEqual = new EqualTo("house", HOUSE_SLYTHERIN);

		Collection<Operator> n1 = z.split();
		assertEquals(3, n1.size());
		assertTrue(n1.contains(new And(firstNameL, ravenclawEqual)));
		assertTrue(n1.contains(new And(firstNameL, slytherinEqual)));
		assertTrue(n1.contains(new And(above1980, lastNameWeasley)));

		Operator[] n2 = n1.toArray(new Operator[n1.size()]);
		Operator joined = Operator.join(n2);
		assertEquals(z, joined);
	}

	@Test
	public void testSplit_scenario8() {
		WildcardPattern lStar = new WildcardPattern("L*");
		Operator firstNameL = new Like("firstName", lStar);
		Operator ravenclawSlytherinIn = In.create("house",
				Arrays.asList(HOUSE_RAVENCLAW, HOUSE_SLYTHERIN));
		Operator above1980 = new GreaterThan("birthYear", 1980);
		Operator lastNameWeasley = new EqualTo("lastName", "Weasley");

		Operator x = new Or(firstNameL, ravenclawSlytherinIn);
		Operator y = new Or(above1980, lastNameWeasley);
		Operator z = new And(x, y);

		Operator ravenclawEqual = new EqualTo("house", HOUSE_RAVENCLAW);
		Operator slytherinEqual = new EqualTo("house", HOUSE_SLYTHERIN);

		Collection<Operator> n1 = z.split();
		assertEquals(6, n1.size());
		assertTrue(n1.contains(new And(firstNameL, above1980)));
		assertTrue(n1.contains(new And(firstNameL, lastNameWeasley)));
		assertTrue(n1.contains(new And(ravenclawEqual, above1980)));
		assertTrue(n1.contains(new And(ravenclawEqual, lastNameWeasley)));
		assertTrue(n1.contains(new And(slytherinEqual, above1980)));
		assertTrue(n1.contains(new And(slytherinEqual, lastNameWeasley)));

		Operator[] n2 = n1.toArray(new Operator[n1.size()]);
		Operator joined = Operator.join(n2);
		assertEquals(z, joined);
	}

	/**
	 * This confirms that Ins are joined together correctly.
	 */
	@Test
	public void testSplit_scenario9() {
		Operator ravenclawSlytherinIn = In.create("house",
				Arrays.asList(HOUSE_RAVENCLAW, HOUSE_SLYTHERIN));
		Operator hufflepuffIn = In.create("house",
				Arrays.asList(HOUSE_HUFFLEPUFF));

		Operator or = new Or(ravenclawSlytherinIn, hufflepuffIn);
		Collection<Operator> n1 = or.split();
		assertEquals(3, n1.size());
		assertTrue(n1.contains(new EqualTo("house", HOUSE_RAVENCLAW)));
		assertTrue(n1.contains(new EqualTo("house", HOUSE_HUFFLEPUFF)));
		assertTrue(n1.contains(new EqualTo("house", HOUSE_SLYTHERIN)));

		Operator[] n2 = n1.toArray(new Operator[n1.size()]);
		Operator joined = Operator.join(n2);
		assertEquals(joined, In.create("house", Arrays.asList(HOUSE_RAVENCLAW,
				HOUSE_SLYTHERIN, HOUSE_HUFFLEPUFF)));
	}

	@Test
	public void testSplit_scenario10() {
		Operator notRavenclawSlytherinIn = new Not(In.create("house",
				Arrays.asList(HOUSE_RAVENCLAW, HOUSE_SLYTHERIN)));
		Operator hufflepuffGryffindorIn = In.create("house",
				Arrays.asList(HOUSE_HUFFLEPUFF, HOUSE_GRYFFINDOR));

		Operator above1980 = new GreaterThan("birthYear", 1980);
		Operator lastNameWeasley = new EqualTo("lastName", "Weasley");

		Operator c = new And(above1980, notRavenclawSlytherinIn);
		Operator d = new And(lastNameWeasley, hufflepuffGryffindorIn);

		Operator notRavenclawEqual = new Not(new EqualTo("house",
				HOUSE_RAVENCLAW));
		Operator notSlytherinEqual = new Not(new EqualTo("house",
				HOUSE_SLYTHERIN));

		Operator hufflepuffEqual = new EqualTo("house", HOUSE_HUFFLEPUFF);
		Operator gryffindorEqual = new EqualTo("house", HOUSE_GRYFFINDOR);

		Operator or = new Or(c, d);
		Collection<Operator> n1 = or.split();
		assertEquals(4, n1.size());
		assertTrue(n1.contains(new And(above1980, notSlytherinEqual)));
		assertTrue(n1.contains(new And(above1980, notRavenclawEqual)));
		assertTrue(n1.contains(new And(lastNameWeasley, hufflepuffEqual)));
		assertTrue(n1.contains(new And(lastNameWeasley, gryffindorEqual)));

		Operator[] n2 = n1.toArray(new Operator[n1.size()]);
		Operator joined = Operator.join(n2);
		assertEquals(or, joined);
	}

	/**
	 * This tests how expressions are simplified.
	 * <p>
	 * Some specific examples used here are from
	 * http://electronics-course.com/boolean-algebra /
	 * https://www.youtube.com/watch?v=59BbncMjL8I
	 */
	@Test
	public void testSimplify() {
		Operator a = new EqualTo("a", 0);
		Operator b = new EqualTo("b", 0);
		Operator c = new EqualTo("c", 0);
		Operator d = new EqualTo("d", 0);
		Operator e = new EqualTo("e", 0);

		// first the basic stuff:
		{
			Operator x = new Or(a, b, a, b, a);
			assertEquals(new Or(a, b), x);
		}

		{
			Operator x = new And(a, b, a, b, a);
			assertEquals(new And(a, b), x);
		}

		{
			Operator x = new And(a, new Not(a));
			assertEquals(Operator.FALSE, x);
		}

		{
			Operator x = new And(b, a, new Not(a));
			assertEquals(Operator.FALSE, x);
		}

		{
			Operator x = new Or(a, new Not(a));
			assertEquals(Operator.TRUE, x);
		}

		{
			Operator x = new Or(b, a, new Not(a));
			assertEquals(Operator.TRUE, x);
		}

		{
			Operator x = new Or(b, new And(a, new Not(a)));
			assertEquals(b, x);
		}

		{
			Operator x = new Or(b, c, new And(a, new Not(a)));
			assertEquals(new Or(b, c), x);
		}

		{
			Operator x = new Or(b, new And(c, a, new Not(a)));
			assertEquals(b, x);
		}

		{
			Operator x = new Or(new And(a, b), new Not(new And(a, b)));
			assertEquals(Operator.TRUE, x);
		}

		{
			Operator x = new And(new Or(a, b), new Not(new Or(a, b)));
			assertEquals(Operator.FALSE, x);
		}

		// from the exercises in the boolean logic tutorial:

		{
			Operator x = new Or(new And(a, b), new And(a, new Or(b, c)),
					new And(b, new Or(b, c)));
			Operator simplified = new Or(b, new And(a, c));
			assertEquals(x, simplified);
		}

		{
			Operator x = new Or(new And(new Not(a), new Not(b), new Not(c)),
					new And(new Not(a), b, c), new And(a, new Not(b),
							new Not(c)), new And(a, new Not(b), c), new And(a,
							b, c));
			Operator simplified = new Or(new And(a, new Not(b)), new And(
					new Not(b), new Not(c)), new And(b, c));
			assertEquals(x, simplified);
		}

		{
			Operator x = new And(new Or(a, new Not(a)), new Or(new And(a, b),
					new And(a, b, new Not(c))));
			Operator simplified = new And(a, b);
			assertEquals(x, simplified);
		}

		// expression #5:
		{
			Operator x = new Or(new And(b, c), new And(new Not(b), c));
			assertEquals(x, c);
		}

		// expression #6:
		{
			Operator x = new Or(new And(new Not(a), b), new And(b, new Not(a),
					new Not(c)), new And(b, c, d, new Not(a)), new And(b, e,
					new Not(a), new Not(c), new Not(d)));
			Operator simplified = new And(new Not(a), b);
			assertEquals(x, simplified);
		}

		// I couldn't follow expression #7 (off-balance parentheses?)

		// expression #8:
		{
			Operator x = new Or(new And(a, new Not(b), c), new And(new Not(a),
					b, c), new And(new Not(a), new Not(b), c));
			Operator simplified = new Or(new And(new Not(a), c), new And(
					new Not(b), c));
			assertEquals(x, simplified);
		}

	}

	@Test
	public void testEquals_like() throws Exception {
		WildcardPattern.Format format = new WildcardPattern.Format();
		format.caseSensitive = true;
		testEquals(true, "matches(x, \"HELLO\")", "x == \"HELLO\"", format);
		testEquals(true, "matches(x, \"hello\")", "x == \"hello\"", format);
		testEquals(false, "matches(x, \"HELLO\")", "x == \"hello\"", format);
		testEquals(false, "matches(x, \"hello\")", "x == \"HELLO\"", format);

		format.caseSensitive = false;
		testEquals(false, "matches(x, \"HELLO\")", "x == \"HELLO\"", format);

		testEquals("x != null", "matches(x, \"*\")");
		testEquals(false, "x != null", "matches(x, \"*A\")", format);
		testEquals(false, "x != null", "matches(x, \"A*\")", format);
		testEquals(false, "x != null", "matches(x, \"?\")", format);
	}
}
