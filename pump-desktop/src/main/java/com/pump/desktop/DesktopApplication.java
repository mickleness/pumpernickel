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
package com.pump.desktop;

import java.awt.Frame;
import java.io.IOException;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.debug.AWTMonitor;
import com.pump.desktop.cache.CacheManager;
import com.pump.desktop.error.BugReporter;
import com.pump.desktop.error.ErrorDialogThrowableHandler;
import com.pump.desktop.error.ErrorManager;
import com.pump.desktop.logging.SessionLog;
import com.pump.desktop.temp.TempFileManager;
import com.pump.util.JVM;
import com.pump.window.WindowList;

public class DesktopApplication {
	
	/**
	 * 
	 * @param qualifiedAppName a String such as "com.apple.GarageBand"
	 * @param simpleAppName a String such as "GarageBand"
	 * @param version a String representing the version. This should only contain letters, numbers, periods and/or hyphens.
	 * @param supportEmail if non-null then error dialogs will include a "Report..." button to help users
	 * email tech support about exceptions that occur during a session.
	 * @param frameClass if non-null then we'll add a listener that guarantees to exit the app after all Frames of the type frameClass
	 * are closed. (This does not in any way help manage saving dirty windows, this just makes sure the process doesn't silently linger
	 * after the user has closed all UI components.) As a default you can just use Frame.class here, but if you are focused
	 * on a very specific extension of JFrame you can use that subclass.
	 * @throws IOException
	 */
	public static void initialize(final String qualifiedAppName,final String simpleAppName,final String version,final String supportEmail,final Class<?> frameClass) throws IOException {
		System.setProperty("apple.laf.useScreenMenuBar", "true");
	    
	    SessionLog.initialize(qualifiedAppName, 5);
	    System.out.println("DesktopApplication.initialize( "+qualifiedAppName+", "+simpleAppName+", "+version+", "+supportEmail+", "+
	    		(frameClass==null ? "null" : frameClass.getName()) +")");
	    JVM.printProfile();
	    ErrorManager.initialize(simpleAppName);
	    TempFileManager.initialize(qualifiedAppName);
		AWTMonitor.installAWTListener(simpleAppName, false);
		CacheManager.initialize(qualifiedAppName, version);
		
		while(true) {
			/*
			 * Really early in initialization: talking to SwingUtilities can throw a NPE if the EDT
			 * isn't initialized yet.
			 */
			try {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						try {
							String lf = UIManager.getSystemLookAndFeelClassName();
							UIManager.setLookAndFeel(lf);
						} catch (Throwable e) {
							e.printStackTrace();
						}
		
						try {
							if(supportEmail!=null) {
								BugReporter.initialize(qualifiedAppName, simpleAppName, supportEmail);
								ErrorDialogThrowableHandler edth = ErrorManager.getDefaultErrorHandler();
								edth.addLeftComponent( BugReporter.get().createPanel(edth) );
							}
						} catch(IOException e) {
							e.printStackTrace();
						}
					}
				});
				break;
			} catch(RuntimeException e) {}
		}


		if(frameClass!=null) {
			// be sure to exit the JVM when all windows are closed:
			WindowList.addChangeListener(new ChangeListener()
			{
	
				@Override
				public void stateChanged(ChangeEvent e)
				{
					Frame[] frames = WindowList.getFrames(false, false, true);
					for (Frame frame : frames)
					{
						if (frame.isShowing() && frameClass.isInstance(frame))
							return;
					}
					System.exit(0);
				}
	
			});
		}
	}
}