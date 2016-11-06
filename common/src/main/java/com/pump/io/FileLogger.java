/*
 * @(#)FileLogger.java
 *
 * $Date: 2015-06-07 13:23:26 -0400 (Sun, 07 Jun 2015) $
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
import java.io.FileOutputStream;
import java.io.IOException;

import com.pump.util.JVM;

/** This logs text to a file.
 */
public class FileLogger {

	File f;
	
	public FileLogger(String name) {
		this(createLocalLog(name));
	}

	/** This attempts to create a log in the "right" location
	 * for a given OS.
	 * @param fileName the name of the file, such as "My App.txt".
	 * @return the location this file should be.
	 */
	public static File createLocalLog(String fileName) {
		if (JVM.isMac) {
			return new File(System.getProperty("user.home") + "/Library/Logs/"
					+ fileName);
		} else if (JVM.isWindows) {
			return new File(System.getProperty("user.home")
					+ "\\Application Data\\" + fileName);
		}
		return new File(System.getProperty("user.home") + File.separator
				+ fileName);
	}

	public FileLogger(File f) {
		this.f = f;
		if (!f.exists()) {
			try {
				if (f.getParentFile()!=null && f.getParentFile().exists() == false)
					f.getParentFile().mkdirs();
				f.createNewFile();
			} catch (IOException e) {
				throw new RuntimeException(e.getMessage());
			}
		}
	}

	public void print(Object obj) {
		if(obj==null) 
			obj = "null";
		print2(obj.toString());
	}

	public File getFile() {
		return f;
	}

	private int inside = 0;

	public void println(Object obj) {
		print(obj);
		print("\r\n");
	}

	private void print2(String s) {
		synchronized (this) {
			try {
				inside++;
				FileOutputStream out = new FileOutputStream(f, true);
				out.write(s.getBytes());
				out.close();
			} catch (IOException e) {
				if (inside == 1) {
					/** This little clause is intended for logging the
					 * console output.  If we're logging the
					 * console output, but then an error occurs
					 * ~and we print it to the console~ then we might
					 * enter a nasty loop.  Instead only print one
					 * error.
					 */
					e.printStackTrace();
				}
			} finally {
				inside--;
			}
		}
	}
}
