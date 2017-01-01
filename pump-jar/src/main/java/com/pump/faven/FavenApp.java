package com.pump.faven;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeSet;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipException;

import com.pump.io.FileTreeIterator;
import com.pump.io.IOUtils;

/**
 * Faven is a temporary stopgap to build jars that resemble the hierarchy
 * used in the Pumpernickel project that properly configured Maven pom
 * files would generate.
 *
 */
public class FavenApp {
	
	public static void main(String[] args) {
		FavenApp app = new FavenApp();
		app.run();
	}
	
	/**
	 * This comparator splits strings by a period and evaluates each term.
	 * <p>
	 * For example:
	 * "1.01.02" vs "1.01.2" will be split into 3 terms. The first two are equal,
	 * and this comparator will sort the two terms as "02".compareTo("2")
	 */
	public static final Comparator<String> VERSION_COMPARATOR = new Comparator<String>() {

		@Override
		public int compare(String o1, String o2) {
			String[] t1 = o1.split("\\.");
			String[] t2 = o1.split("\\.");
			for(int a = 0; a<Math.max(t1.length, t2.length); a++) {
				if(a<t1.length && a<t2.length) {
					int k = t1[a].compareTo(t2[a]);
					if(k!=0)
						return k;
				} else if(a<t1.length) {
					//"1.0.x" vs "1.0"
					return 1;
				} else if(a<t2.length) {
					return -1;
				}
			}
			return o1.compareTo(o2);
		}
		
	};
	
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
	
	/**
	 * This identifies the artifact ID and version of a jar.
	 */
	class JarId {
		public final String artifactId, version;
		
		JarId(String artifactId, String version) {
			this.artifactId = artifactId;
			this.version = version;
		}
		
		@Override
		public int hashCode() {
			return toString().hashCode();
		}
		
		@Override
		public String toString() {
			return artifactId+":"+version;
		}
		
		@Override
		public boolean equals(Object t) {
			if(!(t instanceof JarId))
				return false;
			return toString().equals(t.toString());
		}
	}
	
	class Project {
		
		String artifactId;
		String version;
		
		String pomStr;
		File projectDir;
		File pomFile;
		
		List<JarId> dependencies;

		public Project(File dir) throws IOException, MissingPomException {
			projectDir = dir;
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
				File t = releases.get(dependency);
				if(t==null || DELETED_FILE.equals(t)) {
					return false;
				}
			}
			return true;
		}

