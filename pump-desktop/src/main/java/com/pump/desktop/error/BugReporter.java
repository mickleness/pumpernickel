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
package com.pump.desktop.error;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.dnd.DnDConstants;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

import com.pump.awt.dnd.FileLabel;
import com.pump.desktop.cache.CacheManager;
import com.pump.desktop.logging.SessionLog;
import com.pump.io.IOUtils;
import com.pump.swing.JThrobber;

public class BugReporter {
	private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy.MM.dd 'at' HH.mm.ss z");

	/** If true then the session log is embedded in incident archives. */
	public static boolean includeSessionLog = true;
	
	/** If true then users documents are embedded in incident archives.
	 * (The file is identified by searching for the client property "Window.documentFile"
	 * on the root pane of frames.)
	 */
	public static boolean includeDocuments = true;
	/** If true then screenshots are embedded in incident archives. */
	public static boolean includeScreenshots = true;
	/** Only embed documents in incident archives if they are under this file size (measured in bytes) */
	public static float documentSizeLimit = 1024*1024;
	
	private static BugReporter GLOBAL = null;
	
	public static synchronized void initialize(String qualifiedAppName, String simpleAppName,String recipient) throws IOException {
		if(GLOBAL!=null)
			throw new IllegalStateException("BugReporter.initialize was already called for "+GLOBAL.qualifiedAppName+", "+GLOBAL.simpleAppName+", "+GLOBAL.recipient);
		GLOBAL = new BugReporter(qualifiedAppName, simpleAppName, recipient);
	}
	
	public static synchronized BugReporter get() {
		return GLOBAL;
	}
	
	String qualifiedAppName, simpleAppName, recipient;
	File incidentDir;

	private BugReporter(String qualifiedAppName, String simpleAppName,String recipient) throws IOException {
		GLOBAL = this;
		this.qualifiedAppName = qualifiedAppName;
		this.simpleAppName = simpleAppName;
		this.recipient = recipient;
		
		incidentDir = new File(CacheManager.get().getDirectory(false), "Incidents");
		if(!incidentDir.exists()) {
			if(!incidentDir.mkdirs())
				throw new IOException("mkdirs failed for "+incidentDir.getAbsolutePath());
		}
		System.out.println("BugReporter initialized using: "+incidentDir.getAbsolutePath());
		File[] children = incidentDir.listFiles();
		Date oneWeekAgo = new Date();
		oneWeekAgo.setTime(oneWeekAgo.getTime()-1000*60*60*24*7);
		for(File child : children) {
			String filename = child.getName();
			if(filename.endsWith(".zip")) {
				filename = filename.substring(0, filename.length() - ".zip".length());
				try {
					Date date = DATE_FORMAT.parse(filename);
					if(date.before(oneWeekAgo)) {
						if(IOUtils.delete(child)) {
							System.out.println("BugReporter deleted \""+child.getName()+"\"");
						} else {
							System.err.println("BugReporter could not delete \""+child.getName()+"\"");
						}
					}
				} catch(Exception e) {
					e.printStackTrace();
					System.err.println("BugReporter could not parse \""+child.getName()+"\"");
				}
			}
		}
	}

	static int threadCtr = 0;
	
	class ReportPanel extends JPanel {

		private static final long serialVersionUID = 1L;

		JButton button = new JButton("Report...");
		JThrobber throbber = new JThrobber();
		JLabel errorLabel = new JLabel("Error");
		FileLabel fileLabel = new FileLabel( DnDConstants.ACTION_COPY );
		
