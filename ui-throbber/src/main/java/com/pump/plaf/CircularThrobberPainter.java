package com.pump.plaf;

import java.awt.*;

public class CircularThrobberPainter extends ThrobberPainter {
    @Override
    protected void doPaint(Graphics2D g, float k, Color foreground) {
        float strokeWidth = 2;
        float radius = 9;

        strokeWidth = Math.min(strokeWidth, radius);

        // this is not a perfect replica of Material's indeterminate arc...
        // but it's good enough for now.

        long period = 1500;
        double startAngle = 360
                * Math.pow(.5 + Math.sin(k * Math.PI - Math.PI / 2.0) / 2.0, 2)
                + 1 * k * 360;

        k = (k + .5f) % 1f;
        double extent = 50 + 220 * Math.pow(
                .5 + Math.sin(2 * k * Math.PI - 3 * Math.PI / 2.0) / 2.0, 1);

        CircularProgressBarUI.paintArc(g, foreground,
                10, 10,
                startAngle, extent, radius
                - strokeWidth / 2, strokeWidth);
    }

    @Override
    public int getPreferredPeriod() {
        return 1500;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(20, 20);
    }

    @Override
    public Color getPreferredForeground() {
        return Color.gray;
    }

    @Override
    public int getPreferredRepaintInterval() {
        return 1000/24;
    }
}
