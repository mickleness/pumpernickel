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
package com.pump.data;

/**
 * Possible states for a bean/record to be described with.
 */
public enum BeanState {
	/**
	 * This indicates that a bean was recently created. Here "recent" may relate to
	 * the time at which it was created or to a series of events which triggered its creation.
	 */
	CREATED, 
	/**
	 * This indicates that a bean currently exists, although it was not recently created.
	 */
	EXISTS,
	/**
	 * This indicates a bean is known to have previously existed, but has been deleted.
	 */
	DELETED,
	/**
	 * This indicates a bean has no record of existing
	 */
	UNDEFINED
}