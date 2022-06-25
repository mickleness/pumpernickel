package com.pump.image.transition;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public class FoldTransition3D extends Transition3D {

	/**
	 * @return the transitions that should be used to demonstrate this
	 *         transition.
	 */
	public static Transition[] getDemoTransitions() {
		return new Transition[] { new FoldTransition3D(UP, false),
				new FoldTransition3D(DOWN, false),
				new FoldTransition3D(LEFT, false),
				new FoldTransition3D(RIGHT, false),
				new FoldTransition3D(UP, true),
				new FoldTransition3D(DOWN, true),
				new FoldTransition3D(LEFT, true),
				new FoldTransition3D(RIGHT, true) };
	}

	int direction;
	boolean twice;

	public FoldTransition3D(int direction, boolean twice) {
		if (!(direction == Transition.UP || direction == Transition.DOWN
				|| direction == Transition.LEFT
				|| direction == Transition.RIGHT)) {
			throw new IllegalArgumentException(
					"direction must be UP, DOWN, LEFT or RIGHT");
		}
		this.twice = twice;
		this.direction = direction;
	}

	@Override
	protected void doPaint(Graphics2D g, BufferedImage frameA,
			BufferedImage frameB, float progress) {
		int h = frameA.getHeight();
		int w = frameA.getWidth();

		int[] imageIndices = progress < .5 ? new int[] { 1, 0 }
				: new int[] { 0, 1 };

		double q = .5 * Math.sin(Math.PI * progress - Math.PI / 2.0) + .5;
		q = .5 * Math.sin(Math.PI * q - Math.PI / 2.0) + .5;

		// TODO: implement other directions
		if (direction == LEFT) {
			double cursor = w * (1 - q);
			double imgA_width = w * (1 - q) + q * w / 3;
			double imgB_width = w * q + (1 - q) * w / 3;

			for (int imageIndex : imageIndices) {
				if (imageIndex == 0) {
					paintHorizontalCreasedImage((Graphics2D) g.create(), frameA,
							cursor - imgA_width, imgA_width);
				} else if (imageIndex == 1) {
					if (twice) {
						paintHorizontalCreasedImage((Graphics2D) g.create(),
								frameB, cursor, imgB_width);
					} else {
						Graphics2D g2 = (Graphics2D) g.create();
						g2.translate(cursor, 0);
						g2.drawImage(frameB, 0, 0, null);
						g2.dispose();
					}
				}
			}
		} else if (direction == RIGHT) {
			double cursor = w * q;
			double imgA_width = w * (1 - q) + q * w / 3;
			double imgB_width = w * q + (1 - q) * w / 3;

			for (int imageIndex : imageIndices) {
				if (imageIndex == 0) {
					paintHorizontalCreasedImage((Graphics2D) g.create(), frameA,
							cursor, imgA_width);
				} else if (imageIndex == 1) {
					if (twice) {
						paintHorizontalCreasedImage((Graphics2D) g.create(),
								frameB, cursor - imgB_width, imgB_width);
					} else {
						Graphics2D g2 = (Graphics2D) g.create();
						g2.translate(cursor - w, 0);
						g2.drawImage(frameB, 0, 0, null);
						g2.dispose();
					}
				}
			}
		}

		// TODO: resolve antialias edges
		// TODO: apply similar fixes to other 3D transitions
		// TODO: add background color well to demo inspector? remove background
		// background color from other transitions?
		// TODO: add some sort of page turn transition
	}

	private void paintHorizontalCreasedImage(Graphics2D g, BufferedImage img,
			double x, double collapsedWidth) {
		int h = img.getHeight();
		int w = img.getWidth();

		if (w == collapsedWidth) {
			g.translate(x, 0);
			g.drawImage(img, 0, 0, null);
			return;
		}

		// TODO: make context support subimages w/o copying
		BufferedImage half1 = copy(img.getSubimage(0, 0, w / 2, h));
		BufferedImage half2 = copy(img.getSubimage(w / 2, 0, w - w / 2, h));
		BufferedImage scratchImage = borrowScratchImage(w, h);

		try {
			double crease = x + collapsedWidth / 2.0;
			double k = 1 - collapsedWidth / h;

			// @formatter:off
			Quadrilateral3D q1 = new Quadrilateral3D(
					x,0,0,
					crease, 0, -k * 100,
					crease, h, -k * 100,
					x, h, 0
					);
			Quadrilateral3D q2 = new Quadrilateral3D(
					crease,0, -k * 100,
					x + collapsedWidth, 0, 0,
					x + collapsedWidth, h, 0,
					crease, h, -k * 100
					);
			// @formatter:on

			Quadrilateral2D j1 = paint(scratchImage, g.getRenderingHints(),
					half1, q1, true, true);
			Quadrilateral2D j2 = paint(scratchImage, g.getRenderingHints(),
					half2, q2, true, true);

			Color transBlack = new Color(0, 0, 0, 0);
			Color lightBlack = new Color(0, 0, 0, (int) (250 * k));

			Paint p = null;
			if (j1 == null && j2 == null) {
				// do nothing
			} else if (j2 == null) {
				p = new LinearGradientPaint(j1.topLeft, j1.topRight,
						new float[] { 0, 1 },
						new Color[] { transBlack, lightBlack });
			} else if (j1 == null) {
				p = new LinearGradientPaint(j2.topLeft, j2.topRight,
						new float[] { 0, 1 },
						new Color[] { transBlack, lightBlack });
			} else {
				Rectangle2D r = j1.toShape().getBounds2D();
				r.add(j2.toShape().getBounds2D());

				float creaseAsFloat = (float) ((j1.topRight.getX()
						- j1.topLeft.getX()) / r.getWidth());
				creaseAsFloat = (float) Math
						.min(Math.max(creaseAsFloat, .0000001), .999999f);

				p = new LinearGradientPaint(j1.topLeft, j2.topRight,
						new float[] { 0, creaseAsFloat, 1 },
						new Color[] { transBlack, lightBlack, transBlack });
			}

			if (p != null) {
				Graphics2D scratchG = scratchImage.createGraphics();
				scratchG.setPaint(p);
				scratchG.setComposite(AlphaComposite.SrcAtop);
				scratchG.fillRect(0, 0, w, h);
				scratchG.dispose();
			}

			clearOutside(scratchImage, j1 == null ? null : j1.toShape(),
					j2 == null ? null : j2.toShape());

			g.drawImage(scratchImage, 0, 0, null);
		} finally {
			releaseScratchImage(half1, half2, scratchImage);
		}

	}

	private BufferedImage copy(BufferedImage src) {
		BufferedImage d = borrowScratchImage(src.getWidth(), src.getHeight());
		Graphics2D g = d.createGraphics();
		g.drawImage(src, 0, 0, null);
		g.dispose();
		return d;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("Fold ");
		if (direction == Transition.UP) {
			sb.append("Up");
		} else if (direction == Transition.DOWN) {
			sb.append("Down");
		} else if (direction == Transition.LEFT) {
			sb.append("Left");
		} else {
			sb.append("Right");
		}
		if (twice) {
			sb.append(" Twice");
		}
		return sb.toString();
	}

}
