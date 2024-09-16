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
package com.pump.io.parser;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.CharacterIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.pump.io.parser.java.JavaParser.BracketType;
import com.pump.util.BasicReceiver;
import com.pump.util.Receiver;

public abstract class Parser {

	/**
	 * This token is used to represent a Java comment (single-line and
	 * multiline)
	 */
	public static class CommentToken extends Token {
		public CommentToken(String tokenText, int tokenStart, int lineNumber,
				int documentStart) {
			super(tokenText, tokenStart, lineNumber, documentStart);
		}

		public CommentToken(String tokenText, int tokenStart) {
			this(tokenText, tokenStart, 0, tokenStart);
		}
	}

	public static class UnparsedToken extends Token {

		private static int getLinePosition(int docPosition, String doc) {
			int lastLineStart = 0;
			for (int a = 0; a < doc.length(); a++) {
				char ch = doc.charAt(a);
				char next = a + 1 < doc.length() ? doc.charAt(a + 1)
						: CharacterIterator.DONE;
				if (ch == '\r' && next == '\n') {
					lastLineStart = a + 2;
					a++;
				} else if (ch == '\n' || ch == '\r') {
					lastLineStart = a + 1;
				}
			}
			return docPosition - lastLineStart;
		}

		private static int getLineNumber(int docPosition, String doc) {
			int lineNumber = 0;
			for (int a = 0; a < doc.length(); a++) {
				char ch = doc.charAt(a);
				char next = a + 1 < doc.length() ? doc.charAt(a + 1)
						: CharacterIterator.DONE;
				if (ch == '\r' && next == '\n') {
					lineNumber++;
					a++;
				} else if (ch == '\n' || ch == '\r') {
					lineNumber++;
				}
			}
			return lineNumber;
		}

		ParserException e;

		/**
		 * 
		 * @param docPosition
		 *            the last position in the document of parsable content.
		 * @param doc
		 *            the original document.
		 */
		public UnparsedToken(int docPosition, String doc, ParserException e) {
			super(doc.substring(docPosition),
					getLinePosition(docPosition, doc), getLineNumber(
							docPosition, doc), docPosition);
			setException(e);
		}
	}

	/**
	 * This token is used to represent a consecutive series of whitespace.
	 * <p>
	 * Note this is the only token where the line number attribute is
	 * misleading. This token may include several line returns, so the line
	 * number this ends on may not be the line number this starts on.
	 */
	public static class WhitespaceToken extends Token {
		public WhitespaceToken(String tokenText, int tokenStart,
				int lineNumber, int documentStart) {
			super(tokenText, tokenStart, lineNumber, documentStart);
		}

		public WhitespaceToken(String tokenText, int tokenStart) {
			this(tokenText, tokenStart, 0, tokenStart);
		}

		/**
		 * Split this token into a series of tokens that end in the character(s)
		 * provided. For example, if this token represents: "\\t\\t\\n\\t" and
		 * you split using the "\\n" char, then this will return two tokens:
		 * "\\t\\t\\n" and "\\t"
		 * 
		 * @param markers
		 *            characters that mark where to break to start a new token.
		 * @return a series of tokens that are parts of this token.
		 */
		public WhitespaceToken[] split(char... markers) {
			List<WhitespaceToken> list = new ArrayList<>();
			int startingPos = 0;
			int lineOffset = 0;
			StringBuilder pending = new StringBuilder();
			for (int a = 0; a < getText().length(); a++) {
				char ch = getText().charAt(a);
				boolean hit = false;
				for (char marker : markers) {
					if (ch == marker) {
						hit = true;
						break;
					}
				}
				pending.append(ch);

				if (hit) {
					list.add(new WhitespaceToken(pending.toString(),
							getStartIndex() + startingPos, getLineNumber()
									+ lineOffset, getDocumentStartIndex()
									+ startingPos));
					startingPos = a + 1;
					pending = new StringBuilder();
				}

				if (ch == '\n') {
					lineOffset++;
				}
			}
			if (pending.length() > 0) {
				list.add(new WhitespaceToken(pending.toString(),
						getStartIndex() + startingPos, getLineNumber()
								+ lineOffset, getDocumentStartIndex()
								+ startingPos));
			}
			return list.toArray(new WhitespaceToken[0]);
		}
	}

