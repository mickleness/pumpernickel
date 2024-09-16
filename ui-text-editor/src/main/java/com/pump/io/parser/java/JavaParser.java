/**
 * This software is released as part of the Pumpernickel project.
 * <p>
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * <p>
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.io.parser.java;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.text.CharacterIterator;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import com.pump.io.LookAheadReader;
import com.pump.io.parser.Parser;
import com.pump.io.parser.ParserException;
import com.pump.io.parser.Token;
import com.pump.util.Receiver;

/**
 * This helps convert Java source code into Java
 * {@link Token} objects.
 */
public class JavaParser extends Parser {

	/** An enum representing all possible Java modifiers. */
	public enum JavaModifier {
		ABSTRACT(Modifier.ABSTRACT), FINAL(Modifier.FINAL), NATIVE(
				Modifier.NATIVE), PRIVATE(Modifier.PRIVATE), PROTECTED(
				Modifier.PROTECTED), PUBLIC(Modifier.PUBLIC), STATIC(
				Modifier.STATIC), STRICT(Modifier.STRICT), SYNCHRONIZED(
				Modifier.SYNCHRONIZED), TRANSIENT(Modifier.TRANSIENT), VOLATILE(
				Modifier.VOLATILE);

		/**
		 * The constant in the Modifier class that relates to this modifier.
		 */
		public final int constant;

		JavaModifier(int c) {
			this.constant = c;
		}
	}

	/**
	 * An enum representing all possible Java declaration types (class,
	 * interface, enum)
	 */
	public enum DeclarationType {
		CLASS, INTERFACE, ENUM
	}

	/**
	 * "true", "false" or "null". This is NOT a subset of any other list; it is
	 * a unique designation.
	 */
	public static final Set<String> JAVA_LITERALS = new HashSet<>();

	/**
	 * "boolean", "char", "double", "float", "int", "long", or "short". This is
	 * a subset of {@link #JAVA_KEYWORDS}.
	 */
	public static final Set<String> JAVA_PRIMITIVES = new HashSet<>();

	/**
	 * This is a superset containing {@link #JAVA_PRIMITIVES},
	 * {@link #JAVA_MODIFIERS}, {@link #JAVA_DECLARATION_TYPES}, and other
	 * reserved keywords used in Java.
	 */
	public static final Set<String> JAVA_KEYWORDS = new HashSet<>();
	/**
	 * "abstract", "final", "native", "private", "protected", "public",
	 * "static", "strict", "synchronized", "transient", or "volatile". This is a
	 * subset of {@link #JAVA_KEYWORDS}.
	 */
	public static final Set<String> JAVA_MODIFIERS = new HashSet<>();
	/**
	 * "class", "enum", or "interface". This is a subset of
	 * {@link #JAVA_KEYWORDS}.
	 */
	public static final Set<String> JAVA_DECLARATION_TYPES = new HashSet<>();
	static {
		Collections.addAll(JAVA_KEYWORDS, "abstract", "assert", "boolean",
				"break", "case", "catch", "class", "char", "const", "continue",
				"default", "do", "double", "else", "enum", "extends", "final",
				"finally", "float", "for", "goto", "if", "implements",
				"import", "instanceof", "int", "interface", "long", "native",
				"new", "null", "package", "private", "protected", "public",
				"return", "short", "static", "strictfp", "super", "switch",
				"synchronized", "synchronize", "this", "throw", "throws",
				"transient", "try", "void", "volatile", "while");
		Collections.addAll(JAVA_PRIMITIVES, "boolean", "char", "double",
				"float", "int", "long", "short");
		Collections.addAll(JAVA_MODIFIERS, "abstract", "final", "native",
				"private", "protected", "public", "static", "strict",
				"synchronized", "transient", "volatile");
		Collections
				.addAll(JAVA_DECLARATION_TYPES, "class", "enum", "interface");
		Collections.addAll(JAVA_LITERALS, "true", "false", "null");
	}

	/**
	 * This token represents any word of text. A "word" could be a variable
	 * name, a method name, or a reserved word. It is any string that starts
	 * with a letter followed by a series of letters, digits or underscores.
	 */
	public static class WordToken extends Token {
		public final boolean isKeyword;
		public final boolean isPrimitive;
		public final boolean isLiteral;
		public final boolean isModifier;
		public final boolean isDeclarationType;

