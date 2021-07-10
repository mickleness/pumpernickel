package com.pump.showcase.resourcegenerator;

import java.util.LinkedList;
import java.util.List;

/**
 * Create files/data used in the HTML documentation for several showcase demos.
 */
public abstract class DemoResourceGenerator {

	public static void main(String[] args) throws Exception {
		DemoResourceContext context = new DemoResourceContext();

		DemoResourceGenerator[] gs = new DemoResourceGenerator[] {
				new VectorImageDemoResourceGenerator(),
				new ThumbnailGeneratorDemoResourceGenerator(),
				new Transition2DDemoResourceGenerator(),
				new Transition3DDemoResourceGenerator() };

		for (DemoResourceGenerator g : gs) {
			long time = System.currentTimeMillis();
			System.out.println(g.getClass().getSimpleName() + " Running:");
			g.run(context);
			time = System.currentTimeMillis() - time;
			System.out.println("(" + time + " ms)");
			System.out.println();
		}
		System.out.println("Finished");
		System.exit(0);
	}

	public abstract void run(DemoResourceContext context) throws Exception;

	/**
	 * Split a String into several rows of text. This is intended to break up a
	 * large base64 block of text into multiple rows.
	 * 
	 * @param str
	 *            a large String
	 * @param charLimit
	 *            the maximum number of characters in a row
	 * @return a list of Strings that can be combined together to recreate the
	 *         input.
	 */
	protected List<String> splitRows(String str, int charLimit) {
		List<String> returnValue = new LinkedList<>();
		while (str.length() > 0) {
			String row = str.substring(0, Math.min(str.length(), charLimit));
			returnValue.add(row);
			str = str.substring(row.length());
		}
		return returnValue;
	}
}
