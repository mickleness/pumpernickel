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
package com.pump;


/** An exception that indicates an operation was cancelled, either
 * directly or indirectly by something the user did.
 */
public class UserCancelledException extends RuntimeException {
    private static final long serialVersionUID = 1L;

	public UserCancelledException() {}

	public UserCancelledException(String msg) {
		super(msg);
	}

	public UserCancelledException(Throwable cause) {
		super(cause);
	}
}