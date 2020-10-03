package com.pump.text.html.style;

import java.util.List;

import org.junit.Test;

import junit.framework.TestCase;

/**
 * All the example Strings are from real-world examples I grabbed online.
 * (Except for Strings that test error conditions.)
 *
 */
public class CssParserTest extends TestCase {

	/**
	 * Test a rule with one selector and one key/value pair and no whitespace.
	 */
	@Test
	public void testRule_1_singleRuleNoWhiteSpace() {
		List<CssParser.Rule> rules = new CssParser()
				.parseRules("a{color:#FF0000;}");
		assertEquals(1, rules.size());

		CssParser.Rule rule = rules.get(0);
		assertEquals(1, rule.getSelectors().size());
		assertEquals("a", rule.getSelectors().get(0));

		assertEquals(1, rule.getProperties().size());
		assertEquals("#FF0000", rule.getProperties().get("color"));
	}

	/**
	 * Test a rule with one selector and one key/value pair and some whitespace.
	 */
	@Test
	public void testRule_2_singleRuleWhiteSpace() {
		List<CssParser.Rule> rules = new CssParser()
				.parseRules(" body { font-size: 5pt }");
		assertEquals(1, rules.size());

		CssParser.Rule rule = rules.get(0);
		assertEquals(1, rule.getSelectors().size());
		assertEquals("body", rule.getSelectors().get(0));

		assertEquals(1, rule.getProperties().size());
		assertEquals("5pt", rule.getProperties().get("font-size"));
	}

	/**
	 * Test a rule with one selector and multiple properties that end without a
	 * semicolon.
	 */
	@Test
	public void testRule_3_singleRuleMultipleProperties() {
		List<CssParser.Rule> rules = new CssParser()
				.parseRules("a {text-decoration: underline; color: blue}");
		assertEquals(1, rules.size());

		CssParser.Rule rule = rules.get(0);
		assertEquals(1, rule.getSelectors().size());
		assertEquals("a", rule.getSelectors().get(0));

		assertEquals(2, rule.getProperties().size());
		assertEquals("underline", rule.getProperties().get("text-decoration"));
		assertEquals("blue", rule.getProperties().get("color"));
	}

	/**
	 * Test a rule with one selector, and multiple properties that end with a
	 * semicolon.
	 */
	@Test
	public void testRule_4_singleRuleMultiplePropertiesEndWithSemicolon() {
		List<CssParser.Rule> rules = new CssParser().parseRules(
				"p {font-size:10px; font-family: Open Sans; margin-top: 0px; padding-top: 0px;}");
		assertEquals(1, rules.size());

		CssParser.Rule rule = rules.get(0);
		assertEquals(1, rule.getSelectors().size());
		assertEquals("p", rule.getSelectors().get(0));

		assertEquals(4, rule.getProperties().size());
		assertEquals("10px", rule.getProperties().get("font-size"));
		assertEquals("Open Sans", rule.getProperties().get("font-family"));
		assertEquals("0px", rule.getProperties().get("margin-top"));
		assertEquals("0px", rule.getProperties().get("padding-top"));
	}

	/**
	 * Test a rule with a selector that starts with a period.
	 */
	@Test
	public void testRule_5_singleRuleSelectorStartsWithPeriod() {
		List<CssParser.Rule> rules = new CssParser()
				.parseRules(".lilIcon {padding: 2px 4px 2px 0px;}");
		assertEquals(1, rules.size());

		CssParser.Rule rule = rules.get(0);
		assertEquals(1, rule.getSelectors().size());
		assertEquals(".lilIcon", rule.getSelectors().get(0));

		assertEquals(1, rule.getProperties().size());
		assertEquals("2px 4px 2px 0px", rule.getProperties().get("padding"));
	}

	/**
	 * Test a rule with a selector that contains a colon.
	 */
	@Test
	public void testRule_6_singleRuleSelectorContainsColon() {
		List<CssParser.Rule> rules = new CssParser()
				.parseRules(" a:link { color: #0000FF; } ");
		assertEquals(1, rules.size());

		CssParser.Rule rule = rules.get(0);
		assertEquals(1, rule.getSelectors().size());
		assertEquals("a:link", rule.getSelectors().get(0));

		assertEquals(1, rule.getProperties().size());
		assertEquals("#0000FF", rule.getProperties().get("color"));
	}

