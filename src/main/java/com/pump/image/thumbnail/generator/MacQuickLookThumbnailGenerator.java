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
package com.pump.image.thumbnail.generator;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.imageio.ImageIO;

import com.pump.desktop.temp.TempFileManager;
import com.pump.io.FileUtils;
import com.pump.io.IOUtils;
import com.pump.util.BufferedPipe;

/**
 * This ThumbnailGenerator uses Mac's "Quick Look" to create thumbnails.
 * (Specifically: this creates a new Process calling "qlmanage".)
 * <p>
 * One big concern I have about using qlmanage is: it appears possible to hang
 * forever if a file is unsupported. For example: if I pass in a DMG or PKG file
 * it appears to never terminate on its own. (I didn't expect it to create a
 * helpful thumbnail, but I expected qlmanage to terminate.) So I addressed this
 * with two features:
 * <ul>
 * <li>Timeouts: if qlmanage doesn't exit after a fixed amount of time, we kill
 * it.</li>
 * <li>Supported file extensions: we only look at particular file extensions;
 * anything that is unsupported returns a null thumbnail immediately.</li>
 * </ul>
 * <p>
 * By default we use a pre-wired list of file extensions, and this default
 * collection is completely arbitrary. (And it may change over time.)
 * <p>
 * There are downsides to these restrictions. If a thumbnail takes a long time
 * to calculate: it might run afoul of the timeout and we abort prematurely. And
 * the list of file extensions may be incomplete/lacking. (For example: I
 * noticed that "pptx" isn't in my list of supported files, but if you have
 * Office installed then Quick Look can probably read it.)
 * 
 * @see <a href=
 *      "https://developer.apple.com/library/archive/documentation/UserExperience/Conceptual/Quicklook_Programming_Guide/Articles/QLUserExperience.html#//apple_ref/doc/uid/TP40005020-CH3-SW3">https://developer.apple.com/library/archive/documentation/UserExperience/Conceptual/Quicklook_Programming_Guide/Articles/QLUserExperience.html#//apple_ref/doc/uid/TP40005020-CH3-SW3</a>
 */
public class MacQuickLookThumbnailGenerator implements ThumbnailGenerator {

	private static class ProcessBuilderThread {
		String threadName;
		boolean outputCommand, pipeOutput;

		final ProcessBuilder processBuilder = new ProcessBuilder(
				new String[] {});
		BufferedPipe errPipe;

		ProcessBuilderThread(String name, boolean outputCommand,
				boolean pipeOutput) {
			this.outputCommand = outputCommand;
			this.pipeOutput = pipeOutput;

			threadName = "Execute " + name;
		}

		Process process;

		public Process getProcess() {
			return process;
		}

		public Thread start(boolean blocking) throws Exception {
			if (outputCommand) {
				System.out.println(toString(processBuilder));
			}
			final AtomicReference<Exception> exRef = new AtomicReference<>();
			Runnable runnable = new Runnable() {
				@Override
				public void run() {
					try {
						if (pipeOutput) {
							new BufferedPipe(process.getInputStream(),
									System.out, "\t", true);
						}
						errPipe = new BufferedPipe(process.getErrorStream(),
								System.err, "\t", true);
						int code = process.waitFor();
						if (code != 0) {
							System.err.println(
									"\t" + threadName + " error: " + code);
						} else {
							if (pipeOutput)
								System.out.println(
										"\t" + threadName + " complete");
						}
					} catch (Exception e) {
						e.printStackTrace();
						exRef.set(e);
					}
				}
			};
			Thread thread = new Thread(runnable, threadName);
			process = processBuilder.start();
			if (blocking) {
				thread.run();
			} else {
				thread.start();
			}

			// this can only (reliably) happen for blocking calls:
			Exception ex = exRef.get();
			if (ex != null)
				throw ex;

			return thread;
		}

		protected String toString(ProcessBuilder pb) {
			List<String> list = pb.command();
			StringBuilder sb = new StringBuilder();
			for (String s : list) {
				if (sb.length() > 0) {
					sb.append(" ");
				}

				if (s.contains(" ") && !s.contains("\"")) {
					sb.append("\"" + s + "\"");
				} else {
					sb.append(s);
				}
			}
			return sb.toString();
		}
	}

