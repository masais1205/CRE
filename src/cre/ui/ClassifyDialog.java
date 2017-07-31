package cre.ui;

import com.sun.org.apache.xml.internal.utils.ListingErrorHandler;
import cre.Config.ConfigClassify;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

/**
 * Created by HanYizhao on 2017/4/7.
 */
public class ClassifyDialog extends JDialog {

    private final String[] classes;
    private final String[] names;
    private String[] inWhichClass;

    private boolean ok = false;

    public boolean OK() {
        return ok;
    }

    public TreeMap<String, List<Integer>> getNewMap() {
        TreeMap<String, List<Integer>> map = new TreeMap<>();
        for (String aClass : classes) {
            map.put(aClass, new ArrayList<Integer>());
        }
        for (int i = 0; i < inWhichClass.length; i++) {
            List<Integer> l = map.get(inWhichClass[i]);
            l.add(i);
        }
        return map;
    }

    public ClassifyDialog(Window owner, Object object, ConfigClassify classify, TreeMap<String, List<Integer>> map) {
        super(owner, ModalityType.DOCUMENT_MODAL);
        classes = classify.classNames;
        names = classify.attributeNames;
        inWhichClass = new String[names.length];
        for (Map.Entry<String, List<Integer>> i : map.entrySet()) {
            for (Integer k : i.getValue()) {
                inWhichClass[k] = i.getKey();
            }
        }
        JPanel rootPanel = new JPanel(new BorderLayout());
        this.add(rootPanel);
        JScrollPane scrollPane = new JScrollPane();
        rootPanel.add(scrollPane, BorderLayout.CENTER);
        JPanel mainPanelContainer = new JPanel(new BorderLayout());
        JPanel mainPanel = new JPanel();
        mainPanelContainer.add(mainPanel, BorderLayout.NORTH);
        scrollPane.setViewportView(mainPanelContainer);

        mainPanel.setLayout(new GridBagLayout());

        JTable table = new JTable();
        MyTableModel model = new MyTableModel();
        table.setRowHeight(Tool.HighResolution(20));
        table.setModel(model);
        JComboBox<String> column = new JComboBox<>(classes);
        table.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(column));
        table.getColumnModel().getColumn(2).setCellRenderer(new ComboBoxColumn());
        GridBagConstraints s = new GridBagConstraints();
        s.gridwidth = 0;
        s.weightx = 1;
        s.fill = GridBagConstraints.HORIZONTAL;
        int ten = Tool.HighResolution(10), five = Tool.HighResolution(5);
        s.insets = new Insets(five, ten, 0, ten);
        mainPanel.add(table.getTableHeader(), s);
        s.insets = new Insets(0, ten, five, ten);
        mainPanel.add(table, s);
        s.fill = GridBagConstraints.NONE;
        s.anchor = GridBagConstraints.EAST;
        JButton buttonOk = new JButton("OK");
        buttonOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ok = true;
                ClassifyDialog.this.dispose();
            }
        });
        mainPanel.add(buttonOk, s);
        this.pack();
        Dimension oldSize = getSize();
        this.setSize(Math.max(Tool.HighResolution(300), oldSize.width), oldSize.height);
        Tool.moveToCenter(this, true);
    }

    class ComboBoxColumn implements TableCellRenderer {

        JComboBox<String> comboBox;

        public ComboBoxColumn() {
            this.comboBox = new JComboBox<>(classes);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            comboBox.setSelectedItem(value);
            return comboBox;
        }
    }

    class MyTableModel extends AbstractTableModel {

        private Class<?>[] cellType = {int.class, String.class, JComboBox.class};
        private String[] columnNames = {"ID", "Attribute", "Class"};

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return cellType[columnIndex];
        }

        @Override
        public String getColumnName(int column) {
            return columnNames[column];
        }

        @Override
        public int getRowCount() {
            return names.length;
        }

        @Override
        public int getColumnCount() {
            return 3;
        }


        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            if (columnIndex == 2) {
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (columnIndex == 2) {
                inWhichClass[rowIndex] = (String) aValue;
            }
        }

        @Override
        public Object getValueAt(int rowIndex, int
                columnIndex) {
            if (columnIndex == 0) {
                return rowIndex + 1;
            } else if (columnIndex == 1) {
                return names[rowIndex];
            } else {
                return inWhichClass[rowIndex];
            }
        }
    }
}
