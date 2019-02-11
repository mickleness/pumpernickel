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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * This identifies the artifact ID and version of a jar.
 */
public class JarId implements Comparable<JarId> {
	public final String artifactId, version;

	public JarId(File jarFile) throws IOException {
		try (InputStream in = new FileInputStream(jarFile)) {
			try (JarInputStream jarIn = new JarInputStream(in)) {
				JarEntry e = jarIn.getNextJarEntry();
				while (e != null) {
					if (e.getName().startsWith("META-INF/maven/")
							&& e.getName().endsWith(".properties")) {
						Properties p = new Properties();
						p.load(jarIn);
						artifactId = p.getProperty("artifactId");
						version = p.getProperty("version");
						return;
					}
					e = jarIn.getNextJarEntry();
				}
			}
		}
		throw new IOException(
				"Failed to identify jar entry \"META-INF/maven/*.properties\"");
	}

	public JarId(String artifactId, String version) {
		this.artifactId = artifactId;
		this.version = version;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public String toString() {
		return artifactId + ":" + version;
	}

	@Override
	public boolean equals(Object t) {
		if (!(t instanceof JarId))
			return false;
		return toString().equals(t.toString());
	}

	@Override
	public int compareTo(JarId o) {
		int k = artifactId.compareTo(o.artifactId);
		if (k != 0)
			return k;
		String[] s1 = split(version);
		String[] s2 = split(o.version);
		for (int a = 0; a < Math.max(s1.length, s2.length); a++) {
			String t1 = a < s1.length ? s1[a] : null;
			String t2 = a < s2.length ? s2[a] : null;
			if (t1 == null && t2 != null) // "1.00" vs "1.00.01"
				return -1;
			if (t1 != null && t2 == null) // "1.00.01" vs "1.00"
				return 1;
			Integer i1 = getInt(t1);
			Integer i2 = getInt(t2);
			if (i1 != null && i2 != null) {
				k = i1.compareTo(i2);
				if (k != 0)
					return k;
			} else {
				k = t1.compareTo(t2);
				if (k != 0)
					return k;
			}
		}
		return 0;
	}

	private String[] split(String str) {
		List<String> terms = new ArrayList<>();
		StringBuilder sb = new StringBuilder();
		for (int a = 0; a < str.length(); a++) {
			char ch = str.charAt(a);
			if (ch == '.' || ch == '-') {
				if (sb.length() > 0) {
					terms.add(sb.toString());
					sb = new StringBuilder();
				}
			} else {
				sb.append(ch);
			}
		}
		if (sb.length() > 0)
			terms.add(sb.toString());
		return terms.toArray(new String[terms.size()]);
	}

	private Integer getInt(String str) {
		try {
			return Integer.valueOf(str);
		} catch (NumberFormatException e) {
			return null;
		}
	}
}