		public WordToken(String tokenText, int tokenStart) {
			this(tokenText, tokenStart, 0, tokenStart);
		}

		public WordToken(String tokenText, int tokenStart, int lineNumber,
				int documentStart) {
			super(tokenText, tokenStart, lineNumber, documentStart);
			isKeyword = JAVA_KEYWORDS.contains(tokenText);
			isPrimitive = JAVA_PRIMITIVES.contains(tokenText);
			isLiteral = JAVA_LITERALS.contains(tokenText);
			isModifier = JAVA_MODIFIERS.contains(tokenText);
			isDeclarationType = JAVA_DECLARATION_TYPES.contains(tokenText);
		}
	}

	/** This token represents a char literal. */
	public static class CharToken extends Token {
		final char decodedChar;

		/**
		 * @param decodedChar
		 *            the token this character expresses.
		 */
		public CharToken(String tokenText, char decodedChar, int tokenStart) {
			this(tokenText, decodedChar, tokenStart, 0, tokenStart);
		}

		/**
		 * @param decodedChar
		 *            the character this token expresses.
		 */
		public CharToken(String tokenText, char decodedChar, int tokenStart,
				int lineNumber, int documentStart) {
			super(tokenText, tokenStart, lineNumber, documentStart);
			if (tokenText.charAt(0) != '\'') {
				setException(new ParserException(this,
						"The token text must begin with a single-quotation mark"));
			} else if (tokenText.charAt(tokenText.length() - 1) != '\'') {
				setException(new ParserException(this,
						"The token text must end with a single-quotation mark"));
			}
			this.decodedChar = decodedChar;
		}

		public char getDecodedChar() {
			return decodedChar;
		}
	}

	/** This abstract token represents number. */
	public abstract static class NumberToken extends Token {

		protected NumberToken(String tokenText, int tokenStart, int lineNumber,
				int documentStart) {
			super(tokenText, tokenStart, lineNumber, documentStart);
		}

		public abstract Number getNumber();
	}

	/** This NumberToken represents a double value. */
	public static class DoubleToken extends NumberToken {
		double doubleValue;

		public DoubleToken(String substring, int startIndex, int lineNumber,
				int documentStart) {
			super(substring, startIndex, lineNumber, documentStart);
			try {
				doubleValue = Double.parseDouble(substring);
			} catch (NumberFormatException e) {
				setException(new ParserException(this, e));
			}
		}

		public DoubleToken(String substring, int startIndex) {
			this(substring, startIndex, 0, startIndex);
		}

		public double doubleValue() {
			return doubleValue;
		}

		@Override
		public Number getNumber() {
			return doubleValue();
		}
	}

	/** This NumberToken represents a float value. */
	public static class FloatToken extends NumberToken {
		float floatValue;

		public FloatToken(String substring, int startIndex, int lineNumber,
				int documentStart) {
			super(substring, startIndex, lineNumber, documentStart);
			try {
				floatValue = Float.parseFloat(substring);
			} catch (NumberFormatException e) {
				setException(new ParserException(this, e));
			}
		}

		public FloatToken(String substring, int startIndex) {
			this(substring, startIndex, 0, startIndex);
		}

		public float floatValue() {
			return floatValue;
		}

		@Override
		public Number getNumber() {
			return floatValue();
		}
	}

	/** This NumberToken represents a long value. */
	public static class LongToken extends NumberToken {
		long longValue;

		public LongToken(String substring, int startIndex, int lineNumber,
				int documentStart) {
			super(substring, startIndex, lineNumber, documentStart);
			String s = substring.toLowerCase();
			if (s.endsWith("l"))
				s = s.substring(0, s.length() - 1);
			try {
				longValue = Long.parseLong(s);
			} catch (NumberFormatException e) {
				setException(new ParserException(this, e));
			}
		}

		public LongToken(String substring, int startIndex) {
			this(substring, startIndex, 0, startIndex);
		}

		public long longValue() {
			return longValue;
		}

		@Override
		public Number getNumber() {
			return longValue();
		}
	}

	/** This NumberToken represents an integer value. */
	public static class IntegerToken extends NumberToken {
		int intValue;

