package com.pump.data.operator;

import java.util.Arrays;
import java.util.Collection;

import junit.framework.TestCase;

import org.junit.Test;

import com.pump.data.operator.Operator.And;
import com.pump.data.operator.Operator.Context;
import com.pump.data.operator.Operator.EqualTo;
import com.pump.data.operator.Operator.GreaterThan;
import com.pump.data.operator.Operator.In;
import com.pump.data.operator.Operator.LesserThan;
import com.pump.data.operator.Operator.Like;
import com.pump.data.operator.Operator.Not;
import com.pump.data.operator.Operator.Or;
import com.pump.text.WildcardPattern;

public class OperatorTest extends TestCase {

	public static class StudentBean {
		enum House {
			Gryffindor, Hufflepuff, Ravenclaw, Slytherin
		}

		String firstName, lastName;
		int birthYear;
		House house;

		public StudentBean(String firstName, String lastName, House house,
				int birthYear) {
			this.firstName = firstName;
			this.lastName = lastName;
			this.house = house;
			this.birthYear = birthYear;
		}
	}

	static StudentBean harryPotter = new StudentBean("Harry", "Potter",
			StudentBean.House.Gryffindor, 1980);
	static StudentBean ronWeasley = new StudentBean("Ron", "Weasley",
			StudentBean.House.Gryffindor, 1980);
	static StudentBean hermioneGranger = new StudentBean("Hermione", "Granger",
			StudentBean.House.Gryffindor, 1980);
	static StudentBean lavenderBrown = new StudentBean("Lavender", "Brown",
			StudentBean.House.Gryffindor, 1979);
	static StudentBean pavartiPatil = new StudentBean("Pavarti", "Patil",
			StudentBean.House.Gryffindor, 1980);
	static StudentBean padmaPatil = new StudentBean("Padma", "Patil",
			StudentBean.House.Ravenclaw, 1980);
	static StudentBean ginnyWeasley = new StudentBean("Ginny", "Weasley",
			StudentBean.House.Gryffindor, 1981);
	static StudentBean choChang = new StudentBean("Cho", "Chang",
			StudentBean.House.Ravenclaw, 1979);
	static StudentBean lunaLovegood = new StudentBean("Luna", "Lovegood",
			StudentBean.House.Ravenclaw, 1981);
	static StudentBean nullStudent = new StudentBean(null, null, null, -1);

