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
 * The CSS outline-width property sets the thickness of an element's outline. An
 * outline is a line that is drawn around an element, outside the border.
 */
public class CssOutlineWidthParser extends AbstractWidthParser {

	public static final String PROPERTY_OUTLINE_WIDTH = "outline-width";

	@Override
	public String getPropertyName() {
		return PROPERTY_OUTLINE_WIDTH;
	}
}