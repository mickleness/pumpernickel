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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This ripples across the text (from-left-to-right) using a transparent
 * stencil-like appearance:
 * <p>
 * <img src=
 * "https://raw.githubusercontent.com/mickleness/pumpernickel/master/pump-release/resources/wave-text-effect.gif"
 * alt="demo of the WaveTextEffect" >
 * </p>
 */
public class WaveTextEffect implements TextEffect {
	public static final Color DEFAULT_FOREGROUND = new Color(0, 230, 80);
	public static final Color DEFAULT_SHADOW = new Color(0, 150, 40);

	int width, height;
	Font font;
	String text;

	List<BlockLetter.Simple> blockLetters = new ArrayList<BlockLetter.Simple>();
	float textWidth = 0;
	float textHeight = 0;
	Color foreground;
	Color stroke;

	public WaveTextEffect(Font font, String text, int width, int height) {
		this(font, text, width, height, DEFAULT_FOREGROUND, DEFAULT_SHADOW);
	}

	public WaveTextEffect(Font font, String text, int width, int height,
			Color foreground, Color stroke) {
		Objects.requireNonNull(font);
		Objects.requireNonNull(text);
		Objects.requireNonNull(foreground);
		Objects.requireNonNull(stroke);
		this.font = font;
		this.text = text;
		this.width = width;
		this.height = height;
		this.foreground = foreground;
		this.stroke = stroke;

		FontRenderContext frc = new FontRenderContext(new AffineTransform(),
				true, true);
		for (int a = 0; a < text.length(); a++) {
			char c = text.charAt(a);
			if (Character.isWhitespace(c) == false) {
				BlockLetter.Simple l = new BlockLetter.Simple(c, font,
						foreground) {
					@Override
					public void paintDepth(Graphics2D g, float x, float y) {
					}

					@Override
					public void paintForeground(Graphics2D g, float x, float y) {
						Graphics2D g2 = prep(g, x, y);
						g2.scale(1 + depth / 50f, 1 + depth / 50f);
						g2.setPaint(foreground);
						g2.fill(outline);
						g2.dispose();

						paintOutline(g, x, y, WaveTextEffect.this.stroke,
								new BasicStroke(1f, BasicStroke.CAP_SQUARE,
										BasicStroke.JOIN_ROUND));
					}

					protected void paintOutline(Graphics2D g, float x, float y,
							Paint paint, Stroke stroke) {
						Graphics2D g2 = (Graphics2D) g.create();
						g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
								RenderingHints.VALUE_ANTIALIAS_ON);
						g2.translate(x - depth * Math.cos(angle), y - depth
								* Math.sin(angle));
						g2.setStroke(stroke);
						g2.scale(1 + depth / 50f, 1 + depth / 50f);
						g2.setPaint(paint);
						g2.draw(outline);
						g2.dispose();
					}
				};
				l.setBlockPaint(new Color(0, 0, 0, 0));
				l.put("x", new Float(textWidth));
				textHeight = Math.max(l.getDepth(), textHeight);
				blockLetters.add(l);
			}
			Rectangle2D r = font.getStringBounds(c + "", frc);
			textWidth += (float) r.getWidth();
		}

	}

	public void paint(Graphics2D g, float fraction) {
		for (int a = 0; a < blockLetters.size(); a++) {
			BlockLetter.Simple l = blockLetters.get(a);
			l.setBlockPaint(new Color(0, 255, 0, 50), new Color(0, 255, 0, 50));
			float x = ((Number) l.get("x")).floatValue();

			float xFraction = x / textWidth;
			float k1 = (float) (.5 - .5 * Math.cos(fraction * 2 * Math.PI));
			k1 = (float) (Math.pow(k1, .3f));
			float k2 = (1 - xFraction) * (1 - fraction) + xFraction * fraction;
			l.setDepth(k1 * 68 * k2 * (1 - fraction));

			l.paint(g, width / 2 - textWidth / 2 + x, height / 2 + textHeight
					/ 2);
		}
	}

	public Dimension getPreferredSize() {
		return new Dimension(width, height);
	}
}