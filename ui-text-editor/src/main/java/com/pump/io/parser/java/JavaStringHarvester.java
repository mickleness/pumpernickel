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
package com.pump.io.parser.java;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.pump.io.parser.Parser.StringToken;
import com.pump.io.parser.Token;
import com.pump.util.Receiver;

/** This collects all the Strings in a java source file. */
public class JavaStringHarvester {

	/**
	 * Returns the string literals in this java file.
	 * 
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public static String[] get(File file) throws IOException {
		JavaStringHarvester h = new JavaStringHarvester(file);
		return h.getStrings();
	}

	protected List<String> strings = new ArrayList<>();
	private Receiver<Token> receiver = new Receiver<Token>() {

		@Override
		public void add(Token... tokens) {
			for (Token token : tokens) {
				if (token instanceof StringToken) {
					StringToken s = (StringToken) token;
					strings.add(s.getDecodedString());
				}
			}
		}

	};

	/** Create a JavaStringHarvester that reads a File. */
	public JavaStringHarvester(File file) throws IOException {
		try (FileInputStream in = new FileInputStream(file)) {
			initialize(in, "ISO-8859-1");
		}
	}

	/** Create a JavaStringHarvester that reads an InputStream. */
	public JavaStringHarvester(InputStream in, String encoding)
			throws IOException {
		initialize(in, encoding);
	}

	/** Create a JavaStringHarvester that reads a String. */
	public JavaStringHarvester(String sourceCode) {
		try {
			new JavaParser().parse(new StringReader(sourceCode), receiver);
		} catch (IOException e) {
			// a String-backed reader shouldn't throw an IOException
			throw new RuntimeException(e);
		}
	}

	private void initialize(InputStream in, String encoding) throws IOException {
		try (InputStreamReader reader = new InputStreamReader(in,
				Charset.forName("ISO-8859-1"))) {
			new JavaParser().parse(reader, receiver);
		}
	}

	/** Return all the decoded Strings in the source code provided. */
	public String[] getStrings() {
		return strings.toArray(new String[strings.size()]);
	}
}