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
package com.pump.swing.toolbar;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceAdapter;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceListener;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.image.BufferedImage;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.RootPaneContainer;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import com.pump.awt.AnimatedLayout;
import com.pump.blog.Blurb;
import com.pump.swing.MockComponent;
import com.pump.util.JVM;

/** This toolbar mimick's Apple's standard toolbars and their
 * drag-and-drop customization.
 * <P>There are certain assumptions made about this component, such as:
 * <ul><LI>This will be given lots of horizontal space.</li>
 * <LI>Components you try to display in it will be of similar "reasonable" heights.</li>
 * <LI>This will be used in a JFrame.</li>
 * <LI>You will not need to call <code>setVisible()</code> on components in this toolbar.</LI></ul>
 * <P>There is room for improvement in this implementation.  As of this writing:
 * <ul><LI>The "show icon/text" combobox is not yet implemented.</li>
 * <LI>Cocoa toolbars elegantly cut off components and have left/right scrolling
 * arrows when the toolbar is not given enough horizontal space: this toolbar
 * currently does not address this problem.</li>
 * <LI>Also there are at least 2 TODO notes in this class itself, commenting
 * on aesthetic bugs.</LI></ul>
 * 
 */
@Blurb (
title = "Customize Toolbar: Implementing Mac-Like Toolbars",
releaseDate = "June 2008",
summary = "In lots of Apple's software there's a handy menu option called \"Customize Toolbar\" that "+
"lets users drag and drop components in a toolbar.  It's functional <i>and</i> great eye candy.\n"+
"<p>This article presents a similar mechanism for Java.  A single thumbnail doesn't do justice to this feature, but "+
"if you go to the article you'll see a screencast.",
article = "http://javagraphics.blogspot.com/2008/06/customize-toolbar-implementing-mac-like.html",
javadocLink = true
)
public class CustomizedToolbar extends JPanel {
	private static final long serialVersionUID = 1L;
	protected static final String DIALOG_ACTIVE = "customizeDialogActive";
	

	/** This is where the order of components is stored. */
	static final Preferences prefs = Preferences.userNodeForPackage(CustomizedToolbar.class);
	
	/** Removes the stored layout information for a particular toolbar. */
	public static void resetPreferences(String toolbarName) {
		String base = toolbarName+".component";
		int ctr = 0;
		String s = base+ctr;
		while(prefs.get(s, null)!=null) {
			prefs.remove(s);
			ctr++;
			s = base+ctr;
		}
	}

	/** Removes the stored layout information for all toolbars. */
	public static void resetAllPreferences() {
		try {
			prefs.clear();
		} catch(BackingStoreException e) {
			RuntimeException e2 = new RuntimeException();
			e2.initCause(e);
			throw e2;
		}
	}

	/** Are we painting against a dark background?
	 * This checks the JVM version, the os, and whether the window's ultimate parent
	 * uses Apple's brush-metal-look. 
	 */
	protected static boolean isDarkBackground(Window w) {
		if(!isMac)
			return false;
		
		if(JVM.getMajorJavaVersion()<1.5)
			return false;
		
		while(w!=null) {
			if(w instanceof RootPaneContainer) {
				JRootPane rootPane = ((RootPaneContainer)w).getRootPane();
				Object obj = rootPane.getClientProperty("apple.awt.brushMetalLook");
				if(obj==null) obj = Boolean.FALSE;
				if(obj.toString().equals("true")) {
					return true;
				}
			}
			w = w.getOwner();
		}
		return false;
	}
	
	/** Runs <code>updateContents()</code> in the AWT thread. */
	private Runnable updateContentsRunnable = new Runnable() {
		public void run() {
			updateContents();
		}
	};
	
	/** This listens possibly updates this components if the preferences change. */
	private PreferenceChangeListener prefListener = new PreferenceChangeListener() {
		public void preferenceChange(PreferenceChangeEvent evt) {
			SwingUtilities.invokeLater(updateContentsRunnable);
		}
	};

