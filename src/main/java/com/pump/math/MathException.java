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
package com.pump.math;

/**
 * A collection of exceptions related to math.
 * 
 */
public class MathException extends Exception {
	private static final long serialVersionUID = 1L;

	/**
	 * An exception used when an operand or calculation involves a negative
	 * number and it shouldn't.
	 */
	public static class NegativeException extends MathException {
		private static final long serialVersionUID = 1L;

		protected NegativeException() {
			super();
		}

		protected NegativeException(String message, Throwable cause,
				boolean enableSuppression, boolean writableStackTrace) {
			super(message, cause, enableSuppression, writableStackTrace);
		}

		protected NegativeException(String message, Throwable cause) {
			super(message, cause);
		}

		protected NegativeException(String message) {
			super(message);
		}

		protected NegativeException(Throwable cause) {
			super(cause);
		}
	}

	/**
	 * An exception used when an operation results in overflow.
	 */
	public static class OverflowException extends MathException {
		private static final long serialVersionUID = 1L;

		public OverflowException() {
			super();
		}

		public OverflowException(String message, Throwable cause,
				boolean enableSuppression, boolean writableStackTrace) {
			super(message, cause, enableSuppression, writableStackTrace);
		}

		public OverflowException(String message, Throwable cause) {
			super(message, cause);
		}

		public OverflowException(String message) {
			super(message);
		}

		public OverflowException(Throwable cause) {
			super(cause);
		}
	}

	public static class NonIntegerException extends MathException {
		private static final long serialVersionUID = 1L;

		public NonIntegerException() {
			super();
		}

		public NonIntegerException(String message, Throwable cause,
				boolean enableSuppression, boolean writableStackTrace) {
			super(message, cause, enableSuppression, writableStackTrace);
		}

		public NonIntegerException(String message, Throwable cause) {
			super(message, cause);
		}

		public NonIntegerException(String message) {
			super(message);
		}

		public NonIntegerException(Throwable cause) {
			super(cause);
		}
	}

	public static class DivideByZeroException extends MathException {
		private static final long serialVersionUID = 1L;

		public DivideByZeroException() {
			super();
		}

		public DivideByZeroException(String message, Throwable cause,
				boolean enableSuppression, boolean writableStackTrace) {
			super(message, cause, enableSuppression, writableStackTrace);
		}

		public DivideByZeroException(String message, Throwable cause) {
			super(message, cause);
		}

		public DivideByZeroException(String message) {
			super(message);
		}

		public DivideByZeroException(Throwable cause) {
			super(cause);
		}
	}

	public MathException() {
		super();
	}

	public MathException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public MathException(String message, Throwable cause) {
		super(message, cause);
	}

	public MathException(String message) {
		super(message);
	}

	public MathException(Throwable cause) {
		super(cause);
	}
}