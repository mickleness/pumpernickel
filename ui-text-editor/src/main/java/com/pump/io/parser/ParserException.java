/**
 * This software is released as part of the Pumpernickel project.
 * <p>
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * <p>
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.io.parser;

import java.io.Serial;

/** This is an exception related to a poorly formed Token. */
public class ParserException extends RuntimeException {
	@Serial
	private static final long serialVersionUID = 1L;

	protected Token token;

	public ParserException(Token token, Throwable throwable) {
		super(throwable);
		this.token = token;
	}

	public ParserException(Token token, String msg) {
		super(msg);
		this.token = token;
	}

	public Token getToken() {
		return token;
	}
}