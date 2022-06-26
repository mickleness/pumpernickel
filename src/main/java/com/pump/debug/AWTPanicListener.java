/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.debug;

/**
 * This listener is notified when the event dispatch thread is blocked for a
 * substantial amount of time.
 * <P>
 * This type of listener could, for example, try to submit information to the
 * developer, or try to do an emergency auto-save and exit the application.
 * <P>
 * This listener may want to immediately call
 * <code>SwingUtilities.invokeLater()</code> with a runnable that can
 * <i>cancel</i> whatever this listener is doing. That way if the event dispatch
 * thread does resume business as usual: this listener will abort whatever it's
 * working on.
 *
 */
public interface AWTPanicListener {
	public void AWTPanic(String applicationName);
}