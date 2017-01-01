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
package com.pump.animation;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.JPanel;

/** This is an experiment to reduce applet flickering.
 */
public abstract class BufferedAnimationPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	
	class RefreshRunnable implements Runnable {
		int w, h;
		RefreshRunnable(int width,int height) {
			w = Math.max(1, width);
			h = Math.max(1, height);
		}
		
		public void run() {
			try {
				synchronized(synchronizationLock) {
					//if there's another RefreshRunnable queued up, then abort:
					if(currentRunnable!=this) {
						return;
					}
				}
				BufferedImage refreshedBuffer = getBuffer(w, h);
				Graphics2D g = refreshedBuffer.createGraphics();
				g.clipRect(0, 0, w, h);
				paintAnimation(g, w, h);
				g.dispose();
				
				synchronized(synchronizationLock) {
					BufferedImage prevBuffer = buffer;
					buffer = refreshedBuffer;
					storeBuffer(prevBuffer);
				}
				
				repaint();
			} catch(Throwable t) {
				t.printStackTrace();
			}
		}
	}
	
	BufferedImage buffer;
	Dimension size = new Dimension(0, 0);
	Runnable currentRunnable;
	Executor executor = Executors.newSingleThreadExecutor();
	Object synchronizationLock = new Object();
	
	public BufferedAnimationPanel() {
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				setBufferSize(getWidth(), getHeight());
			}
		});
		setDoubleBuffered(false);
	}
	
	protected abstract void paintAnimation(Graphics2D g,int width,int height);
	
	/** Called exclusively on the EDT.
	 * 
	 */
	private void setBufferSize(int w,int h) {
		synchronized(synchronizationLock) {
			if(size==null || size.width!=w || size.height!=h) {
				size = new Dimension(w, h);
				refresh();
			}
		}
	}
	
	private BufferedImage spareImage;
	private BufferedImage getBuffer(int w,int h) {
		BufferedImage returnValue;
		if(spareImage==null || spareImage.getWidth()!=w || spareImage.getHeight()!=h) {
			returnValue = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		} else {
			returnValue = spareImage;
		}
		spareImage = null;
		return returnValue;
	}
	
	private void storeBuffer(BufferedImage bi) {
		spareImage = bi;
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		BufferedImage currentBuffer = buffer;
		if(currentBuffer!=null) {
			synchronized(currentBuffer) {
				g.drawImage(currentBuffer, 0, 0, null);
			}
		} else {
			refresh();
		}
	}
	
	public void refresh() {
		synchronized(synchronizationLock) {
			RefreshRunnable refreshRunnable = new RefreshRunnable(size.width, size.height);
			currentRunnable = refreshRunnable;
			executor.execute(refreshRunnable);
		}
	}
}