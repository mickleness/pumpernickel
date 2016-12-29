/*
 * @(#)SwivelPathAnimationDemo.java
 *
 * $Date: 2016-07-30 12:32:18 +0500 (Sat, 30 Jul 2016) $
 *
 * Copyright (c) 2012 by Jeremy Wood.
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
package com.pump.showcase;

import java.awt.BasicStroke;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.TexturePaint;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.ProgressMonitor;
import javax.swing.RootPaneContainer;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MouseInputAdapter;

import com.pump.UserCancelledException;
import com.pump.animation.BufferedAnimationPanel;
import com.pump.animation.SwivelPathAnimation;
import com.pump.animation.SwivelPathAnimation.Highlight;
import com.pump.animation.quicktime.JPEGMovWriter;
import com.pump.awt.BufferedImagePaintable;
import com.pump.awt.Dimension2D;
import com.pump.awt.EmptyPaintable;
import com.pump.awt.Paintable;
import com.pump.awt.TextBoxPaintable;
import com.pump.awt.TextClusterPaintable;
import com.pump.blog.Blurb;
import com.pump.geom.ShapeBounds;
import com.pump.geom.TransformUtils;
import com.pump.image.ImageLoader;
import com.pump.io.SuffixFilenameFilter;
import com.pump.swing.AnimationController;
import com.pump.swing.JFancyBox;
import com.pump.swing.JThrobber;
import com.pump.swing.PartialLineBorder;

/** This demos the {@link SwivelPathAnimation} class.
 */
@Blurb (
filename = "SwivelPathAnimationDemo",
title = "Text: Swivel Animations",
releaseDate = "Mar 2012",
summary = "This animation idly skims over a series of text boxes by panning and zooming at interesting angles.",
instructions = "This applet demonstrates a complex animation that swivels and zooms to show clumps of text "+
"at right angles. On the left you see the path the animation will take, and on the right you see the animation "+
"itself with a familiar playback controller. Click the \"Help\" button for more information.",
link = "http://javagraphics.blogspot.com/2012/03/text-swivel-animations.html",
sandboxDemo = true
)
public class SwivelPathAnimationDemo extends JPanel {
	private static final long serialVersionUID = 1L;
	
	static enum Corner {TOPLEFT, TOPRIGHT, BOTTOMLEFT, BOTTOMRIGHT}
	
	class OverviewPanel extends JComponent {
		private static final long serialVersionUID = 1L;
		
		Highlight indicatedHighlight = null;
		
		MouseInputAdapter mouseListener = new MouseInputAdapter() {
			Highlight draggedHighlight;
			Corner draggedCorner = null;
			
			@Override
			public void mouseClicked(MouseEvent e) {}

			@Override
			public void mouseDragged(MouseEvent e) {
				if(draggedHighlight==null) return;
				try {
					Point2D abstractPoint = convertToAbstract(e.getPoint());
					
					if(draggedCorner!=null && e.isShiftDown()==false) {
						AffineTransform tx = new AffineTransform();
						tx.rotate( -draggedHighlight.getAngle() );
						tx.translate( -draggedHighlight.getCenterX(), -draggedHighlight.getCenterY() );
						Point2D vector = tx.transform(abstractPoint, null);
						draggedHighlight.setWidth( Math.max(1, Math.abs(vector.getX()*2) ) );
						draggedHighlight.setHeight( Math.max(1, Math.abs(vector.getY()*2) ) );
					} else if(draggedCorner!=null) {
						double angle = Math.atan2( abstractPoint.getY() - draggedHighlight.getCenterY(), abstractPoint.getX() - draggedHighlight.getCenterX() );
						if(draggedCorner==Corner.TOPLEFT) {
							angle = angle + Math.PI*3/4; //because it's top-left
						} else if(draggedCorner==Corner.TOPRIGHT) {
							angle = angle + Math.PI*1/4; //because it's top-right
						} else if(draggedCorner==Corner.BOTTOMRIGHT) {
							angle = angle - Math.PI*1/4; //because it's bottom-right
						} else if(draggedCorner==Corner.BOTTOMLEFT) {
							angle = angle - Math.PI*3/4; //because it's bottom-left
						}
						draggedHighlight.setAngle(angle);
					} else {
						draggedHighlight.setCenter( abstractPoint.getX(), abstractPoint.getY() );
					}
					updatePath();
				} catch(NoninvertibleTransformException e2) {
					e2.printStackTrace();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}

			@Override
			public void mouseMoved(MouseEvent e) {
				Highlight h = getHighlight(e);
				if(h==null) {
					setCursor( Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR) );
				} else {
					try {
						Point2D abstractPoint = convertToAbstract(e.getPoint());
						Corner corner = getCorner( h, abstractPoint );
						if(corner!=null) {
							setCursor( Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR) );
						} else {
							setCursor( Cursor.getPredefinedCursor(Cursor.HAND_CURSOR) );
						}
					} catch(NoninvertibleTransformException e2) {
						setCursor( Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR) );
					}
				}
				if(h!=indicatedHighlight) {
					indicatedHighlight = h;
					repaint();
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				try {
					Highlight h = getHighlight(e);
					Point2D abstractPoint = convertToAbstract(e.getPoint());
					if(h==null) {
						h = new Highlight(abstractPoint.getX(), abstractPoint.getY());
						h.setDuration( highlightDuration );
						h.setWidth( paintable.getWidth()*.1 );
						h.setHeight( paintable.getHeight()*.1 );
						indicatedHighlight = h;
						draggedCorner = Corner.TOPLEFT;
						highlights.add(h);
					} else {
						draggedCorner = getCorner( h, abstractPoint );
					}
					draggedHighlight = h;
					updatePath();
				} catch(NoninvertibleTransformException e2) {
					e2.printStackTrace();
				}
			}
			
