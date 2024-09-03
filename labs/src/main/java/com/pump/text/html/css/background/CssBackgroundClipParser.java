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
package com.pump.text.html.css.background;

import java.util.Arrays;

import com.pump.text.html.css.CssPropertyParser;

public class CssBackgroundClipParser
		implements CssPropertyParser<CssBackgroundClipValue> {

	@Override
	public String getPropertyName() {
		return CssBackgroundClipValue.PROPERTY_BACKGROUND_CLIP;
	}

	@Override
	public CssBackgroundClipValue parse(String cssString) {
		String s = cssString.toLowerCase();
		for (CssBackgroundClipValue.Mode mode : CssBackgroundClipValue.Mode
				.values()) {
			if (s.startsWith(mode.toString())) {
				return new CssBackgroundClipValue(cssString, mode);
			}
		}
		throw new IllegalArgumentException(
				"Unsupported keyword \"" + s + "\". Expected "
						+ Arrays.asList(CssBackgroundClipValue.Mode.values()));
	}

}