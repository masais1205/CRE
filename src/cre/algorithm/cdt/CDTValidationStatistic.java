package cre.algorithm.cdt;

import cre.algorithm.tool.OtherTool;

import javax.print.attribute.standard.MediaSize;
import java.util.Collection;
import java.util.List;

/**
 * Created by HanYizhao on 2017/7/6.
 * <p>This class is used to do validation. Store some variables returned from validation.</p>
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

    public int[][] confusionMatrix = new int[2][2];

    public double truePrecision, falsePrecision, trueRecall, falseRecall, trueFMeasure, falseFMeasure,
            weightedPrecision, weightedRecall, weightedFMeasure;

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
            i1 = i1 == 1 ? 1 : 0;
            i2 = i2 == 1 ? 1 : 0;
            confusionMatrix[i1][i2]++;
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

        truePrecision = getPrecision(true);
        falsePrecision = getPrecision(false);
        trueRecall = getRecall(true);
        falseRecall = getRecall(false);
        trueFMeasure = getFMeasure(true);
        falseFMeasure = getFMeasure(false);
        weightedPrecision = getWeightedPrecision();
        weightedRecall = getWeightedRecall();
        weightedFMeasure = getWeightedFMeasure();
    }

    public static CDTValidationStatistic average(Collection<CDTValidationStatistic> all) {
        CDTValidationStatistic n = new CDTValidationStatistic();
        int[] count = new int[6];
        for (CDTValidationStatistic i : all) {
            n.truePrecision += i.truePrecision;
            n.trueRecall += i.trueRecall;
            n.trueFMeasure += i.trueFMeasure;
            n.falsePrecision += i.falsePrecision;
            n.falseRecall += i.falseRecall;
            n.falseFMeasure += i.falseFMeasure;
            n.weightedFMeasure += i.weightedFMeasure;
            n.weightedPrecision += i.weightedPrecision;
            n.weightedRecall += i.weightedRecall;

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

        n.truePrecision /= all.size();
        n.trueRecall /= all.size();
        n.trueFMeasure /= all.size();
        n.falsePrecision /= all.size();
        n.falseRecall /= all.size();
        n.falseFMeasure /= all.size();
        n.weightedFMeasure /= all.size();
        n.weightedPrecision /= all.size();
        n.weightedRecall /= all.size();

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

    public double getWeightedFMeasure() {
        int sum1 = confusionMatrix[0][1] + confusionMatrix[0][0];
        int sum2 = confusionMatrix[1][0] + confusionMatrix[1][1];
        double precisionTotal = getFMeasure(false) * sum1;
        precisionTotal += getFMeasure(true) * sum2;
        return precisionTotal / (sum1 + sum2);
    }

    public double getWeightedRecall() {
        int sum = confusionMatrix[0][0] + confusionMatrix[1][1];
        if (sum == 0) {
            return 0;
        } else {
            return (double) sum
                    / (sum + confusionMatrix[1][0] + confusionMatrix[0][1]);
        }
    }

    public double getWeightedPrecision() {
        int sum1 = confusionMatrix[0][1] + confusionMatrix[0][0];
        int sum2 = confusionMatrix[1][0] + confusionMatrix[1][1];
        double precisionTotal = getPrecision(false) * sum1;
        precisionTotal += getPrecision(true) * sum2;
        return precisionTotal / (sum1 + sum2);
    }

    public double getFMeasure(boolean consider1) {

        double precision = getPrecision(consider1);
        double recall = getRecall(consider1);
        if ((precision + recall) == 0) {
            return 0;
        }
        return 2 * precision * recall / (precision + recall);
    }

    public double getRecall(boolean consider1) {
        int sum;
        if (consider1) {
            sum = confusionMatrix[1][0] + confusionMatrix[1][1];
            if (sum == 0) {
                return 0;
            } else {
                return (double) confusionMatrix[1][1] / sum;
            }
        } else {
            sum = confusionMatrix[0][0] + confusionMatrix[0][1];
            if (sum == 0) {
                return 0;
            } else {
                return (double) confusionMatrix[0][0] / sum;
            }
        }
    }

    public double getPrecision(boolean consider1) {
        int sum;
        if (consider1) {
            sum = confusionMatrix[0][1] + confusionMatrix[1][1];
            if (sum == 0) {
                return 0;
            } else {
                return (double) confusionMatrix[1][1] / sum;
            }
        } else {
            sum = confusionMatrix[0][0] + confusionMatrix[1][0];
            if (sum == 0) {
                return 0;
            } else {
                return (double) confusionMatrix[0][0] / sum;
            }
        }
    }

    public String getDetailedAccuracy() {
        String line = OtherTool.getLineSeparator();
        StringBuilder sb = new StringBuilder();
        sb.append("=== Detailed Accuracy By Class ===").append(line).append(line);
        sb.append("                 Precision  Recall  F-Measure  Class").append(line);
        sb.append("                 ").append(OtherTool.fromDoubleToString(falsePrecision));
        sb.append("    ").append(OtherTool.fromDoubleToString(falseRecall));
        sb.append("  ").append(OtherTool.fromDoubleToString(falseFMeasure));
        sb.append("     0").append(line);
        sb.append("                 ").append(OtherTool.fromDoubleToString(truePrecision));
        sb.append("    ").append(OtherTool.fromDoubleToString(trueRecall));
        sb.append("  ").append(OtherTool.fromDoubleToString(trueFMeasure));
        sb.append("     1").append(line);
        sb.append("Weighted Avg.    ").append(OtherTool.fromDoubleToString(weightedPrecision));
        sb.append("    ").append(OtherTool.fromDoubleToString(weightedRecall));
        sb.append("  ").append(OtherTool.fromDoubleToString(weightedFMeasure));
        sb.append(line);
        return sb.toString();

    }

    public String getConfusionMatrix() {
        String line = OtherTool.getLineSeparator();
        StringBuilder sb = new StringBuilder();
        sb.append("=== Confusion Matrix ===").append(line).append(line);
        sb.append("\ta\tb\t<-- classified as").append(line);
        sb.append('\t').append(confusionMatrix[0][0]).append('\t').append(confusionMatrix[0][1]);
        sb.append('\t').append("a = 0").append(line);
        sb.append('\t').append(confusionMatrix[1][0]).append('\t').append(confusionMatrix[1][1]);
        sb.append('\t').append("b = 1").append(line);
        return sb.toString();
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
