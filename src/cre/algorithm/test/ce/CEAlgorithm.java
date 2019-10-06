package cre.algorithm.test.ce;

import com.google.common.collect.Table;
import cre.algorithm.CanShowOutput;
import cre.algorithm.test.MathListCombination;
import cre.algorithm.test.Statistic;
import org.apache.commons.math3.ml.distance.DistanceMeasure;

import java.awt.*;
import java.util.*;
import java.util.List;

import static cre.algorithm.test.ce.DistMeasure.minDistLocation.sortDist;

/**
 * Created by HanYizhao on 4/13/2017.
 */
public class CEAlgorithm {

    public static final char char_Star = '*';
    public static final char char_QUESTION = '×';

    static class MergeConfig {
        double zc;
        int[] order;
        int[] reverseOrder;
        HashSet<Integer> positionNotFidOddsRatio;
        int mergeDepth;
    }

    private static void mergeQuestionList(List<AbstractCE> old, List<AbstractCE> plusResult, List<AbstractCE> minusResult,
                                          List<AbstractCE> questionResult, MergeConfig mc, CanShowOutput canShowOutput) {
        canShowOutput.showLogString("Merge Question: " + old.size() + "\t" + plusResult.size() + "\t" + questionResult.size());
        //As index
        HashMap<String, Integer> map = new HashMap<>();
        // Copy of old
        ArrayList<AbstractCE> list = new ArrayList<>(old.size());
        int cc = 0;
        for (AbstractCE i : old) {
            list.add(i);
            map.put(new String(i.value), cc);
            cc++;
        }
        List<AbstractCE> newPlusList = new ArrayList<>();
        List<AbstractCE> newMinusList = new ArrayList<>();
        boolean hasMergeOne;
        int count = 0;
        List<Integer> tempOrder = new ArrayList<>();
        for (int i : mc.reverseOrder) {
            if (!mc.positionNotFidOddsRatio.contains(i)) {
                tempOrder.add(i);
            }
        }
        int[] realOrder = new int[tempOrder.size()];
        for (int i = 0; i < tempOrder.size(); i++) {
            realOrder[i] = tempOrder.get(i);
        }
        List<List<int[]>> realOrders = MathListCombination.listAllCombination(realOrder, mc.mergeDepth);
        for (List<int[]> order : realOrders) {
            do {
                count++;
                hasMergeOne = false;
                for (int[] i : order) {
                    ce:
                    for (int l = 0; l < list.size(); l++) {
                        AbstractCE tempCE = list.get(l);
                        if (tempCE != null) {
                            char[] charTemps = Arrays.copyOf(tempCE.value, tempCE.value.length);
                            for (int k = 0; k < i.length; k++) {
                                int tPosition = i[k];
                                if (charTemps[tPosition] == '0' || charTemps[tPosition] == '1') {
                                    charTemps[tPosition] = charTemps[tPosition] == '0' ? '1' : '0';
                                } else {
                                    continue ce;
                                }
                            }
                            Integer position = map.remove(new String(charTemps));
                            if (position != null) {
                                hasMergeOne = true;
                                AbstractCE newCE = tempCE.mergeInstance(list.get(position),
                                        i, char_QUESTION, null, mc.zc);
                                list.set(l, null);
                                list.set(position, null);
                                map.remove(new String(tempCE.value));
                                if (newCE.cEValue.compareTo(CEValue.QUESTION) == 0) {
                                    list.set(l, newCE);
                                    map.put(new String(newCE.value), l);
                                } else if (newCE.cEValue.compareTo(CEValue.PLUS) == 0) {
                                    newPlusList.add(newCE);
                                } else {
                                    newMinusList.add(newCE);
                                }
                            }

                        }
                    }

                }
            } while (hasMergeOne);
        }

        canShowOutput.showLogString("loop count: " + count);
        for (Map.Entry<String, Integer> i : map.entrySet()) {
            questionResult.add(list.get(i.getValue()));
        }
        if (newPlusList.size() != 0) {
            mergeSimpleList(newPlusList, plusResult, mc, CEValue.PLUS, canShowOutput);
        }
        if (newMinusList.size() != 0) {
            mergeSimpleList(newMinusList, minusResult, mc, CEValue.MINUS, canShowOutput);
        }
        old.clear();
        canShowOutput.showLogString("Merge Question Finish: " + "\t" + plusResult.size() + "\t" + questionResult.size());
    }

