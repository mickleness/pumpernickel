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
package com.pump.breakout;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.pump.io.IOUtils;
import com.pump.io.parser.Parser.CommentToken;
import com.pump.io.parser.Parser.WhitespaceToken;
import com.pump.io.parser.Token;
import com.pump.io.parser.java.JavaClassSummary;
import com.pump.io.parser.java.JavaParser;
import com.pump.io.parser.java.JavaParser.BracketType;
import com.pump.io.parser.java.JavaParser.WordToken;

/**
 * This class builds a single self-contained java file that includes as many
 * dependent java files as it can find in a given workspace.
 */
public class Breakout {

	/**
	 * This simply refers to the name that is being imported.
	 * <p>
	 * For example, the name property of this object may be "java.util.List" or
	 * "java.util.*".
	 */
	class ImportStatement implements Comparable<ImportStatement> {

		/** Text resembling "java.util.List" or "java.util.*" */
		protected String name;

		/**
		 * Create a new ImportStatement.
		 * 
		 * @param name
		 *            this may be "import java.util.List" or simply
		 *            "java.util.List".
		 */
		public ImportStatement(String name) {
			this.name = name;
			if (name.startsWith("import "))
				name = name.substring(7);
			if (name.endsWith(";"))
				name = name.substring(0, name.length() - 1);
		}

		@Override
		public int hashCode() {
			return name.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (!(obj instanceof ImportStatement))
				return false;
			ImportStatement other = (ImportStatement) obj;
			return name.equals(other.name);
		}

		/**
		 * Return true if this import ends with an asterisk.
		 */
		public boolean isWildcard() {
			return name.endsWith(".*");
		}

		/**
		 * This consults the workspace context for all known classnames we have
		 * the source code for that matches this import.
		 * <p>
		 * If this ImportStatement is not a wildcard, then this returns a
		 * one-element classname.
		 */
		public Collection<String> getWildcardMatches() {
			if (!isWildcard()) {
				return Arrays.asList(name);
			}

			String s = name.substring(0, name.length() - 2);

			Collection<String> classNames = new HashSet<>();
			for (String className : context.getClassNames()) {
				if (className.startsWith(s))
					classNames.add(className);
			}
			return classNames;
		}

		/**
		 * Returns the name of this import, such as "java.util.List" or
		 * "java.util.*".
		 */
		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return "import " + name + ";";
		}

