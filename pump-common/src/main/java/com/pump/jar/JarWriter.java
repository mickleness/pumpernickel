/*
 * @(#)JarWriter.java
 *
 * $Date: 2016-06-06 22:21:55 +0500 (Mon, 06 Jun 2016) $
 *
 * Copyright (c) 2011 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Jeremy Wood. For details see accompanying license terms.
 * 
 * This software is probably, but not necessarily, discussed here:
 * https://javagraphics.java.net/
 * 
 * That site should also contain the most recent official version
 * of this software.  (See the SVN repository for more details.)
 */
package com.pump.jar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.pump.io.FileTreeIterator;
import com.pump.io.IOUtils;
import com.pump.io.java.JavaStringHarvester;
import com.pump.util.BufferedPipe;

/** This helps create jars.
*/
public class JarWriter {
	
	private static class FileExistsException extends RuntimeException {
		private static final long	serialVersionUID	= 1L;
	
		File originalFile;
		File newFile;
		String entryName;
		
		public FileExistsException(String msg,String entryName,File originalFile,File newFile) {
			super(msg);
			this.entryName = entryName;
			this.originalFile = originalFile;
			this.newFile = newFile;
		}
		
		public File getNewFile() {
			return newFile;
		}
		
		public String getEntryName() {
			return entryName;
		}
		
		public File getOriginalFile() {
			return originalFile;
		}
	}
	
	private static class AliasFileSystem {
		Map<String, File> pathMap = Collections.synchronizedMap(new TreeMap<String, File>());

		/** Create an empty of an AliasFileSystem. */
		public AliasFileSystem() {}

		/** Create a cloned of an AliasFileSystem. */
		public AliasFileSystem(AliasFileSystem s) {
			pathMap.putAll(s.pathMap);
		}
		
		public boolean addFile(String path,File file) throws IOException {
			assert path!=null;
			assert file!=null;
			
			if(!file.exists())
				throw new IllegalArgumentException("this file does not exist: "+file.getAbsolutePath());
			
			synchronized(pathMap) {
				File existing = pathMap.get(path);
				if(existing!=null) {
					if(IOUtils.equals(existing, file))
						return false;
					System.err.println("existing file: "+existing.getAbsolutePath());
					System.err.println("incoming file: "+file.getAbsolutePath());
					//throw new FileExistsException("the path \""+path+"\" already contained a file that does not match the argument.", path, existing, file);
				}
				pathMap.put(path, file);
				return true;
			}
		}

		public void importStructure(File root,FileFilter filter) throws IOException {
			importFile(root, root, filter);
		}

		private void importFile(File root,File file,FileFilter filter) throws IOException {
			if(file.isDirectory()) {
				for(File child : file.listFiles()) {
					if(filter==null || (filter.accept(child)))
						importFile(root,child,filter);
				}
				return;
			}
			if(filter!=null && (!filter.accept(file)))
				return;
			
			String path = file.getAbsolutePath().substring(root.getAbsolutePath().length());
			if(path.startsWith(File.separator))
				path = path.substring(1);
			
			addFile(path, file);
		}
		
		/** Export certain files from this structure into the root directory provided.
		 * 
		 * @param root the directory to export files to.
		 * @param fileExtensions the file extensions to export. This does not support
		 * wildcards like "*". (It is intended only to really support ".java"...)
		 * @throws IOException
		 */
		public void exportStructure(File root, String... fileExtensions) throws IOException {
			if(!root.isDirectory())
				throw new IllegalArgumentException();
			synchronized(pathMap) {
				String rootPath = root.getAbsolutePath();
				if(!rootPath.endsWith(File.separator))
					rootPath = rootPath+File.separator;
				for(String s : pathMap.keySet()) {
					boolean ok = false;
					for(String ext : fileExtensions) {
						if(s.endsWith(ext)) {
							ok = true;
						}
					}
					if(ok) {
						File newFile = new File(rootPath+s);
						IOUtils.copy( pathMap.get(s), newFile);
					}
				}
			}
		}
	}
	
