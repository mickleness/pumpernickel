package com.pump.image.transition;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.image.BufferedImage;

public class FoldTransition3D extends Transition3D {

	/**
	 * @return the transitions that should be used to demonstrate this
	 *         transition.
	 */
	public static Transition[] getDemoTransitions() {
		return new Transition[] { new FoldTransition3D(UP),
				new FoldTransition3D(DOWN), new FoldTransition3D(LEFT),
				new FoldTransition3D(RIGHT), };
	}

	int direction;

	public FoldTransition3D(int direction) {
		if (!(direction == Transition.UP || direction == Transition.DOWN
				|| direction == Transition.LEFT
				|| direction == Transition.RIGHT)) {
			throw new IllegalArgumentException(
					"direction must be UP, DOWN, LEFT or RIGHT");
		}
		this.direction = direction;
	}

	@Override
	protected void doPaint(Graphics2D g, BufferedImage frameA,
			BufferedImage frameB, float progress) {
		int h = frameA.getHeight();
		int w = frameA.getWidth();

		BufferedImage scratchImage = new BufferedImage(w, h,
				BufferedImage.TYPE_INT_ARGB);

		switch (direction) {
		default:
			// TODO: make context support subimages w/o copying
			BufferedImage half1 = copy(frameA.getSubimage(0, 0, w / 2, h));
			BufferedImage half2 = copy(
					frameA.getSubimage(w / 2, 0, w - w / 2, h));

			progress = (float) Math.pow(progress, 2);

			double cursor = w * (1 - progress);
			double imgA_width = cursor + progress * w / 4;
			double imgA_leftEdge = cursor - imgA_width;

			double creaseX = cursor - imgA_width / 2;

			// @formatter:off
			Quadrilateral3D q1 = new Quadrilateral3D(
					imgA_leftEdge,0,0,
					creaseX, 0, -progress * 100,
					creaseX, h, -progress * 100,
					imgA_leftEdge, h, 0
					);
			Quadrilateral3D q2 = new Quadrilateral3D(
					creaseX,0, -progress * 100,
					cursor, 0, 0,
					cursor, h, 0,
					creaseX, h, -progress * 100
					);
			// @formatter:on

			paint(g, w, h, half1, q1, true);
			paint(g, w, h, half2, q2, true);
			g.translate(cursor, 0);
			g.drawImage(frameB, 0, 0, null);
		}
	}

	protected Quadrilateral2D paint(Graphics2D g, int width, int height,
			BufferedImage img, Quadrilateral3D quad3D,
			boolean skipIfFlippedHorizontally) {
		Quadrilateral2D q = super.paint(g, width, height, img, quad3D,
				skipIfFlippedHorizontally);
		if (q != null) {
			Paint p = null;
			if (quad3D.topLeft.getZ() < quad3D.topRight.getZ()) {
				double j = -quad3D.topLeft.getZ() + quad3D.topRight.getZ();
				int alpha = (int) (255 * j / 100);
				p = new GradientPaint((float) q.topLeft.getX(), 0,
						new Color(0, 0, 0, alpha / 4),
						(float) q.topRight.getX(), 0, new Color(0, 0, 0, 0));
			} else if (quad3D.topLeft.getZ() > quad3D.topRight.getZ()) {
				// make this side slightly darker, because in UI's we usually
				// imagine a light source from the upper-left
				double j = quad3D.topLeft.getZ() - quad3D.topRight.getZ();
				int alpha = (int) (255 * j / 100);
				p = new GradientPaint((float) q.topRight.getX(), 0,
						new Color(0, 0, 0, alpha / 3), (float) q.topLeft.getX(),
						0, new Color(0, 0, 0, 0));
			}
			if (p != null) {
				g.setPaint(p);
				g.fill(q.toShape());
			}
		}
		return q;
	}

	private static BufferedImage copy(BufferedImage src) {
		BufferedImage d = new BufferedImage(src.getWidth(), src.getHeight(),
				src.getType());
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
		return sb.toString();
	}

}
