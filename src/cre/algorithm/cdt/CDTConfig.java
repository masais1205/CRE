package cre.algorithm.cdt;

/**
 * Created by HanYizhao on 2017/4/7.
 */
public class CDTConfig implements Cloneable {
    private int height;
    private boolean pruned;
    private boolean test_improve_PA;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("-h ").append(height);
        if (pruned) {
            sb.append(" -p");
        }
        if (test_improve_PA) {
            sb.append(" -i");
        }

        return sb.toString();
    }

    public int getHeightMin() {
        return 2;
    }

    public int getHeightMax() {
        return 50;
    }

    public String getHeightComment() {
        return "The maximum height of the tree is: 50";
    }

    public String getPrunedComment() {
        return "Test if the child node can improve PA value";
    }

    public String getPrunedShownName() {
        return "test PA value?";
    }

    @Override
    public Object clone() {
        CDTConfig newC = null;
        try {
            newC = (CDTConfig) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return newC;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isPruned() {
        return pruned;
    }

    public void setPruned(boolean pruned) {
        this.pruned = pruned;
    }

    public boolean isTest_improve_PA() {
        return test_improve_PA;
    }

    public void setTest_improve_PA(boolean test_improve_PA) {
        this.test_improve_PA = test_improve_PA;
    }
}
