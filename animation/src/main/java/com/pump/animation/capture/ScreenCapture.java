/*
 * @(#)ScreenCapture.java
 *
 * $Date: 2016-01-30 19:07:08 -0500 (Sat, 30 Jan 2016) $
 *
 * Copyright (c) 2011 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Jeremy Wood. For details see accompanying license terms.
 * 
 * This software is probably, but not necessarily, discussed here:
 * https://javagraphics.java.net/
 * 
 * That site should also contain the most recent official version
 * of this software.  (See the SVN repository for more details.)
 */
package com.pump.animation.capture;

import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;

import com.pump.animation.AnimationReader;
import com.pump.animation.ResettableAnimationReader;
import com.pump.awt.MouseTracker;
import com.pump.image.ImageLoader;
import com.pump.io.IOUtils;

/** This creates an animation recording the screen.
 * <P>This ties into several Java-level components: it uses
 * a <code>RepaintManager</code> and two different <code>AWTEventListeners</code>.
 * Because of this, it is pretty efficient at only capturing/recording
 * what data is appropriate.  However it does not capture data that happens
 * outside of Java's control/awareness.
 * <P>Note this class is abstract, and it does not actually capture pixels from
 * the screen.  That is up to subclasses to implement.  The <code>RobotScreenCapture</code>
 * class achieves this using the <code>Robot</code> object, and the
 * <code>ComponentPaintCapture</code> relies on the <code>Component.paint()</code> method.
 * It is also possible to use 3rd parties. For example: when QTJ was still in use, that
 * could be used to capture pixel data from the screen.
 * <P>Note this assumes there is only 1 monitor of interest to record from.
 */
public abstract class ScreenCapture {

	/** This monitors the areas that Java repaints, and on request it will
	 * relay this information to a <code>ScreenCapture</code> object.
	 *
	 */
	private static class FilteredRepaintManager extends RepaintManager {

		Point p1 = new Point();
		Point p2 = new Point();
		@Override
		public void addDirtyRegion(JComponent c, int x, int y, int w, int h) {
			filterRegion(c,x,y,w,h);
			super.addDirtyRegion(c, x, y, w, h);
		}

		private Rectangle dirtyScreenRect = null;

		private void filterRegion(JComponent c,int x,int y,int w, int h) {
			if(c instanceof JTextComponent) {
				//the first of what will no doubt be many hacks
				//required to make this work correctly:
				x = 0;
				y = 0;
				w = c.getWidth();
				h = c.getHeight();
			}
			p1.x = x;
			p1.y = y;
			p2.x = x+w;
			p2.y = y+h;

			SwingUtilities.convertPointToScreen(p1, c);
			SwingUtilities.convertPointToScreen(p2, c);

			synchronized(this) {
				if(dirtyScreenRect==null) {
					dirtyScreenRect = new Rectangle(p1);
				}
				dirtyScreenRect.add(p1);
				dirtyScreenRect.add(p2);
			}
		}

		/** This retrieves the cached data regarding what parts of the screen
		 * are dirty, and it clears the cached data once it is retrieved.
		 * @return the area of the screen that has been repainted since this
		 * method was last called.
		 */
		protected Rectangle getDirtyScreenRect() {
			synchronized(this) {
				Rectangle returnValue = dirtyScreenRect;
				dirtyScreenRect = null;
				return returnValue;
			}
		}

		@Override
		public void markCompletelyDirty(JComponent component) {
			filterRegion(component,0,0,component.getWidth(),component.getHeight());
			super.markCompletelyDirty(component);
		}

	}

	/** A tiny snapshot of screen-related activity.
	 * <P>Several <code>Record</code> object strung together
	 * can be used to reconstruct a movie of the user's activity.
	 */
	private static class Record {
		long time;
		Rectangle r;
		File file;
		int x = MouseTracker.getX();
		int y = MouseTracker.getY();
		boolean pressed = MouseTracker.isButtonPressed();

		public Record(long time,Rectangle r,File file) {
			this.time = time;
			this.r = r;
			this.file = file;
		}
	}

