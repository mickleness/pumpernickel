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
package com.pump.data.operator;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.pump.io.parser.Parser.BracketCharToken;
import com.pump.io.parser.Parser.StringToken;
import com.pump.io.parser.Parser.SymbolCharToken;
import com.pump.io.parser.ParserException;
import com.pump.io.parser.Token;
import com.pump.io.parser.java.JavaParser;
import com.pump.io.parser.java.JavaParser.CharToken;
import com.pump.io.parser.java.JavaParser.NumberToken;
import com.pump.io.parser.java.JavaParser.WordToken;
import com.pump.text.WildcardPattern;

/**
 * This parses a Java-like string into an Operator, if possible. For example
 * this can parse "a || b" or "x == 3" or "(y >= 2 && z) || (x<0)".
 */
public class OperatorParser {

	/**
	 * This consolidates several consecutive SymbolCharToken into one word.
	 * <p>
	 * For example: we don't want to see separate tokens for "<=" or "!=" or
	 * "&&".
	 */
	private static class SymbolWordToken extends Token {

		public SymbolWordToken(List<SymbolCharToken> tokens) {
			super(createText(tokens), tokens.get(0).getStartIndex(), tokens
					.get(0).getLineNumber(), tokens.get(0)
					.getDocumentStartIndex());
		}

		private static String createText(List<SymbolCharToken> tokens) {
			StringBuilder sb = new StringBuilder();
			for (SymbolCharToken t : tokens) {
				sb.append(t.getText());
			}
			return sb.toString();
		}
	}

	JavaParser javaParser;
	WildcardPattern.Format wildcardFormat;

	public OperatorParser() {
		this(null);
	}

	public OperatorParser(WildcardPattern.Format format) {
		this.wildcardFormat = format == null ? new WildcardPattern.Format()
				: format;
	}

	public Operator parse(String str) throws Exception {
		JavaParser p = getJavaParser();
		Token[] tokens = p.parse(str, false);
		return readAllTokens(tokens);
	}

	public Operator parse(InputStream in) throws Exception {
		JavaParser p = getJavaParser();
		Token[] tokens = p.parse(in, false);
		return readAllTokens(tokens);
	}

	protected Operator readAllTokens(Token[] tokens) {
		tokens = collapseSymbols(tokens);

		AtomicInteger tokensPtr = new AtomicInteger(0);

		Operator op = readOneToken(tokens, tokensPtr);
		do {
			int i = tokensPtr.incrementAndGet();
			if (i < tokens.length) {
				if (tokens[i].getText().equals("&&")) {
					tokensPtr.incrementAndGet();
					Operator op2 = readOneToken(tokens, tokensPtr);
					op = new And(op, op2);
				} else if (tokens[i].getText().equals("||")) {
					tokensPtr.incrementAndGet();
					Operator op2 = readOneToken(tokens, tokensPtr);
					op = new Or(op, op2);
				} else {
					throw new ParserException(tokens[i], new Exception(
							"Unsupported operator \"" + tokens[i].getText()
									+ "\""));
				}
			} else {
				return op;
			}
		} while (true);
	}

	private Token[] collapseSymbols(Token[] tokens) {
		List<Token> returnValue = new ArrayList<>();
		List<SymbolCharToken> uncommittedSymbols = new ArrayList<>();
		for (int a = 0; a < tokens.length; a++) {
			boolean addToUncommitted = false;
			if (tokens[a] instanceof SymbolCharToken
					&& !(tokens[a] instanceof BracketCharToken)) {
				// consolidate "!=", but not "&&!" or "||!"
				if (tokens[a].getText().equals("!")) {
					addToUncommitted = uncommittedSymbols.isEmpty();
				} else {
					addToUncommitted = true;
				}
			}

			if (addToUncommitted) {
				SymbolCharToken s = (SymbolCharToken) tokens[a];
				uncommittedSymbols.add(s);
			} else {
				if (uncommittedSymbols.size() > 0) {
					returnValue.add(new SymbolWordToken(uncommittedSymbols));
					uncommittedSymbols.clear();
				}
				returnValue.add(tokens[a]);
			}
		}
		if (uncommittedSymbols.size() > 0) {
			returnValue.add(new SymbolWordToken(uncommittedSymbols));
			uncommittedSymbols.clear();
		}
		return returnValue.toArray(new Token[returnValue.size()]);
	}

