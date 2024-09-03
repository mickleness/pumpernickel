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
package com.pump.desktop.logging;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import com.pump.io.ConsoleLogger;
import com.pump.io.FileLogger;
import com.pump.io.FileUtils;

/**
 * This class combines the ConsoleLogger (which records System.err and
 * System.out to a file) and java's Logger architecture (which can now also log
 * to the same ConsoleLogger file).
 * <p>
 * This is intended for use with desktop applications, and each user session of
 * the desktop application is recorded in a unique file. The static initializer
 * for this class also manages a directory that keeps multiple files from past
 * sessions.
 *
 */
public class SessionLog {
	private static SessionLog GLOBAL;

	private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
			"yyyy.MM.dd 'at' HH.mm.ss z");

	/**
	 * This initializes a ConsoleLogger based in a constant directory called
	 * "[MyApp] Logs".
	 * <p>
	 * The problem with using just one file as the console log is: by the time
	 * end users report a bug/exception/problem to you, they may be several
	 * sessions away from the session with the original problem. (Especially if
	 * the original problem made them force quit the application.) By storing
	 * several logs in the same folder: we can usually retrieve the correct log.
	 * 
	 * @param appName
	 *            The name of the application that is collecting logs. For
	 *            example, if this is "Gemini" then the directory "Gemini Logs"
	 *            will be used. It will help to provide a more qualified path
	 *            name to guarantee uniqueness, such as "com.pump.gemini".
	 * @param limit
	 *            if non-null, then this method will sift through all other logs
	 *            in this directory and only retain the most recent n-many logs.
	 * @throws IOException
	 */
	public synchronized static void initialize(String appName, Integer limit)
			throws IOException {
		if (GLOBAL != null)
			throw new RuntimeException(
					"SessionLog.initialize was already called for "
							+ GLOBAL.appName);
		File dir = FileLogger.createLocalLog(appName + " Logs");
		if (!dir.exists()) {
			FileUtils.mkdirs(dir);
		} else {
			if (!dir.isDirectory())
				throw new IOException("the file \"" + dir.getAbsolutePath()
						+ "\" needs to be a directory.");
		}

		Date date = new Date();
		String filename = DATE_FORMAT.format(date) + ".txt";
		File file = new File(dir, filename);
		if (file.exists())
			throw new IOException("the file \"" + file.getAbsolutePath()
					+ "\" already exists");

		GLOBAL = new SessionLog(file, appName);
		System.out.println(
				"SessionLog initialized using: " + file.getAbsolutePath());

		if (limit != null) {
			File[] children = dir.listFiles();
			TreeMap<Date, File> files = new TreeMap<>();
			for (File child : children) {
				if ((!child.isHidden()) && child.isFile()) {
					String childname = child.getName();
					if (childname.endsWith(".txt")) {
						try {
							Date childdate = DATE_FORMAT.parse(childname
									.substring(0, childname.length() - 4));
							files.put(childdate, child);
						} catch (Exception e) {
						}
					}
				}
			}
			while (files.size() > limit.intValue()) {
				Date d = files.keySet().iterator().next();
				File f = files.remove(d);
				f.delete();
				System.out
						.println("SessionLog: deleting " + f.getAbsolutePath());
			}
		}
	}

	public static SessionLog get() {
		if (GLOBAL == null)
			throw new NullPointerException(
					"SessionLog.initialize() has not been called.");
		return GLOBAL;
	}

	private static class MyFileHandler extends Handler {

		ConsoleLogger fileLogger;

		MyFileHandler(File file) {
			fileLogger = new ConsoleLogger(file);
		}

		@Override
		public void publish(LogRecord record) {
			fileLogger.println(record.getMessage());
		}

		@Override
		public void flush() {
		}

		@Override
		public void close() {
		}

	}

	Logger logger;
	String appName;
	File file;

	SessionLog(File file, String appName) {
		this.file = file;
		logger = Logger.getLogger(appName);
		logger.addHandler(new ConsoleHandler());
		logger.addHandler(new MyFileHandler(file));
		LogManager.getLogManager().addLogger(logger);
	}

	public File getFile() {
		return file;
	}

	public Logger getLogger() {
		return logger;
	}
}