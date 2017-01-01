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
import java.awt.geom.GeneralPath;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;

/** This resembles the "square" button UI as seen in Mac OS 10.5.
 * <p><img src="https://javagraphics.java.net/resources/filledbuttonui/SquareButtonUI.png" alt="SquareButtonUI Screenshot">
 * <P>It is not intended to be an exact replica, but it is very similar.
 * <P>According to <a href="http://nadeausoftware.com/node/87">this</a> article, the "square" look
 * is often used for:
 * <BR>"Buttons on a tool bar, or buttons smaller than a standard button".
 * 
 * @see com.bric.plaf.FilledButtonUIDemo
 */
public class SquareButtonUI extends FilledButtonUI {
	
	public static final ButtonShape SQUARE_SHAPE = new ButtonShape(0,0);

	/** The <code>SimpleButtonFill</code> used to achieve the "Square" look.
	 */
	public static final SimpleButtonFill SQUARE_FILL = new SimpleButtonFill() {
		private float[] fillWeights = new float[] {0, .25f, .9f, 1};
		protected Color strokeColor = new Color(0xff838383);
		
		private Color[] normalColors = new Color[] {
				new Color(0xFFF4F4F4),
				new Color(0xFFDDDDDD),
				new Color(0xFFF9F9F9),
				new Color(0xFFF9F9F9)
		};
		
		private Color[] darkestColors = new Color[] {
				new Color(0xFFB9B9B9),
				new Color(0xFF727272),
				new Color(0xFFCBCBCB),
				new Color(0xFFCBCBCB)
		};
		
		private Color[] darkerColors = new Color[] {
				new Color(0xFFBEBEBE),
				new Color(0xFF838383),
				new Color(0xFFDCDCDC),
				new Color(0xFFDCDCDC)
		};

		@Override
		public Paint getDarkerFill(Rectangle fillRect) {
			return PlafPaintUtils.getVerticalGradient("square.darker", 
					fillRect.height, fillRect.y,
					fillWeights, darkerColors);
		}

		@Override
		public Paint getDarkestFill(Rectangle fillRect) {
			return PlafPaintUtils.getVerticalGradient("square.darkest", 
					fillRect.height, fillRect.y,
					fillWeights, darkestColors);
		}

		@Override
		public Paint getNormalFill(Rectangle fillRect) {
			return PlafPaintUtils.getVerticalGradient("square.normal", 
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

	private static SquareButtonUI squareButtonUI = new SquareButtonUI();

	/** Create a new instance of this ButtonUI for a component.
	 * <p>This method is required if you want to make this ButtonUI the default
	 * UI by invoking:
	 * <br><code>UIManager.getDefaults().put("ButtonUI", "com.pump.plaf.SquareButtonUI");</code>
	 */
    public static ComponentUI createUI(JComponent c) {
        return squareButtonUI;
    }
    
	public SquareButtonUI() {
		super(SQUARE_FILL, SQUARE_SHAPE);
	}
	
	private static Color shadow = new Color(0,0,0,20);
	@Override
	public void paintBackground(Graphics2D g,ButtonInfo info) {
		super.paintBackground(g, info);
		if(info.button.getClientProperty(SHAPE)==null && info.button.isContentAreaFilled()) {
			g.setStroke(new BasicStroke(1));
			boolean showSeparators = isShowingSeparators(info.button);
			g.setColor(shadow);
			Rectangle rect = new Rectangle(info.fillBounds.x, info.fillBounds.y, 
					info.fillBounds.width, info.fillBounds.height);
			if(info.button.isBorderPainted()) {
				rect.x += 1; rect.y +=2;
				rect.width -= 2; rect.height -=2;
			}
			if(showSeparators) {
				g.draw(rect);
			} else {
				int verticalPosition = getVerticalPosition(info.button);
				if(verticalPosition==POS_ONLY) {
					int horizontalPosition = getHorizontalPosition(info.button);
					GeneralPath partialRect = new GeneralPath();
					if(horizontalPosition==POS_LEFT) {
						partialRect.moveTo(info.fillBounds.x + info.fillBounds.width, rect.y);
						partialRect.lineTo(rect.x, rect.y);
						partialRect.lineTo(rect.x, rect.y + rect.height);
						partialRect.lineTo(info.fillBounds.x + info.fillBounds.width, rect.y + rect.height);
					} else if(horizontalPosition==POS_MIDDLE) {
						partialRect.moveTo(info.fillBounds.x + info.fillBounds.width, rect.y);
						partialRect.lineTo(info.fillBounds.x, rect.y);
						partialRect.moveTo(info.fillBounds.x, rect.y + rect.height);
						partialRect.lineTo(info.fillBounds.x + info.fillBounds.width, rect.y + rect.height);
					} else if(horizontalPosition==POS_RIGHT) {
						partialRect.moveTo(info.fillBounds.x, rect.y + rect.height);
						partialRect.lineTo(rect.x + rect.width, rect.y + rect.height);
						partialRect.lineTo(rect.x + rect.width, rect.y);
						partialRect.lineTo(info.fillBounds.x, rect.y);
					}
					g.draw(partialRect);
				}
			}
		}
	}
	
	@Override
	public boolean isFillOpaque() {
		return true;
	}
};