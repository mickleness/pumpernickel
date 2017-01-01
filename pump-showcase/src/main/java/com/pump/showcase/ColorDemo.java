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
package com.pump.showcase;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import com.pump.inspector.InspectorGridBagLayout;
import com.pump.plaf.BasicColors;
import com.pump.plaf.ColorPaletteUI;
import com.pump.plaf.ColorSet;
import com.pump.plaf.DefaultColors;
import com.pump.plaf.HSBColorPaletteUI;
import com.pump.plaf.HSLColorPaletteUI;
import com.pump.plaf.HueDistribution;
import com.pump.plaf.ModifierColorPaletteUI;
import com.pump.plaf.SimpleColorPaletteUI;
import com.pump.swing.CollapsibleContainer;
import com.pump.swing.ColorPalette;
import com.pump.swing.ColorPicker;
import com.pump.swing.SectionContainer.Section;

public class ColorDemo extends JPanel {
	private static final long serialVersionUID = 1L;
	
	static HueDistribution hues = new HueDistribution(new float[] {0, //red
			1f/12f, //orange
			1f/6f, //yellow
			1f/3f, //green
			1f/2f, //cyan
			2f/3f, //blue
			5f/6f, //purple
			1 //full circle
			});
	
	enum PaletteStyle {
		Solid() {
			public void run(ColorPalette palette) {
				palette.putClientProperty(ColorSet.PALETTE_STYLE_PROPERTY, ColorSet.PALETTE_STYLE_DEFAULT );
			}
		}, Gradient() {
			public void run(ColorPalette palette) {
				palette.putClientProperty(ColorSet.PALETTE_STYLE_PROPERTY, ColorSet.PALETTE_STYLE_GRADIENT );
			}
		}, Streaks() {
			public void run(ColorPalette palette) {
				palette.putClientProperty(ColorSet.PALETTE_STYLE_PROPERTY, ColorSet.PALETTE_STYLE_STREAKS );
			}
		};
	
		public abstract void run(ColorPalette palette);
	}
	
	enum PaletteCellStyle {
		None() {
			public void run(ColorPalette palette) {
				palette.putClientProperty(ColorSet.PALETTE_CELL_STYLE_PROPERTY, ColorSet.PALETTE_CELL_STYLE_DEFAULT);
			}
		}, Shadow() {
			public void run(ColorPalette palette) {
				palette.putClientProperty(ColorSet.PALETTE_CELL_STYLE_PROPERTY, ColorSet.PALETTE_CELL_STYLE_SHADOW);
			}
		}, Scribble() {
			public void run(ColorPalette palette) {
				palette.putClientProperty(ColorSet.PALETTE_CELL_STYLE_PROPERTY, ColorSet.PALETTE_CELL_STYLE_SCRIBBLE);
			}
		};

		
		public abstract void run(ColorPalette palette);
	}
	
	enum PaletteType {
		
		Simple_Large() {

			@Override
			ColorPaletteUI createPaletteUI() {
				return new SimpleColorPaletteUI(new DefaultColors(true));
			}
			
		}, Simple_Small() {

			@Override
			ColorPaletteUI createPaletteUI() {
				return new SimpleColorPaletteUI(new BasicColors(true));
			}
			
		}, Modifier() {

			@Override
			ColorPaletteUI createPaletteUI() {
				return new ModifierColorPaletteUI();
			}
			
		}, HSB() {

			@Override
			ColorPaletteUI createPaletteUI() {
				return new HSBColorPaletteUI();
			}
			
		}, HSL() {

			@Override
			ColorPaletteUI createPaletteUI() {
				return new HSLColorPaletteUI(hues,10,7*2);
			}
			
		};
		
		abstract ColorPaletteUI createPaletteUI();
	}
	
	public static class ColorPaletteDemo extends JPanel {
		private static final long serialVersionUID = 1L;
		
		JTextArea description = new JTextArea("The ColorPalette component is a grid presenting a variety of colors the user can select. It is not as compact as the ColorWell component, but it is not as large as a typical color-choosing dialog.");
		JPanel controls = new JPanel(new GridBagLayout());
		JPanel paletteContainer = new JPanel(new GridBagLayout());
		ColorPalette colorPalette = new ColorPalette();
		
