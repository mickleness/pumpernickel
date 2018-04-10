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
package com.pump.plaf;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.Timer;
import javax.swing.plaf.ComponentUI;

import com.pump.swing.ColorPalette;

public class FadingColorPaletteUI extends ColorPaletteUI {

	/** This client property maps to the ColorSet currently being rendered. */
	protected static final String PALETTE_COLOR_SET = "paletteColorSet";

	static final ColorSet DEFAULT_COLORS = new DefaultColors(true);

	public static ComponentUI createUI(JComponent jc) {
		return new FadingColorPaletteUI();
	}

	public static final int EFFECT_FADE = 0;
	public static final int EFFECT_SLIDE_UP = 1;
	public static final int EFFECT_SLIDE_DOWN = 2;
	public static final int EFFECT_SLIDE_LEFT = 3;
	public static final int EFFECT_SLIDE_RIGHT = 4;

	static PropertyChangeListener colorSetListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			ColorPalette cp = (ColorPalette) evt.getSource();
			ColorSetFader fader = (ColorSetFader) cp
					.getClientProperty("colorSetFader");
			if (fader == null) {
				fader = new ColorSetFader(cp);
				cp.putClientProperty("colorSetFader", fader);
			}
			ColorSet newColors = (ColorSet) evt.getNewValue();
			if (fader.endsWith(newColors) == false)
				fader.addColorSet(newColors, EFFECT_FADE);
			cp.repaint();
		}
	};

	@Override
	protected void processKeyEvent(KeyEvent keyEvent, int dx, int dy) {
		ColorPalette cp = (ColorPalette) keyEvent.getSource();
		if (isAnimating(cp))
			return;
		super.processKeyEvent(keyEvent, dx, dy);
	}

	public boolean isAnimating(JComponent jc) {
		ColorSetFader fader = (ColorSetFader) jc
				.getClientProperty("colorSetFader");
		if (fader == null)
			return false;
		return fader.isRunning();
	}

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);
		c.addPropertyChangeListener(PALETTE_COLOR_SET, colorSetListener);
	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);
		c.removePropertyChangeListener(PALETTE_COLOR_SET, colorSetListener);
	}

	@Override
	public void paint(Graphics g0, JComponent c) {
		Graphics2D g = (Graphics2D) g0;

		ColorPalette cp = (ColorPalette) c;

		ColorSetFader fader = (ColorSetFader) cp
				.getClientProperty("colorSetFader");
		if (fader != null) {

			if (cp.getBorder() != null) {
				Insets borderInsets = cp.getBorder().getBorderInsets(cp);
				g0.setColor(cp.getForeground());
				g0.fillRect(borderInsets.left, borderInsets.top, cp.getWidth()
						- borderInsets.left - borderInsets.right,
						cp.getHeight() - borderInsets.top - borderInsets.bottom);
			}

			Rectangle r = getImageBounds(cp);
			TexturePaint checkerboard = PlafPaintUtils.getCheckerBoard(10);
			g.setPaint(checkerboard);
			g.fillRect(r.x, r.y, r.width, r.height);

			fader.paint(g);
		} else {
			super.paint(g, cp);
		}
	}

	@Override
	protected ColorSet getColorSet(ColorPalette cp) {
		ColorSet colorSet = (ColorSet) cp.getClientProperty(PALETTE_COLOR_SET);
		if (colorSet == null)
			return DEFAULT_COLORS;
		return colorSet;
	}

	protected void setColors(ColorPalette cp, ColorSet newColors,
			int effectStyle) {
		if (newColors == null)
			throw new NullPointerException();

		cp.putClientProperty(PALETTE_COLOR_SET, newColors);
		cp.putClientProperty(RELATIVE_POINT_PROPERTY, null);
	}

	static final ActionListener faderListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			ColorSetFader fader = (ColorSetFader) e.getSource();
			fader.iterate();
		}
	};

	static class ColorSetFader extends Timer {
		private static final long serialVersionUID = 1L;

		ColorPalette cp;
		List<ColorSetLayer> layers = new ArrayList<ColorSetLayer>();

		public ColorSetFader(ColorPalette cp) {
			super(25, FadingColorPaletteUI.faderListener);
			this.cp = cp;
		}

		public ColorSetLayer getLayer(ColorSet colors) {
			for (int a = 0; a < layers.size(); a++) {
				ColorSetLayer layer = layers.get(a);
				if (layer.colors.equals(colors))
					return layer;
			}
			return null;
		}

		Number DEFAULT_DURATION = new Float(500);

		public void iterate() {
			int a = layers.size() - 1;
			long time = System.currentTimeMillis();
			while (a >= 0) {
				ColorSetLayer layer = layers.get(a);
				long elapsed = time - layer.startTime;
				Number duration = (Number) cp
						.getClientProperty("animationDuration");
				if (duration == null)
					duration = DEFAULT_DURATION;
				layer.progress = (elapsed) / duration.floatValue();
				if (layer.progress >= 1) {
					layer.progress = 1;
					while (a - 1 >= 0) {
						ColorSetLayer unneededLayer = layers.get(a - 1);
						unneededLayer.colors.flush();
						layers.remove(a - 1);
						a--;
					}
				}
				a--;
			}

			if (layers.size() == 1) {
				stop();
			}
			cp.repaint();
		}

		public void addColorSet(ColorSet set, int effectStyle) {
			ColorSetLayer newLayer = new ColorSetLayer(set, effectStyle);
			layers.add(newLayer);

			Number duration = (Number) cp
					.getClientProperty("animationDuration");
			if (duration == null)
				duration = DEFAULT_DURATION;

			if (duration.intValue() <= 1) {
				while (layers.size() > 1) {
					layers.remove(0);
				}
				// when we start out: don't fade!
				newLayer.progress = 1;
			} else if (layers.size() == 1) {
				// when we start out: don't fade!
				newLayer.progress = 1;
			} else {
				start();
			}
		}

		public void paint(Graphics2D g) {
			ColorPaletteUI ui = cp.getUI();
			Rectangle imageBounds = ui.getImageBounds(cp);

			g.clipRect(imageBounds.x, imageBounds.y, imageBounds.width,
					imageBounds.height);

			for (int a = 0; a < layers.size(); a++) {
				Graphics2D g2 = (Graphics2D) g.create();
				ColorSetLayer layer = layers.get(a);
				if (layer.effectStyle == EFFECT_SLIDE_DOWN) {
					int dy = (int) ((1 - layer.progress) * cp.getHeight());
					g2.translate(0, dy);
				} else if (layer.effectStyle == EFFECT_SLIDE_UP) {
					int dy = (int) ((1 - layer.progress) * cp.getHeight());
					g2.translate(0, -dy);
				} else if (layer.effectStyle == EFFECT_SLIDE_RIGHT) {
					int dx = (int) ((1 - layer.progress) * cp.getWidth());
					g2.translate(dx, 0);
				} else if (layer.effectStyle == EFFECT_SLIDE_LEFT) {
					int dx = (int) ((1 - layer.progress) * cp.getWidth());
					g2.translate(-dx, 0);
				} else {
					g2.setComposite(AlphaComposite.getInstance(
							AlphaComposite.SRC_OVER, layer.progress));
				}
				g2.drawImage(layer.colors.getImage(imageBounds.width,
						imageBounds.height, cp), imageBounds.x, imageBounds.y,
						null);
				g2.dispose();

				if (a == layers.size() - 1) {
					int rgb = (cp).getColor().getRGB();
					Point2D p = (Point2D) cp
							.getClientProperty(RELATIVE_POINT_PROPERTY);

					if (p != null) {
						int rgb2 = layer.colors.getRGB((float) p.getX(),
								(float) p.getY());
						if (rgb2 != rgb) {
							p = null;
						}
					}

					if (p == null) {
						p = layer.colors.getRelativePoint(rgb);

						int rgb2 = layer.colors.getRGB((float) p.getX(),
								(float) p.getY());
						if (getRGBDistanceSquared(rgb, rgb2) > 3 * 4) {
							// This is unlikely *if the user picked the current
							// color from this ColorSet*
							// but if the Color set set externally: this might
							// often happen.
							p = null;
						} else {
							cp.putClientProperty(RELATIVE_POINT_PROPERTY, p);
						}
					}

					if (p != null && p.getX() >= 0 && p.getY() >= 0
							&& p.getX() <= 1 && p.getY() <= 1) {

						Graphics2D g3 = (Graphics2D) g.create();
						g3.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
								RenderingHints.VALUE_ANTIALIAS_ON);
						g3.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
								RenderingHints.VALUE_STROKE_PURE);
						Ellipse2D circle = new Ellipse2D.Float(
								(float) (p.getX() * imageBounds.width - 3 + imageBounds.x),
								(float) (p.getY() * imageBounds.height
										+ imageBounds.y - 3), 6, 6);
						g3.setColor(Color.white);
						g3.draw(circle);
						circle.setFrame((float) (p.getX() * imageBounds.width
								- 4 + imageBounds.x), (float) (p.getY()
								* imageBounds.height + imageBounds.y - 4), 8, 8);
						g3.setColor(Color.black);
						g3.draw(circle);
						g3.dispose();
					}
				}
			}
		}

		public boolean endsWith(ColorSet set) {
			if (layers.size() == 0)
				return false;
			ColorSetLayer top = layers.get(layers.size() - 1);
			if (top.colors == set)
				return true;
			return false;
		}
	}

	static class ColorSetLayer {
		ColorSet colors;
		float progress = 0;
		int effectStyle;
		long startTime = System.currentTimeMillis();

		public ColorSetLayer(ColorSet colors, int effectStyle) {
			this.colors = colors;
			this.effectStyle = effectStyle;
		}
	}
}