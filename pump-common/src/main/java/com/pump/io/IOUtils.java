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
package com.pump.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Serializable;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import com.pump.util.JVM;

/**
 * A collection of static methods relating to Files and IO operations.
 * <P>
 * These methods should have very few class dependencies; if you're considering
 * adding a method that will require several other classes/packages, please put
 * it in a different location.
 *
 */
public class IOUtils {

	static DecimalFormat format = new DecimalFormat("0.0");

	/**
	 * @param file
	 *            the file to return the size of.
	 * @return a human-readable String representing a file's size. For example,
	 *         this may return "67.3 KB" or "203.4 MB".
	 */
	public static String formatFileSize(File file) {
		return formatFileSize(file.length());
	}

	/**
	 * @param byteCount
	 *            the size of a file in bytes.
	 * @return a human-readable String representing a file's size. For example,
	 *         this may return "67.3 KB" or "203.4 MB".
	 */
	public static String formatFileSize(long byteCount) {
		if (byteCount < 1024) {
			return byteCount + " bytes";
		} else {
			double d = byteCount;
			d = d / 1024.0;
			if (d < 1024.0) {
				return format.format(d) + " KB";
			}
			d = d / 1024;
			if (d < 1024) {
				return format.format(d) + " MB";
			}
			d = d / 1024;
			if (d < 1024) {
				return format.format(d) + " GB";
			}
			d = d / 1024;
			return format.format(d) + " TB";
		}
	}

	/**
	 * Format a number as String that can be always be large enough to hold
	 * "totalSize" digits. So for example: "1" should be formatted as "001" if
	 * there will eventually be "235" elements, but it should be formatted as
	 * "01" if there will only be "23" elements.
	 * 
	 * @param index
	 *            the index to format.
	 * @param totalSize
	 *            the maximum value we're counting towards.
	 * @return a String representation of <code>index</code>.
	 */
	public static String formatIndex(int index, int totalSize) {
		int digits = (int) (Math.ceil(Math.log(totalSize) / Math.log(10)) + .5);
		String s = "" + index;
		while (s.length() < digits) {
			s = "0" + s;
		}
		return s;
	}

	/**
	 * The description of InputStream.skip() is unfulfilling. It is perfectly OK
	 * for InputStream.skip() to return zero, but not necessarily mean EOF.
	 * <P>
	 * This method will skip either the number of bytes requested, or if it
	 * falls short that indicates we read to the end of the file.
	 * 
	 * @param in
	 *            the InputStream to skip bytes from
	 * @param amount
	 *            the amount of bytes to skip
	 * @return the number of bytes skipped.
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public static long skipFully(InputStream in, long amount)
			throws IOException {
		if (amount < 0)
			return 0;

		long sum = 0;
		long t = in.skip(amount - sum);
		while (t + sum != amount) {
			if (t == 0) {
				// in.read() has a clear EOF indicator, though:
				t = in.read();
				if (t == -1)
					return sum;
				sum++;
			} else {
				sum += t;
			}
			t = in.skip(amount - sum);
		}

		return t + sum;
	}

	/**
	 * Delete this file and all its children, if any.
	 * <p>
	 * Special OS-specific commands may be applied to optimize deletion.
	 * 
	 * @param file
	 *            the file to delete.
	 * @return true if the delete was successful.
	 */
	public static boolean delete(File file) {
		boolean success = true;
		if (file.isDirectory()) {
			if (deleteDir(file))
				return true;

			File[] children = file.listFiles();

			// this probably indicates a lack of the correct permissions
			if (children == null)
				throw new NullPointerException();

			for (int a = 0; a < children.length; a++) {
				try {
					if (delete(children[a]) == false)
						success = false;
				} catch (RuntimeException e) {
					// don't let one child's exception stop us from deleting
					// other files, though:
					success = false;
				}
			}
		}
		if (file.delete() == false)
			success = false;
		return success;
	}