		public ReportPanel(final ErrorDialogThrowableHandler dialogThrowableHandler) {
			super(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0; c.gridy = 0; c.weightx = 1; c.weighty = 1;
			add(button, c);
			c.gridx++; c.insets = new Insets(0,4,0,4);
			add(throbber, c);
			c.gridx++; c.insets = new Insets(0,0,0,0);
			add(errorLabel, c);
			c.gridx++;
			add(fileLabel, c);
			
			addAncestorListener(new AncestorListener() {

				@Override
				public void ancestorAdded(AncestorEvent event)
				{
					refresh();
				}

				@Override
				public void ancestorRemoved(AncestorEvent event)
				{
					refresh();
				}

				@Override
				public void ancestorMoved(AncestorEvent event)
				{
					refresh();
				}
				
				boolean wasShowing = false;
				private void refresh() {
					boolean isShowing = isShowing();
					if(isShowing && (!wasShowing)) {
						reset();
					}
					wasShowing = isShowing;
				}
				
			});
			
			button.addActionListener(new ActionListener() {
				
				Runnable createArchive = new Runnable() {
					public void run() {
						try {
							String filename = DATE_FORMAT.format(new Date())+".zip";
							final File zipFile = new File(incidentDir, filename);
							
							final ThrowableDescriptor[] throwables = dialogThrowableHandler==null ? 
									new ThrowableDescriptor[] {} :
								dialogThrowableHandler.getThrowables();
							writeIncidentReportArchive(zipFile, throwables);
							
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									button.setVisible(false);
									throbber.setVisible(false);
									errorLabel.setVisible(false);
									fileLabel.setFile(zipFile);
									fileLabel.setText("Report.zip");
									fileLabel.setVisible(true);
									
									try {
										StringBuffer uri = new StringBuffer();
										uri.append("mailto:");
										uri.append( URLEncoder.encode(recipient, "UTF-8"));
										uri.append("?subject=");
										uri.append( encode(simpleAppName+" Problem"));
										StringBuffer body = new StringBuffer();
										body.append("Please drag the zip archive from the dialog to this message, add any helpful comments or descriptions, and click \"Send\".\n\n");
										for(int a = 0; a<throwables.length; a++) {
											if(throwables[a].getUserFriendlyMessage()!=null)
												body.append( throwables[a].getUserFriendlyMessage()+"\n" );
											body.append( throwables[a].throwable.getMessage()+"\n" );
											body.append( ErrorManager.getStackTrace(throwables[a].throwable)+"\n" );
										}
										uri.append("&body=");
										uri.append( encode(body.toString()) );
										URI u = new URI(uri.toString());
										Desktop.getDesktop().mail(u);
									} catch(URISyntaxException | IOException e) {
										e.printStackTrace();
									}
								}
							});
						} catch(Throwable t) {
							System.err.println("BugReporter: the following error occurred while trying to prepare an incident report:");
							System.err.println(ErrorManager.getStackTrace(t));
							SwingUtilities.invokeLater(new Runnable() {
								public void run() {
									button.setVisible(false);
									throbber.setVisible(false);
									errorLabel.setVisible(true);
									fileLabel.setVisible(false);
								}
							});
						}
					}
				};
				
				public void actionPerformed(ActionEvent e) {
					throbber.setVisible(true);
					Thread createArchiveThread = new Thread(createArchive, "BugReporter - create archive "+(threadCtr++));
					createArchiveThread.start();
				}
			});
			
