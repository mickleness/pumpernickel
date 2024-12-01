package com.pump.plaf;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 * This always paints a throbber at its preferred size, but it is painted
 * through an AffineTransform to accommodate the bounds provided.
 */
public abstract class ScaledThrobberPainter implements ThrobberPainter {
    @Override

    public void paint(Graphics2D g, Rectangle bounds, Float fraction, Color foreground) {
        if (fraction == null) {
            fraction = ThrobberPainter.getCurrentFraction(getPreferredPeriod());
        } else if (fraction < 0 || fraction > 1) {
            throw new IllegalArgumentException(
                    "fraction (" + fraction + ") must be within [0, 1]");
        }

        g = (Graphics2D) g.create();

        Dimension prefSize = getPreferredSize();
        double scale = Math.min(
                ((double)bounds.width) / ((double)prefSize.width),
                ((double)bounds.height) / ((double)prefSize.height)
        );

        Rectangle2D oldRect = new Rectangle2D.Double(0, 0, prefSize.width, prefSize.height);
        Rectangle2D newRect = new Rectangle2D.Double(bounds.getCenterX() - scale * prefSize.width / 2,
                bounds.getCenterY() - scale * prefSize.height / 2,
                scale * prefSize.width, scale * prefSize.height);
        double scaleX = newRect.getWidth() / oldRect.getWidth();
        double scaleY = newRect.getHeight() / oldRect.getHeight();
        double translateX = -oldRect.getX() * scaleX + newRect.getX();
        double translateY = -oldRect.getY() * scaleY + newRect.getY();
        g.transform(new AffineTransform(scaleX, 0, 0, scaleY, translateX, translateY));

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_PURE);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

        doPaint(g, fraction, foreground);
    }

    /**
     * Paint this throbber at its preferred size, positioned at 0,0.
     */
    protected abstract void doPaint(Graphics2D g, float fraction, Color foreground);
}
