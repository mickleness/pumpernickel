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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;
import javax.swing.plaf.basic.BasicSliderUI;

import com.pump.audio.WavReader;
import com.pump.geom.ShapeBounds;
import com.pump.geom.TransformUtils;
import com.pump.math.function.Function;
import com.pump.math.function.PolynomialFunction;

/**
 * This SliderUI renders a waveform over the track of the slider, and the thumb
 * is rendered as a one-pixel line.
 * <p>
 * Currently this only supports WAV files.
 */
public class WaveformSliderUI extends BasicSliderUI {

	public static Shape createWaveform(URL source) throws IOException {
		final GeneralPath path = new GeneralPath();
		final AudioFormat audioFormat;
		long totalDataSize;
		try (InputStream in = source.openStream()) {

			WavReader r = new WavReader(in);
			totalDataSize = r.skip();
			audioFormat = r.getAudioFormat();
		}

		path.moveTo(0, 0);
		try (InputStream in = source.openStream()) {
			int sampleSizeInBits = audioFormat.getSampleSizeInBits();
			if (!(sampleSizeInBits == 8 || sampleSizeInBits == 16))
				throw new IOException("sampleSizeInBits = " + sampleSizeInBits);
			if (!(audioFormat.getEncoding().equals(Encoding.PCM_SIGNED) || audioFormat
					.getEncoding().equals(Encoding.PCM_UNSIGNED)))
				throw new IOException("sunpported encoding \""
						+ audioFormat.getEncoding() + "\"");
			final int sampleSize = sampleSizeInBits / 8;
			final boolean isSigned = audioFormat.getEncoding().equals(
					Encoding.PCM_SIGNED);
			final int minSampleValue = isSigned ? (int) (-Math.pow(256,
					sampleSize)) / 2 : 0;
			final int maxSampleValue = isSigned ? (int) (Math.pow(256,
					sampleSize)) / 2 - 1
					: (int) (Math.pow(256, sampleSize)) - 1;

			// we're going to assume 1000 pixels of data is plenty
			final Function sampleIndexToPixel = PolynomialFunction.createFit(0,
					0, totalDataSize / audioFormat.getChannels(), 1000);

			WavReader r = new WavReader(in) {

				int currentX = 0;
				int currentMaxY = 0;
				long sampleIndex = 0;

				@Override
				protected void processSamples(byte[] sample, int offset,
						int length, int numberOfSamples) throws IOException {
					for (int a = offset; a < offset + length; a += sampleSize) {
						final int value = com.pump.audio.PCMUtils.decodeSample(
								sample, a, sampleSize, isSigned,
								audioFormat.isBigEndian());
						int range = maxSampleValue + minSampleValue;
						int z = Math.abs(value - range / 2);

						int x = (int) Math.floor(sampleIndexToPixel
								.evaluate(sampleIndex++));
						if (x == currentX) {
							currentMaxY = Math.max(currentMaxY, z);
						} else {
							path.lineTo(currentX, -currentMaxY);
							currentX = x;
							currentMaxY = z;
						}
					}
					path.lineTo(currentX, -currentMaxY);
				}

			};
			r.read();

			Rectangle unitRect = new Rectangle(0, 0, 1, 1);
			Rectangle2D bounds = ShapeBounds.getBounds(path);

			path.lineTo(bounds.getMaxX(), bounds.getMaxY());
			path.closePath();

			// manually set the height, so for really quiet sounds we show
			// sufficient extra padding
			int h = (maxSampleValue - minSampleValue) / 2;
			bounds.setFrame(bounds.getX(), -h, bounds.getWidth(), h);
			path.transform(TransformUtils.createAffineTransform(bounds,
					unitRect));
		}
		return path;
	}

	URL source;
	Shape waveform;
	boolean isDragging = false;

	/**
	 * The BasicSliderUI users a Timer to scroll the thumb to where you clicked.
	 * But in this UI we just jump straight to where the mouse is. just
	 */
	MouseInputAdapter mouseListener = new MouseInputAdapter() {

		@Override
		public void mousePressed(MouseEvent e) {
			if (!slider.isEnabled())
				return;

			if (slider.isRequestFocusEnabled())
				slider.requestFocus();

			if (!SwingUtilities.isLeftMouseButton(e))
				return;

			calculateGeometry();
			isDragging = true;
			slider.setValueIsAdjusting(true);
			adjustValue(e);

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			if (!slider.isEnabled() || !SwingUtilities.isLeftMouseButton(e))
				return;
			adjustValue(e);
			isDragging = false;
			slider.setValueIsAdjusting(false);
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			if (!slider.isEnabled() || !SwingUtilities.isLeftMouseButton(e))
				return;
			adjustValue(e);
		}

		private void adjustValue(MouseEvent e) {
			float f;
			if (slider.getOrientation() == JSlider.HORIZONTAL) {
				f = (float) (e.getX() - trackRect.x) / (float) trackRect.width;
			} else {
				f = (float) (e.getY() - trackRect.y) / (float) trackRect.height;
			}
			int range = slider.getMaximum() - slider.getMinimum();
			int value = slider.getMinimum() + (int) (range * f);
			if (value < slider.getMinimum())
				value = slider.getMinimum();
			if (value > slider.getMaximum())
				value = slider.getMaximum();
			slider.setValue(value);
		}
	};

