package cre.ui;

import cre.MyStringOutputStream;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by HanYizhao on 2017/7/23.
 */
public class LogFrame extends JFrame {

    public LogFrame(String title, MyStringOutputStream outputStream) throws HeadlessException {
        this.outputStream = outputStream;
        this.setTitle(title);
        this.setSize(Tool.HighResolution(600), Tool.HighResolution(400));
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        this.setLayout(new BorderLayout());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        this.add(scrollPane, BorderLayout.CENTER);
        JPanel southPanel = new JPanel();
        this.add(southPanel, BorderLayout.SOUTH);
        JButton refreshButton = new JButton("Refresh");
        JButton clearButton = new JButton("Clear");
        southPanel.add(refreshButton);
        southPanel.add(clearButton);
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refresh();
            }
        });
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clear();
            }
        });
        refresh();
    }

    private void refresh() {
        textArea.setText(outputStream.toString());
    }

    private void clear() {
        outputStream.clear();
        refresh();
    }

    private JTextArea textArea = new JTextArea();
    private MyStringOutputStream outputStream;
}
