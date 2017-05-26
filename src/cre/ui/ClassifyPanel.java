package cre.ui;

import cre.Config.OtherConfig;
import cre.ConfigSetter;
import cre.algorithm.AbstractAlgorithm;
import cre.algorithm.CanShowOutput;
import cre.algorithm.CanShowStatus;
import cre.algorithm.cdt.CDTAlgorithm;
import cre.algorithm.test.TestAlgorithm;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by HanYizhao on 4/13/2017.
 * <p>
 * When user chooses a file in FilePanel, ClassifyPanel becomes available.
 * This panel consisits of three parts.
 * North is the section in which user can choose an algorithm and configure the algorithm.
 * West is the section which has “start button” and “stop button” and shows history of each transaction.
 * When we change the item in history, the center part, a JTextArea will switch the outputs.
 */
public class ClassifyPanel extends MyPanel implements CanShowOutput {

    boolean isPressed = false;

    public ClassifyPanel(MainFrameEventHandler mainFrame) {
        this.mainFrame = mainFrame;
        this.setLayout(new BorderLayout());
        JPanel topPanel = new JPanel(new GridBagLayout());
        this.add(topPanel, BorderLayout.NORTH);
        topPanel.setBorder(new TitledBorder("Classifier"));
        GridBagConstraints s = new GridBagConstraints();
        s.weightx = 0;
        int ten = Tool.HighResolution(10);
        int five = Tool.HighResolution(5);
        int two = Tool.HighResolution(2);
        int seven = Tool.HighResolution(7);
        s.insets = new Insets(ten, ten, ten, five);
        s.gridwidth = 1;
        topPanel.add(chooseButton, s);
        s.insets = new Insets(ten, five, ten, ten);
        s.weightx = 1;
        s.gridwidth = 0;
        s.fill = GridBagConstraints.HORIZONTAL;
        topPanel.add(configTextField, s);
        configTextField.setFocusable(false);
        configTextField.setCursor(Cursor.getDefaultCursor());
        configTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                configTextFieldMouseClicked();
            }
        });

        final JPanel leftPanel = new JPanel(new GridBagLayout());
        this.add(leftPanel, BorderLayout.WEST);
        leftPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (leftPanel.getPreferredSize().getWidth() - e.getX() <
                        Tool.HighResolution(5)) {
                    isPressed = true;
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
            }


            @Override
            public void mouseExited(MouseEvent e) {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        });
        leftPanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (isPressed) {
                    if (e.getX() > Tool.HighResolution(10)
                            && ClassifyPanel.this.getSize().getWidth() - e.getX() >
                            Tool.HighResolution(15)) {
                        leftPanel.setPreferredSize(new Dimension(e.getX() + 1, 0));
                        setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
                        revalidate();
                        repaint();
                    }
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (leftPanel.getPreferredSize().getWidth() - e.getX() <
                        Tool.HighResolution(5)) {
                    setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
                } else {
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            }

        });
        leftPanel.setPreferredSize(new Dimension(Toolkit.getDefaultToolkit().getScreenSize().width * 300 / 1366, 0));
        JPanel testOptionPanel = new JPanel(new GridBagLayout());
        testOptionPanel.setBorder(new TitledBorder("Test Options"));


        s.weightx = 1;
        s.gridwidth = 0;
        s.insets = new Insets(ten, five, five, five);
        leftPanel.add(testOptionPanel, s);

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(validationRadio);
        buttonGroup.add(crossValidationRadio);
        validationRadio.setSelected(true);

        s.gridwidth = 1;
        s.insets = new Insets(two, 0, 0, 0);
        testOptionPanel.add(validationRadio, s);
        testOptionPanel.add(new JLabel("Repeat times"), s);
        s.gridwidth = 0;
        validationRepeatTimeTextField.setIntRange(1, 100);
        testOptionPanel.add(validationRepeatTimeTextField, s);

        s.gridwidth = 1;
        s.insets = new Insets(0, 0, 0, 0);
        testOptionPanel.add(new JLabel(), s);
        testOptionPanel.add(new JLabel("Test"), s);
        validationTestingRatioTextField.setIntRange(0, 99);
        testOptionPanel.add(validationTestingRatioTextField, s);
        s.gridwidth = GridBagConstraints.REMAINDER;

        testOptionPanel.add(new JLabel("%", JLabel.CENTER), s);
        crossValidationComboBox.setSelectedItem(10);
        s.gridwidth = 1;
        s.insets = new Insets(two, 0, 0, 0);
        testOptionPanel.add(crossValidationRadio, s);
        testOptionPanel.add(new JLabel("Folds"), s);
        s.gridwidth = 0;
        testOptionPanel.add(crossValidationComboBox, s);
        s.gridwidth = 1;
        s.insets = new Insets(ten, seven, five, five);
        leftPanel.add(startButton, s);
        s.gridwidth = 0;
        s.insets = new Insets(ten, five, five, seven);
        leftPanel.add(stopButton, s);
        JScrollPane scrollPane = new JScrollPane(resultList);
        scrollPane.setBorder(new TitledBorder("Result list"));
        s.gridwidth = 0;
        s.weighty = 1;
        s.fill = GridBagConstraints.BOTH;
        s.insets = new Insets(0, five, 0, five);
        leftPanel.add(scrollPane, s);

        JScrollPane textScroll = new JScrollPane(textArea);
        textScroll.setBorder(new TitledBorder("Output"));
        this.add(textScroll, BorderLayout.CENTER);

        chooseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseClassifier();
            }
        });
        textArea.setEditable(false);
        stopButton.setEnabled(false);
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startButtonClicked();
            }
        });

        resultList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    resultListItems.get(oldSelectionIndex).messageBuffer = new StringBuilder(textArea.getText());
                    oldSelectionIndex = resultList.getSelectedIndex();
                    textArea.setText(resultListItems.get(oldSelectionIndex).messageBuffer.toString());
                }
            }
        });
    }

    /**
     * This attribute is only working for the resultList.
     * Store the selected index of resultList.
     */
    private int oldSelectionIndex = 0;

    @Override
    public void showLogString(String value) {
        System.out.println(value);
    }

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
                    resultListItems.get(which).messageBuffer.append(value);
                    textArea.append("\n");
                }
            }
        });
        showLogString(value);
    }


    /**
     * Called when "Start Button" is clicked.
     */
    private void startButtonClicked() {
        if (nowSelectedAlgorithm == null) {
            JOptionPane.showMessageDialog(mainFrame.getFrame(),
                    "Please Choose a Classifier first");
        } else {
            final AbstractAlgorithm calculatingAlgorithm;
            final OtherConfig nowOtherConfig;
            try {
                calculatingAlgorithm = (AbstractAlgorithm) nowSelectedAlgorithm.clone();
                nowOtherConfig = new OtherConfig(
                        validationRadio.isSelected() ? OtherConfig.Validation.VALIDATION
                                : OtherConfig.Validation.CROSS_VALIDATION,
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
                    calculatingAlgorithm.doAlgorithm(ClassifyPanel.this, canShowStatus, nowOtherConfig);
                    canShowStatus.showStatus("OK");
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            calculatingFinished();
                        }
                    });
                }
            });
            runningThread = thread;
            thread.start();
        }
    }

    /**
     * Called when the calculation is finished.
     */
    private void calculatingFinished() {
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        runningThread = null;
    }

    /**
     * Called by MainFrame to inform ClassifyPanel that user chooses a new file.
     *
     * @param nowSelectedFile the new file
     */
    public void setNowSelectedFile(File nowSelectedFile) {
        this.nowSelectedFile = nowSelectedFile;
        if (nowSelectedAlgorithm != null) {
            nowSelectedAlgorithm = nowSelectedAlgorithm.getCloneBecauseChangeOfFile(nowSelectedFile);
        }
    }

    /**
     * Called when "configTextField" is clicked.
     */
    private void configTextFieldMouseClicked() {
        if (nowSelectedAlgorithm == null) {
            chooseClassifier();
        } else {
            try {
                Object config = nowSelectedAlgorithm.getConfiguration();
                ConfigSetter.show(mainFrame.getFrame(), config);
                refreshConfigTextField();
            } catch (ConfigSetter.ConfigException e) {
                String error = "ERROR" + e.getMessage();
                mainFrame.getCanShowStatus().showStatus(error);
                e.printStackTrace();
                JOptionPane.showMessageDialog(mainFrame.getFrame(),
                        error, "ERROR", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * When the configuration of algorithm is changed,
     * this function is called to refresh the content of "configTextField"
     */
    private void refreshConfigTextField() {
        if (nowSelectedAlgorithm != null) {
            configTextField.setText(nowSelectedAlgorithm.toString());
        } else {
            configTextField.setText("");
        }
    }

    /**
     * Choose an algorithm. This function is called when use press "choose button"
     * or press "configTextField" with no algorithm.
     */
    private void chooseClassifier() {
        String[] names = new String[]{"CDT", "Test"};
        ChooseFromStringList choose = new ChooseFromStringList(mainFrame.getFrame(),
                "", names);
        choose.setVisible(true);
        int index = choose.getSelectedIndex();
        if (index > -1) {
            switch (index) {
                case 0: {
                    nowSelectedAlgorithm = new CDTAlgorithm(nowSelectedFile);
                }
                break;
                case 1: {
                    nowSelectedAlgorithm = new TestAlgorithm(nowSelectedFile);
                }
                break;
                default:
                    break;
            }
            refreshConfigTextField();
        }
    }

    private JButton chooseButton = new JButton("Choose");
    private JTextField configTextField = new JTextField(17);
    private JButton startButton = new JButton("Start");
    private JButton stopButton = new JButton("Stop");
    private DefaultListModel<String> listModel = new DefaultListModel<>();
    private JList<String> resultList = new JList<>(listModel);
    private JTextArea textArea = new JTextArea();
    private JComboBox<Integer> crossValidationComboBox = new JComboBox<>(new Integer[]{2, 3, 4, 5, 6, 7, 8, 9, 10});
    private JRadioButton validationRadio = new JRadioButton("Validation"),
            crossValidationRadio = new JRadioButton("Cross Validation");
    private MyTextField validationRepeatTimeTextField = new MyTextField("10"),
            validationTestingRatioTextField = new MyTextField("50");

    /**
     * The interface in which there are functions MainFrame provide.
     */
    private MainFrameEventHandler mainFrame;

    /**
     * The file which is chosen. Maybe not the file used by the running algorithm.
     * Because when user press 'Start', he can still choose another file.
     */
    private File nowSelectedFile;


    /**
     * The algorithm which is chosen. Not the running algorithm.
     * Because when user press 'Start' and then the cloned algorithm starts running, the user can still choose a algorithm
     * which is not related to the running.
     */
    private AbstractAlgorithm nowSelectedAlgorithm;

    /**
     * A data model of result list. Include the algorithm and output
     */
    private ArrayList<ResultListItem> resultListItems = new ArrayList<>();
    /**
     * The only thread to do calculation using specific algorithm.
     */
    private Thread runningThread;

    @Override
    public boolean canCloseSafely() {
        return runningThread == null;
    }

    @Override
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
    }
}
