package cre.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by HanYizhao on 2017/4/21.
 */
public class ChooseFromStringList extends JDialog {
    public ChooseFromStringList(Frame owner, String title, String[] stringList) {
        super(owner, title, ModalityType.DOCUMENT_MODAL);
        JPanel rootPanel = new JPanel(new GridBagLayout());
        this.add(rootPanel);
        list = new JList<>(stringList);
        GridBagConstraints s = new GridBagConstraints();
        s.weighty = 1;
        s.weightx = 1;
        s.gridwidth = 0;
        s.fill = GridBagConstraints.BOTH;
        rootPanel.add(new JScrollPane(list), s);
        JButton okButton = new JButton("OK");
        JButton cancelButton = new JButton("Cancel");
        s.weighty = 0;
        s.gridwidth = 1;
        s.fill = GridBagConstraints.HORIZONTAL;
        rootPanel.add(okButton, s);
        s.gridwidth = 0;
        rootPanel.add(cancelButton, s);
        this.getRootPane().setDefaultButton(okButton);
        this.pack();
        Tool.moveToCenter(this, true);

        if (stringList.length > 0) {
            list.setSelectedIndex(0);
        }

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                okButtonActionPerformed();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelButtonActionPerformed();
            }
        });
        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener() {
            @Override
            public void eventDispatched(AWTEvent event) {
                if (event.getID() == KeyEvent.KEY_PRESSED) {
                    if(((KeyEvent) event).getKeyCode() == KeyEvent.VK_ESCAPE){
                        ChooseFromStringList.this.dispose();
                    }
                }
            }
        }, AWTEvent.KEY_EVENT_MASK);
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    ChooseFromStringList.this.dispose();
                }
            }
        });

    }

    private JList<String> list;
    private int index = -1;

    /**
     * get the index of algorithm selected from 0 to ...
     * if there is no item selected, return -1
     *
     * @return index -1 to ...
     */
    public int getSelectedIndex() {
        return index;
    }


    private void okButtonActionPerformed() {
        index = list.getSelectedIndex();
        this.dispose();
    }

    private void cancelButtonActionPerformed() {
        this.dispose();
    }
}
