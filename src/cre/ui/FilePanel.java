package cre.ui;

import cre.ui.custom.MyFixWidthPanel;
import cre.ui.custom.MyImageLabel;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by HanYizhao on 4/13/2017.
 * <p>
 * Until now, FilePanel has only one function, choose a file.
 * When user chooses a file, this class inform the MainFrameEventHandler(actually MainFrame) by calling its function selectANewFile(File).
 * <p>
 * Notice: user may choose a file repeatedly even when other algorithm is calculating.
 */
public class FilePanel extends JPanel {
    public FilePanel(MainFrameEventHandler mainFrame) {
        this.mainFrame = mainFrame;
        this.setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane();
        this.add(scrollPane, BorderLayout.CENTER);
        JPanel mainPanelContainer = new MyFixWidthPanel(new BorderLayout());
        JPanel mainPanel = new JPanel();
        mainPanelContainer.add(mainPanel, BorderLayout.NORTH);
        scrollPane.setViewportView(mainPanelContainer);
        scrollPane.setBorder(null);
        mainPanel.setLayout(new GridBagLayout());

        GridBagConstraints s = new GridBagConstraints();
        s.weightx = 0;
        int ten = Tool.HighResolution(10);
        s.insets = new Insets(ten, ten, ten, ten);
        s.gridwidth = 1;
        JButton openFileButton = new JButton("Open file...");
        mainPanel.add(openFileButton, s);

        int fontSize = openFileButton.getFont().getSize();
        helpLabel = new MyImageLabel(getClass().getResource("/image/help.png"), getClass().getResource("/image/help_active.png"), new Dimension(fontSize, fontSize));
        mainPanel.add(helpLabel);

        s.weightx = 1;
        s.gridwidth = GridBagConstraints.REMAINDER;
        s.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(fileTextField, s);

        s.gridwidth = GridBagConstraints.REMAINDER;
        s.fill = GridBagConstraints.NONE;
        //mainPanel.add();


        fileTextField.setEditable(false);
        openFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFileButtonActionPerformed();
            }
        });

        helpLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Document document = new DefaultStyledDocument();
                MutableAttributeSet set = new SimpleAttributeSet();
                StyleConstants.setFontSize(set, Tool.HighResolution(14));
                StyleConstants.setBold(set, true);
                MutableAttributeSet set2 = new SimpleAttributeSet();
                StyleConstants.setFontSize(set2, Tool.HighResolution(12));
                StyleConstants.setBold(set2, false);
                try {
                    document.insertString(document.getLength(), "CDT", set);
                    document.insertString(document.getLength(),
                            " requires a comma-separated value (CSV) formatted file, in which\n" +
                                    "1. row headers are not permitted,\n" +
                                    "2. column headers (first row) must be attribute names,\n" +
                                    "3. instances are store from row 2 onwards,\n" +
                                    "4. attributes have binary values (i.e. 0 or 1).\n\n", set2);
                    document.insertString(document.getLength(), "CR-CS", set);
                    document.insertString(document.getLength(), " requires an input data file in C4.5 format.\n\n", set2);
                    document.insertString(document.getLength(), "CR-PA", set);
                    document.insertString(document.getLength(), " requires an input data file in C4.5 format.\n\n", set2);
                } catch (BadLocationException e1) {
                    e1.printStackTrace();
                }
                TextInformationFrame frame = new TextInformationFrame("", document);
                frame.setVisible(true);
            }
        });

    }

    private MainFrameEventHandler mainFrame;
    private JTextField fileTextField = new JTextField(17);
    private MyImageLabel helpLabel;

    private static JFileChooser fileChooser;

    static {
        fileChooser = new JFileChooser();
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setMultiSelectionEnabled(false);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Data file (*.csv, *.names)", "csv", "names");
        FileNameExtensionFilter filter1 = new FileNameExtensionFilter("CSV data file (*.csv)", "csv");
        FileNameExtensionFilter filter2 = new FileNameExtensionFilter("C4.5 data file (*.names)", "names");
        fileChooser.addChoosableFileFilter(filter);
        fileChooser.addChoosableFileFilter(filter1);
        fileChooser.addChoosableFileFilter(filter2);
        fileChooser.setAcceptAllFileFilterUsed(false);
    }

    private void openFileButtonActionPerformed() {
        int ch = fileChooser.showDialog(this, null);
        if (ch == JFileChooser.APPROVE_OPTION) {
            File f = fileChooser.getSelectedFile();
            fileTextField.setText(f.getAbsolutePath());
            mainFrame.selectANewFile(f);
        }
    }

}
