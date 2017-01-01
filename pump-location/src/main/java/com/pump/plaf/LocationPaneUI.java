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
package com.pump.plaf;

import java.awt.Dialog;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.RootPaneContainer;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.ComponentUI;

import com.pump.icon.ArrowIcon;
import com.pump.icon.PaddedIcon;
import com.pump.icon.TriangleIcon;
import com.pump.io.location.FileLocation;
import com.pump.io.location.IOLocation;
import com.pump.io.location.LocationFactory;
import com.pump.io.location.SearchParameters;
import com.pump.io.location.SearchResults;
import com.pump.swing.JThrobber;
import com.pump.swing.io.LocationBrowser;
import com.pump.swing.io.LocationHistory;
import com.pump.swing.io.LocationPane;
import com.pump.swing.io.OpenLocationPane;

public abstract class LocationPaneUI extends ComponentUI {

	/** This is supplied to the constructor. */
	protected final LocationPane locationPane;
	/** This is created in the constructor. */
	protected final LocationBrowser browser;
	
	protected final JToolBar navigationButtons = new JToolBar();
	protected final JButton commitButton = new JButton();
	protected final JButton cancelButton = new JButton();
	protected final JButton backButton = new JButton(new PaddedIcon(new TriangleIcon(SwingConstants.WEST,8,8),new Insets(1,1,1,1)));
	protected final JButton nextButton = new JButton(new PaddedIcon(new TriangleIcon(SwingConstants.EAST,8,8),new Insets(1,1,1,1)));
	protected final JTextField searchField = new JTextField();
	protected final JButton newFolderButton = new JButton();
	protected final JButton upButton = new JButton(new ArrowIcon(SwingConstants.SOUTH,10,10));
	protected final JThrobber throbber = new JThrobber();
	protected int adjustingComboBox = 0;
	protected final JComboBox comboBox = new JComboBox();
	
	private List<ActionListener> cancelListeners = new ArrayList<ActionListener>();
	private List<ActionListener> commitListeners = new ArrayList<ActionListener>();
	
