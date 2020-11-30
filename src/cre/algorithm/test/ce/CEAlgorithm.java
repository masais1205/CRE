package cre.algorithm.test.ce;

import com.google.common.base.Joiner;
import com.google.common.collect.Table;
import cre.algorithm.CanShowOutput;
import cre.algorithm.ToolFunctions;
import cre.algorithm.test.MathListCombination;
import cre.algorithm.test.Statistic;
import cre.algorithm.test.TestConfig;
import org.apache.commons.math3.ml.distance.DistanceMeasure;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import javax.xml.crypto.dsig.keyinfo.KeyValue;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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

/*    private static void mergeQuestionList(List<AbstractCE> old, List<AbstractCE> plusResult, List<AbstractCE> minusResult,
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
                                        Arrays.stream(i).boxed().collect(Collectors.toList()), PCMembers, char_QUESTION, null, mc.zc);
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
    }*/

/*    private static void mergeSimpleList(List<AbstractCE> old, List<AbstractCE> result, int[] PCMembers, MergeConfig mc, CEValue preferredValue, CanShowOutput canShowOutput) {
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
                                        Arrays.stream(new int[]{i}).boxed().collect(Collectors.toList()), PCMembers, char_Star, null, mc.zc);
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
    }*/

/*    public static void doMerge(Collection<AbstractCE> old, List<AbstractCE> result, int[] PCMembers,
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
    }*/


    public static List<Integer> getMergePoistion(String xor) {
        List<Integer> positionsList = new ArrayList<>();
        boolean hasNumer = false, hasSymbol = false;
        for(int i = 0; i < xor.length(); i++){
            if(xor.charAt(i) == '1'){
                hasNumer = true;
                positionsList.add(i);
            }
            else if (xor.charAt(i) == char_QUESTION | xor.charAt(i) == char_Star) {
                hasSymbol = true;
                positionsList.add(i);
            }
        }
        if (hasNumer & hasSymbol) // X11 vs 010, if this line is commended, they would be merged; no merge otherwise.
            return new ArrayList<>();
//        System.out.println("---------------"+xor+" "+ Joiner.on(',').join(positionsList));
        return positionsList;
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
        int distance;
        double diffCE;
        for(int j=1; j<n; j++) {
            row = distanceMatrix.row(j);

            for(int k=0; k<j; k++) {
                distance = row.get(k);
                diffCE = Math.abs(CEList.get(j).statistics[4] - CEList.get(k).statistics[4]);
                location.add(new DistMeasure.minDistLocation(j, k, distance, diffCE));
            }
        }
        return location;
    }

    public static void updateDistMeasure(DistMeasure distMeasure, ArrayList<AbstractCE> list, int rowIndex) {
        AbstractCE jCE = list.get(rowIndex);
        char[] jCEValue = jCE.value;
        for (int c=0; c<list.size(); c++) {
            AbstractCE kCE = list.get(c);
            if (kCE == null)
                continue;
            char[] kCEValue = kCE.value;
            char[] xorChars =new char[jCE.value.length];
            int dist = 0;
//            System.out.println("================");
            for (int p=0; p<jCE.value.length; p++) {
                if (jCEValue[p] == kCEValue[p])
                    xorChars[p] = '0';
                else if ((jCEValue[p]==char_QUESTION & kCEValue[p]!=char_QUESTION) |
                        (jCEValue[p]!=char_QUESTION & kCEValue[p]==char_QUESTION)) {
//                    System.out.println("================?????????");
                    xorChars[p] = char_QUESTION;
                    dist++;
                }
                else {
                    xorChars[p] = '1';
                    dist++;
                }
            }
            String xor = String.valueOf(xorChars);
//            System.out.println("================"+xor);
            distMeasure.xorMatrix.put(rowIndex, c, xor);
            distMeasure.distanceMatrix.put(rowIndex, c, dist);
        }
    }

