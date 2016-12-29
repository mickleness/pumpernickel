/*
 * @(#)DecoratedPanelUI.java
 *
 * $Date: 2016-04-24 02:01:35 +0500 (Sun, 24 Apr 2016) $
 *
 * Copyright (c) 2014 by Jeremy Wood.
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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.plaf.PanelUI;

import com.pump.awt.GradientTexturePaint;
import com.pump.awt.GradientTexturePaint.Cycle;
import com.pump.awt.Scribbler;
import com.pump.geom.GeneralPathWriter;
import com.pump.image.BrushedMetalLook;
import com.pump.util.ObservableProperties;
import com.pump.util.ObservableProperties.Key;

/** This <code>PanelUI</code> offers several attributes to provide
 * more modern and sophisticated panels.
 * <p>The attributes are broken up into two groups:
 * <ul><li>Border Attributes: these are applied to the border, which can
 * be applied separately as a <code>javax.swing.Border</code>.</li>
 * <li>Panel Attributes: these are applied to the panel as a whole,
 * which means they affect the middle of the panel as well as the
 * border.</li></ul>
 * <p>The {@link Renderer} class manages painting all attributes. Both
 * the {@link Border} and the <code>DecoratedPanelUI</code> defer
 * to the same <code>Renderer</code> to paint everything. (But both entities
 * clip the Graphics2D destination strategically to only paint
 * the appropriate portions.)
 * <p>Here are examples of some of the harder-to-describe attributes. (These samples may not
 * show each feature in the most compelling/flattering presentation,
 * but they should help demonstrate what different attributes do.)
 * <p><table summary="Sample DecoratedPanel Configurations" cellpadding="20"><tr><td>
 * Glaze:
 * <p><img src="https://javagraphics.java.net/resources/decorated-panel/glaze-demo.gif" alt="Glaze Demo">
 * </td><td>
 * Bevel:
 * <p><img src="https://javagraphics.java.net/resources/decorated-panel/bevel-demo.gif" alt="Bevel Demo">
 * </td><td>
 * Corners:
 * <p><img src="https://javagraphics.java.net/resources/decorated-panel/corner-demo.gif" alt="Corner Demo">
 * </td></tr><tr><td>
 * Scribble:
 * <p><img src="https://javagraphics.java.net/resources/decorated-panel/scribble-demo.gif" alt="Scribble Demo">
 * </td><td>
 * Shadow:
 * <p><img src="https://javagraphics.java.net/resources/decorated-panel/shadow-demo.gif" alt="Shadow Demo">
 * </td></tr></table>
 * 
 * @see com.bric.plaf.DecoratedPanelUIDemo
 * @see <a href="http://javagraphics.blogspot.com/2014/03/panels-improved-panelui.html">Panels: an Improved UI</a>
 */
public class DecoratedPanelUI extends PanelUI {
	
	/** Create a simple <code>DecoratedPanelUI</code> that is tinted a little bit
	 * darker than normal panels with a bevel shadow oriented at the top.
	 * <p><img src="https://javagraphics.java.net/resources/decorated-panel/createDarkRecessedUI.png" alt="Dark Recessed Demo">
	 */
	public static DecoratedPanelUI createDarkRecessedUI() {
		DecoratedPanelUI ui = new DecoratedPanelUI();
		ui.getBorder().setBevelShadowAngle((float)(Math.PI/2));
		ui.getBorder().setBevelShadowMaxBlend(.08f);
		ui.getBorder().setBevelHighlightMaxBlend(.08f);
		ui.getBorder().setBevelShadowLayerCount(2);
		ui.getBorder().setBevelHighlightLayerCount(2);
		ui.getBorder().setCornerSize(4);
		ui.getBorder().setBorderPaint(new Color(0x33000000, true));
		ui.setColors( blendColors(UIManager.getColor("Panel.background"), Color.black, .05f));
		return ui;
	}

	/** Create a plain <code>DecoratedPanelUI</code> with a thin rounded border.
	 * <p><img src="https://javagraphics.java.net/resources/decorated-panel/createMinimalRoundedUI.png" alt="Minimal Rounded Demo">
	 */
	public static DecoratedPanelUI createMinimalRoundedUI() {
		DecoratedPanelUI ui = new DecoratedPanelUI();
		ui.getBorder().setBevelShadowLayerCount(0);
		ui.getBorder().setBevelHighlightLayerCount(0);
		ui.getBorder().setCornerSize(20);
		ui.getBorder().setCurvature(1);
		ui.getBorder().setBorderPaint(new Color(0x333333));
		ui.getBorder().setStrokeWidth(.5f);
		return ui;
	}

	/** Create a simple <code>DecoratedPanelUI</code> with a light shadow.
	 * <p><img src="https://javagraphics.java.net/resources/decorated-panel/createSubtleShadowUI.png" alt="Subtle Shadow Demo">
	 */
	public static DecoratedPanelUI createSubtleShadowUI() {
		DecoratedPanelUI ui = new DecoratedPanelUI();
		ui.getBorder().setBevelHighlightLayerCount(6);
		ui.getBorder().setBevelHighlightMaxBlend(0.08f);
		ui.getBorder().setBevelShadowLayerCount(1);
		ui.getBorder().setBevelShadowMaxBlend(0.04f);
		ui.getBorder().setBevelShadowAngle(1.4451327f);
		ui.getBorder().setBorderPaint(new Color(0x030303));
		ui.setBottomColor(new Color(238,238,238));
		ui.setTopColor(new Color(255,255,255));
		ui.getBorder().setCornerSize(0);
		ui.getBorder().setCurvature(0);
		ui.getBorder().setDropShadowAlpha(0.0085f);
		ui.getBorder().setDropShadowLayerCount(10);
		ui.getBorder().setStrokeWidth(.15f);
		return ui;
	}

	/** Create a <code>DecoratedPanelUI</code> with a scribble
	 * border (resembling torn paper) and a light shadow/border.
	 * <p><img src="https://javagraphics.java.net/resources/decorated-panel/createSubtleScribbleUI.png" alt="Subtle Scribble Demo">
	 */
	public static DecoratedPanelUI createSubtleScribbleUI() {
		DecoratedPanelUI ui = new DecoratedPanelUI();
		ui.getBorder().setBorderPaint(new Color(0x030303));
		ui.setColors(new Color(250,250,250));
		ui.getBorder().setCornerSize(14);
		ui.getBorder().setDropShadowAlpha(0.0075f);
		ui.getBorder().setDropShadowLayerCount(14);
		ui.getBorder().setScribbleSize(5);
		ui.getBorder().setStrokeWidth(.2f);
		return ui;
	}

