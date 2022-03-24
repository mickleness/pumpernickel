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
package com.pump.showcase.demo;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;

import com.pump.image.pixel.Scaling;

/**
 * This relates to issue #82, and should either be deleted or fully integrated
 * into the format ScalingDemo on completion.
 */
public class ScalingDemo2 {

	interface Model {
		void scale(BufferedImage source, BufferedImage dest);
	}

	static class ScalingIteratorModel implements Model {

		@Override
		public void scale(BufferedImage source, BufferedImage dest) {
			Scaling.scale(source, dest);
		}

	}

	static class InterpolationModel implements Model {

		Object interpolationHint;

		protected InterpolationModel(Object interpolationHint) {
			this.interpolationHint = interpolationHint;
		}

		@Override
		public void scale(BufferedImage source, BufferedImage dest) {
			Graphics2D g = dest.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					interpolationHint);

			double sx = ((double) dest.getWidth())
					/ ((double) source.getWidth());
			double sy = ((double) dest.getHeight())
					/ ((double) source.getHeight());

			g.scale(sx, sy);
			g.drawImage(source, 0, 0, null);
			g.dispose();
		}

	}

	static class BicubicInterpolationModel extends InterpolationModel {

		protected BicubicInterpolationModel() {
			super(RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		}

	}

	static class BilinearInterpolationModel extends InterpolationModel {

		protected BilinearInterpolationModel() {
			super(RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		}

	}

	static class NearestNeighborInterpolationModel extends InterpolationModel {

		protected NearestNeighborInterpolationModel() {
			super(RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		}

	}

	public static void main(String[] args) throws IOException {
		BufferedImage docIcon = createDocumentIcon();
		writeFile("src_image", docIcon);

		Model[] models = new Model[] { new NearestNeighborInterpolationModel(),
				new BilinearInterpolationModel(),
				new BicubicInterpolationModel(), new ScalingIteratorModel() };

		StringBuilder sb = new StringBuilder("Size\t");
		for (Model model : models) {
			sb.append(model.getClass().getSimpleName() + "\t");
		}
		System.out.println(sb.toString());
		for (int size = 16; size <= 48; size++) {
			sb = new StringBuilder(size + "\t");
			BufferedImage dst = new BufferedImage(size, size,
					docIcon.getType());
			for (Model model : models) {
				long[] samples = new long[10];
				for (int a = 0; a < samples.length; a++) {
					Graphics2D g = dst.createGraphics();
					g.setComposite(AlphaComposite.Clear);
					g.fillRect(0, 0, dst.getWidth(), dst.getHeight());
					g.dispose();

					samples[a] = System.currentTimeMillis();
					for (int z = 0; z < 1000; z++) {
						model.scale(docIcon, dst);
					}
					samples[a] = System.currentTimeMillis() - samples[a];
				}
				Arrays.sort(samples);
				sb.append(samples[samples.length / 2] + "\t");
				writeFile(size + "-" + model.getClass().getSimpleName(), dst);
			}
			System.out.println(sb.toString());
		}
	}

	private static void writeFile(String name, BufferedImage image)
			throws IOException {
		File file = new File(name + ".png");
		ImageIO.write(image, "png", file);
	}

	private static BufferedImage createDocumentIcon() {
		BufferedImage bi = new BufferedImage(16, 16,
				BufferedImage.TYPE_INT_ARGB);
		Path2D outline = new Path2D.Double();
		outline.moveTo(2, 0);
		outline.lineTo(15 - 2 - 5, 0);
		outline.lineTo(15 - 2, 5);
		outline.lineTo(15 - 2, 15);
		outline.lineTo(15 - 2, 15);
		outline.lineTo(2, 15);
		outline.closePath();

		Path2D crease = new Path2D.Double();
		crease.moveTo(15 - 2 - 5, 0);
		crease.lineTo(15 - 2 - 5, 5);
		crease.lineTo(15 - 2, 5);

		Graphics2D g = bi.createGraphics();
		g.setColor(Color.white);
		g.fill(outline);
		g.setColor(Color.black);
		g.setStroke(new BasicStroke(1));
		g.draw(outline);

		g.setColor(new Color(0, 0, 0, 128));
		g.draw(crease);
		g.dispose();
		return bi;
	}
}