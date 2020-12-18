package cre.algorithm.test.ce;

/**
 * Created by HanYizhao on 2017/5/17.
 */
public class LineValue {
    private char[] value;
    private int[] WYValues;
    private double groundTruthValue;

    public LineValue(char[] buffer) {
        value = new char[buffer.length];
        System.arraycopy(buffer, 0, value, 0, buffer.length);
        WYValues = new int[4];
//        groundTruthValue = 0;
    }

    public int[] getWYValues() {
        return WYValues;
    }

    public int getWYSum() {
        return WYValues[0] + WYValues[1] + WYValues[2] + WYValues[3];
    }

    public void addSomeItem(int[] values) {
        if (values.length == 4) {
            for (int i = 0; i < 4; i++) {
                WYValues[i] += values[i];
            }
        }
    }

    public void addItem(boolean W, boolean Y) {
        if (W) {
            if (Y) {
                WYValues[0]++;
            } else {
                WYValues[1]++;
            }
        } else {
            if (Y) {
                WYValues[2]++;
            } else {
                WYValues[3]++;
            }
        }
    }

    public void updateGTValue(double GTValue) {
        double tmpValue = groundTruthValue * getWYSum();
        tmpValue += GTValue;
        groundTruthValue = tmpValue / (getWYSum() + 1);
    }

    public void updateGTValue() {
        double[] WYValuesTmp = new double[4];
        for (int i=0; i<4; i++)
            WYValuesTmp[i] = WYValues[i] == 0 ? 0.5 : WYValues[i];

        double WAll0 = WYValuesTmp[2] + WYValuesTmp[3];
        double WAll1 = WYValuesTmp[0] + WYValuesTmp[1];

        double p1 = WYValuesTmp[0] / WAll1;
        double p2 = WYValuesTmp[2] / WAll0;

        groundTruthValue = p1 - p2;
    }

    public char[] getValue() {
        return value;
    }

    public double getGroundTruthValue() {return groundTruthValue;};
}