//    public static void doMergeOne(List<AbstractCE> CEList, ArrayList<AbstractCE> list, ArrayList<AbstractCE> reliableList,
//                                  int GT, List<Integer> positions, int[] PCMembers, int jKey, int kKey, Integer jValue, Integer kValue,
//                                  double zc, double reliabilityMinSupport, DistMeasure distMeasure) {
//        AbstractCE newCE = list.get(jValue).mergeInstance(list.get(kValue), GT,
//                positions, PCMembers, char_QUESTION, null, zc);
//        if (newCE.reliable)
//            reliableList.add(newCE);
//        list.set(jValue, newCE);
//        if(jKey != jValue)
//            list.set(jKey, null);
//        if(kKey != jValue)
//            list.set(kKey, null);
//        if(kValue != jValue)
//            list.set(kValue, null);
//        updateDistMeasure(distMeasure, list, jValue);
//    }


    public static void printMatrix(Table<Integer, Integer, Integer> matrix, List<AbstractCE> CEList, CanShowOutput canShowOutput) {
        StringBuilder sb = new StringBuilder();
        if (CEList.get(0).value.length > 6)
            sb.append("\t");
        sb.append("Matrix");
        for (int i=0; i<CEList.size(); i++)
            sb.append("\t" + String.valueOf(CEList.get(i).value));
        canShowOutput.showOutputString(sb.toString());

        for (int i=0; i<CEList.size(); i++) {
            sb = new StringBuilder();
            sb.append(String.valueOf(CEList.get(i).value) + " (" + (CEList.get(i).isSignificant ? "\u2713" : "x") + ")\t");
            for (int j=0; j<i; j++) {
                sb.append(Integer.toString(matrix.get(i, j)) + "\t");
                if (CEList.get(i).value.length > 6)
                    sb.append("\t");
            }
            canShowOutput.showOutputString(sb.toString());
        }
    }


    public static int getNumSignificant(Collection<AbstractCE> CEList) {
        int numSignificant = 0;
        for (AbstractCE i : CEList)
            if (i.isSignificant)
                numSignificant ++;
        return numSignificant;
    }


    public static boolean isMostGeneral(char[] value) {
        for (char i : value) {
            if (i != char_QUESTION)
                return false;
        }
        return true;
    }


    public static AbstractCE furtherGeneralisation(Collection<AbstractCE> old, int GT, AbstractCE patt, double significanceLevel) {
        char[] pattValue = patt.value;
        int valueLength = pattValue.length;
        List<Integer> toBePositions = new ArrayList<>();
        for (int i=0; i<valueLength; i++)
            if (patt.value[i] != char_QUESTION)
                toBePositions.add(i);

        char[] possPattValue = Arrays.copyOf(pattValue, pattValue.length);
        TrueFalseCE newPatt = new TrueFalseCE(pattValue);
        double largerSignificance = -1;
        for (int i=0; i<toBePositions.size(); i++) {
            int[] stats = new int[4];
            possPattValue[toBePositions.get(i)] = char_QUESTION;
            TrueFalseCE possPatt = new TrueFalseCE(possPattValue);
            for (AbstractCE o : old) {
                if (ToolFunctions.isSamePatternGroup(o.value, possPattValue))
                    for (int j=0; j<4; j++)
                        stats[j] += o.statistics[j];
            }
            possPatt.updateItem(stats);
            possPatt.updateStatistics(GT);
            possPatt.updateSignificance(significanceLevel);

            if (possPatt.significance > largerSignificance) {
                largerSignificance = possPatt.significance;
                newPatt = possPatt;
            }
        }

        return newPatt;
    }


    public static void doMergeTwoConstraints(Collection<AbstractCE> old, int GT, List<AbstractCE> result, int[] PCMembers,
                                             int[] order, int[] reverseOrder, TestConfig config,
                                             HashSet<Integer> positionNotFitOddsRatio, CanShowOutput canShowOutput, boolean debug) {
        /*
        More specifically, the search strategy is as the following.
        1. for each insignificant pattern, find its closest patterns with the smallest edit distance.
        2. In the set of closest patterns, chose the pattern with the closest treatment effect value to generalise with
        the given pattern.
         */
        List<AbstractCE> CEList = new ArrayList<>();
        int num_significant = 0;
        double significanceLevel = config.transSignificanceLevel();
        boolean hasMostGeneral = false;

        for (AbstractCE i : old) {
            i.updateSignificance(significanceLevel);
            if (i.isSignificant) {
                num_significant++;
                result.add(i);
            }
            i.updateStatistics(GT);
            CEList.add(i);
        }

        DistMeasure distMeasure = new DistMeasure();
        distMeasure.buildDistanceMatrix(CEList);
        List<DistMeasure.minDistLocation> location = getMinDistLocation(CEList, distMeasure.distanceMatrix);
//        printMatrix(distMeasure.distanceMatrix, CEList.size(), canShowOutput);
        sortDist(location);
        int minDist = location.get(0).distance;

        int rowIdx, colIdx, dist;
        // Initially get first location of minimal distance (idx_loc = 0). Sometimes the first location involved all significant patterns,
        // then get the next location in sorted locations (idx_loc++)
        int idx_loc = 0;
        StringBuilder tmp_sb = new StringBuilder();
        while (CEList.size() - num_significant > 1 && minDist < order.length && minDist > 0) {
//            canShowOutput.showOutputString("*****************CEList " + Integer.toString(CEList.size()) + " *numSign " + Integer.toString(num_significant) +
//                    " *minDist " + Double.toString(minDist) + " *order " + Integer.toString(order.length) + "************************");
            if (debug) {
                printMatrix(distMeasure.distanceMatrix, CEList, canShowOutput);
                canShowOutput.showOutputString("Pattern size (significant): " + Integer.toString(CEList.size()) + " (" +
                        Integer.toString(num_significant) + ")" + ";\tminimal distance: " + Double.toString(minDist));
            }
            DistMeasure.minDistLocation loc = location.get(idx_loc);
            rowIdx = loc.rowIndex;
            colIdx = loc.colIndex;
            dist = loc.distance;
            boolean allSignificant = CEList.get(rowIdx).isSignificant;

            List<AbstractCE> removeIdxList = new ArrayList<>();
            removeIdxList.add(CEList.get(rowIdx));

            String xor_tmp = distMeasure.xorMatrix.get(rowIdx, colIdx);
            List<Integer> positions = getMergePoistion(xor_tmp);
            StringBuilder sb = new StringBuilder();
            for (Integer p : positions)
                sb.append(Integer.valueOf(p));

            AbstractCE newCE;
            if (dist == 1) {
                // dist==1, merge two patterns
                if (allSignificant)
                    if (CEList.get(colIdx).isSignificant) {
                        idx_loc++;
                        continue;
                    }
                if (debug)
                    canShowOutput.showOutputString("miniDistance=1, 2 patterns would be merged: " + String.valueOf(CEList.get(rowIdx).value) +
                            " & " + String.valueOf(CEList.get(colIdx).value) + "\n");
                newCE = CEList.get(rowIdx).mergeInstance(CEList.get(colIdx), GT,
                        positions, PCMembers, char_QUESTION, null, significanceLevel);
                removeIdxList.add(CEList.get(colIdx));
            }
            else {
                // dist>1, find all pattern based on merge position and merge all of them
                char[] attrValue;
                boolean toBeMerge = false;
                List<AbstractCE> tmpCEList = new ArrayList<>();
                for (int i=0; i<CEList.size(); i++) {
                    if (i == rowIdx)
                        continue;
                    attrValue = CEList.get(i).value;
                    toBeMerge = ToolFunctions.isSamePatternGroup(attrValue, CEList.get(rowIdx).value, positions);
//                    canShowOutput.showOutputString(sb + " * " + String.valueOf(attrValue) + " * " + String.valueOf(CEList.get(rowIdx).value) + " * " + Boolean.toString(toBeMerge));
                    if (toBeMerge) {
                        if (allSignificant)
                            allSignificant &= CEList.get(i).isSignificant;
                        removeIdxList.add(CEList.get(i));
                        tmpCEList.add(CEList.get(i));
                    }
                }
//                canShowOutput.showOutputString(Boolean.toString(allSignificant) + " " + Integer.toString(CEList.size()) + " " + Integer.toString(num_significant));
                if (allSignificant) {
                    idx_loc++;
                    continue;
                }
                if (debug) {
                    tmp_sb = new StringBuilder();
                    tmp_sb.append(String.valueOf(CEList.get(rowIdx).value) + " & ");
                    for (AbstractCE ab : tmpCEList)
                        tmp_sb.append(String.valueOf(ab.value) + " & ");
                    canShowOutput.showOutputString("miniDistance=" + Integer.toString(dist) + ", " + Integer.toString(tmpCEList.size() + 1) +
                            " patterns would be merged: " + tmp_sb +"\n");
                }
                newCE = CEList.get(rowIdx).mergeInstanceList(tmpCEList, GT,
                        positions, PCMembers, char_QUESTION, null, significanceLevel);
            }
            newCE.updateSignificance(significanceLevel);
            if (newCE.isSignificant) {
                hasMostGeneral = isMostGeneral(newCE.value);
                result.add(newCE);
            }
            CEList.removeAll(removeIdxList);
            CEList.add(newCE);
            num_significant = getNumSignificant(CEList);

            // generate new distance matrix with updated pattern set
            distMeasure = new DistMeasure();
            distMeasure.buildDistanceMatrix(CEList);
            location = getMinDistLocation(CEList, distMeasure.distanceMatrix);
            if (location.size() < 1)
                break;
            sortDist(location);
            // distance matrix updated, get the first location of minimal distance
            idx_loc = 0;
            minDist = location.get(idx_loc).distance;
        }

        for (AbstractCE i : CEList)
            if (! i.isSignificant) {
                hasMostGeneral = isMostGeneral(i.value);
                boolean isSignificant = i.isSignificant;
                TrueFalseCE tmpCE = new TrueFalseCE(i.value);
                while (!(hasMostGeneral || isSignificant)) { // repeat generalisation process, till significant or most general
                    tmpCE = (TrueFalseCE) furtherGeneralisation(old, GT, tmpCE, significanceLevel);
                    hasMostGeneral = isMostGeneral(tmpCE.value);
                    isSignificant = tmpCE.isSignificant;
                }
                result.add(tmpCE);
            }

        // if the most general pattern was not included, add it in
        if (! hasMostGeneral) {
            char[] mostGeneralValue = new char[order.length];
            for (int idx=0; idx<order.length; idx++)
                mostGeneralValue[idx] = char_QUESTION;
            int[] stats = new int[4];
            for (AbstractCE o : old) {
                for (int idx=0; idx<4; idx++)
                    stats[idx] += o.statistics[idx];
            }

            TrueFalseCE mostGeneralPattern = new TrueFalseCE(mostGeneralValue);
            mostGeneralPattern.updateItem(stats);
            mostGeneralPattern.updateStatistics(GT);
            mostGeneralPattern.updateSignificance(significanceLevel);
            result.add(mostGeneralPattern);
        }
    }

