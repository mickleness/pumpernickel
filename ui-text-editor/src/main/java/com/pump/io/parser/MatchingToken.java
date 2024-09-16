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

public interface MatchingToken<T extends Token> {

	/**
	 * Return the opposite matching token for this token. For example, if there
	 * are two tokens "(" and ")" that begin and end an expression: each token
	 * should point to the other as its match.
	 * 
	 * @return the opposite matching token for this token.
	 */
	T getMatch();

	/**
	 * Assign the opposite match of this MatchingToken.
	 * 
	 * @param oppositeMatch
	 *            the opposite match of this MatchingToken.
	 */
	void setMatch(T oppositeMatch);
}