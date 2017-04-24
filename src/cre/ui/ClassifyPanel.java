package cre.ui;

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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by HanYizhao on 4/13/2017.
 */
public class ClassifyPanel extends MyPanel implements CanShowOutput {

    public ClassifyPanel(MainFrameEventHandler mainFrame) {
        this.mainFrame = mainFrame;
        this.setLayout(new BorderLayout());
        JPanel topPanel = new JPanel(new GridBagLayout());
        this.add(topPanel, BorderLayout.NORTH);
        topPanel.setBorder(new TitledBorder("Classifier"));
        GridBagConstraints s = new GridBagConstraints();
        s.weightx = 0;
        s.insets = new Insets(10, 10, 10, 5);
        s.gridwidth = 1;
        topPanel.add(chooseButton, s);
        s.insets = new Insets(10, 5, 10, 10);
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

        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setPreferredSize(new Dimension(200, 0));
        this.add(leftPanel, BorderLayout.WEST);
        s.weightx = 1;
        s.gridwidth = 1;
        s.insets = new Insets(10, 7, 5, 5);
        leftPanel.add(startButton, s);
        s.weightx = 1;
        s.gridwidth = 0;
        s.insets = new Insets(10, 5, 5, 7);
        leftPanel.add(stopButton, s);
        JScrollPane scrollPane = new JScrollPane(resultList);
        scrollPane.setBorder(new TitledBorder("Result list"));
        s.gridwidth = 0;
        s.weightx = 1;
        s.weighty = 1;
        s.fill = GridBagConstraints.BOTH;
        s.insets = new Insets(0, 5, 5, 5);
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
                    restoreTextAreaStatus();
                }
            }
        });
    }

    private int oldSelectionIndex = 0;

    private void restoreTextAreaStatus() {
        resultListItems.get(oldSelectionIndex).messageBuffer = new StringBuilder(textArea.getText());
        oldSelectionIndex = resultList.getSelectedIndex();
        textArea.setText(resultListItems.get(oldSelectionIndex).messageBuffer.toString());
    }

    private void saveTextAreaStatus() {
        System.out.println("saveTextAreaStatus " + resultList.getSelectedIndex());
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
    }

    private void startButtonClicked() {
        if (nowSelectedAlgorithm == null) {
            JOptionPane.showMessageDialog(mainFrame.getFrame(), "Please Choose a Classifier first");
        } else {
            startButton.setEnabled(false);
            stopButton.setEnabled(true);
            ResultListItem item = new ResultListItem();
            item.algorithm = nowSelectedAlgorithm;
            resultListItems.add(item);
            listModel.addElement(new SimpleDateFormat().format(new Date()) + " - " + nowSelectedAlgorithm.toString());
            resultList.setSelectedIndex(listModel.size() - 1);
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    CanShowStatus canShowStatus = mainFrame.getCanShowStatus();
                    canShowStatus.showStatus("Starting...");
                    nowSelectedAlgorithm.doAlgorithm(ClassifyPanel.this, canShowStatus);
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

    private void calculatingFinished() {
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        runningThread = null;
    }

    public void setNowSelectedFile(File nowSelectedFile) {
        this.nowSelectedFile = nowSelectedFile;
        if (nowSelectedAlgorithm != null) {
            nowSelectedAlgorithm = nowSelectedAlgorithm.getCloneBecauseChangeOfFile(nowSelectedFile);
        }
    }

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

    private void refreshConfigTextField() {
        if (nowSelectedAlgorithm != null) {
            configTextField.setText(nowSelectedAlgorithm.toString());
        } else {
            configTextField.setText("");
        }
    }

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

    private MainFrameEventHandler mainFrame;

    private File nowSelectedFile;

    private AbstractAlgorithm nowSelectedAlgorithm;

    private ArrayList<ResultListItem> resultListItems = new ArrayList<>();

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


    private class ResultListItem {
        AbstractAlgorithm algorithm;
        StringBuilder messageBuffer = new StringBuilder();
    }
}
