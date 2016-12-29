/*
 * @(#)KeyStoreCollection.java
 *
 * $Date$
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
package com.pump.jar;

import java.io.File;
import java.io.FileFilter;

import com.pump.io.SuffixFilenameFilter;

public class KeyStoreCollection extends FileCollection {

	static FileFilter jksFileFilter = new SuffixFilenameFilter("jks", "p12");

	public KeyStoreCollection() {
		super("keystore-");
	}

	@Override
	public void process(File file) {
		if(jksFileFilter.accept(file) && file.exists())
			files.add(file);
	}

}
