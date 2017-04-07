package cre.Config;

/**
 * Created by HanYizhao on 2017/4/6.
 */
public class ConfigDouble extends ConfigBase {
    public double max;
    public double min;

    public ConfigDouble(String name, String comments, String shownName) {
        super(name, comments, shownName);
        max = Double.MAX_VALUE;
        min = -Double.MAX_VALUE;
    }
}