	/**
	 * This is a collection of file extension (like "mp4" or "jpg") that Quick
	 * Look supports. This collection is based on an arbitrary/empirical sweep
	 * of my local computer.
	 */
	public static Collection<String> DEFAULT_FILE_EXTENSIONS = new HashSet<>(
			Arrays.asList("3gp", "aa", "aax", "aif", "bmp", "doc", "docx",
					"gif", "icns", "ico", "jpeg", "jpg", "m3u", "m4a", "m4b",
					"m4p", "m4v", "mid", "mod", "mov", "mp3", "mp4", "odt",
					"ogg", "opus", "otf", "pdf", "pict", "png", "psd", "rtf",
					"tiff", "ttf", "txt", "wav", "webp", "xls", "xlsx", "xml"));

	/**
	 * The default timeout to apply for the qlmanage process.
	 */
	public static long DEFAULT_PROCESS_TIMEOUT = 1000;

	boolean pipeOutput;
	long processTimeoutMS;
	Collection<String> fileExtensions;

	/**
	 * Create a new MacQuickLookThumbnailGenerator that uses a default set of
	 * file extensions and timeout, and that does not pipe output to System.out.
	 */
	public MacQuickLookThumbnailGenerator() {
		this(false, null, DEFAULT_PROCESS_TIMEOUT);
	}

	/**
	 * Create a new MacQuickLookThumbnailGenerator.
	 * 
	 * @param pipeOutput
	 *            if true then we'll print the process output to System.out.
	 * @param an
	 *            optional set of file extensions to accept. If this is null
	 *            then the default set is used.
	 * @param the
	 *            number of milliseconds to wait before killing the qlmanage
	 *            process.
	 */
	public MacQuickLookThumbnailGenerator(boolean pipeOutput,
			Collection<String> fileExtensions, long processTimeoutMS) {
		setPipeOutput(pipeOutput);
		if (fileExtensions == null)
			fileExtensions = DEFAULT_FILE_EXTENSIONS;
		this.fileExtensions = Collections
				.unmodifiableCollection(fileExtensions);
		this.processTimeoutMS = processTimeoutMS;
	}

	/**
	 * @param pipeOutput
	 *            if true then we'll print the process output to System.out.
	 */
	public void setPipeOutput(boolean pipeOutput) {
		this.pipeOutput = pipeOutput;
	}

	/**
	 * Return true if the Process's output should be piped to System.out.
	 */
	public boolean isPipeOutput() {
		return pipeOutput;
	}

	@Override
	public BufferedImage createThumbnail(File file, int maxImageSizeRequest)
			throws Exception {
		if (file.isDirectory())
			return null;

		String fileExtension = IOUtils.getExtension(file.getName(), true);
		if (!fileExtensions.contains(fileExtension))
			return null;

		if (!TempFileManager.isInitialized())
			TempFileManager.initialize("MacQuickLookFilePreviewApp");

		File destDir = TempFileManager.get()
				.createFile("MacQuickLookFilePreview", null);
		Process process = null;
		try {
			FileUtils.mkdir(destDir);
			ProcessBuilderThread t = new ProcessBuilderThread("QuickLook",
					pipeOutput, pipeOutput);

			List<String> args = new LinkedList<>();
			args.add("qlmanage");
			args.add("-o");
			args.add(destDir.getAbsolutePath());

			// if we use "-p" to create a preview: that could be a movie
			// or some other format. Instead we should use "-t" to create
			// a thumbnail.
			args.add("-t");
			args.add(file.getAbsolutePath());

			if (maxImageSizeRequest > 0) {
				// "-s" is used for "Size for the thumbnail"
				args.add("-s");
				args.add(Integer.toString(maxImageSizeRequest));
			}

			// "-c" is used for "Force the content type used for the documents"
			// I had some notes suggesting this worked once, but now it
			// consistently fails: the qlmanage process hangs forever.
			// args.add("-c");
			// args.add("image/png");

			// we could also use "-g" to force the generator:
			// "/System/Library/QuickLook/Image.qlgenerator"

			t.processBuilder.command(args);

			Thread thread = t.start(false);
			process = t.getProcess();
			synchronized (thread) {
				thread.wait(processTimeoutMS);
			}
			if (process.isAlive())
				return null;

			if (t.errPipe != null) {
				String str = t.errPipe.getLog().trim();
				if (!str.isEmpty()) {
					throw new RuntimeException(str);
				}
			}

			File[] l = destDir.listFiles();
			if (l.length == 0)
				return null;
			return ImageIO.read(l[0]);
		} finally {
			if (process != null && process.isAlive())
				process.destroyForcibly();
			IOUtils.delete(destDir);
		}
	}
}