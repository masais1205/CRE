package cre.algorithm.test;

/**
 * Created by HanYizhao on 2017/3/10.
 */
public class OR {
    private int[] values;
    private double orValue = -1;
    private Object attach;

    public OR(Object attach) {
        values = new int[4];
        this.attach = attach;
    }

    public Object getAttach() {
        return attach;
    }

    public void addValue(boolean W, boolean Y) {
        if (W) {
            if (Y) {
                values[0]++;
            } else {
                values[1]++;
            }
        } else {
            if (Y) {
                values[2]++;
            } else {
                values[3]++;
            }
        }
    }

    /**
     * Get odds ratio.
     *
     * @param reCalculate Calculate again or use cache.
     * @return Always >= 1
     */
    public double getOR(boolean reCalculate) {
        if (reCalculate || orValue < 0) {
            orValue = ((double) values[0] * values[3])
                    / ((values[1] == 0 ? 0.5 : values[1])
                    * (values[2] == 0 ? 0.5 : values[2]));
            if (orValue < 1) {
                orValue = 1.0 / (orValue < 0.00000001 ? 0.00000001 : orValue);
            }
        }
        return orValue;
    }

}