			Corner getCorner(Highlight h,Point2D p) {
				AffineTransform transform = getTransform();
				double det = transform.getDeterminant();
				double k = 10/Math.sqrt(det);
				Point2D topLeft = h.getTopLeft();
				Point2D topRight = h.getTopRight();
				Point2D bottomLeft = h.getBottomLeft();
				Point2D bottomRight = h.getBottomRight();
				double tld = topLeft.distance(p);
				double trd = topRight.distance(p);
				double bld = bottomLeft.distance(p);
				double brd = bottomRight.distance(p);
				if( tld<k && tld<trd && tld<bld && tld<brd) {
					return Corner.TOPLEFT;
				} else if( trd<k && trd<tld && trd<bld && trd<brd) {
					return Corner.TOPRIGHT;
				} else if( bld<k && bld<tld && bld<trd && bld<brd) {
					return Corner.BOTTOMLEFT;
				} else if( brd<k && brd<tld && brd<trd && brd<bld) {
					return Corner.BOTTOMRIGHT;
				}
				return null;
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				mouseMoved(e);
				draggedHighlight = null;
				repaint();
			}
			
			Point2D convertToAbstract(Point p) throws NoninvertibleTransformException {
				AffineTransform transform = getTransform();
				Point2D point = transform.createInverse().transform(new Point2D.Double(p.getX(), p.getY()), null);
				return point;
			}
			
			Highlight getHighlight(MouseEvent e) {
				try {
					Point2D point = convertToAbstract(e.getPoint());
					for(int a = highlights.size()-1; a>=0; a--) {
						Highlight h = highlights.get(a);
						Shape shape = h.getOutline();
						if(shape.contains(point))
							return h;
					}
				} catch(NoninvertibleTransformException e2) {
					e2.printStackTrace();
				}
				return null;
			}
		};
		
		public OverviewPanel() {
			setPreferredSize(new Dimension(300, 225));
			addMouseListener(mouseListener);
			addMouseMotionListener(mouseListener);
		}
		
		AffineTransform getTransform() {
			Dimension paintableSize = new Dimension( paintable.getWidth(), paintable.getHeight() );
			Dimension mySize = new Dimension( getWidth(), getHeight() );
			Dimension scaledSize = Dimension2D.scaleProportionally(paintableSize, mySize);
			AffineTransform t = TransformUtils.createAffineTransform(
					0, 0,
					paintableSize.getWidth(), 0,
					0, paintableSize.getHeight(),
					getWidth()/2-scaledSize.width/2, getHeight()/2-scaledSize.height/2,
					getWidth()/2+scaledSize.width/2, getHeight()/2-scaledSize.height/2,
					getWidth()/2-scaledSize.width/2, getHeight()/2+scaledSize.height/2 );
			return t;
		}
		
