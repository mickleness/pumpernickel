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

import java.awt.Dialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import com.pump.plaf.FocusArrowListener;
import com.pump.swing.DialogFooter.EscapeKeyBehavior;
import com.pump.util.JVM;
import com.pump.util.Storage;
import com.pump.window.WindowDragger;

/** This is an alternative to <code>JOptionPane</code> dialogs.
 * <P>The main distinctions are:
 * <ul><LI> Use of the {@link com.bric.swing.FixedWidthTextArea}, so you don't have to worry about line breaks.</li>
 * <LI> Use of the {@link com.bric.swing.DialogFooter}, to give more flexible options in the dialog footer (particularly for a help button).</li>
 * <LI> Several OS-specific enhancements to blend in with individual platforms.</LI></ul>
 * <P>This class tries to observe conventions from Microsoft and Apple when possible,
 * but it does not claim to follow strict adherence to their guidelines.
 * <H3>Core Parameters</H3>
 * <P>There are several different method options/parameters you can use to set up a QDialog.  Here
 * is a thorough discussion of what the main parameters mean, and how to use each:
 * <ul><LI> <b><code>frame</code></b> the optional component to host this dialog.
 * <P>On Mac in Java 1.6+, if this is non-null then the dialog will be presented
 * as a sheet to this frame, unless <code>setDocumentModal(false)</code> has been called.</li>
 * <LI> <b><code>icon</code></b> the optional icon/image to present on the left side of this dialog.
 * <P>Microsoft <A HREF="http://msdn.microsoft.com/en-us/library/aa511268.aspx">cautions</A>:
 * "Don't use large graphics that serve no purpose beyond filling space with eye candy." 
 * <A HREF="http://msdn.microsoft.com/en-us/library/aa511277.aspx">Also</A>: "Not every message needs
 * an icon. Icons are not a way to decorate messages."
 * <P>At the same time, Apple 
 * <A HREF="http://developer.apple.com/documentation/UserExperience/Conceptual/AppleHIGuidelines/XHIGWindows/chapter_18_section_7.html#//apple_ref/doc/uid/20000961-BACFBACB">points out</A>: 
 * "Because of the Mac OS X window layering model, an icon is necessary to make it clear to the user which application is displaying the alert."
 * <P>This contradiction is a little tricky, but it's not as bad as it sounds.  Microsoft's
 * comments were especially geared toward the abuse of warning and error icons.  In fact
 * both Microsoft and Apple agree strongly on this point:
 * <BR>&nbsp; &nbsp; &nbsp; Microsoft says: "We overwarn in Windows programs ... [this] makes using a program
 * feel like a hazardous activity, and it detracts from truly significant issues."
 * <BR>&nbsp; &nbsp; &nbsp; Apple says: "Don't use a caution icon for tasks whose only purpose is to overwrite or remove data,
 * such as Save or Empty Trash; too-frequent use of the caution icon dilutes its significance."
 * <P>Both companies stress that if you over-use warning (and error) icons, they lose their
 * significance to users: so try to keep the usage of those icons to a minimum.
 * <P>But what to do about icons in general?  Microsoft insists they are optional.  Apple says
 * they are not.  Actually Apple has already provided a partial solution:
 * Macs don't actually have a QUESTION or INFORMATION icons like other L&amp;F's do.  For those
 * messages all you see is the applications icon (that is, the icon has nothing to
 * do with a "question message", or an "information message").  So they already brand most of your
 * dialogs with your application for you.
 * <P>So QDialog takes this a step further to provide a cross-platform solution: when
 * you request a <code>PLAIN_MESSAGE</code> dialog containing only text, on Macs you'll
 * still receive the application icon, but on other platforms there will be no icon.
 * If you don't like this behavior: you can find a <code>QDialog.showDialog()</code>
 * method that lets you <i>explicitly</i> define the icon as <code>null</code>.</li>
 * <LI> <b><code>dialogTitle</code></b> the title of this dialog.  Although technically optional, this is recommended by both Vista and Apple:
 * <P>Apple <A HREF="http://developer.apple.com/documentation/UserExperience/Conceptual/AppleHIGuidelines/XHIGWindows/chapter_18_section_7.html#//apple_ref/doc/uid/20000961-BACFBACB">advises</A>
 * using "a title that matches the command" that invoked this dialog.
 * <BR>Vista <A HREF="http://msdn.microsoft.com/en-us/library/aa511268.aspx">says</a>:
 * "Use the title to identify the command, feature, or program where a dialog box came from."
 * <P>Note on Mac in Java 1.6+, dialogs can be presented as sheet if the <code>frame</code> argument
 * is non-null.  This makes the title irrelevant, so while it is advised to include a title:
 * don't assume it will always be visible in all platforms.
 * <P>Meanwhile it's not hard to find examples on either platform that don't follow
 * this advice.  (For example: I just emptied the trash on my Mac: no dialog title there.)</li>
 * <LI> <b><code>content</code></b> the main message/controls you want to present to the user.
 * <P>This can be any component, but some method signatures of <code>showDialog()</code> let
 * you specify the following 3 elements and composes the <code>content</code> based on
 * those.
 * <P>&nbsp; &nbsp; &nbsp; <b><code>boldMessage</code></b> this can be used to help create the <code>content</code>
 * argument if that parameter is not explicitly provided.  Apple calls this the "message text",
 * and defines it as "provid[ing] a short, simple summary of the error or condition that
 * summoned the alert. This should be a complete sentence; often it is presented as a question."
 * <P>Vista also uses a combination of bold/plain text in several dialogs, but I don't think
 * they have a universally succinct rule explaining it.
 * <P>&nbsp; &nbsp; &nbsp; <b><code>plainMessage</code></b> this follows the <code>boldMessage</code> and is also
 * part of the <Code>content</code> component.  Apple calls this the "informative text",
 * and defines it as "provid[ing] a fuller description of the situation, its consequences,
 * and ways to get out of it. For example, a warning that an action cannot be undone is an
 * appropriate use of informative text."
 * <P>&nbsp; &nbsp; &nbsp; <b><code>innerComponent</code></b> this follows the <code>plainMessage</code> and is
 * the last element in the <code>content</code> panel.  This is an arbitrary component.  It was
 * originally intended to be a <Code>JTextField</code>, but it could be command links or
 * anything your heart desires.</li>
 * <LI> <b><code>footer</code></b> the controls at the bottom of this dialog.  See {@link com.bric.swing.DialogFooter}
 * for more information.  Several method signatures don't require a <code>DialogFooter</code> directly,
 * but create one for you based on other arguments.</li>
 * <LI> <b><code>options</code></b> this is used to indicate what buttons the user can
 * select to dismiss this dialog (for example DialogFooter.OK_CANCEL_OPTION or DialogFooter.YES_NO_OPTION).
 * See {@link com.bric.swing.DialogFooter} for more information.</li>
 * <LI> <b><code>closeable</code></b> whether the user should be able to close this dialog without
 * using a commit button.  If this is <code>true</code>, then the close
 * decoration can dismiss a dialog.  (In this case the <code>showDialog()</code> methods will either return
 * CANCEL_OPTION or -1.)
 * <P>If this is <code>false</code>, then (if the title bar is visible) the close decoration will
 * remain visible but it will not function.  (This is because, to my knowledge, the close decoration
 * cannot be suppressed in Java.)  This means the dialog must be dismissed via one of the action buttons.
 * <P>If this value is not specified, then it is treated as <code>true</code> if one
 * of the options available in the DialogFooter.CANCEL_OPTION.  Otherwise this is treated as <code>false</code>.
 * For example, a DialogFooter.YES_NO_OPTION dialog will not be closeable, but a DialogFooter.YES_NO_CANCEL_OPTION dialog will be.</li>
 * <LI> <b><code>dontShowKey</code></b> if non-null, then a do-not-show-again checkbox will be provided.  This string
 * serves as the unique key to the preferences that stores whether the user has already
 * selected this checkbox.  So this does not need to be human-readable, but it needs to be unique for
 * every dialog that offers this feature.
 * <P>See the field <code>prefStorage</code> for more information about how this value is stored.
 * <P>If the user has ever selected this checkbox, then this method returns -1 or DialogFooter.CANCEL_OPTION, as if
 * the user closed this dialog box using the close decoration.  It does not make sense for this
 * checkbox to appear when there is no option to 'cancel' a dialog, or when a dialog is not closeable.
 * <P>Note this feature should be used reluctantly.  The Vista interface guidelines state:
 * <BR>"If you think your dialog box needs a <i>Don't show this item again</i> option, that is a
 * clear sign that it is annoying and potentially unnecessary."
 * <P>(The article continues to discuss/suggest alternatives.)
 * <BR>Or as Werner Randelshofer advised, "Get rid of this feature. Dialogs which are candidates
 * for such a checkbox, shouldn't be in the UI in the first place."</li>
 * <LI> <b><code>alwaysApplyKey</code></b> if non-null, then a always-apply-this-decision checkbox will be provided.  This string
 * serves as the unique key to the preferences that stores whether the user has already
 * selected that checkbox.  So this does not need to be human-readable, but it needs to be unique for
 * every dialog that offers this feature.
 * <P>See the field <code>prefStorage</code> for more information about how this value is stored.
 * <P>If the user has ever selected this checkbox, then this method returns the index of the last
 * commit button the user selected.  It does not make sense for this checkbox to appear when
 * this dialog concerns possible data-loss, because the user could permanently choose to
 * lose data.
 * <P>Like the "dont show again" checkbox: this should be used very reluctantly.  If you think you need
 * either of these checkboxes: your entire dialog/workflow might benefit from redesign.</LI></ul>
 * <H3>Platform Differences</H3>
 * Because I was mostly studying Vista and Apple guidelines, the main GUI differences
 * are designed mostly for these two environments.  There are several OS-specific adjustments
 * that take place in this class:
 * <ul><LI> Insets are different on different platforms.</li>
 * <LI> On Vista the help link is <i>removed from the footer</i> and placed just
 * below the <code>content</code> area.  This is because in big bold letters the
 * Vista guidelines <A HREF="http://msdn.microsoft.com/en-us/library/aa511268.aspx#help">say</A>:
 * "Locate Help links at the bottom of the content area of the dialog box."</li>
 * <LI> Also on Vista a separator is added between the content area and the footer.</li>
 * <LI> Macs use sheets in Java 1.6+ if the <code>frame</code> argument is
 * non-null.</LI></ul>
 * 
 */
