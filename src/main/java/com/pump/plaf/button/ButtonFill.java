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

/**
 * Information for rendering a button's background and border.
 */
public abstract class ButtonFill {

	/**
	 * If non-null, this is a 1-pixel highlight painted below this button. This
	 * may vary with the button's state, but if it is non-null in one state it
	 * should always be non-null.
	 * <P>
	 * If this is non-null, this may affect the height of the button. You can
	 * return a transparent color to "trick" the UI into giving an extra pixel
	 * here, if desired.
	 */
	public abstract Color getShadowHighlight(ButtonState.Float buttonState);

	/** Returns the current border for a button. */
	public abstract Paint getStroke(ButtonState.Float buttonState,
			Rectangle fillRect);

	/** Returns the current fill for a button. */
	public abstract Paint getFill(ButtonState.Float buttonState,
			Rectangle fillRect);
}