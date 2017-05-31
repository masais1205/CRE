package cre.algorithm.test.ce;

/**
 * Created by HanYizhao on 4/13/2017.
 */
public abstract class AbstractCE {
    public char[] value;

    public CEValue cEValue;

    public AbstractCE(char[] buffer) {
        value = new char[buffer.length];
        System.arraycopy(buffer, 0, value, 0, buffer.length);
    }

    public abstract AbstractCE mergeInstance(AbstractCE c2, int[] position, char positionChar, CEValue preferredValue, double zc);

    public abstract void updateCEValue(double zc);

    public abstract int getInstanceNumber();

}
