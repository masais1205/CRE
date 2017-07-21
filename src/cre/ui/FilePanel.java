package cre.ui;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by HanYizhao on 4/13/2017.
 * Until now, FilePanel has only one function, choose a file.
 * When user chooses a file, this class inform the MainFrameEventHandler(actually MainFrame) by calling its function selectANewFile(File).
 * <p>
 * Notice: user may choose a file repeatly even when other algorithm is calculating.
 */
public class FilePanel extends JPanel {
    public FilePanel(MainFrameEventHandler mainFrame) {
        this.mainFrame = mainFrame;
        this.setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane();
        this.add(scrollPane, BorderLayout.CENTER);
        JPanel mainPanelContainer = new JPanel(new BorderLayout());
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
        mainPanel.add(openFileButton, s);
        s.weightx = 1;
        s.gridwidth = 0;
        s.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(fileTextField, s);

        fileTextField.setEditable(false);

        openFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFileButtonActionPerformed();
            }
        });
    }

    private MainFrameEventHandler mainFrame;
    private JButton openFileButton = new JButton("Open file...");
    private JTextField fileTextField = new JTextField(17);

    private static JFileChooser fileChooser;

    static {
        fileChooser = new JFileChooser();
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setMultiSelectionEnabled(false);
        FileNameExtensionFilter filter1 = new FileNameExtensionFilter("CSV data file (*.csv)", "csv");
        FileNameExtensionFilter filter2 = new FileNameExtensionFilter("C4.5 data file (*.names)", "names");
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
