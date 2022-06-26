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
package com.pump.release;

import java.io.File;
import java.io.IOException;

import com.pump.io.FileTreeIterator;
import com.pump.io.IOUtils;

/**
 * This updates the source code header of all com.pump java-files in a
 * directory.
 */
public class UpdateSourceCodeHeader {
	public void run(File dir) throws IOException {
		FileTreeIterator iter = new FileTreeIterator(dir, "java");
		while (iter.hasNext()) {
			File file = iter.next();

			if (file.getName().contains("package-info"))
				continue;

			String str = IOUtils.read(file);
			int i1 = str.indexOf("package ");

			int i2 = str.indexOf(";", i1);
			String packageName = str.substring(i1 + "package ".length(), i2);
			if (packageName.startsWith("com.pump")) {
				String header = ""
						+ "/**\n"
						+ " * This software is released as part of the Pumpernickel project.\n"
						+ " * \n"
						+ " * All com.pump resources in the Pumpernickel project are distributed under the\n"
						+ " * MIT License:\n"
						+ " * https://github.com/mickleness/pumpernickel/raw/master/License.txt\n"
						+ " * \n"
						+ " * More information about the Pumpernickel project is available here:\n"
						+ " * https://mickleness.github.io/pumpernickel/\n"
						+ " */";
				String body = str.substring(i1);
				String newFile = header + "\n" + body;
				if (IOUtils.write(file, newFile, true)) {
					System.out.println("Updated source header for "
							+ file.getAbsolutePath());
				}

			}
		}
	}
}