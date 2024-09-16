package com.pump.swing;

import javax.swing.*;
import java.awt.*;

/**
 * This is a demo app that features the JavaFormatter, XMLFormatter, and LineNumberBorder.
 */
public class TextEditorApp {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(TextEditorApp::new);
    }

    public TextEditorApp() {
        try {
            String lf = UIManager.getSystemLookAndFeelClassName();
            UIManager.setLookAndFeel(lf);
        } catch (Throwable e) {
            e.printStackTrace();
        }

        JFrame f = new JFrame();
        JTabbedPane tabs = new JTabbedPane();
        tabs.add("Java", createJavaTextPane());
        tabs.add("XML", createXMLTextPane());
        f.getContentPane().add(tabs);
        f.pack();
        f.setVisible(true);
    }

    private JComponent createJavaTextPane() {
        JTextPane textPane = new JTextPane();
        textPane.setText("/** Sample Class */\npublic class Demo {\n\tpublic Demo() {\n\t\t// TODO: insert code here\n\t}\n}");
        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setPreferredSize(new Dimension(800, 600));
        new JavaFormatter(textPane);
        LineNumberBorder.install(scrollPane, textPane);
        return scrollPane;
    }

    private JComponent createXMLTextPane() {
        JTextPane textPane = new JTextPane();
        textPane.setText("""
                <?xml version="1.0"?>
                <mysqldump xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
                \t<database name="test">
                \t\t<!--This table needs work -->
                \t\t<table_structure name="onerow">
                \t\t\t<field Field="a" Type="int(11)" Null="YES" Key="" Extra="" />
                \t\t</table_structure>
                \t\t<table_data name="onerow">
                \t\t\t<row>
                \t\t\t\t<field name="a">1</field>
                \t\t\t</row>
                \t\t</table_data>
                \t</database>
                </mysqldump>""");
        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setPreferredSize(new Dimension(800, 600));
        new JavaFormatter(textPane);
        LineNumberBorder.install(scrollPane, textPane);
        return scrollPane;
    }
}
