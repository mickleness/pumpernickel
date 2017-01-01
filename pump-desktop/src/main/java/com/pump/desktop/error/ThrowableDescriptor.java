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