	@SuppressWarnings("rawtypes")
	private Operator readOneToken(Token[] tokens, AtomicInteger tokensPtr) {
		Token t = tokens[tokensPtr.intValue()];
		if (t.getText().equals("!")) {
			tokensPtr.incrementAndGet();
			return new Not(readOneToken(tokens, tokensPtr));
		}

		if (t instanceof BracketCharToken) {
			BracketCharToken b = (BracketCharToken) t;
			BracketCharToken opp = b.getMatch();
			int i = Arrays.asList(tokens).indexOf(opp);
			Token[] copy = new Token[i - tokensPtr.intValue() - 1];
			System.arraycopy(tokens, tokensPtr.intValue() + 1, copy, 0,
					copy.length);
			tokensPtr.addAndGet(copy.length + 1);
			return readAllTokens(copy);
		}

		if (t instanceof WordToken) {
			WordToken w = (WordToken) t;

			if (w.getText().equalsIgnoreCase("true")) {
				return Operator.TRUE;
			} else if (w.getText().equalsIgnoreCase("false")) {
				return Operator.FALSE;
			}

			Token t2 = tokensPtr.intValue() + 1 < tokens.length ? tokens[tokensPtr
					.intValue() + 1] : null;
			Token t3 = tokensPtr.intValue() + 2 < tokens.length ? tokens[tokensPtr
					.intValue() + 2] : null;

			if (t2 == null
					|| (t2.getText().equals("&&") || t2.getText().equals("||"))) {
				return new EqualTo(w.getText(), Boolean.TRUE);
			}

			if (w.getText().equals(Like.FUNCTION_NAME)
					&& t2.getText().equals("(")) {
				WordToken w2 = (WordToken) t3;

				Token t5 = tokensPtr.intValue() + 4 < tokens.length ? tokens[tokensPtr
						.intValue() + 4] : null;
				String value = (String) getValue(t5);

				tokensPtr.addAndGet(5);

				return new Like(w2.getText(), new WildcardPattern(value,
						wildcardFormat));
			} else if (w.getText().equals(In.FUNCTION_NAME)
					&& t2.getText().equals("(")) {
				WordToken w2 = (WordToken) t3;

				tokensPtr.addAndGet(4);

				Collection<Object> values = readList(tokens, tokensPtr);

				tokensPtr.incrementAndGet();
				return In.create(w2.getText(), values);
			}

			Comparable value = getValue(t3);

			String s = t2.getText();
			tokensPtr.addAndGet(2);
			if (s.equals("==")) {
				return new EqualTo(w.getText(), value);
			} else if (s.equals("!=")) {
				return new Not(new EqualTo(w.getText(), value));
			} else if (s.equals(">=")) {
				return new Or(new GreaterThan(w.getText(), (Comparable) value),
						new EqualTo(w.getText(), value));
			} else if (s.equals("<=")) {
				return new Or(new LesserThan(w.getText(), (Comparable) value),
						new EqualTo(w.getText(), value));
			} else if (s.equals(">")) {
				return new GreaterThan(w.getText(), (Comparable) value);
			} else if (s.equals("<")) {
				return new LesserThan(w.getText(), (Comparable) value);
			} else {
				throw new ParserException(t2, new Exception(
						"Unsupported operator \"" + s + "\""));
			}
		}

		return null;
	}

	/**
	 * Initially tokensPtr should point to a "{" char, and on exit it should
	 * point to a "}" char.
	 * 
	 * @param tokens
	 * @param tokensPtr
	 * @return
	 */
	private Collection<Object> readList(Token[] tokens, AtomicInteger tokensPtr) {
		List<Object> returnValue = new ArrayList<>();
		BracketCharToken b = (BracketCharToken) tokens[tokensPtr.get()];
		BracketCharToken opp = b.getMatch();
		int oppIndex = Arrays.asList(tokens).indexOf(opp);
		for (int a = tokensPtr.get() + 1; a < oppIndex; a++) {
			if (tokens[a].getText().equals(","))
				continue;
			returnValue.add(getValue(tokens[a]));
		}
		tokensPtr.set(oppIndex);
		return returnValue;
	}

	private Comparable getValue(Token t) {
		if (t instanceof NumberToken) {
			NumberToken nt = (NumberToken) t;
			return (Comparable) nt.getNumber();
		} else if (t instanceof StringToken) {
			StringToken st = (StringToken) t;
			return st.getDecodedString();
		} else if (t instanceof CharToken) {
			CharToken ct = (CharToken) t;
			return ct.getDecodedChar();
		} else if (t.getText().equals("true")) {
			return Boolean.TRUE;
		} else if (t.getText().equals("false")) {
			return Boolean.FALSE;
		} else if (t.getText().equals("null")) {
			return null;
		}
		throw new IllegalArgumentException("Unsupported token: " + t);
	}

	private synchronized JavaParser getJavaParser() {
		if (javaParser == null) {
			javaParser = new JavaParser();
		}
		return javaParser;
	}

}