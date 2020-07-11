package com.pump.image.shadow;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import javax.imageio.ImageIO;

import com.pump.image.MutableBufferedImage;

import junit.framework.TestCase;

public class ShadowRendererTest extends TestCase {
	static final String PROPERTY_NAME = "name";

	static class ResultSet {
		TreeSet<Result> all = new TreeSet<>();
		TreeSet<Result> ones = new TreeSet<>();
		TreeSet<Result> twos = new TreeSet<>();
		TreeSet<Result> threes = new TreeSet<>();

		public void add(Result r) {
			int z = 0;
			if (r.j1 != 0)
				z++;
			if (r.j2 != 0)
				z++;
			if (r.j3 != 0)
				z++;
			if (z == 3)
				threes.add(r);
			if (z == 2)
				twos.add(r);
			if (z == 1)
				ones.add(r);
			all.add(r);

			trim(all);
			trim(ones);
			trim(twos);
			trim(threes);
		}

		private void trim(TreeSet<Result> set) {
			int size = set.size();
			Iterator<Result> iter = set.descendingIterator();
			while (size > 10) {
				Result r = iter.next();
				iter.remove();
				size--;
			}
		}

		public void output(String str) throws Exception {
			Iterator<Result> iter = all.iterator();
			Collection<Collection<Integer>> combos = new HashSet<>();
			int fileCtr = 0;
			while (fileCtr < 5 && iter.hasNext()) {
				Result result = iter.next();
				List<Integer> z = new ArrayList<>();
				if (result.j1 != 0)
					z.add(result.j1);
				if (result.j2 != 0)
					z.add(result.j2);
				if (result.j3 != 0)
					z.add(result.j3);

				Collections.sort(z);
				if (combos.add(z)) {
					writePNG(result.image, str + result.name);
					fileCtr++;
				}
			}
		}
	}

	static class Result implements Comparable<Result> {
		int j1, j2, j3;
		String name;
		long error;
		BufferedImage image;

		public Result(String name, long error, BufferedImage image, int j1,
				int j2, int j3) {
			this.name = name;
			this.error = error;
			this.image = image;
			this.j1 = j1;
			this.j2 = j2;
			this.j3 = j3;
		}

		@Override
		public int compareTo(Result o) {
			int k = Long.compare(error, o.error);
			if (k != 0)
				return k;
			return name.compareTo(o.name);
		}

	}

	public void testComposite() throws Exception {
		if (true)
			return;

		ShadowRenderer gaussianRenderer = new GaussianShadowRenderer();
		ShadowRenderer fastRenderer = new FastShadowRenderer();
		List<MutableBufferedImage> images = getImages();
		for (MutableBufferedImage image : images) {
			String name = (String) image.getProperty(PROPERTY_NAME);

			for (int k = 1; k < 50; k++) {
				ShadowAttributes attr = new ShadowAttributes(k, 1);
				BufferedImage gaussianImage = gaussianRenderer
						.createShadow(image, attr);
				writePNG(gaussianImage, name + "-" + k);

				ResultSet results = new ResultSet();
				for (int j1 = 0; j1 <= k / 2 + 1; j1++) {
					BufferedImage t1 = image;
					if (j1 != 0) {
						attr = new ShadowAttributes(j1, 1);
						t1 = fastRenderer.createShadow(image, attr);
					}

					for (int j2 = 0; j2 <= k / 2 + 1; j2++) {
						BufferedImage t2 = t1;
						if (j2 != 0) {
							attr = new ShadowAttributes(j2, 1);
							t2 = fastRenderer.createShadow(t2, attr);
						}

						for (int j3 = 0; j3 <= 0; j3++) {
							BufferedImage t3 = t2;

							if (j3 != 0) {
								attr = new ShadowAttributes(j3, 1);
								t3 = fastRenderer.createShadow(t3, attr);
							}

							Result result = compare(j1 + "-" + j2 + "-" + j3,
									gaussianImage, t3, j1, j2, j3);

							results.add(result);
						}
					}
				}
				results.output(name + "-" + k + "-");
			}
		}
	}

