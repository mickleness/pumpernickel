/*
 * @(#)LengthSpinnerEditor.java
 *
 * $Date: 2014-03-13 04:15:48 -0400 (Thu, 13 Mar 2014) $
 *
 * Copyright (c) 2011 by Jeremy Wood.
 * All rights reserved.
 *
 * The copyright of this software is owned by Jeremy Wood. 
 * You may not use, copy or modify this software, except in  
 * accordance with the license agreement you entered into with  
 * Jeremy Wood. For details see accompanying license terms.
 * 
 * This software is probably, but not necessarily, discussed here:
 * https://javagraphics.java.net/
 * 
 * That site should also contain the most recent official version
 * of this software.  (See the SVN repository for more details.)
 */
package com.pump.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Iterator;
import java.util.Locale;

import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.DefaultFormatterFactory;

import com.pump.math.Length;

/** The editor used for the <code>LengthSpinner</code>.
 * 
 */
class LengthSpinnerEditor extends JSpinner.DefaultEditor {
	private static final long serialVersionUID = 1L;
	
	/** Position the caret at the beginning of the text field after
	 * the user hits return.
	 * (I'm not entirely sure why we do this, but it's what "normal"
	 * SpinnerNumberModels do somehow, so we'll do it too.)
	 */
	private static ActionListener adjustCaretListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			JTextField f = (JTextField)e.getSource();
			f.setCaretPosition(0);
		}
	};
	
	public LengthSpinnerEditor(LengthSpinner spinner,DecimalFormat numberFormat) {
		super(spinner);
	    if (!(spinner.getModel() instanceof LengthSpinnerModel)) {
			throw new IllegalArgumentException("model not a LengthSpinnerModel");
	    }
	    if(numberFormat==null) {
	    	numberFormat = new DecimalFormat("0.##");
	    }
		
		JFormattedTextField ftf = getTextField();
		ftf.setHorizontalAlignment(SwingConstants.TRAILING);
		ftf.setEditable(true);
		
		LengthSpinnerModel m = (LengthSpinnerModel)spinner.getModel();
		
		LengthFormatter formatter = new LengthFormatter(spinner,numberFormat);
		
		ftf.setFormatterFactory(new DefaultFormatterFactory(formatter));

		int columns = 0;
		Iterator<Length> i = m.getMaximums();
		while(i.hasNext()) {
			Length l = i.next();
			columns = Math.max(columns,formatter.valueToString(l).length());
		}
		i = m.getMinimums();
		while(i.hasNext()) {
			Length l = i.next();
			columns = Math.max(columns,formatter.valueToString(l).length());
		}
		setColumns(columns);
		
		ftf.addActionListener(adjustCaretListener);
	}
	
	/** Defines the number of columns the text field should use.
	 * <P>The constructor initially makes a reasonable decision
	 * about this, but if you need high accuracy you may want
	 * to adjust this yourself.
	 */
	public void setColumns(int c) {
		JFormattedTextField ftf = getTextField();
		ftf.setColumns(c);
	}
	
	/** Returns the number of columns the text field is
	 * designed for.
	 */
	public int getColumns() {
		JFormattedTextField ftf = getTextField();
		return ftf.getColumns();
	}

	@Override
	public void commitEdit() throws ParseException {
		super.commitEdit();
		getTextField().setCaretPosition(0);
	}
}

/** The formatter for the <code>LengthSpinner</code>.
 * 
 * This formats a number (using a DecimalFormat) and appends
 * an abbreviation from <code>Length.Unit</code>.
 */
class LengthFormatter extends JFormattedTextField.AbstractFormatter {
	private static final long serialVersionUID = 1L;
	
	LengthSpinner spinner;
	DecimalFormat format;
	
	public LengthFormatter(LengthSpinner spinner,DecimalFormat format) {
		this.spinner = spinner;
		this.format = format;
	}

	/** This can parse a single number "3.0" or a number followed by
	 * a unit name "3.0 meters".  Anything else throws an exception.
	 * 
	 */
	@Override
	public Object stringToValue(String s) throws ParseException {
		LengthSpinnerModel m = (LengthSpinnerModel)spinner.getModel();
		
		int i = s.indexOf(' ');
		if(i==-1) {
			try {
				Number n = NumberFormat.getInstance(Locale.getDefault()).parse(s);
				return new Length(n.doubleValue(),m.getCurrentUnit());
			} catch(NumberFormatException e) {
				throw new ParseException("unrecognized number value \""+s+"\"",0);
			}
		}
		
		String s1 = s.substring(0,i).trim();
		String s2 = s.substring(i+1).trim();
		if(s2.indexOf(' ')!=-1) {
			throw new ParseException("unrecognized unit name: \""+s+"\"",0);
		}
		try {
			Number n = NumberFormat.getInstance(Locale.getDefault()).parse(s1);
			Length.Unit f = Length.getUnitByName(s2.toLowerCase());
			if(f==null) {
				throw new ParseException("unrecognized unit: \""+s2+"\"",0);
			}
			return new Length(n.doubleValue(),f);
		} catch(NumberFormatException e) {
			throw new ParseException("unrecognized number value \""+s1+"\"",0);
		}
	}

	@Override
	public String valueToString(Object obj) {
		Length l = (Length)obj;
		return format.format(l.getValue(l.getUnit()))+" "+l.getUnit().getAbbreviation();
	}
	
}
