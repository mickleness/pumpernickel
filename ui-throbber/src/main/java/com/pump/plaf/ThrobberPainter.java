package com.pump.plaf;

import java.awt.*;

public abstract class ThrobberPainter {
    public void paint(Graphics2D g, Float fraction, int circleDiameter, Color foreground) {
        if (fraction == null) {
            int p = getPreferredPeriod();
            int i = (int)(System.currentTimeMillis() % p);
            fraction = ((float) i) / ((float) p);
        } else if (fraction < 0 || fraction > 1) {
            throw new IllegalArgumentException(
                    "fraction (" + fraction + ") must be within [0, 1]");
        }

        g = (Graphics2D) g.create();
        Dimension prefSize = getPreferredSize();
        double scale = ((double)circleDiameter) / Math.min(prefSize.width, prefSize.height);
        g.scale(scale, scale);

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL,
                RenderingHints.VALUE_STROKE_PURE);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION,
                RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);

        doPaint(g, fraction, foreground);
    }

    protected abstract void doPaint(Graphics2D g, float fraction, Color foreground);

    /**
     * Return the default recommended period of this animation in milliseconds.
     */
    public abstract int getPreferredPeriod();

    public abstract Dimension getPreferredSize();

    public abstract Color getPreferredForeground();

    public abstract int getPreferredRepaintInterval();
}