			reset();
		}
		
		public void reset() {
			button.setVisible(true);
			throbber.setVisible(false);
			errorLabel.setVisible(false);
			fileLabel.setVisible(false);
		}
		
	}
	
	protected void writeIncidentReportArchive(File zipFile,ThrowableDescriptor[] throwables) {
		
		try(FileOutputStream fileOut = new FileOutputStream(zipFile)) {
			try(final ZipOutputStream zipOut = new ZipOutputStream(fileOut)) {
				zipOut.putNextEntry(new ZipEntry("summary.txt"));
				zipOut.write( getSummary(throwables).getBytes("UTF-8") );
				zipOut.closeEntry();
				
				if(includeSessionLog) {
					SessionLog sessionLog = SessionLog.get();
					if(sessionLog!=null) {
						File sessionLogFile = sessionLog.getFile();
						zipOut.putNextEntry(new ZipEntry(sessionLogFile.getName()));
						IOUtils.write(sessionLogFile, zipOut);
						zipOut.closeEntry();
					}
				}
				
				if(includeScreenshots || includeDocuments) {
					Runnable edtRunnable = new Runnable() {
						public void run() {
							Window[] windows = Window.getWindows();
							try {
								if(includeScreenshots) {
									for(int a = 0; a<windows.length; a++) {
										int width = windows[a].getWidth();
										int height = windows[a].getHeight();
										if(windows[a].isShowing() && width>0 && height>0) {
											BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
											Graphics2D g = bi.createGraphics();
											windows[a].paint(g);
											g.dispose();
											String imageName = "Window "+(a+1);
											if(windows[a] instanceof Frame) {
												imageName += " \""+ ((Frame)windows[a]).getTitle()+"\"";
											}
											zipOut.putNextEntry(new ZipEntry(imageName+".png"));
											ImageIO.write(bi, "png", zipOut);
											zipOut.closeEntry();
										}
									}
									Robot robot = new Robot();
									Dimension size = Toolkit.getDefaultToolkit().getScreenSize();
									Rectangle screenRect = new Rectangle(0,0,size.width,size.height);
									BufferedImage screen = robot.createScreenCapture(screenRect);
									zipOut.putNextEntry(new ZipEntry("Screen.png"));
									ImageIO.write(screen, "png", zipOut);
									zipOut.closeEntry();
								}
							} catch(Exception e) {
								System.err.println("BugReporter: an error occurred while trying to get screenshots.");
							}
							try {
								if(includeDocuments) {
									for(Window window : windows) {
										if(window instanceof JFrame) {
											JFrame jf = (JFrame)window;
											Object t = jf.getRootPane().getClientProperty("Window.documentFile");
											if(t instanceof File) {
												File file = (File)t;
												zipOut.putNextEntry(new ZipEntry(file.getName()));
												if(file.length() < documentSizeLimit) {
													IOUtils.write(file, zipOut);
												} else {
													byte[] b = ("This file was too large to encode ("+file.length()+" byte).").getBytes("UTF-8");
													zipOut.write( b );
												}
												zipOut.closeEntry();
											}
										}
									}
								}
							} catch(Exception e) {
								System.err.println("BugReporter: an error occurred while trying to embed documents.");
							}
						}
					};
					if(SwingUtilities.isEventDispatchThread()) {
						edtRunnable.run();
					} else {
						try {
							SwingUtilities.invokeAndWait(edtRunnable);
						} catch(Exception e) {
							System.err.println("BugReporter: an error occurred while trying to get screenshots.");
						}
					}
				}
			}
		} catch(Exception e) {
			System.err.println("An error occurred preparing the incident report archive.");
		}
	}

	public JComponent createPanel(ErrorDialogThrowableHandler dialogThrowableHandler) {
		return new ReportPanel(dialogThrowableHandler);
	}
	
	private String getSummary(ThrowableDescriptor[] t) {
		StringBuffer sb = new StringBuffer();
		for(ThrowableDescriptor t2 : t) {
			if(t2.getUserFriendlyMessage()!=null)
				sb.append(t2.getUserFriendlyMessage()+"\n");
			sb.append(t2.throwable.getMessage()+"\n");
			sb.append( ErrorManager.getStackTrace(t2.throwable) );
			sb.append("\n\n");
		}
		return sb.toString();
	}
	
	private static String encode(String s) throws UnsupportedEncodingException {
		StringBuffer sb = new StringBuffer();
		for(int a = 0; a<s.length(); a++) {
			char ch = s.charAt(a);
			if( (ch>='a' && ch<='z') || (ch>='A' && ch<='Z') || (ch>='0' && ch<='9')) {
				sb.append(ch);
			} else {
				String s2 = Integer.toHexString((int)ch);
				if(s2.length()==1) {
					sb.append("%0"+s2);
				} else if(s2.length()==2) {
					sb.append("%"+s2);
				} else {
					//TOOD: what happens to more complex chars?
				}
			}
		}
		return sb.toString();
	}
	
	public String getSimpleAppName() {
		return simpleAppName;
	}
	
	public String getQualifiedAppName() {
		return qualifiedAppName;
	}
	
	public File getIncidentDirectory() {
		return incidentDir;
	}

	public String getRecipient() {
		return recipient;
	}
}