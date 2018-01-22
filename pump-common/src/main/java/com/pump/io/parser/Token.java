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
package com.pump.io.parser;

/**
 * A basic text token from a parsed line of text.
 * <p>
 * For example if you split the String "3 + 15 = 18", you would probably end up
 * with these tokens: <br>
 * The number 3 <br>
 * The plus operator <br>
 * The number 15 <br>
 * The equals sign (or operator, depending on the language you're parsing) <br>
 * The number 18
 * <p>
 * Optionally some parsers may also choose to include tokens for all the
 * consecutive whitespace, so this may have 9 tokens instead of 5.
 */
public class Token implements Comparable<Token> {

	/** Convert an array of tokens back into a line of text. */
	public static String toString(Token... line) {
		StringBuffer sb = new StringBuffer();
		Token lastToken = null;
		for (int a = 0; a < line.length; a++) {
			// assume if a token is missing that it's whitespace:
			if (lastToken != null) {
				for (int b = lastToken.getDocumentEndIndex(); b < line[a]
						.getDocumentStartIndex(); b++) {
					sb.append(' ');
				}
			}
			Token t = line[a];
			sb.append(t.getText());
			lastToken = line[a];
		}
		return sb.toString();
	}

	final String text;
	final int start;
	final int lineNumber;
	final int docStart;

	ParserException exception;

	/**
	 * 
	 * @param tokenText
	 * @param tokenStart
	 *            the starting position of this token relative to this line.
	 * @param lineNumber
	 * @param documentStart
	 *            the starting position of this token relative to the document.
	 *            This should be equal to tokenStart when lineNumber is zero
	 */
	public Token(String tokenText, int tokenStart, int lineNumber,
			int documentStart) {
		text = tokenText;
		start = tokenStart;
		docStart = documentStart;
		this.lineNumber = lineNumber;
	}

	/**
	 * Return the index in the document where this token starts. This is
	 * different from {@link #getStartIndex()}, which is relative to the line
	 * number. This is relative to the entire document.
	 * 
	 * @return the index in the document where this token starts.
	 */
	public int getDocumentStartIndex() {
		return docStart;
	}

	/**
	 * Return the end position of this token relative to this document.
	 * 
	 * @return the end position of this token relative to this document.
	 */
	public int getDocumentEndIndex() {
		return docStart + text.length();
	}

	/**
	 * Return the start position of this token relative to this line. See
	 * {@link #getLineNumber()}.
	 * 
	 * @return the start position of this token relative to this line.
	 */
	public int getStartIndex() {
		return start;
	}

	/**
	 * Return the end position of this token relative to this line.
	 * 
	 * @return the end position of this token relative to this line.
	 */
	public int getEndIndex() {
		return start + text.length();
	}

	/** Return the text length of this token. */
	public int getLength() {
		return text.length();
	}

	/** Return the literal text of this token. */
	public String getText() {
		return text;
	}

	/**
	 * Return the exception associated with this Token, or null if this isn't
	 * associated with an error.
	 * 
	 * @return the exception associated with this Token, or null if this isn't
	 *         associated with an error.
	 */
	public ParserException getException() {
		return exception;
	}

	/**
	 * Assign the exception associated with this Token.
	 * 
	 * @param ex
	 *            the exception to associate with this Token.
	 */
	public void setException(ParserException ex) {
		exception = ex;
	}

	@Override
	public int hashCode() {
		return text.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Token))
			return false;
		Token other = (Token) obj;
		return other.text.equals(text) && other.docStart == docStart;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[ \"" + text + "\", at "
				+ getLineNumber() + ":" + getStartIndex() + " ("
				+ getDocumentStartIndex() + ")]";
	}

	/**
	 * Get the line number of this token. See {@link #getStartIndex()}.
	 * 
	 * @return the line number of this token.
	 */
	public int getLineNumber() {
		return lineNumber;
	}

	@Override
	public int compareTo(Token o) {
		if (lineNumber < o.lineNumber)
			return -1;
		if (lineNumber > o.lineNumber)
			return 1;
		if (start < o.start)
			return -1;
		if (start > o.start)
			return 1;
		return text.compareTo(o.text);
	}
}