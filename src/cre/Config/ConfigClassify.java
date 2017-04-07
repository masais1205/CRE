package cre.Config;

import cre.ConfigSetter;

import java.util.*;

/**
 * Created by HanYizhao on 2017/4/6.
 */
public class ConfigClassify extends ConfigBase {

    public String[] classNames;
    public String[] attributeNames;

    public ConfigClassify(String name, String comments, String shownName) {
        super(name, comments, shownName);
    }

    public static String toString(TreeMap<String, List<Integer>> map, ConfigClassify classify) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, List<Integer>> i : map.entrySet()) {
            sb.append(i.getKey());
            sb.append(":");
            List<String> s = new ArrayList<>();
            for (Integer k : i.getValue()) {
                s.add(classify.attributeNames[k]);
            }
            sb.append(Arrays.toString(s.toArray()));
            sb.append(" ");
        }
        return sb.toString();
    }


    public static String toString(Object o, ConfigClassify classify) {
        TreeMap<String, List<Integer>> map = ConfigSetter.getFieldTreeMap(o, classify.getName());
        if (map == null) {
            map = new TreeMap<>();
            List<Integer> integers = new ArrayList<>();
            for (int i = 0; i < classify.attributeNames.length; i++) {
                integers.add(i);
            }
            map.put(classify.classNames[0], integers);
            ConfigSetter.setFieldValue(o, classify.getName(), TreeMap.class, map);
        }
        return toString(map, classify);
    }

}
