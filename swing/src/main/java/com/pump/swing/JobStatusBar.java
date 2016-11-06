/*
 * @(#)JobStatusBar.java
 *
 * $Date: 2014-03-23 02:01:48 -0400 (Sun, 23 Mar 2014) $
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

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.job.Job;
import com.pump.job.JobManager;

/** A panel that contains a label and a spinning progress bar.
 * Both are made visible when a job is active in the <code>JobManager</code>.
 */
public class JobStatusBar extends JPanel {
	private static final long serialVersionUID = 1L;
	
	class JobPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		protected JLabel descriptionLabel = new JLabel(" ");
		protected JLabel noteLabel = new JLabel(" ");
		protected JThrobber throbber = new JThrobber();
		
		Job job;
		
		JobPanel(Job job) {
			this.job = job;
			setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0; c.gridy = 0;
			c.weightx = 1; c.weighty = 0;
			c.anchor = GridBagConstraints.WEST;
			c.fill = GridBagConstraints.HORIZONTAL;
			c.insets = new Insets(4,4,4,4);
			add(descriptionLabel, c);
			c.gridy++;
			add(noteLabel, c);
			
			noteLabel.setFont(UIManager.getFont("ToolTip.font"));
			
			c.gridx++; c.anchor = GridBagConstraints.EAST;
			c.fill = GridBagConstraints.NONE;
			c.weightx = 0;
			c.insets = new Insets(0,4,0,4); c.gridheight = GridBagConstraints.REMAINDER;
			add(throbber, c);
			
			ChangeListener changeListener = new ChangeListener() {
				Runnable runnable = new Runnable() {
					public void run() {
						updateComponents();
					}
				};
				
				public void stateChanged(ChangeEvent e) {
					SwingUtilities.invokeLater(runnable);
				}
			};
			
			job.addDescriptionListener(changeListener);
			job.addNoteListener(changeListener);

			updateComponents();
		}
		
		protected void updateComponents() {
			String d = job.getDescription();
			if(d==null || d.length()==0) d = " ";
			descriptionLabel.setText(d);

			String n = job.getNote();
			if(n==null || n.length()==0) n = " ";
			noteLabel.setText(n);
		}
	}
	
	protected final JobManager jobManager;
	protected boolean alwaysVisible;
	
	ChangeListener managerListener = new ChangeListener() {
		Runnable runnable = new Runnable() {
			public void run() {
				updateComponents();
			}
		};
		public void stateChanged(ChangeEvent e) {
			SwingUtilities.invokeLater(runnable);
		}
	};
	
	public JobStatusBar(JobManager jobManager,boolean alwaysVisible) {
		super(new GridBagLayout());
		this.jobManager = jobManager;
		this.alwaysVisible = alwaysVisible;
		jobManager.addChangeListener(managerListener);
		updateComponents();
	}
	
	private static <T> int getIndex(T[] array, T element) {
		for(int a = 0; a<array.length; a++) {
			if(array[a]==element) return a;
		}
		return -1;
	}
	
	private JPanel fluff = new JPanel();
	protected void updateComponents() {
		setVisible(alwaysVisible || jobManager.isActive());
		Job[] jobs = jobManager.getActiveJobs();
		List<JobPanel> toKeep = new ArrayList<JobPanel>();
		int toRemoveCtr = 0;
		for(int a = 0; a<getComponentCount(); a++) {
			Component c = getComponent(a);
			if(c instanceof JobPanel) {
				JobPanel jp = (JobPanel)c;
				int i = getIndex(jobs, jp.job);
				if(i!=-1) {
					toKeep.add(jp);
					jobs[i] = null;
				} else {
					toRemoveCtr++;
				}
			}
		}
		if(toKeep.size()==jobs.length && toRemoveCtr==0)
			return;
		
		removeAll();
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 1; c.gridy = 1; c.weightx = 1; c.weighty = 0;
		c.fill = GridBagConstraints.BOTH;
		for(JobPanel jp : toKeep) {
			add(jp, c);
			c.gridy++;
		}
		for(int a = 0; a<jobs.length; a++) {
			if(jobs[a]!=null) {
				add(new JobPanel(jobs[a]), c);
				c.gridy++;
			}
		}
		c.weighty = 1;
		fluff.setOpaque(false);
		add(fluff, c);
		revalidate();
	}
}
