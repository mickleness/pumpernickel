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
package com.pump.io;


/** This is an exception related to a poorly formed Token. */
public class ParserException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	protected Token token;
	
	public ParserException(Token token,Throwable throwable) {
		super(throwable);
		this.token = token;
	}
	
	public ParserException(Token token,String msg) {
		super(msg);
		this.token = token;
	}
	
	public Token getToken() {
		return token;
	}
}