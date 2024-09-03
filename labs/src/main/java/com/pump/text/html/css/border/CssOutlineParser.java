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
package com.pump.text.html.css.border;

/**
 * The outline CSS shorthand property set all the outline properties in a single
 * declaration such as "thick double #32a1ce"
 */
public class CssOutlineParser extends CssBorderParser {

	public static final String PROPERTY_OUTLINE = "outline";

	@Override
	public String getPropertyName() {
		return PROPERTY_OUTLINE;
	}
}