	public WaveformSliderUI(JSlider slider, URL source) throws IOException {
		super(slider);
		this.source = source;
		waveform = createWaveform(source);
	}

	/**
	 * Returns true if the user is dragging the slider.
	 *
	 * @return true if the user is dragging the slider
	 * @since 1.5
	 */
	@Override
	public boolean isDragging() {
		return isDragging;
	}

	public URL getSource() {
		return source;
	}

	@Override
	public void paintTrack(Graphics g0) {
		Graphics2D g = (Graphics2D) g0.create();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor(slider.getForeground());
		Rectangle unitRect = new Rectangle(0, 0, 1, 1);
		g.transform(TransformUtils.createAffineTransform(unitRect, trackRect));
		g.fill(waveform);
		g.dispose();
	}

	/**
	 * This scratch image is used to render the track and the xor'ed thumb. The
	 * XOR composite only works if the background is transparent.
	 */
	BufferedImage scratch;

	public void paint(Graphics g, JComponent c) {
		recalculateIfInsetsChanged();
		recalculateIfOrientationChanged();
		Rectangle clip = g.getClipBounds();

		boolean clean = false;
		if (scratch == null || scratch.getWidth() != c.getWidth()
				|| scratch.getHeight() != c.getHeight()) {
			scratch = new BufferedImage(c.getWidth(), c.getHeight(),
					BufferedImage.TYPE_INT_ARGB);
			clean = true;
		}
		Graphics2D g2 = scratch.createGraphics();
		if (!clean) {
			g2.setComposite(AlphaComposite.Clear);
			g2.fillRect(0, 0, c.getWidth(), c.getHeight());
			g2.setComposite(AlphaComposite.SrcOver);
		}

		if (!clip.intersects(trackRect) && slider.getPaintTrack())
			calculateGeometry();

		if (slider.getPaintTrack() && clip.intersects(trackRect)) {
			paintTrack(g2);
		}
		// this UI doesn't paint labels or ticks
		if (slider.hasFocus() && clip.intersects(focusRect)) {
			paintFocus(g2);
		}
		if (clip.intersects(thumbRect)) {
			paintThumb(g2);
		}
		g2.dispose();
		g.drawImage(scratch, 0, 0, null);
	}

	@Override
	public void paintThumb(Graphics g0) {
		Graphics2D g = (Graphics2D) g0.create();
		g.setComposite(AlphaComposite.Xor);
		g.setColor(slider.getForeground());
		if (slider.getOrientation() == JSlider.HORIZONTAL) {
			int x = thumbRect.x + thumbRect.width / 2;
			g.drawLine(x, trackRect.y, x, trackRect.height);
		} else {
			int y = thumbRect.y + thumbRect.height / 2;
			g.drawLine(trackRect.x, y, trackRect.width, y);
		}
		g.dispose();
	}

	@Override
	public void paintFocus(Graphics g) {
		// do nothing, we don't paint focus
	}

	@Override
	public Dimension getPreferredSize(JComponent c) {
		return new Dimension(100, 20);
	}

	@Override
	public Dimension getMinimumSize(JComponent c) {
		return getPreferredSize(c);
	}

	@Override
	public Dimension getMaximumSize(JComponent c) {
		return getMaximumSize(c);
	}

	@Override
	protected void installListeners(JSlider slider) {
		super.installListeners(slider);
		slider.removeMouseListener(trackListener);
		slider.removeMouseMotionListener(trackListener);
		slider.addMouseListener(mouseListener);
		slider.addMouseMotionListener(mouseListener);
		slider.setForeground(Color.darkGray);
	}

	@Override
	protected void uninstallListeners(JSlider slider) {
		super.uninstallListeners(slider);
		slider.removeMouseListener(mouseListener);
		slider.removeMouseMotionListener(mouseListener);
	}

}