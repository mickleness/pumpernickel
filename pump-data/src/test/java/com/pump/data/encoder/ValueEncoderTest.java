package com.pump.data.encoder;

import junit.framework.TestCase;

import com.pump.data.encoder.ValueEncoder;

public class ValueEncoderTest extends TestCase {

	public void testString() {
		testString("abc");
		testString(null);
		testString("\"Quotes!\'");
		testString("\t\n\r\'\"\\");
	}

	public void testChar() {
		testChar('a');
		testChar(null);
		testChar('\'');
		testChar('\n');
	}
	
	protected void testString(String str) {
		String encoded = ValueEncoder.STRING.encode(str);
		String decoded = ValueEncoder.STRING.parse(encoded);
		assertEquals(str, decoded);
	}
	
	protected void testChar(Character ch) {
		String encoded = ValueEncoder.CHAR.encode(ch);
		Character decoded = ValueEncoder.CHAR.parse(encoded);
		assertEquals(ch, decoded);
	}
}
