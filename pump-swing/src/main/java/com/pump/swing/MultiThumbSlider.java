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
package com.pump.swing;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ComponentUI;

import com.pump.blog.Blurb;
import com.pump.plaf.MultiThumbSliderUI;
import com.pump.util.JVM;

/** This JComponent resembles a <code>JSlider</code>, except there are
 * at least two thumbs.  A <code>JSlider</code> is designed to modify
 * one number within a certain range of values.  By contrast a <code>MultiThumbSlider</code>
 * actually modifies a <i>table</i> of data.  Each thumb in a <code>MultiThumbSlider</code>
 * should be thought of as a key, and it maps to an abstract value.  In the case
 * of the <code>GradientSlider</code>: each value is a <code>java.awt.Color</code>.
 * Other subclasses could come along that map to other abstract objects.  (For example,
 * a <code>VolumeSlider</code> might map each thumb to a specific volume level.  This
 * type of widget would let the user control fading in/out of an audio track.)
 * <P>The slider graphically represents the domain from zero to one, so each thumb
 * is always positioned within that domain.  If the user drags
 * a thumb outside this domain: that thumb disappears.
 * <P>There is always a selected thumb in each slider when this slider has the
 * keyboard focus.  The user can press the tab key (or shift-tab) to transfer focus to different
 * thumbs.  Also the arrow keys can be used to control the selected thumb.
 * <P>The user can click and drag any thumb to a new location.  If a thumb is dragged
 * so it is less than zero or greater than one: then that thumb is removed.  If the user
 * clicks between two existing thumbs: a new thumb is created if <code>autoAdd</code> is
 * set to <code>true</code>.  (If <code>autoAdd</code> is set to false: nothing happens.)
 * <P>There are unimplemented methods in this class: <code>doDoubleClick()</code> and
 * <code>doPopup()</code>.  The UI will invoke these methods as needed; this gives the
 * user a chance to edit the values represented at a particular point.
 * <P>Also using the keyboard:
 * <ul><LI>In a horizontal slider, the user can press modifier+left or modifer+right to insert
 * a new thumb to the left/right of the currently selected thumb.  (Where "modifier" refers
 * to <code>Toolkit.getDefaultTookit().getMenuShortcutKeyMask()</code>.  On Mac this is META, and on Windows
 * this is CONTROL.)  Likewise on a vertical slider the up/down arrow keys can be used to add
 * thumbs.</li>
 * <LI>The delete/backspace key can be used to remove thumbs.</li>
 * <LI>In a horizontal slider, the down arrow key can be used to invoke <code>doPopup()</code>.
 * This should invoke a <code>JPopupMenu</code> that is keyboard accessible, so the user should be
 * able to navigate this component without a mouse.  Likewise on a vertical slider the right
 * arrow key should do the same.</li>
 * <LI>The space bar or return key invokes <code>doDoubleClick()</code>.</LI></ul>
 * <P>Because thumbs can be abstractly inserted, the values each thumb represents should be
 * tween-able.  That is, if there is a value at zero and a value at one, the call
 * <code>getValue(.5f)</code> must return a value that is halfway between those values.
 * <P>Also note that although the thumbs must always be between zero and one: the minimum
 * and maximum thumbs do not have to be zero and one.  The user can adjust them so the
 * minimum thumb is, say, .2f, and the maximum thumb is .5f.
 * @param <T> the type of data each float maps to. For example: in the GradientSlider
 * this value is a Color. Sometimes this property may be unnecessary. If this slider is only
 * meant to store the relative position of thumbs, then you may set this to a trivial
 * stub-like object like a String or Character.
 * 
 * @see com.pump.swing.MultiThumbSliderDemo
 */
