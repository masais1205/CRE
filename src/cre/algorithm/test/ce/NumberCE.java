package cre.algorithm.test.ce;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by HanYizhao on 4/13/2017.
 */
public class NumberCE extends AbstractCE {

    private List<Double> trueList = new ArrayList<>();
    private List<Double> falseList = new ArrayList<>();

    public NumberCE(char[] buffer) {
        super(buffer);
//        groundTruthValue = 0;
    }


    public void addItem(boolean W, double Y) {
        if (W) {
            trueList.add(Y);
        } else {
            falseList.add(Y);
        }
    }

//    @Override
    public void updateGTValue(double GTValue) {
        double tmpValue = groundTruthValue * getInstanceNumber();
        tmpValue += GTValue;
        groundTruthValue = tmpValue / (getInstanceNumber() + 1);
    }

    @Override
    public AbstractCE mergeInstance(AbstractCE cc, int GT, List<Integer> position, int[] PCMembers, char positionChar,
                                    CEValue preferredValue, double zc) {
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

    @Override
    public AbstractCE mergeInstanceList(Collection<AbstractCE> ccList, int GT, List<Integer> position, int[] PCMembers, char positionChar,
                                    CEValue preferredValue, double zc) {
        NumberCE result = new NumberCE(this.value);
        for (AbstractCE cc : ccList) {
            NumberCE c2 = (NumberCE) cc;
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
    public void updateSignificance(double significanceLevel) {
        double X1 = mean(trueList);
        double X2 = mean(falseList);

        double var1 = var(trueList, X1);
        double var2 = var(falseList, X2);

        double t = (X1 - X2)
                / Math.sqrt(var1 / trueList.size() + var2 / falseList.size());

        isSignificant = t >= significanceLevel ? true : false;
    }

    @Override
    public int getInstanceNumber() {
        return trueList.size() + falseList.size();
    }


    @Override
    public void updateStatistics(int GT) {
        statistics[0] = trueList.size();
        statistics[1] = falseList.size();
        for(int i=2; i<5; i++)
            statistics[i] = -1;
    }


    @Override
    public void updateReliable(double reliabilityMinSupport) {
        int num = getInstanceNumber();
        reliable = num >= reliabilityMinSupport ? true : false;
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
