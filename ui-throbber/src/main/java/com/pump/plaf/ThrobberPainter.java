package com.pump.plaf;

import java.awt.*;

/**
 * This paints a throbber. Apple's UX guidelines call this an
 * "asynchronous progress indicator". Material's UX guidelines call this an
 * "indeterminate circular indicator".
 * <p>
 * This is a small animated component used to indicate activity when
 * the completion time is very short OR when you don't know how to reliably
 * estimate the completion time / progress updates.
 * <p>
 * Here are some possible UI implementations:
 * <p>
 * <table summary="Sample ThrobberUIs" cellpadding="10">
 * <tr>
 * <td><img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/throbber/AquaThrobberUIx2.gif"
 * alt="Sample of AquaThrobberUI"></td>
 * <td><img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/throbber/ChasingArrowsThrobberUIx2.gif"
 * alt="Sample of ChasingArrowsThrobberUI"></td>
 * <td><img src=
 * "https://github.com/mickleness/pumpernickel/raw/master/resources/throbber/PulsingCirclesThrobberUIx2.gif"
 * alt="Sample of PulsingCirclesThrobberUI"></td>
 * </tr>
 * </table>
 * <p>
 * There are two easy ways to deploy this:
 * <li><ol>Create a JLabel and assign a {@link ThrobberIcon}.</ol>
 * <ol>Create a JProgressBar, use an indeterminate state, and (optionally) assign the
 * client property {@link CircularProgressBarUI#PROPERTY_THROBBER_PAINTER}</ol></li>
 * <h2>UX Guidelines</h2>
 * <p>
 * <a href=
 * "https://developer.apple.com/library/mac/documentation/UserExperience/Conceptual/AppleHIGuidelines/Controls/Controls.html#//apple_ref/doc/uid/TP30000359-TPXREF106"
 * >Apple's OSX Human Interface Guidelines</a> call this an
 * "Asynchronous Progress Indicator", and describe it as follows: <blockquote>An
 * asynchronous progress indicator provides feedback on an ongoing process.
 * <p>
 * Asynchronous progress indicators are available in Interface Builder. In the
 * Attributes pane of the inspector, select Spinning for the style and be sure
 * the Indeterminate checkbox is selected. To create an asynchronous progress
 * indicator using AppKit programming interfaces, use the NSProgressIndicator
 * class with style NSProgressIndicatorSpinningStyle.
 * <p>
 * <b>Appearance and Behavior</b>
 * <p>
 * The appearance of the asynchronous progress indicator is provided
 * automatically. The asynchronous progress indicator always spins at the same
 * rate.
 * <p>
 * <b>Guidelines</b>
 * <p>
 * Use an asynchronous progress indicator when space is very constrained, such
 * as in a text field or near a control. Because this indicator is small and
 * unobtrusive, it is especially useful for asynchronous events that take place
 * in the background, such as retrieving messages from a server.
 * <p>
 * If the process might change from indeterminate to determinate, start with an
 * indeterminate progress bar. You don't want to start with an asynchronous
 * progress indicator because the determinate progress bar is a different shape
 * and takes up much more space. Similarly, if the process might change from
 * indeterminate to determinate, use an indeterminate progress bar instead of an
 * asynchronous progress indicator, because it is the same shape and size as the
 * determinate progress bar.
 * <p>
 * In general, avoid supplying a label. Because an asynchronous progress
 * indicator typically appears when the user initiates a process, a label is not
 * usually necessary. If you decide to provide a label that appears with the
 * indicator, create a complete or partial sentence that briefly describes the
 * process that is occurring. You should use sentence-style capitalization (for
 * more information on this style, see "Capitalizing Labels and Text") and you
 * can end the label with an ellipsis (...) to emphasize the ongoing nature of
 * the processing.</blockquote>
 * <p>
 * Alternatively, <a href="http://en.wikipedia.org/wiki/Throbber">Wikipedia</a>
 * describes a throbber as:
 * <p>
 * <blockquote>A throbber is a graphic found in a graphical user interface of a
 * computer program that animates to show the user that the program is
 * performing an action in the background (such as downloading content,
 * conducting intensive calculations or communicating with an external device).
 * In contrast to a progress bar, a throbber does not convey how much of the
 * action has been completed.</blockquote>
 */
public interface ThrobberPainter {

    // originally based on https://javagraphics.blogspot.com/2014/03/implementing-jthrobber.html

    void paint(Graphics2D g, Rectangle bounds, Float fraction, Color foreground);

    /**
     * Return the default recommended period of this animation in milliseconds.
     */
    int getPreferredPeriod();

    /**
     * Return the default recommended size of this throbber.
     */
    Dimension getPreferredSize();

    /**
     * Return the current fractional value between [0f,1f) to describe an animation with a given period.
     *
     * @param period the milliseconds the animation takes to complete.
     * @return a value between [0f, 1f) based on the period and <code>System.currentTimeMillis()</code>
     */
    static float getCurrentFraction(int period) {
        int i = (int)(System.currentTimeMillis() % period);
        return ((float) i) / ((float) period);
    }
}
