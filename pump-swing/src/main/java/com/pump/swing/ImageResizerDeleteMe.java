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
package com.pump.swing;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import com.pump.image.pixel.Scaling;
import com.pump.io.FileTreeIterator;
import com.pump.io.IOUtils;

public class ImageResizerDeleteMe {
	public static void main(String[] args) {
		File file = new File("/Users/jeremy/Documents/Resized Wedding Photos");
		FileTreeIterator iter = new FileTreeIterator(file, "jpg", "jpeg", "png");
		Map<Long, Collection<File>> fileSizeMap = new HashMap<>();
		long saved = 0;
		while (iter.hasNext()) {
			File f = iter.next();
			try {
				Long fileSize = new Long(f.length());
				Collection<File> otherFiles = fileSizeMap.get(fileSize);
				if (otherFiles == null) {
					otherFiles = new ArrayList<>();
					fileSizeMap.put(fileSize, otherFiles);
				}

				if (isFileMatch(otherFiles, f)) {
					System.out.println("deleted: " + f.getAbsolutePath());
					f.delete();
					continue;
				}
				otherFiles.add(f);
			} catch (Exception e) {
				System.err.println("Couldn't validate (or delete): "
						+ f.getAbsolutePath());
				e.printStackTrace();
			}
		}

		iter = new FileTreeIterator(file, "jpg", "jpeg", "png");
		while (iter.hasNext()) {
			File f = iter.next();
			try {
				BufferedImage bi = ImageIO.read(f);
				BufferedImage bi2;
				if (bi.getWidth() > bi.getHeight()) {
					bi2 = Scaling.scaleProportionally(bi, new Dimension(1024,
							768));
				} else {
					bi2 = Scaling.scaleProportionally(bi, new Dimension(768,
							1024));
				}
				if (bi2.getWidth() != bi.getWidth()
						|| bi2.getHeight() != bi.getHeight()) {
					long localSaved = f.length();
					f.delete();
					String filename = f.getName();
					if (filename.contains("."))
						filename = filename.substring(0,
								filename.lastIndexOf('.'));
					File dest = new File(f.getParent(), filename + ".jpg");
					ImageIO.write(bi2, "jpg", dest);
					localSaved = localSaved - dest.length();
					if (localSaved < 0)
						System.err.println("WARNING: increased file size");
					if (f.getAbsolutePath().equals(dest.getAbsolutePath())) {
						System.out.println("Resaved " + f.getAbsolutePath()
								+ " (" + localSaved + ")");
					} else {
						System.out.println("Converted " + f.getAbsolutePath()
								+ " to " + dest.getName() + " (" + localSaved
								+ ")");
					}
					saved += localSaved;
				}
			} catch (Exception e) {
				System.err.println("Couldn't resize: " + f.getAbsolutePath());
				e.printStackTrace();
			}
		}
		System.out.println("Done. Saved: " + saved + " bytes");
	}

	protected static boolean isFileMatch(Collection<File> candidateFiles,
			File file) throws IOException {
		for (File candidateFile : candidateFiles) {
			if (IOUtils.equals(candidateFile, file)) {
				return true;
			}
		}
		return false;
	}
}