/*
 * @(#)EmphasizedLabelUI.java
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
package com.pump.plaf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Window;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.plaf.basic.BasicLabelUI;

/** This is adapted from Ken Orr's EmphasizedLabelUI in the MacWidgets project.
 * (The original is licensed under GNU Lesser General Public License)
 * See MacWidgets for details: http://code.google.com/p/macwidgets/
 * 
 * This is based on the same code released in v0.9.5, except:
 * <ul><li>it doesn't reference WindowUtils.  (The static method {@code isParentWindowFocused} was
 * pasted into this class).
 * <li>The shadow color is optional</li></ul>
 * 
 * I migrated this into the bric.plaf directory so it won't conflict with any
 * possible future updates.
 */
public class EmphasizedLabelUI extends BasicLabelUI {

    private Color fShadowColor;
    private Color fFocusedTextColor;
    private Color fUnfocusedTextColor;

    public static final Color DEFAULT_EMPHASIS_COLOR = new Color(255, 255, 255, 110);
    public static final Color DEFAULT_FOCUSED_FONT_COLOR = new Color(0x000000);
    public static final Color DEFAULT_UNFOCUSED_FONT_COLOR = new Color(0x3f3f3f);
    public static final Color DEFAULT_DISABLED_FONT_COLOR = new Color(0x3f3f3f);

    /**
     * Creates an {@code EmphasizedLabelUI} using the default colors.
     */
    public EmphasizedLabelUI() {
        this(DEFAULT_FOCUSED_FONT_COLOR, DEFAULT_UNFOCUSED_FONT_COLOR,
                DEFAULT_EMPHASIS_COLOR);
    }

    /**
     * Creates an {@code EmphasizedLabelUI} using the given colors.
     *
     * @param focusedTextColor   the color to draw the text with when the parent
     *                           {@link java.awt.Window} has focus.
     * @param unfocusedTextColor the color to draw the text with when the parent
     *                           {@link java.awt.Window} does not have focus.
     * @param emphasisColor      the color to draw the emphasis text with.
     */
    public EmphasizedLabelUI(Color focusedTextColor, Color unfocusedTextColor, Color emphasisColor) {
        fFocusedTextColor = focusedTextColor;
        fUnfocusedTextColor = unfocusedTextColor;
        fShadowColor = emphasisColor;

    }

    @Override
	public void installUI(JComponent c) {
        super.installUI(c);
        c.setOpaque(false);
    }

    @Override
	public void uninstallUI(JComponent c) {
        super.uninstallUI(c);
    }
    
    /**
     * {@code true} if the given {@link Component}'s has a parent {@link Window} (i.e. it's not
     * null) and that {@link Window} is currently active (focused).
     *
     * @param component the {@code Component} to check the parent {@code Window}'s focus for.
     * @return {@code true} if the given {@code Component}'s parent {@code Window} is currently
     *         active.
     */
    public static boolean isParentWindowFocused(Component component) {
        Window window = SwingUtilities.getWindowAncestor(component);
        return window != null && window.isFocused();
    }

    @Override
	protected void paintEnabledText(JLabel label, Graphics g, String s,
                                    int textX, int textY) {
    	Graphics2D g2 = (Graphics2D)g.create();
    	g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    	if(fShadowColor!=null) {
	        g2.setColor(fShadowColor);
	        g2.setFont(label.getFont());
	        BasicGraphicsUtils.drawStringUnderlineCharAt(g2, s, -1, textX, textY + 1);
    	}
    	g2.setColor(isParentWindowFocused(label)
                ? fFocusedTextColor : fUnfocusedTextColor);
        BasicGraphicsUtils.drawStringUnderlineCharAt(g2, s, -1, textX, textY);
        g2.dispose();
    }

	@Override
	protected void paintDisabledText(JLabel label, Graphics g, String s, int textX, int textY) {
    	Graphics2D g2 = (Graphics2D)g.create();
    	g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		if(fShadowColor!=null) {
	        g2.setColor(fShadowColor);
	        g2.setFont(label.getFont());
	        BasicGraphicsUtils.drawStringUnderlineCharAt(g2, s, -1, textX, textY + 1);
		}
        g2.setColor(DEFAULT_DISABLED_FONT_COLOR);
        BasicGraphicsUtils.drawStringUnderlineCharAt(g2, s, -1, textX, textY);
        g2.dispose();
    }
}
