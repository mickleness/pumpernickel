package com.pump.awt;

import java.awt.*;

/**
 * Static methods related to Colors.
 */
public class ColorUtils {

    /**
     * Tween between two colors.
     *
     * @param c1 the first color to tween between
     * @param c2 the second color to tween between
     * @param f a value between [0,1], where 0 will return c1, and 1 will return c2.
     *          A value of .25 will return a value that is 25% of the way between c1 and c2.
     * @return a Color object that is between c1 and c2.
     */
    public static Color tween(Color c1, Color c2, double f) {
        if (f <= 0)
            return c1;
        if (f >= 1)
            return c2;
        int r = (int) (c1.getRed() * (1 - f) + f * c2.getRed());
        int g = (int) (c1.getGreen() * (1 - f) + f * c2.getGreen());
        int b = (int) (c1.getBlue() * (1 - f) + f * c2.getBlue());
        int a = (int) (c1.getAlpha() * (1 - f) + f * c2.getAlpha());
        return new Color(r, g, b, a);
    }
}
