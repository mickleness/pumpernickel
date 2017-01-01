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

import java.io.File;
import java.util.Map;
import java.util.Set;

public interface MissingJarResponseManager {

	/** This method guesses the desired behavior.
	 * <p>If <code>getBehavior()</code> has already returned
	 * <code>null</code> then we're going to show a dialog to
	 * the user to make them decide. But this method helps
	 * prepopulate the choices. If the user likes what they see,
	 * they can just hit the return key and move on.
	 * <p>This method may consult other projects and see
	 * if they have already stored a Behavior for this file.
	 * 
	 * @param jarFile the file to guess the Behavior for.
	 * @return a reasonable guess at the Behavior to associate
	 * with this File, or null.
	 */
	public MissingJarResponse guessBehavior(File jarFile);
	
	/** 
	 * 
	 * @param file the file to fetch the Behavior for.
	 * @param knownDependency if true then we know for certain that this
	 * file is needed. If false then we're not sure if this file is required.
	 * @return the last Behavior assigned to this File.
	 * This may return null if no Behavior is defined.
	 */
	public MissingJarResponse getBehavior(File file,boolean knownDependency);

	/**
	 * 
	 * @param file the file to assign the Behavior for.
	 * @param b the Behavior this file should use.
	 */
	public void setBehavior(File file,MissingJarResponse b);

	/**
	 * 
	 * @param manager the choice model used.
	 * @param requiredJars known jar behaviors. This is where the users choices will be stored, so it may be empty but it can not be null.
	 * This is also used to set up the dialog if you want to preset certain choices.
	 * @param jarFiles the set of jar files that the user will make choices for. When this dialog is dismissed it is guaranteed that the
	 * jarBehaviors map will have a non-null value associated with each of these Files.
	 * @param primaryClassName the UI needs the name of the compiled class to explain things to the user.
	 */
	public void resolveBehaviors(MissingJarResponseManager manager,
			Map<File, MissingJarResponse> requiredJars, Set<File> keySet,
			String name);
}