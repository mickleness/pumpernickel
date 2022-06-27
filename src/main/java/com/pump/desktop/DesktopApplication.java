/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.desktop;

import java.awt.Desktop;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Taskbar;
import java.awt.Taskbar.Feature;
import java.awt.desktop.AboutEvent;
import java.awt.desktop.AboutHandler;
import java.awt.desktop.QuitStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Objects;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.data.AbstractAttributeDataImpl;
import com.pump.data.Key;
import com.pump.debug.AWTMonitor;
import com.pump.desktop.cache.CacheManager;
import com.pump.desktop.error.BugReporter;
import com.pump.desktop.error.ErrorDialogThrowableHandler;
import com.pump.desktop.error.ErrorManager;
import com.pump.desktop.logging.SessionLog;
import com.pump.desktop.temp.TempFileManager;
import com.pump.util.JVM;
import com.pump.window.WindowList;

public class DesktopApplication extends AbstractAttributeDataImpl {
	private static final long serialVersionUID = 1L;
	private static DesktopApplication installedApplication;

	/**
	 * Return the currently installed DesktopApplication.
	 */
	public static DesktopApplication get() {
		return installedApplication;
	}

	// list to Window changes and call System.exit(0) if it looks like
	// all our frames are closed.
	static {
		WindowList.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				DesktopApplication app = get();
				if (app == null)
					return;

				Class frameClass = app.getFrameClass();
				if (frameClass == null)
					return;

				Frame[] frames = WindowList.getFrames(false, false, true);
				for (Frame frame : frames) {
					if (frame.isShowing() && frameClass.isInstance(frame))
						return;
				}
				System.exit(0);
			}

		});
	}

	public final static Key<Runnable> KEY_ABOUT_RUNNABLE = new Key<>(
			Runnable.class, "aboutRunnable");
	public final static Key<Object> KEY_MAC_APPLICATION = new Key<>(
			Object.class, "com.apple.eawt.Application");

	public final static Key<String> KEY_APP_QUALIFIED_NAME = new Key<>(
			String.class, "qualifiedName");
	public final static Key<String> KEY_APP_SIMPLE_NAME = new Key<>(
			String.class, "simpleName");
	public final static Key<String> KEY_APP_COPYRIGHT = new Key<>(String.class,
			"copyright");
	public final static Key<String> KEY_APP_VERSION = new Key<>(String.class,
			"version");
	public final static Key<String> KEY_SUPPORT_EMAIL = new Key<>(String.class,
			"supportEmail");
	public final static Key<Class> KEY_FRAME_CLASS = new Key<>(Class.class,
			"frameClass");
	public final static Key<URL> KEY_URL = new Key<>(URL.class, "url");

	/**
	 * Initialize a new DesktopApplication.
	 * 
	 * @param qualifiedAppName
	 *            a String such as "com.apple.GarageBand"
	 * @param simpleAppName
	 *            a String such as "GarageBand"
	 * @param version
	 *            a String representing the version. This should only contain
	 *            letters, numbers, periods and/or hyphens.
	 * @param supportEmail
	 *            an optional email to help automate sending errors to.
	 * @throws IOException
	 */
	public DesktopApplication(String qualifiedAppName, String simpleAppName,
			String version, String supportEmail) throws IOException {

		Objects.requireNonNull(qualifiedAppName);
		Objects.requireNonNull(simpleAppName);
		Objects.requireNonNull(version);

		setAttribute(KEY_APP_QUALIFIED_NAME, qualifiedAppName);
		setAttribute(KEY_APP_SIMPLE_NAME, simpleAppName);
		setAttribute(KEY_APP_VERSION, version);
		setAttribute(KEY_SUPPORT_EMAIL, supportEmail);
		setAttribute(KEY_ABOUT_RUNNABLE, new DefaultAboutRunnable());
		install();
	}

	/**
	 * @return a String such as "com.apple.GarageBand"
	 */
	public String getQualifiedName() {
		return getAttribute(KEY_APP_QUALIFIED_NAME);
	}

	/**
	 * @return a String such as "GarageBand"
	 */
	public String getSimpleName() {
		return getAttribute(KEY_APP_SIMPLE_NAME);
	}

	/**
	 * @return an optional email to help automate sending errors to.
	 */
	public String getSupportEmail() {
		return getAttribute(KEY_SUPPORT_EMAIL);
	}

	/**
	 * @return an optional class of a java.awt.Frame subclass that should
	 *         represent the main windows this app uses.
	 *         <p>
	 *         If this is non-null then a listener will automatically call
	 *         System.exit when all such frames are closed. (This does not in
	 *         any way help manage saving dirty windows, this just makes sure
	 *         the process doesn't silently linger after the user has closed all
	 *         UI components.) As a default you can just use Frame.class here,
	 *         but if you are focused on a very specific extension of JFrame you
	 *         can use that subclass.
	 * 
	 */
	public Class getFrameClass() {
		return getAttribute(KEY_FRAME_CLASS);
	}

	/**
	 * @return a String representing the version. This should only contain
	 *         letters, numbers, periods and/or hyphens.
	 */
	public String getVersion() {
		return getAttribute(KEY_APP_VERSION);
	}

	@Override
	public String toString() {
		return getQualifiedName() + " " + this.getVersion();
	}

	/**
	 * This initializes things like the ErrorManager, TempFileManager and
	 * CacheManager.
	 * <p>
	 * This can only be called once per JVM session.
	 * 
	 * @throws IOException
	 */
	private void install() throws IOException {
		synchronized (DesktopApplication.class) {
			DesktopApplication existing = get();
			if (existing != null && existing != this) {
				throw new IllegalStateException("The application " + existing
						+ " was installed before attempting to install "
						+ this);
			}
			installedApplication = this;
		}

		final long startTime = System.currentTimeMillis();
		System.setProperty("apple.laf.useScreenMenuBar", "true");

		String qualifiedAppName = getQualifiedName();
		String simpleAppName = getSimpleName();
		String version = getVersion();

		SessionLog.initialize(qualifiedAppName, 5);
		System.out.println("DesktopApplication installing " + toString());
		JVM.printProfile();
		ErrorManager.initialize(simpleAppName);
		TempFileManager.initialize(qualifiedAppName);
		AWTMonitor.installAWTListener(simpleAppName, false);
		CacheManager.initialize(qualifiedAppName, version);

		while (true) {
			/*
			 * Really early in initialization: talking to SwingUtilities can
			 * throw a NPE if the EDT isn't initialized yet.
			 */
			try {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						try {
							String lf = UIManager
									.getSystemLookAndFeelClassName();
							UIManager.setLookAndFeel(lf);
						} catch (Throwable e) {
							e.printStackTrace();
						}

						try {
							String supportEmail = getSupportEmail();
							if (supportEmail != null) {
								String qualifiedAppName = getQualifiedName();
								String simpleAppName = getSimpleName();
								BugReporter.initialize(qualifiedAppName,
										simpleAppName, supportEmail);
								ErrorDialogThrowableHandler edth = ErrorManager
										.getDefaultErrorHandler();
								edth.addLeftComponent(
										BugReporter.get().createPanel(edth));
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
				break;
			} catch (RuntimeException e) {
			}
		}

		SwingUtilities.invokeLater(new Runnable() {
			long lastInvocation = -1;
			int invocationCtr = 0;

			public void run() {
				long currentTime = System.currentTimeMillis();
				if (currentTime - lastInvocation < 100) {
					invocationCtr++;
				} else {
					invocationCtr = 0;
				}
				lastInvocation = currentTime;

				if (invocationCtr == 5) {
					long millis = currentTime - startTime;
					System.out.println("Startup took "
							+ NumberFormat.getInstance().format(millis)
							+ " ms");
				} else {
					SwingUtilities.invokeLater(this);
				}
			}
		});

		// TODO: add support for preference dialog, maybe?

		initializeQuitStrategy();
		initializeAboutHandler();
	}

	private void initializeQuitStrategy() {
		if (Desktop.isDesktopSupported()) {
			Desktop d = Desktop.getDesktop();
			if (d.isSupported(Desktop.Action.APP_QUIT_STRATEGY)) {
				d.setQuitStrategy(QuitStrategy.CLOSE_ALL_WINDOWS);
				return;
			}
		}
	}

	private void initializeAboutHandler() {
		if (Desktop.isDesktopSupported()) {
			Desktop d = Desktop.getDesktop();
			if (d.isSupported(Desktop.Action.APP_ABOUT)) {
				d.setAboutHandler(new AboutHandler() {

					@Override
					public void handleAbout(AboutEvent e) {
						Runnable runnable = get().getAboutRunnable();
						if (runnable != null)
							runnable.run();
					}

				});
				d.setQuitStrategy(QuitStrategy.CLOSE_ALL_WINDOWS);
				return;
			}
		}
	}

	/**
	 * Set the runnable to execute when the user selects "About [app name]"
	 */
	public void setAboutRunnable(Runnable runnable) {
		setAttribute(KEY_ABOUT_RUNNABLE, runnable);
	}

	/**
	 * Return the runnable to execute when the user selects "About [app name]".
	 */
	public Runnable getAboutRunnable() {
		return getAttribute(KEY_ABOUT_RUNNABLE);
	}

	/**
	 * Return the optional image associated with this application, or null if
	 * none has been defined.
	 * <p>
	 * This should be a small thumbnail (maybe 60x60) that can be used in an
	 * about dialog or other similar informational presentation.
	 */
	public Image getImage() {
		if (Taskbar.isTaskbarSupported()
				&& Taskbar.getTaskbar().isSupported(Feature.ICON_IMAGE)) {
			return Taskbar.getTaskbar().getIconImage();
		}
		return null;
	}

	/**
	 * Assign the image thumbnail that represents this application.
	 */
	public void setImage(BufferedImage bi) {
		if (Taskbar.isTaskbarSupported()
				&& Taskbar.getTaskbar().isSupported(Feature.ICON_IMAGE))
			Taskbar.getTaskbar().setIconImage(bi);
	}

	/**
	 * Return an optional String describing this application's copyright.
	 */
	public String getCopyright() {
		return getAttribute(KEY_APP_COPYRIGHT);
	}

	/**
	 * Assign a String describing this application's copyright.
	 */
	public void setCopyright(String copyright) {
		setAttribute(KEY_APP_COPYRIGHT, copyright);
	}

	/**
	 * Assing the String describing this application's copyright as: "Copyright
	 * [firstYear]-[currentYear] [author]".
	 * 
	 * @param firstYear
	 *            the first year to list in the copyright's year range.
	 * @param author
	 *            the author (either a person or a company).
	 */
	public void setCopyright(int firstYear, String author) {
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		String yearStr = firstYear == currentYear
				? Integer.toString(currentYear)
				: firstYear + "-" + currentYear;
		String str = "Copyright " + yearStr + " " + author;
		setCopyright(str);
	}

	/**
	 * Set the optional java.awt.Frame class used to identify when this
	 * application still has active frames. If this properties is defined: then
	 * when all active frames of this type are closed this app will
	 * automatically close.
	 */
	public void setFrameClass(Class frameClass) {
		setAttribute(KEY_FRAME_CLASS, frameClass);
	}

	/**
	 * Return an optional URL that offers more information about this app.
	 */
	public URL getURL() {
		return getAttribute(KEY_URL);
	}

	/**
	 * Assign a URL that offers more information about this app. This is used in
	 * the default about dialog.
	 * 
	 * @param url
	 */
	public void setURL(URL url) {
		setAttribute(KEY_URL, url);
	}
}