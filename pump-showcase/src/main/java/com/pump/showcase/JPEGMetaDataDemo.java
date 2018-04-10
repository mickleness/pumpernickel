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
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.pump.image.jpeg.JPEGMetaData;
import com.pump.image.thumbnail.Thumbnail;
import com.pump.swing.BasicConsole;
import com.pump.swing.FilePanel;
import com.pump.swing.FilePanel.FileData;
import com.pump.util.JVM;

/**
 * A simple demo for the {@link JPEGMetaData} class.
 * 
 */
public class JPEGMetaDataDemo extends JPanel {
	private static final long serialVersionUID = 1L;

	int maxHeight = 80;
	BufferedImage bigImage;

	FilePanel filePanel = new FilePanel("File:", new String[] { "jpg", "jpeg" });
	JLabel iconLabel = new JLabel();

	BasicConsole console = new BasicConsole(false, true);
	PrintStream out = console.createPrintStream(false);
	PrintStream err = console.createPrintStream(true);

	public JPEGMetaDataDemo() {
		bigImage = new BufferedImage(500, 500, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = bigImage.createGraphics();
		g.setPaint(new GradientPaint(0, 0, Color.green, 500, 500, Color.blue));
		g.fillRect(0, 0, 500, 500);
		g.dispose();

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
		c.gridx = 2;
		c.gridy = 0;
		c.gridheight = 3;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.CENTER;
		c.gridheight = 2;
		add(iconLabel, c);
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
							FileData jpegFile = filePanel.getFileData();
							BufferedImage thumbnail = null;
							try (InputStream in = jpegFile.createInputStream()) {
								thumbnail = JPEGMetaData.getThumbnail(in);
							} catch (IOException e) {
								e.printStackTrace(err);
							}
							if (thumbnail != null) {
								iconLabel.setIcon(new ImageIcon(thumbnail));
								bigImage = thumbnail;
							} else {
								iconLabel.setIcon(null);
								try (InputStream in = jpegFile
										.createInputStream()) {
									bigImage = ImageIO.read(in);
								} catch (IOException e2) {
									e2.printStackTrace(err);
								}
							}

							Thread testThread = new Thread(new TestRunnable(
									jpegFile, out, err));
							testThread.start();
						} catch (Exception e) {
							e.printStackTrace(err);
						}
					}
				});

		out.println(JVM.getProfile());
	}

	static class TestRunnable implements Runnable {
		FileData jpeg;
		PrintStream out, err;

		public TestRunnable(FileData jpeg, PrintStream out, PrintStream err) {
			this.jpeg = jpeg;
			this.out = out;
			this.err = err;
		}

		public void run() {
			try {
				out.println("-------------------------\n"
						+ "Starting tests for \"" + jpeg.getName() + "\"");

				// this thread is triggered by selecting a new JPEG:
				// give the GUI a chance to catch up and repaint everything
				// before we start:
				try {
					Thread.sleep(200);
				} catch (Exception e) {
				}
				;

				long time, memory;
				out.println("\nTesting JPEGMetaData:");
				try {
					time = System.currentTimeMillis();
					memory = Runtime.getRuntime().freeMemory();
					try (InputStream in = jpeg.createInputStream()) {
						if (JPEGMetaData.getThumbnail(in) == null)
							throw new UnsupportedOperationException(
									"JPEGMetaData could not read a thumbnail for \""
											+ jpeg.getName() + "\"");
					}
					time = System.currentTimeMillis() - time;
					memory = memory - Runtime.getRuntime().freeMemory();

					out.println("\tTime for JPEGMetaData: " + time + " ms");
					out.println("\tAllocation for JPEGMetaData: " + memory
							/ 1024 + " KB");

				} catch (Exception e) {
					e.printStackTrace(err);
				}

				out.println("\nTesting ImageIO (thumbnail):");
				Iterator<ImageReader> iterator = ImageIO
						.getImageReadersBySuffix("jpeg");
				while (iterator.hasNext()) {
					ImageReader reader = iterator.next();
					try {
						time = System.currentTimeMillis();
						memory = Runtime.getRuntime().freeMemory();
						try (InputStream in = jpeg.createInputStream()) {
							reader.setInput(ImageIO.createImageInputStream(in));
							BufferedImage thumbnail = reader
									.readThumbnail(0, 0);
							if (thumbnail == null) {
								throw new UnsupportedOperationException(
										"ImageIO could not read a thumbnail for \""
												+ jpeg.getName() + "\"");
							}
						}
						time = System.currentTimeMillis() - time;
						memory = memory - Runtime.getRuntime().freeMemory();

						out.println("\tTime for "
								+ getName(reader.getClass().getName()) + ": "
								+ time + " ms");
						out.println("\tAllocation for "
								+ getName(reader.getClass().getName()) + ": "
								+ memory / 1024 + " KB");
					} catch (Exception e) {
						e.printStackTrace(err);
					}
				}

				out.println("\nTesting ImageIO (full size):");
				iterator = ImageIO.getImageReadersBySuffix("jpeg");
				while (iterator.hasNext()) {
					ImageReader reader = iterator.next();
					try {
						time = System.currentTimeMillis();
						memory = Runtime.getRuntime().freeMemory();
						try (InputStream in = jpeg.createInputStream()) {
							reader.setInput(ImageIO.createImageInputStream(in));
							BufferedImage image = reader.read(0);
							if (image == null) {
								throw new UnsupportedOperationException(
										"ImageIO could not read an image for \""
												+ jpeg.getName() + "\"");
							}
							Thumbnail.Plain.create(image, new Dimension(128,
									128));
						}
						time = System.currentTimeMillis() - time;
						memory = memory - Runtime.getRuntime().freeMemory();
						out.println("\tTime for "
								+ getName(reader.getClass().getName()) + ": "
								+ time + " ms");
						out.println("\tAllocation for "
								+ getName(reader.getClass().getName()) + ": "
								+ memory / 1024 + " KB");
					} catch (Exception e) {
						e.printStackTrace(err);
					}
				}
			} catch (Throwable t) {
				t.printStackTrace(err);
			}
		}

		private String getName(String className) {
			int i = className.lastIndexOf('.');
			if (i == -1)
				return className;
			return className.substring(i + 1);
		}
	}
}