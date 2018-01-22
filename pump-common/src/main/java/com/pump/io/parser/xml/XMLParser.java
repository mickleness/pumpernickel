package com.pump.io.parser.xml;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.CharacterIterator;
import java.util.Arrays;

import com.pump.io.LookAheadReader;
import com.pump.io.parser.MatchingToken;
import com.pump.io.parser.Parser;
import com.pump.io.parser.ParserException;
import com.pump.io.parser.Token;
import com.pump.util.Receiver;

public class XMLParser extends Parser {

	public static class StartTagToken extends TagDeclarationToken<EndTagToken> {

		public StartTagToken(int tokenStart, int lineNumber, int documentStart,
				boolean closingElement) {
			super(closingElement ? "</" : "<", tokenStart, lineNumber,
					documentStart);
		}

		/**
		 * Return true if this token begins "</". For example the tag "<name>"
		 * is an opening tag, and the tag "</name>" is a closing tag.
		 * 
		 * @return
		 */
		public boolean isClosingTag() {
			return getText().equals("</");
		}
	}

	public static class StartDTDTagToken extends
			TagDeclarationToken<EndDTDTagToken> {
		public StartDTDTagToken(int tokenStart, int lineNumber,
				int documentStart) {
			super("<!", tokenStart, lineNumber, documentStart);
		}
	}

	public static class EndDTDTagToken extends
			TagDeclarationToken<StartDTDTagToken> {
		public EndDTDTagToken(int tokenStart, int lineNumber, int documentStart) {
			super(">", tokenStart, lineNumber, documentStart);
		}
	}

	public static class AssignmentToken extends SymbolCharToken {
		public AssignmentToken(char tokenChar, int tokenStart, int lineNumber,
				int documentStart) {
			super(tokenChar, tokenStart, lineNumber, documentStart);
		}
	}

	public static abstract class TagDeclarationToken<Z extends Token> extends
			Token implements MatchingToken<Z> {
		Z match;

		public TagDeclarationToken(String tokenText, int tokenStart,
				int lineNumber, int documentStart) {
			super(tokenText, tokenStart, lineNumber, documentStart);
		}

		@Override
		public Z getMatch() {
			return match;
		}

		@Override
		public void setMatch(Z match) {
			this.match = match;
		}
	}

	public static class StartPrologToken extends
			TagDeclarationToken<EndPrologToken> {
		public StartPrologToken(int tokenStart, int lineNumber,
				int documentStart) {
			super("<?", tokenStart, lineNumber, documentStart);
		}
	}

	public static class EndPrologToken extends
			TagDeclarationToken<StartPrologToken> {
		public EndPrologToken(int tokenStart, int lineNumber, int documentStart) {
			super("?>", tokenStart, lineNumber, documentStart);
		}
	}

	public static class CommentStartToken extends Token {
		public CommentStartToken(int tokenStart, int lineNumber,
				int documentStart) {
			super("<!--", tokenStart, lineNumber, documentStart);
		}
	}

	public static class StartCommentToken extends
			TagDeclarationToken<EndCommentToken> {
		public StartCommentToken(int tokenStart, int lineNumber,
				int documentStart) {
			super("<!--", tokenStart, lineNumber, documentStart);
		}
	}

	public static class EndCommentToken extends
			TagDeclarationToken<StartCommentToken> {
		public EndCommentToken(int tokenStart, int lineNumber, int documentStart) {
			super("-->", tokenStart, lineNumber, documentStart);
		}
	}

	public static class CommentToken extends Token {
		public CommentToken(String comment, int tokenStart, int lineNumber,
				int documentStart) {
			super(comment, tokenStart, lineNumber, documentStart);
		}
	}

	public static class EndTagToken extends TagDeclarationToken<StartTagToken> {
		boolean inline;

		public EndTagToken(String tokenText, int tokenStart, int lineNumber,
				int documentStart, boolean inline) {
			super(tokenText, tokenStart, lineNumber, documentStart);
			this.inline = inline;
		}

		public boolean isInline() {
			return inline;
		}
	}

	public static class WordToken extends Token {

		public WordToken(String tokenText, int tokenStart, int lineNumber,
				int documentStart) {
			super(tokenText, tokenStart, lineNumber, documentStart);
		}

	}

	public static class ContentToken extends Token {

		public ContentToken(String tokenText, int tokenStart, int lineNumber,
				int documentStart) {
			super(tokenText, tokenStart, lineNumber, documentStart);
		}

	}

