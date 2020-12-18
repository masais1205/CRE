package cre.algorithm.test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by HanYizhao on 2017/4/24.
 */
public class TestConfig extends TestConfigBase implements Cloneable {
    private double ZC;
    private double oddsRatio;
    private int mergeDepth;
    private boolean featureSelection;
    private String mergeStrategy;
    private String significanceLevel;
    private double reliabilityMinSupport;
    private TreeMap<String, List<Integer>> type;
    private boolean debug;

    @Override
    public Object clone() {
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
        return "mergeDepth=" + mergeDepth + " oddsRatio=" + oddsRatio + " ZC=" + ZC + "[" + sb.toString() + "]";
    }

    public TestConfig(String fileName) {
        this.setZC(1.96);
        this.setOddsRatio(2);
        this.setMergeDepth(1);
        this.setFeatureSelection(false);
        this.setMergeStrategy("Reliability first");
        this.setSignificanceLevel("90%");
        this.setReliabilityMinSupport(0.01);
        this.setDebug(false);
        super.fileName = fileName;
    }

    public TestConfig(String fileName, boolean featureSelection, String significanceLevel) {
        this.setZC(1.96);
        this.setOddsRatio(2);
        this.setMergeDepth(1);
        this.setFeatureSelection(featureSelection);
        this.setMergeStrategy("Reliability first");
        this.setSignificanceLevel(significanceLevel);
        this.setReliabilityMinSupport(0.01);
        this.setDebug(false);
        super.fileName = fileName;
    }

    public void init() throws Exception {
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
            } else {
                throw new Exception("The count of attributes less than tree.");
            }
        } catch (IOException e) {
            throw e;
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

    public boolean getFeatureSelection() { return featureSelection; }
    public void setFeatureSelection(boolean featureSelection) { this.featureSelection = featureSelection; }
    public String getFeatureSelectionShownName() {
        return "Feature selection";
    }
    public String getFeatureSelectionComment() {
        return "Automated feature selection, yes/no";
    }

    public boolean getDebug() { return debug; }
    public void setDebug(boolean debug) { this.debug = debug; }
    public String getDebugShownName() {
        return "Debug Mode";
    }
    public String getDebugComment() {
        return "Debug Mode, yes/no";
    }

    public boolean getMergeStrategyVisible() { return false; }
    public String getMergeStrategy() {return mergeStrategy;}
    public void setMergeStrategy(String mergeStrategy) {
        this.mergeStrategy = mergeStrategy;
    }
    public String getMergeStrategyShownName() { return "Merge strategy"; }
    public String[] getMergeStrategyList() { return new String[]{"Reliability first", "Treatment effect homogeneity first"}; }
    public String getMergeStrategyComment() { return "Two different strategies to generalise unreliable patterns to reliable patterns"; }

    public boolean getSignificanceLevelVisible() { return true; }
    public String getSignificanceLevel() { return significanceLevel; }
    public double transSignificanceLevel() {
//        confidence level, 1.645(90%), 1.96(95%), 2.33(98%), 2.58(99%)
        if(this.significanceLevel == "99%") return 2.58;
        else if(this.significanceLevel == "95%")  return 1.96;
        else return 1.645;
    }
    public void setSignificanceLevel(String significanceLevel) {
        this.significanceLevel = significanceLevel;
    }
    public String getSignificanceLevelShownName() { return "Significance level"; }
    public String[] getSignificanceLevelList() { return new String[]{"99%", "95%", "90%"}; }
    public String getSignificanceLevelComment() { return "Statistical significance level"; }

    public boolean getReliabilityMinSupportVisible() { return false; }
    public double getReliabilityMinSupport() {return reliabilityMinSupport;}
    public void setReliabilityMinSupport(double reliabilityMinSupport) {
        this.reliabilityMinSupport = reliabilityMinSupport;
    }
    public String getReliabilityMinSupportShownName() {return "Reliability minimal support";}
    public double getReliabilityMinSupportMax() {return 0.1;}
    public double getReliabilityMinSupportMin() {return 0.0001;}

    public int getMergeDepth() {
        return mergeDepth;
    }
    public void setMergeDepth(int mergeDepth) {
        this.mergeDepth = mergeDepth;
    }
    public String getMergeDepthShownName() {
        return "Depth of merge";
    }
    public String getMergeDepthComment() {
        return "Must bigger than -1";
    }
    public int getMergeDepthMin() {
        return 0;
    }
    public boolean getMergeDepthVisible() { return false; }

    public String getOddsRatioShownName() {
        return "Odds Ratio";
    }
    public double getOddsRatioMax() {
        return 10;
    }
    public double getOddsRatioMin() {
        return 1.5;
    }
    public double getOddsRatio() {
        return oddsRatio;
    }
    public void setOddsRatio(double oddsRatio) {
        this.oddsRatio = oddsRatio;
    }
    public boolean getOddsRatioVisible() { return false; }

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
        return 10;
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
    public boolean getZCVisible() { return false; }

    public TreeMap<String, List<Integer>> getType() {
        return type;
    }

    public void setType(TreeMap<String, List<Integer>> type) {
        this.type = type;
    }
}
