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
package com.pump.geom;

import java.awt.geom.PathIterator;
import java.io.Serial;

/**
 * A PathIterator that parses serialized shape info.
 */
class SerializedPathIterator implements PathIterator {
	char[] c;
	int ctr = 0;
	double[] data = new double[6];
	int currentSegment = -1;
	int windingRule;

	public SerializedPathIterator(String s, int windingRule) {
		if (!(windingRule == PathIterator.WIND_EVEN_ODD || windingRule == PathIterator.WIND_NON_ZERO))
			throw new IllegalArgumentException(
					"The winding rule must be PathIterator.WIND_NON_ZERO or PathIterator.WIND_EVEN_ODD");

		c = s.toCharArray();
		this.windingRule = windingRule;
		next();
	}

	public int getWindingRule() {
		return windingRule;
	}

	protected void consumeWhiteSpace(boolean expectingWhiteSpace) {
		if (ctr >= c.length) {
			ctr = c.length + 2;
			return;
		}

		char ch = c[ctr];
		if (!Character.isWhitespace(ch)) {
			if (!expectingWhiteSpace)
				return;
			throw new ParserException("expected whitespace", ctr, 1);
		}
		while (true) {
			ctr++;
			if (ctr >= c.length) {
				ctr = c.length + 2;
				return;
			}

			ch = c[ctr];
			if (!Character.isWhitespace(ch)) {
				return;
			}
		}
	}

	public void next() {
		consumeWhiteSpace(false);

		if (ctr >= c.length) {
			ctr = c.length + 2;
			return;
		}
		int terms;
		char k = c[ctr];

		switch (k) {
			case 'm', 'M' -> {
				currentSegment = PathIterator.SEG_MOVETO;
				terms = 2;
			}
			case 'l', 'L' -> {
				currentSegment = PathIterator.SEG_LINETO;
				terms = 2;
			}
			case 'q', 'Q' -> {
				currentSegment = PathIterator.SEG_QUADTO;
				terms = 4;
			}
			case 'c', 'C' -> {
				currentSegment = PathIterator.SEG_CUBICTO;
				terms = 6;
			}
			case 'z', 'Z' -> {
				currentSegment = PathIterator.SEG_CLOSE;
				terms = 0;
			}
			default -> throw new ParserException(
					"Unrecognized character in shape data: '" + c[ctr] + "'",
					ctr, 1);
		}
		ctr++;
		if (terms > 0) {
			parseTerms(terms);
		} else {
			if (ctr < c.length) {
				if (!Character.isWhitespace(c[ctr]))
					throw new ParserException("expected whitespace after z",
							ctr, 1);
			}
		}
	}

	class ParserException extends RuntimeException {
		@Serial
		private static final long serialVersionUID = 1L;

		ParserException(String msg, int ptr, int length) {
			super(msg);
			System.err.println("\"" + (new String(c)) + "\"");
			String sb = " ".repeat(Math.max(0, ptr + 1)) +
					"^".repeat(Math.max(0, length));
			System.err.println(sb);
		}
	}

	protected void parseTerms(int terms) {
		for (int a = 0; a < terms; a++) {
			data[a] = parseTerm();
		}
	}

	protected double parseTerm() {
		consumeWhiteSpace(true);
		int i = ctr;
		while (i < c.length && (!Character.isWhitespace(c[i]))) {
			i++;
		}
		String string = new String(c, ctr, i - ctr);
		try {
			return Double.parseDouble(string);
		} catch (RuntimeException e) {
			// just constructing this prints data to System.err:
			throw new ParserException(e.getMessage(), ctr, i
					- ctr);
		} finally {
			ctr = i;
		}
	}

	public int currentSegment(double[] d) {
		d[0] = data[0];
		d[1] = data[1];
		d[2] = data[2];
		d[3] = data[3];
		d[4] = data[4];
		d[5] = data[5];
		return currentSegment;
	}

	public int currentSegment(float[] f) {
		f[0] = (float) data[0];
		f[1] = (float) data[1];
		f[2] = (float) data[2];
		f[3] = (float) data[3];
		f[4] = (float) data[4];
		f[5] = (float) data[5];
		return currentSegment;
	}

	public boolean isDone() {
		return ctr > c.length + 1;
	}
}