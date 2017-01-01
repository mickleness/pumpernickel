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
package com.pump.geom;


/** This indicates that a path had no shape data.
 * <P>This means it had no lines, quadratic or cubic
 * segments in it (although it may have had a MOVE_TO
 * and a CLOSE segment).
 *
 */
public class EmptyPathException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public EmptyPathException() {
	}

	public EmptyPathException(String message) {
		super(message);
	}

	public EmptyPathException(Throwable cause) {
		super(cause);
	}

	public EmptyPathException(String message, Throwable cause) {
		super(message, cause);
	}

}