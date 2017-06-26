package cre.ui;

import sun.plugin.javascript.JSClassLoader;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.io.IOException;
import java.net.URL;

/**
 * Created by 16502 on 2017/6/21.
 */
public class TextInformationFrame extends JFrame {
    public TextInformationFrame(String title, Document document) {
        this.setTitle(title);
        this.setSize(Tool.HighResolution(400), Tool.HighResolution(600));
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        JTextPane textPane = new JTextPane();
        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(scrollPane);
        textPane.setDocument(document);
        textPane.setEditable(false);
        Tool.moveToCenter(this);
    }
}