	/** Create a <code>DecoratedPanelUI</code> with a large rounded border,
	 * strong bevels, and a large glaze.
	 * <p><img src="https://javagraphics.java.net/resources/decorated-panel/createPlasticUI.png" alt="Plastic Demo">
	 */
	public static DecoratedPanelUI createPlasticUI() {
		DecoratedPanelUI ui = new DecoratedPanelUI();
		ui.setGlazeLevel(1);
		ui.setGlazeOpacity(.5f);
		ui.setTopColor(new Color(0xEEEEEE));
		ui.setBottomColor(new Color(0xCCCCCC));
		ui.getBorder().setBevelHighlightLayerCount(6);
		ui.getBorder().setBevelShadowLayerCount(6);
		ui.getBorder().setBorderPaint(Color.black);
		ui.getBorder().setStrokeWidth(.5f);
		ui.getBorder().setCurvature(1);
		ui.getBorder().setCornerSize(20);
		ui.getBorder().setDropShadowLayerCount(0);
		return ui;
	}

	/** Create a <code>DecoratedPanelUI</code> that uses a special
	 * <code>Renderer</code> to simulate a brushed metal look with small
	 * but rounded corners.
	 * <p><img src="https://javagraphics.java.net/resources/decorated-panel/createBrushedMetalUI.png" alt="Brushed Metal Demo">
	 */
	public static DecoratedPanelUI createBrushedMetalUI() {
		Renderer brushedMetalRenderer = new Renderer() {
			BufferedImage bi = null;
			@Override
			protected void paintBody(Graphics2D g,Component c,Map<String, Object> attributes) {
				if(bi==null || bi.getWidth()!=c.getWidth() || bi.getHeight()!=c.getHeight()) {
					bi = BrushedMetalLook.paint(
							new Line2D.Float(0,c.getHeight()/2,c.getWidth(),c.getHeight()/2),
							c.getHeight(),
							new Rectangle(0,0,c.getWidth(),c.getHeight()),
							new Color(0xBBBBBB),
							false);
				}
				TexturePaint p = new TexturePaint(bi,
						new Rectangle(0,0,c.getWidth(),c.getHeight()) );
				g.setPaint(p);
				g.fill(body);
			}
		};
		
		DecoratedPanelUI ui = new DecoratedPanelUI(brushedMetalRenderer);
		ui.setGlazeOpacity(0);
		ui.getBorder().setBevelHighlightLayerCount(0);
		ui.getBorder().setBevelShadowLayerCount(0);
		ui.getBorder().setBorderPaint(Color.black);
		ui.getBorder().setStrokeWidth(0);
		ui.getBorder().setCurvature(1);
		ui.getBorder().setCornerSize(5);
		ui.getBorder().setDropShadowLayerCount(0);
		return ui;
	}
	
