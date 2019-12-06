package cre.algorithm.test.ce;

import com.google.common.collect.Table;
import cre.algorithm.CanShowOutput;
import cre.algorithm.test.MathListCombination;
import cre.algorithm.test.Statistic;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import javax.xml.crypto.dsig.keyinfo.KeyValue;
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
                                          List<AbstractCE> questionResult, int[] PCMembers, MergeConfig mc, CanShowOutput canShowOutput) {
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
                                        i, PCMembers, char_QUESTION, null, mc.zc);
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
            mergeSimpleList(newPlusList, plusResult, PCMembers, mc, CEValue.PLUS, canShowOutput);
        }
        if (newMinusList.size() != 0) {
            mergeSimpleList(newMinusList, minusResult, PCMembers, mc, CEValue.MINUS, canShowOutput);
        }
        old.clear();
        canShowOutput.showLogString("Merge Question Finish: " + "\t" + plusResult.size() + "\t" + questionResult.size());
    }

    private static void mergeSimpleList(List<AbstractCE> old, List<AbstractCE> result, int[] PCMembers, MergeConfig mc, CEValue preferredValue, CanShowOutput canShowOutput) {
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
                                        new int[]{i}, PCMembers, char_Star, null, mc.zc);
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

    public static void doMerge(Collection<AbstractCE> old, List<AbstractCE> result, int[] PCMembers,
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
        mergeSimpleList(plusList, plusResult, PCMembers, mc, CEValue.PLUS, canShowOutput);
        mergeSimpleList(minusList, minusResult, PCMembers, mc, CEValue.MINUS, canShowOutput);
        mergeQuestionList(questionList, plusResult, minusResult, questionResult, PCMembers, mc, canShowOutput);
        mergeSimpleList(plusResult, result, PCMembers, mc, CEValue.PLUS, canShowOutput);
        mergeSimpleList(minusResult, result, PCMembers, mc, CEValue.MINUS, canShowOutput);
        result.addAll(questionResult);
        canShowOutput.showLogString("All Finish:" + result.size());
    }

    public static int[] getMergePoistion(String xor) {
        List<Integer> positionsList = new ArrayList<>();
        System.out.println(xor);
        for(int i = 0; i < xor.length(); i++){
            if(xor.charAt(i) == '1' || xor.charAt(i) == char_QUESTION || xor.charAt(i) == char_Star){
                positionsList.add(i);
            }
        }
        int[] positions = new int[positionsList.size()];
        for (int i=0; i<positionsList.size(); i++)
            positions[i] = positionsList.get(i);
        return positions;
    }

    public static List<Integer> getKeysFromValue(Map hm,Object value){
        Set ref = hm.keySet();
        Iterator it = ref.iterator();
        List<Integer> list = new ArrayList();

        while (it.hasNext()) {
            Object o = it.next();
            if(hm.get(o).equals(value)) {
                list.add((Integer) o);
            }
        }
        return list;
    }

    public static List<DistMeasure.minDistLocation> getMinDistLocation(List<AbstractCE> CEList, Table<Integer, Integer, Integer> distanceMatrix) {
        List<DistMeasure.minDistLocation> location = new ArrayList<>();
        int n = CEList.size();

        Map<Integer, Integer> row = new HashMap<>();
        int distance, cntUnreliable;
        double diffCE;
        for(int j=1; j<n; j++) {
            row = distanceMatrix.row(j);

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
        return location;
    }

    public static void doMergeOne(List<AbstractCE> CEList, ArrayList<AbstractCE> list, int[] positions,
                                                   int[] PCMembers, int jKey, int kKey, Integer jValue, Integer kValue, double zc) {
        AbstractCE newCE = list.get(jValue).mergeInstance(list.get(kValue),
                positions, PCMembers, char_QUESTION, null, zc);
        DistMeasure.updateXorMatrix(jValue, CEList.size(), positions, char_QUESTION);

        list.set(jValue, newCE);
        if(jKey != jValue)
            list.set(jKey, null);
        if(kKey != jValue)
            list.set(kKey, null);
        if(kValue != jValue)
            list.set(kValue, null);
    }

    public static void doMergeEffectHomo(Collection<AbstractCE> old, List<AbstractCE> result, int[] PCMembers,
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

        List<DistMeasure.minDistLocation> location = getMinDistLocation(CEList, distanceMatrix);
        sortDist(location);

        HashMap<Integer, Integer> map = new HashMap<>();
        ArrayList<AbstractCE> list = new ArrayList<>(CEList.size());
        for (int i=0; i<CEList.size(); i++) {
            list.add(CEList.get(i));
            map.put(i, i);
        }

        int jKey, kKey;
        Integer jValue, kValue;
        boolean notMerge;
        for(DistMeasure.minDistLocation loc : location) {
            jKey = loc.rowIndex;
            kKey = loc.colIndex;
            jValue = map.get(jKey);
            kValue = map.get(kKey);
            notMerge = list.get(jValue).reliable & list.get(kValue).reliable;
            if (notMerge || jValue == kValue)
                continue;

            String xor = xorMatrix.get(jValue, kValue);
            int[] positions = getMergePoistion(xor);
            if (positions.length == 0)
                continue;
            doMergeOne(CEList, list, positions, PCMembers, jKey, kKey, jValue, kValue, zc);

            List<Integer> keys = getKeysFromValue(map, kValue);
            for(Integer k : keys)
                map.put(k, jValue);
        }

        for (AbstractCE i : list)
            if (i != null) {
                result.add(i);
            }
    }

    public static void doMergeReliable(Collection<AbstractCE> old, List<AbstractCE> result, int[] PCMembers,
                                         int[] order, int[] reverseOrder, double zc,
                                         HashSet<Integer> positionNotFitOddsRatio, int mergeDepth, CanShowOutput canShowOutput) {
        List<AbstractCE> CEList = new ArrayList<>();
        List<AbstractCE> reliableCEList = new ArrayList<>();
        List<AbstractCE> unreliableCEList = new ArrayList<>();
        for (AbstractCE i : old) {
            i.updateReliable();
            i.updateStatistics();
            CEList.add(i);
            if (!i.reliable)
                unreliableCEList.add(i);
            else
                reliableCEList.add(i);
        }

        HashMap<Integer, Integer> map = new HashMap<>();
        ArrayList<AbstractCE> unreliableList = new ArrayList<>(CEList.size());
        for (int i=0; i<reliableCEList.size(); i++)
            result.add(reliableCEList.get(i));
        for (int i=0; i<unreliableCEList.size(); i++) {
            unreliableList.add(unreliableCEList.get(i));
            map.put(i, i);
        }

        DistMeasure distMeasure = new DistMeasure();
        distMeasure.buildDistanceMatrix(unreliableCEList);
        Table<Integer, Integer, String> xorMatrix = distMeasure.xorMatrix;
        Table<Integer, Integer, Integer> distanceMatrix = distMeasure.distanceMatrix;
        for (int a=0; a<unreliableCEList.size(); a++) {
            for (int b=0;b <unreliableCEList.size(); b++) {
                System.out.print(distanceMatrix.get(a,b).toString() + " ");
            }
            System.out.println();
        }

        List<DistMeasure.minDistLocation> location = getMinDistLocation(unreliableCEList, distanceMatrix);
        sortDist(location);
        for(int a=0; a<location.size(); a++) {
            DistMeasure.minDistLocation loc = location.get(a);
            canShowOutput.showOutputString(Integer.toString(loc.rowIndex) + " " + Integer.toString(loc.colIndex) + " " + Integer.toString(loc.minDistance));
        }

        int jKey, kKey;
        Integer jValue, kValue;
        for(int a=0; a<location.size(); a++) {
            DistMeasure.minDistLocation loc = location.get(a);
            jKey = loc.rowIndex;
            kKey = loc.colIndex;
            jValue = map.get(jKey);
            kValue = map.get(kKey);
            boolean notMerge = unreliableList.get(jValue).reliable & unreliableList.get(kValue).reliable;
            if (notMerge || jValue == kValue)
                continue;
            canShowOutput.showOutputString(Integer.toString(loc.minDistance));
            canShowOutput.showOutputString(Integer.toString(jValue) + " " + Integer.toString(kValue));
//            canShowOutput.showOutputString(unreliableList.get(jValue).toString());
//            canShowOutput.showOutputString(unreliableList.get(kValue).toString());

            String xor = xorMatrix.get(jValue, kValue);
            int[] positions = getMergePoistion(xor);
            if (positions.length == 0)
                continue;
            doMergeOne(unreliableCEList, unreliableList, positions, PCMembers, jKey, kKey, jValue, kValue, zc);

            List<Integer> keys = getKeysFromValue(map, kValue);
            for(Integer k : keys) {
                map.put(k, jValue);
            }
            canShowOutput.showOutputString(unreliableList.get(jValue).toString());
            canShowOutput.showOutputString("\n");
        }

        int cnt = 0;
        for (AbstractCE i : unreliableList)
            if (i != null) {
                result.add(i);
                cnt++;
            }
//        canShowOutput.showOutputString(Integer.toString(cnt));
    }


}