		public IntegerToken(String substring, int startIndex, int lineNumber,
				int documentStart) {
			super(substring, startIndex, lineNumber, documentStart);
			try {
				intValue = Integer.parseInt(substring);
			} catch (NumberFormatException e) {
				setException(new ParserException(this, e));
			}
		}

		public IntegerToken(String substring, int startIndex) {
			this(substring, startIndex, 0, startIndex);
		}

		public int intValue() {
			return intValue;
		}

		@Override
		public Number getNumber() {
			return intValue();
		}
	}

	/** This represents different types of brackets. */
	public enum BracketType {
		PARENTHESES('(', ')'), BRACES('[', ']'), BRACKETS('{', '}'), CHEVRONS(
				'‹', '›');

		public final char openChar, closeChar;

		BracketType(char openChar, char closeChar) {
			this.openChar = openChar;
			this.closeChar = closeChar;
		}
	}

	@Override
	public void parse(InputStream in, Receiver<Token> receiver)
			throws IOException {
		try (Reader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
			parse(reader, receiver);
		}
	}

	public void parse(Reader reader, Receiver<Token> receiver)
			throws IOException {
		LinkedList<BracketCharToken> brackets = new LinkedList<>();

		StringBuffer scratch = new StringBuffer();
		try (LookAheadReader l = new LookAheadReader(reader)) {
			int lineNumber = 0;
			int lastLineStart = 0;
			while (l.current() != CharacterIterator.DONE) {
				char ch = l.current();
				char next = l.peek(1);
				int start = (int) l.getPosition();
				if (ch == '/' && next == '*') {
					// parse a multiline comment:
					StringBuilder comment = new StringBuilder();

					boolean finished = false;
					while (l.hasNext()) {
						ch = l.current();
						next = l.peek(1);
						comment.append(ch);
						if (ch == '*' && next == '/') {
							comment.append(next);
							l.skip(2);
							finished = true;
							break;
						} else if (ch == '\r' && next == '\n') {
							lineNumber++;
							l.skip(2);
							lastLineStart = (int) l.getPosition();
						} else if (ch == '\r' || next == '\n') {
							lineNumber++;
							l.next();
							lastLineStart = (int) l.getPosition();
						} else {
							l.next();
						}
					}
					Token token = new CommentToken(comment.toString(), start
							- lastLineStart, lineNumber, start);

					receiver.add(token);
					if (!finished)
						token.setException(new ParserException(token,
								"this text appeared to have an unclosed javadoc"));

					continue;
				} else if (ch == '/' && next == '/') {
					// parse a single-line comment that occupies the rest of
					// this line:
					StringBuilder comment = new StringBuilder();

					while (l.current() != CharacterIterator.DONE) {
						ch = l.current();
						comment.append(ch);
						l.next();

						ch = l.current();
						if (ch == '\r' && l.peek(1) == '\n') {
							l.skip(1);
							break;
						} else if (ch == '\r' || ch == '\n') {
							break;
						}
					}

					receiver.add(new CommentToken(comment.toString(), start
							- lastLineStart, lineNumber, start));
					continue;
				} else if (ch == '"' || ch == '\'') {
					// parse either a String or a char literal:

					// How the String looks when encoded in Java. This includes
					// the enclosing double quotation or single quotation marks.
					StringBuilder encodedString = new StringBuilder(
							Character.toString(ch));

					// How the String is actually parsed. The encoded String may
					// include the two characters \n, but the decoded converts
					// this to one new line character.
					StringBuffer decodedString = new StringBuffer();

					l.next();
					boolean finished = false;
					Exception ex = null;
					String error = null;
					while (l.current() != CharacterIterator.DONE
							&& error == null && ex == null) {
						// store at least 6 chars, because a unicode char is
						// the longest char we may parse: ģ
						scratch.delete(0, scratch.length());
						peek(l, scratch, 6);
						int charsRead = 0;
						try {
							charsRead = JavaEncoding.decode(scratch, 0,
									decodedString);
						} catch (Exception e) {
							if (ex == null)
								ex = e;
						}
						encodedString.append(scratch.subSequence(0, charsRead));
						l.skip(charsRead);
						if (charsRead == 1) {
							char charRead = scratch.charAt(0);
							if (charRead == ch) {
								finished = true;
								decodedString.deleteCharAt(decodedString
										.length() - 1);

								break;
							} else if (charsRead == 1 && charRead == '\n'
									|| charRead == '\r') {
								error = "Line breaks in literals should be encoded as \\n or \\r.";
							}
						}
					}

					Token token;
					if (ch == '\'') {
						char newCh = decodedString.length() > 0 ? decodedString
								.charAt(0) : '?';
						token = new CharToken(encodedString.toString(), newCh,
								start - lastLineStart, lineNumber, start);
						if (decodedString.length() != 1) {
							token.setException(new ParserException(token,
									"This char token didn't evaluate to a singular character."));
						}
					} else if (ch == '\"') {
						token = new StringToken(encodedString.toString(),
								decodedString.toString(),
								start - lastLineStart, lineNumber, start);
					} else {
						throw new RuntimeException("Unexpected condition");
					}
					receiver.add(token);
					if (ex != null) {
						token.setException(new ParserException(token, ex));
					} else if (error != null) {
						token.setException(new ParserException(token, error));
					} else if (!finished) {
						token.setException(new ParserException(token,
								"This text appeared to have an unclosed literal."));
					}

					continue;
				} else if (Character.isWhitespace(ch)) {
					// parse a block of consecutive whitespace
					StringBuilder whitespace = new StringBuilder();
					int origLineStart = lastLineStart;
					int origLineNumber = lineNumber;
					do {
						ch = l.current();
						whitespace.append(ch);
						next = l.peek(1);

						if (ch == '\r' && next == '\n') {
							lineNumber++;
							l.skip(1);
							lastLineStart = (int) l.getPosition();
						} else if (ch == '\r' || ch == '\n') {
							lineNumber++;
							lastLineStart = (int) l.getPosition() + 1;
						}
						l.next();
						l.peek(1);
					} while (Character.isWhitespace(l.current()) && l.hasNext());

					receiver.add(new WhitespaceToken(whitespace.toString(),
							start - origLineStart, origLineNumber, start));
					continue;
				} else if (Character.isLetter(ch)) {
					// parse a word (any consecutive string letters/digits
					// that follow a letter):

					StringBuilder word = new StringBuilder();
					while (Character.isLetterOrDigit(ch) || ch == '_') {
						word.append(ch);
						l.next();
						ch = l.current();
					}
					receiver.add(new WordToken(word.toString(), start
							- lastLineStart, lineNumber, start));
					continue;
				}

				// TODO: address hex numbers

				readNumber:
				if (ch == '+' || ch == '-' || Character.isDigit(ch)
						|| ch == '.') {
					// now the hardest one to parse: numbers.

					// "[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?" (and
					// optional f or L)

					// So first, just tackle the left half of this
					// expression:
					// [-+]?[0-9]*\\.?[0-9]+

					String error = null;
					StringBuilder number = new StringBuilder();
					int pos = 0;
					if (ch == '+' || ch == '-') {
						number.append(ch);
						pos++;
						if (l.peek(pos) == '.') {
							number.append('.');
							pos++;
							int requiredClusterCtr = 0;
							while (Character.isDigit(l.peek(pos))) {
								number.append(l.peek(pos));
								pos++;
								requiredClusterCtr++;
							}
							if (requiredClusterCtr == 0) {
								error = "This number must have digits after the decimal.";
							}
						} else {
							int clusterCtr = 0;
							while (Character.isDigit(l.peek(pos))) {
								number.append(l.peek(pos));
								pos++;
								clusterCtr++;
							}
							if (l.peek(pos) == '.') {
								number.append(l.peek(pos));
								pos++;
								int requiredClusterCtr = 0;
								while (Character.isDigit(l.peek(pos))) {
									number.append(l.peek(pos));
									pos++;
									requiredClusterCtr++;
								}
								if (requiredClusterCtr == 0) {
									error = "This number must have digits after the decimal.";
								}
							} else {
								if (clusterCtr == 0) {
									// we're not reading a number, we just
									// had a plus or minus sign.
									break readNumber;
								}
							}
						}
					} else if (ch == '.') {
						number.append('.');
						pos++;
						int requiredClusterCtr = 0;
						while (Character.isDigit(l.peek(pos))) {
							number.append(l.peek(pos));
							pos++;
							requiredClusterCtr++;
						}
						if (requiredClusterCtr == 0) {
							// this wasn't a number, it was just a period.
							break readNumber;
						}
					} else {
						int clusterCtr = 0;
						while (Character.isDigit(l.peek(pos))) {
							number.append(l.peek(pos));
							pos++;
							clusterCtr++;
						}
						if (clusterCtr == 0) {
							// how did we get here if we didn't have a
							// cluster of digits?
							throw new RuntimeException("Unexpected condition");
						}
						if (l.peek(pos) == '.') {
							number.append(l.peek(pos));
							pos++;
							int requiredClusterCtr = 0;
							while (Character.isDigit(l.peek(pos))) {
								number.append(l.peek(pos));
								pos++;
								requiredClusterCtr++;
							}
							if (requiredClusterCtr == 0) {
								error = "This number must have digits after the decimal.";
							}
						}
					}

					// whew. That was the hard(er) part. Now we just parse
					// the optional tail of that expression:
					Token newToken;
					if (l.peek(pos) == 'f' || l.peek(pos) == 'F') {
						number.append(l.peek(pos));
						newToken = new FloatToken(number.toString(), start
								- lastLineStart, lineNumber, start);
					} else if (l.peek(pos) == 'd' || l.peek(pos) == 'D') {
						number.append(l.peek(pos));
						newToken = new DoubleToken(number.toString(), start
								- lastLineStart, lineNumber, start);
					} else if (l.peek(pos) == 'l' || l.peek(pos) == 'L') {
						number.append(l.peek(pos));
						newToken = new LongToken(number.toString(), start
								- lastLineStart, lineNumber, start);
					} else if (l.peek(pos) == 'e' || l.peek(pos) == 'E') {
						// almost done, but now we have to parse:

						// ([eE][-+]?[0-9]+)?

						number.append(l.peek(pos));
						pos++;
						if (l.peek(pos) == '+' || l.peek(pos) == '-') {
							number.append(l.peek(pos));
							pos++;
						}
						int requiredClusterCtr = 0;
						while (Character.isDigit(l.peek(pos))) {
							number.append(l.peek(pos));
							pos++;
							requiredClusterCtr++;
						}
						if (requiredClusterCtr == 0) {
							error = "This exponent must have digits after exponent indicator.";
						}
						newToken = new DoubleToken(number.toString(), start
								- lastLineStart, lineNumber, start);
					} else {
						if (number.indexOf(".") != -1) {
							newToken = new DoubleToken(number.toString(), start
									- lastLineStart, lineNumber, start);
						} else {
							newToken = new IntegerToken(number.toString(),
									start - lastLineStart, lineNumber, start);
						}
					}

					if (error != null) {
						newToken.setException(new ParserException(newToken,
								error));
					}

					receiver.add(newToken);
					l.skip(number.length());
					continue;
				}

				BracketType[] bracketTypes = BracketType.values();
				BracketCharToken bct = null;
				for (int a = 0; a < bracketTypes.length && bct == null; a++) {
					if (ch == bracketTypes[a].closeChar) {
						bct = new BracketCharToken(bracketTypes[a], false,
								start - lastLineStart, lineNumber, start);
					} else if (ch == bracketTypes[a].openChar) {
						bct = new BracketCharToken(bracketTypes[a], true, start
								- lastLineStart, lineNumber, start);
					}
				}
				if (bct != null) {
					if (brackets != null) {
						if (bct.isOpen()) {
							brackets.add(bct);
						} else {
							BracketCharToken last = (brackets.size() == 0) ? null
									: brackets.removeLast();
							if (last != null
									&& last.isOpen()
									&& last.getBracketType().equals(
									bct.getBracketType())) {
								last.setMatch(bct);
							} else {
								// signal that bracket matching is broken,
								// and we stop trying to match
								brackets = null;
							}
						}
					}
					receiver.add(bct);
				} else {
					receiver.add(new SymbolCharToken(ch, start - lastLineStart,
							lineNumber, start));
				}
				l.next();
			}
		}
	}

	/**
	 * Store the first {@code maximum} chars (accessed by peeking) into the
	 * destination buffer. This starts with {@code reader.peek(0)}, which means
	 * it will include the current queued char.
	 */
	private static void peek(LookAheadReader reader, StringBuffer dest,
			int maximum) {
		for (int a = 0; a < maximum; a++) {
			dest.append(reader.peek(a));
		}
	}
}