package com.pump.xray;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;

import org.junit.Test;

import com.pump.io.IOUtils;
import com.pump.io.IndentedPrintStream;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

public class ClassWriterTest extends TestCase {
	
	@Test
	public void testObject() throws Exception {
		assertEquals(Object.class, "Object.snippet");
		assertEquals(CharSequence.class, "CharSequence.snippet");
	}
	
	public void assertEquals(Class type, String resourceName) throws Exception {
		try {
			ClassWriter writer = new ClassWriter(null, type, true);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			IndentedPrintStream ips = new IndentedPrintStream(out, true, "UTF-8");
			writer.write(ips, true);
			
			String str = new String(out.toByteArray(), "UTF-8");
			URL resource = ClassWriterTest.class.getResource(resourceName);
			String str2;
			try(InputStream in = resource.openStream()) {
				str2 = IOUtils.read(in);
			}
			str2 = str2.replace("\n", "\r\n");
			assertEquals(str2, str);
		} catch(AssertionFailedError e) {
			System.err.println("Failure for \""+resourceName+"\" ("+type.getName()+")");
			throw e;
		}
	}
}
