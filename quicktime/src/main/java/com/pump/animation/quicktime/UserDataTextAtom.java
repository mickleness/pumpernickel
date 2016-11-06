/*
 * @(#)UserDataTextAtom.java
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
package com.pump.animation.quicktime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.pump.io.GuardedInputStream;
import com.pump.io.GuardedOutputStream;

/** This is not a public class because I expect to make some significant
 * changes to this project in the next year.
 * <P>Use at your own risk.  This class (and its package) may change in future releases.
 * <P>Not that I'm promising there will be future releases.  There may not be.  :)
 */
class UserDataTextAtom extends LeafAtom {
	List<TextEntry> entries = new ArrayList<TextEntry>();
	
	String id;
	public UserDataTextAtom(Atom parent,String id,GuardedInputStream in) throws IOException {
		super(parent);
		this.id = id;
		while(in.isAtLimit()==false) {
			int size = read16Int(in);
			int language = read16Int(in);
			byte[] data = new byte[size];
			read(in,data);
			entries.add(new TextEntry(language,data));
		}
	}
	
	@Override
	protected String getIdentifier() {
		return id;
	}

	@Override
	protected void writeContents(GuardedOutputStream out) throws IOException {
		for(int a = 0; a<entries.size(); a++) {
			TextEntry e = entries.get(a);
			write16Int(out,e.data.length);
			write16Int(out,e.language);
			out.write(e.data);
		}
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("UserDataTextAtom[ ");
		for(int a = 0; a<entries.size(); a++) {
			TextEntry e = entries.get(a);
			sb.append("\""+(new String(e.data))+"\" ");
		}
		sb.append("]");
		return sb.toString();
	}

	public static class TextEntry {
		int language;
		byte[] data;
		public TextEntry(int l,byte[] d) {
			this.language = l;
			this.data = d;
		}
	}
}
