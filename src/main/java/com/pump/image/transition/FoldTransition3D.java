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
package com.pump.image.transition;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * This transition always folds the outgoing into two halves, and it may
 * optionally unfold the incoming image as it slides into place. Here are
 * playback samples:
 * <p>
 * <table summary="Sample Animations of FoldTransition3D" cellspacing="50"
 * border="0">
 * <tr>
 * <td align="center"><img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/FoldTransition3D/FoldLeft.gif"
 * alt="Fold Left">
 * <p>
 * Fold Left</td>
 * <td align="center"><img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/FoldTransition3D/FoldRight.gif"
 * alt="Fold Right">
 * <p>
 * Fold Right</td>
 * <td align="center"><img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/FoldTransition3D/FoldUp.gif"
 * alt="Fold Up">
 * <p>
 * Fold Up</td>
 * <td align="center"><img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/FoldTransition3D/FoldDown.gif"
 * alt="Fold Down">
 * <p>
 * Fold Down</td>
 * </tr>
 * <tr>
 * <td align="center"><img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/FoldTransition3D/FoldLeftTwice.gif"
 * alt="Fold Left Twice">
 * <p>
 * Fold Left Twice</td>
 * <td align="center"><img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/FoldTransition3D/FoldRightTwice.gif"
 * alt="Fold Right Twice">
 * <p>
 * Fold Right Twice</td>
 * <td align="center"><img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/FoldTransition3D/FoldUpTwice.gif"
 * alt="Fold Up Twice">
 * <p>
 * Fold Up Twice</td>
 * <td align="center"><img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/transition/FoldTransition3D/FoldDownTwice.gif"
 * alt="Fold Down Twice">
 * <p>
 * Fold Down Twice</td>
 * </tr>
 * </table>
 */
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

	/**
	 * Create a new FoldTransition3D.
	 * 
	 * @param direction
	 *            UP, DOWN, LEFT or RIGHT
	 * @param twice
	 *            if true then both the outgoing and incoming images are folded.
	 *            If false then only the outgoing image is folded.
	 */
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

		if (direction == LEFT) {
			double cursor = w * (1 - q);
			double imgA_width = w * (1 - q) + q * w / 3;

			for (int imageIndex : imageIndices) {
				if (imageIndex == 0) {
					paintHorizontalCreasedImage((Graphics2D) g.create(), frameA,
							cursor - imgA_width, imgA_width);
				} else if (imageIndex == 1) {
					if (twice) {
						double imgB_width = w * q + (1 - q) * w / 3;
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

			for (int imageIndex : imageIndices) {
				if (imageIndex == 0) {
					paintHorizontalCreasedImage((Graphics2D) g.create(), frameA,
							cursor, imgA_width);
				} else if (imageIndex == 1) {
					if (twice) {
						double imgB_width = w * q + (1 - q) * w / 3;
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
		} else if (direction == UP) {
			double cursor = h * (1 - q);
			double imgA_height = h * (1 - q) + q * h / 3;

			for (int imageIndex : imageIndices) {
				if (imageIndex == 0) {
					paintVerticalCreasedImage((Graphics2D) g.create(), frameA,
							cursor - imgA_height, imgA_height);
				} else if (imageIndex == 1) {
					if (twice) {
						double imgB_height = h * q + (1 - q) * h / 3;
						paintVerticalCreasedImage((Graphics2D) g.create(),
								frameB, cursor, imgB_height);
					} else {
						Graphics2D g2 = (Graphics2D) g.create();
						g2.translate(0, cursor);
						g2.drawImage(frameB, 0, 0, null);
						g2.dispose();
					}
				}
			}
		} else if (direction == DOWN) {
			double cursor = h * q;
			double imgA_height = h * (1 - q) + q * h / 3;

			for (int imageIndex : imageIndices) {
				if (imageIndex == 0) {
					paintVerticalCreasedImage((Graphics2D) g.create(), frameA,
							cursor, imgA_height);
				} else if (imageIndex == 1) {
					if (twice) {
						double imgB_height = h * q + (1 - q) * h / 3;
						paintVerticalCreasedImage((Graphics2D) g.create(),
								frameB, cursor - imgB_height, imgB_height);
					} else {
						Graphics2D g2 = (Graphics2D) g.create();
						g2.translate(0, cursor - h);
						g2.drawImage(frameB, 0, 0, null);
						g2.dispose();
					}
				}
			}
		}

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

		BufferedImage half1 = getSubimage(img, 0, 0, w / 2, h);
		BufferedImage half2 = getSubimage(img, w / 2, 0, w - w / 2, h);
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

			Color transparent = new Color(0, 0, 0, 0);
			Color shadow = new Color(0, 0, 0, (int) (250 * k));

			Paint p = null;
			if (j1 == null && j2 == null) {
				// do nothing
			} else if (j2 == null) {
				Rectangle2D r = j1.toShape().getBounds2D();
				p = new LinearGradientPaint((float) r.getMinX(),
						(float) r.getMinY(), (float) r.getMaxX(),
						(float) r.getMinY(), new float[] { 0, 1 },
						new Color[] { transparent, shadow });
			} else if (j1 == null) {
				Rectangle2D r = j2.toShape().getBounds2D();
				p = new LinearGradientPaint((float) r.getMinX(),
						(float) r.getMinY(), (float) r.getMaxX(),
						(float) r.getMinY(), new float[] { 0, 1 },
						new Color[] { shadow, transparent });
			} else {
				Rectangle2D r = j1.toShape().getBounds2D();
				r.add(j2.toShape().getBounds2D());

				float creaseAsFloat = (float) ((j1.topRight.getX()
						- j1.topLeft.getX()) / r.getWidth());
				creaseAsFloat = (float) Math
						.min(Math.max(creaseAsFloat, .0000001), .999999f);

				p = new LinearGradientPaint(j1.topLeft, j2.topRight,
						new float[] { 0, creaseAsFloat, 1 },
						new Color[] { transparent, shadow, transparent });
			}

			if (p != null) {
				Graphics2D scratchG = scratchImage.createGraphics();
				scratchG.setPaint(p);
				scratchG.setComposite(AlphaComposite.SrcAtop);
				scratchG.fillRect(0, 0, w, h);
				scratchG.dispose();
			}

			clearOutside(scratchImage, j1, j2);

			g.drawImage(scratchImage, 0, 0, null);
		} finally {
			releaseScratchImage(half1, half2, scratchImage);
		}
	}

	private void paintVerticalCreasedImage(Graphics2D g, BufferedImage img,
			double y, double collapsedHeight) {
		int h = img.getHeight();
		int w = img.getWidth();

		if (h == collapsedHeight) {
			g.translate(0, y);
			g.drawImage(img, 0, 0, null);
			return;
		}

		BufferedImage half1 = getSubimage(img, 0, 0, w, h / 2);
		BufferedImage half2 = getSubimage(img, 0, h / 2, w, h - h / 2);
		BufferedImage scratchImage = borrowScratchImage(w, h);

		try {
			double crease = y + collapsedHeight / 2.0;
			double k = 1 - collapsedHeight / w;

			// @formatter:off
			Quadrilateral3D q1 = new Quadrilateral3D(
					0, y, 0,
					w, y, 0,
					w, crease, -k * 100,
					0, crease, -k * 100
					);
			Quadrilateral3D q2 = new Quadrilateral3D(
					0, crease, -k * 100,
					w, crease, -k * 100,
					w, y + collapsedHeight, 0,
					0, y + collapsedHeight, 0 );
			// @formatter:on

			Quadrilateral2D j1 = paint(scratchImage, g.getRenderingHints(),
					half1, q1, true, true);
			Quadrilateral2D j2 = paint(scratchImage, g.getRenderingHints(),
					half2, q2, true, true);

			Color transparent = new Color(0, 0, 0, 0);
			Color shadow = new Color(0, 0, 0, (int) (250 * k));

			Paint p = null;
			if (j1 == null && j2 == null) {
				// do nothing
			} else if (j2 == null) {
				Rectangle2D r = j1.toShape().getBounds2D();
				p = new LinearGradientPaint((float) r.getMinX(),
						(float) r.getMinY(), (float) r.getMinX(),
						(float) r.getMaxY(), new float[] { 0, 1 },
						new Color[] { transparent, shadow });
			} else if (j1 == null) {
				Rectangle2D r = j2.toShape().getBounds2D();
				p = new LinearGradientPaint((float) r.getMinX(),
						(float) r.getMinY(), (float) r.getMinX(),
						(float) r.getMaxY(), new float[] { 0, 1 },
						new Color[] { shadow, transparent });
			} else {
				Rectangle2D r = j1.toShape().getBounds2D();
				r.add(j2.toShape().getBounds2D());

				float creaseAsFloat = (float) ((j1.bottomRight.getY()
						- j1.topRight.getY()) / r.getHeight());
				creaseAsFloat = (float) Math
						.min(Math.max(creaseAsFloat, .0000001), .999999f);

				p = new LinearGradientPaint(j1.topLeft, j2.bottomLeft,
						new float[] { 0, creaseAsFloat, 1 },
						new Color[] { transparent, shadow, transparent });
			}

			if (p != null) {
				Graphics2D scratchG = scratchImage.createGraphics();
				scratchG.setPaint(p);
				scratchG.setComposite(AlphaComposite.SrcAtop);
				scratchG.fillRect(0, 0, w, h);
				scratchG.dispose();
			}

			clearOutside(scratchImage, j1, j2);

			g.drawImage(scratchImage, 0, 0, null);
		} finally {
			releaseScratchImage(half1, half2, scratchImage);
		}
	}

	private BufferedImage getSubimage(BufferedImage img, int x, int y, int w,
			int h) {
		// TODO: I'd love to refactor this away someday, but right now the log
		// we use to render 3D images doesn't appear to support subimages.
		BufferedImage src = img.getSubimage(x, y, w, h);
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