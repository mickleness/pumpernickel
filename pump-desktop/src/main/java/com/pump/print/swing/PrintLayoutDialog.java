/*
 * @(#)PrintLayoutDialog.java
 *
 * $Date: 2016-01-18 21:58:54 -0500 (Mon, 18 Jan 2016) $
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
package com.pump.print.swing;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.SpinnerNumberModel;

import com.pump.awt.Paintable;
import com.pump.print.PrintLayout;
import com.pump.swing.DialogFooter;
import com.pump.swing.HelpComponent;

/** This dialog prompts the user to design a PrintLayout object.
 * 
 */
public class PrintLayoutDialog extends JDialog {

	private static final long serialVersionUID = 1L;
	
	Paintable[] paintables;
	PrintLayout printLayout;
	PrintLayoutPreviewPanel layoutPreview;
	JButton printButton = new JButton("Print");
	JButton cancelButton = new JButton("Cancel");
	JButton okButton = new JButton("OK");
	PrintLayoutPropertiesPanel propertiesPanel;
	DialogFooter footer;
	
	/** Used to indicate if the changes should be saved when
	 * this dialog is hidden.
	 */
	private boolean commitChanges = false;
	
	/** The original layout passed to the constructor.
	 * This is only updated when the user presses OK or Print.
	 */
	private PrintLayout originalPrintLayout;
	
	ActionListener actionListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			if(e.getSource()==printButton) {
				commitChanges = true;
				print();
			} else if(e.getSource()==okButton) {
				commitChanges = true;
			}
		}
	};
	
	/** Creates a new dialog with the Paintable tiles that might be printed.
	 * This is the recommended constructor to use if you have the Paintable
	 * objects ready at this point.
	 * @param parent the parent Frame.
	 * @param title the dialog title.
	 * @param printLayout the PrintLayout to use.
	 * <P>This is modified only after 'OK' or 'Print' is selected.  At that
	 * point the user's changes are stored in this layout.
	 * @param tiles the tiles to print.
	 * @param helpURL an optional URL that will provide a button/link for
	 * help in the lower-left corner of the dialog.  This may be null.
	 */
	public PrintLayoutDialog(Frame parent,String title,PrintLayout printLayout,Paintable[] tiles,String helpURL) {
		this(parent,title,printLayout,helpURL);
		paintables = new Paintable[tiles.length];
		System.arraycopy(tiles,0,paintables,0,tiles.length);
		layoutPreview.setPaintables(paintables);
		pack();
	}

	/** Creates a new dialog.
	 * It is recommended that you provide the Paintables up front, so you should
	 * use the other constructor.  But if this is not possible, then this
	 * constructor will create a valid dialog.  In this case you need to overwrite
	 * the print() method: otherwise this dialog has no idea what to print.
	 * @param parent the parent Frame.
	 * @param title the dialog title.
	 * @param layout the PrintLayout to use.
	 * <P>This is modified only after 'OK' or 'Print' is selected.  At that
	 * point the user's changes are stored in this layout.
	 * @param helpURL an optional URL that will provide a button/link for
	 * help in the lower-left corner of the dialog.  This may be null.
	 *
	 * @throws HeadlessException if this is invoked in a headless environment.
	 */
	public PrintLayoutDialog(Frame parent, String title,PrintLayout layout,String helpURL)
			throws HeadlessException {
		super(parent, title, true);
		originalPrintLayout = layout;
		printLayout = new PrintLayout(layout);
		getContentPane().setLayout(new GridBagLayout());
		propertiesPanel = new PrintLayoutPropertiesPanel(printLayout,true,false);
		layoutPreview  = new PrintLayoutPreviewPanel(printLayout);
		JButton[] dismissButtons = new JButton[] {
				printButton,
				okButton,
				cancelButton
		};
		
		JComponent[] leftControls;
		if(helpURL==null || helpURL.length()==0) {
			leftControls = new JComponent[] {};
		} else {
			leftControls = new JComponent[] { HelpComponent.createHelpComponent(helpURL, null) };
		}
		
		footer = new DialogFooter(leftControls,
				dismissButtons,
				true,
				dismissButtons[0]);
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0; c.weightx = 1; c.weighty = 0;
		c.fill = GridBagConstraints.NONE;
		c.insets = new Insets(10,10,10,10);
		c.gridy++; c.fill = GridBagConstraints.BOTH; c.weighty = 1;
		getContentPane().add(layoutPreview,c);
		
		c.gridx++; c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST; c.weightx = 0;
		getContentPane().add(propertiesPanel,c);
		
		
		c.gridx = 0; c.gridy++; c.gridwidth = 2; c.weighty = 0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(10,10,10,10); c.anchor = GridBagConstraints.CENTER;
		getContentPane().add(footer,c);
		
		Dimension d = propertiesPanel.getPreferredSize();
		d.width = Math.max(d.width,d.height);
		d.height = Math.max(d.width,d.height);
		layoutPreview.setPreferredSize(d);

		footer.addActionListener(actionListener);
		
		setResizable(false);
		
		pack();
	}

	/** Returns the <code>PrintLayoutPropertiesPanel</code> this dialog uses.
	 * 
	 */
	public PrintLayoutPropertiesPanel getPropertiesPanel() {
		return propertiesPanel;
	}
	
	/** Returns the <code>PrintLayoutPreviewPanel</code> this dialog uses.
	 * 
	 */
	public PrintLayoutPreviewPanel getPreviewPanel() {
		return layoutPreview;
	}
	
	@Override
	public void setVisible(boolean b) {
		if(b) {
			commitChanges = false;
			printLayout.setLayout(originalPrintLayout);
		}
		super.setVisible(b);
		
		if(!b) {
			if(commitChanges) {
				originalPrintLayout.setLayout(printLayout);
			}
		}
	}
	
	/** This is equivalent to click the "Print" button.
	 * This method will throw an exception if this dialog was
	 * not constructed with an array of Paintable objects.
	 */
	public void print() {
		if(paintables==null) {
			throw new NullPointerException("There was nothing provided to print.  Either an array of Paintable objects needs to be passed to the constructor of this dialog, or the print() method be overridden.");
		}
		PrinterJob job = PrinterJob.getPrinterJob();
		PageFormat pageFormat = printLayout.createPageFormat();
		SpinnerNumberModel model = (SpinnerNumberModel)layoutPreview.navigationPanel.getModel();
		int pageCount = ((Number)model.getMaximum()).intValue();
		job.setPrintable( printLayout.createPrintable(paintables,0,pageCount),pageFormat);
		
		if(job.printDialog()) {
			try {
				job.print();
			} catch(PrinterException e) {
				e.printStackTrace();
			}
		}
	}
}
