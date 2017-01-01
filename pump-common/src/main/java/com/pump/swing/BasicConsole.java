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
package com.pump.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Highlighter.HighlightPainter;

import com.pump.blog.Blurb;
import com.pump.util.ThreadedSemaphore;

/** A modest JTextArea that appends text from thread-safe PrintStreams, and helps
 * format tab-separated tables.
 * <p>Here is a screenshot illustrating the expected usage of this class:
 * <p><img src="https://raw.githubusercontent.com/mickleness/pumpernickel/master/pump-release/resources/ShapeBoundsDemo.png" alt="Screenshot of ShapeBoundsDemo">
 * 
 * @see <a href="http://javagraphics.blogspot.com/2015/03/text-managing-console-in-swing.html">Text: Managing a Console in Swing</a>
 * @see com.bric.io.ConsoleLogger
 */
@Blurb (
		filename = "BasicConsole",
		title = "Text: Managing a Console in Swing",
		releaseDate = "March 2015",
		summary = "This discusses options for presenting a console-like Swing component."
				+ "<p>The class I end up developing is the <a href=\"https://javagraphics.java.net/doc/com/bric/swing/BasicConsole.html\">BasicConsole</a>: "
				+ "a thread-safe JTextArea that highlights multiple PrintStreams in different colors and offers modest support for tab-formatted tables.",
		link = "http://javagraphics.blogspot.com/2015/03/text-managing-console-in-swing.html",
		sandboxDemo = false
		)
public class BasicConsole extends JTextArea {
	private static final long serialVersionUID = 1L;

	/** The translucent red used for System.err by default. */
	private static Color DEFAULT_ERROR_HIGHLIGHT = new Color(255,0,0,60);