@Blurb (
imageName = "MultiThumbSlider.png",
title = "Sliders: Creating a Slider with Multiple Thumbs",
releaseDate = "January 2015",
summary = "This is a GUI component that mimicks the <code>javax.swing.JSlider</code>, "
		+ "except it supports an arbitrary number of thumbs. There are currently three "
		+ "UI implementations for this component: one for Apple, one for Windows, and "
		+ "one for Metal."
		+ "<p>What happens when thumbs intersect/overlap? There are 3 enums for that: "
		+ "NUDGE_OTHER, STOP_AGAINST, and JUMP_OVER_OTHER.",
article = "http://javagraphics.blogspot.com/2015/01/sliders-creating-slider-with-multiple.html"
)
public class MultiThumbSlider<T> extends JComponent {
	private static final long serialVersionUID = 1L;
	
	/** A set of possible behaviors when one thumb collides with another. */
	public static enum Collision { 
		/** When the user drags one thumb and it collides with another, nudge the other thumb as far as possible. */
		NUDGE_OTHER, 
		/** When the user drags one thumb and it collides with another, skip over the other thumb. */
		JUMP_OVER_OTHER, 
		/** When the user drags one thumb and it collides with another, bump into the other thumb and don't allow any more movement. */
		STOP_AGAINST };
	
	/** The property that controls whether clicking between thumbs automatically adds a thumb. */
	public static final String AUTOADD_PROPERTY = MultiThumbSlider.class.getName()+".auto-add";
	
	/** The property that controls whether the user can remove a thumb (either by dragging or with the delete key). */
	public static final String REMOVAL_ALLOWED = MultiThumbSlider.class.getName()+".removal-allowed";
	
	/** The property that is changed when <code>setSelectedThumb()</code> is called. */
	public static final String SELECTED_THUMB_PROPERTY = MultiThumbSlider.class.getName()+".selected-thumb";

	/** The property that is changed when <code>setCollisionPolicy(c)</code> is called. */
	public static final String COLLISION_PROPERTY = MultiThumbSlider.class.getName()+".collision";
	
	/** The property that is changed when <code>setInverted(b)</code> is called. */
	public static final String INVERTED_PROPERTY = MultiThumbSlider.class.getName()+".inverted";
	
	/** The property that is changed when <code>setInverted(b)</code> is called. */
	public static final String THUMB_OVERLAP_PROPERTY = MultiThumbSlider.class.getName()+".thumb-overlap";

	/** The property that is changed when <code>setMinimumThumbnailCount(b)</code> is called. */
	public static final String THUMB_MINIMUM_PROPERTY = MultiThumbSlider.class.getName()+".thumb-minimum";
	
	/** The property that is changed when <code>setOrientation(i)</code> is called. */
	public static final String ORIENTATION_PROPERTY = MultiThumbSlider.class.getName()+".orientation";
	
	/** The property that is changed when <code>setValues()</code> is called.
	 * Note this is used when either the positions or the values are updated, because
	 * they need to be updated at the same time to maintain an exact one-to-one
	 * ratio.
	 */
	public static final String VALUES_PROPERTY = MultiThumbSlider.class.getName()+".values";
	
	/** The property that is changed when <code>setValueIsAdjusting(b)</code> is called. */
	public static final String ADJUST_PROPERTY = MultiThumbSlider.class.getName()+".adjusting";
	
	/** The property that is changed when <code>setPaintTicks(b)</code> is called. */
	public static final String PAINT_TICKS_PROPERTY = MultiThumbSlider.class.getName()+".paint ticks";
	
	/** The positions of the thumbs */
	protected float[] thumbPositions = new float[0];
	
	/** The values for each thumb */
	protected T[] values;
	
	/** ChangeListeners registered with this slider. */
	List<ChangeListener> changeListeners;
	
	/** The orientation constant for a horizontal slider.
	 */
	public static final int HORIZONTAL = SwingConstants.HORIZONTAL;

	/** The orientation constant for a vertical slider.
	 */
	public static final int VERTICAL = SwingConstants.VERTICAL;

	
	/** Creates a new horizontal MultiThumbSlider.
	 * 
	 * @param thumbPositions an array of values from zero to one.
	 * @param values an array of values, each value corresponds to a value in <code>thumbPositions</code>.
	 */
	public MultiThumbSlider(float[] thumbPositions, T[] values) {
		this( HORIZONTAL, thumbPositions, values);
	}
	
