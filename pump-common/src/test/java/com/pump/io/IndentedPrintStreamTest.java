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
package com.pump.io;

import java.io.ByteArrayOutputStream;

import org.junit.Test;

import junit.framework.TestCase;

public class IndentedPrintStreamTest extends TestCase {
	
	@Test
	public void testIndentation() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		try(IndentedPrintStream ips = new IndentedPrintStream(out, true, "UTF-8")) {
			ips.println("Outer Text");
			try(AutoCloseable c = ips.indent()) {
				ips.println("Middle Text");
				try(AutoCloseable c2 = ips.indent()) {
					ips.println("Inner Text");
				}
				ips.println("More Middle Text");
			}
			ips.println("More Outer Text");
		}
		
		String txt = new String(out.toByteArray(), "UTF-8");
		assertEquals("Outer Text\r\n\tMiddle Text\r\n\t\tInner Text\r\n\tMore Middle Text\r\nMore Outer Text\r\n", txt);
	}
}