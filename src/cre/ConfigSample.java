package cre;

import java.util.List;
import java.util.TreeMap;

/**
 * Created by HanYizhao on 2017/4/6.
 */
public class ConfigSample {
    private int aaaaaaaaaaaaaaaaaa;
    private double b;
    private boolean c;
    private String d;
    private String dd;
    private TreeMap<String, List<Integer>> attri;
    private TreeMap<String, List<Integer>> malesOrNot;

    public String[] getMalesOrNotNames() {
        return new String[]{"Tina", "Bob", "Tom", "Helen"};
    }

    public String[] getMalesOrNotClasses() {
        return new String[]{"Male", "Female"};
    }

    public TreeMap<String, List<Integer>> getMalesOrNot() {
        return malesOrNot;
    }

    public void setMalesOrNot(TreeMap<String, List<Integer>> malesOrNot) {
        this.malesOrNot = malesOrNot;
    }

    public String getD() {
        return d;
    }

    public void setD(String d) {
        this.d = d;
    }

    public String getDd() {
        return dd;
    }

    public void setDd(String dd) {
        this.dd = dd;
    }

    public TreeMap<String, List<Integer>> getAttri() {
        return attri;
    }

    public void setAttri(TreeMap<String, List<Integer>> attri) {
        this.attri = attri;
    }

    public String[] getAttriNames() {
        return new String[]{"A", "B", "C", "D", "E"};
    }

    public String[] getAttriClasses() {
        return new String[]{"X", "Y", "E"};
    }


    public String[] getDList() {
        return new String[]{"123", "234"};
    }


    public double getBMax() {
        return 100;
    }


    public double getB() {
        return b;
    }

    public void setB(double b) {
        this.b = b;
    }

    public boolean isC() {
        return c;
    }

    public void setC(boolean c) {
        this.c = c;
    }

    public int getAaaaaaaaaaaaaaaaaa() {
        return aaaaaaaaaaaaaaaaaa;
    }

    public void setAaaaaaaaaaaaaaaaaa(int aaaaaaaaaaaaaaaaaa) {
        this.aaaaaaaaaaaaaaaaaa = aaaaaaaaaaaaaaaaaa;
    }

    public int getAaaaaaaaaaaaaaaaaaMax() {
        return 10;
    }

    public int getAaaaaaaaaaaaaaaaaaMin() {
        return -1;
    }

    public String getAaaaaaaaaaaaaaaaaaComment() {
        return "AA";
    }

}
