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
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;

import javax.swing.JComponent;

/** A <code>ThrobberUI</code> that shows 3 arcs detaching
 * from a circle and floating outward while fading away.
 * <p><table summary="Sample Animations of DetachingArcThrobberUI" cellpadding="10"><tr>
 * <td><img src="https://javagraphics.java.net/resources/throbber/DetachingArcThrobberUI.gif" alt="DetachingArcThrobberUI"></td>
 * <td><img src="https://javagraphics.java.net/resources/throbber/DetachingArcThrobberUIx2.gif" alt="DetachingArcThrobberUI, Magnified 2x"></td>
 * <td><img src="https://javagraphics.java.net/resources/throbber/DetachingArcThrobberUIx4.gif" alt="DetachingArcThrobberUI, Magnified 4x"></td>
 * </tr></table>
 * <p>On installation: the component's foreground is set to black,
 * but if that is changed then that color is used to render this animation.
 * <P>The default period for this animation is 2000, but you can modify
 * this with the period client properties {@link ThrobberUI#PERIOD_KEY} or
 * {@link ThrobberUI#PERIOD_MULTIPLIER_KEY}.
 *
 */
public class DetachingArcThrobberUI extends ThrobberUI {
	
	public static final int DEFAULT_PERIOD = 2000;

	public DetachingArcThrobberUI() {
		super(1000/100);
	}

	@Override
	protected void paintForeground(Graphics2D g, JComponent jc, Dimension size,Float fixedFraction) {
		Color color = jc==null ? getDefaultForeground() : jc.getForeground();
		g.setColor(color);
		float w = size.width;
		float h = size.height;
		Ellipse2D middle = new Ellipse2D.Float(w/2f-w/6f, h/2f-h/6f, w/3, h/3);
		g.fill(middle);

		float f;
		if(fixedFraction!=null) {
			f = fixedFraction.floatValue();
		} else {
			int p = getPeriod(jc, DEFAULT_PERIOD);
			float t = System.currentTimeMillis()%p;
			f = t / p;
		}
		
		//f = (float)(f+.05*Math.sin(2*Math.PI*f));

		int pulses = 2;
		int partitions = 3;
		for(int a = 0; a<pulses; a++) {
			float k = ((float)a)/((float)pulses);
			for(int b = 0; b<partitions; b++) {
				paintArc( g, jc, (f+k)%1, w, h, -1, 360*(f)+360*b/partitions, (float)(1+.5*Math.sin(4*Math.PI*f+2*b*Math.PI/partitions)) );
			}
		}

		middle = new Ellipse2D.Float(w/2f-w/10f, h/2f-h/10f, w/5, h/5);
		g.setColor(Color.white);
		g.fill(middle);
	}
	
	private void paintArc(Graphics2D g,JComponent jc,float f,float w,float h,float multiplier,float angleOffset,float a) {
		float z = f*f*f*f;
		float r = f * w / 2f;
		float arcX = w/2f - r;
		float arcY = h/2f - r;
		float arcW = 2*r;
		float arcH = 2*r;
		float arcStart = (float)( multiplier*Math.sqrt(f)*360 + angleOffset );
		float arcExtent = (1-f)*(1-f)*360/3;
		
		Arc2D arc = new Arc2D.Float( arcX, arcY, arcW, arcH, arcStart, arcExtent, Arc2D.OPEN);
		g.setStroke(new BasicStroke(4*(1-z)));
		
		Color c = jc==null ? getDefaultForeground() : jc.getForeground();
		int alpha = (int)( 255*(1-f)*a );
		g.setColor(new Color( c.getRed(), c.getGreen(), c.getBlue(), Math.min(alpha, 255) ));
		g.draw(arc);
	}

	@Override
	public Color getDefaultForeground() {
		return Color.black;
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(16, 16);
	}

}