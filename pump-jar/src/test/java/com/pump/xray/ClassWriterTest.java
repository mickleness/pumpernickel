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
package com.pump.xray;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

import junit.framework.AssertionFailedError;
import junit.framework.TestCase;

import org.junit.Test;

import com.pump.io.IOUtils;
import com.pump.io.IndentedPrintStream;

public class ClassWriterTest extends TestCase {
	
	/**
	 * This is modeled after a compiler issue observed in the ObservableSet
	 * when x-ray first tried to autogenerate those signatures.
	 * <p>
	 * Apparently have a static parameterized class and a nested non-static
	 * parameterized class <em>requires</em> we use the simple class name
	 * (in this case: "Operation") instead of the fully qualified class name
	 * ("com.xyz.DummyClass.Operation")
	 */
	static class DummyClass<T> {

		class Operation<R> {
			public R run() {
				return null;
			}
		}
		
		protected <R> R execute(Operation<R> arg0) {
			return null;
		}
	}

	/**
	 * This is modeled after a compiler issue observed in the ObservableSet
	 * when x-ray first tried to autogenerate those signatures.
	 * <p>
	 * Apparently have a static parameterized class and a nested non-static
	 * parameterized class <em>requires</em> we use the simple class name
	 * (in this case: "Operation") instead of the fully qualified class name
	 * ("com.xyz.DummyClass.Operation")
	 */
	static class DummyClass2 {
		
		public DummyClass2(String... varArgs) {}
		
		public void method(int... varArgs) {}
	}
	
	@Test
	public void testObject() throws Exception {
		assertEquals(Object.class, "Object.snippet");
		assertEquals(CharSequence.class, "CharSequence.snippet");
		assertEquals(DummyClass.class, "DummyClass.snippet");
		assertEquals(DummyClass2.class, "DummyClass2.snippet");
	}
	
	public void assertEquals(Class type, String resourceName) throws Exception {
		String str = null;
		try {
			SourceCodeManager m = new SourceCodeManager();
			m.addClasses(type);
			
			ClassWriter writer;
			if(type.getDeclaringClass()==null) {
				writer = m.build().values().iterator().next();
			} else {
				Class z = type.getDeclaringClass()==null ? type : type.getDeclaringClass();
				writer = m.build().get(z);
				writer = writer.getDeclaredType(type);
			}
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ClassWriterStream cws = new ClassWriterStream(out, true, "UTF-8");
			writer.setJavadocEnabled(false);
			writer.write(cws);
			
			str = new String(out.toByteArray(), "UTF-8");
			URL resource = ClassWriterTest.class.getResource(resourceName);
			String str2;
			try(InputStream in = resource.openStream()) {
				str2 = IOUtils.read(in, "UTF-8");
			}
			assertEquals(str2, str);
		} catch(AssertionFailedError e) {
			System.err.println("Failure for \""+resourceName+"\" ("+type.getName()+")");
			System.err.println(str);
			throw e;
		}
	}
}