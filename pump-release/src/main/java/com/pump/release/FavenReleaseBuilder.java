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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import com.pump.desktop.temp.TempFileManager;
import com.pump.io.FileTreeIterator;
import com.pump.io.IOUtils;

/**
 * FavenReleaseBuilder is a temporary stopgap to build jars that resemble the hierarchy
 * used in the Pumpernickel project that properly configured Maven pom
 * files would generate.
 *
 */
public class FavenReleaseBuilder {
	
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
	
	/**
	 * Populates the pump-release directory with the appropriate jars,
	 * poms, and metadata files.
	 * <p>
	 * This method will update the Releases object and eventually call
	 * {@link Releases#save()}.
	 * 
	 * @param projects the Projects that should be (re-)written
	 * @param releases the object that manages the releases.
	 */
	public void run(Collection<Project> projects,Releases releases) {
		for(Project project : projects) {
			project.getDependencies();
			
			//act like this jar doesn't exist yet so we can rebuild it
			releases.setJar(project.getJarId(), null);
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
						releases.setJar(p.getJarId(), jar);
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
			releases.save();
			writePomAndMetaData(releases.getDirectory());
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Assuming that all jars are in place, this writes the adjacent pom and metadata.xml files.
	 */
	private void writePomAndMetaData(File releaseProjectDir) throws IOException {
		FileTreeIterator iter = new FileTreeIterator(releaseProjectDir, "jar");
		Map<String, TreeSet<String>> artifactToVersionMap = new HashMap<>();
		while(iter.hasNext()) {
			File jarFile = iter.next();
			String version = jarFile.getParentFile().getName();
			String artifactId = jarFile.getParentFile().getParentFile().getName();
			
			//this wasn't a jar in our release hierarchy
			if(artifactId.equals("bin") || jarFile.getParentFile().getName().equals("resources"))
				continue;
			
			String pomFileName = artifactId+"-"+version+".pom";
			File pomFile = new File(jarFile.getParentFile(), pomFileName);
			File src = extractPomFromJar(jarFile, artifactId);
			if(src==null)
				throw new NullPointerException("No pom.xml file found for "+artifactId+":"+version);
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
					File pomFile = TempFileManager.get().createFile("faven", ".pom");
					IOUtils.write(jarIn, pomFile);
					return pomFile;
				}
			}
		}
		return null;
	}
}