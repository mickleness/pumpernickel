package com.pump.io.parser;

public interface MatchingToken<T extends Token> {

	/**
	 * Return the opposite matching token for this token. For example, if there
	 * are two tokens "(" and ")" that begin and end an expression: each token
	 * should point to the other as its match.
	 * 
	 * @return the opposite matching token for this token.
	 */
	public T getMatch();

	/**
	 * Assign the opposite match of this MatchingToken.
	 * 
	 * @param oppositeMatch
	 *            the opposite match of this MatchingToken.
	 */
	public void setMatch(T oppositeMatch);
}
