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
package com.pump.swing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Highlighter.HighlightPainter;
import javax.swing.text.JTextComponent;
import javax.swing.text.StyledDocument;

import com.pump.io.parser.Parser.UnparsedToken;
import com.pump.io.parser.Parser.WhitespaceToken;
import com.pump.io.parser.ParserException;
import com.pump.io.parser.Token;
import com.pump.util.BasicConsumer;
import com.pump.util.FixedCapacityMap;
import java.util.function.Consumer;

/**
 * This TextComponentHighlighter relies on {@link Token Tokens}.
 *
 */
public abstract class TokenTextComponentHighlighter extends
		TextComponentHighlighter {

	/** This manages cached results. */
	private final Map<String, ParseResults> cachedValues = new FixedCapacityMap<>(5);

	public TokenTextComponentHighlighter(JTextComponent jtc) {
		super(jtc);
	}

	/** This helps cache parsed data to avoid lots of redundant overhead. */
	private class ParseResults {
		Token[] tokens;
		RuntimeException rEx;
		Error error;
		Token[] strippedTokens;

		ParseResults(String inputText) {
			try {
				BasicConsumer<Token> consumer = new BasicConsumer<>();
				try {
					createTokens(inputText, consumer);
				} catch (ParserException e) {
					Token lastToken = consumer.getSize() == 0 ? null : consumer
							.getElementAt(consumer.getSize() - 1);
					int pos = lastToken == null ? 0 : lastToken
							.getDocumentEndIndex();
					consumer.accept(new UnparsedToken(pos, inputText, e));
				}
				tokens = consumer.toArray(new Token[0]);
			} catch (RuntimeException r) {
				rEx = r;
			} catch (Exception e) {
				e.printStackTrace();
				tokens = new Token[] { new Token(inputText, 0, 0, 0) };
			} catch (Error e) {
				error = e;
			}
		}

		protected Token[] getTokens() {
			if (rEx != null)
				throw rEx;
			if (error != null)
				throw error;
			return tokens;
		}

		protected Token[] getStrippedTokens() {
			if (rEx != null)
				throw rEx;
			if (error != null)
				throw error;
			if (strippedTokens == null) {
				strippedTokens = stripWhitespace(tokens);
			}
			return strippedTokens;
		}

		private Token[] stripWhitespace(Token[] tokens) {
			List<Token> returnValue = new ArrayList<>();
			for (Token token : tokens) {
				if (!(token instanceof WhitespaceToken)) {
					returnValue.add(token);
				}
			}
			return returnValue.toArray(new Token[0]);
		}
	}

	/**
	 * Return the Tokens that are parsed from this text. Because
	 * JavaParser.parse(..) can be expensive for complex code, this method
	 * caches some values. When possible you should avoid calling
	 * JavaParser.parse(..) and use this method instead.
	 * 
	 * @param text
	 *            the java source code to parse.
	 * @param includeWhitespace
	 *            true if WhitespaceTokens should be included, false if they
	 *            should be stripped away.
	 * @return Return the Tokens that are parsed from this text.
	 */
	public synchronized Token[] getTokens(String text, boolean includeWhitespace) {
		if (!active) {
			return new Token[] { new Token(text, 0, 0, 0) };
		}
		text = text.replace("\r\n", "\n");
		ParseResults results = cachedValues.get(text);
		if (results == null) {
			results = new ParseResults(text);
			cachedValues.put(text, results);
		}
		if (includeWhitespace)
			return results.getTokens();
		return results.getStrippedTokens();
	}

	protected abstract void createTokens(String inputText,
			Consumer<Token> consumer) throws Exception;

	@Override
	protected void formatTextComponent(String text, StyledDocument doc,
			int selectionStart, int selectionEnd) throws BadLocationException {

		Token[] tokens = getTokens(text, true);
		final Map<Token, AttributeSet> tokenAttributes = new TreeMap<>();

		for (int index = 0; index < tokens.length; index++) {
			HighlightPainter painter = getHighlightPainter(tokens, index,
					selectionStart, selectionEnd);
			if (painter != null) {
				allHighlights.add(jtc.getHighlighter().addHighlight(
						tokens[index].getDocumentStartIndex(),
						tokens[index].getDocumentEndIndex(), painter));
			}
			AttributeSet attributes = getAttributes(tokens, index,
					selectionStart, selectionEnd);
			if (attributes != null) {
				tokenAttributes.put(tokens[index], attributes);
			}
		}

		for (Token token : tokenAttributes.keySet()) {
			AttributeSet attr = tokenAttributes.get(token);
			doc.setCharacterAttributes(token.getDocumentStartIndex(),
					token.getLength(), attr, true);
		}
	}

	/** @return the HighlightPainter for tokenIndex in the list of tokens. */
	protected HighlightPainter getHighlightPainter(Token[] allToken,
			int tokenIndex, int selectionStart, int selectionEnd) {
		return null;
	}

	/** @return the AttributeSet for tokenIndex in the list of tokens. */
	protected AttributeSet getAttributes(Token[] allTokens, int tokenIndex,
			int selectionStart, int selectionEnd) {
		return null;
	}
}