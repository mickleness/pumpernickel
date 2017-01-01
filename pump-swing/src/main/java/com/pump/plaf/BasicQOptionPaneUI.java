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

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.Border;

import com.pump.awt.TextSize;
import com.pump.swing.DialogFooter;
import com.pump.swing.QOptionPane;

public class BasicQOptionPaneUI extends QOptionPaneUI {
	
	private static Color readColor(ResourceBundle bundle,String key,Color defaultColor) {
		try {
			String string = bundle.getString(key);
			if(string!=null) {
				if(string.length()>0 && string.charAt(0)=='#') {
					string = string.substring(1);
					int hex = Integer.parseInt(string,16);
					return new Color(hex, string.length()>6);
				} else {
					throw new RuntimeException("unrecognized color \""+string+"\"");
				}
			}
		} catch(MissingResourceException e) {}
		
		return defaultColor;
	}
	
	private static boolean readBoolean(ResourceBundle bundle,String key,boolean defaultValue) {
		try {
			String string = bundle.getString(key);
			if(string!=null) {
				return Boolean.valueOf(string).booleanValue();
			}
		} catch(MissingResourceException e) {}
		
		return defaultValue;
	}
	
	private static int readInt(ResourceBundle bundle,String key,int defaultValue) {
		try {
			String string = bundle.getString(key);
			if(string!=null) {
				return Integer.parseInt(string);
			}
		} catch(MissingResourceException e) {}
		
		return defaultValue;
	}
	
	private static Insets readInsets(ResourceBundle bundle,String key) {
		int top = 0;
		int left = 0;
		int right = 0;
		int bottom = 0;
		
		try {
			top = Integer.parseInt(bundle.getString(key+".top"));
		} catch(MissingResourceException e) {}

		try {
			left = Integer.parseInt(bundle.getString(key+".left"));
		} catch(MissingResourceException e) {}
		try {
			bottom = Integer.parseInt(bundle.getString(key+".bottom"));
		} catch(MissingResourceException e) {}
		try {
			right = Integer.parseInt(bundle.getString(key+".right"));
		} catch(MissingResourceException e) {}
		
		Insets i = new Insets(top, left, bottom, right);
		return i;
	}
	
	private static Font readFont(ResourceBundle bundle,String key,Font defaultFont) {
		String baseFont = null;
		try {
			baseFont = bundle.getString(key);
		} catch(MissingResourceException e) {}
		
		Font font = null;
		if(baseFont!=null && baseFont.length()>2 && baseFont.charAt(0)=='"' && baseFont.charAt(baseFont.length()-1)=='"') {
			baseFont = baseFont.substring(1, baseFont.length()-1);
			font = UIManager.getFont(baseFont);
		} else if(baseFont!=null) {
			font = new Font(baseFont, defaultFont.getStyle(), defaultFont.getSize());
		} else {
			font = defaultFont;
		}
		
		try {
			String family = bundle.getString(key+".family");
			if(family!=null) {
				font = new Font(family, font.getStyle(), font.getSize());
			}
		} catch(MissingResourceException e) {}
		
		try {
			String style = bundle.getString(key+".style");
			if("bold".equals(style)) {
				font = font.deriveFont( Font.BOLD );
			} else if("italic".equals(style)) {
				font = font.deriveFont( Font.ITALIC );
			} else if("plain".equals(style)) {
				font = font.deriveFont( Font.PLAIN );
			} else {
				throw new RuntimeException("unrecognized style \""+style+"\"");
			}
		} catch(MissingResourceException e) {}
		
		try {
			String size = bundle.getString(key+".size");
			if(size!=null && size.charAt(0)=='+' || size.charAt(0)=='-') {
				if(size.charAt(0)=='+')
					size = size.substring(1);
				float change = Float.parseFloat(size);
				font = font.deriveFont( font.getSize2D()+change );
			} else if(size!=null) {
				float newSize = Float.parseFloat(size);
				font = font.deriveFont( newSize );
			}
		} catch(MissingResourceException e) {}
		
		return font;
	}
	