public class QDialog extends JDialog 
{
	private static final long serialVersionUID = 1L;

	/** Shows either a Mac-style or a Vista-style save-changes dialog.
	 * 
	 * @param frame the parent frame
	 * @param documentName the optional document name
	 * @param saveTriggersDialog if this is <code>true</code>, then the "Save"
	 * button will have "..." appended to the end to indicate a dialog will
	 * be necessary.
	 * @return one of the option constants: DialogFooter.CANCEL_OPTION, DialogFooter.SAVE_OPTION, DialogFooter.DONT_SAVE_OPTION
	 */
	public static int showSaveChangesDialog(Frame frame,String documentName,boolean saveTriggersDialog) {
		JComponent content = null;
		String dialogTitle = strings.getString("dialogSaveTitle");
		if(JVM.isMac) {
			if(documentName==null) {
				content = QDialog.createContentPanel(strings.getString("dialogMacSavePromptUntitled"), 
						strings.getString("dialogMacSaveInfo"), 
						null, //innerComponent
						true); //selectable
			} else {
				content = QDialog.createContentPanel(replace(strings.getString("dialogMacSavePromptTitled"),"^0",documentName), 
						strings.getString("dialogMacSaveInfo"), 
						null, //innerComponent
						true); //selectable
			}
		} else {
			if(documentName==null) {
				content = QDialog.createContentPanel(null,
						strings.getString("dialogVistaSavePromptUntitled"), 
						null, //innerComponent
						true); //selectable
			} else {
				content = QDialog.createContentPanel(null,
						replace(strings.getString("dialogVistaSavePromptTitled"),"^0",documentName),
						null, //innerComponent
						true); //selectable
			}
		}
		
		DialogFooter footer = DialogFooter.createDialogFooter(new JComponent[] {},
				DialogFooter.SAVE_DONT_SAVE_CANCEL_OPTION, EscapeKeyBehavior.TRIGGERS_CANCEL);
		JComponent[] dismissControls = footer.getDismissControls();
		if(saveTriggersDialog) {
			JButton saveButton = (JButton)dismissControls[0];
			saveButton.setText(saveButton.getText()+"...");
		}
		
		int i = QDialog.showDialog(frame, 
				dialogTitle, 
				getIcon(PLAIN_MESSAGE), 
				content, 
				footer, 
				true, 
				null, //dontShowKey
				null); //alwaysApplyKey
		if(i==-1) return DialogFooter.CANCEL_OPTION;
		Integer k = (Integer)dismissControls[i].getClientProperty(DialogFooter.PROPERTY_OPTION);
		return k.intValue();
	}
	
