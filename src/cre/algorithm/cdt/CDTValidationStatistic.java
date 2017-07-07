package cre.algorithm.cdt;

import cre.algorithm.tool.OtherTool;

import java.util.Collection;
import java.util.List;

/**
 * Created by HanYizhao on 2017/7/6.
 */
public class CDTValidationStatistic {
    public int correctCount;
    public int inCorrectCount;
    public double correct;
    public double incorrect;
    public double meanAbsoluteError;
    public double rootMeanSquareError;
    public double relativeAbsoluteError;
    public double rootRelativeSquaredError;

    private CDTValidationStatistic() {
    }

    public CDTValidationStatistic(List<String> real, List<String> test) throws Exception {
        if (real.size() != test.size()) {
            throw new Exception("Size of real is not equal with test");
        }
        double realSum = 0;
        for (String i : real) {
            realSum += Integer.parseInt(i);
        }
        double realMean = realSum / real.size();
        double RAE_TopSum = 0;
        double RAE_BottomSum = 0;
        double RRSE_TopSum = 0;
        double RRSE_BottomSum = 0;
        for (int i = 0; i < real.size(); i++) {
            String s1 = real.get(i);
            String s2 = test.get(i);
            if (s1.equals(s2)) {
                correctCount++;
            } else {
                inCorrectCount++;
            }
            int i1 = Integer.parseInt(s1);
            int i2 = Integer.parseInt(s2);
            i1 = i1 == 1 ? 0 : 1;
            i2 = i2 == 1 ? 0 : 1;
            int abs = Math.abs(i1 - i2);
            double pow = Math.pow(i1 - i2, 2);
            meanAbsoluteError += abs;
            rootMeanSquareError += pow;
            RAE_TopSum += abs;
            RAE_BottomSum += Math.abs(realMean - i1);
            RRSE_TopSum += pow;
            RRSE_BottomSum += Math.pow(realMean - i1, 2);
        }
        correct = (double) correctCount / (correctCount + inCorrectCount);
        incorrect = (double) inCorrectCount / (correctCount + inCorrectCount);
        meanAbsoluteError = meanAbsoluteError / real.size();
        rootMeanSquareError = Math.sqrt(rootMeanSquareError / real.size());
        relativeAbsoluteError = RAE_TopSum / RAE_BottomSum;
        rootRelativeSquaredError = Math.sqrt(RRSE_TopSum / RRSE_BottomSum);
    }

    public static CDTValidationStatistic average(Collection<CDTValidationStatistic> all) {
        CDTValidationStatistic n = new CDTValidationStatistic();
        int[] count = new int[6];
        for (CDTValidationStatistic i : all) {
            n.correctCount += i.correctCount;
            n.inCorrectCount += i.inCorrectCount;
            if (!Double.isNaN(i.correct) && !Double.isInfinite(i.correct)) {
                n.correct += i.correct;
                count[0]++;
            }
            if (!Double.isNaN(i.incorrect) && !Double.isInfinite(i.incorrect)) {
                n.incorrect += i.incorrect;
                count[1]++;
            }
            if (!Double.isNaN(i.meanAbsoluteError) && !Double.isInfinite(i.meanAbsoluteError)) {
                n.meanAbsoluteError += i.meanAbsoluteError;
                count[2]++;
            }
            if (!Double.isNaN(i.rootMeanSquareError) && !Double.isInfinite(i.rootMeanSquareError)) {
                n.rootMeanSquareError += i.rootMeanSquareError;
                count[3]++;
            }
            if (!Double.isNaN(i.relativeAbsoluteError) && !Double.isInfinite(i.relativeAbsoluteError)) {
                n.relativeAbsoluteError += i.relativeAbsoluteError;
                count[4]++;
            }
            if (!Double.isNaN(i.rootRelativeSquaredError) && !Double.isInfinite(i.rootRelativeSquaredError)) {
                n.rootRelativeSquaredError += i.rootRelativeSquaredError;
                count[5]++;
            }
        }
        n.correctCount = Math.round((float) n.correctCount / all.size());
        n.inCorrectCount = Math.round((float) n.inCorrectCount / all.size());
        n.correct = n.correct / count[0];
        n.incorrect = n.incorrect / count[1];
        n.meanAbsoluteError = n.meanAbsoluteError / count[2];
        n.rootMeanSquareError = n.rootMeanSquareError / count[3];
        n.relativeAbsoluteError = n.relativeAbsoluteError / count[4];
        n.rootRelativeSquaredError = n.rootRelativeSquaredError / count[5];
        return n;
    }


    @Override
    public String toString() {
        return
                "Correctly Classified Instances\t" + correctCount +
                        "\t" + OtherTool.fromDoubleToString(true, correct) +
                        "\nIncorrectly Classified Instances\t" + inCorrectCount +
                        "\t" + OtherTool.fromDoubleToString(true, incorrect) +
                        "\nMean absolute error\t" + OtherTool.fromDoubleToString(meanAbsoluteError) +
                        "\nRoot mean squared error\t" + OtherTool.fromDoubleToString(rootMeanSquareError) +
                        "\nRelative absolute error\t" + OtherTool.fromDoubleToString(true, relativeAbsoluteError) +
                        "\nRoot relative squared error\t" + OtherTool.fromDoubleToString(true, rootRelativeSquaredError) +
                        "\nTotal Number of Instances\t" + (correctCount + inCorrectCount);
    }
}
