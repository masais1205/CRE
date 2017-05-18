package cre.algorithm.test;


import cre.algorithm.CalculatingException;
import cre.algorithm.CanShowOutput;
import cre.algorithm.CanShowStatus;
import cre.algorithm.test.ce.*;

import cre.algorithm.tool.FileTool;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by HanYizhao on 2017/3/9.
 */
public class TestOldAlgorithm {
    private static String delimiter = ",";

    public static void do_it(String fileName, double ZC, int WP, int YP, int[] XPArray,
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
                XPReverseSorted[orWX.length - i - 1] = ((Integer) orWX[i].getAttach());
            }
            for (int i = 0; i < orYX.length; i++) {
                XPSorted[i] = ((Integer) orYX[i].getAttach());
            }
            for (AbstractCE i : trainingData.values()) {
                i.updateCEValue(ZC);
            }
            List<AbstractCE> mergeResult = new ArrayList<>();
            CEAlgorithm.doMerge(trainingData.values(), mergeResult, XPSorted, XPReverseSorted, ZC, canShowOutput);
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

            CESearchTool searchTool = new CESearchTool(mergeResult);
            int[] plusStatus = new int[4];
            int[] minusStatus = new int[4];
            int questionStatus = 0;
            int notMatch = 0;
            for (LineValue lv : testingData.values()) {
                CEValue ceValue = searchTool.getCEValue(lv.getValue());
                if (ceValue != null) {
                    switch (ceValue) {
                        case PLUS:
                            for (int i = 0; i < 4; i++) {
                                plusStatus[i] += lv.getWYValues()[i];
                            }
                            break;
                        case MINUS:
                            for (int i = 0; i < 4; i++) {
                                minusStatus[i] += lv.getWYValues()[i];
                            }
                            break;
                        case QUESTION:
                            for (int i = 0; i < 4; i++) {
                                questionStatus += lv.getWYValues()[i];
                            }
                            break;
                    }

                } else {
                    for (int i = 0; i < 4; i++) {
                        notMatch += lv.getWYValues()[i];
                    }
                    canShowOutput.showLogString("CESearchTool#getCEValue return null");
                }
            }
            canShowOutput.showOutputString(CEValue.PLUS + "\t" + Arrays.toString(plusStatus));
            canShowOutput.showOutputString(CEValue.MINUS + "\t" + Arrays.toString(minusStatus));
            canShowOutput.showOutputString(CEValue.QUESTION + "\t" + questionStatus);
            canShowOutput.showOutputString("not match\t" + notMatch);
            double sum = 0;
            double bingo = 0;
            for (int i = 0; i < 4; i++) {
                sum += plusStatus[i];
                sum += minusStatus[i];
            }
            bingo += plusStatus[0];
            bingo += plusStatus[3];
            bingo += minusStatus[1];
            bingo += minusStatus[2];
            canShowOutput.showOutputString("accuracy: " + bingo / sum);
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
}