	/** The components this toolbar may display. */
	JComponent[] componentList;
	
	/** The name of the component currently being dragged. */
	String draggingComponent;
	
	/** Whether a drag is originating from the toolbar, or the
	 * dialog. This can make a big difference on how the drag is treated.
	 */
	boolean draggingFromToolbar;

	/** Whether the default set of controls is being dragged to
	 * the toolbar.
	 */
	boolean draggingDefaults;
	
	/** The minimum height of this toolbar, based on the
	 * preferred height of all its components (whether they
	 * are visible or not.
	 */
	int minimumHeight;
	
	/** Are we on a Mac? */
    private static final boolean isMac = (System.getProperty("os.name").toLowerCase().indexOf("mac")!=-1);
	
	/** The padding between each component in this toolbar. */
	Insets componentInsets = new Insets(4,4,4,4);
	
	/** Controls whether we change visiblity of components during DnD operations. */
	protected static boolean hideActiveComponents = DragSource.isDragImageSupported();

	private static DragSource dragSource = DragSource.getDefaultDragSource();
	
	private DragSourceListener dragSourceListener = new DragSourceAdapter() {
		@Override
		public void dragDropEnd(DragSourceDropEvent dsde) {
			endDrag(dsde);
		}
	};
	
	/** The default contents of this toolbar. */
	String[] defaultContents;
		
	private DragGestureListener dragGestureListener = new DragGestureListener() {

		public void dragGestureRecognized(DragGestureEvent dge) {
			Point p = dge.getDragOrigin();
			Component c = dge.getComponent();
			JFrame f = (JFrame)SwingUtilities.getWindowAncestor(c);
			p = SwingUtilities.convertPoint(c, p, f);
			
			for(int a = 0; a<componentList.length; a++) {
				if(triggerDrag(f,p,dge,componentList[a]))
					return;
			}
			//double-check for separators & gaps:
			for(int a = 0; a<getComponentCount(); a++) {
				if(triggerDrag(f,p,dge,(JComponent)getComponent(a)))
					return;
			}
			
		}
		
		private boolean triggerDrag(JFrame f,Point p,DragGestureEvent dge,JComponent c) {
			Rectangle r = new Rectangle(0,0,c.getWidth(),c.getHeight());
			r = SwingUtilities.convertRectangle(c, 
					r, 
					f);
			
			if(r.contains(p)) {
				draggingFromToolbar = true;
					draggingDefaults = false;
				draggingComponent = c.getName();
				MockComponent mc = new MockComponent(c);
				Transferable transferable = new MockComponentTransferable(mc);
				BufferedImage bi = mc.getBufferedImage();
				dge.startDrag(DragSource.DefaultMoveDrop, 
						bi, 
						new Point(r.x-p.x, r.y-p.y), 
						transferable, 
						dragSourceListener);
				return true;
			}
			return false;
		}
		
	};
	
	private DropTargetListener dropTargetListener = new DropTargetListener() {

		public void dragEnter(DropTargetDragEvent dtde) {
			dragOver(dtde);
		}

		public void dragExit(DropTargetEvent dte) {
			if(draggingDefaults) {
				return;
			}
			updateContents(getContents(new Point(-1000,-1000)));
			if(draggingComponent!=null) {
				JComponent theComponent = getComponent(draggingComponent);
				Rectangle r = (Rectangle)theComponent.getClientProperty(AnimatedLayout.DESTINATION);
				if(r!=null) {
					theComponent.setBounds( r );
				}
				if(hideActiveComponents)
					theComponent.setVisible(true);
			}
		}

		public void dragOver(DropTargetDragEvent dtde) {
			if(draggingComponent==null) {
				dtde.rejectDrag();
			} else {
				Point p = dtde.getLocation();
				p = SwingUtilities.convertPoint(
						((DropTarget)dtde.getSource()).getComponent(), 
						p, 
						CustomizedToolbar.this );
				String[] contents = getContents(p);
				updateContents(contents);
				dtde.acceptDrag(DnDConstants.ACTION_MOVE);
			}
		}

		public void drop(DropTargetDropEvent dtde) {
			if(draggingComponent==null) {
				dtde.rejectDrop();
			} else {

				if(draggingDefaults) {
					setContents(getDefaultContents());
				} else {
					Point p = dtde.getLocation();
					p = SwingUtilities.convertPoint(
							((DropTarget)dtde.getSource()).getComponent(), 
							p, 
							CustomizedToolbar.this );
					
					String[] contents = getContents(p);
					setContents(contents);
					dtde.acceptDrop(DnDConstants.ACTION_MOVE);
					JComponent theComponent = getComponent(draggingComponent);
					Rectangle r = (Rectangle)theComponent.getClientProperty(AnimatedLayout.DESTINATION);
					if(r!=null) {
						theComponent.setBounds( r );
					}
					if(hideActiveComponents)
						theComponent.setVisible(true);
				}
			}
			dtde.dropComplete(true);
		}

		public void dropActionChanged(DropTargetDragEvent dtde) {}
	};
	