		JComboBox<PaletteType> typeComboBox = createComboBox(PaletteType.class, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshPalette();
			}
		});
		
		JComboBox<PaletteStyle> styleComboBox = createComboBox(PaletteStyle.class, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PaletteStyle style = (PaletteStyle)styleComboBox.getSelectedItem();
				style.run(colorPalette);
			}
		});

		JComboBox<PaletteCellStyle> cellStyleComboBox = createComboBox(PaletteCellStyle.class, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PaletteCellStyle style = (PaletteCellStyle)cellStyleComboBox.getSelectedItem();
				style.run(colorPalette);
			}
		});
		
		private <T> JComboBox<T> createComboBox(Class<T> type,ActionListener listener) {
			JComboBox<T> comboBox = new JComboBox<>();
			for(T constant : type.getEnumConstants()) {
				comboBox.addItem(constant);
			}
			comboBox.addActionListener(listener);
			return comboBox;
		}
		
		public ColorPaletteDemo() {
			description.setEditable(false);
			description.setOpaque(false);
			description.setLineWrap(true);
			description.setWrapStyleWord(true);
			
			setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0; c.gridy = 0 ; c.weightx = 1; c.weighty = 0;
			c.insets = new Insets(3,3,3,3);
			c.fill = GridBagConstraints.BOTH; c.gridwidth = GridBagConstraints.REMAINDER;
			add(description, c);
			c.gridwidth = 1;
			c.weightx = 0; c.weighty = 1;
			c.gridy++; c.gridx = 0;
			add(controls,c);
			c.gridx++; c.anchor = GridBagConstraints.WEST; c.weightx = 1;
			add(paletteContainer,c);
			
			InspectorGridBagLayout layout = new InspectorGridBagLayout(controls);
			layout.addRow(new JLabel("Type:"), typeComboBox, false);
			layout.addRow(new JLabel("Style:"), styleComboBox, false);
			layout.addRow(new JLabel("Cell Style:"), cellStyleComboBox, false);

			c = new GridBagConstraints();
			c.gridx = 0; c.gridy = 0; c.weightx = 1; c.weighty = 1;
			paletteContainer.add( colorPalette, c);
			
			refreshPalette();
		}
		
		private void refreshPalette() {
			PaletteType type = (PaletteType) typeComboBox.getSelectedItem();
			colorPalette.setUI(type.createPaletteUI());
		}
	}

	public static class ColorPickerDemo extends JPanel {
		private static final long serialVersionUID = 1L;

		ColorPicker picker = new ColorPicker(true,false);
		JComboBox<String> comboBox = new JComboBox<>();
		JCheckBox alphaCheckbox = new JCheckBox("Include Alpha");
		JCheckBox hsbCheckbox = new JCheckBox("Include HSB Values");
		JCheckBox rgbCheckbox = new JCheckBox("Include RGB Values");
		JCheckBox modeCheckbox = new JCheckBox("Include Mode Controls",true);
		JButton button = new JButton("Show Dialog");
		JTextArea description = new JTextArea("The ColorPicker started out as a color dialog (an alternative to the JColorChooser), but can also be reduced to a few JComponents if you'd rather place it in your UI directly.");
		JPanel controls = new JPanel(new GridBagLayout());
		
		public ColorPickerDemo() {
			description.setEditable(false);
			description.setOpaque(false);
			description.setLineWrap(true);
			description.setWrapStyleWord(true);
			
			GridBagConstraints c = new GridBagConstraints();
			c.gridx = 0; c.gridy = 0; c.weightx = 1; c.weighty = 0;
			c.insets = new Insets(5,5,5,5); c.anchor = GridBagConstraints.WEST;
			controls.add(comboBox,c);
			c.gridy++;
			controls.add(alphaCheckbox,c);
			c.gridy++;
			controls.add(hsbCheckbox,c);
			c.gridy++;
			controls.add(rgbCheckbox,c);
			c.gridy++;
			controls.add(modeCheckbox,c);
			c.gridy++;
			controls.add(button,c);
			
			setLayout(new GridBagLayout());
			c = new GridBagConstraints();
			c.gridx = 0; c.gridy = 0 ; c.weightx = 1; c.weighty = 0;
			c.insets = new Insets(3,3,3,3);
			c.fill = GridBagConstraints.BOTH; c.gridwidth = GridBagConstraints.REMAINDER;
			add(description, c);
			c.gridwidth = 1;
			c.weightx = 0; c.weighty = 1;
			c.gridy++; c.gridx = 0;
			add(controls,c);
			c.gridx++;
			add(picker,c);
			c.gridx++; c.anchor = GridBagConstraints.WEST; c.weightx = 1;
			add(picker.getExpertControls(),c);
			

			picker.setPreferredSize(new Dimension(220,200));
			
			comboBox.addItem("Hue");
			comboBox.addItem("Saturation");
			comboBox.addItem("Brightness");
			comboBox.addItem("Red");
			comboBox.addItem("Green");
			comboBox.addItem("Blue");
			
			ActionListener checkboxListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Object src = e.getSource();
					if(src==alphaCheckbox) {
						picker.setOpacityVisible(alphaCheckbox.isSelected());
					} else if(src==hsbCheckbox) {
						picker.setHSBControlsVisible(hsbCheckbox.isSelected());
					} else if(src==rgbCheckbox) {
						picker.setRGBControlsVisible(rgbCheckbox.isSelected());
					} else if(src==modeCheckbox) {
						picker.setModeControlsVisible(modeCheckbox.isSelected());
					}
					revalidate();
					repaint();
				}
			};
			picker.setOpacityVisible(false);
			picker.setHSBControlsVisible(false);
			picker.setRGBControlsVisible(false);
			picker.setHexControlsVisible(false);
			picker.setPreviewSwatchVisible(false);
			
			picker.addPropertyChangeListener(ColorPicker.MODE_PROPERTY, new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
					int m = picker.getMode();
					if(m==ColorPicker.HUE) {
						comboBox.setSelectedIndex(0);
					} else if(m==ColorPicker.SAT) {
						comboBox.setSelectedIndex(1);
					} else if(m==ColorPicker.BRI) {
						comboBox.setSelectedIndex(2);
					} else if(m==ColorPicker.RED) {
						comboBox.setSelectedIndex(3);
					} else if(m==ColorPicker.GREEN) {
						comboBox.setSelectedIndex(4);
					} else if(m==ColorPicker.BLUE) {
						comboBox.setSelectedIndex(5);
					}
				}
			});
			
			alphaCheckbox.addActionListener(checkboxListener);
			hsbCheckbox.addActionListener(checkboxListener);
			rgbCheckbox.addActionListener(checkboxListener);
			modeCheckbox.addActionListener(checkboxListener);
			button.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					Color color = picker.getColor();
					Window window = SwingUtilities.getWindowAncestor(button);
					color = ColorPicker.showDialog(window, color, true);
					if(color!=null)
						picker.setColor(color);
				}
			});

			comboBox.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					int i = ((JComboBox)e.getSource()).getSelectedIndex();
					if(i==0) {
						picker.setMode(ColorPicker.HUE);
					} else if(i==1) {
						picker.setMode(ColorPicker.SAT);
					} else if(i==2) {
						picker.setMode(ColorPicker.BRI);
					} else if(i==3) {
						picker.setMode(ColorPicker.RED);
					} else if(i==4) {
						picker.setMode(ColorPicker.GREEN);
					} else if(i==5) {
						picker.setMode(ColorPicker.BLUE);
					}
				}
			});
			comboBox.setSelectedIndex(2);
		}
	}
	
	CollapsibleContainer container = new CollapsibleContainer();
	
	Section colorPickerSection = container.addSection("colorPicker", "Color Picker");
	Section colorPaletteSection = container.addSection("colorPalette", "Color Palette");
	
	
	
	public ColorDemo() {
		fill(this, container);
		fill(colorPickerSection.getBody(), new ColorPickerDemo());
		fill(colorPaletteSection.getBody(), new ColorPaletteDemo());
		
		container.getHeader(colorPickerSection).putClientProperty(CollapsibleContainer.COLLAPSIBLE, Boolean.FALSE);
		container.getHeader(colorPaletteSection).putClientProperty(CollapsibleContainer.COLLAPSIBLE, Boolean.FALSE);
	}
	
	private void fill(JComponent parent, JComponent child) {
		parent.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0; c.weightx = 1; c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		parent.add(child, c);
	}
	
	
}