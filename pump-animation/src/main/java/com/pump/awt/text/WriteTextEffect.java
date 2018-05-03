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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Objects;

import com.pump.awt.Dimension2D;
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
	Color foreground;

	public WriteTextEffect(WritingFont font, int fontSize, String text,
			int width, int height, Color foreground) {
		Objects.requireNonNull(font);
		Objects.requireNonNull(text);
		Objects.requireNonNull(foreground);
		this.font = font;
		this.text = text;
		this.foreground = foreground;
		size = new Dimension(width, height);
		layout = new WritingTextLayout(font, fontSize, text);
	}

	@Override
	public void paint(Graphics2D g, float fraction) {
		g = (Graphics2D) g.create();
		Rectangle r = new Rectangle(0, 0, size.width, size.height);
		Dimension2D d = layout.getSize(r);
		g.translate(size.width / 2 - d.getWidth() / 2,
				size.height / 2 - d.getHeight() / 2);
		layout.paint(g, r, fraction * layout.getDuration(), foreground);
		g.dispose();
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(size.width, size.height);
	}

}