	/** Use special OS-level tricks to delete entire directories at a time. */
	private static boolean deleteDir(File dir) {
		if (!dir.isDirectory())
			throw new IllegalArgumentException();

		if (!dir.exists()) {
			return true;
		}

		try {
			Process p;
			if (JVM.isWindows) {
				String[] cmd1 = { "cmd", "/C", "rmdir", dir.getAbsolutePath(),
						"/S", "/Q" };
				p = Runtime.getRuntime().exec(cmd1);
			} else { // on Mac or Linux
				String[] cmd = { "rm", "-R", dir.getAbsolutePath() };
				p = Runtime.getRuntime().exec(cmd);
			}
			int status = p.waitFor();
			if (dir.exists()) {
				System.err.println("IOUtils.deleteDir( "
						+ dir.getAbsolutePath() + " ) failed (status=" + status
						+ ")");
				String s = read(p.getErrorStream());
				if (s != null)
					System.err.println(s);
				return false;
			} else {
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.err.println("IOUtils.deleteDir " + dir.getAbsolutePath());
		return false;
	}

	/**
	 * @return true if this file is a zip file.
	 * @param file
	 *            the file to check.
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public static boolean isZip(File file) throws IOException {
		if (file.exists() == false)
			return false;

		try (InputStream in = new FileInputStream(file);
				ZipInputStream zipIn = new ZipInputStream(in)) {
			ZipEntry e = zipIn.getNextEntry();
			if (e == null)
				return false;
			int ctr = 0;
			while (e != null && ctr < 4) {
				e = zipIn.getNextEntry();
				ctr++;
			}
			return true;
		} catch (Throwable t) {
			return false;
		}
	}

	/**
	 * Returns true if these zip files act like equivalent sets. The order of
	 * the zip entries is not important: if they contain exactly the same
	 * contents, this returns true.
	 * 
	 * @param zip1
	 *            one zip file
	 * @param zip2
	 *            another zip file
	 * @param ignorableEntries
	 *            if non-null then this if a ZipEntry matches any of these
	 *            patterns it is ignored in the comparison. For example "*.RSA"
	 *            will skip all entries that end in ".RSA".
	 * @return true if the two zip archives are equivalent sets
	 * @throws IOException
	 */
	public static boolean zipEquals(File zip1, File zip2,
			String... ignorableEntries) throws IOException {
		if (zip1.equals(zip2))
			return true;

		if (zip1.exists() != zip2.exists())
			return false;

		if (!zip1.exists())
			return true; // erg, two missing files are equal I guess?

		Pattern[] ignorablePatterns = ignorableEntries == null ? new Pattern[] {}
				: new Pattern[ignorableEntries.length];
		for (int a = 0; a < ignorablePatterns.length; a++) {
			ignorablePatterns[a] = Pattern.compile(ignorableEntries[a]);
		}

		if (zip1.length() != zip2.length() && ignorablePatterns.length == 0)
			return false;

		Map<String, Long> crcMap = new HashMap<String, Long>();

		try (InputStream in = new FileInputStream(zip1);
				ZipInputStream zipIn = new ZipInputStream(in)) {
			ZipEntry e = zipIn.getNextEntry();
			scanZipArchive: while (e != null) {
				try {
					for (Pattern pattern : ignorablePatterns) {
						if (pattern.matcher(e.getName()).matches()) {
							continue scanZipArchive;
						}
					}
					long crc = getCRC(zipIn, false);
					crcMap.put(e.getName(), crc);
				} finally {
					e = zipIn.getNextEntry();
				}
			}
		}

		try (InputStream in = new FileInputStream(zip2);
				ZipInputStream zipIn = new ZipInputStream(in)) {
			ZipEntry e = zipIn.getNextEntry();
			scanZipArchive: while (e != null) {
				try {
					Long existingCRC = crcMap.remove(e.getName());
					if (existingCRC == null) {
						for (Pattern pattern : ignorablePatterns) {
							if (pattern.matcher(e.getName()).matches()) {
								continue scanZipArchive;
							}
						}
						return false;
					}
					long crc = getCRC(zipIn, false);
					if (crc != existingCRC)
						return false;
				} finally {
					e = zipIn.getNextEntry();
				}
			}
		}

		if (crcMap.size() > 0)
			return false;
		return true;
	}

	/**
	 * Return the length of an InputStream. This is measured by invoking
	 * <code>InputStream.skip(..)</code> and <code>InputStream.read()</code>.
	 */
	public static long getLength(InputStream in) throws IOException {
		long t = in.skip(65536);
		while (in.read() != -1) {
			t++;
			t += in.skip(65536);
		}
		return t;
	}

	/**
	 * Calculate the CRC32 checksum of all the data in an InputStream.
	 * 
	 * @param in
	 *            the InputStream to read to completion.
	 * @param close
	 *            whether this method will invoke <code>in.close()</code> on
	 *            completion.
	 * @return the CRC32 checksum for this InputStream.
	 * @throws IOException
	 */
	public static long getCRC(InputStream in, boolean close) throws IOException {
		CRC32 crc = new CRC32();
		byte[] chunk = new byte[4096];
		try {
			int t = in.read(chunk);
			while (t != -1) {
				crc.update(chunk, 0, t);
				t = in.read(chunk);
			}
			return crc.getValue();
		} finally {
			if (close) {
				in.close();
			}
		}
	}

	static private byte[] b1;
	static private byte[] b2;

	public synchronized static boolean equals(InputStream in1, InputStream in2)
			throws IOException {
		if (b1 == null)
			b1 = new byte[4096];
		if (b2 == null)
			b2 = new byte[4096];

		int k1 = read(in1, b1);
		int k2 = read(in2, b2);
		while (k1 > 0 && k2 > 0) {
			if (k1 != k2) {
				return false;
			}
			if (equals(b1, b2, k1) == false) {
				return false;
			}
			k1 = read(in1, b1);
			k2 = read(in2, b2);
		}
		return true;
	}

	public static boolean equals(byte[] a, byte[] a2, int length) {
		if (a == a2)
			return true;
		if (a == null || a2 == null)
			return false;

		if (length > a.length)
			throw new IllegalArgumentException();
		if (length > a2.length)
			throw new IllegalArgumentException();

		for (int i = 0; i < length; i++)
			if (a[i] != a2[i])
				return false;

		return true;
	}

	/** This accepts all files that are not hidden. */
	public static final FileFilter VISIBLE_FILE_FILTER = new FileFilter() {

		@Override
		public boolean accept(File file) {
			return !file.isHidden();
		}

	};

	/**
	 * @return true if two files are exactly equal. This will call
	 *         <code>zipEquals()</code> if both files are zip files.
	 * @param file1
	 *            the first file
	 * @param file2
	 *            the second file
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public static boolean equals(File file1, File file2) throws IOException {
		if (file1.exists() != file2.exists())
			return false;

		if (isZip(file1) && isZip(file2)) {
			return zipEquals(file1, file2);
		}

		if (file1.length() != file2.length())
			return false;
		if (file1.isDirectory() != file2.isDirectory())
			return false;
		if (file1.isDirectory()) {
			File[] list1 = file1.listFiles(VISIBLE_FILE_FILTER);
			File[] list2 = file2.listFiles(VISIBLE_FILE_FILTER);
			if (list1.length != list2.length)
				return false;
			Arrays.sort(list1);
			Arrays.sort(list2);
			for (int a = 0; a < list1.length; a++) {
				if (!equals(list1[a], list2[a]))
					return false;
			}
			return true;
		}

		try (InputStream in1 = new FileInputStream(file1);
				InputStream in2 = new FileInputStream(file2)) {
			return equals(in1, in2);
		}
	}

	/**
	 * Read data into the destination array.
	 * 
	 * @param in
	 *            the InputStream to read.
	 * @param dest
	 *            the destination to write to
	 * @return the number of bytes read (note this will be less than dest.length
	 *         when the end of the stream is reached).
	 * @throws IOException
	 */
	public static int read(InputStream in, byte[] dest) throws IOException {
		int length = dest.length;
		int read = 0;
		int k = in.read(dest, read, length - read);
		while (k != -1 && read < dest.length) {
			read += k;
			k = in.read(dest, read, dest.length - read);
		}
		if (k != -1) {
			read += k;
		}
		return read;
	}

	/**
	 * Writes a copy of a file. This is shorthand for
	 * <code>copy(src, dst, false)</code>.
	 * 
	 * @param src
	 *            the file to copy
	 * @param dst
	 *            the location to write the new file. This method also invokes
	 *            parentFile.mkdirs() and dst.createNewFile(). If either of
	 *            these methods return false then an IOException is thrown
	 *            before any data transfer is attempted.
	 * @throws IOException
	 */
	public synchronized static void copy(File src, File dst) throws IOException {
		copy(src, dst, false);
	}

	private static Boolean useCP = null;

	/**
	 * Writes a copy of a file.
	 * 
	 * @param src
	 *            the file to copy
	 * @param dst
	 *            the location to write the new file. This method also invokes
	 *            parentFile.mkdirs() and dst.createNewFile(). If either of
	 *            these methods return false then an IOException is thrown
	 *            before any data transfer is attempted.
	 * @param abortIfIdentical
	 *            if true then this method first calls
	 *            <code>equals(src, dst)</code>, and exits this method if the
	 *            two files are identical. This avoids any writing, but it
	 *            introduces a separate pass (reading) before any writing
	 *            begins.
	 * @throws IOException
	 * 
	 * @return false if we aborted without writing anything, true if the
	 *         operation wrote/copied the file.
	 */
	public synchronized static boolean copy(File src, File dst,
			boolean abortIfIdentical) throws IOException {
		if (!dst.getParentFile().exists())
			if (!dst.getParentFile().mkdirs())
				throw new IOException("mkdirs failed for "
						+ dst.getParentFile().getAbsolutePath());

		if (abortIfIdentical) {
			if (equals(src, dst))
				return false;
		}

		if (useCP == null || useCP) {
			try {
				String[] cmd = { "cp", src.getAbsolutePath(),
						dst.getAbsolutePath() };
				Process process = Runtime.getRuntime().exec(cmd);
				int rv = process.waitFor();
				if (rv == 0)
					return true;
			} catch (Exception e) {
				if (e.getMessage().contains("Cannot run program \"cp\"")) {
					useCP = Boolean.FALSE;
				} else {
					e.printStackTrace();
				}
			}
		}
		if (b1 == null)
			b1 = new byte[4096];

		if (!dst.exists())
			if (!dst.createNewFile())
				throw new IOException("createNewFile failed for "
						+ dst.getAbsolutePath());
		try (InputStream in = new FileInputStream(src);
				OutputStream out = new FileOutputStream(dst)) {
			int k = in.read(b1);
			while (k != -1) {
				out.write(b1, 0, k);
				k = in.read(b1);
			}
			return true;
		} catch (IOException | RuntimeException e) {
			System.err.println("src = " + src.getAbsolutePath());
			System.err.println("dst = " + dst.getAbsolutePath());
			throw e;
		}
	}

	/**
	 * Rewrite a file using the lines provided.
	 * 
	 * @see #readLines(File, int)
	 * 
	 * @param file
	 *            the file to write to
	 * @param lines
	 *            the new lines to write.
	 * @param abortIfNoChange
	 *            if true then this method first checks whether the current file
	 *            exactly matches what will be written.
	 * @return true if the file was written, false if this method returned
	 *         without writing anything.
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public static boolean writeLines(File file, String[] lines,
			boolean abortIfNoChange) throws IOException {
		if (abortIfNoChange) {
			String[] oldLines = readLines(file, lines.length + 1);
			if (oldLines == null)
				oldLines = new String[] {};

			if (oldLines.length == lines.length) {
				boolean dirty = false;
				for (int a = 0; a < lines.length && (!dirty); a++) {
					if (!lines[a].equals(oldLines[a])) {
						dirty = true;
					}
				}
				if (!dirty)
					return false;
			}
		}
		file.delete();
		file.createNewFile();

		try (FileOutputStream out = new FileOutputStream(file);
				PrintStream ps = new PrintStream(out)) {
			for (int a = 0; a < lines.length; a++) {
				ps.println(lines[a]);
			}
			ps.flush();
			return true;
		}
	}

	/**
	 * @return all the lines of text in a text file.
	 * 
	 * @see #writeLines(File, String[], boolean)
	 * @param file
	 *            the file to read
	 * @param maxArraySize
	 *            the maximum number of lines this method will return. If this
	 *            is negative then it is ignored and all lines will always be
	 *            read.
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public static String[] readLines(File file, int maxArraySize)
			throws IOException {
		try (FileInputStream in = new FileInputStream(file)) {
			return readLines(in, maxArraySize);
		}
	}

	/**
	 * @return all the lines of text in a text file.
	 * 
	 * @see #writeLines(File, String[], boolean)
	 * @param in
	 *            the input stream to read.
	 * @param maxArraySize
	 *            the maximum number of lines this method will return. If this
	 *            is negative then it is ignored and all lines will always be
	 *            read.
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public static String[] readLines(InputStream in, int maxArraySize)
			throws IOException {
		try (BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
			List<String> strings = new ArrayList<String>();
			String s = br.readLine();
			while (s != null
					&& (maxArraySize < 0 || strings.size() < maxArraySize)) {
				strings.add(s);
				s = br.readLine();
			}
			return strings.toArray(new String[strings.size()]);
		}
	}

	/**
	 * Write the text provided to a File.
	 * 
	 * @param file
	 *            the file to write to.
	 * @param text
	 *            the text to write.
	 * @param abortIfNoChange
	 *            if true then this method first checks whether the current file
	 *            exactly matches what will be written.
	 * @return true if the file was written, false if this method returned
	 *         without writing anything.
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public static boolean write(File file, String text, boolean abortIfNoChange)
			throws IOException {
		if (text == null)
			throw new NullPointerException();
		if (abortIfNoChange) {
			String oldText = read(file);
			if (text.equals(oldText)) {
				return false;
			}
		}
		file.getParentFile().mkdirs();
		file.delete();
		file.createNewFile();
		try (FileOutputStream out = new FileOutputStream(file);
				OutputStreamWriter writer = new OutputStreamWriter(out)) {
			writer.write(text);
			writer.flush();
			return true;
		}
	}

	/**
	 * @return the contents of a file as a String, or null if the file does not
	 *         exists.
	 * @param file
	 *            the file to read
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public static String read(File file) throws IOException {
		if (file == null || (!file.exists()))
			return null;

		try (FileInputStream in = new FileInputStream(file)) {
			return read(in);
		}
	}

	/**
	 * 
	 * @param in
	 *            the InputStream to read to completion.
	 * 
	 * @return the String read from the InputStream.
	 * @throws IOException
	 */
	public static String read(InputStream in) throws IOException {
		return read(in, (String) null);
	}

	/**
	 * 
	 * @param in
	 *            the InputStream to read to completion.
	 * 
	 * @param charsetName
	 *            The optional name of a supported
	 *            {@link java.nio.charset.Charset </code>charset<code>}
	 * 
	 * @return the String read from the InputStream.
	 * @throws IOException
	 */
	public static String read(InputStream in, String charsetName)
			throws IOException {
		try (InputStreamReader inputReader = charsetName == null ? new InputStreamReader(
				in) : new InputStreamReader(in, charsetName)) {
			try (BufferedReader br = new BufferedReader(inputReader)) {
				return read(br);
			}
		}
	}

	public static String read(Reader r) throws IOException {
		if (r instanceof BufferedReader) {
			return doRead((BufferedReader) r);
		}
		return doRead(new BufferedReader(r));
	}

	private static String doRead(BufferedReader br) throws IOException {
		StringBuffer sb = null;
		String s = br.readLine();
		while (s != null) {
			if (sb == null) {
				sb = new StringBuffer();
				sb.append(s);
			} else {
				sb.append("\n");
				sb.append(s);
			}
			s = br.readLine();
		}
		if (sb == null)
			return "";
		return sb.toString();
	}

	/**
	 * Create a simple map based on key/values separated by an "=".
	 * 
	 * @param resource
	 *            the resource to read the map from
	 * @return a map of key/value Strings from the text resource provided.
	 */
	public static Map<String, String> readMap(URL resource) {
		Map<String, String> map = new HashMap<>();
		try (InputStream in = resource.openStream()) {
			String[] lines = IOUtils.readLines(in, 10000);
			for (String line : lines) {
				int i = line.indexOf('=');
				map.put(line.substring(0, i), line.substring(i + 1));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return map;
	}

	/**
	 * Writes the entire InputStream into the destination file.
	 * 
	 * @param in
	 *            the input stream to write
	 * @param dest
	 *            the file to write to.
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public static void write(InputStream in, File dest) throws IOException {
		if (dest.exists() == false)
			dest.createNewFile();
		try (OutputStream out = new FileOutputStream(dest)) {
			write(in, out);
		}
	}

	/**
	 * Writes a <code>Properties</code> object to a <code>File</code>.
	 * 
	 * @param p
	 *            the Properties to write
	 * @param file
	 *            the file to write to.
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public static void write(Properties p, File file) throws IOException {
		try (OutputStream out = new FileOutputStream(file)) {
			p.store(out, "");
		}
	}

	/**
	 * Loads properties into a file.
	 * <P>
	 * This assumes the file was written with the
	 * <code>Properties.store()</code> method.
	 * 
	 * @param p
	 *            the destination to store properties in
	 * @param file
	 *            the file to read from.
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public static void load(Properties p, File file) throws IOException {
		try (InputStream in = new FileInputStream(file)) {
			p.load(in);
		}
	}

	/**
	 * Loads properties into a file.
	 * <P>
	 * This assumes the file was written with the
	 * <code>Properties.store()</code> method.
	 * 
	 * @param p
	 *            the destination to store properties in
	 * @param url
	 *            the URL to read from.
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public static void load(Properties p, URL url) throws IOException {
		try (InputStream in = url.openStream()) {
			p.load(in);
		}
	}

	/**
	 * Writes a file to an OutputStream. This does not close the OutputStream.
	 * 
	 * @param file
	 *            the file to write.
	 * @param out
	 *            the stream to write to.
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public static void write(File file, OutputStream out) throws IOException {
		try (InputStream in = new FileInputStream(file)) {
			write(in, out);
		}
	}

	/**
	 * Writes the InputStream into the OutputStream. This does not close
	 * anything.
	 * 
	 * @param in
	 *            the data to read.
	 * @param out
	 *            the destination to write to.
	 * @throws IOException
	 *             if an IO problem occurs.
	 */
	public synchronized static void write(InputStream in, OutputStream out)
			throws IOException {
		if (b1 == null)
			b1 = new byte[4096];

		int t = in.read(b1);
		while (t != -1) {
			out.write(b1, 0, t);
			t = in.read(b1);
		}
	}

	/**
	 * @return a temp file path that does not yet exist with the base name
	 *         provided.
	 */
	public static File getUniqueTempFile(String name) {
		File parent = new File(System.getProperty("java.io.tmpdir"));
		return getUniqueFile(parent, name, false, true);
	}

	private static Map<File, Map<String, AtomicLong>> dirMap = new HashMap<File, Map<String, AtomicLong>>();

	/**
	 * Returns a file that does not yet exist.
	 * <p>
	 * If a file of the given name and parent does not already exist, then this
	 * method simply returns <code>new File(parentFile, name)</code>.
	 * 
	 * @param parentFile
	 *            the directory to create the file in.
	 * @param name
	 *            the starting name, which this method might modify to guarantee
	 *            unique-ness.
	 * @param includeSpaces
	 *            if false then there will be no spaces in the returned file
	 *            name. (If there were already spaces in the path of the parent,
	 *            though: those are unchanged.)
	 * @param uniquePerSession
	 *            if true then the File returned will be unique in this session.
	 *            This is useful for multithreaded environments if the user
	 *            isn't going to interact with these directories (such as temp
	 *            directories). But it is bad if this helps rename a saved file.
	 *            (Just because we previously used "Untitled 2" does NOT mean
	 *            that if the user has since deleted "Untitled" and "Untitled 2"
	 *            that we need to iterate to the unique "Untitled 3" option.)
	 * @return a File object that maps to something that does not yet exist.
	 */
	public static File getUniqueFile(File parentFile, String name,
			boolean includeSpaces, boolean uniquePerSession) {
		if (!includeSpaces)
			name = name.replace(" ", "");

		String baseName, ext;
		int i = name.lastIndexOf('.');
		if (i == -1) {
			ext = "";
			baseName = name;
		} else {
			ext = name.substring(i);
			baseName = name.substring(0, i);
		}

		AtomicLong ctr;
		if (uniquePerSession) {
			synchronized (dirMap) {
				Map<String, AtomicLong> m = dirMap.get(parentFile);
				if (m == null) {
					m = new HashMap<String, AtomicLong>();
					dirMap.put(parentFile, m);
				}
				ctr = m.get(name);
				if (ctr == null) {
					ctr = new AtomicLong(1);
					m.put(name, ctr);
				}
			}
		} else {
			if (Character.isDigit(baseName.charAt(baseName.length() - 1))) {
				// it ends with a number, so use that as our base number:
				i = baseName.length() - 1;
				while (i >= 0 && Character.isDigit(baseName.charAt(i))) {
					i--;
				}
				long k = Long.parseLong(baseName.substring(i + 1));

				// k necessarily exists (since we just found it), so increment:
				ctr = new AtomicLong(k + 1);
			} else {
				ctr = new AtomicLong(1);
			}
		}

		while (true) {
			File file;
			long k;
			synchronized (ctr) {
				k = ctr.getAndIncrement();
			}
			if (k == 1) {
				file = new File(parentFile, name);
			} else {
				String newName;
				if (includeSpaces) {
					newName = baseName + " " + k + ext;
				} else {
					newName = baseName + k + ext;
				}
				file = new File(parentFile, newName);
			}
			if (!file.exists()) {
				return file;
			}
		}
	}

	/**
	 * Reveal (select) a file in the operating system. This will be
	 * platform-specific, and for now is only supported on Mac.
	 * 
	 * @param file
	 *            the file to reveal.
	 * @return true if this method is reasonably confident the request was
	 *         successful. False otherwise.
	 */
	public static boolean reveal(File file) {
		try {
			if (JVM.isMac) {
				String script = "tell application \"Finder\""
						+ "\nset thePath to POSIX file \""
						+ file.getAbsolutePath() + "\""
						+ "\ntell application \"Finder\" to reveal thePath"
						+ "\ntell application \"Finder\" to activate"
						+ "\nend tell";

				ScriptEngineManager mgr = new ScriptEngineManager();
				ScriptEngine engine = mgr.getEngineByName("AppleScript");

				engine.eval(script);
				return true;
			}
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Return true if this file is an alias to another file.
	 * 
	 * @param file
	 *            the file to check against.
	 * @return true if this file is an alias to another file.
	 */
	public static boolean isAlias(File file) {
		try {
			if (!file.exists()) {
				return false;
			} else {
				String cnnpath = file.getCanonicalPath();
				String abspath = file.getAbsolutePath();
				boolean returnValue = !abspath.equals(cnnpath);
				return returnValue;
			}
		} catch (IOException ex) {
			return false;
		}
	}

	/**
	 * Deserialize a serialized, compressed object at the file path provided.
	 * 
	 * See {@link #serialize(Serializable, File)}
	 * 
	 * @param file
	 *            the file to read.
	 * @return the deserialized data.
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Serializable deserialize(File file) throws IOException,
			ClassNotFoundException {
		try (FileInputStream fileIn = new FileInputStream(file)) {
			try (GZIPInputStream zipIn = new GZIPInputStream(fileIn)) {
				try (ObjectInputStream objIn = new ObjectInputStream(zipIn)) {
					return (Serializable) objIn.readObject();
				}
			}
		}
	}

	/**
	 * Serialize and compress an object to the file path provided.
	 * 
	 * See {@link #deserialize(File)}.
	 * 
	 * @param object
	 *            the object to serialized.
	 * @param dest
	 *            the file to write.
	 * @throws IOException
	 */
	public static void serialize(Serializable object, File dest)
			throws IOException {

		if (!dest.exists()) {
			if (!dest.createNewFile()) {
				throw new IOException("File.createNewFile() failed for "
						+ dest.getAbsolutePath());
			}
		}

		try (FileOutputStream fileOut = new FileOutputStream(dest)) {
			try (GZIPOutputStream zipOut = new GZIPOutputStream(fileOut)) {
				try (ObjectOutputStream objOut = new ObjectOutputStream(zipOut)) {
					objOut.writeObject(object);
				}
			}
		}
	}
}