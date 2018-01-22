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
package com.pump.io.parser.java;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.Stack;

import com.pump.io.parser.Parser.BracketCharToken;
import com.pump.io.parser.Token;
import com.pump.io.parser.java.JavaParser.BracketType;
import com.pump.io.parser.java.JavaParser.DeclarationType;
import com.pump.io.parser.java.JavaParser.JavaModifier;
import com.pump.io.parser.java.JavaParser.WordToken;
import com.pump.util.Receiver;

/**
 * This identifies basic crucial details about java source code.
 */
public class JavaClassSummary {
	/** This is used to abort parsing tokens prematurely. */
	static class FinishedException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}

	/** This interprets incoming Tokens to populate this JavaClassSummary's data */
	class MyReceiver implements Receiver<Token> {
		StringBuffer uncommittedPackageName = null;
		StringBuffer uncommittedImportStatement = null;
		Stack<BracketType> brackets = new Stack<>();

		@Override
		public void add(Token... tokens) {
			for (Token token : tokens) {
				if (token.getText().equals(";")) {
					if (uncommittedPackageName != null) {
						packageName = uncommittedPackageName.toString().trim();
						uncommittedPackageName = null;
					} else if (uncommittedImportStatement != null) {
						importedClasses.add(uncommittedImportStatement
								.toString().trim());
						uncommittedImportStatement = null;
					}
				} else if (uncommittedPackageName != null) {
					uncommittedPackageName.append(token.getText());
				} else if (uncommittedImportStatement != null) {
					uncommittedImportStatement.append(token.getText());
				} else if (token.getText().equals("package")) {
					uncommittedPackageName = new StringBuffer();
				} else if (token.getText().equals("import")) {
					uncommittedImportStatement = new StringBuffer();
				} else if (token instanceof WordToken
						&& ((WordToken) token).isModifier) {
					modifiers.add(JavaModifier.valueOf(token.getText()
							.toUpperCase()));
				} else if (brackets.size() == 0 && token instanceof WordToken
						&& ((WordToken) token).isDeclarationType) {
					declarationType = DeclarationType.valueOf(token.getText()
							.toUpperCase());
				} else if (brackets.size() == 0 && declarationType != null
						&& simpleName == null && token instanceof WordToken) {
					simpleName = token.getText();
				} else if (token instanceof BracketCharToken) {
					if (token.getText().equals("{") && brackets.size() == 0) {
						throw new FinishedException();
					}

					BracketCharToken bct = (BracketCharToken) token;
					if (bct.isOpen()) {
						brackets.push(bct.getBracketType());
					} else {
						if (brackets.size() > 0
								&& brackets.peek() == bct.getBracketType()) {
							brackets.pop();
						} else {
							// unbalanced brackets of some sort:
							error = true;
						}
					}
				}
			}
		}

	}

	protected MyReceiver receiver = new MyReceiver();
	protected String simpleName;
	protected String packageName;
	protected Set<String> importedClasses = new LinkedHashSet<>();
	protected Set<JavaModifier> modifiers = new LinkedHashSet<>();
	protected DeclarationType declarationType = null;
	protected boolean error = false;

	/**
	 * Create a JavaClassSummary from a String
	 * 
	 * @param string
	 *            the source code.
	 */
	public JavaClassSummary(String string) {
		this(new StringReader(string));
	}

	public JavaClassSummary(File javaFile) throws MalformedURLException,
			IOException {
		this(javaFile.toURI().toURL());
	}

	public JavaClassSummary(URL url) throws IOException {
		try (InputStream in = url.openStream()) {
			initialize(new InputStreamReader(in, Charset.forName("UTF-8")));
		}
	}

	/**
	 * Create a JavaClassSummary from a Reader
	 * 
	 * @param reader
	 *            the source code.
	 */
	public JavaClassSummary(Reader reader) {
		initialize(reader);
	}

	private void initialize(Reader reader) {
		try {
			new JavaParser().parse(reader, receiver);
			if (receiver.brackets.size() != 0) {
				error = true;
			}
		} catch (FinishedException e) {
			// do nothing, this is how we prematurely exit
		} catch (IOException e) {
			// this shouldn't happen for a StringReader
			throw new RuntimeException(e);
		}
	}

	/**
	 * Create a JavaClassSummary from an InputStream that refers to java source
	 * code.
	 * 
	 * @param in
	 *            an InputStream to read the source code through.
	 * @param charset
	 *            the character set to parse the input stream with.
	 * @throws IOException
	 */
	public JavaClassSummary(InputStream in, Charset charset) throws IOException {
		this(new InputStreamReader(in, charset));
	}

	/**
	 * Return the package name of this class.
	 * 
	 * @return the package name of this class. Such as "javax.swing".
	 */
	public String getPackageName() {
		return packageName;
	}

	/**
	 * Return the simple name of this class/interface/enum. For example if
	 * source code starts with "public class Foo" then this will return "Foo".
	 * 
	 * @return the simple name of this class/interface/enum.
	 */
	public String getSimpleName() {
		return simpleName;
	}

	/**
	 * Return the fully qualified name of this class/interface/enum. For example
	 * if type declaration starts with "package org.apache; public class Foo"
	 * then this will return "org.apache.Foo".
	 *
	 * 
	 * @return the fully qualified name of this class/interface/enum.
	 */
	public String getCanonicalName() {
		if (packageName != null && packageName.length() > 0) {
			return packageName + "." + simpleName;
		}
		return simpleName;
	}

	/** Return the package name in a java file. */
	public static String getPackageName(String sourceCode) {
		JavaClassSummary summary = new JavaClassSummary(sourceCode);
		return summary.getPackageName();
	}

	/** Return the package name in a java file. */
	public static String getPackageName(File sourceCodeFile) throws IOException {
		try (FileInputStream in = new FileInputStream(sourceCodeFile)) {
			JavaClassSummary summary = new JavaClassSummary(in,
					Charset.forName("ISO-8859-1"));
			return summary.getPackageName();
		}
	}

	/** Return the class name in a java file. */
	public static String getClassName(File sourceCodeFile) throws IOException {
		String filename = sourceCodeFile.getName();
		if (!filename.endsWith(".java")) {
			throw new IllegalArgumentException(
					"this file is not a .java file: "
							+ sourceCodeFile.getAbsolutePath());
		}
		filename = filename.substring(0, filename.length() - 5);

		String packageName = getPackageName(sourceCodeFile);
		if (packageName == null) {
			return null;
		}
		return packageName + "." + filename;
	}

	/**
	 * Return the imported classnames. Note here a "name" might be
	 * "java.util.*", or it might be an explicit classname such as
	 * "java.util.HashMap".
	 */
	public Collection<String> getImportClassnames() {
		return new HashSet<>(importedClasses);
	}
}