	/** A subdirectory of temp files that is deleted on exit. */
	protected static File tempDir = null;
	static {
		try {
			tempDir = IOUtils.getUniqueFile(new File(System.getProperty("java.io.tmpdir")), "capture", false, true);
			if(!tempDir.mkdirs())
				throw new RuntimeException("mkdir failed for "+tempDir.getAbsolutePath());
			Runtime.getRuntime().addShutdownHook(new Thread() {
				public void run() {
					IOUtils.delete(tempDir);
				}
			});
		} catch(RuntimeException e) {
			throw e;
		} catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	/** This iterates through your animation one frame at a time.
	 */
	private static class ScreenCaptureAnimation implements ResettableAnimationReader {
		List<Record> list;
		int frameIndex = 0;
		Rectangle captureBounds;
		boolean loop;
		double playbackRate;

		public ScreenCaptureAnimation(Rectangle captureBounds,List<Record> snapshots,boolean loop,double playbackRate) {
			this.list = snapshots;
			this.captureBounds = captureBounds;
			this.loop = loop;
			this.playbackRate = playbackRate;
		}

		public double getDuration() {
			double sum = 0;
			for(int a = 0; a<list.size(); a++) {
				sum += getFrameDuration(a);
			}
			return sum;
		}

		public int getFrameCount() {
			return list.size()-1;
		}

		public double getFrameDuration() {
			return getFrameDuration(frameIndex);
		}

		private double getFrameDuration(int i) {
			long i1 = (list.get(i)).time;
			long i2 = (i+1)==list.size() ? i1+1000 : (list.get(i+1)).time;
			double elapsed = i2 - i1;
			elapsed = elapsed / 1000.0;
			return elapsed / playbackRate;
		}

		public int getHeight() {
			return captureBounds.height;
		}

		public int getLoopCount() {
			return loop ? AnimationReader.LOOP_FOREVER  : 1;
		}

		public int getWidth() {
			return captureBounds.width;
		}

		PaintedCursor cursor = new PaintedCursor();

		BufferedImage lastImage = null;
		public BufferedImage getNextFrame(boolean cloneImage) throws IOException {
			if(lastImage==null) {
				Record r = list.get(frameIndex);
				lastImage = ImageIO.read(r.file);
			}
			frameIndex++;
			if(frameIndex==list.size())
				return null;

			Record r = list.get(frameIndex);
			if(cloneImage) {
				BufferedImage img = new BufferedImage(lastImage.getWidth(),lastImage.getHeight(),BufferedImage.TYPE_INT_RGB);
				Graphics2D g = img.createGraphics();
				g.drawImage(lastImage, 0, 0, null);
				g.dispose();
				lastImage = img;
			}
			Graphics g = lastImage.getGraphics();
			g.translate(-captureBounds.x, -captureBounds.y);
			cursor.restore(g);

			if(r.file!=null) {
				BufferedImage bi = ImageIO.read(r.file);
				try {
					g.drawImage(bi, r.r.x, r.r.y, null);
				} catch(RuntimeException e) {
					System.err.println(r.r+", "+bi.getWidth()+"x"+bi.getHeight());
					throw e;
				}
			}
			g.dispose();

			cursor.assign(r.x, r.y, r.pressed, lastImage);

			g = lastImage.getGraphics();
			g.translate(-captureBounds.x, -captureBounds.y);
			cursor.paint(g);
			g.dispose();
			return lastImage;
		}

		class PaintedCursor {
			BufferedImage cursorImage = ImageLoader.createImage(ScreenCapture.class.getResource("cursor.png"));
			BufferedImage cursorClickedImage = ImageLoader.createImage(ScreenCapture.class.getResource("cursorClicked.png"));
			int hotX = 11;
			int hotY = 9;

			int x = -1;
			int y = -1;
			boolean pressed = false;
			BufferedImage lastImage = null;

			public void restore(Graphics g) {
				try {
					if(lastImage!=null) {
						g.drawImage(lastImage, x-hotX, y-hotY, null);
					}
				} catch(RuntimeException e) {
					e.printStackTrace();
				}
			}

			public void paint(Graphics g) {
				g.drawImage(pressed ? cursorClickedImage : cursorImage, 
						x-hotX, y-hotY, null);
			}

			public void assign(int x,int y,boolean pressed,BufferedImage bi) {
				this.x = x;
				this.y = y;
				this.pressed = pressed;
				if(lastImage==null) {
					lastImage = new BufferedImage(cursorImage.getWidth(),cursorImage.getHeight(),BufferedImage.TYPE_INT_RGB);
				}

				Graphics2D g = lastImage.createGraphics();
				g.drawImage(bi,-x+hotX+captureBounds.x,-y+hotY+captureBounds.y,null);
				g.dispose();
			}
		}

		public void reset() {
			frameIndex = 0;
		}
	}

	FilteredRepaintManager filteredRepaintManager = new FilteredRepaintManager();

	/** The thread constantly capturing data. */
	Thread capture;

	/** Whether <code>stop</code> has been called. */
	boolean stop = true;

	boolean targetedRepaints = true;

	double playbackRate = 1;

	/** A list of <code>Record</code> objects used to reconstruct an animation. */
	List<Record> snapshots = new ArrayList<Record>();

	/** Adds <code>Record</code> objects when the mouse moves (or is pressed) */
	ChangeListener mouseListener = new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
			if(stop) return;
			if(targetedRepaints) {
				addSnapshot(new Record(System.currentTimeMillis(),null,null));
			} else {
				try {
					record(captureBounds);
				} catch(Exception e2) {
					addSnapshot(new Record(System.currentTimeMillis(),null,null));
					e2.printStackTrace();
				}
			}
		}
	};

	/** Adds a <code>Record</code> object. */
	private synchronized void addSnapshot(Record r) {
		/** Inside we check to see if we should merge this record with
		 * the previous.  Also if two Record objects were created slightly
		 * out of sync (so that the time stamp of the new one is BEFORE
		 * the existing one) this tries to merge those.
		 */
		if(snapshots.size()>0) {
			Record last = snapshots.get(snapshots.size()-1);
			if(last.time==r.time) {
				if(last.r==null && r.r!=null) {
					last.r = r.r;
					last.file = r.file;
					return;
				} else if(last.r!=null && r.r==null) {
					return;
				} else if(last.r==null && r.r==null) {
					return;
				}
			}
			if(r.file==null && last.time>r.time)
				return;
			if(last.file==null && last.time>r.time) {
				last.file = r.file;
				last.r = r.r;
				return;
			}
			if(last.time>r.time) {
				Rectangle r2 = r.r;
				r2.add(last.r);
				try {
					record(r2);
				} catch(Exception e) {
					e.printStackTrace();
				}
				return;
			}
		}
		snapshots.add(r);
	}

	Runnable captureThreadRunnable = new Runnable() {
		public void run() {
			while(stop==false) {
				SwingUtilities.invokeLater(new CaptureRunnable());
				try {
					Thread.sleep(20);
				} catch(Exception e) {
					Thread.yield();
				}
			}
		}
	};

	private Rectangle captureBounds = new Rectangle(0,0,
			Toolkit.getDefaultToolkit().getScreenSize().width,
			Toolkit.getDefaultToolkit().getScreenSize().height);


	/** Constructs a <code>ScreenCapture</code> object that will
	 * record only the rectangle provided.
	 * @param captureBounds the rectangle to record within.
	 */
	public ScreenCapture(Rectangle captureBounds) {
		this();
		this.captureBounds = (Rectangle)captureBounds.clone();
	}

	/** Constructs a <code>ScreenCapture</code> object that will
	 * record the entire screen.
	 */
	public ScreenCapture() {
		//I tried Toolkit.addEventListener() and Toolkit.getSystemEventQueue().push()
		//and neither approach would reveal PaintEvents to me.

		//this approach, however, will tell me what's repainted:
		Runnable repaintRunnable = new Runnable() {
			public void run() {
				RepaintManager.setCurrentManager(filteredRepaintManager);
			}
		};
		SwingUtilities.invokeLater(repaintRunnable);

		Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
			Map<Component, Rectangle> table = new HashMap<Component, Rectangle>();
			public void eventDispatched(AWTEvent event) {
				if(stop) {
					table.clear();
					return;
				}

				if(event instanceof ComponentEvent) {
					ComponentEvent c = (ComponentEvent)event;
					Component t = c.getComponent();
					Point p = new Point(0,0);
					SwingUtilities.convertPointToScreen(p, t);
					Rectangle r = new Rectangle(p.x,p.y,t.getWidth(),t.getHeight());
					r.setFrame(r.x-12,r.y-12,r.width+24,r.height+30); //on Mac OS, allow for shadows.
					Rectangle repaintArea = r;

					/** Can't rely on JComponents and the clientProperties here,
					 * because tooltips can take place in a heavyweight window
					 */
					Rectangle lastBounds = table.get(t);
					if(lastBounds!=null) {
						repaintArea = r.union(lastBounds);
					}
					table.put(t, (Rectangle)r.clone());

					SwingUtilities.invokeLater(new CaptureRunnable(repaintArea));
				}
			}

		}, AWTEvent.COMPONENT_EVENT_MASK);
	}

	/** Control whether this stores only targeted repaints (true) or
	 * whether this aggressively repaints everything constantly (false).
	 * The default is <code>true</code>.
	 * <p>This serves as a safety net. Ideally it shouldn't be necessary.
	 */
	public void setTargetedRepaints(boolean targetedRepaints) {
		this.targetedRepaints = targetedRepaints;
	}

	/** Moves capturing activity into the AWT thread. */
	class CaptureRunnable implements Runnable {
		Rectangle r;

		public CaptureRunnable() {
		}
		public CaptureRunnable(Rectangle r) {
			this.r = r;
		}

		public void run() {
			if(r==null)
				r = filteredRepaintManager.getDirtyScreenRect();
			try {
				record(r);
			} catch(Exception e) {
				e.printStackTrace();
				stop(false);
			}
		}
	}

	/** Return the playback rate. By default this property is 1, but you can accelerate or decelerate
	 * it if desired. Note this properties is applied to the entire recording (you can't change
	 * it mid-recording and change playback speed for only part of the animation).
	 */
	public double getPlaybackRate() {
		return playbackRate;
	}

	/** Set the playback rate. By default this property is 1, but you can accelerate or decelerate
	 * it if desired. Note this properties is applied to the entire recording (you can't change
	 * it mid-recording and change playback speed for only part of the animation).
	 */
	public void setPlaybackRate(double playbackRate) {
		this.playbackRate = playbackRate;
	}

	/** This begins recording a movie.
	 */
	public void start() {
		try {
			synchronized(this) {
				if(capture!=null && capture.isAlive())
					throw new RuntimeException("Capture is already running.");
				capture = new Thread(captureThreadRunnable);
				Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
				snapshots.clear();
				record(new Rectangle(0,0,d.width,d.height));
				stop = false;
				capture.start();
				MouseTracker.addChangeListener(mouseListener);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	private void record(Rectangle r) throws Exception {
		//before recording anything, make sure we have pixels that
		//are within our area of interest
		if(r!=null && r.width>0 && r.height>0) {
			r = r.intersection(captureBounds);
			if(r.width>0 && r.height>0) {
				long time = System.currentTimeMillis();
				File file = capture(r);
				addSnapshot(new Record(time,r,file));
			}
		}
	}

	/** Stops recording the screen content, and returns an animation iterator. 
	 * 
	 * @param loop true if the animation this returns should loop.
	 */
	public ResettableAnimationReader stop(boolean loop) {
		stop = true;
		MouseTracker.removeChangeListener(mouseListener);
		return new ScreenCaptureAnimation(captureBounds,snapshots,loop,playbackRate);
	}

	/** This captures a portion of the screen and returns a File
	 * representing that image.
	 * @param r the rectangle to capture
	 * @return a file representing the image captured.  This should be
	 * of a "normal" image format (PNG, JPEG, etc.)
	 * @throws Exception
	 */
	protected abstract File capture(Rectangle r) throws Exception;
}
