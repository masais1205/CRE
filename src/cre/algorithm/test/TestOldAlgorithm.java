package cre.algorithm.test;


import cre.algorithm.CalculatingException;
import cre.algorithm.CanShowOutput;
import cre.algorithm.CanShowStatus;
import cre.algorithm.test.ce.*;

import cre.algorithm.tool.FileTool;
import cre.algorithm.tool.OtherTool;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by HanYizhao on 2017/3/9.
 */
public class TestOldAlgorithm {
    private static String delimiter = ",";

    public static Statistic do_it(String fileName, double ZC, double odd_ratio, int WP, int YP, int[] XPArray,
                                  int[] crossValidationGroup, int nowFold,
                                  CanShowStatus canShowStatus,
                                  CanShowOutput canShowOutput) throws CalculatingException {

        BufferedReader br = null;
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
                    delimiter, YP, names.length, crossValidationGroup, nowFold, canShowOutput);
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
            while ((tempS = br.readLine()) != null) {
                isTrainingSet = crossValidationGroup[count - 2] != nowFold;
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
                canShowOutput.showOutputString("Odd Ratio");
                canShowOutput.showOutputString(sb.toString());
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
            for (int i = 0; i < orYX.length; i++) {
                Integer tempValue = ((Integer) orYX[i].getAttach());
                XPSorted[i] = tempValue;
                if (orYX[i].getOR(false) > odd_ratio) {
                    orYXPNoFitOddsRatio.add(tempValue);
                }
            }

            /////////!!!!! Jiuyong asked to make XPReverseSorted same as XPSorted
            System.arraycopy(XPSorted, 0, XPReverseSorted, 0, XPSorted.length);
            ////////!!!!!!!!!!!!!!!!!


            for (AbstractCE i : trainingData.values()) {
                i.updateCEValue(ZC);
            }
            //show pattern numbers before generation
            int countPlus = 0, countMinus = 0, countQuestion = 0;
            for (AbstractCE ce : trainingData.values()) {
                switch (ce.cEValue) {
                    case PLUS:
                        countPlus++;
                        break;
                    case QUESTION:
                        countQuestion++;
                        break;
                    case MINUS:
                        countMinus++;
                        break;
                }
            }
            canShowOutput.showOutputString("PLUS\tMINUS\tQUESTION");
            canShowOutput.showOutputString(countPlus + "\t" + countMinus + "\t" + countQuestion);
            /////////////
            List<AbstractCE> mergeResult = new ArrayList<>();
            CEAlgorithm.doMerge(trainingData.values(), mergeResult, XPSorted,
                    XPReverseSorted, ZC, orYXPNoFitOddsRatio, canShowOutput);
            //show pattern numbers after generation
            countPlus = 0;
            countMinus = 0;
            countQuestion = 0;
            int trainPlusMinusCount = 0;
            for (AbstractCE ce : mergeResult) {
                switch (ce.cEValue) {
                    case PLUS:
                        countPlus++;
                        break;
                    case QUESTION:
                        countQuestion++;
                        break;
                    case MINUS:
                        countMinus++;
                        break;
                }
            }
            trainPlusMinusCount = countMinus + countPlus;
            canShowOutput.showOutputString("After");
            canShowOutput.showOutputString("PLUS\tMINUS\tQUESTION");
            canShowOutput.showOutputString(countPlus + "\t" + countMinus + "\t" + countQuestion);
            //////////////////

            //Log training result.
            {
                StringBuilder sb = new StringBuilder();
                sb.append(mergeResult.size());
                sb.append("\n");
                for (int i = 0; i < XPArray.length; i++) {
                    sb.append(names[XPArray[i]]);
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
                canShowOutput.showLogString(sb.toString());
            }

            // Training is finished. Start testing.
            CESearchTool searchTool = new CESearchTool(mergeResult);
            int notMatch = 0;
            HashMap<String, LineValue> testDataStatistic = new HashMap<>();
            for (LineValue lv : testingData.values()) {
                char[] charValue = searchTool.getCharValue(lv.getValue());
                if (charValue != null) {
                    String tS = new String(charValue);
                    LineValue testLv = testDataStatistic.get(tS);
                    if (testLv == null) {
                        testLv = new LineValue(charValue);
                        testDataStatistic.put(tS, testLv);
                    }
                    testLv.addSomeItem(lv.getWYValues());
                } else {
                    for (int i = 0; i < 4; i++) {
                        notMatch += lv.getWYValues()[i];
                    }
                    canShowOutput.showLogString("CESearchTool#getCEValue return null");
                }
            }

            int success = 0;
            int failed = 0;
            canShowOutput.showLogString("\n===Testing process===");
            int testPlusMinusCount = 0;
            for (LineValue lv : testDataStatistic.values()) {
                double[] dData = OtherTool.fromIntArrayToNoZeroArray(lv.getWYValues());
                double ATE = dData[0] / (dData[0] + dData[1]) - dData[2] / (dData[2] + dData[3]);
                CEValue ceValue = searchTool.getCEValue(lv.getValue());
                {
                    if (ceValue != null && (ceValue.compareTo(CEValue.MINUS) == 0
                            || ceValue.compareTo(CEValue.PLUS) == 0)) {
                        StringBuilder sbs = new StringBuilder();
                        for (int i = 0; i < lv.getValue().length; i++) {
                            sbs.append(lv.getValue()[i]);
                            sbs.append('\t');
                        }
                        sbs.append(ceValue);
                        sbs.append('\t');
                        for (int i = 0; i < 4; i++) {
                            sbs.append(dData[i]);
                            sbs.append("\t");
                        }
                        sbs.append(ATE);
                        canShowOutput.showLogString(sbs.toString());
                        testPlusMinusCount++;
                    }
                }
                if (ceValue != null) {
                    if (ceValue.compareTo(CEValue.PLUS) == 0) {
                        if (ATE > 0) {
                            success++;
                        } else {
                            failed++;
                        }
                    } else if (ceValue.compareTo(CEValue.MINUS) == 0) {
                        if (ATE < 0) {
                            success++;
                        } else {
                            failed++;
                        }
                    }
                }
            }
            canShowOutput.showOutputString("accuracy: " + (double) success / (success + failed));
            canShowOutput.showOutputString("Testing Data not matched: " + notMatch + "/" + testingDataCount);
            canShowOutput.showOutputString("Pattern(testing / training): " + testPlusMinusCount + "/" + trainPlusMinusCount);
            Statistic statistic = new Statistic();
            statistic.accuracy = (double) success / (success + failed);
            statistic.testNoMatch = (double) notMatch / testingDataCount;
            statistic.patternMatch = (double) testPlusMinusCount / trainPlusMinusCount;
            return statistic;
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
}
