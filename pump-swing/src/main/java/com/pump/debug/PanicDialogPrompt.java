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
package com.pump.debug;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.pump.swing.DialogFooter;
import com.pump.swing.QDialog;

/** This controversial class launches a new process to ask the user
 * if they want to try to abort the event dispatch thread (by calling Thread.stop()).
 * <P>There are several legitimate reasons to disparage this class:
 * <ul><li>Thread.stop() has been deprecated for ages, and is unsafe.</li>
 * <li>Launching another process is a tacky solution.</li>
 * <li>The dialog that results is hard for a user to understand.</li></ul>
 * <P>... and the list goes on.  That being said: this class has saved my program
 * before.  (That is: the AWT thread was locked -- because of my own programming
 * blunders -- and this class perked up and restored control for me.  As a result
 * I didn't lose saved work!)
 */
public class PanicDialogPrompt implements AWTPanicListener {

	/** Pass the name of an application as the argument, and this
	 * main() method prompts the user with a dialog asking them
	 * if they want to abort whatever that application is currently doing.
     * @param args the application's arguments. (This is unused.)
	 */
	public static void main(String[] args) {
		if(args==null || args.length==0 || args[0].trim().length()==0) 
			args = new String[] {"Unknown"};

		try {
			String lf = UIManager.getSystemLookAndFeelClassName();
			UIManager.setLookAndFeel(lf);
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
		String name = args[0];
		JButton abortButton = new JButton("Abort");
		abortButton.putClientProperty(DialogFooter.PROPERTY_UNSAFE, Boolean.TRUE);
		DialogFooter footer = new DialogFooter(
				new JComponent[] {},
				new JComponent[] {new JButton("Cancel"), abortButton },
				true,
				null
		);
		int i = QDialog.showDialog( new JFrame(), 
				"Unresponsive Application",  //dialogTitle
				QDialog.getIcon(QDialog.WARNING_MESSAGE),  //icon
				QDialog.createContentPanel("The application \""+name+"\" appears to be unresponsive. Would you like to try to abort its current activity?", 
						"This may result in data loss, but it may temporarily restore control of the application. If this works, you should immediately try to save your work and exit \""+name+"\".", 
						null, //innerComponent 
						true), //selectable
				footer, 
				false,  //closeable
				null, //dontShowKey 
				null); //alwaysApplyKey
		if(i==1) {
			System.out.println("abort");
		} else {
			System.out.println("ignore");
		}
		System.exit(0);
	}
	
	public void AWTPanic(String applicationName) {
		SwingUtilities.invokeLater(new Runnable() {
			//if we recover, we should kill the other process.
			public void run() {
				if(monitorProcess!=null) {
					try {
						monitorProcess.destroy();
					} catch(Throwable t) {}
				}
			}
		});
		askToStopAWTThread(applicationName);
	}


	public static final boolean isMac = (System.getProperty("os.name").toLowerCase().indexOf("mac")!=-1);

	private static Process monitorProcess;
	
	/** This launches another process to prompt the user to cancel
	 * the current activity.  If the other process indicates that
	 * the user wants to cancel, then this method will
	 * call <code>stopAWTThread</code>
	 * @param applicationName the name to present to the user
	 * of the application this might affect.
	 */
	public static void askToStopAWTThread(String applicationName) {
		if(applicationName==null || applicationName.trim().length()==0)
			applicationName = "Unknown";
		try {
			String classpath = System.getProperty("java.class.path");
			if(isMac) {
				monitorProcess = Runtime.getRuntime().exec(new String[] {
						"java",
						"-Xdock:name="+applicationName+" Monitor",
						"-classpath",
						classpath,
						"com.bric.debug.PanicDialogPrompt",
						applicationName
				});
			} else {
				monitorProcess = Runtime.getRuntime().exec(new String[] {
						"java",
						"-classpath",
						classpath,
						"com.bric.debug.PanicDialogPrompt",
						applicationName
				});
			}
			monitorProcess.waitFor();
			InputStream in = monitorProcess.getInputStream();
			monitorProcess = null;
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			try {
				String s = br.readLine();
				if(s.equals("abort")) {
					stopAWTThread();
				}
			} catch(IOException e) {
				//this can happen if the stream is closed
			}
			return;
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	
	/** This uses the unsafe <code>Thread.stop()</code> method
	 * on the AWT thread to try to regain control of the application.
	 */
	@SuppressWarnings("deprecation")
	public static void stopAWTThread() {
		Thread[] array = new Thread[Thread.activeCount()];
		Thread.enumerate(array);
		for(int a = 0; a<array.length; a++) {
			System.out.println(array[a].getName());
			if(array[a].getName().indexOf("AWT-EventQueue-")==0) {
				try {
					array[a].stop();
					System.err.println("Reset the AWT thread.");
				} catch(Throwable t) {
					System.err.println("Attempted unsuccessfully to stop the AWT thread.");
				}
			}
		}
	}
	
}