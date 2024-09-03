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
package com.pump.util;

import java.util.ArrayList;
import java.util.List;

/** Static methods related to <code>Strings</code> and text. */
public class Text {

	public static boolean isWhiteSpace(String s) {
		for (int a = 0; a < s.length(); a++) {
			if (Character.isWhitespace(s.charAt(a)) == false)
				return false;
		}
		return true;
	}

	public static String[] getParagraphs(String s) {
		int index = 0;
		List<String> list = new ArrayList<String>();
		while (index < s.length()) {
			int i1 = s.indexOf('\n', index);
			int i2 = s.indexOf('\r', index);
			int i;
			if (i1 == -1 && i2 != -1) {
				i = i2;
			} else if (i1 != -1 && i2 == -1) {
				i = i1;
			} else {
				i = Math.min(i1, i2);
			}
			if (i == -1) {
				list.add(s.substring(index));
				index = s.length();
			} else {
				list.add(s.substring(index, i));
				i++;
				index = i;
			}
		}
		return list.toArray(new String[list.size()]);
	}
}