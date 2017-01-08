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
import java.awt.Paint;
import java.awt.Rectangle;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

/** This resembles the "gradient" button UI as seen in Mac OS 10.5.
 * <p><img src="https://raw.githubusercontent.com/mickleness/pumpernickel/master/pump-release/resources/filledbuttonui/GradientButtonUI.png" alt="GradientButtonUI Screenshot">
 * <P>It is not intended to be an exact replica, but it is very similar.
 * <P>According to <a href="http://nadeausoftware.com/node/87">this</a> article, the "gradient" look
 * is originally intended to:
 * <br>"select among options, such as different ways of viewing something".
 * 
 * @see com.pump.showcase.FilledButtonUIDemo
 */
public class GradientButtonUI extends FilledButtonUI {
	public static final ButtonShape GRADIENT_SHAPE = new ButtonShape(0,0);

	/** The <code>SimpleButtonFill</code> used to achieve the "gradient" look.
	 */
	public static final SimpleButtonFill GRADIENT_FILL = new SimpleButtonFill() {
		private float[] fillWeights = new float[] {0, .4999f, .5f, 1};
		private Color strokeColor = new Color(0xff979797);
		
		private Color[] darkerColors = new Color[] {
				new Color(0xffC8C8C8),
				new Color(0xffCFCFCF),
				new Color(0xffBABABA),
				new Color(0xffC7C7C7)
		};
		private Color[] normalColors = new Color[] {
				new Color(0xffFEFEFE),
				new Color(0xffFEFEFE),
				new Color(0xffEDEDED),
				new Color(0xffF7F7F7)
		};
		private Color[] darkestColors = new Color[] {
				new Color(0xffB2B2B2),
				new Color(0xffBCBCBC),
				new Color(0xffAFAFAF),
				new Color(0xffB9B9B9)
		};

		@Override
		public Paint getDarkerFill(Rectangle fillRect) {
			return PlafPaintUtils.getVerticalGradient("gradientUI.darker", 
					fillRect.height, fillRect.y,
					fillWeights, darkerColors);
		}

		@Override
		public Paint getDarkestFill(Rectangle fillRect) {
			return PlafPaintUtils.getVerticalGradient("gradientUI.darkest", 
					fillRect.height, fillRect.y,
					fillWeights, darkestColors);
		}

		@Override
		public Paint getNormalFill(Rectangle fillRect) {
			return PlafPaintUtils.getVerticalGradient("gradientUI.normal", 
					fillRect.height, fillRect.y,
					fillWeights, normalColors);
		}

		@Override
		public Paint getRolloverFill(Rectangle fillRect) {
			return null;
		}

		@Override
		public Paint getStroke(AbstractButton button, Rectangle fillRect) {
			return strokeColor;
		}
	};

	private static GradientButtonUI gradientButtonUI = new GradientButtonUI();

	/** Create a new instance of this ButtonUI for a component.
	 * <p>This method is required if you want to make this ButtonUI the default
	 * UI by invoking:
	 * <br><code>UIManager.getDefaults().put("ButtonUI", "com.pump.plaf.GradientButtonUI");</code>
	 */
    public static ComponentUI createUI(JComponent c) {
        return gradientButtonUI;
    }
	
	public GradientButtonUI() {
		super(GRADIENT_FILL, GRADIENT_SHAPE);
	}
	
	@Override
	protected int getPreferredHeight() {
		return 18;
	}
	
	@Override
	public boolean isFillOpaque() {
		return true;
	}
}