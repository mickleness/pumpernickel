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
package com.pump.awt.text;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import com.pump.awt.text.writing.WritingFont;
import com.pump.awt.text.writing.WritingTextLayout;

/**
 * A TextEffect that draws text using a
 * {@link com.pump.awt.text.writing.WritingFont}.
 */
public class WriteTextEffect implements TextEffect {
	Dimension size;
	WritingFont font;
	String text;
	WritingTextLayout layout;

	public WriteTextEffect(WritingFont font, String text, int width, int height) {
		this.font = font;
		this.text = text;
		size = new Dimension(width, height);
		layout = new WritingTextLayout(font, 36, text);
	}

	@Override
	public void paint(Graphics2D g, float fraction) {
		layout.paint(g, new Rectangle(0, 0, size.width, size.height), fraction
				* layout.getDuration());
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(size.width, size.height);
	}

}