	/** Creates a new MultiThumbSlider.
	 * 
	 * @param orientation must be <code>HORIZONTAL</code> or <code>VERTICAL</code>
	 * @param thumbPositions an array of values from zero to one.
	 * @param values an array of values, each value corresponds to a value in <code>thumbPositions</code>.
	 */
	public MultiThumbSlider(int orientation, float[] thumbPositions, T[] values) {
		setOrientation(orientation);
		setValues(thumbPositions,values);
		setFocusable(true);
		updateUI();
	}
	
	public MultiThumbSliderUI<T> getUI() {
		return (MultiThumbSliderUI<T>)ui;
	}

    @Override
	public void updateUI() {
    	String name = UIManager.getString("MultiThumbSliderUI");
    	if(name==null) {
    		if(UIManager.getSystemLookAndFeelClassName().contains("MetalLookAndFeel")) {
    			name = "com.pump.plaf.MetalMultiThumbSliderUI";
    		} else if(JVM.isMac) {
    			name = "com.pump.plaf.AquaMultiThumbSliderUI";
    		} else if(JVM.isWindows) {
    			name = "com.pump.plaf.VistaMultiThumbSliderUI";
    		} else {
    			name = "com.pump.plaf.DefaultMultiThumbSliderUI";
    		}
    	}
    	try {
	    	Class<?> c = Class.forName(name);
	    	Constructor<?>[] constructors = c.getConstructors();
	    	for(int a = 0; a<constructors.length; a++) {
	    		Class<?>[] types = constructors[a].getParameterTypes();
	    		if(types.length==1 && types[0].equals(MultiThumbSlider.class)) {
	    			MultiThumbSliderUI<T> ui = (MultiThumbSliderUI<T>)constructors[a].newInstance(new Object[] {this});
	    			setUI(ui);
	    			return;
	    		}
	    	}
    	} catch(ClassNotFoundException e) {
    		throw new RuntimeException("The class \""+name+"\" could not be found.");
    	} catch(Throwable t) {
    		RuntimeException e = new RuntimeException("The class \""+name+"\" could not be constructed.");
    		e.initCause(t);
    		throw e;
    	}
    }
    
    public void setUI(MultiThumbSliderUI<T> ui) {
    	super.setUI( (ComponentUI)ui );
    }

	/** This listener will be notified when the colors/positions of
	 * this slider are modified.
	 * <P>Note you can also listen to these events by listening to
	 * the <code>VALUES_PROPERTY</code>, but this mechanism is provided
	 * as a convenience to resemble the <code>JSlider</code> model.
	 * @param l the <code>ChangeListener</code> to add.
	 */
	public void addChangeListener(ChangeListener l) {
		if(changeListeners==null)
			changeListeners = new ArrayList<ChangeListener>();
		if(changeListeners.contains(l))
			return;
		changeListeners.add(l);
	}
	
	/** Removes a <code>ChangeListener</code> from this slider.
	 */
	public void removeChangeListener(ChangeListener l) {
		if(changeListeners==null)
			return;
		changeListeners.remove(l);
	}
	
	/** Invokes all the ChangeListeners. */
	protected void fireChangeListeners() {
		if(changeListeners==null)
			return;
		for(int a = 0; a<changeListeners.size(); a++) {
			try {
				(changeListeners.get(a)).stateChanged(new ChangeEvent(this));
			} catch(Throwable t) {
				t.printStackTrace();
			}
		}
	}
	
	/** Depending on which thumb is selected, this may shift the focus
	 * to the next available thumb, or it may shift the focus to the
	 * next focusable <code>JComponent</code>.
	 */
	@Override
	public void transferFocus() {
		transferThumbFocus(true);
	}
	
