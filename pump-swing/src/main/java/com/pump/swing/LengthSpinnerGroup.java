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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import com.pump.math.Length;

/** Similar to a ButtonGroup: this monitors several <code>LengthSpinners</code>
 * and makes sure they all use the same units.
 * <P>It's most likely that, given a set of related controls (for example:
 * width and height), if the user wants to view one in centimeters: then the
 * other should be viewed in centimeters, too.  So if these spinners are grouped:
 * then when one changes unit, the other changes unit automatically.
 *
 */
public class LengthSpinnerGroup {
	PropertyChangeListener unitListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent e) {
			Length.Unit newValue = (Length.Unit)e.getNewValue();
			for(int a = 0; a<getSpinnerCount(); a++) {
				LengthSpinner spinner = getSpinner(a);
				spinner.putClientProperty(LengthSpinner.PROPERTY_UNIT, newValue);
			}
		}
	};
	
	ArrayList<LengthSpinner> spinners = new ArrayList<LengthSpinner>();
	
	public void add(LengthSpinner spinner) {
		if(spinners.contains(spinner)==false) {
			spinner.addPropertyChangeListener(LengthSpinner.PROPERTY_UNIT, unitListener);
			spinners.add(spinner);
		}
	}
	
	public int getSpinnerCount() {
		return spinners.size();
	}
	
	public LengthSpinner getSpinner(int i) {
		return spinners.get(i);
	}
	
	public void remove(LengthSpinner spinner) {
		spinners.remove(spinner);
		spinner.removePropertyChangeListener(LengthSpinner.PROPERTY_UNIT, unitListener);
	}
}