	/** Returns the contents (by name, in order of appearance)
	 * after factoring in the point that the mouse is current at
	 * point p.  (This means the component that is currently
	 * being dragged will offset everything and appear near
	 * point p.)
	 */
	private String[] getContents(Point p) {
		if(draggingComponent==null || draggingDefaults) {
			return getContents();
		}

		Rectangle toolbarBounds = new Rectangle(0,0,getWidth(),getHeight());
		
		boolean verticallyInside = p.y>=0 && 
			p.y<=toolbarBounds.height;
		
		String[] order = getContents();
		
		if((!verticallyInside) && draggingFromToolbar==false) {
			return order;
		}
		
		int a = 0;
		Component theComponent = getComponent(draggingComponent);
		if(hideActiveComponents)
			theComponent.setVisible(false);
		while(a<order.length) {
			if(order[a].equals(draggingComponent)) {
				order = remove(order,a);
			} else {
				a++;
			}
		}

		if((!verticallyInside) && draggingFromToolbar) {
			return order;
		}
		
		
		for(a = 0; a<order.length; a++) {
			JComponent c = getComponent(order[a]);
			Rectangle r = c.getBounds();
			if(p.x<r.x+r.width/2) {
				order = insert(order,draggingComponent,a);
				return order;
			}
		}

		order = insert(order,draggingComponent,order.length);
		return order;
	}
	
	/** Returns a clone of this array, using the same component type
	 * the argument uses.
	 * @param array the array to clone
	 * @return a new array
	 */
	@SuppressWarnings("unchecked")
	private static <T> T[] clone(T[] array) {
		Class<?> cl = array.getClass().getComponentType();
		Object[] newArray = (Object[])Array.newInstance(cl, array.length);
		System.arraycopy(array,0,newArray,0,array.length);
		return (T[])newArray;
	}
	
	/** Removes an element from an array.
	 * <P>This returns an array of type array.getClass(), not
	 * necessarily an array of type Object.
	 * @param array the array to remove an element from
	 * @param index the index of the element to remove
	 * @return the new array that is 1 unit smaller than the argument array.
	 */
	@SuppressWarnings("unchecked")
	private static <T> T[] remove(T[] array,int index) {
		Class<?> cl = array.getClass().getComponentType();
		Object[] newArray = (Object[])Array.newInstance(cl, array.length-1);
		System.arraycopy(array,0,newArray, 0, index);
		System.arraycopy(array,index+1,newArray, index, array.length-index-1);
		return (T[])newArray;
	}

	/** Adds an element to an array.
	 * <P>This returns an array of type array.getClass(), not
	 * necessarily an array of type Object.
	 * @param array the array to add an element to
	 * @param newObject the object to add
	 * @param index the index to insert at.
	 * @return the new array that is 1 unit larger than the argument array.
	 */
	@SuppressWarnings("unchecked")
	private static <T> T[] insert(T[] array,Object newObject,int index) {
		Class<?> cl = array.getClass().getComponentType();
		Object[] newArray = (Object[])Array.newInstance(cl, array.length+1);
		System.arraycopy(array,0,newArray, 0, index);
		newArray[index] = newObject;
		System.arraycopy(array,index,newArray, index+1, array.length-index);
		return (T[])newArray;
	}
	