//    public static void doMergeEffectHomo(Collection<AbstractCE> old, int GT, List<AbstractCE> result, int[] PCMembers,
//                               int[] order, int[] reverseOrder, double zc, double reliabilityMinSupport,
//                               HashSet<Integer> positionNotFitOddsRatio, int mergeDepth, CanShowOutput canShowOutput) {
//        List<AbstractCE> CEList = new ArrayList<>();
//        for (AbstractCE i : old) {
//            i.updateReliable(reliabilityMinSupport);
//            i.updateStatistics(GT);
//            CEList.add(i);
//        }
//
//        DistMeasure distMeasure = new DistMeasure();
//        distMeasure.buildDistanceMatrix(CEList);
//        List<DistMeasure.minDistLocation> location = getMinDistLocation(CEList, distMeasure.distanceMatrix);
//        sortDist(location);
//
//        HashMap<Integer, Integer> map = new HashMap<>();
//        ArrayList<AbstractCE> list = new ArrayList<>(CEList.size());
//        ArrayList<AbstractCE> reliableList = new ArrayList<>(CEList.size());
//        for (int i=0; i<CEList.size(); i++) {
//            list.add(CEList.get(i));
//            map.put(i, i);
//            if (CEList.get(i).reliable)
//                reliableList.add(CEList.get(i));
//        }
//
//        int jKey, kKey;
//        Integer jValue, kValue;
//        boolean notMerge;
//        for(DistMeasure.minDistLocation loc : location) {
//            jKey = loc.rowIndex;
//            kKey = loc.colIndex;
//            jValue = map.get(jKey);
//            kValue = map.get(kKey);
//            notMerge = list.get(jValue).reliable & list.get(kValue).reliable; // at least one unreliable
//            if (notMerge || jValue == kValue)
//                continue;
//
//            String xor = distMeasure.xorMatrix.get(jValue, kValue);
//            List<Integer> positions = getMergePoistion(xor);
//            if (positions.size() == 0)
//                continue;
//            doMergeOne(CEList, list, reliableList, GT, positions, PCMembers, jKey, kKey, jValue, kValue, zc,
//                    reliabilityMinSupport, distMeasure);
////            canShowOutput.showOutputString(distMeasure.xorMatrix.get(jValue, kValue));
//
//            List<Integer> keys = getKeysFromValue(map, kValue);
//            for(Integer k : keys)
//                map.put(k, jValue);
//        }
//
//        List<String> listString = new ArrayList<>();
//        for (AbstractCE i : list)
//            if (i != null) {
//                result.add(i);
//                listString.add(String.valueOf(i.value));
//            }
////        for (AbstractCE r : reliableList) {
////            String rString = String.valueOf(r.value);
////            boolean existing = false;
////            for (String iString : listString) {
////                if (rString.equals(iString))
////                    existing = true;
////            }
////            if (!existing)
////                result.add(r);
////        }
//    }

