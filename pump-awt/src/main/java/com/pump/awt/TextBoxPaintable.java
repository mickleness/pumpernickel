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
package com.pump.awt;

import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.SystemColor;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pump.geom.ShapeBounds;

/** A Paintable that renders text as shapes.
 */
public class TextBoxPaintable implements Paintable {
	static FontRenderContext frc = new FontRenderContext(new AffineTransform(), true, true);
	
	Shape untransformedShape;
	Rectangle untransformedBounds;
	String text;
	String[] lines;
	Paint paint;
	float insets;
	
	/** Create a TextBoxPaintable using the system's default text color.
	 * @param string the initial string to render in this text.
	 * @param font the font for this text box
	 * @param maxWidth the maximum width to impose on this text box
	 * @param insets the new insets to use
	 */
	public TextBoxPaintable(String string,Font font,float maxWidth,float insets) {
		this(string, font, maxWidth, insets, null);
	}
	
	public TextBoxPaintable(String string,Font font,float maxWidth,float insets,Paint paint) {
		if(paint==null)
			paint = SystemColor.textText;
		this.insets = insets;
		this.text = string;
		this.paint = paint;
		
		List<String> lines = new ArrayList<String>();
		Map<TextAttribute, Object> attributes = new HashMap<TextAttribute, Object>();
		attributes.put( TextAttribute.FONT, font);
		AttributedString attrString = new AttributedString(string, attributes);
		LineBreakMeasurer lbm = new LineBreakMeasurer( attrString.getIterator(), frc);
		TextLayout tl = lbm.nextLayout(maxWidth);
		float dy = 0;
		GeneralPath path = new GeneralPath();
		int startIndex = 0;
		while(tl!=null) {
			path.append( tl.getOutline( AffineTransform.getTranslateInstance(0, dy) ), false );
			dy += tl.getAscent()+tl.getDescent()+tl.getLeading();
			int charCount = tl.getCharacterCount();
			lines.add( text.substring(startIndex, startIndex+charCount) );
			startIndex += charCount;
			tl = lbm.nextLayout(maxWidth);
		}
		this.lines = lines.toArray(new String[lines.size()]);
		untransformedShape = path;
		Rectangle2D b = ShapeBounds.getBounds(untransformedShape);
		b.setFrame(b.getX(), b.getY(), b.getWidth() + 2*insets, b.getHeight() + 2*insets);
		untransformedBounds = b.getBounds();
	}
	
	public int getLineCount() {
		return lines.length;
	}
	
	public String getLine(int a) {
		return lines[a];
	}
	
	public String getText() {
		return text;
	}

	public int getWidth() {
		return untransformedBounds.width;
	}

	public int getHeight() {
		return untransformedBounds.height;
	}

	public void paint(Graphics2D g) {
		g = (Graphics2D)g.create();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
		
		g.translate( -untransformedBounds.x, -untransformedBounds.y);
		g.translate( insets, insets);
		g.setPaint( paint );
		g.fill( untransformedShape );
		g.dispose();
	}
}