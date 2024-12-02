package com.pump.plaf;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

/**
 * This is an Icon implementation of a {@link ThrobberPainter}.
 * <p>
 * This object automatically sets up a <code>javax.swing.Timer</code>
 * that continually repaints components until they are no longer showing.
 */
public class ThrobberIcon implements Icon {

    /**
     * Return either an AquaThrobberPainter (on Mac) or a CircularThrobberPainter.
     */
    public static ThrobberPainter getDefaultThrobberPainter() {
        if (isAqua()) {
            return new AquaThrobberPainter();
        }
        return new CircularThrobberPainter();
    }

    static boolean isAqua() {
        return "Aqua".equals(UIManager.getLookAndFeel().getID());
    }

    protected final int width;
    protected final int height;
    protected final ThrobberPainter painter;

    /**
     * Create a new ThrobberIcon that uses either an AquaThrobberPainter or a CircularThrobberPainter.
     */
    public ThrobberIcon() {
        this(getDefaultThrobberPainter());
    }

    /**
     * Create a new ThrobberIcon.
     *
     * @param painter the ThrobberPaint this icon renders.
     */
    public ThrobberIcon(ThrobberPainter painter) {
        this(painter, null);
    }

    /**
     * Create a new ThrobberIcon.
     *
     * @param painter the ThrobberPaint this icon renders.
     * @param size the size of this ThrobberIcon. If this is left null then
     *             {@link ThrobberPainter#getPreferredSize()} is used.
     */
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

        setupRepaints(c);
    }

    private static final String PROPERTY_REPAINT_TIMER = ThrobberIcon.class.getName() + "#repaintTimer";

    /**
     * This makes sure a javax.swing.Timer is running that will repaint the given Component
     * every 20ms until it is no longer visible.
     * <p>
     * As long as this method is called within a <code>paint(..)</code> method: this Timer does
     * not need to handle restarting itself. The Component in question will repaint when
     * it becomes visible again, and that will call this method to make sure there is an
     * active Timer.
     * </p>
     */
    static void setupRepaints(Component c) {
        if (!(c instanceof JComponent jc))
            return;
        Timer timer = (Timer) jc.getClientProperty(PROPERTY_REPAINT_TIMER);
        if (timer == null) {
            timer = new Timer(10, null);
            jc.putClientProperty(PROPERTY_REPAINT_TIMER, timer);
            timer.addActionListener(e -> {
                if (jc.isShowing()) {
                    jc.repaint();
                } else {
                    stopRepaints(jc);
                }
            });
            timer.start();
        }
    }

    public static void stopRepaints(JComponent jc) {
        Timer timer = (Timer) jc.getClientProperty(PROPERTY_REPAINT_TIMER);
        if (timer != null) {
            timer.stop();
            jc.putClientProperty(PROPERTY_REPAINT_TIMER, null);
        }
    }
}
