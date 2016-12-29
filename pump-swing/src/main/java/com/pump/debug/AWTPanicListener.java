/*
 * @(#)AWTPanicListener.java
 *
 * $Date: 2014-03-13 04:15:48 -0400 (Thu, 13 Mar 2014) $
 *
 * Copyright (c) 2011 by Jeremy Wood.
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
package com.pump.debug;

/** This listener is notified when the event dispatch thread is
 * blocked for a substantial amount of time.
 * <P>This type of listener could, for example, try to submit
 * information to the developer, or try to do an emergency auto-save
 * and exit the application.
 * <P>This listener may want to immediately call <code>SwingUtilities.invokeLater()</code>
 * with a runnable that can <i>cancel</i> whatever this listener is doing.
 * That way if the event dispatch thread does resume business as usual: this
 * listener will abort whatever it's working on.
 *
 */
public interface AWTPanicListener {
	public void AWTPanic(String applicationName);
}
