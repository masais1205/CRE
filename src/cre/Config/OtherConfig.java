package cre.Config;

/**
 * Created by HanYizhao on 2017/5/15.
 * Test options
 */
public class OtherConfig implements Cloneable {

    private int crossValidationFolds;

    public OtherConfig() {
        this.crossValidationFolds = 10;
    }

    public int getCrossValidationFolds() {
        return crossValidationFolds;
    }

    public void setCrossValidationFolds(int crossValidationFolds) {
        this.crossValidationFolds = crossValidationFolds;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