	/** Make a duplicate of a .jar file that omits certain types of files.
	 * 
	 * @param jarSrc the source jar to copy
	 * @param jarDest the destination to write a new jar to. (If a file already
	 * exists here then an exception is thrown.)
	 * @param extensionToOmit the file extension to exclude from the new jar 
	 * @throws IOException
	 */
	public static void filter(File jarSrc,File jarDest,String extensionToOmit) throws IOException {
		if(!jarSrc.exists())
			throw new IllegalArgumentException();
		
		if(jarDest.exists())
			throw new IOException("the file "+jarDest.getAbsolutePath()+" already exists");
		
		jarDest.getParentFile().mkdirs();
		jarDest.createNewFile();
		
		try(InputStream in = new FileInputStream(jarSrc); 
				OutputStream out = new FileOutputStream(jarDest);
				JarInputStream jIn = new JarInputStream(in);
				JarOutputStream jOut = new JarOutputStream(out)) {
			JarEntry entry = jIn.getNextJarEntry();
			while(entry!=null) {
				String name = entry.getName();
				if(name.endsWith(extensionToOmit)==false) {
					jOut.putNextEntry(entry);
					IOUtils.write(jIn, jOut);
				}
				
				jOut.closeEntry();
				jIn.closeEntry();
				entry = jIn.getNextJarEntry();
			}
			jOut.finish();
		}
	}

	final List<File> sourcepaths = new ArrayList<File>();
	
	/** Other jars this JarWriter should reference. */
	List<File> jars = new ArrayList<File>();
	
	/** Other classpaths this JarWirter should reference.*/
	List<File> classpaths = new ArrayList<File>();
	
	AliasFileSystem additionalResources = new AliasFileSystem();
	
	boolean verbose = true;
	
	/** Adds all the .java files in this directory.
	* As java files are added, they are scanned and
	* other resources may be added, also (such as PNGs, properties files, etc.)
	 * @throws IOException if an IO problem occurs.
	*/
	public void addSourcepath(File sourcepath) throws IOException {
		if(sourcepath==null) throw new NullPointerException();
		
		sourcepaths.add(sourcepath);
	}
	
	public File[] getJars() {
		return jars.toArray(new File[jars.size()]);
	}
	
	/** @return all the sourcepaths this writer includes.
	 */
	public List<File> getSourcepaths() {
		return new ArrayList<File>(sourcepaths);
	}
	
	/** Add a jar to the classpath of this project.
	 * @throws IOException if an IO problem occurs.
	*/
	public void addJar(File jar) throws IOException {
		jars.add(jar);
	}
	
	/** Add a classpath to the classpaths of this writer. */
	public void addClasspath(File k) {
		classpaths.add(k);
	}
	
	/** Add a new resource to this project.
	 * 
	 * @param path the resource name in the jar archive, using File.separator
	 * @param file the file to add. This is not consulted until <code>createJar()</code>
	 * is called.
	 */
	public void addResource(String path,File file) throws IOException {
		additionalResources.addFile(path, file);
	}
	
