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

import java.io.File;
import java.io.FileOutputStream;

import com.pump.desktop.temp.TempFileManager;
import com.pump.io.FileTreeIterator;
import com.pump.io.IOUtils;
import com.pump.io.java.JavaClassSummary;

public class XrayDemo {
	public static void main(String[] args) throws Exception {
		TempFileManager.initialize("xray-demo");
		XrayDemo d = new XrayDemo();
		try {
			d.run();
			
			System.out.println("Done.");
		} finally {
			System.exit(0);
		}
	}

	public void run() throws Exception {
		File pumpernickelDir = new File(System.getProperty("user.dir")).getParentFile();
		FileTreeIterator iter = new FileTreeIterator(pumpernickelDir, "java");
		File srcDir = TempFileManager.get().createFile("source", "path");
		srcDir.mkdirs();
		SourceCodeManager sourceCodeManager = new SourceCodeManager();
		while(iter.hasNext()) {
			File javaFile = iter.next();
			if(javaFile.getAbsolutePath().contains("pump-release"))
				continue;
			
			JavaClassSummary jcs = new JavaClassSummary(javaFile);
			String classname = jcs.getCanonicalName();
			Class<?> t = Class.forName(classname);
			sourceCodeManager.addClasses(t);
		}
		
		JarBuilder jarBuilder = new JarBuilder(sourceCodeManager);
		File jarFile = IOUtils.getUniqueFile(new File(System.getProperty("user.home")), "xray-demo.jar", false, false);
		try(FileOutputStream out = new FileOutputStream(jarFile)) {
			jarBuilder.write(out);
		}
		System.out.println("Wrote "+jarFile.getAbsolutePath());
	}
}