    private static void mergeSimpleList(List<AbstractCE> old, List<AbstractCE> result, MergeConfig mc, CEValue preferredValue, CanShowOutput canShowOutput) {
        canShowOutput.showLogString("Merge " + preferredValue + ": " + old.size() + "\t" + result.size());
        HashMap<String, Integer> map = new HashMap<>();
        ArrayList<AbstractCE> list = new ArrayList<>(old.size());
        int cc = 0;
        for (AbstractCE i : old) {
            list.add(i);
            map.put(new String(i.value), cc);
            cc++;
        }
        boolean hasMergeOne;
        int count = 0;
        do {
            count++;
            hasMergeOne = false;
            for (int i : mc.order) {
                for (int l = 0; l < list.size(); l++) {
                    AbstractCE tempCE = list.get(l);
                    if (tempCE != null) {
                        char charTemp = tempCE.value[i];
                        if (charTemp == '0' || charTemp == '1') {
                            tempCE.value[i] = charTemp == '0' ? '1' : '0';
                            Integer position = map.remove(new String(tempCE.value));
                            tempCE.value[i] = charTemp;
                            if (position != null) {
                                hasMergeOne = true;
                                //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                                AbstractCE newCE = tempCE.mergeInstance(list.get(position),
                                        new int[]{i}, char_Star, null, mc.zc);
                                if (newCE.cEValue.compareTo(preferredValue) != 0) {
                                    canShowOutput.showLogString("ERROR");
                                }
                                list.set(l, newCE);
                                list.set(position, null);
                                map.remove(new String(tempCE.value));
                                map.put(new String(newCE.value), l);
                            }
                        }
                    }
                }
            }
        } while (hasMergeOne);
        old.clear();
        canShowOutput.showLogString("loop count: " + count);
        for (Map.Entry<String, Integer> i : map.entrySet()) {
            result.add(list.get(i.getValue()));
        }
        canShowOutput.showLogString("Merge " + preferredValue + " Finish: " + old.size() + "\t" + result.size());
    }

    public static void doMerge(Collection<AbstractCE> old, List<AbstractCE> result,
                               int[] order, int[] reverseOrder, double zc,
                               HashSet<Integer> positionNotFitOddsRatio, int mergeDepth, CanShowOutput canShowOutput) {
        MergeConfig mc = new MergeConfig();
        mc.order = order;
        mc.reverseOrder = reverseOrder;
        mc.zc = zc;
        mc.positionNotFidOddsRatio = positionNotFitOddsRatio;
        mc.mergeDepth = mergeDepth;
        List<AbstractCE> plusList = new ArrayList<>();
        List<AbstractCE> minusList = new ArrayList<>();
        List<AbstractCE> questionList = new ArrayList<>();
        for (AbstractCE i : old) {
            if (i.cEValue.compareTo(CEValue.PLUS) == 0) {
                plusList.add(i);
            } else if (i.cEValue.compareTo(CEValue.QUESTION) == 0) {
                questionList.add(i);
            } else {
                minusList.add(i);
            }
        }
        List<AbstractCE> plusResult = new ArrayList<>();
        List<AbstractCE> questionResult = new ArrayList<>();
        List<AbstractCE> minusResult = new ArrayList<>();
        mergeSimpleList(plusList, plusResult, mc, CEValue.PLUS, canShowOutput);
        mergeSimpleList(minusList, minusResult, mc, CEValue.MINUS, canShowOutput);
        mergeQuestionList(questionList, plusResult, minusResult, questionResult, mc, canShowOutput);
        mergeSimpleList(plusResult, result, mc, CEValue.PLUS, canShowOutput);
        mergeSimpleList(minusResult, result, mc, CEValue.MINUS, canShowOutput);
        result.addAll(questionResult);
        canShowOutput.showLogString("All Finish:" + result.size());
    }

    public static int[] getMergePoistion(String xor) {
        List<Integer> positionsList = new ArrayList<>();
        for(int i = 0; i < xor.length(); i++){
            if(xor.charAt(i) == '1'){
                positionsList.add(i);
            }
        }
        int[] positions = new int[positionsList.size()];
        for (int i=0; i<positionsList.size(); i++)
            positions[i] = positionsList.get(i);
        return positions;
    }

    public static void doMergeReliability(Collection<AbstractCE> old, List<AbstractCE> result,
                               int[] order, int[] reverseOrder, double zc,
                               HashSet<Integer> positionNotFitOddsRatio, int mergeDepth, CanShowOutput canShowOutput) {
        List<AbstractCE> CEList = new ArrayList<>();
        for (AbstractCE i : old) {
            i.updateReliable();
            i.updateStatistics();
            CEList.add(i);
        }

        DistMeasure distMeasure = new DistMeasure();
        distMeasure.buildDistanceMatrix(CEList);
        Table<Integer, Integer, String> xorMatrix = distMeasure.xorMatrix;
        Table<Integer, Integer, Integer> distanceMatrix = distMeasure.distanceMatrix;

        int n = CEList.size();
        System.out.println(CEList.size());
        System.out.println(distanceMatrix.size());
        for (int j=0; j<n; j++) {
            for (int k = 0; k<n; k++) {
                System.out.print(distanceMatrix.get(j,k));
                System.out.print("\t");
            }
            System.out.println();
        }
        System.out.println("=========================");

        List<DistMeasure.minDistLocation> location = new ArrayList<>();
        Map<Integer, Integer> row = new HashMap<>();
        int distance, cntUnreliable;
        double diffCE;
        for(int j=1; j<n; j++) {
            row = distanceMatrix.row(j);

//            Map.Entry<Integer, Integer> min = null;
//            for (Map.Entry<Integer, Integer> entry : row.entrySet()) {
//                if (min == null || min.getValue() > entry.getValue()) {
//                    min = entry;
//                }
//            }
//            // min.getKey() & min.getValue() // key & value

            for(int k=0; k<j; k++) {
                cntUnreliable = 0;
                distance = row.get(k);
                if (! CEList.get(j).reliable)
                    cntUnreliable++;
                if (! CEList.get(k).reliable)
                    cntUnreliable++;
                diffCE = Math.abs(CEList.get(j).statistics[4] - CEList.get(k).statistics[4]);
                location.add(new DistMeasure.minDistLocation(j, k, distance, cntUnreliable, diffCE));
            }

        }

        sortDist(location);

        List<Integer> mergerdIndex = new ArrayList<>();
        int jdx, kdx;
        for(DistMeasure.minDistLocation loc : location) {
            jdx = loc.rowIndex;
            kdx = loc.colIndex;
            if (mergerdIndex.indexOf(jdx)>=0 || mergerdIndex.indexOf(kdx)>=0)
                continue;
            mergerdIndex.add(jdx);
            mergerdIndex.add(kdx);

            String xor = xorMatrix.get(jdx, kdx);
            int[] positions = getMergePoistion(xor);
            AbstractCE newCE = CEList.get(jdx).mergeInstance(CEList.get(kdx),
                    positions, char_QUESTION, null, zc);

            result.add(newCE);
        }

        for (int i=0; i<CEList.size(); i++)
            if (mergerdIndex.indexOf(i) < 0)
                result.add(CEList.get(i));
    }


}
