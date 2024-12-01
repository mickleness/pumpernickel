package com.pump.plaf;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class ThrobberIcon implements Icon {

    protected final int width;
    protected final int height;
    protected final ThrobberPainter painter;

    public ThrobberIcon(ThrobberPainter painter) {
        this(painter, null);
    }

    public ThrobberIcon(ThrobberPainter painter, Dimension size) {
        this.painter = Objects.requireNonNull(painter);

        if (size == null) {
            size = painter.getPreferredSize();
        }
        width = size.width;
        height = size.height;
    }

    @Override
    public int getIconHeight() {
        return height;
    }

    @Override
    public int getIconWidth() {
        return width;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Rectangle r = new Rectangle(x, y, getIconWidth(), getIconHeight());
        painter.paint((Graphics2D)g, r, null, c.getForeground());
    }
}
