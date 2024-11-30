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
    public void paintIcon(Component c, Graphics g0, int x, int y) {
        Graphics2D g = (Graphics2D) g0.create();
        try {
            int size = Math.min(getIconWidth(), getIconHeight());
            int dx = (getIconWidth() - size) / 2;
            int dy = (getIconHeight() - size) / 2;
            g.translate(x + dx, y + dy);
            painter.paint(g, null, size, c.getForeground());
        } finally {
            g.dispose();
        }
    }
}
