package com.pump.data.operator;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
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

public class OperatorParser {

	// TODO: add support for IN and LIKE operators

	/**
	 * This consolidates several consecutive SymbolCharToken into one word.
	 * <p>
	 * For example: we don't want to see separate tokesn for "<=" or "!=" or
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
					op = And.create(op, op2);
				} else if (tokens[i].getText().equals("||")) {
					tokensPtr.incrementAndGet();
					Operator op2 = readOneToken(tokens, tokensPtr);
					op = Or.create(op, op2);
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
			return Not.create(readOneToken(tokens, tokensPtr));
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

			if (w.getText().equals("true")) {
				return Operator.TRUE;
			} else if (w.getText().equals("false")) {
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

			Comparable value = null;
			if (t3 instanceof NumberToken) {
				NumberToken nt = (NumberToken) t3;
				value = (Comparable) nt.getNumber();
			} else if (t3 instanceof StringToken) {
				StringToken st = (StringToken) t3;
				value = st.getDecodedString();
			} else if (t3 instanceof CharToken) {
				CharToken ct = (CharToken) t3;
				value = ct.getDecodedChar();
			} else if (t3.getText().equals("true")) {
				value = Boolean.TRUE;
			} else if (t3.getText().equals("false")) {
				value = Boolean.FALSE;
			}

			if (value != null) {
				String s = t2.getText();
				tokensPtr.addAndGet(2);
				if (s.equals("==")) {
					return new EqualTo(w.getText(), value);
				} else if (s.equals("!=")) {
					return Not.create(new EqualTo(w.getText(), value));
				} else if (s.equals(">=")) {
					return Or.create(new GreaterThan(w.getText(),
							(Comparable) value),
							new EqualTo(w.getText(), value));
				} else if (s.equals("<=")) {
					return Or.create(new LesserThan(w.getText(),
							(Comparable) value),
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
		}

		return null;
	}

	private synchronized JavaParser getJavaParser() {
		if (javaParser == null) {
			javaParser = new JavaParser();
		}
		return javaParser;
	}

}
