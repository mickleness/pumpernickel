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
package com.pump.showcase;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.pump.image.bmp.BmpDecoder;
import com.pump.image.bmp.BmpEncoder;
import com.pump.image.thumbnail.Thumbnail;
import com.pump.swing.BasicConsole;
import com.pump.swing.FilePanel;
import com.pump.swing.FilePanel.FileData;
import com.pump.util.JVM;

/**
 * This application prompts you for a BMP file and then executes a few tests.
 * <P>
 * The first test measures the time and memory used to create a thumbnail, and
 * the second test measures the time and memory used to write a file.
 * <p>
 * The console is printed to the screen using a BasicConsole to see the results.
 * <p>
 * Also the BMP image itself is displayed in a separate window so you can
 * visually confirm that the results are what you expect.
 */
public class BmpComparisonDemo extends JPanel {
	private static final long serialVersionUID = 1L;

	FilePanel filePanel = new FilePanel("File:", new String[] { "bmp" });
	BasicConsole console = new BasicConsole(false, true);
	PrintStream out = console.createPrintStream(false);
	PrintStream err = console.createPrintStream(true);
	PrintStream blue = console.createPrintStream(new Color(0x220000ff, true));

	public BmpComparisonDemo() {
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(3, 3, 3, 3);
		c.weightx = 1;
		c.weighty = 0;
		c.anchor = GridBagConstraints.EAST;
		c.fill = GridBagConstraints.BOTH;
		add(filePanel, c);
		c.gridy++;
		c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.SOUTH;
		add(new JLabel("Console:"), c);
		c.gridy = 4;
		c.gridx = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;

		c.gridy++;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		JScrollPane scrollPane = new JScrollPane(console);
		scrollPane.setPreferredSize(new Dimension(600, 600));
		add(scrollPane, c);

		filePanel.addPropertyChangeListener(FilePanel.FILE_DATA_KEY,
				new PropertyChangeListener() {

					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						try {
							final FileData bmpFile = filePanel.getFileData();
							Thread testThread = new Thread(
									"BmpComparisonDemo thread") {
								public void run() {
									try {
										// verify we read it correctly:
										BufferedImage bi = show(bmpFile);
										Thread.sleep(500);
										runThumbnailTest(bmpFile, out, blue);
										runEncodeTest(bi, out, blue);
									} catch (Throwable e) {
										e.printStackTrace(err);
									} finally {
										out.println("\nFinished.");
									}
								}
							};
							testThread.start();
						} catch (Exception e) {
							e.printStackTrace(err);
						}
					}
				});
		out.println(JVM.getProfile());

		out.println("\nAfter selecting a file, this compares the com.pump.image.bmp.* classes"
				+ "\n(highlighted in blue) with existing ImageIO classes.\n");

