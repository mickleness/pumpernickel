package com.pump.xray;

import java.io.File;

import com.pump.desktop.temp.TempFileManager;
import com.pump.io.FileTreeIterator;
import com.pump.io.IOUtils;
import com.pump.io.java.JavaClassSummary;

public class XrayDemo {
	public static void main(String[] args) throws Exception {
		TempFileManager.initialize("xray-demo");
		XrayDemo d = new XrayDemo();
		d.run();
		
		System.out.println("Done.");
		System.exit(0);
	}

	public void run() throws Exception {
		File pumpernickelDir = new File(System.getProperty("user.dir")).getParentFile();
		FileTreeIterator iter = new FileTreeIterator(pumpernickelDir, "java");
		File srcDir = TempFileManager.get().createFile("source", "path");
		srcDir.mkdirs();
		JarBuilder builder = new JarBuilder();
		while(iter.hasNext()) {
			File javaFile = iter.next();
			if(javaFile.getAbsolutePath().contains("pump-release"))
				continue;
			
			JavaClassSummary jcs = new JavaClassSummary(javaFile);
			String classname = jcs.getCanonicalName();
			Class t = Class.forName(classname);
			ClassWriter w = new ClassWriter(builder, t, true);
			File dest = new File(srcDir.getAbsolutePath() + File.separator + classname.replace(".", File.separator)+".java");
			IOUtils.write(dest, w.toString(), false);
		}
		System.currentTimeMillis();
	}
	
	
}