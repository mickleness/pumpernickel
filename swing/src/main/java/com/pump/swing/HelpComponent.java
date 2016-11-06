/*
 * @(#)HelpComponent.java
 *
 * $Date: 2014-03-13 05:40:29 -0400 (Thu, 13 Mar 2014) $
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

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JComponent;

import com.pump.util.JVM;

public class HelpComponent {

    /** This is a listener that is notified when a help component is
     * clicked.  When a help component is created, an Object is associated
     * with it known as "helpData".
     * <P>The default <code>helpListener</code> looks at the <code>helpData</code>
     * object and, if it a URL (or a String that can be converted to a URL), then
     * <code>BrowserLauncher.openURL()</code> is called.
     * <P>If you implement your own <code>helpListner</code>, it should
     * probably include:
     * <BR><code>JComponent src = (JComponent)e.getSource();</code>
     * <BR><code>Object helpData = src.getClientProperty("helpData");</code>
     * <P>(Do not assume the source is a button: on Windows the source
     * may be a <code>JLink</code>, which is an extension of a <code>JLabel</code>.)
	 */
    public static ActionListener helpListener = new ActionListener() {
    	public void actionPerformed(ActionEvent e) {
    		JComponent src = (JComponent)e.getSource();
    		Object helpData = src.getClientProperty("helpData");
    		URL url = null;
    		if(helpData instanceof String) {
    			try {
    				url = new URL( (String)helpData );
    			}  catch(Exception e2) {}
    		} else if(helpData instanceof URL) {
    			url = (URL)helpData;
    		}
    		if(url!=null) {
    			try {
					Desktop.getDesktop().browse(url.toURI());
				} catch (IOException e1) {
					throw new RuntimeException(e1);
				} catch (URISyntaxException e1) {
					throw new RuntimeException(e1);
				}
	    	} else {
	    		System.err.println(helpData);
	    		System.err.println("This help object is not supported.");
	    	}
    	}
    };
    

    /** This returns a <code>JComponent</code> that can be used to trigger
     * help.
     * <P>On Mac, this will return a <code>JButton</code> that has the
     * correct icon (see Apple Technical Note 2196).  This button
     * will have the tooltip <code>helpText</code>, if that string is
     * non-null.
     * <P>On other platforms, this will return a <code>JLink</code>
     * with the text <code>helpText</code>.  If <code>helpText</code>
     * is <code>null</code>, then the text "Learn More" is used.
     * This is based on Vista's <A HREF="http://msdn.microsoft.com/en-us/library/aa511268.aspx#help">advice</a>:
     * "Don't use general or vague Help topic links or generic Help buttons. Users often ignore generic Help."
     * The Vista guidelines go on to explicitly discourage using "Learn More" because it comes across
     * as too general/common, so it is encouraged that you provide an accurate string
     * to encourage your users to follow the link.
     * (Also Microsoft <A HREF="http://msdn.microsoft.com/en-us/library/aa511449.aspx">points out</A> you
     * should: "Design your UI so that users don't need Help," and
	 * "Understand that you don't have to provide help for every feature in the UI.")
     * 
     * @param helpData an object (preferably a URL) indicating what this button should do.
     * <P>The default <code>helpListener</code> expects this to be a URL or a String representing
     * a URL, but you can pass any object here and implement your own <code>helpListener</code> if
     * needed.
     * @param helpText text associated with this help.  On Mac this is the tooltip of the button,
     * and on other platforms this text is the link.
     * @return a <code>JComponent</code> used to retrieve help.
     */
    public static JComponent createHelpComponent(Object helpData,String helpText) {
    	if(helpListener==null) {
    		System.err.println("The field 'helpListener' is undefined.  This needs to be defined in order for the help button to work.");
    	}
    	
    	if(JVM.isMac) {
        	JButton helpButton;
    		helpButton = new JButton("");
        	helpButton.putClientProperty("JButton.buttonType", "help");
        	helpButton.putClientProperty("helpData", helpData);
        	helpButton.addActionListener(helpListener);
        	if(helpText!=null) {
        		helpButton.setToolTipText(helpText);
        	}
        	helpButton.putClientProperty("help.link", Boolean.TRUE);
        	return helpButton;
    	} else {
    		if(helpText==null)
    			helpText = DialogFooter.strings.getString("dialogHelpLearnMore");
    		JLink helpLink = new JLink(helpText);
    		helpLink.putClientProperty("helpData", helpData);
        	helpLink.addActionListener(helpListener);
        	helpLink.putClientProperty("help.link", Boolean.TRUE);
        	return helpLink;
    	}
    }
    
}