	/** This is used by both the {@link Border} and the {@link DecoratedPanelUI} to
	 * render all the attributes.
	 * <p>You can override some methods to provide unique looks. For example
	 * by override the {@link #paintBody(Graphics2D, Component, Map)} method
	 * you can fill the body with a texture paint to resemble brushed metal (which
	 * makes the attributes {@link DecoratedPanelUI#COLOR_TOP} and {@link DecoratedPanelUI#COLOR_BOTTOM}
	 * irrelevant). If customizing renderers via subclasses becomes a common pattern then
	 * this class should be refactored to somehow take that into account.
	 */
	public static class Renderer {
		protected static final RenderingHints qualityHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		static {
			qualityHints.put(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
			qualityHints.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
			qualityHints.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
			qualityHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		}
		
		
		protected GeneralPath body = new GeneralPath();
		private GeneralPath embellishedBody = new GeneralPath();
		protected GeneralPath glaze = new GeneralPath();
		
		/** Redefine the GeneralPath argument provided with the new border/body outline of this UI.
		 * 
		 * @param c the component being painted.
		 * @param map the attributes. At any given time: some attributes
		 * might be missing.
		 * @param body the path this method redefines.
		 * @param extraPadding a positive integer that expands the size of this shape.
		 */
		protected void updateBodyShape(Component c,Map<String, Object> map,GeneralPath body,int extraPadding) {
			GeneralPath b = new GeneralPath();
			int padding = 0;
			if(System.getProperty("os.name").toLowerCase().indexOf("mac")!=-1) {
				String s = System.getProperty("os.version");
				int i = s.indexOf('.');
				s = s.substring(i+1);
				i = s.indexOf('.');
				if(i!=-1) {
					s = s.substring(0,i);
				}
				i = Integer.parseInt(s);
				if(i>=4)
					padding = 1;
			}
			int shadowLayers = DecoratedPanelUI.Border.DROP_SHADOW_LAYER_COUNT.get(map, 0);
			int scribblePadding = (int)( DecoratedPanelUI.Border.SCRIBBLE_SIZE.get(map, 0f)+.5 );
			Insets insets = max(
				new Insets(scribblePadding, scribblePadding, scribblePadding, scribblePadding),
				new Insets(shadowLayers, shadowLayers, shadowLayers, shadowLayers)	
			);
			
			int w = c.getWidth()-padding-insets.right-insets.left;
			int h = c.getHeight()-padding-insets.top-insets.bottom;
			
			float strokeWidth = Border.STROKE_WIDTH.get(map,1f);
			float curveUpperLeft = Border.CURVE_UPPER_LEFT.get(map,0f);
			float curveUpperRight = Border.CURVE_UPPER_RIGHT.get(map,0f);
			float curveLowerLeft = Border.CURVE_LOWER_LEFT.get(map,0f);
			float curveLowerRight = Border.CURVE_LOWER_RIGHT.get(map,0f);
			float cornerSizeLowerLeft = Border.CORNER_SIZE_LOWER_LEFT.get(map,0f);
			float cornerSizeLowerRight = Border.CORNER_SIZE_LOWER_RIGHT.get(map,0f);
			float cornerSizeUpperLeft = Border.CORNER_SIZE_UPPER_LEFT.get(map,0f);
			float cornerSizeUpperRight = Border.CORNER_SIZE_UPPER_RIGHT.get(map,0f);
			
			final float k = strokeWidth/2;
			int j = extraPadding;

			b.reset();
			float z = .5f*curveUpperLeft;
			if(cornerSizeUpperLeft>0) {
				b.moveTo(k-j, cornerSizeUpperLeft-j);
				b.curveTo( k-j, cornerSizeUpperLeft*(1-z)+k-j,
						cornerSizeUpperLeft*(1-z)-j, k-j,
						cornerSizeUpperLeft-j, k-j
					);
			} else {
				b.moveTo(k-j,k-j);
			}
			
			z = .5f*curveUpperRight;

			if(cornerSizeUpperRight>0) {
				b.lineTo(w-cornerSizeUpperRight+j,k-j);
				b.curveTo(w+cornerSizeUpperRight*(z-1)-k+j, k-j,
					w-k+j,cornerSizeUpperRight*(1-z)+k-j,
					w-k+j,cornerSizeUpperRight+k-j);
			} else {
				b.lineTo(w-k+j,k-j);
			}
			
			z = .5f*curveLowerRight;

			if(cornerSizeLowerRight>0) {
				b.lineTo(w-k+j,h-cornerSizeLowerRight-k+j);
				b.curveTo(w-k+j,h+cornerSizeLowerRight*(z-1)-k+j,
					w+cornerSizeLowerRight*(z-1)-k+j,h-k+j,
					w-cornerSizeLowerRight-k+j,h-k+j);
			} else {
				b.lineTo(w-k+j,h-k+j);
			}

			z = .5f*curveLowerLeft;

			if(cornerSizeLowerLeft>0) {
				b.lineTo(cornerSizeLowerLeft+k-j,h-k+j);
				b.curveTo(cornerSizeLowerLeft*(1-z)+k-j,h-k+j,
					k-j,h+cornerSizeLowerLeft*(z-1)-k+j,
					k-j,h-cornerSizeLowerLeft-k+j);
			} else {
				b.lineTo(k-j,h-k+j);
			}

			z = .5f*curveUpperLeft;
			if(cornerSizeUpperLeft>0) {
				b.lineTo(k-j, cornerSizeUpperLeft-j);
			} else {
				b.lineTo(k-j,k-j);
			}
			
			b.closePath();
			b.transform( AffineTransform.getTranslateInstance(insets.left, insets.top) );
			
			body.reset();

			float scribbleSize = Border.SCRIBBLE_SIZE.get(map,0f);
			if(scribbleSize!=0) {
				Scribbler.create(b, scribbleSize, scribbleSize, 0, new GeneralPathWriter(body));
			} else {
				body.append(b, false);
			}
		}

		/** Redefine the GeneralPath argument provided with the new glaze outline of this UI.
		 * 
		 * @param c the component being painted.
		 * @param attributes the attributes. At any given time: some attributes
		 * might be missing.
		 * @param glaze the path this method redefines.
		 */
		protected void updateGlazeShape(Component c,Map<String, Object> attributes,GeneralPath glaze) {
			int w = c.getWidth();
			int h = c.getHeight();

			float glazeLevel = DecoratedPanelUI.GLAZE_LEVEL.get(attributes,1f);
			
			glaze.reset();

			float z = glazeLevel;
			glaze.moveTo(0,h);
			glaze.lineTo(0,(1-z)*(h*.9f)+z*(h*.8f));
			glaze.curveTo( 0,(1-z)*(h*.8f)+z*h*.6f,
					(1-z)*w*1f/8f+z*w*1f/8f, (1-z)*(h*.7f)+z*h*.5f,
					(1-z)*w*1f/2f+z*w*1f/2f, (1-z)*(h*.7f)+z*h*.5f);
			glaze.curveTo( (1-z)*w*7f/8f+z*w*7f/8f, (1-z)*h*.7f+z*h*.5f,
					(1-z)*w+z*w*.8f,(1-z)*h*.8f+z*h*.01f,
					w,(1-z)*h*.9f+z*h*.01f);
			glaze.lineTo(w,h);
			glaze.closePath();
		}
		
		/** Paint the drop shadow.
		 *
		 *  @see #paint(Graphics2D, Component, Map)
		 */
		protected void paintDropShadow(Graphics2D g,Component c,Map<String, Object> attributes) {
			float alpha = DecoratedPanelUI.Border.DROP_SHADOW_ALPHA.get(attributes, 0f);
			int layers = DecoratedPanelUI.Border.DROP_SHADOW_LAYER_COUNT.get(attributes, 0);
			
			Graphics2D g2 = (Graphics2D)g.create();
			GeneralPath scratch = new GeneralPath();
			float ll = Border.CURVE_LOWER_LEFT.get(attributes, 0f);
			float lr = Border.CURVE_LOWER_RIGHT.get(attributes, 0f);
			float ul = Border.CURVE_UPPER_LEFT.get(attributes, 0f);
			float ur = Border.CURVE_UPPER_RIGHT.get(attributes, 0f);
			float lls = Border.CORNER_SIZE_LOWER_LEFT.get(attributes, 0f);
			float lrs = Border.CORNER_SIZE_LOWER_RIGHT.get(attributes, 0f);
			float uls = Border.CORNER_SIZE_UPPER_LEFT.get(attributes, 0f);
			float urs = Border.CORNER_SIZE_UPPER_RIGHT.get(attributes, 0f);
			float maxSize = 100;
			try {
				g2.setColor(new Color(0,0,0, (int)(255*alpha)));
				for(float layer = 0; layer<layers; layer++) {
					attributes.put(Border.CURVE_LOWER_LEFT.toString(), Math.min(1, ll+.03f*layer));
					attributes.put(Border.CURVE_UPPER_LEFT.toString(), Math.min(1, ul+.03f*layer));
					attributes.put(Border.CURVE_LOWER_RIGHT.toString(), Math.min(1, lr+.03f*layer));
					attributes.put(Border.CURVE_UPPER_RIGHT.toString(), Math.min(1, ur+.03f*layer));
					attributes.put(Border.CORNER_SIZE_LOWER_LEFT.toString(), Math.min(maxSize, lls+layer));
					attributes.put(Border.CORNER_SIZE_LOWER_RIGHT.toString(), Math.min(maxSize, uls+layer));
					attributes.put(Border.CORNER_SIZE_UPPER_LEFT.toString(), Math.min(maxSize, lrs+layer));
					attributes.put(Border.CORNER_SIZE_UPPER_RIGHT.toString(), Math.min(maxSize, urs+layer));
					updateBodyShape(c, attributes, scratch, (int)(layer+.5));
					g2.translate(0, .5f);
					g2.setStroke(new BasicStroke(layer+.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
					
					g2.fill(scratch);
				}
			} finally {
				attributes.put(Border.CURVE_LOWER_LEFT.toString(), ll);
				attributes.put(Border.CURVE_UPPER_LEFT.toString(), ul);
				attributes.put(Border.CURVE_LOWER_RIGHT.toString(), lr);
				attributes.put(Border.CURVE_UPPER_RIGHT.toString(), ur);
				attributes.put(Border.CORNER_SIZE_LOWER_LEFT.toString(), lls);
				attributes.put(Border.CORNER_SIZE_UPPER_LEFT.toString(), uls);
				attributes.put(Border.CORNER_SIZE_LOWER_RIGHT.toString(), lrs);
				attributes.put(Border.CORNER_SIZE_UPPER_RIGHT.toString(), urs);
				g2.dispose();
			}
		}

		/** Paint the body fill.
		 *
		 *  @see #paint(Graphics2D, Component, Map)
		 */
		protected void paintBody(Graphics2D g,Component c,Map<String, Object> attributes) {
			Color c1 = DecoratedPanelUI.COLOR_TOP.get(attributes, null);
			Color c2 = DecoratedPanelUI.COLOR_BOTTOM.get(attributes, null);
			if(c1!=null && c2!=null && !c1.equals(c2)) {
				Paint p = new GradientTexturePaint( new Color[] {c1, c2}, new float[] {0, 1}, new Point(0,0), new Point(0,c.getHeight()), Cycle.TILE );
				/*Paint p = new GradientPaint(
						0, 0,
						c1,
						0, c.getHeight(),
						c2
				);*/
				g.setPaint(p);
				g.fill(body);
			} else if(c1!=null) {
				g.setPaint(c1);
				g.fill(body);
			} else if(c2!=null) {
				g.setPaint(c2);
				g.fill(body);
			}
		}
		
		/** Paints everything in this renderer, after updating the body/glaze shapes.
		 * Specifically this invokes the paint methods in this order:
		 * <ol><li>{@link #paintDropShadow(Graphics2D, Component, Map)}
		 * <li>{@link #paintBody(Graphics2D, Component, Map)}
		 * <li>{@link #paintBevels(Graphics2D, Component, Map)}
		 * <li>{@link #paintGlaze(Graphics2D, Component, Map)}
	 	 * <li>{@link #paintBorder(Graphics2D, Component, Map)}</li></ol>
		 * 
		 * @param g the graphics to paint to.
		 * @param c the component being rendered.
		 * @param attributes the available attributes, note some may be missing.
		 */
		public void paint(Graphics2D g,Component c,Map<String, Object> attributes) {
			updateBodyShape(c, attributes, body, 0);
			updateGlazeShape(c, attributes, glaze);
			updateBodyShape(c, attributes, embellishedBody, 1);
			
			Graphics2D g2 = (Graphics2D)g.create();
			try {
				g2.setRenderingHints(qualityHints);
				paintDropShadow(g2, c, attributes);
				paintBody(g2, c, attributes);
				paintBevels(g2, c, attributes);
				paintGlaze(g2, c, attributes);
				paintBorder(g2, c, attributes);
			} finally {
				g2.dispose();
			}
		}

		/** Paint the stroke border. 
		 *
		 *  @see #paint(Graphics2D, Component, Map)
		 */
		protected void paintBorder(Graphics2D g, Component c,
				Map<String, Object> attributes) {

			float borderWidth = Border.STROKE_WIDTH.get(attributes,1f);
			Paint borderPaint = Border.BORDER_PAINT.get(attributes, Color.black);
			
			if(borderWidth>0) {
				g.setStroke(new BasicStroke(borderWidth,BasicStroke.CAP_BUTT,BasicStroke.JOIN_ROUND,1));
				g.setPaint(borderPaint);
				g.draw(body);
			}
		}

		/** Paint the glaze.
		 *
		 *  @see #paint(Graphics2D, Component, Map)
		 */
		protected void paintGlaze(Graphics2D g, Component c,
				Map<String, Object> attributes) {

			float glazeOpacity = DecoratedPanelUI.GLAZE_OPACITY.get(attributes,0f);
			float z = DecoratedPanelUI.GLAZE_LEVEL.get(attributes,1f);	
			
			if(glazeOpacity>0) {
				Graphics2D g2 = (Graphics2D)g.create();
				try {
					int h = c.getHeight();
	
					Paint glazeFill = new GradientPaint(
							0, (int)((1-z)*h*.7+z*h*.3),
							new Color(0,0,0,(int)(glazeOpacity*50)),
							0, h,
							new Color(0,0,0,0));
					g2.clip(body);
					g2.setPaint(glazeFill);
					g2.fill(glaze);
				} finally {
					g2.dispose();
				}
			}
		}

		/** Paint the bevels.
		 *
		 *  @see #paint(Graphics2D, Component, Map)
		 */
		protected void paintBevels(Graphics2D g, Component c,
				Map<String, Object> attributes) {
			float borderWidth = Border.STROKE_WIDTH.get(attributes,1f);
			float shadowTheta = Border.BEVEL_SHADOW_THETA.get(attributes,0f);
			int shadowStrokeCount = Border.BEVEL_SHADOW_LAYER_COUNT.get(attributes, 0);
			int highlightStrokeCount = Border.BEVEL_HIGHLIGHT_LAYER_COUNT.get(attributes, 0);
			float maxShadowBlend = Border.BEVEL_SHADOW_MAX_BLEND.get(attributes, 0f);
			float maxHighlightBlend = Border.BEVEL_HIGHLIGHT_MAX_BLEND.get(attributes, 0f);
			
			//paint the shadow:
			Graphics2D g2 = (Graphics2D)g.create();
			try {
				if(borderWidth>1) {
					g2.clip(embellishedBody);
				} else {
					g2.clip(body);
				}
				g2.setStroke(new BasicStroke(2));
				float shadowDX = (float)Math.cos(shadowTheta);
				float shadowDY = (float)Math.sin(shadowTheta);
				g2.translate( borderWidth*Math.cos(shadowTheta)/2.0, borderWidth*Math.sin(shadowTheta)/2.0 );
				for(int a = 0; a<shadowStrokeCount; a++) {
					float f = ((float)(shadowStrokeCount-a))/((float)(shadowStrokeCount));
					g2.setColor(blendColors(new Color(0,0,0,0),Color.black, f*maxShadowBlend));
					g2.translate(shadowDX,shadowDY);
					g2.draw(body);
				}
			} finally {
				g2.dispose();
			}
				
			//paint the highlight
			g2 = (Graphics2D)g.create();
			try {	
				if(borderWidth>1) {
					g2.clip(embellishedBody);
				} else {
					g2.clip(body);
				}
				g2.setStroke(new BasicStroke(2));
				float shadowDX = (float)Math.cos(shadowTheta+Math.PI);
				float shadowDY = (float)Math.sin(shadowTheta+Math.PI);
				g2.translate( borderWidth*Math.cos(shadowTheta+Math.PI)/2.0, borderWidth*Math.sin(shadowTheta+Math.PI)/2.0 );
				for(int a = 0; a<highlightStrokeCount; a++) {
					float f = ((float)(highlightStrokeCount-a))/((float)(highlightStrokeCount));
					g2.setColor(blendColors(new Color(255,255,255,0),Color.white, f*maxHighlightBlend));
					g2.translate(shadowDX,shadowDY);
					g2.draw(body);
				}
			} finally {
				g2.dispose();
			}

		}
	}
	
	/** The Border installed by a {@link DecoratedPanelUI}.
	 * <p>This border was originally designed to be used in combination with the
	 * <code>DecorationPanelUI</code>, but it can also be used
	 * independently as needed.
	 */
	public static class Border implements javax.swing.border.Border {
		/** The attribute defining the stroke width, ranging from 0 to 5 pixels. */
		public static final Key<Float> STROKE_WIDTH = new Key<Float>("stroke-width", Float.class, 0, 5, true, true);

		/** The attribute defining the upper-left corner's curvature, ranging from 0 (90 degrees) to 1 (circular curve). */
		public static final Key<Float> CURVE_UPPER_LEFT = new Key<Float>("curve-upper-left", Float.class, 0, 1, true, true);

		/** The attribute defining the upper-right corner's curvature, ranging from 0 (90 degrees) to 1 (circular curve). */
		public static final Key<Float> CURVE_UPPER_RIGHT = new Key<Float>("curve-upper-right", Float.class, 0, 1, true, true);

		/** The attribute defining the lower-left corner's curvature, ranging from 0 (90 degrees) to 1 (circular curve). */
		public static final Key<Float> CURVE_LOWER_LEFT = new Key<Float>("curver-lower-left", Float.class, 0, 1, true, true);
		
		/** The attribute defining the lower-right corner's curvature, ranging from 0 (90 degrees) to 1 (circular curve). */
		public static final Key<Float> CURVE_LOWER_RIGHT = new Key<Float>("curve-lower-right", Float.class, 0, 1, true, true);

		/** The attribute defining the upper-left corner's radius, ranging from 0 to 100 pixels. */
		public static final Key<Float> CORNER_SIZE_UPPER_LEFT = new Key<Float>("corner-size-upper-left", Float.class, 0, 100, true, true);

		/** The attribute defining the upper-right corner's radius, ranging from 0 to 100 pixels. */
		public static final Key<Float> CORNER_SIZE_UPPER_RIGHT = new Key<Float>("corner-size-upper-right", Float.class, 0, 100, true, true);

		/** The attribute defining the lower-left corner's radius, ranging from 0 to 100 pixels. */
		public static final Key<Float> CORNER_SIZE_LOWER_LEFT = new Key<Float>("corner-size-lower-left", Float.class, 0, 100, true, true);

		/** The attribute defining the lower-right corner's radius, ranging from 0 to 100 pixels. */
		public static final Key<Float> CORNER_SIZE_LOWER_RIGHT = new Key<Float>("corner-size-lower-right", Float.class, 0, 100, true, true);
		
		/** The attribute defining the border paint, which is only made visible if {@link #STROKE_WIDTH} is nonzero. */
		public static final Key<Paint> BORDER_PAINT = new Key<Paint>("border-paint", Paint.class);
		
		/** The attribute defining the angle the bevel shadows and highlights are oriented, ranging from 0 to 2*pi. A value of zero
		 * positions the shadow on the left side, and it rotates clockwise from there: so a value of pi/2 puts the shadow on the top and a
		 * value of pi puts the shadow on the right. The highlight is always opposite the shadow.
		 */
		public static final Key<Float> BEVEL_SHADOW_THETA = new Key<Float>("bevel-shadow-theta", Float.class);
		
		/** The attribute defining the number of paint operations used to layer the bevel shadow, ranging from 0 to 20. */
		public static final Key<Integer> BEVEL_SHADOW_LAYER_COUNT = new Key<Integer>("bevel-shadow-layer-count", Integer.class, 0, 20, true, true);

		/** The attribute defining the number of paint operations used to layer the bevel highlight, ranging from 0 to 20. */
		public static final Key<Integer> BEVEL_HIGHLIGHT_LAYER_COUNT = new Key<Integer>("bevel-highlight-layer-count", Integer.class, 0, 20, true, true);
		
		/** The attribute defining how dark to make the shadow bevel, ranging from 0 to 1. */
		public static final Key<Float> BEVEL_SHADOW_MAX_BLEND = new Key<Float>("bevel-shadow-max-blend", Float.class, 0, 1, true, true);
		
		/** The attribute defining how light to make the highlight bevel, ranging from 0 to 1. */
		public static final Key<Float> BEVEL_HIGHLIGHT_MAX_BLEND = new Key<Float>("bevel-highlight-max-blend", Float.class, 0, 1, true, true);

		/** The attribute defining the number of paint operations used to layer the drop shadow, ranging from 0 to 20. */
		public static final Key<Integer> DROP_SHADOW_LAYER_COUNT = new Key<Integer>("drop-shadow-layer-count", Integer.class, 0, 20, true, true);

		/** The attribute defining how dark to make each drop shadow layer, ranging from 0 to .05. */
		public static final Key<Float> DROP_SHADOW_ALPHA = new Key<Float>("drop-shadow-alpha", Float.class, 0, .05f, true, true);
		
		/** The attribute defining how many pixels to deviate from the ideal body outline when applying a scribble effect, ranging from 0 to 5 pixels. */
		public static final Key<Float> SCRIBBLE_SIZE = new Key<Float>("scribble-size", Float.class, 0, 5, true, true);
		
		ObservableProperties properties = new ObservableProperties();
		Renderer renderer;
		
		public Border() {
			this(new Renderer());
		}
		
		public Border(Renderer renderer) {
			if(renderer==null) throw new NullPointerException();
			
			this.renderer = renderer;
			setScribbleSize(0f);
			setDropShadowAlpha(0);
			setDropShadowLayerCount(0);
			setBevelShadowLayerCount(0);
			setBevelHighlightLayerCount(0);
			setStrokeWidth(0);
			setCornerSize(0);
			setCurvature(1);
			setBevelShadowMaxBlend(.08f);
			setBevelHighlightMaxBlend(1f);
			setBorderPaint(Color.black);
			setBevelShadowAngle(0);
		}

		/** Return true if the argument is supported by this class. */
		public static boolean isSupported(Key<?> key) {
			if(BEVEL_HIGHLIGHT_LAYER_COUNT.equals(key))
				return true;
			if(BEVEL_HIGHLIGHT_MAX_BLEND.equals(key))
				return true;
			if(BEVEL_SHADOW_LAYER_COUNT.equals(key))
				return true;
			if(BEVEL_SHADOW_MAX_BLEND.equals(key))
				return true;
			if(BEVEL_SHADOW_THETA.equals(key))
				return true;
			if(BORDER_PAINT.equals(key))
				return true;
			if(CORNER_SIZE_LOWER_LEFT.equals(key))
				return true;
			if(CORNER_SIZE_LOWER_RIGHT.equals(key))
				return true;
			if(CORNER_SIZE_UPPER_LEFT.equals(key))
				return true;
			if(CORNER_SIZE_UPPER_RIGHT.equals(key))
				return true;
			if(CURVE_LOWER_LEFT.equals(key))
				return true;
			if(CURVE_LOWER_RIGHT.equals(key))
				return true;
			if(CURVE_UPPER_LEFT.equals(key))
				return true;
			if(CURVE_UPPER_RIGHT.equals(key))
				return true;
			if(DROP_SHADOW_ALPHA.equals(key))
				return true;
			if(DROP_SHADOW_LAYER_COUNT.equals(key))
				return true;
			if(SCRIBBLE_SIZE.equals(key))
				return true;
			if(STROKE_WIDTH.equals(key))
				return true;
			return false;
		}
		
		/** Return a property.
		 * 
		 * @throws IllegalArgumentException if the key provided is not supported by this class.
		 */
		public <T> T getProperty(Key<T> key) {
			if(!isSupported(key))
				throw new IllegalArgumentException("The Border class does not supported \""+key+"\"");
			return properties.get(key);	
		}

		/** Assign a property.
		 * 
		 * @throws IllegalArgumentException if the key provided is not supported by this class.
		 */
		public <T> void setProperty(Key<T> key,T value) {
			if(!isSupported(key))
				throw new IllegalArgumentException("The Border class does not supported \""+key+"\"");
			properties.set(key, value);
		}
		
		/** Add a listener that is notified when attributes change. */
		public void addPropertyChangeListener(PropertyChangeListener pcl) {
			properties.addListener(pcl);
		}

		/** Remove a listener. */
		public void removePropertyChangeListener(PropertyChangeListener pcl) {
			properties.removeListener(pcl);
		}
		
		/** Return the stroke width.
		 * @see #STROKE_WIDTH
		 */
		public float getStrokeWidth() {
			return properties.get(STROKE_WIDTH);
		}
		
		/** Assign the scribble size.
		 * @see #SCRIBBLE_SIZE
		 */
		public void setScribbleSize(float f) {
			properties.set(SCRIBBLE_SIZE, f);
		}
		
		/** Return the scribble size.
		 * @see #SCRIBBLE_SIZE
		 */
		public float getScribbleSize() {
			return properties.get(SCRIBBLE_SIZE);
		}
		
		/** Set the stroke width.
		 * @see #STROKE_WIDTH
		 */
		public void setStrokeWidth(float f) {
			properties.set(STROKE_WIDTH, f);
		}
		
		/** Set the curvature of all corners.
		 * @see #CURVE_LOWER_LEFT
		 * @see #CURVE_LOWER_RIGHT
		 * @see #CURVE_UPPER_LEFT
		 * @see #CURVE_UPPER_RIGHT
		 */
		public void setCurvature(float i) {
			properties.set(CURVE_LOWER_LEFT, i);
			properties.set(CURVE_LOWER_RIGHT, i);
			properties.set(CURVE_UPPER_LEFT, i);
			properties.set(CURVE_UPPER_RIGHT, i);
		}
		
		/** Set the curvature of the lower-right corner.
		 * @see #CURVE_LOWER_RIGHT
		 * @see #setCurvature(float)
		 */
		public void setLowerRightCurvature(float i) {
			properties.set(CURVE_LOWER_RIGHT, i);
		}

		/** Set the curvature of the upper-left corner.
		 * @see #CURVE_UPPER_LEFT
		 * @see #setCurvature(float)
		 */
		public void setUpperLeftCurvature(float i) {
			properties.set(CURVE_UPPER_LEFT, i);
		}

		/** Set the curvature of the upper-right corner.
		 * @see #CURVE_UPPER_RIGHT
		 * @see #setCurvature(float)
		 */
		public void setUpperRightCurvature(float i) {
			properties.set(CURVE_UPPER_RIGHT, i);
		}

		/** Set the curvature of the lower-left corner.
		 * @see #CURVE_LOWER_LEFT
		 * @see #setCurvature(float)
		 */
		public void setLowerLeftCurvature(float i) {
			properties.set(CURVE_LOWER_LEFT, i);
		}
		
		/** Return the lower-left curvature.
		 * @see #CURVE_LOWER_LEFT
		 */
		public float getLowerLeftCurvature() {
			return properties.get(CURVE_LOWER_LEFT);
		}

		/** Return the lower-right curvature.
		 * @see #CURVE_LOWER_RIGHT
		 */
		public float getLowerRightCurvature() {
			return properties.get(CURVE_LOWER_RIGHT);
		}

		/** Return the upper-left curvature.
		 * @see #CURVE_UPPER_LEFT
		 */
		public float getUpperLeftCurvature() {
			return properties.get(CURVE_UPPER_LEFT);
		}

		/** Return the lower-right curvature.
		 * @see #CURVE_LOWER_RIGHT
		 */
		public float getUpperRightCurvature() {
			return properties.get(CURVE_UPPER_RIGHT);
		}

		/** Return the lower-left corner size.
		 * @see #CORNER_SIZE_LOWER_LEFT
		 */
		public float getLowerLeftCornerSize() {
			return properties.get(CORNER_SIZE_LOWER_LEFT);
		}

		/** Return the lower-right corner size.
		 * @see #CORNER_SIZE_LOWER_RIGHT
		 */
		public float getLowerRightCornerSize() {
			return properties.get(CORNER_SIZE_LOWER_RIGHT);
		}

		/** Return the upper-left corner size.
		 * @see #CORNER_SIZE_UPPER_LEFT
		 */
		public float getUpperLeftCornerSize() {
			return properties.get(CORNER_SIZE_UPPER_LEFT);
		}

		/** Return the number of layers in the drop shadow.
		 * @see #DROP_SHADOW_LAYER_COUNT
		 */
		public int getDropShadowLayerCount() {
			return properties.get(DROP_SHADOW_LAYER_COUNT);
		}
		
		/** Set the number of layers in the drop shadow.
		 * @see #DROP_SHADOW_LAYER_COUNT
		 */
		public void setDropShadowLayerCount(int i) {
			properties.set(DROP_SHADOW_LAYER_COUNT, i);
		}
		
		/** Return the opacity of each layer in the drop shadow.
		 * @see #DROP_SHADOW_ALPHA
		 */
		public float getDropShadowAlpha() {
			return properties.get(DROP_SHADOW_ALPHA);
		}

		/** Set the opacity of each layer in the drop shadow.
		 * @see #DROP_SHADOW_ALPHA
		 */
		public void setDropShadowAlpha(float f) {
			properties.set(DROP_SHADOW_ALPHA, f);
		}

		/** Return the upper-right corner size.
		 * @see #CORNER_SIZE_UPPER_RIGHT
		 */
		public float getUpperRightCornerSize() {
			return properties.get(CORNER_SIZE_UPPER_RIGHT);
		}

		/** Paint this border.
		 * <p>This asks a {@link Renderer} to paint everything, but
		 * first uses a clipping to punch a whole in the middle of
		 * the Graphics2D. (So only the border will show.)
		 * <p>If the component has applied a <code>DecoratedPanelUI</code>:
		 * then attributes from that panel are also retrieved so the
		 * gradient/glaze/etc appear correct in the the border.
		 */
		public void paintBorder(Component c, Graphics g, int x, int y,
				int width, int height) {
			Graphics2D g2 = (Graphics2D)g.create();
			try {
				g2.translate(-x, -y);
			
				Area borderOutline = new Area(new Rectangle(0,0,width,height));
				Insets insets = getBorderInsets(c);
				borderOutline.subtract(new Area(new Rectangle(
						insets.left, insets.top,
						width - insets.left - insets.right,
						height - insets.top - insets.bottom
						)));
				g2.clip(borderOutline);
				
				Map<String, Object> r = properties.getMap(false, false, ObservableProperties.DEFAULT);
				if(c instanceof JPanel) {
					JPanel jp = (JPanel)c;
					if(jp.getUI() instanceof DecoratedPanelUI) {
						DecoratedPanelUI d = (DecoratedPanelUI)jp.getUI();
						r.putAll(d.properties.getMap(false, false, ObservableProperties.DEFAULT));
					}
				}
				
				renderer.paint(g2, c, r);
			} finally {
				g2.dispose();
			}
		}

		/** Return the minimum insets this border requires. */
		public Insets getBorderInsets(Component c) {
			int top, left, bottom, right;
			float w = getStrokeWidth();
			right = (int)(w+Math.max( getUpperRightCornerSize(), getLowerRightCornerSize())+.5f);
			bottom = (int)(w+Math.max( getLowerLeftCornerSize(), getLowerRightCornerSize())+.5f);
			top = (int)(w+Math.max( getUpperRightCornerSize(), getUpperLeftCornerSize())+.5f);
			left = (int)(w+Math.max( getLowerLeftCornerSize(), getUpperLeftCornerSize())+.5f);
			Insets curvatureInsets = new Insets(top, left, bottom, right);
			
			float bevel = Math.max(getBevelShadowLayerCount(), getBevelHighlightLayerCount());
			int k = (int)(w + bevel + 2*getScribbleSize()+.5) + getDropShadowLayerCount();
			Insets layerInsets = new Insets( k, k, k, k);

			return max(curvatureInsets, layerInsets);
		}

		/** Returns <code>false</code>. */
		public boolean isBorderOpaque() {
			return false;
		}
		
		/** Set the maximum blend of the bevel highlight.
		 * @see #BEVEL_HIGHLIGHT_MAX_BLEND
		 */
		public void setBevelHighlightMaxBlend(float f) {
			properties.set(BEVEL_HIGHLIGHT_MAX_BLEND, f);
		}
		
		/** Set the corner size of all 4 corners.
		 * @see #CORNER_SIZE_LOWER_LEFT
		 * @see #CORNER_SIZE_LOWER_RIGHT
		 * @see #CORNER_SIZE_UPPER_LEFT
		 * @see #CORNER_SIZE_UPPER_RIGHT
		 */
		public void setCornerSize(float f) {
			properties.set(CORNER_SIZE_LOWER_LEFT, f);
			properties.set(CORNER_SIZE_LOWER_RIGHT, f);
			properties.set(CORNER_SIZE_UPPER_LEFT, f);
			properties.set(CORNER_SIZE_UPPER_RIGHT, f);
		}
		
		/** Return the border paint.
		 * @see #BORDER_PAINT
		 */
		public void setBorderPaint(Paint p) {
			properties.set(BORDER_PAINT, p);
		}
		
		/** Set the lower-left corner size.
		 * @see #CORNER_SIZE_LOWER_LEFT
		 * @see #setCornerSize(float)
		 */
		public void setLowerLeftCornerSize(float f) {
			properties.set(CORNER_SIZE_LOWER_LEFT, f);
		}

		/** Set the lower-right corner size.
		 * @see #CORNER_SIZE_LOWER_RIGHT
		 * @see #setCornerSize(float)
		 */
		public void setLowerRightCornerSize(final float f) {
			properties.set(CORNER_SIZE_LOWER_RIGHT, f);
		}

		/** Set the upper-left corner size.
		 * @see #CORNER_SIZE_UPPER_LEFT
		 * @see #setCornerSize(float)
		 */
		public void setUpperLeftCornerSize(final float f) {
			properties.set(CORNER_SIZE_UPPER_LEFT, f);
		}

		/** Set the upper-right corner size.
		 * @see #CORNER_SIZE_UPPER_RIGHT
		 * @see #setCornerSize(float)
		 */
		public void setUpperRightCornerSize(final float f) {
			properties.set(CORNER_SIZE_UPPER_RIGHT, f);
		}

		/** Return the maximum blend of the bevel shadow.
		 * @see #BEVEL_SHADOW_MAX_BLEND
		 */
		public float getBevelShadowMaxBlend() {
			return properties.get(BEVEL_SHADOW_MAX_BLEND);
		}

		/** Return the maximum blend of the bevel highlight.
		 * @see #BEVEL_HIGHLIGHT_MAX_BLEND
		 */
		public float getBevelHighlightMaxBlend() {
			return properties.get(BEVEL_HIGHLIGHT_MAX_BLEND);
		}
		
		/** Return the number of layers in the bevel shadow.
		 * @see #BEVEL_SHADOW_LAYER_COUNT
		 */
		public int getBevelShadowLayerCount() {
			return properties.get(BEVEL_SHADOW_LAYER_COUNT);
		}
		
		/** Return the number of layers in the bevel highlight.
		 * @see #BEVEL_HIGHLIGHT_LAYER_COUNT
		 */
		public int getBevelHighlightLayerCount() {
			return properties.get(BEVEL_HIGHLIGHT_LAYER_COUNT);
		}
		
		/** Return the angle of the bevel shadow.
		 * @see #BEVEL_SHADOW_THETA
		 */
		public float getBevelShadowTheta() {
			return properties.get(BEVEL_SHADOW_THETA);
		}
		
		/** Set the angle of the bevel shadow.
		 * @see #BEVEL_SHADOW_THETA
		 */
		public void setBevelShadowAngle(float f) {
			properties.set(BEVEL_SHADOW_THETA, f);
		}
		
		/** Set the number of layers in the bevel shadow.
		 * @see #BEVEL_SHADOW_LAYER_COUNT
		 */
		public void setBevelShadowLayerCount(int i) {
			properties.set(BEVEL_SHADOW_LAYER_COUNT, i);
		}

		/** Set the number of layers in the bevel highlight.
		 * @see #BEVEL_HIGHLIGHT_LAYER_COUNT
		 */
		public void setBevelHighlightLayerCount(int i) {
			properties.set(BEVEL_HIGHLIGHT_LAYER_COUNT, i);
		}

		/** Set the maximum blend of the bevel shadow.
		 * @see #BEVEL_SHADOW_MAX_BLEND
		 */
		public void setBevelShadowMaxBlend(float f) {
			properties.set(BEVEL_SHADOW_MAX_BLEND, f);
		}
		
		@Override
		public String toString() {
			return "DecoratedPanelUI.Border( "+properties+")";
		}
	}
	
	protected Set<JComponent> subscribedPanels = new HashSet<JComponent>();

	/** The attribute defining how tall make the glaze, ranging from 0 to 1. */
	public static final Key<Float> GLAZE_LEVEL = new Key<Float>("glaze-level", Float.class, 0, 1, true, true);

	/** The attribute defining how opaque make the glaze, ranging from 0 to 1. */
	public static final Key<Float> GLAZE_OPACITY = new Key<Float>("glaze-opacity", Float.class, 0, 1, true, true);
	
	/** The attribute defining the top color of the vertical gradient.
	 * If this is null, then only the COLOR_BOTTOM is used.
	 * If that is also null, then the panel background is used.
	 * (And if that is null or <code>panel.isOpaque()</code> is false, then the body is left empty. 
	 */
	public static final Key<Color> COLOR_TOP = new Key<Color>("color-top", Color.class);

	/** The attribute defining the bottom color of the vertical gradient.
	 * If this is null, then only the COLOR_TOP is used.
	 * If that is also null, then the panel background is used.
	 * (And if that is null or <code>panel.isOpaque()</code> is false, then the body is left empty. 
	 */
	public static final Key<Color> COLOR_BOTTOM = new Key<Color>("color-bottom", Color.class);
	
	protected Border border;
	protected ObservableProperties properties = new ObservableProperties();
	protected Renderer renderer;
	
	/** Create a new <code>DecoratedPanelUI</code> with a default {@link Renderer}.
	 */
	public DecoratedPanelUI() {
		this(null);
	}
	
	/** Create a new <code>DecoratedPanelUI</code> with a given {@link Renderer}
	 */
	public DecoratedPanelUI(Renderer renderer) {
		if(renderer==null) 
			renderer = new Renderer();
		this.renderer = renderer;
		border = new Border(renderer);
		setGlazeLevel(0);
		setGlazeOpacity(0);
		Color panelColor = UIManager.getColor("Panel.background");
		if(panelColor==null) panelColor = new Color(0xCCCCCC);
		setTopColor( panelColor );
		setBottomColor( panelColor );
		addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				repaintSubscribedPanels();
			}
		});
	}