	/** Replaces text.
	 * <P>Yes this is currently in the String class, but that method
	 * did not exist in Java 1.4, so this method is provided for backwards compatibility.
	 */
	private static String replace(String text,String searchFor,String replaceWith) {
		int i;
		while( (i = text.indexOf(searchFor))!=-1) {
			text = text.substring(0,i)+replaceWith+text.substring(i+searchFor.length());
		}
		return text;
	}
	
    
    /** If this is <code>true</code>, then the mnemonics on
     * buttons should be displayed when this class creates
     * a dialog.
     * <P>By default this is <code>false</code>.
     * <P>Note this operates by "hijacking" the UIManager
     * property "Button.showMnemonics" just before/after
     * this dialog is displayed.  It is possible this
     * could have adverse effects, but so far no side-effects
     * have been observed.
     * 
     */
    public static boolean showMnemonics = false;
	
	/** This is used to store information about when the user
	 * selects a "Don't Show This Again" or a
	 * "Always Apply This Decision" checkbox.
	 * <P>The default implementation defers to Java's standard
	 * <code>Preferences</code> class, but it's made public so
	 * you can override it.  There should probably be some
	 * way in your program for the user to reset these preferences.
	 * <BR>(The default implementation stores keys in:
	 * <BR><code>Preferences.userNodeForPackage(QDialog.class)</code>.)
	 * 
	 */
	public static Storage prefStorage = new Storage() {

		public String get(Object key)
		{
			return Preferences.userNodeForPackage(QDialog.class).get(key.toString(), null);
		}

		public boolean put(Object key, String value)
		{
			String lastValue = Preferences.userNodeForPackage(QDialog.class).get(key.toString(), null);
			Preferences.userNodeForPackage(QDialog.class).put(key.toString(), value);
			if(value==null && lastValue==null)
				return false;
			if(value==null || lastValue==null)
				return true;
			return value.equals(lastValue);
		}
		
	};
	
	private static int uniqueCtr = 0;
	
	/** Used for plain messages that have no standard icon.
	 * <P>See the javadocs for this class regarding important notes about
	 * how QDialog can interpret this constant.
	 * <P>Note the usage is similar to JOptionPane's, but the numerical value is
	 * different, so you cannot substitute JOptionPane.PLAIN_MESSAGE for QDialog.PLAIN_MESSAGE.
	 */
	public static final int PLAIN_MESSAGE = uniqueCtr++;
	
	/** Used for information messages.  This is used to indicate a specific icon.
	 * <P>Note the usage is similar to JOptionPane's, but the numerical value is
	 * different, so you cannot substitute JOptionPane.INFORMATION_MESSAGE for QDialog.INFORMATION_MESSAGE.
	 * <P>(On Macs this icon is the application's icon.)
	 */
	public static final int INFORMATION_MESSAGE = uniqueCtr++;

	/** Used for question messages.  This is used to indicate a specific icon.
	 * <P>Note the usage is similar to JOptionPane's, but the numerical value is
	 * different, so you cannot substitute JOptionPane.QUESTION_MESSAGE for QDialog.QUESTION_MESSAGE.
	 * <P>(On Macs this icon is the application's icon.).
	 */
	public static final int QUESTION_MESSAGE = uniqueCtr++;

	/** Used for warning messages.  This is used to indicate a specific icon.
	 * <P>Note the usage is similar to JOptionPane's, but the numerical value is
	 * different, so you cannot substitute JOptionPane.WARNING_MESSAGE for QDialog.WARNING_MESSAGE.
	 */
	public static final int WARNING_MESSAGE = uniqueCtr++;

	/** Used for error messages.  This is used to indicate a specific icon.
	 * <P>Note the usage is similar to JOptionPane's, but the numerical value is
	 * different, so you cannot substitute JOptionPane.ERROR_MESSAGE for QDialog.ERROR_MESSAGE.
	 */
	public static final int ERROR_MESSAGE = uniqueCtr++;
    
    /** Creates the typical panel that contains the heart of the message/information
     * presented to the user.
     * <P>If this method creates a text area, it will use
     * <code>UIManager.getFont("OptionPane.messageFont")</code> as its font.
     * @param boldText the topmost text to appear.
     * <P>This is formatted in bold, in a <code>FixedWidthTextArea</code>.
     * This may be null.
     * @param plainText this appears below <code>boldText</code>, in a <code>FixedWidthTextArea</code>.
     * On Macs, if <code>boldText</code> is non-null, the
     * <code>plainText</code> is displayed at a slightly smaller font size
     * (using <code>JComponent.sizeVariant</code> of <code>small</code>.)
     * @param innerComponent this optional component will appear below both
     * text areas.  This will be asked to fill the width
     * of this panel.
     * This was originally added as a convenient way to add a <code>JTextField</code>
     * to a dialog, but any custom component could go here.
     * @param selectable controls whether the text should be selectable.
     * <P>The Apple Interface Guidelines state:
     * <BR>"When it provides an obvious user benefit, static text in a dialog
	 * should be selectable. For example, a user should be able to copy an error
	 * message, a serial number, or IP address to paste elsewhere."
     * @return the component to display in the center of a dialog.
     */
    public static JComponent createContentPanel(String boldText,String plainText,JComponent innerComponent,boolean selectable) {
   		if(plainText==null && boldText==null)
   			throw new IllegalArgumentException();
   		
   		//these defaults implemented for Nimbus's sake,
   		//per Nathan's suggestion/request:
   		UIDefaults textAreaDefaults = new UIDefaults();
   		textAreaDefaults.put("TextArea[Disabled+NotInScrollPane].textForeground", SystemColor.textText);
   		textAreaDefaults.put("TextArea[Disabled+NotInScrollPane].backgroundPainter", null);
   		textAreaDefaults.put("TextArea[Enabled+NotInScrollPane].backgroundPainter", null);
    	
    	FixedWidthTextArea boldTextArea = null;
    	if(boldText!=null) {
	    	boldTextArea = new FixedWidthTextArea(boldText,332);
	    	boldTextArea.setLineWrap(true);
	    	boldTextArea.setFocusable(false);
	    	boldTextArea.setWrapStyleWord(true);
	    	boldTextArea.setEditable(false);
	    	boldTextArea.setForeground(SystemColor.textText);
	    	Font font = getMessageFont();
	    	boldTextArea.setFont(font.deriveFont(Font.BOLD));
	    	boldTextArea.setOpaque(false);

	    	boldTextArea.setBorder(null);
	    	boldTextArea.putClientProperty("Nimbus.Overrides.InheritDefaults", "false");
	    	boldTextArea.putClientProperty("Nimbus.Overrides", textAreaDefaults);
	   		
	        if(selectable==false) {
	        	boldTextArea.setEnabled(false);
	        	boldTextArea.setDisabledTextColor(boldTextArea.getForeground());
	        }
    	}

   		FixedWidthTextArea plainTextArea = null;
   		if(plainText!=null) {
	   		plainTextArea = new FixedWidthTextArea(plainText,332);
	   		plainTextArea.setFocusable(false);
	    	plainTextArea.setLineWrap(true);
	    	plainTextArea.setWrapStyleWord(true);
	    	plainTextArea.setEditable(false);
	    	plainTextArea.setForeground(SystemColor.textText);
	    	Font font = getMessageFont();
	    	plainTextArea.setFont(font);
	    	plainTextArea.setOpaque(false);

	    	plainTextArea.setBorder(null);
	    	plainTextArea.putClientProperty("Nimbus.Overrides.InheritDefaults", "false");
	    	plainTextArea.putClientProperty("Nimbus.Overrides", textAreaDefaults);
	    	
	    	if(selectable==false) {
	    		plainTextArea.setEnabled(false);
	    		plainTextArea.setDisabledTextColor(plainTextArea.getForeground());
	        }
	    	
   			if(boldTextArea!=null)
   	        	plainTextArea.putClientProperty("JComponent.sizeVariant", "small");
   		}
   		
   		
   		if(boldTextArea!=null && plainTextArea==null && innerComponent==null) {
   			return boldTextArea;
   		} else if(boldTextArea==null && plainTextArea!=null && innerComponent==null) {
   			return plainTextArea;
   		} else if(boldTextArea==null && plainTextArea==null && innerComponent!=null) {
   			return innerComponent;
   		}
	    JPanel container = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.gridy = 0;
        c.weightx = 1; c.weighty = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(0,0,5,0);
        if(boldTextArea!=null) {
        	if(plainTextArea==null && innerComponent==null)
        		c.insets = new Insets(0,0,0,0);
        	container.add(boldTextArea,c);
    	    c.gridy++;
        }
        if(plainTextArea!=null) {
        	if(innerComponent==null)
        		c.insets = new Insets(0,0,0,0);
        	container.add(plainTextArea,c);
        	c.gridy++;
        }
        if(innerComponent!=null) {
        	container.add(innerComponent,c);
        }
	    return container;
    }