	/**
	 * This token represents a character. It is the default token used when
	 * incoming text doesn't match any other pattern.
	 */
	public static class SymbolCharToken extends Token {
		char ch;

		public SymbolCharToken(char ch, int position, int lineNumber,
				int documentStart) {
			super(Character.toString(ch), position, lineNumber, documentStart);
			this.ch = ch;
		}

		public SymbolCharToken(char ch, int position) {
			this(ch, position, 0, position);
		}

		public char getChar() {
			return ch;
		}
	}

	/**
	 * This token represents an opening or closing bracket, parentheses, or
	 * curly bracket.
	 */
	public static class BracketCharToken extends SymbolCharToken implements
			MatchingToken<BracketCharToken> {
		char ch;
		BracketCharToken matching;
		BracketType bracketType;
		boolean open;

		public BracketCharToken(BracketType bracketType, boolean open,
				int position) {
			this(bracketType, open, position, 0, position);
		}

		public BracketCharToken(BracketType bracketType, boolean open,
				int position, int lineNumber, int documentStart) {
			super(open ? bracketType.openChar : bracketType.closeChar,
					position, lineNumber, documentStart);
			this.bracketType = bracketType;
			this.open = open;
		}

		/**
		 * Return the type of bracket this token represents.
		 * 
		 * @return the type of bracket this token represents.
		 */
		public BracketType getBracketType() {
			return bracketType;
		}

		/**
		 * Return true if this is the opening bracket, false otherwise.
		 * 
		 * @return true if this is the opening bracket, false otherwise.
		 */
		public boolean isOpen() {
			return open;
		}

		@Override
		public void setMatch(BracketCharToken token) {
			if (token.bracketType != this.bracketType)
				throw new IllegalArgumentException("bracket types must match");

			if (open == token.open)
				throw new IllegalArgumentException("brackets must relate");

			matching = token;
			token.matching = this;
		}

		/**
		 * Return the optional matching (opposing) bracket token.
		 * 
		 * @return the optional matching (opposing) bracket token. This may be
		 *         null for any number of reasons, including incomplete or
		 *         poorly formed code.
		 */
		@Override
		public BracketCharToken getMatch() {
			return matching;
		}

		/**
		 * Return this token's text as a single character.
		 */
		public char getChar() {
			return ch;
		}
	}

	/** This token represents a String literal. */
	public static class StringToken extends Token {
		final String decodedString;

		/**
		 * @param decodedString
		 *            the decoded String. For example, the encoded String may be
		 *            "\u1234", but the decoded String will be a single
		 *            character.
		 */
		public StringToken(String tokenText, String decodedString,
				int tokenStart) {
			this(tokenText, decodedString, tokenStart, 0, tokenStart);
		}

		/**
		 * @param decodedString
		 *            the decoded String. For example, the encoded String may be
		 *            "\u1234", but the decoded String will be a single
		 *            character.
		 */
		public StringToken(String tokenText, String decodedString,
				int tokenStart, int lineNumber, int documentStart) {
			super(tokenText, tokenStart, lineNumber, documentStart);
			if (tokenText.charAt(0) != '\"') {
				setException(new ParserException(this,
						"The token text must begin with a double-quotation mark"));
			} else if (tokenText.charAt(tokenText.length() - 1) != '\"') {
				setException(new ParserException(this,
						"The token text must end with a double-quotation mark"));
			}
			this.decodedString = decodedString;
		}

		public String getDecodedString() {
			return decodedString;
		}
	}

	public static class SkipWhiteSpaceReceiver implements Receiver<Token> {

		Receiver<Token> receiver;

		public SkipWhiteSpaceReceiver(Receiver<Token> receiver) {
			Objects.requireNonNull(receiver);
			this.receiver = receiver;
		}

