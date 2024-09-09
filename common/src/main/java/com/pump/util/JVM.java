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
package com.pump.util;

import javax.swing.UIManager;

/**
 * Static methods relating to the JVM environment.
 */
public class JVM {

	/**
	 * Prints basic information about this session's JVM: the OS name &
	 * version, the Java version, and (on Mac) whether Quartz is being used.
	 */
	public static void printProfile() {
		System.out.println(getProfile());
	}

	/**
	 * Gets basic information about this session's JVM: the OS name &
	 * version, the Java version, and (on Mac) whether Quartz is being used.
	 */
	public static String getProfile() {
		return "OS = " + System.getProperty("os.name") + " ("
				+ System.getProperty("os.version") + "), "
				+ System.getProperty("os.arch") + "\n" +
				"Java Version = " + System.getProperty("java.version") + "\n";
	}

	/**
	 * The major Java version being used (1.4, 1.5, 1.6, etc.), or -1 if this
	 * value couldn't be correctly determined.
	 */
	public static final float javaVersion = deriveMajorJavaVersion();

	private static final String osName = getOSName();

	private static String getOSName() {
		return (System.getProperty("os.name").toLowerCase());
	}

	/** Whether this session is on a Mac. */
	public static final boolean isMac = (osName.contains("mac"));

	/** Whether this session is on a Linux machine. */
	public static final boolean isLinux = (osName.contains("linux"));

	/** Whether this session is on Windows. */
	public static final boolean isWindows = (osName.contains("windows"));

	/** Whether this session is on Vista. */
	public static final boolean isVista = (osName.contains("vista"));

	/** Whether this session is on Windows 7. */
	public static final boolean isWindows7 = isWindows
			&& (osName.contains("7"));

	/** Whether this session is on Windows XP. */
	public static final boolean isWindowsXP = isWindows
			&& (osName.contains("xp"));

	/** Whether this session is on Windows Vista or Windows 7. */
	public static final boolean isVistaOrWindows7 = isVista || isWindows7;

	/**
	 * This converts the system property "java.version" to a float value. This
	 * drops rightmost digits until a legitimate float can be parsed. <BR>
	 * For example, this converts "1.6.0_05" to "1.6". <BR>
	 * This value is cached as the system property "java.major.version".
	 * Although technically this value is a String, it will always be parseable
	 * as a float.
	 */
	private static float deriveMajorJavaVersion() {
		String s = System.getProperty("java.version");
		float f = -1;
		int i = s.length();
		while (f < 0 && i > 0) {
			try {
				f = Float.parseFloat(s.substring(0, i));
			} catch (Exception e) {
				// intentionally empty
			}
			i--;
		}
		return f;
	}

	/**
	 * Return true if the current L&F is Aqua.
	 */
	public static boolean isAqua() {
		return "Aqua".equals(UIManager.getLookAndFeel().getID());
	}
}