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
package com.pump.data.branch;

public abstract class BranchException extends Exception {
	private static final long serialVersionUID = 1L;

	public BranchException() {
		super();
	}

	public BranchException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public BranchException(String message, Throwable cause) {
		super(message, cause);
	}

	public BranchException(String message) {
		super(message);
	}

	public BranchException(Throwable cause) {
		super(cause);
	}

	@SuppressWarnings("rawtypes")
	public abstract Branch getBranch();
	
	public abstract Object getBeanId();
}