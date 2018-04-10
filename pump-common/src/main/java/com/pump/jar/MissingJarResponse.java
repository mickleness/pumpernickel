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
package com.pump.jar;

/**
 * The three options we can choose from when dealing with external jar
 * dependencies.
 */
public enum MissingJarResponse {
	IGNORE, BUNDLE_ENTIRE_JAR, BUNDLE_ONLY_REQUIRED_CLASSES
}