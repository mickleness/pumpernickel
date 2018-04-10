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

/**
 * This identifies the artifact ID and version of a jar.
 */
public class JarId {
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
		return artifactId + ":" + version;
	}

	@Override
	public boolean equals(Object t) {
		if (!(t instanceof JarId))
			return false;
		return toString().equals(t.toString());
	}
}