	static class CacheLastTokenReceiver implements Receiver<Token> {
		Token lastToken;
		Receiver<Token> delegate;

		CacheLastTokenReceiver(Receiver<Token> delegate) {
			this.delegate = delegate;
		}

		public Token getLastToken() {
			return lastToken;
		}

		@Override
		public void add(Token... elements) {
			lastToken = elements[elements.length - 1];
			delegate.add(elements);
		}
	}

	private void parseTagAttributes(LookAheadReader l,
			CacheLastTokenReceiver receiver,
			boolean allowBracketsOrParentheses, String... closingTagTokens)
			throws IOException {
		TagDeclarationToken startingToken = (TagDeclarationToken) receiver
				.getLastToken();
		while (l.current() != CharacterIterator.DONE) {
			char ch = l.current();
			char next = l.peek(1);
			int start = (int) l.getPosition();

			if (ch == '[' && allowBracketsOrParentheses) {
				receiver.add(new SymbolCharToken(ch, start, 0, start));
				l.next();
				parseElements(l, receiver, "]");
			} else if (ch == '(' && allowBracketsOrParentheses) {
				receiver.add(new SymbolCharToken(ch, start, 0, start));
				l.next();
				parseElements(l, receiver, ")");
			} else if (ch == '?' && next == '>') {
				receiver.add(new EndPrologToken(start, 0, start));
				l.next(2);
			} else if (ch == '/' && next == '>') {
				receiver.add(new EndTagToken("/>", start, 0, start, true));
				l.next(2);
			} else if (ch == '>') {
				receiver.add(new EndTagToken(">", start, 0, start, true));
				l.next();
			} else if (ch == '"' || ch == '\'') {
				char quote = ch;
				StringBuilder encodedStr = new StringBuilder();
				StringBuilder decodedStr = new StringBuilder();
				encodedStr.append(ch);
				boolean closed = false;

				while (!closed && l.next()) {
					ch = l.current();
					encodedStr.append(ch);
					if (ch == quote) {
						closed = true;
						l.next();
					} else {
						decodedStr.append(ch);
					}
				}

				StringToken strToken = new StringToken(encodedStr.toString(),
						decodedStr.toString(), start, 0, start);
				if (!closed) {
					throw new ParserException(strToken,
							"This string was not closed.");
				}
				receiver.add(strToken);
			} else if (Character.isWhitespace(ch)) {
				StringBuilder sb = new StringBuilder();
				while (ch != CharacterIterator.DONE
						&& Character.isWhitespace(ch)) {
					sb.append(ch);
					l.next();
					ch = l.current();
				}
				receiver.add(new WhitespaceToken(sb.toString(), start, 0, start));
			} else if (ch == '=') {
				receiver.add(new AssignmentToken(ch, start, 0, start));
				l.next();
			} else if (ch == '%' || ch == '#' || ch == ';' || ch == ')'
					|| ch == ']') {
				receiver.add(new SymbolCharToken(ch, start, 0, start));
				l.next();
			} else if (Character.isLetter(ch)) {
				StringBuilder sb = new StringBuilder();
				while (ch != CharacterIterator.DONE
						&& (Character.isLetterOrDigit(ch) || ch == '-'
								|| ch == '_' || ch == ':' || ch == '.')) {
					sb.append(ch);
					l.next();
					ch = l.current();
				}
				receiver.add(new WordToken(sb.toString(), start, 0, start));
			} else {
				Token token = new Token(Character.toString(ch), start, 0, start);
				throw new ParserException(token, "Unsupported character \'"
						+ ch + "\'");
			}

			String lastTokenText = receiver.getLastToken().getText();
			for (String closingTag : closingTagTokens) {
				if (lastTokenText.equals(closingTag)) {
					TagDeclarationToken endToken = (TagDeclarationToken) receiver
							.getLastToken();
					endToken.setMatch(startingToken);
					startingToken.setMatch(endToken);
					return;
				}
			}
		}
	}

	@Override
	public void parse(InputStream in, Receiver<Token> receiver)
			throws IOException {
		Prolog prolog = new Prolog();
		in = Prolog.parseEncoding(in, prolog);
		String encoding = prolog.getAttribute("encoding");
		if (encoding == null) {
			encoding = "utf-8";
		}
		try (InputStreamReader reader = new InputStreamReader(in, encoding)) {
			parse(reader, receiver);
		}
	}

	public void parse(Reader reader, Receiver<Token> receiver)
			throws IOException {
		try (LookAheadReader l = new LookAheadReader(reader)) {
			parseElements(l, receiver);
		}
	}

