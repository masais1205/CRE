package cre;

import cre.Config.*;
import cre.ui.ConfigDialog;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by HanYizhao on 2017/4/6.
 */
public class ConfigSetter {
    public static void show(Frame ower, Object config) throws ConfigException {
        ArrayList<ConfigBase> configs = getConfigsFromObject(config);
        ConfigDialog dialog = new ConfigDialog(ower, config, configs);
        dialog.setVisible(true);
    }


    private static ArrayList<ConfigBase> getConfigsFromObject(Object config) throws ConfigException {
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
    public static boolean setMethodValue(Object o, String methodName, Class valueType, Object value) {
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
    public static Object getMethodValue(Object o, String methodName, Class returnType) {
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
