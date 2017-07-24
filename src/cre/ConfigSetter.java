package cre;

import cre.Config.*;
import cre.ui.*;
import cre.ui.custom.MyFormattedTextField;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

/**
 * Created by HanYizhao on 2017/4/6.
 * Some tools about configurations of an algorithm.
 */
public class ConfigSetter {

    /**
     * create a JPanel from which user can modify configurations
     *
     * @param frame  owner. In order to show a dialog.
     * @param config the object.
     * @return The view
     * @throws ConfigException
     */
    public static JPanel createAJPanel(final Window frame, final Object config) throws ConfigException {
        final ArrayList<ConfigBase> configBases = getConfigsFromObject(config);

        final ArrayList<Object> widgetList = new ArrayList<>();
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints left = new GridBagConstraints();
        int five = Tool.HighResolution(5);
        int ten = Tool.HighResolution(10);
        left.insets = new Insets(five, five, five, five);
        left.anchor = GridBagConstraints.WEST;
        left.gridwidth = 1;
        left.weightx = 0;
        GridBagConstraints right = new GridBagConstraints();
        right.insets = new Insets(five, five, five, five);
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
                final ConfigBoolean nowConfig = (ConfigBoolean) i;
                comboBox.addItemListener(new ItemListener() {
                    @Override
                    public void itemStateChanged(ItemEvent e) {
                        if (e.getStateChange() == ItemEvent.SELECTED) {
                            ConfigSetter.setFieldValue(config, nowConfig.getName(),
                                    boolean.class, e.getItem());
                        }
                    }
                });
                widgetList.add(comboBox);
            } else if (i instanceof ConfigInt) {
                MyFormattedTextField tf = new MyFormattedTextField(NumberFormat.getIntegerInstance());
                ConfigInt ci = (ConfigInt) i;
                mainPanel.add(tf, right);
                int original = ConfigSetter.getFieldInt(config, i.getName());
                tf.setValue(original);
                tf.setIntRange(ci.min, ci.max);
                tf.setToolTipText(i.getComments());
                final ConfigInt nowConfig = (ConfigInt) i;
                tf.addTextFieldChangeListener(new MyFormattedTextField.TextFieldChangeListener() {
                    @Override
                    public void textFieldChange(String newValue) {
                        try {
                            ConfigSetter.setFieldValue(config, nowConfig.getName(), int.class,
                                    Integer.parseInt(newValue));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                widgetList.add(tf);
            } else if (i instanceof ConfigDouble) {
                MyFormattedTextField tf = new MyFormattedTextField(new DecimalFormat());
                ConfigDouble cd = (ConfigDouble) i;
                mainPanel.add(tf, right);
                tf.setValue(ConfigSetter.getFieldDouble(config, i.getName()));
                tf.setDoubleRange(cd.min, cd.max);
                tf.setToolTipText(i.getComments());
                final ConfigDouble nowConfig = (ConfigDouble) i;
                tf.addTextFieldChangeListener(new MyFormattedTextField.TextFieldChangeListener() {
                    @Override
                    public void textFieldChange(String newValue) {
                        try {
                            ConfigSetter.setFieldValue(config, nowConfig.getName(), double.class,
                                    Double.parseDouble(newValue));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                widgetList.add(tf);
            } else if (i instanceof ConfigString) {
                if (((ConfigString) i).options == null) {
                    MyFormattedTextField tf = new MyFormattedTextField();
                    mainPanel.add(tf, right);
                    String text = ConfigSetter.getFieldString(config, i.getName());
                    if (text != null) {
                        tf.setValue(text);
                    }
                    tf.setToolTipText(i.getComments());
                    final ConfigString nowConfig = (ConfigString) i;
                    tf.addTextFieldChangeListener(new MyFormattedTextField.TextFieldChangeListener() {
                        @Override
                        public void textFieldChange(String newValue) {
                            try {
                                ConfigSetter.setFieldValue(config, nowConfig.getName(), String.class,
                                        newValue);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    widgetList.add(tf);
                } else {
                    JComboBox<String> comboBox = new JComboBox<>(((ConfigString) i).options);
                    mainPanel.add(comboBox, right);
                    comboBox.setSelectedItem(ConfigSetter.getFieldString(config, i.getName()));
                    comboBox.setToolTipText(i.getComments());
                    final ConfigString nowConfig = (ConfigString) i;
                    comboBox.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            if (e.getStateChange() == ItemEvent.SELECTED) {
                                ConfigSetter.setFieldValue(config, nowConfig.getName(),
                                        String.class, e.getItem());
                            }
                        }
                    });
                    widgetList.add(comboBox);
                }
            } else if (i instanceof ConfigClassify) {
                final MyFormattedTextField tf = new MyFormattedTextField();
                mainPanel.add(tf, right);
                final ConfigClassify classify = (ConfigClassify) i;
                tf.setValue(ConfigClassify.toString(config, classify));
                tf.setTag(ConfigSetter.getFieldTreeMap(config, i.getName()));
                tf.setToolTipText(i.getComments());
                widgetList.add(tf);
                tf.setEditable(false);
                tf.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        ClassifyDialog dialog = new ClassifyDialog(frame,
                                config, classify,
                                ((TreeMap<String, java.util.List<Integer>>) tf.getTag()));
                        dialog.setVisible(true);
                        if (dialog.OK()) {
                            TreeMap<String, java.util.List<Integer>> map = dialog.getNewMap();
                            tf.setTag(map);
                            tf.setValue(ConfigClassify.toString(map, classify));
                            ConfigSetter.setFieldValue(config, classify.getName(), TreeMap.class, map);
                        }
                    }
                });
            }
        }
        GridBagConstraints tipConstraints = new GridBagConstraints();
        tipConstraints.anchor = GridBagConstraints.EAST;
        tipConstraints.gridwidth = GridBagConstraints.REMAINDER;
        return mainPanel;
    }


    /**
     * Get fields of a Object. These fields will be configurations.
     *
     * @param config The object
     * @return The list of configuration
     * @throws ConfigException
     */
    public static ArrayList<ConfigBase> getConfigsFromObject(Object config) throws ConfigException {
        ArrayList<ConfigBase> configBases = new ArrayList<>();
        Class<?> c = config.getClass();
        Field[] fields = c.getDeclaredFields();
        for (Field i : fields) {
            Class<?> fi = i.getType();
            String name = i.getName();
            String comments = getFieldComment(config, name);
            String shownName = getFieldShownName(config, name);
            if (shownName == null) {
                shownName = name;
            }
            if (fi == int.class) {
                ConfigInt configInt = new ConfigInt(name, comments, shownName);
                configInt.max = getFieldIntMax(config, name);
                configInt.min = getFieldIntMin(config, name);
                configBases.add(configInt);
            } else if (fi == double.class) {
                ConfigDouble configDouble = new ConfigDouble(name, comments, shownName);
                configDouble.max = getFieldDoubleMax(config, name);
                configDouble.min = getFieldDoubleMin(config, name);
                configBases.add(configDouble);
            } else if (fi == boolean.class) {
                ConfigBoolean configBoolean = new ConfigBoolean(name, comments, shownName);
                configBases.add(configBoolean);
            } else if (fi == String.class) {
                ConfigString configString = new ConfigString(name, comments, shownName);
                configString.options = getFieldStringRange(config, name);
                configBases.add(configString);
            } else if (fi == TreeMap.class) {
                ConfigClassify configClassify = new ConfigClassify(name, comments, shownName);
                configClassify.classNames = getFieldTreeMapClasses(config, name);
                configClassify.attributeNames = getFieldTreeMapNames(config, name);
                if (configClassify.classNames == null) {
                    throw new ConfigException("Cannot find class of " + name + ". Missing Method?");
                }
                if (configClassify.attributeNames == null) {
                    throw new ConfigException("Cannot find names of " + name + ". Missing Method?");
                }
                configBases.add(configClassify);
            }
        }
        return configBases;
    }

    public static class ConfigException extends Exception {
        public ConfigException(String message) {
            super(message);
        }
    }

    private static String getFieldIsName(String fieldName) {
        return "is" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);

    }

    private static String getFieldGetName(String fieldName) {
        return "get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    private static String getFieldSetName(String fieldName) {
        return "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }


    /**
     * Get the Comment of a field.
     *
     * @param o         the object
     * @param fieldName name of field
     * @return the comment or null
     */
    public static String getFieldComment(Object o, String fieldName) {
        return (String) getMethodValue(o, getFieldGetName(fieldName) + "Comment", String.class);
    }

    /**
     * Get the shown name of a field.
     *
     * @param o         the object
     * @param fieldName name of field
     * @return the comment or null
     */
    public static String getFieldShownName(Object o, String fieldName) {
        return (String) getMethodValue(o, getFieldGetName(fieldName) + "ShownName", String.class);
    }

    /**
     * Get value of field whose class is boolean.
     *
     * @param o         the object
     * @param fieldName field name
     * @return value or false
     */
    public static boolean getFieldBoolean(Object o, String fieldName) {
        Object returned = getMethodValue(o, getFieldIsName(fieldName), boolean.class);
        return returned != null && ((boolean) returned);
    }

    /**
     * set value
     *
     * @param o         the object
     * @param fieldName name of field
     * @param valueType class of value
     * @param value     value
     * @return success or not
     */
    public static boolean setFieldValue(Object o, String fieldName, Class<?> valueType, Object value) {
        return setMethodValue(o, getFieldSetName(fieldName), valueType, value);
    }

    /**
     * Get value of field whose class is int.
     *
     * @param o         the object
     * @param fieldName field name
     * @return value or 0
     */
    public static int getFieldInt(Object o, String fieldName) {
        Object returned = getMethodValue(o, getFieldGetName(fieldName), int.class);
        return returned == null ? 0 : ((int) returned);
    }

    /**
     * Get the max value of a field whose class is int.
     *
     * @param o         the object
     * @param fieldName name of field
     * @return max value or Integer.MAX_VALUE
     */
    public static int getFieldIntMax(Object o, String fieldName) {
        Object returned = getMethodValue(o, getFieldGetName(fieldName) + "Max", int.class);
        return returned == null ? Integer.MAX_VALUE : ((int) returned);
    }

    /**
     * Get the min value of a field whose class is int.
     *
     * @param o         the object
     * @param fieldName name of field
     * @return min value or Integer.MIN_VALUE
     */
    public static int getFieldIntMin(Object o, String fieldName) {
        Object returned = getMethodValue(o, getFieldGetName(fieldName) + "Min", int.class);
        return returned == null ? Integer.MIN_VALUE : ((int) returned);
    }

    /**
     * Get of value field whose class is double.
     *
     * @param o         the object
     * @param fieldName field name
     * @return value or 0
     */
    public static double getFieldDouble(Object o, String fieldName) {
        Object returned = getMethodValue(o, getFieldGetName(fieldName), double.class);
        return returned == null ? 0 : ((double) returned);
    }

    /**
     * Get of value field whose class is string.
     *
     * @param o         the object
     * @param fieldName field name
     * @return value or null
     */
    public static String getFieldString(Object o, String fieldName) {
        Object returned = getMethodValue(o, getFieldGetName(fieldName), String.class);
        return returned == null ? null : ((String) returned);
    }

    /**
     * Get of value field whose class is TreeMap.
     *
     * @param o         the object
     * @param fieldName field name
     * @return value or null
     */
    public static TreeMap<String, java.util.List<Integer>> getFieldTreeMap(Object o, String fieldName) {
        Object returned = getMethodValue(o, getFieldGetName(fieldName), TreeMap.class);
        return returned == null ? null : ((TreeMap<String, java.util.List<Integer>>) returned);
    }

    /**
     * Get the max value of a field whose class is double.
     *
     * @param o         the object
     * @param fieldName name of field
     * @return max value or Double.MAX_VALUE
     */
    public static double getFieldDoubleMax(Object o, String fieldName) {
        Object returned = getMethodValue(o, getFieldGetName(fieldName) + "Max", double.class);
        return returned == null ? Double.MAX_VALUE : ((double) returned);
    }

    /**
     * Get the min value of a field whose class is double.
     *
     * @param o         the object
     * @param fieldName name of field
     * @return min value or Double.MIN_VALUE
     */
    public static double getFieldDoubleMin(Object o, String fieldName) {
        Object returned = getMethodValue(o, getFieldGetName(fieldName) + "Min", double.class);
        return returned == null ? -Double.MAX_VALUE : ((double) returned);
    }

    public static String[] getFieldStringRange(Object o, String fieldName) {
        Object returned = getMethodValue(o, getFieldGetName(fieldName) + "List", String[].class);
        return ((String[]) returned);
    }

    public static String[] getFieldTreeMapClasses(Object o, String fieldName) {
        Object returned = getMethodValue(o, getFieldGetName(fieldName) + "Classes", String[].class);
        return ((String[]) returned);
    }

    public static String[] getFieldTreeMapNames(Object o, String fieldName) {
        Object returned = getMethodValue(o, getFieldGetName(fieldName) + "Names", String[].class);
        return ((String[]) returned);
    }


    /**
     * Invoke method whose name is methodName
     *
     * @param o          the object
     * @param methodName method Name
     * @param valueType  the type of value
     * @param value      value
     * @return success or not
     */
    private static boolean setMethodValue(Object o, String methodName, Class valueType, Object value) {
        try {
            Class<?> c = o.getClass();
            Method m = c.getDeclaredMethod(methodName, valueType);
            try {
                m.invoke(o, value);
                return true;
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * Invoke method whose name is methodName
     *
     * @param o          the object
     * @param methodName method Name
     * @param returnType the type of returned value of this method
     * @return value or null
     */
    private static Object getMethodValue(Object o, String methodName, Class returnType) {
        try {
            Class<?> c = o.getClass();
            Method m = c.getDeclaredMethod(methodName);
            if (m.getReturnType() == returnType) {
                try {
                    return m.invoke(o);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        } catch (NoSuchMethodException e) {

        }
        return null;
    }
}
