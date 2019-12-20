package cre.algorithm.test.ce;

import java.util.List;

/**
 * Created by HanYizhao on 4/13/2017.
 */
public abstract class AbstractCE {
    public char[] value;

    public CEValue cEValue;

    public double groundTruth; // (ground truth) individual/conditional causal effect

    public double[] statistics = new double[5];

    public boolean reliable;

    public AbstractCE(char[] buffer) {
        value = new char[buffer.length];
        System.arraycopy(buffer, 0, value, 0, buffer.length);
    }

    public abstract AbstractCE mergeInstance(AbstractCE c2, List<Integer> position, int[] PCMembers, char positionChar, CEValue preferredValue, double zc);

    public abstract void updateCEValue(double zc);

    public abstract int getInstanceNumber();

    public abstract void updateStatistics();

    public abstract void updateReliable();

}
