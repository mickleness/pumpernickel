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

import javax.swing.text.AttributeSet;
import javax.swing.text.JTextComponent;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import com.pump.io.parser.Parser.StringToken;
import com.pump.io.parser.Parser.SymbolCharToken;
import com.pump.io.parser.Parser.UnparsedToken;
import com.pump.io.parser.Token;
import com.pump.io.parser.java.JavaParser.CharToken;
import com.pump.io.parser.xml.XMLParser;
import com.pump.io.parser.xml.XMLParser.CommentToken;
import com.pump.io.parser.xml.XMLParser.TagDeclarationToken;
import com.pump.io.parser.xml.XMLParser.WordToken;
import com.pump.text.TokenTextComponentHighlighter;
import com.pump.util.Receiver;

public class XMLFormatter extends TokenTextComponentHighlighter {

	protected SimpleAttributeSet defaultAttributes, elementNameAttributes,
			errorAttributes, commentAttributes, stringAttributes,
			importantPunctuationAttributes;

	/**
	 * Create a new XMLFormatter.
	 * 
	 * @param jtc
	 *            the text component to apply formatting to.
	 */
	public XMLFormatter(JTextComponent textPane) {
		super(textPane);

		textPane.putClientProperty("caretWidth", Integer.valueOf(3));
		textPane.getCaret().setBlinkRate(500);

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

			elementNameAttributes = new SimpleAttributeSet(defaultAttributes);
			StyleConstants.setBold(elementNameAttributes, true);
			StyleConstants.setForeground(elementNameAttributes, keywordColor);

			commentAttributes = new SimpleAttributeSet(defaultAttributes);
			StyleConstants.setForeground(commentAttributes, commentColor);

			errorAttributes = new SimpleAttributeSet(defaultAttributes);
			StyleConstants.setForeground(errorAttributes, errorColor);

			stringAttributes = new SimpleAttributeSet(defaultAttributes);
			StyleConstants.setForeground(stringAttributes, stringColor);

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

		Token prev = tokenIndex == 0 ? null : tokens[tokenIndex - 1];
		if (token instanceof WordToken && prev instanceof TagDeclarationToken) {
			return elementNameAttributes;
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
		new XMLParser().parse(inputText, receiver);
	}
}