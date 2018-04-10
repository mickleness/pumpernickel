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
package com.pump.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/** Static methods related to warnings. */
public class Warnings {

	private static Map<String, Long> textToTime;
	private static Collection<String> printOnceMessages;

	/**
	 * Prints a message once to System.err. A unique string will only be printed
	 * once per session. This is intended for cases where a developer should be
	 * alerted to a possible problem, but the console should not be flooded with
	 * messages. Also the "problem" is one that only deserves a warning -- not
	 * an exception.
	 */
	public static synchronized void printOnce(String message) {
		if (printOnceMessages == null)
			printOnceMessages = new HashSet<>();

		if (printOnceMessages.contains(message)) {
			return;
		}
		System.err.println(message);
		printOnceMessages.add(message);
	}

	/**
	 * Prints this message to the console, but only once every so often.
	 * <P>
	 * This is intended to prevent the console from being flooded with warning
	 * messages for a frequently occurring problem/situation.
	 * 
	 * @param text
	 *            the message to print.
	 * @param delay
	 *            the number of milliseconds to wait before another message may
	 *            be printed.
	 */
	public synchronized static void println(String text, long delay) {
		if (text == null)
			return;
		if (textToTime == null)
			textToTime = new HashMap<String, Long>();

		long currentTime = System.currentTimeMillis();
		Long l = textToTime.get(text);
		if (l == null || currentTime > l.longValue()) {
			textToTime.put(text, new Long(currentTime + delay));
			System.err.println(text);
		}
	}
}