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
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * This effect begins and ends with <code>BlockLetters</code> with no height.
 * During the animation: the text pops up, pulses twice, and then returns to its
 * original state:
 * <p>
 * <img src=
 * "https://raw.githubusercontent.com/mickleness/pumpernickel/master/pump-release/resources/explode-text-effect.gif"
 * alt="explode text effect demo">
 * </p>
 * <p>
 * The colors of the blocks are randomly generated within a certain HSB range.
 * The angles are distributed in an arc from left-to-right.
 * </p>
 */
public class ExplodeTextEffect implements TextEffect {
	int width, height;
	Font font;
	String text;
	Color foreground;

	List<BlockLetter> blockLetters = new ArrayList<BlockLetter>();
	float textWidth = 0;
	float textHeight = 0;

	public ExplodeTextEffect(Font font, String text, int width, int height,
			Color foreground) {
		Objects.requireNonNull(font);
		Objects.requireNonNull(text);
		Objects.requireNonNull(foreground);
		this.font = font;
		this.text = text;
		this.width = width;
		this.height = height;
		this.foreground = foreground;

		FontRenderContext frc = new FontRenderContext(new AffineTransform(),
				true, true);
		for (int a = 0; a < text.length(); a++) {
			char c = text.charAt(a);
			if (Character.isWhitespace(c) == false) {
				BlockLetter.Simple l = new BlockLetter.Simple(c, font,
						foreground);
				l.setBlockPaint(createShadow(a));
				l.put("x", new Float(textWidth));
				textHeight = Math.max(l.getDepth(), textHeight);
				blockLetters.add(l);
			}
			Rectangle2D r = font.getStringBounds(c + "", frc);
			textWidth += (float) r.getWidth();
		}
	}

	public Dimension getPreferredSize() {
		return new Dimension(width, height);
	}

	/** Create the block shadow color for a specific character index. */
	protected Color createShadow(int index) {
		Random r = new Random(1000 * index);
		float[] hsb = new float[3];
		while (true) {
			Color c = new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255));
			Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsb);
			if (hsb[1] > .6 && hsb[1] < .9 && hsb[2] > .5 && hsb[2] < .8) {
				return c;
			}
		}
	}

	public void paint(Graphics2D g, float fraction) {
		int mid = blockLetters.size() / 2;
		for (int a = 0; a < mid; a++) {
			process(g, a, fraction);
		}
		for (int a = blockLetters.size() - 1; a >= mid; a--) {
			process(g, a, fraction);
		}
	}

	private void process(Graphics2D g, int charIndex, float fraction) {
		BlockLetter l = (BlockLetter) blockLetters.get(charIndex);
		float x = ((Number) l.get("x")).floatValue();

		float xFraction = (x + l.getCharWidth() / 2) / textWidth;
		float localAngle = (float) (Math.PI / 8 + 6 * Math.PI / 8 * xFraction);
		l.setAngle(localAngle);

		float k1 = (float) (.5 - .5 * Math.cos(fraction * 2 * Math.PI));
		float doubleDip = (float) (.5 + .5 * Math.pow(
				Math.sin(fraction * Math.PI * 2), 2));
		l.setDepth((float) (k1 * 20 / Math.sin(localAngle) * doubleDip));

		l.paint(g, width / 2 - textWidth / 2 + x, height / 2 + textHeight / 2);
	}
}