		@Override
		public void add(Token... elements) {
			for (Token e : elements) {
				if (!(e instanceof WhitespaceToken)) {
					receiver.add(e);
				}

			}
		}
	}

	/**
	 * Parse Java source code.
	 * 
	 * @param in
	 *            the stream to create {@link Token} objects
	 *            from.
	 * @param includeWhitespaceTokens
	 *            if false then this will ignore {@link WhitespaceToken}
	 *            objects.
	 * @param receiver
	 *            the object that will receive tokens as they become available.
	 */
	public void parse(InputStream in, boolean includeWhitespaceTokens,
			Receiver<Token> receiver) throws Exception {
		Objects.requireNonNull(in);
		Objects.requireNonNull(receiver);

		if (!includeWhitespaceTokens) {
			receiver = new SkipWhiteSpaceReceiver(receiver);
		}
		parse(in, receiver);
	}

	/**
	 * Parse Java source code.
	 * 
	 * @param in
	 *            the stream to create {@link Token} objects
	 *            from.
	 * @param includeWhitespaceTokens
	 *            if false then this will ignore {@link WhitespaceToken}
	 *            objects.
	 * @return an array of Tokens from the String provided.
	 */
	public Token[] parse(InputStream in, boolean includeWhitespaceTokens)
			throws Exception {
		BasicReceiver<Token> receiver = new BasicReceiver<>();
		parse(in, includeWhitespaceTokens, receiver);
		return receiver.toArray(new Token[receiver.getSize()]);
	}

	/**
	 * Parse tokens, and then group them by line
	 * 
	 * @param in
	 *            the stream to create {@link Token} objects
	 *            from.
	 * @param includeWhitespaceTokens
	 *            if false then this will ignore {@link WhitespaceToken}
	 *            objects.
	 * @return each element in the outer array is a line of text.
	 */
	public Token[][] parseLines(InputStream in, boolean includeWhitespaceTokens)
			throws Exception {

		final List<Token[]> allLines = new ArrayList<>();
		final List<Token> uncommittedLine = new ArrayList<>();

		Receiver<Token> receiver = new Receiver<>() {
			int lastLineNumber = 0;

			@Override
			public void add(Token... tokens) {
				for (Token token : tokens) {
					int lineNumber = token.getLineNumber();
					if (lineNumber != lastLineNumber) {
						Token[] line = uncommittedLine
								.toArray(new Token[0]);
						allLines.add(line);
						uncommittedLine.clear();
					}
					uncommittedLine.add(token);

					lastLineNumber = lineNumber;
				}
			}

		};
		parse(in, includeWhitespaceTokens, receiver);

		Token[] line = uncommittedLine
				.toArray(new Token[0]);
		allLines.add(line);

		return allLines.toArray(new Token[allLines.size()][]);
	}

	/**
	 * Parse Java source code.
	 * 
	 * @param expr
	 *            the text to parse.
	 * @param includeWhitespaceTokens
	 *            if false then this will ignore {@link WhitespaceToken}
	 *            objects.
	 * @return an array of Tokens from the String provided.
	 */
	public Token[] parse(String expr, boolean includeWhitespaceTokens)
			throws Exception {
		BasicReceiver<Token> receiver = new BasicReceiver<>();
		try {
			parse(new ByteArrayInputStream(expr.getBytes(StandardCharsets.UTF_8)), includeWhitespaceTokens, receiver);
		} catch (IOException e) {
			// we shouldn't have an IOException for a String
			throw new RuntimeException(e);
		}
		return receiver.toArray(new Token[receiver.getSize()]);
	}

	/**
	 * Parse data from an InputStream.
	 * 
	 * @param in
	 *            the stream to create {@link Token} objects
	 *            from.
	 * @param receiver
	 *            the receiver in which tokens are placed as they are parsed.
	 */
	public abstract void parse(InputStream in, Receiver<Token> receiver)
			throws Exception;
}