		Font font = new Font("Verdana", 0, 12);
		BufferedImage scaledBackground;
		@Override
		protected void paintComponent(Graphics g0) {
			super.paintComponent(g0);
			
			Graphics2D g = (Graphics2D)g0;
			AffineTransform origTransform = g.getTransform();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
			g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			
			AffineTransform t = getTransform();
		
			g.transform(t);
			Shape clip = g.getClip();
			g.clipRect(0, 0, paintable.getWidth(), paintable.getHeight());
			paintable.paint(g);
			g.setClip(clip);
			
			g.setTransform(origTransform);
			if(path!=null) {
				g.setColor(new Color(255,0,0,35));
				g.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 10, new float[] {4, 4}, 0));
				g.draw( t.createTransformedShape(path) );
			}
			g.transform(t);
			
			
			g.setStroke(new BasicStroke(2));
			for(int a = 0; a<highlights.size(); a++) {
				Highlight h = highlights.get(a);
				Shape shape = h.getOutline();
				g.setColor(Color.black);
				g.draw(shape);
				
				if(h==indicatedHighlight) {
					GeneralPath arrow = new GeneralPath();
					double i = h.getWidth()*.27;
					double k = h.getWidth()/20.0;
					double j = h.getHeight()/3.0;
					double m = h.getHeight()/20.0;
					arrow.moveTo( (float)(h.getCenterX()-i-k), (float)(h.getCenterY()+h.getHeight()/2-m ));
					arrow.lineTo( (float)(h.getCenterX()-i-k), (float)(h.getCenterY()-h.getHeight()/2+j+m ));
					arrow.lineTo( (float)(h.getCenterX()-i-3*k), (float)(h.getCenterY()-h.getHeight()/2+j+m ));
					arrow.lineTo( (float)(h.getCenterX()-i), (float)(h.getCenterY()-h.getHeight()/2+m ));
					arrow.lineTo( (float)(h.getCenterX()-i+3*k), (float)(h.getCenterY()-h.getHeight()/2+j+m ));
					arrow.lineTo( (float)(h.getCenterX()-i+k), (float)(h.getCenterY()-h.getHeight()/2+j+m ));
					arrow.lineTo( (float)(h.getCenterX()-i+k), (float)(h.getCenterY()+h.getHeight()/2-m ));
					arrow.closePath();
					arrow.transform( AffineTransform.getRotateInstance(h.getAngle(), h.getCenterX(), h.getCenterY()));
					g.setColor(new Color(0,175,45,255));
					g.fill(arrow);
					
					String id = Integer.toString(a+1);
					TextLayout textLayout = new TextLayout( id, font, g.getFontRenderContext());
					GeneralPath textShape = new GeneralPath(textLayout.getOutline(new AffineTransform()));
					Rectangle2D textBounds = ShapeBounds.getBounds(textShape);
					Dimension newSize = Dimension2D.scaleProportionally(new Dimension( (int)(textBounds.getWidth()+1), (int)(textBounds.getHeight()+1) ), 
							new Dimension( (int)(h.getWidth()/3+1), (int)(h.getHeight()/3+1) ));
					AffineTransform textTransform = TransformUtils.createAffineTransform(
							textBounds.getMinX(), textBounds.getMinY(),
							textBounds.getMaxX(), textBounds.getMinY(),
							textBounds.getMinX(), textBounds.getMaxY(),
							h.getCenterX()-newSize.width*.65, h.getCenterY()-newSize.height/2,
							h.getCenterX()+newSize.width*.65, h.getCenterY()-newSize.height/2,
							h.getCenterX()-newSize.width*.65, h.getCenterY()+newSize.height/2 );
					textShape.transform(textTransform);
					textShape.transform( AffineTransform.getRotateInstance(h.getAngle(), h.getCenterX(), h.getCenterY()));
					g.setColor(new Color(165,0,160,255));
					g.fill(textShape);
				}
			}
		}
	}
	
	class PreviewPanel extends BufferedAnimationPanel {
		private static final long serialVersionUID = 1L;
		
		PropertyChangeListener timeListener = new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent e) {
				refresh();
			}
		};
		
		BufferedImage backgroundPatternImage;
		
		PreviewPanel() {
			setPreferredSize(new Dimension(600, 450));
			controller.addPropertyChangeListener( AnimationController.TIME_PROPERTY, timeListener);
			setBorder(new PartialLineBorder(Color.gray, new Insets(0, 1, 0, 1)));
			backgroundPatternImage = ImageLoader.createImage( SwivelPathAnimationDemo.class.getResource("Plimp_by_mickleness.png") );
		}
		
		
		
		@Override
		protected void paintAnimation(Graphics2D g,int width, int height) {
			float time = controller.getTime()/controller.getDuration();
			paint( (Graphics2D)g, width, height, time);
		}
		
		private void paint(Graphics2D g,int width,int height,float time) {
			g = (Graphics2D)g.create();
			if(swivelPathAnimation==null) {
				g.setFont(new Font("Verdana", 0, 14));
				g.setColor(Color.darkGray);
				Rectangle2D r = g.getFontMetrics().getStringBounds(emptyText, g);
				g.drawString(emptyText, 
						(float)(width/2-r.getWidth()/2), 
						(float)(height/2+r.getHeight()/2) );
			} else {
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
				AffineTransform transform = swivelPathAnimation.getTransform( time, width, height );
				
				paintBackgroundPatternImage(g, backgroundPatternImage, .3, transform, time, width, height);
				
				g.transform(transform);
				paintable.paint(g);
			}
			g.dispose();
		}
		
		private void paintBackgroundPatternImage(Graphics2D g,
				BufferedImage img, double scale,
				AffineTransform transform,double time,int width,int height) {
			g = (Graphics2D)g.create();
			AffineTransform patternTx = new AffineTransform();
			
			patternTx.translate(width/2, height/2);
			patternTx.scale(scale, scale);
			patternTx.rotate(Math.PI/4);
			patternTx.concatenate(transform);
			
			g.transform(patternTx);
			Rectangle2D paintR = new Rectangle2D.Double(
					0, time*img.getHeight()*100,
					img.getWidth(), 
					img.getHeight() );
			Paint checkerPaint = new TexturePaint(img, paintR);
			g.setPaint(checkerPaint);
			
			try {
				AffineTransform inverse = patternTx.createInverse();
				Rectangle r = ShapeBounds.getBounds(new Rectangle(0,0,width,height), inverse).getBounds();
				g.fill(r);
			} catch(NoninvertibleTransformException e) {
				e.printStackTrace();
			}
			
			g.dispose();
		}
	}
	
	Paintable paintable = new EmptyPaintable(400,300);
	List<Highlight> highlights = new ArrayList<Highlight>();
	Shape path;
	OverviewPanel overviewPanel = new OverviewPanel();
	JPanel overviewContainer = new JPanel(new GridBagLayout());
	JPanel overviewControls = new JPanel(new GridBagLayout());
	JButton clearButton = new JButton("Clear");
	JButton helpButton = new JButton("Help");
	JButton exportButton = new JButton("Export");
	AnimationController controller = new AnimationController();
	PreviewPanel previewPanel = new PreviewPanel();
	SwivelPathAnimation swivelPathAnimation = null;

	String emptyText = "Empty";
	CardLayout cardLayout = new CardLayout();
	JPanel leftSide = new JPanel(cardLayout);
	JPanel rightSide = new JPanel(new GridBagLayout());
	JSplitPane splitPane = new JSplitPane(  JSplitPane.HORIZONTAL_SPLIT, leftSide, rightSide);
	String[] textBlurbs;
	JLabel blurbLabel = new JLabel(" Blurbs: ");
	JSpinner blurbSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
	JFancyBox help;
	String helpText = "This is a demonstration of a visual effect used to iterate over small graphics on a large canvas.\n\n"+
	"The broad goal of this animation is to present data/graphics in a way that engages viewers more than a simple scrolling list.\n\n"+
	"The panel on the left is an overview of the entire canvas, and the panel on the right is your animation.\n\n" +
	"By default this app grabs the 35 most recent posts from FML.com and arranges them at different angles. You can also drag and drop images onto the left panel to pan around an image instead of blocks of text.\n\n"+
	"The animation is composed of a series of highlights. These are depicted in the overview panel as a rectangle. You can click anywhere in the overview panel to create a new highlight. The animation simply swivels from one highlight to another. You can resize a highlight by dragging the corner, or drag with the shift key to rotate a highlight.\n\n"+
	"To get a better feel for how to manipulate/track highlights, try lowering the number of blurbs to about 5 or 10. The default (35) makes for a great demo movie, but is too cluttered to easily edit.\n\n"+
	"This app is just a technical proof-of-concept; it is not a finished or marketable product. All source code is freely available inside this app.";
	
	public SwivelPathAnimationDemo(RootPaneContainer rpc) {
		textBlurbs = createBlurbs();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					blurbSpinner.setModel(new SpinnerNumberModel(textBlurbs.length, 1, textBlurbs.length, 1));
					blurbLabel.setEnabled(true);
					blurbSpinner.setEnabled(true);
					setTextBlurbCount(textBlurbs.length);
				} catch(Exception e) {
					emptyText = "Error";
					e.printStackTrace();
					repaint();
				}
			}
		});
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1; c.weighty = 1;
		c.gridx = 0; c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		add(splitPane, c);
		
		JComponent loadingPanel = new JPanel(new GridBagLayout());
		c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0;
		c.weightx = 1; c.weighty = 1;
		JComponent progress = new JThrobber();
		progress.setPreferredSize(new Dimension(64, 64));
		loadingPanel.add( progress, c);
		
		leftSide.add(loadingPanel, "loading");
		leftSide.add(overviewContainer, "overview");
		
		c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0;
		c.weightx = 1; c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		overviewContainer.add(overviewPanel, c);
		rightSide.add(previewPanel, c);
		c.gridy++;
		c.weighty = 0; c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;
		overviewContainer.add(overviewControls, c);
		rightSide.add(controller, c);
		
		c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0;
		c.insets = new Insets(3, 3, 3, 3);
		overviewControls.add(helpButton, c);
		c.weightx = 0; c.weighty = 1;
		c.gridx++;
		overviewControls.add(clearButton, c);
		c.gridx++;
		overviewControls.add(blurbLabel, c);
		c.gridx++;
		overviewControls.add(blurbSpinner, c);
		c.gridx++;
		overviewControls.add(exportButton, c);
		
		blurbLabel.setEnabled(false);
		blurbSpinner.setEnabled(false);
		
		clearButton.setToolTipText("Remove all highlights.");
		exportButton.setToolTipText("Export this animation as a QuickTime movie.");
		helpButton.setToolTipText("Show instructions for this demo.");
		
		clearButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				clear();
			}
		});

		helpButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				help();
			}
		});

		exportButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				export();
			}
		});
		
		new DropTarget(overviewPanel, new DropTargetListener() {

			public void dragEnter(DropTargetDragEvent e) {}

			public void dragExit(DropTargetEvent e) {}

			public void dragOver(DropTargetDragEvent e) {
				e.acceptDrag(DnDConstants.ACTION_COPY);
			}

			public void drop(DropTargetDropEvent e) {
				e.acceptDrop(DnDConstants.ACTION_COPY);
				Transferable transferable = e.getTransferable();
				boolean success = false;
				try {
					if(transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
						Image img = (Image)transferable.getTransferData(DataFlavor.imageFlavor);
						setImage(img);
					} else if(transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
						@SuppressWarnings("unchecked")
						List<File> files = (List<File>)transferable.getTransferData(DataFlavor.javaFileListFlavor);
						if(files!=null && files.size()>=1) {
							File file = files.get(0);
							Image img = Toolkit.getDefaultToolkit().createImage(file.toURI().toURL());
							setImage(img);
						}
					}
					success = true;
				} catch(Exception e2) {
					e2.printStackTrace();
				} finally {
					e.dropComplete(success);
				}
			}
			
			private void setImage(Image img) {
				BufferedImage bi = ImageLoader.createImage(img);
				paintable = new BufferedImagePaintable(bi);
				blurbLabel.setEnabled(false);
				blurbSpinner.setEnabled(false);
				clear();
				repaint();
			}

			public void dropActionChanged(DropTargetDragEvent e) {}
		});
		
		blurbSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				setTextBlurbCount( ((Number)blurbSpinner.getValue()).intValue() );
			}
		});
		
		help = new JFancyBox(rpc, helpText);
	}
	
	protected String[] createBlurbs() {
		return new String[] {
				"There is a theory which states that if ever anybody discovers exactly what the universe is for and why it is here, it will instantly disappear and be replaced by somethign even more bizarre and inexplicable. There is another theory which states that this has already happened. - Douglas Adams",
				"If your morals make your life dreary, depend upon it: they are wrong. - Robert Louis Stevenson",
				"The life which is unexamined is not worth living. - Plato",
				"Life is too important to be taken seriously. - Oscar Wilde",
				"We live in a world of many illusions and much of human belief and behavior is ritualized nonsense. - Wes Nisker",
				"There are only two ways to live your life. One is as though nothing is a miracle. The other is as though everything is a miracle. - Albert Einstein",
				"To a large degree 'reality' is whatever the people who are around at the time agree to. - Milton H. Miller",
				"Success is a little like wrestling a gorilla. You don't quit when you're tired - you quit when the gorilla is tired. - Robert Strauss",
				"Depend upon it, Sir, when a man knows he is to be hanged in a fortnight, it concentrates his mind wonderfully. - Samuel Johnson",
				"The art of being wise is the art of knowing what to overlook. - William James",
				"Perhaps in time the so-called Dark Ages will be thought of as including our own. - G.C. Lichtenberg",
				"Good people are good because they've come to wisdom through failure, We get very little wisdom from success, you know.",
				"If we could read the secret history of our enemies, we would find in each man's life a sorrow and a suffering enough to disarm all hostility. - Henry Wadsworth Longfellow",
				"I cannot give you the formula for success, but I can give you the formula for failure - try to please everybody. - Herbert Bayard Swope",
				"People seem not to see that their opinion of the world is also a confession of character. - Ralph Waldo Emerson",
				"We don't remember days, we remember moments. - Cesare Pavese",
				"Gratitude is not only the greatest of virtues, but the mother of all the rest. - Cicero",
				"You're searching, Joe, for things that don't exist; I mean beginnings. Ends and beginnings - there are no such things. There are only middles. - Robert Frost",
				
				
		};
	}

	public void export() {
		Frame frame = (Frame)SwingUtilities.getWindowAncestor(this);
		FileDialog fd = new FileDialog( frame, "Export", FileDialog.SAVE );
		fd.setFile("Exported Movie.mov");
		fd.setFilenameFilter(new SuffixFilenameFilter("mov"));
		fd.pack();
		fd.setLocationRelativeTo(null);
		fd.setVisible(true);
		if(fd.getFile()==null)
			return;
		final int fps = 24;
		final File dest = new File(fd.getDirectory()+fd.getFile());
		final int frameCount = (int)(controller.getDuration()*fps);
		final ProgressMonitor monitor = new ProgressMonitor(this, "Exporting", "Writing Frames...", 0, frameCount);
		Thread exportThread = new Thread("Export Thread") {
			@Override
			public void run() {
				JPEGMovWriter writer = null;
				try {
					dest.createNewFile();
					writer = new JPEGMovWriter(dest, .95f);
					int width = previewPanel.getWidth();
					int height = previewPanel.getHeight();
					BufferedImage image = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB);
					for(int a = 0; a<frameCount; a++) {
						monitor.setProgress(a);
						monitor.setNote("Writing Frame "+(a+1));
						if(monitor.isCanceled())
							throw new UserCancelledException();
						Graphics2D g = image.createGraphics();
						g.setColor(Color.white);
						g.fillRect(0,0,width,height);
						g.setColor(Color.black);
						//vital for performance:
						g.clipRect(0, 0, width, height);
						float time = ((float)a)/((float)frameCount);
						previewPanel.paint(g, width, height, time);
						g.dispose();
						writer.addFrame( 1f/(fps), image, null);
					}
					monitor.close();
				} catch(Exception e) {
					e.printStackTrace();
				} finally {
					if(writer!=null) {
						try {
							writer.close(true);
						} catch(Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		};
		exportThread.start();
		
	}
	
	void setTextBlurbCount(int i) {
		String[] myBlurbs = new String[ Math.min(i, textBlurbs.length) ];
		System.arraycopy( textBlurbs, 0, myBlurbs, 0, myBlurbs.length);
		
		TextClusterPaintable wcp = new TextClusterPaintable(
				myBlurbs,
				new Font("Verdana", 0, 14),
				7, 18,
				250, 0);
		
		paintable = wcp;
		highlights.clear();
		for(int a = 0; a<wcp.getCount(); a++) {
			TextBoxPaintable tx = wcp.getPaintable(a);
			Rectangle2D bounds = wcp.getTransformedBounds(a);
			Highlight h = new Highlight( bounds.getCenterX(), bounds.getCenterY() );
			h.setWidth( tx.getWidth()+10 );
			h.setHeight( tx.getHeight()+10 );
			h.setAngle( TransformUtils.getRotationAngle(wcp.getTransform(a)) );
			
			h.setDuration( tx.getText().length()*.025 );
			highlights.add(h);
		}
		updatePath();
		cardLayout.show(leftSide, "overview");
	}
	
	void help() {
		help.setVisible(true);
	}
	
	void clear() {
		highlights.clear();
		updatePath();
	}
	
	float swivelDuration = 2;
	float highlightDuration = 4;
	protected void updatePath() {
		if(highlights.size()>=1) {
			swivelPathAnimation = new SwivelPathAnimation( highlights.toArray(new Highlight[highlights.size()]), 2 );
			controller.setDuration( swivelPathAnimation.getDuration() );
			path = swivelPathAnimation.getPath();
		} else {
			path = null;
			swivelPathAnimation = null;
			controller.setDuration( 1 );
		}
		overviewPanel.repaint();
		previewPanel.refresh();
	}
}
