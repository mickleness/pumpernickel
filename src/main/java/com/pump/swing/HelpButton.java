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

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.JButton;

import com.pump.icon.GlyphIcon;
import com.pump.plaf.button.QButtonUI;
import com.pump.plaf.button.RoundRectButtonUI;

/**
 * This contains static methods to help initialize a help button.
 * <P>
 * There are lots of guidelines about help. For example
 * <a href="http://msdn.microsoft.com/en-us/library/aa511268.aspx#help"
 * >Microsoft writes</a> <blockquote> Don't use general or vague Help topic
 * links or generic Help buttons. Users often ignore generic Help. ... Design
 * your UI so that users don't need Help ... Understand that you don't have to
 * provide help for every feature in the UI. </blockquote>
 *
 */
public class HelpButton {

	/**
	 * This returns a <code>JButton</code> that can be used to trigger help.
	 * <p>
	 * Currently this is a circular button with the text "?".
	 * 
	 * @param actionListener
	 *            the optional listener that is triggered when you click this
	 *            JButton.
	 * @param tooltipText
	 *            the optional tooltip text for this component.
	 * @return the help button
	 */
	public static JButton create(ActionListener actionListener,
			String tooltipText) {
		return create(actionListener, new RoundRectButtonUI(), 14, tooltipText);
	}

	/**
	 * This returns a <code>JButton</code> that can be used to trigger help.
	 * <p>
	 * Currently this is a circular button with the text "?".
	 * 
	 * @param actionListener
	 *            the optional listener that is triggered when you click this
	 *            JButton.
	 * @param buttonUI
	 *            the QButtonUI used to render the help button
	 * @param fontSize
	 *            the font size for the question mark icon in the help button.
	 * @param tooltipText
	 *            the optional tooltip text for this component.
	 * @return the help button
	 */
	public static JButton create(ActionListener actionListener,
			QButtonUI buttonUI, int fontSize, String tooltipText) {
		JButton helpButton = new JButton();
		helpButton.putClientProperty(QButtonUI.PROPERTY_IS_CIRCLE,
				Boolean.TRUE);
		Font font = helpButton.getFont().deriveFont(Font.BOLD);
		Icon glyphIcon = new GlyphIcon(font, '?', fontSize, Color.black);
		helpButton.setIcon(glyphIcon);
		helpButton.setUI(buttonUI);
		if (actionListener != null)
			helpButton.addActionListener(actionListener);
		helpButton.setToolTipText(tooltipText);

		// TODO: localize
		helpButton.getAccessibleContext().setAccessibleName("Help");

		return helpButton;
	}

	/**
	 * This returns a <code>JButton</code> that can be used to trigger help.
	 * <p>
	 * Currently this is a circular button with the text "?".
	 * 
	 * @param url
	 *            the URL the help button should open
	 * @return the help button
	 */
	public static JButton create(final URL url) {
		ActionListener actionListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().browse(url.toURI());
				} catch (IOException e1) {
					e1.printStackTrace();
				} catch (URISyntaxException e1) {
					e1.printStackTrace();
				}
			}

		};
		return create(actionListener, null);
	}

}