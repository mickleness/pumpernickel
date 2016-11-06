/*

 * @(#)ConsoleLogger.java
 *
 * $Date: 2015-06-07 14:43:57 -0400 (Sun, 07 Jun 2015) $
 *
 * Copyright (c) 2011 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Jeremy Wood. For details see accompanying license terms.
 * 
 * This software is probably, but not necessarily, discussed here:
 * https://javagraphics.java.net/
 * 
 * That site should also contain the most recent official version
 * of this software.  (See the SVN repository for more details.)
 */
package com.pump.io;

import java.io.File;
import java.io.PrintStream;

import com.pump.util.ThreadedSemaphore;

/** This logs the System.err and System.out streams
 * to a text file.
 *
 * @see com.bric.swing.BasicConsole
 */
public class ConsoleLogger extends FileLogger {
	boolean logOut = true;
	boolean logErr = true;
	int OUT = 1;
	int ERR = 2;
	
	/** Create a ConsoleLogger that writes to a file with the given name.
	 * 
	 * @param logName the name of the file to write.
	 */
	public ConsoleLogger(String logName) {
		super(logName);
		System.setOut(new FilterStream(System.out, OUT));
		System.setErr(new FilterStream(System.err, ERR));
	}
	
	/** Create a ConsoleLogger that writes to a given file.
	 * 
	 * @param file the file to write to.
	 */
	public ConsoleLogger(File file) {
		super(file);
		System.setOut(new FilterStream(System.out, OUT));
		System.setErr(new FilterStream(System.err, ERR));
	}


	class FilterStream extends PrintStream {
		int type;
		ThreadedSemaphore semaphore = new ThreadedSemaphore(1);

		public FilterStream(PrintStream other, int type) {
			super(other);
			this.type = type;
		}

		private boolean isActive() {
			return (type == OUT && logOut) || (type == ERR && logErr);
		}
		
		@Override
		public void print(boolean b) {
			boolean acquire = semaphore.tryAcquire();
			try {
				super.print(b);
				if(acquire && isActive()) {
					ConsoleLogger.this.print(new Boolean(b));
				}
			} finally {
				if(acquire)
					semaphore.release();
			}
		}

		@Override
		public void print(char c) {
			boolean acquire = semaphore.tryAcquire();
			try {
				super.print(c);
				if(acquire && isActive()) {
					ConsoleLogger.this.print(Character.toString(c));
				}
			} finally {
				if(acquire)
					semaphore.release();
			}
		}

		@Override
		public void print(char[] s) {
			boolean acquire = semaphore.tryAcquire();
			try {
				super.print(s);
				if(acquire && isActive()) {
					ConsoleLogger.this.print(new String(s));
				}
			} finally {
				if(acquire)
					semaphore.release();
			}
		}

		@Override
		public void print(double d) {
			boolean acquire = semaphore.tryAcquire();
			try {
				super.print(d);
				if(acquire && isActive()) {
					ConsoleLogger.this.print(Double.toString(d));
				}
			} finally {
				if(acquire)
					semaphore.release();
			}
		}

		@Override
		public void print(float f2) {
			boolean acquire = semaphore.tryAcquire();
			try {
				super.print(f2);
				if(acquire && isActive()) {
					ConsoleLogger.this.print(Float.toString(f2));
				}
			} finally {
				if(acquire)
					semaphore.release();
			}
		}

		@Override
		public void print(int i) {
			boolean acquire = semaphore.tryAcquire();
			try {
				super.print(i);
				if(acquire && isActive()) {
					ConsoleLogger.this.print(Integer.toString(i));
				}
			} finally {
				if(acquire)
					semaphore.release();
			}
		}

		@Override
		public void print(long l) {
			boolean acquire = semaphore.tryAcquire();
			try {
				super.print(l);
				if(acquire && isActive()) {
					ConsoleLogger.this.print(Long.toString(l));
				}
			} finally {
				if(acquire)
					semaphore.release();
			}
		}

		@Override
		public void println() {
			boolean acquire = semaphore.tryAcquire();
			try {
				super.println();
				if(acquire && isActive()) {
					ConsoleLogger.this.println("");
				}
			} finally {
				if(acquire)
					semaphore.release();
			}
		}

		@Override
		public void println(boolean x) {
			boolean acquire = semaphore.tryAcquire();
			try {
				super.println(x);
				if(acquire && isActive()) {
					ConsoleLogger.this.println(new Boolean(x));
				}
			} finally {
				if(acquire)
					semaphore.release();
			}
		}

		@Override
		public void println(char x) {
			boolean acquire = semaphore.tryAcquire();
			try {
				super.println(x);
				if(acquire && isActive()) {
					ConsoleLogger.this.println(Character.toString(x));
				}
			} finally {
				if(acquire)
					semaphore.release();
			}
		}

		@Override
		public void println(char[] x) {
			boolean acquire = semaphore.tryAcquire();
			try {
				super.println(x);
				if(acquire && isActive()) {
					ConsoleLogger.this.println(new String(x));
				}
			} finally {
				if(acquire)
					semaphore.release();
			}
		}

		@Override
		public void println(double x) {
			boolean acquire = semaphore.tryAcquire();
			try {
				super.println(x);
				if(acquire && isActive()) {
					ConsoleLogger.this.println(Double.toString(x));
				}
			} finally {
				if(acquire)
					semaphore.release();
			}
		}

		@Override
		public void println(float x) {
			boolean acquire = semaphore.tryAcquire();
			try {
				super.println(x);
				if(acquire && isActive()) {
					ConsoleLogger.this.println(Float.toString(x));
				}
			} finally {
				if(acquire)
					semaphore.release();
			}
		}

		@Override
		public void println(int x) {
			boolean acquire = semaphore.tryAcquire();
			try {
				super.println(x);
				if(acquire && isActive()) {
					ConsoleLogger.this.println(Integer.toString(x));
				}
			} finally {
				if(acquire)
					semaphore.release();
			}
		}

		@Override
		public void println(long x) {
			boolean acquire = semaphore.tryAcquire();
			try {
				super.println(x);
				if(acquire && isActive()) {
					ConsoleLogger.this.println(Long.toString(x));
				}
			} finally {
				if(acquire)
					semaphore.release();
			}
		}

		@Override
		public void print(Object x) {
			boolean acquire = semaphore.tryAcquire();
			try {
				super.print(x);
				if(acquire && isActive()) {
					ConsoleLogger.this.print(x);
				}
			} finally {
				if(acquire)
					semaphore.release();
			}
		}

		@Override
		public void print(String x) {
			boolean acquire = semaphore.tryAcquire();
			try {
				super.print(x);
				if(acquire && isActive()) {
					ConsoleLogger.this.print(x);
				}
			} finally {
				if(acquire)
					semaphore.release();
			}
		}

		@Override
		public void println(Object x) {
			boolean acquire = semaphore.tryAcquire();
			try {
				super.println(x);
				if(acquire && isActive()) {
					ConsoleLogger.this.println(x);
				}
			} finally {
				if(acquire)
					semaphore.release();
			}
		}

		@Override
		public void println(String x) {
			boolean acquire = semaphore.tryAcquire();
			try {
				super.println(x);
				if(acquire && isActive()) {
					ConsoleLogger.this.println(x);
				}
			} finally {
				if(acquire)
					semaphore.release();
			}
		}
	}

	public void setLoggingSystemOut(boolean b) {
		logOut = b;
	}

	public void setLoggingSystemErr(boolean b) {
		logErr = b;
	}
}
