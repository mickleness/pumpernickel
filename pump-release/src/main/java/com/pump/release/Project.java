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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipException;

import com.pump.desktop.temp.TempFileManager;
import com.pump.io.FileTreeIterator;
import com.pump.io.IOUtils;


	
public class Project {
	
	static class MissingPomException extends Exception {
		private static final long serialVersionUID = 1L;

		File directory;
		
		MissingPomException(File directory) {
			super(directory.getAbsolutePath());
			this.directory = directory;
		}
		
		public File getDirectory() {
			return directory;
		}
		
	}
		
		String artifactId;
		String version;
		
		String pomStr;
		File projectDir;
		File pomFile;
		Releases releases;
		List<JarId> dependencies;

		public Project(Releases releases,File dir) throws IOException, MissingPomException {
			projectDir = dir;
			this.releases = releases;
			pomFile = new File(dir, "pom.xml");
			if(!pomFile.exists()) {
				throw new MissingPomException(dir);
			}
			pomStr = IOUtils.read(pomFile);
			artifactId = parse("artifactId", null);
			version = parse("version", null);
			
			if(!dir.getName().equals(artifactId))
				throw new RuntimeException("Directory name \""+dir.getName()+"\" should match artifact id \""+getJarId().artifactId+"\".");
			
		}

		/**
		 * Parse an xml tag from the pom file.
		 */
		private String parse(String tagName, ParsePosition pos) {
			return parse(pomStr, tagName, pos);
		}

		/**
		 * Parse an xml tag from text.
		 * 
		 * @param body the xml text to parse
		 * @param tagName the tag to search for
		 * @param pos the position to being searching at.
		 * @return the parsed xml tag value, or null if no tag was found after the position.
		 */
		private String parse(String body,String tagName, ParsePosition pos) {
			if(pos==null)
				pos = new ParsePosition(0);
			int i1 = body.indexOf("<"+tagName+">", pos.getIndex());
			
			if(i1==-1)
				return null;
			
			int i2 = body.indexOf("</"+tagName+">", i1);
			String str = body.substring(i1 + tagName.length() + 2, i2);
			pos.setIndex(i2 + tagName.length() + 3);
			return str;
		}

		/**
		 * Return the ids of all the dependencies of this project,
		 * according to the project's pom file.
		 */
		public List<JarId> getDependencies() {
			if(dependencies==null) {
				dependencies = new ArrayList<>();
				ParsePosition pos = new ParsePosition(0);
				String str = parse("dependency", pos);
				while(str!=null) {
					String artifactId = parse(str, "artifactId", null);
					String version = parse(str, "version", null);
					dependencies.add(new JarId(artifactId, version));
					
					str = parse("dependency", pos);
				}
			}
			return new ArrayList<>(dependencies);
		}
		
		@Override
		public String toString() {
			return getJarId().toString();
		}
		
		@Override
		public int hashCode() {
			return toString().hashCode();
		}
		
		@Override
		public boolean equals(Object obj) {
			if(!(obj instanceof Project))
				return false;
			return toString().equals(obj.toString());
		}

		public JarId getJarId() {
			return new JarId( artifactId, version );
		}

		/**
		 * Return true if all the dependencies this project requires are
		 * well defined. (This takes into account the required versions of
		 * each dependency.)
		 */
		public boolean areDependenciesDefined() {
			for(JarId dependency : dependencies) {
				File t = releases.getJar(dependency);
				if(t==null || Workspace.DELETED_FILE.equals(t)) {
					return false;
				}
			}
			return true;
		}

		public File buildJar() throws IOException {
			File jarFile = TempFileManager.get().createFile("faven", ".jar");
			
			//TODO: this just dumps the existing compiled code in a jar
			//we need to replace this with logic that actually compiles
			//against the specified dependencies. This will let us stay
			//true to the intention of the dependency version, and it will
			//let us abort if we know there are compiler errors.
			
			File targetDir = new File(projectDir, "target");
			if(!targetDir.exists())
				throw new RuntimeException("\"target\" did not exist in "+projectDir.getAbsolutePath());
			File classesDir = new File(targetDir, "classes");
			if(!classesDir.exists())
				throw new RuntimeException("\"target\\classes\" did not exist in "+projectDir.getAbsolutePath());
			
			Manifest manifest = createManifest();
			Collection<String> entries = new HashSet<>();
			try(FileOutputStream fileOut = new FileOutputStream(jarFile)) {
				try(JarOutputStream jarOut = new JarOutputStream(fileOut, manifest)) {
					FileTreeIterator iter = new FileTreeIterator(classesDir);
					while(iter.hasNext()) {
						File f = iter.next();
						if( (!f.isDirectory()) && (!f.isHidden()) ) {
							String path = f.getAbsolutePath().substring(classesDir.getAbsolutePath().length()+1);
							path = path.replace(File.separatorChar, '/');
							entries.add(path);
							jarOut.putNextEntry(new JarEntry(path));
							IOUtils.write(f, jarOut);
						}
					}
					String pomPath = "META-INF/maven/com.pump/"+artifactId+"/pom.xml";
					entries.add(pomPath);
					jarOut.putNextEntry(new JarEntry(pomPath));
					IOUtils.write(pomFile, jarOut);
					
					String propertiesPath = "META-INF/maven/com.pump/"+artifactId+"/pom.properties";
					entries.add(propertiesPath);
					jarOut.putNextEntry(new JarEntry(propertiesPath));
					Properties p = new Properties();
					p.put("version", version);
					p.put("artifactId", artifactId);
					p.put("groupId", "com.pump");
					p.store(jarOut, null);
					
					for(JarId dependency : getDependencies()) {
						File dependencyJar = releases.getJar(dependency);
						if(dependencyJar==null || Workspace.DELETED_FILE.equals(dependencyJar)) {
							throw new RuntimeException("The dependency \""+dependency+"\" could not be resolved, and this method shouldn't be invoked if dependencies aren't resolvable.");
						}
						
						try(FileInputStream dependentFileIn = new FileInputStream(dependencyJar)) {
							try(JarInputStream dependentJarIn = new JarInputStream(dependentFileIn)) {
								for(JarEntry e = dependentJarIn.getNextJarEntry(); e!=null; e = dependentJarIn.getNextJarEntry()) {
									if(entries.add(e.getName())) {
										try {
											jarOut.putNextEntry(new JarEntry(e.getName()));
										} catch(ZipException e2) {
											System.err.println(dependency+", "+getJarId());
											throw e2;
										}
										IOUtils.write(dependentJarIn, jarOut);
									}
								}
							}
						}
					}
				}
			}
			
			return jarFile;
		}

		private Manifest createManifest() {
            Manifest manifest = new Manifest();
            Attributes attributes = manifest.getMainAttributes();
            attributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");
            attributes.put( new Attributes.Name("Created-By"), "Pump Faven");
            String mainClass = parse("mainClass", null);
            if(mainClass!=null) {
            	attributes.put(Attributes.Name.MAIN_CLASS, mainClass);
            }
            return manifest;
		}	
	}