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
package com.pump.desktop.error;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.LinkedList;
import java.util.List;

public class ErrorManager {
	
	private static ErrorManager GLOBAL;
	
	/**
	 * 
	 * @param applicationName the name of the application. This may be shown in feedback to the user.
	 * @throws SecurityException if you don't have permission to call Thread.setDefaultUncaughtExceptionHandler()
	 */
	public static synchronized void initialize(String applicationName)  throws SecurityException  {
		if(GLOBAL!=null)
			throw new RuntimeException("ErrorManager already initialized using "+GLOBAL.applicationName);
		GLOBAL = new ErrorManager(applicationName);
	}
	
	public static synchronized ErrorManager get() {
		return GLOBAL;
	}
	
	List<ThrowableHandler> handlers = new LinkedList<>();
	String applicationName;
	ErrorDialogThrowableHandler defaultHandler = new ErrorDialogThrowableHandler();
	
	ErrorManager(String applicationName) throws SecurityException {
		if(applicationName==null)
			throw new NullPointerException();
		
		this.applicationName = applicationName;
		handlers.add(defaultHandler);
		
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
				GLOBAL.process(e);
			}
		});
	}
	
	public void addHandler(ThrowableHandler handler) {
		synchronized(handlers) {
			handlers.add(0, handler);
		}
	}
	
	public ThrowableHandler process(Throwable t) {
		return process(new ThrowableDescriptor(t));
	}

	private int recursionCtr = 0;
	public ThrowableHandler process(ThrowableDescriptor td) {
		recursionCtr++;
		try {
			synchronized(handlers) {
				for(ThrowableHandler handler : handlers) {
					try {
						if(handler.processThrowable(td))
							return handler;
					} catch(Exception e) {
						if(recursionCtr==1)
							e.printStackTrace();
					}
				}
			}
		} finally {
			recursionCtr--;
		}
		return null;
	}

	public static ErrorDialogThrowableHandler getDefaultErrorHandler()
	{
		return GLOBAL.defaultHandler;
	}

	/** Print an exception to the console without invoking an error dialog.
	 * 
	 * @param e the exception to print to the console.
	 */
	public static void println(Throwable e)
	{
		System.err.println(getStackTrace(e));
	}

	public static String getStackTrace(Throwable throwable) {
		try(StringWriter sWriter = new StringWriter()) {
			try(PrintWriter pWriter = new PrintWriter(sWriter)) {
				throwable.printStackTrace(pWriter);
			}
			sWriter.close();
			return sWriter.toString();
		} catch(IOException e2) {
			//this should never happen for a StringWriter
			System.err.println("ErrorManager: An error occurred printing a "+throwable.getClass().getCanonicalName());
			return "error";
		}
	}
}