		addHierarchyListener(new HierarchyListener() {

			@Override
			public void hierarchyChanged(HierarchyEvent e) {
				if (BmpComparisonDemo.this.isShowing()
						&& filePanel.getFileData() == null) {
					try {
						filePanel.setFile(createSampleFile());
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		});
	}

	protected File createSampleFile() throws IOException {
		BufferedImage bi = new BufferedImage(800, 600,
				BufferedImage.TYPE_INT_RGB);
		Random random = new Random(0);
		int[] row = new int[bi.getWidth()];
		for (int y = 0; y < bi.getHeight(); y++) {
			for (int x = 0; x < row.length; x++) {
				int r = random.nextInt(255);
				int g = random.nextInt(255);
				int b = random.nextInt(255);
				row[x] = (r << 16) + (g << 8) + (b << 0);
			}
			bi.getRaster().setDataElements(0, y, bi.getWidth(), 1, row);
		}
		File tempFile = File
				.createTempFile("sample" + bi.getWidth() + "x" + bi.getHeight()
						+ "img", ".bmp");
		tempFile.deleteOnExit();

		BmpEncoder.write(bi, tempFile);
		return tempFile;
	}

	private static BufferedImage getImageIOThumbnail(InputStream file,
			Dimension maxSize) throws IOException {
		try (ImageInputStream in = ImageIO.createImageInputStream(file)) {
			Iterator<ImageReader> i = ImageIO.getImageReaders(in);
			while (i.hasNext()) {
				ImageReader r = i.next();
				r.setInput(in, false);
				// ImageReadParam p = new ImageReadParam();
				// p.setSourceRenderSize(maxSize);
				BufferedImage bigImage = r.read(0);
				return Thumbnail.Plain.create(bigImage, maxSize);
			}
			throw new IOException("Unsupported file");
		}
	}

	private static BufferedImage show(FileData file) throws IOException {
		try (InputStream in = file.createInputStream()) {
			BufferedImage image = BmpDecoder.readImage(in);
			// this isn't really needed now; it's just a distraction from the
			// demo app:
			/*
			 * JFrame f = new JFrame("BMP Verification");
			 * f.getContentPane().add(new JScrollPane(new JLabel(new
			 * ImageIcon(image)))); f.pack(); f.setVisible(true);
			 */
			return image;
		}
	}

	private static void runThumbnailTest(FileData file, PrintStream out,
			PrintStream blue) throws IOException {

		out.println("\nMeasuring performance when creating thumbnails:\n");
		out.print("\t");
		blue.print("com.pump");
		out.println("\tImageIO\t");
		out.print("Time (ms)\t");

		int repeat = 30;
		long[] times1 = new long[12];
		long[] times2 = new long[times1.length];
		long[] memory1 = new long[times1.length];
		long[] memory2 = new long[times1.length];
		Dimension maxSize = new Dimension(128, 128);
		for (int a = 0; a < times1.length; a++) {
			System.runFinalization();
			System.gc();
			System.runFinalization();
			System.gc();
			times1[a] = System.currentTimeMillis();
			memory1[a] = Runtime.getRuntime().freeMemory();
			for (int b = 0; b < repeat; b++) {
				try (InputStream in = file.createInputStream()) {
					@SuppressWarnings("unused")
					BufferedImage bi = BmpDecoder.createThumbnail(in, maxSize);
				}
			}
			times1[a] = System.currentTimeMillis() - times1[a];
			memory1[a] = memory1[a] - Runtime.getRuntime().freeMemory();
		}
		Arrays.sort(times1);
		Arrays.sort(memory1);
		blue.print(times1[times1.length / 2]);

		for (int a = 0; a < times2.length; a++) {
			System.runFinalization();
			System.gc();
			System.runFinalization();
			System.gc();
			times2[a] = System.currentTimeMillis();
			memory2[a] = Runtime.getRuntime().freeMemory();
			for (int b = 0; b < repeat; b++) {
				try (InputStream in = file.createInputStream()) {
					@SuppressWarnings("unused")
					BufferedImage bi = getImageIOThumbnail(in, maxSize);
				}
			}
			times2[a] = System.currentTimeMillis() - times2[a];
			memory2[a] = memory2[a] - Runtime.getRuntime().freeMemory();
		}
		Arrays.sort(times2);
		Arrays.sort(memory2);
		out.println("\t" + times2[times2.length / 2] + "\t");

		out.print("Memory (KB)\t");
		blue.print((memory1[memory1.length / 2] / 1024));
		out.print("\t" + (memory2[memory2.length / 2] / 1024) + "\t");
		out.println();
	}

	private static void runEncodeTest(BufferedImage bi, PrintStream out,
			PrintStream blue) throws IOException {

		out.println("\nMeasuring performance when encoding BMPs:\n");
		out.print("\t");
		blue.print("com.pump");
		out.println("\tImageIO\t");
		out.print("Time (ms)\t");

		ByteArrayOutputStream bOut = new ByteArrayOutputStream();
		int repeat = 30;
		long[] times1 = new long[30];
		long[] times2 = new long[times1.length];
		long[] memory1 = new long[times1.length];
		long[] memory2 = new long[times1.length];
		for (int a = 0; a < times1.length; a++) {
			System.runFinalization();
			System.gc();
			System.runFinalization();
			System.gc();
			times1[a] = System.currentTimeMillis();
			memory1[a] = Runtime.getRuntime().freeMemory();
			for (int b = 0; b < repeat; b++) {
				bOut.reset();
				BmpEncoder.write(bi, bOut);
			}
			times1[a] = System.currentTimeMillis() - times1[a];
			memory1[a] = memory1[a] - Runtime.getRuntime().freeMemory();
		}
		Arrays.sort(times1);
		Arrays.sort(memory1);
		blue.print(times1[times1.length / 2]);

		for (int a = 0; a < times2.length; a++) {
			System.runFinalization();
			System.gc();
			System.runFinalization();
			System.gc();
			times2[a] = System.currentTimeMillis();
			memory2[a] = Runtime.getRuntime().freeMemory();
			for (int b = 0; b < repeat; b++) {
				bOut.reset();
				javax.imageio.ImageIO.write(bi, "bmp", bOut);
			}
			times2[a] = System.currentTimeMillis() - times2[a];
			memory2[a] = memory2[a] - Runtime.getRuntime().freeMemory();
		}
		Arrays.sort(times2);
		Arrays.sort(memory2);
		out.print("\t" + times2[times2.length / 2] + "\t");
		out.println();
		out.print("Memory (KB)\t");
		blue.print((memory1[memory1.length / 2] / 1024));
		out.print("\t" + (memory2[memory2.length / 2] / 1024) + "\t");
		out.println();
	}
}