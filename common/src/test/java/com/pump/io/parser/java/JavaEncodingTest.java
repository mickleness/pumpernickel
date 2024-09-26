package com.pump.io.parser.java;

import com.pump.io.parser.html.HTMLEncoding;
import junit.framework.TestCase;
import org.junit.Test;

public class JavaEncodingTest extends TestCase {

    /**
     * Test encoding & decoding all 0xffff chars
     */
    @Test
    public void testEncodingDecoding_allChars() {
        for (char ch = 0; ch < 0xffff; ch++) {
            String str = Character.toString(ch);
            String encoded = JavaEncoding.encode(str);
            String decoded = JavaEncoding.decode(encoded);
            assertEquals("\"" + str + "\" converted to \"" + encoded + "\" (" + ((int)ch) + ")", str, decoded);
        }
    }
}
