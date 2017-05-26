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
        for (Statistic i : data) {
            result.accuracy += i.accuracy;
            result.testNoMatch += i.testNoMatch;
            result.patternMatch += i.patternMatch;
        }
        result.accuracy = result.accuracy / data.size();
        result.testNoMatch = result.testNoMatch / data.size();
        result.patternMatch = result.patternMatch / data.size();
        return result;
    }

    @Override
    public String toString() {
        return "Average:\nAccuracy:\t" + String.format(Locale.ENGLISH, "%.2f", accuracy * 100) + "%\n"
                + "Testing Data not matched:\t" + String.format(Locale.ENGLISH, "%.2f", testNoMatch * 100) + "%\n"
                + "Pattern(testing / training):\t" + String.format(Locale.ENGLISH, "%.2f", patternMatch * 100) + "%";
    }
}
