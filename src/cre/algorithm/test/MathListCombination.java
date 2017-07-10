package cre.algorithm.test;

import java.util.*;

/**
 * Created by HanYizhao on 2017/5/26.
 */
public class MathListCombination {

    private static class MyIntArray {
        int[] value;

        MyIntArray(int[] value) {
            this.value = value;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(value);
        }

        @Override
        public boolean equals(Object obj) {
            return !(obj == null || !(obj instanceof MyIntArray))
                    && Arrays.equals(this.value, ((MyIntArray) obj).value);
        }
    }

    private static class Temp implements Comparable<Temp> {
        int[] value;
        int sum;

        Temp(int singleValue) {
            value = new int[]{singleValue};
            sum = singleValue;
        }

        Temp(Temp original, int value, int position) {
            this.value = new int[original.value.length + 1];
            sum = original.sum + value;
            if (position > 0) {
                System.arraycopy(original.value, 0, this.value, 0, position);
            }
            this.value[position] = value;
            if (position < original.value.length) {
                System.arraycopy(original.value, position, this.value,
                        position + 1, original.value.length - position);
            }
        }

        @Override
        public int compareTo(Temp o) {
            return this.sum - o.sum;
        }
    }

    public static List<List<int[]>> listAllCombination(final int[] original, int maxSubLength) {
        int length = original.length;
        List<Temp> tempList = new ArrayList<>();
        List<int[]> realResult = new ArrayList<>();
        List<List<int[]>> trueResult = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            tempList.add(new Temp(i));
        }
        if (maxSubLength >= 1) {
            for (int anOriginal : original) {
                realResult.add(new int[]{anOriginal});
            }
            trueResult.add(realResult);
        }

        int nowSubLength = 2;
        while (nowSubLength <= maxSubLength && nowSubLength <= length) {
            realResult = new ArrayList<>();
            List<Temp> newTempList = new ArrayList<>();
            HashSet<MyIntArray> checkSet = new HashSet<>();
            for (int i = 0; i < length; i++) {
                for (Temp temp : tempList) {
                    int a = Arrays.binarySearch(temp.value, i);
                    if (a < 0) {
                        Temp newTemp = new Temp(temp, i, -(a + 1));
                        MyIntArray tempA = new MyIntArray(newTemp.value);
                        if (!checkSet.contains(tempA)) {
                            newTempList.add(newTemp);
                            checkSet.add(tempA);
                        }
                    }
                }
            }
            Collections.sort(newTempList);
            for (Temp i : newTempList) {
                int[] v = new int[i.value.length];
                for (int k = 0; k < i.value.length; k++) {
                    v[k] = original[i.value[k]];
                }
                realResult.add(v);
            }
            trueResult.add(realResult);
            nowSubLength++;
            tempList = newTempList;
        }
        return trueResult;
    }


}
