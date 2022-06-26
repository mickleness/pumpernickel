/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.io;

import java.io.ByteArrayOutputStream;

import org.junit.Test;

import com.pump.util.JVM;

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
		String lineBreak = JVM.isWindows ? "\r\n" : "\n";
		assertEquals("Outer Text" + lineBreak+"\tMiddle Text"+lineBreak+"\t\tInner Text"+lineBreak+"\tMore Middle Text"+lineBreak+"More Outer Text"+lineBreak, txt);
	}
}