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
 * There are lots of guidelines about help. For example <a
 * href="http://msdn.microsoft.com/en-us/library/aa511268.aspx#help" >Microsoft
 * writes</a> <blockquote> Don't use general or vague Help topic links or
 * generic Help buttons. Users often ignore generic Help. ... Design your UI so
 * that users don't need Help ... Understand that you don't have to provide help
 * for every feature in the UI. </blockquote>
 *
 */
public class HelpButton {

	/**
	 * This returns a <code>JButton</code> that can be used to trigger help.
	 * <p>
	 * Currently this is a circular button with the text "?".
	 * 
	 * @param actionListener
	 *            the listener that is triggered when you either click the help
	 *            JButton or JLink.
	 * @param tooltipText
	 *            the optional tooltip text for this component.
	 * @return the help button
	 */
	public static JButton create(ActionListener actionListener,
			String tooltipText) {
		JButton helpButton = new JButton();
		QButtonUI ui = new RoundRectButtonUI();
		helpButton
				.putClientProperty(QButtonUI.PROPERTY_IS_CIRCLE, Boolean.TRUE);
		Font font = helpButton.getFont().deriveFont(Font.BOLD);
		Icon glyphIcon = new GlyphIcon(font, '?', 14, Color.black);
		helpButton.setIcon(glyphIcon);
		helpButton.setUI(ui);
		helpButton.addActionListener(actionListener);
		helpButton.setToolTipText(tooltipText);

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