	/** Return true if the argument is supported by this class. */
	public static boolean isSupported(Key<?> key) {
		if(COLOR_BOTTOM.equals(key))
			return true;
		if(COLOR_TOP.equals(key))
			return true;
		if(GLAZE_LEVEL.equals(key))
			return true;
		if(GLAZE_OPACITY.equals(key))
			return true;
		return false;
	}

	/** Return a property.
	 * 
	 * @throws IllegalArgumentException if the key provided is not supported by this class.
	 */
	public <T> T getProperty(Key<T> key) {
		if(!isSupported(key))
			throw new IllegalArgumentException("The DecoratedPanelUI class does not supported \""+key+"\"");
		return properties.get(key);	
	}

	/** Assign a property.
	 * 
	 * @throws IllegalArgumentException if the key provided is not supported by this class.
	 */
	public <T> void setProperty(Key<T> key,T value) {
		if(!isSupported(key))
			throw new IllegalArgumentException("The DecoratedPanelUI class does not supported \""+key+"\"");
		properties.set(key, value);
	}
	
	/** Adds a PropertyChangeListener to this object and its {@link Border}.
	 */
	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		properties.addListener(pcl);
		border.addPropertyChangeListener(pcl);
	}

	/** Removes a PropertyChangeListener from this object and its {@link Border}.
	 */
	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		properties.removeListener(pcl);
		border.removePropertyChangeListener(pcl);
	}
	
	/** Return the {@link Border} for this UI. This border is automatically
	 * installed when <code>panel.setUI(this)</code> is called. (Or more specifically:
	 * this is automatically installed when <code>this.installUI(panel)</code>
	 * is called.)
	 */
	public Border getBorder() {
		return border;
	}
	
	public Shape getShape(Component c) {
		BufferedImage bi = new BufferedImage(1,1,BufferedImage.TYPE_INT_ARGB);
		renderer.paint(bi.createGraphics(), c, getAllProperties());
		return new GeneralPath(renderer.body);
	}
	
	@Override
	public String toString() {
		return "DecoratedPanelUI[ "+getAllProperties()+" ]";
	}
	
	/** This paints the UI.
	 * <p>This method relies on the {@link Renderer} class to paint everything,
	 * but first it clips the Graphics2D to hide the border (so the border
	 * will render separately).
	 */
	@Override
	public void paint(Graphics g, JComponent c) {
		super.paint(g, c);
		
		Graphics2D g2 = (Graphics2D)g.create();
		try {
			if(c.getBorder()==border) {
				Insets i = border.getBorderInsets(c);
				g2.clipRect(i.left, i.top, c.getWidth()-i.left-i.right, c.getHeight()-i.top-i.bottom);
			}
			Map<String, Object> r = getAllProperties();
			renderer.paint( g2, c, r);
		} finally {
			g2.dispose();
		}
	}
	
	/** Return all properties */
	protected Map<String, Object> getAllProperties() {
		Map<String, Object> r = properties.getMap(false, false, ObservableProperties.DEFAULT);
		r.putAll( border.properties.getMap(false, false, ObservableProperties.DEFAULT) );
		return r;
	}
	
	/** Return the glaze level.
	 * @see #GLAZE_LEVEL
	 */
	public float getGlazeLevel() {
		return properties.get(GLAZE_LEVEL);
	}

	/** Return the glaze opacity.
	 * @see #GLAZE_OPACITY
	 */
	public float getGlazeOpacity() {
		return properties.get(GLAZE_OPACITY);
	}
	
	/** Set the glaze level.
	 * @see #GLAZE_LEVEL
	 */
	public void setGlazeLevel(final float l) {
		properties.set(GLAZE_LEVEL, l);
	}
	
	/** Set the glaze opacity.
	 * @see GLAZE_OPACITY
	 */
	public void setGlazeOpacity(final float t) {
		properties.set(GLAZE_OPACITY, t);
	}

	/** Return the top color of the vertical gradient.
	 * @see #COLOR_TOP
	 */
	public Color getTopColor() {
		return properties.get(COLOR_TOP);
	}

	/** Set the top color of the vertical gradient.
	 * @see #COLOR_TOP
	 */
	public void setTopColor(Color c) {
		properties.set(COLOR_TOP, c);
	}

	/** Set the top and bottom colors to the same value,
	 * making this a uniform color.
	 * @see #COLOR_TOP
	 * @see #COLOR_BOTTOM
	 */
	public void setColors(Color c) {
		setTopColor(c);
		setBottomColor(c);
	}

	/** Return the bottom color of the vertical gradient.
	 * @see #COLOR_BOTTOM
	 */
	public Color getBottomColor() {
		return properties.get(COLOR_BOTTOM);
	}

	/** Set the bottom color of the vertical gradient.
	 * @see #COLOR_BOTTOM
	 */
	public void setBottomColor(Color c) {
		properties.set(COLOR_BOTTOM, c);
	}
	
	/** Repaint all panels using this UI.
	 */
	protected void repaintSubscribedPanels() {
		synchronized(subscribedPanels) {
			for(JComponent jc : subscribedPanels) {
				jc.repaint();
			}
		}
	}
	
    @Override
	public void installUI(JComponent c) {
		super.installUI(c);
		c.setBorder(border);
		synchronized(subscribedPanels) {
			subscribedPanels.add(c);
		}
	}

	@Override
	public void uninstallUI(JComponent c) {
		super.uninstallUI(c);
		synchronized(subscribedPanels) {
			subscribedPanels.remove(c);
		}
	}


	/** Blends 2 colors together
     * 
     * @param a the first color
     * @param b the second color
     * @param p the progress (as a decimal between zero and one) to use.
     * When p is zero, the color returned is "a".  When p is one, the color
     * returned is "b".  When p is .5, the color is 50% "a" and 50% "b", etc.
     * @return a color that is between a and b.
     */
	public static Color blendColors(Color a,Color b,float p) {
        if(p<0 || p>1)
            throw new IllegalArgumentException("p ("+p+") must be between [0,1]");
		return new Color(
				(int)(a.getRed()*(1-p)+b.getRed()*p),
				(int)(a.getGreen()*(1-p)+b.getGreen()*p),
				(int)(a.getBlue()*(1-p)+b.getBlue()*p),
				(int)(a.getAlpha()*(1-p)+b.getAlpha()*p)
		);
	}
	
	protected static Insets max(Insets i1,Insets i2) {
		return new Insets(
			Math.max(i1.top, i2.top),
			Math.max(i1.left, i2.left),
			Math.max(i1.bottom, i2.bottom),
			Math.max(i1.right, i2.right)
		);
	}
}