		@Override
		public int compareTo(ImportStatement o) {
			return name.compareTo(o.name);
		}
	}

	/**
	 * This represents a single java source code file.
	 */
	class FileEntry {

		/**
		 * This represents a single top-level declaration inside a source code
		 * file.
		 * <p>
		 * This declaration may be a class, interface, @interface, or enum.
		 */
		class Declaration {
			/** The name of this declaration. */
			String name;
			/** The tokens used to create this declaration. */
			List<Token> tokens;
			/** A map of modifier text to their modifiers. */
			Map<String, WordToken> modifierMap = new HashMap<>();

			/**
			 * Create a Declaration from a series of Tokens.
			 */
			protected Declaration(List<Token> tokens) {
				if (tokens == null)
					throw new NullPointerException();

				this.tokens = tokens;

				List<String> types = Arrays.asList("class", "interface",
						"enum", "@interface");
				for (int a = 0; a < tokens.size(); a++) {
					if (types.contains(tokens.get(a).getText())) {
						findName: for (int b = a + 1; b < tokens.size(); b++) {
							if (tokens.get(b) instanceof WhitespaceToken) {
								// do nothing
							} else {
								name = tokens.get(a + 1).getText();
								break findName;
							}
						}

						int b = a - 1;
						scanModifiers: while (b >= 0) {
							if (tokens.get(b) instanceof WhitespaceToken) {
								// do nothing
							} else if (tokens.get(b).getText().equals("@")) {
								// do nothing, this can be an "@interface"
								// declaration
							} else if (tokens.get(b) instanceof WordToken) {
								WordToken wt = (WordToken) tokens.get(b);
								if (wt.isModifier) {
									modifierMap.put(wt.getText(), wt);
								} else {
									break scanModifiers;
								}
							} else {
								break scanModifiers;
							}
							b--;
						}
						break;
					}
				}
			}

			/**
			 * Write this declaration.
			 * 
			 * @param writer
			 *            the writer to write to.
			 * @param canIncludeOuterPublicDefinition
			 *            if true then this will include the modifier "public".
			 *            If false then this will skip over the modifier
			 *            "public" (for the top-level declaration only).
			 */
			public void write(Writer dest,
					boolean canIncludeOuterPublicDefinition) throws IOException {
				Collection<Token> tokensToSkip = new HashSet<>();

				if (!canIncludeOuterPublicDefinition) {
					WordToken t = modifierMap.get("public");
					if (t != null)
						tokensToSkip.add(t);
				}

				boolean skippedLastToken = false;
				for (Token t : tokens) {
					if (tokensToSkip.contains(t)) {
						skippedLastToken = true;
					} else {
						if (skippedLastToken && t instanceof WhitespaceToken) {
							// skip this one too!
						} else {
							dest.append(t.getText());
						}
						skippedLastToken = false;
					}
				}
			}

			/**
			 * Replace a phrase in this declaration with another phrase.
			 * <p>
			 * This assumes the phrases can be easily broken down into tokens.
			 * For example, if you search for "Entry" and replace it with
			 * "Map.Entry": that will work. But if (for some reason) you search
			 * for partial tokens like "ntry", then this will not work.
			 * 
			 * @param searchPhrase
			 *            the phrase to search for
			 * @param replacementPhrase
			 *            the phrase to replace the search phrase with.
			 */
			public void replace(String searchPhrase, String replacementPhrase) {
				List<Token> searchTokens, replacementTokens;
				try {
					searchTokens = Arrays.asList(new JavaParser().parse(
							searchPhrase, true));
					replacementTokens = Arrays.asList(new JavaParser().parse(
							replacementPhrase, true));
				} catch (Exception e) {
					throw new RuntimeException(e);
				}

				List<Token> copy = new ArrayList<>(tokens.size()
						+ replacementTokens.size() * 50);
				for (int a = 0; a < tokens.size(); a++) {
					boolean matchedSearchPhrase = true;
					for (int b = 0; b < searchTokens.size()
							&& matchedSearchPhrase; b++) {
						if (!searchTokens.get(b).getText()
								.equals(tokens.get(a + b).getText())) {
							matchedSearchPhrase = false;
						}
					}

					if (matchedSearchPhrase) {
						copy.addAll(replacementTokens);
						a += searchTokens.size() - 1;
					} else {
						copy.add(tokens.get(a));
					}
				}

				tokens = copy;
			}
		}

		/** The file this FileEntry is based on. */
		File javaFile;
		/**
		 * The class summary for the file provided. This will include the
		 * package and imports.
		 */
		JavaClassSummary summary;
		/**
		 * This boolean indicates whether this file represents the primary
		 * declaration that is allowed to contain a "public" modifier.
		 */
		boolean primary;
		/**
		 * The file header detected in this java file.
		 */
		String fileHeader;
		/**
		 * All the declarations represented in this FileEntry.
		 */
		List<Declaration> declarations = new ArrayList<>();

		/**
		 * Create a FileEntry based on a java file.
		 * 
		 * @param javaFile
		 *            the java source code to parse.
		 * @param primary
		 *            true if this should be considered the primary FileEntry
		 *            for this Breakout writer.
		 */
		protected FileEntry(File javaFile, boolean primary) throws Exception {
			this.javaFile = javaFile;
			this.summary = new JavaClassSummary(javaFile);
			this.primary = primary;

			for (String i : summary.getImportClassnames()) {
				addImportStatement(i, javaFile);
			}

			Token[] tokens;
			try (FileInputStream fileIn = new FileInputStream(javaFile)) {
				tokens = new JavaParser().parse(fileIn, true);
			}

			StringBuilder sb = new StringBuilder();
			Token lastCommentToken = null;
			int endOfImportsIndex = -1;
			int endOfPackageIndex = -1;
			for (int a = 0; a < tokens.length; a++) {
				if (endOfImportsIndex == -1
						&& tokens[a] instanceof CommentToken) {
					if (lastCommentToken == null
							|| lastCommentToken.getLineNumber() != tokens[a]
									.getLineNumber()) {
						sb.append("\n");
					}
					sb.append(tokens[a].getText().trim());
				} else if ("import".equals(tokens[a].getText())) {
					while (a < tokens.length
							&& (!";".equals(tokens[a].getText()))) {
						a++;
					}
					endOfImportsIndex = a + 1;
				} else if ("package".equals(tokens[a].getText())) {
					while (a < tokens.length
							&& (!";".equals(tokens[a].getText()))) {
						a++;
					}
					endOfPackageIndex = a + 1;
				}
			}
			fileHeader = sb.toString();

			int startOfDeclarations = endOfImportsIndex;
			if (startOfDeclarations == -1)
				startOfDeclarations = endOfPackageIndex;
			if (startOfDeclarations == -1)
				startOfDeclarations = 0;

			for (int a = startOfDeclarations; a < tokens.length; a++) {
				a = parseDeclaration(tokens, a);
			}

			for (int a = 0; a < tokens.length; a++) {
				if (tokens[a] instanceof WordToken) {
					WordToken word = (WordToken) tokens[a];
					if (!(word.isDeclarationType || word.isKeyword
							|| word.isLiteral || word.isModifier || word.isPrimitive)) {
						String str = summary.getPackageName() + "."
								+ word.getText();
						if (!classNameToFileEntryMap.containsKey(str)) {
							File otherJavaFile = context.getJavaFile(str);
							if (otherJavaFile != null) {
								addJavaFile(otherJavaFile, false);
							}
						}
					}
				}
			}
		}

		/**
		 * Parse one Declaration object from an array of tokens.
		 * 
		 * @param tokens
		 *            the tokens read from a java source code file.
		 * @param index
		 *            the index in <code>tokens</code> to begin reading from.
		 * @return the index in <code>tokens</code> where this declaration
		 *         ended.
		 */
		private int parseDeclaration(Token[] tokens, int index) {
			List<Token> myTokens = new ArrayList<>();
			int level = 0;
			while (index < tokens.length) {
				if ("{".equals(tokens[index].getText())) {
					do {
						myTokens.add(tokens[index]);
						if ("{".equals(tokens[index].getText())) {
							level++;
						} else if ("}".equals(tokens[index].getText())) {
							level--;
						}
						index++;
					} while (index < tokens.length && level != 0);

					Declaration myDeclaration = new Declaration(myTokens);
					declarations.add(myDeclaration);
					return index;
				} else {
					myTokens.add(tokens[index]);
				}

				for (BracketType t : BracketType.values()) {
					if (Character.toString(t.openChar).equals(
							tokens[index].getText())) {
						level++;
					} else if (Character.toString(t.closeChar).equals(
							tokens[index].getText())) {
						level--;
					}
				}

				index++;
			}
			return index;
		}

		/**
		 * Return all comments before the first import statement. This could
		 * include comments above or below the package declaration.
		 */
		public String getFileHeader() {
			return fileHeader;
		}

		/**
		 * Write all the declarations in this FileEntry.
		 * 
		 * @param writer
		 *            the writer to write the declarations to.
		 * @param canIncludeOuterPublicDefinition
		 *            if true then the modifier "public" is included, but if
		 *            false then that modifier is stripped out of top-level
		 *            declarations.
		 */
		public void writeBody(Writer writer,
				boolean canIncludeOuterPublicDefinition) throws IOException {
			for (Declaration declaration : declarations) {
				declaration.write(writer, canIncludeOuterPublicDefinition);
			}
		}

		/**
		 * Replace a phrase in this FileEntry with another phrase.
		 * <p>
		 * This assumes the phrases can be easily broken down into tokens. For
		 * example, if you search for "Entry" and replace it with "Map.Entry":
		 * that will work. But if (for some reason) you search for partial
		 * tokens like "ntry", then this will not work.
		 * 
		 * @param searchPhrase
		 *            the phrase to search for
		 * @param replacementPhrase
		 *            the phrase to replace the search phrase with.
		 */
		public void replace(String searchPhrase, String replacementPhrase) {
			for (Declaration declaration : declarations) {
				declaration.replace(searchPhrase, replacementPhrase);
			}
		}
	}

	/** The context we use to lookup classnames to java source code. */
	protected WorkspaceContext context;
	/** A map of classnames to the FileEntry that represents them. */
	protected Map<String, FileEntry> classNameToFileEntryMap = new TreeMap<>();
	/**
	 * A map of ImportStatements to the java source code files that included
	 * those imports.
	 */
	protected Map<ImportStatement, Collection<File>> importStatements = new TreeMap<>();

	/**
	 * Create a new Breakout object.
	 * 
	 * @param context
	 *            the context used to look up java source code files.
	 * @param primaryJavaFile
	 *            the file this Breakout object is writing. The modifiers for
	 *            top-level declarations in this file will not be modified.
	 */
	public Breakout(WorkspaceContext context, File primaryJavaFile)
			throws Exception {
		if (context == null)
			throw new NullPointerException();
		this.context = context;
		addJavaFile(primaryJavaFile, true);
	}

	/**
	 * Add an ImportStatement.
	 * 
	 * @param str
	 *            the import statement. This could be "import java.util.*;" or
	 *            simply "java.util.*".
	 * @param source
	 *            the file this import originated in.
	 */
	protected synchronized void addImportStatement(String str, File source) {
		ImportStatement is = new ImportStatement(str);
		Collection<File> c = importStatements.get(is);
		if (c == null) {
			c = new HashSet<>();
			importStatements.put(is, c);
		}
		c.add(source);
	}

	private Collection<File> consideredFiles = new HashSet<>();

	/**
	 * Add a java source code file to the master file we're composing.
	 * 
	 * @param javaFile
	 *            the source code file to add.
	 * @param primary
	 *            true if this can keep its "public" modifier, false if that
	 *            modifier must be stripped out.
	 * 
	 * @throws IOException
	 */
	private synchronized void addJavaFile(File javaFile, boolean primary)
			throws Exception {
		if (consideredFiles.add(javaFile)) {
			FileEntry entry = new FileEntry(javaFile, primary);
			FileEntry oldEntry = classNameToFileEntryMap.put(
					entry.summary.getCanonicalName(), entry);
			if (oldEntry != null
					&& (!IOUtils.equals(oldEntry.javaFile, entry.javaFile))) {
				throw new IllegalArgumentException("The classname "
						+ entry.summary.getCanonicalName()
						+ " is defined at least twice, once as "
						+ oldEntry.javaFile.getAbsolutePath() + " and once as "
						+ entry.javaFile.getAbsolutePath());
			}
		}
	}

	/**
	 * This scans all non-wildcard import statements and bundles as many files
	 * as it can find in this context in our new autogenerated java file.
	 */
	protected synchronized void resolveExplicitImportStatements()
			throws Exception {
		boolean dirty = true;
		do {
			dirty = false;
			Iterator<Entry<ImportStatement, Collection<File>>> statementIter = importStatements
					.entrySet().iterator();
			scanImports: while (statementIter.hasNext()) {
				Entry<ImportStatement, Collection<File>> entry = statementIter
						.next();
				ImportStatement statement = entry.getKey();
				if (!statement.isWildcard()) {
					String name = statement.getName();
					String lhs = name;
					String rhs = "";
					while (lhs != null) {
						File otherJava = context.getJavaFile(lhs);
						if (otherJava != null) {
							if (rhs.length() > 0) {
								int i = lhs.lastIndexOf('.');
								String q = i >= 0 ? lhs.substring(i + 1) + "."
										+ rhs : lhs + "." + rhs;
								for (File file : entry.getValue()) {
									replace(file, rhs, q);
								}
							}

							statementIter.remove();
							addJavaFile(otherJava, false);
							dirty = true;

							// we can't do more than 1 per pass or we'll get
							// concurrent modification problems:
							break scanImports;
						}

						int i = lhs.lastIndexOf('.');
						if (i == -1) {
							lhs = null;
						} else {
							if (rhs.length() > 0)
								rhs = "." + rhs;
							rhs = lhs.substring(i + 1) + rhs;
							lhs = lhs.substring(0, i);
						}
					}
				}
			}
		} while (dirty);
	}

	/**
	 * Replace a phrase in a File with another phrase.
	 * <p>
	 * This assumes we've already represented this File as a FileEntry.
	 * <p>
	 * This assumes the phrases can be easily broken down into tokens. For
	 * example, if you search for "Entry" and replace it with "Map.Entry": that
	 * will work. But if (for some reason) you search for partial tokens like
	 * "ntry", then this will not work.
	 * 
	 * @param javaSourceCode
	 *            the file to replace the phrase in.
	 * @param searchPhrase
	 *            the phrase to search for
	 * @param replacementPhrase
	 *            the phrase to replace the search phrase with.
	 */
	private synchronized void replace(File javaSourceCode, String searchPhrase,
			String replacementPhrase) throws IOException {
		String classname = JavaClassSummary.getClassName(javaSourceCode);
		FileEntry entry = classNameToFileEntryMap.get(classname);
		if (entry == null)
			throw new IllegalArgumentException();

		entry.replace(searchPhrase, replacementPhrase);
	}

	/**
	 * This produces the autogenerated java source code.
	 */
	@Override
	public synchronized String toString() {
		try {

			resolveExplicitImportStatements();
			// TODO: this doesn't address wildcard import statements, we need to
			// test against compilation

			if (classNameToFileEntryMap.size() == 1) {
				return IOUtils.read(classNameToFileEntryMap.values().iterator()
						.next().javaFile);
			}

			try (StringWriter writer = new StringWriter()) {
				FileEntry primary = getPrimaryEntry();
				writer.append(getMasterFileHeader() + "\n\n");
				if (primary.getFileHeader().trim().length() > 0)
					writer.append(primary.getFileHeader() + "\n\n");
				writer.append("package " + primary.summary.getPackageName()
						+ ";\n\n");

				for (Entry<ImportStatement, Collection<File>> importStatement : importStatements
						.entrySet()) {
					writer.append("import "
							+ importStatement.getKey().getName() + ";");
					writer.append("\n");
				}

				primary.writeBody(writer, true);

				for (FileEntry entry : classNameToFileEntryMap.values()) {
					if (entry != primary)
						entry.writeBody(writer, false);
				}

				return writer.toString();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Return the header displayed at the top of the autogenerated java file.
	 */
	protected synchronized String getMasterFileHeader() {
		String str = "/**\n"
				+ " * This file was auto-generated by the Breakout class available here: https://github.com/mickleness/pumpernickel/tree/master/src/main/java/com/pump/breakout\n";

		FileEntry primary = getPrimaryEntry();
		str = str + " *\n";
		str = str
				+ " * This file is modeled after "
				+ primary.summary.getCanonicalName()
				+ ", but it is bundled to include the following additional files:\n *\n";

		for (FileEntry entry : classNameToFileEntryMap.values()) {
			if (!entry.primary)
				str = str + " * " + entry.summary.getCanonicalName() + "\n";
		}
		str = str + " */\n";
		return str;
	}

	/**
	 * Return the FileEntry associated with the one primary entry in this
	 * autogenerated file.
	 */
	protected FileEntry getPrimaryEntry() {
		for (FileEntry entry : classNameToFileEntryMap.values()) {
			if (entry.primary)
				return entry;
		}
		// this shouldn't happen
		return null;
	}
}