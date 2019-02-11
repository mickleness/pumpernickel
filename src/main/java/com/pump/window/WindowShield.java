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
package com.pump.window;

import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Stack;

import javax.swing.JFrame;
import javax.swing.JWindow;
import javax.swing.RootPaneContainer;
import javax.swing.Timer;
import javax.swing.event.MouseInputAdapter;

/**
 * This is a simple way to shield JWindows and JFrames from receiving
 * MouseEvents when the windows are inactive.
 * <P>
 * Originally this was inspired by this post: <BR>
 * http://lists.apple.com/archives/java-dev/2009/Nov/msg00156.html
 */
public class WindowShield {

	private static final MouseInputAdapter mouseListener = new MouseInputAdapter() {

		@Override
		public void mouseClicked(MouseEvent e) {
			e.consume();
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			e.consume();
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			e.consume();
		}

		@Override
		public void mouseExited(MouseEvent e) {
			e.consume();
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			e.consume();
		}

		@Override
		public void mousePressed(MouseEvent e) {
			e.consume();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			e.consume();
		}

	};

	private static Stack<RootPaneContainer> removeList = new Stack<RootPaneContainer>();
	private static Stack<RootPaneContainer> installList = new Stack<RootPaneContainer>();

	private static Timer timer = new Timer(50, new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			long t = System.currentTimeMillis();
			while (installList.size() > 0) {
				RootPaneContainer rpc = installList.pop();
				rpc.getGlassPane().setVisible(true);
			}
			// when removing we have a little extra precaution:
			int a = 0;
			while (a < removeList.size()) {
				RootPaneContainer rpc = removeList.get(a);
				long timeStamp = ((Long) rpc.getRootPane().getClientProperty(
						"WindowShield.removeTimeStamp")).longValue();
				if (t - timeStamp > 100) {
					removeList.remove(a);
					rpc.getGlassPane().setVisible(false);
				} else {
					a++;
				}
			}

			if (removeList.size() == 0 && installList.size() == 0) {
				timer.stop();
			}
		}
	});

	/**
	 * Removes the shield from a RootPaneContainer.
	 */
	private static void removeShield(RootPaneContainer rpc) {
		/**
		 * If we call rpc.getGlassPane().setVisible(false) in this method body,
		 * then the shield is removed too soon. We need to delay it just a
		 * smidge.
		 */
		installList.remove(rpc);
		rpc.getRootPane().putClientProperty("WindowShield.removeTimeStamp",
				new Long(System.currentTimeMillis()));
		removeList.add(rpc);
		timer.start();
	}

	private static void installShield(RootPaneContainer rpc) {
		removeList.remove(rpc);
		installList.add(rpc);
		timer.start();
	}

	/**
	 * This adds a MouseListener to the glass pane of the window, and a
	 * WindowListener to the window itself. Whenever the window is deactivated
	 * the glass pane is made visible and events are blocked. When reactivated
	 * the glass pane is removed so events will behave normally.
	 */
	public static void install(JWindow w) {
		install2(w);
	}

	/**
	 * This adds a MouseListener to the glass pane of the frame, and a
	 * WindowListener to the window itself. Whenever the frame is deactivated
	 * the glass pane is made visible and events are blocked. When reactivated
	 * the glass pane is removed so events will behave normally.
	 */
	public static void install(JFrame f) {
		install2(f);
	}

	private static void install2(RootPaneContainer rpc) {
		rpc.getGlassPane().addMouseListener(mouseListener);
		rpc.getGlassPane().addMouseMotionListener(mouseListener);
		((Window) rpc).addWindowListener(new WindowListener() {
			public void windowActivated(WindowEvent e) {
				removeShield((RootPaneContainer) e.getWindow());
			}

			public void windowClosed(WindowEvent e) {
			}

			public void windowClosing(WindowEvent e) {
			}

			public void windowDeactivated(WindowEvent e) {
				installShield((RootPaneContainer) e.getWindow());
			}

			public void windowDeiconified(WindowEvent e) {
			}

			public void windowIconified(WindowEvent e) {
			}

			public void windowOpened(WindowEvent e) {
			}

		});

		/**
		 * You could also add the mouseListener object as a MouseWheelListener,
		 * but it's questionable whether you should: 1. On some platforms you're
		 * allowed to scroll in a window that is not the foremost window. 2.
		 * This requires Java 1.5 compatibility. As of this writing this project
		 * still tries to be 1.4 compliant, so reflection would be required.
		 * 
		 */
	}
}