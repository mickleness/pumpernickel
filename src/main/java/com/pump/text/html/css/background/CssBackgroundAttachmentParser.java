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
package com.pump.text.html.css.background;

import java.util.Arrays;
import java.util.List;

import com.pump.text.html.css.CssListParser;
import com.pump.text.html.css.background.CssBackgroundAttachmentValue.Mode;

public class CssBackgroundAttachmentParser
		extends CssListParser<CssBackgroundAttachmentValue> {

	@Override
	public String getPropertyName() {
		return CssBackgroundAttachmentValue.PROPERTY_BACKGROUND_ATTACHMENT;
	}

	@Override
	protected int parseListElement(String cssString, int index,
			List<CssBackgroundAttachmentValue> dest) {
		String s = cssString.substring(index).toUpperCase();
		for (Mode mode : Mode.values()) {
			if (s.startsWith(mode.name())) {
				dest.add(new CssBackgroundAttachmentValue(cssString
						.substring(index, index + mode.name().length()), mode));
				return index + mode.name().length();
			}
		}
		throw new IllegalArgumentException("Unsupported keyword \"" + s
				+ "\". Expected " + Arrays.asList(Mode.values()));
	}

}