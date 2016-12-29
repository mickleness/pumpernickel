/*
 * @(#)ThrowableDescriptor.java
 *
 * $Date: 2015-09-13 14:46:53 -0400 (Sun, 13 Sep 2015) $
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
package com.pump.desktop.error;

import com.pump.util.ObservableProperties;
import com.pump.util.ObservableProperties.Key;

/** This collects meta information about a Throwable to help give the user helpful
 * feedback and/or make informed decisions.
 */
public class ThrowableDescriptor
{
	private final static Key<String> KEY_USER_FRIENDLY_MESSAGE = new Key<String>(ThrowableDescriptor.class+"#USER_FRIENDLY_MESSAGE", String.class);
	
	/** The throwable this incident involves (this cannot be null). */
	public final Throwable throwable;

	protected ObservableProperties attributes = new ObservableProperties();

	public ThrowableDescriptor(Throwable throwable) {
		if(throwable==null)
			throw new NullPointerException();
		
		this.throwable = throwable;
	}
	
	public ThrowableDescriptor(String userFriendlyMessage,Throwable throwable) {
		this(throwable);
		attributes.set(KEY_USER_FRIENDLY_MESSAGE, userFriendlyMessage);
	}	
	
	/**
	 * 
	 * @return an optional user-friendly String describing this error.
	 */
	public String getUserFriendlyMessage() {
		return attributes.get(KEY_USER_FRIENDLY_MESSAGE);
	}
}