	/** Shifts the focus forward or backward.
	 * This may decide to select another thumb, or it may
	 * call <code>super.transferFocus()</code> to let the
	 * next JComponent receive the focus.
	 * 
	 * @param forward whether we're shifting forward or backward
	 */
	private void transferThumbFocus(boolean forward) {
		int direction = (forward) ? 1 : -1;
		
		//because vertical sliders are technically inverted already:
		if(getOrientation()==VERTICAL)
			direction = direction*-1;
		
		//because inverted sliders are, well, inverted:
		if(isInverted())
			direction = direction*-1;
		
		int selectedThumb = getSelectedThumb();
		if(direction==1) {
			if(selectedThumb!=thumbPositions.length-1) {
				setSelectedThumb(selectedThumb+1);
				return;
			}
		} else {
			if(selectedThumb!=0) {
				setSelectedThumb(selectedThumb-1);
				return;
			}
		}
		if(forward) {
			super.transferFocus();
		} else {
			super.transferFocusBackward();
		}
	}

	/** Depending on which thumb is selected, this may shift the focus
	 * to the previous available thumb, or it may shift the focus to the
	 * previous focusable <code>JComponent</code>.
	 */
	@Override
	public void transferFocusBackward() {
		transferThumbFocus(false);
	}


	/** This creates a new value for insertion.
	 * <P>If the <code>pos</code> argument
	 * is outside the domain of thumbs, then a value still needs to be
	 * returned.
	 * 
	 * @param pos a position between zero and one
	 * @return a value that corresponds to the position <code>pos</code>
	 */
	public T createValueForInsertion(float pos) {
		throw new NullPointerException("this method is undefined. Either auto-adding should be disabled, or this method needs to be overridden to return a value");
	}
	
	/** Removes a specific thumb
	 * 
	 * @param thumbIndex the thumb index to remove.
	 */
	public void removeThumb(int thumbIndex) {
		if(thumbIndex<0 || thumbIndex>thumbPositions.length)
			throw new IllegalArgumentException("There is no thumb at index "+thumbIndex+" to remove.");
		
		float[] f = new float[thumbPositions.length-1];
		T[] c = createSimilarArray(values, values.length-1);
		System.arraycopy(thumbPositions, 0, f, 0, thumbIndex);
		System.arraycopy(values, 0, c, 0, thumbIndex);
		System.arraycopy(thumbPositions, thumbIndex+1, f, thumbIndex, f.length-thumbIndex);
		System.arraycopy(values, thumbIndex+1, c, thumbIndex, f.length-thumbIndex);
		setValues(f,c);
	}
	
	/** This is a kludgy casting trick to make our arrays mesh with generics. */
	private T[] createSimilarArray(T[] srcArray,int length) {
		Class<?> componentType = srcArray.getClass().getComponentType();
		return (T[])Array.newInstance(componentType, length);
	}
	
	/** An optional method subclasses can override to react to the user's
	 * double-click.  When a thumb is double-clicked the user is trying to edit
	 * the value for that thumb.  A double-click probably
	 * suggests the user wants a detailed set of controls to edit a value, such
	 * as a dialog.
	 * <P>Note this method will be called with arguments (-1,-1) if
	 * the space bar or return key is pressed.
	 * <P>By default this method does nothing, and returns <code>false</code>
	 * <P>Note the (x,y) information passed to this method is only provided so
	 * subclasses can position components (such as a JPopupMenu).  It can be
	 * assumed for a double-click event that the user has selected a thumb
	 * (since one click will click/create a thumb) and intends to edit the currently
	 * selected thumb.
	 * @param x the x-value of the mouse click location
	 * @param y the y-value of the mouse click location
	 * @return <code>true</code> if this event was consumed, or acted upon.
	 * <code>false</code> if this is unimplemented.
	 */
	public boolean doDoubleClick(int x,int y) {
		return false;
	}

	/** An optional method subclasses can override to react to the user's
	 * request for a contextual menu.  When a thumb is right-clicked the
	 * user is trying to edit the value for that thumb.  A right-click probably
	 * suggests the user wants very quick, simple options to adjust a thumb.
	 * <P>By default this method does nothing, and returns <code>false</code>
	 * @param x the x-value of the mouse click location
	 * @param y the y-value of the mouse click location
	 * @return <code>true</code> if this event was consumed, or acted upon.
	 * <code>false</code> if this is unimplemented.
	 */
	public boolean doPopup(int x,int y) {
		return false;
	}
	
