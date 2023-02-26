import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.desktop.AppForegroundEvent;
import java.awt.desktop.AppForegroundListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.text.DateFormat;
import java.util.*;

/**
 * This is a simple Swing that helps demonstrate my frustration when Stage Manager helps hide
 * a JFrame. The application includes instructions on launch to explore the problem.
 */
public class StageManagerTest extends JFrame {

    public static void main(String[] args) {
        System.setProperty("apple.laf.useScreenMenuBar", "true");
        SwingUtilities.invokeLater(() -> {
            StageManagerTest test = new StageManagerTest();
            test.pack();
            test.setLocationRelativeTo(null);
            test.setVisible(true);

            JFrame focusDecoy = new JFrame();
            focusDecoy.getContentPane().add(new JTextField("Focus Decoy"));
            focusDecoy.pack();
            focusDecoy.setLocationRelativeTo(null);
            focusDecoy.setVisible(true);
        });
    }


    JTextPane instructions = new JTextPane();
    JTextPane log = new JTextPane();
    JScrollPane scrollPane = new JScrollPane(log);
    AffineTransform lastUsedTransform = null;

    JLabel label = new JLabel(new Icon() {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            float hue = ((float) (System.currentTimeMillis()%1000)) / 1000f;
            Color color = new Color(Color.HSBtoRGB(hue, 1, 1));
            g.setColor(color);
            g.fillRect(0,0,getIconWidth(),getIconHeight());
            c.repaint();

            lastUsedTransform = ((Graphics2D)g).getTransform();
        }

        @Override
        public int getIconWidth() {
            return 600;
        }

        @Override
        public int getIconHeight() {
            return 20;
        }
    });

    private Boolean isAppInForeground = null;

    Runnable updateLog = new Runnable() {
        Map<String, Object> lastStatus = new HashMap<>();
        @Override
        public void run() {
            Map<String, Object> currentStatus = new TreeMap<>();
            currentStatus.put("isShowing", isShowing());
            currentStatus.put("isAppInForeground", isAppInForeground);
            currentStatus.put("isIconified", ((getExtendedState() & Frame.ICONIFIED) > 0) );
            currentStatus.put("bounds", getBounds());
            currentStatus.put("locationOnScreen", getLocationOnScreen());

            // Harshitha O recommended adding these properties
            currentStatus.put("isActive", isActive() );
            currentStatus.put("isFocused", isFocused() );
            currentStatus.put("isAlwaysOnTop", isAlwaysOnTop() );

            currentStatus.put("Graphics2D scaling factor", lastUsedTransform == null ? "None" :
                    Math.sqrt(lastUsedTransform.getDeterminant()) );

            Map<String, Object> diff = getDiff(lastStatus, currentStatus);
            if (!diff.isEmpty()) {
                String newLog = log.getText() + "\n";

                Date date = new Date(System.currentTimeMillis());

                for (Map.Entry<String, Object> entry : diff.entrySet()) {
                    newLog = newLog + "\n" + DateFormat.getTimeInstance().format(date) + "\t\t" + entry.getKey()+" = "+ entry.getValue();
                }

                log.setText(newLog);
            }
            lastStatus = currentStatus;
        }

        private Map<String, Object> getDiff(Map<String, Object> oldMap, Map<String, Object> newMap) {
            Collection<String> allKeys = new TreeSet<>();
            allKeys.addAll(oldMap.keySet());
            allKeys.addAll(newMap.keySet());

            Map<String, Object> returnValue = new LinkedHashMap<>();
            for (String key : allKeys) {
                Object v1 = oldMap.get(key);
                Object v2 = newMap.get(key);
                if (!Objects.equals(v1, v2)) {
                    returnValue.put(key, v2);
                }
            }
            return returnValue;
        }
    };

    public StageManagerTest() {

        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.APP_EVENT_FOREGROUND)) {
            Desktop.getDesktop().addAppEventListener(new AppForegroundListener() {
                @Override
                public void appRaisedToForeground(AppForegroundEvent e) {
                    isAppInForeground = true;
                }

                @Override
                public void appMovedToBackground(AppForegroundEvent e) {
                    isAppInForeground = false;
                }
            });
        }

        instructions.setText("Instructions:\n" +
                        "1. Launch this app with \"Stage Manager\" active.\n" +
                        "2. Make sure the \"Focus Decoy\" text field has the keyboard focus.\n" +
                        "3. Click the yellow minimize button in the titlebar of this large frame.\n" +
                        "4. After a few seconds reopen this app in the stage manager.\n" +
                        "\n" +
                        "Observed behavior: the log (in the scrollpane below) doesn't perceive anything changed when this window is minimized\n" +
                        "\n" +
                        "Expected behavior: there should be some property we can poll/listen to to identify whether our app is showing. (Maybe the ultimate solution is to add a new property/state/listener?)"
        );


        instructions.setEditable(false);
        log.setEditable(false);

        instructions.setBorder(new EmptyBorder(10, 10, 10, 10));
        scrollPane.setBorder(new CompoundBorder(new EmptyBorder(10, 10, 10, 10), scrollPane.getBorder()));

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(instructions, BorderLayout.NORTH);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        getContentPane().add(label, BorderLayout.SOUTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        scrollPane.setPreferredSize(new Dimension(200, 500));
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(100);

                        SwingUtilities.invokeLater(updateLog);
                    } catch(Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        }, "StageManager-log-thread");
        thread.setDaemon(true);
        thread.start();

        JMenuBar menuBar = new JMenuBar();
        JMenu windowMenu = new JMenu("Window");
        AbstractAction minimizeAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setExtendedState(JFrame.ICONIFIED);
            }
        };
        minimizeAction.putValue(Action.NAME, "Minimize");
        minimizeAction.putValue(Action.ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_M, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        JMenuItem minimizeMenuItem = new JMenuItem(minimizeAction);
        windowMenu.add(minimizeMenuItem);
        menuBar.add(windowMenu);
        setJMenuBar(menuBar);
    }
}
