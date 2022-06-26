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
package com.pump.showcase.resourcegenerator;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import com.pump.animation.AnimationReader;
import com.pump.animation.ResettableAnimationReader;
import com.pump.image.gif.GifWriter;
import com.pump.image.gif.GifWriter.ColorReduction;
import com.pump.image.transition.AbstractTransition;
import com.pump.image.transition.Transition;
import com.pump.image.transition.Transition3D;
import com.pump.io.AdjacentFileOutputStream;
import com.pump.showcase.demo.Transition2DDemo;

/**
 * This creates the gif thumbnail for all the transition in the Transition2D
 * demo.
 */
public class Transition2DDemoResourceGenerator extends DemoResourceGenerator {
	/**
	 * The width and height of each thumbnail
	 */
	static final int SIZE = 100;

	class Output implements Callable<Void> {
		Transition transition;
		DemoResourceContext context;
		File gifFile;

		Output(DemoResourceContext context, Transition transition) {
			this.transition = transition;
			this.context = context;
		}

		@Override
		public Void call() throws Exception {
			File classTransitionDir = new File(transitionDir,
					transition.getClass().getSimpleName());
			context.indexDirectory(classTransitionDir);
			transitionClassDirs.add(classTransitionDir);
			String filename = (transition.toString() + ".gif").replaceAll(" ",
					"");

			if (!classTransitionDir.exists() && !classTransitionDir.mkdirs())
				throw new IOException("File.mkdirs() failed for "
						+ classTransitionDir.getAbsolutePath());

			gifFile = new File(classTransitionDir, filename);

			try (AdjacentFileOutputStream fileOut = context
					.createFileOutputStream(gifFile)) {
				try {
					writeTransition(transition, fileOut);
				} catch (Exception e) {
					fileOut.cancel();
					throw e;
				}
			}
			System.out.println("Wrote: " + gifFile.getAbsolutePath());
			return null;
		}
	}

	Collection<File> transitionClassDirs = new HashSet<>();
	File transitionDir;

	@Override
	public void run(DemoResourceContext context) throws Exception {
		transitionDir = context
				.getFile("resources" + File.separator + "transition");

		List<Output> outputs = new LinkedList<>();
		for (Transition[] ts : getTransitions()) {
			for (Transition transition : ts) {
				Output output = new Output(context, transition);
				outputs.add(output);
				context.queueCallable(output);
			}
		}
		context.waitForExecutor();

		for (File transitionClassDir : transitionClassDirs) {
			context.removeOldFiles(transitionClassDir);
		}

		Output[][] table = createTable(outputs);
		StringBuilder sb = new StringBuilder();
		sb.append("<table>");
		for (int y = 0; y < table.length; y++) {
			sb.append("<tr>");
			for (int x = 0; x < table[y].length; x++) {
				sb.append("<td>");
				Output output = table[y][x];

				// URL separator
				String s = output.gifFile.getAbsolutePath()
						.replaceAll(File.separator, "/");
				int i = s.indexOf("/resources/");
				String url = "https://raw.githubusercontent.com/mickleness/pumpernickel/master"
						+ s.substring(i);

				sb.append("<img src=\"" + url + "\" width=\"" + SIZE
						+ "\" height=\"" + SIZE + "\"><br>"
						+ output.transition.toString());
				sb.append("</td>");
			}
			sb.append("</tr>");
		}
		sb.append("</table>");

		System.out.println(sb);
	}

	protected Transition[][] getTransitions() {
		return Transition2DDemo.getTransitions();
	}

	private Output[][] createTable(Collection<Output> gifFiles) {
		List<Output[]> table = new ArrayList<>();
		Iterator<Output> gifFileIter = gifFiles.iterator();
		while (gifFileIter.hasNext()) {
			List<Output> row = new LinkedList<>();
			for (int a = 0; a < 4; a++) {
				if (gifFileIter.hasNext()) {
					Output next = gifFileIter.next();
					row.add(next);
				}
			}
			table.add(row.toArray(new Output[row.size()]));
		}
		return table.toArray(new Output[table.size()][]);
	}

	private static void writeTransition(final Transition transition,
			OutputStream out) throws IOException {
		final BufferedImage bi1 = AbstractTransition.createImage(SIZE, "A",
				true, false);
		final BufferedImage bi2 = AbstractTransition.createImage(SIZE, "B",
				false, false);
		final int totalFrames = 200;
		final int middleFrame = totalFrames / 2;

		ResettableAnimationReader animation = new ResettableAnimationReader() {
			int ctr = 0;

			BufferedImage bi;

			public BufferedImage getNextFrame(boolean cloneImage)
					throws IOException {
				if (ctr == totalFrames)
					return null;

				if (bi == null || cloneImage) {
					bi = new BufferedImage(SIZE, SIZE,
							BufferedImage.TYPE_INT_RGB);
				}
				Graphics2D g = bi.createGraphics();
				g.setColor(Color.black);
				g.fillRect(0, 0, SIZE, SIZE);
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
						RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
				if (transition instanceof Transition3D) {
					g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
							RenderingHints.VALUE_INTERPOLATION_BICUBIC);
				} else {
					g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
							RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				}
				g.setRenderingHint(RenderingHints.KEY_RENDERING,
						RenderingHints.VALUE_RENDER_QUALITY);

				float f;
				if (ctr < middleFrame) {
					f = ((float) ctr) / ((float) middleFrame);
					transition.paint(g, bi1, bi2, f);
				} else {
					f = ((float) (ctr - middleFrame)) / ((float) middleFrame);
					transition.paint(g, bi2, bi1, f);
				}
				g.dispose();

				ctr++;

				return bi;
			}

			public double getDuration() {
				double sum = 0;
				for (int a = 0; a < totalFrames; a++) {
					sum += getFrameDuration(a);
				}
				return sum;
			}

			public int getFrameCount() {
				return totalFrames;
			}

			public int getLoopCount() {
				return AnimationReader.LOOP_FOREVER;
			}

			public double getFrameDuration() {
				return getFrameDuration(ctr - 1);
			}

			public double getFrameDuration(int index) {
				if (index == 0 || index == middleFrame) {
					return 2;
				}
				return .007;
			}

			public int getWidth() {
				return SIZE;
			}

			public int getHeight() {
				return SIZE;
			}

			public void reset() {
				ctr = 0;
			}

		};
		GifWriter.write(out, animation, ColorReduction.FROM_ALL_FRAMES, true);
	}
}