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
package com.pump.io.location;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

public class DefaultIOLocationFilter extends IOLocationFilter {
	static List<String> badZips = new ArrayList<String>();

	JComponent component;

	public DefaultIOLocationFilter(JComponent jc) {
		component = jc;
	}

	protected boolean getBoolean(String keyName, boolean defaultValue) {
		Boolean b = (Boolean) component.getClientProperty(keyName);
		if (b == null)
			return defaultValue;
		return b.booleanValue();
	}

	@Override
	public IOLocation filter(IOLocation loc) {
		if (getBoolean("showHiddenFiles", false) == false) {
			if (loc.isHidden())
				return null;
		}
		if (getBoolean("openArchives", false)) {
			synchronized (badZips) {
				if (badZips.contains(loc.toString()) == false) {
					String name = loc.getName().toLowerCase();
					if (name.endsWith(".zip") || name.endsWith(".jar")) {
						try {
							ZipArchiveLocation zipLoc = new ZipArchiveLocation(
									loc);
							return zipLoc;
						} catch (OutOfMemoryError e) {
							e.printStackTrace();
							System.err.println("out of memory reading " + loc);
							badZips.add(loc.toString());
						} catch (Exception e) {
							e.printStackTrace();
							System.err.println("error reading " + loc);
							badZips.add(loc.toString());
						}
					}
				}
			}
		}
		return loc;
	}
}