package cre.Config;

/**
 * Created by HanYizhao on 2017/4/6.
 */
public class ConfigString extends ConfigBase {
    public String[] options;

    public ConfigString(String name, String comments, String shownName) {
        super(name, comments, shownName);
    }

    public ConfigString(String name, String comments, String shownName, boolean visible) {
        super(name, comments, shownName, visible);
    }
}
