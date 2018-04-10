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
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

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
	int width, height;
	Font font;
	String text;

	List<BlockLetter.Simple> blockLetters = new ArrayList<BlockLetter.Simple>();
	float textWidth = 0;
	float textHeight = 0;
	Color foreground = new Color(0, 230, 80);
	Color shadow = new Color(0, 150, 40);

	public WaveTextEffect(Font font, String text, int width, int height) {
		this.font = font;
		this.text = text;
		this.width = width;
		this.height = height;

		FontRenderContext frc = new FontRenderContext(new AffineTransform(),
				true, true);
		for (int a = 0; a < text.length(); a++) {
			char c = text.charAt(a);
			if (Character.isWhitespace(c) == false) {
				BlockLetter.Simple l = new BlockLetter.Simple(c, font,
						foreground) {
					@Override
					public void paintDepth(Graphics2D g, float x, float y) {
						Graphics2D g2 = prep(g, x, y);

						Shape[] shapes = getDepthBlocks();

						g2.setPaint(new Color(0, 0, 0, 30));
						g2.setStroke(new BasicStroke(1f,
								BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND));
						GeneralPath combo = new GeneralPath();
						combo.append(shapes[0], false);
						combo.append(shapes[1], false);
						g2.draw(combo);
					}

					@Override
					public void paintForeground(Graphics2D g, float x, float y) {
						super.paintForeground(g, x, y);
						paintOutline(g, x, y, new Color(0, 0, 0, 200),
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
						g2.setPaint(paint);
						g2.draw(outline);
						g2.dispose();
					}
				};
				l.setBlockPaint(shadow);
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