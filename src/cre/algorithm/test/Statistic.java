package cre.algorithm.test;

import java.util.Collection;
import java.util.Locale;

/**
 * Created by HanYizhao on 2017/5/24.
 */
public class Statistic {
    public double accuracy;
    public double testNoMatch;
    public double patternMatch;

    public static Statistic average(Collection<Statistic> data) {
        Statistic result = new Statistic();
        int accuracyCount = 0;
        int testNoMatchCount = 0;
        int patternMatchCount = 0;
        for (Statistic i : data) {
            if (!Double.isNaN(i.accuracy) && !Double.isInfinite(i.accuracy)) {
                result.accuracy += i.accuracy;
                accuracyCount++;
            }
            if (!Double.isNaN(i.testNoMatch) && !Double.isInfinite(i.testNoMatch)) {
                result.testNoMatch += i.testNoMatch;
                testNoMatchCount++;
            }
            if (!Double.isNaN(i.patternMatch) && !Double.isInfinite(i.patternMatch)) {
                result.patternMatch += i.patternMatch;
                patternMatchCount++;
            }
        }
        result.accuracy = result.accuracy / accuracyCount;
        result.testNoMatch = result.testNoMatch / testNoMatchCount;
        result.patternMatch = result.patternMatch / patternMatchCount;
        return result;
    }

    @Override
    public String toString() {
        return "Average:\nAccuracy:\t" + String.format(Locale.ENGLISH, "%.2f", accuracy * 100) + "%\n"
                + "Testing Data not matched:\t" + String.format(Locale.ENGLISH, "%.2f", testNoMatch * 100) + "%\n"
                + "Pattern(testing / training):\t" + String.format(Locale.ENGLISH, "%.2f", patternMatch * 100) + "%";
    }
}