	/** Tells if tick marks are to be painted.
	 * @return whether ticks should be painted on this slider.
	 */
	public boolean isPaintTicks() {
		Boolean b = (Boolean)getClientProperty(PAINT_TICKS_PROPERTY);
		if(b==null) return false;
		return b;
	}
	
	/** Turns on/off the painted tick marks for this slider.
	 * <P>This triggers a <code>PropertyChangeEvent</code> for 
	 * <code>PAINT_TICKS_PROPERTY</code>.
	 * @param b whether tick marks should be painted
	 */
	public void setPaintTicks(boolean b) {
		putClientProperty(PAINT_TICKS_PROPERTY, b);
	}
	
	/** This creates and inserts a thumb at a position indicated.
	 * <P>This method relies on the abstract <code>createValueForInsertion(float)</code> to
	 * determine what value to put at the new thumb location.
	 * 
	 * @param pos the new thumb position
	 * @return the index of the newly created thumb
	 * @see #createValueForInsertion(float)
	 */
	public int addThumb(float pos) {
		if(pos<0 || pos>1)
			throw new IllegalArgumentException("the new position ("+pos+") must be between zero and one");
		T newValue = createValueForInsertion(pos);
		float[] f = new float[thumbPositions.length+1];
		T[] c = createSimilarArray(values, values.length+1);

		int newIndex = -1;
		if(pos<thumbPositions[0]) {
			System.arraycopy(thumbPositions,0,f,1,thumbPositions.length);
			System.arraycopy(values,0,c,1,values.length);
			newIndex = 0;
			f[0] = pos;
			c[0] = newValue;
		} else if(pos>thumbPositions[thumbPositions.length-1]) {
			System.arraycopy(thumbPositions,0,f,0,thumbPositions.length);
			System.arraycopy(values,0,c,0,values.length);
			newIndex = f.length-1;
			f[f.length-1] = pos;
			c[c.length-1] = newValue;
		} else {
			boolean addedYet = false;
			for(int a = 0; a<f.length; a++) {
				if(addedYet==false && thumbPositions[a]<pos) {
					f[a] = thumbPositions[a];
					c[a] = values[a];
				} else {
					if(addedYet==false) {
						c[a] = newValue;
						f[a] = pos;
						addedYet = true;
						newIndex = a;
					} else {
						f[a] = thumbPositions[a-1];
						c[a] = values[a-1];
					}
				}
			}
		}
		setValues(f,c);
		return newIndex;
	}
	
	/** This is used to notify other objects when the user is in the process
	 * of adjusting values in this slider.
	 * <P>A listener may not want to act on certain changes until this property
	 * is <code>false</code> if it is expensive to process certain changes.
	 * 
	 * <P>This triggers a <code>PropertyChangeEvent</code> for 
	 * <code>ADJUST_PROPERTY</code>.
	 * @param b
	 */
	public void setValueIsAdjusting(boolean b) {
		putClientProperty(ADJUST_PROPERTY, b);
	}
	
	/** <code>true</code> if the user is current modifying this component.
	 * @return the value of the <code>adjusting</code> property
	 */
	public boolean isValueAdjusting() {
		Boolean b = (Boolean)getClientProperty(ADJUST_PROPERTY);
		if(b==null) return false;
		return b;
	}
	
	/** The thumb positions for this slider.
	 * <P>There is a one-to-one correspondence between this array and the
	 * <code>getValues()</code> array.
	 * <P>This array is always sorted in ascending order.
	 * 
	 * @return an array of the positions of thumbs.
	 */
	public float[] getThumbPositions() {
		float[] f = new float[thumbPositions.length];
		System.arraycopy(thumbPositions,0,f,0,f.length);
		return f;
	}

	/** The values for thumbs for this slider.
	 * <P>There is a one-to-one correspondence between this array and the
	 * <code>getThumbPositions()</code> array.
	 * 
	 * @return an array of the values associated with each thumb.
	 */
	public T[] getValues() {
		T[] c = createSimilarArray(values, values.length);
		System.arraycopy(values,0,c,0,c.length);
		return c;
	}
	
