package cre.ui;

import cre.ui.custom.MyIconFrame;

import javax.swing.*;
import javax.swing.text.Document;

/**
 * Created by 16502 on 2017/6/21.
 * <p>
 * Show text
 */
public class TextInformationFrame extends MyIconFrame {
    public TextInformationFrame(String title, Document document) {
        this.setTitle(title);
        this.setSize(Tool.HighResolution(600), Tool.HighResolution(400));
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