		public File buildJar() throws IOException {
			File jarFile = File.createTempFile("faven", ".jar");
			tempFiles.add(jarFile);
			
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
						File dependencyJar = releases.get(dependency);
						if(dependencyJar==null || DELETED_FILE.equals(dependencyJar)) {
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
	
	/** A placeholder object a file that should be deleted/missing. */
	static File DELETED_FILE = new File("deleted file");
	
	/** All files that should be deleted when {@link #run()} exits. */
	Collection<File> tempFiles = new LinkedList<>();
	/** The directory for the pump-release project. */
	File releaseProjectDir;
	/** All known projects (based on the file system). */
	List<Project> projects = new ArrayList<>();
	/** A map of JarIds to their released jar files. */
	Map<JarId, File> releases = new HashMap<>();
	
	/**
	 * Populates the pump-release directory with the appropriate jars,
	 * poms, and metadata files.
	 */
	public void run() {
		try {
			File pumpDir = getPumpernickelDirectory();
			
			for(File child : pumpDir.listFiles()) {
				if(child.isDirectory() && child.getName().startsWith("pump-")) {
					try {
						Project p = new Project(child);
						projects.add(p);
					} catch(MissingPomException e) {
						if(child.getName().equals("pump-release")) {
							releaseProjectDir = e.getDirectory();
						} else {
							e.printStackTrace();
						}
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			
			buildReleaseMap();
			
			for(Project project : projects) {
				project.getDependencies();
				
				//act like this jar doesn't exist yet so we can rebuild it
				if(releases.containsKey(project.getJarId()))
					releases.put(project.getJarId(), DELETED_FILE);
			}
			
			List<Project> remainingProjects = new LinkedList<>( projects );
			while(remainingProjects.size()>0) {
				int startingSize = remainingProjects.size();
				
				try {
					Iterator<Project> iter = remainingProjects.iterator();
					buildJar : while(iter.hasNext()) {
						Project p = iter.next();
						if(p.areDependenciesDefined()) {
							File jar = p.buildJar();
							releases.put(p.getJarId(), jar);
							iter.remove();
							break buildJar;
						}
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
				
				if(remainingProjects.size()==startingSize) {
					throw new RuntimeException("Unable to resolve dependencies for: "+remainingProjects);
				}
			}
			
			try {
				writeReleasesFromMap();
				writePomAndMetaData();
			} catch(IOException e) {
				e.printStackTrace();
			}
		} finally {
			for(File tempFile : tempFiles) {
				tempFile.delete();
			}
		}
	}
	
	/**
	 * Assuming that all jars are in place, this writes the adjacent pom and metadata.xml files.
	 */
	private void writePomAndMetaData() throws IOException {
		FileTreeIterator iter = new FileTreeIterator(releaseProjectDir, "jar");
		Map<String, TreeSet<String>> artifactToVersionMap = new HashMap<>();
		while(iter.hasNext()) {
			File jarFile = iter.next();
			String version = jarFile.getParentFile().getName();
			String artifactId = jarFile.getParentFile().getParentFile().getName();
			String pomFileName = artifactId+"-"+version+".pom";
			File pomFile = new File(jarFile.getParentFile(), pomFileName);
			File src = extractPomFromJar(jarFile, artifactId);
			if(src==null)
				throw new NullPointerException("No pom.xml file found for "+artifactId+":"+version);
			tempFiles.add(src);
			boolean existed = pomFile.exists();
			if(IOUtils.copy(src, pomFile, true)) {
				if(existed) {
					System.out.println("Replaced "+pomFile.getAbsolutePath());
				} else {
					System.out.println("Added "+pomFile.getAbsolutePath());
				}
			} else {
				System.out.println("Skipped "+pomFile.getAbsolutePath()+" (no changes)");
			}
			
			TreeSet<String> versions = artifactToVersionMap.get(artifactId);
			if(versions==null) {
				versions = new TreeSet<>(VERSION_COMPARATOR);
				artifactToVersionMap.put(artifactId, versions);
			}
			versions.add(version);
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		for(Entry<String, TreeSet<String>> entry : artifactToVersionMap.entrySet()) {
			StringBuilder sb = new StringBuilder();
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
			sb.append("<metadata>\n");
			sb.append("\t<groupId>com.pump</groupId>\n");
			sb.append("\t<artifactId>"+entry.getKey()+"</artifactId>\n");
			sb.append("\t<versioning>\n");
			sb.append("\t\t<release>"+entry.getValue().last()+"</release>\n");
			sb.append("\t\t<versions>\n");
			for(String v : entry.getValue()) {
				sb.append("\t\t\t<version>"+v+"</version>\n");
			}
			sb.append("\t\t<lastUpdated>"+dateFormat.format(new Date())+"</lastUpdated>\n");
			sb.append("\t\t</versions>\n");
			sb.append("\t</versioning>\n");
			sb.append("</metadata>\n");
			
			File comDir = new File(releaseProjectDir, "com");
			File pumpDir = new File(comDir, "pump");
			File artifactDir = new File(pumpDir, entry.getKey());
			File metadataFile = new File(artifactDir, "maven-metadata-local.xml");
			boolean existed = metadataFile.exists();
			if(existed) {
				String oldData = IOUtils.read(metadataFile);
				String pre1 = oldData.substring(0, oldData.indexOf("<lastUpdated>"));
				String pre2 = sb.substring(0, sb.indexOf("<lastUpdated>"));
				if(pre1.equals(pre2)) {
					System.out.println("Skipped "+metadataFile.getAbsolutePath()+" (no changes)");
					continue;
				}
			}
			
			if(IOUtils.write(metadataFile, sb.toString(), true)) {
				if(existed) {
					System.out.println("Replaced "+metadataFile.getAbsolutePath());
				} else {
					System.out.println("Added "+metadataFile.getAbsolutePath());
				}
			}
		}
	}

	/**
	 * Extract the pom file from jar.
	 */
	private File extractPomFromJar(File jarFile, String artifactId) throws IOException {
		try(FileInputStream fileIn = new FileInputStream(jarFile);
				JarInputStream jarIn = new JarInputStream(fileIn)) {
			for(JarEntry e = jarIn.getNextJarEntry(); e!=null; e = jarIn.getNextJarEntry()) {
				String name = e.getName();
				if(name.equals("META-INF/maven/com.pump/"+artifactId+"/pom.xml")) {
					File pomFile = File.createTempFile("faven", ".pom");
					IOUtils.write(jarIn, pomFile);
					return pomFile;
				}
			}
		}
		return null;
	}

	/**
	 * Flush the releases map (mapping JarIds to jars) back to the file system.
	 * This only writes/copies files when changes are detected.
	 */
	private void writeReleasesFromMap() throws IOException {
		File comDir = new File(releaseProjectDir, "com");
		if(!comDir.exists())
			throw new RuntimeException("\"com\" not found in \"pump-release\".");
		File pumpDir = new File(comDir, "pump");
		if(!pumpDir.exists())
			throw new RuntimeException("\"com\\pump\" not found in \"pump-release\".");
		
		for(Entry<JarId, File> entry : releases.entrySet()) {
			File namedDir = new File(pumpDir, entry.getKey().artifactId);
			if(!namedDir.exists())
				namedDir.mkdirs();
			File versionDir = new File(namedDir, entry.getKey().version);
			if(!versionDir.exists())
				versionDir.mkdirs();
			String jarName = entry.getKey().artifactId+"-"+entry.getKey().version+".jar";
			File targetFile = new File(versionDir, jarName);
			
			File srcFile = entry.getValue();
			if(DELETED_FILE.equals(srcFile)) {
				if(targetFile.exists()) {
					System.out.println("Deleted "+targetFile.getAbsolutePath());
					targetFile.delete();
				} else {
					System.out.println("Failed to create "+targetFile.getAbsolutePath());
				}
			} else {
				if(targetFile.exists() && IOUtils.zipEquals(targetFile, srcFile, ".*pom\\.properties"))
				{
					System.out.println("Skipped "+targetFile.getAbsolutePath()+" (no changes)");
					continue;
				}
				if(targetFile.exists()) {
					System.out.println("Replaced "+targetFile.getAbsolutePath());
				} else {
					System.out.println("Added "+targetFile.getAbsolutePath());
				}
				IOUtils.copy(srcFile, targetFile, true);
			}
		}
	}
	
	/**
	 * Build the "releases" map, which  maps JarIds to known released jars.
	 */
	private void buildReleaseMap() {
		File comDir = new File(releaseProjectDir, "com");
		if(!comDir.exists())
			throw new RuntimeException("\"com\" not found in \"pump-release\".");
		File pumpDir = new File(comDir, "pump");
		if(!pumpDir.exists())
			throw new RuntimeException("\"com\\pump\" not found in \"pump-release\".");
		FileTreeIterator iter = new FileTreeIterator(pumpDir, "jar");
		while(iter.hasNext()) {
			File f = iter.next();
			String path = f.getAbsolutePath().substring(pumpDir.getAbsolutePath().length()+1);
			String artifactId = path.substring(0, path.indexOf(File.separator));
			path = path.substring(path.indexOf(File.separator)+1);
			String version = path.substring(0, path.indexOf(File.separator));
			JarId id = new JarId(artifactId, version);
			releases.put(id, f);
		}
	}

	/**
	 * Return the "pumpernickel" directory that contains all the subprojects.
	 */
	private File getPumpernickelDirectory() {
		File file = new File(System.getProperty("user.dir")).getParentFile();
		return file;
	}
}
