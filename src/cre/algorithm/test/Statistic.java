package cre.algorithm.test;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import cre.algorithm.test.ce.AbstractCE;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import static java.lang.Math.abs;

/**
 * Created by HanYizhao on 2017/5/24.
 */
public class Statistic {
    public double accuracy;
    public double testNoMatch;
    public double patternMatch;
    public double sd;
    public double recall;
    public double pehe;
    public double peheSD;


    public static Statistic average(Collection<Statistic> data) {
        Statistic result = new Statistic();
        List<Double> accuracyDetail = new ArrayList<>();
        List<Double> peheDetail = new ArrayList<>();
        int accuracyCount = 0;
        int peheCount = 0;
        int testNoMatchCount = 0;
        int patternMatchCount = 0;
        int recallCount = 0;
        for (Statistic i : data) {
            if (!Double.isNaN(i.accuracy) && !Double.isInfinite(i.accuracy)) {
                result.accuracy += i.accuracy;
                accuracyCount++;
                accuracyDetail.add(i.accuracy);
            }
            if (!Double.isNaN(i.pehe) && !Double.isInfinite(i.pehe)) {
                result.pehe += i.pehe;
                peheCount++;
                peheDetail.add(i.pehe);
            }
            if (!Double.isNaN(i.testNoMatch) && !Double.isInfinite(i.testNoMatch)) {
                result.testNoMatch += i.testNoMatch;
                testNoMatchCount++;
            }
            if (!Double.isNaN(i.patternMatch) && !Double.isInfinite(i.patternMatch)) {
                result.patternMatch += i.patternMatch;
                patternMatchCount++;
            }
            if (!Double.isNaN(i.recall) && !Double.isInfinite(i.recall)) {
                result.recall += i.recall;
                recallCount++;
            }
        }
        result.accuracy = result.accuracy / accuracyCount;
        result.pehe = result.pehe / peheCount;
        {
            double sum = 0;
            for (double i : accuracyDetail) {
                sum += Math.pow(i - result.accuracy, 2);
            }
            result.sd = Math.sqrt(sum / accuracyCount);
        }
        {
            double sum = 0;
            for (double i : peheDetail) {
                sum += Math.pow(i - result.pehe, 2);
            }
            result.peheSD = Math.sqrt(sum / peheCount);
        }
        result.testNoMatch = result.testNoMatch / testNoMatchCount;
        result.patternMatch = result.patternMatch / patternMatchCount;
        result.recall = result.recall / recallCount;
        return result;
    }

    @Override
    public String toString() {
//        return "Accuracy:\t" + String.format(Locale.ENGLISH, "%.2f", accuracy * 100) + "%\n"
//                + "Accuracy SD:\t" + String.format(Locale.ENGLISH, "%f", sd) + "\n"
//                + "Recall:\t" + String.format(Locale.ENGLISH, "%.2f", recall * 100) + "%\n"
//                + "Testing Data not matched:\t" + String.format(Locale.ENGLISH, "%.2f", testNoMatch * 100) + "%\n";
        return "PEHE:\t" + String.format(Locale.ENGLISH, "%.2f", pehe) + "\n"
                + "PEHE SD:\t" + String.format(Locale.ENGLISH, "%f", peheSD) + "\n"
                + "Testing Data not matched:\t" + String.format(Locale.ENGLISH, "%.2f", testNoMatch * 100) + "%\n";
    }
}
