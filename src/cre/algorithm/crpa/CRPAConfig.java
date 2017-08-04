package cre.algorithm.crpa;

import cre.algorithm.crcs.PreprocessingLogic;
import cre.algorithm.tool.FileTool;
import cre.algorithm.tool.OtherTool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by HanYizhao on 2017/7/19.
 * <p>The configuration file of CRPA algorithm</p>
 */
public class CRPAConfig extends CRPAConfigBase {
    public class P {
        public int num_combinedvariables; // the number of exposure variables
        public boolean oddsRatioOrChiSquare;
        public double oddsratio;
        public double gsup;
        public double ChisquareValue;
        public double PaValue;
    }


    /**
     * Copied from old CRE.
     */
    public class Values {
        public int[] ChosenTest;   //user chosen test attribute
        public int[] ChosenControl;   //user chosen control attribute
        public int Controlmethod;  //user chosen control method
    }

    private String maxRules;
    private String minSupport;
    private String oddsRatioOrChiSquare;
    private String paValue;
    private TreeMap<String, List<Integer>> type;
    private String recommend;

    public CRPAConfig(File nameFile) {
        this.maxRules = maxRulesOptions[0];
        this.oddsRatioOrChiSquare = oddsRatioOrChiSquareOptions[0];
        this.recommend = recommendOptions[0];
        this.minSupport = minSupportOptions[1];
        this.paValue = paValueOptions[1];
        super.nameFile = nameFile;
    }

    public void init() throws Exception {
        PreprocessingLogic pre = new PreprocessingLogic();
        pre.onlyGetNames(nameFile);
        super.ret = pre.getData();
        List<String> atts = new ArrayList<>();
        for (int i = 0; i < ret.maxAtt; i++) {
            if (!ret.specialStatus[i].equals("1")) {
                atts.add(ret.attName[i]);
            }
        }
        super.attributeNames = new String[atts.size()];
        atts.toArray(super.attributeNames);
        type = new TreeMap<>();
        List<Integer> l1 = new ArrayList<>();
        for (int i = 0; i < attributeNames.length; i++) {
            l1.add(i);
        }
        type.put(attributeClasses[0], new ArrayList<Integer>());
        type.put(attributeClasses[1], new ArrayList<Integer>());
        type.put(attributeClasses[2], new ArrayList<Integer>());
        type.put(attributeClasses[3], l1);
    }

    public String[] getPaValueList() {
        return paValueOptions;
    }

    public String getPaValueShownName() {
        return "PA test";
    }

    public String getPaValueComment() {
        return "The confidence level for the partial association tests. (default: 95%)";
    }

    public String getRecommendComment() {
        return "\nForced: user choose\n" +
                "Recommended: user choose + algrithm choose.";
    }

    public boolean getRecommendVisible() {
        return false;
    }

    public String[] getRecommendList() {
        return recommendOptions;
    }

    public boolean getTypeVisible() {
        return false;
    }

    public String[] getTypeNames() {
        return super.attributeNames;
    }

    public String[] getTypeClasses() {
        return attributeClasses;
    }

    public String[] getMaxRulesList() {
        return maxRulesOptions;
    }

    public String getMaxRulesShownName() {
        return "Max rule level";
    }

    public String getMaxRulesComment() {
        return "The maximum number of items in the LHS of a causal rule. For example, If 2 is selected, a causal rule discovered may contain 1 or 2 items in its LHS. (default: 1)";
    }

    public String[] getOddsRatioOrChiSquareList() {
        return oddsRatioOrChiSquareOptions;
    }

    public String getOddsRatioOrChiSquareShownName() {
        return "Association test";
    }

    public String getOddsRatioOrChiSquareComment() {
        return "\nOdds ratio -- lower bound: odds ratio is used to measure the strength of the relationship and lower bound approach is to test the significance of the relationship.\n" +
                "Odds ratio -- threshold: odds ratio is used to measure the strength of the relationship and a user selected odds ratio threshold is used.\n"
                + "Chi Square -- threshold: Chi square test is used with the selected confidence level.\n"
                + "(default: \"Odds ratio -- lower bound\"; see Appendix A in Reference [2] for more details)";
    }


    public String[] getMinSupportList() {
        return minSupportOptions;
    }

    public String getMinSupportShownName() {
        return "Min support";
    }

    public String getMinSupportComment() {
        return "The percentages of data samples that support a rule. (default: 0.05)";
    }