    private static Font getMessageFont()
	{
    	Font font = UIManager.getFont("OptionPane.messageFont");
    	if(font==null)
    		font = UIManager.getFont("OptionPane.font");
    	if(font==null)
    		font = new Font("Default", 0, 13);
    	return font;
	}

	/** This presents a modal dialog to the user.
     * <P>These arguments are discussed in more detail in the "Core Parameters"
     * section of QDialog's javadocs.
     * @param frame the optional frame this dialog belongs to.
     * @param dialogTitle the optional dialog title.
     * @param type the dialog type; this is used to determine the icon (such as INFORMATION_MESSAGE).
     * @param boldMessage the optional message to appear in bold
     * @param plainMessage the optional message to appear in a plain font
     * @param innerComponent the optional component to appear below
     * <code>boldMessage</code> and <code>plainMessage</code>.
     * @param lowerLeftComponent an optional component to put in the lower-left of the dialog footer.
     * This is generally intended to be a help button.  See <code>HelpComponent</code>.
	 * @param options one of DialogFooter's OPTIONS constants (such as DialogFooter.YES_NO_CANCEL_OPTION)
	 * indicating what buttons are present in the <code>DialogFooter</code>.
	 * @param defaultOption the button that is the default option for this dialog (such as DialogFooter.YES_OPTION),
	 * or -1 if there is no default option.
     * @param dontShowKey if non-null, a do-not-show-again checkbox will appear.
     * @param alwaysApplyKey if non-null, a always-apply-this-decision checkbox will appear.
     * @param escapeKeyBehavior a DialogFooter constant for the escape key behavior.
	 * @return the option the user selected (such as DialogFooter.YES_OPTION).  If the dialog is
	 * dismissed the returned value is DialogFooter.CANCEL_OPTION.
     */
    public static int showDialog(Frame frame,String dialogTitle,int type,String boldMessage,String plainMessage,JComponent innerComponent,JComponent lowerLeftComponent,int options,int defaultOption,String dontShowKey,String alwaysApplyKey,EscapeKeyBehavior escapeKeyBehavior) {
    	JComponent[] leftControls;
    	if(lowerLeftComponent==null) {
    		leftControls = new JButton[] {};
    	} else {
    		leftControls = new JComponent[] { lowerLeftComponent };
    	}
    	JComponent content = createContentPanel(boldMessage,plainMessage,innerComponent,true);
    	
    	return showDialog(frame, dialogTitle, type, content, leftControls, options, defaultOption, dontShowKey, alwaysApplyKey, escapeKeyBehavior );
    }



    /** This presents a modal dialog to the user.
     * <P>These arguments are discussed in more detail in the "Core Parameters"
     * section of QDialog's javadocs.
     * @param frame the optional frame this dialog belongs to.
     * @param dialogTitle the optional dialog title.
     * @param type the dialog type; this is used to determine the icon (such as INFORMATION_MESSAGE).
     * @param content the component to display in the middle of this dialog.
	 * @param leftControls controls such as "Help" or "Reset" that appear on
	 * the left side of the <code>DialogFooter</code>.
	 * @param options one of the DialogFooter's OPTIONS constants (such as DialogFooter.YES_NO_CANCEL_OPTION)
	 * indicating what buttons are present in the <code>DialogFooter</code>.
	 * @param defaultOption the button that is the default option for this dialog (such as DialogFooter.YES_OPTION),
	 * or -1 if there is no default option.
     * @param dontShowKey if non-null, a do-not-show-again checkbox will appear.
     * @param alwaysApplyKey if non-null, a always-apply-this-decision checkbox will appear.
     * @param escapeKeyBehavior one of the <code>DialogFooter.ESCAPE_KEY</code> constants.
	 * @return the option the user selected (such as DialogFooter.YES_OPTION).  If the dialog is
	 * dismissed the returned value is DialogFooter.CANCEL_OPTION.
     */
    public static int showDialog(Frame frame,String dialogTitle,int type,JComponent content,JComponent[] leftControls,int options,int defaultOption,String dontShowKey,String alwaysApplyKey,EscapeKeyBehavior escapeKeyBehavior) {
    	return showDialog_(frame, dialogTitle, type, content, leftControls, options, defaultOption, dontShowKey, alwaysApplyKey, escapeKeyBehavior);
    }


