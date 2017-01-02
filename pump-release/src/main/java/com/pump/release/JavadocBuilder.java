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
package com.pump.release;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import com.pump.desktop.temp.TempFileManager;
import com.pump.io.FileTreeIterator;
import com.pump.io.IOUtils;
import com.pump.io.Token;
import com.pump.io.java.JavaParser;
import com.pump.util.BufferedPipe;

public class JavadocBuilder {
	
	class JavaFile {
		String packageName = "";
		File file;
		String artifactId;
		
		JavaFile(File file) throws IOException {
			this.file = file;
			
			Token[] tokens = JavaParser.parse(IOUtils.read(file), false);
			for(int a = 0; a<tokens.length; a++) {
				if(tokens[a].getText().equals("package")) {
					readPackageName : for(int b = a+1; b<tokens.length; b++) {
						if(tokens[b].getText().equals(";")) {
							break readPackageName;
						} else {
							packageName += tokens[b].getText();
						}
					}
					break;
				}
			}
			
			File t = file;
			while(t!=null) {
				if(t.getName().startsWith("pump-")) {
					artifactId = t.getName();
					break;
				}
				t = t.getParentFile();
			}
		}
		
		@Override
		public String toString() {
			return artifactId+"/"+packageName+"/"+file.getName();
		}
	}
	
	Collection<JavaFile> javaFiles = new HashSet<>();
	
	private void buildSourcePath(Workspace workspace,File tempSourcePath) throws IOException {
		FileTreeIterator iter = new FileTreeIterator(workspace.getDirectory(), "java");
		while(iter.hasNext()) {
			File file = iter.next();
			JavaFile javaFile = new JavaFile(file);
			
			if( "pump-release".equals(javaFile.artifactId) || 
					(!javaFile.packageName.startsWith("com.pump")) ||
					javaFile.file.getAbsolutePath().contains(File.separator+"src"+File.separator+"test"+File.separator)
					)
				continue;
			javaFiles.add(javaFile);
		}
		
		for(JavaFile javaFile : javaFiles) {
			String[] packageTerms = javaFile.packageName.split("\\.");
			File parent = tempSourcePath;
			for(String term : packageTerms) {
				parent = new File(parent, term);
				if(!parent.exists()) {
					if(!parent.mkdirs())
						throw new IOException("File.mkdirs failed for "+parent.getAbsolutePath());
				}
			}
			File newFile = new File(parent, javaFile.file.getName());
			try(FileOutputStream fileOut = new FileOutputStream(newFile)) {
				IOUtils.write(javaFile.file, fileOut);
			}
		}
	}
	
	private String getPackageNames() {
		Collection<String> c = new HashSet<>();
		for(JavaFile javaFile : javaFiles) {
			c.add(javaFile.packageName);
		}
		
		String str = "";
		for(String p : c) {
			if(str.length()>0)
				str += " ";
			str += p;
		}
		return str;
	}
	
	public void run(Workspace workspace) throws Exception {
		File tempSourcePath = TempFileManager.get().createFile("src", "path");
		File javadocDest = TempFileManager.get().createFile("java", "doc");
		if(!tempSourcePath.mkdirs())
			throw new IOException("File.mkdirs failed for "+tempSourcePath.getAbsolutePath());
		if(!javadocDest.mkdirs())
			throw new IOException("File.mkdirs failed for "+tempSourcePath.getAbsolutePath());
		buildSourcePath(workspace, tempSourcePath);
		
		String packageNames = getPackageNames();

		StringBuffer srcPathsString = new StringBuffer(tempSourcePath.getAbsolutePath());
		
		//String tagletPath = "bin";
		//String cmd = "javadoc -J-mx140m"+
		//		" -d "+destDir+
        //        " -tagletPath "+tagletPath +
        //        " -taglet com.bric.blog.SampleTaglet" +
		//		" -classpath "+srcPathsString+File.pathSeparator+System.getProperty("java.class.path")+
		//		" "+packageNames;
		String cmd = "javadoc -J-mx140m -d "+javadocDest.getCanonicalPath()+" -classpath "+srcPathsString+File.pathSeparator+System.getProperty("java.class.path")+" "+packageNames;
		System.out.println(cmd);
		Process process = Runtime.getRuntime().exec( cmd );

		new BufferedPipe(process.getErrorStream(), System.err, "\t");
		new BufferedPipe(process.getInputStream(), System.out, "\t");
		
		int status = process.waitFor();
		if(status==0) {
			System.out.println("javadoc finished");
		} else {
			System.out.println("javadoc finished, status "+status);
		}
		
		File docsDir = new File(workspace.getDirectory(), "docs");
		if(!docsDir.exists())
			throw new RuntimeException(docsDir.getAbsolutePath()+" does not exist");
		File javadocDir = new File(docsDir, "javadoc");
		if(!javadocDir.exists())
			throw new RuntimeException(javadocDir.getAbsolutePath()+" does not exist");
		
		copy(javadocDest, javadocDir);
			
		//formatHTML(destDir);
	}

	private void copy(File srcDir, File destDir) throws Exception {
		Collection<String> processed = new HashSet<>();
		FileTreeIterator iter = new FileTreeIterator(srcDir);
		while(iter.hasNext()) {
			File f = iter.next();
			if(f.isDirectory())
				continue;
			
			String path = f.getAbsolutePath().substring(srcDir.getAbsolutePath().length() + 1);
			processed.add(path);
			
			File t = new File(destDir.getAbsolutePath() + File.separator + path);
			if(!t.getParentFile().exists()) {
				if(!t.getParentFile().mkdirs())
					throw new IOException("File.mkdirs failed for "+t.getParentFile());
			}
			IOUtils.copy(f, t, true);
		}
		
		iter = new FileTreeIterator(destDir);
		while(iter.hasNext()) {
			File f = iter.next();
			if(f.isDirectory())
				continue;
			
			String path = f.getAbsolutePath().substring(destDir.getAbsolutePath().length() + 1);
			if(!processed.contains(path)) {
				if(!f.delete())
					throw new IOException("File.delete() failed for "+f.getAbsolutePath());
			}
		}
	}
}