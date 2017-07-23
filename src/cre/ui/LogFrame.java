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
        JScrollPane scrollPane = new JScrollPane(textArea);
        this.add(scrollPane, BorderLayout.CENTER);
        JPanel southPanel = new JPanel();
        this.add(southPanel, BorderLayout.SOUTH);
        JButton refreshButton = new JButton("Refresh");
        southPanel.add(refreshButton);
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refresh();
            }
        });
        refresh();
    }

    private void refresh() {
        textArea.setText(outputStream.toString());
    }

    private JTextArea textArea = new JTextArea();
    private MyStringOutputStream outputStream;
}