    /** This presents a modal dialog to the user.
     * <P>These arguments are discussed in more detail in the "Core Parameters"
     * section of QDialog's javadocs.
     * @param dialog the optional dialog this dialog belongs to.
     * @param dialogTitle the optional dialog title.
     * @param type the dialog type; this is used to determine the icon (such as INFORMATION_MESSAGE).
     * @param content the component to display in the middle of this dialog.
	 * @param leftControls controls such as "Help" or "Reset" that appear on
	 * the left side of the <code>DialogFooter</code>.
	 * @param options one of the DialogFooter's OPTIONS constants (such as DialogFooter.YES_NO_CANCEL_OPTION)
	 * indicating what buttons are present in the <code>DialogFooter</code>.
	 * @param defaultOption the button that is the default option for this dialog (such as DialogFooter.YES_OPTION),
	 * or -1 if there is no default option.
     * @param dontShowKey if non-null, a do-not-show-again checkbox will appear.
     * @param alwaysApplyKey if non-null, a always-apply-this-decision checkbox will appear.
     * @param escapeKeyBehavior one of the <code>DialogFooter.ESCAPE_KEY</code> constants.
	 * @return the option the user selected (such as DialogFooter.YES_OPTION).  If the dialog is
	 * dismissed the returned value is DialogFooter.CANCEL_OPTION.
     */
    public static int showDialog(Dialog dialog,String dialogTitle,int type,JComponent content,JComponent[] leftControls,int options,int defaultOption,String dontShowKey,String alwaysApplyKey,EscapeKeyBehavior escapeKeyBehavior) {
    	return showDialog_(dialog, dialogTitle, type, content, leftControls, options, defaultOption, dontShowKey, alwaysApplyKey, escapeKeyBehavior);
    }
    
    private static int showDialog_(Window window,String dialogTitle,int type,JComponent content,JComponent[] leftControls,int options,int defaultOption,String dontShowKey,String alwaysApplyKey,EscapeKeyBehavior escapeKeyBehavior) {
        DialogFooter footer = DialogFooter.createDialogFooter(leftControls,options,defaultOption,escapeKeyBehavior);
    	boolean canCancel = options==DialogFooter.CANCEL_OPTION ||
	    	options==DialogFooter.OK_CANCEL_OPTION ||
	    	options==DialogFooter.SAVE_DONT_SAVE_CANCEL_OPTION ||
	    	options==DialogFooter.YES_NO_CANCEL_OPTION;
    	
    	showDialog_(window, dialogTitle, getIcon(type), content, footer, canCancel, dontShowKey, alwaysApplyKey);
    	
    	JButton selectedButton = (JButton)footer.getLastSelectedComponent();
    	if(selectedButton==null) return DialogFooter.CANCEL_OPTION;
    	
    	return ((Integer)selectedButton.getClientProperty(DialogFooter.PROPERTY_OPTION)).intValue();
    }


    /** This presents a modal dialog to the user.
     * <P>These arguments are discussed in more detail in the "Core Parameters"
     * section of QDialog's javadocs.
     * @param frame the optional frame this dialog belongs to.
     * @param dialogTitle the optional dialog title.
     * @param type the dialog type; this is used to determine the icon (such as INFORMATION_MESSAGE).
     * @param content the component to display in the middle of this dialog.
	 * @param leftControls controls such as "Help" or "Reset" that appear on
	 * the left side of the <code>DialogFooter</code>.
	 * @param buttonNames the names of buttons to use on the right side of
	 * the <code>DialogFooter</code>.  These should be listed in
	 * order of priority (for example, if "Save" is the most important
	 * and "Cancel" is the least, they should be listed: "Save", "Cancel".)
	 * @param defaultButtonIndex the index of the button name that is
	 * the default, or -1 if there is no default button.
	 * @param closeable whether the close decoration can dismiss
     * this dialog.
     * @param dontShowKey if non-null, a do-not-show-again checkbox will appear.
     * @param alwaysApplyKey if non-null, a always-apply-this-decision checkbox will appear.
     * @param escapeKeyBehavior see the <code>DialogFooter.ESCAPE_KEY</code> constants.
	 * @return the index of the button in the footer's dismissable buttons that was pressed, or -1
     * if this dialog was closed without pressing a commit button (via the close decoration)
     */
    public static int showDialog(Frame frame,String dialogTitle,int type,JComponent content,JComponent[] leftControls,String[] buttonNames,int defaultButtonIndex,boolean closeable,String dontShowKey,String alwaysApplyKey,EscapeKeyBehavior escapeKeyBehavior) {
    	if(escapeKeyBehavior==EscapeKeyBehavior.TRIGGERS_CANCEL) 
    		throw new IllegalArgumentException("TRIGGERS_CANCEL cannot be used with this static method: no cancel button is provided.");
    		
    	JButton[] rightButtons = new JButton[buttonNames.length];
    	for(int a = 0; a<rightButtons.length; a++) {
    		rightButtons[a] = new JButton(buttonNames[a]);
    	}
    	JButton defaultButton = null;
    	if(defaultButtonIndex>=0 && defaultButtonIndex<buttonNames.length) {
    		defaultButton = rightButtons[defaultButtonIndex];
    		if(escapeKeyBehavior==EscapeKeyBehavior.TRIGGERS_DEFAULT) {
    			DialogFooter.makeEscapeKeyActivate(defaultButton);
    		}
    	} else {
    		if(escapeKeyBehavior==EscapeKeyBehavior.TRIGGERS_NONDEFAULT) {
    			if(rightButtons.length==1) {
        			DialogFooter.makeEscapeKeyActivate(rightButtons[0]);
    			} else {
        			throw new IllegalArgumentException("request for escape key to map to "+rightButtons.length+" buttons.");
    			}
    		}
    		
    	}
    	DialogFooter footer = new DialogFooter(leftControls,rightButtons,true,defaultButton);
    	return QDialog.showDialog(frame, dialogTitle, getIcon(type), content, footer, closeable, dontShowKey, alwaysApplyKey);
    }
    
    /** This returns the standard icon for a specific dialog type.
     * <P>A <code>PLAIN_MESSAGE</code> dialog has a null icon, except
     * on Mac (see the javadocs for this class for a discussion of how
     * icons are used).
     * <BR>All other dialog types consult the <code>UIManager</code> for
     * the appropriate icon:
     * "OptionPane.informationIcon", "OptionPane.warningIcon",
     * "OptionPane.questionIcon", or "OptionPane.errorIcon".
     * 
     * @param dialogType must be <code>PLAIN_MESSAGE</code>,
     * <code>ERROR_MESSAGE</code>, <code>WARNING_MESSAGE</code>,
     * <code>QUESTION_MESSAGE</code>, or <code>INFORMATION_MESSAGE</code>.
     * @return an <code>Icon</code> associated with a type of dialog.
     */
    public static Icon getIcon(int dialogType) {
		if(dialogType==PLAIN_MESSAGE) {
			if(JVM.isMac)
				return UIManager.getIcon("OptionPane.informationIcon");
			return null;
		} else if(dialogType==INFORMATION_MESSAGE) {
			return UIManager.getIcon("OptionPane.informationIcon");
		} else if(dialogType==ERROR_MESSAGE) {
			return UIManager.getIcon("OptionPane.errorIcon");
		} else if(dialogType==WARNING_MESSAGE) {
			return UIManager.getIcon("OptionPane.warningIcon");
		} else if(dialogType==QUESTION_MESSAGE) {
			return UIManager.getIcon("OptionPane.questionIcon");
		} else {
			throw new IllegalArgumentException("The type must be PLAIN_MESSAGE, INFORMATION_MESSAGE, QUESTION_MESSAGE, ERROR_MESSAGE or WARNING_MESSAGE.");
		}
    }
    

