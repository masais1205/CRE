package cre.ui;

import cre.Config.*;
import cre.ConfigSetter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;

/**
 * Created by HanYizhao on 2017/4/6.
 */
public class ConfigDialog extends JDialog {

    public ConfigDialog(Frame owner, final Object config,
                        final ArrayList<ConfigBase> configBases) {
        super(owner, true);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        final ArrayList<Object> widgetList = new ArrayList<>();
        JPanel rootPanel = new JPanel(new BorderLayout());
        this.add(rootPanel);
        JScrollPane scrollPane = new JScrollPane();
        rootPanel.add(scrollPane, BorderLayout.CENTER);
        JPanel mainPanelContainer = new JPanel(new BorderLayout());
        JPanel mainPanel = new JPanel();
        mainPanelContainer.add(mainPanel, BorderLayout.NORTH);
        scrollPane.setViewportView(mainPanelContainer);

        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints left = new GridBagConstraints();
        left.insets = new Insets(5, 10, 5, 5);
        left.anchor = GridBagConstraints.EAST;
        left.gridwidth = 1;
        left.weightx = 0;
        GridBagConstraints right = new GridBagConstraints();
        right.insets = new Insets(5, 5, 5, 10);
        right.gridwidth = 0;
        right.weightx = 1;
        right.fill = GridBagConstraints.HORIZONTAL;
        for (ConfigBase i : configBases) {
            JLabel label = new JLabel(i.getShownName());
            mainPanel.add(label, left);
            if (i instanceof ConfigBoolean) {
                JComboBox<Boolean> comboBox = new JComboBox<>(new Boolean[]{true, false});
                mainPanel.add(comboBox, right);
                comboBox.setSelectedItem(ConfigSetter.getFieldBoolean(config, i.getName()));
                comboBox.setToolTipText(i.getComments());
                widgetList.add(comboBox);
            } else if (i instanceof ConfigInt) {
                MyTextField tf = new MyTextField(40);
                ConfigInt ci = (ConfigInt) i;
                mainPanel.add(tf, right);
                int original = ConfigSetter.getFieldInt(config, i.getName());
                tf.setText(original + "");
                tf.setIntRange(ci.min, ci.max);
                tf.setToolTipText(i.getComments());
                widgetList.add(tf);
            } else if (i instanceof ConfigDouble) {
                MyTextField tf = new MyTextField(40);
                ConfigDouble cd = (ConfigDouble) i;
                mainPanel.add(tf, right);
                tf.setText(ConfigSetter.getFieldDouble(config, i.getName()) + "");
                tf.setDoubleRange(cd.min, cd.max);
                tf.setToolTipText(i.getComments());
                widgetList.add(tf);
            } else if (i instanceof ConfigString) {
                if (((ConfigString) i).options == null) {
                    MyTextField tf = new MyTextField(40);
                    mainPanel.add(tf, right);
                    String text = ConfigSetter.getFieldString(config, i.getName());
                    if (text != null) {
                        tf.setText(text);
                    }
                    tf.setToolTipText(i.getComments());
                    widgetList.add(tf);
                } else {
                    JComboBox<String> comboBox = new JComboBox<>(((ConfigString) i).options);
                    mainPanel.add(comboBox, right);
                    comboBox.setSelectedItem(ConfigSetter.getFieldString(config, i.getName()));
                    comboBox.setToolTipText(i.getComments());
                    widgetList.add(comboBox);
                }
            } else if (i instanceof ConfigClassify) {
                final MyTextField tf = new MyTextField(40);
                mainPanel.add(tf, right);
                final ConfigClassify classify = (ConfigClassify) i;
                tf.setText(ConfigClassify.toString(config, classify));
                tf.setTag(ConfigSetter.getFieldTreeMap(config, i.getName()));
                tf.setToolTipText(i.getComments());
                widgetList.add(tf);
                tf.setEditable(false);
                tf.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        ClassifyDialog dialog = new ClassifyDialog(ConfigDialog.this,
                                config, classify,
                                ((TreeMap<String, List<Integer>>) tf.getTag()));
                        dialog.setVisible(true);
                        if (dialog.OK()) {
                            TreeMap<String, List<Integer>> map = dialog.getNewMap();
                            tf.setTag(map);
                            tf.setText(ConfigClassify.toString(map, classify));
                        }
                    }
                });
            }
        }
        JButton buttonOK = new JButton("OK");
        JButton buttonCancel = new JButton("Cancel");
        buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ConfigDialog.this.dispose();
            }
        });

        buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int i = 0; i < configBases.size(); i++) {
                    ConfigBase cb = configBases.get(i);
                    Object widget = widgetList.get(i);
                    if (cb instanceof ConfigBoolean) {
                        JComboBox ww = (JComboBox) widget;
                        ConfigSetter.setFieldValue(config, cb.getName(), boolean.class, ww.getSelectedItem());
                    } else if (cb instanceof ConfigInt) {
                        MyTextField tf = (MyTextField) widget;
                        try {
                            int newValue = Integer.parseInt(tf.getText());
                            ConfigSetter.setFieldValue(config, cb.getName(), int.class, newValue);
                        } catch (NumberFormatException ignore) {
                        }
                    } else if (cb instanceof ConfigDouble) {
                        MyTextField tf = (MyTextField) widget;
                        try {
                            double newValue = Double.parseDouble(tf.getText());
                            ConfigSetter.setFieldValue(config, cb.getName(), double.class, newValue);
                        } catch (NumberFormatException ignore) {
                        }
                    } else if (cb instanceof ConfigString) {
                        ConfigString cs = (ConfigString) cb;
                        if (cs.options == null) {
                            ConfigSetter.setFieldValue(config, cb.getName(), String.class, ((MyTextField) widget).getText());
                        } else {
                            ConfigSetter.setFieldValue(config, cb.getName(), String.class, ((JComboBox) widget).getSelectedItem());
                        }
                    } else if (cb instanceof ConfigClassify) {
                        ConfigSetter.setFieldValue(config, cb.getName(), TreeMap.class, ((MyTextField) widget).getTag());
                    }
                }
                ConfigDialog.this.dispose();
            }
        });

        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.add(buttonOK);
        panel.add(buttonCancel);
        right.anchor = GridBagConstraints.EAST;
        right.fill = GridBagConstraints.NONE;
        right.gridwidth = 0;
        panel.setBackground(Color.red);
        mainPanel.add(panel, right);
        this.pack();
        Tool.moveToCenter(this, true);

    }
}
