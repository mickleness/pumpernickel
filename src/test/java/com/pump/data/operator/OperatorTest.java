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
		Operator ravenclaw = new In("house",
				Arrays.asList(StudentBean.House.Ravenclaw));
		assertTrue(ravenclaw.evaluate(context, padmaPatil));
		assertFalse(ravenclaw.evaluate(context, pavartiPatil));
		assertFalse(ravenclaw.evaluate(context, nullStudent));
	}

	@Test
	public void testToString_scenario1() throws Exception {
		Operator ravenclaw = new In("house",
				Arrays.asList(StudentBean.House.Ravenclaw));
		Operator above1980 = new GreaterThan("birthYear", 1980);
		Operator lastNameNotWeasley = Not.create(new EqualTo("lastName",
				"Weasley"));

		Operator or = Or.create(ravenclaw, above1980);
		Operator and = And.create(lastNameNotWeasley, or);
		assertEquals(
				"lastName != 'Weasley' && (contains(house, {'Ravenclaw'}) || birthYear > '1980')",
				and.toString());
	}

	@Test
	public void testToString_scenario2() throws Exception {
		Operator ravenclaw = new In("house",
				Arrays.asList(StudentBean.House.Ravenclaw));
		Operator above1980 = new GreaterThan("birthYear", 1980);
		Operator lastNameNotWeasley = Not.create(new EqualTo("lastName",
				"Weasley"));

		Operator notOr = Not.create(Or.create(ravenclaw, above1980));
		Operator and = And.create(lastNameNotWeasley, notOr);
		assertEquals(
				"lastName != 'Weasley' && !(contains(house, {'Ravenclaw'}) || birthYear > '1980')",
				and.toString());
	}

	@Test
	public void testToString_scenario3() throws Exception {
		Operator ravenclaw = new In("house",
				Arrays.asList(StudentBean.House.Ravenclaw));
		Operator above1980 = new GreaterThan("birthYear", 1980);
		Operator lastNameWeasley = new EqualTo("lastName", "Weasley");

		Operator notOr = Not.create(Or.create(ravenclaw, above1980));
		Operator notAnd = Not.create(And.create(notOr, lastNameWeasley));
		assertEquals(
				"!(!(contains(house, {'Ravenclaw'}) || birthYear > '1980') && lastName == 'Weasley')",
				notAnd.toString());
	}

	@Test
	public void testToString_scenario4() throws Exception {
		WildcardPattern pStar = new WildcardPattern("P*");
		Operator lastNameP = new Like("lastName", pStar);

		assertEquals("matches(lastName, 'P*')", lastNameP.toString());
		assertEquals("!matches(lastName, 'P*')", Not.create(lastNameP)
				.toString());
		assertEquals("matches(lastName, 'P*')",
				Not.create(Not.create(lastNameP)).toString());
		assertEquals("!matches(lastName, 'P*')",
				Not.create(Not.create(Not.create(lastNameP))).toString());
	}

	@Test
	public void testToString_scenario5() throws Exception {
		WildcardPattern lStar = new WildcardPattern("L*");
		Operator firstNameL = new Like("firstName", lStar);
		Operator ravenclaw = new In("house",
				Arrays.asList(StudentBean.House.Ravenclaw));
		Operator above1980 = new GreaterThan("birthYear", 1980);
		Operator lastNameWeasley = new EqualTo("lastName", "Weasley");

		Operator and1 = And.create(firstNameL, ravenclaw);
		Operator and2 = And.create(above1980, lastNameWeasley);
		Operator or = Or.create(and1, and2);
		assertEquals(
				"(matches(firstName, 'L*') && contains(house, {'Ravenclaw'})) || (birthYear > '1980' && lastName == 'Weasley')",
				or.toString());
	}

	@Test
	public void testEquals_scenario1() throws Exception {
		WildcardPattern lStar = new WildcardPattern("L*");
		Operator firstNameL = new Like("firstName", lStar);
		Operator ravenclaw = new In("house",
				Arrays.asList(StudentBean.House.Ravenclaw));
		Operator above1980 = new GreaterThan("birthYear", 1980);
		Operator lastNameWeasley = new EqualTo("lastName", "Weasley");

		Operator a_and1 = And.create(firstNameL, ravenclaw);
		Operator a_and2 = And.create(above1980, lastNameWeasley);
		Operator a_masterAnd = And.create(a_and1, a_and2);

		Operator b_and1 = And.create(firstNameL, lastNameWeasley);
		Operator b_and2 = And.create(above1980, ravenclaw);
		Operator b_masterAnd = And.create(b_and1, b_and2);

		assertTrue(a_masterAnd.equals(b_masterAnd));
		assertTrue(b_masterAnd.equals(a_masterAnd));

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
		Operator ravenclaw = new In("house",
				Arrays.asList(StudentBean.House.Ravenclaw));
		Operator above1980 = new GreaterThan("birthYear", 1980);
		Operator lastNameWeasley = new EqualTo("lastName", "Weasley");

		Operator a1 = And.create(firstNameL, ravenclaw);
		Operator a2 = Or.create(Not.create(a1), above1980, lastNameWeasley);

		Operator b1 = Or.create(Not.create(firstNameL), Not.create(ravenclaw));
		Operator b2 = Or.create(b1, above1980, lastNameWeasley);

		assertTrue(a2.equals(b2));
		assertTrue(b2.equals(a2));

		Operator c1 = Or.create(lastNameWeasley, Not.create(firstNameL),
				Not.create(ravenclaw));
		Operator c2 = Or.create(c1, above1980);

		assertTrue(a2.equals(c2));
		assertTrue(c2.equals(a2));

		Operator d = Or.create(above1980, lastNameWeasley,
				Not.create(firstNameL), Not.create(ravenclaw));
		assertTrue(a2.equals(d));
		assertTrue(d.equals(a2));

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

		assertTrue(a_firstNameL.equals(b_firstNameL));
		assertTrue(b_firstNameL.equals(a_firstNameL));

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

		assertTrue(f.equals(e));
		assertTrue(e.equals(f));

		Operator g = Not.create(Not.create(a_firstNameL));

		assertTrue(a_firstNameL.equals(g));
		assertTrue(g.equals(a_firstNameL));

		assertFalse(a_firstNameL.equals(f));
		assertFalse(f.equals(a_firstNameL));
	}

	/**
	 * This confirms split() for Like operators are converted to EqualTo
	 * operators.
	 */
	@Test
	public void testSplit_scenario1() {
		Operator houseIn = new In("house", Arrays.asList(
				StudentBean.House.Ravenclaw, StudentBean.House.Hufflepuff));

		Collection<Operator> n1 = houseIn.split();
		assertEquals(2, n1.size());
		assertTrue(n1
				.contains(new EqualTo("house", StudentBean.House.Ravenclaw)));
		assertTrue(n1.contains(new EqualTo("house",
				StudentBean.House.Hufflepuff)));

		// Operator[] n2 = n1.toArray(new Operator[n1.size()]);
		// Operator joined = Or.create(n2);
		// assertTrue(joined.simplify().equals(houseIn));
	}

	/**
	 * This is a variation of scenario1 that includes a Not modifier.
	 */
	@Test
	public void testSplit_scenario2() {
		Operator houseIn = new In("house", Arrays.asList(
				StudentBean.House.Ravenclaw, StudentBean.House.Hufflepuff));

		Operator op = Not.create(houseIn);
		Collection<Operator> n1 = op.split();
		assertEquals(2, n1.size());
		assertTrue(n1.contains(Not.create(new EqualTo("house",
				StudentBean.House.Ravenclaw))));
		assertTrue(n1.contains(Not.create(new EqualTo("house",
				StudentBean.House.Hufflepuff))));

		// Operator[] n2 = n1.toArray(new Operator[n1.size()]);
		// Operator joined = Or.create(n2);
		// assertTrue(joined.simplify().equals(op));
	}

	/**
	 * This tests split() for a a simple tree of 4 OR'ed operators.
	 */
	@Test
	public void testSplit_scenario3() {
		WildcardPattern lStar = new WildcardPattern("L*");
		Operator firstNameL = new Like("firstName", lStar);
		Operator ravenclawIn = new In("house",
				Arrays.asList(StudentBean.House.Ravenclaw));
		Operator ravenclawEqual = new EqualTo("house",
				StudentBean.House.Ravenclaw);
		Operator above1980 = new GreaterThan("birthYear", 1980);
		Operator lastNameWeasley = new EqualTo("lastName", "Weasley");

		Operator or1 = Or.create(Not.create(firstNameL), ravenclawIn);
		Operator or2 = Not.create(Or.create(above1980, lastNameWeasley));
		Operator or3 = Or.create(or1, or2);

		Collection<Operator> n1 = or3.split();
		assertEquals(4, n1.size());
		assertTrue(n1.contains(Not.create(firstNameL)));
		assertTrue(n1.contains(ravenclawEqual));
		assertTrue(n1.contains(Not.create(above1980)));
		assertTrue(n1.contains(Not.create(lastNameWeasley)));
	}

	/**
	 * This is a variation of scenario3 that uses four ANDs instead of ORs
	 */
	@Test
	public void testSplit_scenario4() {
		WildcardPattern lStar = new WildcardPattern("L*");
		Operator firstNameL = new Like("firstName", lStar);
		Operator ravenclawIn = new In("house",
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
	}

	/**
	 * This includes a negated AND which must be converted to 2 elements and an
	 * IN with 2 elements.
	 */
	@Test
	public void testSplit_scenario5() {
		WildcardPattern lStar = new WildcardPattern("L*");
		Operator firstNameL = new Like("firstName", lStar);
		Operator ravenclawSlytherinIn = new In("house", Arrays.asList(
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
	}

	@Test
	public void testSplit_scenario6() {
		WildcardPattern lStar = new WildcardPattern("L*");
		Operator firstNameL = new Like("firstName", lStar);
		Operator ravenclawSlytherinIn = new In("house", Arrays.asList(
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
	}

	@Test
	public void testSplit_scenario7() {
		WildcardPattern lStar = new WildcardPattern("L*");
		Operator firstNameL = new Like("firstName", lStar);
		Operator ravenclawSlytherinIn = new In("house", Arrays.asList(
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
	}

	@Test
	public void testSplit_scenario8() {
		WildcardPattern lStar = new WildcardPattern("L*");
		Operator firstNameL = new Like("firstName", lStar);
		Operator ravenclawSlytherinIn = new In("house", Arrays.asList(
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
	}

	/**
	 * This tests how expressions are simplified.
	 * <p>
	 * The specific examples used here are based on
	 * http://electronics-course.com/boolean-algebra
	 */
	@Test
	public void testSimplify() {
		Operator a = new EqualTo("a", 0);
		Operator b = new EqualTo("b", 0);
		Operator c = new EqualTo("c", 0);
		Operator d = new EqualTo("d", 0);
		Operator e = new EqualTo("e", 0);
		Operator f = new EqualTo("f", 0);

		// first the basic stuff:
		{
			Operator x = Or.create(a, b, a, b, a);
			assertTrue(Or.create(a, b).equals(x));
		}

		{
			Operator x = And.create(a, b, a, b, a);
			assertTrue(And.create(a, b).equals(x));
		}

		{
			Operator x = And.create(a, Not.create(a));
			assertTrue(Operator.FALSE.equals(x));
		}

		{
			Operator x = And.create(b, a, Not.create(a));
			assertTrue(Operator.FALSE.equals(x));
		}

		{
			Operator x = Or.create(a, Not.create(a));
			assertTrue(Operator.TRUE.equals(x));
		}

		{
			Operator x = Or.create(b, a, Not.create(a));
			assertTrue(Operator.TRUE.equals(x));
		}

		{
			Operator x = Or.create(b, And.create(a, Not.create(a)));
			assertTrue(b.equals(x));
		}

		{
			Operator x = Or.create(b, c, And.create(a, Not.create(a)));
			assertTrue(Or.create(b, c).equals(x));
		}

		{
			Operator x = Or.create(b, And.create(c, a, Not.create(a)));
			assertTrue(b.equals(x));
		}

		// from the exercises in the boolean logic tutorial:
		{
			Operator x = Or.create(And.create(a, b),
					And.create(a, Or.create(b, c)),
					And.create(b, Or.create(b, c)));
			Operator simplified = Or.create(b, And.create(a, c));
			assertTrue(x.equals(simplified));
		}

		{
			Operator x = Or.create(
					And.create(Not.create(a), Not.create(b), Not.create(c)),
					And.create(Not.create(a), b, c),
					And.create(a, Not.create(b), Not.create(c)),
					And.create(a, Not.create(b), c), And.create(a, b, c));
			Operator simplified = Or.create(And.create(a, Not.create(b)),
					And.create(Not.create(b), Not.create(c)), And.create(b, c));
			assertTrue(x.equals(simplified));
		}

		// Operator w = Or.create(And.create(b, c), And.create(Not.create(b),
		// c));
		// assertEquals(w.simplify(), c);

	}
}
