package com.pump.io.parser.html;

import junit.framework.TestCase;
import org.junit.Test;

public class HtmlEncodingTest extends TestCase {

    /**
     * Test encoding & decoding all 0xffff chars
     */
    @Test
    public void testEncodingDecoding_allChars() {
        for (char ch = 0; ch < 0xffff; ch++) {
            String str = Character.toString(ch);
            String encoded = HTMLEncoding.encode(str);
            String decoded = HTMLEncoding.decode(encoded);
            assertEquals("\"" + str + "\" converted to \"" + encoded + "\" (" + ((int)ch) + ")", str, decoded);
        }
    }
}