	/**
	 * Test a rule with several new line characters as whitespace.
	 */
	@Test
	public void testRule_7_singleRuleMultiline() {
		List<CssParser.Rule> rules = new CssParser()
				.parseRules("body {\n" + "  margin: 25px;\n"
						+ "  background-color: rgb(240,240,240);\n"
						+ "  font-family: arial, sans-serif;\n"
						+ "  font-size: 14px;\n" + "}");
		assertEquals(1, rules.size());

		CssParser.Rule rule = rules.get(0);
		assertEquals(1, rule.getSelectors().size());
		assertEquals("body", rule.getSelectors().get(0));

		assertEquals(4, rule.getProperties().size());
		assertEquals("25px", rule.getProperties().get("margin"));
		assertEquals("rgb(240,240,240)",
				rule.getProperties().get("background-color"));
		assertEquals("arial, sans-serif",
				rule.getProperties().get("font-family"));
		assertEquals("14px", rule.getProperties().get("font-size"));
	}

	/**
	 * Test a rule with one selector with a space in it.
	 */
	@Test
	public void testRule_8_singleRuleDescendantSelector() {
		List<CssParser.Rule> rules = new CssParser().parseRules(
				"ul li {padding-bottom:1ex; font-family: Open Sans; font-size:10px; list-style-type: circle;}");
		assertEquals(1, rules.size());

		CssParser.Rule rule = rules.get(0);
		assertEquals(1, rule.getSelectors().size());
		assertEquals("ul li", rule.getSelectors().get(0));

		assertEquals(4, rule.getProperties().size());
		assertEquals("1ex", rule.getProperties().get("padding-bottom"));
		assertEquals("Open Sans", rule.getProperties().get("font-family"));
		assertEquals("10px", rule.getProperties().get("font-size"));
		assertEquals("circle", rule.getProperties().get("list-style-type"));
	}

	/**
	 * Test a rule with multiple selectors
	 */
	@Test
	public void testRule_9_singleRuleCommaSeparatedSelector() {
		List<CssParser.Rule> rules = new CssParser().parseRules(
				"h2, h3, h4 { border-width:3px; border-style:solid;\n"
						+ "border-color:#893712; }");
		assertEquals(1, rules.size());

		CssParser.Rule rule = rules.get(0);
		assertEquals(3, rule.getSelectors().size());
		assertEquals("h2", rule.getSelectors().get(0));
		assertEquals("h3", rule.getSelectors().get(1));
		assertEquals("h4", rule.getSelectors().get(2));

		assertEquals(3, rule.getProperties().size());
		assertEquals("3px", rule.getProperties().get("border-width"));
		assertEquals("solid", rule.getProperties().get("border-style"));
		assertEquals("#893712", rule.getProperties().get("border-color"));
	}

	/**
	 * Test a rule with multiple selectors with spaces.
	 */
	@Test
	public void testRule_10_singleRuleCommaSeparatedWithSpaceSelector() {
		List<CssParser.Rule> rules = new CssParser().parseRules(
				"body *,body *:before,body *:after{box-sizing:inherit}");
		assertEquals(1, rules.size());

		CssParser.Rule rule = rules.get(0);
		assertEquals(3, rule.getSelectors().size());
		assertEquals("body *", rule.getSelectors().get(0));
		assertEquals("body *:before", rule.getSelectors().get(1));
		assertEquals("body *:after", rule.getSelectors().get(2));

		assertEquals(1, rule.getProperties().size());
		assertEquals("inherit", rule.getProperties().get("box-sizing"));
	}

	/**
	 * Test a rule with multiple complex selectors.
	 */
	@Test
	public void testRule_11_singleRuleWeirdSelectors() {
		List<CssParser.Rule> rules = new CssParser()
				.parseRules("pre.s-code-block>code .hljs-subst,\n"
						+ "code[class*=\"language-\"] .hljs-subst {\n"
						+ "	color: var(--highlight-color)\n" + "}");
		assertEquals(1, rules.size());

		CssParser.Rule rule = rules.get(0);
		assertEquals(2, rule.getSelectors().size());
		assertEquals("pre.s-code-block>code .hljs-subst",
				rule.getSelectors().get(0));
		assertEquals("code[class*=\"language-\"] .hljs-subst",
				rule.getSelectors().get(1));

		assertEquals(1, rule.getProperties().size());
		assertEquals("var(--highlight-color)",
				rule.getProperties().get("color"));
	}