	static Context context = new Context() {

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
		// And.create(new LesserThan(StudentBean.class, "birthYear", 1980));
		// fail();
		// } catch (Exception e) {
		// // success
		// }
		//
		// try {
		// Or.create(new LesserThan(StudentBean.class, "birthYear", 1980));
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
		Operator lastNameNotWeasley = Not.create(new EqualTo("lastName",
				"Weasley"));
		assertFalse(lastNameNotWeasley.evaluate(context, ginnyWeasley));
		assertFalse(lastNameNotWeasley.evaluate(context, ronWeasley));
		assertTrue(lastNameNotWeasley.evaluate(context, pavartiPatil));
		assertTrue(lastNameNotWeasley.evaluate(context, harryPotter));

		Operator lastNameNotNull = Not.create(new EqualTo("lastName", null));
		assertTrue(lastNameNotNull.evaluate(context, hermioneGranger));
		assertFalse(lastNameNotNull.evaluate(context, nullStudent));

		// nulls aren't allowed
		try {
			Not.create(null);
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
		Operator ravenclaw = In.create("house",
				Arrays.asList(StudentBean.House.Ravenclaw));
		assertTrue(ravenclaw.evaluate(context, padmaPatil));
		assertFalse(ravenclaw.evaluate(context, pavartiPatil));
		assertFalse(ravenclaw.evaluate(context, nullStudent));
	}

	@Test
	public void testToString_scenario1() throws Exception {
		Operator ravenclaw = In.create("house",
				Arrays.asList(StudentBean.House.Ravenclaw));
		Operator above1980 = new GreaterThan("birthYear", 1980);
		Operator lastNameNotWeasley = Not.create(new EqualTo("lastName",
				"Weasley"));

		Operator or = Or.create(ravenclaw, above1980);
		Operator and = And.create(lastNameNotWeasley, or);
		assertEquals(
				"lastName != \"Weasley\" && (house == \"Ravenclaw\" || birthYear > 1980)",
				and.toString());
	}

	@Test
	public void testToString_scenario2() throws Exception {
		Operator ravenclaw = In.create("house",
				Arrays.asList(StudentBean.House.Ravenclaw));
		Operator above1980 = new GreaterThan("birthYear", 1980);
		Operator lastNameNotWeasley = Not.create(new EqualTo("lastName",
				"Weasley"));

		Operator notOr = Not.create(Or.create(ravenclaw, above1980));
		Operator and = And.create(lastNameNotWeasley, notOr);
		assertEquals(
				"lastName != \"Weasley\" && !(house == \"Ravenclaw\" || birthYear > 1980)",
				and.toString());
	}

	@Test
	public void testToString_scenario3() throws Exception {
		Operator ravenclaw = In.create("house",
				Arrays.asList(StudentBean.House.Ravenclaw));
		Operator above1980 = new GreaterThan("birthYear", 1980);
		Operator lastNameWeasley = new EqualTo("lastName", "Weasley");

		Operator notOr = Not.create(Or.create(ravenclaw, above1980));
		Operator notAnd = Not.create(And.create(notOr, lastNameWeasley));
		assertEquals(
				"!(!(house == \"Ravenclaw\" || birthYear > 1980) && lastName == \"Weasley\")",
				notAnd.toString());
	}

	@Test
	public void testToString_scenario4() throws Exception {
		WildcardPattern pStar = new WildcardPattern("P*");
		Operator lastNameP = new Like("lastName", pStar);

		assertEquals("matches(lastName, \"P*\")", lastNameP.toString());
		assertEquals("!matches(lastName, \"P*\")", Not.create(lastNameP)
				.toString());
		assertEquals("matches(lastName, \"P*\")",
				Not.create(Not.create(lastNameP)).toString());
		assertEquals("!matches(lastName, \"P*\")",
				Not.create(Not.create(Not.create(lastNameP))).toString());
	}

	@Test
	public void testToString_scenario5() throws Exception {
		WildcardPattern lStar = new WildcardPattern("L*");
		Operator firstNameL = new Like("firstName", lStar);
		Operator ravenclaw = In.create("house",
				Arrays.asList(StudentBean.House.Ravenclaw));
		Operator above1980 = new GreaterThan("birthYear", 1980);
		Operator lastNameWeasley = new EqualTo("lastName", "Weasley");

		Operator and1 = And.create(firstNameL, ravenclaw);
		Operator and2 = And.create(above1980, lastNameWeasley);
		Operator or = Or.create(and1, and2);
		assertEquals(
				"(matches(firstName, \"L*\") && house == \"Ravenclaw\") || (birthYear > 1980 && lastName == \"Weasley\")",
				or.toString());
	}

	@Test
	public void testEquals_scenario1() throws Exception {
		WildcardPattern lStar = new WildcardPattern("L*");
		Operator firstNameL = new Like("firstName", lStar);
		Operator ravenclaw = In.create("house",
				Arrays.asList(StudentBean.House.Ravenclaw));
		Operator above1980 = new GreaterThan("birthYear", 1980);
		Operator lastNameWeasley = new EqualTo("lastName", "Weasley");

		Operator a_and1 = And.create(firstNameL, ravenclaw);
		Operator a_and2 = And.create(above1980, lastNameWeasley);
		Operator a_masterAnd = And.create(a_and1, a_and2);

		Operator b_and1 = And.create(firstNameL, lastNameWeasley);
		Operator b_and2 = And.create(above1980, ravenclaw);
		Operator b_masterAnd = And.create(b_and1, b_and2);

		assertEquals(a_masterAnd, b_masterAnd);
		assertEquals(b_masterAnd, a_masterAnd);

		Operator lastNameGranger = new EqualTo("lastName", "Granger");
		Operator c_and1 = And.create(firstNameL, lastNameGranger);
		Operator c_and2 = And.create(above1980, ravenclaw);
		Operator c_masterAnd = And.create(c_and1, c_and2);
		assertFalse(a_masterAnd.equals(c_masterAnd));
		assertFalse(b_masterAnd.equals(c_masterAnd));
		assertFalse(c_masterAnd.equals(a_masterAnd));
		assertFalse(c_masterAnd.equals(b_masterAnd));
	}

	@Test
	public void testEquals_scenario2() throws Exception {
		WildcardPattern lStar = new WildcardPattern("L*");
		Operator firstNameL = new Like("firstName", lStar);
		Operator ravenclaw = In.create("house",
				Arrays.asList(StudentBean.House.Ravenclaw));
		Operator above1980 = new GreaterThan("birthYear", 1980);
		Operator lastNameWeasley = new EqualTo("lastName", "Weasley");

		Operator a1 = And.create(firstNameL, ravenclaw);
		Operator a2 = Or.create(Not.create(a1), above1980, lastNameWeasley);

		Operator b1 = Or.create(Not.create(firstNameL), Not.create(ravenclaw));
		Operator b2 = Or.create(b1, above1980, lastNameWeasley);

		assertEquals(a2, b2);
		assertEquals(b2, a2);

		Operator c1 = Or.create(lastNameWeasley, Not.create(firstNameL),
				Not.create(ravenclaw));
		Operator c2 = Or.create(c1, above1980);

		assertEquals(a2, c2);
		assertEquals(c2, a2);

		Operator d = Or.create(above1980, lastNameWeasley,
				Not.create(firstNameL), Not.create(ravenclaw));
		assertEquals(a2, d);
		assertEquals(d, a2);

		Operator e = Or.create(above1980, lastNameWeasley, firstNameL,
				Not.create(ravenclaw));
		assertFalse(a2.equals(e));
		assertFalse(e.equals(a2));

		Operator f = Or.create(above1980, Not.create(firstNameL),
				Not.create(ravenclaw));
		assertFalse(a2.equals(f));
		assertFalse(f.equals(a2));

		Operator g = And.create(above1980, lastNameWeasley,
				Not.create(firstNameL), Not.create(ravenclaw));
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

		Operator e = Not.create(a_firstNameL);

		assertFalse(a_firstNameL.equals(e));
		assertFalse(e.equals(a_firstNameL));

		Operator f = Not.create(a_firstNameL);

		assertEquals(f, e);
		assertEquals(e, f);

		Operator g = Not.create(Not.create(a_firstNameL));

		assertEquals(a_firstNameL, g);
		assertEquals(g, a_firstNameL);

		assertFalse(a_firstNameL.equals(f));
		assertFalse(f.equals(a_firstNameL));
	}

	protected void testEquals(String expected, String value) throws Exception {
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

			Operator op1 = new OperatorParser().parse(expected_revised);
			Operator op2 = new OperatorParser().parse(value_revised);
			assertEquals(op1, op2);
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
			Operator not = Not.create(op).getCanonicalOperator();
			Operator notNot = Not.create(not).getCanonicalOperator();
			assertEquals(op, notNot);
		}

		// TODO this currently fails for one expression:
		// Operator op5 = new OperatorParser().parse(expected + " || !("
		// + expected + ")");
		// assertEquals(Operator.TRUE, op5);
		// Operator op6 = new OperatorParser().parse(value + " || !(" +
		// value
		// + ")");
		// assertEquals(Operator.TRUE, op6);
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

		// // TODO: mutually exclusive INs result in a constant FALSE
		// {
		// Operator z = In.create("x", Arrays.asList(
		// StudentBean.House.Gryffindor, StudentBean.House.Slytherin));
		// Operator y = In.create("x", Arrays.asList(
		// StudentBean.House.Ravenclaw, StudentBean.House.Hufflepuff));
		// Operator w = And.create(z, y);
		// assertEquals(Operator.FALSE, w);
		// }
		//
		// // overlapping INs result in the smallest possible subset
		// {
		// Operator z = In.create("x", Arrays.asList(
		// StudentBean.House.Gryffindor, StudentBean.House.Slytherin));
		// Operator y = In
		// .create("x", Arrays.asList(StudentBean.House.Gryffindor,
		// StudentBean.House.Hufflepuff));
		// Operator w = And.create(z, y);
		// assertEquals(new EqualTo("x", StudentBean.House.Gryffindor), w);
		// }
	}

