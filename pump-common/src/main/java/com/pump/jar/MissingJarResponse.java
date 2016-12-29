package com.pump.jar;

/** The three options we can choose from when dealing with external jar dependencies. */
public enum MissingJarResponse {
	 IGNORE, BUNDLE_ENTIRE_JAR, BUNDLE_ONLY_REQUIRED_CLASSES 
}
