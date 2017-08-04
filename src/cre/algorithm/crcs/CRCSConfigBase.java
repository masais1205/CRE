package cre.algorithm.crcs;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by HanYizhao on 2017/7/14.
 * <p>This class is the background class of {@link CRCSConfig}.</p>
 * <p>Store some options and some parameters which can not be modified by user.</p>
 */
public class CRCSConfigBase implements Cloneable {

    protected File nameFile;

    protected static String[] oddsRatioOrChiSquareOptions;
    protected static String[] maxRulesOptions = {"1", "2", "3", "4"};
    protected static String[] minSupportOptions = {"0.01", "0.05", "0.1"};

    protected static String oddsRatioName = "Odds ratio";
    protected static String ChiSquareName = "Chi square";
    protected static String oddsOrChiSplit = " - ";

    protected static String[] attributeClasses = {"Exposure", "Control", "Ignore", "Others"};
    protected String[] attributeNames;
    public PreprocessingLogic.retclass ret;

    protected static String[] recommendOptions = {"Recommended", "Forced"};
    protected int maxNumberOfControlVariablesMax;


    static {
        List<String> result = new ArrayList<>();
        String temp = oddsRatioName;
        String[] oddsOptions = new String[]{"Lower bound", "1.25", "1.5", "2.0", "2.5"};
        String[] ChiSquareOptions = new String[]{"90%", "95%", "99%", "99.9%"};
        for (String i : oddsOptions) {
            result.add(temp + oddsOrChiSplit + i);
        }
        temp = ChiSquareName;
        for (String i : ChiSquareOptions) {
            result.add(temp + oddsOrChiSplit + i);
        }
        oddsRatioOrChiSquareOptions = new String[result.size()];
        result.toArray(oddsRatioOrChiSquareOptions);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        CRCSConfigBase c = (CRCSConfigBase) super.clone();
        if (this.attributeNames != null) {
            c.attributeNames = Arrays.copyOf(this.attributeNames,
                    this.attributeNames.length);
        }
        return super.clone();
    }
}
