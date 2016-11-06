/*
 * @(#)JavaStringHarvester.java
 *
 * $Date: 2015-12-20 22:22:16 -0600 (Sun, 20 Dec 2015) $
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
package com.pump.io.java;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.pump.io.Token;
import com.pump.io.java.JavaParser.StringToken;
import com.pump.util.Receiver;

/** This collects all the Strings in a java source file. */
public class JavaStringHarvester {
	
	/** Returns the string literals in this java file. 
	 * @throws IOException if an IO problem occurs.
	 */
	public static String[] get(File file) throws IOException {
		JavaStringHarvester h = new JavaStringHarvester(file);
		return h.getStrings();
	}
	
	protected List<String> strings = new ArrayList<>();
	private Receiver<Token> receiver = new Receiver<Token>() {

		@Override
		public void add(Token... tokens) {
			for(Token token : tokens) {
				if(token instanceof StringToken) {
					StringToken s = (StringToken)token;
					strings.add(s.getDecodedString());
				}
			}
		}
		
	};

	/** Create a JavaStringHarvester that reads a File. */
	public JavaStringHarvester(File file) throws IOException {
		try(FileInputStream in = new FileInputStream(file)) {
			initialize(in, "ISO-8859-1");
		}
	}

	/** Create a JavaStringHarvester that reads an InputStream. */
	public JavaStringHarvester(InputStream in,String encoding) throws IOException {
		initialize(in, encoding);
	}

	/** Create a JavaStringHarvester that reads a String. */
	public JavaStringHarvester(String sourceCode) {
		try {
			JavaParser.parse(new StringReader(sourceCode), true, receiver);
		} catch(IOException e) {
			//a String-backed reader shouldn't throw an IOException
			throw new RuntimeException(e);
		}
	}
	
	private void initialize(InputStream in,String encoding) throws IOException {
		try(InputStreamReader reader = new InputStreamReader(in, Charset.forName("ISO-8859-1"))) {
			JavaParser.parse(reader, true, receiver);
		}
	}
	
	/** Return all the decoded Strings in the source code provided. */
	public String[] getStrings() {
		return strings.toArray(new String[strings.size()]);
	}
}
