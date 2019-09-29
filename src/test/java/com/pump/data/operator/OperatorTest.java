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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
		assertEquals(op, parse(str));
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

		testEquals(
				"!(matches(firstName, \"L*\") && house == \"Ravenclaw\") || birthYear > 1980 || lastName == \"Weasley\"",
				"(!matches(firstName, \"L*\") || house != \"Ravenclaw\") || birthYear > 1980 || lastName == \"Weasley\"");

		testEquals(
				"!(matches(firstName, \"L*\") && house == \"Ravenclaw\") || birthYear > 1980 || lastName == \"Weasley\"",
				"(lastName == \"Weasley\" || !matches(firstName, \"L*\") || house != \"Ravenclaw\") || birthYear > 1980");

		testEquals(
				"!(matches(firstName, \"L*\") && house == \"Ravenclaw\") || birthYear > 1980 || lastName == \"Weasley\"",
				"birthYear > 1980 || lastName == \"Weasley\" || !matches(firstName, \"L*\") || house != \"Ravenclaw\"");

		testEquals(
				false,
				"birthYear > 1980 || lastName == \"Weasley\" || matches(firstName, \"L*\") || house != \"Ravenclaw\"",
				"!(matches(firstName, \"L*\") && house == \"Ravenclaw\") || birthYear > 1980 || lastName == \"Weasley\"",
				null);

		testEquals(
				false,
				"!(matches(firstName, \"L*\") && house == \"Ravenclaw\") || birthYear > 1980 || lastName == \"Weasley\"",
				"birthYear > 1980 || !matches(firstName, \"L*\") || house != \"Ravenclaw\"",
				null);

		testEquals(
				false,
				"!(matches(firstName, \"L*\") && house == \"Ravenclaw\") || birthYear > 1980 || lastName == \"Weasley\"",
				"birthYear > 1980 && lastName == \"Weasley\" && !matches(firstName, \"L*\") && house != \"Ravenclaw\"",
				null);
	}

	@Test
	public void testEquals_scenario3() throws Exception {

		testEquals(false, "matches(firstName, \"L*\")",
				"matches(lastName, \"L*\")", null);

		testEquals(false, "matches(firstName, \"L*\")",
				"matches(firstName, \"*l\")", null);

		testEquals(false, "matches(firstName, \"L*\")",
				"!matches(firstName, \"L*\")", null);

		testEquals("matches(firstName, \"L*\")",
				"!(!matches(firstName, \"L*\"))");

		testEquals(false, "matches(firstName, \"L*\")",
				"!matches(firstName, \"L*\")", null);
	}

	protected void testEquals(String expected, String value) throws Exception {
		testEquals(true, expected, value, null);
	}

	private static Operator parse(String str) throws Exception {
		return parse(str, null);
	}

	private static Operator parse(String str, WildcardPattern.Format format)
			throws Exception {
		if (format == null)
			format = new WildcardPattern.Format();
		return new OperatorParser(format).parse(str);
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

			Operator op1 = parse(expected_revised, format);
			Operator op1_copy = parse(expected_revised, format);
			Operator op2 = parse(value_revised, format);
			Operator op2_copy = parse(value_revised, format);

			if (equals) {
				assertTrue(op1.equals(op2, false));
				assertTrue(op2.equals(op1, false));
			} else {
				assertFalse("\"" + op1.toString() + "\" should not equal \""
						+ op2.toString() + "\"", op1.equals(op2, false));
				assertFalse("\"" + op2.toString() + "\" should not equal \""
						+ op1.toString() + "\"", op2.equals(op1, false));
			}

			// test strict equivalency
			assertTrue(op1.equals(op1_copy, true));
			assertTrue(op2.equals(op2_copy, true));
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
		Operator op = parse(str);
		{
			Operator canonicalOp = op.getCanonicalOperator();
			assertTrue(op.equals(canonicalOp, false));
		}

		{
			Operator op2 = new And(op, new Not(op));
			assertTrue(Operator.FALSE.equals(op2, false));
		}

		{
			Operator op2 = new Or(op, new Not(op));
			assertTrue(Operator.TRUE.equals(op2, false));
		}

		{
			Operator not = new Not(op).getCanonicalOperator();
			Operator notNot = new Not(not).getCanonicalOperator();
			assertTrue(op.equals(notNot, false));
		}

		{
			Collection<Operator> split = op.split();
			Operator join = Operator.join(split.toArray(new Operator[split
					.size()]));
			assertTrue(op.equals(join, false));
		}

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
	public void testSplit_scenario1() throws Exception {
		Operator houseIn = parse("contains(house, {\"Hufflepuff\", \"Ravenclaw\"})");

		Collection<Operator> n1 = houseIn.split();
		assertEquals(2, n1.size());

		testContains(n1, "house == \"Ravenclaw\"");
		testContains(n1, "house == \"Hufflepuff\"");

		Operator[] n2 = n1.toArray(new Operator[n1.size()]);
		Operator joined = Operator.join(n2);
		assertEquals(houseIn, joined);
	}

	/**
	 * This is a variation of scenario1 that includes a Not modifier.
	 */
	@Test
	public void testSplit_scenario2() throws Exception {
		Operator op = parse("!contains(house, {\"Hufflepuff\", \"Ravenclaw\"})");
		Collection<Operator> n1 = op.split();
		assertEquals(1, n1.size());

		testContains(n1, "house != \"Hufflepuff\" && house != \"Ravenclaw\"");

		Operator[] n2 = n1.toArray(new Operator[n1.size()]);
		Operator joined = Operator.join(n2);
		assertEquals(op, joined);
	}

	/**
	 * This tests split() for a a simple tree of 4 OR'ed operators.
	 */
	@Test
	public void testSplit_scenario3() throws Exception {
		Operator or = parse("!matches(firstName, \"L*\") || house == \"Ravenclaw\" || !(birthYear > 1980 || lastName == \"Weasley\")");

		Collection<Operator> n1 = or.split();
		assertEquals(3, n1.size());

		testContains(n1, "!matches(firstName, \"L*\")");
		testContains(n1, "house == \"Ravenclaw\"");
		testContains(n1, "birthYear <= 1980 && lastName != \"Weasley\"");

		Operator[] n2 = n1.toArray(new Operator[n1.size()]);
		Operator joined = Operator.join(n2);
		testEquals(or.toString(), joined.toString());
		assertEquals(Collections.emptyList(), getIns(or));
	}

	/**
	 * Return all the Ins that are a descendant of the argument (including the
	 * argument itself)
	 * 
	 * @param op
	 * @return
	 */
	private List<In> getIns(Operator op) {
		if (op instanceof In)
			return Arrays.asList((In) op);
		List<In> returnValue = new ArrayList<>();
		for (int a = 0; a < op.getOperandCount(); a++) {
			if (op.getOperand(a) instanceof Operator) {
				returnValue.addAll(getIns((Operator) op.getOperand(a)));
			}
		}
		return returnValue;
	}

	/**
	 * This is a variation of scenario3 that uses four ANDs instead of ORs
	 */
	@Test
	public void testSplit_scenario4() throws Exception {

		Operator op = parse("(!matches(firstName, \"L*\") && house == \"Ravenclaw\") && (birthYear > 1980 && lastName == \"Weasley\")");
		Collection<Operator> n1 = op.split();
		testContains(
				n1,
				"!matches(firstName, \"L*\") && house == \"Ravenclaw\" && birthYear > 1980 && lastName == \"Weasley\"");

		Operator[] n2 = n1.toArray(new Operator[n1.size()]);
		Operator joined = Operator.join(n2);
		testEquals(op.toString(), joined.toString());
		assertEquals(getIns(op), getIns(joined));
	}

	private boolean testContains(Collection<Operator> c, String str)
			throws Exception {
		Operator op = parse(str);
		for (Operator e : c) {
			if (e.equals(op, false))
				return true;
		}
		return false;
	}

	/**
	 * This includes a negated AND which must be converted to 2 elements and an
	 * IN with 2 elements.
	 */
	@Test
	public void testSplit_scenario5() throws Exception {
		Operator op = parse("(!matches(firstName, \"L*\") && contains(house, {\"Ravenclaw\", \"Slytherin\"})) && !(birthYear > 1980 && lastName == \"Weasley\")");

		Collection<Operator> n1 = op.split();
		assertEquals(4, n1.size());

		testContains(n1,
				"!matches(firstName, \"L*\") && house == \"Ravenclaw\" && birthYear <= 1980");
		testContains(
				n1,
				"!matches(firstName, \"L*\") && house == \"Ravenclaw\" && lastName != \"Weasley\"");
		testContains(n1,
				"!matches(firstName, \"L*\") && house == \"Slytherin\" && birthYear <= 1980");
		testContains(
				n1,
				"!matches(firstName, \"L*\") && house == \"Slytherin\" && lastName != \"Weasley\"");

		Operator[] n2 = n1.toArray(new Operator[n1.size()]);
		Operator joined = Operator.join(n2);
		testEquals(op.toString(), joined.toString());
		// TODO: this would be nice to support one day
		// assertEquals(getIns(op), getIns(joined));
	}

	@Test
	public void testSplit_scenario6() throws Exception {
		Operator op = parse("(matches(firstName, \"L*\") || contains(house, {\"Ravenclaw\", \"Slytherin\"})) || (birthYear > 1980 && lastName == \"Weasley\")");

		Collection<Operator> n1 = op.split();
		assertEquals(4, n1.size());

		testContains(n1, "matches(firstName, \"L*\")");
		testContains(n1, "house == \"Ravenclaw\"");
		testContains(n1, "house == \"Slytherin\"");
		testContains(n1, "birthYear > 1980 && lastName == \"Weasley\"");

		Operator[] n2 = n1.toArray(new Operator[n1.size()]);
		Operator joined = Operator.join(n2);
		testEquals(op.toString(), joined.toString());
		assertEquals(getIns(op), getIns(joined));
	}

	@Test
	public void testSplit_scenario7() throws Exception {
		Operator op = parse("(matches(firstName, \"L*\") && contains(house, {\"Ravenclaw\", \"Slytherin\"})) || (birthYear > 1980 && lastName == \"Weasley\")");

		Collection<Operator> n1 = op.split();
		assertEquals(3, n1.size());

		testContains(n1, "matches(firstName, \"L*\") && house == \"Ravenclaw\"");
		testContains(n1, "matches(firstName, \"L*\") && house == \"Slytherin\"");
		testContains(n1, "birthYear > 1980 && lastName == \"Weasley\"");

		Operator[] n2 = n1.toArray(new Operator[n1.size()]);
		Operator joined = Operator.join(n2);
		testEquals(op.toString(), joined.toString());
		// TODO: this would be nice to support one day
		// assertEquals(getIns(op), getIns(joined));
	}

	@Test
	public void testSplit_scenario8() throws Exception {
		Operator op = parse("(matches(firstName, \"L*\") || contains(house, {\"Ravenclaw\", \"Slytherin\"})) && (birthYear > 1980 || lastName == \"Weasley\")");

		Collection<Operator> n1 = op.split();
		assertEquals(6, n1.size());

		testContains(n1, "matches(firstName, \"L*\") && birthYear > 1980");
		testContains(n1,
				"matches(firstName, \"L*\") && lastName == \"Weasley\"");
		testContains(n1, "house == \"Ravenclaw\" && birthYear > 1980");
		testContains(n1, "house == \"Ravenclaw\" && lastName == \"Weasley\"");
		testContains(n1, "house == \"Slytherin\" && birthYear > 1980");
		testContains(n1, "house == \"Slytherin\" && lastName == \"Weasley\"");

		Operator[] n2 = n1.toArray(new Operator[n1.size()]);
		Operator joined = Operator.join(n2);
		testEquals(op.toString(), joined.toString());
		// TODO: this would be nice to support one day
		// assertEquals(getIns(op), getIns(joined));
	}

	/**
	 * This confirms that Ins are joined together correctly.
	 */
	@Test
	public void testSplit_scenario9() throws Exception {
		Operator op = parse("contains(house, {\"Ravenclaw\", \"Slytherin\"}) || house == \"Hufflepuff\"");

		Collection<Operator> n1 = op.split();
		assertEquals(3, n1.size());

		testContains(n1, "house == \"Ravenclaw\"");
		testContains(n1, "house == \"Hufflepuff\"");
		testContains(n1, "house == \"Slytherin\"");

		Operator[] n2 = n1.toArray(new Operator[n1.size()]);
		Operator joined = Operator.join(n2);
		testEquals(op.toString(), joined.toString());

		assertTrue(getIns(joined).size() > 0);
	}

	@Test
	public void testSplit_scenario10() throws Exception {
		Operator op = parse("(birthYear > 1980 && !contains(house, {\"Ravenclaw\", \"Slytherin\"})) || (lastName == \"Weasley\" && contains(house, {\"Hufflepuff\", \"Gryffindor\"}))");

		Collection<Operator> n1 = op.split();
		assertEquals(3, n1.size());

		testContains(n1,
				"birthYear > 1980 && house != \"Slytherin\" && house != \"Ravenclaw\"");
		testContains(n1, "lastName == \"Weasley\" && house == \"Hufflepuff\"");
		testContains(n1, "lastName == \"Weasley\" && house == \"Gryffindor\"");

		Operator[] n2 = n1.toArray(new Operator[n1.size()]);
		Operator joined = Operator.join(n2);

		testEquals(op.toString(), joined.toString());
		// TODO: this would be nice to support one day
		// assertEquals(getIns(op), getIns(joined));
	}

	/**
	 * This tests how expressions are simplified.
	 * <p>
	 * Some specific examples used here are from
	 * http://electronics-course.com/boolean-algebra /
	 * https://www.youtube.com/watch?v=59BbncMjL8I
	 */
	@Test
	public void testSimplify() throws Exception {

		// first the basic stuff:
		{
			testSimplifyEquals("a || b", "a || b || a || b || a", true);
			testSimplifyEquals("a && b", "a && b && a && b && a", true);
			testSimplifyEquals("false", "a && !a", true);
			testSimplifyEquals("false", "b && a && !a", true);
			testSimplifyEquals("true", "a || !a", true);
			testSimplifyEquals("true", "b || a || !a", true);
			testSimplifyEquals("b", "b || (a && !a)", true);
			testSimplifyEquals("b || c", "b || c || (a && !a)", true);
			testSimplifyEquals("b", "b || (c && a && !a)", true);
			testSimplifyEquals("true", "(a && b) || !(a && b)", false);
			testSimplifyEquals("false", "(a || b) && !(a || b)", true);
		}

		// from the exercises in the boolean logic tutorial:
		{
			testSimplifyEquals("(a && c) || b",
					"(a && b) || (a && (b || c)) || (b && (b || c))", true);
			testSimplifyEquals(
					"(a && !b) || (!b && !c) || (b && c)",
					"(!a && !b && !c) || (!a && b && c) || (a && !b && !c) || (a && !b && c) || (a && b && c)",
					false);
			testSimplifyEquals("a && b",
					"(a || !a) && ((a && b) || (a && b && !c))", true);

			// expression #5:
			testSimplifyEquals("c", "(b && c) || (!b && c)", true);

			// expression #6:
			testSimplifyEquals(
					"!a && b",
					"(!a && b) || (b && !a && !c) || (b && c && d && !a) || (b && e && !a && !c && !d)",
					true);

			// I couldn't follow expression #7 (off-balance parentheses?)

			// expression #8:
			testSimplifyEquals("(!a && c) || (!b && c)",
					"(a && !b && c) || (!a && b && c) || (!a && !b && c)",
					false);

		}
	}

	/**
	 * In addition to all the work that goes into
	 * {@link #testEquals(String, String)}, this also confirms that the complex
	 * expression is simplified into the simple expression.
	 * 
	 * @param simpleStr
	 *            a simple expression, like "true"
	 * @param complexStr
	 *            a more complex expression that can be simplified to the first
	 *            argument, like "a || !a"
	 * @param testCanonicalSimplification
	 *            if true then we confirm that the canonical representation
	 *            simplified away the more complex String. TODO: ideally we
	 *            should remove this argument and always treat it as "true", but
	 *            for now a few expressions don't pass this test.
	 * @throws Exception
	 */
	private void testSimplifyEquals(String simpleStr, String complexStr,
			boolean testCanonicalSimplification) throws Exception {
		testEquals(simpleStr, complexStr);

		if (testCanonicalSimplification) {
			Operator simpleOp = parse(simpleStr);
			Operator complexOp = parse(complexStr);
			Operator reducedComplexOp = complexOp.getCanonicalOperator();
			assertEquals(simpleOp, reducedComplexOp);
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

		testEquals(true, "x != null", "matches(x, \"*\")", format);
		testEquals(true, "x != null", "matches(x, \"*A\")", format);
		testEquals(true, "x != null", "matches(x, \"A*\")", format);
		testEquals(true, "x != null", "matches(x, \"?\")", format);
	}

	/**
	 * This tests splitting, removing an element, and rejoining the remaining
	 * elements
	 * <p>
	 * This is based on an observed real-world failure.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSplitScenario_11() throws Exception {
		String str = "contains(courseOid, {\"crs01000107826\", \"crs01000107947\", \"crs01000121691\", \"crs01000124242\", \"crs01000130966\", \"crs01000130967\"}) && programStudiesOid == \"GPR0000001e0Cp\"";
		Operator op = new OperatorParser().parse(str);
		Collection<Operator> split = op.createCanonicalOperator().split();
		assertEquals(6, split.size());

		split.remove(new OperatorParser()
				.parse("courseOid == \"crs01000107826\" && programStudiesOid == \"GPR0000001e0Cp\""));
		assertEquals(5, split.size());

		String str2 = "contains(courseOid, {\"crs01000107947\", \"crs01000121691\", \"crs01000124242\", \"crs01000130966\", \"crs01000130967\"}) && programStudiesOid == \"GPR0000001e0Cp\"";
		Operator op2 = new OperatorParser().parse(str2);
		Operator joined = Operator.join(split);
		assertEquals(op2, joined);
	}

	/**
	 * This tests the negation of scenario 11.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testSplitScenario_12() throws Exception {
		String str = "!contains(courseOid, {\"crs01000107826\", \"crs01000107947\", \"crs01000121691\", \"crs01000124242\", \"crs01000130966\", \"crs01000130967\"}) && programStudiesOid == \"GPR0000001e0Cp\"";
		Operator op = new OperatorParser().parse(str);
		Collection<Operator> split = op.createCanonicalOperator().split();
		assertEquals(6, split.size());

		split.remove(new OperatorParser()
				.parse("courseOid != \"crs01000107826\" && programStudiesOid == \"GPR0000001e0Cp\""));
		assertEquals(5, split.size());

		String str2 = "!contains(courseOid, {\"crs01000107947\", \"crs01000121691\", \"crs01000124242\", \"crs01000130966\", \"crs01000130967\"}) && programStudiesOid == \"GPR0000001e0Cp\"";
		Operator op2 = new OperatorParser().parse(str2);
		Operator joined = Operator.join(split);
		assertEquals(op2, joined);
	}
}