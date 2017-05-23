package cre.ui;

import cre.algorithm.CanShowOutput;
import cre.algorithm.CanShowStatus;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

/**
 * Created by HanYizhao on 4/13/2017.
 */
public class MainFrame extends JFrame implements MainFrameEventHandler, CanShowStatus {

    public MainFrame() throws HeadlessException {
        this.setSize(Tool.HighResolution(800), Tool.HighResolution(600));
        this.setTitle("CRE");
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (classifyPanel.canCloseSafely()) {
                    MainFrame.this.dispose();
                } else {
                    int a = JOptionPane.showConfirmDialog(MainFrame.this,
                            "There are some threads still running. Do you want to stop forcedly?",
                            "Confirm", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (a == JOptionPane.OK_OPTION) {
                        classifyPanel.doForceClose();
                        MainFrame.this.dispose();
                    }
                }
            }
        });

        JPanel root = new JPanel();
        root.setLayout(new BorderLayout());
        this.add(root);

        //tabbedPane.setFont(new Font("", Font.PLAIN, 15));
        root.add(tabbedPane, BorderLayout.CENTER);
        tabbedPane.addTab("File", filePanel);
        tabbedPane.addTab("Classify", classifyPanel);
        tabbedPane.setEnabledAt(1, false);
        root.add(toolBar, BorderLayout.SOUTH);
        toolBar.add(new JLabel("Status: "));
        toolBar.add(statusLabel);
        Tool.moveToCenter(this);

    }

    private FilePanel filePanel = new FilePanel(this);
    private ClassifyPanel classifyPanel = new ClassifyPanel(this);
    private JToolBar toolBar = new JToolBar();
    private JLabel statusLabel = new JLabel();
    JTabbedPane tabbedPane = new JTabbedPane();


    @Override
    public void selectANewFile(File file) {
        classifyPanel.setNowSelectedFile(file);
        if (!tabbedPane.isEnabledAt(1)) {
            tabbedPane.setEnabledAt(1, true);
        }
    }

    @Override
    public Frame getFrame() {
        return this;
    }

    @Override
    public CanShowStatus getCanShowStatus() {
        return this;
    }


    @Override
    public void showStatus(final String value) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                statusLabel.setText(value);
            }
        });
    }
}