	/** The localized strings used in dialogs. */
	public static ResourceBundle strings = ResourceBundle.getBundle("com.pump.swing.QDialog");
	
    /** This presents a modal dialog to the user.
     * <P>This is the "real" method that actually creates and lays out the <code>JDialog</code>.
     * <P>These arguments are discussed in more detail in the "Core Parameters"
     * section of QDialog's javadocs.
     * @param frame the optional frame this dialog belongs to.
     * @param dialogTitle the optional dialog title.
     * @param icon the optional icon to display in this dialog.
     * @param content the component to display in the middle of this dialog.
     * @param footer the {@link com.bric.swing.DialogFooter} for this dialog.
     * @param closeable whether the close decoration should dismiss
     * this dialog.
     * @param dontShowKey if non-null, a do-not-show-again checkbox will appear.
     * @param alwaysApplyKey if non-null, a always-apply-this-decision checkbox will appear.
	 * @return the index of the button in the footer's dismissable buttons that was pressed, or -1
     * if this dialog was closed without pressing a commit button (via the close decoration)
     */
	public static int showDialog(Frame frame,String dialogTitle,Icon icon,JComponent content,DialogFooter footer,boolean closeable,String dontShowKey,String alwaysApplyKey) {
		return showDialog_(frame, dialogTitle, icon, content, footer, closeable, dontShowKey, alwaysApplyKey);
	}

    /** This presents a modal dialog to the user.
     * <P>This is the "real" method that actually creates and lays out the <code>JDialog</code>.
     * <P>These arguments are discussed in more detail in the "Core Parameters"
     * section of QDialog's javadocs.
     * @param dialog the optional dialog this dialog belongs to.
     * @param dialogTitle the optional dialog title.
     * @param icon the optional icon to display in this dialog.
     * @param content the component to display in the middle of this dialog.
     * @param footer the {@link com.bric.swing.DialogFooter} for this dialog.
     * @param closeable whether the close decoration should dismiss
     * this dialog.
     * @param dontShowKey if non-null, a do-not-show-again checkbox will appear.
     * @param alwaysApplyKey if non-null, a always-apply-this-decision checkbox will appear.
	 * @return the index of the button in the footer's dismissable buttons that was pressed, or -1
     * if this dialog was closed without pressing a commit button (via the close decoration)
     */
	public static int showDialog(Dialog dialog,String dialogTitle,Icon icon,JComponent content,DialogFooter footer,boolean closeable,String dontShowKey,String alwaysApplyKey) {
		return showDialog_(dialog, dialogTitle, icon, content, footer, closeable, dontShowKey, alwaysApplyKey);
	}
	
	private static int showDialog_(Window window,String dialogTitle,Icon icon,JComponent content,DialogFooter footer,boolean closeable,String dontShowKey,String alwaysApplyKey) {
		QDialog dialog;
		if(window instanceof Frame) {
			dialog = new QDialog( (Frame)window, dialogTitle, icon, content, footer, closeable);
		} else {
			dialog = new QDialog( (Dialog)window, dialogTitle, icon, content, footer, closeable);
		}
		if(dontShowKey!=null) {
			dialog.setDontShowKey(dontShowKey);
		}
		if(alwaysApplyKey!=null) {
			dialog.setAlwaysApplyKey(alwaysApplyKey);
		}
		if(dialog.hasPreconditionedResponse())
			return dialog.getPreconditionedResponse();
		
		dialog.pack();
		dialog.setLocationRelativeTo(window);
		
		dialog.setVisible(true);
		
		return dialog.getSelectedIndex();
	}
	
	/** Uses reflection to retrieve a static field from a class */
	private static Object get(String className,String fieldName) {
		try {
			Class<?> c = Class.forName(className);
			Field f = c.getField(fieldName);
			return f.get(null);
		} catch(Throwable t) {
			return null;
		}
	}
	
	/** This uses reflection to call a method that may not exist in the compiling JVM. */
	private static void invoke(Class<?> c,Object obj,String methodName,Object[] arguments) {
		try {
			Method[] methods = c.getMethods();
			for(int a = 0; a<methods.length; a++) {
				if(methods[a].getName().equals(methodName)) {
					try {
						methods[a].invoke(obj, arguments);
						return;
					} catch(Throwable t) {}
				}
			}
		} catch(Throwable t) {
			t.printStackTrace();
		}
	}
	