//    public static void doMergeReliable(Collection<AbstractCE> old, int GT, List<AbstractCE> result, int[] PCMembers,
//                                         int[] order, int[] reverseOrder, double zc,  double reliabilityMinSupport,
//                                         HashSet<Integer> positionNotFitOddsRatio, int mergeDepth, CanShowOutput canShowOutput) {
//        List<AbstractCE> CEList = new ArrayList<>();
//        for (AbstractCE i : old) {
//            i.updateReliable(reliabilityMinSupport);
//            i.updateStatistics(GT);
//            CEList.add(i);
//        }
//
//        HashMap<Integer, Integer> map = new HashMap<>();
//        ArrayList<AbstractCE> list = new ArrayList<>(CEList.size());
//        ArrayList<AbstractCE> reliableList = new ArrayList<>(CEList.size());
//        for (int i=0; i<CEList.size(); i++) {
//            list.add(CEList.get(i));
//            map.put(i, i);
//            if (CEList.get(i).reliable)
//                reliableList.add(CEList.get(i));
//        }
//
//        DistMeasure distMeasure = new DistMeasure();
//        distMeasure.buildDistanceMatrix(CEList);
//
//        List<DistMeasure.minDistLocation> location = getMinDistLocation(CEList, distMeasure.distanceMatrix);
//        sortDist(location);
//        int jKey, kKey;
//        Integer jValue, kValue;
//        for(int a=0; a<location.size(); a++) {
//            DistMeasure.minDistLocation loc = location.get(a);
//            jKey = loc.rowIndex;
//            kKey = loc.colIndex;
//            jValue = map.get(jKey);
//            kValue = map.get(kKey);
//            boolean notMerge = list.get(jValue).reliable | list.get(kValue).reliable; // both unreliable, then merge
//            if (notMerge || jValue == kValue)
//                continue;
//
//            String xor = distMeasure.xorMatrix.get(jValue, kValue);
//            List<Integer> positions = getMergePoistion(xor);
//            if (positions.size() == 0)
//                continue;
//
//            List<Integer> kValueList = new ArrayList<>();
//            kValueList.add(kValue);
//            if (distMeasure.distanceMatrix.get(jValue, kValue) > 1) {
//                for (int c=0; c<list.size(); c++) {
//                    if (kValueList.contains(c) || c==jValue || list.get(c)==null)
//                        continue;
//                    String xor_tmp = distMeasure.xorMatrix.get(jValue, c);
////                    canShowOutput.showOutputString(xor+"============"+xor_tmp);
//                    List<Integer> tmp_positions = getMergePoistion(xor_tmp);
//                    if (tmp_positions.size() == 0)
//                        continue;
//                    boolean flag = true;
//                    for (int p : tmp_positions)
//                        if (!positions.contains(p))
//                            flag = false;
//                    if (flag) {
////                        canShowOutput.showOutputString("yes");
//                        kValueList.add(c);
//                        continue;
//                    }
//
//                    xor_tmp = distMeasure.xorMatrix.get(kValue, c);
//                    tmp_positions = getMergePoistion(xor_tmp);
//                    if (tmp_positions.size() == 0)
//                        continue;
//                    flag = true;
//                    for (int p : tmp_positions)
//                        if (!positions.contains(p))
//                            flag = false;
//                    if (flag)
//                        kValueList.add(c);
//                }
//            }
//
//            for (Integer k : kValueList) {
//                doMergeOne(CEList, list, reliableList, GT, positions, PCMembers, jKey, kKey, jValue, k, zc,
//                        reliabilityMinSupport, distMeasure);
////                canShowOutput.showOutputString(xor+"----------"+distMeasure.xorMatrix.get(jValue, k));
//
//                List<Integer> keys = getKeysFromValue(map, k);
//                for (Integer key : keys) {
//                    map.put(key, jValue);
//                }
//            }
//        }
//
//        List<String> listString = new ArrayList<>();
//        for (AbstractCE i : list)
//            if (i != null) {
//                result.add(i);
//                listString.add(String.valueOf(i.value));
//            }
////        for (AbstractCE r : reliableList) {
////            String rString = String.valueOf(r.value);
////            boolean existing = false;
////            for (String iString : listString) {
////                if (rString.equals(iString))
////                    existing = true;
////            }
////            if (!existing)
////                result.add(r);
////        }
//    }

