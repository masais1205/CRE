package cre.algorithm.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by HanYizhao on 2017/4/24.
 */
public class TestConfig implements Cloneable {
    private double ZC;
    private TreeMap<String, List<Integer>> type;

    private String[] attributeNames;
    private String[] attributeClasses = {"W", "X", "Y", "remove"};

    @Override
    protected Object clone() {
        TestConfig c = null;
        try {
            c = (TestConfig) super.clone();
            if (this.type != null) {
                c.type = new TreeMap<>();
                for (Map.Entry<String, List<Integer>> i : this.type.entrySet()) {
                    List<Integer> tempList = new ArrayList<>(i.getValue());
                    c.type.put(i.getKey(), tempList);
                }
            }
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return c;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (type != null && attributeNames != null) {
            for (Map.Entry<String, List<Integer>> i : type.entrySet()) {
                sb.append(i.getKey());
                sb.append(":");
                List<String> s = new ArrayList<>();
                for (Integer k : i.getValue()) {
                    s.add(attributeNames[k]);
                }
                sb.append(Arrays.toString(s.toArray()));
                sb.append(" ");
            }
        }
        return "ZC=" + ZC + "[" + sb.toString() + "]";
    }

    public TestConfig(String fileName) {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(fileName));
            attributeNames = br.readLine().split(",");
            if (attributeNames.length >= 3) {
                type = new TreeMap<>();
                List<Integer> t = new ArrayList<>();
                t.add(0);
                type.put(attributeClasses[0], t);
                t = new ArrayList<>();
                t.add(attributeNames.length - 1);
                type.put(attributeClasses[2], t);
                t = new ArrayList<>();
                for (int i = 1; i < attributeNames.length - 1; i++) {
                    t.add(i);
                }
                type.put(attributeClasses[1], t);
                t = new ArrayList<>();
                type.put(attributeClasses[3], t);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String[] getTypeNames() {
        return attributeNames;
    }

    public String[] getTypeClasses() {
        return attributeClasses;
    }

    public String getZCShownName() {
        return "Threshold";
    }

    public String getTypeShownName() {
        return "Classify attributes";
    }

    public double getZCMax() {
        return 2;
    }

    public double getZCMin() {
        return 0.1;
    }

    public double getZC() {
        return ZC;
    }

    public void setZC(double ZC) {
        this.ZC = ZC;
    }

    public TreeMap<String, List<Integer>> getType() {
        return type;
    }

    public void setType(TreeMap<String, List<Integer>> type) {
        this.type = type;
    }
}