	@Test
	public void testEquals_scenario6_overlappingRanges() {

		// FFFF
		{
			// x > 0 && x < 3
			Operator z = And.create(new GreaterThan("x", 0), new LesserThan(
					"x", 3));
			// x > 2 && x < 5
			Operator y = And.create(new GreaterThan("x", 2), new LesserThan(
					"x", 5));

			// x > 2 && x < 3
			assertEquals(
					And.create(new GreaterThan("x", 2), new LesserThan("x", 3)),
					And.create(z, y));

			// x > 0 && x < 5
			Operator e = And.create(new GreaterThan("x", 0), new LesserThan(
					"x", 5));
			assertEquals(e, Or.create(z, y));
		}

		// FFFT
		{
			// x > 0 && x < 3
			Operator z = And.create(new GreaterThan("x", 0), new LesserThan(
					"x", 3));
			// x > 2 && x <= 5
			Operator y = And.create(new GreaterThan("x", 2),
					Or.create(new LesserThan("x", 5), new EqualTo("x", 5)));

			// x > 2 && x < 3
			assertEquals(
					And.create(new GreaterThan("x", 2), new LesserThan("x", 3)),
					And.create(z, y));

			// x > 0 && x <= 5
			Operator e = And.create(new GreaterThan("x", 0),
					Or.create(new LesserThan("x", 5), new EqualTo("x", 5)));
			assertEquals(e, Or.create(z, y));
		}

		// FFTF
		{
			// x > 0 && x < 3
			Operator z = And.create(new GreaterThan("x", 0), new LesserThan(
					"x", 3));
			// x >= 2 && x < 5
			Operator y = And.create(
					Or.create(new GreaterThan("x", 2), new EqualTo("x", 2)),
					new LesserThan("x", 5));

			// x >= 2 && x < 3
			assertEquals(And.create(
					Or.create(new GreaterThan("x", 2), new EqualTo("x", 2)),
					new LesserThan("x", 3)), And.create(z, y));

			// x > 0 && x < 5
			Operator e = And.create(new GreaterThan("x", 0), new LesserThan(
					"x", 5));
			assertEquals(e, Or.create(z, y));
		}

		// FFTT
		{
			// x > 0 && x < 3
			Operator z = And.create(new GreaterThan("x", 0), new LesserThan(
					"x", 3));
			// x >= 2 && x <= 5
			Operator y = And.create(
					Or.create(new GreaterThan("x", 2), new EqualTo("x", 2)),
					Or.create(new LesserThan("x", 5), new EqualTo("x", 5)));

			// x >= 2 && x < 3
			assertEquals(And.create(
					Or.create(new GreaterThan("x", 2), new EqualTo("x", 2)),
					new LesserThan("x", 3)), And.create(z, y));

			// x > 0 && x <= 5
			Operator e = And.create(new GreaterThan("x", 0),
					Or.create(new LesserThan("x", 5), new EqualTo("x", 5)));
			assertEquals(e, Or.create(z, y));
		}

		// FTFF
		{
			// x > 0 && x <= 3
			Operator z = And.create(new GreaterThan("x", 0),
					Or.create(new LesserThan("x", 3), new EqualTo("x", 3)));
			// x > 2 && x < 5
			Operator y = And.create(new GreaterThan("x", 2), new LesserThan(
					"x", 5));

			// x > 2 && x <= 3
			assertEquals(And.create(new GreaterThan("x", 2),
					Or.create(new LesserThan("x", 3), new EqualTo("x", 3))),
					And.create(z, y));

			// x > 0 && x < 5
			Operator e = And.create(new GreaterThan("x", 0), new LesserThan(
					"x", 5));
			assertEquals(e, Or.create(z, y));
		}

		// FTFT
		{
			// x > 0 && x <= 3
			Operator z = And.create(new GreaterThan("x", 0),
					Or.create(new LesserThan("x", 3), new EqualTo("x", 3)));
			// x > 2 && x <= 5
			Operator y = And.create(new GreaterThan("x", 2),
					Or.create(new LesserThan("x", 5), new EqualTo("x", 5)));

			// x > 2 && x <= 3
			assertEquals(And.create(new GreaterThan("x", 2),
					Or.create(new LesserThan("x", 3), new EqualTo("x", 3))),
					And.create(z, y));

			// x > 0 && x <= 5
			Operator e = And.create(new GreaterThan("x", 0),
					Or.create(new LesserThan("x", 5), new EqualTo("x", 5)));
			assertEquals(e, Or.create(z, y));
		}

		// FTTF
		{
			// x > 0 && x <= 3
			Operator z = And.create(new GreaterThan("x", 0),
					Or.create(new LesserThan("x", 3), new EqualTo("x", 3)));
			// x >= 2 && x < 5
			Operator y = And.create(
					Or.create(new GreaterThan("x", 2), new EqualTo("x", 2)),
					new LesserThan("x", 5));

			// x >= 2 && x <= 3
			assertEquals(And.create(
					Or.create(new GreaterThan("x", 2), new EqualTo("x", 2)),
					Or.create(new LesserThan("x", 3), new EqualTo("x", 3))),
					And.create(z, y));

			// x > 0 && x < 5
			Operator e = And.create(new GreaterThan("x", 0), new LesserThan(
					"x", 5));
			assertEquals(e, Or.create(z, y));
		}

		// FTTT
		{
			// x > 0 && x <= 3
			Operator z = And.create(new GreaterThan("x", 0),
					Or.create(new LesserThan("x", 3), new EqualTo("x", 3)));
			// x >= 2 && x <= 5
			Operator y = And.create(
					Or.create(new GreaterThan("x", 2), new EqualTo("x", 2)),
					Or.create(new LesserThan("x", 5), new EqualTo("x", 5)));

			// x >= 2 && x <= 3
			assertEquals(And.create(
					Or.create(new GreaterThan("x", 2), new EqualTo("x", 2)),
					Or.create(new LesserThan("x", 3), new EqualTo("x", 3))),
					And.create(z, y));

			// x > 0 && x <= 5
			Operator e = And.create(new GreaterThan("x", 0),
					Or.create(new LesserThan("x", 5), new EqualTo("x", 5)));
			assertEquals(e, Or.create(z, y));
		}

		// TFFF
		{
			// x >= 0 && x < 3
			Operator z = And.create(
					Or.create(new GreaterThan("x", 0), new EqualTo("x", 0)),
					new LesserThan("x", 3));
			// x > 2 && x < 5
			Operator y = And.create(new GreaterThan("x", 2), new LesserThan(
					"x", 5));

			// x > 2 && x < 3
			assertEquals(
					And.create(new GreaterThan("x", 2), new LesserThan("x", 3)),
					And.create(z, y));

			// x >= 0 && x < 5
			Operator e = And.create(
					Or.create(new GreaterThan("x", 0), new EqualTo("x", 0)),
					new LesserThan("x", 5));
			assertEquals(e, Or.create(z, y));
		}

		// TFFT
		{
			// x >= 0 && x < 3
			Operator z = And.create(
					Or.create(new GreaterThan("x", 0), new EqualTo("x", 0)),
					new LesserThan("x", 3));
			// x > 2 && x <= 5
			Operator y = And.create(new GreaterThan("x", 2),
					Or.create(new LesserThan("x", 5), new EqualTo("x", 5)));

			// x > 2 && x < 3
			assertEquals(
					And.create(new GreaterThan("x", 2), new LesserThan("x", 3)),
					And.create(z, y));

			// x >= 0 && x <= 5
			Operator e = And.create(
					Or.create(new GreaterThan("x", 0), new EqualTo("x", 0)),
					Or.create(new LesserThan("x", 5), new EqualTo("x", 5)));
			assertEquals(e, Or.create(z, y));
		}

		// TFTF
		{
			// x >= 0 && x < 3
			Operator z = And.create(
					Or.create(new GreaterThan("x", 0), new EqualTo("x", 0)),
					new LesserThan("x", 3));
			// x >= 2 && x < 5
			Operator y = And.create(
					Or.create(new GreaterThan("x", 2), new EqualTo("x", 2)),
					new LesserThan("x", 5));

			// x >= 2 && x < 3
			assertEquals(And.create(
					Or.create(new GreaterThan("x", 2), new EqualTo("x", 2)),
					new LesserThan("x", 3)), And.create(z, y));

			// x >= 0 && x < 5
			Operator e = And.create(
					Or.create(new GreaterThan("x", 0), new EqualTo("x", 0)),
					new LesserThan("x", 5));
			assertEquals(e, Or.create(z, y));
		}

		// TFTT
		{
			// x >= 0 && x < 3
			Operator z = And.create(
					Or.create(new GreaterThan("x", 0), new EqualTo("x", 0)),
					new LesserThan("x", 3));
			// x >= 2 && x <= 5
			Operator y = And.create(
					Or.create(new GreaterThan("x", 2), new EqualTo("x", 2)),
					Or.create(new LesserThan("x", 5), new EqualTo("x", 5)));

			// x >= 2 && x < 3
			assertEquals(And.create(
					Or.create(new GreaterThan("x", 2), new EqualTo("x", 2)),
					new LesserThan("x", 3)), And.create(z, y));

			// x >= 0 && x <= 5
			Operator e = And.create(
					Or.create(new GreaterThan("x", 0), new EqualTo("x", 0)),
					Or.create(new LesserThan("x", 5), new EqualTo("x", 5)));
			assertEquals(e, Or.create(z, y));
		}

		// TTFF
		{
			// x >= 0 && x <= 3
			Operator z = And.create(
					Or.create(new GreaterThan("x", 0), new EqualTo("x", 0)),
					Or.create(new LesserThan("x", 3), new EqualTo("x", 3)));
			// x > 2 && x < 5
			Operator y = And.create(new GreaterThan("x", 2), new LesserThan(
					"x", 5));

			// x > 2 && x <= 3
			assertEquals(And.create(new GreaterThan("x", 2),
					Or.create(new LesserThan("x", 3), new EqualTo("x", 3))),
					And.create(z, y));

			// x >= 0 && x < 5
			Operator e = And.create(
					Or.create(new GreaterThan("x", 0), new EqualTo("x", 0)),
					new LesserThan("x", 5));
			assertEquals(e, Or.create(z, y));
		}

		// TTFT
		{
			// x >= 0 && x <= 3
			Operator z = And.create(
					Or.create(new GreaterThan("x", 0), new EqualTo("x", 0)),
					Or.create(new LesserThan("x", 3), new EqualTo("x", 3)));
			// x > 2 && x <= 5
			Operator y = And.create(new GreaterThan("x", 2),
					Or.create(new LesserThan("x", 5), new EqualTo("x", 5)));

			// x > 2 && x <= 3
			assertEquals(And.create(new GreaterThan("x", 2),
					Or.create(new LesserThan("x", 3), new EqualTo("x", 3))),
					And.create(z, y));

			// x >= 0 && x <= 5
			Operator e = And.create(
					Or.create(new GreaterThan("x", 0), new EqualTo("x", 0)),
					Or.create(new LesserThan("x", 5), new EqualTo("x", 5)));
			assertEquals(e, Or.create(z, y));
		}

		// TTTF
		{
			// x >= 0 && x <= 3
			Operator z = And.create(
					Or.create(new GreaterThan("x", 0), new EqualTo("x", 0)),
					Or.create(new LesserThan("x", 3), new EqualTo("x", 3)));
			// x >= 2 && x < 5
			Operator y = And.create(
					Or.create(new GreaterThan("x", 2), new EqualTo("x", 2)),
					new LesserThan("x", 5));

			// x >= 2 && x <= 3
			assertEquals(And.create(
					Or.create(new GreaterThan("x", 2), new EqualTo("x", 2)),
					Or.create(new LesserThan("x", 3), new EqualTo("x", 3))),
					And.create(z, y));

			// x >= 0 && x < 5
			Operator e = And.create(
					Or.create(new GreaterThan("x", 0), new EqualTo("x", 0)),
					new LesserThan("x", 5));
			assertEquals(e, Or.create(z, y));
		}

		// TTTT
		{
			// x >= 0 && x <= 3
			Operator z = And.create(
					Or.create(new GreaterThan("x", 0), new EqualTo("x", 0)),
					Or.create(new LesserThan("x", 3), new EqualTo("x", 3)));
			// x >= 2 && x <= 5
			Operator y = And.create(
					Or.create(new GreaterThan("x", 2), new EqualTo("x", 2)),
					Or.create(new LesserThan("x", 5), new EqualTo("x", 5)));

			// x >= 2 && x <= 3
			assertEquals(And.create(
					Or.create(new GreaterThan("x", 2), new EqualTo("x", 2)),
					Or.create(new LesserThan("x", 3), new EqualTo("x", 3))),
					And.create(z, y));

			// x >= 0 && x <= 5
			Operator e = And.create(
					Or.create(new GreaterThan("x", 0), new EqualTo("x", 0)),
					Or.create(new LesserThan("x", 5), new EqualTo("x", 5)));
			assertEquals(e, Or.create(z, y));
		}
	}

