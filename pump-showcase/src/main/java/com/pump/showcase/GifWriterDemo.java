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
package com.pump.showcase;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.pump.UserCancelledException;
import com.pump.animation.ResettableAnimationReader;
import com.pump.image.gif.GifWriter;
import com.pump.inspector.InspectorGridBagLayout;
import com.pump.swing.DialogFooter;
import com.pump.swing.FileDialogUtils;
import com.pump.swing.QDialog;

public class GifWriterDemo extends JPanel {
	private static final long serialVersionUID = 1L;
	
	static class ImageAnimationReader implements ResettableAnimationReader {
		Dimension size = null;
		List<BufferedImage> images;
		int index = 0;
		double duration;

		public ImageAnimationReader(List<BufferedImage> allImages,double duration) {
			for(int a = 0; a<allImages.size(); a++) {
				BufferedImage bi = allImages.get(a);
				if(size==null) {
					size = new Dimension(bi.getWidth(), bi.getHeight());
				} else {
					size.width = Math.max(size.width, bi.getWidth());
					size.height = Math.max(size.height, bi.getHeight());
				}
			}
			if(size==null)
				throw new NullPointerException();
			if(size.width<=0)
				throw new IllegalArgumentException();
			if(size.height<=0)
				throw new IllegalArgumentException();
			this.images = allImages;
			this.duration = duration;
		}

		@Override
		public BufferedImage getNextFrame(boolean cloneImage)
				throws IOException {
			if(index>=images.size())
				return null;
			
			BufferedImage bi = new BufferedImage(size.width, size.height, BufferedImage.TYPE_INT_RGB);
			Graphics2D g = bi.createGraphics();
			BufferedImage k = images.get(index);
			g.drawImage(k, bi.getWidth()/2 - k.getWidth()/2, bi.getHeight()/2 - k.getHeight()/2, null);
			index++;
			g.dispose();
			
			return bi;
		}

		@Override
		public double getDuration() {
			return duration*images.size();
		}

		@Override
		public int getFrameCount() {
			return images.size();
		}

		@Override
		public int getLoopCount() {
			return LOOP_FOREVER;
		}

		@Override
		public double getFrameDuration() {
			return duration;
		}

		@Override
		public int getWidth() {
			return size.width;
		}

		@Override
		public int getHeight() {
			return size.height;
		}

		@Override
		public void reset() {
			index = 0;
		}
		
	}

	static class DemoAnimationReader implements ResettableAnimationReader {
		int ctr = 0;
		
		int w, h;
		Paint transparentPattern;
		
		DemoAnimationReader(int width,int height) {
			this.w = width;
			this.h = height;
			int k = 64;
			BufferedImage patternImage = new BufferedImage(k, k, BufferedImage.TYPE_INT_ARGB);
			
			Graphics2D g = patternImage.createGraphics();
			g.setColor(new Color(0x44decb));
			g.fillRect(0,0,k/2,k/2);
			g.setColor(new Color(0x35ccab));
			g.fillRect(k/2,k/2,k/2,k/2);
			g.dispose();

			transparentPattern = new TexturePaint(patternImage, new Rectangle(0,0,k,k));
		}
		
		public BufferedImage getNextFrame(boolean cloneImage)
				throws IOException {
			if(ctr==getFrameCount()) return null;
			
			BufferedImage bi = new BufferedImage(
					getWidth(),
					getHeight(),
					BufferedImage.TYPE_INT_ARGB
					);
			Graphics2D g = bi.createGraphics();
			g.setComposite(AlphaComposite.Clear);
			g.fillRect(0,0,bi.getWidth(),bi.getHeight());
			g.setComposite(AlphaComposite.SrcOver);
			g.setPaint(transparentPattern);
			g.fillRect(0,0,bi.getWidth(),bi.getHeight());
			
			g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setFont(new Font("Verdana",0,h*12/100));
			FontMetrics fm = g.getFontMetrics();
			Rectangle2D rect = fm.getStringBounds(""+ctr, g);
			
			float r = (float)(Math.sqrt(rect.getWidth()*rect.getWidth()+rect.getHeight()*rect.getHeight()))/2f;
			Ellipse2D e = new Ellipse2D.Float( bi.getWidth()/2-r, bi.getHeight()/2-r, 2*r, 2*r );
			g.setColor(new Color(0xbbccfb));
			g.fill(e);
			g.setColor(Color.black);
			g.setStroke(new BasicStroke(3));
			g.draw(e);
			
			g.drawString(""+ctr, (float)(bi.getWidth()/2-rect.getWidth()/2), (float)(bi.getHeight()/2+rect.getHeight()/2-fm.getDescent()) );
			g.dispose();
			ctr++;
			return bi;
		}

