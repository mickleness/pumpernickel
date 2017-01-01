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
package com.pump.swing.io;

import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;

import com.pump.icon.PaddedIcon;
import com.pump.image.ImageLoader;
import com.pump.io.location.IOLocation;
import com.pump.io.location.IOLocationFilter;
import com.pump.io.location.IOLocationGroup;
import com.pump.plaf.LocationPaneUI;

public abstract class LocationPane extends JComponent {

	public static final BufferedImage FOLDER_THUMBNAIL = ImageLoader.createImage( LocationPane.class.getResource("FolderIcon.png") );
	public static final BufferedImage FILE_THUMBNAIL = ImageLoader.createImage( LocationPane.class.getResource("DocumentIcon.png") );
	
    private static final long serialVersionUID = 1L;
    public static final String ACCESSORY_KEY = "location.pane.accessory";

	protected static final KeyStroke escapeKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
	protected static final KeyStroke commandPeriodKey = KeyStroke.getKeyStroke(KeyEvent.VK_PERIOD, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());

	private IOLocationGroup group = null;
	private Object groupKey = null;
	final GraphicCache graphicCache;
	final LocationHistory locationHistory;
	final IOSelectionModel selectionModel;
	final IOLocationFilter backupFilter = new IOLocationFilter() {
		String[] properties = new String[] {"suffixes", "suffix"};
		public IOLocation filter(IOLocation loc) {
			String name = loc.getPath().toLowerCase();
			for(int a = 0; a<properties.length; a++) {
				Object obj = getClientProperty(properties[a]);
				if(obj instanceof String) {
					String s = ((String)obj);
					if(s.length()==0 || "*".equals(s) || ".*".equals(s) || name.endsWith( "."+s)) {
						return loc;
					}
					return null;
				} else if(obj instanceof String[]) {
					String[] suffixes = (String[])obj;
					for(int b = 0; b<suffixes.length; b++) {
						if( suffixes[b].length()==0 || "*".equals(suffixes[b]) || 
								".*".equals(suffixes[b]) || name.endsWith( "."+suffixes[b].toLowerCase() )) {
							return loc;
						}
					}
					return null;
				}
			}
			//if no suffixes are defined: accept everything.
			
			return loc;
		}
		
	};
	
	public LocationPane(boolean allowMultipleSelection) {
		this(new LocationHistory(), new IOSelectionModel(allowMultipleSelection), new GraphicCache());
	}
	
	public LocationPane(LocationHistory locationHistory,IOSelectionModel selectionModel,GraphicCache graphicCache) {
		this.locationHistory = locationHistory;
		this.selectionModel = selectionModel;
		this.graphicCache = graphicCache;
		
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentHidden(ComponentEvent e) {
				updateLocationGroup();
			}
		});
		addHierarchyListener(new HierarchyListener() {
			public void hierarchyChanged(HierarchyEvent e) {
				updateLocationGroup();
			}
		});

		ChangeListener disposalListener = new ChangeListener() {
			public synchronized void stateChanged(ChangeEvent e) {
				updateLocationGroup();
			}
		};
		getLocationHistory().addChangeListener(disposalListener);
		disposalListener.stateChanged(null);
		
		putClientProperty("suffix","");
		
		locationHistory.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				getSelectionModel().setSelection(new IOLocation[] {});
			}
		});
		
		updateUI();
	}
	
	/** Updates the group and adds/releases consumers appropriately. */
	private void updateLocationGroup() {
		IOLocationGroup currentGroup = null;
		if(isShowing()) {
			IOLocation currentLoc = getLocationHistory().getLocation();
			currentGroup = currentLoc==null ? null : currentLoc.getGroup();
		} else {
			currentGroup = null;
		}
		
		if(group!=currentGroup) {
			if(group!=null) {
				group.releaseConsumer(groupKey);
			}
			group = currentGroup;
			if(group!=null) {
				groupKey = group.addConsumer();
			} else {
				groupKey = null;
			}
		}
	}
	
	@Override
	protected void setUI(ComponentUI ui) {
		super.setUI(ui);
		IOLocation defaultLocation = getDefaultDirectory();
		locationHistory.replaceAll(defaultLocation);
	}
	
	public void setAccessory(JComponent jc) {
		putClientProperty(ACCESSORY_KEY,jc);
	}
	
	public JComponent getAccessory() {
		return (JComponent)getClientProperty(ACCESSORY_KEY);
	}
	
	public LocationHistory getLocationHistory() {
		return locationHistory;
	}
	
	public IOSelectionModel getSelectionModel() {
		return selectionModel;
	}
	
	/** Controls whether this pane should be opening/creating a new
	 * directory or not.
	 */
	public void setDirectory(boolean b) {
		putClientProperty("directory", b ? Boolean.TRUE : Boolean.FALSE);
	}

	/** Returns whether this pane should be opening/creating a new
	 * directory or not.  The default value is false.
	 * <p>You can add a PropertyChangeListener to this component
	 * for the "directory" property to know when this changes.
	 */
	public boolean isDirectory() {
		Boolean b = (Boolean)getClientProperty("directory");
		if(b==null) b = Boolean.FALSE;
		return b.booleanValue();
	}
	
	public GraphicCache getGraphicCache() {
		return graphicCache;
	}
	
	protected IOLocation getDefaultDirectory() {
		return getLocationPaneUI().getDefaultDirectory();
	}
	
	public abstract LocationPaneUI getLocationPaneUI();
	
	public void setFilter(IOLocationFilter f) {
		putClientProperty("filter",f);
	}
	
	/** The filter to apply in this <code>LocationPane</code>.
	 * This should never return null.  By default it will be based
	 * on the client property "suffix" or "suffixes" if either
	 * is defined.  You can explicitly set this by calling
	 * <code>setFilter()</code>.
	 */
	public IOLocationFilter getFilter() {
		IOLocationFilter filter = (IOLocationFilter)getClientProperty("filter");
		if(filter!=null) return filter;
		return backupFilter;
	}

	public static String getString(JComponent jc,String key,String defaultValue) {
		String str = (String) jc.getClientProperty(key);
		if(str==null)
			return defaultValue;
		return str;
	}

	public static boolean getBoolean(JComponent jc,String key,boolean defaultValue) {
		Boolean b = (Boolean) jc.getClientProperty(key);
		if(b==null)
			return defaultValue;
		return b;
	}
}