	@Test
	public void testEquals_scenario7_overlappingInfiniteRanges() {

		// test the left interval spanning to -inf

		{
			// x < 3
			Operator z = new LesserThan("x", 3);
			// x > 2 && x < 5
			Operator y = And.create(new GreaterThan("x", 2), new LesserThan(
					"x", 5));

			// x > 2 && x < 3
			assertEquals(
					And.create(new GreaterThan("x", 2), new LesserThan("x", 3)),
					And.create(z, y));

			// x < 5
			Operator e = new LesserThan("x", 5);
			assertEquals(e, Or.create(z, y));
		}

		{
			// x <= 3
			Operator z = Or.create(new LesserThan("x", 3), new EqualTo("x", 3));
			// x > 2 && x < 5
			Operator y = And.create(new GreaterThan("x", 2), new LesserThan(
					"x", 5));

			// x > 2 && x < 3
			assertEquals(And.create(new GreaterThan("x", 2),
					Or.create(new LesserThan("x", 3), new EqualTo("x", 3))),
					And.create(z, y));

			// x < 5
			Operator e = new LesserThan("x", 5);
			assertEquals(e, Or.create(z, y));
		}

		{
			// x < 3
			Operator z = new LesserThan("x", 3);
			// x >= 2 && x < 5
			Operator y = And.create(
					Or.create(new GreaterThan("x", 2), new EqualTo("x", 2)),
					new LesserThan("x", 5));

			// x >= 2 && x < 3
			assertEquals(And.create(
					Or.create(new GreaterThan("x", 2), new EqualTo("x", 2)),
					new LesserThan("x", 3)), And.create(z, y));

			// x < 5
			Operator e = new LesserThan("x", 5);
			assertEquals(e, Or.create(z, y));
		}

		{
			// x < 3
			Operator z = new LesserThan("x", 3);
			// x > 2 && x <= 5
			Operator y = And.create(new GreaterThan("x", 2),
					Or.create(new LesserThan("x", 5), new EqualTo("x", 5)));

			// x > 2 && x < 3
			assertEquals(
					And.create(new GreaterThan("x", 2), new LesserThan("x", 3)),
					And.create(z, y));

			// x <= 5
			Operator e = Or.create(new LesserThan("x", 5), new EqualTo("x", 5));
			assertEquals(e, Or.create(z, y));
		}

		{
			// x <= 3
			Operator z = Or.create(new LesserThan("x", 3), new EqualTo("x", 3));
			// x >= 2 && x < 5
			Operator y = And.create(
					Or.create(new GreaterThan("x", 2), new EqualTo("x", 2)),
					new LesserThan("x", 5));

			// x >= 2 && x < 3
			assertEquals(And.create(
					Or.create(new GreaterThan("x", 2), new EqualTo("x", 2)),
					Or.create(new LesserThan("x", 3), new EqualTo("x", 3))),
					And.create(z, y));

			// x < 5
			Operator e = new LesserThan("x", 5);
			assertEquals(e, Or.create(z, y));
		}

		{
			// x <= 3
			Operator z = Or.create(new LesserThan("x", 3), new EqualTo("x", 3));
			// x > 2 && x <= 5
			Operator y = And.create(new GreaterThan("x", 2),
					Or.create(new LesserThan("x", 5), new EqualTo("x", 5)));

			// x > 2 && x < 3
			assertEquals(And.create(new GreaterThan("x", 2),
					Or.create(new LesserThan("x", 3), new EqualTo("x", 3))),
					And.create(z, y));

			// x <= 5
			Operator e = Or.create(new LesserThan("x", 5), new EqualTo("x", 5));
			assertEquals(e, Or.create(z, y));
		}

		{
			// x < 3
			Operator z = new LesserThan("x", 3);
			// x >= 2 && x <= 5
			Operator y = And.create(
					Or.create(new GreaterThan("x", 2), new EqualTo("x", 2)),
					Or.create(new LesserThan("x", 5), new EqualTo("x", 5)));

			// x >= 2 && x < 3
			assertEquals(And.create(
					Or.create(new GreaterThan("x", 2), new EqualTo("x", 2)),
					new LesserThan("x", 3)), And.create(z, y));

			// x <= 5
			Operator e = Or.create(new LesserThan("x", 5), new EqualTo("x", 5));
			assertEquals(e, Or.create(z, y));
		}

		{
			// x <= 3
			Operator z = Or.create(new LesserThan("x", 3), new EqualTo("x", 3));
			// x >= 2 && x <= 5
			Operator y = And.create(
					Or.create(new GreaterThan("x", 2), new EqualTo("x", 2)),
					Or.create(new LesserThan("x", 5), new EqualTo("x", 5)));

			// x >= 2 && x <= 3
			assertEquals(And.create(
					Or.create(new GreaterThan("x", 2), new EqualTo("x", 2)),
					Or.create(new LesserThan("x", 3), new EqualTo("x", 3))),
					And.create(z, y));

			// x <= 5
			Operator e = Or.create(new LesserThan("x", 5), new EqualTo("x", 5));
			assertEquals(e, Or.create(z, y));
		}

		// now test the right interval spanning to +inf

		{
			// x > 0 && x < 3
			Operator z = And.create(new GreaterThan("x", 0), new LesserThan(
					"x", 3));
			// x > 2
			Operator y = new GreaterThan("x", 2);

			// x > 2 && x < 3
			assertEquals(
					And.create(new GreaterThan("x", 2), new LesserThan("x", 3)),
					And.create(z, y));

			// x > 0
			Operator e = new GreaterThan("x", 0);
			assertEquals(e, Or.create(z, y));
		}

		// TODO: iterate over other combos
	}

