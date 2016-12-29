/*
 * @(#)CustomPaper.java
 *
 * $Date: 2014-03-13 04:15:48 -0400 (Thu, 13 Mar 2014) $
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
package com.pump.print;

import java.awt.Insets;
import java.awt.print.Paper;

/** This extends Java's <code>Paper</code> definition to include a name.
 * Also several predefined paper sizes are provided.
 *
 */
public class CustomPaper extends Paper {
	public static CustomPaper[] common = new CustomPaper[] {
		//TODO: localize
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
		CustomPaper.create("Super B/A3", 13, 19)
	};
	
	private static CustomPaper create(String name,double widthInches,double heightInches) {
		return new CustomPaper(name,widthInches*72,heightInches*72);
	}
	
	String paperName;
	public CustomPaper(String name,double width,double height) {
		super();
		
		
		setSize(width, height);
		paperName = name;
		Insets insets = new Insets(18,18,18,18);
		this.setImageableArea(insets.left, insets.top, 
				width-insets.left-insets.right, 
				height-insets.top-insets.bottom);
	}
	
	public String getName() {
		return paperName;
	}
	
	@Override
	public String toString() {
		return getName();
	}
}
