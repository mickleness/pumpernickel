/*
 * @(#)Text.java
 *
 * $Date: 2016-01-30 18:40:21 -0500 (Sat, 30 Jan 2016) $
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
package com.pump.util;

import java.util.ArrayList;
import java.util.List;

/** Static methods related to <code>Strings</code> and text. */
public class Text {

	public static boolean isWhiteSpace(String s) {
		for(int a = 0; a<s.length(); a++) {
			if(Character.isWhitespace(s.charAt(a))==false)
				return false;
		}
		return true;
	}
	
	public static String[] getParagraphs(String s) {
		int index = 0;
		List<String> list = new ArrayList<String>();
		while(index<s.length()) {
			int i1 = s.indexOf('\n',index);
			int i2 = s.indexOf('\r',index);
			int i;
			if(i1==-1 && i2!=-1) {
				i = i2;
			} else if(i1!=-1 && i2==-1) {
				i = i1;
			} else {
				i = Math.min(i1,i2);
			}
			if(i==-1) {
				list.add(s.substring(index));
				index = s.length();
			} else {
				list.add(s.substring(index,i));
				i++;
				index = i;
			}
		}
		return list.toArray(new String[list.size()]);
	}
}