//    public static void refinePattern(Collection<AbstractCE> old, int GT, List<AbstractCE> result, int[] PCMembers,
//                                     int[] order, int[] reverseOrder, double zc,  double reliabilityMinSupport,
//                                     HashSet<Integer> positionNotFitOddsRatio, int mergeDepth, CanShowOutput canShowOutput) {
//        for (int i=0; i<result.size(); i++) {
//            char[] pattern = result.get(i).value;
//            double[] stats = new double[5];
//            List<Integer> varIdx = new ArrayList<>();
//            for (int j=0; j<pattern.length; j++) {
//                if (pattern[j] != char_QUESTION)
//                    varIdx.add(j);
//            }
//            List<Integer> PCIdx = new ArrayList<>();
//            int j = 0;
//            for (int ord : order) {
//                if (IntStream.of(PCMembers).anyMatch(x -> x == ord))
//                    PCIdx.add(j);
//                j++;
//            }
//
//            for (AbstractCE oldce : old) {
//                char[] record = oldce.value;
//                boolean matched = true;
//                for (int idx : varIdx) {
//                    if (pattern[idx] != record[idx]) {
//                        matched = false;
//                        break;
//                    }
//                }
//                if (matched) {
//                    ((TrueFalseCE) result.get(i)).updateItem(oldce.statistics);
//                }
////                canShowOutput.showOutputString("before"+Arrays.toString(oldce.statistics));
//            }
//            result.get(i).updateStatistics(GT);
////            canShowOutput.showOutputString(Arrays.toString(result.get(i).value)+"\t"+Arrays.toString(result.get(i).statistics));
//        }
//    }

}
