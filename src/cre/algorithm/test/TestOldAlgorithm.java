package cre.algorithm.test;


import cre.Config.OtherConfig;
import cre.algorithm.CalculatingException;
import cre.algorithm.CanShowOutput;
import cre.algorithm.CanShowStatus;
import cre.algorithm.ToolFunctions;
import cre.algorithm.test.ce.*;

import cre.algorithm.tool.FileTool;
import cre.algorithm.tool.OtherTool;
import cre.algorithm.tool.TemporaryFileManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import org.apache.commons.math3.analysis.function.StepFunction;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import javax.xml.crypto.dom.DOMCryptoContext;

/**
 * Created by HanYizhao on 2017/3/9.
 */
public class TestOldAlgorithm {
    private static String delimiter = ",";

    public static Statistic do_it(String fileName, TestConfig config,
//                                  double ZC, double odd_ratio, int mergeDepth,  boolean featureSelection,
//                                  String mergeStrategy, double reliabilityMinSupport,
                                  int WP, int YP, int[] XPArray,
                                  int GT, int[] group, int testGroupId, OtherConfig otherConfig,
                                  CanShowStatus canShowStatus,
                                  CanShowOutput canShowOutput, boolean isTesting) throws CalculatingException {
        String testFileName = otherConfig.getTestFile();
        String groundTruthFileName = otherConfig.getGroundTruthFile();
        BufferedReader br = null;
        boolean debug = config.getDebug();

        // modified by mss
        int[] PCMembers = new int[XPArray.length];

//        String rFileName = "";
//        try {
//            File f = TemporaryFileManager.getInstance().releasePackedFile("/r_code/adjustment.R");
//            rFileName = f.getAbsolutePath();
//            canShowOutput.showOutputString(f.getAbsolutePath());
//        } catch (Exception e) {
//            canShowOutput.showOutputString(e.toString());
//            e.printStackTrace();
//        }

        try {
            int[] XPArray_R = new int[XPArray.length];
            RConnection c = new RConnection();
            String filePath = fileName.replace("\\","/");
//            String rFileName = "C:/Users/maysy020/Documents/adjustment.R";
            File f = TemporaryFileManager.getInstance().releasePackedFile("/r_code/adjustment.R");
            String rFileName = f.getAbsolutePath();
            rFileName = rFileName.replace("\\","/");
//            canShowOutput.showOutputString(rFileName);
            c.eval("source(\"" + rFileName + "\")");
            c.assign("fileName", fileName);
            int[] w = new int[1];
            w[0] = WP;
            c.assign("w", w);
            int[] y = new int[1];
            y[0] = YP;
            c.assign("y", y);
            int[] xArray = XPArray;
            c.assign("xArray", xArray);
            double[] alpha = new double[1];
            alpha[0] = 0.05;
            c.assign("alpha", alpha);
            c.assign("featureSelection", Boolean.toString(config.getFeatureSelection()));
            c.assign(".tmp.", "adjustment(fileName,w,y,xArray,alpha,featureSelection)");
            REXP r = c.parseAndEval("try(eval(parse(text=.tmp.)),silent=TRUE)");
            if (r.inherits("try-error")) {
                canShowOutput.showOutputString("Error: " + r.asString());
                c.close();
//                return 0;
            }

            REXP XPArray_REXP = c.eval("adjustment(fileName,w,y,xArray,alpha,featureSelection)");
            XPArray_R = XPArray_REXP.asIntegers();
//            for(int xp : XPArray_R)
//                canShowOutput.showOutputString(String.valueOf(xp));
//            canShowOutput.showOutputString("String.valueOf(xp)");

            boolean flag = true;
            int cnt_xp = 0;
            int cnt_xp_black = 0;
            for(int xp: XPArray_R) {
                if(xp < 0)
                    flag = false;
                else {
                    XPArray[cnt_xp] = xp;
                    cnt_xp++;
                }
                if(flag) {
                    PCMembers[cnt_xp_black] = xp;
                    cnt_xp_black++;
                }
            }
            XPArray = trimlength(XPArray, cnt_xp);
            PCMembers = trimlength(PCMembers, cnt_xp_black);
//            canShowOutput.showOutputString("Z");
//            for(int xp : XPArray)
//                canShowOutput.showOutputString(String.valueOf(xp));
//            canShowOutput.showOutputString("stop");

            c.close();
        } catch (Exception e) {
            canShowOutput.showOutputString(e.toString());
        } // mss
//        canShowOutput.showOutputString("Z+C"+Integer.toString(PCMembers.length));
//        for(int pc : PCMembers)
//            canShowOutput.showOutputString(String.valueOf(pc));

        int[] XPSorted;
        int[] XPReverseSorted;
        try {
            br = new BufferedReader(new FileReader(fileName));
            String header = br.readLine();
            String[] names = header.split(delimiter);
            OR[] orWX = new OR[XPArray.length];
            OR[] orYX = new OR[XPArray.length];
            for (int i = 0; i < XPArray.length; i++) {
                orWX[i] = new OR(i);
                orYX[i] = new OR(i);
            }


            boolean simpleTrueFalse;
            double YMedian = 0;

            Double YMedianResult = FileTool.getMedianOfAttribute(fileName,
                    delimiter, YP, names.length, group, testGroupId, canShowOutput);
            if (YMedianResult == null) {
                simpleTrueFalse = true;
            } else {
                simpleTrueFalse = false;
                YMedian = YMedianResult;
            }
            canShowOutput.showLogString("SimpleTrueFalse:" + simpleTrueFalse);

            char[] cBuffer = new char[XPArray.length];
            int count = 2;
            String tempS;
            boolean isTrainingSet;
            HashMap<String, AbstractCE> trainingData = new HashMap<>();
            HashMap<String, LineValue> testingData = new HashMap<>();
            int testingDataCount = 0;

            BufferedReader testBr = new BufferedReader(new FileReader(fileName));
            if (otherConfig.getValidation().toString().equals("SUPPLIED_TEST_DATA")) {
                testBr = new BufferedReader(new FileReader(testFileName));
                String testHeader = testBr.readLine();
                String[] testNames = testHeader.split(delimiter);
                for (int t = 0; t < names.length; t++) {
                    if (!names[t].equals(testNames[t])) {
                        canShowOutput.showOutputString("Error. Attributes names of training and testing data" +
                                " have to be consistent!");
                        return null;
                    }
                }
            }

            int trainingInstanceNumer = 0;
            while (true) {
                if (otherConfig.getValidation().toString().equals("SUPPLIED_TEST_DATA")) {
                    tempS = br.readLine();
                    if (tempS != null)
                        isTrainingSet = true;
//                        isTrainingSet = group == null || (group[count - 2] == testGroupId);
                    else {
                        tempS = testBr.readLine();
                        if (tempS == null)
                            break;
                        isTrainingSet = false;
                    }
                }
                else {
                    tempS = br.readLine();
                    if (tempS == null)
                        break;
                    isTrainingSet = group == null || (group[count - 2] != testGroupId);
                }
                String[] tempSS = tempS.split(delimiter);
                if (tempSS.length == names.length) {
                    double yValue = Double.parseDouble(tempSS[YP]);
                    boolean WValue = tempSS[WP].equals("1");
                    boolean YBooleanValue = simpleTrueFalse ? tempSS[YP].equals("1") : yValue > YMedian;
                    for (int i = 0; i < XPArray.length; i++) {
                        cBuffer[i] = tempSS[XPArray[i]].equals("1") ? '1' : '0';
                        if (isTrainingSet) {
                            orWX[i].addValue(WValue, tempSS[XPArray[i]].equals("1"));
                            orYX[i].addValue(YBooleanValue, tempSS[XPArray[i]].equals("1"));
                        }
                    }
                    String s = new String(cBuffer);
                    if (isTrainingSet) {
                        trainingInstanceNumer++;
                        if (simpleTrueFalse) {
                            AbstractCE cE = trainingData.get(s);
                            if (cE == null) {
                                cE = new TrueFalseCE(cBuffer);
                                trainingData.put(s, cE);
                            }
                            if (GT > 0)
                                ((TrueFalseCE) cE).updateGTValue(Double.parseDouble(tempSS[GT]));
                            ((TrueFalseCE) cE).addItem(WValue, YBooleanValue);
                        } else {
                            AbstractCE cE = trainingData.get(s);
                            if (cE == null) {
                                cE = new NumberCE(cBuffer);
                                trainingData.put(s, cE);
                            }
                            if (GT > 0)
                                ((NumberCE) cE).updateGTValue(Double.parseDouble(tempSS[GT]));
                            ((NumberCE) cE).addItem(WValue, yValue);
                        }
                    } else {
                        testingDataCount++;
                        LineValue lv = testingData.get(s);
                        if (lv == null) {
                            lv = new LineValue(cBuffer);
                            testingData.put(s, lv);
                        }
                        if (GT > 0)
                            lv.updateGTValue(Double.parseDouble(tempSS[GT]));
                        lv.addItem(WValue, YBooleanValue);
                    }
                } else {
                    canShowOutput.showOutputString("Line value ERROR: (line:" + count + ") " + tempS);
                    break;
                }
                count++;
            }
            /////////show OR
            {
                StringBuilder sb = new StringBuilder();
                sb.append("Treatment\t");
                sb.append(names[WP]);
                sb.append("\nOutcome\t");
                sb.append(names[YP]);
                sb.append("\nattribute\tY\tW\n");
                for (int i = 0; i < XPArray.length; i++) {
                    sb.append(names[XPArray[i]]);
                    sb.append("\t");
                    sb.append(String.format(Locale.ENGLISH, "%.2f", orYX[i].getOR(false)));
                    sb.append("\t");
                    sb.append(String.format(Locale.ENGLISH, "%.2f", orWX[i].getOR(false)));
                    sb.append("\n");
                }
                canShowOutput.showLogString("Odd Ratio");
                canShowOutput.showLogString(sb.toString());
            }
            ///////////////

            HashSet<Integer> orYXPNoFitOddsRatio = new HashSet<>();
            HashSet<Integer> orWXPNoFitOddsRatio = new HashSet<>();
            //ascending sort
            Arrays.sort(orWX, new Comparator<OR>() {
                @Override
                public int compare(OR o1, OR o2) {
                    double d1 = o1.getOR(false);
                    double d2 = o2.getOR(false);
                    return d1 > d2 ? 1 : (d1 < d2 ? -1 : 0);
                }
            });
            Arrays.sort(orYX, new Comparator<OR>() {
                @Override
                public int compare(OR o1, OR o2) {
                    double d1 = o1.getOR(false);
                    double d2 = o2.getOR(false);
                    return d1 > d2 ? 1 : (d1 < d2 ? -1 : 0);
                }
            });
            XPSorted = new int[XPArray.length];
            XPReverseSorted = new int[XPArray.length];
            for (int i = 0; i < orWX.length; i++) {
                Integer tempValue = ((Integer) orWX[i].getAttach());
                XPReverseSorted[orWX.length - i - 1] = tempValue;
                if (orWX[i].getOR(false) > config.getOddsRatio()) {
                    orWXPNoFitOddsRatio.add(tempValue);
                }
            }
            // modified by mss, add question black list (i.e. Z) to orYXPNoFitOddsRatio
            List<Integer> tempIndex = new ArrayList<>();
            if (PCMembers.length > 0) {
                for (int xp : PCMembers)
                    tempIndex.add(Arrays.binarySearch(XPArray, xp));
            }
            // mss
            for (int i = 0; i < orYX.length; i++) {
                Integer tempValue = ((Integer) orYX[i].getAttach());
                XPSorted[i] = tempValue;
                if (orYX[i].getOR(false) > config.getOddsRatio() && !orYXPNoFitOddsRatio.contains(tempValue)) {
//                    canShowOutput.showOutputString("OR threshold " + String.valueOf(XPArray[tempValue]));
                    orYXPNoFitOddsRatio.add(tempValue);
                } else {
                    if (tempIndex.size() > 0 && tempIndex.contains(tempValue) && !orYXPNoFitOddsRatio.contains(tempValue)) {
                        orYXPNoFitOddsRatio.add(tempValue);
//                        canShowOutput.showOutputString("C " + String.valueOf(XPArray[tempValue]));
                    }
                }
            }

            /////////!!!!! Jiuyong asked to make XPReverseSorted same as XPSorted
            System.arraycopy(XPSorted, 0, XPReverseSorted, 0, XPSorted.length);
            ////////!!!!!!!!!!!!!!!!!

            /////////////
            List<AbstractCE> mergeResult = new ArrayList<>();

            CEAlgorithm.doMergeTwoConstraints(trainingData.values(), GT, mergeResult, PCMembers, XPSorted,
                        XPReverseSorted, config, orYXPNoFitOddsRatio, canShowOutput, debug);

//
            //Log training result.
            if (!isTesting) {
                StringBuilder sb = new StringBuilder();
//                sb.append(mergeResult.size());
//                sb.append("\n");
                for (int i = 0; i < XPArray.length; i++) {
                    int p = XPArray[i];
                    if(IntStream.of(PCMembers).anyMatch(x -> x == p))
                        sb.append("[");
                    sb.append(names[p]);
                    if(IntStream.of(PCMembers).anyMatch(x -> x == p))
                        sb.append("]");
                    sb.append("\t");
                }
                if (simpleTrueFalse) {
                    sb.append("isSignificant\tn11\tn12\tn21\tn22\t");
                    sb.append("p1-p2\t");
                    sb.append("causalEffect");
                } else {
                    sb.append("isSignificant\tW=1\tW=0");
                }
                sb.append("\n");
                for (AbstractCE i : mergeResult) {
//                    canShowOutput.showOutputString(Boolean.toString(i.isSignificant));
                    sb.append(i.toString());
                    sb.append("\n");
                }
                canShowOutput.showOutputString(sb.toString());
            } else {

                // Training is finished. Start testing.
                CESearchTool searchTool = new CESearchTool(mergeResult);
                int notMatch = 0;
                int successInstance = 0;
                int allInstance = 0;
                int allInstanceIncludeQuestion = 0;
                canShowOutput.showLogString("\n===Testing process===");
                HashMap<String, LineValue> testDataStatistic = new HashMap<>();

                // group data with same att values
                for (LineValue lv : testingData.values()) {
                    AbstractCE patt = searchTool.getNearestFreqPatt(lv.getValue());
                    char[] charValue = patt.value;
                    if (charValue != null) {
                        String tS = new String(charValue);
                        LineValue testLv = testDataStatistic.get(tS);
                        if (testLv == null) {
                            testLv = new LineValue(charValue);
                            testDataStatistic.put(tS, testLv);
                        }
                        testLv.addSomeItem(lv.getWYValues());
                        if (debug)
                            canShowOutput.showOutputString("sample: " + Arrays.toString(lv.getValue()) +
                                " -> pattern: " + Arrays.toString(testLv.getValue()));
                    } else {
                        for (int i = 0; i < 4; i++) {
                            notMatch += lv.getWYValues()[i];
                        }
                        canShowOutput.showLogString("CESearchTool#getCEValue return null");
                    }
                }

                // metrics: consistency between training and testing
                double sqrDiff = 0;
                double ape = 0;
                double consistent = 0;
                for (LineValue lv : testDataStatistic.values()) {
                    lv.updateGTValue();
                    double ATE = lv.getGroundTruthValue();
                    int instanceCount = lv.getWYSum();
                    allInstanceIncludeQuestion += instanceCount;

                    // PEHE
                    AbstractCE patt = searchTool.getNearestFreqPatt(lv.getValue()); // get nearest and most frequent pattern
                    double ce  = patt.groundTruthValue;
                    sqrDiff += Math.pow((ATE - ce), 2) * instanceCount;
                    // MAPE
                    ape += ce==0 ? 0 : Math.abs(ATE - ce) / ce * instanceCount;
                    // consistency within patterns
                    int numInstance = lv.getWYSum();
                    if (ToolFunctions.isSamePatternGroup(lv.getValue(), patt.value)) {
                        double lvCe = lv.getGroundTruthValue();
                        if (ce>=-0.01 && ce<=0.01 && lvCe>=-0.01 && lvCe<=0.01)
                            consistent += numInstance;
                        else if (ce * lvCe >= 0)
                            consistent += numInstance;
                    }
                }

                Statistic statistic = new Statistic();
                statistic.accuracy = (double) successInstance / allInstance;
                statistic.recall = (double) successInstance / allInstanceIncludeQuestion;
                statistic.testNoMatch = (double) notMatch / testingDataCount;
                statistic.pehe = Math.sqrt(sqrDiff / allInstanceIncludeQuestion);
                statistic.mape = ape / allInstanceIncludeQuestion;
                statistic.consistencyInPattern = consistent / allInstanceIncludeQuestion;

                if (debug) {
                    canShowOutput.showOutputString("===========================================");
                    canShowOutput.showOutputString("Consistency within patterns: " + statistic.consistencyInPattern);
                    canShowOutput.showOutputString("PEHE: " + Math.sqrt(statistic.pehe));
                    canShowOutput.showOutputString("MAPE: " + statistic.mape);
                    canShowOutput.showOutputString("Testing Data not matched: " + statistic.testNoMatch);
                }
                return statistic;
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
        return null;
    }



    public static int[] trimlength(int[] array, int len){
        int [] newArray = new int[len];
        for (int i=0; i<len; i++){
            newArray[i] = array[i];
        }
        return newArray;
    }
}