	private ActionListener controlListener = new ActionListener() {
		Runnable comboBoxRunnable = new Runnable() {
			public void run() {
				IOLocation loc = (IOLocation)comboBox.getSelectedItem();
				locationPane.getLocationHistory().append(loc);
			}
		};
		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			if(src==backButton) {
				locationPane.getLocationHistory().back();
			} else if(src==nextButton) {
				locationPane.getLocationHistory().next();
			} else if(src==upButton) {
				IOLocation d = locationPane.getLocationHistory().getLocation();
				//TODO: this should be in a separate thread:
				d = d.getParent();
				locationPane.getLocationHistory().append(d);
			} else if(src==newFolderButton) {
				createNewFolder();
			} else if(src==comboBox) {
				if(adjustingComboBox>0) return;
				
				SwingUtilities.invokeLater(comboBoxRunnable);
			}
		}
	};
	
	private ChangeListener directoryListener = new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
			updateDirectoryControls();
		}
	};
	
	private ChangeListener selectionListener = new ChangeListener() {
		public void stateChanged(ChangeEvent e) {
			updateSelection();
		}
	};
	
	private DocumentListener searchFieldListener = new DocumentListener() {

		public void changedUpdate(DocumentEvent e) {
			String text = searchField.getText();
			IOLocation loc = locationPane.getLocationHistory().getLocation();
			if(text.length()==0) {
				if(loc instanceof SearchResults) {
					loc = ((SearchResults)loc).getSearchDirectory();
					locationPane.getLocationHistory().replace( loc );
				}
				return;
			}
			
			SearchParameters parameters = new SearchParameters(text);
			//TODO: reinstate SearchParameters
			if(loc instanceof SearchResults) {
				loc = ((SearchResults)loc).getSearchDirectory();
				//locationPane.getLocationHistory().replace( loc.search(parameters) );
			} else {
				//locationPane.getLocationHistory().append( loc.search(parameters) );
			}
			
		}

		public void insertUpdate(DocumentEvent e) {
			changedUpdate(e);
		}

		public void removeUpdate(DocumentEvent e) {
			changedUpdate(e);
		}
	};
	
	public LocationPaneUI(LocationPane locationPane) {
		this.locationPane = locationPane;
		this.browser = new LocationBrowser(locationPane.getSelectionModel(),
				locationPane.getLocationHistory(),
				locationPane.getGraphicCache() );

		searchField.getDocument().addDocumentListener(searchFieldListener);
		
		locationPane.getLocationHistory().addChangeListener(new ChangeListener() {
			Runnable updateText = new Runnable() {
				public void run() {
					IOLocation current = LocationPaneUI.this.locationPane.getLocationHistory().getLocation();
					String newText;
					if(!(current instanceof SearchResults)) {
						newText = "";
					} else {
						//if this SearchResults object was from the result
						//of the back/next arrows, then this text field
						//won't be accurate unless we change it:
						SearchResults sr = (SearchResults)current;
						newText = sr.getSearchText();
					}
					String oldText = searchField.getText();
					if(oldText.equals(newText)==false)
						searchField.setText(newText);
				}
			};
			
			public void stateChanged(ChangeEvent e) {
				SwingUtilities.invokeLater(updateText);
			}
		});
		
		navigationButtons.add(backButton);
		navigationButtons.add(nextButton);
		
		navigationButtons.setFloatable(false);
		
		browser.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				updateProgressBar();
			}
		});
		updateProgressBar();

		comboBox.setRenderer(new IOLabelCellRenderer(comboBox, browser.getGraphicCache()));

		nextButton.addActionListener(controlListener);
		backButton.addActionListener(controlListener);
		upButton.addActionListener(controlListener);
		newFolderButton.addActionListener(controlListener);
		comboBox.addActionListener(controlListener);
		
		locationPane.getLocationHistory().addChangeListener(directoryListener);
		locationPane.getSelectionModel().addChangeListener(selectionListener);

		IOLocation d = locationPane.getLocationHistory().getLocation();
		if(d!=null)
			updateDirectoryControls();


		commitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(commitButtonPressed()) {
					fireCommitListeners();
					Window w = SwingUtilities.getWindowAncestor(commitButton);
					Boolean b = (Boolean)LocationPaneUI.this.locationPane.getClientProperty("saveLocation.commitButton.dismissWindow");
					if(b==null) b = Boolean.TRUE;
					if(b.booleanValue()==false)
						return;
					if(w!=null)
						w.setVisible(false);
				}
			}
		});


		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(cancelButtonPressed()) {
					fireCancelListeners();
					Window w = SwingUtilities.getWindowAncestor(cancelButton);
					Boolean b = (Boolean)LocationPaneUI.this.locationPane.getClientProperty("saveLocation.cancelButton.dismissWindow");
					if(b==null) b = Boolean.TRUE;
					if(b.booleanValue()==false)
						return;
					w.setVisible(false);
				}
			}
		});
		
		updateSelection();
		
		locationPane.addHierarchyListener(new HierarchyListener() {
			boolean wasShowing = false;
			
			Runnable updateRunnable = new Runnable() {
				public void run() {
					boolean isShowing = LocationPaneUI.this.locationPane.isShowing();
					if(isShowing!=wasShowing) {
						setShortcutsActive(isShowing);
						wasShowing = isShowing;
					}
				}
			};
			
			public void hierarchyChanged(HierarchyEvent e) {
				SwingUtilities.invokeLater(updateRunnable);
			}
		});
		setShortcutsActive(false);
	}
	
	public LocationPane getLocationPane() {
		return locationPane;
	}

	protected KeyStroke desktopKeystroke = KeyStroke.getKeyStroke(KeyEvent.VK_D, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
	protected AbstractAction navigateToDesktop = new AbstractAction() {
    	private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			File desktopFile = new File(System.getProperty("user.dir")+File.separator+"Desktop");
			if(desktopFile.exists()) {
				LocationHistory history = LocationPaneUI.this.locationPane.getLocationHistory();
				IOLocation[] list = history.getList();
				for(int a = 0; a<list.length; a++) {
					if(list[a] instanceof FileLocation) {
						IOLocation newLoc = LocationFactory.get().create(desktopFile);
						history.append(newLoc);
						return;
					}
				}
			}
    	}
    };
    
	/** Turn on/off special shortcuts. For example: on Mac command+D should navigate to the desktop.
	 */
	protected void setShortcutsActive(boolean b) {
		Window window = SwingUtilities.getWindowAncestor(locationPane);
		/** T4L Bug 21770 had to do with a normally hidden LocationPaneUI consuming cmd+D
		 * keystrokes. So now we only install these keystrokes if we're visible and
		 * in a dialog...
		 */
		if(window instanceof RootPaneContainer && window instanceof Dialog) {
			RootPaneContainer rpc = (RootPaneContainer)window;
			JRootPane rootPane = rpc.getRootPane();
			if(b) {
				rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(desktopKeystroke, "navigateToDesktop");
				rootPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(desktopKeystroke, "navigateToDesktop");
				rootPane.getActionMap().put("navigateToDesktop", navigateToDesktop);
			} else {
				rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).remove(desktopKeystroke);
				rootPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).remove(desktopKeystroke);
			}
		}
	}

	/** This is the recommended way to know when this dialog has been cancelled.
	 * If you call <code>getCommitButton().addActionListener()</code> you will
	 * be notified when the commit button is clicked, but some UI's might
	 * also check to see if the current file name replaces an existing file
	 * and prompt the user according.
	 */
	public void addCancelListener(ActionListener l) {
		if(cancelListeners.contains(l)) return;
		cancelListeners.add(l);
	}
	
	/** This is the recommended way to know when this dialog has been committed.
	 * If you call <code>getCommitButton().addActionListener()</code> you will
	 * be notified when the commit button is clicked, but some UI's might
	 * also check to see if the current file name replaces an existing file
	 * and prompt the user according.
	 */
	public void addCommitListener(ActionListener l) {
		if(commitListeners.contains(l)) return;
		commitListeners.add(l);
	}
	
	/** This is called automatically when <code>commitButtonPressed()</code> returns <code>true</code>. */
	protected void fireCommitListeners() {
		for(int a = 0; a<commitListeners.size(); a++) {
			ActionListener l = commitListeners.get(a);
			try {
				l.actionPerformed(new ActionEvent(commitButton, ActionEvent.ACTION_PERFORMED, "commit"));
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	/** This is called automatically when <code>commitButtonPressed()</code> returns <code>true</code>. */
	protected void fireCancelListeners() {
		for(int a = 0; a<cancelListeners.size(); a++) {
			ActionListener l = cancelListeners.get(a);
			try {
				l.actionPerformed(new ActionEvent(commitButton, ActionEvent.ACTION_PERFORMED, "commit"));
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/** This method is called when the <code>commitButton</code> is pressed.
	 * @return true this dialog should close normally.  <code>false</code> indicates
	 * that the dialog should not close, and presumably some other action is being taken.
	 * <p>For example, in a save pane if a file with this name is detected: then a
	 * subclass might prompt the user with an error dialog and return <code>false</code>.
	 */
	protected boolean commitButtonPressed() {
		if(locationPane instanceof OpenLocationPane) {
			OpenLocationPane olp = (OpenLocationPane)locationPane;
			if(olp.isDirectory()==false) {
				IOLocation[] locations = locationPane.getSelectionModel().getSelection();
				if(locations.length==1 && locations[0].isDirectory() && locations[0].isNavigable()) {
					locationPane.getLocationHistory().append(locations[0]);
					return false;
				}
				//TODO: what happens if you have multiple directories
				//selected and you click "Open", when directories aren't supported?
			}
		}
		return true;
	}
	
	/** This method is called when the <code>commitButton</code> is pressed.
	 * @return true this dialog should close normally.  <code>false</code> indicates
	 * that the dialog should not close, and presumably some other action is being taken.
	 * <p>For example, if a file with this name is detected: then a subclass might prompt
	 * the user with an error dialog and return <code>false</code>.
	 */
	protected boolean cancelButtonPressed() {
		return true;
	}
	
	public LocationBrowser getBrowser() {
		return browser;
	}
	
	protected void updateSelection() {
		if(locationPane instanceof OpenLocationPane) {
			IOLocation[] loc = locationPane.getSelectionModel().getSelection();
			boolean commitEnabled = loc.length>0;
			if(loc.length==1 && locationPane.isDirectory()==false) {
				//in this case "Open" should always be enabled:
				//either a file is selected (to be opened) or a folder
				//is selected (to be navigated into)
			} else {
				for(int a = 0; a<loc.length; a++) {
					if(locationPane.isDirectory()!=loc[a].isDirectory()) {
						commitEnabled = false;
					}
				}
			}
			commitButton.setEnabled(commitEnabled);
		}
	}
	/** Update the enabled state of the next button.
	 * This is automatically called when the current displayed
	 * directory changes.
	 */
	protected void updateNextButton() {
		nextButton.setEnabled(locationPane.getLocationHistory().hasNext());
	}
	
	/** Update the enabled state of the back button.
	 * This is automatically called when the current displayed
	 * directory changes.
	 */
	protected void updateBackButton() {
		backButton.setEnabled(locationPane.getLocationHistory().hasBack());
	}

	/** Update the enabled state of the up button.
	 * This is automatically called when the current displayed
	 * directory changes.
	 */
	protected void updateUpButton() {
		IOLocation d = locationPane.getLocationHistory().getLocation();
		String parentPath = d.getParentPath();
		upButton.setEnabled(parentPath!=null);
	}

	/** Update the enabled state of the new folder button.
	 * This is automatically called when the current displayed
	 * directory changes.
	 */
	protected void updateNewFolderButton() {
		IOLocation d = locationPane.getLocationHistory().getLocation();
		newFolderButton.setEnabled(d.isDirectory() && d.canWrite());
	}

	/** Update the contents and enabled state of the combobox.
	 * <p>Subclasses must increment the protected field "adjustingComboBox"
	 * before changing the contents/selection of this combobox, otherwise
	 * events will be inappropriately fired.  (Then decrement the field
	 * again when finished.)
	 * <p>This is automatically called when the current displayed
	 * directory changes.
	 */
	protected void updateComboBox() {
		adjustingComboBox++;
		try {
			comboBox.removeAllItems();
			IOLocation current = locationPane.getLocationHistory().getLocation();
			while(current!=null) {
				comboBox.addItem(current);
				current = current.getParent();
			}
			comboBox.setEnabled(comboBox.getItemCount()>0);
		} finally {
			adjustingComboBox--;
		}
	}
	
	/** This calls updateNextButton(), updateBackButton(), updateNewFolderButton(),
	 * updateUpButton(), and updateComboBox().
	 * 
	 */
	protected void updateDirectoryControls() {
		updateNextButton();
		updateBackButton();
		updateNewFolderButton();
		updateUpButton();
		updateComboBox();
	}
	
	public JButton getCommitButton() {
		return commitButton;
	}
	
	public JButton getCancelButton() {
		return cancelButton;
	}
	
	/** The default name for a new directory.  This may prompt the user for
	 * a dialog.
	 * @return if this returns <code>null</code> then no directory should be created.
	 */
	protected abstract String getNewFolderName();
	
	/** This is called when the user clicks the <code>newFolderButton</code>.
	 * The default implementation uses <code>getNewFolderName()</code>, possibly
	 * followed by a number if a file already exists with that name.
	 */
	protected void createNewFolder() {
		try {
			String name = getNewFolderName();
			if(name==null) return;
			
			IOLocation directory = locationPane.getLocationHistory().getLocation();
			IOLocation newDirectory = directory.getChild(name);
			int ctr = 2;
			while(newDirectory.exists()) {
				newDirectory = directory.getChild(name+" "+ctr);
				ctr++;
			}
			createNewFolder(newDirectory);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	/** This is used in <code>createNewFolder()</code> to decide
	 * whether to open newly created folders, or merely select them.
	 * 
	 * @return returns <code>false</code>.
	 */
	protected boolean isNewFolderOpened() {
		return false;
	}
	
	/** This is a convenience method to help create a new folder.
	 * This will guarantee that the directory will be visible,
	 * refresh the browser, select the newly created directory,
	 * and any other misc GUI needs.
	 * <p>This is called by <code>createNewFolder()</code>.
	 * @param newDirectory the new location to create.
	 */
	protected void createNewFolder(final IOLocation newDirectory) {
		try {
			newDirectory.mkdir();
			
			if(isNewFolderOpened()) {
				locationPane.getLocationHistory().append(newDirectory);
			} else {
				//this will rarely be necessary, but just in case:
				locationPane.getLocationHistory().append(newDirectory.getParent());
			}
			
			/** Suppose the browser takes X ms to refresh:
			 * This new folder can't be selected in the browser until
			 * it exists there.  So we at least can add a little delayed
			 * response to help.
			 * What's a more elegant solution?
			 * worst case scenario: the new directory isn't selected.
			 * This is not ideal, but also not a big problem.
			 */
			Thread delay = new Thread("updating selection") {
				@Override
				public void run() {
					browser.refresh(false,true);
					if(isNewFolderOpened()==false) {
						browser.getSelectionModel().setSelection(newDirectory);
					}
				}
			};
			delay.start();
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private Runnable updateProgressBarRunnable = new Runnable() {
		public void run() {
			updateProgressBar();
		}
	};
	protected void updateProgressBar() {
		if(SwingUtilities.isEventDispatchThread()==false) {
			SwingUtilities.invokeLater( updateProgressBarRunnable );
			return;
		}
		boolean visible = browser.isLoading();
		throbber.setVisible(visible);
	}

	/** The directory this pane should open up to by default. */
	public abstract IOLocation getDefaultDirectory();
	
	protected abstract void installGUI(JComponent c);

	@Override
	public void installUI(JComponent c) {
		if(c!=locationPane)
			throw new IllegalArgumentException("this object can only be installed in the component it was constructed with");
		c.removeAll();
		installGUI(c);
	}

	@Override
	public void uninstallUI(JComponent c) {
		// TODO Auto-generated method stub
		super.uninstallUI(c);
	}
}