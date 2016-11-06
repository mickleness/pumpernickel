/*
 * @(#)ParserException.java
 *
 * $Date: 2015-12-26 01:54:45 -0600 (Sat, 26 Dec 2015) $
 *
 * Copyright (c) 2015 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Jeremy Wood. For details see accompanying license terms.
 * 
 * This software is probably, but not necessarily, discussed here:
 * https://javagraphics.java.net/
 * 
 * That site should also contain the most recent official version
 * of this software.  (See the SVN repository for more details.)
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
