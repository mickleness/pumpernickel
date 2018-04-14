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
package com.pump.io.parser.xml;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;

import com.pump.io.parser.Parser;
import com.pump.io.parser.Parser.StringToken;
import com.pump.io.parser.Parser.WhitespaceToken;
import com.pump.io.parser.Token;
import com.pump.io.parser.xml.XMLParser.AssignmentToken;
import com.pump.io.parser.xml.XMLParser.CommentToken;
import com.pump.io.parser.xml.XMLParser.ContentToken;
import com.pump.io.parser.xml.XMLParser.EndCommentToken;
import com.pump.io.parser.xml.XMLParser.EndPrologToken;
import com.pump.io.parser.xml.XMLParser.EndTagToken;
import com.pump.io.parser.xml.XMLParser.StartCommentToken;
import com.pump.io.parser.xml.XMLParser.StartDTDTagToken;
import com.pump.io.parser.xml.XMLParser.StartPrologToken;
import com.pump.io.parser.xml.XMLParser.StartTagToken;
import com.pump.io.parser.xml.XMLParser.TagDeclarationToken;
import com.pump.io.parser.xml.XMLParser.WordToken;

public class XMLParserTest extends TestCase {

	/**
	 * Make sure an unclosed comment doesn't throw a RuntimeException.
	 */
	@Test
	public void testUnclosedComment() throws Exception {
		String xml = "<!--  \n";
		getTokens(xml, false);

	}

	@Test
	public void testDTD() throws Exception {
		// TODO: implement this tests
		String xml = "<!DOCTYPE garden [\n\t<!ELEMENT garden (plants)*>\n\t<!ELEMENT plants (#PCDATA)>\b\t<!ATTLIST plants category CDATA #REQUIRED>\n]>";
		Token[] tokens = getTokens(xml, false);
		int ctr = -1;
		assertToken(tokens[++ctr], "<!", StartDTDTagToken.class);
		assertToken(tokens[++ctr], "DOCTYPE", WordToken.class);
		assertToken(tokens[++ctr], "garden", WordToken.class);
		System.currentTimeMillis();

		xml = "<!DOCTYPE address\n[\n\t<!ELEMENT address (name,company,phone)>\n\t<!ELEMENT name (#PCDATA)>\n\t<!ELEMENT company (#PCDATA)>\n\t<!ELEMENT phone (#PCDATA)>\n]>";
		xml = "<!DOCTYPE address SYSTEM \"address.dtd\">";
	}

	@Test
	public void testComment() throws Exception {
		String xml = "<!-------Your comment----->";
		Token[] tokens = getTokens(xml, false);
		int ctr = -1;
		StartCommentToken start = assertToken(tokens[++ctr], "<!--",
				StartCommentToken.class);
		assertToken(tokens[++ctr], "-----Your comment---", CommentToken.class);
		EndCommentToken end = assertToken(tokens[++ctr], "-->",
				EndCommentToken.class);
		assertMatches(start, end);
		assertEquals(2, ctr);
	}

	private <T extends Token> T assertToken(Token token, String text,
			Class<T> tokenType) {
		assertEquals(text, token.getText());
		assertTrue(tokenType.isInstance(token));
		return (T) token;
	}

	@Test
	public void testPrologTokens() throws Exception {
		Token[] tokens = getTokens("<?xml version = \"1.0\"?>", true);
		int ctr = -1;
		StartPrologToken startToken = assertToken(tokens[++ctr], "<?",
				StartPrologToken.class);
		assertToken(tokens[++ctr], "xml", WordToken.class);
		assertToken(tokens[++ctr], " ", WhitespaceToken.class);
		assertToken(tokens[++ctr], "version", WordToken.class);
		assertToken(tokens[++ctr], " ", WhitespaceToken.class);
		assertToken(tokens[++ctr], "=", AssignmentToken.class);
		assertToken(tokens[++ctr], " ", WhitespaceToken.class);
		assertToken(tokens[++ctr], "\"1.0\"", StringToken.class);
		assertEquals("1.0", ((StringToken) tokens[ctr]).getDecodedString());
		EndPrologToken endToken = assertToken(tokens[++ctr], "?>",
				EndPrologToken.class);
		assertMatches(startToken, endToken);
		assertEquals(8, ctr);
	}

	private void assertMatches(TagDeclarationToken<?> tokenA,
			TagDeclarationToken<?> tokenB) {
		assertEquals(tokenA, tokenB.getMatch());
		assertEquals(tokenB, tokenA.getMatch());
	}

	@Test
	public void testAttributeSingleQuote() throws Exception {
		testAttribute("'");
	}

