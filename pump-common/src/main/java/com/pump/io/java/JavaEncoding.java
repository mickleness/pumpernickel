/*
 * @(#)JavaEncoding.java
 *
 * $Date: 2015-12-26 20:42:44 -0600 (Sat, 26 Dec 2015) $
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
package com.pump.io.java;

import java.util.HashSet;
import java.util.Set;

/** Static methods related to reading and writing strings that use a \ character to escape characters.
 * 
 * @see com.bric.io.HTMLEncoding
 */
public class JavaEncoding {
	
	static Set<Character> okChars = new HashSet<>();
	static {
		char[] ch = new char[] { '!', '@', '#', '$', '%', '^', '&', ' ', '*', '(', ')', '+', '-', '_', '[', ']', '{', '}', ';', ':', ',', '.', '/',
				'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
				'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
				'0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
		for(char t : ch) {
			okChars.add(t);
		}
	}
	
	/** Write a character to a StringBuffer. For example a tab character is written
	 * as "\t". If a character is not a simple recognized character, then it is written
	 * with unicode encoding ("\u0123").
	 * @param ch the character to encode.
	 * @param dest the StringBuffer to write to.
	 */
	public static void encode(char ch,StringBuffer dest) {
		if(okChars.contains(ch)) {
			dest.append(ch);
		} else if(ch=='\\') {
			dest.append("\\\\");
		} else if(ch=='\t') {
			dest.append("\\t");
		} else if(ch=='\b') {
			dest.append("\\b");
		} else if(ch=='\r') {
			dest.append("\\r");
		} else if(ch=='\n') {
			dest.append("\\n");
		} else if(ch=='\f') {
			dest.append("\\f");
		} else if(ch=='\"') {
			dest.append("\\\"");
		} else if(ch=='\'') {
			dest.append("\\'");
		} else {
			dest.append("\\u");
			int k = ch;
			String s = Integer.toString( k, 16 );
			while(s.length()<4) {
				s = "0"+s;
			}
			dest.append(s);
		}
	}
	
	/** Reads (or decodes) one character, such as "\\t" or "\u1234" or simply "x"
	 * 
	 * @param s the String being read. This should NOT include the beginning/ending quotes.
	 * @param index the index within s to start reading.
	 * @param dest the buffer to write to. This may be null, in which case you won't be storing
	 * the parsed data (but you can still monitor the character index).
	 * @return the new index the next character should begin reading from. For example
	 * if the initial index is 0 for the String "x", then this returns 1 because the index
	 * only increased by 1 character. But if the initial index is 0 for the String "\u0123", then this
	 * returns 6, so the cursor is positioned after the '3'.
	 */
	public static int decode(CharSequence s,int index,StringBuffer dest) {
		char c = s.charAt(index++);
		if(c=='\\') {
			c = s.charAt(index++);
			if(c=='t') {
				if(dest!=null)
					dest.append('\t');
			} else if(c=='n') {
				if(dest!=null)
					dest.append('\n');
			} else if(c=='b') {
				if(dest!=null)
					dest.append('\b');
			} else if(c=='r') {
				if(dest!=null)
					dest.append('\r');
			} else if(c=='f') {
				if(dest!=null)
					dest.append('\f');
			} else if(c=='"') {
				if(dest!=null)
					dest.append('\"');
			} else if(c=='\'') {
				if(dest!=null)
					dest.append('\'');
			} else if(c=='\\') {
				if(dest!=null)
					dest.append('\\');
			} else if(c=='u') {
				char c1 = s.charAt(index++);
				char c2 = s.charAt(index++);
				char c3 = s.charAt(index++);
				char c4 = s.charAt(index++);
				int i = Integer.parseInt(c1+""+c2+c3+c4,16);
				
				if(dest!=null)
					dest.append( (char)i );
			} else if(Character.isDigit(c)) {
				char c1 = c;
				c = s.charAt(index);
				int i;
				if(Character.isDigit(c)) {
					char c2 = c;
					index++;
					c = s.charAt(index);
					if(Character.isDigit(c)) {
						char c3 = c;
						index++;
						i = Integer.parseInt(c1+""+c2+c3,8);
					} else {
						i = Integer.parseInt(c1+""+c2,8);
					}
				} else {
					i = Integer.parseInt(c1+"",8);
				}
				
				if(dest!=null)
					dest.append( (char)i );
				
			} else {
				throw new RuntimeException("Unexpected character \'"+c+"\' in \""+s+"\"");
			}
		} else {
			if(dest!=null)
				dest.append(c);
		}
		return index;
	}

	/** Encode a String using the Java conventions (using a \ character for special chars, including unicode
	 * identifiers.
	 * @param str a String to encode.
	 * @return a String that a Java compiler will accept. For example a tab character becomes "\t", or complex
	 * characters might use unicode encoding such as "\u0123".
	 */
	public static String encode(String str) {
		StringBuffer dest = new StringBuffer();
		for(int a = 0; a<str.length(); a++) {
			char ch = str.charAt(a);
			encode(ch, dest);
		}
		return dest.toString();
	}

	/** Return a String previously encoded with {@link #encode(String)}
	 * 
	 * @param string a String using \ encoding.
	 * @return a String previously encoded with {@link #encode(String)}
	 */
	public static String decode(String string) {
		StringBuffer dest = new StringBuffer();
		int index = 0;
		while(index<string.length()) {
			index = decode(string, index, dest);
		}
		return dest.toString();
	}
}
