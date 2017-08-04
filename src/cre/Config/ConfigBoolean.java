package cre.Config;

/**
 * Created by HanYizhao on 2017/4/6.
 */
public class ConfigBoolean extends ConfigBase {

    public ConfigBoolean(String name, String comments, String shownName) {
        super(name, comments, shownName);
    }

    public ConfigBoolean(String name, String comments, String shownName, boolean visible) {
        super(name, comments, shownName, visible);
    }
}
