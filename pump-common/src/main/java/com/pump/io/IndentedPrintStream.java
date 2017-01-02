package com.pump.io;

import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

/**
 * This PrintStream helps manage indentation when you invoke any <code>print(x)</code> or
 * <code>println(x)</code> method.
 * <p>
 * In addition to use {@link #indent()} and {@link #unindent()}, you can use the try-with-resources syntax
 * so your code would resemble:
 * <p>
 * <pre> try(IndentedPrintStream ips = new IndentedPrintStream(out, true, "UTF-8")) {
 * 	ips.println("Outer Text");
 * 	try(AutoCloseable c = ips.indent()) {
 * 		ips.println("Middle Text");
 * 		try(AutoCloseable c2 = ips.indent()) {
 * 			ips.println("Inner Text");
 * 		}
 * 		ips.println("More Middle Text");
 * 	}
 * 	ips.println("More Outer Text");
}</pre>
 */
public class IndentedPrintStream extends PrintStream {
	
	private class CloseIndentation implements AutoCloseable {

		int amount;
		
		CloseIndentation(int amount) {
			this.amount = amount;
		}
		
		@Override
		public synchronized void close() throws Exception {
			indent(amount);
			
			//make sure subsequent attempts to close do nothing:
			amount = 0;
		}
		
	}
	
	protected StringBuilder indentation = new StringBuilder();
	protected boolean emptyNewLine = true;
	protected final String indentationString;

	/**
	 * Create a new IndentedPrintStream that uses "\t" for each indentation.
	 * 
	 * @param out
	 * @param autoFlush
	 * @param encoding
	 * @throws UnsupportedEncodingException
	 */
	public IndentedPrintStream(OutputStream out, boolean autoFlush, String encoding)
			throws UnsupportedEncodingException {
		this(out, autoFlush, encoding, "\t");
	}

	/**
	 * Create a new IndentedPrintStream.
	 * 
	 * @param out
	 * @param autoFlush
	 * @param encoding
	 * @param indentationString the String used to indent each section of code. "\t" is the recommended default.
	 * @throws UnsupportedEncodingException
	 */
	public IndentedPrintStream(OutputStream out, boolean autoFlush, String encoding, String indentationString)
			throws UnsupportedEncodingException {
		super(out, autoFlush, encoding);
		
		if(indentationString==null)
			throw new NullPointerException();
		
		this.indentationString = indentationString;
	}
	
	
	/**
	 * Indent this stream one unit.
	 * 
	 * @return an optional AutoCloseable that will unindent this stream one unit when closed.
	 */
	public AutoCloseable indent() {
		return indent(1);
	}
	
	/**
	 * Indent this stream n-many units.
	 * @param n the number of units to indent. This may be negative to unindent. (If this is zero:
	 * this method does nothing.)
	 * @return an optional AutoCloseable that will unindent this stream one unit when closed.
	 */
	public AutoCloseable indent(int n) {
		CloseIndentation returnValue = new CloseIndentation(-n);
		if(n>0) {
			while(n>0) {
				indentation.append(indentationString);
				n--;
			}
		} else {
			while(n<0) {
				if(indentation.length()==0)
					throw new RuntimeException("Illegal attempt to unindent past zero.");
				
				indentation.delete(indentation.length() - indentationString.length(), indentation.length());
				n++;
			}
		}
		return returnValue;
	}
	
	/**
	 * This reverses the effects of {@link #indent()}.
	 */
	public void unindent() {
		indent(-1);
	}
	
	/**
	 * If we're on an empty line, this first prints the appropriate indentation (if any).
	 */
	protected void prePrint() {
		if(emptyNewLine) {
			emptyNewLine = false;
			super.print(indentation);
		}
	}

	@Override
	public synchronized void print(boolean b) {
		prePrint();
		super.print(b);
	}

	@Override
	public synchronized void print(char c) {
		prePrint();
		super.print(c);
	}

	@Override
	public synchronized void print(int i) {
		prePrint();
		super.print(i);
	}

	@Override
	public synchronized void print(long l) {
		prePrint();
		super.print(l);
	}

	@Override
	public synchronized void print(float f) {
		prePrint();
		super.print(f);
	}

	@Override
	public synchronized void print(double d) {
		prePrint();
		super.print(d);
	}

	@Override
	public synchronized void print(char[] s) {
		prePrint();
		super.print(s);
	}

	@Override
	public synchronized void print(String s) {
		prePrint();
		super.print(s);
	}

	@Override
	public synchronized void print(Object obj) {
		prePrint();
		super.print(obj);
	}

	@Override
	public synchronized void println() {
		prePrint();
		super.println();
		emptyNewLine = true;
	}

	@Override
	public synchronized void println(boolean x) {
		prePrint();
		super.println(x);
		emptyNewLine = true;
	}

	@Override
	public synchronized void println(char x) {
		prePrint();
		super.println(x);
		emptyNewLine = true;
	}

	@Override
	public synchronized void println(int x) {
		prePrint();
		super.println(x);
		emptyNewLine = true;
	}

	@Override
	public synchronized void println(long x) {
		prePrint();
		super.println(x);
		emptyNewLine = true;
	}

	@Override
	public synchronized void println(float x) {
		prePrint();
		super.println(x);
		emptyNewLine = true;
	}

	@Override
	public synchronized void println(double x) {
		prePrint();
		super.println(x);
		emptyNewLine = true;
	}

	@Override
	public synchronized void println(char[] x) {
		prePrint();
		super.println(x);
		emptyNewLine = true;
	}

	@Override
	public synchronized void println(String x) {
		prePrint();
		super.println(x);
		emptyNewLine = true;
	}

	@Override
	public synchronized void println(Object x) {
		prePrint();
		super.println(x);
		emptyNewLine = true;
	}
}