	static RenderingHints qualityHints = new RenderingHints(new HashMap<>());
	{
		qualityHints.put(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		qualityHints.put(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		qualityHints.put(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);
	}

	private Result compare(String name, BufferedImage gaussianImage,
			BufferedImage fastImage, int j1, int j2, int j3) {
		int maxWidth = Math.max(gaussianImage.getWidth(), fastImage.getWidth());
		int maxHeight = Math.max(gaussianImage.getHeight(),
				fastImage.getHeight());

		BufferedImage bi1 = new BufferedImage(maxWidth, maxHeight,
				BufferedImage.TYPE_INT_ARGB);
		BufferedImage bi2 = new BufferedImage(maxWidth, maxHeight,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g1 = bi1.createGraphics();
		Graphics2D g2 = bi2.createGraphics();
		g1.setRenderingHints(qualityHints);
		g2.setRenderingHints(qualityHints);
		g1.translate(maxWidth / 2f - gaussianImage.getWidth() / 2f,
				maxHeight / 2f - gaussianImage.getHeight() / 2f);
		g2.translate(maxWidth / 2f - fastImage.getWidth() / 2f,
				maxHeight / 2f - fastImage.getHeight() / 2f);
		g1.drawImage(gaussianImage, 0, 0, null);
		g2.drawImage(fastImage, 0, 0, null);
		g1.dispose();
		g2.dispose();

		long error = getError(bi1, bi2);

		return new Result(name, error, fastImage, j1, j2, j3);
	}

	private long getError(BufferedImage bi1, BufferedImage bi2) {
		long sum = 0;
		int[] row1 = new int[bi1.getWidth()];
		int[] row2 = new int[bi1.getWidth()];
		for (int y = 0; y < bi1.getHeight(); y++) {
			bi1.getRaster().getDataElements(0, y, row1.length, 1, row1);
			bi2.getRaster().getDataElements(0, y, row2.length, 1, row2);
			for (int x = 0; x < row1.length; x++) {
				int argb1 = row1[x];
				int argb2 = row2[x];
				int alpha1 = (argb1 >> 24) & 0xff;
				int alpha2 = (argb2 >> 24) & 0xff;
				int k = alpha1 - alpha2;
				sum += k * k;
			}
		}
		return sum;
	}

	private static File writePNG(BufferedImage img, String name)
			throws Exception {
		File file = new File(name + ".png");
		System.out.println(file);
		ImageIO.write(img, "png", file);
		return file;
	}

	public static List<MutableBufferedImage> getImages() {
		List<MutableBufferedImage> returnValue = new ArrayList<>();

		MutableBufferedImage square = new MutableBufferedImage(100, 100,
				BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = square.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.red);
		g.fillRect(0, 0, 100, 100);
		g.dispose();
		square.setProperty(PROPERTY_NAME, "square");

		MutableBufferedImage rightTriangle = new MutableBufferedImage(100, 100,
				BufferedImage.TYPE_INT_ARGB);
		g = rightTriangle.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.red);
		GeneralPath p = new GeneralPath();
		p.moveTo(100, 0);
		p.lineTo(100, 100);
		p.lineTo(0, 100);
		p.closePath();
		g.fill(p);
		g.dispose();
		rightTriangle.setProperty(PROPERTY_NAME, "right triangle");

		MutableBufferedImage isoscelesTriangle = new MutableBufferedImage(100,
				100, BufferedImage.TYPE_INT_ARGB);
		g = isoscelesTriangle.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(Color.red);
		p = new GeneralPath();
		p.moveTo(0, 100);
		p.lineTo(50, 0);
		p.lineTo(100, 100);
		p.closePath();
		g.fill(p);
		g.dispose();
		isoscelesTriangle.setProperty(PROPERTY_NAME, "isosceles triangle");

		Font font = new Font("Arial", 0, 96);
		for (char ch : new char[] { 'A', 'B', 'C', 'D', 'E', 'G', 'H', 'J', 'K',
				'M', 'N', '$', '#', '@', 'b', 'f', 'g', 'z' }) {

			String str = Character.toString(ch);
			Rectangle2D r = font.getStringBounds(str, g.getFontRenderContext());
			Rectangle r2 = r.getBounds();

			MutableBufferedImage letterImg = new MutableBufferedImage(r2.width,
					r2.height, BufferedImage.TYPE_INT_ARGB);
			g = letterImg.createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g.setColor(Color.red);
			g.setFont(font);
			g.drawString(str, (float) (r2.getWidth() / 2 - r.getWidth() / 2),
					(float) (-r.getY()));
			g.dispose();
			letterImg.setProperty(PROPERTY_NAME, str);

			returnValue.add(letterImg);
		}

		// returnValue.add(square);
		// returnValue.add(rightTriangle);
		// returnValue.add(isoscelesTriangle);

		return returnValue;
	}
}