	/**
	 * This confirms split() for Like operators are converted to EqualTo
	 * operators.
	 */
	@Test
	public void testSplit_scenario1() {
		Operator houseIn = In.create("house", Arrays.asList(
				StudentBean.House.Ravenclaw, StudentBean.House.Hufflepuff));

		Collection<Operator> n1 = houseIn.split();
		assertEquals(2, n1.size());
		assertTrue(n1
				.contains(new EqualTo("house", StudentBean.House.Ravenclaw)));
		assertTrue(n1.contains(new EqualTo("house",
				StudentBean.House.Hufflepuff)));

		Operator[] n2 = n1.toArray(new Operator[n1.size()]);
		Operator joined = Operator.join(n2);
		assertEquals(joined, houseIn);
	}

	/**
	 * This is a variation of scenario1 that includes a Not modifier.
	 */
	@Test
	public void testSplit_scenario2() {
		Operator houseIn = In.create("house", Arrays.asList(
				StudentBean.House.Ravenclaw, StudentBean.House.Hufflepuff));

		Operator op = Not.create(houseIn);
		Collection<Operator> n1 = op.split();
		assertEquals(2, n1.size());
		assertTrue(n1.contains(Not.create(new EqualTo("house",
				StudentBean.House.Ravenclaw))));
		assertTrue(n1.contains(Not.create(new EqualTo("house",
				StudentBean.House.Hufflepuff))));

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
				Arrays.asList(StudentBean.House.Ravenclaw));
		Operator ravenclawEqual = new EqualTo("house",
				StudentBean.House.Ravenclaw);
		Operator above1980 = new GreaterThan("birthYear", 1980);
		Operator lastNameWeasley = new EqualTo("lastName", "Weasley");