	private void testAttribute(String quoteChar) throws Exception {
		String xml = "<address category = " + quoteChar + "residence"
				+ quoteChar + "/>";
		Token[] tokens = getTokens(xml, false);
		int ctr = -1;
		StartTagToken startTag = assertToken(tokens[++ctr], "<",
				StartTagToken.class);
		assertToken(tokens[++ctr], "address", WordToken.class);
		assertToken(tokens[++ctr], "category", WordToken.class);
		assertToken(tokens[++ctr], "=", AssignmentToken.class);
		assertToken(tokens[++ctr], quoteChar + "residence" + quoteChar,
				StringToken.class);
		assertEquals("residence",
				((StringToken) tokens[ctr]).getDecodedString());
		EndTagToken endTag = assertToken(tokens[++ctr], "/>", EndTagToken.class);
		assertMatches(startTag, endTag);
		assertEquals(5, ctr);
	}

	@Test
	public void testAttributeDoubleQuote() throws Exception {
		testAttribute("\"");
	}

	@Test
	public void testContentInsideTag() throws Exception {
		String xml = "<address>\n\t1600 Penn Ave\n</address>";
		Token[] tokens = getTokens(xml, false);
		int ctr = -1;
		StartTagToken startA = assertToken(tokens[++ctr], "<",
				StartTagToken.class);
		assertToken(tokens[++ctr], "address", WordToken.class);
		EndTagToken endA = assertToken(tokens[++ctr], ">", EndTagToken.class);
		assertMatches(startA, endA);
		assertToken(tokens[++ctr], "1600 Penn Ave", ContentToken.class);
		StartTagToken startB = assertToken(tokens[++ctr], "</",
				StartTagToken.class);
		assertToken(tokens[++ctr], "address", WordToken.class);
		EndTagToken endB = assertToken(tokens[++ctr], ">", EndTagToken.class);
		assertMatches(startB, endB);
		assertEquals(6, ctr);
	}

	@Test
	public void testContentOutsideTag() throws Exception {
		String xml = "1600 Penn Ave";
		Token[] tokens = getTokens(xml, false);
		int ctr = -1;
		assertToken(tokens[++ctr], "1600 Penn Ave", ContentToken.class);
		assertEquals(0, ctr);
	}

	@Test
	public void testSimpleXML() throws Exception {
		String xml = "<contact-info>\n\t<address category = \"residence\">\n\t\t<name>Tanmay Patil</name>\n\t</address>\n</contact-info>";
		Token[] tokens = getTokens(xml, false);

		int ctr = -1;
		StartTagToken tokenA = assertToken(tokens[++ctr], "<",
				StartTagToken.class);
		assertToken(tokens[++ctr], "contact-info", WordToken.class);
		EndTagToken tokenB = assertToken(tokens[++ctr], ">", EndTagToken.class);
		assertMatches(tokenA, tokenB);

		tokenA = assertToken(tokens[++ctr], "<", StartTagToken.class);
		assertToken(tokens[++ctr], "address", WordToken.class);
		assertToken(tokens[++ctr], "category", WordToken.class);
		assertToken(tokens[++ctr], "=", AssignmentToken.class);
		assertToken(tokens[++ctr], "\"residence\"", StringToken.class);
		assertEquals("residence",
				((StringToken) tokens[ctr]).getDecodedString());
		tokenB = assertToken(tokens[++ctr], ">", EndTagToken.class);
		assertMatches(tokenA, tokenB);

		tokenA = assertToken(tokens[++ctr], "<", StartTagToken.class);
		assertToken(tokens[++ctr], "name", WordToken.class);
		tokenB = assertToken(tokens[++ctr], ">", EndTagToken.class);
		assertMatches(tokenA, tokenB);

		assertToken(tokens[++ctr], "Tanmay Patil", ContentToken.class);

		tokenA = assertToken(tokens[++ctr], "</", StartTagToken.class);
		assertToken(tokens[++ctr], "name", WordToken.class);
		tokenB = assertToken(tokens[++ctr], ">", EndTagToken.class);
		assertMatches(tokenA, tokenB);

		tokenA = assertToken(tokens[++ctr], "</", StartTagToken.class);
		assertToken(tokens[++ctr], "address", WordToken.class);
		tokenB = assertToken(tokens[++ctr], ">", EndTagToken.class);
		assertMatches(tokenA, tokenB);

		tokenA = assertToken(tokens[++ctr], "</", StartTagToken.class);
		assertToken(tokens[++ctr], "contact-info", WordToken.class);
		tokenB = assertToken(tokens[++ctr], ">", EndTagToken.class);
		assertMatches(tokenA, tokenB);

		assertEquals(21, ctr);
	}

	/**
	 * Convert text to an array of Tokens, and verify that the Tokens can be
	 * used to reassemble the original test.
	 * 
	 * @param xml
	 * @return
	 * @throws Exception
	 */
	private Token[] getTokens(String xml, boolean includeWhitespace)
			throws Exception {
		Token[] tokens = getParser().parse(xml, true);
		List<Token> returnValue = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		for (Token token : tokens) {
			sb.append(token.getText());
			if (token instanceof WhitespaceToken) {
				if (includeWhitespace) {
					returnValue.add(token);
				}
			} else {
				returnValue.add(token);
			}
		}
		assertEquals(xml, sb.toString());
		return returnValue.toArray(new Token[returnValue.size()]);
	}

	protected Parser getParser() {
		return new XMLParser();
	}

}