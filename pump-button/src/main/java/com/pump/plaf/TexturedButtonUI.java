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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

/** This resembles the "textured" button UI as seen in Mac OS 10.5.
 * <p><img src="https://raw.githubusercontent.com/mickleness/pumpernickel/master/pump-release/resources/filledbuttonui/TexturedButtonUI.png" alt="TexturedButtonUI Screenshot">
 * <p>It is not intended to be an exact replica, but it is very similar.
 * 
 * @see com.bric.plaf.FilledButtonUIDemo
 */
public class TexturedButtonUI extends FilledButtonUI {
	
	public static final ButtonShape TEXTURED_SHAPE = new ButtonShape(3,3);

	/** The <code>SimpleButtonFill</code> used to achieve the "Textured" look.
	 */
	public static final SimpleButtonFill TEXTURED_FILL = new SimpleButtonFill() {
		private float[] fillWeights = new float[] {0, 1};
		protected Color strokeColor = new Color(0xff585858);
		
		private Color[] normalColors = new Color[] {
				new Color(0xffFEFEFE),
				new Color(0xffA9A9A9)
		};
		
		private Color[] darkestColors = new Color[] {
				new Color(0xff3A3A3A),
				new Color(0xff494949)
		};
		
		private Color[] darkerColors = new Color[] {
				new Color(0xffAFAFAF),
				new Color(0xff787878)
		};

		@Override
		public Paint getDarkerFill(Rectangle fillRect) {
			return PlafPaintUtils.getVerticalGradient("textured.darker", 
					fillRect.height, fillRect.y,
					fillWeights, darkerColors);
		}

		@Override
		public Paint getDarkestFill(Rectangle fillRect) {
			return PlafPaintUtils.getVerticalGradient("textured.darkest", 
					fillRect.height, fillRect.y,
					fillWeights, darkestColors);
		}

		@Override
		public Paint getNormalFill(Rectangle fillRect) {
			return PlafPaintUtils.getVerticalGradient("textured.normal", 
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

	private static TexturedButtonUI texturedButtonUI = new TexturedButtonUI();

	/** Create a new instance of this ButtonUI for a component.
	 * <p>This method is required if you want to make this ButtonUI the default
	 * UI by invoking:
	 * <br><code>UIManager.getDefaults().put("ButtonUI", "com.pump.plaf.TexturedButtonUI");</code>
	 */
    public static ComponentUI createUI(JComponent c) {
        return texturedButtonUI;
    }
    
	public TexturedButtonUI() {
		super(TEXTURED_FILL, TEXTURED_SHAPE);
	}

	private static BasicStroke outline1 = new BasicStroke(9, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	private static BasicStroke outline2 = new BasicStroke(6, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	private static BasicStroke outline3 = new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
	@Override
	public void paintBackground(Graphics2D g,ButtonInfo info) {
		super.paintBackground(g, info);
		if(info.button.isContentAreaFilled() || info.button.isBorderPainted()) {
			ButtonModel model = info.button.getModel();
			if(model.isSelected() || model.isArmed() || isSpacebarPressed(info.button)) {
				g = (Graphics2D)g.create();
				
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
						RenderingHints.VALUE_ANTIALIAS_ON);
				g.clip( info.fill);
				g.setColor(new Color(0,0,0,15));
				g.setStroke(outline1);
				g.draw( info.fill );
				g.setStroke(outline2);
				g.draw( info.fill );
				g.setStroke(outline3);
				g.draw( info.fill );
			}
		}
	}
	
	@Override
	protected int getPreferredHeight() {
		return 19;
	}
	
	@Override
	public boolean isFillOpaque() {
		return true;
	}
};