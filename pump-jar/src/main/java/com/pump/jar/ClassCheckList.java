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
package com.pump.jar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.pump.swing.FileCheckList;
import com.pump.util.SearchConstraints;

public class ClassCheckList extends FileCheckList {
	private static final long serialVersionUID = 1L;

	/**
	 * @return all the tokens in a String separated by white space and made
	 *         lowercase.
	 */
	private static List<String> getTokens(String s) {
		List<String> returnValue = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		for (int a = 0; a < s.length(); a++) {
			char ch = s.charAt(a);
			if (!Character.isWhitespace(ch)) {
				sb.append(ch);
			} else {
				if (sb.length() > 0) {
					returnValue.add(sb.toString().toLowerCase());
					sb.delete(0, sb.length());
				}
			}
		}
		if (sb.length() > 0) {
			returnValue.add(sb.toString().toLowerCase());
			sb.delete(0, sb.length());
		}
		return returnValue;
	}

	private static boolean isMainLine(String s) {
		int i = s.indexOf("main");
		if (i != -1) {
			List<String> preTokens = getTokens(s.substring(0, i));
			if (!preTokens.remove("public"))
				return false;
			if (!preTokens.remove("static"))
				return false;
			if (!preTokens.remove("void"))
				return false;
			if (preTokens.size() > 0)
				return false;

			String mainParameters = s.substring(i + "main".length()).trim();
			// s2 will now be something like "(String[] args)" or
			// "(String args[])"
			String postParameters = null;
			if (mainParameters.startsWith("(")) {
				mainParameters = mainParameters.substring(1);
				int i2 = mainParameters.indexOf(')');
				if (i2 != -1) {
					postParameters = mainParameters.substring(i2 + 1);
					mainParameters = mainParameters.substring(0, i2);
				}

			}
			List<String> postTokens = getTokens(mainParameters);
			postTokens.remove("final"); // meh, this is a harmless modifier
			if (postTokens.remove("string[]")) {
				if (postTokens.size() == 1)
					return true;
			} else if (postTokens.remove("string")) {
				if (postTokens.size() == 1 && postTokens.get(0).endsWith("[]"))
					return true;
			}
		}
		return false;
	}

	public static boolean containsMainMethod(File file) {
		try (InputStream in = new FileInputStream(file)) {
			try (BufferedReader br = new BufferedReader(new InputStreamReader(
					in))) {
				String s = br.readLine();
				while (s != null) {
					s = s.trim();

					if (isMainLine(s))
						return true;

					s = br.readLine();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static final SearchConstraints<File> basicComparatorConstraints = new SearchConstraints<File>() {

		@Override
		public boolean accepts(File obj) {
			return true;
		}

		public int compare(File f1, File f2) {
			return f1.getName().toLowerCase()
					.compareTo(f2.getName().toLowerCase());
		}

	};

	protected static FileFilter includeMainFilter = new FileFilter() {
		public boolean accept(File file) {
			if (file.getName().toLowerCase().endsWith(".java") == false)
				return false;
			return containsMainMethod(file);
		}
	};

	protected static FileFilter excludeMainFilter = new FileFilter() {
		public boolean accept(File file) {
			if (file.getName().toLowerCase().endsWith(".java") == false)
				return false;
			return !(containsMainMethod(file));
		}
	};

	public ClassCheckList(boolean includeMainMethod) {
		this(null, includeMainMethod);
	}

	public ClassCheckList(File directory, boolean includeMainMethod) {
		super(directory, includeMainMethod ? includeMainFilter
				: excludeMainFilter, basicComparatorConstraints);
	}

	public boolean contains(String className) {
		File[] files = getVisibleFiles();
		for (int a = 0; a < files.length; a++) {
			String name = JarWriter.getClassName(files[a]);
			if (name.equals(className))
				return true;
		}
		return false;
	}

	@Override
	protected String getText(File file) {
		String s = file.getName();
		if (s.toLowerCase().endsWith(".java"))
			s = s.substring(0, s.length() - 5);
		String packageName = JarWriter.getPackage(file);
		s += " (" + packageName + ")";
		return s;
	}
}