	private static ResourceBundle getDefaultResourceBundle() {
		String osName = System.getProperty("os.name");
		StringBuffer sb = new StringBuffer();
		sb.append("QOptionPaneUI_");
		for(int a = 0; a<osName.length(); a++) {
			char c = osName.charAt(a);
			if(Character.isWhitespace(c)==false) {
				sb.append(c);
			}
		}
		
		//this guarantees the JarWriter will sniff this out and include all the appropriate files
		//TODO: revisit this
		"QOptionPaneUI_Windows7.properties".length();
		"QOptionPaneUI_WindowsXP.properties".length();
		"QOptionPaneUI_MacOSX.properties".length();
		
		try {
			return ResourceBundle.getBundle("com/bric/plaf/"+sb.toString());	
		} catch(Throwable t) {
			return ResourceBundle.getBundle("com/bric/plaf/QOptionPaneUI_Windows7");
		}
	}
	
	public BasicQOptionPaneUI() {
		this( getDefaultResourceBundle() );
	}
	
	public static final String KEY_UPPER_BODY_INSETS = "upperBody.insets";
	public static final String KEY_ICON_INSETS = "icon.insets";
	public static final String KEY_MAIN_MESSAGE_INSETS = "mainMessage.insets";
	public static final String KEY_SECONDARY_MESSAGE_INSETS = "secondaryMessage.insets";
	public static final String KEY_FOOTER_INSETS = "footer.insets";
	public static final String KEY_MAIN_MESSAGE_FONT = "mainMessage.font";
	public static final String KEY_SECONDARY_MESSAGE_FONT = "secondaryMessage.font";
	public static final String KEY_MAIN_MESSAGE_COLOR = "mainMessage.color";
	public static final String KEY_SECONDARY_MESSAGE_COLOR = "secondaryMessage.color";
	public static final String KEY_MESSAGE_WIDTH = "message.width";
	public static final String KEY_FOOTER_SEPARATOR_COLOR = "footer.separator.color";
	public static final String KEY_FOOTER_BACKGROUND = "footer.background";
	public static final String KEY_FOOTER_BUTTON_GAP = "footer.button.gap";
	public static final String KEY_FOOTER_UNSAFE_BUTTON_GAP = "footer.unsafeButton.gap";
	public static final String KEY_FOOTER_BUTTON_PADDING = "footer.button.internalPadding";
	public static final String KEY_FOOTER_LEFT_ALIGN_UNSAFE_BUTTONS = "footer.leftAlignUnsafeButtons";
	public static final String KEY_TEXT_EDITABLE = "text.selectable";
	public static final String KEY_UPPER_BODY_BACKGROUND = "upperBody.background";
	
	private static Map<String, Object> typeTable = new HashMap<String, Object>();
	{
		typeTable.put(KEY_UPPER_BODY_BACKGROUND, Color.class);
		typeTable.put(KEY_TEXT_EDITABLE, Boolean.class);
		typeTable.put(KEY_UPPER_BODY_INSETS, Insets.class);
		typeTable.put(KEY_ICON_INSETS, Insets.class);
		typeTable.put(KEY_MAIN_MESSAGE_INSETS, Insets.class);
		typeTable.put(KEY_SECONDARY_MESSAGE_INSETS, Insets.class);
		typeTable.put(KEY_FOOTER_INSETS, Insets.class);
		typeTable.put(KEY_MAIN_MESSAGE_FONT, Font.class);
		typeTable.put(KEY_SECONDARY_MESSAGE_FONT, Font.class);
		typeTable.put(KEY_MAIN_MESSAGE_COLOR, Color.class);
		typeTable.put(KEY_SECONDARY_MESSAGE_COLOR, Color.class);
		typeTable.put(KEY_MESSAGE_WIDTH, Integer.class);
		typeTable.put(KEY_FOOTER_BUTTON_GAP, Integer.class);
		typeTable.put(KEY_FOOTER_UNSAFE_BUTTON_GAP, Integer.class);
		typeTable.put(KEY_FOOTER_BUTTON_PADDING, Integer.class);
		typeTable.put(KEY_FOOTER_LEFT_ALIGN_UNSAFE_BUTTONS, Boolean.class);
		typeTable.put(KEY_FOOTER_SEPARATOR_COLOR, Color.class);
		typeTable.put(KEY_FOOTER_BACKGROUND, Color.class);
	}