	/**
	 * Test parsing multiple sequential rules.
	 */
	@Test
	public void testRule_12_multipleRules() {
		List<CssParser.Rule> rules = new CssParser()
				.parseRules("body {background-color: powderblue;}\n"
						+ "h1   {color: blue;}\n" + "p    {color: red;}");
		assertEquals(3, rules.size());

		CssParser.Rule rule = rules.get(0);
		assertEquals(1, rule.getSelectors().size());
		assertEquals("body", rule.getSelectors().get(0));

		assertEquals(1, rule.getProperties().size());
		assertEquals("powderblue",
				rule.getProperties().get("background-color"));

		rule = rules.get(1);
		assertEquals(1, rule.getSelectors().size());
		assertEquals("h1", rule.getSelectors().get(0));

		assertEquals(1, rule.getProperties().size());
		assertEquals("blue", rule.getProperties().get("color"));

		rule = rules.get(2);
		assertEquals(1, rule.getSelectors().size());
		assertEquals("p", rule.getSelectors().get(0));

		assertEquals(1, rule.getProperties().size());
		assertEquals("red", rule.getProperties().get("color"));
	}

	/**
	 * Test an escape encoded down arrow.
	 */
	@Test
	public void testRule_13_escapedString() {
		List<CssParser.Rule> rules = new CssParser().parseRules(
				"nav a:hover:after {\n" + "    content: \"\\2193\";\n" + "}");
		assertEquals(1, rules.size());

		CssParser.Rule rule = rules.get(0);
		assertEquals(1, rule.getSelectors().size());
		assertEquals("nav a:hover:after", rule.getSelectors().get(0));

		assertEquals(1, rule.getProperties().size());
		assertEquals("\"\u2193\"", rule.getProperties().get("content"));
	}

	/**
	 * Test two different escape encoded strings. In this example the subsequent
	 * text starts with a non-hex character ('m').
	 */
	@Test
	public void testRule_14a_escapedString_variations() {
		// derived from example at
		// https://www.w3.org/International/questions/qa-escapes
		List<CssParser.Rule> rules1 = new CssParser()
				.parseRules("a:{content:\"\\E9motion\"}");
		List<CssParser.Rule> rules2 = new CssParser()
				.parseRules("a:{content:\"\\0000E9motion\"}");

		assertEquals(rules1, rules2);
	}

	/**
	 * Another test of two different escape encoded strings. In this example the
	 * subsequent text starts with a hex character ('d').
	 */
	@Test
	public void testRule_14b_escapedString_variations() {
		// derived from example at
		// https://www.w3.org/International/questions/qa-escapes
		List<CssParser.Rule> rules1 = new CssParser()
				.parseRules("a:{content:\"\\E9 dition\"}");
		List<CssParser.Rule> rules2 = new CssParser()
				.parseRules("a:{content:\"\\0000E9dition\"}");

		assertEquals(rules1, rules2);
	}

	/**
	 * Test an escape-encoded number.
	 */
	@Test
	public void testRule_15_escapedString_selector() {
		// derived from example at
		// https://www.w3.org/International/questions/qa-escapes
		List<CssParser.Rule> rules = new CssParser()
				.parseRules(".\\31 23 { content:na }");

		CssParser.Rule rule = rules.get(0);
		assertEquals(1, rule.getSelectors().size());
		assertEquals(".123", rule.getSelectors().get(0));
	}

	/**
	 * This test uses an equals sign instead of a colon when defining the
	 * key/value pair.
	 */
	@Test
	public void testError_1_incompleteKeyValuePair() {
		try {
			new CssParser().parseRules("a{color=#FF0000;}");
			fail("this CSS used an equal sign instead of a colon; it should have failed.");
		} catch (RuntimeException e) {
			// pass!
		}
	}

	/**
	 * This test omits the closing bracket.
	 */
	@Test
	public void testError_2_noClosingBracket() {
		try {
			new CssParser().parseRules("a{color:#FF0000;");
			fail("this CSS did not close with a bracket");
		} catch (RuntimeException e) {
			// pass!
		}
	}

	/**
	 * This test has two consecutive semicolons as if a key/value pair is
	 * missing.
	 */
	@Test
	public void testError_3_emptyKeyValuePair() {
		try {
			new CssParser().parseRules("a{color:#FF0000;;}");
			fail("this CSS included an empty key/value pair");
		} catch (RuntimeException e) {
			// pass!
		}
	}