		Operator or1 = Or.create(Not.create(firstNameL), ravenclawIn);
		Operator or2 = Not.create(Or.create(above1980, lastNameWeasley));
		Operator or3 = Or.create(or1, or2);

		Collection<Operator> n1 = or3.split();
		assertEquals(3, n1.size());
		assertTrue(n1.contains(Not.create(firstNameL)));
		assertTrue(n1.contains(ravenclawEqual));
		assertTrue(n1.contains(And.create(Not.create(above1980),
				Not.create(lastNameWeasley))));

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
				Arrays.asList(StudentBean.House.Ravenclaw));
		Operator ravenclawEqual = new EqualTo("house",
				StudentBean.House.Ravenclaw);
		Operator above1980 = new GreaterThan("birthYear", 1980);
		Operator lastNameWeasley = new EqualTo("lastName", "Weasley");

		Operator and1 = And.create(Not.create(firstNameL), ravenclawIn);
		Operator and2 = And.create(above1980, lastNameWeasley);
		Operator and3 = And.create(and1, and2);

		Collection<Operator> n1 = and3.split();
		assertEquals(1, n1.size());
		assertTrue(n1.contains(And.create(Not.create(firstNameL),
				ravenclawEqual, above1980, lastNameWeasley)));

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
		Operator ravenclawSlytherinIn = In.create("house", Arrays.asList(
				StudentBean.House.Ravenclaw, StudentBean.House.Slytherin));
		Operator above1980 = new GreaterThan("birthYear", 1980);
		Operator lastNameWeasley = new EqualTo("lastName", "Weasley");

		Operator and1 = And
				.create(Not.create(firstNameL), ravenclawSlytherinIn);
		Operator and2 = Not.create(And.create(above1980, lastNameWeasley));
		Operator and3 = And.create(and1, and2);

		Operator ravenclawEqual = new EqualTo("house",
				StudentBean.House.Ravenclaw);
		Operator slytherinEqual = new EqualTo("house",
				StudentBean.House.Slytherin);

		Collection<Operator> n1 = and3.split();
		assertEquals(4, n1.size());
		assertTrue(n1.contains(And.create(Not.create(firstNameL),
				ravenclawEqual, Not.create(above1980))));
		assertTrue(n1.contains(And.create(Not.create(firstNameL),
				ravenclawEqual, Not.create(lastNameWeasley))));
		assertTrue(n1.contains(And.create(Not.create(firstNameL),
				slytherinEqual, Not.create(above1980))));
		assertTrue(n1.contains(And.create(Not.create(firstNameL),
				slytherinEqual, Not.create(lastNameWeasley))));

		Operator[] n2 = n1.toArray(new Operator[n1.size()]);
		Operator joined = Operator.join(n2);
		assertEquals(and3, joined);
	}

	@Test
	public void testSplit_scenario6() {
		WildcardPattern lStar = new WildcardPattern("L*");
		Operator firstNameL = new Like("firstName", lStar);
		Operator ravenclawSlytherinIn = In.create("house", Arrays.asList(
				StudentBean.House.Ravenclaw, StudentBean.House.Slytherin));
		Operator above1980 = new GreaterThan("birthYear", 1980);
		Operator lastNameWeasley = new EqualTo("lastName", "Weasley");

		Operator x = Or.create(firstNameL, ravenclawSlytherinIn);
		Operator y = And.create(above1980, lastNameWeasley);
		Operator z = Or.create(x, y);

		Operator ravenclawEqual = new EqualTo("house",
				StudentBean.House.Ravenclaw);
		Operator slytherinEqual = new EqualTo("house",
				StudentBean.House.Slytherin);

		Collection<Operator> n1 = z.split();
		assertEquals(4, n1.size());
		assertTrue(n1.contains(firstNameL));
		assertTrue(n1.contains(ravenclawEqual));
		assertTrue(n1.contains(slytherinEqual));
		assertTrue(n1.contains(And.create(above1980, lastNameWeasley)));

		Operator[] n2 = n1.toArray(new Operator[n1.size()]);
		Operator joined = Operator.join(n2);
		assertEquals(joined, z);
	}

	@Test
	public void testSplit_scenario7() {
		WildcardPattern lStar = new WildcardPattern("L*");
		Operator firstNameL = new Like("firstName", lStar);
		Operator ravenclawSlytherinIn = In.create("house", Arrays.asList(
				StudentBean.House.Ravenclaw, StudentBean.House.Slytherin));
		Operator above1980 = new GreaterThan("birthYear", 1980);
		Operator lastNameWeasley = new EqualTo("lastName", "Weasley");

		Operator x = And.create(firstNameL, ravenclawSlytherinIn);
		Operator y = And.create(above1980, lastNameWeasley);
		Operator z = Or.create(x, y);

		Operator ravenclawEqual = new EqualTo("house",
				StudentBean.House.Ravenclaw);
		Operator slytherinEqual = new EqualTo("house",
				StudentBean.House.Slytherin);

		Collection<Operator> n1 = z.split();
		assertEquals(3, n1.size());
		assertTrue(n1.contains(And.create(firstNameL, ravenclawEqual)));
		assertTrue(n1.contains(And.create(firstNameL, slytherinEqual)));
		assertTrue(n1.contains(And.create(above1980, lastNameWeasley)));

		Operator[] n2 = n1.toArray(new Operator[n1.size()]);
		Operator joined = Operator.join(n2);
		assertEquals(z, joined);
	}

	@Test
	public void testSplit_scenario8() {
		WildcardPattern lStar = new WildcardPattern("L*");
		Operator firstNameL = new Like("firstName", lStar);
		Operator ravenclawSlytherinIn = In.create("house", Arrays.asList(
				StudentBean.House.Ravenclaw, StudentBean.House.Slytherin));
		Operator above1980 = new GreaterThan("birthYear", 1980);
		Operator lastNameWeasley = new EqualTo("lastName", "Weasley");

		Operator x = Or.create(firstNameL, ravenclawSlytherinIn);
		Operator y = Or.create(above1980, lastNameWeasley);
		Operator z = And.create(x, y);

		Operator ravenclawEqual = new EqualTo("house",
				StudentBean.House.Ravenclaw);
		Operator slytherinEqual = new EqualTo("house",
				StudentBean.House.Slytherin);

		Collection<Operator> n1 = z.split();
		assertEquals(6, n1.size());
		assertTrue(n1.contains(And.create(firstNameL, above1980)));
		assertTrue(n1.contains(And.create(firstNameL, lastNameWeasley)));
		assertTrue(n1.contains(And.create(ravenclawEqual, above1980)));
		assertTrue(n1.contains(And.create(ravenclawEqual, lastNameWeasley)));
		assertTrue(n1.contains(And.create(slytherinEqual, above1980)));
		assertTrue(n1.contains(And.create(slytherinEqual, lastNameWeasley)));

		Operator[] n2 = n1.toArray(new Operator[n1.size()]);
		Operator joined = Operator.join(n2);
		assertEquals(z, joined);
	}

	/**
	 * This confirms that Ins are joined together correctly.
	 */
	@Test
	public void testSplit_scenario9() {
		Operator ravenclawSlytherinIn = In.create("house", Arrays.asList(
				StudentBean.House.Ravenclaw, StudentBean.House.Slytherin));
		Operator hufflepuffIn = In.create("house",
				Arrays.asList(StudentBean.House.Hufflepuff));

		Operator or = Or.create(ravenclawSlytherinIn, hufflepuffIn);
		Collection<Operator> n1 = or.split();
		assertEquals(3, n1.size());
		assertTrue(n1
				.contains(new EqualTo("house", StudentBean.House.Ravenclaw)));
		assertTrue(n1.contains(new EqualTo("house",
				StudentBean.House.Hufflepuff)));
		assertTrue(n1
				.contains(new EqualTo("house", StudentBean.House.Slytherin)));

		Operator[] n2 = n1.toArray(new Operator[n1.size()]);
		Operator joined = Operator.join(n2);
		assertEquals(joined, In.create("house", Arrays.asList(
				StudentBean.House.Ravenclaw, StudentBean.House.Slytherin,
				StudentBean.House.Hufflepuff)));
	}

	@Test
	public void testSplit_scenario10() {
		Operator notRavenclawSlytherinIn = Not.create(In.create("house", Arrays
				.asList(StudentBean.House.Ravenclaw,
						StudentBean.House.Slytherin)));
		Operator hufflepuffGryffindorIn = In.create("house", Arrays.asList(
				StudentBean.House.Hufflepuff, StudentBean.House.Gryffindor));

		Operator above1980 = new GreaterThan("birthYear", 1980);
		Operator lastNameWeasley = new EqualTo("lastName", "Weasley");

		Operator c = And.create(above1980, notRavenclawSlytherinIn);
		Operator d = And.create(lastNameWeasley, hufflepuffGryffindorIn);

		Operator notRavenclawEqual = Not.create(new EqualTo("house",
				StudentBean.House.Ravenclaw));
		Operator notSlytherinEqual = Not.create(new EqualTo("house",
				StudentBean.House.Slytherin));

		Operator hufflepuffEqual = new EqualTo("house",
				StudentBean.House.Hufflepuff);
		Operator gryffindorEqual = new EqualTo("house",
				StudentBean.House.Gryffindor);

		Operator or = Or.create(c, d);
		Collection<Operator> n1 = or.split();
		assertEquals(4, n1.size());
		assertTrue(n1.contains(And.create(above1980, notSlytherinEqual)));
		assertTrue(n1.contains(And.create(above1980, notRavenclawEqual)));
		assertTrue(n1.contains(And.create(lastNameWeasley, hufflepuffEqual)));
		assertTrue(n1.contains(And.create(lastNameWeasley, gryffindorEqual)));

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
			Operator x = Or.create(a, b, a, b, a);
			assertEquals(Or.create(a, b), x);
		}

		{
			Operator x = And.create(a, b, a, b, a);
			assertEquals(And.create(a, b), x);
		}

		{
			Operator x = And.create(a, Not.create(a));
			assertEquals(Operator.FALSE, x);
		}

		{
			Operator x = And.create(b, a, Not.create(a));
			assertEquals(Operator.FALSE, x);
		}

		{
			Operator x = Or.create(a, Not.create(a));
			assertEquals(Operator.TRUE, x);
		}

		{
			Operator x = Or.create(b, a, Not.create(a));
			assertEquals(Operator.TRUE, x);
		}

		{
			Operator x = Or.create(b, And.create(a, Not.create(a)));
			assertEquals(b, x);
		}

		{
			Operator x = Or.create(b, c, And.create(a, Not.create(a)));
			assertEquals(Or.create(b, c), x);
		}

		{
			Operator x = Or.create(b, And.create(c, a, Not.create(a)));
			assertEquals(b, x);
		}

		{
			Operator x = Or.create(And.create(a, b),
					Not.create(And.create(a, b)));
			assertEquals(Operator.TRUE, x);
		}

		{
			Operator x = And.create(Or.create(a, b),
					Not.create(Or.create(a, b)));
			assertEquals(Operator.FALSE, x);
		}

		// from the exercises in the boolean logic tutorial:

		{
			Operator x = Or.create(And.create(a, b),
					And.create(a, Or.create(b, c)),
					And.create(b, Or.create(b, c)));
			Operator simplified = Or.create(b, And.create(a, c));
			assertEquals(x, simplified);
		}

		{
			Operator x = Or.create(
					And.create(Not.create(a), Not.create(b), Not.create(c)),
					And.create(Not.create(a), b, c),
					And.create(a, Not.create(b), Not.create(c)),
					And.create(a, Not.create(b), c), And.create(a, b, c));
			Operator simplified = Or.create(And.create(a, Not.create(b)),
					And.create(Not.create(b), Not.create(c)), And.create(b, c));
			assertEquals(x, simplified);
		}

		{
			Operator x = And.create(Or.create(a, Not.create(a)), Or.create(
					And.create(a, b), And.create(a, b, Not.create(c))));
			Operator simplified = And.create(a, b);
			assertEquals(x, simplified);
		}

		// expression #5:
		{
			Operator x = Or.create(And.create(b, c),
					And.create(Not.create(b), c));
			assertEquals(x, c);
		}

		// expression #6:
		{
			Operator x = Or.create(
					And.create(Not.create(a), b),
					And.create(b, Not.create(a), Not.create(c)),
					And.create(b, c, d, Not.create(a)),
					And.create(b, e, Not.create(a), Not.create(c),
							Not.create(d)));
			Operator simplified = And.create(Not.create(a), b);
			assertEquals(x, simplified);
		}

		// I couldn't follow expression #7 (off-balance parentheses?)

		// expression #8:
		{
			Operator x = Or.create(And.create(a, Not.create(b), c),
					And.create(Not.create(a), b, c),
					And.create(Not.create(a), Not.create(b), c));
			Operator simplified = Or.create(And.create(Not.create(a), c),
					And.create(Not.create(b), c));
			assertEquals(x, simplified);
		}

	}
}