		public double getDuration() {
			return getFrameCount()*getFrameDuration();
		}

		public int getFrameCount() {
			return 200;
		}

		public int getLoopCount() {
			return LOOP_FOREVER;
		}

		public double getFrameDuration() {
			return .1;
		}

		public int getWidth() {
			return w;
		}

		public int getHeight() {
			return h;
		}

		public void reset() {
			ctr = 0;
		}
		
	}
	
	JRadioButton defaultButton = new JRadioButton("None (Autogenerated Images)");
	JRadioButton folderButton = new JRadioButton("From Folder:");
	JTextField folderField = new JTextField();
	JTextField durationField = new JTextField("2", 4);
	JButton createButton = new JButton("Create...");
	
	public GifWriterDemo() {
		InspectorGridBagLayout layout = new InspectorGridBagLayout(this);
		layout.addRow(new JLabel("GIF Frame Source:"), defaultButton, false);
		layout.addRow(null, combine(folderButton, folderField), true);
		layout.addRow(new JLabel("Frame Duration:"), combine(durationField, new JLabel("seconds")), false);
		layout.addRow(createButton, SwingConstants.CENTER, false);
		
		ButtonGroup g = new ButtonGroup();
		g.add(defaultButton);
		g.add(folderButton);
		
		defaultButton.doClick();
		
		createButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(folderButton.isSelected()) {
					File dir = new File(folderField.getText());
					write(dir);
				} else {
					writeDefault();
				}
			}
		});
	}
	
	protected void write(File dir) {
		File[] children = dir.listFiles();
		Arrays.sort(children, new Comparator<File>() {
			@Override
			public int compare(File o1, File o2) {
				return o1.getAbsolutePath().compareTo(o2.getAbsolutePath());
			}
		});
		List<BufferedImage> images = new ArrayList<>();
		for(int a = 0; a<children.length; a++) {
			if((!children[a].isDirectory()) && (!children[a].isHidden())) {
				try {
					BufferedImage bi = ImageIO.read(children[a]);
					if(bi!=null)
						images.add(bi);
				} catch(Exception e2) {
					//it's OK if we can't read some images...
				}
			}
		}
		
		if(images.size()==0) {
			Frame frame = (Frame)SwingUtilities.getWindowAncestor(GifWriterDemo.this);
			QDialog.showDialog(frame, "Error", QDialog.ERROR_MESSAGE, "No images were found.", "No images were found in the folder \""+dir.getName()+"\".", 
					null, // innerComponent, 
					null, //lowerLeftComponent, 
					DialogFooter.OK_OPTION, DialogFooter.OK_OPTION, 
					null, //dontShowKey, 
					null, //alwaysApplyKey, 
					DialogFooter.EscapeKeyBehavior.TRIGGERS_DEFAULT);
		}
		
		double duration;
		try {
			duration = Double.parseDouble(durationField.getText());
		} catch(NumberFormatException e) {
			throw new RuntimeException("The duration \""+durationField.getText()+"\" could not be parsed as a number.");
		}
		
		File destFile = getDestinationFile();
		try {
			GifWriter.write(destFile, new ImageAnimationReader(images, duration), GifWriter.ColorReduction.FROM_ALL_FRAMES);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public File getDestinationFile() {
		Frame frame = (Frame)SwingUtilities.getWindowAncestor(GifWriterDemo.this);
		File file = FileDialogUtils.showSaveDialog(frame, "Save GIF", "gif");
		if(file==null)
			throw new UserCancelledException();
		return file;
	}

	protected void writeDefault() {
		File destFile = getDestinationFile();
		try {
			GifWriter.write(destFile, new DemoAnimationReader(400, 300), GifWriter.ColorReduction.FROM_FIRST_FRAME);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private JPanel combine(JComponent... components) {
		JPanel p = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 0; c.gridy = 0; c.weightx = 0; c.weighty = 0;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0,0,0,3);
		for(int a = 0; a<components.length; a++) {
			if(a==components.length-1)
				c.weightx = 1;
			p.add(components[a], c);
			c.gridx++;
		}
		return p;
	}
}