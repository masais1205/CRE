package cre.ui;

import cre.algorithm.AbstractAlgorithm;
import cre.algorithm.CanShowOutput;
import cre.algorithm.CanShowStatus;
import cre.algorithm.cdt.CDTAlgorithm;
import cre.algorithm.test.TestAlgorithm;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by HanYizhao on 4/13/2017.
 */
public class MainFrame extends JFrame implements MainFrameEventHandler, CanShowStatus {

    public MainFrame() throws HeadlessException {
        this.setSize(Tool.HighResolution(800), Tool.HighResolution(600));
        this.setTitle("Causal Rule Explorer");
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int inThreadCount = 0;
                for (AlgorithmPanel i : abstractAlgorithmList) {
                    if (!i.canCloseSafely()) {
                        inThreadCount++;
                    }
                }
                if (inThreadCount == 0) {
                    MainFrame.this.dispose();
                } else {
                    int a = JOptionPane.showConfirmDialog(MainFrame.this,
                            "There " + (inThreadCount == 1 ? "is one thread" : ("are " + inThreadCount + " threads"))
                                    + " still running. Do you want to stop forcedly?",
                            "Confirm", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (a == JOptionPane.OK_OPTION) {
                        for (AlgorithmPanel i : abstractAlgorithmList) {
                            if (!i.canCloseSafely()) {
                                i.doForceClose();
                            }
                        }
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
        for (int i = 0; i < algorithmNames.length; i++) {
            tabbedPane.addTab(algorithmNames[i], new JPanel());
            tabbedPane.setEnabledAt(i + 1, false);
        }
        root.add(toolBar, BorderLayout.SOUTH);
        toolBar.add(new JLabel("Status: "));
        toolBar.add(statusLabel);
        Tool.moveToCenter(this);

    }

    private FilePanel filePanel = new FilePanel(this);
    private JToolBar toolBar = new JToolBar();
    private JLabel statusLabel = new JLabel();
    JTabbedPane tabbedPane = new JTabbedPane();

    private final String[] algorithmNames = {"CDT", "Test"};

    private boolean isFirstTime = true;

    @Override
    public void selectANewFile(File file) {
        if (isFirstTime) {
            isFirstTime = false;
            for (int i = 0; i < algorithmNames.length; i++) {
                AbstractAlgorithm abstractAlgorithm = null;
                switch (i) {
                    case 0:
                        abstractAlgorithm = new CDTAlgorithm(file);
                        break;
                    case 1:
                        abstractAlgorithm = new TestAlgorithm(file);
                        break;
                }
                AlgorithmPanel algorithmPanel = new AlgorithmPanel(this,
                        abstractAlgorithm);
                abstractAlgorithmList.add(algorithmPanel);
                tabbedPane.setComponentAt(i + 1,
                        algorithmPanel);
                tabbedPane.setEnabledAt(i + 1, true);
            }
        } else {
            for (AlgorithmPanel i : abstractAlgorithmList) {
                i.setAlgorithm(i.getAlgorithm().getCloneBecauseChangeOfFile(file));
            }
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

    private ArrayList<AlgorithmPanel> abstractAlgorithmList = new ArrayList<>();
}

