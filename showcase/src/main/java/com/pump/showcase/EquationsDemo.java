package com.pump.showcase;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.pump.math.Equations;

public class EquationsDemo extends JPanel {
	private static final long serialVersionUID = 1L;
	
	class VariablesPanel extends JPanel {
		JTextField[][] fields;
		
		public VariablesPanel(int varCount) {
			super(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			fields = new JTextField[varCount][varCount+1];
			for(int y = 0; y<fields.length; y++) {
				c.gridy = y;
				c.gridx = 0;
				for(int x = 0; x<fields[y].length; x++) {
					fields[y][x] = new JTextField(5);
					add(fields[y][x], c);
					c.gridx++;
					if(x!=fields[y].length-1) {
						String s = " * v"+(x+1);
						if(x==fields[y].length-2) {
							s += " = ";
						} else {
							s += " + ";
						}
						add(new JLabel(s), c);
						c.gridx++;
					}
				}
			}
		}

		public double[][] getVariables() {
			double[][] k = new double[fields.length][];
			for(int y = 0; y<fields.length; y++) {
				k[y] = new double[fields[y].length];
				for(int x = 0; x<fields[y].length; x++) {
					k[y][x] = Double.parseDouble(fields[y][x].getText());
				}
			}
			return k;
		}

		public void setVariables(double[][] array) {
			for(int y = 0; y<fields.length; y++) {
				for(int x = 0; x<fields[y].length; x++) {
					fields[y][x].setText(Double.toString(array[y][x]));
				}
			}
		}
	}

	JPanel headerRow = new JPanel(new GridBagLayout());
	JLabel label = new JLabel("Number of Variables:");
	JSpinner spinner = new JSpinner(new SpinnerNumberModel(3,2,20,1));
	JButton solve = new JButton("Solve");
	JTextArea descriptionText = new JTextArea("This uses Gaussian elimination to solve a simple system of equations for different variables.");
	VariablesPanel varPanel;
	
	public EquationsDemo() {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0; c.weightx = 1; c.weighty = 1;
		c.insets = new Insets(4,4,4,4);
		headerRow.add(label, c);
		c.gridx++;
		headerRow.add(spinner, c);
		
		descriptionText.setOpaque(false);
		descriptionText.setEditable(false);
		descriptionText.setLineWrap(true);
		descriptionText.setWrapStyleWord(true);
		
		repopulate();
	}
	
	protected void repopulate() {
		int varCount = ((Number)spinner.getValue()).intValue();
		varPanel = new VariablesPanel(varCount);
		
		setLayout(new GridBagLayout());
		removeAll();
		
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0;
		c.weightx = 1; c.weighty = 0;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0,5,15,5);
		add(descriptionText, c);
		c.insets = new Insets(5,5,5,5);
		c.gridy++;
		c.fill = GridBagConstraints.NONE;
		add(headerRow, c);
		c.gridy++;
		add(varPanel, c);
		c.gridy++;
		add(solve, c);
		
		solve.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				double[][] array = varPanel.getVariables();
				Equations.solve(array, true);
				varPanel.setVariables(array);
			}
		});
		spinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				repopulate();
			}
		});
	
		invalidate();
		revalidate();
	}
}
