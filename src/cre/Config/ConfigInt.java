package cre.Config;

/**
 * Created by HanYizhao on 2017/4/6.
 */
public class ConfigInt extends ConfigBase {
    public int min;
    public int max;

    public ConfigInt(String name, String comments, String shownName) {
        super(name, comments, shownName);
        min = Integer.MIN_VALUE;
        max = Integer.MAX_VALUE;
    }
}
