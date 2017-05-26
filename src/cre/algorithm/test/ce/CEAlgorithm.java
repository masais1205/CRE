package cre.algorithm.test.ce;

import cre.algorithm.CanShowOutput;

import java.awt.*;
import java.util.*;
import java.util.List;

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
        boolean twoMerge = false;
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
        do {
            count++;
            hasMergeOne = false;
            for (int i : mc.reverseOrder) {
                if (!mc.positionNotFidOddsRatio.contains(i)) {
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
                                    AbstractCE newCE = tempCE.mergeInstance(list.get(position),
                                            new int[]{i}, char_QUESTION, null, mc.zc);
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
                }
            }
        } while (hasMergeOne);
        // Merge AbstractCE with only two different attributes.
        if (mc.twoMerge) {
            do {
                count++;
                hasMergeOne = false;
                class pair {
                    int x;
                    int y;

                    pair(int x, int y) {
                        this.x = x;
                        this.y = y;
                    }
                }
                List<pair> pairs = new ArrayList<>();
                for (int i = 0; i < mc.reverseOrder.length - 1; i++) {
                    for (int l = i + 1; l < mc.reverseOrder.length; l++) {
                        pairs.add(new pair(mc.reverseOrder[i], mc.reverseOrder[l]));
                    }
                }
                for (pair i : pairs) {
                    if (!mc.positionNotFidOddsRatio.contains(i.x)
                            && !mc.positionNotFidOddsRatio.contains(i.y)) {
                        for (int l = 0; l < list.size(); l++) {
                            AbstractCE tempCE = list.get(l);
                            if (tempCE != null) {
                                char charTempX = tempCE.value[i.x];
                                char charTempY = tempCE.value[i.y];
                                if ((charTempX == '0' || charTempX == '1')
                                        && (charTempY == '0' || charTempY == '1')) {
                                    tempCE.value[i.x] = charTempX == '0' ? '1' : '0';
                                    tempCE.value[i.y] = charTempY == '0' ? '1' : '0';
                                    Integer position = map.remove(new String(tempCE.value));
                                    tempCE.value[i.x] = charTempX;
                                    tempCE.value[i.y] = charTempY;
                                    if (position != null) {
                                        hasMergeOne = true;
                                        AbstractCE newCE = tempCE.mergeInstance(list.get(position),
                                                new int[]{i.x, i.y}, char_QUESTION, null, mc.zc);
                                        System.out.println("FFFF" + newCE.toString());
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
                               HashSet<Integer> positionNotFitOddsRatio, CanShowOutput canShowOutput) {
        MergeConfig mc = new MergeConfig();
        mc.order = order;
        mc.reverseOrder = reverseOrder;
        mc.zc = zc;
        mc.positionNotFidOddsRatio = positionNotFitOddsRatio;
        mc.twoMerge = true;
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


}