    @Override
    public Object clone() {
        CRPAConfig c = null;
        try {
            c = (CRPAConfig) super.clone();
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

    public String getNewNameFileContent() {
        List<String> stringlist = FileTool.getFileContent(nameFile);
        List<Integer> removedList = type.get(attributeClasses[2]);
        int withoutComment = -1;
        int withoutIgnore = 0;
        for (int i = 0; i < stringlist.size(); i++) {
            String temp = stringlist.get(i);
            if (temp.length() != 0 && !temp.startsWith("|")) {
                if (withoutComment != -1) {
                    if (!ret.specialStatus[withoutComment].equals("1")) {
                        if (removedList.contains(withoutIgnore)) {
                            String[] temps = temp.split(": ");
                            if (temps.length == 2) {
                                String newString = temps[0] + ": ignore.";
                                stringlist.set(i, newString);
                            }
                        }
                        withoutIgnore++;
                    }
                }
                withoutComment++;
            }
        }
        StringBuilder sb = new StringBuilder();
        String lineSeparator = OtherTool.getLineSeparator();
        for (String i : stringlist) {
            sb.append(i);
            sb.append(lineSeparator);
        }
        return sb.toString();
    }

    public Values toValues() {
        Values values = new Values();
        values.Controlmethod = this.recommend.equals(recommendOptions[0]) ? 0 : 1;
        List<Integer> removeList = type.get(attributeClasses[2]);
        List<Integer> list1 = type.get(attributeClasses[0]);
        List<Integer> list2 = type.get(attributeClasses[1]);
        List<Integer> result1 = new ArrayList<>();
        List<Integer> result2 = new ArrayList<>();
        int notIgnoreCount = 0;
        int originNotIgnoreCount = 0;
        for (int i = 0; i < ret.maxAtt; i++) {
            if (!ret.specialStatus[i].equals("1")) {
                if (!removeList.contains(originNotIgnoreCount)) {
                    if (list1.contains(originNotIgnoreCount)) {
                        result1.add(notIgnoreCount + 1);
                    } else if (list2.contains(originNotIgnoreCount)) {
                        result2.add(notIgnoreCount + 1);
                    }
                    notIgnoreCount++;
                }
                originNotIgnoreCount++;
            }
        }
        values.ChosenTest = OtherTool.fromIntegerListToArray(result1);
        values.ChosenControl = OtherTool.fromIntegerListToArray(result2);
        return values;
    }


    public P toP() {
        P p = new P();
        p.num_combinedvariables = Integer.parseInt(this.maxRules);
        p.gsup = Double.parseDouble(this.minSupport);
        if (this.oddsRatioOrChiSquare.startsWith(oddsRatioName)) {
            String value = this.oddsRatioOrChiSquare.substring(oddsRatioName.length() + oddsOrChiSplit.length());
            try {
                p.oddsratio = Double.parseDouble(value);
            } catch (NumberFormatException e) {
                p.oddsratio = 1;
            }
            p.ChisquareValue = 0;
            p.oddsRatioOrChiSquare = true;
        } else if (this.oddsRatioOrChiSquare.startsWith(ChiSquareName)) {
            String value = this.oddsRatioOrChiSquare.substring(ChiSquareName.length() + oddsOrChiSplit.length());
            switch (value) {
                case "95%":
                    p.ChisquareValue = 3.84;
                    break;
                case "90%":
                    p.ChisquareValue = 2.71;
                    break;
                case "99%":
                    p.ChisquareValue = 6.64;
                    break;
                case "99.9%":
                    p.ChisquareValue = 10.83;
                    break;
            }
            p.oddsratio = 0;
            p.oddsRatioOrChiSquare = false;
        } else {
            System.out.println("ERROR: oddsRatioOrChiSquare Value :" + this.oddsRatioOrChiSquare);
        }
        switch (paValue) {
            case "90%":
                p.PaValue = 2.71;
                break;
            case "95%":
                p.PaValue = 3.84;
                break;
            case "99%":
                p.PaValue = 6.64;
                break;
            case "99.9%":
                p.PaValue = 10.83;
                break;
        }
        return p;
    }

    public String getMaxRules() {
        return maxRules;
    }

    public void setMaxRules(String maxRules) {
        this.maxRules = maxRules;
    }

    public String getOddsRatioOrChiSquare() {
        return oddsRatioOrChiSquare;
    }

    public void setOddsRatioOrChiSquare(String oddsRatioOrChiSquare) {
        this.oddsRatioOrChiSquare = oddsRatioOrChiSquare;
    }

    public String getMinSupport() {
        return minSupport;
    }

    public void setMinSupport(String minSupport) {
        this.minSupport = minSupport;
    }

    public TreeMap<String, List<Integer>> getType() {
        return type;
    }

    public void setType(TreeMap<String, List<Integer>> type) {
        this.type = type;
    }

    public void setRecommend(String recommend) {
        this.recommend = recommend;
    }

    public String getRecommend() {
        return recommend;
    }

    public String getPaValue() {
        return paValue;
    }

    public void setPaValue(String paValue) {
        this.paValue = paValue;
    }

    @Override
    public String toString() {
        return "max Rules=" + maxRules +
                ", minSupport=" + minSupport +
                ", oddsRatioOrChiSquare=" + oddsRatioOrChiSquare +
                ", paValue=" + paValue +
                ", recommend=" + recommend;
    }

}
