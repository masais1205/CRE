package cre.algorithm.test.ce;

/**
 * Created by HanYizhao on 2017/5/17.
 */
public class LineValue {
    private char[] value;
    private int[] WYValues;

    public LineValue(char[] buffer) {
        value = new char[buffer.length];
        System.arraycopy(buffer, 0, value, 0, buffer.length);
        WYValues = new int[4];
    }

    public int[] getWYValues() {
        return WYValues;
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

    public char[] getValue() {
        return value;
    }


}
