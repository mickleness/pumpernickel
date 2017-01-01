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

/** This choice always uses one fixed <code>MissingJarResponse</code>.
 */
public class MissingJarConstantResponseManager implements MissingJarResponseManager {

	final MissingJarResponse behavior;
	final boolean applyToNonrequiredJars;
	
	/**
	 * 
	 * @param b the fixed MissingJarResponse this choice always uses.
	 */
	public MissingJarConstantResponseManager(MissingJarResponse b,boolean applyToNonrequiredJars) {
		if(b==null)
			throw new NullPointerException();
		
		this.applyToNonrequiredJars = applyToNonrequiredJars;
		this.behavior = b;
	}
	
	@Override
	public MissingJarResponse getBehavior(File file,boolean knownDependency) {
		if(knownDependency || applyToNonrequiredJars)
			return behavior;
		return MissingJarResponse.IGNORE;
	}

	/** This throws an exception if the argument b is not
	 * the Behavior passed to this object's constructor.
	 */
	@Override
	public void setBehavior(File file, MissingJarResponse b) {
		if(!behavior.equals(b))
			throw new RuntimeException("this operation is not supported for a fixed behavior");
	}

	@Override
	public MissingJarResponse guessBehavior(File jarFile) {
		return behavior;
	}

	@Override
	public void resolveBehaviors(MissingJarResponseManager manager,
			Map<File, MissingJarResponse> requiredJars, Set<File> keySet,
			String name) {
		for(File file : keySet) {
			requiredJars.put(file, behavior);
		}
	}
}