	protected final ResourceBundle resourceBundle;
	
	public BasicQOptionPaneUI(ResourceBundle resourceBundle) {
		this.resourceBundle = resourceBundle;
	}
	
	public int getInt(QOptionPane pane,String key) {
		Number n = ((Number)pane.getClientProperty(key));
		if(n!=null) return n.intValue();
		return readInt(resourceBundle, key, 0);
	}
	
	public Insets getInsets(QOptionPane pane,String key) {
		Insets n = ((Insets)pane.getClientProperty(key));
		if(n!=null) return (Insets)n.clone();
		return readInsets(resourceBundle, key);
	}
	
	public boolean getBoolean(QOptionPane pane,String key) {
		Boolean b = ((Boolean)pane.getClientProperty(key));
		if(b!=null) return b.booleanValue();
		return readBoolean(resourceBundle, key, false);
	}
	
	public Font getFont(QOptionPane pane,String key) {
		Font f = ((Font)pane.getClientProperty(key));
		if(f!=null) return f;
		return readFont(resourceBundle, key, UIManager.getFont("TextArea.font"));
	}
	
	public Color getColor(QOptionPane pane,String key) {
		Color c = ((Color)pane.getClientProperty(key));
		if(c!=null) return c;
		return readColor(resourceBundle, key, null);
	}
	
	private void installBorder(JComponent comp,Insets insets,Color debugColor) {
		comp.setBorder(new DebugBorder(insets, debugColor));
	}
	
	static class DebugBorder implements Border {
		Insets insets;
		Color debugColor;
		
		DebugBorder(Insets i,Color debugColor) {
			insets = i;
			this.debugColor = debugColor;
		}

		@SuppressWarnings("unused")
		public void paintBorder(Component c, Graphics g0, int x, int y,
				int width, int height) {
			Graphics2D g = (Graphics2D)g0;
			Area area = new Area(new Rectangle(x, y, width, height));
			area.subtract(new Area(new Rectangle(
					x+insets.left, y+insets.top,
					width-insets.left-insets.right,
					height-insets.top-insets.bottom
			)));
			if(c.isOpaque()) {
				Color bkgnd = c.getBackground();
				g.setColor(bkgnd);
				g.fill(area);
			}
			if(false) {
				g.setColor(debugColor);
				g.fill(area);
			}
		}

		public Insets getBorderInsets(Component c) {
			return insets;
		}

		public boolean isBorderOpaque() {
			return false;
		}
	}
		
	private PropertyChangeListener customizationListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			Class<?> type = (Class<?>)typeTable.get(evt.getPropertyName());
			if(type==null) return;
			
