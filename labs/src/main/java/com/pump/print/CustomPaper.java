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
package com.pump.print;

import java.awt.Insets;
import java.awt.print.Paper;

/**
 * This extends Java's <code>Paper</code> definition to include a name. Also
 * several predefined paper sizes are provided.
 *
 */
public class CustomPaper extends Paper {
	public static CustomPaper[] common = new CustomPaper[] {
			// TODO: localize
			CustomPaper.create("US Letter", 8.5, 11.0),
			CustomPaper.create("US Legal", 8.5, 14.0),
			CustomPaper.create("A4", 8.26, 11.69),
			CustomPaper.create("A5", 5.83, 8.26),
			CustomPaper.create("ROC 16K", 7.75, 10.75),
			CustomPaper.create("JB5", 7.17, 10.12),
			CustomPaper.create("B5", 6.93, 9.85),
			CustomPaper.create("#10 Envelope", 4.12, 9.50),
			CustomPaper.create("Choukei 3 Envelope", 4.72, 9.25),
			CustomPaper.create("Tabloid", 11, 17),
			CustomPaper.create("A3", 11.69, 16.54),
			CustomPaper.create("Tabloid Extra", 12, 18),
			CustomPaper.create("Super B/A3", 13, 19) };

	private static CustomPaper create(String name, double widthInches,
			double heightInches) {
		return new CustomPaper(name, widthInches * 72, heightInches * 72);
	}

	String paperName;

	public CustomPaper(String name, double width, double height) {
		super();

		setSize(width, height);
		paperName = name;
		Insets insets = new Insets(18, 18, 18, 18);
		this.setImageableArea(insets.left, insets.top, width - insets.left
				- insets.right, height - insets.top - insets.bottom);
	}

	public String getName() {
		return paperName;
	}

	@Override
	public String toString() {
		return getName();
	}
}