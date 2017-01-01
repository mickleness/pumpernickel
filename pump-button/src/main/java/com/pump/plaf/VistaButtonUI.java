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

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

/** A <code>ButtonUI</code> that resembles buttons in Vista.
 * <p><img src="https://raw.githubusercontent.com/mickleness/pumpernickel/master/pump-release/resources/filledbuttonui/VistaButtonUI.png" alt="VistaButtonUI Screenshot">
 * <P>As of this writing this UI does not perform animated
 * fades, but is otherwise a reasonable replica.
 * 
 * @see com.bric.plaf.FilledButtonUIDemo
 */
public class VistaButtonUI extends FilledButtonUI {

	private static final SimpleButtonFill VISTA_FILL = new SimpleButtonFill() {
		final float[] weights = new float[] {0, .35f, .49999f, .5f, 1};
		final Color[] normalColors = new Color[] {
				Color.white,
				Color.white,
				new Color(235, 235, 235),
				new Color(221, 221, 221),
				new Color(207, 207, 207)
		};
		final Color[] rolloverColors = new Color[] {
				new Color(234, 246, 253),
				new Color(234, 246, 253),
				new Color(217, 240, 252),
				new Color(190, 230, 252),
				new Color(167, 217, 245)
		};
		final Color[] pressedColors = new Color[] {
				new Color(229, 244, 252),
				new Color(211, 236, 249),
				new Color(196, 229, 246),
				new Color(152, 209, 239),
				new Color(104, 179, 219)
		};
		Color normalStrokeColor = new Color(112, 112, 112);
		Color focusedStrokeColor = new Color(60, 127, 177);
		Color pressedStrokeColor = new Color(44, 98, 139);

		@Override
		public Paint getDarkerFill(Rectangle fillRect) {
			return PlafPaintUtils.getVerticalGradient("vista.darker", 
					fillRect.height, 
					fillRect.y, 
					weights, pressedColors );
		}

		@Override
		public Paint getDarkestFill(Rectangle fillRect) {
			return null;
		}

		@Override
		public Paint getNormalFill(Rectangle fillRect) {
			return PlafPaintUtils.getVerticalGradient("vista.normal", 
					fillRect.height, 
					fillRect.y, 
					weights, normalColors );
		}

		@Override
		public Paint getRolloverFill(Rectangle fillRect) {
			return PlafPaintUtils.getVerticalGradient("vista.rollover", 
					fillRect.height, 
					fillRect.y, 
					weights, rolloverColors );
		}

		@Override
		public Paint getStroke(AbstractButton button, Rectangle fillRect) {
			if(fillRect==null) return normalStrokeColor;
			if(button.getModel().isPressed() || button.getModel().isSelected() ||
					isSpacebarPressed(button) )
				return pressedStrokeColor;
			
			if(button.hasFocus())
				return focusedStrokeColor;
			
			return normalStrokeColor;
		}
		
	};
	
	public static final ButtonShape VISTA_SHAPE = new ButtonShape(2,2);

	private static VistaButtonUI vistaButtonUI = new VistaButtonUI();

	/** Create a new instance of this ButtonUI for a component.
	 * <p>This method is required if you want to make this ButtonUI the default
	 * UI by invoking:
	 * <br><code>UIManager.getDefaults().put("ButtonUI", "com.pump.plaf.VistaButtonUI");</code>
	 */
    public static ComponentUI createUI(JComponent c) {
        return vistaButtonUI;
    }
	
	public VistaButtonUI() {
		super(VISTA_FILL, VISTA_SHAPE);
	}

	@Override
	public PaintFocus getFocusPainting(AbstractButton button) {
		return PaintFocus.NONE;
	}


	private final static Color focusedHighlight = new Color(66, 209, 245);
	private final static Color normalHighlight = new Color(255, 255, 255, 220);
	private final static Color darkHighlight = new Color(0, 0, 0, 60);
	
	@Override
	public void paintStroke(Graphics2D g, ButtonInfo info) {
		if(isStrokePainted(info.button)) {
			Graphics2D clippedG = (Graphics2D)g.create();
			clippedG.clip( info.fill );
			clippedG.setStroke( new BasicStroke(3) );
			if(info.button.getModel().isSelected() || info.button.getModel().isPressed() ||
					isSpacebarPressed(info.button)) {
				clippedG.setColor( darkHighlight );
			} else if(hasFocus(info.button) && info.button.isFocusPainted()) {
				clippedG.setColor( focusedHighlight );
			} else {
				clippedG.setColor( normalHighlight );
			}
			clippedG.draw( info.stroke );
			clippedG.dispose();
		}
		super.paintStroke(g, info);
		
	}

	@Override
	public boolean isFillOpaque() {
		return true;
	}
}