			if(type.isInstance(evt.getNewValue())==false) {
				String desc = evt.getNewValue()==null ? "null" : evt.getNewValue().getClass().getName();
				throw new RuntimeException("the key \""+evt.getPropertyName()+"\" should map to a "+type.getName()+" (found "+desc+")");
			}
			QOptionPane pane = (QOptionPane)evt.getSource();
			installCustomizations( pane );
		}
	};

	@Override
	public void installUI(JComponent c) {
		super.installUI(c);

		QOptionPane optionPane = (QOptionPane)c;
		JLabel iconLabel = getIconLabel(optionPane);
		JTextArea mainText = getMainMessageTextArea(optionPane);
		JTextArea secondaryText = getSecondaryMessageTextArea(optionPane);
		JPanel dialogFooterContainer = getFooterContainer(optionPane);
		JPanel upperBody = this.getUpperBody(optionPane);
		
		Color upperBackground = getColor(optionPane, KEY_UPPER_BODY_BACKGROUND);
		if(upperBackground!=null) {
			upperBody.setOpaque(true);
			upperBody.setBackground(upperBackground);
		}
		optionPane.setOpaque(true);
		iconLabel.setOpaque(false);
		mainText.setOpaque(false);
		secondaryText.setOpaque(false);
		dialogFooterContainer.setOpaque(false);
		
		optionPane.addPropertyChangeListener(customizationListener);
		
		installCustomizations(optionPane);
	}
	
	@Override
	protected void installComponents(QOptionPane pane) {
		pane.removeAll();
		pane.setLayout(new GridBagLayout());
		
		JPanel upperBody = getUpperBody(pane);
		upperBody.setLayout(new GridBagLayout());
		upperBody.setOpaque(false);
		
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0; gbc.gridy = 0;
		gbc.weighty = 1; gbc.weightx = 0;
		gbc.anchor = GridBagConstraints.NORTHEAST;
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridheight = 3;
		upperBody.add( getIconLabel(pane), gbc );

		gbc.gridx = 1; gbc.gridy = 0;
		gbc.weightx = 1;
		gbc.fill = GridBagConstraints.BOTH;
		gbc.anchor = GridBagConstraints.CENTER;
		gbc.gridheight = 1; gbc.weighty = 0;
		upperBody.add( getMainMessageTextArea(pane), gbc );
		gbc.gridy++;
		upperBody.add( getSecondaryMessageTextArea(pane), gbc );
		gbc.gridy++; gbc.weighty = 1; 
		upperBody.add( getCustomComponentContainer(pane), gbc );
		
		JSeparator separator = getFooterSeparator(pane);
		JPanel footerContainer = getFooterContainer(pane);
		
		gbc = new GridBagConstraints();
		gbc.gridx = 0; gbc.gridy = 0;
		gbc.weightx = 1; gbc.weighty = 1;
		gbc.fill = GridBagConstraints.BOTH;
		pane.add(upperBody, gbc);
		gbc.weighty = 0; gbc.gridy++;
		pane.add(separator, gbc);
		gbc.gridy++;
		pane.add(footerContainer, gbc);
	}
	
	private void installCustomizations(QOptionPane optionPane) {
		JLabel iconLabel = getIconLabel(optionPane);
		JTextArea mainText = getMainMessageTextArea(optionPane);
		JTextArea secondaryText = getSecondaryMessageTextArea(optionPane);
		JPanel footerContainer = getFooterContainer(optionPane);
		JSeparator footerSeparator = getFooterSeparator(optionPane);
		JPanel upperBody = getUpperBody(optionPane);
		
		installBorder(footerContainer, 
				getInsets(optionPane, KEY_FOOTER_INSETS), 
				new Color(0xffDDDDDD));
		installBorder(upperBody, 
				getInsets(optionPane, KEY_UPPER_BODY_INSETS), 
				new Color(0xDDffDD));
		installBorder(iconLabel, 
				getInsets(optionPane, KEY_ICON_INSETS), 
				new Color(0xffDDff));
		installBorder(mainText, 
				getInsets(optionPane, KEY_MAIN_MESSAGE_INSETS), 
				new Color(0xDDDDff));
		installBorder(secondaryText, 
				getInsets(optionPane, KEY_SECONDARY_MESSAGE_INSETS), 
				new Color(0xDDffff));
		
		Color separatorColor = getColor(optionPane, KEY_FOOTER_SEPARATOR_COLOR);
		footerSeparator.setVisible(separatorColor!=null);
		footerSeparator.setUI(new LineSeparatorUI(separatorColor));
		
		mainText.setFont( getFont(optionPane, KEY_MAIN_MESSAGE_FONT) );
		secondaryText.setFont( getFont(optionPane, KEY_SECONDARY_MESSAGE_FONT) );
		mainText.setForeground( getColor(optionPane, KEY_MAIN_MESSAGE_COLOR) );
		secondaryText.setForeground( getColor(optionPane, KEY_SECONDARY_MESSAGE_COLOR) );
		mainText.setDisabledTextColor( getColor(optionPane, KEY_MAIN_MESSAGE_COLOR) );
		secondaryText.setDisabledTextColor( getColor(optionPane, KEY_SECONDARY_MESSAGE_COLOR) );
		
		updateMainMessage(optionPane);
		updateSecondaryMessage(optionPane);
		updateFooter(optionPane);
	}

	@Override
	protected void updateMainMessage(QOptionPane pane) {
		super.updateMainMessage(pane);
		JTextArea mainText = getMainMessageTextArea(pane);
		Dimension mainTextSize = TextSize.getPreferredSize(mainText, 
				getInt(pane, KEY_MESSAGE_WIDTH));
		Insets insets = getInsets(pane, KEY_MAIN_MESSAGE_INSETS);
		mainTextSize.width += insets.left+insets.right;
		mainTextSize.height += insets.top+insets.bottom;
		mainText.setPreferredSize(mainTextSize);
		mainText.setMinimumSize(mainTextSize);
		mainText.setEnabled( getBoolean(pane, KEY_TEXT_EDITABLE) );
		mainText.setOpaque(false);
	}
	
	@Override
	protected void updateSecondaryMessage(QOptionPane pane) {
		super.updateSecondaryMessage(pane);
		JTextArea secondaryText = getSecondaryMessageTextArea(pane);
		Dimension secondaryTextSize = TextSize.getPreferredSize(secondaryText, 
				getInt(pane, KEY_MESSAGE_WIDTH));
		Insets insets = getInsets(pane, KEY_SECONDARY_MESSAGE_INSETS);
		secondaryTextSize.width += insets.left+insets.right;
		secondaryTextSize.height += insets.top+insets.bottom;
		secondaryText.setPreferredSize(secondaryTextSize);
		secondaryText.setMinimumSize(secondaryTextSize);
		secondaryText.setEnabled( getBoolean(pane, KEY_TEXT_EDITABLE) );
		secondaryText.setOpaque(false);
	}
	
	@Override
	protected void updateFooter(QOptionPane pane) {
		super.updateFooter(pane);
		
		JPanel container = super.getFooterContainer(pane);
		Color background = getColor(pane, KEY_FOOTER_BACKGROUND);
		if(background!=null) {
			container.setOpaque(true);
			container.setBackground(background);
		} else {
			container.setOpaque(false);
		}

		DialogFooter footer = pane.getDialogFooter();
		if(footer!=null) {
			int gap = getInt(pane, KEY_FOOTER_BUTTON_GAP);
			int unsafeGap = getInt(pane, KEY_FOOTER_UNSAFE_BUTTON_GAP);
			int padding = getInt(pane, KEY_FOOTER_BUTTON_PADDING);
			
			if(gap!=0)
				footer.setButtonGap(gap);
			if(unsafeGap!=0)
				footer.setUnsafeButtonGap(unsafeGap);
			if(padding!=0)
				footer.setInternalButtonPadding(padding, 0);
			
			boolean leftAlign = getBoolean(pane, KEY_FOOTER_LEFT_ALIGN_UNSAFE_BUTTONS);
			
			footer.setFillWidth( leftAlign );
			
			container.removeAll();
			container.setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0; gbc.gridy = 0;
			gbc.weightx = 0; gbc.weighty = 1;
			if(leftAlign) {
				int iconWidth = 0;
				JLabel iconLabel = getIconLabel(pane);
				if(iconLabel.isVisible()) {
					iconWidth = iconLabel.getPreferredSize().width;
					Insets i = getInsets(pane, KEY_ICON_INSETS);
					iconWidth += Math.max(i.left + i.right - 5, 0);
					JPanel fluff = new JPanel();
					fluff.setOpaque(false);
					fluff.setPreferredSize(new Dimension(iconWidth, 1));
					container.add(fluff, gbc);
				}
			}
			gbc.gridx++; gbc.weightx = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			container.add(footer, gbc);
			
			container.setVisible(true);
		} else {
			container.setVisible(false);
		}
	}
}