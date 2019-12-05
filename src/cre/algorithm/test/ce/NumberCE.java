package cre.algorithm.test.ce;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HanYizhao on 4/13/2017.
 */
public class NumberCE extends AbstractCE {

    private List<Double> trueList = new ArrayList<>();
    private List<Double> falseList = new ArrayList<>();


    public NumberCE(char[] buffer) {
        super(buffer);
    }


    public void addItem(boolean W, double Y) {
        if (W) {
            trueList.add(Y);
        } else {
            falseList.add(Y);
        }
    }

    @Override
    public AbstractCE mergeInstance(AbstractCE cc, int[] position, int[] PCMembers, char positionChar, CEValue preferredValue, double zc) {
        NumberCE c2 = (NumberCE) cc;
        NumberCE result = new NumberCE(this.value);
        for (int i : position) {
            result.value[i] = positionChar;
        }
        result.trueList.addAll(this.trueList);
        result.trueList.addAll(c2.trueList);
        result.falseList.addAll(this.falseList);
        result.falseList.addAll(c2.falseList);
        if (preferredValue != null) {
            result.cEValue = preferredValue;
        } else {
            result.updateCEValue(zc);
        }
        return result;
    }

    /**
     * mean
     *
     * @param data data
     * @return mean
     */
    private static double mean(List<Double> data) {
        double result = 0;
        for (double i : data) {
            result += i;
        }
        return result / data.size();
    }

    private static double var(List<Double> data, double mean) {
        double result = 0;
        for (double i : data) {
            result += (i - mean) * (i - mean);
        }
        return result / (data.size() - 1);
    }

    @Override
    public void updateCEValue(double zc) {
        double X1 = mean(trueList);
        double X2 = mean(falseList);

        double var1 = var(trueList, X1);
        double var2 = var(falseList, X2);

        double t = (X1 - X2)
                / Math.sqrt(var1 / trueList.size() + var2 / falseList.size());

        if (t > zc) {
            cEValue = CEValue.PLUS;
        } else if (t < -zc) {
            cEValue = CEValue.MINUS;
        } else {
            cEValue = CEValue.QUESTION;
        }
    }

    @Override
    public int getInstanceNumber() {
        return trueList.size() + falseList.size();
    }


    @Override
    public void updateStatistics() {
        statistics[0] = trueList.size();
        statistics[1] = falseList.size();
        for(int i=2; i<5; i++)
            statistics[i] = -1;
    }


    @Override
    public void updateReliable() {
        int num = getInstanceNumber();
        reliable = num >= 20 ? true : false;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (char aValue : value) {
            sb.append(aValue);
            sb.append('\t');
        }
        sb.append(cEValue);
        sb.append('\t');
        sb.append(trueList.size());
        sb.append('\t');
        sb.append(falseList.size());
        return sb.toString();
    }
}
