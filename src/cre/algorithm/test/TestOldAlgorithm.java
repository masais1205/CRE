package cre.algorithm.test;


import cre.algorithm.CanShowOutput;
import cre.algorithm.CanShowStatus;
import cre.algorithm.test.ce.AbstractCE;
import cre.algorithm.test.ce.CEAlgorithm;
import cre.algorithm.test.ce.NumberCE;
import cre.algorithm.test.ce.TrueFalseCE;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Created by HanYizhao on 2017/3/9.
 */
public class TestOldAlgorithm {
    private static String delimiter = ",";

    public static void do_it(String fileName, double ZC, String WName, String YName, String[] removeName,
                             CanShowStatus canShowStatus, CanShowOutput canShowOutput) {

        BufferedReader br = null;
        BufferedReader brPre = null;
        int WP;
        int YP;
        HashSet<Integer> XP = new HashSet<>();
        int[] XPArray;
        int[] XPSorted;
        int[] XPReverseSorted;
        try {
            br = new BufferedReader(new FileReader(fileName));
            brPre = new BufferedReader(new FileReader(fileName));
            String header = br.readLine();
            brPre.readLine();
            String[] names = header.split(delimiter);
            HashMap<String, Integer> namesPositionMap = new HashMap<>();
            for (int i = 0; i < names.length; i++) {
                namesPositionMap.put(names[i], i);
            }
            Integer temp;
            temp = namesPositionMap.remove(WName);
            if (temp == null) {
                canShowOutput.showOutputString("Not find column name : " + WName);
                return;
            }
            WP = temp;
            temp = namesPositionMap.remove(YName);
            if (temp == null) {
                canShowOutput.showOutputString("Not find column name : " + YName);
                return;
            }
            YP = temp;
            for (String i : removeName) {
                temp = namesPositionMap.remove(i);
                if (temp == null) {
                    canShowOutput.showOutputString("Not find column name : " + i);
                    return;
                }
            }
            XPArray = new int[namesPositionMap.size()];
            int cC = 0;
            for (Integer i : namesPositionMap.values()) {
                XP.add(i);
                XPArray[cC] = i;
                cC++;
            }
            Arrays.sort(XPArray);
            HashMap<String, AbstractCE> data = new HashMap<>();
            String tempS;
            int count = 0;
            char[] cBuffer = new char[XPArray.length];
            OR[] orWX = new OR[XPArray.length];
            OR[] orYX = new OR[XPArray.length];
            for (int i = 0; i < XPArray.length; i++) {
                orWX[i] = new OR(i);
                orYX[i] = new OR(i);
            }

            boolean simpleTrueFalse = true;
            double YMedian = 0;
            {
                List<Double> yValueList = new ArrayList<>();
                while ((tempS = brPre.readLine()) != null) {
                    String[] tempSS = tempS.split(delimiter);
                    if (tempSS.length == names.length) {
                        if (!tempSS[YP].equals("1") && !tempSS[YP].equals("0")) {
                            simpleTrueFalse = false;
                            yValueList.add(Double.parseDouble(tempSS[YP]));
                        }
                    } else {
                        canShowOutput.showOutputString("Line value ERROR: (line:" + count + ") " + tempS);
                        break;
                    }
                }
                if (!simpleTrueFalse) {
                    Collections.sort(yValueList);
                    int length = yValueList.size();
                    if (length % 2 != 0) {
                        YMedian = yValueList.get(length / 2);
                    } else {
                        YMedian = (yValueList.get(length / 2) + yValueList.get(length / 2 - 1)) / 2;
                    }
                }
            }
            canShowOutput.showOutputString("SimpleTrueFalse:" + simpleTrueFalse);


            while ((tempS = br.readLine()) != null) {
                String[] tempSS = tempS.split(delimiter);
                if (tempSS.length == names.length) {
                    double yValue = Double.parseDouble(tempSS[YP]);
                    for (int i = 0; i < XPArray.length; i++) {
                        cBuffer[i] = tempSS[XPArray[i]].equals("1") ? '1' : '0';
                        orWX[i].addValue(tempSS[WP].equals("1"), tempSS[XPArray[i]].equals("1"));
                        if (simpleTrueFalse) {
                            orYX[i].addValue(tempSS[YP].equals("1"), tempSS[XPArray[i]].equals("1"));
                        } else {
                            orYX[i].addValue(yValue > YMedian, tempSS[XPArray[i]].equals("1"));
                        }
                    }
                    String s = new String(cBuffer);
                    if (simpleTrueFalse) {
                        AbstractCE cE = data.get(s);
                        if (cE == null) {
                            cE = new TrueFalseCE(cBuffer);
                            data.put(s, cE);
                        }
                        ((TrueFalseCE) cE).addItem(tempSS[WP].equals("1"), tempSS[YP].equals("1"));
                    } else {
                        AbstractCE cE = data.get(s);
                        if (cE == null) {
                            cE = new NumberCE(cBuffer);
                            data.put(s, cE);
                        }
                        ((NumberCE) cE).addItem(tempSS[WP].equals("1"), yValue);
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
            for (AbstractCE i : data.values()) {
                i.updateCEValue(ZC);
            }
            List<AbstractCE> mergeResult = new ArrayList<>();
            CEAlgorithm.doMerge(data.values(), mergeResult, XPSorted, XPReverseSorted, ZC, canShowOutput);
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
            canShowOutput.showOutputString(sb.toString());
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
            if (brPre != null) {
                try {
                    brPre.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