	private static ActionListener footerListener = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			JComponent src = (JComponent)e.getSource();
			JRootPane rootPane = SwingUtilities.getRootPane(src);
			rootPane.putClientProperty("QDialog.selectedButton",src);
		}
	};
	
	//end of the static stuff
	
	protected boolean closeable;
	protected boolean documentModal;
	protected DialogFooter footer;
	protected JCheckBox dontShowCheckbox = new JCheckBox();
	protected JCheckBox alwaysApplyCheckbox = new JCheckBox();
	protected Icon icon;
	JLabel iconLabel = new JLabel();
	String dontShowKey;
	String alwaysApplyKey;
	protected WindowDragger dragger;
	protected JComponent content;
	
	WindowListener windowListener = new WindowAdapter() {
		Object originalMnemonicValue = UIManager.getDefaults().get("Button.showMnemonics");

		@Override
		public void windowClosed(WindowEvent e) {
			UIManager.getDefaults().put("Button.showMnemonics", originalMnemonicValue);

			if(dontShowKey!=null && dontShowCheckbox.isSelected()) {
				prefStorage.put(dontShowKey, "true");
			}
			if(alwaysApplyKey!=null && alwaysApplyCheckbox.isSelected()) {
				int selectedButtonIndex = getSelectedIndex();
				prefStorage.put(alwaysApplyKey, Integer.toString(selectedButtonIndex));
			}
		}

		@Override
		public void windowOpened(WindowEvent e) {
			if(showMnemonics)
				UIManager.getDefaults().put("Button.showMnemonics", Boolean.TRUE);
			getRootPane().putClientProperty("QDialog.selectedButton", null);
		}
		
	};
	
	/** Creates a new modal QDialog.
	 * 
	 * @param frame the optional frame to bind this to.
	 * @param dialogTitle the optional title for this dialog.
	 */
	public QDialog(Frame frame, String dialogTitle) {
		super(frame,dialogTitle,true);
		setResizable(false);
		addWindowListener(windowListener);
		dragger = new WindowDragger(getContentPane());
		dontShowCheckbox.addKeyListener(new FocusArrowListener());
		alwaysApplyCheckbox.addKeyListener(new FocusArrowListener());
	}
	
	/** Creates a new modal QDialog.
	 * 
	 * @param frame the optional frame to bind this to.
	 * @param dialogTitle the optional title for this dialog.
	 * @param icon the optional icon to use.
	 * @param content the heart of this dialog.
	 * @param footer the footer.
	 * @param closeable whether the close decoration can dismiss this dialog.
	 */
	public QDialog(Frame frame, String dialogTitle, Icon icon,
			JComponent content, DialogFooter footer,boolean closeable) {
		this(frame, dialogTitle);
		setIcon(icon);
		setContent(content);
		setFooter(footer);
		setCloseable(closeable);
	}
	
	/** Creates a new modal QDialog.
	 * 
	 * @param dialog the optional dialog to bind this to.
	 * @param dialogTitle the optional title for this dialog.
	 */
	public QDialog(Dialog dialog, String dialogTitle) {
		super(dialog,dialogTitle,true);
		setResizable(false);
		addWindowListener(windowListener);
		dragger = new WindowDragger(getContentPane());
		dontShowCheckbox.addKeyListener(new FocusArrowListener());
		alwaysApplyCheckbox.addKeyListener(new FocusArrowListener());
	}
	
	/** Creates a new modal QDialog.
	 * 
	 * @param dialog the optional dialog to bind this to.
	 * @param dialogTitle the optional title for this dialog.
	 * @param icon the optional icon to use.
	 * @param content the heart of this dialog.
	 * @param footer the footer.
	 * @param closeable whether the close decoration can dismiss this dialog.
	 */
	public QDialog(Dialog dialog, String dialogTitle, Icon icon,
			JComponent content, DialogFooter footer,boolean closeable) {
		this(dialog, dialogTitle);
		setIcon(icon);
		setContent(content);
		setFooter(footer);
		setCloseable(closeable);
	}
	
	/** Returns whether this dialog is closeable.
	 */
	public boolean isCloseable() {
		return closeable;
	}
	
	/** Controls whether this dialog is closeable.
	 * 
	 * @param closeable whether the close decoration can dismiss this dialog.
	 */
	public void setCloseable(boolean closeable) {
		this.closeable = closeable;
		if(closeable) {
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		} else {
			//this dialog should not be closeable,
			//but in a Java dialog the user can (unfortunately)
			//always use the red close decoration.
			
			//not the best solution, but we just
			//ignore it when the user clicks the close decoration
			setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			
			//on Mac we *could* simply make the dialog undecorated, but
			//Werner convinced me this is too non-standard.
		}
	}
	
	@Override
	public void setModal(boolean b) {
		super.setModal(b);
	}
	
	/** In Java 1.6 this calls <code>setModalityType(DOCUMENT_MODAL)</code>.
	 * Also on Macs this will use sheets.
	 * 
	 * @param b whether this dialog is document modal.
	 */
	public void setDocumentModal(boolean b) {
		documentModal = b;
		
		//do NOT combine a WindowDragger with a modal sheet.
		dragger.setActive(!b);

		try {
			if(b) {
				if(JVM.isMac && JVM.getMajorJavaVersion()>=1.6) {
					//note there is extra code below to handle sheets well.
					
					invoke(Dialog.class, QDialog.this, "setModalityType", new Object[] {
						get("java.awt.Dialog$ModalityType","DOCUMENT_MODAL")
					});
					
					getRootPane().putClientProperty("apple.awt.documentModalSheet",Boolean.TRUE);
				}
			} else {
				if(JVM.isMac && JVM.getMajorJavaVersion()>=1.6) {
					//note there is extra code below to handle sheets well.
					
					invoke(Dialog.class, QDialog.this, "setModalityType", new Object[] {
						get("java.awt.Dialog$ModalityType","APPLICATION_MODAL")
					});
					
					getRootPane().putClientProperty("apple.awt.documentModalSheet",Boolean.FALSE);
				}
			}
		} catch(SecurityException e) {
			System.err.println("this exception was ignored, but prevented this dialog from setting modality properties:");
			e.printStackTrace();
		}
	}
	
	/** Returns whether this dialog is document modal. 
	 * 
	 */
	public boolean isDocumentModal() {
		return documentModal;
	}
	
	protected void updateLayout() {
		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0; c.gridy = 0;
		if(icon!=null) {
			c.insets = new Insets(0,0,10,20);
			c.weightx = 0; c.weighty = 1;
			c.anchor = GridBagConstraints.NORTH;
			c.gridheight = 2;
			c.insets.top+=4;
			c.insets.bottom-=2;
			panel.add(iconLabel,c);
			c.gridx++;
			c.gridheight = 1;
		}
		c.insets = new Insets(0,0,10,0);
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1; c.weighty = 1;
		if(content!=null)
			panel.add(content,c);
		
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weighty = 0;
		if(dontShowKey!=null) {
			dontShowCheckbox.setText(strings.getString("dontShowAgainCheckbox"));
			c.gridy = 1;
			panel.add(dontShowCheckbox,c);
		} else if(alwaysApplyKey!=null) {
			alwaysApplyCheckbox.setText(strings.getString("alwaysApplyCheckbox"));
			c.gridy = 1;
			panel.add(alwaysApplyCheckbox,c);
		}
		
		if(JVM.isVistaOrWindows7 && footer!=null) {
			//move the help component above the footer
			JComponent[] leftControls = footer.getLeftControls();
			for(int a = 0; a<leftControls.length; a++) {
				Boolean b = (Boolean)leftControls[a].getClientProperty("help.link");
				if(b==null) b = Boolean.FALSE;
				if(b.equals(Boolean.TRUE)) {
					c.gridy++; c.fill = GridBagConstraints.NONE;
					c.anchor = GridBagConstraints.WEST;
					panel.add(leftControls[a],c);
				}
			}
		}

		Insets dialogInsets;
		if(JVM.isMac) {
			dialogInsets = new Insets(11,20,15,20);
		} else if(JVM.isVistaOrWindows7) {
			dialogInsets = new Insets(10,10,10,10);
		} else {
			dialogInsets = new Insets(11,11,11,11);
		}
		
		JPanel parent = new JPanel(new GridBagLayout());
		c.gridx = 0; c.gridy = 0; c.fill = GridBagConstraints.BOTH; 
		c.weightx = 1; c.weighty = 1;
		c.insets = (Insets)dialogInsets.clone();
		c.insets.bottom = 0;
		parent.add(panel,c);

		 c.weighty = 0;
		if(JVM.isVistaOrWindows7) {
			c.gridy = 1;
			c.gridx = 0; c.gridwidth = GridBagConstraints.REMAINDER;
			c.insets = new Insets(9,0,0,0);
			c.fill = GridBagConstraints.HORIZONTAL;
			parent.add(new JSeparator(),c);
		}

		c.gridy = 2;
		c.insets = new Insets(0,dialogInsets.left,dialogInsets.bottom,dialogInsets.right);
		if(footer!=null)
			parent.add(footer,c);

		getContentPane().removeAll();
		getContentPane().add(parent);
		getContentPane().invalidate();
	}
	
	/** Assigns the heart of this dialog.
	 * 
	 */
	public void setContent(JComponent content) {
		this.content = content;
		updateLayout();
	}
	
	/** Returns the heart of this dialog.
	 * 
	 */
	public JComponent getContent() {
		return content;
	}
	
	/** Assigns the footer of this dialog.
	 * 
	 */
	public void setFooter(DialogFooter f) {
		if(footer!=null) {
			footer.removeActionListener(footerListener);
		}
		footer = f;
		footer.addActionListener(footerListener);
		
		//the enforces mnemonics
		JComponent[] dismissControls = footer.getDismissControls();
		
		for(int a = 0; a<dismissControls.length; a++) {
			if(dismissControls[a] instanceof JButton) {
				int keyCode = ((JButton)dismissControls[a]).getMnemonic();
				if(keyCode!=0) {
					KeyStroke keyStroke = KeyStroke.getKeyStroke(keyCode, 0);
					getRootPane().getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put( keyStroke, keyStroke);
					AbstractAction action = new DialogFooter.ClickAction((JButton)dismissControls[a]) {
						private static final long serialVersionUID = 1L;

						@Override
						public void actionPerformed(ActionEvent e) {
							if(showMnemonics==false)
								return;
						}
					};
					getRootPane().getActionMap().put(keyStroke, action);	
				}
			}
		}
		updateLayout();
	}
	
	/** Returns the footer of this dialog.
	 */
	public DialogFooter getFooter() {
		return footer;
	}
	
	/** Assigns the optional icon for this dialog.
	 */
	public void setIcon(Icon i) {
		icon = i;
		iconLabel = new JLabel(icon);
		
		updateLayout();
	}
	
	/** Returns the icon for this dialog.
	 */
	public Icon getIcon() {
		return icon;
	}
	
	/** Assigns the optional "Don't Show This Again" key.
	 */
	public void setDontShowKey(String s) {
		dontShowKey = s;
		updateLayout();
	}

	/** Assigns the optional "Always Apply This Decision" key.
	 */
	public void setAlwaysApplyKey(String s) {
		alwaysApplyKey = s;
		updateLayout();
	}
	
	/** Returns the "Don't Show This Again" key. */
	public String getDontShowKey() {
		return dontShowKey;
	}

	/** Returns the "Always Apply This Decision" key. */
	public String getAlwaysApplyKey() {
		return alwaysApplyKey;
	}
	
	/** Returns the footer dismissal button that was last clicked after this dialog was made visible.
	 * This method may return null if no button has been clicked yet.
	 */
	public JButton getSelectedButton() {
		JButton dismissalButton = (JButton)getRootPane().getClientProperty("QDialog.selectedButton");
		return dismissalButton;
	}

	/** Returns the index of the button in the footer used to dismiss this dialog.
	 */
	public int getSelectedIndex() {
		JButton dismissalButton = getSelectedButton();
		
		//now because this dialog is modal we're going to block until the
		//dialog is hidden...

		if(dismissalButton==null) return -1;
		int selectedButtonIndex = -1;
		JComponent[] dismissalControls = footer.getDismissControls();
		for(int a = 0; a<dismissalControls.length; a++) {
			if(dismissalButton==dismissalControls[a])
				selectedButtonIndex = a;
		}
		
		if(selectedButtonIndex!=-1)
			return selectedButtonIndex;
		
		if(isVisible())
			throw new RuntimeException("This dialog hasn't been closed yet, so there is no selected button.");
		
		throw new RuntimeException("Unexpected condition: selected button not found.");
	}
	
	@Override
	public void pack() {
		if(JVM.isMac) {
			//for sheets on Mac, 2 packs is necessary.
			super.pack();
			super.pack();
		} else {
			super.pack();
		}
	}
	
	/** This consults the dontShowKey and alwaysApplyKey,
	 * and returns <code>true</code> if the user has previously
	 * selected either of those options.
	 */
	public boolean hasPreconditionedResponse() {
		if(dontShowKey!=null) {
			//check to see if the user has already indicated
			//they don't want to see this dialog:
			String bool = prefStorage.get(dontShowKey);
			if(bool==null) bool = "false";
			if(bool.equals("true")) return true;
		}
		if(alwaysApplyKey!=null) {
			//check to see if the user has already indicated
			//a preference here
			String number = prefStorage.get(alwaysApplyKey);
			if(number==null) number = "-1";
			try {
				int i = Integer.parseInt(number);
				if(i>=0) return true;
			} catch(NumberFormatException e) {}
		}
		return false;
	}
	
	/** If <code>hasPreconditionedResponse()</code> is true,
	 * this should be called instead of making the dialog visible.
	 * @return the index of the footer option the user wanted to
	 * automatically select.
	 */
	public int getPreconditionedResponse() {
		if(dontShowKey!=null) {
			//check to see if the user has already indicated
			//they don't want to see this dialog:
			String bool = prefStorage.get(dontShowKey);
			if(bool==null) bool = "false";
			if(bool.equals("true")) return -1;
		}
		if(alwaysApplyKey!=null) {
			//check to see if the user has already indicated
			//a preference here
			String number = prefStorage.get(alwaysApplyKey);
			if(number==null) number = "-1";
			try {
				int i = Integer.parseInt(number);
				if(i>=0) return i;
			} catch(NumberFormatException e) {
				//very odd.  But let's continue to make a dialog:
				prefStorage.put(alwaysApplyKey, "-1");
			}
		}
		throw new RuntimeException("there is no preconditioned response.  You should not call this method if hasPreconditionedResponse() returns false.");
	}
}