package com.pump.plaf;

import java.awt.*;

public class CircularThrobberPainter implements ThrobberPainter {

    @Override
    public void paint(Graphics2D g, Rectangle bounds, Float k, Color foreground) {
        if (k == null)
            k = ThrobberPainter.getCurrentFraction(getPreferredPeriod());

        float radius = Math.min(bounds.width, bounds.height) / 2f;
        float strokeWidth = radius / 4;

        // this is not a perfect replica of Material's indeterminate arc...
        // but it's good enough for now.

        double startAngle = 360
                * Math.pow(.5 + Math.sin(k * Math.PI - Math.PI / 2.0) / 2.0, 2)
                + 1 * k * 360 + 30;

        k = (k + .5f) % 1f;
        double extent = 50 + 220 * Math.pow(
                .5 + Math.sin(2 * k * Math.PI - 3 * Math.PI / 2.0) / 2.0, 1);

        CircularProgressBarUI.paintArc(g, foreground,
                bounds.getCenterX(), bounds.getCenterY(),
                startAngle, extent, radius
                - strokeWidth / 2, strokeWidth, false);
    }

    @Override
    public int getPreferredPeriod() {
        return 1500;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(20, 20);
    }
}
