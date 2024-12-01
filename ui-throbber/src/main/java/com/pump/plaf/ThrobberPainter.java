package com.pump.plaf;

import java.awt.*;

public interface ThrobberPainter {

    void paint(Graphics2D g, Rectangle bounds, Float fraction, Color foreground);

    /**
     * Return the default recommended period of this animation in milliseconds.
     */
    int getPreferredPeriod();

    Dimension getPreferredSize();

    Color getPreferredForeground();


    static float getCurrentFraction(int period) {
        int i = (int)(System.currentTimeMillis() % period);
        return ((float) i) / ((float) period);
    }
}
