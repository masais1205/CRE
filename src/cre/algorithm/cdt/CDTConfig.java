package cre.algorithm.cdt;

/**
 * Created by HanYizhao on 2017/4/7.
 * <p>The configuration class of CDT algorithm.</p>
 *
 */
public class CDTConfig implements Cloneable {
    private int height;
    private boolean pruned;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("-h ").append(height);
        if (pruned) {
            sb.append(" -p");
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
        return "Whether pruning is performed.";
    }

    public String getPrunedShownName() {
        return "pruned";
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

}