	/** This hard-to-describe variable has to do with when things
	 * are initialized, and laying out this toolbar.
	 * <P>If things are set up incorrectly: all the components
	 * slide into place in an animation.  :)  This is "neat", but
	 * gratuitous eye-candy.
	 */
	private boolean updatedWhileShowing = false;
	
	/** Creates a new CustomizedToolbar.
	 * 
	 * @param components the components that may be in this toolbar.
	 * <P>Each component must have a unique name; this is how the order
	 * of each component is stored between sessions.
	 * @param defaults the default order of the components.  This array
	 * should contain the names of the components in the previous argument.
	 * <P>These defaults are used when there are no preferences to indicate how to lay
	 * out this toolbar.
	 * <P>Special/reserved names include:
	 * <ul><LI>"-": used to indicate a JSeparator.</li>
	 * <LI>" ": used to indicate a regular gap.</li>
	 * <LI>"\t": used to indicate a flexible gap.</LI></ul>
	 * <P>You can use as many of these special names as you like, but actual
	 * components in the previous argument should only be referenced once.
	 * @param toolbarName a unique name for this type of toolbar.  This name
	 * serves as a key in the preferences to retrieve the order of the components
	 * in this toolbar.  Most applications will only have 1 type of customized
	 * toolbar.  Apple's Mail application has at least 2 toolbars: one
	 * in the main window, and one when you compose a new message.  So
	 * each type of window should use a unique name.
	 */
	public CustomizedToolbar(JComponent[] components,String[] defaults,String toolbarName) {
		super(new AnimatedLayout(false));
		
		int separatorCtr = 0;
		int spaceCtr = 0;
		int flexCtr = 0;
		for(int a = 0; a<defaults.length; a++) {
			if(defaults[a].equals("-")) {
				defaults[a] = "-"+separatorCtr;
				separatorCtr++;
			} else if(defaults[a].equals(" ")) {
				defaults[a] = " "+spaceCtr;
				spaceCtr++;
			} else if(defaults[a].equals("\t")) {
				defaults[a] = "\t"+flexCtr;
				flexCtr++;
			}
		}
		defaultContents = clone(defaults);
		 
		minimumHeight = getMinimumHeight(components);
		
		setName(toolbarName);
		if(isEmpty()) {
			String base = getName()+".component";
			for(int a = 0; a<defaults.length; a++) {
				if(defaults[a]==null)
					throw new NullPointerException("defaults["+a+"] is null");
				prefs.put(base+a, defaults[a]);
			}
		}
		setMinimumSize(new Dimension(5,minimumHeight+componentInsets.top+componentInsets.bottom));
		setPreferredSize(new Dimension(5,minimumHeight+componentInsets.top+componentInsets.bottom));
		setMaximumSize(new Dimension(5,minimumHeight+componentInsets.top+componentInsets.bottom));
		componentList = new JComponent[components.length];
		System.arraycopy(components,0,componentList,0,components.length);
		prefs.addPreferenceChangeListener(prefListener);
		
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				updateContents();
			}
		});
		
		if(SwingUtilities.isEventDispatchThread()) {
			updateContentsRunnable.run();
		} else {
			//oh don't pretend you never create things off the AWT thread when
			//you know you shouldn't...
			SwingUtilities.invokeLater(updateContentsRunnable);
		}
		
		addHierarchyListener(new HierarchyListener() {

			public void hierarchyChanged(HierarchyEvent e) {
				if(updatedWhileShowing)
					return;
				updateContents();
			}
			
		});
	}
	
	protected String[] getDefaultContents() {
		return clone(defaultContents);
	}
	
	private static int getMinimumHeight(JComponent[] components) {
		int h = 0;
		for(int a = 0; a<components.length; a++) {
			h = Math.max(components[a].getPreferredSize().height, h);
			h = Math.max(components[a].getHeight(), h);
		}
		return h;
	}
	
	/** Returns the components that may or may not be visible
	 * in this toolbar, depending on how the user has configured
	 * this toolbar.
	 */
	public JComponent[] getPossibleComponents() {
		JComponent[] array = new JComponent[componentList.length];
		System.arraycopy(componentList,0,array,0,componentList.length);
		return array;
	}
	
	protected void endDrag(DragSourceDropEvent e) {
		if(draggingComponent!=null) {
			Point p = e.getLocation();
			SwingUtilities.convertPointFromScreen(p, this);
			if(contains(p)==false) {
				//adding this extra ability to commit changes
				//makes sure if you simply drag an element
				//off the toolbar: it gets recorded.
				setContents(getContents(new Point(-1000,-1000)));
			}
		}
		draggingComponent = null;
		/** TODO: is there some way when a drop ends if it IS
		 * accepted to NOT show the image slide back to its original
		 * location?  For example:
		 * 1.  Open this demo app
		 * 2.  Click "Customize"
		 * 3.  Drag a component off the toolbar in the window
		 * into nothingness.
		 * Note the *image* slides back to where it came from, because
		 * this is how the OS shows the drag was not accepted.  But
		 * even though it was not accepted by any other entity:
		 * the drag was successful, and showing that sliding image is
		 * incorrect.
		 */
	}
	
	private boolean isEmpty() {
		String base = getName()+".component";
		String s = prefs.get(base+"0", null);
		return s==null;
	}
	
	private void updateContents() {
		updateContents(getContents());
	}
	
	/** Returns the component names (in order of appearance) that
	 * this toolbar should display from the preferences.
	 */
	private String[] getContents() {
		List<String> v = new ArrayList<String>();
		String base = getName()+".component";
		String s = null;
		int ctr = 0;
		while( (s = prefs.get(base+ctr, null))!=null ) {
			v.add(s);
			ctr++;
		}
		
		return v.toArray(new String[v.size()]);
	}
	
	/** Stores the component names (in order of appearance)
	 * that this toolbar should display in the preferences.
	 */
	private void setContents(String[] array) {
		String base = getName()+".component";
		for(int a = 0; a<array.length; a++) {
			prefs.put(base+a, array[a]);
		}
		
		int ctr = array.length;
		while( prefs.get(base+ctr, null)!=null ) {
			prefs.remove(base+ctr);
			ctr++;
		}
	}
	
	/** Updates the bounds of each component so this toolbar shows
	 * the components listed in the <code>contents</code> argument.
	 * <P>Note this uses the <code>AnimatedLayout</code>, so components
	 * are not immediately updated.
	 */
	private void updateContents(String[] contents) {
		if(isShowing()==false) {
			//never run this method before this component is
			//layed out in a parent: if this component calculates
			//its layout with a width of zero (which it will have
			//before a parent lays it out first): this will
			//make screwey flexible spaces.
			
			//A special HierarchyListener was added so
			//as soon as this component IS set up in a parent,
			//we calculate the layout.
			return;
		}
		
		updatedWhileShowing = true;
		
		int x = 0;
		
		Component[] components = this.getComponents();
		
		for(int a = 0; a<components.length; a++) {
			JComponent jc = (JComponent)components[a];
			jc.putClientProperty("legitimate", Boolean.FALSE);
		}
		
		int totalFlexGaps = 0;
		int minPreferredWidth = 0;
		for(int a = 0; a<contents.length; a++) {
			try {
				if(contents[a].length()>0 && contents[a].charAt(0)=='\t') {
					//this is a flexible gap
					totalFlexGaps++;
				} else {
					JComponent comp = getComponent( contents[a] );
					Dimension d = comp.getPreferredSize();
					minPreferredWidth += d.width;
				}
			} catch(Exception e) {}
		}
		
		int extraSpace = getWidth()-(componentInsets.left+componentInsets.right)*contents.length-minPreferredWidth;
		if(extraSpace<0) extraSpace = 0;
		
		for(int a = 0; a<contents.length; a++) {
			try {
				JComponent comp = getComponent( contents[a] );
				Dimension d;
				
				if(contents[a].length()>0 && contents[a].charAt(0)=='\t') {
					//flexible gap:
					int width = extraSpace/totalFlexGaps;
					extraSpace = extraSpace-width;
					totalFlexGaps--;
					
					d = new Dimension(width,minimumHeight);
				} else {
					d = comp.getPreferredSize();
				}
				if(comp instanceof JSeparator)
					d.height = minimumHeight;
				
				boolean contains = false;
				for(int b = 0; b<components.length && contains==false; b++) {
					if(components[b]==comp)
						contains = true;
				}
				if(!contains) {
					if(draggingComponent!=null && hideActiveComponents) {
						boolean show = !comp.getName().equals(draggingComponent);
						comp.setVisible( show );
					}
					add(comp);
				}
				
				Rectangle bounds = new Rectangle(x+componentInsets.left,componentInsets.top+minimumHeight/2-d.height/2,d.width,d.height);
				if(comp.getClientProperty(AnimatedLayout.DESTINATION)==null ||
						(!contains)) {
					comp.setBounds(bounds);
				}
				comp.putClientProperty(AnimatedLayout.DESTINATION, bounds);
				x+=d.width+componentInsets.left+componentInsets.right;
				comp.putClientProperty("legitimate", Boolean.TRUE);
			} catch(NullPointerException e) {
				//this may get thrown if getComponent(name) yields no component
				//this may happen if a component's name changes, or a component
				//is removed.
			}
		}
		
		for(int a = 0; a<components.length; a++) {
			JComponent jc = (JComponent)components[a];
			if(jc.getClientProperty("legitimate").equals(Boolean.FALSE)) {
				remove(jc);
				repaint(); //related Ladislav's bug
			}
		}
	}
	
	protected Insets getComponentInsets() {
		return (Insets)componentInsets.clone();
	}
	
	/** Returns an unused name for a new separator. */
	protected String getNewSeparatorName() {
		int min = 0;
		search : while(true) {
			for(int a = 0; a<getComponentCount(); a++) {
				String name = getComponent(a).getName();
				if(name.equals("-"+min)) {
					min++;
					continue search;
				}
			}
			return "-"+min;
		}
	}

	/** Returns an unused name for a new space. */
	protected String getNewSpaceName() {
		int min = 0;
		search : while(true) {
			for(int a = 0; a<getComponentCount(); a++) {
				String name = getComponent(a).getName();
				if(name.equals(" "+min)) {
					min++;
					continue search;
				}
			}
			return " "+min;
		}
	}

	/** Returns an unused name for a new flexible space. */
	protected String getNewFlexibleSpaceName() {
		int min = 0;
		search : while(true) {
			for(int a = 0; a<getComponentCount(); a++) {
				String name = getComponent(a).getName();
				if(name.equals("\t"+min)) {
					min++;
					continue search;
				}
			}
			return "\t"+min;
		}
	}
	
	/** Gets the component that has a certain name.
	 * <P>The argument should be a name of a component that
	 * was passed when you created this toolbar, or one
	 * of the special names: "-", " ", "\t"
	 */
	protected JComponent getComponent(String name) {
		if(name==null) throw new NullPointerException();
		
		for(int a = 0; a<componentList.length; a++) {
			if(componentList[a].getName().equals(name))
				return componentList[a];
		}
		//the rest of this method deals with separators and gaps
		for(int a = 0; a<getComponentCount(); a++) {
			if(getComponent(a).getName().equals(name))
				return (JComponent)getComponent(a);
		}
		if(name.length()>0 && name.charAt(0)=='-') {
			JSeparator newSeparator = new JSeparator(SwingConstants.VERTICAL);
			newSeparator.setUI(new MacToolbarSeparatorUI());
			newSeparator.setName(name);
			return newSeparator;
		} else if(name.length()>0 && name.charAt(0)==' ') {
			SpaceComponent space = new SpaceComponent(this,false);
			space.setName(name);
			return space;
		} else if(name.length()>0 && name.charAt(0)=='\t') {
			SpaceComponent space = new SpaceComponent(this,true);
			space.setName(name);
			return space;
		}
		throw new NullPointerException("No component \""+name+"\"");
	}
	
	/** Displays the dialog that lets the user customize this component.
	 * <P>This covers the underlying JFrame in a transparent sheet so
	 * the toolbar can also send/receive drag and drop events appropriately,
	 * and to block out the rest of the GUI so the dialog appears modal.
	 * (It is not technically modal, because otherwise we would have trouble
	 * interacting with the toolbar underneath.)
	 * @param dialogMaxWidth this is the width used to decide when to wrap
	 * rows of components.  Depending on the size of your toolbar, and the 
	 * platform, you may want to change this number.
	 */
	public void displayDialog(int dialogMaxWidth) {
		final JFrame parent = (JFrame)SwingUtilities.getWindowAncestor(this);
		if(parent==null) throw new NullPointerException("the dialog cannot be shown if the parent frame is null.");

		putClientProperty(DIALOG_ACTIVE, Boolean.TRUE); //makes spaces paint themselves
		CustomizedToolbarOptions options = new CustomizedToolbarOptions(this,dialogMaxWidth);
		
		final JComponent modalCover = new JComponent() {
			private static final long serialVersionUID = 1L;
		};
		dragSource.createDefaultDragGestureRecognizer(modalCover, DnDConstants.ACTION_COPY_OR_MOVE, dragGestureListener);
		new DropTarget(modalCover,dropTargetListener);
		
		/** Having a MouseListener -- even if its empty -- prevents
		 * MouseEvents from leaking through to components underneath.
		 */
		modalCover.addMouseListener(new MouseAdapter() {});
		Dimension windowSize = parent.getSize();
		modalCover.setSize(windowSize);
		final JLayeredPane layeredPane = parent.getLayeredPane();
		layeredPane.add(modalCover);

		/** Must be non-modal so you can interact with the toolbar underneath. */
		final JDialog dialog = new JDialog(parent,false);
		dialog.getContentPane().add(options);
		dialog.setUndecorated(true);
		if(!isMac) {
			//undecorated windows need a little border:
			options.setBorder(new LineBorder(Color.gray));
		}
		dialog.pack();
		
		layeredPane.setLayer(modalCover, JLayeredPane.MODAL_LAYER.intValue());
		FakeSheetWindowListener windowListener = new FakeSheetWindowListener(parent,dialog,this,modalCover);
		parent.addComponentListener(windowListener);
		dialog.getRootPane().setDefaultButton(options.done);
		AbstractAction dialogCloseAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
			}
		};
		windowListener.repositionDialog();
		options.done.addActionListener(dialogCloseAction);
		/** TODO: I'm uncomfortable making this window unfocusable.
		 * Is there a way to keep it focusable and NOT let the parent
		 * JFrame -- that implements a brushed metal look -- paint
		 * itself in shades of light gray?
		 * 
		 * Apple's existing implementation does not use the keyboard
		 * focus, true, so one could argue it will not be missed.
		 * However, I'm wondering if this dialog needs to support keyboard
		 * actions to make it more accessible.
		 * 
		 */
		dialog.setFocusableWindowState(false);
		//dialog.setAlwaysOnTop(true);
		dialog.setVisible(true);
		dialog.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentHidden(ComponentEvent e) {
				layeredPane.remove(modalCover);
				putClientProperty(DIALOG_ACTIVE, Boolean.FALSE);
			}
		});
		
		KeyStroke escapeKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		dialog.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(escapeKey, escapeKey);
		dialog.getRootPane().getActionMap().put(escapeKey, dialogCloseAction);
	}
}