	/**
	 * 
	 * @param f an array of floats
	 * @return a string representation of f
	 */
	private static String toString(float[] f) {
		StringBuffer sb = new StringBuffer();
		sb.append('[');
		for(int a = 0; a<f.length; a++) {
			sb.append(Float.toString(f[a]));
			if(a!=f.length-1) {
				sb.append(", ");
			}
		}
		sb.append(']');
		return sb.toString();
	}
	
	/** This assigns new positions/values for the thumbs in this slider.
	 * The two must be assigned at exactly the same time, so there is
	 * always the same number of thumbs/sliders.
	 * 
	 * <P>This triggers a <code>PropertyChangeEvent</code> for 
	 * <code>VALUES_PROPERTY</code>, and possibly for the
	 * <code>SELECTED_THUMB_PROPERTY</code> if that had to be adjusted, too.
	 * 
	 * @param thumbPositions an array of the new position of each thumb
	 * @param values an array of the value associated with each thumb
	 * @throws IllegalArgumentException if the size of the arrays are different,
	 * or if the thumbPositions array is not sorted in ascending order.
	 */
	public void setValues(float[] thumbPositions,T[] values) {
		if(values.length!=thumbPositions.length)
			throw new IllegalArgumentException("there number of positions ("+thumbPositions.length+") must equal the number of values ("+values.length+")");
		
		for(int a = 0; a<values.length; a++) {
			if(values[a]==null)
				throw new NullPointerException();
			if(a>0 && thumbPositions[a]<thumbPositions[a-1])
				throw new IllegalArgumentException("the thumb positions must be ascending order ("+toString(thumbPositions)+")");
			if(thumbPositions[a]<0 || thumbPositions[a]>1)
				throw new IllegalArgumentException("illegal thumb value "+thumbPositions[a]+" (must be between zero and one)");
		}
		
		//don't clone arrays and fire off events if
		//there really is no change here:
		if(thumbPositions.length==this.thumbPositions.length) {
			boolean equal = true;
			for(int a = 0; a<thumbPositions.length && equal; a++) {
				if(thumbPositions[a]!=this.thumbPositions[a])
					equal = false;
			}
			for(int a = 0; a<values.length && equal; a++) {
				if(!values[a].equals(this.values[a]))
					equal = false;
			}
			if(equal)
				return; //no change!  go home.
		}
		
		this.thumbPositions = new float[thumbPositions.length];
		System.arraycopy(thumbPositions,0,this.thumbPositions,0,thumbPositions.length);
		this.values = createSimilarArray(values, values.length);
		System.arraycopy(values,0,this.values,0,values.length);
		int oldThumb = getSelectedThumb();
		int newThumb = oldThumb;
		if(newThumb>=thumbPositions.length) {
			newThumb = thumbPositions.length-1;
		}
		firePropertyChange(VALUES_PROPERTY, null, values);
		if(oldThumb!=newThumb) {
			setSelectedThumb(newThumb);
		}
		fireChangeListeners();
	}
	
	/** The number of thumbs in this slider.
	 * 
	 * @return the number of thumbs.
	 */
	public int getThumbCount() {
		return thumbPositions.length;
	}
	
	/** Assigns the currently selected thumb.  A value of -1 indicates
	 * that no thumb is currently selected.
	 * <P>A slider should always have a selected thumb if it has the keyboard focus, though,
	 * so be careful when you modify this.
	 * <P>This triggers a <code>PropertyChangeEvent</code> for 
	 * <code>SELECTED_THUMB_PROPERTY</code>.
	 * 
	 * @param index the new selected thumb
	 */
	public void setSelectedThumb(int index) {
		putClientProperty(SELECTED_THUMB_PROPERTY,new Integer(index));
	}
	
	/** Returns the selected thumb index, or -1 if this component doesn't have
	 *  the keyboard focus.
	 * 
	 * @return the selected thumb index
	 */
	public int getSelectedThumb() {
		return getSelectedThumb(true);
	}

