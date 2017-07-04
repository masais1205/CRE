package cre.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * Created by HanYizhao on 2017/7/4.
 */
public class ResizableScrollAndToolPanel extends JPanel {
    public ResizableScrollAndToolPanel(final ResizablePanel resizablePanel) {
        this.setLayout(new BorderLayout());
        final ResizableScrollPane scrollPane = new ResizableScrollPane(resizablePanel);
        this.add(scrollPane, BorderLayout.CENTER);
        JPanel panel = new JPanel();
        this.add(panel, BorderLayout.SOUTH);
        buttonZoomIn.setToolTipText("Zoom In (Ctrl + Mouse Wheel up)");
        buttonZoomOut.setToolTipText("Zoom Out (Ctrl + Mouse Wheel down)");
        panel.add(buttonZoomIn);
        panel.add(buttonZoomOut);
        panel.add(buttonSaveAs);
        buttonZoomIn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scrollPane.zoom(true, null);
            }
        });
        buttonZoomOut.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scrollPane.zoom(false, null);
            }
        });
        buttonSaveAs.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resizablePanel.doSaveAs();
            }
        });

    }

    private JButton buttonZoomIn = new JButton("Zoom In");
    private JButton buttonZoomOut = new JButton("Zoom Out");
    private JButton buttonSaveAs = new JButton("Save As");

}
