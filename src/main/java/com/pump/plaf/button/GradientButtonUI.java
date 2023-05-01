/**
 * This software is released as part of the Pumpernickel project.
 * 
 * All com.pump resources in the Pumpernickel project are distributed under the
 * MIT License:
 * https://github.com/mickleness/pumpernickel/raw/master/License.txt
 * 
 * More information about the Pumpernickel project is available here:
 * https://mickleness.github.io/pumpernickel/
 */
package com.pump.plaf.button;

import java.awt.Color;
import java.awt.Paint;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

import com.pump.plaf.PlafPaintUtils;

/**
 * This resembles the "gradient" button UI as seen in Mac OS 10.15.
 * <p>
 * This is one of the very few specialized button UI's that <a href=
 * "https://developer.apple.com/design/human-interface-guidelines/macos/buttons/gradient-buttons/"
 * >Apple's guidelines still encourage using</a>: <blockquote> A gradient button
 * initiates an immediate action related to a view, like adding or removing
 * table rows. Gradient buttons contain icons—not text—and can be configured to
 * behave as push buttons, toggles, or pop-up buttons. They usually reside in
 * close proximity to (either within or beneath) their associated view.
 * </blockquote>
 * <p>
 * <img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/filledbuttonui/GradientButtonUI.png"
 * alt="GradientButtonUI Screenshot">
 * <P>
 * It is not intended to be an exact replica, but it is very similar.
 */
public class GradientButtonUI extends QButtonUI {

	/**
	 * The <code>SimpleButtonFill</code> used to achieve the "gradient" look.
	 */
	public static final SimpleButtonFill GRADIENT_FILL = new SimpleButtonFill() {
		private float[] fillWeights = new float[] { 0, 1 };
		private Color strokeColor = new Color(0xff979797);

		private Color[] normalColors = new Color[] { new Color(0xffFAFAFA),
				new Color(0xffFEFEFE) };
		private Color[] darkestColors = new Color[] { new Color(0xffc1c1c1),
				new Color(0xffb5b5b5) };

		private VerticalGradientMaker darkestFill = new VerticalGradientMaker(fillWeights, darkestColors, "gradientUI.darker");
		private VerticalGradientMaker normalFill = new VerticalGradientMaker(fillWeights, normalColors, "gradientUI.normal");

		@Override
		public VerticalGradientMaker getDarkestFill(Rectangle fillRect) {
			return darkestFill;
		}

		@Override
		public VerticalGradientMaker getNormalFill(Rectangle fillRect) {
			return normalFill;
		}

		@Override
		public Paint getStroke(ButtonState.Float state, Rectangle fillRect) {
			return strokeColor;
		}
	};

	private static GradientButtonUI gradientButtonUI = new GradientButtonUI();

	/**
	 * Create a new instance of this ButtonUI for a component.
	 * <p>
	 * This method is required if you want to make this ButtonUI the default UI
	 * by invoking: <br>
	 * <code>UIManager.getDefaults().put("ButtonUI", "com.pump.plaf.GradientButtonUI");</code>
	 */
	public static ComponentUI createUI(JComponent c) {
		return gradientButtonUI;
	}

	public GradientButtonUI() {
		setCornerRadius(0);
		setButtonFill(GRADIENT_FILL);
	}
}