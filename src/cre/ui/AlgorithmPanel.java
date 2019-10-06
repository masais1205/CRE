package cre.ui;

import cre.Config.OtherConfig;
import cre.ConfigSetter;
import cre.algorithm.AbstractAlgorithm;
import cre.algorithm.CanShowOutput;
import cre.algorithm.CanShowStatus;
import cre.algorithm.crcs.CRCSAlgorithm;
import cre.algorithm.crpa.CRPAAlgorithm;
import cre.ui.custom.MyFixWidthPanel;
import cre.ui.custom.MyFormattedTextField;
import cre.ui.custom.MyTitledBorder;
import cre.view.ResizablePanel;
import cre.view.ResizableScrollAndToolPanel;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by HanYizhao on 2017/6/26.
 */
public class AlgorithmPanel extends JPanel implements ItemListener, CanShowOutput {
    public AlgorithmPanel(final MainFrameEventHandler mainFrame, final AbstractAlgorithm algorithm) {
        this.mainFrame = mainFrame;
        this.algorithm = algorithm;
        this.setLayout(new BorderLayout());


        int ten = Tool.HighResolution(10);
        int five = Tool.HighResolution(5);
        int four = Tool.HighResolution(4);
        int two = Tool.HighResolution(2);
        int seven = Tool.HighResolution(7);

        URL helpImage = getClass().getResource("/image/help.png");
        URL helpActiveImage = getClass().getResource("/image/help_active.png");
        JSplitPane mainSplitPane = new JSplitPane();
        mainSplitPane.setDividerSize(four);
        this.add(mainSplitPane, BorderLayout.CENTER);
        mainSplitPane.add(leftPanel, JSplitPane.LEFT);
        mainSplitPane.add(rightPanel, JSplitPane.RIGHT);

        leftPanel.setDividerSize(four);
        leftPanel.setDividerLocation(Tool.HighResolution(350));
        leftPanel.setPreferredSize(new Dimension(Tool.HighResolution(300), 0));

        optionPanelHelper.add(optionPanel, BorderLayout.NORTH);
        optionPanelHelper.setMaximumSize(new Dimension());


        GridBagConstraints s = new GridBagConstraints();

        JPanel leftBottomPanel = new JPanel(new GridBagLayout());
        leftPanel.add(leftBottomPanel, JSplitPane.BOTTOM);

        s.weightx = 1;
        s.weighty = 1;
        s.gridwidth = GridBagConstraints.REMAINDER;
        s.fill = GridBagConstraints.BOTH;
        s.insets = new Insets(0, 0, 0, 0);
        leftPanel.add(mScroll, JSplitPane.TOP);

        JPanel startStopPanel = new JPanel(new GridLayout(1, 2, ten, 0));
        startStopPanel.add(startButton);
        startStopPanel.add(stopButton);
        s.weightx = 1;
        s.weighty = 0;
        s.gridwidth = GridBagConstraints.REMAINDER;
        s.insets.set(seven, five, five, five);
        leftBottomPanel.add(startStopPanel, s);

        s.weightx = 1;
        s.insets.set(0, five, 0, five);
        s.weighty = 1;
        s.fill = GridBagConstraints.BOTH;
        JScrollPane scrollPane = new JScrollPane(resultList);
        scrollPane.setBorder(new MyTitledBorder("Result list"));
        leftBottomPanel.add(scrollPane, s);

        s.weightx = 1;
        s.weighty = 0;
        s.gridwidth = GridBagConstraints.REMAINDER;
        s.fill = GridBagConstraints.HORIZONTAL;
        s.insets = new Insets(five, five, five, five);

        Document optionsDoc = Tool.getAlgorithmHelpDoc(algorithm.getName(), algorithm.getIntroduction(), null);

        optionPanel.add(testOptionPanel, s);
        testOptionPanel.setBorder(new MyTitledBorder("Options").setOtherInfo(helpImage,
                helpActiveImage,
                "Click to see doc", algorithm.getName(), optionsDoc));
        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(algorithmOnlyRadio);
        buttonGroup.add(predictionRadio);

        s.insets.set(two, 0, 0, 0);
        s.fill = GridBagConstraints.NONE;
        s.anchor = GridBagConstraints.WEST;
        testOptionPanel.add(algorithmOnlyRadio, s);
        testOptionPanel.add(predictionRadio, s);
        s.fill = GridBagConstraints.HORIZONTAL;
        s.insets.set(two, ten + ten, 0, 0);
        testOptionPanel.add(predictionOptionPanel, s);
        ButtonGroup predictionGroup = new ButtonGroup();
        predictionGroup.add(validationRadio);
        predictionGroup.add(crossValidationRadio);


        s.gridwidth = 1;
        s.insets = new Insets(two, 0, 0, 0);
        predictionOptionPanel.add(validationRadio, s);
        predictionOptionPanel.add(new JLabel("Test"), s);
        validationTestingRatioTextField.setIntRange(0, 99);
        validationTestingRatioTextField.setValue(50);
        predictionOptionPanel.add(validationTestingRatioTextField, s);
        s.gridwidth = GridBagConstraints.REMAINDER;

        predictionOptionPanel.add(new JLabel("%", JLabel.CENTER), s);

        crossValidationComboBox.setSelectedItem(10);
        s.gridwidth = 1;
        s.insets = new Insets(two, 0, 0, 0);
        predictionOptionPanel.add(crossValidationRadio, s);
        predictionOptionPanel.add(new JLabel("Folds"), s);
        s.gridwidth = 1;
        predictionOptionPanel.add(crossValidationComboBox, s);
        s.gridwidth = GridBagConstraints.REMAINDER;
        predictionOptionPanel.add(new JLabel(), s);


        s.gridwidth = 1;
        s.insets = new Insets(five, five, 0, 0);
        predictionOptionPanel.add(new JLabel("Repeat times"), s);
        s.insets.left = 0;
        predictionOptionPanel.add(new JLabel(), s);
        s.gridwidth = 1;
        validationRepeatTimeTextField.setIntRange(1, 100);
        validationRepeatTimeTextField.setValue(10);
        predictionOptionPanel.add(validationRepeatTimeTextField, s);
        s.gridwidth = GridBagConstraints.REMAINDER;
        predictionOptionPanel.add(new JLabel(), s);

        algorithmOnlyRadio.setSelected(true);
        validationRadio.setSelected(true);
        crossValidationComboBox.setEnabled(false);
        predictionOptionPanel.setVisible(false);

        algorithmOnlyRadio.addItemListener(this);
        predictionRadio.addItemListener(this);
        validationRadio.addItemListener(this);
        crossValidationRadio.addItemListener(this);

        if (algorithm instanceof CRPAAlgorithm || algorithm instanceof CRCSAlgorithm) {
            testOptionPanel.setVisible(false);
        }

        try {
            algorithmOptionPanel = ConfigSetter.createAJPanel(mainFrame.getFrame(), algorithm.getConfiguration());
            s.weightx = 1;
            s.fill = GridBagConstraints.HORIZONTAL;
            s.gridwidth = GridBagConstraints.REMAINDER;
            s.insets.set(ten, five, five, five);
            Document helpDoc = Tool.getAlgorithmHelpDoc(algorithm.getName(), testOptionPanel.isVisible() ? null : algorithm.getIntroduction(), ConfigSetter.getConfigsFromObject(algorithm.getConfiguration()));
            algorithmOptionPanel.setBorder(new MyTitledBorder("Parameters").setOtherInfo(helpImage, helpActiveImage, "Click to see doc", algorithm.getName(), helpDoc));
            optionPanel.add(algorithmOptionPanel, s);
        } catch (ConfigSetter.ConfigException e) {
            e.printStackTrace();
        }
        stopButton.setEnabled(false);
        startButton.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startButtonClicked();
            }
        });
        startButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!startButton.isEnabled()) {
                    if (runningThread == null) {
                        String errorMessage = algorithm.getInitErrorMessage();
                        if (errorMessage != null) {
                            JOptionPane.showMessageDialog(mainFrame.getFrame(), errorMessage, "ERROR", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopButtonClicked();
            }
        });
        rightPanel.setBorder(new TitledBorder("Output"));
        rightPanel.addTab("Results", new JScrollPane(textArea));
        textArea.setEditable(false);

        Font oldFont = textArea.getFont();
        if (oldFont != null) {
            textArea.setFont(new Font(Font.MONOSPACED, oldFont.getStyle(), oldFont.getSize()));
        }

        resultList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    resultListSelectedChange();
                }
            }
        });


    }

    private void stopButtonClicked() {
        stopButton.setEnabled(false);
        if (calculatingAlgorithm != null) {
            mainFrame.getCanShowStatus().showStatus("Stopping...");
            calculatingAlgorithm.setShouldStop();
        }
    }

    public void setAlgorithm(AbstractAlgorithm algorithm) {
        this.algorithm = algorithm;
        try {
            int five = Tool.HighResolution(5);
            int ten = Tool.HighResolution(10);
            URL helpImage = getClass().getResource("/image/help.png");
            URL helpActiveImage = getClass().getResource("/image/help_active.png");
            GridBagConstraints s = new GridBagConstraints();
            optionPanel.remove(algorithmOptionPanel);
            algorithmOptionPanel = ConfigSetter.createAJPanel(mainFrame.getFrame(), algorithm.getConfiguration());
            s.weightx = 1;
            s.fill = GridBagConstraints.HORIZONTAL;
            s.gridwidth = GridBagConstraints.REMAINDER;
            s.insets.set(ten, five, five, five);
            Document helpDoc = Tool.getAlgorithmHelpDoc(algorithm.getName(), testOptionPanel.isVisible() ? null : algorithm.getIntroduction(), ConfigSetter.getConfigsFromObject(algorithm.getConfiguration()));
            algorithmOptionPanel.setBorder(new MyTitledBorder("Parameters").setOtherInfo(helpImage, helpActiveImage, "Click to see doc", algorithm.getName(), helpDoc));
            optionPanel.add(algorithmOptionPanel, s);
        } catch (ConfigSetter.ConfigException e) {
            e.printStackTrace();
        }
    }

    public AbstractAlgorithm getAlgorithm() {
        return algorithm;
    }

    private void resultListSelectedChange() {
        resultListItems.get(oldSelectionIndex).messageBuffer = new StringBuilder(textArea.getText());
        oldSelectionIndex = resultList.getSelectedIndex();
        ResultListItem ri = resultListItems.get(oldSelectionIndex);
        textArea.setText(ri.messageBuffer.toString());
        int tabCount = rightPanel.getTabCount();
        for (int i = tabCount - 1; i > 0; i--) {
            rightPanel.removeTabAt(i);
        }
        if (ri.figures != null && ri.figures.size() > 0) {
            for (int i = 0; i < ri.figures.size(); i++) {
                ResizablePanel rp = ri.figures.get(i);
                String title;
                if (rp.getTag() == null) {
                    title = "Diagram " + (i + 1);
                } else {
                    title = rp.getTag().toString();
                }
                rightPanel.addTab(title, new ResizableScrollAndToolPanel(rp));
            }
        }
    }


    @Override
    public void itemStateChanged(ItemEvent e) {
        if (e.getSource() == predictionRadio) {
            predictionOptionPanel.setVisible(e.getStateChange() == ItemEvent.SELECTED);
        } else if (e.getSource() == validationRadio) {
            validationTestingRatioTextField.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
        } else if (e.getSource() == crossValidationRadio) {
            crossValidationComboBox.setEnabled(e.getStateChange() == ItemEvent.SELECTED);
        }
    }

    /**
     * Called when the calculation is finished.
     */
    private void calculatingFinished() {
        startButton.setText("Start");
        startButton.setEnabled(canStart);
        stopButton.setEnabled(false);
        runningThread = null;
        calculatingAlgorithm = null;
    }

    /**
     * Called when "Start Button" is clicked.
     */
    private void startButtonClicked() {
        if (algorithm == null) {
            JOptionPane.showMessageDialog(mainFrame.getFrame(),
                    "algorithm is null.");
        } else {
            final OtherConfig nowOtherConfig;
            try {
                calculatingAlgorithm = (AbstractAlgorithm) algorithm.clone();
                nowOtherConfig = new OtherConfig(
                        algorithmOnlyRadio.isSelected() ? OtherConfig.Validation.NONE :
                                (validationRadio.isSelected() ? OtherConfig.Validation.VALIDATION :
                                        OtherConfig.Validation.CROSS_VALIDATION),
                        Integer.parseInt(validationRepeatTimeTextField.getText()),
                        Integer.parseInt(validationTestingRatioTextField.getText()),
                        (Integer) crossValidationComboBox.getSelectedItem());
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(mainFrame.getFrame(),
                        e.getMessage(), "ERROR", JOptionPane.ERROR_MESSAGE);
                return;
            }
            startButton.setEnabled(false);
            startButton.setText("Running");
            stopButton.setEnabled(true);
            ResultListItem item = new ResultListItem();
            item.algorithm = calculatingAlgorithm;
            resultListItems.add(item);
            listModel.addElement(new SimpleDateFormat().format(new Date()) + " - " + calculatingAlgorithm.toString());
            resultList.setSelectedIndex(listModel.size() - 1);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    CanShowStatus canShowStatus = mainFrame.getCanShowStatus();
                    canShowStatus.showStatus("Starting...");
                    long startTime = System.nanoTime();
                    List<ResizablePanel> panelss = null;
                    try {
                        panelss = calculatingAlgorithm.doMyAlgorithm(AlgorithmPanel.this, canShowStatus, nowOtherConfig);
                    } catch (Exception e) {
                        e.printStackTrace();
                        AlgorithmPanel.this.showOutputString("ERROR. See log for more details");
                    }
                    final List<ResizablePanel> panels = panelss;
                    long endTime = System.nanoTime();
                    AlgorithmPanel.this.showOutputString("All Time:" + String.format("%.6f", (double) (endTime - startTime) / 1000000000) + "s");
                    if (calculatingAlgorithm.isShouldStop()) {
                        canShowStatus.showStatus(calculatingAlgorithm.getName() + " Stopped.");
                    } else {
                        canShowStatus.showStatus(calculatingAlgorithm.getName() + " Finished.");
                    }
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            resultListItems.get(resultListItems.size() - 1).figures = panels;
                            resultListSelectedChange();
                            calculatingFinished();
                        }
                    });
                }
            });
            runningThread = thread;
            thread.start();
        }
    }

    public void setCanStart(boolean canStart) {
        this.canStart = canStart;
        if (runningThread == null) {
            startButton.setEnabled(canStart);
        }
    }

    /**
     * This attribute is only working for the resultList.
     * Store the selected index of resultList.
     */
    private int oldSelectionIndex = 0;

    /**
     * The interface in which there are functions MainFrame provide.
     */
    private MainFrameEventHandler mainFrame;

    /**
     * The algorithm which is chosen. Not the running algorithm.
     * Because when user press 'Start' and then the cloned algorithm starts running, the user can still choose a algorithm
     * which is not related to the running.
     */
    private AbstractAlgorithm algorithm;

    /**
     * For some reason, algorithm can not start. For example, algorithm init failed.
     */
    private boolean canStart = true;

    private AbstractAlgorithm calculatingAlgorithm;

    private JSplitPane leftPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    private JTabbedPane rightPanel = new JTabbedPane();

    private JPanel predictionOptionPanel = new JPanel(new GridBagLayout());
    private JPanel optionPanelHelper = new MyFixWidthPanel(new BorderLayout());
    private JScrollPane mScroll = new JScrollPane(optionPanelHelper);
    private JPanel optionPanel = new JPanel(new GridBagLayout());
    private JPanel algorithmOptionPanel;
    private JPanel testOptionPanel = new JPanel(new GridBagLayout());

    private JRadioButton algorithmOnlyRadio = new JRadioButton("Causal discovery");
    private JRadioButton predictionRadio = new JRadioButton("Classification");
    private JRadioButton validationRadio = new JRadioButton("Validation");
    private JRadioButton crossValidationRadio = new JRadioButton("Cross Validation");


    private JComboBox<Integer> crossValidationComboBox = new JComboBox<>(new Integer[]{2, 3, 4, 5, 6, 7, 8, 9, 10});
    private MyFormattedTextField validationRepeatTimeTextField = new MyFormattedTextField(NumberFormat.getIntegerInstance()),
            validationTestingRatioTextField = new MyFormattedTextField(NumberFormat.getIntegerInstance());

    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> resultList = new JList<>(listModel);

    private JButton startButton = new JButton("Start");
    private JButton stopButton = new JButton("Stop");

    private JTextArea textArea = new JTextArea();

    /**
     * A data model of result list. Include the algorithm and output
     */
    private ArrayList<ResultListItem> resultListItems = new ArrayList<>();

    @Override
    public void showOutputString(final String value) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                int which = listModel.getSize() - 1;
                if (resultList.getSelectedIndex() == which) {
                    textArea.append(value);
                    textArea.append("\n");
                } else {
                    resultListItems.get(which).messageBuffer.append(value).append("\n");
                }
            }
        });
        showLogString(value);
    }

    @Override
    public void showLogString(String value) {
        System.out.println(value);
    }

    /**
     * The only thread to do calculation using specific algorithm.
     */
    private Thread runningThread;

    public boolean canCloseSafely() {
        return runningThread == null;
    }

    public void doForceClose() {
        if (runningThread != null) {
            runningThread.stop();
        }
    }

    /**
     * A Data Structure
     */
    private class ResultListItem {
        /**
         * algorithm
         */
        AbstractAlgorithm algorithm;
        /**
         * When this item is switched to the background,
         * use this attribute to store the outputs.
         */
        StringBuilder messageBuffer = new StringBuilder();
        List<ResizablePanel> figures;
    }


}