	/** Returns the currently selected thumb index.
	 * <P>Note this might be -1, indicating that there is no selected thumb.
	 * 
	 * <P>It is recommend you use the <code>getSelectedThumb()</code> method
	 * most of the time.  This method is made public so UI's can provide
	 * a better user experience as this component gains and loses focus.
	 * 
	 * @param ignoreIfUnfocused if this component doesn't have focus and this
	 * is <code>true</code>, then this returns -1.  If this is <code>false</code>
	 * then this returns the internal value used to store the selected index, but
	 * the user may not realize this thumb is "selected".
	 * @return the selected thumb
	 */
	public int getSelectedThumb(boolean ignoreIfUnfocused) {
		if(hasFocus()==false && ignoreIfUnfocused) return -1;
		Integer i = (Integer)getClientProperty(SELECTED_THUMB_PROPERTY);
		if(i==null) return -1;
		return i.intValue();
	}
	
	/** Controls whether thumbs are automatically added when the
	 * user clicks in a space that doesn't already have a thumb.
	 * 
	 * @param b whether auto adding is active or not
	 */
	public void setAutoAdding(boolean b) {
		putClientProperty(AUTOADD_PROPERTY, b);
	}
	
	/** Whether thumbs are automatically added when the
	 * user clicks in a space that doesn't already have a thumb.
	 */
	public boolean isAutoAdding() {
		Boolean b = (Boolean)getClientProperty(AUTOADD_PROPERTY);
		if(b==null) return true;
		return b;
	}
	
	/** The orientation of this slider.
	 * 
	 * @return HORIZONTAL or VERTICAL
	 */
	public int getOrientation() {
		Integer i = (Integer)getClientProperty(ORIENTATION_PROPERTY);
		if(i==null) return HORIZONTAL;
		return i;
	}
	
	/** Reassign the orientation of this slider.
	 * 
	 * @param i must be HORIZONTAL or VERTICAL
	 */
	public void setOrientation(int i) {
		if(!(i==SwingConstants.HORIZONTAL || i==SwingConstants.VERTICAL))
			throw new IllegalArgumentException("the orientation must be HORIZONTAL or VERTICAL");
		putClientProperty(ORIENTATION_PROPERTY, i);
	}
	
	/** Whether this slider is inverted or not.
	 */
	public boolean isInverted() {
		Boolean b = (Boolean)getClientProperty(INVERTED_PROPERTY);
		if(b==null) return false;
		return b;
	}

	/** Assigns whether this slider is inverted or not.
	 * 
	 * <P>This triggers a <code>PropertyChangeEvent</code> for 
	 * <code>INVERTED_PROPERTY</code>.
	 */
	public void setInverted(boolean b) {
		putClientProperty(INVERTED_PROPERTY, b);
	}

	public Collision getCollisionPolicy() {
		Collision c = (Collision)getClientProperty(COLLISION_PROPERTY);
		if(c==null)
			c = Collision.JUMP_OVER_OTHER;
		return c;
	}
	
	public void setCollisionPolicy(Collision c) {
		putClientProperty(COLLISION_PROPERTY, c);
	}

	public boolean isThumbRemovalAllowed() {
		Boolean b = (Boolean)getClientProperty(REMOVAL_ALLOWED);
		if(b==null)
			b = true;
		return b;
	}
	
	public void setThumbRemovalAllowed(boolean b) {
		putClientProperty(REMOVAL_ALLOWED, b);
	}

	public void setMinimumThumbnailCount(int i) {
		putClientProperty(THUMB_MINIMUM_PROPERTY, i);
	}
	
	public int getMinimumThumbnailCount() {
		Integer i = (Integer)getClientProperty(THUMB_MINIMUM_PROPERTY);
		if(i==null) return 1;
		return i;
	}

	public void setThumbOverlap(boolean i) {
		putClientProperty(THUMB_OVERLAP_PROPERTY, i);
	}
	
	public boolean isThumbOverlap() {
		Boolean b = (Boolean)getClientProperty(THUMB_OVERLAP_PROPERTY);
		if(b==null) return false;
		return b;
	}
}