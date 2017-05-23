package cre.algorithm.tool;

import java.util.List;

/**
 * Created by HanYizhao on 2017/5/17.
 */
public class OtherTool {
    public static int[] fromIntegerListToArray(List<Integer> original) {
        int[] result = new int[original.size()];
        for (int i = 0; i < original.size(); i++) {
            result[i] = original.get(i);
        }
        return result;
    }

    public static double[] fromIntArrayToNoZeroArray(int[] input) {
        double[] result = new double[input.length];
        for (int i = 0; i < input.length; i++) {
            result[i] = input[i] == 0 ? 0.5 : input[i];
        }
        return result;
    }

}
