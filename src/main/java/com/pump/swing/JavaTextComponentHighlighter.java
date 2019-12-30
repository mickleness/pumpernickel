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
package com.pump.swing;

import java.awt.Color;
import java.awt.Font;
import java.io.Reader;
import java.io.StringReader;

import javax.swing.text.AttributeSet;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import com.pump.io.parser.Parser.CommentToken;
import com.pump.io.parser.Parser.StringToken;
import com.pump.io.parser.Parser.SymbolCharToken;
import com.pump.io.parser.Parser.UnparsedToken;
import com.pump.io.parser.Token;
import com.pump.io.parser.java.JavaParser;
import com.pump.io.parser.java.JavaParser.CharToken;
import com.pump.io.parser.java.JavaParser.WordToken;
import com.pump.text.TokenTextComponentHighlighter;
import com.pump.util.Receiver;

/**
 * This highlights text using a monospaced formatting scheme similar to the
 * default Eclipse formatting options.
 * 
 * @see <a
 *      href="https://javagraphics.blogspot.com/2015/12/text-formatting-source-code-in-swing.html">Text:
 *      Formatting Source Code in Swing</a>
 */
public class JavaTextComponentHighlighter extends TokenTextComponentHighlighter {

	protected SimpleAttributeSet defaultAttributes;
	protected SimpleAttributeSet keywordAttributes;
	protected SimpleAttributeSet errorAttributes;
	protected SimpleAttributeSet commentAttributes;
	protected SimpleAttributeSet stringAttributes;
	protected SimpleAttributeSet importantPunctuationAttributes;

	/**
	 * Create a new JavaTextComponentHighlighter.
	 * 
	 * @param jtc
	 *            the text component to apply formatting to.
	 */
	public JavaTextComponentHighlighter(JTextComponent jtc) {
		super(jtc);

		jtc.putClientProperty("caretWidth", new Integer(3));
		jtc.getCaret().setBlinkRate(500);

		initializeAttributes();

		Font defaultFont = new Font(
				StyleConstants.getFontFamily(defaultAttributes), 0,
				StyleConstants.getFontSize(defaultAttributes));
		jtc.setFont(defaultFont);
	}

	@Override
	protected SimpleAttributeSet getDefaultAttributes() {
		initializeAttributes();
		return defaultAttributes;
	}

	/** Initialize the SimpleAttributeSets used to format text. */
	protected void initializeAttributes() {
		if (defaultAttributes == null) {
			Color keywordColor = new Color(127, 0, 85);
			Color commentColor = new Color(0, 140, 0);
			Color stringColor = new Color(45, 0, 255);
			Color errorColor = new Color(200, 0, 25);

			defaultAttributes = new SimpleAttributeSet();
			StyleConstants.setFontFamily(defaultAttributes, "Monospaced");
			StyleConstants.setFontSize(defaultAttributes, 14);

			keywordAttributes = new SimpleAttributeSet(defaultAttributes);
			StyleConstants.setBold(keywordAttributes, true);
			StyleConstants.setForeground(keywordAttributes, keywordColor);

			commentAttributes = new SimpleAttributeSet(defaultAttributes);
			StyleConstants.setForeground(commentAttributes, commentColor);

			stringAttributes = new SimpleAttributeSet(defaultAttributes);
			StyleConstants.setForeground(stringAttributes, stringColor);

			errorAttributes = new SimpleAttributeSet(defaultAttributes);
			StyleConstants.setForeground(errorAttributes, errorColor);

			importantPunctuationAttributes = new SimpleAttributeSet(
					defaultAttributes);
			StyleConstants.setBold(importantPunctuationAttributes, true);
		}
	}

	@Override
	protected AttributeSet getAttributes(Token[] tokens, int tokenIndex,
			int selectionStart, int selectionEnd) {
		Token token = tokens[tokenIndex];
		if (token instanceof StringToken || token instanceof CharToken) {
			return stringAttributes;
		}

		if (token instanceof WordToken) {
			WordToken word = (WordToken) token;
			if (word.isKeyword || word.isLiteral)
				return keywordAttributes;
		}

		if (token instanceof CommentToken) {
			return commentAttributes;
		}

		if (token instanceof UnparsedToken) {
			return errorAttributes;
		}

		if (token instanceof SymbolCharToken) {
			char ch = ((SymbolCharToken) token).getChar();
			if (ch == ';' || ch == ',') {
				return importantPunctuationAttributes;
			}
		}
		return defaultAttributes;
	}

	@Override
	protected void createTokens(String inputText, Receiver<Token> receiver)
			throws Exception {
		try (Reader reader = new StringReader(inputText)) {
			new JavaParser().parse(reader, receiver);
		}
	}
}