	private void parseElements(LookAheadReader l, Receiver<Token> receiver,
			String... closingTagTokens) throws IOException {
		CacheLastTokenReceiver cltr = new CacheLastTokenReceiver(receiver);
		while (l.current() != CharacterIterator.DONE) {
			char ch = l.current();
			char next = l.peek(1);
			int start = (int) l.getPosition();
			if (ch == '<' && next == '!' && l.peek(2) == '-'
					&& l.peek(3) == '-') {
				StartCommentToken startComment = new StartCommentToken(start,
						0, start);
				cltr.add(startComment);
				l.next(4);
				start = (int) l.getPosition();
				ch = l.current();

				StringBuilder sb = new StringBuilder();
				while (ch != CharacterIterator.DONE
						&& !(ch == '-' && l.peek(1) == '-' && l.peek(2) == '>')) {
					sb.append(ch);
					l.next();
					ch = l.current();
				}
				cltr.add(new CommentToken(sb.toString(), start, 0, start));
				EndCommentToken endComment = new EndCommentToken(
						(int) l.getPosition(), 0, (int) l.getPosition());
				cltr.add(endComment);
				endComment.setMatch(startComment);
				startComment.setMatch(endComment);
				l.next(3);
			} else if (ch == '<' && next == '?') {
				cltr.add(new StartPrologToken(start, 0, start));
				l.next(2);
				parseTagAttributes(l, cltr, false, "?>");
			} else if (ch == '<' && next == '/') {
				StartTagToken startTag = new StartTagToken(start, 0, start,
						true);
				cltr.add(startTag);
				l.next(2);
				parseTagAttributes(l, cltr, false, ">");
			} else if (ch == '<' && next == '!') {
				cltr.add(new StartDTDTagToken(start, 0, start));
				l.next(2);
				parseTagAttributes(l, cltr, true, ">", "/>");
			} else if (ch == '<') {
				cltr.add(new StartTagToken(start, 0, start, false));
				l.next();
				parseTagAttributes(l, cltr, false, ">", "/>");
			} else if (ch == ']'
					&& Arrays.asList(closingTagTokens).contains("]")) {
				cltr.add(new SymbolCharToken(ch, start, 0, start));
				l.next();
			} else {
				StringBuilder sb = new StringBuilder();
				while (ch != CharacterIterator.DONE && ch != '<') {
					sb.append(ch);
					l.next();
					ch = l.current();
				}

				int leadingWhitespace = getLeadingWhitespace(sb);
				if (leadingWhitespace == sb.length()) {
					cltr.add(new WhitespaceToken(sb.toString(), start, 0, start));
				} else {
					int trailingWhitespace = getTrailingWhitespace(sb);
					String leadingWhitespaceStr = sb.substring(0,
							leadingWhitespace);
					if (leadingWhitespace > 0) {
						cltr.add(new WhitespaceToken(leadingWhitespaceStr,
								start, 0, start));
					}
					cltr.add(new ContentToken(sb.substring(leadingWhitespace,
							sb.length() - trailingWhitespace), start
							+ leadingWhitespace, 0, start + leadingWhitespace));
					if (trailingWhitespace > 0) {
						String trailingWhitespaceStr = sb.substring(sb.length()
								- trailingWhitespace, sb.length());
						cltr.add(new WhitespaceToken(trailingWhitespaceStr,
								start + sb.length() - trailingWhitespace, 0,
								start + sb.length() - trailingWhitespace));
					}
				}
			}

			String lastTokenText = cltr.getLastToken().getText();
			for (String closingTag : closingTagTokens) {
				if (lastTokenText.equals(closingTag)) {
					return;
				}
			}
		}
	}

	private int getTrailingWhitespace(StringBuilder sb) {
		int run = 0;
		for (int a = sb.length() - 1; a >= 0; a--) {
			if (Character.isWhitespace(sb.charAt(a))) {
				run++;
			} else {
				break;
			}
		}
		return run;
	}

	private int getLeadingWhitespace(StringBuilder sb) {
		int run = 0;
		for (int a = 0; a < sb.length(); a++) {
			if (Character.isWhitespace(sb.charAt(a))) {
				run++;
			} else {
				break;
			}
		}
		return run;
	}

	private boolean isWhitespace(String str) {
		for (int a = 0; a < str.length(); a++) {
			if (!Character.isWhitespace(str.charAt(a))) {
				return false;
			}
		}
		return str.length() > 0;
	}

}