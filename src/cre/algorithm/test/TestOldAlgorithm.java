package cre.algorithm.test;


import cre.Config.OtherConfig;
import cre.algorithm.CalculatingException;
import cre.algorithm.CanShowOutput;
import cre.algorithm.CanShowStatus;
import cre.algorithm.test.ce.*;

import cre.algorithm.tool.FileTool;
import cre.algorithm.tool.OtherTool;
import cre.algorithm.tool.TemporaryFileManager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

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

    public static Statistic do_it(String fileName, String testFileName, double ZC, double odd_ratio, int mergeDepth, int WP, int YP, int[] XPArray,
                                  int[] group, int testGroupId, OtherConfig otherConfig,
                                  CanShowStatus canShowStatus,
                                  CanShowOutput canShowOutput, boolean isTesting) throws CalculatingException {

        BufferedReader br = null;

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

            c.assign(".tmp.", "adjustment(fileName,w,y,xArray,alpha)");
            REXP r = c.parseAndEval("try(eval(parse(text=.tmp.)),silent=TRUE)");
            if (r.inherits("try-error")) {
                canShowOutput.showOutputString("Error: " + r.asString());
                c.close();
//                return 0;
            }

            REXP XPArray_REXP = c.eval("adjustment(fileName,w,y,xArray,alpha)");
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

            while (true) {
                if (otherConfig.getValidation().toString().equals("SUPPLIED_TEST_DATA")) {
                    tempS = br.readLine();
                    if (tempS != null)
                        isTrainingSet = group == null || (group[count - 2] == testGroupId);
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
                        if (simpleTrueFalse) {
                            AbstractCE cE = trainingData.get(s);
                            if (cE == null) {
                                cE = new TrueFalseCE(cBuffer);
                                trainingData.put(s, cE);
                            }
                            ((TrueFalseCE) cE).addItem(WValue, YBooleanValue);
                        } else {
                            AbstractCE cE = trainingData.get(s);
                            if (cE == null) {
                                cE = new NumberCE(cBuffer);
                                trainingData.put(s, cE);
                            }
                            ((NumberCE) cE).addItem(WValue, yValue);
                        }
                    } else {
                        testingDataCount++;
                        LineValue lv = testingData.get(s);
                        if (lv == null) {
                            lv = new LineValue(cBuffer);
                            testingData.put(s, lv);
                        }
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
                if (orWX[i].getOR(false) > odd_ratio) {
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
                if (orYX[i].getOR(false) > odd_ratio && !orYXPNoFitOddsRatio.contains(tempValue)) {
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


            for (AbstractCE i : trainingData.values()) {
                i.updateCEValue(ZC);
                System.out.println(i);
            }
            //show pattern numbers before generation
            int countPlus = 0, countMinus = 0, countQuestion = 0;
            int countPlusInstanceCount = 0, countMinusInstanceCount = 0, countQuestionInstanceCount = 0;
            for (AbstractCE ce : trainingData.values()) {
                switch (ce.cEValue) {
                    case PLUS:
                        countPlus++;
                        countPlusInstanceCount += ce.getInstanceNumber();
                        break;
                    case QUESTION:
                        countQuestion++;
                        countQuestionInstanceCount += ce.getInstanceNumber();
                        break;
                    case MINUS:
                        countMinus++;
                        countMinusInstanceCount += ce.getInstanceNumber();
                        break;
                }
            }
            canShowOutput.showLogString("PLUS\tMINUS\tQUESTION");
            canShowOutput.showLogString(countPlus + "(" + countPlusInstanceCount + ")\t"
                    + countMinus + "(" + countMinusInstanceCount + ")\t"
                    + countQuestion + "(" + countQuestionInstanceCount + ")");

            // modified by mss, show pattern before merge
//            canShowOutput.showOutputString("before merge");
//            if (!isTesting) {
//                StringBuilder sb = new StringBuilder();
//                sb.append("\n");
//                for (int i = 0; i < XPArray.length; i++) {
//                    sb.append(names[XPArray[i]]);
//                    sb.append("\t");
//                }
//                if (simpleTrueFalse) {
//                    sb.append("ce.TrueFalseCE\tn11\tn12\tn21\tn22\t");
//                    sb.append("p1-p2");
//                } else {
//                    sb.append("ce.TrueFalseCE\tW=1\tW=0");
//                }
//                sb.append("\n");
//                for (AbstractCE i : trainingData.values()) {
//                    sb.append(i.toString());
//                    sb.append("\n");
//                }
//                canShowOutput.showOutputString(sb.toString());
//            }
//            canShowOutput.showOutputString("finish");
            // mss

            /////////////
            List<AbstractCE> mergeResult = new ArrayList<>();
//            CEAlgorithm.doMerge(trainingData.values(), mergeResult, PCMembers, XPSorted,
//                    XPReverseSorted, ZC, orYXPNoFitOddsRatio, mergeDepth, canShowOutput);

            // add by mss, treatment effect homogeneity first
//            CEAlgorithm.doMergeEffectHomo(trainingData.values(), mergeResult, PCMembers, XPSorted,
//                    XPReverseSorted, ZC, orYXPNoFitOddsRatio, mergeDepth, canShowOutput);

            // add by mss, reliability first
            CEAlgorithm.doMergeReliable(trainingData.values(), mergeResult, PCMembers, XPSorted,
                        XPReverseSorted, ZC, orYXPNoFitOddsRatio, mergeDepth, canShowOutput);

            //show pattern numbers after generation
            countPlus = 0;
            countMinus = 0;
            countQuestion = 0;
            countPlusInstanceCount = 0;
            countMinusInstanceCount = 0;
            countQuestionInstanceCount = 0;
            int trainPlusMinusCount = 0;
            for (AbstractCE ce : mergeResult) {
                switch (ce.cEValue) {
                    case PLUS:
                        countPlus++;
                        countPlusInstanceCount += ce.getInstanceNumber();
                        break;
                    case QUESTION:
                        countQuestion++;
                        countQuestionInstanceCount += ce.getInstanceNumber();
                        break;
                    case MINUS:
                        countMinus++;
                        countMinusInstanceCount += ce.getInstanceNumber();
                        break;
                }
            }
            trainPlusMinusCount = countMinus + countPlus;
            canShowOutput.showLogString("After");
            canShowOutput.showLogString("PLUS\tMINUS\tQUESTION");
            canShowOutput.showLogString(countPlus + "(" + countPlusInstanceCount + ")\t"
                    + countMinus + "(" + countMinusInstanceCount + ")\t"
                    + countQuestion + "(" + countQuestionInstanceCount + ")");
            //////////////////

            //Log training result.
            if (!isTesting) {
                StringBuilder sb = new StringBuilder();
//                sb.append(mergeResult.size());
//                sb.append("\n");
                for (int i = 0; i < XPArray.length; i++) {
                    int p = XPArray[i];
                    if(IntStream.of(PCMembers).anyMatch(x -> x == p))
                        sb.append("[");
                    sb.append(names[XPArray[i]]);
                    if(IntStream.of(PCMembers).anyMatch(x -> x == p))
                        sb.append("]");
                    sb.append("\t");
                }
                if (simpleTrueFalse) {
                    sb.append("ce.TrueFalseCE\tn11\tn12\tn21\tn22\t");
                    sb.append("p1-p2");
                } else {
                    sb.append("ce.TrueFalseCE\tW=1\tW=0");
                }
                sb.append("\n");
                for (AbstractCE i : mergeResult) {
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
                int success = 0;
                int failed = 0;
                canShowOutput.showLogString("\n===Testing process===");
                int testPlusMinusCount = 0;
                HashMap<String, LineValue> testDataStatistic = new HashMap<>();
                for (LineValue lv : testingData.values()) {
                    double[] dData = OtherTool.fromIntArrayToNoZeroArray(lv.getWYValues());
//                    canShowOutput.showOutputString(Arrays.toString(dData));
                    double ATE = dData[0] / (dData[0] + dData[1]) - dData[2] / (dData[2] + dData[3]);
                    char ateSign = searchTool.getSign(ATE);
                    int instanceCount = lv.getWYSum();
                    allInstanceIncludeQuestion += instanceCount;
//                    char[] charValue = searchTool.getCharValue(lv.getValue()); // get matched pattern, maybe not nearest
                    char ceSign = searchTool.getNearestFreqCESign(lv.getValue()); // get nearest and most frequent pattern
//                    char ceSign = searchTool.getNearestAvgCESign(lv.getValue()); // get nearest pattern, average
//                    char ceSign = searchTool.getNearestPCCESign(lv.getValue(), PCMembers); // get nearest pattern, more PC variables invovled
//                    canShowOutput.showOutputString("test: "+Character.toString(ateSign)+"\tpattern: "+ Character.toString(ceSign));

                    if (ceSign != '?') {
                        allInstance += instanceCount;
                        if (ceSign == ateSign) {
                            success++;
                            successInstance += instanceCount;
                        } else {
                            failed++;
                        }
                    } else {
                        for (int i = 0; i < 4; i++) {
                            notMatch += lv.getWYValues()[i];
                        }
                        canShowOutput.showLogString("CESearchTool#getCEValue return null");
                    }
                }

                canShowOutput.showLogString("accuracy: " + (double) successInstance / allInstance);
                canShowOutput.showLogString("Testing Data not matched: " + notMatch + "/" + testingDataCount);
//                canShowOutput.showLogString("Pattern(testing / training): " + testPlusMinusCount + "/" + trainPlusMinusCount);
                Statistic statistic = new Statistic();
                statistic.accuracy = (double) successInstance / allInstance;
                statistic.recall = (double) successInstance / allInstanceIncludeQuestion;
                statistic.testNoMatch = (double) notMatch / testingDataCount;
//                statistic.patternMatch = (double) testPlusMinusCount / trainPlusMinusCount;
                canShowOutput.showLogString(statistic.toString());
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
