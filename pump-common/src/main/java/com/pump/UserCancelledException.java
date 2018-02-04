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

/**
 * An exception that indicates an operation was cancelled, either directly or
 * indirectly by something the user did.
 */
public class UserCancelledException extends RuntimeException implements
		Ignorable {
	private static final long serialVersionUID = 1L;

	boolean ignored;

	public UserCancelledException() {
		this(false);
	}

	public UserCancelledException(String msg) {
		this(msg, false);
	}

	public UserCancelledException(Throwable cause) {
		this(cause, false);
	}

	public UserCancelledException(boolean ignored) {
		super();
		this.ignored = ignored;
	}

	public UserCancelledException(String msg, boolean ignored) {
		super(msg);
		this.ignored = ignored;
	}

	public UserCancelledException(Throwable cause, boolean ignored) {
		super(cause);
		this.ignored = ignored;
	}

	@Override
	public boolean isIgnored() {
		return ignored;
	}
}