	/** Creates a new jar.
	* @param classes the classes to compile and put in this jar.
	* @param compilerVersion the java compiler version to use (1.6, 1.8, etc)
	* @param manifest the optional manifest to embed in this jar file.
	* @param jarDest the .jar file to write to.
	* @param filter a filter to control which files are added to the jar. 
	* By default all related files will be added to this jar 
	* (java files, class files, related resources), but you may want
	* to exclude certain files.
	* @param jarChoice an optional model to decide what to do with dependencies.
	* @return text describing what went wrong if compilation problems occurred. This returns null if this method
	* was successful.
	* @throws IOException if an IO problem occurs.
	*/
	public String createJar(String[] classes,float compilerVersion,Manifest manifest,File jarDest,FileFilter filter,MissingJarResponseManager jarChoice) throws IOException {
		final AliasFileSystem inputStructure = new AliasFileSystem();
		
		FileFilter omitClassFilter = new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				String s = pathname.getName();
				//these files/folders are special and usually hidden.
				if(s.startsWith("."))
					return false;
				if(pathname.isHidden())
					return false;
				//sometimes .java and .class files are side-by-side
				if(s.endsWith(".class"))
					return false;
				if(s.endsWith("pom.xml"))
					return false;
				return true;

			}
		};
		
		for(File sourcepath : sourcepaths) {
			inputStructure.importStructure(sourcepath, omitClassFilter);
			//in maven-ish projects, look for the separate "resources" directory:
			String path = sourcepath.getAbsolutePath().replace(File.separator, "/");
			if(path.endsWith("/src/main/java")) {
				File resources = new File(path.replace("/src/main/java", "/src/main/resources").replace("/", File.separator));
				if(resources.exists())
					inputStructure.importStructure(resources, omitClassFilter);
			}
		}
		
		File newClasspath = IOUtils.getUniqueTempFile("classpath");
		if(!newClasspath.mkdir())
			throw new IOException("mkdir failed for "+newClasspath);
		
		try {
			AliasFileSystem outputJarStructure = new AliasFileSystem(additionalResources);
			
			StringBuffer sb = new StringBuffer();
			List<File> sourceFiles = new ArrayList<>();
			for(int a = 0; a<classes.length; a++) {
				String sourcePath = classes[a].replace(".",File.separator)+".java";
				File sourceFile = inputStructure.pathMap.get(sourcePath);
				if(sourceFile==null)
					throw new IOException("Failed to find source for \""+classes[a]+"\" in the given sourcepaths");
				sourceFiles.add(sourceFile);
			}
				
			try {
				String errors = compile(sourceFiles, newClasspath, inputStructure, outputJarStructure, compilerVersion, null, jarChoice);
				if(errors!=null) {
					sb.append(errors+"\n");
				}
			} catch(RuntimeException e) {
				for(int a = 0; a<sourceFiles.size(); a++)
				{
					System.err.println("sourceFile["+a+"] = "+sourceFiles.get(a).getAbsolutePath());
				}
				System.err.println("newClasspath = "+newClasspath.getAbsolutePath());
				throw e;
			}

			if(sb.length()>0) {
				return sb.toString().trim();
			}
			
			if(!jarDest.exists()) {
				if(!jarDest.createNewFile())
					throw new IOException("failed to create "+jarDest.getAbsolutePath());
			}
			try(OutputStream out = new FileOutputStream(jarDest);
					JarOutputStream jOut = manifest==null ? new JarOutputStream(out) : new JarOutputStream(out, manifest) ) {
				Iterator<String> iter = outputJarStructure.pathMap.keySet().iterator();
				while(iter.hasNext()) {
					String resourceName = iter.next();
					File file = outputJarStructure.pathMap.get(resourceName);
					if(filter==null || filter.accept(file)) {
						resourceName = resourceName.replace(File.separator, "/");
						ZipEntry e = new ZipEntry(resourceName);
						jOut.putNextEntry(e);
						IOUtils.write(file, jOut);
						jOut.closeEntry();
					}
				}
				jOut.finish();
			}
		} finally {
			IOUtils.delete(newClasspath);
		}
		return null;
	}
	
	/**
	 * 
	 * @param mainSourceFiles
	 * @param newClasspathRoot
	 * @param input
	 * @param jarOutput
	 * @param javaVersion
	 * @param compiledFiles
	 * @param jarChoice
	 * @return the errors/problems with this compilation, or null if it was successful.
	 * @throws IOException
	 */
	private String compile(List<File> mainSourceFiles,File newClasspathRoot,
			AliasFileSystem input,AliasFileSystem jarOutput,
			float javaVersion,Set<File> compiledFiles,
			MissingJarResponseManager jarChoice) throws IOException {
		if(compiledFiles==null) compiledFiles = new HashSet<File>();
		
		List<File> filesToDelete = new ArrayList<>();
		try {
    		List<File> remainingFiles = new ArrayList<>();
    		for(File mainSourceFile : mainSourceFiles)
    		{
    			if(mainSourceFile.exists()==false)
    				throw new IOException("Main java file not found ("+mainSourceFile.getAbsolutePath()+")");
    
    			if(compiledFiles.contains(mainSourceFile))
    			{
    				System.out.println("Skipping redundant compilation request for "+mainSourceFile.getName());
    			} else {
    				remainingFiles.add(mainSourceFile);
    			}
    		}
    		
    		if(remainingFiles.size()==0)
    			return null;
    		
    		if(!newClasspathRoot.exists())
    			if(!newClasspathRoot.mkdirs())
    				throw new IOException("mkdirs failed for "+newClasspathRoot.getAbsolutePath());
    
    		String allSourcePaths = "";
    		for(File sourcepath : sourcepaths) {
    			if(allSourcePaths.length()>0)
    				allSourcePaths = allSourcePaths+File.pathSeparator;
    			allSourcePaths = allSourcePaths+sourcepath.getAbsolutePath();
    		}
    		
    		String myClasspath = allSourcePaths;
    		for(int a = 0; a<jars.size(); a++) {
    			File file = (File)jars.get(a);
    			myClasspath = myClasspath+File.pathSeparator+file.getAbsolutePath();
    		}
    		for(int a = 0; a<classpaths.size(); a++) {
    			File file = (File)classpaths.get(a);
    			myClasspath = myClasspath+File.pathSeparator+file.getAbsolutePath();
    		}
    
    		List<String> command = new ArrayList<String>();
    
    		command.add(getJavacCommand());
    		if(isVerbose()) {
    			command.add("-verbose");
    		}
    		command.add("-source");
    		command.add(javaVersion+"");
    		command.add("-target");
    		command.add(javaVersion+"");
    		command.add("-d");
    		command.add(newClasspathRoot.getAbsolutePath());
    		if(remainingFiles.size()==1)
    		{
        		command.add("-sourcepath");
        		command.add(allSourcePaths);
        		command.add("-classpath");
        		command.add(myClasspath);
    			command.add(remainingFiles.get(0).getAbsolutePath());
    		}
    		else
    		{
    			//we have multiple files, so dump everything in one directory
    			//and pass "*.java" to the javac command.
    			File tempDir = IOUtils.getUniqueFile(null, "tmpSrcPath", false, true);
				tempDir.mkdirs();
				filesToDelete.add(tempDir);
				for(File remainingFile : remainingFiles)
				{
					IOUtils.copy(remainingFile, new File(tempDir, remainingFile.getName()));
				}
				
	    		command.add("-sourcepath");
        		command.add(allSourcePaths+File.pathSeparator+tempDir.getAbsolutePath());
	    		command.add("-classpath");
	    		command.add(myClasspath);
	    		if(false) {
					command.add(tempDir.getAbsolutePath()+File.separator+"*.java");
	    		} else {
	    			StringBuilder sb = new StringBuilder();
		    		for(File child : tempDir.listFiles()) {
		    			command.add(child.getAbsolutePath());
		    		}
	    		}
    		}
    		command.add("-J-Xmx768M");
    		command.add("-J-Xms256M");
    		
    		ProcessBuilder pb = new ProcessBuilder(command);
    		pb.environment().put("sourcepath", allSourcePaths);
    		System.out.println(command);
    		Process process = pb.start();
    		
    		final List<String> loadedClasses = new ArrayList<String>();
    		final StringBuffer output = new StringBuffer();
    		new BufferedPipe(process.getErrorStream(), System.err, "\t") {
    
    			@Override
    			protected void process(String s) {
    				output.append(s);
    				output.append('\n');
    				if(s.startsWith("[loading ")) {
    					String s2 = s.substring("[loading ".length());
    					s2 = s2.substring(0,s2.length()-1);
    					loadedClasses.add(s2);
    				} else if(s.startsWith("[parsing completed ")==false &&
    						s.startsWith("[checking ")==false &&
    						s.startsWith("[search path for source files: [")==false ) {
    					//System.out.println("\t"+errStream);
    				}
    				super.process(s);
    			}
    			
    		};
    		new BufferedPipe(process.getInputStream(), System.out, "\t");
    		int errorCode;
    		try {
    			errorCode = process.waitFor();
    			if(errorCode!=0)
    				return output.toString();
    		} catch (InterruptedException e1) {
    			throw new RuntimeException(e1);
    		}
    		
    		jarOutput.importStructure(newClasspathRoot, null);
    		
    		String base = newClasspathRoot.getAbsolutePath()+File.separator;
    		//if(base.endsWith(File.separator)==false)
    		//	base = base+File.separator;
    		
    		FileTreeIterator i = new FileTreeIterator(newClasspathRoot,"class");
    		while(i.hasNext()) {
    			File classFile = (File)i.next();
    			String path = classFile.getAbsolutePath();
    			path = path.substring(base.length());
    			path = path.substring(0,path.length()-".class".length());
    			String javaPath = path+".java";
    			
    			File javaFile = input.pathMap.get(javaPath);
    			if(javaFile!=null) { //sometimes this file won't exist (inner classes, for example)
    				jarOutput.addFile(javaPath, javaFile);
    				
    				String z = getPackage(javaFile).replace(".", File.separator);
    				//localSourcepath will end with "/"
    				String localSourcepath = javaFile.getAbsolutePath();
    				localSourcepath = localSourcepath.substring(0, localSourcepath.indexOf( z ));
    				
    				//Now identify resources that need to get added, too:
    				String[] strings = JavaStringHarvester.get(javaFile);
    				
    				stringSearch : for(int a = 0; a<strings.length; a++) {
    					
    					//search once for the string as-is, and the second loop we replace / with \
    					for(int b = 0; b<2; b++) {
    					
    						if(strings[a].trim().length()==0 || strings[a].startsWith("http:"))
    							continue stringSearch;
    						
    						String resourcePath = javaPath.substring(0, javaPath.lastIndexOf('/')+1)+strings[a];
    						if( input.pathMap.get(resourcePath)!=null )
    						{
    							jarOutput.addFile(resourcePath, input.pathMap.get(resourcePath));
    							continue stringSearch;
    						}
    						
    						//search for properties files relative to the java file.
    						//This will resemble "com.bric.swing.resources.DialogFooter"
    						String propertiesPath = strings[a].replace('.',File.separatorChar)+".properties";
    						File propertiesFile = input.pathMap.get(propertiesPath);
    						if(propertiesFile!=null) {
    							
    							//that's great, but remember where there is "MyStrings.properties"
    							//there should also be "MyStrings*.properties", such as:
    							//"MyStrings_fr.properties" or "MyStrings_es.properties"
    							
    							File propertiesDirectory = propertiesFile.getParentFile();
    							int k = propertiesFile.getName().lastIndexOf(".properties");
    							for(File child : propertiesDirectory.listFiles()) {
    								if(child.getName().startsWith( propertiesFile.getName().substring(0,k)) &&
    										child.getName().endsWith( propertiesFile.getName().substring(k)) ) {
    									int m = propertiesFile.getAbsolutePath().length() - propertiesPath.length();
    									String childPath = child.getAbsolutePath().substring( m );
    									jarOutput.addFile(childPath , child);
    								}
    							}
    							continue stringSearch;
    						}
    						
    						//search for java files accessed by reflection:
    						//(for example: UI's are often specified only by class name, not by a hard reference the compiler will notice)
    						String classPath = strings[a].replace('.',File.separatorChar);
    						File newClassFile = new File(base+classPath+".class");
    						if(!newClassFile.exists()) {
    							String s = classPath+".java";
    							for(File sourcepath : sourcepaths) {
    								File newJavaFile = new File(sourcepath.getAbsolutePath()+File.separator+s);
    								if(newJavaFile.exists()) {
    									compile( Arrays.asList(newJavaFile), newClasspathRoot, input, jarOutput, javaVersion, compiledFiles, jarChoice);
    									continue stringSearch;
    								}
    							}
    							
    							if(strings[a].contains(".") && strings[a].length()>5) {
    								/* If code points to a resource (like a png), in rare cases it may live
    								 * in another package. Search everything!
    								 */
    								String q = strings[a].replace('/', File.separatorChar);
    								for(String k : input.pathMap.keySet()) {
    									if(k.endsWith(q)) {
    										jarOutput.addFile(k, input.pathMap.get(k));
    										continue stringSearch;
    									}
    								}
    							}
    						}
    						strings[a] = strings[a].replace('/', '\\');
    					}
    				}
    			}
    		}
    		
    		/* Now we're going to look at all loaded classes. This is our opportunity
    		 * to either compile dependencies (if we have the source for them),
    		 * or figure out what to do if the dependencies come from jars. In that 
    		 * case we generally have 3 options, as outlined in the JarDependencyChoice.Behavior
    		 * enum. If a Behavior is unspecified: we'll have to ask the user what they
    		 * want to see happen.
    		 */
    		Map<File, MissingJarResponse> requiredJars = new HashMap<File, MissingJarResponse>();
    		Map<File, Set<String>> requiredPaths = new HashMap<File, Set<String>>();
    		boolean anyUnknownBehaviors = false;
    		if(jarChoice==null)
    			jarChoice = new MissingJarConstantResponseManager( MissingJarResponse.BUNDLE_ENTIRE_JAR, false );
    		
    		processLoadedClasses : for(int a = 0; a<loadedClasses.size(); a++) {
    			String s = (String)loadedClasses.get(a);
    			if(s.indexOf('(')!=-1) {
    				String path = s.substring(s.indexOf('(')+1);
    				path = path.substring(0,path.indexOf(')'));
    				path = path.replace(':', File.separatorChar);
    				
    
    				File compiledClassFile = new File(base+path);
    				File sourceFile = input.pathMap.get(path.substring(0, path.length()-".class".length())+".java");
    				if(sourceFile!=null && (compiledClassFile.exists()==false)) {
    					compile( Arrays.asList(sourceFile), newClasspathRoot, input, jarOutput, javaVersion, compiledFiles, jarChoice);
    					continue processLoadedClasses;
    				} else if(sourceFile==null && (compiledClassFile.exists()==false)) {
    					//see if we can extract only the required files from a jar
    					if(s.indexOf('[')!=-1) {
    						String s2 = s.substring(s.indexOf('[')+1);
    						s2 = s2.substring(0, s2.indexOf('('));
    						File jarFile = new File(s2);
    						if((s2.endsWith(".jar") || s2.endsWith(".zip") && jarFile.exists())) {
    							Set<String> paths = requiredPaths.get(jarFile);
    							if(paths==null) {
    								paths = new HashSet<String>();
    								requiredPaths.put(jarFile, paths);
    							}
    							paths.add(path);
    							MissingJarResponse b = jarChoice.getBehavior(jarFile, true);
    							if(b==null) {
    								anyUnknownBehaviors = true;
    							} else {
    								requiredJars.put(jarFile, b);
    							}
    						}
    					}
    				}
    			}
    		}
    		
    		for(File jarFile : JarWriter.this.jars) {
    			MissingJarResponse b = jarChoice.getBehavior(jarFile, false);
    			if(b!=null) {
    				requiredJars.put(jarFile, b);
    			}
    		}
    		
    		if(anyUnknownBehaviors) {
    			jarChoice.resolveBehaviors(jarChoice, requiredJars, requiredPaths.keySet(), remainingFiles.get(0).getName());
    		}
    		
    		for(File jarFile : requiredJars.keySet()) {
    			MissingJarResponse b = requiredJars.get(jarFile);
    			if(MissingJarResponse.IGNORE.equals(b)) {
    				//ignore it: nothing to do here
    			} else if(MissingJarResponse.BUNDLE_ENTIRE_JAR.equals(b)) {
    				extractJar(jarFile, jarOutput, newClasspathRoot, null);
    			} else if(MissingJarResponse.BUNDLE_ONLY_REQUIRED_CLASSES.equals(b)) {
    				Set<String> paths = requiredPaths.get(jarFile);
    				extractJar(jarFile, jarOutput, newClasspathRoot, paths);
    			}
    		}
    	} finally {
        	for(File fileToDelete : filesToDelete) {
        		IOUtils.delete(fileToDelete);
        	}
        }
		return null;
	}
	
	protected String getJavacCommand()
	{
		return "javac";
	}

	/**
	 * 
	 * @param jarFile the jar file to extract entries from
	 * @param fileStructure the file structure to add the zip entries to
	 * @param dir the directory zip entries should be unzipped into. This file should be deleted later.
	 * @param entryNames if null then all zip entries are copied, if non-null then only these zip entries are copied.
	 * @throws IOException
	 */
	private void extractJar(File jarFile,AliasFileSystem fileStructure,File dir,Set<String> entryNames) throws IOException {
		System.out.println("Transferring: "+jarFile.getAbsolutePath());
		try(FileInputStream in = new FileInputStream(jarFile);
			ZipInputStream zIn = new ZipInputStream(in)) {
			ZipEntry e = zIn.getNextEntry();
			while(e!=null) {
				if( (!e.isDirectory()) && (!e.getName().contains("META-INF/"))) {
					System.out.println("\tCopying: "+e.getName());
					if(entryNames==null || entryNames.contains(e.getName())) {
						File tmp = IOUtils.getUniqueFile(dir, e.getName().replace(File.separator, "-"), false, true);

						if(!tmp.getParentFile().exists()) {
							if(!tmp.getParentFile().mkdirs()) {
								throw new IOException("mkdirs failed for "+tmp.getParentFile().getAbsolutePath());
							}
						}
						
						if(!tmp.createNewFile())
							throw new IOException("createNewFile failed for "+tmp.getAbsolutePath());
						try(OutputStream out = new FileOutputStream(tmp)) {
							IOUtils.write(zIn, out);
						}
						String entryName = e.getName().replace("/", File.separator);
						try {
							fileStructure.addFile(entryName, tmp);
						} catch(FileExistsException e2) {
							if(e2.getEntryName().endsWith("LICENSE.txt")) {
								entryName = "LICENSE ("+jarFile.getName()+").txt";
								fileStructure.addFile(entryName, tmp);
							} else if(e2.getEntryName().startsWith("org\\apache\\") ) {
								System.err.println("Duplicate entries noted but ignored:");
								e2.printStackTrace();
							} else {
								throw e2;
							}
						}
					}
				}
				e = zIn.getNextEntry();
			}
		}
	}
	
	/** Create a Manifest file.
	 * 
	 * @param mainClass an optional entry point for the application.
	 * @param sealed an optional sealed attribute.
	 * @return a Manifest file using the attributes provided.
	 */
	public static Manifest createManifest(String mainClass,Boolean sealed) {
		Manifest manifest = new Manifest();
		manifest.getMainAttributes().putValue("Manifest-Version", "1.0");
		if(mainClass!=null) {
			if(mainClass.endsWith(".java"))
				mainClass = mainClass.substring(0, mainClass.length()-".java".length());
			if(mainClass.endsWith(".class"))
				mainClass = mainClass.substring(0, mainClass.length()-".class".length());
			mainClass = mainClass.replace('/', '.');
			manifest.getMainAttributes().putValue("Main-Class", mainClass);
		}
		if(sealed!=null)
			manifest.getMainAttributes().putValue("Sealed", sealed.toString());
		return manifest;
	}
	
	/** Returns all the sourcepaths in the directory provided.
	*/
	public static File[] getSourcepaths(File directory) {
		Set<File> returnValue = new TreeSet<File>();
		getSourcepaths(directory, returnValue);
		return returnValue.toArray(new File[returnValue.size()]);
	}
	
	private static File getSourcepaths(File file,Set<File> knownSourcepaths) {
		if(file.isDirectory()) {
			File[] children = file.listFiles();
			for(File child : children) {
				File returnValue = getSourcepaths(child, knownSourcepaths);
				if(returnValue!=null) {
					if(returnValue.equals(file)) {
						return null;
					}
					return returnValue;
				}
			}
			return null;
		}
		
		if(file.getName().toLowerCase().endsWith(".java")) {
			String path = getPackage(file);
			if(path==null)
				throw new NullPointerException("no package for \""+file.getAbsolutePath()+"\"");
			path = path.replace(".", File.separator);
			String s = file.getAbsolutePath();
	
			String[] packageTerms = split(path, File.separator);
			s = s.substring(0,s.length()-path.length()-file.getName().length()-1);
			File sourcepath = new File(s);
			boolean problem = !sourcepath.exists();
			validateDirectoryStructure : {
				File t = sourcepath;
				for(int a = 0; a<packageTerms.length && (!problem); a++) {
					t = new File(t, packageTerms[a]);
					if(!t.exists())
						problem = true;
				}
			}
				
			if(problem) {
				System.err.println("JarWriter: Warning: the file \""+file.getAbsolutePath()+"\" had a package \""+path+"\" that did not match the local file system.");
			} else {
				if(knownSourcepaths.add(sourcepath))
					return sourcepath;
			}
		}
		return null;
	}
	
	private static String[] split(String path, String separator) {
		List<String> returnValue = new ArrayList<String>();
		int i;
		while( (i = path.indexOf(separator)) !=-1) {
			returnValue.add(path.substring(0,i));
			path = path.substring(i+separator.length());
		}
		returnValue.add(path);
		return returnValue.toArray(new String[ returnValue.size() ]);
	}

	/** Returns the class name for a given .java file.
	 * For example, given the file "Thread.java", this
	 * should return "java.lang.Thread".
	 * 
	 * @param javaFile the java file to query.
	 * @return the class name for a given .java file.
	 */
	public static String getClassName(File javaFile) {
		String packageName = getPackage(javaFile);
		String fileName = javaFile.getName();
		int i = fileName.lastIndexOf('.');
		if(i!=-1) {
			fileName = fileName.substring(0,i);
		}
		return packageName+"."+fileName;
	}

	/** Returns the package name of a file.  This string is declared
	 * towards the beginning of a file.  If this file
	 * begins with "package com.foo" then this method returns "com.foo".
	 * 
	 * @param file
	 * @return the package name of a file.
	 */
	public static String getPackage(File file) {
		try(InputStream in = new FileInputStream(file); 
				BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
			String s = br.readLine();
			while(s!=null) {
				s = s.trim();
				if(s.startsWith("package ") || s.startsWith("package\t")) {
					s = s.substring("package".length()).trim().toLowerCase();
					s = s.substring(0,s.length()-1); //semicolon
					return s;
				}
				s = br.readLine();
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/** Define whether we'll pass "verbose" as an argument to javac.
	 * 
	 * @param b whether we'll pass "verbose" as an argument to javac.
	 */
	public void setVerbose(boolean b)
	{
		verbose = b;
	}
	
	/** Return whether we'll pass "verbose" as an argument to javac.
	 * 
	 * @return whether we'll pass "verbose" as an argument to javac.
	 */
	public boolean isVerbose() {
		return verbose;
	}
}