	/** Create and display a BasicConsole in a JScrollPane in a new JFrame
	 * that is set to EXIT_ON_CLOSE.
	 * <p>If called outside the EDT: this method will block until the EDT
	 * has a chance to set everything up.
	 * @param reassignSystemOutputStream if true then System.out and System.err will be
	 * reassigned. Note this may cause security-related exceptions in some environments.
	 * Instead: if you have control of the output you're looking for consider using
	 * <code>createPrintStream()</code> to create a customized PrintStream to direct
	 * your data towards and not interact with <code>System.out</code>.
	 * @param resizeTabSpacing if true then this component is going to contain table-like
	 * data. If false then the tab spacing is unchanged.
	 * @param exitOnClose if true then the JFrame that displays this BasicConsole is set to EXIT_ON_CLOSE.
	 * This is intended for apps where this is the only UI. For apps where this is only an auxiliary part of the UI,
	 * this argument needs to be false.
	 * @return a new BasicConsole.
	 */
	public static BasicConsole create(final String frameTitle,final boolean reassignSystemOutputStream,final boolean resizeTabSpacing,final boolean exitOnClose) {
		final BasicConsole[] c = new BasicConsole[] { null };
		Runnable r = new Runnable() {
			public void run() {
				try {
					String lf = UIManager.getSystemLookAndFeelClassName();
					UIManager.setLookAndFeel(lf);
				} catch (Throwable e) {
					e.printStackTrace();
				}

				JFrame frame = new JFrame(frameTitle);
				c[0] = new BasicConsole(reassignSystemOutputStream, resizeTabSpacing);
				JScrollPane scrollPane = new JScrollPane(c[0]);
				scrollPane.setPreferredSize(new Dimension(500, 500));
				frame.getContentPane().add(scrollPane);
				frame.pack();
				if(exitOnClose)
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setVisible(true);
				synchronized(c) {
					c.notifyAll();
				}
			}
		};
		if(SwingUtilities.isEventDispatchThread()) {
			r.run();
		} else {
			SwingUtilities.invokeLater(r);
			while(c[0]==null) {
				synchronized(c) {
					try {
						c.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return c[0];
	}

	private class AppendingOutputStream extends OutputStream {
		Color highlight;
		OutputStream secondaryPrintStream;

		public AppendingOutputStream(Color highlight,OutputStream secondaryPrintStream) {
			this.highlight = highlight;
			this.secondaryPrintStream = secondaryPrintStream;
		}

		@Override
		public void write(int b) throws IOException {
			BasicConsole.this.append( Character.toString( (char)b ), highlight );
			if(secondaryPrintStream!=null)
				secondaryPrintStream.write(b);
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			BasicConsole.this.append(new String(b, off, len), highlight);
			if(secondaryPrintStream!=null)
				secondaryPrintStream.write(b, off, len);
		}

		@Override
		public void write(byte[] b) throws IOException {
			BasicConsole.this.append(new String(b), highlight);
			if(secondaryPrintStream!=null)
				secondaryPrintStream.write(b);
		}
	}

	private class AppendingPrintStream extends PrintStream {
		Color highlight;
		PrintStream secondaryPrintStream;
		ThreadedSemaphore semaphore = new ThreadedSemaphore(1);

		AppendingPrintStream(Color highlight,PrintStream secondaryPrintStream) {
			super(new AppendingOutputStream(highlight, secondaryPrintStream));
			this.highlight = highlight;
			this.secondaryPrintStream = secondaryPrintStream;
		}

		@Override
		public void print(String s) {
			boolean first = semaphore.tryAcquire(1);
			try {
				BasicConsole.this.append(s, highlight);
				if(first && secondaryPrintStream!=null) {
					secondaryPrintStream.print(s);
				}
			} finally {
				if(first)
					semaphore.release(1);
			}
			
		}
		
		@Override
	    public void println(Object x) {
			boolean first = semaphore.tryAcquire(1);
			try {
				String s = String.valueOf(x);
		        synchronized (this) {
		            print(s);
		            print("\n");
		            flush();
		        }
				if(first && secondaryPrintStream!=null) {
					secondaryPrintStream.println(x);
				}
			} finally {
				if(first)
					semaphore.release(1);
			}
	    }


	}

	final boolean resizeTabSpacing;
	
	public enum DefaultPrintStream {
		/** This indicates that System.out and System.err should not be touched in setting up a BasicConsole.
		 * 
		 */
		DO_NOT_CHANGE, 
		/** This indicates that System.out and System.err should be replaced in setting up a BasicConsole. */
		OVERRIDE, 
		/** This indicates that System.out and System.err should be extended (so the original PrintStream
		 * is still notified, but the BasicConsole hears changes too).
		 */
		EXTEND;
	}


	/**
	 * 
	 * @param reassignSystemOutputStream if true then System.out and System.err will be
	 * reassigned. Note this may cause security-related exceptions in some environments.
	 * Instead: if you have control of the output you're looking for consider using
	 * <code>createPrintStream()</code> to create a customized PrintStream to direct
	 * your data towards and not interact with <code>System.out</code>.
	 * @param resizeTabSpacing if true then this component will change the tab-spacing
	 * to display tab-separated columns in a table.
	 */
	public BasicConsole(boolean reassignSystemOutputStream,boolean resizeTabSpacing) {
		this( reassignSystemOutputStream ? DefaultPrintStream.OVERRIDE : DefaultPrintStream.DO_NOT_CHANGE, resizeTabSpacing );
	}
	
	public BasicConsole(DefaultPrintStream behavior,boolean resizeTabSpacing) {
		this.resizeTabSpacing = resizeTabSpacing;
		setFont(new Font("Monospaced",0,13));
		setEditable(false);
		if(DefaultPrintStream.EXTEND.equals(behavior)) {
			try {
				System.setOut(createPrintStream(false, System.out));
				System.setErr(createPrintStream(true, System.err));
			} catch(SecurityException e) {
				String msg = "A SecurityException occurred while trying to call System.setOut() and/or System.setErr()";
				append(msg, DEFAULT_ERROR_HIGHLIGHT);
				System.err.println(msg);
				e.printStackTrace();
			}
		} else if(DefaultPrintStream.OVERRIDE.equals(behavior)) {
			try {
				System.setOut(createPrintStream(false));
				System.setErr(createPrintStream(true));
			} catch(SecurityException e) {
				String msg = "A SecurityException occurred while trying to call System.setOut() and/or System.setErr()";
				append(msg, DEFAULT_ERROR_HIGHLIGHT);
				System.err.println(msg);
				e.printStackTrace();
			}
		}

		TextContextualMenuHelper.install(this, true, false, false, true);

		//this is only used when the user selects "Clear" to remove all data:
		getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void removeUpdate(DocumentEvent e) {
				if(getText().length()==0)
					highlightRuns.clear();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {}

			@Override
			public void changedUpdate(DocumentEvent e) {}

		});

		ContextualMenuHelper.add(this, "Copy as HTML", new Runnable() {

			private HighlightRun getRun(int charIndex) {
				for(HighlightRun r : highlightRuns) {
					if(charIndex>=r.start && charIndex<r.end)
						return r;
				}
				return null;
			}

			public void run() {
				StringBuffer sb = new StringBuffer();
				sb.append("<html><body>");
				String text = getText();
				boolean wasInsideTable = false;
				HighlightRun lastHighlight = null;
				for(int a = 0; a<text.length(); a++) {
					char ch = text.charAt(a);
					if(ch=='\n') {
						if(lastHighlight!=null) {
							sb.append("</div>");
							lastHighlight = null;
						}
						int i = text.indexOf('\n', a+1);
						String entireLine = i==-1 ? text.substring(a+1) : text.substring(a+1, i);
						boolean isInsideTable = entireLine.contains("\t");
						if(isInsideTable && (!wasInsideTable)) {
							sb.append("\n");
							sb.append("<br><table>");
							sb.append("\n<tr><td>");
						} else if( (!isInsideTable) && wasInsideTable) {
							sb.append("</td></tr></table>");
							sb.append("\n");
						} else if(isInsideTable) {
							sb.append("</td></tr>\n<tr><td>");
						} else {
							sb.append("\n<br>");
						}
						wasInsideTable = isInsideTable;
					} else if(ch=='\t') {
						if(lastHighlight!=null) {
							sb.append("</div>");
							lastHighlight = null;
						}
						sb.append("</td><td>");
					} else {
						HighlightRun currentHighlight = getRun(a);
						if(currentHighlight!=lastHighlight) {
							if(lastHighlight!=null) {
								sb.append("</div>");
							}
							if(currentHighlight!=null) {
								String hex;
								int alpha = (int)( Math.sqrt(currentHighlight.highlight.getAlpha()/255.0)*255 );
								int red = currentHighlight.highlight.getRed()*(255-alpha) + 255*alpha;
								int green = currentHighlight.highlight.getGreen()*(255-alpha) + 255*alpha;
								int blue = currentHighlight.highlight.getBlue()*(255-alpha) + 255*alpha;
								int rgb = ((red/255) << 16) + ((green/255) << 8) + ((blue/255) << 0);
								hex = Integer.toHexString(rgb);
								while(hex.length()>6) hex = hex.substring(1);
								while(hex.length()<6) hex = "0"+hex;

								sb.append("<div style=\"background-color:#"+hex+"\">");
							}
						}

						if(ch=='<') {
							sb.append("&lt;");
						} else if(ch=='>') {
							sb.append("&gt;");
						} else {
							sb.append(ch);
						}
						lastHighlight = currentHighlight;
					}
				}
				if(lastHighlight!=null) {
					sb.append("</div>");
				}
				sb.append("\n</body></html>");
				Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(sb.toString()), null);
			}
		});

		int m = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

		getInputMap().put( KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, m), "zoomIn");
		getInputMap().put( KeyStroke.getKeyStroke(KeyEvent.VK_EQUALS, m+MouseEvent.SHIFT_MASK), "zoomIn");
		getActionMap().put("zoomIn", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				Font font = getFont();
				float fontSize = font.getSize2D();
				fontSize = Math.min(fontSize+1,24);
				setFont( font.deriveFont(fontSize) );
			}
		});
		getInputMap().put( KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, m), "zoomOut");
		getActionMap().put("zoomOut", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				Font font = getFont();
				float fontSize = font.getSize2D();
				fontSize = Math.max(fontSize-1,9);
				setFont( font.deriveFont(fontSize) );
			}
		});
	}

	/** Create a new PrintStrem that appends text to this BasicConsole.
	 * This method and the PrintStream it returns are thread-safe.
	 * 
	 * @param applyErrHighlighter if true then text printed to this
	 * stream will be highlighted with a translucent red. If false
	 * then the text will come in undecorated.
	 * @return a PrintStream that appends data to this BasicConsole.
	 */
	public PrintStream createPrintStream(boolean applyErrHighlighter) {
		return createPrintStream( applyErrHighlighter ?  DEFAULT_ERROR_HIGHLIGHT : null);
	}

	/** Create a new PrintStrem that appends text to this BasicConsole.
	 * This method and the PrintStream it returns are thread-safe.
	 * 
	 * @param applyErrHighlighter if true then text printed to this
	 * stream will be highlighted with a translucent red. If false
	 * then the text will come in undecorated.
	 * @param delegatePrintStream a PrintStream that also receives all
	 * incoming stream data. This lets you layer a PrintStream on top of
	 * the default System.out if you are running from a dev environment like Eclipse.
	 * @return a PrintStream that appends data to this BasicConsole.
	 */
	public PrintStream createPrintStream(boolean applyErrHighlighter,PrintStream delegatePrintStream) {
		return createPrintStream( applyErrHighlighter ?  DEFAULT_ERROR_HIGHLIGHT : null, delegatePrintStream);
	}

	/** Create a new PrintStrem that appends text to this BasicConsole.
	 * This method and the PrintStream it returns are thread-safe.
	 * 
	 * @param highlighterColor the optional highlighter color this PrintStream uses.
	 * This color should include some opacity so it combines with (and does not
	 * obscure) the default text selection highlight. (So instead of making a soft opaque
	 * red, you should make a bright red that is translucent.)
	 * @return a PrintStream that appends data to this BasicConsole.
	 */
	public PrintStream createPrintStream(Color highlighterColor) {
		return createPrintStream(highlighterColor, null);
	}

	/** Create a new PrintStrem that appends text to this BasicConsole.
	 * This method and the PrintStream it returns are thread-safe.
	 * 
	 * @param highlighterColor the optional highlighter color this PrintStream uses.
	 * This color should include some opacity so it combines with (and does not
	 * obscure) the default text selection highlight. (So instead of making a soft opaque
	 * red, you should make a bright red that is translucent.)
	 * @param delegatePrintStream a PrintStream that also receives all
	 * incoming stream data. This lets you layer a PrintStream on top of
	 * the default System.out if you are running from a dev environment like Eclipse.
	 * @return a PrintStream that appends data to this BasicConsole.
	 */
	public PrintStream createPrintStream(Color highlighterColor,PrintStream delegatePrintStream) {
		return new AppendingPrintStream(highlighterColor, delegatePrintStream);
	}

	private static class Insertion {
		String s;
		Color highlight;
		Insertion(String s,Color highlight) {
			this.s = s;
			this.highlight = highlight;
		}
	}

	private static class HighlightRun {
		final int start;
		int end;
		final Color highlight;
		HighlightRun(int start,int end,Color highlight) {
			this.start = start;
			this.end = end;
			this.highlight = highlight;
		}
	}

	private List<HighlightRun> highlightRuns = new ArrayList<HighlightRun>();
	private List<Insertion> pendingInsertions = new ArrayList<Insertion>();
	Runnable clearQueue = new Runnable() {
		public void run() {
			Insertion[] array;
			synchronized(pendingInsertions) {
				array = pendingInsertions.toArray(new Insertion[pendingInsertions.size()]);
				pendingInsertions.clear();
			}
			for(Insertion r : array) {
				int l1 = getText().length();
				boolean reapply = highlightRuns.size()>0 && 
						highlightRuns.get(highlightRuns.size()-1).end==l1;
				BasicConsole.this.append(r.s);
				if(resizeTabSpacing)
					validateTabSize(r.s);

				if(r.highlight!=null) {
					try {
						int[] newRun = new int[] {l1, l1+r.s.length()};
						HighlightPainter highlighter = new DefaultHighlightPainter(r.highlight);
						getHighlighter().addHighlight(newRun[0], newRun[1], highlighter);
						if(highlightRuns.size()>0 && 
								highlightRuns.get(highlightRuns.size()-1).end==newRun[0]) {
							highlightRuns.get(highlightRuns.size()-1).end = newRun[1];
						} else {
							highlightRuns.add(new HighlightRun(l1, l1+r.s.length(), r.highlight));
						}
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
				}

				if( reapply ) {
					reapplyErrorHighlighting();
				}
			}
		}

		private void validateTabSize(String insertion) {
			String allText = BasicConsole.this.getText();
			int lastNewLine = -1;
			for(int a = allText.length()-insertion.length(); a>=0 && lastNewLine==-1; a--) {
				char ch = allText.charAt(a);
				if(ch=='\n')
					lastNewLine = a;
			}
			String s = allText.substring(lastNewLine+1);
			String[] lines = s.split("\n");
			int maxColumnLength = 0;
			for(String line : lines) {
				String[] tabs = line.trim().split("\t");
				if(tabs.length>1) {
					for(String tab : tabs) {
						maxColumnLength = Math.max(maxColumnLength, tab.length());
					}
				}
			}
			if(getTabSize()<maxColumnLength+2) {
				setTabSize(maxColumnLength+2);
			}
		}
	};

	/** This is the only recommended way to add text to this component.
	 * TODO: change the BasicConsole so it inherits from JComponent and not JTextArea.
	 * Make it impossible to accidentally access text data through alternative methods.
	 * 
	 * @param s the text to append to this console.
	 * @param highlight the optional highlight color.
	 */
	public void append(String s,Color highlight) {
		synchronized(pendingInsertions) {
			pendingInsertions.add( new Insertion(s, highlight) );
			if(!SwingUtilities.isEventDispatchThread()) {
				SwingUtilities.invokeLater(clearQueue);
				return;
			}

			clearQueue.run();
		}
	}

	private void reapplyErrorHighlighting() {
		getHighlighter().removeAllHighlights();
		for(HighlightRun run : highlightRuns) {
			try {
				getHighlighter().addHighlight(run.start, run.end, new DefaultHighlightPainter(run.highlight));
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
	}
}