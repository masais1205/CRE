package cre.ui;

import cre.MyStringOutputStream;
import cre.algorithm.AbstractAlgorithm;
import cre.algorithm.CanShowStatus;
import cre.algorithm.cdt.CDTAlgorithm;
import cre.algorithm.crcs.CRCSAlgorithm;
import cre.algorithm.crpa.CRPAAlgorithm;
import cre.algorithm.test.TestAlgorithm;
import cre.ui.custom.MyIconFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by HanYizhao on 4/13/2017.
 * <p>
 * MainFrame
 */
public class MainFrame extends MyIconFrame implements MainFrameEventHandler, CanShowStatus {

    public MainFrame() throws HeadlessException {
        this.setSize(Tool.HighResolution(800), Tool.HighResolution(600));
        this.setTitle("Causal Rule Explorer");
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        int two = Tool.HighResolution(2);

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int inThreadCount = 0;
                for (AlgorithmPanel i : abstractAlgorithmList.values()) {
                    if (!i.canCloseSafely()) {
                        inThreadCount++;
                    }
                }
                if (inThreadCount == 0) {
                    MainFrame.this.dispose();
                    Runtime.getRuntime().exit(0);
                } else {
                    int a = JOptionPane.showConfirmDialog(MainFrame.this,
                            "There " + (inThreadCount == 1 ? "is one thread" : ("are " + inThreadCount + " threads"))
                                    + " still running. Do you want to stop forcedly?",
                            "Confirm", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
                    if (a == JOptionPane.OK_OPTION) {
                        for (AlgorithmPanel i : abstractAlgorithmList.values()) {
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
        FilePanel filePanel = new FilePanel(this);
        tabbedPane.addTab("File", filePanel);
        for (int i = 0; i < algorithmNames.length; i++) {
            tabbedPane.addTab(algorithmNames[i], new JPanel());
            tabbedPane.setEnabledAt(i + 1, false);
        }
        tabbedPane.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (int i = 1; i < tabbedPane.getTabCount(); i++) {
                    Rectangle rect = tabbedPane.getBoundsAt(i);
                    if (rect.contains(e.getX(), e.getY())) { //判断是否点在边界内
                        if (!tabbedPane.isEnabledAt(i)) {
                            String me = algorithmNoShownInitErrorMessage[i - 1];
                            if (me != null) {
                                JOptionPane.showMessageDialog(getFrame(), me, "ERROR", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                }
            }
        });
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new GridBagLayout());
        GridBagConstraints s = new GridBagConstraints();
        root.add(southPanel, BorderLayout.SOUTH);
        s.gridwidth = 1;
        s.anchor = GridBagConstraints.WEST;
        s.insets.set(two, two, two, two);
        southPanel.add(new JLabel("Status: "), s);
        s.weightx = 1;
        southPanel.add(statusLabel, s);
        s.weightx = 0;
        s.gridwidth = GridBagConstraints.REMAINDER;
        JButton logButton = new JButton("Log");
        logButton.setMargin(new Insets(0, two, 0, two));
        southPanel.add(logButton, s);
        Tool.moveToCenter(this, true);
        System.out.println("Reassigns the \"standard\" output stream.");
        System.setOut(new PrintStream(outPutBuffer, true));
        System.err.println("Reassigns the \"standard\" error output stream.");
        System.setErr(new PrintStream(outPutBuffer, true));
        logButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                LogFrame lf = new LogFrame("Log", outPutBuffer);
                lf.setVisible(true);
            }
        });
    }

    private JLabel statusLabel = new JLabel();
    private MyStringOutputStream outPutBuffer = new MyStringOutputStream();
    private JTabbedPane tabbedPane = new JTabbedPane();

    private final String[] algorithmNames = {"CDT", "CR-CS", "CR-PA", "DEEP"};
    private final String[] algorithmNoShownInitErrorMessage = new String[algorithmNames.length];

    @Override
    public void selectANewFile(File file) {
        ArrayList<String> notFitALgorithm = new ArrayList<>();
        for (int i = 0; i < algorithmNames.length; i++) {
            AlgorithmPanel panel = abstractAlgorithmList.get(i);
            if (panel == null) {
                AbstractAlgorithm abstractAlgorithm;
                switch (i) {
                    // commented by mss
                    case 0:
                        abstractAlgorithm = new CDTAlgorithm(file);
                        break;
                    case 1:
                        abstractAlgorithm = new CRCSAlgorithm(file);
                        break;
                    case 2:
                        abstractAlgorithm = new CRPAAlgorithm(file);
                        break;
                    default:
                        abstractAlgorithm = new TestAlgorithm(file);
                        break;
                }
                try {
                    abstractAlgorithm.init();
                    AlgorithmPanel algorithmPanel = new AlgorithmPanel(this,
                            abstractAlgorithm);
                    abstractAlgorithmList.put(i, algorithmPanel);
                    tabbedPane.setComponentAt(i + 1,
                            algorithmPanel);
                    tabbedPane.setEnabledAt(i + 1, true);
                } catch (Throwable e) {
                    e.printStackTrace();
                    abstractAlgorithm.setInitErrorMessage(e.getMessage());
                    algorithmNoShownInitErrorMessage[i] = e.getMessage();
                    notFitALgorithm.add(algorithmNames[i] + ": " + e.getMessage());
                }
            } else {
                try {
                    panel.setAlgorithm(panel.getAlgorithm().getCloneBecauseChangeOfFile(file));
                    panel.setCanStart(true);
                } catch (Throwable e) {
                    e.printStackTrace();
                    panel.setCanStart(false);
                    panel.getAlgorithm().setInitErrorMessage(e.getMessage());
                    notFitALgorithm.add(algorithmNames[i] + ": " + e.getMessage());
                }
            }
        }
        if (notFitALgorithm.size() > 0) {
            showStatus(notFitALgorithm.size() + " algorithm" + (notFitALgorithm.size() == 1 ? "" : "s")
                    + " can not handle this file. See log for more details.");
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
                System.out.println(value);
            }
        });
    }

    private Map<Integer, AlgorithmPanel> abstractAlgorithmList = new HashMap<>();
}