	/**
	 * This test has no selector before the first curly bracket.
	 */
	@Test
	public void testError_4_emptySelector() {
		try {
			new CssParser().parseRules("{color:#FF0000;}");
			fail("this CSS did not include a selector");
		} catch (RuntimeException e) {
			// pass!
		}
	}

	/**
	 * This tests a series of random CSS I found while looking at examples
	 * online. The characteristics in these are probably present in other tests.
	 */
	@Test
	public void test_no_parsing_errors() throws Exception {
		// this is just a laundry list of misc css examples I found:
		String[] strings = new String[] {

				"body { font-size: 5pt; font-weight: 2; font-style: bold }",

				"h1 {font-size: 60pt}",

				"h2 {font-size: 50pt }",

				"body {font-family: Open Sans; font-size: 10px;}",

				"h2 {font-size:14px; font-family: Open Sans; margin-bottom: 0px; margin-top: 0px;}",

				"h4 {color: #000000; font-size:10px; font-family: Open Sans; font-weight: bold; margin-bottom: 5px;}",

				"h5 img {margin-right:8px; font-family: Open Sans;}",

				".typeIcon {height: 10px; width: 10px;}",

				"td {vertical-align: top; font-family: Open Sans;}",

				"ul {margin-top:0; margin-bottom:1ex; list-style-image: url(https://www.google.com)}",

				"h2{color:#FBC87A;}",

				".br {height: 1px; line-height: 1px; min-height: 1px;}",

				"img {max-width: 100%; display: block;}",

				"ul{list-style-type:circle;margin:0px 20px;}",

				"a { color: blue; font-style: italic }",

				"body {color:#292929; font-family:Helvetica, Arial, sans-serif; margin: 4px;}",

				"H1 {color: red;  font-size: 120%; font-weight: bold;}",

				"code {font-family: courier; font-size: 22pt}",

				" a:visited { color: #800080; } ",

				" a:active { color: #FF0000; text-decoration: underline; } ",

				"body {font-family:Sans;font-size:12pt;}",

				"h3 {margin:0; padding:0;margin-top:8px;margin-bottom:3px; }",

				"h4 {margin-bottom:1px; margin-top:2ex; padding:0; color:#446699; font-size:12px}",

				"p  {margin-top:0; margin-bottom:2ex; padding:0;}",

				"ul li {padding-bottom:1ex}",

				"li.outPorts {padding-bottom:0px}",

				"ul.param_dep {margin-top:0; margin-bottom:1ex; list-style-type:none; list-style-image:none; }",

				"ul li ul {margin-top:0; margin-bottom:1ex; list-style-type:none; list-style-image:none; }",

				"ul li small ul {margin-top:0; list-style-type:none; list-style-image:none; }",

				"a:hover {text-decoration:underline}",

				"dt  {font-weight:bold;}",

				"a {text-decoration:underline; font-weight:bold;color:blue;}",

				"hr  {color:red; background-color:red}",

				"h2{color:#FBC87A;}",

				"h1 {\n" + "  color: blue;\n" + "  font-family: verdana;\n"
						+ "  font-size: 300%;\n" + "}\n" + "p {\n"
						+ "  color: red;\n" + "  font-family: courier;\n"
						+ "  font-size: 160%;\n" + "}",

				"/* Applies to the entire body of the HTML document (except where overridden by more specific\n"
						+ "selectors). */\n" + "body {\n" + "  margin: 25px;\n"
						+ "  background-color: rgb(240,240,240);\n"
						+ "  font-family: arial, sans-serif;\n"
						+ "  font-size: 14px;\n" + "}\n" + "\n"
						+ "/* Applies to all <h1>...</h1> elements. */\n"
						+ "h1 {\n" + "  font-size: 35px;\n"
						+ "  font-weight: normal;\n" + "  margin-top: 5px;\n"
						+ "}\n" + "\n"
						+ "/* Applies to all elements with <... class=\"someclass\"> specified. */\n"
						+ ".someclass { color: red; }\n" + "\n"
						+ "/* Applies to the element with <... id=\"someid\"> specified. */\n"
						+ "#someid { color: green; }",

				"body {\n" + "  background-color: lightblue;\n" + "}\n" + "\n"
						+ "h1 {\n" + "  color: navy;\n"
						+ "  margin-left: 20px;\n" + "}"

		};

		for (String string : strings) {
			try {
				new CssParser().parseRules(string);
			} catch (Exception e) {
				System.err.println(string);
				throw e;
			}
		}
	}
}
